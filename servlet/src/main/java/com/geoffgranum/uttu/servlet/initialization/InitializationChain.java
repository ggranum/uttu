/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet.initialization;

import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Inject this class into your application to add and execute initialization steps in a predictable order.
 *
 * Initialization entries are added via the addInitializationEntry method. Each entry may have a priorityIndex.
 *
 * The order of execution is not directly dependent upon the order the Entry was added to the chain;
 * for entries that do not provide a priorityIndex, a priorityIndex is provided via a counter. This counter
 * begins at a large number (100,000), to allow systems to implement their own counting blocks, such as in chunks of
 * one thousand. For example:
 *
 * <code>databaseInitCounter = new AtomicInteger(1000);
 * remoteServiceAInitCounter = new AtomicInteger(2000);
 * remoteServiceBInitCounter = new AtomicInteger(3000);
 * </code>
 *
 * Conflicting initialization priorityKey values will throw an exception.
 */
public final class InitializationChain {

  private final List<EntryWrapper> entries = new ArrayList<>();

  private final AtomicBoolean initializationStarted = new AtomicBoolean(false);
  private final AtomicBoolean initializationComplete = new AtomicBoolean(false);

  private final AtomicInteger defaultIndex = new AtomicInteger(100000);
  private Iterator<Map.Entry<Integer, InitializationEntry>> chainIterator;
  private Injector injector;

  public InitializationChain() {
  }

  public void init(Injector injector) {
    if(this.injector != null){
      throw new InitializationException("InitializationChain has already been initialized.");
    }
    this.injector = injector;
  }

  public void add(InitializationEntry entry) {
    EntryWrapper entryWrapper = new EntryWrapper(entry);
    addEntry(entryWrapper);
  }

  private void addEntry(EntryWrapper entryWrapper) {
    if(initializationStarted.get()) {
      throw new InitializationAlreadyPerformedException();
    }
    entries.add(entryWrapper);
  }

  public void add(Class<? extends InitializationEntry> entry) {
    addEntry(new EntryWrapper(entry));
  }

  public void doInitialize() throws InitializationException {
    boolean started = initializationStarted.getAndSet(true);
    boolean finished = initializationComplete.get();
    if(finished) {
      throw new InitializationAlreadyPerformedException("Initialization process has already completed.");
    }
    if(injector == null){
      throw new InitializationException("InitializationChain was not itself initialized (InitializationChain#init() has not been called).");
    }
    if(!started) {
      this.preInitialize(injector);
    }
    if(chainIterator.hasNext()) {
      InitializationEntry entry = chainIterator.next().getValue();
      entry.doInitialize(this);
    } else {
      this.initializationComplete.set(true);
    }

  }

  private void preInitialize(Injector injector) {
    Map<Integer, InitializationEntry> map = new HashMap<>();
    for (EntryWrapper wrapper : entries) {
      InitializationEntry entry = wrapper.getEntry(injector);
      Integer idx = entry.priorityIndex().orElse(this.defaultIndex.getAndIncrement());
      map.put(idx, entry);
    }
    TreeSet<Map.Entry<Integer, InitializationEntry>> sortedSet = new TreeSet<>(new EntryComparator());
    sortedSet.addAll(map.entrySet());
    chainIterator = sortedSet.iterator();
  }

  private class EntryComparator implements Comparator<Map.Entry<Integer, InitializationEntry>> {

    @Override
    public int compare(
        Map.Entry<Integer, InitializationEntry> o1,
        Map.Entry<Integer, InitializationEntry> o2) {
      return o1.getKey().compareTo(o2.getKey());
    }
  }

  private static class EntryWrapper {

    private final Class<? extends InitializationEntry> entryClass;
    private final InitializationEntry entry;

    private EntryWrapper(InitializationEntry entry) {
      this.entry = entry;
      this.entryClass = null;
    }

    private EntryWrapper(Class<? extends InitializationEntry> entryClass) {
      this.entry = null;
      this.entryClass = entryClass;
    }

    InitializationEntry getEntry(Injector injector) {
      InitializationEntry result = entry;
      if(entry == null) {
        result = injector.getInstance(entryClass);
      }
      return result;
    }
  }
}
 
