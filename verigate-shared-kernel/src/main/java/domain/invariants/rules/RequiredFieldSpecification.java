/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import domain.invariants.GenericInvariantError;
import domain.invariants.InvariantError;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import domain.invariants.SpecificationUtils;
import java.util.Objects;
import java.util.function.Function;

/**
 * A specific implementation of {@link Specification} that determines if a required field in an
 * entity is not null. This class is designed for generic use, where the field to be validated can
 * be specified along with a function to retrieve its value.
 *
 * @param <T> the type of the entity being evaluated by this specification.
 */
public final class RequiredFieldSpecification<T> implements Specification<T> {

  // The name of the field to be validated.
  private final String fieldName;

  // Function to retrieve the string value of the field from an entity.
  private final Function<T, ?> getStringValue;

  private final String specificationName;
  private final InvariantError error;

  /**
   * Constructs a new RequiredFieldSpecification with the specified field name and function to
   * retrieve the field's value from an entity.
   *
   * @param fieldName the name of the field that must not be null.
   * @param getStringValue a function that retrieves the value of the field from an entity as a
   *     String.
   */
  public RequiredFieldSpecification(String fieldName, Function<T, ?> getStringValue) {
    this(null, fieldName, getStringValue, null);
  }

  /**
   * Constructs a new RequiredFieldSpecification with the specified field name, function to retrieve
   * the field's value from an entity, and a custom error to be thrown if the field's value does not
   * meet the invariant.
   *
   * @param fieldName the name of the field that must not be null.
   * @param getStringValue a function that retrieves the value of the field from an entity as a
   *     String. The function takes an entity of type EntityT and returns an Object that should
   *     ideally be a String representation of the field's value.
   * @param error the custom error that can be thrown if the field's value is null or does not meet
   *     other specified criteria. This parameter is optional and can be null, in which case a
   *     default error or no error may be thrown based on the implementation.
   */
  public <TypeT> RequiredFieldSpecification(
      Class<TypeT> clazz, String fieldName, Function<T, ?> getStringValue, InvariantError error) {
    this.specificationName = (clazz == null) ? this.getClass().getSimpleName() : null;
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
    this.error = error;
  }

  /**
   * Evaluates the specified instance to determine if the required field is not null. This is the
   * core method that implements the specification pattern for this class.
   *
   * @param instance the instance to be evaluated against the specification.
   * @return a {@link SpecificationResult} indicating whether the field is not null, along with any
   *     appropriate error messages if the specification is not satisfied.
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    boolean passed = getStringValue.apply(instance) != null;

    return SpecificationUtils.getResult(
        passed,
        this.specificationName,
        Objects.requireNonNullElse(this.error, GenericInvariantError.FIELD_IS_REQUIRED),
        fieldName);
  }
}
