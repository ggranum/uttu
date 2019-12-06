/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet;

import com.geoffgranum.uttu.core.log.Log;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.lang.reflect.Type;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

public class GuiceResteasyBootstrap extends ResteasyBootstrap {

  private Injector injector;

  @Inject
  public GuiceResteasyBootstrap(Injector injector) {
    this.injector = injector;
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    super.contextInitialized(event);
    final ServletContext context = event.getServletContext();
    final Registry registry = (Registry)context.getAttribute(Registry.class.getName());
    final ResteasyProviderFactory providerFactory =
        (ResteasyProviderFactory)context.getAttribute(ResteasyProviderFactory.class.getName());

    for (final Binding<?> binding : injector.getBindings().values()) {
      final Type type = binding.getKey().getTypeLiteral().getType();
      if(type instanceof Class) {
        final Class<?> beanClass = (Class)type;
        if(GetRestful.isRootResource(beanClass)) {
          final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
          Log.debug(getClass(), "Registering resource factory for %s", beanClass.getName());
          registry.addResourceFactory(resourceFactory);
        }
        if(beanClass.isAnnotationPresent(Provider.class)) {
          Log.debug(getClass(), "Registering provider instance for %s", beanClass.getName());
          providerFactory.registerProviderInstance(binding.getProvider().get());
        }
      }
    }
  }
}

