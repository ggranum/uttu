/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoffgranum.uttu.core.validation.ValidationException;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.fail;

/**
 * @author ggranum
 */
public class EnvOrFileSourcedConfigurationReaderTest {

  @Test
  public void testCanReadFromMap() {

    EnvOrFileSourcedConfigurationReader<DefaultBootstrapConfiguration> uttu = new EnvOrFileSourcedConfigurationReader<>("uttu",
                                                                                                                         Env.DEVELOPMENT,
                                                                                                                         "./config",
                                                                                                                         "uttu",
                                                                                                                         DefaultBootstrapConfiguration.class,
                                                                                                                         new ObjectMapper());

    Map<String, String> values = ImmutableMap.<String, String>builder()
                                     .put("httpPort", "8080")
                                     .put("httpsPort", "8043")
                                     .put("env", "dev")
                                     .put("logDir", "./")
                                     .build();
    DefaultBootstrapConfiguration cfg = uttu.from(values);
    assertThat(cfg.httpsPort, is(8043));
  }

  @Test(expectedExceptions = ValidationException.class)
  public void testConfigurationEnforcesConstraints() {
    EnvOrFileSourcedConfigurationReader<DefaultBootstrapConfiguration> uttu = new EnvOrFileSourcedConfigurationReader<>("uttu",
                                                                                                                         Env.DEVELOPMENT,
                                                                                                                         "./config",
                                                                                                                         "uttu",
                                                                                                                         DefaultBootstrapConfiguration.class,
                                                                                                                         new ObjectMapper());

    Map<String, String> values = ImmutableMap.<String, String>builder()
                                     .put("httpPort", "8080")
                                     .build();
    DefaultBootstrapConfiguration cfg = uttu.from(values);
    fail("Should not get here.");
  }

  @Test
  public void testCanReadFromFile() {

  }
}
