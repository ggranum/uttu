/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access.role;

import com.geoffgranum.uttu.core.persistence.id.Identifier;

import javax.annotation.concurrent.Immutable;
import java.math.BigInteger;

/**
 * @author Geoff M. Granum
 */
@Immutable
public final class RoleId implements Identifier {

  public final BigInteger value;

  public RoleId(BigInteger value) {
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
      RoleId that = (RoleId) o;
      objectsEqual = value.equals(that.value);
    }
    return objectsEqual;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
 
