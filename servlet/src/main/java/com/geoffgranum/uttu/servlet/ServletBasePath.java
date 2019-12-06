/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet;

/**
 * @author Geoff M. Granum
 */
public class ServletBasePath {

  private final String servletBasePath;

  public ServletBasePath(String servletBasePath) {
    this.servletBasePath = servletBasePath;
  }

  public String getBasePath() {
    return servletBasePath;
  }
}
 
