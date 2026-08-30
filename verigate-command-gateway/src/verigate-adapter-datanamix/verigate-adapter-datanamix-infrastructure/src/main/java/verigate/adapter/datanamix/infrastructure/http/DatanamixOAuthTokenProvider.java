/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.datanamix.infrastructure.config.DatanamixApiConfiguration;

/**
 * Obtains and caches OAuth2 access tokens via Datanamix's client-credentials flow
 * ({@code POST /v1/oauth/token}). Only used when {@link DatanamixApiConfiguration#isLive()}
 * — sandbox requests need no token at all.
 *
 * <p>Tokens are cached in memory until shortly before their reported expiry (Datanamix's
 * docs describe a ~4 hour lifetime); a fresh token is requested automatically once the
 * cached one expires.
 */
public class DatanamixOAuthTokenProvider {

  private static final Logger logger = LoggerFactory.getLogger(DatanamixOAuthTokenProvider.class);
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
  private static final Duration EXPIRY_SAFETY_MARGIN = Duration.ofMinutes(1);
  private static final String TOKEN_ENDPOINT = "/v1/oauth/token";

  private final DatanamixApiConfiguration configuration;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  private volatile String cachedToken;
  private volatile Instant cachedTokenExpiry;

  /** Constructs a new instance. */
  public DatanamixOAuthTokenProvider(
      DatanamixApiConfiguration configuration, ObjectMapper objectMapper) {
    this.configuration = configuration;
    this.objectMapper = objectMapper;
    this.httpClient = HttpClient.newBuilder().connectTimeout(DEFAULT_TIMEOUT).build();
  }

  /**
   * Returns a valid bearer token, fetching a new one if none is cached or the cached
   * token is at/near expiry.
   */
  public synchronized String getAccessToken() throws TransientException, PermanentException {
    if (cachedToken != null && cachedTokenExpiry != null
        && Instant.now().isBefore(cachedTokenExpiry.minus(EXPIRY_SAFETY_MARGIN))) {
      return cachedToken;
    }
    return requestNewToken();
  }

  private String requestNewToken() throws TransientException, PermanentException {
    String clientId = configuration.getClientId();
    String clientSecret = configuration.getClientSecret();
    if (clientId == null || clientId.trim().isEmpty()
        || clientSecret == null || clientSecret.trim().isEmpty()) {
      throw new IllegalStateException(
          "DATANAMIX_CLIENT_ID and DATANAMIX_CLIENT_SECRET are required for LIVE requests");
    }

    TokenRequestDto requestBody = new TokenRequestDto("client_credentials", clientId, clientSecret);

    String payload;
    try {
      payload = objectMapper.writeValueAsString(requestBody);
    } catch (IOException e) {
      throw new PermanentException("Failed to serialize Datanamix token request", e);
    }

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(configuration.getBaseUrl() + TOKEN_ENDPOINT))
        .timeout(DEFAULT_TIMEOUT)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(payload))
        .build();

    HttpResponse<String> response;
    try {
      response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      logger.warn("Datanamix token request network error: {}", e.getMessage());
      throw new TransientException("Failed to reach Datanamix token endpoint", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new TransientException("Datanamix token request interrupted", e);
    }

    if (response.statusCode() != 200) {
      logger.error(
          "Datanamix token request failed: status={}, body={}",
          response.statusCode(), response.body());
      throw new PermanentException(
          "Datanamix token request failed with status " + response.statusCode());
    }

    TokenResponseDto tokenResponse;
    try {
      tokenResponse = objectMapper.readValue(response.body(), TokenResponseDto.class);
    } catch (IOException e) {
      throw new PermanentException("Failed to parse Datanamix token response", e);
    }

    if (tokenResponse.accessToken() == null || tokenResponse.accessToken().isEmpty()) {
      throw new PermanentException("Datanamix token response did not contain an access token");
    }

    cachedToken = tokenResponse.accessToken();
    long expiresInSeconds = tokenResponse.expiresIn() != null ? tokenResponse.expiresIn() : 3600L;
    cachedTokenExpiry = Instant.now().plusSeconds(expiresInSeconds);

    logger.info("Obtained new Datanamix access token, expires in {}s", expiresInSeconds);
    return cachedToken;
  }

  private record TokenRequestDto(
      @JsonProperty("grant_type") String grantType,
      @JsonProperty("client_id") String clientId,
      @JsonProperty("client_secret") String clientSecret
  ) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record TokenResponseDto(
      @JsonProperty("access_token") String accessToken,
      @JsonProperty("expires_in") Long expiresIn
  ) {
  }
}
