/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.persistence.id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
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

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || getClass() != o.getClass()) {
      return false;
    }

    TypedId id1 = (TypedId)o;

    if(!id.equals(id1.id)) {
      return false;
    }
    return true;
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
    public void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws
        IOException,
            JsonProcessingException {
      jgen.writeString(value.value().toString());
    }
  }
}
 
