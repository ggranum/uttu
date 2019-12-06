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
@JsonDeserialize(builder = ServerPerformanceInfo.Builder.class)
public final class ServerPerformanceInfo {

  public final LoggingInterceptorPerfInfo logging;

  private ServerPerformanceInfo(Builder builder) {
    logging = builder.logging;
  }

  public static final class Builder {

    @JsonProperty private LoggingInterceptorPerfInfo logging;

    public Builder() {
    }

    public Builder logging(LoggingInterceptorPerfInfo logging) {
      this.logging = logging;
      return this;
    }

    public ServerPerformanceInfo build() {
      return new ServerPerformanceInfo(this);
    }
  }
}
