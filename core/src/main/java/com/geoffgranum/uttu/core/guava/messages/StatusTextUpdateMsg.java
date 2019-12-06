/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.guava.messages;

/**
 * @author Geoff M. Granum
 */
public class StatusTextUpdateMsg extends StatusMessage {

  public final String msg;
  public final Class invoker;

  public StatusTextUpdateMsg(Class invoker, String msgFormat, Object... args) {
    super(false);
    this.invoker = invoker;
    this.msg = String.format(msgFormat, args);
  }

  protected StatusTextUpdateMsg(Class invoker, boolean isError, String msgFormat, Object... args) {
    super(isError);
    this.invoker = invoker;
    this.msg = String.format(msgFormat, args);
  }
}
 
