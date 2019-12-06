/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet.initialization;

public class InitializationAlreadyPerformedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InitializationAlreadyPerformedException() {
  }

  public InitializationAlreadyPerformedException(String message) {
    super(message);
  }

  public InitializationAlreadyPerformedException(String message, Throwable cause) {
    super(message, cause);
  }

  public InitializationAlreadyPerformedException(Throwable cause) {
    super(cause);
  }
}
 
