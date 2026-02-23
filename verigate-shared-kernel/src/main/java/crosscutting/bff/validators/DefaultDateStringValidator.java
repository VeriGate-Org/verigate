/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/** Represents a default date string validator. */
public class DefaultDateStringValidator implements ConstraintValidator<ValidDateString, String> {
  @Override
  public void initialize(ValidDateString constraintAnnotation) {}

  @Override
  public boolean isValid(String dateString, ConstraintValidatorContext context) {
    if (dateString == null) {
      return true;
    }
    try {
      LocalDate.parse(dateString);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
