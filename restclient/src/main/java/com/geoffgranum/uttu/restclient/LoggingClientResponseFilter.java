/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.restclient;

import com.geoffgranum.uttu.core.log.Log;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import org.apache.commons.io.IOUtils;

/**
 * @author Geoff M. Granum
 */
public class LoggingClientResponseFilter implements ClientResponseFilter {

  @Override
  public void filter(
      ClientRequestContext requestContext,
      ClientResponseContext responseContext) throws IOException {
    InputStream stream = responseContext.getEntityStream();
    String response = IOUtils.toString(stream, StandardCharsets.UTF_8.name());
    stream.reset();
    Log.trace(getClass(), response);
  }
}
 
