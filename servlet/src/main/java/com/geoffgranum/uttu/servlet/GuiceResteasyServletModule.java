/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet;

import com.geoffgranum.uttu.core.log.Log;
import com.geoffgranum.uttu.servlet.exception.ServiceExceptionMapper;
import com.google.common.collect.Maps;
import com.google.inject.servlet.ServletModule;
import java.util.Map;
import java.util.Optional;
import javax.servlet.Filter;
import org.jboss.resteasy.jsapi.JSAPIServlet;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public abstract class GuiceResteasyServletModule extends ServletModule {

  private static final String REST_SERVLET_PATH_ROOT = "/*";
  private final Optional<String> restJsPath;

  public GuiceResteasyServletModule() {
    this(Optional.empty());
  }

  public GuiceResteasyServletModule(Optional<String> restJsPath) {
    this.restJsPath = restJsPath;
  }

  @Override
  protected void configureServlets() {
    bindResources();
    configureResteasy();
    configureSecurityFilter();
  }

  private void configureSecurityFilter() {
    Optional<Class<Filter>> authFilterClass = getAuthFilterClass();
    authFilterClass.ifPresent(filterClass -> filter("/*").through(filterClass));
  }

  public Optional<Class<Filter>> getAuthFilterClass() {
    return Optional.empty();
  }

  protected abstract void bindResources();

  private void configureResteasy() {
    bind(ServiceExceptionMapper.class).asEagerSingleton();
    /* bind jackson converters for JAXB/JSON serialization */
    //        bind(ResteasyJaxbJsonProvider.class);
    bind(GuiceResteasyBootstrap.class).asEagerSingleton();

    // In the unlikely event that you need servlet debug logging, swap the following two lines and update 'serve' call:
    //bind(LoggingHttpServletDispatcher.class).asEagerSingleton();
    bind(HttpServletDispatcher.class).asEagerSingleton();

    Map<String, String> initParams = getInitParams();
    Log.debug(getClass(), "Serving %s with RestEasy.", initParams.get("resteasy.servlet.mapping.prefix"));
    configureRestEasyJavascriptApiServlet();
    serve(getUrlPattern()).with(HttpServletDispatcher.class, initParams);
  }

  public Map<String, String> getInitParams() {
    Map<String, String> initParams = Maps.newHashMap();
    // Should Resteasy scan for REST annotated Services and Beans ? No.
    initParams.put("resteasy.scan", "false");
    // Turn off caching
    initParams.put("cachingAllowed", "false");
    // Turn off monitoring for @RolesAllows on Service methods.
    initParams.put("resteasy.role.based.security", "false");
    // Make it so that the requester can force a media type by adding an extension.
    initParams.put("resteasy.media.type.mappings",
                   "html : text/html, json : application/json, xml : application/xml");

    // serve rest resources from "/":
    initParams.put("resteasy.servlet.mapping.prefix", "/");

    return initParams;
  }

  /**
   * Override to change root path.
   */
  public String getUrlPattern() {
    return REST_SERVLET_PATH_ROOT;
  }

  private void configureRestEasyJavascriptApiServlet() {
    if(restJsPath.isPresent()) {
      bind(JSAPIServlet.class).asEagerSingleton();
      serve("/rest-js").with(JSAPIServlet.class);
    }
  }
}
 
