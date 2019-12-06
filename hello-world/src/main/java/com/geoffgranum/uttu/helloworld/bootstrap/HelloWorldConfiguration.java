/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.helloworld.bootstrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geoffgranum.uttu.core.exception.FormattedException;
import com.geoffgranum.uttu.core.validation.Validated;
import com.geoffgranum.uttu.servlet.bootstrap.BootstrapConfiguration;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * @author ggranum
 */
@JsonDeserialize(builder = HelloWorldConfiguration.Builder.class)
public final class HelloWorldConfiguration implements BootstrapConfiguration {

  public final @NotNull @Length(min = 1) String env;
  public final @Min(1) @Max(65535) int httpPort;
  public final @Min(1) @Max(65535) int httpsPort;
  public final Optional<String> dbUrl;
  public final @Min(0) @Max(65535) int dbPort;
  public final @Length(min = 2, max = 20) String dbName;
  public final boolean wipeDatabase;
  public final Optional<String> adminPassword;
  public final Optional<String> adminName;
  public final @Length(min = 1, max = 200) Optional<String> jettyHome;
  public final Set<String> someStringSet;

  private HelloWorldConfiguration(Builder builder) {
    env = builder.env;
    httpPort = builder.httpPort;
    httpsPort = builder.httpsPort;
    dbUrl = Optional.ofNullable(builder.dbUrl);
    dbPort = builder.dbPort;
    dbName = builder.dbName;
    wipeDatabase = builder.wipeDatabase;
    adminPassword = Optional.ofNullable(builder.adminPassword);
    adminName = Optional.ofNullable(builder.adminName);
    jettyHome = Optional.ofNullable(builder.jettyHome);
    someStringSet = ImmutableSet.copyOf(builder.someStringSet);
  }

  public String toJson(ObjectMapper mapper) {
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new FormattedException(e, "Could not write HelloWorldConfiguration as Json");
    }
  }

  @Override
  public int httpPort() {
    return this.httpPort;
  }

  @Override
  public int httpsPort() {
    return this.httpsPort;
  }

  @Override
  public String jettyHome() {
    return this.jettyHome.orElse("./");
  }


  public static HelloWorldConfiguration fromJson(ObjectMapper mapper, String json) {
    try {
      return mapper.readValue(json, HelloWorldConfiguration.class);
    } catch (IOException e) {
      // This will be verbose, but without it we won't know the cause of the fatal exception.
      throw new FormattedException(e, "Could not create instance from provided JSON.\n\n %s \n\n", json);
    }
  }

  public static final class Builder extends Validated {

    @JsonProperty private @NotNull @Length(min = 1) String env;
    @JsonProperty private @Min(1) @Max(65535) Integer httpPort = 0;
    @JsonProperty private @Min(1) @Max(65535) Integer httpsPort = 0;
    @JsonProperty private String dbUrl;
    @JsonProperty private @Min(0) @Max(65535) Integer dbPort = 0;
    @JsonProperty private @Length(min = 2, max = 20) String dbName;
    @JsonProperty private Boolean wipeDatabase = false;
    @JsonProperty private String adminPassword;
    @JsonProperty private String adminName;
    @JsonProperty private String jettyHome;
    @JsonProperty private Set<String> someStringSet;

    public Builder() {
    }

    public Builder env(String env) {
      this.env = env;
      return this;
    }

    public Builder httpPort(int httpPort) {
      this.httpPort = httpPort;
      return this;
    }

    public Builder httpsPort(int httpsPort) {
      this.httpsPort = httpsPort;
      return this;
    }

    public Builder dbUrl(String dbUrl) {
      this.dbUrl = dbUrl;
      return this;
    }

    public Builder dbPort(int dbPort) {
      this.dbPort = dbPort;
      return this;
    }

    public Builder dbName(String dbName) {
      this.dbName = dbName;
      return this;
    }

    public Builder wipeDatabase(boolean wipeDatabase) {
      this.wipeDatabase = wipeDatabase;
      return this;
    }

    public Builder adminPassword(String adminPassword) {
      this.adminPassword = adminPassword;
      return this;
    }

    public Builder adminName(String adminName) {
      this.adminName = adminName;
      return this;
    }

    public Builder jettyHome(String jettyHome) {
      this.jettyHome = jettyHome;
      return this;
    }

    public Builder someStringSet(Set<String> someStringSet) {
      this.someStringSet = someStringSet;
      return this;
    }

    public HelloWorldConfiguration build() {
      checkValid();
      return new HelloWorldConfiguration(this);
    }
  }
}
