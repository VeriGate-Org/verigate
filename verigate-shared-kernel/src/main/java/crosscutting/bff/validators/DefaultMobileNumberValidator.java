/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import domain.invariants.rules.MobileNumberSpecification;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.function.Function;

/** Represents a default Phone Number validator. */
public class DefaultMobileNumberValidator
    implements ConstraintValidator<ValidMobileNumber, String> {
  @Override
  public void initialize(ValidMobileNumber constraintAnnotation) {}

  @Override
  public boolean isValid(String mobileNumber, ConstraintValidatorContext context) {
    Function<String, String> getMobileNumber = (String n) -> n;

    if (mobileNumber == null) {
      return true;
    }
    try {
      var mobileNumberSpecification =
          new MobileNumberSpecification<String>("MobileNumber", getMobileNumber);
      return mobileNumberSpecification.isSatisfiedBy(mobileNumber).satisfied();
    } catch (Exception e) {
      return false;
    }
  }
}
