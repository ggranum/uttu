/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.persistence.id;

import com.google.common.primitives.Ints;
import org.apache.commons.codec.binary.Hex;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author ggranum
 */
public class DefaultIdGeneratorTest {

  @Test
  public void testSearchForMacReturnsAnAddress() throws Exception {
    byte[] x = DefaultIdGenerator.searchNetworksForMacAddress();
    assertThat(x.length, is(6));
  }

  @Test
  public void testCanConvertToMongoStyleByteArray() throws Exception {
    int now = (int) (System.currentTimeMillis() / 1000);
    DefaultIdGenerator idGen = new DefaultIdGenerator();
    String mongoId = idGen.asMongo(idGen.next());
    assertThat(mongoId.length(), is(24));
    // Reads the int from the beginning of the array, so it is in fact the time.
    int seconds = Ints.fromByteArray(Hex.decodeHex(mongoId.toCharArray()));
    assertThat("Time difference should be 1 second or less.", seconds - now, Matchers.allOf(lessThan(2), greaterThanOrEqualTo(0)));
  }
}
