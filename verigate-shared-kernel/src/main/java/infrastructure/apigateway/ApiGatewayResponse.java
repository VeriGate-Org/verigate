/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.apigateway;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Represents a response from an API Gateway. */
public final class ApiGatewayResponse {

  private final int statusCode;
  private final String body;
  private final Map<String, String> headers;
  private final boolean isBase64Encoded;

  /**
   * Constructs a new ApiGatewayResponse object.
   *
   * @param statusCode The HTTP status code of the response.
   * @param body The body of the response.
   * @param headers The headers of the response.
   * @param isBase64Encoded Indicates whether the body is Base64 encoded.
   */
  public ApiGatewayResponse(
      int statusCode, String body, Map<String, String> headers, boolean isBase64Encoded) {
    this.statusCode = statusCode;
    this.body = body;
    this.headers = headers;
    this.isBase64Encoded = isBase64Encoded;
  }

  // Getters
  /**
   * Gets the HTTP status code of the response.
   *
   * @return The HTTP status code.
   */
  public int getStatusCode() {
    return this.statusCode;
  }

  /**
   * Gets the body of the response.
   *
   * @return The response body.
   */
  public String getBody() {
    return this.body;
  }

  /**
   * Gets the headers of the response.
   *
   * @return The response headers.
   */
  public Map<String, String> getHeaders() {
    return this.headers;
  }

  /**
   * Checks if the body is Base64 encoded.
   *
   * @return True if the body is Base64 encoded, false otherwise.
   */
  public boolean getIsBase64Encoded() {
    return this.isBase64Encoded;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ApiGatewayResponse that)) {
      return false;
    }
    return statusCode == that.statusCode
        && isBase64Encoded == that.isBase64Encoded
        && Objects.equals(body, that.body)
        && Objects.equals(headers, that.headers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(statusCode, body, headers, isBase64Encoded);
  }

  /** Builder class for constructing an ApiGatewayResponse object. */
  public static class Builder {

    private int statusCode = 200;
    private String body = "{}";
    private Map<String, String> headers = new HashMap<>();

    private boolean isBase64Encoded = false;

    /**
     * Sets the HTTP status code of the response.
     *
     * @param statusCode The HTTP status code.
     * @return The Builder object.
     */
    public Builder setStatusCode(int statusCode) {
      this.statusCode = statusCode;
      return this;
    }

    /**
     * Sets the HTTP status code of the response.
     *
     * @param cacheTimeInSeconds The cache time in seconds.
     * @return The Builder object.
     */
    public Builder setCacheTime(int cacheTimeInSeconds) {
      headers.put("Cache-Control", "public, max-age=" + cacheTimeInSeconds);
      return this;
    }

    /**
     * Sets the body of the response.
     *
     * @param body The response body.
     * @return The Builder object.
     */
    public Builder setBody(String body) {
      this.body = body;
      return this;
    }

    /**
     * Sets the headers of the response.
     *
     * @param headers The response headers.
     * @return The Builder object.
     */
    public Builder setHeaders(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    /**
     * Adds a JSON header to the response.
     *
     * @return The Builder object.
     */
    public Builder addJsonHeader() {
      this.headers.put("Content-Type", "application/json");
      return this;
    }

    /**
     * Sets whether the body is Base64 encoded.
     *
     * @param isBase64Encoded True if the body is Base64 encoded, false otherwise.
     * @return The Builder object.
     */
    public Builder setIsBase64Encoded(boolean isBase64Encoded) {
      this.isBase64Encoded = isBase64Encoded;
      return this;
    }

    /**
     * Builds an ApiGatewayResponse object with the specified properties.
     *
     * @return The constructed ApiGatewayResponse object.
     */
    public ApiGatewayResponse build() {
      return new ApiGatewayResponse(statusCode, body, headers, isBase64Encoded);
    }
  }

  @Override
  public String toString() {
    return "ApiGatewayResponse{"
        + "statusCode="
        + statusCode
        + ", body='"
        + body
        + '\''
        + ", headers="
        + headers
        + ", isBase64Encoded="
        + isBase64Encoded
        + '}';
  }
}
