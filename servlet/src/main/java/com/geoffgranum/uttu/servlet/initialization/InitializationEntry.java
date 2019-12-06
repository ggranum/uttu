/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.servlet.initialization;

import java.util.Optional;

public interface InitializationEntry {

  default Optional<Integer> priorityIndex() {
    return Optional.empty();
  }

  void doInitialize(InitializationChain chain);
}
 
