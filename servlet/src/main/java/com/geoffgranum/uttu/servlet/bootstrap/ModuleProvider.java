/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.google.inject.Module;
import java.util.Set;

/**
 * @author ggranum
 */
public interface ModuleProvider {

  abstract Set<Module> get(Env env, BootstrapConfiguration baseConfiguration);
}
