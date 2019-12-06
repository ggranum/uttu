/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.validation;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @author Geoff M. Granum
 */
public abstract class Validated {

  private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  public final Set<ConstraintViolation<Validated>> validate() {
    return validator.validate(this);
  }

  public final void checkValid() {
    Set<ConstraintViolation<Validated>> violations = validator.validate(this);
    if (violations.size() != 0) {
      throw new ValidationException(this, violations);
    }
  }
}
