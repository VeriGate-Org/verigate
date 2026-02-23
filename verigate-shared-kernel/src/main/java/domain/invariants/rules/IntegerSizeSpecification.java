/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import domain.invariants.FieldInvariantError;
import domain.invariants.GenericInvariantError;
import domain.invariants.InvariantError;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import domain.invariants.SpecificationUtils;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Specification to check if an integer field is within the specified size range. This specification
 * is used to validate the length of integer fields in an entity.
 *
 * @param <T> the type of entity to which the specification is applied
 */
public final class IntegerSizeSpecification<T> implements Specification<T> {

  private final String fieldName;
  private final Function<T, Integer> getIntegerValue;
  private final int requiredMinSize;
  private final int requiredMaxSize;

  /**
   * Constructor to create a specification for integer field size validation.
   *
   * @param fieldName the name of the field to validate
   * @param getIntegerValue the function to retrieve the integer value from the entity
   * @param requiredMinSize the minimum size required for the integer field
   * @param requiredMaxSize the maximum size allowed for the integer field
   */
  public IntegerSizeSpecification(
      String fieldName,
      Function<T, Integer> getIntegerValue,
      int requiredMinSize,
      int requiredMaxSize) {
    this.fieldName = fieldName;
    this.getIntegerValue = getIntegerValue;
    this.requiredMinSize = requiredMinSize;
    this.requiredMaxSize = requiredMaxSize;
  }

  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    var value = this.getIntegerValue.apply(instance);

    if (value == null) {
      return SpecificationResult.success();
    }

    Set<InvariantError> errors = new HashSet<>();

    if (requiredMaxSize < value) {
      errors.add(
          new FieldInvariantError(GenericInvariantError.INTEGER_IS_TOO_LARGE, this.fieldName));
    }

    if (requiredMinSize > value) {
      errors.add(
          new FieldInvariantError(GenericInvariantError.INTEGER_IS_TOO_SMALL, this.fieldName));
    }

    return SpecificationUtils.getResult(errors.isEmpty(), this.getClass(), errors);
  }
}
