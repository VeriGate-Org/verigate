/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Represents a default UUID validator. */
public class DefaultBooleanValidator implements ConstraintValidator<ValidBoolean, Object> {
  @Override
  public void initialize(ValidBoolean constraintAnnotation) {}

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    try {
      // if value is a boolean
      if (value instanceof Boolean) {
        return true;
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
