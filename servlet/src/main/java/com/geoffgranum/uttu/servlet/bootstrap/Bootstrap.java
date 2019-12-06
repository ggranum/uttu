/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoffgranum.uttu.core.base.VersionInfo;
import com.geoffgranum.uttu.core.exception.FatalException;
import com.geoffgranum.uttu.core.log.Log;
import com.geoffgranum.uttu.servlet.UttuApplication;
import com.geoffgranum.uttu.servlet.UttuServletContextListener;
import com.geoffgranum.uttu.servlet.exception.InvalidCommandLineException;
import com.geoffgranum.uttu.servlet.initialization.InitializationChain;
import com.geoffgranum.uttu.servlet.initialization.InitializationException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.servlet.ServletModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Determine the current environment and load bootstrap configuration properties.
 *
 * Sets the log factory for apache commons and jboss logging to slf4j.
 * Enables verbose javax.net.debug logging by default. This is so new implementors see the ssl certs being loaded. Use the provided static method
 * to disable this.
 *
 * Load Priority: defaults < configFiles < environment < commandLine
 *
 * @author ggranum
 */
public final class Bootstrap {

  /**
   * Capture the original values in case a consumer wants to reset them.
   */
  public static final @Nullable String JAVAX_NET_DEBUG_ORIGINAL = System.getProperty("javax.net.debug");
  public static final @Nullable String ORG_JBOSS_LOGGING_PROVIDER_ORIGINAL = System.getProperty("org.jboss.logging.provider");
  public static final @Nullable String ORG_APACHE_COMMONS_LOGGING_LOGFACTORY_ORIGINAL = System.getProperty("org.apache.commons.logging.LogFactory");
  public static final String ENV = "env";
  static {
    /*
     * This class is the second to be loaded, but has to be loaded during that classes main method, so this is a good place to init these system properties.
     */
    System.setProperty("javax.net.debug", "all");
    System.setProperty("org.jboss.logging.provider", "slf4j");
    System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.SLF4JLogFactory");
  }
  final Class<? extends BootstrapConfiguration> configurationClass;
  private final String appName;
  private final String bootstrapConfigFileName;
  private final String environmentPrefix;
  private final String basePath;
  private final Map<String, String> commandLineArgs;
  private final AtomicBoolean baseConfigurationCreated = new AtomicBoolean(false);
  private final AtomicBoolean injectorCreated = new AtomicBoolean(false);
  private final ServletModule servletModule;
  private final Set<Module> modules;
  private final Stage injectionStage;
  private final Optional<ModuleProvider> moduleProvider;
  private EnvSources envSources;
  private Injector injector;
  private BootstrapConfiguration baseConfiguration;

  private Bootstrap(Builder builder) {
    appName = builder.appName;
    bootstrapConfigFileName = builder.bootstrapConfigFileName;
    environmentPrefix = builder.environmentPrefix;
    basePath = builder.basePath;
    commandLineArgs = getConfigurationParamsFromCommandLineArgs(builder.commandLineArgs);
    modules = builder.modules;
    servletModule = builder.servletModule;
    injectionStage = builder.injectionStage;
    moduleProvider = Optional.ofNullable(builder.moduleProvider);
    configurationClass = builder.configurationClass;
  }

  private void init() {
    this.envSources = EnvReader.determineEnvironment(commandLineArgs, environmentPrefix, basePath);
    this.baseConfiguration = buildConfiguration(envSources.selectedEnv, configurationClass);
    ImmutableSet.Builder<Module> modules = initModules();
    injector = createInjector(modules.build());
  }

  private ImmutableSet.Builder<Module> initModules() {
    VersionInfo applicationVersion = determineCodeVersion(basePath);
    UttuBootstrapModule
            uttuBootstrapModule = new UttuBootstrapModule(this.envSources.selectedEnv, this, applicationVersion, new InitializationChain());

    ImmutableSet.Builder<Module> modules = ImmutableSet.<Module>builder().add(uttuBootstrapModule)
                                               .add(servletModule)
                                               .addAll(this.modules);
    if(moduleProvider.isPresent()) {
      ModuleProvider provider = this.moduleProvider.get();
      Set<Module> providedModules = provider.get(envSources.selectedEnv, baseConfiguration);
      modules.addAll(providedModules);
    }
    return modules;
  }

  private VersionInfo determineCodeVersion(String basePath) {
    VersionInfo versionInfo;
    File versionFile = new File(basePath, "config/version.number");
    if(!versionFile.exists()) {
      Log.warn(getClass(),
               "No 'version.number' file found at path %s. Providing a valid version for your application is highly recommended",
               versionFile.getAbsolutePath());
      versionInfo = new VersionInfo.Builder().fromVersionString("0.0.0-MISSING").build();
    } else {
      try {
        InputStream stream = new FileInputStream(versionFile);
        String versionString = IOUtils.toString(stream, StandardCharsets.UTF_8);
        versionInfo = new VersionInfo.Builder().fromVersionString(versionString).build();
      } catch (IOException e) {
        throw new FatalException(e, "Could not read version from file at path %s.", versionFile.getAbsolutePath());
      }
    }
    return versionInfo;
  }

  private Injector createInjector(Set<Module> modules) {

    return Guice.createInjector(injectionStage, modules);
  }

