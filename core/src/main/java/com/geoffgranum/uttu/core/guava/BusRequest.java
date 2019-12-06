/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.guava;

import javax.annotation.Nullable;

public class BusRequest {

  private final long requestId;

  public BusRequest() {
    this(null);
  }

  public BusRequest(@Nullable Long requestId) {
    this.requestId = requestId == null ? createId(this.hashCode()) : requestId;
  }

  private static long createId(int hashCode) {
    long millis = System.currentTimeMillis();
    millis = millis << 4;
    return millis | hashCode;
  }

  public long getId() {
    return requestId;
  }
}
 
