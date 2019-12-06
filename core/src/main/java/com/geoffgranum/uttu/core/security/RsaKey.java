/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.security;

import java.math.BigInteger;

/**
 * @author Geoff M. Granum
 */
public final class RsaKey {

  private final boolean isPrivate;
  private final BigInteger modulus;
  private final BigInteger exponent;

  public RsaKey(BigInteger modulus, BigInteger exponent, boolean isPrivate) {
    this.isPrivate = isPrivate;
    this.modulus = modulus;
    this.exponent = exponent;
  }
}
 
