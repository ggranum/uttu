/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.exception;

/**
 * Thrown at server startup. Might be possible to continue startup when thrown. But probably not, eh?
 *
 * @author Geoff M. Granum
 */
public class ApplicationInitializationException extends FormattedException
{

  private static final long serialVersionUID = 1L;

  public ApplicationInitializationException(String msgFormat, Object... args)
  {
    super(msgFormat, args);
  }

  public ApplicationInitializationException(Throwable cause)
  {
    super(cause);
  }

  public ApplicationInitializationException(Throwable cause, String msgFormat, Object... args)
  {
    super(cause, msgFormat, args);
  }
}
 
