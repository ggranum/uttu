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
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

public abstract class TypedId<T> implements Comparable<TypedId<T>>, Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty
  private final BigInteger id;

  public TypedId(String stringId) {
    this(new BigInteger(stringId));
  }

  public TypedId(BigInteger id) {
    this.id = id;
  }

  /**
   * Mongo uses 12 bytes, we use 14, as our timestamp is millisecond resolution, theirs is 1 second resolution.
   */
  public static @Nonnull
  byte[] asMongo(@Nonnull BigInteger id) {
    byte[] in = id.toByteArray();
    byte[] out = new byte[12]; // Mongo is 12 bytes, we are 14.
    long millis = Longs.fromBytes(in[0], in[1], in[2], in[3], in[4], in[5], in[6], in[7]);
    byte[] seconds = Ints.toByteArray((int) (millis / 1000));
    out[0] = seconds[0];
    out[1] = seconds[1];
    out[2] = seconds[2];
    out[3] = seconds[3];
    System.arraycopy(in, 6, out, 4, 8);
    return out;
  }

  public byte[] asMongo() {
    return TypedId.asMongo(this.id);
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

  public String toString() {
    return String.format("%s:%s", getClass().getSimpleName(), String.valueOf(id));
  }

  public BigInteger value() {
    return id;
  }


  public static class TypedIdSerializer<T extends TypedId> extends StdSerializer<T> {

    public TypedIdSerializer(Class<T> idType) {
      super(idType);
    }

    @Override
    public void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
      jgen.writeString(value.value().toString());
    }
  }
}
 
