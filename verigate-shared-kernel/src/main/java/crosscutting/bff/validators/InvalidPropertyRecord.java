/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

/** Represents a property violation record. */
public final class InvalidPropertyRecord {
  public final String propertyName;
  public final String errorCode;
  public final String errorMessage;
  public final String invalidValue;

  /**
   * Constructs a new InvalidPropertyRecord.
   */
  public InvalidPropertyRecord(
      String propertyName, String errorCode, String errorMessage, String invalidValue) {
    this.propertyName = propertyName;
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
    this.invalidValue = invalidValue;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getInvalidValue() {
    return invalidValue;
  }
}
