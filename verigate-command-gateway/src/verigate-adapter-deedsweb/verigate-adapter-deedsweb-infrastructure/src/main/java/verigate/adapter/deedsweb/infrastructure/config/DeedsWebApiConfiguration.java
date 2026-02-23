/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.config;

import java.util.Properties;
import verigate.adapter.deedsweb.infrastructure.constants.EnvironmentConstants;

/**
 * Configuration class for OpenSanctions API settings.
 * Provides access to environment variables and default values.
 */
public class DeedsWebApiConfiguration {

  private final Properties properties;

  public DeedsWebApiConfiguration(Properties properties) {
    this.properties = properties;
  }

  /**
   * Gets the OpenSanctions API key.
   */
  public String getApiKey() {
    return getProperty(EnvironmentConstants.OPENSANCTIONS_API_KEY);
  }

  /**
   * Gets the OpenSanctions API base URL.
   */
  public String getBaseUrl() {
    return getProperty(
        EnvironmentConstants.OPENSANCTIONS_BASE_URL, EnvironmentConstants.DEFAULT_BASE_URL);
  }

  /**
   * Gets the connection timeout in milliseconds.
   */
  public int getConnectionTimeoutMs() {
    String timeout =
        getProperty(
            EnvironmentConstants.OPENSANCTIONS_CONNECTION_TIMEOUT_MS,
            EnvironmentConstants.DEFAULT_CONNECTION_TIMEOUT_MS);
    return Integer.parseInt(timeout);
  }

  /**
   * Gets the read timeout in milliseconds.
   */
  public int getReadTimeoutMs() {
    String timeout =
        getProperty(
            EnvironmentConstants.OPENSANCTIONS_READ_TIMEOUT_MS,
            EnvironmentConstants.DEFAULT_READ_TIMEOUT_MS);
    return Integer.parseInt(timeout);
  }

  /**
   * Gets the number of retry attempts.
   */
  public int getRetryAttempts() {
    String retries =
        getProperty(
            EnvironmentConstants.OPENSANCTIONS_RETRY_ATTEMPTS,
            EnvironmentConstants.DEFAULT_RETRY_ATTEMPTS);
    return Integer.parseInt(retries);
  }

  /**
   * Gets the retry delay in milliseconds.
   */
  public int getRetryDelayMs() {
    String delay =
        getProperty(
            EnvironmentConstants.OPENSANCTIONS_RETRY_DELAY_MS,
            EnvironmentConstants.DEFAULT_RETRY_DELAY_MS);
    return Integer.parseInt(delay);
  }

  private String getProperty(String key) {
    String value = System.getenv(key);
    if (value != null) {
      return value;
    }
    return properties.getProperty(key);
  }

  private String getProperty(String key, String defaultValue) {
    String value = getProperty(key);
    return value != null ? value : defaultValue;
  }
}
