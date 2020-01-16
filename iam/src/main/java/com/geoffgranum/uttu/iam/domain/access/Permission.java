/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access;

import java.math.BigInteger;

public class Permission {

  private final BigInteger id;
  private final String name;

  public Permission(BigInteger id, String name) {
    this.id = id;
    this.name = name;
  }

  public BigInteger getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
 
