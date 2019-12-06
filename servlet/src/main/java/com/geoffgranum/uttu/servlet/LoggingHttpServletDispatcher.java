/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet;

import com.geoffgranum.uttu.core.log.Log;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyUriInfo;

public class LoggingHttpServletDispatcher extends HttpServletDispatcher {

  private static final long serialVersionUID = 1L;

  @Override
  public void init(ServletConfig servletConfig) throws ServletException {
    Log.trace(getClass(), "Initializing Servlet Dispatcher.");
    super.init(servletConfig);
    Log.trace(getClass(), "Servlet Dispatcher initialized.");
  }

  @Override
  public void destroy() {
    Log.trace(getClass(), "Destroying Servlet Dispatcher.");
    super.destroy();
    Log.trace(getClass(), "Servlet Dispatcher destroyed.");
  }

  @Override
  protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws
      ServletException,
          IOException {
    try {
      Log.trace(getClass(), "Servicing request: %s", httpServletRequest.getRequestURL());
    } catch (Exception e) {
      Log.error(getClass(), e, "Logging produced an error.");
    }
    super.service(httpServletRequest, httpServletResponse);
    try {
      Log.trace(getClass(), "Request serviced: %s", httpServletRequest.getRequestURL());
    } catch (Exception e) {
      Log.error(getClass(), e, "Logging produced an error.");
    }
  }

  @Override
  public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response) throws
      IOException {
    try {
      Log.trace(getClass(), "Servicing request: %s, %s", httpMethod, request.getRequestURL());
    } catch (Exception e) {
      Log.error(getClass(), e, "Logging produced an error.");
    }
    try {
      super.service(httpMethod, request, response);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    try {
      if(Log.traceEnabled(getClass())) {
        Log.trace(getClass(), "Request serviced: %s, %s", httpMethod, request.getRequestURL());
        if(response.getStatus() == HttpServletResponse.SC_NOT_FOUND) {
          Log.warn(getClass(), "Did you remember to bind the Response object type in the Servlet Module?");
        }
      }
    } catch (Exception e) {
      Log.error(getClass(), e, "Logging produced an error.");
    }
  }

  @Override
  public HttpRequest createResteasyHttpRequest(
      String httpMethod,
      HttpServletRequest request,
      ResteasyHttpHeaders headers,
      ResteasyUriInfo uriInfo,
      HttpResponse theResponse,
      HttpServletResponse response) {
    try {
      Log.trace(getClass(), "Create Resteasy Http Request: %s", request.getRequestURL());
    } catch (Exception e) {
      Log.error(getClass(), e, "Logging produced an error.");
    }
    return super.createResteasyHttpRequest(httpMethod, request, headers, uriInfo, theResponse, response);
  }

  @Override
  public HttpResponse createResteasyHttpResponse(HttpServletResponse response) {
    Log.trace(getClass(), "Create Resteasy Http Response");
    return super.createResteasyHttpResponse(response);
  }

  @Override
  protected HttpRequest createHttpRequest(
      String httpMethod,
      HttpServletRequest request,
      ResteasyHttpHeaders headers,
      ResteasyUriInfo uriInfo,
      HttpResponse theResponse,
      HttpServletResponse response) {
    Log.trace(getClass(), "createHttpRequest");
    return super.createHttpRequest(httpMethod, request, headers, uriInfo, theResponse, response);
  }

  @Override
  protected HttpResponse createServletResponse(HttpServletResponse response) {
    Log.trace(getClass(), "createServletResponse");
    return super.createServletResponse(response);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      Log.trace(getClass(), "doGet %s", req.getRequestURL());
    } catch (Exception e) {
      Log.error(getClass(), e, "Logging produced an error.");
    }
    super.doGet(req, resp);
  }

  @Override
  protected long getLastModified(HttpServletRequest req) {
    return super.getLastModified(req);
  }

  @Override
  protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.doHead(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      Log.trace(getClass(), "doPost %s", req.getRequestURL());
    } catch (Exception e) {
      Log.error(getClass(), e, "Logging produced an error.");
    }
    super.doPost(req, resp);
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      Log.trace(getClass(), "doPut %s", req.getRequestURL());
    } catch (Exception e) {
      Log.error(getClass(), e, "Logging produced an error.");
    }
    super.doPut(req, resp);
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.doDelete(req, resp);
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.doOptions(req, resp);
  }

  @Override
  protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.doTrace(req, resp);
  }

  @Override
  public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
    super.service(req, res);
  }
}
 
