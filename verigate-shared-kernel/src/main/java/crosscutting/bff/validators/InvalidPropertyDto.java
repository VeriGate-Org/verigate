/*
 *  VeriGate (c) 2024. All rights reserved.
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 */

package crosscutting.bff.validators;

/** Represents a validation error violation item DTO. */
public final class InvalidPropertyDto {
  public final String propertyName;
  public final String errorMessage;
  public final String errorCode;
  public final String invalidValue;

  /**
   * Constructs a new InvalidPropertyDto.
   */
  public InvalidPropertyDto() {
    this.propertyName = null;
    this.errorMessage = null;
    this.errorCode = null;
    this.invalidValue = null;
  }

  /**
   * Constructs a new InvalidPropertyDto.
   */
  public InvalidPropertyDto(
      String propertyName, String errorCode, String errorMessage, String invalidValue) {
    this.propertyName = propertyName;
    this.errorMessage = errorMessage;
    this.invalidValue = invalidValue;
    this.errorCode = errorCode;
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
