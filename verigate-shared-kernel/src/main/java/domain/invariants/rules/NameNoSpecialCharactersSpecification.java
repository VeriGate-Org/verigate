/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import domain.invariants.GenericInvariantError;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import domain.invariants.SpecificationUtils;
import java.util.function.Function;

/**
 * Specifies a validation rule that an entity's name field must contain only allowed characters, as
 * defined by a regular expression. This specification helps maintain data integrity by ensuring
 * that names do not contain unexpected or special characters that could affect data processing or
 * display.
 *
 * @param <T> the type of entity being validated
 */
public final class NameNoSpecialCharactersSpecification<T> implements Specification<T> {

  // The name of the field to be validated.
  private final String fieldName;

  // Function to retrieve the string value of the field from an entity.
  private final Function<T, String> getStringValue;

  // Regex pattern that defines the allowed characters in the field value.
  private final String allowedCharactersRegex;

  /**
   * Constructs a new specification with a default set of allowed characters. This constructor is
   * used when only common characters are to be allowed in the field.
   *
   * @param fieldName the name of the field to check
   * @param getStringValue a function to extract the string value from the entity
   */
  public NameNoSpecialCharactersSpecification(
      String fieldName, Function<T, String> getStringValue) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
    // Include basic Latin, extended ranges, hyphen.
    this.allowedCharactersRegex = "[A-Za-zÀ-ÖØ-öø-ÿ'-]+";
  }

  /**
   * Constructs a new specification for checking prohibited characters in a string field using a
   * custom regex pattern provided by the user.
   *
   * @param fieldName the name of the field to check
   * @param getStringValue a function to extract the string value from the entity
   * @param allowedCharactersRegex a regex pattern defining allowed characters
   */
  public NameNoSpecialCharactersSpecification(
      String fieldName, Function<T, String> getStringValue, String allowedCharactersRegex) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
    this.allowedCharactersRegex = allowedCharactersRegex;
  }

  /**
   * Evaluates the instance to determine if the specified field's value contains only allowed
   * characters.
   *
   * @param instance the instance to be validated
   * @return a {@link SpecificationResult} indicating whether the field's value meets the
   *     specification
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    String value = getStringValue.apply(instance);

    if (value == null) {
      return SpecificationResult.success();
    }

    boolean passed = value.replace(" ", "").matches(allowedCharactersRegex);

    return SpecificationUtils.getResult(
        passed,
        this.getClass(),
        GenericInvariantError.STRING_FIELD_HAS_PROHIBITED_CHARACTERS,
        fieldName);
  }
}
