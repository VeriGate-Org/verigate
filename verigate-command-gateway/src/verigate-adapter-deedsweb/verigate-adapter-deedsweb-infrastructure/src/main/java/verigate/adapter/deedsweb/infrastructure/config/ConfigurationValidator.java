/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import verigate.adapter.deedsweb.infrastructure.constants.EnvironmentConstants;

/**
 * Validates DeedsWeb API configuration.
 */
public class ConfigurationValidator {

  private static final Logger LOGGER = Logger.getLogger(ConfigurationValidator.class.getName());

  /**
   * Validates the DeedsWeb API configuration.
   *
   * @param config the configuration to validate
   * @throws IllegalStateException if configuration is invalid
   */
  public static void validate(DeedsWebApiConfiguration config) {
    List<String> missingConfigurations = new ArrayList<>();

    // Check required configurations
    if (isBlank(config.getApiKey())) {
      missingConfigurations.add(EnvironmentConstants.DEEDSWEB_API_KEY);
    }

    if (isBlank(config.getBaseUrl())) {
      missingConfigurations.add(EnvironmentConstants.DEEDSWEB_BASE_URL);
    }

    // Validate timeout values
    try {
      if (config.getConnectionTimeoutMs() <= 0) {
        missingConfigurations.add(
            EnvironmentConstants.DEEDSWEB_CONNECTION_TIMEOUT_MS + " (must be positive)");
      }
    } catch (NumberFormatException e) {
      missingConfigurations.add(
          EnvironmentConstants.DEEDSWEB_CONNECTION_TIMEOUT_MS + " (must be numeric)");
    }

    try {
      if (config.getReadTimeoutMs() <= 0) {
        missingConfigurations.add(
            EnvironmentConstants.DEEDSWEB_READ_TIMEOUT_MS + " (must be positive)");
      }
    } catch (NumberFormatException e) {
      missingConfigurations.add(
          EnvironmentConstants.DEEDSWEB_READ_TIMEOUT_MS + " (must be numeric)");
    }

    // Validate retry configuration
    try {
      if (config.getRetryAttempts() < 0) {
        missingConfigurations.add(
            EnvironmentConstants.DEEDSWEB_RETRY_ATTEMPTS + " (must be non-negative)");
      }
    } catch (NumberFormatException e) {
      missingConfigurations.add(
          EnvironmentConstants.DEEDSWEB_RETRY_ATTEMPTS + " (must be numeric)");
    }

    try {
      if (config.getRetryDelayMs() < 0) {
        missingConfigurations.add(
            EnvironmentConstants.DEEDSWEB_RETRY_DELAY_MS + " (must be non-negative)");
      }
    } catch (NumberFormatException e) {
      missingConfigurations.add(
          EnvironmentConstants.DEEDSWEB_RETRY_DELAY_MS + " (must be numeric)");
    }

    // Report validation errors
    if (!missingConfigurations.isEmpty()) {
      String errorMessage =
          "DeedsWeb API configuration validation failed. Missing or invalid: "
              + String.join(", ", missingConfigurations);
      LOGGER.severe(errorMessage);
      throw new IllegalStateException(errorMessage);
    }

    LOGGER.info("DeedsWeb API configuration validation passed");
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
