/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.persistence.id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

public class TypedId<T> implements Identifier, Comparable<TypedId<T>>, Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty
  private final BigInteger id;

  public TypedId(@Nonnull String hexId) {
    this(new BigInteger(hexId, 16));
  }

  public TypedId(@Nonnull BigInteger id) {
    this.id = id;
  }

  /**
   * The (up to) 16 byte value that is our identifier.
   *
   * @return A valid identifier.
   */
  @Nonnull
  public BigInteger value() {
    return id;
  }


  @Nonnull
  public String toString() {
    return String.format("%s:%s", getClass().getSimpleName(), toHexString());
  }

  /**
   * Convenience method for clarity. Provides the hexadecimal value of this identifier.
   * It is NOT possible to reconstruct which Class this identifier was generated with from the value returned by this method.
   *
   * @return A valid JSON representation of this ID.
   */
  @Nonnull
  public String toJson() {
    return toHexString();
  }

  @Nonnull
  public String toHexString() {
    return id.toString(16);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    //noinspection rawtypes
    TypedId id1 = (TypedId) o;

    return id.equals(id1.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public int compareTo(TypedId<T> o) {
    return this.id.compareTo(o.id);
  }


  public static class TypedIdSerializer<T extends TypedId<?>> extends StdSerializer<T> {

    public TypedIdSerializer(Class<T> idType) {
      super(idType);
    }

    @Override
    public void serialize(T identifier, JsonGenerator jgen, SerializerProvider provider) throws IOException {
      jgen.writeString(identifier.toJson());
    }
  }
}
 
