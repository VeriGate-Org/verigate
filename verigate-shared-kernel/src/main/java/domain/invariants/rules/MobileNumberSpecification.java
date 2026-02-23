/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import domain.invariants.GenericInvariantError;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import domain.invariants.SpecificationUtils;
import java.util.Locale;
import java.util.function.Function;

/**
 * Specification to check if the mobile number of an entity is valid.
 * This specification is used to validate the mobile number of an entity.
 *
 * @param <T> the type of entity to which the specification is applied
 */
public final class MobileNumberSpecification<T> implements Specification<T> {

  private final Function<T, String> getStringValue;
  private final String fieldName;

  /**
   * Constructor to create a specification for validating the mobile number of an entity.
   *
   * @param fieldName The name of the field to validate.
   * @param getStringValue A function to retrieve the string value from the entity.
   */
  public MobileNumberSpecification(String fieldName, Function<T, String> getStringValue) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
  }

  /**
   * Checks if the mobile number of the instance is valid.
   *
   * @param instance The instance to validate.
   * @return The result of the validation.
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {
    String mobileNumber = getStringValue.apply(instance);

    try {
      PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
      var phoneNumber = phoneUtil.parse(mobileNumber, Locale.getDefault().getCountry());
      boolean isValid = phoneUtil.isValidNumber(phoneNumber);

      if (!isValid) {
        return SpecificationUtils.getResult(
            false, this.getClass(), GenericInvariantError.MOBILE_NUMBER_IS_INVALID, fieldName);
      }

    } catch (Exception e) {
      return SpecificationUtils.getResult(
          false, this.getClass(), GenericInvariantError.MOBILE_NUMBER_IS_INVALID, fieldName);
    }

    return SpecificationResult.success();
  }
}
