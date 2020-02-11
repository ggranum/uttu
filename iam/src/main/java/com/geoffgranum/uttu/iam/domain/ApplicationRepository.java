/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.geoffgranum.uttu.iam.domain;

import com.geoffgranum.uttu.core.base.VersionInfo;

/**
 * @author Geoff M. Granum
 */
public interface ApplicationRepository {

  String getName();

  void drop();

  void initialize();

  void migrate(VersionInfo versionInfo, VersionInfo codeVersion);
}
