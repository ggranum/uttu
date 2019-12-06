/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet;

import com.geoffgranum.uttu.core.log.Log;
import com.google.inject.Binding;
import com.google.inject.servlet.GuiceServletContextListener;
import java.lang.reflect.Type;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

public abstract class ResteasyGuiceServletContextListener extends GuiceServletContextListener {

  private ResteasyDeployment deployment;

  @Override
  public void contextInitialized(ServletContextEvent event) {
    super.contextInitialized(event);
    doGuiceResteasyBootstrap(event);
  }

  private void doGuiceResteasyBootstrap(ServletContextEvent event) {
    doResteasyBootstrapContextInitialized(event);
    scanForResourcesAndProviders(event);
  }

  private void doResteasyBootstrapContextInitialized(ServletContextEvent event) {
    ServletContext servletContext = event.getServletContext();

    ListenerBootstrap config = new ListenerBootstrap(event.getServletContext());
    deployment = config.createDeployment();
    deployment.start();

    servletContext.setAttribute(ResteasyProviderFactory.class.getName(), deployment.getProviderFactory());
    servletContext.setAttribute(Dispatcher.class.getName(), deployment.getDispatcher());
    servletContext.setAttribute(Registry.class.getName(), deployment.getRegistry());
  }

  private void scanForResourcesAndProviders(ServletContextEvent event) {
    final ServletContext context = event.getServletContext();
    final Registry registry = (Registry)context.getAttribute(Registry.class.getName());
    final ResteasyProviderFactory providerFactory =
        (ResteasyProviderFactory)context.getAttribute(ResteasyProviderFactory.class.getName());

    for (final Binding<?> binding : getInjector().getBindings().values()) {
      final Type type = binding.getKey().getTypeLiteral().getType();
      if(type instanceof Class) {
        final Class<?> beanClass = (Class)type;
        if(GetRestful.isRootResource(beanClass)) {
          final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
          Log.debug(getClass(), "registering factory for %s", beanClass.getName());
          registry.addResourceFactory(resourceFactory);
        }
        if(beanClass.isAnnotationPresent(Provider.class)) {
          Log.debug(getClass(), "registering provider instance for %s", beanClass.getName());
          providerFactory.registerProviderInstance(binding.getProvider().get());
        }
      }
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    if(deployment != null) {
      deployment.stop();
    }
  }
}
 
