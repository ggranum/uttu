/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.restclient;

import com.geoffgranum.uttu.core.log.Log;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

/**
 * @author Geoff M. Granum
 */
public class LoggingClientRequestFilter implements ClientRequestFilter {

  @Override
  public void filter(ClientRequestContext requestContext) throws IOException {
    Log.trace(getClass(), "Making request to: %s", requestContext.getUri().toString());
    for (Map.Entry<String, List<String>> entry : requestContext.getStringHeaders().entrySet()) {
      Log.trace(getClass(), "\tHeader: '%s'=%s", entry.getKey(), entry.getValue());
    }
  }
}
 
