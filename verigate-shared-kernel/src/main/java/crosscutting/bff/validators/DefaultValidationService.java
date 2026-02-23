/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import com.google.inject.Inject;
import infrastructure.apigateway.ApiGatewayResponse;
import infrastructure.apigateway.ApiGatewayResponse.Builder;
import infrastructure.functions.lambda.serializers.http.JsonSerializer;
import java.util.logging.Logger;

/**
 * Default implementation of the ValidationService interface.
 */
public class DefaultValidationService implements ValidationService {

  private final DtoValidator dtoValidator;
  private final DtoValidationFailedResponseMapper validationFailureResponseMapper;
  private final JsonSerializer jsonSerializer;
  private final Logger logger = Logger.getLogger(DefaultValidationService.class.getName());

  /**
   * Constructs a new DefaultValidationService.
   *
   * @param dtoValidator the DTO validator
   * @param validationFailureResponseMapper the validation failure response mapper
   * @param jsonSerializer the JSON serializer
   */
  @Inject
  public DefaultValidationService(
      DtoValidator dtoValidator,
      DtoValidationFailedResponseMapper validationFailureResponseMapper,
      JsonSerializer jsonSerializer) {
    this.dtoValidator = dtoValidator;
    this.validationFailureResponseMapper = validationFailureResponseMapper;
    this.jsonSerializer = jsonSerializer;
  }

  @Override
  public <T> ApiGatewayResponse validateAndRespond(T commandDto) {
    var violations = dtoValidator.validate(commandDto);
    if (!violations.isEmpty()) {
      var validationErrorResponseDto = validationFailureResponseMapper.map(violations);
      var json = jsonSerializer.serialize(validationErrorResponseDto);
      ApiGatewayResponse response =
          new Builder().setStatusCode(400).setBody(json).addJsonHeader().build();
      logger.info("Validation failed, validationResponse: " + response);

      return response;
    }
    return null;
  }
}
