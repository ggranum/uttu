/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.geoffgranum.uttu.core.log.Log;
import com.geoffgranum.uttu.servlet.initialization.InitializationException;
import com.google.common.base.CaseFormat;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

/**
 * Many teams prefer configuring app state via configuration files when possible.This presents an interesting conflict between
 * how to specify the value for environment (e.g. 'dev', 'qa', 'prod'). Knowing 'env' beforehand allows us to query
 * config files by environment key - 'config.dev.json', etc.
 *
 * There are a number of solutions:
 * 1) Require the env value be set via environment property, then get the config by keyed file name.
 * 2) Add the env value to the required constructor params, and force each team to figure it out on their own.
 * 3) Check a default config file in two forms: for example 'env.name', whose value is overridden by an optionally
 * present 'env.local.name'. 'local', because we recommend that '*.local.*' files be ignored via .gitignore.
 *
 * We implement 2) and 3) here. Teams are free to implement their own method in order to provide an Env instance
 * to the Bootstrap process.
 *
 *
 * We first attempt to read a file 'config/env.local.name'.
 * If missing, we attempt to read 'config/env.name'.
 * If missing, we check for the environment property '${environmentPrefix}_ENV'.
 * If still missing, we throw an error.
 *
 * @author ggranum
 */
public final class EnvReader {

  /**
   * @param environmentPrefix The prefix to apply when attempting to retrieve values from the system environment.
   *                          For example, setting this to 'MY_APP' will attempt to set the value for 'FOO_VALUE' using
   *                          the environment key 'MY_APP_FOO_VALUE'.
   *
   * @param basePath The root of the application. Defaults to './'.
   */
  public static EnvSources determineEnvironment(Map<String, String> cmdLineArgs, String environmentPrefix, String basePath) {
    try {

      Optional<Env> envFile = getFromEnvFile(basePath);
      Optional<Env> envLocalFile = getFromEnvLocalFile(basePath);
      Optional<Env> environment = getFromEnvironmentVariable(environmentPrefix);
      Optional<Env> commandLine = getFromCommandLineArgs(cmdLineArgs);

      return new EnvSources(envFile, envLocalFile, environment, commandLine);

    } catch (IOException e) {
      throw new InitializationException("Could not read environment name from config/env.name or config/env.local.name file."
                                        + "  env.name file must contain only the name of the environment as the first line."
                                        + "All subsequent lines are ignored.");
    }
  }

  private static Optional<Env> getFromCommandLineArgs(Map<String, String> cmdLineArgs) {
    Optional<Env> env = Optional.empty();
    if(cmdLineArgs.containsKey(Bootstrap.ENV)) {
      env = Optional.of(Env.valueOf(cmdLineArgs.get("env")));
      Log.info(EnvReader.class, "Env '%s' found in command line arguments", env.get().key);
    } else {
      Log.debug(EnvReader.class, "Env not present in command line arguments");
    }
    return env;
  }

  private static Optional<Env> getFromEnvironmentVariable(String environmentPrefix) {
    Optional<Env> env = Optional.empty();
    String key = environmentPrefix + '_' + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, Bootstrap.ENV);
    String value = System.getenv(key);
    if(value != null){
      Log.info(EnvReader.class, "Env '%s' found in system environment variables with key '%s'", value, key);
      env = Optional.of(Env.valueOf(value));
    } else {
      Log.debug(EnvReader.class, "No env found in system environment variables for key '%s'", key);
    }
    return env;

  }

  private static Optional<Env> getFromEnvFile(String basePath) throws IOException {
    Optional<Env> env = Optional.empty();
    File file = new File(basePath, "config/env.name");

    if(file.exists()) {
      String name = Files.readAllLines(file.toPath()).get(0).trim();
      env = Optional.of(Env.valueOf(name));
      Log.info(EnvReader.class, "Found env '%s' in env.name file.", name, file.getAbsolutePath());
    } else {
      Log.debug(EnvReader.class, "No env.name file present for path '%s'", file.getAbsolutePath());
    }
    return env;
  }

  private static Optional<Env> getFromEnvLocalFile(String basePath) throws IOException {
    Optional<Env> env = Optional.empty();
    File file = new File(basePath, "config/env.local.name");
    if(file.exists()) {
      String name = Files.readAllLines(file.toPath()).get(0).trim();
      env = Optional.of(Env.valueOf(name));
      Log.info(EnvReader.class, "Found env '%s' in env.local.name file.", env, file.getAbsolutePath());
    } else {
      Log.debug(EnvReader.class, "No env.local.name file present for path '%s'", file.getAbsolutePath());
    }
    return env;
  }
}
