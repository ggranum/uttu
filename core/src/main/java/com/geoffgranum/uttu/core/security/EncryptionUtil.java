/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.security;

import com.geoffgranum.uttu.core.exception.FatalException;
import com.geoffgranum.uttu.core.log.Log;
import com.google.common.base.Charsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Geoff Granum
 */
public final class EncryptionUtil {

  //
  public static final String AUTH_TOKEN_SEP_CHAR = "|";

  private static final String CIPHER = "AES/CBC/PKCS5Padding";

  private static final String KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";

  private static final String KEY_SPEC_ALGORITHM = "AES";

  private static final int HASH_ITERATIONS =
      Integer.parseInt(System.getProperty("com.geoffgranum.security.hash_iterations", "7459"));

  private static final int KEY_LENGTH_BITS = 256;

  private static final SecretKeyFactory SECRET_KEY_FACTORY;
  static {
    try {
      SECRET_KEY_FACTORY = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
    } catch (Exception e) {
      String msg = "Are the JCE Unlimited Strength Jurisdiction Policy Files installed?";
      Log.warn(EncryptionUtil.class, msg);
      throw new RuntimeException(msg, e);
    }
  }
  public static void checkJavaCryptographicExtensionsInstalled() {
    String anyValue = "randomMeaninglessString";
    SecretKey secretKey;
    try {
      secretKey = generateSecretKey(anyValue.toCharArray(), anyValue.getBytes());
      Cipher cipher = Cipher.getInstance(CIPHER);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey); // will explode here if JCE are missing.
    } catch (InvalidKeyException e) {
      String msg = "Are the JCE Unlimited Strength Jurisdiction Policy Files installed?";
      Log.warn(EncryptionUtil.class, msg);
      throw new FatalException(e, msg);
    } catch (GeneralSecurityException e) {
      throw new FatalException(e, "Unexpected error while checking for JCE libs.");
    }
  }

  private EncryptionUtil() {
  }

  public static String encrypt(String tokenClearText, char[] password, byte[] salt) throws GeneralSecurityException {
    SecretKey secretKey = generateSecretKey(password, salt);
    Cipher cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

    AlgorithmParameters params = cipher.getParameters();
    byte[] paramSpec = params.getParameterSpec(IvParameterSpec.class).getIV();

    byte[] clearTextBytes = Base64.encodeBase64(StringUtils.getBytesUtf8(tokenClearText));
    byte[] cipherBytes = cipher.doFinal(clearTextBytes);
    byte[] authTokenBytes = ArrayUtils.addAll(paramSpec, cipherBytes);

    return Base64.encodeBase64String(authTokenBytes);
  }

  public static String decrypt(String tokenString, char[] password, byte[] salt) throws GeneralSecurityException {
    int initVectorLength = 16;
    byte[] tokenBytes = Base64.decodeBase64(tokenString);
    byte[] paramSpec = Arrays.copyOfRange(tokenBytes, 0, initVectorLength);
    byte[] cipherBytes = Arrays.copyOfRange(tokenBytes, initVectorLength, tokenBytes.length);
    SecretKey secretKey = generateSecretKey(password, salt);
    Cipher cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(paramSpec));
    byte[] clearTextBytes = cipher.doFinal(cipherBytes);
    return StringUtils.newStringUtf8(Base64.decodeBase64(clearTextBytes));
  }

  private static SecretKey generateSecretKey(char[] password, byte[] salt) throws GeneralSecurityException {
    KeySpec pbeKeySpec = new PBEKeySpec(
        password,
        salt,
        HASH_ITERATIONS,
        KEY_LENGTH_BITS);
    SecretKey secretKey = SECRET_KEY_FACTORY.generateSecret(pbeKeySpec);
    return new SecretKeySpec(secretKey.getEncoded(), KEY_SPEC_ALGORITHM);
  }

  public static byte[] generateSalt(int byteCount) {
    SecureRandom sr = null;
    try {
      sr = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException e) {
      throw new FatalException(e, "Missing SHA1PRNG algorithm.");
    }
    byte[] salt = new byte[byteCount];
    sr.nextBytes(salt);
    return salt;
  }

  public static byte[] sha512(String passwordClearText, byte[] salt, int iterations) {

    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA-512");
    } catch (NoSuchAlgorithmException e) {
      throw new FatalException(e, "Missing SHA-512 algorithm.");
    }
    digest.reset();
    digest.update(salt);
    byte[] hashedPass = digest.digest(passwordClearText.getBytes(Charsets.UTF_8));
    for (int i = 0; i < iterations; i++) {
      digest.reset();
      hashedPass = digest.digest(hashedPass);
    }
    return hashedPass;
  }
}


