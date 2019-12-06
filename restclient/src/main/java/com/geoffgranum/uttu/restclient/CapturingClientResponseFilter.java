/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.restclient;

import com.geoffgranum.uttu.core.log.Log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import org.apache.commons.io.IOUtils;

/**
 * @author Geoff M. Granum
 */
public class CapturingClientResponseFilter extends LoggingClientResponseFilter {

  private String responseBody;
  private ClientRequestContext requestContext;
  private ClientResponseContext responseContext;

  public String getResponseBody() {
    return responseBody;
  }

  public ClientRequestContext getRequestContext() {
    return requestContext;
  }

  public ClientResponseContext getResponseContext() {
    return responseContext;
  }

  @Override
  public void filter(
      ClientRequestContext requestContext,
      ClientResponseContext responseContext) throws IOException {
    this.requestContext = requestContext;
    this.responseContext = responseContext;
    InputStream stream = responseContext.getEntityStream();
    // Stream can be null if response is empty.
    if(stream != null) {
      byte[] bytes = IOUtils.toByteArray(stream);
      responseBody = new String(bytes, StandardCharsets.UTF_8);
      responseContext.setEntityStream(new ByteArrayInputStream(bytes));
      Log.debug(getClass(), responseBody);
    }
  }
}
 
