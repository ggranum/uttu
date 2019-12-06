/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.geoffgranum.uttu.core.base.Initializer;
import com.geoffgranum.uttu.core.exception.ApplicationInitializationException;
import com.geoffgranum.uttu.core.log.Log;
import com.geoffgranum.uttu.servlet.initialization.InitializationChain;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Init and start the initialization chain.
 * @author Geoff M. Granum
 */
@Singleton
public final class ApplicationInitializer implements Initializer {

  private final EventBus domainBus;

  private final InitializationChain initChain;
  private final Injector injector;
  private final AtomicBoolean initialized = new AtomicBoolean(false);
  private final AtomicBoolean initInProgress = new AtomicBoolean(false);

  @Inject
  private ApplicationInitializer(EventBus domainBus, InitializationChain initChain, Injector injector) {
    this.domainBus = domainBus;
    this.initChain = initChain;
    this.injector = injector;
    this.domainBus.register(this);
  }

  @Subscribe
  public void handleDeadEvent(DeadEvent event) {
    Log.warn(getClass(), "Dead event: %s", event.getEvent().toString());
  }

  @Override
  public synchronized final void init() {
    if (!initInProgress.getAndSet(true)) {
      try {
        this.initChain.init(injector);
        initChain.doInitialize();
        initialized.set(true);
      } catch (ApplicationInitializationException e) {
        domainBus.post(e);
        throw e;
      } catch (Exception e) {
        ApplicationInitializationException exception = new ApplicationInitializationException(e);
        domainBus.post(exception);
        throw exception;
      } finally {
        initInProgress.set(false);
      }
    }
  }

  @Override
  public boolean initialized() {
    return initialized.get();
  }

}
 
