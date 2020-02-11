/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity;

/**
 * @author Geoff M. Granum
 */
public interface EncryptionService {

  EncryptionToken encryptText(String plainText);

  EncryptionToken encryptText(String plainText, String salt);
}
