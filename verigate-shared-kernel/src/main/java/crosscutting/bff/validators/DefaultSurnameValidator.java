/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import domain.invariants.rules.SurnameSpecification;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.function.Function;

/** Represents a default Surname validator. */
public class DefaultSurnameValidator implements ConstraintValidator<ValidSurname, String> {
  @Override
  public void initialize(ValidSurname constraintAnnotation) {}

  @Override
  public boolean isValid(String surname, ConstraintValidatorContext context) {
    Function<String, String> getSurname = (String n) -> n;

    if (surname == null) {
      return true;
    }
    try {
      var surnameSpecification = new SurnameSpecification<String>("Surname", getSurname, 1, 200);
      return surnameSpecification.isSatisfiedBy(surname).satisfied();
    } catch (Exception e) {
      return false;
    }
  }
}
