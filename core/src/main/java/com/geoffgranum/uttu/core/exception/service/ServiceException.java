/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.exception.service;

import com.geoffgranum.uttu.core.exception.FormattedException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ServiceException is intended to indicate an exceptional case occurring within the scope of a remote service call, such as a REST call.
 * Typically one would throw specific subclasses of this type, but it can also be used directly to wrap other exceptions.
 *
 * ServiceExceptions can safely bubble to the response. They are mapped by ServiceExceptionMapper, and will be logged if there is no attempt to handle them
 * prior to returning a response to the original request.
 *
 * Stack Traces ARE NOT ENABLED for service exceptions by default. This means that creating a service exception using an unknowable message is a bad idea.
 * For example, when catching an Exception and rethrowing it as a ServiceException, be sure to enable logging, like so:
 *
 * throw new ServiceException(e, "An error message that has meaning and that you can actually search for").enableStackTrace();
 *
 * Failure to do so will result in incredibly difficult to find errors, as only the message will be printed to the logs by default.
 *
 * @author Geoff M. Granum
 */
public class ServiceException extends FormattedException implements HttpResponseAware {

  private static final long serialVersionUID = 1L;

  private static final int STATUS_CODE_UNSET = Integer.MIN_VALUE;

  private final AtomicInteger statusCode = new AtomicInteger(STATUS_CODE_UNSET);

  public ServiceException(String msgFormat, Object... args) {
    this(null, msgFormat, args);
  }

  public ServiceException(Throwable cause) {
    this(cause, STATUS_CODE_UNSET, cause.getMessage());
  }

  public ServiceException(Throwable cause, String msgFormat, Object... args) {
    this(cause, STATUS_CODE_UNSET, msgFormat, args);
  }

  public ServiceException(int statusCode, String msgFormat, Object... args) {
    this(null, statusCode, msgFormat, args);
    this.statusCode.set(statusCode);
  }

  public ServiceException(Throwable cause, int statusCode, String msgFormat, Object... args) {
    super(cause, msgFormat, args);
    this.statusCode.set(statusCode);
    this.disableStackTrace();
  }

  @Override
  public int statusCode() {
    return this.statusCode.get();
  }

  @Override
  public String getHttpResponseMessage() {
    return getMessage();
  }

  public boolean hasStatusCode() {
    return this.statusCode.get() != STATUS_CODE_UNSET;
  }
}
 
