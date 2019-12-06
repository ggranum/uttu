/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.exception;

/**
 * @author ggranum
 */
public interface CustomFaultCode
{

  String getCustomFaultCode();

  CustomFaultCode withCustomCode(String code);
}
