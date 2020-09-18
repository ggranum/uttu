/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.core.exception.FatalException;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.inject.Inject;

/**
 * The JsonConfigurationReader is intended for parsing configuration files specifically. More specifically, it is
 * intended for parsing configuration files at startup-time.
 * As such it performs property expansion, and on failure it throws FatalExceptions.
 *
 * Files read by this class will have substitution parameters replaced, in a manner modeled after Log4j2.
 * See http://logging.apache.org/log4j/2.x/manual/configuration.html#PropertySubstitution
 *
 * The following contexts are provided:
 *
 * ${env:KEY} Looks up KEY from System.getenv() map.
 * ${sys:KEY} Looks up KEY from System.getProperties() map.
 *
 * Thus, a JSON file containing
 *
 * <code>
 *     {
 *         "somePassword": "MyFooVar${env:MY_DB_PASSWORD}"
 *     }
 * </code>
 *
 * would attempt to lookup "MY_DB_PASSWORD" from System.getenv().
 *
 * @author ggranum
 */
public class JsonConfigurationReader {

  private final Env env;
  private final ObjectMapper mapper;
  private final StrSubstitutor envSub = new StrSubstitutor(System.getenv());
  private final StrSubstitutor sysPropSub = new StrSubstitutor(System.getProperties());

  @Inject
  public JsonConfigurationReader(Env env, ObjectMapper mapper) {
    this.env = env;
    this.mapper = mapper;
  }

  public JsonConfigurationReader(Env env) {
    this(env, new ObjectMapper());
  }

  /**
   *
   *
   * @deprecated Use read(File, Class)
   */
  @Deprecated
  public <T> T read(String baseName, Class<T> type) {
    File f = getConfigFileForEnvironment(baseName);
    return read(f, type);
  }

  public <T> T read(File jsonFile, T typeInstance) {
    //noinspection unchecked
    return (T)this.read(jsonFile, typeInstance.getClass());
  }

  public <T> T read(InputStream jsonStream, Class<T> type) throws IOException {
    return this.mapper.readValue(jsonStream, type);
  }

  public <T> T read(File jsonFile, Class<T> type) {
    String json = readJsonFile(jsonFile);
    T config;
    try {
      config = mapper.readValue(json, type);
    } catch (IOException e) {
      throw new FatalException(e,
                               "Could not parse JSON config for env '%s' from file at path '%s'",
                               env.key,
                               jsonFile.getAbsolutePath());
    }

    return config;
  }

  /**
   * Assumes your configuration files live at {applicationRoot}/config/{configFileName}.{env.key}.json.
   * If you don't want your configuration files to live at '{applicationRoot}/config/{configFileName}.{env.key}.json', then you'll
   * need to override this method.
   * If you don't want to parse from JSON... well, override the whole class. But this project assumes a lot of JSON use...
   *
   * @param baseName The base name for the config file, such as "myApp".
   *
   * @return The File, which has not been checked for existence.
   */
  private File getConfigFileForEnvironment(String baseName) {
    String filePath = "config/" + baseName + "." + env.key + ".json";
    return new File(filePath);
  }

  public String readJsonFile(File file) {
    String content;
    try {
      byte[] contentBytes = Files.readAllBytes(file.toPath());
      content = new String(contentBytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new FatalException(e,
                               "Could not read config file for env '%s' from file at path '%s'",
                               env.key,
                               file.getAbsolutePath());
    }
    Verify.isNotEmpty(content,
                      FatalException.class,
                      "Could not read config file for env '%s' from file at path '%s'",
                      env.key,
                      file.getAbsolutePath());

    content = replaceLookups(content);
    return content;
  }

  /**
   * Replaces matching substitution strings. Modeled after Log4j2.
   * See http://logging.apache.org/log4j/2.x/manual/configuration.html#PropertySubstitution
   *
   * ${env:KEY} Looks up KEY from System.getenv() map.
   * ${sys:KEY} Looks up KEY from System.getProperties() map.
   *
   * @param content The raw json content.
   * @return the provided content with any substitution strings replaced by the values found in the relevant context.
   */
  private String replaceLookups(String content) {
    StringBuilder result = new StringBuilder(content);
    envSub.replaceIn(result);
    sysPropSub.replaceIn(result);
    return result.toString();
  }
}
