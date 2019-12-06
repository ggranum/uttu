/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.log.intercept;

import com.google.inject.AbstractModule;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

public class LoggingInterceptionModule extends AbstractModule {

  public LoggingInterceptionModule() {
  }

  @Override
  protected void configure() {
    bindInterceptor(any(), annotatedWith(Logged.class), new LoggingInterceptor());
  }
}
 
