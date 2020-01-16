/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.geoffgranum.uttu.iam.domain.authentication;

import com.fasterxml.jackson.annotation.JsonValue;
import com.geoffgranum.uttu.core.persistence.id.Identifier;

import javax.annotation.concurrent.Immutable;
import java.math.BigInteger;

@Immutable
public final class TenantId implements Identifier {

  public final BigInteger value;

  public TenantId(BigInteger value) {
    this.value = value;
  }

  @Override
  public BigInteger value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    boolean objectsEqual = false;
    if (o != null && this.getClass() == o.getClass()) {
      TenantId that = (TenantId) o;
      objectsEqual = value.equals(that.value);
    }
    return objectsEqual;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @JsonValue
  @Override
  public String toString() {
    return value.toString();
  }
}
