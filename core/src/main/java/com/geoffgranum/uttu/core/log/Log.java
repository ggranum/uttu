/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * If I weren't so lazy this would be called 'Log4JLogger' and I'd have a different class named 'Log' that would
 * be doing something like this: http://bill.burkecentral.com/2012/05/22/write-your-own-logging-abstraction/
 *
 * But I am lazy. So this is just a nice central point from which to replace / augment the logging framework
 * if such is ever desired.
 *
 * @author Geoff M. Granum
 */
public class Log {

  public static void debug(Class clazz, String msgFormat, Object... args) {
    log(Level.DEBUG, clazz, msgFormat, args);
  }

  public static void debug(Class clazz, Throwable throwable, String msgFormat, Object... args) {
    log(Level.DEBUG, clazz, throwable, msgFormat, args);
  }

  public static boolean debugEnabled(Class clazz) {
    return enabled(clazz, Level.DEBUG);
  }

  public static boolean enabled(Class clazz, Level level) {
    return logger(clazz).isEnabled(level.level());
  }

  private static Logger logger(Class clazz) {
    return LogManager.getLogger(clazz);
  }

  public static void error(Class clazz, String msgFormat, Object... args) {
    log(Level.ERROR, clazz, msgFormat, args);
  }

  public static void error(Class clazz, Throwable throwable) {
    log(Level.ERROR, clazz, throwable, throwable.getMessage());
  }

  public static void error(Class clazz, Throwable throwable, String msgFormat, Object... args) {
    log(Level.ERROR, clazz, throwable, msgFormat, args);
  }

  public static void fatal(Class clazz, String msgFormat, Object... args) {
    log(Level.FATAL, clazz, msgFormat, args);
  }

  public static void info(Class clazz, String msgFormat, Object... args) {
    log(Level.INFO, clazz, msgFormat, args);
  }

  public static void log(Level level, Class clazz, String msgFormat, Object... args) {

    Logger logger = logger(clazz);
    if(logger.isEnabled(level.level())) {
      logger.log(level.level(), safeFormat(msgFormat, args));
    }
  }

  public static void log(Level level, String loggerName, String msgFormat, Object... args) {

    Logger logger = logger(loggerName);
    if(logger.isEnabled(level.level())) {
      logger.log(level.level(), safeFormat(msgFormat, args));
    }
  }

  public static void log(Level level, Class clazz, Throwable t, String msgFormat, Object... args) {
    Logger logger = logger(clazz);
    if(logger.isEnabled(level.level())) {
      logger.log(level.level(), safeFormat(msgFormat, args), t);
    }
  }

  public static void log(Level level, boolean printStack, String clazz, Throwable t, String msgFormat, Object... args) {
    if(printStack) {
      log(level, clazz, t, msgFormat, args);
    } else {
      log(level, clazz, msgFormat, args);
    }
  }

  public static void log(Level level, boolean printStack, Class clazz, Throwable t, String msgFormat, Object... args) {
    if(printStack) {
      log(level, clazz, t, msgFormat, args);
    } else {
      log(level, clazz, msgFormat, args);
    }
  }

  public static void log(Level level, String name, Throwable throwable, String msgFormat, Object... args) {
    LogManager.getLogger(name).log(level.level(), safeFormat(msgFormat, args), throwable);
  }

  public static void trace(Class clazz, String msgFormat, Object... args) {
    log(Level.TRACE, clazz, msgFormat, args);
  }

  public static void trace(String loggerName, String msgFormat, Object... args) {
    log(Level.TRACE, loggerName, msgFormat, args);
  }

  public static boolean traceEnabled(Class clazz) {
    return enabled(clazz, Level.TRACE);
  }

  public static void unhandled(Class clazz, Throwable throwable) {
    log(Level.ERROR, "UnhandledExceptions", throwable, throwable.getMessage());
    log(Level.ERROR, clazz, throwable, throwable.getMessage());
  }

  public static void unhandled(Class clazz, Throwable throwable, String msgFormat, Object... args) {
    log(Level.ERROR, "UnhandledExceptions", throwable, msgFormat, args);
    log(Level.ERROR, clazz, throwable, msgFormat, args);
  }

  public static void warn(Class clazz, String msgFormat, Object... args) {
    log(Level.WARN, clazz, msgFormat, args);
  }

  public static void warn(Class clazz, Throwable throwable, String msgFormat, Object... args) {
    log(Level.WARN, clazz, throwable, msgFormat, args);
  }

  private static Logger logger(String loggerName) {
    return LogManager.getLogger(loggerName);
  }

  private static String safeFormat(String msgFormat, Object... args) {
    String msg = msgFormat;
    if(ArrayUtils.isNotEmpty(args)) {
      try {
        msg = String.format(msgFormat, args);
      } catch (Throwable e) {
        warn(Log.class,
             e,
             "Error while formatting message for logging. Invalid or missing arguments? Message:  " + msgFormat);
        msg = msgFormat;
      }
    }
    return msg;
  }
}

