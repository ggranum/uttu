/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.validation;

import com.geoffgranum.uttu.core.exception.FormattedException;

import java.util.Set;
import javax.validation.ConstraintViolation;

/**
 * @author Geoff M. Granum
 */
public class ValidationException extends FormattedException {

  private static final long serialVersionUID = 1L;
  transient public final Validated builder;
  transient public final Set<ConstraintViolation<Validated>> violations;

  public ValidationException(Validated builder, Set<ConstraintViolation<Validated>> violations) {
    super("One or more failures while validating %s: %s", builder.getClass().getSimpleName(), createMessage(violations));

    this.builder = builder;
    this.violations = violations;
  }

  private static String createMessage(Set<ConstraintViolation<Validated>> violations) {
    StringBuilder sb = new StringBuilder();
    for (ConstraintViolation<Validated> violation : violations) {
      sb.append("\n\t");
      String msg = violation.getMessage();
      if(!msg.contains(violation.getPropertyPath().toString())){
        sb.append(violation.getPropertyPath().toString()).append(" cannot be set to '").append(violation.getInvalidValue()).append("': ");
      }
      sb.append(msg);
    }

    return sb.toString();
  }
}
 
