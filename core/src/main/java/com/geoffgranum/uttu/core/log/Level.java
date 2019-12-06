/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.log;

/**
 *
 * @author Geoff M. Granum
 */
public enum Level {

  OFF() {
    @Override
    public org.apache.logging.log4j.Level level() {
      return org.apache.logging.log4j.Level.OFF;
    }
  },
  FATAL() {
    @Override
    public org.apache.logging.log4j.Level level() {
      return org.apache.logging.log4j.Level.FATAL;
    }
  },
  ERROR() {
    @Override
    public org.apache.logging.log4j.Level level() {
      return org.apache.logging.log4j.Level.ERROR;
    }
  },
  WARN() {
    @Override
    public org.apache.logging.log4j.Level level() {
      return org.apache.logging.log4j.Level.WARN;
    }
  },
  INFO() {
    @Override
    public org.apache.logging.log4j.Level level() {
      return org.apache.logging.log4j.Level.INFO;
    }
  },
  DEBUG() {
    @Override
    public org.apache.logging.log4j.Level level() {
      return org.apache.logging.log4j.Level.DEBUG;
    }
  },
  TRACE() {
    @Override
    public org.apache.logging.log4j.Level level() {
      return org.apache.logging.log4j.Level.TRACE;
    }
  },
  ALL() {
    @Override
    public org.apache.logging.log4j.Level level() {
      return org.apache.logging.log4j.Level.ALL;
    }
  };

  public org.apache.logging.log4j.Level level() {
    return org.apache.logging.log4j.Level.OFF;
  }

}
 
