/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.persistence.id;

import java.math.BigInteger;

/**
 * As simple or as complex an id as you like.
 */
public interface IdGenerator {

  /**
   * Implementations of this method MUST be distributed-database safe. Keys MUST be provided in numerically
   * increasing order, such that
   * : true == ( this.nextId() &lt; this.nextId() )
   * This must be universally true, (within a margin of error) so best just start the ID with the system
   * time in millis.
   *
   * @return A new, unique id.
   */
  public BigInteger nextId();

  public <T extends TypedId> T nextId(Class<T> type);
}
