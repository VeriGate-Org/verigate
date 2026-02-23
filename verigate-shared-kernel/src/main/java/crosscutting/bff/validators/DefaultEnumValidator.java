/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

/** Represents a default enum validator. */
public class DefaultEnumValidator implements ConstraintValidator<ValidEnumValue, String> {
  private Class<? extends Enum<?>> enumClass;

  @Override
  public void initialize(ValidEnumValue constraintAnnotation) {
    enumClass = constraintAnnotation.enumClass();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    try {
      var validValues =
          (String[])
              Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toArray(String[]::new);

      for (String validValue : validValues) {
        if (validValue.equals(value)) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }
}
