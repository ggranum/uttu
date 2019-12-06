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
public class BadRequestException extends ServiceException {

  private static final long serialVersionUID = 1L;

  public BadRequestException(String message) {
    super(HttpStatus.SC_BAD_REQUEST, message);
  }

  public BadRequestException(String messageFormat, Object... args) {
    super(HttpStatus.SC_BAD_REQUEST, messageFormat, args);
  }

  public BadRequestException(Throwable cause, String messageFormat, Object... args) {
    super(cause, HttpStatus.SC_BAD_REQUEST, messageFormat, args);
  }

}
 
