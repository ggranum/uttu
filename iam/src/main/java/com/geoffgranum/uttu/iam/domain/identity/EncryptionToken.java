/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity;

import com.geoffgranum.uttu.core.exception.service.ServiceException;
import com.geoffgranum.uttu.core.security.EncryptionUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Geoff M. Granum
 */
public class EncryptionToken {

  public final String saltAsHexString;
  public final String hashAsHexString;

  private EncryptionToken(String saltAsHexString, String hashAsHexString) {
    checkNotNull(saltAsHexString, "Token salt is required.");
    checkNotNull(hashAsHexString, "Token hash is required.");
    this.saltAsHexString = saltAsHexString;
    this.hashAsHexString = hashAsHexString;
  }

  public static EncryptionToken from(String hexSalt, String hexHash) {
    return new EncryptionToken(hexSalt, hexHash);
  }

  public static EncryptionToken fromHex(String passwordHex, String saltHex) {
    return new EncryptionToken(saltHex, passwordHex);
  }

  public static EncryptionToken fromPasswordClearText(String passwordClearText) {
    byte[] saltBytes = EncryptionUtil.generateSalt(16);
    int hashIterationCount = ByteBuffer.wrap(saltBytes).getShort();

    byte[] hashedPassword = EncryptionUtil.sha512(
        passwordClearText,
        saltBytes,
        hashIterationCount);
    return new EncryptionToken(Hex.encodeHexString(saltBytes), Hex.encodeHexString(hashedPassword));
  }

  public static EncryptionToken fromPasswordClearText(String passwordClearText, String hexSalt) {
    try {
      byte[] saltBytes = Hex.decodeHex(hexSalt.toCharArray());
      int hashIterationCount = ByteBuffer.wrap(saltBytes).getShort();
      byte[] hashedPassword = EncryptionUtil.sha512(
          passwordClearText,
          saltBytes,
          hashIterationCount);
      return new EncryptionToken(hexSalt, Hex.encodeHexString(hashedPassword));
    } catch (DecoderException e) {
      throw new ServiceException(e, "Could not decode user's password salt.");
    }
  }

  @Override
  public boolean equals(Object o) {
    boolean objectsEqual = false;
    if (this == o) {
      objectsEqual = true;
    } else if (o != null && getClass() == o.getClass()) {
      EncryptionToken that = (EncryptionToken) o;
      if (hashAsHexString.equals(that.hashAsHexString) && saltAsHexString.equals(that.saltAsHexString)) {
        objectsEqual = true;
      }
    }
    return objectsEqual;
  }

  @Override
  public int hashCode() {
    int result = saltAsHexString.hashCode();
    result = 31 * result + hashAsHexString.hashCode();
    return result;
  }
}
 
