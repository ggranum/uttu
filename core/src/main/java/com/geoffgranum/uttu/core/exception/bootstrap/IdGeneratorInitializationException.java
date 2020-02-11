package com.geoffgranum.uttu.core.exception.bootstrap;

import com.geoffgranum.uttu.core.exception.FormattedException;

/**
 * @author ggranum
 */
public class IdGeneratorInitializationException extends FormattedException {
  public IdGeneratorInitializationException(Throwable cause, String msgFormat, Object... args) {
    super(cause, msgFormat, args);
  }
}
