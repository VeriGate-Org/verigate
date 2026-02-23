/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import java.util.Set;

/** Represents a DTO validation failed response mapper. */
public interface DtoValidationFailedResponseMapper {
  public ValidationErrorResponseDto map(Set<InvalidPropertyRecord> violations);
}
