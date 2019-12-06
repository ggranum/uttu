/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.servlet.bootstrap;

import com.geoffgranum.uttu.core.exception.FatalException;
import java.util.Optional;

public final class EnvSources {

  public final Optional<Env> commandLine;
  public final Optional<Env> environmentVariable;
  public final Optional<Env> envLocalFile;
  public final Optional<Env> envFile;

  public final Env selectedEnv;

  public EnvSources(Optional<Env> envFile,
                    Optional<Env> envLocalFile,
                    Optional<Env> environmentVariable,
                    Optional<Env> commandLine) {
    this.envFile = envFile;
    this.envLocalFile = envLocalFile;
    this.commandLine = commandLine;
    this.environmentVariable = environmentVariable;
    if(commandLine.isPresent()) {
      selectedEnv = commandLine.get();
    } else if(environmentVariable.isPresent()) {
      selectedEnv = environmentVariable.get();
    } else if(envLocalFile.isPresent()) {
      selectedEnv = envLocalFile.get();
    } else if(envFile.isPresent()) {
      selectedEnv = envFile.get();
    } else {
      throw new FatalException("No environment configured. See debug level log messages for available options.");
    }
  }
}
