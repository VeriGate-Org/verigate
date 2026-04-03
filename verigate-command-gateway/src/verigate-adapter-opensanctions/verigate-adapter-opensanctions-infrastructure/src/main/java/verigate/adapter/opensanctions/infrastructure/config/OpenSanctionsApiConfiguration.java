/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.config;

import crosscutting.config.Config;
import crosscutting.environment.Environment;
import verigate.adapter.opensanctions.domain.constants.DomainConstants;
import verigate.adapter.opensanctions.infrastructure.constants.EnvironmentConstants;

/**
 * Configuration class for the OpenSanctions API integration. Provides type-safe access to
 * configuration properties with sensible defaults.
 */
public class OpenSanctionsApiConfiguration {

  private final Environment environment;
  private final Config config;

  public OpenSanctionsApiConfiguration(Environment environment, Config config) {
    this.environment = environment;
    this.config = config;
  }

  /** Returns the API key used to authenticate against the OpenSanctions API. */
  public String getApiKey() {
    return environment.get(EnvironmentConstants.OPENSANCTIONS_API_KEY);
  }

  /** Returns the base URL for the OpenSanctions API. */
  public String getBaseUrl() {
    String apiUrl =
        getValue(EnvironmentConstants.OPENSANCTIONS_BASE_URL, "opensanctions.api.base-url");
    return apiUrl != null ? apiUrl : EnvironmentConstants.DEFAULT_BASE_URL;
  }

  /** Gets the connection timeout in milliseconds. */
  public int getConnectionTimeoutMs() {
    return getIntValue(
        EnvironmentConstants.OPENSANCTIONS_CONNECTION_TIMEOUT_MS,
        "opensanctions.http.connection-timeout-ms",
        Integer.parseInt(EnvironmentConstants.DEFAULT_CONNECTION_TIMEOUT_MS));
  }

  /** Gets the read timeout in milliseconds. */
  public int getReadTimeoutMs() {
    return getIntValue(
        EnvironmentConstants.OPENSANCTIONS_READ_TIMEOUT_MS,
        "opensanctions.http.read-timeout-ms",
        Integer.parseInt(EnvironmentConstants.DEFAULT_READ_TIMEOUT_MS));
  }

  /** Gets the number of retry attempts. */
  public int getRetryAttempts() {
    return getIntValue(
        EnvironmentConstants.OPENSANCTIONS_RETRY_ATTEMPTS,
        "opensanctions.http.retry-attempts",
        Integer.parseInt(EnvironmentConstants.DEFAULT_RETRY_ATTEMPTS));
  }

  /** Gets the retry delay in milliseconds. */
  public int getRetryDelayMs() {
    return getIntValue(
        EnvironmentConstants.OPENSANCTIONS_RETRY_DELAY_MS,
        "opensanctions.http.retry-delay-ms",
        Integer.parseInt(EnvironmentConstants.DEFAULT_RETRY_DELAY_MS));
  }

  /** Returns the requests-per-second rate limit. */
  public int getRateLimitRps() {
    return getIntValue(
        EnvironmentConstants.OPENSANCTIONS_RATE_LIMIT_RPS,
        "opensanctions.rate-limit.requests-per-second",
        DomainConstants.DEFAULT_RATE_LIMIT_RPS);
  }

  /** Returns the burst capacity for rate limiting. */
  public int getRateLimitBurst() {
    return getIntValue(
        EnvironmentConstants.OPENSANCTIONS_RATE_LIMIT_BURST,
        "opensanctions.rate-limit.burst-capacity",
        DomainConstants.DEFAULT_RATE_LIMIT_BURST);
  }

  /** Returns the API key with sensitive characters masked. */
  public String getMaskedApiKey() {
    String apiKey = getApiKey();
    if (apiKey == null || apiKey.trim().isEmpty()) {
      return "***";
    }
    String trimmedKey = apiKey.trim();
    if (trimmedKey.length() < 8) {
      return "***";
    }
    return trimmedKey.substring(0, 4) + "***" + trimmedKey.substring(trimmedKey.length() - 4);
  }

  /** Returns the base URL with any credentials removed for safe logging. */
  public String getMaskedApiUrl() {
    String apiUrl = getBaseUrl();
    if (apiUrl == null) {
      return "";
    }
    int credentialsSeparator = apiUrl.indexOf("@");
    String sanitizedUrl =
        credentialsSeparator > 0 ? apiUrl.substring(credentialsSeparator + 1) : apiUrl;

    int querySeparator = sanitizedUrl.indexOf("?");
    if (querySeparator >= 0) {
      return sanitizedUrl.substring(0, querySeparator + 1) + "***";
    }

    return sanitizedUrl;
  }

  /**
   * Validates that the configuration contains the required values.
   *
   * @throws IllegalStateException when mandatory values are missing
   */
  public void validate() {
    String apiKey = getApiKey();
    if (apiKey == null || apiKey.trim().isEmpty()) {
      throw new IllegalStateException("OpenSanctions API key is required");
    }

    String apiUrl = getBaseUrl();
    if (apiUrl == null || apiUrl.trim().isEmpty()) {
      throw new IllegalStateException("OpenSanctions API URL is required");
    }

    if (!apiUrl.startsWith("http://") && !apiUrl.startsWith("https://")) {
      throw new IllegalStateException("OpenSanctions API URL must start with http or https");
    }
  }

  private String getValue(String environmentKey, String configKey) {
    String value = environment.get(environmentKey);
    if (value == null || value.trim().isEmpty()) {
      value = config.get(configKey);
    }
    return value;
  }

  private int getIntValue(String environmentKey, String configKey, int defaultValue) {
    String value = getValue(environmentKey, configKey);
    if (value == null || value.trim().isEmpty()) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
