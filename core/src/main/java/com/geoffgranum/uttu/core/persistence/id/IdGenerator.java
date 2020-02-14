/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.persistence.id;

import javax.annotation.Nonnull;
import java.math.BigInteger;

/**
 * As simple or as complex an id as you like.
 */
public interface IdGenerator {

  /**
   * Implementations of this method MUST be distributed-database safe. Keys MUST be provided in numerically
   * increasing order, such that
   * : true == ( this.nextId() &lt; this.nextId() )
   * This must be universally true, (within a margin of error) so best just prefix the ID with the system
   * time in millis and end with a local counter, with something specific to each machine in between.
   * <p>
   * MUST be thread safe, at least to within the margin of error of your application's needs.
   *
   * @return A new, unique id.
   */
  @Nonnull
  BigInteger next();

  /**
   * Implementations of this method MUST be distributed-database safe. Keys MUST be provided in numerically
   * increasing order, such that
   * : true == ( this.nextId() &lt; this.nextId() )
   * This must be universally true, (within a margin of error) so best just prefix the ID with the system
   * time in millis and end with a local counter, with something specific to each machine in between.
   * <p>
   * MUST be thread safe, at least to within the margin of error of your application's needs.
   *
   * @return A new, unique id as a Hexadecimal String.
   */
  @Nonnull
  String nextHex();

  /**
   * BigInteger truncates leading zeros, so if an id is constructed out of sets of byte arrays, reconstructing that
   * array becomes difficult unless we track the original length.
   * @return The number of bytes that went into the construction of the BigInteger identifier.
   */
  int idByteLength();

  /**
   * Returns the un-truncated hex value of the `id` provided.
   *
   * @param id An id backed by a BigInteger value.
   * @return The hex string representation, including any leading zero values that the BigInteger value may have
   * truncated.
   */
  default String toHex(TypedId<?> id) {
    return id.toHex(this.idByteLength());
  }

  default byte[] toBytes(TypedId<?> id) {
    return id.toBytes(this.idByteLength());
  }

  @Nonnull
  default <T extends Identified> TypedId<T> next(Class<T> type) {
    return new TypedId<T>(next());
  }

}
