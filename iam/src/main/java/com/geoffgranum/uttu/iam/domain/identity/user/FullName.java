/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.user;

import static com.geoffgranum.uttu.core.base.Verify.hasMaxLength;
import static com.geoffgranum.uttu.core.base.Verify.isNotEmpty;

public class FullName {

  public final String familyName;
  public final String givenName;

  public FullName(String givenName, String familyName) {
    isNotEmpty(givenName, "Given name is required.");
    hasMaxLength(givenName, 100, "Given name must be 100 characters or less.");
    isNotEmpty(familyName, "Family name is required.");
    hasMaxLength(familyName, 100, "Family name must be 100 characters or less.");
    this.givenName = givenName;
    this.familyName = familyName;
  }

  public String asGivenFamilyFormattedName() {
    return givenName + " " + familyName;
  }

  public String asFamilyGivenFormattedName() {
    return familyName + " " + givenName;
  }

  public FullName withChangedGivenName(String givenName) {
    return new FullName(givenName, familyName);
  }

  public FullName withChangedFamilyName(String familyName) {
    return new FullName(givenName, familyName);
  }

  @Override
  public boolean equals(Object anObject) {
    boolean equalObjects = false;

    if (anObject != null && this.getClass() == anObject.getClass()) {
      FullName typedObject = (FullName) anObject;
      equalObjects =
          this.givenName.equals(typedObject.givenName) &&
              familyName.equals(typedObject.familyName);
    }

    return equalObjects;
  }

  @Override
  public int hashCode() {
    int result = familyName.hashCode();
    result = 31 * result + givenName.hashCode();
    return result;
  }
}
 
