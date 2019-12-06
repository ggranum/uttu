/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.geoffgranum.uttu.servlet.util.StaticFromJson;

/**
 * @author ggranum
 */
public interface BootstrapConfiguration extends StaticFromJson {

  int httpPort();

  int httpsPort();

  String jettyHome();

}
