/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity;

import com.geoffgranum.uttu.core.base.Verify;

import static com.geoffgranum.uttu.core.base.Verify.isTrue;

/**
 * Defines a way to automatically disable a user after a particular time out.
 *
 * @author Geoff M. Granum
 */
public class Enablement {

  public final boolean enabled;
  public final Long startMils;
  public final Long endMils;

  public Enablement(boolean enabled, Long startMils, Long endMils) {
    Verify.isNotNull(startMils, "The start date must be provided.");
    Verify.isNotNull(endMils, "The end date must be provided.");
    isTrue(startMils < endMils, "Enablement start date must be before end date.");

    this.enabled = enabled;
    this.startMils = startMils;
    this.endMils = endMils;
  }


  public static Enablement indefiniteEnablement() {
    return new Enablement(true, System.currentTimeMillis(), Long.MAX_VALUE);
  }

  public boolean isEnablementEnabled() {
    boolean enabled = false;

    if (this.isEnabled()) {
      if (!this.isTimeExpired()) {
        enabled = true;
      }
    }

    return enabled;
  }

  public boolean isTimeExpired() {
    boolean timeExpired = false;
    long now = System.currentTimeMillis();
    if (now < this.startMils || now > (this.endMils)) {
      timeExpired = true;
    }

    return timeExpired;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public boolean equals(Object anObject) {
    boolean equalObjects = false;

    if (anObject != null && this.getClass() == anObject.getClass()) {
      Enablement typedObject = (Enablement) anObject;
      equalObjects =
          this.isEnabled() == typedObject.isEnabled() &&
              this.startMils.equals(typedObject.startMils) &&
              this.endMils.equals(typedObject.endMils);
    }

    return equalObjects;
  }

  @Override
  public int hashCode() {
    int hashCodeValue =
        +(19563 * 181)
            + (this.isEnabled() ? 1 : 0)
            + this.startMils.hashCode()
            + this.endMils.hashCode();

    return hashCodeValue;
  }

  @Override
  public String toString() {
    return "Enablement [enabled=" + enabled + ", endDate=" + endMils + ", startDate=" + startMils + "]";
  }
}
 
