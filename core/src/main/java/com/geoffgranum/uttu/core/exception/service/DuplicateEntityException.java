/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.exception.service;

/**
 * @author Geoff M. Granum
 */
public class DuplicateEntityException extends ServiceException {

  private static final long serialVersionUID = 1L;

  public DuplicateEntityException(Throwable cause, String msgFormat, Object... args) {
    super(cause, msgFormat, args);
  }

  public DuplicateEntityException(String msgFormat, Object... args) {
    super(msgFormat, args);
  }
}
 
