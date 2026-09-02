/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http;

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
import verigate.adapter.datanamix.infrastructure.config.DatanamixApiConfiguration;

/**
 * Thin HTTP client for the Datanamix API. Attaches a bearer token only when configured
 * for {@code LIVE} — sandbox requests need no {@code Authorization} header at all (a
 * supplied header is accepted but ignored per Datanamix's docs), so this adapter omits
 * it entirely in sandbox mode rather than depending on credentials that may not exist.
 */
public class DatanamixHttpAdapter {

  private static final Logger logger = LoggerFactory.getLogger(DatanamixHttpAdapter.class);
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

  private final DatanamixApiConfiguration configuration;
  private final DatanamixOAuthTokenProvider tokenProvider;
  private final ObjectMapper objectMapper;
  private final HttpClient httpClient;

  /** Constructs a new instance. */
  public DatanamixHttpAdapter(
      DatanamixApiConfiguration configuration,
      DatanamixOAuthTokenProvider tokenProvider,
      ObjectMapper objectMapper) {
    this.configuration = configuration;
    this.tokenProvider = tokenProvider;
    this.objectMapper = objectMapper;
    this.httpClient = HttpClient.newBuilder().connectTimeout(DEFAULT_TIMEOUT).build();
  }

  /**
   * Executes a POST request against the Datanamix API and deserializes the response.
   */
  public <T, R> R post(String endpoint, T requestBody, Class<R> responseType)
      throws TransientException, PermanentException {

    String payload;
    try {
      payload = objectMapper.writeValueAsString(requestBody);
    } catch (IOException e) {
      throw new PermanentException("Failed to serialize Datanamix request", e);
    }

    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
        .uri(URI.create(configuration.getBaseUrl() + endpoint))
        .timeout(DEFAULT_TIMEOUT)
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(payload));

    if (configuration.isLive()) {
      requestBuilder.header("Authorization", "Bearer " + tokenProvider.getAccessToken());
    }

    HttpResponse<String> response;
    try {
      response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      // Connection failure, timeout, DNS issue, etc. -- retriable, and deliberately kept
      // separate from the JSON-parsing IOException below (see story 2.1's code review
      // finding on DocumentCipcHttpAdapter for why conflating the two is a real bug).
      logger.warn("Datanamix network error calling {}: {}", endpoint, e.getMessage());
      throw new TransientException("Failed to reach Datanamix API", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new TransientException("Datanamix HTTP request interrupted", e);
    }

    String responseBody = evaluateResponse(endpoint, response);

    try {
      return objectMapper.readValue(responseBody, responseType);
    } catch (IOException e) {
      logger.error("Failed to parse Datanamix response for {}: {}", endpoint, e.getMessage());
      throw new PermanentException("Failed to parse Datanamix API response", e);
    }
  }

  private String evaluateResponse(String endpoint, HttpResponse<String> response)
      throws TransientException, PermanentException {
    int statusCode = response.statusCode();
    String responseBody = response.body();

    logger.debug("Datanamix response for {} - status: {}", endpoint, statusCode);

    if (statusCode == 200) {
      return responseBody;
    }

    switch (statusCode) {
      case 400 -> throw permanent("Datanamix bad request", statusCode, responseBody);
      case 403 -> throw permanent("Datanamix request forbidden", statusCode, responseBody);
      case 404 -> throw permanent("Datanamix resource not found", statusCode, responseBody);
      case 500, 502, 503, 504 ->
          throw transientError("Datanamix server error", statusCode, responseBody);
      default -> throw permanent("Unexpected Datanamix response", statusCode, responseBody);
    }
  }

  private PermanentException permanent(String message, int statusCode, String body) {
    logger.error("{} - status: {}, body: {}", message, statusCode, body);
    return new PermanentException(message);
  }

  private TransientException transientError(String message, int statusCode, String body) {
    logger.warn("{} - status: {}, body: {}", message, statusCode, body);
    return new TransientException(message);
  }
}
