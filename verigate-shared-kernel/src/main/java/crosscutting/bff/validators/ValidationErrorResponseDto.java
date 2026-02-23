/*
 *  VeriGate (c) 2024. All rights reserved.
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 */

package crosscutting.bff.validators;

import java.util.List;

/** Represents a validation error response DTO. */
public record ValidationErrorResponseDto(Boolean success, List<InvalidPropertyDto> errors) {}
