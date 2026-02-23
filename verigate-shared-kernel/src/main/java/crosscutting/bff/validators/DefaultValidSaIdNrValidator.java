/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import crosscutting.util.SAIdNumberValidityCheck;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Represents a default sa id nr validator. */
public class DefaultValidSaIdNrValidator implements ConstraintValidator<ValidSaIdNumber, String> {
  @Override
  public void initialize(ValidSaIdNumber constraintAnnotation) {}

  @Override
  public boolean isValid(String idNr, ConstraintValidatorContext context) {
    if (idNr == null) {
      return true;
    }
    try {
      return SAIdNumberValidityCheck.check(idNr);
    } catch (Exception e) {
      return false;
    }
  }
}
