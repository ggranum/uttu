/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.log.intercept;

/**
 *
 */

import com.geoffgranum.uttu.core.log.Level;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
public @interface Logged {

  /**
   * Disable logging based on log level. The cost of intercepting the method is about a half millisecond on fully spec'd 2015 MacBook Pro,
   * while the cost if logging is enabled for the class is nearly 60 milliseconds.
   * @return
   */
  Level level() default Level.TRACE;

  /**
   * Enable to log performance timing to method exit log statement. Adds a few milliseconds to the invocation on a fully spec'd 2015 MacBook Pro.
   * Could be significant hit in a linux environment with High Performance Event Timer enabled.
   * See https://pzemtsov.github.io/2017/07/23/the-slow-currenttimemillis.html
   */
  boolean perf() default false;
}
