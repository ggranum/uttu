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
  BigInteger nextId();

  @Nonnull
  <T extends TypedId<?>> T nextId(Class<T> type);

  /**
   * Mongo uses 12 bytes, our default generator uses 16. Our timestamp is millisecond resolution, Mongo's is 1 second.
   * This method must return a hex string that can be accepted by MongoDb's 'new ObjectId(String hex)' constructor.
   */
  @Nonnull
  String asMongo(BigInteger id);

}
