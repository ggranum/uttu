/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.base;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author ggranum
 */
public class VersionInfoTest {


  @Test
  public void testIgnoresNewLines() {
    String test = "1.2.3\n";
    VersionInfo info = new VersionInfo.Builder().fromVersionString(test).build();
    assertThat(info.majorVersion, is(1));
    assertThat(info.minorVersion, is(2));
    assertThat(info.pointVersion, is(3));
    assertThat(info.buildNumber, is(0));
    assertThat(info.buildEnvironment.isPresent(), is(false));
  }

  @Test
  public void testMajorMinorPoint() {
    String test = "1.2.3";
    VersionInfo info = new VersionInfo.Builder().fromVersionString(test).build();
    assertThat(info.majorVersion, is(1));
    assertThat(info.minorVersion, is(2));
    assertThat(info.pointVersion, is(3));
    assertThat(info.buildNumber, is(0));
    assertThat(info.buildEnvironment.isPresent(), is(false));
  }

  @Test
  public void testMajorMinorPointBuild() {
    String test = "1.2.3.99";
    VersionInfo info = new VersionInfo.Builder().fromVersionString(test).build();
    assertThat(info.majorVersion, is(1));
    assertThat(info.minorVersion, is(2));
    assertThat(info.pointVersion, is(3));
    assertThat(info.buildNumber, is(99));
    assertThat(info.buildEnvironment.isPresent(), is(false));
  }

  @Test
  public void testMajorMinorPointDashEnv() {
    String test = "1.2.3-Bob";
    VersionInfo info = new VersionInfo.Builder().fromVersionString(test).build();
    assertThat(info.majorVersion, is(1));
    assertThat(info.minorVersion, is(2));
    assertThat(info.pointVersion, is(3));
    assertThat(info.buildNumber, is(0));
    assertThat(info.buildEnvironment.get(), is("Bob"));
  }


  @Test
  public void testMajorMinorPointBuildDashEnv() {
    String test = "1.2.3.000-Bob";
    VersionInfo info = new VersionInfo.Builder().fromVersionString(test).build();
    assertThat(info.majorVersion, is(1));
    assertThat(info.minorVersion, is(2));
    assertThat(info.pointVersion, is(3));
    assertThat(info.buildNumber, is(0));
    assertThat(info.buildEnvironment.get(), is("Bob"));

    test = "101.202.3111.9999-sam";
    info = new VersionInfo.Builder().fromVersionString(test).build();
    assertThat(info.majorVersion, is(101));
    assertThat(info.minorVersion, is(202));
    assertThat(info.pointVersion, is(3111));
    assertThat(info.buildNumber, is(9999));
    assertThat(info.buildEnvironment.get(), is("sam"));
  }
}
