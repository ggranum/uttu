/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.guava.messages;

public class ShutdownRequestMsg extends StatusMessage {

  private final String reason;

  public ShutdownRequestMsg(String reason) {
    super(false);
    this.reason = reason;
  }

  public String getReason() {
    return reason;
  }
}
 
