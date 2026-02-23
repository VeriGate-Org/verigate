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
 * Specifies a validation rule that an entity's name field must start with a letter. This class
 * defines a specification that ensures the value of a specific field starts with a letter which can
 * be from the basic Latin alphabet or extended ranges (including accented characters), ensuring
 * applicability across various languages.
 *
 * @param <T> the type of entity being validated
 */
public final class NameMustStartWithLetterSpecification<T> implements Specification<T> {

  // Regular expression to check if the name starts with a valid letter.
  private static final String REGULAR_EXPRESSION = "^[A-Za-zÀ-ÖØ-öø-ÿ].*";

  // The name of the field to be validated.
  private final String fieldName;

  // Function to retrieve the string value of the field from an entity.
  private final Function<T, String> getStringValue;

  /**
   * Constructs a new specification for validating that a name begins with a letter.
   *
   * @param fieldName the name of the field to validate
   * @param getStringValue a function to extract the string value from the entity
   */
  public NameMustStartWithLetterSpecification(
      String fieldName, Function<T, String> getStringValue) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
  }

  /**
   * Evaluates the instance to determine if the specified field's value starts with a valid letter.
   *
   * @param instance the instance to be validated
   * @return a {@link SpecificationResult} indicating whether the field's value meets the
   *     specification
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {
    String value = getStringValue.apply(instance);

    boolean passed = value == null || value.matches(REGULAR_EXPRESSION);

    return SpecificationUtils.getResult(
        passed,
        this.getClass(),
        GenericInvariantError.NAME_DOES_NOT_START_WITH_A_VALID_LETTER,
        fieldName);
  }
}
