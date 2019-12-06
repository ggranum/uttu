/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.helloworld;

import com.geoffgranum.uttu.helloworld.resource.HelloWorldResource;
import com.geoffgranum.uttu.helloworld.resource.HelloWorldResourceImpl;
import com.geoffgranum.uttu.servlet.GuiceResteasyServletModule;

/**
 * @author ggranum
 */
public class HelloWorldServletModule extends GuiceResteasyServletModule {

  @Override
  public void bindResources() {
    bind(HelloWorldResource.class).to(HelloWorldResourceImpl.class);
  }
}
