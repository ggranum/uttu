/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.persistence.id;

import com.google.common.primitives.Ints;
import org.testng.annotations.Test;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

/**
 * @author ggranum
 */
public class MongoStyleIdGeneratorTest {

  @Test
  public void testSearchForMacReturnsAnAddress() throws Exception {
    byte[] x = MongoStyleIdGenerator.searchNetworksForMacAddress();
    assertThat(x.length, is(6));
  }


  @Test
  public void testCanConvertToMongoStyleByteArray() throws Exception {
    long now = System.currentTimeMillis();

    BigInteger id = new MongoStyleIdGenerator().nextId();
    byte[] mongoId = MongoStyleIdGenerator.toMongoDb(id);
    assertThat(mongoId.length, is(12));
    int seconds = Ints.fromByteArray(mongoId);
    assertThat("Time difference should be 1 second or less.", (long) (seconds) * 1000L - now, lessThan(2L));
  }
}
