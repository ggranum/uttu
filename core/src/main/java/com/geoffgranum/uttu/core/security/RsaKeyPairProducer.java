/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * @author Geoff M. Granum
 */
public class RsaKeyPairProducer {

  public RsaKeyPair getAPair() {
    KeyPairGenerator keyGen;
    try {
      keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(2048);
      KeyPair kp = keyGen.genKeyPair();
      KeyFactory fact = KeyFactory.getInstance("RSA");
      RSAPublicKeySpec pubKey = fact.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
      RSAPrivateKeySpec privateKey = fact.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);
      return new RsaKeyPair(new RsaKey(pubKey.getModulus(), pubKey.getPublicExponent(), false),
                            new RsaKey(privateKey.getModulus(), privateKey.getPrivateExponent(), true));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }
}
 
