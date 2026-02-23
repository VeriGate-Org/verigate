/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import domain.invariants.FieldInvariantError;
import domain.invariants.GenericInvariantError;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import domain.invariants.SpecificationUtils;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

/**
 * Implements a specification to ensure that a string field in an entity does not contain leading or
 * trailing spaces.
 *
 * @param <T> The type of the entity being validated.
 */
public final class NoTrailingAndLeadingWhitespaceSpecification<T>
    implements Specification<T> {

  private final String fieldName;
  private final Function<T, String> getStringValue;

  /**
   * Constructs a new specification for checking if a string field contains leading or trailing
   * whitespace.
   *
   * @param fieldName The name of the field to be checked.
   * @param getStringValue A function to extract the string value from the entity.
   */
  public NoTrailingAndLeadingWhitespaceSpecification(
      String fieldName, Function<T, String> getStringValue) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
  }

  /**
   * Evaluates the specified instance's string field to ensure there are no leading or trailing
   * spaces.
   *
   * @param instance The instance to be validated.
   * @return A {@link SpecificationResult} indicating whether the field passes the whitespace check.
   *     It returns a success result if the field is empty or if it matches its trimmed version.
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    String value = getStringValue.apply(instance);
    boolean passed = StringUtils.isBlank(value) || value.equals(value.trim());

    return SpecificationUtils.getResult(
        passed,
        this.getClass(),
        new FieldInvariantError(
            GenericInvariantError.STRING_FIELD_HAS_LEADING_OR_TRAILING_WHITESPACE, fieldName));
  }
}
