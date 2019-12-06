/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.geoffgranum.uttu.core.exception.FatalException;
import com.geoffgranum.uttu.core.log.Log;
import com.geoffgranum.uttu.servlet.initialization.InitializationException;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ggranum
 */
public class EnvOrFileSourcedConfigurationReader<T extends BootstrapConfiguration> {

  private final Class<T> model;
  private final ObjectMapper mapper;
  private final String environmentPrefix;
  private final Set<String> valueKeys;
  private final String appName;
  private final Env env;
  private final String configFilesBasePath;

  /**
   * @param env               The runtime environment - dev, prod, qa, or ci.
   * @param environmentPrefix The prefix to apply when attempting to retrieve values from the system environment.
   *                          For example, setting this to 'MY_APP' will attempt to set the value for 'FOO_VALUE' using
   */
  public EnvOrFileSourcedConfigurationReader(String configurationName,
                                             Env env,
                                             String configFilesBasePath,
                                             String environmentPrefix,
                                             Class<T> model,
                                             ObjectMapper mapper) {
    this.configFilesBasePath = configFilesBasePath;
    this.appName = configurationName;
    this.env = env;
    this.environmentPrefix = environmentPrefix;
    this.model = model;
    this.mapper = mapper;

    Set<String> fields = Lists.newArrayList(
        model.getDeclaredFields())
                             .stream()
                             .filter(field -> isPublicFinal(field))
                             .map(field -> field.getName())
                             .collect(Collectors.toSet());
    this.valueKeys = ImmutableSet.<String>builder().addAll(fields).build();
  }

  public T from(Map<String, String> values) {
    StringBuilder b = new StringBuilder("{");
    try {
      for (Map.Entry<String, String> entry : values.entrySet()) {
        b.append('"').append(entry.getKey()).append('"').append(": ").append(entry.getValue()).append(",\n");
      }
      if(values.size() > 0) {
        b.replace(b.length() - 2, b.length(), "");
      }
      b.append("}");
      return mapper.readValue(b.toString(), model);
    } catch (UnrecognizedPropertyException e) {
      throw new InitializationException(e, "Could not read configuration from provided sources.");
    } catch (MismatchedInputException e) {
      throw new InitializationException(e, "Multi-source configuration reader only supports primitive values. "
                                           + "If you need complex values or structured data, consider adding a secondary configuration file and using a "
                                           + "JsonConfigurationProvider or JsonConfigurationReader.");
    } catch (IOException e) {
      Log.trace(getClass(), "Could not parse effective JSON configruation of: \n%s", b.toString());
      throw new InitializationException(e, "Could not read configuration from provided sources.");
    }
  }

  public Map<String, String> readFromEnvironment() {
    Map<String, String> values = new HashMap<>();
    Log.info(getClass(), "Reading configuration from environment properties.");
    String envKey = "";
    String envValue = "";
    try {

      for (String valueKey : this.valueKeys) {
        envKey = this.getEnvironmentKey(valueKey);
        envValue = System.getenv(envKey);
        if(envValue != null) {
          Log.debug(getClass(), "\tFound: %s=%s", envKey, envValue);
        } else {
          Log.info(getClass(), "\tMissing: %s", envKey, envValue);
        }
        if(envValue != null) {
          values.put(valueKey, mapper.writeValueAsString(envValue));
        }
      }
    } catch (JsonProcessingException e) {
      throw new InitializationException(e, "Could not translate value of environment key-value pair '%s=%s' into JSON value.", envKey, envValue);
    }
    return values;
  }

  public Map<String, String> readFromOptionalFile() {
    return this.readFromFile(false);
  }

  public Map<String, String> readFromFile() {
    return this.readFromFile(true);
  }

  private Map<String, String> readFromFile(boolean failIfAbsent) {
    Map<String, String> values = new HashMap<>();
    String fileName = String.format("%s.%s.json", this.appName, this.env.key);
    File file = new File(this.getBasePath(), fileName);
    Log.info(getClass(), "Checking %s for configuration...", file.getAbsolutePath());
    if(failIfAbsent && !file.exists()) {
      throw new InitializationException("Could not find configuration file at path %s", file.getAbsolutePath());
    } else if(!file.exists()) {
      Log.info(getClass(), "Could not find configuration file at path %s. Continuing.", file.getAbsolutePath());
    } else {
      Log.info(getClass(), "\t configuration found. Adding '%s' as configuration source.", file.getAbsolutePath());
      JsonConfigurationReader reader = new JsonConfigurationReader(env, mapper);
      try {
        Map<String, Object> raw = reader.read(file, new HashMap<>());
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
          values.put(entry.getKey(), mapper.writeValueAsString(entry.getValue()));
        }
      } catch (Exception e) {
        throw new FatalException(e, "Could not load JSON file into string map. "
                                    + "EnvOrFileSourced Configurations must be simple String-value pair JSON structures. "
                                    + "No nesting or complex types allowed. ");
      }
    }
    return values;
  }

  public T read() {
    Map<String, String> file = this.readFromFile();
    Map<String, String> env = this.readFromEnvironment();
    Map<String, String> all = ImmutableMap.<String, String>builder().putAll(file).putAll(env).build();
    return this.from(all);
  }

  private boolean isPublicFinal(Field field) {
    int modifiers = field.getModifiers();
    boolean aPublic = Modifier.isPublic(modifiers);
    boolean aFinal = Modifier.isFinal(modifiers);
    return aPublic && aFinal;
  }

  public String getEnvironmentKey(String camelCaseValueName) {
    return this.environmentPrefix + '_' + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, camelCaseValueName);
  }

  public String getBasePath() {
    return this.configFilesBasePath;
  }
}
