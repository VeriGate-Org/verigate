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
 * Implements the {@link Specification} interface to enforce string length constraints on a specific
 * field of an entity.
 *
 * @param <T> The type of the entity being validated.
 */
public final class StringLengthSpecification<T> implements Specification<T> {

  private final String fieldName;
  private final Function<T, String> getValue;
  private final int requiredMinLength;
  private final int requiredMaxLength;

  /**
   * Constructs a new {@code StringLengthSpecification} instance for enforcing string length
   * constraints.
   *
   * @param fieldName The name of the field to validate.
   * @param getValue A function that extracts the length of the string field from an entity of type
   *     {@code EntityT}.
   * @param requiredMinLength The minimum length required for the string field.
   * @param requiredMaxLength The maximum length allowed for the string field.
   */
  public StringLengthSpecification(
      String fieldName,
      Function<T, String> getValue,
      int requiredMinLength,
      int requiredMaxLength) {
    this.fieldName = fieldName;
    this.getValue = getValue;
    this.requiredMinLength = requiredMinLength;
    this.requiredMaxLength = requiredMaxLength;
  }

  /**
   * Checks if the specified instance satisfies the string length constraints.
   *
   * @param instance The instance to validate.
   * @return A {@link SpecificationResult} indicating whether the instance's string field is within
   *     the defined length constraints.
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    String value = this.getValue.apply(instance);

    if (value == null) {
      return SpecificationResult.success();
    }

    Set<InvariantError> errors = new HashSet<>();

    if (requiredMaxLength < value.length()) {
      errors.add(
          new FieldInvariantError(GenericInvariantError.STRING_FIELD_IS_TOO_LONG, this.fieldName));
    }

    if (requiredMinLength > value.length()) {
      errors.add(
          new FieldInvariantError(GenericInvariantError.STRING_FIELD_IS_TOO_SHORT, this.fieldName));
    }

    return SpecificationUtils.getResult(errors.isEmpty(), this.getClass(), errors);
  }
}
