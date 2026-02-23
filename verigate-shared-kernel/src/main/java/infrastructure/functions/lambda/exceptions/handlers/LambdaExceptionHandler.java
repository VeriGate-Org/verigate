/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.common.base.Function;
import infrastructure.apigateway.ApiGatewayResponse;

/**
 * This interface represents a handler for exceptions.
 *
 * @param <ApiGatewayResponse> The return type.
 * @param <E> The exception type.
 * @param <LambdaApiGatewayThrowingFunction> The function that throws the exception.
 */
public interface LambdaExceptionHandler {
  <E extends RuntimeException> Function<APIGatewayProxyRequestEvent, ApiGatewayResponse> handle(
      LambdaApiGatewayThrowingFunction<E> throwingFunction);
}
