/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.exception.service.security;

/**
 * @author Geoff M. Granum
 */
public class InvalidCredentialsException extends AuthenticationException {

  private static final long serialVersionUID = 1L;

  public InvalidCredentialsException(String msgFormat, Object... args) {
    super(msgFormat, args);
  }

  public InvalidCredentialsException(Throwable cause, String msgFormat, Object... args) {
    super(cause, msgFormat, args);
  }

  @Override
  public String getHttpResponseMessage() {
    return "Invalid username or password.";
  }
}
 
