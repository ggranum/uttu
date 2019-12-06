/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.guava.messages;

/**
 * Mostly just so that we can find all of  the Guava messages in the system with a single 'find usages'.
 */
public abstract class StatusMessage {

  private boolean handled;
  public final boolean isError;

  protected StatusMessage(boolean isError) {
    this.isError = isError;
  }

  public void markHandled() {
    handled = true;
  }

  public boolean handled() {
    return handled;
  }
}
 
