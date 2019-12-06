/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.exception;

/**
 * Throwing this exception SHOULD lead to as graceful a shutdown as possible, but MUST cause the application to shut
 * down. Restarts triggered by external monitoring agents such as Tanuki Wrapper are allowed.
 */
public class FatalException extends FormattedException {

  private static final long serialVersionUID = 1L;

  public FatalException(String msg) {
    super(msg);
  }

  public FatalException(Throwable e, String msg, Object... args) {
    super(e, msg, args);
  }

  public FatalException(String msg, Object... args) {
    super(msg, args);
  }
}
 
