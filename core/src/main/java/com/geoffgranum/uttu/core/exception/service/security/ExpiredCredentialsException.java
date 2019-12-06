/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.exception.service.security;

import org.apache.http.HttpStatus;

/**
 * @author Geoff M. Granum
 */
public class ExpiredCredentialsException extends AuthenticationException {

  private static final long serialVersionUID = 1L;

  public ExpiredCredentialsException(String message) {
    super(message);
  }

  @Override
  public int statusCode() {
    return HttpStatus.SC_UNAUTHORIZED;
  }

  @Override
  public String getHttpResponseMessage() {
    return "Your session has expired.";
  }
}
 
