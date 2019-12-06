/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.restclient;

import java.io.IOException;
import java.util.Map;
import javax.ws.rs.client.ClientRequestContext;

/**
 * @author Geoff M. Granum
 */
public class AddHeadersClientRequestFilter extends LoggingClientRequestFilter {

  private final Map<String, String> headers;

  public AddHeadersClientRequestFilter(Map<String, String> headers) {
    this.headers = headers;
  }

  @Override
  public void filter(ClientRequestContext requestContext) throws IOException {
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      requestContext.getHeaders().putSingle(entry.getKey(), entry.getValue());
    }
    super.filter(requestContext);
  }
}
 
