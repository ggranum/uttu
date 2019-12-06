/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.base;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Geoff M. Granum
 */
public class VersionInfo {

  public final int majorVersion;
  public final int minorVersion;
  public final int pointVersion;
  public final int buildNumber;
  public final Optional<String> buildEnvironment;

  private VersionInfo(Builder builder) {
    majorVersion = builder.majorVersion;
    minorVersion = builder.minorVersion;
    pointVersion = builder.pointVersion;
    buildNumber = builder.buildNumber;
    buildEnvironment = Optional.ofNullable(builder.buildEnvironment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(majorVersion, minorVersion, pointVersion, buildNumber);
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final VersionInfo other = (VersionInfo)obj;
    return Objects.equals(this.majorVersion, other.majorVersion)
           && Objects.equals(this.minorVersion,
                             other.minorVersion)
           && Objects.equals(this.pointVersion, other.pointVersion)
           && Objects.equals(this.buildNumber, other.buildNumber);
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder().append(majorVersion)
                          .append(".").append(minorVersion)
                          .append(".").append(pointVersion)
                          .append(".").append(buildNumber);
    buildEnvironment.ifPresent(s -> b.append("-").append(s));
    return b.toString();
  }

  public static final class Builder {

    private int majorVersion;
    private int minorVersion;
    private int pointVersion;
    private int buildNumber;
    private String buildEnvironment;

    public Builder() {
    }

    public Builder fromVersionString(String versionString) {
      Verify.isNotEmpty(versionString, "Version String cannot be empty.");

      String[] tokens = versionString.split("[.-]");
      Verify.isTrue(tokens.length > 2, IllegalStateException.class,
                    "Version number must be of the form 'a.b.c.[d[-e]]', where a,b,c and d are all numeric and " +
                    "represent major, minor, point and build number, respectively. 'e' is optional and may be a string" +
                    "that represents the environment that the build was generated in."
      );
      majorVersion = Integer.parseInt(tokens[0]);
      minorVersion = Integer.parseInt(tokens[1]);
      pointVersion = Integer.parseInt(tokens[2]);
      if(tokens.length == 5 || (tokens.length == 4 && !versionString.contains("-"))) {
        buildNumber = Integer.parseInt(tokens[3]);
      }
      if(tokens.length == 5 || (tokens.length == 4 && versionString.contains("-"))) {
        buildEnvironment = tokens[tokens.length - 1];
      }

      return this;
    }

    public Builder majorVersion(int majorVersion) {
      this.majorVersion = majorVersion;
      return this;
    }

    public Builder minorVersion(int minorVersion) {
      this.minorVersion = minorVersion;
      return this;
    }

    public Builder pointVersion(int pointVersion) {
      this.pointVersion = pointVersion;
      return this;
    }

    public Builder buildEnvironment(String buildEnvironment) {
      this.buildEnvironment = buildEnvironment;
      return this;
    }

    public Builder buildNumber(int buildNumber) {
      this.buildNumber = buildNumber;
      return this;
    }

    public VersionInfo build() {
      return new VersionInfo(this);
    }
  }
}
 
