/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.restclient;

import com.geoffgranum.uttu.core.log.Log;
import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.TreeSet;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

public class ResponseLoggingFilter implements ClientResponseFilter {

  @Override
  public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext)
      throws IOException {
    String uri = URLDecoder.decode(requestContext.getUri().toString(), Charsets.UTF_8.name());
    Log.trace(getClass(), "Response:    %s    %s    %s", uri, requestContext.getMethod(), responseContext.getStatus());
    if(Log.traceEnabled(getClass())) {
      MultivaluedMap<String, String> headers = responseContext.getHeaders();
      TreeSet<String> keys = Sets.newTreeSet(headers.keySet());
      Log.trace(getClass(), "    HEADERS");
      for (String key : keys) {
        Log.trace(getClass(), "    %s: %s", key, headers.get(key));
      }
      Log.trace(getClass(), "    BODY");
    }
  }
}
 
