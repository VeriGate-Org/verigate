/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

/** Represents a default DTO validator. */
public class DefaultDtoValidator implements DtoValidator {
  ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  Validator validator = factory.getValidator();

  /**
   * Validates the specified object.
   *
   * @param obj the object to validate
   * @return the list of property violation records
   */
  public Set<InvalidPropertyRecord> validate(Object obj) {
    Set<ConstraintViolation<Object>> violations = validator.validate(obj);
    var stream =
        violations.stream()
            .map(
                violation ->
                    new InvalidPropertyRecord(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        mapCode(violation.getMessage()),
                        violation.getInvalidValue() == null
                            ? null
                            : "Invalid value has been provided"));

    return stream.collect(Collectors.toSet());
  }

  private String mapCode(String violation) {
    if (violation.equals("INVALID_UUID")) {
      return "Invalid UUID";
    } else if (violation.equals("INVALID_BOOLEAN")) {
      return "Invalid boolean";
    } else if (violation.equals("INVALID_DATE")) {
      return "Invalid date";
    } else if (violation.equals("INVALID_DATE_TIME")) {
      return "Invalid date time";
    } else if (violation.equals("INVALID_ENUM_VALUE")) {
      return "Invalid enum value";
    } else if (violation.equals("INVALID_SAID")) {
      return "Invalid SA ID Number";
    } else if (violation.equals("INVALID_CURRENCY")) {
      return "Invalid currency";
    } else if (violation.equals("INVALID_CURRENCY_CODE")) {
      return "Invalid currency code";
    } else if (violation.equals("MUST_NOT_BE_NULL")) {
      return "Must not be null";
    } else if (violation.equals("MUST_NOT_BE_BLANK")) {
      return "Must not be blank";
    } else if (violation.equals("DATE_OF_BIRTH_MUST_MATCH_ID")) {
      return "Date of birth must match ID";
    } else if (violation.equals("PERCENTAGE_CANNOT_EXCEED_100")) {
      return "Percentage cannot exceed 100";
    } else if (violation.equals("PERCENTAGE_MUST_BE_GREATER_THAN_0")) {
      return "Percentage must be greater than 0";
    } else if (violation.equals("NUMBER_MUST_BE_POSITIVE")) {
      return "Number must be positive";
    } else if (violation.equals("GENDER_MUST_BE_A_VALID_TYPE")) {
      return "Gender must be a valid enum value";
    } else if (violation.equals("COUNTRY_CODE_MUST_BE_A_VALID_TYPE")) {
      return "Country code must be a valid enum value";
    } else if (violation.equals("BROKERPERCENTAGE_MUST_BE_BETWEEN_0_AND_100")) {
      return "Broker percentage must be between 0 and 100";
    } else if (violation.equals("MUST_BE_VALID_PERCENTAGE")) {
      return "Must be a valid percentage";
    } else if (violation.equals("INVALID_PRODUCT_LIFE_TYPE")) {
      return "Must be a valid product life type";
    } else if (violation.equals("INVALID_PRODUCT_LIFE_RELATIONSHIP")) {
      return "Must be a valid product life relationship";
    } else if (violation.equals("INVALID_NAME")) {
      return "Name must be valid";
    } else if (violation.equals("INVALID_SURNAME")) {
      return "Surname must be valid";
    } else if (violation.equals("INVALID_MOBILE_NUMBER")) {
      return "Mobile number must be in a valid format";
    } else {
      return violation;
    }
  }
}
