/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.persistence.id;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author ggranum
 */
public class MongoStyleIdGeneratorTest {

  @Test
  public void testSearchForMacReturnsAnAddress() throws Exception {
    byte[] x = MongoStyleIdGenerator.searchNetworksForMacAddress();
    assertThat(x.length, is(6));
  }
}
