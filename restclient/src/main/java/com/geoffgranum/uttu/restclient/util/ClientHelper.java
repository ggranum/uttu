/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.restclient.util;

import com.geoffgranum.uttu.restclient.CapturingClientResponseFilter;
import com.geoffgranum.uttu.restclient.security.AddAuthHeadersRequestFilter;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * @author ggranum
 */
public class ClientHelper {

  public static <T> T createResource(String baseUri, Class<T> resourceClass) {
    return createResource(baseUri, resourceClass, "", "");
  }

  public static <T> T createResource(String baseUri, Class<T> resourceClass, String userName, String password) {
    Client client = createClient(userName, password);
    client.register(new CapturingClientResponseFilter());
    return proxyResource(client, baseUri, resourceClass);
  }

  public static Client createClient(String username, String password) {
    Client client = ClientBuilder.newClient();
    client.register(new AddAuthHeadersRequestFilter(username, password));

    return client;
  }

  public static <T> T proxyResource(Client client, String baseUri, Class<T> resourceToProxy) {
    ResteasyWebTarget target = (ResteasyWebTarget) client.target(baseUri);
    return target.proxy(resourceToProxy);
  }
}
