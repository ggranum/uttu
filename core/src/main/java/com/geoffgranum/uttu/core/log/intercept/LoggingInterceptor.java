/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.log.intercept;

import com.geoffgranum.uttu.core.log.Level;
import com.geoffgranum.uttu.core.log.Log;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Geoff M. Granum
 */
public class LoggingInterceptor implements MethodInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Object result;
    Intercept intercept = null;
    try {
      intercept = getDetails(invocation);
    } catch (Exception e) {
      Log.error(getClass(), e, "Error performing interceptor logging, proceeding without logging.");
    }
    if(intercept != null && intercept.enabled) {
      result = doInvoke(invocation, intercept);
    } else {
      result = invocation.proceed();
    }
    return result;
  }

  private Intercept getDetails(MethodInvocation invocation) {
    Logged annotation = invocation.getMethod().getAnnotation(Logged.class);
    Class<?> loggedClass = invocation.getThis().getClass();
    String className = loggedClass.getName();
    int idx = className.lastIndexOf("$$Enh"); // enhanced by guice...
    if(idx > 0) {
      loggedClass = loggedClass.getSuperclass();
    }
    String methodName = invocation.getMethod().getName();
    return new Intercept(loggedClass, methodName, annotation.level(), annotation.perf());
  }

  private Object doInvoke(MethodInvocation invocation, Intercept intercept) throws Throwable {
    long start = intercept.perf ? System.nanoTime() : 0L;
    Object result;
    try {
      Log.log(intercept.level, intercept.clazz, "Enter: %s", intercept.classAndMethod);
      result = invocation.proceed();
      if(intercept.perf) {
        long end = System.nanoTime();
        Log.log(intercept.level, intercept.clazz, "Leave: %s [%d] µs", intercept.methodName, (int)((end - start)/1E3));
      } else {
        Log.log(intercept.level, intercept.clazz, "Leave: %s", intercept.classAndMethod);
      }
    } catch (Throwable throwable) {
      Log.log(intercept.level, intercept.clazz, "Leave: %s (Exception thrown: %s)",
              intercept.classAndMethod,
              throwable.getMessage());
      throw throwable; // rethrow the exception, lest we break semantics.
    }
    return result;
  }

  private static class Intercept {

    final Class clazz;
    final String methodName;
    final Level level;
    final boolean perf;
    final boolean enabled;
    final String classAndMethod;

    Intercept(Class<?> clazz, String methodName, Level level, boolean perf) {
      this.clazz = clazz;
      this.methodName = methodName;
      this.level = level;
      this.perf = perf;
      enabled = Log.enabled(clazz, level);
      this.classAndMethod = enabled ? clazz.getSimpleName() + '#' + methodName : "";
    }
  }
}
 
