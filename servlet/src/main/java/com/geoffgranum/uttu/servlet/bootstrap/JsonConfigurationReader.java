/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.core.exception.FatalException;
import org.apache.commons.text.StringSubstitutor;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * The JsonConfigurationReader is intended for parsing configuration files specifically. More specifically, it is
 * intended for parsing configuration files at startup-time.
 * As such it performs property expansion, and on failure it throws FatalExceptions.
 * <p>
 * Files read by this class will have substitution parameters replaced, in a manner modeled after Log4j2.
 * See http://logging.apache.org/log4j/2.x/manual/configuration.html#PropertySubstitution
 * <p>
 * The actual processing of substitutions is provided b {@link org.apache.commons.text.StringSubstitutor},
 * specifically {@link org.apache.commons.text.StringSubstitutor#createInterpolator()}
 * <p>
 * Some examples:
 * <p>
 * ${env:KEY} Looks up KEY from System.getenv() map.
 * ${sys:KEY} Looks up KEY from System.getProperties() map.
 * ${date:yyyy-MM-dd} Formats the current date.
 * <p>
 * <p>
 * <p>
 * Thus, a JSON file containing
 *
 * <code>
 * {
 * "somePassword": "MyFooVar${env:MY_DB_PASSWORD}"
 * }
 * </code>
 * <p>
 * would attempt to lookup "MY_DB_PASSWORD" from System.getenv().
 * <p>
 * Default values can be provided by appending ":-" within the substitution string:
 *
 * <code>
 * {
 * "maxDbConnectionsWithDefault": "${sysEnv:MAX_DB_CONNECTIONS:-8}"
 * }
 * </code>
 * <p>
 * For more examples, see "Using Interpolation" in the StringSubstitutor class documentation.
 *
 * @author ggranum
 */
@NotThreadSafe
public class JsonConfigurationReader {

    private final Env env;
    private final ObjectMapper mapper;
    private final StringSubstitutor interpolatorSub = StringSubstitutor.createInterpolator();

    @Inject
    public JsonConfigurationReader(Env env, ObjectMapper mapper) {
        this.env = env;
        this.mapper = mapper;
        interpolatorSub.setEnableSubstitutionInVariables(true);
    }

    public JsonConfigurationReader(Env env) {
        this(env, new ObjectMapper());
    }

    public <T> T read(File jsonFile, T typeInstance) {
        //noinspection unchecked
        return (T) this.read(jsonFile, typeInstance.getClass());
    }

    public <T> T read(InputStream jsonStream, Class<T> type) throws IOException {
        return this.mapper.readValue(jsonStream, type);
    }

    public <T> T read(File jsonFile, Class<T> type) {
        String content = readJsonFile(jsonFile);
        return read(content, type);
    }

    public <T> T read(String jsonString, T type) {
        //noinspection unchecked
        return (T) read(jsonString, type.getClass());
    }

    public <T> T read(String jsonString, Class<T> type) {
        String json = replaceLookups(jsonString);
        T config;
        try {
            config = mapper.readValue(json, type);
        } catch (IOException e) {
            throw new FatalException(e,
                    "Could not parse JSON config for env '%s'.",
                    env.key);
        }

        return config;
    }

    public String readJsonFile(File file) {
        String content;
        try {
            byte[] contentBytes = Files.readAllBytes(file.toPath());
            content = new String(contentBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new FatalException(e,
                    "Could not read config file for env '%s' from file at path '%s'",
                    env.key,
                    file.getAbsolutePath());
        }
        Verify.isNotEmpty(content,
                FatalException.class,
                "Could not read config file for env '%s' from file at path '%s'",
                env.key,
                file.getAbsolutePath());

        return content;
    }

    /**
     * Replaces matching substitution strings. Modeled after Log4j2.
     * See http://logging.apache.org/log4j/2.x/manual/configuration.html#PropertySubstitution
     * <p>
     * ${env:KEY} Looks up KEY from System.getenv() map.
     * ${sys:KEY} Looks up KEY from System.getProperties() map.
     *
     * @param content The raw json content.
     * @return the provided content with any substitution strings replaced by the values found in the relevant context.
     */
    private String replaceLookups(String content) {
        StringBuilder result = new StringBuilder(content);
        interpolatorSub.replaceIn(result);
        return result.toString();
    }
}
