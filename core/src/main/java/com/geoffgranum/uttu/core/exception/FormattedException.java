/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.exception;

import com.geoffgranum.uttu.core.log.Level;
import com.geoffgranum.uttu.core.log.Log;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Just a wrapper that will format messages strings while creating exceptions, for us lazy folk.
 *
 * @author Geoff M. Granum
 */
public class FormattedException extends RuntimeException implements CustomFaultCode {

  private static final long serialVersionUID = 1L;
  private final AtomicBoolean hasBeenLogged = new AtomicBoolean(false);

  private String customFaultCode;

  /**
   * Defaulting to true is tough call. The majority of exceptions should not log stack traces (because you're creating lots of exception types, right?
   * and adding good exception messages too, right?). However, in those cases where the exception is not well understood, having a stack will make debugging
   * much, much easier.
   *
   * Subclasses that are thrown regularly and are well defined can override the default at the constructor level easily enough.
   */
  private boolean shouldPrintStack = true;

  /**
   * Required for Verify methods that accept an exception class.
   */
  public FormattedException(String message) {
    super(message);
  }

  public FormattedException(String msgFormat, Object... args) {
    this(null, msgFormat, args);
  }

  public FormattedException(Throwable cause, String msgFormat, Object... args) {
    super(FormatSafe(msgFormat, args), cause);
  }

  private static String FormatSafe(String msgFormat, Object[] args) {
    String msg = msgFormat;
    try {
      msg = String.format(msgFormat, args);
    } catch (Exception e) {
      Log.warn(FormattedException.class, "String format failed!");
      Log.error(FormattedException.class, e);
    }
    return msg;
  }

  public FormattedException(Throwable cause) {
    super(cause);
  }

  @Override
  public String getCustomFaultCode() {
    return customFaultCode;
  }

  @Override
  public FormattedException withCustomCode(String code) {
    this.customFaultCode = code;
    return this;
  }

  /**
   * Log the exception, printing the stack trace only if 'shouldPrintStackTrace' is true, and 'hasBeenLogged' is false.
   * Will not log if this exception has had one of its log methods called previously.
   */
  public FormattedException log() {
    if(!hasBeenLogged.getAndSet(true)) {
      Log.log(getLogLevel(), shouldPrintStack(), getThrowingClassName(), this, getLogMessage());
    }
    return this;
  }

  /**
   * Force this exception to be logged, including the entire stack trace. Will not log if this exception has had one of its log methods called previously.
   */
  public FormattedException logWithTrace() {
    if(!hasBeenLogged.getAndSet(true)) {
      Log.log(getLogLevel(), true, getThrowingClassName(), this, getLogMessage());
    }
    return this;
  }

  public Level getLogLevel() {
    return Level.ERROR;
  }

  public FormattedException enableStackTrace() {
    this.shouldPrintStack = true;
    return this;
  }

  public FormattedException disableStackTrace() {
    this.shouldPrintStack = false;
    return this;
  }

  public boolean shouldPrintStack() {
    return this.shouldPrintStack;
  }


  public String getLogMessage() {
    return getMessage();
  }

  public String getThrowingClassName() {
    return getStackTrace()[1].getClassName();
  }
}
 
