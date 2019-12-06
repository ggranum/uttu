/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.restclient;

public interface RestClientConfig {

  String username();

  String password();

  boolean isHttps();

  String host();

  int port();

  default String protocol() {
    return "http" + (isHttps() ? "s" : "");
  }

  default String serviceUri() {
    return String.format("%s://%s:%s", protocol(), host(), port());
  }
}
 
