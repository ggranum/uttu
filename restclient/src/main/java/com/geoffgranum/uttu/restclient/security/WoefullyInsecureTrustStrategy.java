/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.restclient.security;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.TrustStrategy;

/**
 * Work around for connecting to servers with self-signed certs.
 * We *can* (and will) improve the security by adding the certs to our keychain.
 */
public class WoefullyInsecureTrustStrategy implements TrustStrategy {

  @Override
  public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    return true;
  }
}
 
