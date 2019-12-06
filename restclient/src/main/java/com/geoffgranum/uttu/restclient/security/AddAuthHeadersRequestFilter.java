/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.restclient.security;

import com.geoffgranum.uttu.core.log.Log;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import org.apache.commons.codec.binary.Base64;

/**
 * @author Geoff M. Granum
 */
public class AddAuthHeadersRequestFilter implements ClientRequestFilter {

  private final String username;
  private final String password;

  public AddAuthHeadersRequestFilter(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public void filter(ClientRequestContext requestContext) throws IOException {
    String token = username + ":" + password;
    String base64Token = Base64.encodeBase64String(token.getBytes(StandardCharsets.UTF_8));
    requestContext.getHeaders().add("Authorization", "Basic " + base64Token);
    Log.info(getClass(), "Added auth basic header to request: %s %s.",
             requestContext.getMethod(),
             requestContext.getUri());
  }
}
 
