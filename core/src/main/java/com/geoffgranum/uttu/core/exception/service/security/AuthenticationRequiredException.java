/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.exception.service.security;

/**
 * @author Geoff M. Granum
 */
public class AuthenticationRequiredException extends AuthenticationException {

  private static final long serialVersionUID = 1L;

  public AuthenticationRequiredException(String msgFormat) {
    super(msgFormat);
  }

  public AuthenticationRequiredException(String msgFormat, Object... args) {
    super(msgFormat, args);
  }

  public AuthenticationRequiredException(Throwable cause, String msgFormat, Object... args) {
    super(cause, msgFormat, args);
  }

  @Override
  public String getHttpResponseMessage() {
    return "Username or password is invalid.";
  }
}
 
