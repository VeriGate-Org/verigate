/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.config;

import crosscutting.config.Config;
import crosscutting.environment.Environment;
import verigate.adapter.cipc.domain.constants.DomainConstants;
import verigate.adapter.cipc.infrastructure.constants.EnvironmentConstants;

/**
 * Configuration class for the CIPC Public Data API integration. Provides type-safe access to
 * configuration properties with sensible defaults.
 */
public class CipcApiConfiguration {

  private final Environment environment;
  private final Config config;

  /**
   * Creates a new configuration wrapper around the shared environment and config services.
   *
   * @param environment runtime environment provider
   * @param config fallback configuration provider
   */
  public CipcApiConfiguration(Environment environment, Config config) {
    this.environment = environment;
    this.config = config;
  }

  /**
   * Returns the API key used to authenticate against the CIPC API.
   */
  public String getApiKey() {
    return environment.get(EnvironmentConstants.CIPC_API_KEY);
  }

  /**
   * Returns the base URL for the CIPC API.
   */
  public String getBaseUrl() {
    String baseUrl = getValue(EnvironmentConstants.CIPC_BASE_URL, "cipc.api.base-url");
    return baseUrl != null ? baseUrl : EnvironmentConstants.DEFAULT_CIPC_BASE_URL;
  }

  /**
   * Returns the HTTP timeout in seconds for API calls.
   */
  public int getHttpTimeoutSeconds() {
    return getIntValue(
        EnvironmentConstants.CIPC_HTTP_TIMEOUT_SECONDS,
        "cipc.http.timeout-seconds",
        DomainConstants.DEFAULT_HTTP_TIMEOUT_SECONDS);
  }

  /**
   * Returns the number of retry attempts for HTTP calls.
   */
  public int getHttpRetryAttempts() {
    return getIntValue(
        EnvironmentConstants.CIPC_HTTP_RETRY_ATTEMPTS,
        "cipc.http.retry-attempts",
        DomainConstants.DEFAULT_RETRY_ATTEMPTS);
  }

  /**
   * Returns the delay between retry attempts in milliseconds.
   */
  public int getHttpRetryDelayMs() {
    return getIntValue(
        EnvironmentConstants.CIPC_HTTP_RETRY_DELAY_MS,
        "cipc.http.retry-delay-ms",
        DomainConstants.DEFAULT_RETRY_DELAY_MS);
  }

  /**
   * Returns the requests-per-second rate limit.
   */
  public int getRateLimitRps() {
    return getIntValue(
        EnvironmentConstants.CIPC_RATE_LIMIT_RPS,
        "cipc.rate-limit.requests-per-second",
        DomainConstants.DEFAULT_RATE_LIMIT_RPS);
  }

  /**
   * Returns the burst capacity for rate limiting.
   */
  public int getRateLimitBurst() {
    return getIntValue(
        EnvironmentConstants.CIPC_RATE_LIMIT_BURST,
        "cipc.rate-limit.burst-capacity",
        DomainConstants.DEFAULT_RATE_LIMIT_BURST);
  }

  /**
   * Indicates whether HTTP request logging is enabled.
   */
  public boolean isRequestLoggingEnabled() {
    return getBooleanValue(
        EnvironmentConstants.CIPC_ENABLE_REQUEST_LOGGING,
        "cipc.logging.request-enabled",
        DomainConstants.DEFAULT_ENABLE_REQUEST_LOGGING);
  }

  /**
   * Indicates whether HTTP response logging is enabled.
   */
  public boolean isResponseLoggingEnabled() {
    return getBooleanValue(
        EnvironmentConstants.CIPC_ENABLE_RESPONSE_LOGGING,
        "cipc.logging.response-enabled",
        DomainConstants.DEFAULT_ENABLE_RESPONSE_LOGGING);
  }

  /**
   * Returns the desired logging level for HTTP operations.
   */
  public String getLogLevel() {
    String level = getValue(EnvironmentConstants.CIPC_LOG_LEVEL, "cipc.logging.level");
    return level != null ? level : DomainConstants.DEFAULT_LOG_LEVEL;
  }

  /**
   * Returns the configured HTTP connection pool size.
   */
  public int getHttpConnectionPoolSize() {
    return getIntValue(
        EnvironmentConstants.CIPC_HTTP_TIMEOUT_SECONDS, "cipc.http.connection-pool-size", 5);
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

  private boolean getBooleanValue(String environmentKey, String configKey, boolean defaultValue) {
    String value = getValue(environmentKey, configKey);
    return value != null ? Boolean.parseBoolean(value.trim()) : defaultValue;
  }

  /**
   * Returns the API key with sensitive characters masked.
   */
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

  /**
   * Returns the base URL with any credentials removed for safe logging.
   */
  public String getMaskedBaseUrl() {
    String baseUrl = getBaseUrl();
    if (baseUrl == null) {
      return "";
    }
    int credentialsSeparator = baseUrl.indexOf("@");
    String sanitizedUrl =
        credentialsSeparator > 0 ? baseUrl.substring(credentialsSeparator + 1) : baseUrl;

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
      throw new IllegalStateException("CIPC API key is required");
    }

    String baseUrl = getBaseUrl();
    if (baseUrl == null || baseUrl.trim().isEmpty()) {
      throw new IllegalStateException("CIPC base URL is required");
    }

    if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
      throw new IllegalStateException("CIPC base URL must start with http or https");
    }
  }
}
