/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import domain.invariants.rules.FirstNameSpecification;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.function.Function;

/** Represents a default Name validator. */
public class DefaultNameValidator implements ConstraintValidator<ValidName, String> {
  @Override
  public void initialize(ValidName constraintAnnotation) {}

  @Override
  public boolean isValid(String name, ConstraintValidatorContext context) {
    Function<String, String> getName = (String n) -> n;

    if (name == null) {
      return true;
    }
    try {
      var nameSpecification = new FirstNameSpecification<String>("Name", getName, 1, 200);
      return nameSpecification.isSatisfiedBy(name).satisfied();
    } catch (Exception e) {
      return false;
    }
  }
}
