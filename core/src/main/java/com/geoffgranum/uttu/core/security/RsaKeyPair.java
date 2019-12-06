/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.security;

/**
 * @author Geoff M. Granum
 */
public final class RsaKeyPair {

  private final RsaKey publicKey;
  private final RsaKey privateKey;

  public RsaKeyPair(RsaKey publicKey, RsaKey privateKey) {
    this.publicKey = publicKey;
    this.privateKey = privateKey;
  }
}
 
