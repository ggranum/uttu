/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.helloworld.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author ggranum
 */
@JsonDeserialize(builder = LoggingInterceptorPerfInfo.Builder.class)
public final class LoggingInterceptorPerfInfo {

  public final long withDefaults;
  public final long suppressedByLogLevel;
  public final long withPerfEnabled;
  public final long notInterceptedLogStatementsOnly;

  private LoggingInterceptorPerfInfo(Builder builder) {
    withDefaults = builder.withDefaults;
    suppressedByLogLevel = builder.suppressedByLogLevel;
    withPerfEnabled = builder.withPerfEnabled;
    notInterceptedLogStatementsOnly = builder.notInterceptedLogStatementsOnly;
  }

  public static final class Builder {

    @JsonProperty private Long withDefaults = 0L;
    @JsonProperty private Long suppressedByLogLevel = 0L;
    @JsonProperty private Long withPerfEnabled = 0L;
    @JsonProperty private Long notInterceptedLogStatementsOnly = 0L;

    public Builder() {
    }

    public Builder withDefaults(long withDefaults) {
      this.withDefaults = withDefaults;
      return this;
    }

    public Builder suppressedByLogLevel(long suppressedByLogLevel) {
      this.suppressedByLogLevel = suppressedByLogLevel;
      return this;
    }

    public Builder withPerfEnabled(long withPerfEnabled) {
      this.withPerfEnabled = withPerfEnabled;
      return this;
    }

    public Builder notInterceptedLogStatementsOnly(long notInterceptedLogStatementsOnly) {
      this.notInterceptedLogStatementsOnly = notInterceptedLogStatementsOnly;
      return this;
    }

    public LoggingInterceptorPerfInfo build() {
      return new LoggingInterceptorPerfInfo(this);
    }
  }
}
