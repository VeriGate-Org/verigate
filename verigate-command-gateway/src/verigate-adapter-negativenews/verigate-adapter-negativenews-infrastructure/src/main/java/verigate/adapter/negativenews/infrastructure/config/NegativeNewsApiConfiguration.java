/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.config;

import crosscutting.config.Config;
import crosscutting.environment.Environment;
import verigate.adapter.negativenews.domain.constants.DomainConstants;
import verigate.adapter.negativenews.infrastructure.constants.EnvironmentConstants;

/**
 * Configuration class for the Negative News Screening API integration. Provides type-safe access to
 * configuration properties with sensible defaults.
 */
public class NegativeNewsApiConfiguration {

  private final Environment environment;
  private final Config config;

  /**
   * Creates a new configuration wrapper around the shared environment and config services.
   *
   * @param environment runtime environment provider
   * @param config fallback configuration provider
   */
  public NegativeNewsApiConfiguration(Environment environment, Config config) {
    this.environment = environment;
    this.config = config;
  }

  /**
   * Returns the API key used to authenticate against the Negative News API.
   */
  public String getApiKey() {
    return environment.get(EnvironmentConstants.NEGATIVENEWS_API_KEY);
  }

  /**
   * Returns the base URL for the Negative News API.
   */
  public String getApiUrl() {
    String apiUrl = getValue(
        EnvironmentConstants.NEGATIVENEWS_API_URL, "negativenews.api.base-url");
    return apiUrl != null ? apiUrl : EnvironmentConstants.DEFAULT_NEGATIVENEWS_API_URL;
  }

  /**
   * Returns the HTTP timeout in seconds for API calls.
   */
  public int getHttpTimeoutSeconds() {
    return getIntValue(
        EnvironmentConstants.NEGATIVENEWS_HTTP_TIMEOUT_SECONDS,
        "negativenews.http.timeout-seconds",
        DomainConstants.DEFAULT_HTTP_TIMEOUT_SECONDS);
  }

  /**
   * Returns the number of retry attempts for HTTP calls.
   */
  public int getHttpRetryAttempts() {
    return getIntValue(
        EnvironmentConstants.NEGATIVENEWS_HTTP_RETRY_ATTEMPTS,
        "negativenews.http.retry-attempts",
        DomainConstants.DEFAULT_RETRY_ATTEMPTS);
  }

  /**
   * Returns the delay between retry attempts in milliseconds.
   */
  public int getHttpRetryDelayMs() {
    return getIntValue(
        EnvironmentConstants.NEGATIVENEWS_HTTP_RETRY_DELAY_MS,
        "negativenews.http.retry-delay-ms",
        DomainConstants.DEFAULT_RETRY_DELAY_MS);
  }

  /**
   * Returns the requests-per-second rate limit.
   */
  public int getRateLimitRps() {
    return getIntValue(
        EnvironmentConstants.NEGATIVENEWS_RATE_LIMIT_RPS,
        "negativenews.rate-limit.requests-per-second",
        DomainConstants.DEFAULT_RATE_LIMIT_RPS);
  }

  /**
   * Returns the burst capacity for rate limiting.
   */
  public int getRateLimitBurst() {
    return getIntValue(
        EnvironmentConstants.NEGATIVENEWS_RATE_LIMIT_BURST,
        "negativenews.rate-limit.burst-capacity",
        DomainConstants.DEFAULT_RATE_LIMIT_BURST);
  }

  /**
   * Indicates whether HTTP request logging is enabled.
   */
  public boolean isRequestLoggingEnabled() {
    return getBooleanValue(
        EnvironmentConstants.NEGATIVENEWS_ENABLE_REQUEST_LOGGING,
        "negativenews.logging.request-enabled",
        DomainConstants.DEFAULT_ENABLE_REQUEST_LOGGING);
  }

  /**
   * Indicates whether HTTP response logging is enabled.
   */
  public boolean isResponseLoggingEnabled() {
    return getBooleanValue(
        EnvironmentConstants.NEGATIVENEWS_ENABLE_RESPONSE_LOGGING,
        "negativenews.logging.response-enabled",
        DomainConstants.DEFAULT_ENABLE_RESPONSE_LOGGING);
  }

  /**
   * Returns the desired logging level for HTTP operations.
   */
  public String getLogLevel() {
    String level = getValue(
        EnvironmentConstants.NEGATIVENEWS_LOG_LEVEL, "negativenews.logging.level");
    return level != null ? level : DomainConstants.DEFAULT_LOG_LEVEL;
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

  private boolean getBooleanValue(
      String environmentKey, String configKey, boolean defaultValue) {
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
  public String getMaskedApiUrl() {
    String apiUrl = getApiUrl();
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
      throw new IllegalStateException("Negative News API key is required");
    }

    String apiUrl = getApiUrl();
    if (apiUrl == null || apiUrl.trim().isEmpty()) {
      throw new IllegalStateException("Negative News API URL is required");
    }

    if (!apiUrl.startsWith("http://") && !apiUrl.startsWith("https://")) {
      throw new IllegalStateException("Negative News API URL must start with http or https");
    }
  }
}
