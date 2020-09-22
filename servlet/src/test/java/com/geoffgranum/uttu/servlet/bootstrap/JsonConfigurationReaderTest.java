/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author ggranum
 */
public class JsonConfigurationReaderTest {

    @Test
    public void testSubstitutionSources() {
        System.setProperty("SYS_TEST_VALUE", "10");
        setEnvVarHack("ENV_TEST_VALUE", "11");
//        System.setProperty("DATE_TEST_VALUE", "${}");
        StringBuilder b = new StringBuilder("{ \n");
        b.append("\"foo\": ${sys:SYS_TEST_VALUE}, \n");
        b.append("\"bar\": ${env:ENV_TEST_VALUE}, \n");
        b.append("\"aDate\": \"${date:yyyy-MM-dd}\", \n");
        b.append("\"base64Encode\": \"${base64Encoder:base64TestValue}\",  \n");
        b.append("\"base64Decode\": \"${base64Decoder:YmFzZTY0VGVzdFZhbHVl}\" \n");
        b.append("}");

        Map<String, ?> map = new JsonConfigurationReader(Env.DEVELOPMENT).read(b.toString(), new HashMap<>());
        assertThat(map.get("foo"), is(10));
        assertThat(map.get("bar"), is(11));
        assertThat(map.get("base64Decode"), is("base64TestValue"));
        assertThat(map.get("base64Encode"), is("YmFzZTY0VGVzdFZhbHVl"));
        assertThat(map.get("aDate"), is(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
    }

    @Test
    public void testNestedSubstitution() {
        System.setProperty("SYS_TEST_VALUE", "10");
        System.setProperty("SYS_TEST_VALUE2", "11");
        StringBuilder b = new StringBuilder("{ \n");
        b.append("\"foo\": ${sys:MISSING:-${sys:SYS_TEST_VALUE}}, \n");
        b.append("\"bar\": ${sys:MISSING:-${sys:ALSO_MISSING:-${sys:SYS_TEST_VALUE2}}} \n");
        b.append("}");

        Map<String, ?> map = new JsonConfigurationReader(Env.DEVELOPMENT).read(b.toString(), new HashMap<>());
        assertThat(map.get("foo"), is(10));
        assertThat(map.get("bar"), is(11));
    }

    @Test
    public void testSubstituteWithDefault() {
        setEnvVarHack("ENV_TEST_VALUE", "99");
        System.setProperty("TEST_VALUE", "100");
        StringBuilder b = new StringBuilder("{ \n");
        b.append("\"bar\": ${env:ENV_TEST_VALUE},\n");
        b.append("\"foo\": ${env:DOES_NOT_EXIST:-500}, \n");
        b.append("\"baz\": 20.1\n");
        b.append("}");

        Map<String, ?> map = new JsonConfigurationReader(Env.DEVELOPMENT).read(b.toString(), new HashMap<>());
        assertThat(map.get("foo"), is(500));
        assertThat(map.get("bar"), is(99));
    }

    @Test
    public void testReadFromMap() {
        StringBuilder b = new StringBuilder("{ \n");
        b.append("\"foo\": 100, \n");
        b.append("\"bar\": \"someString\",\n");
        b.append("\"baz\": 20.1\n");
        b.append("}");

        try {
            File f = File.createTempFile("uttu.dev", ".json");
            f.deleteOnExit();
            FileWriter w = new FileWriter(f);
            w.write(b.toString());
            w.close();
            Map<String, ?> map = new JsonConfigurationReader(Env.DEVELOPMENT).read(f, new HashMap<>());
            assertThat(map.get("foo"), is(100));
            assertThat(map.get("bar"), is("someString"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEnvVarHack(String name, String val) {
        Map<String, String> env = System.getenv();
        try {
            Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            //noinspection unchecked
            ((Map<String, String>) field.get(env)).put(name, val);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
