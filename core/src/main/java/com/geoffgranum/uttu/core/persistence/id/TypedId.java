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
import org.apache.commons.codec.binary.Hex;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

public class TypedId<T extends Identified> implements Identifier, Comparable<TypedId<T>>, Serializable {

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
   * The value that is our identifier.
   *
   * @return A valid identifier.
   */
  @Nonnull
  public BigInteger value() {
    return id;
  }

  @Nonnull
  public String toString() {
    return String.format("%s:%s", getClass().getSimpleName(), toHex());
  }

  /**
   * Convenience method for clarity. Provides the hexadecimal value of this identifier.
   * It is NOT possible to reconstruct which Class this identifier was generated with from the value returned by this
   * method.
   *
   * @return A valid JSON representation of this ID.
   */
  @Nonnull
  public String toJson() {
    return toHex();
  }

  /**
   * Take care when using identifiers that are made of of sets of byte arrays that are then concatenated into a single
   * array: BigInteger truncates values.
   * @return
   */
  @Nonnull
  public String toHex() {
    return id.toString(16);
  }

  /**
   * You probably want IdGenerator#toHex(TypedId) instead of this method.
   * BigInteger will truncate leading zeros.
   * @param untruncatedLength The original, known byte length of the generated BigInteger value. Must be greater than
   *                          or equal to the actual value of `id`.
   * @return A hexadecimal string of `untruncatedLength` bytes.
   */
  @Nonnull
  public String toHex(int untruncatedLength) {
    byte[] allBytes = toBytes(untruncatedLength);
    return Hex.encodeHexString(allBytes);
  }

  /**
   * You probably want IdGenerator#toBytes(TypedId) instead of this method.
   * BigInteger will truncate leading zeros.
   * @param untruncatedLength The original, known byte length of the generated BigInteger value. Must be greater than
   *                          or equal to the actual value of `id`.
   * @return A byte array `untruncatedLength` bytes.
   */
  public byte[] toBytes(int untruncatedLength) {
    byte[] in = id.toByteArray();
    if (in.length > untruncatedLength) {
      throw new IllegalArgumentException("Truncating Identifiers is not supported. Untruncated length must be less than or equal to actual identifier length.");
    }
    // BigInteger#toByteArray() will truncate leading zeros. We have to add them back in by checking the difference
    // in the returned array size and the required `untruncatedLength` bytes we created the BigInteger with in the first place.
    byte[] allBytes = new byte[untruncatedLength];
    int startAt = untruncatedLength - in.length;
    System.arraycopy(in, 0, allBytes, startAt, in.length);
    return allBytes;
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
 
