/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.user;

import com.geoffgranum.uttu.core.base.Verify;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.Set;

/**
 * @author Geoff M. Granum
 */
public class BasicPasswordService {

  @VisibleForTesting
  protected static final int STRONG_THRESHOLD = 25;
  @VisibleForTesting
  protected static final int VERY_STRONG_THRESHOLD = 40;
  /* Pseudo-random is fine (vs. secure random): this is not for cryptographic functions. */
  private static final Random rand = new Random();

  public static boolean isStrong(String passwordClearText) {
    return calculatePasswordStrength(passwordClearText) >= STRONG_THRESHOLD;
  }

  public static boolean isVeryStrong(String passwordClearText) {
    return calculatePasswordStrength(passwordClearText) >= VERY_STRONG_THRESHOLD;
  }

  public static String generateStrongPassword() {
    return RandomStringUtils.randomAscii(10 + rand.nextInt(9));
  }

  public static boolean isWeak(String passwordClearText) {
    return calculatePasswordStrength(passwordClearText) < STRONG_THRESHOLD;
  }

  @VisibleForTesting
  protected static int calculatePasswordStrength(String passwordClearText) {

    Verify.isNotEmpty(passwordClearText, "Password is required.");
    int strength = 0;

    int length = passwordClearText.length();

    if (length > 8) {
      strength += 10;
      // bonus: one point each additional
      strength += (length - 8);
    }

    int digitCount = 0;
    int letterCount = 0;
    int lowerCount = 0;
    int upperCount = 0;
    int symbolCount = 0;

    char prevChar = '\0';
    int repeatCount = 0;
    int seqCount = 0;
    int invSeqCount = 0;
    Set<Character> uniqueChars = Sets.newHashSet();

    for (int idx = 0; idx < length; ++idx) {
      char ch = passwordClearText.charAt(idx);
      uniqueChars.add(ch);

      repeatCount = ch == prevChar ? repeatCount + 1 : 0;
      strength -= repeatCount;
      seqCount = (ch == prevChar + 1) ? seqCount + 1 : 0;
      strength -= seqCount;
      invSeqCount = (ch == prevChar - 1) ? invSeqCount + 1 : 0;
      strength -= invSeqCount;
      prevChar = ch;

      if (Character.isLetter(ch)) {
        ++letterCount;
        if (Character.isUpperCase(ch)) {
          ++upperCount;
        } else {
          ++lowerCount;
        }
      } else if (Character.isDigit(ch)) {
        ++digitCount;
      } else {
        ++symbolCount;
      }
    }

    strength += uniqueChars.size() * 2;

    // bonus: letters and digits
    if (upperCount != 0) {
      strength += 2;
    } else {
      strength -= 2;
    }
    if (lowerCount != 0) {
      strength += 2;
    } else {
      strength -= 2;
    }
    if (digitCount != 0) {
      strength += 2;
    } else {
      strength -= 2;
    }
    if (symbolCount != 0) {
      strength += 4;
    } else {
      strength -= 2;
    }

    return strength;
  }
}
 
