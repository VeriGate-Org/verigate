/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.apigateway;

/**
 * A factory class for creating API Gateway responses.
 */
public class ApiGatewayResponseFactory {
  /**
   * Creates an API Gateway response with a 200 status code
   * and the provided message as the body.
   *
   * @param message The message to include in the response body.
   * @return An instance of ApiGatewayResponse with a 200 status code
   *         and the provided message as the body.
   */
  public static ApiGatewayResponse okResponse(String message) {
    return new ApiGatewayResponse.Builder()
        .setStatusCode(200)
        .addJsonHeader()
        .setBody(message)
        .build();
  }

  /**
   * Creates an API Gateway response with a status code of 201 (Created)
   * and the specified message as the response body.
   *
   * @param message The message to be included in the response body.
   * @return An instance of the ApiGatewayResponse class representing the created response.
   */
  public static ApiGatewayResponse createdResponse(String message) {
    return new ApiGatewayResponse.Builder()
        .setStatusCode(201)
        .addJsonHeader()
        .setBody(message)
        .build();
  }

  /**
   * Creates an API Gateway response with a status code of 202 (Accepted).
   *
   * @param message The message to include in the response body.
   * @return The API Gateway response.
   */
  public static ApiGatewayResponse acceptedResponse(String message) {
    return new ApiGatewayResponse.Builder()
        .setStatusCode(202)
        .addJsonHeader()
        .setBody(message)
        .build();
  }

  /**
   * Creates a bad request response with the specified message.
   *
   * @param message The error message to include in the response body.
   * @return An instance of ApiGatewayResponse representing the bad request response.
   */
  public static ApiGatewayResponse badRequestResponse(String message) {
    return new ApiGatewayResponse.Builder()
        .setStatusCode(400)
        .addJsonHeader()
        .setBody(message)
        .build();
  }

  /**
   * Creates a not found response with the specified message.
   *
   * @param message The message to include in the response body.
   * @return An instance of ApiGatewayResponse representing the not found response.
   */
  public static ApiGatewayResponse notFoundResponse(String message) {
    return new ApiGatewayResponse.Builder()
        .setStatusCode(404)
        .addJsonHeader()
        .setBody(message)
        .build();
  }

  /**
   * Creates an API Gateway response with a 500 Internal Server Error status code.
   *
   * @param message The error message to include in the response body.
   * @return An instance of {@link ApiGatewayResponse} representing the API Gateway response.
   */
  public static ApiGatewayResponse internalServerErrorResponse(String message) {
    return new ApiGatewayResponse.Builder()
        .setStatusCode(500)
        .addJsonHeader()
        .setBody(message)
        .build();
  }

  /**
   * Creates a service unavailable response with the specified message.
   *
   * @param message The message to include in the response body.
   * @return An instance of ApiGatewayResponse representing the service unavailable response.
   */
  public static ApiGatewayResponse serviceUnavailableResponse(String message) {
    return new ApiGatewayResponse.Builder()
        .setStatusCode(503)
        .addJsonHeader()
        .setBody(message)
        .build();
  }
}
