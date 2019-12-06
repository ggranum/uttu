/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author ggranum
 */
public class JsonConfigurationReaderTest {

  @Test
  public void testReadFromMap() {
    File f = new File("uttu.dev.json");
    StringBuilder b = new StringBuilder("{ \n");
    b.append("\"foo\": 100, \n");
    b.append("\"bar\": \"someString\",\n");
    b.append("\"baz\": 20.1\n");
    b.append("}");

    try {
      FileWriter w = new FileWriter(f);
      w.write(b.toString());
      w.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    f.deleteOnExit();

    Map<String, String> map = new JsonConfigurationReader(Env.DEVELOPMENT).read(f, new HashMap<>());
    assertThat(map.get("foo"), is(100));
    assertThat(map.get("bar"), is("someString"));
  }
}
