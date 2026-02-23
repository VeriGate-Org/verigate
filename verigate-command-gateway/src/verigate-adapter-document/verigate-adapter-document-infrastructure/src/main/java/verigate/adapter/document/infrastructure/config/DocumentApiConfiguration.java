/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.config;

import crosscutting.config.Config;
import crosscutting.environment.Environment;
import verigate.adapter.document.domain.constants.DomainConstants;
import verigate.adapter.document.infrastructure.constants.EnvironmentConstants;

/**
 * Configuration class for the Document Verification API integration. Provides type-safe access to
 * configuration properties with sensible defaults.
 */
public class DocumentApiConfiguration {

  private final Environment environment;
  private final Config config;

  /**
   * Creates a new configuration wrapper around the shared environment and config services.
   *
   * @param environment runtime environment provider
   * @param config fallback configuration provider
   */
  public DocumentApiConfiguration(Environment environment, Config config) {
    this.environment = environment;
    this.config = config;
  }

  /**
   * Returns the base URL for the Document Verification API.
   */
  public String getApiUrl() {
    String apiUrl = getValue(EnvironmentConstants.DOCUMENT_API_URL, "document.api.base-url");
    return apiUrl != null ? apiUrl : EnvironmentConstants.DEFAULT_DOCUMENT_API_URL;
  }

  /**
   * Returns the S3 bucket name for document storage.
   */
  public String getS3Bucket() {
    return getValue(EnvironmentConstants.DOCUMENT_S3_BUCKET, "document.s3.bucket");
  }

  /**
   * Returns the HTTP timeout in seconds for API calls.
   */
  public int getHttpTimeoutSeconds() {
    return getIntValue(
        EnvironmentConstants.DOCUMENT_HTTP_TIMEOUT_SECONDS,
        "document.http.timeout-seconds",
        DomainConstants.DEFAULT_HTTP_TIMEOUT_SECONDS);
  }

  /**
   * Returns the number of retry attempts for HTTP calls.
   */
  public int getHttpRetryAttempts() {
    return getIntValue(
        EnvironmentConstants.DOCUMENT_HTTP_RETRY_ATTEMPTS,
        "document.http.retry-attempts",
        DomainConstants.DEFAULT_RETRY_ATTEMPTS);
  }

  /**
   * Returns the delay between retry attempts in milliseconds.
   */
  public int getHttpRetryDelayMs() {
    return getIntValue(
        EnvironmentConstants.DOCUMENT_HTTP_RETRY_DELAY_MS,
        "document.http.retry-delay-ms",
        DomainConstants.DEFAULT_RETRY_DELAY_MS);
  }

  /**
   * Returns the requests-per-second rate limit.
   */
  public int getRateLimitRps() {
    return getIntValue(
        EnvironmentConstants.DOCUMENT_RATE_LIMIT_RPS,
        "document.rate-limit.requests-per-second",
        DomainConstants.DEFAULT_RATE_LIMIT_RPS);
  }

  /**
   * Returns the burst capacity for rate limiting.
   */
  public int getRateLimitBurst() {
    return getIntValue(
        EnvironmentConstants.DOCUMENT_RATE_LIMIT_BURST,
        "document.rate-limit.burst-capacity",
        DomainConstants.DEFAULT_RATE_LIMIT_BURST);
  }

  /**
   * Indicates whether HTTP request logging is enabled.
   */
  public boolean isRequestLoggingEnabled() {
    return getBooleanValue(
        EnvironmentConstants.DOCUMENT_ENABLE_REQUEST_LOGGING,
        "document.logging.request-enabled",
        DomainConstants.DEFAULT_ENABLE_REQUEST_LOGGING);
  }

  /**
   * Indicates whether HTTP response logging is enabled.
   */
  public boolean isResponseLoggingEnabled() {
    return getBooleanValue(
        EnvironmentConstants.DOCUMENT_ENABLE_RESPONSE_LOGGING,
        "document.logging.response-enabled",
        DomainConstants.DEFAULT_ENABLE_RESPONSE_LOGGING);
  }

  /**
   * Returns the desired logging level for HTTP operations.
   */
  public String getLogLevel() {
    String level = getValue(EnvironmentConstants.DOCUMENT_LOG_LEVEL, "document.logging.level");
    return level != null ? level : DomainConstants.DEFAULT_LOG_LEVEL;
  }

  /**
   * Returns the configured HTTP connection pool size.
   */
  public int getHttpConnectionPoolSize() {
    return getIntValue(
        EnvironmentConstants.DOCUMENT_HTTP_TIMEOUT_SECONDS,
        "document.http.connection-pool-size",
        5);
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
    String apiUrl = getApiUrl();
    if (apiUrl == null || apiUrl.trim().isEmpty()) {
      throw new IllegalStateException("Document API URL is required");
    }

    if (!apiUrl.startsWith("http://") && !apiUrl.startsWith("https://")) {
      throw new IllegalStateException("Document API URL must start with http or https");
    }
  }
}
