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
 * Specifies a validation rule that an entity's name field must not contain consecutive spaces. This
 * class is particularly useful in ensuring that name fields in databases and forms are free from
 * excessive spacing, which is important for maintaining data consistency and format standards.
 *
 * @param <T> the type of entity being validated
 */
public final class NameNoConsecutiveSpacesSpecification<T> implements Specification<T> {

  // Regular expression to check for consecutive spaces in the name.
  private static final String REGULAR_EXPRESSION = "\\s\\s+";

  // The name of the field to be validated.
  private final String fieldName;

  // Function to retrieve the string value of the field from an entity.
  private final Function<T, String> getStringValue;

  /**
   * Constructs a new specification for ensuring that a name does not contain consecutive spaces.
   *
   * @param fieldName the name of the field to validate
   * @param getStringValue a function to extract the string value from the entity
   */
  public NameNoConsecutiveSpacesSpecification(
      String fieldName, Function<T, String> getStringValue) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
  }

  /**
   * Evaluates the instance to determine if the specified field's value contains consecutive spaces.
   *
   * @param instance the instance to be validated
   * @return a {@link SpecificationResult} indicating whether the field's value meets the
   *     specification
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    String value = getStringValue.apply(instance);

    boolean passed = value == null || !value.matches(REGULAR_EXPRESSION);

    return SpecificationUtils.getResult(
        passed, this.getClass(), GenericInvariantError.NAME_CONTAINS_CONSECUTIVE_SPACES, fieldName);
  }
}