  private Injector injector() {
    Log.info(getClass(), "Injector requested.");
    // We really want to discourage access to the injector - implementors can inject it into their classes, after all. Since we only use it once...
    if(injector == null) {
      throw new FatalException("Request for injector before initialization: Injector is still null!");
    }
    return injector;
  }

  public <T extends BootstrapConfiguration> T baseConfiguration() {
    //noinspection unchecked
    return (T)this.baseConfiguration;
  }

  private <T extends BootstrapConfiguration> T buildConfiguration(Env env, Class<T> configurationClass) {
    EnvOrFileSourcedConfigurationReader<T> reader =
        new EnvOrFileSourcedConfigurationReader<>(bootstrapConfigFileName,
                                                  env,
                                                  new File(basePath, "config/").getPath(),
                                                  environmentPrefix,
                                                  configurationClass,
                                                  new ObjectMapper());

    Map<String, String> providedConfig = new HashMap<>();
    Map<String, String> fileMap = reader.readFromOptionalFile();
    Map<String, String> envMap = reader.readFromEnvironment();

    providedConfig.put("env", '"' + env.key + '"');
    if(fileMap.containsKey(ENV)) {
      throw new InitializationException("Attempting to set env in configuration file will cause fantastic confusion. Use the env.name and env.local.name "
                                        + "files.");
    }
    providedConfig.putAll(fileMap);
    providedConfig.putAll(envMap);
    providedConfig.putAll(commandLineArgs);

    return reader.from(providedConfig);
  }

  /**
   * Maybe redo this with https://github.com/ggranum/cli-annotations
   */
  protected Map<String, String> getConfigurationParamsFromCommandLineArgs(String[] args) {
    Map<String, String> argsConfig = Maps.newHashMap();
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if(arg.startsWith("--")) {
        arg = arg.substring(2);
        String value = StringUtils.strip(args[++i], "\"' ");
        if(StringUtils.isNotEmpty(value)) {
          if(value.startsWith("--")) {
            throw new InvalidCommandLineException("Argument is missing a value. Argument: '%s'. Following token: %s",
                                                  arg, value);
          }
          argsConfig.put(arg, value);
        }
      }
    }
    return argsConfig;
  }

  public void resetForcedSystemProperties() {
    System.setProperty("javax.net.debug", "all");
    System.setProperty("org.jboss.logging.provider", "slf4j");
    System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.SLF4JLogFactory");
  }

  public <T extends UttuApplication> T start(Class<T> applicationClass) throws Exception {
    T app = this.injector().getInstance(applicationClass);
    app.start();
    return app;
  }

  public UttuServletContextListener createContextListener(BootstrapConfiguration baseConfig) {
    if(injector == null) {
      throw new InitializationException("Application must be initialized using #start().");
    }
    return new UttuServletContextListener(baseConfig.httpPort(), baseConfig.httpsPort(), injector);
  }

  public static void enableVerboseNetworkAndCertificateLogging() {
    System.setProperty("javax.net.debug", "all");
  }

  public static void disableVerboseNetworkAndCertificateLogging() {
    if(JAVAX_NET_DEBUG_ORIGINAL == null) {
      System.clearProperty("javax.net.debug");
    } else {
      System.setProperty("javax.net.debug", JAVAX_NET_DEBUG_ORIGINAL);
    }
  }

  public static final class Builder {

    private String appName;
    private String bootstrapConfigFileName;
    private String basePath;
    private String environmentPrefix;
    private String[] commandLineArgs = {};
    private Stage injectionStage = Stage.PRODUCTION;
    private ServletModule servletModule;
    private Set<Module> modules = Collections.emptySet();
    private ModuleProvider moduleProvider;
    private Class<? extends BootstrapConfiguration> configurationClass;

    public Builder() {
    }

    public Builder appName(String appName) {
      this.appName = appName;
      return this;
    }

    public Builder bootstrapConfigFileName(String bootstrapConfigFileName) {
      this.bootstrapConfigFileName = bootstrapConfigFileName;
      return this;
    }

    public Builder environmentPrefix(String environmentPrefix) {
      this.environmentPrefix = environmentPrefix;
      return this;
    }

    public Builder basePath(String basePath) {
      this.basePath = basePath;
      return this;
    }

    public Builder commandLineArgs(String[] commandLineArgs) {
      this.commandLineArgs = commandLineArgs;
      return this;
    }

    public Builder injectionStage(Stage injectionStage) {
      this.injectionStage = injectionStage;
      return this;
    }

    public Builder servletModule(ServletModule servletModule) {
      this.servletModule = servletModule;
      return this;
    }

    public Builder modules(Set<Module> modules) {
      this.modules = modules;
      return this;
    }

    public Builder moduleProvider(ModuleProvider moduleProvider) {
      this.moduleProvider = moduleProvider;
      return this;
    }

    public Builder configurationClass(Class<? extends BootstrapConfiguration> configurationClass) {
      this.configurationClass = configurationClass;
      return this;
    }

    public Bootstrap build() {
      if(StringUtils.isEmpty(this.bootstrapConfigFileName)) {
        this.bootstrapConfigFileName = appName.toLowerCase().replace(' ', '_');
      }
      Bootstrap bootstrap = new Bootstrap(this);
      bootstrap.init();
      return bootstrap;
    }
  }
}
