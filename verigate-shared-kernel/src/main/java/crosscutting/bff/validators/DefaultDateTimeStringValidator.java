/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;

/** Represents a default date time string validator. */
public class DefaultDateTimeStringValidator
    implements ConstraintValidator<ValidDateTimeString, String> {
  @Override
  public void initialize(ValidDateTimeString constraintAnnotation) {}

  @Override
  public boolean isValid(String dateTimeString, ConstraintValidatorContext context) {
    if (dateTimeString == null) {
      return true;
    }
    try {
      Instant.parse(dateTimeString);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
