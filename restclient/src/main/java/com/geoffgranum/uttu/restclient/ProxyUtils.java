/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.restclient;

import com.geoffgranum.uttu.core.log.Log;
import com.geoffgranum.uttu.restclient.security.AddAuthHeadersRequestFilter;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Optional;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * @author Geoff M. Granum
 */
public class ProxyUtils {

  private static final Optional<SSLContext> SSL_CONTEXT_OPT = provideSSLContextIfKeyStoreIsAvailable();

  private static Optional<SSLContext> provideSSLContextIfKeyStoreIsAvailable() {
    try {
      Optional<SSLContext> sslContextOptional = Optional.empty();
      if(System.getProperty("javax.net.ssl.keyStore") != null) {
        TrustManagerFactory tmFactory = TrustManagerFactory.getInstance("PKIX");

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(System.getProperty("javax.net.ssl.keyStore")),
                System.getProperty("javax.net.ssl.keyStorePassword").toCharArray()
        );
        tmFactory.init(ks);

        // Set up key manager factory to use our key store
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());

        KeyManager[] km = kmf.getKeyManagers();
        TrustManager[] tm = tmFactory.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

        sslContext.init(km, tm, null);
        SSLContext.setDefault(sslContext);

        String[] protocols = System.getProperty("https.protocols").split(",");
        String[] ciphers = System.getProperty("https.cipherSuites").split(",");
        SSLParameters sslParameters = sslContext.getSupportedSSLParameters();
        sslParameters.setProtocols(protocols);
        sslParameters.setCipherSuites(ciphers);

        sslContextOptional = Optional.of(sslContext);
      }
      return sslContextOptional;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T proxyResource(String baseUri, Class<T> resourceToProxy, String username, String password) {
    Client client;
    if(SSL_CONTEXT_OPT.isPresent()) {
      ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
      client = clientBuilder.sslContext(SSL_CONTEXT_OPT.get()).build();
    } else {
      client = ClientBuilder.newClient();
    }
    client.register(new AddAuthHeadersRequestFilter(username, password));
    ResteasyWebTarget target = (ResteasyWebTarget)client.target(baseUri);
    Log.debug(ProxyUtils.class, "Creating proxy for resource '%s', through the URI: '%s'.",
              resourceToProxy.getSimpleName(),
              target.getUri());
    return target.proxy(resourceToProxy);
  }
}

 
