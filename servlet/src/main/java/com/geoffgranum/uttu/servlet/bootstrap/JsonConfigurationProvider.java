/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import javax.inject.Provider;

/**
 * @author ggranum
 */
public abstract class JsonConfigurationProvider<T> implements Provider<T> {

  private final Class<T> clazz;
  private final JsonConfigurationReader configReader;

  public JsonConfigurationProvider(Class<T> clazz, JsonConfigurationReader configReader) {
    this.clazz = clazz;
    this.configReader = configReader;
  }

  @Override
  public T get() {
    return configReader.read(getBaseName(), clazz);
  }

  protected abstract String getBaseName();
}
