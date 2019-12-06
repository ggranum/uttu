/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet;

import com.geoffgranum.uttu.core.base.Initializer;
import com.geoffgranum.uttu.core.exception.FatalException;
import com.geoffgranum.uttu.core.log.Log;
import com.google.inject.Injector;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;

public class UttuServletContextListener extends ResteasyGuiceServletContextListener {

  private final int httpPort;
  private final int httpsPort;
  private final Injector injector;

  public UttuServletContextListener(int httpPort, int httpsPort, Injector injector) {
    this.httpPort = httpPort;
    this.httpsPort = httpsPort;
    this.injector = injector;
  }

  /**
   * Please don't call this method manually. You really shouldn't need direct access to the injector (as you can inject it in most scenarios).
   */
  @Override
  protected Injector getInjector() {
    Log.trace(getClass(), "Injector requested.");
    if(injector == null) {
      throw new FatalException("Request for injector before contextInitialized: Injector is still null!");
    }
    return injector;
  }

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    Log.debug(getClass(), "GuiceServletContext initializing.");
    Initializer initializer = injector.getInstance(Initializer.class);
    try {
      Log.info(getClass(), "Initializing application."); // and such.
      initializer.init();
      Log.info(getClass(), "Application initialized. Enabling web services.");
    } catch (Exception e) {
      throw new FatalException(e, "Fatal exception while initializing application.");
    }
    super.contextInitialized(servletContextEvent);
    Log.info(getClass(), "Web services enabled on ports %s (http) and %s (https). Application is ready for requests.", httpPort, httpsPort);
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    Log.info(getClass(), "Service is shutting down.");
    super.contextDestroyed(servletContextEvent);
    deregisterDatasourceDrivers();
    Log.info(getClass(), "Service shutdown complete.");
  }

  private void deregisterDatasourceDrivers() {
    try {
      Enumeration<Driver> drivers = DriverManager.getDrivers();
      while (drivers.hasMoreElements()) {
        DriverManager.deregisterDriver(drivers.nextElement());
      }
    } catch (Exception e) {
      Log.debug(getClass(), e, "Deregistration of drivers failed. This isn't anything to worry about.");
    }
    Log.debug(getClass(), "Deregistration of JDBC drivers complete.");
  }

}
