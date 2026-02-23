/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Enum representing specific invariant errors related to string validation and field formatting.
 * Each constant corresponds to a specific type of error that can occur during data validation
 * processes such as input validation for forms or data fields.
 *
 * <p>These error types include checks for string length, whitespace usage, character restrictions,
 * and specific formatting rules for name fields.
 */
public enum GenericInvariantError implements InvariantError {

  /** Error when a string field exceeds the permissible length. */
  STRING_FIELD_IS_TOO_LONG,

  /** Error when a string field does not meet the minimum length requirement. */
  STRING_FIELD_IS_TOO_SHORT,

  /** Error when a string field contains whitespace characters. */
  STRING_FIELD_HAS_WHITESPACE,

  /** Error when a string field starts or ends with whitespace characters. */
  STRING_FIELD_HAS_LEADING_OR_TRAILING_WHITESPACE,

  /** Error when a string field contains characters that are not allowed. */
  STRING_FIELD_HAS_PROHIBITED_CHARACTERS,

  /** Error when a string field contains profane or inappropriate words. */
  STRING_FIELD_CONTAINS_PROFANITY,

  /** Error when a name field contains postnominal elements (e.g., "Jr.", "Sr.", "III"). */
  NAME_FIELD_CONTAINS_POSTNOMINAL,

  /** Error when a name field contains a suffix. */
  NAME_FIELD_CONTAINS_SUFFIX,

  /** Error when a name field includes initials instead of full names. */
  NAME_FIELD_CONTAINS_INITIALS,

  /** Error when a name field includes titles or salutations (e.g., "Mr.", "Dr."). */
  NAME_FIELD_CONTAINS_SALUTATIONS,

  /** Error when a name field does not start with an alphabetically valid letter. */
  NAME_DOES_NOT_START_WITH_A_VALID_LETTER,

  /**
   * Error when a name field contains consecutive apostrophes, which is typically grammatically
   * incorrect.
   */
  NAME_CONTAINS_CONSECUTIVE_APOSTROPHES,

  /** Error when a name field contains consecutive hyphens. */
  NAME_CONTAINS_CONSECUTIVE_HYPHENS,

  /** Error when a name field contains consecutive spaces, indicating a formatting error. */
  NAME_CONTAINS_CONSECUTIVE_SPACES,

  /** Error when a required field is left empty. */
  FIELD_IS_REQUIRED,

  /** Error when an integer field is too small. */
  INTEGER_IS_TOO_SMALL,

  /** Error when an integer field is too large. */
  INTEGER_IS_TOO_LARGE,

  /** Error when mobile number field is invalid. */
  MOBILE_NUMBER_IS_INVALID,

  /** Error when a date field is in the future. */
  DATE_OF_BIRTH_CANNOT_BE_IN_FUTURE,

  /** Error when a South African ID number is invalid. */
  SOUTH_AFRICAN_ID_NUMBER_IS_INVALID,

  /** Error when a dob is missing when using passport nr. */
  PASSPORT_NUMBER_REQUIRES_PARTY_DATE_OF_BIRTH,
  ;

  /**
   * Returns a localized message describing the error, based on the error constant. If no message is
   * available in the resource bundle, a default error message is returned.
   *
   * @return the localized error message or a default message if the localization is missing.
   */
  @Override
  public String getMessage() {
    try {
      ResourceBundle resourceBundle =
          ResourceBundle.getBundle("generic_invariant_error_messages_en");
      return resourceBundle.getString(this.name());
    } catch (MissingResourceException e) {
      return "Missing error message for: " + this.name();
    }
  }

  /**
   * Returns the error code associated with this error type. The error code corresponds to the name
   * of the enum constant, facilitating easier mapping between the error and its description in
   * different parts of an application.
   *
   * @return the code of the error.
   */
  @Override
  public String getCode() {
    return this.name();
  }
}
