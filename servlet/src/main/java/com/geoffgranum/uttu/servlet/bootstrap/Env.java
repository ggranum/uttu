/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.geoffgranum.uttu.core.exception.FatalException;
import java.util.HashMap;
import java.util.Map;

/**
 * Adds a bit of safety to the server startup process by restricting certain actions.
 *
 * It is always possible to configure your 'Development' environment properties file with
 * that of Production in order to go out and do stupid things. This isn't security, it's
 * a safety lock on a handgun.
 *
 * @author Geoff M. Granum
 */
public final class Env {

  private static final Map<String, Env> all = new HashMap<>();

  public static final Env DEV_LOCAL = new Env("dev_local", true, true);
  public static final Env DEVELOPMENT = new Env("dev", true, true);
  public static final Env CI = new Env("ci", true, true);
  public static final Env QA = new Env("qa", false, true);
  public static final Env PRODUCTION = new Env("prod", false, false);


  public final String key;
  public final boolean allowStartWithCleanDatabase;
  public final boolean allowUpdateAdminAtStartup;


  public Env(String key, boolean allowStartWithCleanDatabase, boolean allowUpdateAdminAtStartup) {
    this.key = key;
    this.allowStartWithCleanDatabase = allowStartWithCleanDatabase;
    this.allowUpdateAdminAtStartup = allowUpdateAdminAtStartup;
    all.put(key, this);
  }

  public static Env valueOf(String key){
    if(!all.containsKey(key)){
      throw new FatalException("An environment with key '%s' has not been defined.", key);
    }
    return all.get(key);
  }

}
