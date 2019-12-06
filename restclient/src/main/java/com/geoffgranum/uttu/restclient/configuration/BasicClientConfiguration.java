/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.restclient.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geoffgranum.uttu.core.exception.FormattedException;
import com.geoffgranum.uttu.core.validation.Validated;
import com.geoffgranum.uttu.restclient.RestClientConfig;
import java.io.IOException;

/**
 * @author ggranum
 */
@JsonDeserialize(builder = BasicClientConfiguration.Builder.class)
public final class BasicClientConfiguration implements RestClientConfig {

  public final String host;
  public final boolean isHttps;
  public final int port;
  public final String userName;
  public final String password;

  public BasicClientConfiguration(String host, int port) {
    this(host, port, isDefinitelyProbablySecureMaybe(port));
  }

  public BasicClientConfiguration(String host, int port, boolean secure) {
    this(host, port, secure, "", "");
  }

  public BasicClientConfiguration(String host, int port, boolean secure, String userName, String password) {
    this.host = host;
    this.port = port;
    this.isHttps = secure;
    this.userName = userName;
    this.password = password;
  }

  private BasicClientConfiguration(Builder builder) {
    host = builder.host;
    isHttps = builder.isHttps;
    port = builder.port;
    userName = builder.userName;
    password = builder.password;
  }

  @Override
  public String username() {
    return userName;
  }

  @Override
  public String password() {
    return password;
  }

  @Override
  public boolean isHttps() {
    return this.isHttps;
  }

  @Override
  public String host() {
    return host;
  }

  @Override
  public int port() {
    return port;
  }

  public String toJson(ObjectMapper mapper) {
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new FormattedException(e, "Could not write BasicClientConfiguration as Json");
    }
  }

  private static boolean isDefinitelyProbablySecureMaybe(int port) {
    String pString = String.valueOf(port);
    return pString.endsWith("443") || pString.endsWith("81");
  }

  public static BasicClientConfiguration fromJson(ObjectMapper mapper, String json) {
    try {
      return mapper.readValue(json, BasicClientConfiguration.class);
    } catch (IOException e) {
      // This will be verbose, but without it we won't know the cause of the fatal exception.
      throw new FormattedException(e, "Could not create instance from provided JSON.\n\n %s \n\n", json);
    }
  }

  public static final class Builder extends Validated {

    @JsonProperty private String host;
    @JsonProperty private Boolean isHttps = false;
    @JsonProperty private Integer port = 0;
    @JsonProperty private String userName;
    @JsonProperty private String password;

    public Builder() {
    }

    public Builder host(String host) {
      this.host = host;
      return this;
    }

    public Builder isHttps(boolean isHttps) {
      this.isHttps = isHttps;
      return this;
    }

    public Builder port(int port) {
      this.port = port;
      return this;
    }

    public Builder userName(String userName) {
      this.userName = userName;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public BasicClientConfiguration build() {
      checkValid();
      return new BasicClientConfiguration(this);
    }
  }
}
