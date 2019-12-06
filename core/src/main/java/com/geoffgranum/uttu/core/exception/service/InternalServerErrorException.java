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
public class InternalServerErrorException extends ServiceException {

  private static final long serialVersionUID = 1L;

  public InternalServerErrorException(String message) {
    super(HttpStatus.SC_INTERNAL_SERVER_ERROR, message);
  }

  public InternalServerErrorException(String messageFormat, Object... args) {
    super(HttpStatus.SC_INTERNAL_SERVER_ERROR, messageFormat, args);
  }

  public InternalServerErrorException(Throwable cause, String messageFormat, Object... args) {
    super(cause, HttpStatus.SC_INTERNAL_SERVER_ERROR, messageFormat, args);
  }


}
 
