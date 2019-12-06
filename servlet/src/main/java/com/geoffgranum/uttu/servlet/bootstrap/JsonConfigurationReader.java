/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.core.exception.FatalException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.inject.Inject;

/**
 * @author ggranum
 */
public class JsonConfigurationReader {

  private final Env env;
  private final ObjectMapper mapper;

  @Inject
  public JsonConfigurationReader(Env env, ObjectMapper mapper) {
    this.env = env;
    this.mapper = mapper;
  }

  public JsonConfigurationReader(Env env) {
    this(env, new ObjectMapper());
  }

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
  public File getConfigFileForEnvironment(String baseName) {
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

    return content;
  }
}
