/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.config;

import java.util.Properties;
import verigate.adapter.deedsweb.infrastructure.constants.EnvironmentConstants;

/** Configuration class for DeedsWeb adapter settings. */
public class DeedsWebApiConfiguration {

  private final Properties properties;

  public DeedsWebApiConfiguration(Properties properties) {
    this.properties = properties;
  }

  /**
   * Name of the AWS Secrets Manager secret holding the DeedsWeb SOAP credentials. The secret
   * payload is JSON of the form {@code {"username":"...","password":"..."}}.
   */
  public String getCredentialsSecretName() {
    return getOrDefault(
        EnvironmentConstants.DEFAULT_CREDENTIALS_SECRET_NAME,
        EnvironmentConstants.DEEDSWEB_CREDENTIALS_SECRET_NAME,
        EnvironmentConstants.PROPERTY_CREDENTIALS_SECRET_NAME);
  }

  /** SOAP endpoint base URL for the DeedsWeb registry. */
  public String getBaseUrl() {
    return getOrDefault(
        EnvironmentConstants.DEFAULT_BASE_URL,
        EnvironmentConstants.DEEDSWEB_BASE_URL,
        EnvironmentConstants.PROPERTY_BASE_URL);
  }

  /** Connection timeout in milliseconds. */
  public int getConnectionTimeoutMs() {
    return Integer.parseInt(
        getOrDefault(
            EnvironmentConstants.DEFAULT_CONNECTION_TIMEOUT_MS,
            EnvironmentConstants.DEEDSWEB_CONNECTION_TIMEOUT_MS,
            EnvironmentConstants.PROPERTY_CONNECTION_TIMEOUT_MS));
  }

  /** Read timeout in milliseconds. */
  public int getReadTimeoutMs() {
    return Integer.parseInt(
        getOrDefault(
            EnvironmentConstants.DEFAULT_READ_TIMEOUT_MS,
            EnvironmentConstants.DEEDSWEB_READ_TIMEOUT_MS,
            EnvironmentConstants.PROPERTY_READ_TIMEOUT_MS));
  }

  /** Number of retry attempts for transient failures. */
  public int getRetryAttempts() {
    return Integer.parseInt(
        getOrDefault(
            EnvironmentConstants.DEFAULT_RETRY_ATTEMPTS,
            EnvironmentConstants.DEEDSWEB_RETRY_ATTEMPTS,
            EnvironmentConstants.PROPERTY_RETRY_ATTEMPTS));
  }

  /** Delay between retries in milliseconds. */
  public int getRetryDelayMs() {
    return Integer.parseInt(
        getOrDefault(
            EnvironmentConstants.DEFAULT_RETRY_DELAY_MS,
            EnvironmentConstants.DEEDSWEB_RETRY_DELAY_MS,
            EnvironmentConstants.PROPERTY_RETRY_DELAY_MS));
  }

  /**
   * Returns the first non-blank value found by checking environment variables and properties for
   * the supplied keys (in order), falling back to {@code defaultValue} when none are set.
   */
  private String getOrDefault(String defaultValue, String... keys) {
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
    return defaultValue;
  }
}
