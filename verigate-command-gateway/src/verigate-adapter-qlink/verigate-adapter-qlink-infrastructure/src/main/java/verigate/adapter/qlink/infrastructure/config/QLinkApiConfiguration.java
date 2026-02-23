/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.infrastructure.config;

import crosscutting.config.Config;
import crosscutting.environment.Environment;
import verigate.adapter.qlink.domain.constants.DomainConstants;
import verigate.adapter.qlink.infrastructure.constants.EnvironmentConstants;

/**
 * Configuration class for the QLink Bank Verification API integration. Provides type-safe access to
 * configuration properties with sensible defaults.
 */
public class QLinkApiConfiguration {

  private final Environment environment;
  private final Config config;

  /**
   * Creates a new configuration wrapper around the shared environment and config services.
   *
   * @param environment runtime environment provider
   * @param config fallback configuration provider
   */
  public QLinkApiConfiguration(Environment environment, Config config) {
    this.environment = environment;
    this.config = config;
  }

  /**
   * Returns the client ID used to authenticate against the QLink API.
   */
  public String getClientId() {
    return environment.get(EnvironmentConstants.QLINK_CLIENT_ID);
  }

  /**
   * Returns the username used to authenticate against the QLink API.
   */
  public String getUsername() {
    return environment.get(EnvironmentConstants.QLINK_USERNAME);
  }

  /**
   * Returns the password used to authenticate against the QLink API.
   */
  public String getPassword() {
    return environment.get(EnvironmentConstants.QLINK_PASSWORD);
  }

  /**
   * Returns the API URL for the QLink bank verification API.
   */
  public String getApiUrl() {
    String apiUrl = getValue(EnvironmentConstants.QLINK_API_URL, "qlink.api.url");
    return apiUrl != null ? apiUrl : EnvironmentConstants.DEFAULT_QLINK_API_URL;
  }

  /**
   * Returns the HTTP timeout in seconds for API calls.
   */
  public int getHttpTimeoutSeconds() {
    return getIntValue(
        EnvironmentConstants.QLINK_HTTP_TIMEOUT_SECONDS,
        "qlink.http.timeout-seconds",
        DomainConstants.DEFAULT_HTTP_TIMEOUT_SECONDS);
  }

  /**
   * Returns the number of retry attempts for HTTP calls.
   */
  public int getHttpRetryAttempts() {
    return getIntValue(
        EnvironmentConstants.QLINK_HTTP_RETRY_ATTEMPTS,
        "qlink.http.retry-attempts",
        DomainConstants.DEFAULT_RETRY_ATTEMPTS);
  }

  /**
   * Returns the delay between retry attempts in milliseconds.
   */
  public int getHttpRetryDelayMs() {
    return getIntValue(
        EnvironmentConstants.QLINK_HTTP_RETRY_DELAY_MS,
        "qlink.http.retry-delay-ms",
        DomainConstants.DEFAULT_RETRY_DELAY_MS);
  }

  /**
   * Returns the requests-per-second rate limit.
   */
  public int getRateLimitRps() {
    return getIntValue(
        EnvironmentConstants.QLINK_RATE_LIMIT_RPS,
        "qlink.rate-limit.requests-per-second",
        DomainConstants.DEFAULT_RATE_LIMIT_RPS);
  }

  /**
   * Returns the burst capacity for rate limiting.
   */
  public int getRateLimitBurst() {
    return getIntValue(
        EnvironmentConstants.QLINK_RATE_LIMIT_BURST,
        "qlink.rate-limit.burst-capacity",
        DomainConstants.DEFAULT_RATE_LIMIT_BURST);
  }

  /**
   * Indicates whether HTTP request logging is enabled.
   */
  public boolean isRequestLoggingEnabled() {
    return getBooleanValue(
        EnvironmentConstants.QLINK_ENABLE_REQUEST_LOGGING,
        "qlink.logging.request-enabled",
        DomainConstants.DEFAULT_ENABLE_REQUEST_LOGGING);
  }

  /**
   * Indicates whether HTTP response logging is enabled.
   */
  public boolean isResponseLoggingEnabled() {
    return getBooleanValue(
        EnvironmentConstants.QLINK_ENABLE_RESPONSE_LOGGING,
        "qlink.logging.response-enabled",
        DomainConstants.DEFAULT_ENABLE_RESPONSE_LOGGING);
  }

  /**
   * Returns the desired logging level for HTTP operations.
   */
  public String getLogLevel() {
    String level = getValue(EnvironmentConstants.QLINK_LOG_LEVEL, "qlink.logging.level");
    return level != null ? level : DomainConstants.DEFAULT_LOG_LEVEL;
  }

  /**
   * Returns the configured HTTP connection pool size.
   */
  public int getHttpConnectionPoolSize() {
    return getIntValue(
        EnvironmentConstants.QLINK_HTTP_TIMEOUT_SECONDS, "qlink.http.connection-pool-size", 5);
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
   * Returns the client ID with sensitive characters masked.
   */
  public String getMaskedClientId() {
    String clientId = getClientId();
    if (clientId == null || clientId.trim().isEmpty()) {
      return "***";
    }
    String trimmedId = clientId.trim();
    if (trimmedId.length() < 8) {
      return "***";
    }
    return trimmedId.substring(0, 4) + "***" + trimmedId.substring(trimmedId.length() - 4);
  }

  /**
   * Returns the API URL with any credentials removed for safe logging.
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
    String clientId = getClientId();
    if (clientId == null || clientId.trim().isEmpty()) {
      throw new IllegalStateException("QLink client ID is required");
    }

    String username = getUsername();
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalStateException("QLink username is required");
    }

    String password = getPassword();
    if (password == null || password.trim().isEmpty()) {
      throw new IllegalStateException("QLink password is required");
    }

    String apiUrl = getApiUrl();
    if (apiUrl == null || apiUrl.trim().isEmpty()) {
      throw new IllegalStateException("QLink API URL is required");
    }

    if (!apiUrl.startsWith("http://") && !apiUrl.startsWith("https://")) {
      throw new IllegalStateException("QLink API URL must start with http or https");
    }
  }
}
