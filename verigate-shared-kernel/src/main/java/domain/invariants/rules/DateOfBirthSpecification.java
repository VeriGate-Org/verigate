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
import java.time.LocalDate;
import java.util.function.Function;

/**
 * A specification to check if a date of birth is in the future. This specification ensures that the
 * date of birth provided is logical and valid within the context of typical data entry
 * requirements.
 * It checks if the date of birth is in the future, and if so, returns an error message.
 *
 * @param <T> the type of entity being validated
 * @implNote This class implements the {@link Specification} interface for {@link T}.
 */
public final class DateOfBirthSpecification<T> implements Specification<T> {

  private final Function<T, LocalDate> getDateValue;
  private final String fieldName;
  private final boolean isRequired;

  /**
   * Constructs a new instance of the {@link DateOfBirthSpecification} class.
   *
   * @param fieldName the name of the field to be validated.
   * @param getDateValue a function to retrieve the date of birth from the entity.
   * @param isRequired a flag indicating if the field is required.
   */
  public DateOfBirthSpecification(
      String fieldName, Function<T, LocalDate> getDateValue, boolean isRequired) {
    this.fieldName = fieldName;
    this.getDateValue = getDateValue;
    this.isRequired = isRequired;
  }

  /**
   * Evaluates an instance to determine if the date of birth is in the future.
   *
   * @param instance the instance to be evaluated.
   * @return a {@link SpecificationResult} indicating whether the date of birth is in the future.
   *     The result will be successful if the date of birth is valid. If the date of birth is in the
   *     future, the result will contain an appropriate error message.
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {
    var dateOfBirth = getDateValue.apply(instance);

    if (dateOfBirth == null && !isRequired) {
      return SpecificationResult.success();
    }

    if (dateOfBirth == null) {
      return SpecificationUtils.getResult(
          false, this.getClass(), GenericInvariantError.FIELD_IS_REQUIRED, fieldName);
    }

    var isInFuture = dateOfBirth.isAfter(LocalDate.now());

    if (isInFuture) {
      return SpecificationUtils.getResult(
          false,
          this.getClass(),
          GenericInvariantError.DATE_OF_BIRTH_CANNOT_BE_IN_FUTURE,
          fieldName);
    }

    return SpecificationResult.success();
  }
}
