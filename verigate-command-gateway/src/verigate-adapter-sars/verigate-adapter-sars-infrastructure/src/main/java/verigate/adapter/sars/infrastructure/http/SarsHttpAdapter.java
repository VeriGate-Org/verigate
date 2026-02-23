/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import crosscutting.environment.Environment;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.sars.domain.constants.DomainConstants;
import verigate.adapter.sars.infrastructure.config.EnvironmentConstants;

/**
 * Base HTTP adapter that encapsulates common logic for calling the SARS eFiling API.
 */
public class SarsHttpAdapter {

  private static final Logger logger = LoggerFactory.getLogger(SarsHttpAdapter.class);
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

  private final HttpClient httpClient;
  private final Environment environment;
  private final ObjectMapper objectMapper;

  /**
   * Creates a new HTTP adapter instance.
   *
   * @param environment the runtime environment provider
   * @param objectMapper the Jackson object mapper for JSON serialization
   */
  public SarsHttpAdapter(Environment environment, ObjectMapper objectMapper) {
    this.environment = environment;
    this.objectMapper = objectMapper;
    this.httpClient = HttpClient.newBuilder().connectTimeout(DEFAULT_TIMEOUT).build();
  }

  /**
   * Executes a POST request and deserialises the response into the provided class.
   *
   * @param endpoint the API endpoint path
   * @param requestBody the request body object
   * @param responseType the expected response class type
   * @param <T> the request body type
   * @param <R> the response type
   * @return the deserialized response
   * @throws TransientException if a retriable error occurs
   * @throws PermanentException if a non-retriable error occurs
   */
  public <T, R> R post(String endpoint, T requestBody, Class<R> responseType)
      throws TransientException, PermanentException {
    return executePost(
        endpoint,
        requestBody,
        body -> objectMapper.readValue(body, responseType));
  }

  /**
   * Executes a POST request and deserialises the response using a {@link TypeReference}.
   *
   * @param endpoint the API endpoint path
   * @param requestBody the request body object
   * @param typeReference the type reference for complex response types
   * @param <T> the request body type
   * @param <R> the response type
   * @return the deserialized response
   * @throws TransientException if a retriable error occurs
   * @throws PermanentException if a non-retriable error occurs
   */
  public <T, R> R post(String endpoint, T requestBody, TypeReference<R> typeReference)
      throws TransientException, PermanentException {
    return executePost(
        endpoint,
        requestBody,
        body -> objectMapper.readValue(body, typeReference));
  }

  private <T, R> R executePost(String endpoint, T body, ResponseParser<R> parser)
      throws TransientException, PermanentException {
    try {
      String responseBody = send(endpoint, body);
      R result = parser.parse(responseBody);
      logger.debug("SARS API POST {} succeeded", endpoint);
      return result;
    } catch (IOException e) {
      logger.error(
          "Failed to parse SARS API response for endpoint {}: {}",
          endpoint,
          e.getMessage(),
          e);
      throw new PermanentException("Failed to parse SARS API response", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new TransientException("SARS API HTTP request interrupted", e);
    } catch (TransientException | PermanentException e) {
      throw e;
    }
  }

  private <T> String send(String endpoint, T requestBody)
      throws IOException, InterruptedException, TransientException, PermanentException {
    String payload = objectMapper.writeValueAsString(requestBody);
    HttpRequest request = buildHttpRequest(endpoint, payload);
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return evaluateResponse(endpoint, response);
  }

  private HttpRequest buildHttpRequest(String endpoint, String body) {
    String apiKey = requireValue(EnvironmentConstants.SARS_API_KEY, "SARS_API_KEY");
    String baseUrl = requireValue(EnvironmentConstants.SARS_API_URL, "SARS_API_URL");

    return HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + endpoint))
        .timeout(DEFAULT_TIMEOUT)
        .header("Content-Type", DomainConstants.CONTENT_TYPE_JSON)
        .header(DomainConstants.AUTH_HEADER_NAME, apiKey)
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
  }

  private String evaluateResponse(String endpoint, HttpResponse<String> response)
      throws TransientException, PermanentException {
    int statusCode = response.statusCode();
    String responseBody = response.body();

    logger.debug(
        "SARS API response for {} - status: {}, body length: {}",
        endpoint,
        statusCode,
        responseBody != null ? responseBody.length() : 0);

    if (statusCode == 200) {
      return responseBody;
    }

    switch (statusCode) {
      case 400 ->
          throw permanent(
              "SARS API bad request", statusCode, responseBody,
              "SARS API bad request: %s");
      case 401 ->
          throw permanent(
              "SARS API authentication failed",
              statusCode,
              responseBody,
              "SARS API authentication failed - check API key");
      case 403 ->
          throw permanent(
              "SARS API access forbidden",
              statusCode,
              responseBody,
              "SARS API access forbidden - insufficient permissions");
      case 404 ->
          throw permanent(
              "SARS API resource not found",
              statusCode,
              responseBody,
              "Resource not found in SARS API");
      case 429 ->
          throw transientError("SARS API rate limit exceeded", statusCode, responseBody);
      case 500, 502, 503, 504 ->
          throw transientError("SARS API server error", statusCode, responseBody);
      default ->
          throw permanent(
              "Unexpected SARS API response",
              statusCode,
              responseBody,
              "Unexpected SARS API response: " + statusCode);
    }
  }

  private PermanentException permanent(
      String message, int statusCode, String body, String userMessage) {
    logger.error("{} - status: {}, body: {}", message, statusCode, body);
    return new PermanentException(userMessage);
  }

  private TransientException transientError(String message, int statusCode, String body) {
    logger.warn("{} - status: {}, body: {}", message, statusCode, body);
    return new TransientException(message);
  }

  private String requireValue(String envKey, String name) {
    String value = environment.get(envKey);
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalStateException(name + " environment variable is required");
    }
    return value.trim();
  }

  @FunctionalInterface
  private interface ResponseParser<R> {
    R parse(String body) throws IOException, TransientException, PermanentException;
  }
}
