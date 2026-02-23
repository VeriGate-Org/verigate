/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

/** Represents a default UUID validator. */
public class DefaultUUIDValidator implements ConstraintValidator<ValidUUID, String> {
  @Override
  public void initialize(ValidUUID constraintAnnotation) {}

  @Override
  public boolean isValid(String uuid, ConstraintValidatorContext context) {
    // acts as an optional validator for optional properties in a DTO
    // if the property is not null, it has to be a valid UUID
    if (uuid == null) {
      return true;
    }
    try {
      UUID.fromString(uuid);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
