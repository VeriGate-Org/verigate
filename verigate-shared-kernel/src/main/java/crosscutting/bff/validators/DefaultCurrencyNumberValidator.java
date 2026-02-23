/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/** Represents a default currency number validator. */
public class DefaultCurrencyNumberValidator
    implements ConstraintValidator<ValidCurrencyNumber, Object> {
  @Override
  public void initialize(ValidCurrencyNumber constraintAnnotation) {}

  @Override
  public boolean isValid(Object code, ConstraintValidatorContext context) {
    try {
      var nr = (Number) code;
      BigDecimal.valueOf(nr.doubleValue());
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
