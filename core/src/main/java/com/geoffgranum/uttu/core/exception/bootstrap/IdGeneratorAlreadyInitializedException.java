package com.geoffgranum.uttu.core.exception.bootstrap;

import com.geoffgranum.uttu.core.exception.FormattedException;

/**
 * @author ggranum
 */
public class IdGeneratorAlreadyInitializedException extends FormattedException {
  public IdGeneratorAlreadyInitializedException(String message) {
    super(message);
  }
}
