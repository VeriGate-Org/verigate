/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.config;

import java.util.Properties;
import verigate.adapter.deedsweb.infrastructure.constants.EnvironmentConstants;

/** Configuration class for DeedsWeb API settings. */
public class DeedsWebApiConfiguration {

  private final Properties properties;

  public DeedsWebApiConfiguration(Properties properties) {
    this.properties = properties;
  }

  /** Gets the DeedsWeb API key. */
  public String getApiKey() {
    return getFirstDefined(
        EnvironmentConstants.DEEDSWEB_API_KEY,
        EnvironmentConstants.LEGACY_API_KEY,
        EnvironmentConstants.PROPERTY_API_KEY,
        EnvironmentConstants.LEGACY_PROPERTY_API_KEY);
  }

  /** Gets the DeedsWeb API base URL. */
  public String getBaseUrl() {
    return getFirstDefined(
        EnvironmentConstants.DEFAULT_BASE_URL,
        EnvironmentConstants.DEEDSWEB_BASE_URL,
        EnvironmentConstants.LEGACY_BASE_URL,
        EnvironmentConstants.PROPERTY_BASE_URL,
        EnvironmentConstants.LEGACY_PROPERTY_BASE_URL);
  }

  /** Gets the connection timeout in milliseconds. */
  public int getConnectionTimeoutMs() {
    String timeout =
        getFirstDefined(
            EnvironmentConstants.DEFAULT_CONNECTION_TIMEOUT_MS,
            EnvironmentConstants.DEEDSWEB_CONNECTION_TIMEOUT_MS,
            EnvironmentConstants.LEGACY_CONNECTION_TIMEOUT_MS,
            EnvironmentConstants.PROPERTY_CONNECTION_TIMEOUT_MS,
            EnvironmentConstants.LEGACY_PROPERTY_CONNECTION_TIMEOUT_MS);
    return Integer.parseInt(timeout);
  }

  /** Gets the read timeout in milliseconds. */
  public int getReadTimeoutMs() {
    String timeout =
        getFirstDefined(
            EnvironmentConstants.DEFAULT_READ_TIMEOUT_MS,
            EnvironmentConstants.DEEDSWEB_READ_TIMEOUT_MS,
            EnvironmentConstants.LEGACY_READ_TIMEOUT_MS,
            EnvironmentConstants.PROPERTY_READ_TIMEOUT_MS,
            EnvironmentConstants.LEGACY_PROPERTY_READ_TIMEOUT_MS);
    return Integer.parseInt(timeout);
  }

  /** Gets the number of retry attempts. */
  public int getRetryAttempts() {
    String retries =
        getFirstDefined(
            EnvironmentConstants.DEFAULT_RETRY_ATTEMPTS,
            EnvironmentConstants.DEEDSWEB_RETRY_ATTEMPTS,
            EnvironmentConstants.LEGACY_RETRY_ATTEMPTS,
            EnvironmentConstants.PROPERTY_RETRY_ATTEMPTS,
            EnvironmentConstants.LEGACY_PROPERTY_RETRY_ATTEMPTS);
    return Integer.parseInt(retries);
  }

  /** Gets the retry delay in milliseconds. */
  public int getRetryDelayMs() {
    String delay =
        getFirstDefined(
            EnvironmentConstants.DEFAULT_RETRY_DELAY_MS,
            EnvironmentConstants.DEEDSWEB_RETRY_DELAY_MS,
            EnvironmentConstants.LEGACY_RETRY_DELAY_MS,
            EnvironmentConstants.PROPERTY_RETRY_DELAY_MS,
            EnvironmentConstants.LEGACY_PROPERTY_RETRY_DELAY_MS);
    return Integer.parseInt(delay);
  }

  private String getFirstDefined(String... keys) {
    for (String key : keys) {
      String value = System.getenv(key);
      if (value != null && !value.isBlank()) {
        return value;
      }

      value = properties.getProperty(key);
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return null;
  }
}
