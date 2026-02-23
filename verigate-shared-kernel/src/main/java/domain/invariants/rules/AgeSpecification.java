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
 * Specification to check if the age of an entity is within the specified range. This specification
 * is used to validate the age of an entity.
 *
 * @param <T> the type of entity to which the specification is applied
 */
public final class AgeSpecification<T> implements Specification<T> {

  private final String fieldName;

  private final Function<T, Integer> getIntegerValue;

  private final Integer minAge;

  private final Integer maxAge;

  /**
   * Constructor to create a specification for validating the age of an entity.
   *
   * @param fieldName The name of the field to validate.
   * @param getIntegerValue A function to retrieve the integer value from the entity.
   * @param minAge The minimum age allowed.
   * @param maxAge The maximum age allowed.
   */
  public AgeSpecification(
      String fieldName, Function<T, Integer> getIntegerValue, Integer minAge, Integer maxAge) {
    this.fieldName = fieldName;
    this.getIntegerValue = getIntegerValue;
    this.minAge = minAge;
    this.maxAge = maxAge;
  }

  /**
   * Checks if the age of the instance is within the specified range.
   *
   * @param instance The instance to validate.
   * @return The result of the validation.
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    var value = this.getIntegerValue.apply(instance);

    if (value == null) {
      return SpecificationUtils.getResult(
          false,
          this.getClass(),
          GenericInvariantError.FIELD_IS_REQUIRED,
          fieldName);
    }

    // Check that the string's length is within the specified minimum and maximum range.

    // Combine all individual specifications into a single composite specification. This allows for
    // a single point of validation
    // that checks all rules and only passes if all conditions are satisfied.
    Specification<T> compositeSpecification =
        new IntegerSizeSpecification<>(fieldName, getIntegerValue, minAge, maxAge);

    return compositeSpecification.isSatisfiedBy(instance);
  }
}
