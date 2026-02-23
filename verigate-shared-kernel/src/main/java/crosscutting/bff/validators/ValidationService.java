/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import infrastructure.apigateway.ApiGatewayResponse;

/**
 * Represents a validation service.
 *
 */
public interface ValidationService {
  public <T> ApiGatewayResponse validateAndRespond(T commandDto);
}
