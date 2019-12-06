/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.gradle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Check for the presence of build properties, mark keys as required, etc.
 * @author Geoff M. Granum
 */
public class BuildProps implements Plugin<Project> {

  private final Map<String, Object> props = new HashMap<>();
  private Project project;
  private BuildPropsExtension buildPropsExt;

  @Override
  public void apply(Project project) {
    this.project = project;
    this.buildPropsExt = new BuildPropsExtension();
    this.project.getExtensions().add("buildProps", buildPropsExt);
    this.project.task("initProps");

    props.putAll(System.getenv());
    props.putAll(project.getExtensions().getExtraProperties().getProperties());
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getPropertiesAsMap(Map props) {
    return (Map<String, Object>)(props);
  }

  private void initAndAdd(BuildProp buildProp) {
    Object value = props.get(buildProp.key);
    String sysPropValue = System.getProperty(buildProp.key);
    if(sysPropValue != null) {
      if(value != null) {
        System.out.println(
                              "Using System Property value for " + buildProp.key +
                              ". Previous value was '" + value.toString() + "'. Value is now '" + sysPropValue + "'.");
      }
      value = sysPropValue;
    }
    if(value != null) {
      System.out.println("Adding property: " + buildProp.key + " = " + value.toString());
      project.getExtensions().getExtraProperties().set(buildProp.key, value);
    } else if(buildProp.required) {
      throw new RuntimeException("Missing required property: " + buildProp.key);
    } else {
      System.out.println("Optional property not present: " + buildProp.key);
      project.getExtensions().getExtraProperties().set(buildProp.key, "");
    }
  }

  public class BuildPropsExtension {

    public void optional(String key) {
      BuildProps.this.initAndAdd(new BuildProp(key, false));
    }

    public void optional(List<String> keys) {
      for (String key : keys) {
        optional(key);
      }
    }

    public void required(String key) {
      BuildProps.this.initAndAdd(new BuildProp(key, true));
    }

    public void required(List<String> keys) {
      for (String key : keys) {
        required(key);
      }
    }

    public void source(Object propertyFile) {
      this.source(propertyFile, false);
    }

    public void source(Object propertyFile, boolean failIfMissing) {
      File file = BuildProps.this.project.file(propertyFile);
      if(file.exists()) {
        Properties overrides = new Properties();
        try {
          overrides.load(new FileReader(file));
          props.putAll(getPropertiesAsMap(overrides));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else if(failIfMissing) {
        throw new RuntimeException("Local overrides file doesn't exist.");
      }
    }
  }

  private static class BuildProp {

    public final String key;
    public final boolean required;

    private BuildProp(String key, boolean required) {
      this.key = key;
      this.required = required;
    }
  }
}
 
