/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.guava;

public abstract class DomainEvent {

  private final long occurredAt;
  private final int eventVersion;

  protected DomainEvent() {
    this(System.currentTimeMillis(), 1);
  }

  protected DomainEvent(long occurredAt, int eventVersion) {
    this.occurredAt = occurredAt;
    this.eventVersion = eventVersion;
  }

  public long occurredAt() {
    return occurredAt;
  }

  public long eventVersion() {
    return eventVersion;
  }
}
 
