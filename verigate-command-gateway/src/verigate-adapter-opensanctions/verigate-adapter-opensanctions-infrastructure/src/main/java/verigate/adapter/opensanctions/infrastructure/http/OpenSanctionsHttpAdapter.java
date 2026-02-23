/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import verigate.adapter.opensanctions.infrastructure.config.OpenSanctionsApiConfiguration;

/**
 * Base HTTP adapter for OpenSanctions API communication.
 */
public class OpenSanctionsHttpAdapter {

  private static final Logger LOGGER = Logger.getLogger(OpenSanctionsHttpAdapter.class.getName());
  private static final String API_KEY_HEADER = "Authorization";
  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String APPLICATION_JSON = "application/json";

  protected final HttpClient httpClient;
  protected final OpenSanctionsApiConfiguration config;
  protected final ObjectMapper objectMapper;

  /**
   * Constructor.
   *
   * @param config the API configuration
   */
  public OpenSanctionsHttpAdapter(OpenSanctionsApiConfiguration config) {
    this.config = config;
    this.httpClient =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(config.getConnectionTimeoutMs()))
            .build();

    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  /**
   * Performs a GET request to the specified endpoint.
   */
  protected <T> T get(String endpoint, Class<T> responseType)
      throws TransientException, PermanentException {

    String url = buildUrl(endpoint);
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header(API_KEY_HEADER, "Bearer " + config.getApiKey())
            .timeout(Duration.ofMillis(config.getReadTimeoutMs()))
            .GET()
            .build();

    return executeRequest(request, responseType);
  }

  /**
   * Performs a POST request to the specified endpoint with JSON body.
   */
  protected <T> T post(String endpoint, Object requestBody, Class<T> responseType)
      throws TransientException, PermanentException {

    String url = buildUrl(endpoint);
    String jsonBody = serializeRequestBody(requestBody);

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header(API_KEY_HEADER, "Bearer " + config.getApiKey())
            .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
            .timeout(Duration.ofMillis(config.getReadTimeoutMs()))
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

    return executeRequest(request, responseType);
  }

  private <T> T executeRequest(HttpRequest request, Class<T> responseType)
      throws TransientException, PermanentException {

    Exception lastException = null;

    for (int attempt = 0; attempt <= config.getRetryAttempts(); attempt++) {
      try {
        HttpResponse<String> response =
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return handleResponse(response, responseType);

      } catch (TransientException e) {
        lastException = e;
        if (attempt < config.getRetryAttempts()) {
          LOGGER.info(
              "Retrying request after transient error (attempt "
                  + (attempt + 1)
                  + "/"
                  + (config.getRetryAttempts() + 1)
                  + ")");
          try {
            Thread.sleep(config.getRetryDelayMs());
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new TransientException("Request interrupted", ie);
          }
        }
      } catch (PermanentException e) {
        throw e; // Don't retry permanent exceptions
      } catch (Exception e) {
        throw new TransientException("Unexpected error during HTTP request", e);
      }
    }

    // All retries exhausted
    throw new TransientException(
        "Request failed after " + (config.getRetryAttempts() + 1) + " attempts", lastException);
  }

  private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType)
      throws TransientException, PermanentException {

    int statusCode = response.statusCode();
    String responseBody = response.body();

    LOGGER.fine("Received response: " + statusCode + " - " + responseBody);

    if (statusCode >= 200 && statusCode < 300) {
      return deserializeResponse(responseBody, responseType);
    }

    // Handle different error types
    if (statusCode == 400) {
      throw new PermanentException("Bad request: " + responseBody);
    } else if (statusCode == 401) {
      throw new PermanentException("Unauthorized: Invalid API key");
    } else if (statusCode == 403) {
      throw new PermanentException("Forbidden: Access denied");
    } else if (statusCode == 404) {
      throw new PermanentException("Not found: " + responseBody);
    } else if (statusCode == 429) {
      throw new TransientException("Rate limit exceeded");
    } else if (statusCode >= 500) {
      throw new TransientException("Server error: " + responseBody);
    } else {
      throw new PermanentException("Unexpected HTTP status: " + statusCode + " - " + responseBody);
    }
  }

  private String buildUrl(String endpoint) {
    String baseUrl = config.getBaseUrl();
    if (baseUrl.endsWith("/")) {
      baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
    }
    if (!endpoint.startsWith("/")) {
      endpoint = "/" + endpoint;
    }
    return baseUrl + endpoint;
  }

  private String serializeRequestBody(Object requestBody) throws PermanentException {
    try {
      return objectMapper.writeValueAsString(requestBody);
    } catch (Exception e) {
      throw new PermanentException("Failed to serialize request body", e);
    }
  }

  private <T> T deserializeResponse(String responseBody, Class<T> responseType)
      throws PermanentException {
    try {
      return objectMapper.readValue(responseBody, responseType);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Failed to deserialize response: " + responseBody, e);
      throw new PermanentException("Failed to deserialize API response", e);
    }
  }
}
