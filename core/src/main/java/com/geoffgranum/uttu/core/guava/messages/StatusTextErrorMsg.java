/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.guava.messages;

/**
 * @author Geoff M. Granum
 */
public class StatusTextErrorMsg extends StatusMessage {

  public final String msg;
  public final Class invoker;

  public StatusTextErrorMsg(Class invoker, String msgFormat, Object... args) {
    super(true);
    this.invoker = invoker;
    this.msg = String.format(msgFormat, args);
  }
}
 
