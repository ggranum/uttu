/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access.exception;

import com.geoffgranum.uttu.core.exception.service.ServiceException;
import com.geoffgranum.uttu.core.http.HttpStatus;
import com.geoffgranum.uttu.core.log.Level;
import com.geoffgranum.uttu.core.log.Log;
import com.geoffgranum.uttu.iam.domain.access.Subject;

/**
 * @author Geoff M. Granum
 */
public class PermissionDeniedException extends ServiceException {

  public PermissionDeniedException(String resource, Subject subject) {
    super(HttpStatus.FORBIDDEN.code, "Permission Denied. Username: %s.", subject.user.username);
    Log.warn(getClass(), "User attempted to access forbidden resource. Subject: %s. Resource", subject, resource);
  }

  @Override
  public boolean shouldPrintStack() {
    return false;
  }

  @Override
  public Level getLogLevel() {
    return Level.INFO;
  }
}
 
