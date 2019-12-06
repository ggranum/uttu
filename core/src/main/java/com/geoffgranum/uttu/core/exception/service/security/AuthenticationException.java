/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.exception.service.security;

import com.geoffgranum.uttu.core.exception.service.ServiceException;
import org.apache.http.HttpStatus;

/**
 * @author Geoff M. Granum
 */
public class AuthenticationException extends ServiceException {

  private static final long serialVersionUID = 1L;

  public AuthenticationException(String msgFormat) {
    super(msgFormat);
  }

  public AuthenticationException(String msgFormat, Object... args) {
    super(msgFormat, args);
  }

  public AuthenticationException(Throwable cause, String msgFormat, Object... args) {
    super(cause, msgFormat, args);
  }

  @Override
  public int statusCode() {
    return HttpStatus.SC_UNAUTHORIZED;
  }

  @Override
  public String getHttpResponseMessage() {
    return "An error occurred while authenticating your request.";
  }
}
