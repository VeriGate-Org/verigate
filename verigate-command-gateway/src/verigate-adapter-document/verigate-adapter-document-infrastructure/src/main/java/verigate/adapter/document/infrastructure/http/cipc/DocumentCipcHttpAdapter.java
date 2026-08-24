/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.http.cipc;

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
import verigate.adapter.document.infrastructure.config.DocumentCipcApiConfiguration;

/**
 * Thin, independent HTTP client for calling the CIPC public API directly from the document
 * adapter, used only to cross-validate uploaded CIPC registration documents.
 *
 * <p>Deliberately not shared with {@code verigate-adapter-cipc}'s {@code CipcHttpAdapter} — see
 * story 2.1 design notes for why the document adapter owns its own client rather than taking a
 * cross-adapter dependency. The two classes are similar by design (same external API), not by
 * accident.
 */
public class DocumentCipcHttpAdapter {

  private static final Logger logger = LoggerFactory.getLogger(DocumentCipcHttpAdapter.class);
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

  private final HttpClient httpClient;
  private final DocumentCipcApiConfiguration configuration;
  private final ObjectMapper objectMapper;

  /** Constructs a new instance. */
  public DocumentCipcHttpAdapter(
      DocumentCipcApiConfiguration configuration, ObjectMapper objectMapper) {
    this.configuration = configuration;
    this.objectMapper = objectMapper;
    this.httpClient = HttpClient.newBuilder().connectTimeout(DEFAULT_TIMEOUT).build();
  }

  /**
   * Executes a POST request against the CIPC API and deserializes the response.
   */
  public <T, R> R post(String endpoint, T requestBody, Class<R> responseType)
      throws TransientException, PermanentException {
    try {
      String responseBody = send(endpoint, requestBody);
      R result = objectMapper.readValue(responseBody, responseType);
      logger.debug("CIPC lookup POST {} succeeded", endpoint);
      return result;
    } catch (IOException e) {
      logger.error("Failed to parse CIPC lookup response for {}: {}", endpoint, e.getMessage());
      throw new PermanentException("Failed to parse CIPC API response", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new TransientException("CIPC HTTP request interrupted", e);
    }
  }

  private <T> String send(String endpoint, T requestBody)
      throws IOException, InterruptedException, TransientException, PermanentException {
    String apiKey = requireValue(configuration.getApiKey(), "DOCUMENT_CIPC_API_KEY");
    String baseUrl = requireValue(configuration.getBaseUrl(), "DOCUMENT_CIPC_BASE_URL");

    String payload = objectMapper.writeValueAsString(requestBody);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + endpoint))
        .timeout(DEFAULT_TIMEOUT)
        .header("Content-Type", "application/json")
        .header("Ocp-Apim-Subscription-Key", apiKey)
        .POST(HttpRequest.BodyPublishers.ofString(payload))
        .build();

    HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return evaluateResponse(endpoint, response);
  }

  private String evaluateResponse(String endpoint, HttpResponse<String> response)
      throws TransientException, PermanentException {
    int statusCode = response.statusCode();
    String responseBody = response.body();

    logger.debug("CIPC lookup response for {} - status: {}", endpoint, statusCode);

    if (statusCode == 200) {
      return responseBody;
    }

    switch (statusCode) {
      case 400 -> throw permanent("CIPC lookup bad request", statusCode, responseBody);
      case 401 -> throw permanent("CIPC lookup authentication failed", statusCode, responseBody);
      case 404 -> throw permanent("CIPC company not found", statusCode, responseBody);
      case 429 -> throw transientError("CIPC lookup rate limit exceeded", statusCode, responseBody);
      case 500, 502, 503, 504 ->
          throw transientError("CIPC lookup server error", statusCode, responseBody);
      default -> throw permanent("Unexpected CIPC lookup response", statusCode, responseBody);
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

  private String requireValue(String value, String name) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalStateException(name + " environment variable is required");
    }
    return value.trim();
  }
}
