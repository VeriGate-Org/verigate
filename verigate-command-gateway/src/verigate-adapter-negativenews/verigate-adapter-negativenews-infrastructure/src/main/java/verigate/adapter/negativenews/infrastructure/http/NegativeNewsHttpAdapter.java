/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.http;

import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * Base HTTP adapter that encapsulates common logic for calling the NewsAPI.
 */
public class NegativeNewsHttpAdapter {

  private static final Logger logger = LoggerFactory.getLogger(NegativeNewsHttpAdapter.class);
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  /**
   * Creates a new HTTP adapter instance.
   */
  public NegativeNewsHttpAdapter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.httpClient = HttpClient.newBuilder().connectTimeout(DEFAULT_TIMEOUT).build();
  }

  /**
   * Executes a GET request to the given URL and deserialises the response into the provided class.
   *
   * @param url          the full URL including query parameters
   * @param responseType the class to deserialise the response into
   * @return the deserialised response
   */
  public <R> R get(String url, Class<R> responseType)
      throws TransientException, PermanentException {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .timeout(DEFAULT_TIMEOUT)
          .header("Content-Type", "application/json")
          .header("User-Agent", "VeriGate/1.0")
          .GET()
          .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      String responseBody = evaluateResponse(url, response);

      R result = objectMapper.readValue(responseBody, responseType);
      logger.debug("Negative News GET {} succeeded", url);
      return result;
    } catch (IOException e) {
      logger.error("Failed to parse Negative News response for URL {}: {}", url, e.getMessage(),
          e);
      throw new PermanentException("Failed to parse Negative News API response", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new TransientException("Negative News HTTP request interrupted", e);
    } catch (TransientException | PermanentException e) {
      throw e;
    }
  }

  private String evaluateResponse(String url, HttpResponse<String> response)
      throws TransientException, PermanentException {
    int statusCode = response.statusCode();
    String responseBody = response.body();

    logger.debug(
        "Negative News response for {} - status: {}, body length: {}",
        url,
        statusCode,
        responseBody != null ? responseBody.length() : 0);

    if (statusCode == 200) {
      return responseBody;
    }

    switch (statusCode) {
      case 400 ->
          throw permanent(
              "Negative News API bad request",
              statusCode,
              responseBody,
              "Negative News API bad request: %s");
      case 401 ->
          throw permanent(
              "Negative News API authentication failed",
              statusCode,
              responseBody,
              "Negative News API authentication failed - check API key");
      case 404 ->
          throw permanent(
              "Negative News API resource not found",
              statusCode,
              responseBody,
              "Resource not found in Negative News API");
      case 429 ->
          throw transientError(
              "Negative News API rate limit exceeded", statusCode, responseBody);
      case 500, 502, 503, 504 ->
          throw transientError(
              "Negative News API server error", statusCode, responseBody);
      default ->
          throw permanent(
              "Unexpected Negative News API response",
              statusCode,
              responseBody,
              "Unexpected Negative News API response: " + statusCode);
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
}
