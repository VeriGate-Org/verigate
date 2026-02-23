/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import java.util.Set;
import java.util.stream.Collectors;

/** Represents a default validation response property violation mapper. */
public class DefaultDtoValidationFailedResponseMapper implements DtoValidationFailedResponseMapper {
  /**
   * Maps the specified violations.
   *
   * @param violations the violations to map
   * @return the list of validation error violation item DTOs
   */
  public ValidationErrorResponseDto map(Set<InvalidPropertyRecord> violations) {
    var violationsDto =
        violations.stream()
            .map(
                violation ->
                    new InvalidPropertyDto(
                        violation.getPropertyName(),
                        violation.getErrorCode(),
                        violation.getErrorMessage(),
                        "Invalid value has been provided"))
            .collect(Collectors.toList());

    return new ValidationErrorResponseDto(false, violationsDto);
  }
}
