/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.exception.service;

/**
 * @author Geoff M. Granum
 */
public class UsernameNotUniqueException extends ServiceException {

  private static final long serialVersionUID = 1L;

  public UsernameNotUniqueException(String message) {
    super(message);
  }

  public UsernameNotUniqueException(String msgFormat, Object... args) {
    super(msgFormat, args);
  }
}
 
