/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.persistence.id;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
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

  @Nonnull
  default <T extends Identified> TypedId<T> next(Class<T> type) {
    return new TypedId<T>(next());
  }

  /**
   * Provided a unique Id for concrete subclasses of TypedId.
   *
   * @param type The ID Type.
   * @param <T>  The datatype to which the returned ID will belong.
   * @return A new instance of T.
   */
  @Nonnull
  default <T extends TypedId<?>> T nextConcrete(Class<T> type) {
    T id;
    try {
      @SuppressWarnings("rawtypes") Constructor constructor = type.getConstructor(BigInteger.class);
      id = type.cast(constructor.newInstance(next()));
    } catch (Exception e) {
      /* Unless TypedId gets modified this won't happen. */
      throw new RuntimeException(e);
    }
    return id;
  }

  /**
   * Mongo uses 12 bytes, our default generator uses 16. Our timestamp is millisecond resolution, Mongo's is 1 second.
   * This method must return a hex string that can be accepted by MongoDb's 'new ObjectId(String hex)' constructor.
   * <p>
   * While MongoDB is a special case, it, or API compatible clones (e.g. Azure Cosmos), is fairly pervasive.
   */
  @Nonnull
  String asMongo(BigInteger id);

  @Nonnull
  default String asMongo(TypedId<?> id) {
    return asMongo(id.value());
  }

}
