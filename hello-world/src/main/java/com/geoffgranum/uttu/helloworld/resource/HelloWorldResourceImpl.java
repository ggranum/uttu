/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.helloworld.resource;

import com.geoffgranum.uttu.core.base.VersionInfo;
import com.geoffgranum.uttu.core.log.Level;
import com.geoffgranum.uttu.core.log.Log;
import com.geoffgranum.uttu.core.log.intercept.Logged;
import com.geoffgranum.uttu.helloworld.domain.LoggingInterceptorPerfInfo;
import com.geoffgranum.uttu.helloworld.domain.ServerPerformanceInfo;
import com.geoffgranum.uttu.servlet.bootstrap.Env;
import java.util.Random;
import javax.inject.Inject;

/**
 * @author ggranum
 */
public class HelloWorldResourceImpl implements HelloWorldResource {

  private static final Random random = new Random();
  private final Env env;
  private final VersionInfo appVersion;
  private int N = 100000;

  @Inject
  HelloWorldResourceImpl(Env env, VersionInfo appVersion) {
    this.env = env;
    this.appVersion = appVersion;
  }

  @Logged(level = Level.INFO, perf = true)
  @Override
  public String hello(String whatever) {
    return "Hello " + whatever;
  }

  @Logged(level = Level.INFO, perf = true)
  @Override
  public String helloQuery(String whatever) {
    return "Hello Query " + whatever;
  }

  @Logged(level = Level.INFO, perf = true)
  @Override
  public ServerPerformanceInfo perfCheck(String anyValue) {
    LoggingInterceptorPerfInfo logInfo = new LoggingInterceptorPerfInfo.Builder()
                                             .withDefaults(runTestWithDefaults(anyValue)[1])
                                             .withPerfEnabled(runTestPerfWithIntercept()[1])
                                             .suppressedByLogLevel(runTestSuppressedByLogLevel()[1])
                                             .notInterceptedLogStatementsOnly(runTestNotInterceptedLogStatementsOnly(anyValue)[1])
                                             .build();
    ServerPerformanceInfo info = new ServerPerformanceInfo.Builder().logging(logInfo).build();
    return info;
  }

  private long[] runTestWithDefaults(String whatever) {
    long sum = 0;
    long t1 = System.nanoTime();
    for (int i = 0; i < N; i++) {
      sum += testWithDefaults(whatever);
    }
    long t2 = System.nanoTime();
    long delta = t2 - t1;
    return new long[]{sum, delta / N};
  }

  private long[] runTestPerfWithIntercept() {
    long sum = 0;
    long t1 = System.nanoTime();
    for (int i = 0; i < N; i++) {
      sum += testPerfWithIntercept(t1, System.nanoTime());
    }
    long t2 = System.nanoTime();
    long delta = t2 - t1;
    return new long[]{sum, delta / N};
  }

  private long[] runTestSuppressedByLogLevel() {
    long sum = 0;
    long t1 = System.nanoTime();
    for (int i = 0; i < N; i++) {
      sum += testSuppressedByLogLevel(t1, System.nanoTime());
    }
    long t2 = System.nanoTime();
    long delta = t2 - t1;
    return new long[]{sum, delta / N};
  }

  private long[] runTestNotInterceptedLogStatementsOnly(String whatever) {
    long sum = 0;
    long t1 = System.nanoTime();
    for (int i = 0; i < N; i++) {
      sum += testNotInterceptedLogStatementsOnly(whatever);
    }
    long t2 = System.nanoTime();
    long delta = t2 - t1;
    return new long[]{sum, delta / N};
  }

  @Logged(level = Level.INFO, perf = true)
  private int testPerfWithIntercept(long start, long end) {
    return random.nextInt();
  }

  @Logged(level = Level.TRACE, perf = true)
  private int testSuppressedByLogLevel(long start, long end) {
    Log.trace(HelloWorldResourceImpl.class, "Enter: %s [%d]", "testPerf", (end - start));
    Log.trace(HelloWorldResourceImpl.class, "Leave: %s [%d]", "testPerf", (end - start));
    return random.nextInt();
  }

  @Logged(level = Level.DEBUG)
  private int testWithDefaults(String whatever) {
    return random.nextInt();
  }

  private int testNotInterceptedLogStatementsOnly(String whatever) {
    Log.info(HelloWorldResourceImpl.class, "Enter: %s", whatever);
    Log.info(HelloWorldResourceImpl.class, "Leave: %s", whatever);
    return random.nextInt();
  }

  /**
   *
   */
  private long[] testMNanoTimeGenerator() {
    long sum = 0;
    int N = 100000000;
    long t1 = System.nanoTime();
    for (int i = 0; i < N; i++) {
      sum += System.nanoTime();
    }
    return new long[]{sum, System.nanoTime() - t1};
  }

  @Logged(level = Level.INFO, perf = true)
  @Override
  public String version() {
    return appVersion.toString() + " in environment mode " + env.key;
  }
}
