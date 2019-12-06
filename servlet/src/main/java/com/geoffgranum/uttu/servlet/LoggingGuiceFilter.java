/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet;

import com.geoffgranum.uttu.core.log.Log;
import com.google.inject.servlet.GuiceFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;

public class LoggingGuiceFilter extends GuiceFilter {

  public LoggingGuiceFilter() {
    super();
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
      IOException,
          ServletException {
    try {
      Log.trace(getClass(), "Filtering request: %s", ((HttpServletRequest)servletRequest).getRequestURL());
    } catch (Exception e) {
      Log.error(getClass(), e, "Bad Log. No donut.");
    }
    try {
      super.doFilter(servletRequest, servletResponse, filterChain);
    } catch (IOException | ServletException e) {
      Log.error(getClass(), e, "Error filtering request. ");
      throw e;
    } catch (NotFoundException e) {
      throw new NotFoundException("Did you forget to add a binding in your servlet module? " + e.getMessage(), e);
    }

    try {
      Log.trace(getClass(), "Request filtered: %s", ((HttpServletRequest)servletRequest).getRequestURL());
    } catch (Exception e) {
      Log.error(getClass(), e, "Bad Log. No donut.");
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    Log.trace(getClass(), "Initializing filter");
    try {
      super.init(filterConfig);
    } catch (ServletException e) {
      Log.error(getClass(), e, "Error while initializing filter.");
      throw e;
    }
    Log.trace(getClass(), "Filter initialized.");
  }

  @Override
  public void destroy() {
    Log.trace(getClass(), "Destroying filter.");
    try {
      super.destroy();
    } catch (RuntimeException e) {
      Log.error(getClass(), e, "Error while destroying filter.");
      throw e;
    }
    Log.trace(getClass(), "Filter destroyed.");
  }
}
 
