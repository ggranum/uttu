/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet.util;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Register as a singleton in you ApplicationModule:
 * bind(GuiceAllowAllCorsFilter.class).in(Scopes.SINGLETON);
 */
@Provider
@PreMatching
public class GuiceAllowAllCorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

  private final CorsFilter filter;

  public GuiceAllowAllCorsFilter() {
    CorsFilter filter = new CorsFilter();
    filter.getAllowedOrigins().add("*");
    this.filter = filter;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    filter.filter(requestContext);
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    filter.filter(requestContext, responseContext);
  }
}
