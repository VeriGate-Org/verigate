/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants;

/**
 * Represents a field-specific error that wraps a general {@link InvariantError}, associating it
 * with a specific field of a data structure. This class facilitates tracking and reporting of
 * errors related to specific fields in an object.
 *
 * <p>This class implements {@link InvariantError} and delegates calls to an encapsulated instance
 * of {@link InvariantError} while providing additional context about the field related to the
 * error.
 */
public final class FieldInvariantError implements InvariantError {

  private final InvariantError invariantError;
  private final String fieldName;

  /**
   * Constructs a new {@code FieldInvariantError} with the specified underlying error and associated
   * field name.
   *
   * @param invariantError the {@link InvariantError} instance containing the error details
   * @param fieldName the name of the field related to the error
   */
  public FieldInvariantError(InvariantError invariantError, String fieldName) {
    this.invariantError = invariantError;
    this.fieldName = fieldName;
  }

  /**
   * Returns a message describing the error with the associated field name appended for context.
   *
   * @return a string representing the error message associated with the field
   */
  @Override
  public String getMessage() {
    return String.format("%s : %s", this.invariantError.getMessage(), this.fieldName);
  }

  /**
   * Retrieves the error code associated with the underlying {@link InvariantError}.
   *
   * @return the error code as a string
   */
  @Override
  public String getCode() {
    return this.invariantError.getCode();
  }
}
