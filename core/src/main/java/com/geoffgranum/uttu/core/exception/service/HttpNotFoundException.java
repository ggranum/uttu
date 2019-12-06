/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.exception.service;

import org.apache.http.HttpStatus;

/**
 * @author Geoff M. Granum
 */
public class HttpNotFoundException extends ServiceException {

  private static final long serialVersionUID = 1L;

  public HttpNotFoundException(String message) {
    super(HttpStatus.SC_NOT_FOUND, message);
  }

  public HttpNotFoundException(String messageFormat, Object... args) {
    super(HttpStatus.SC_NOT_FOUND, messageFormat, args);
  }
}
 
