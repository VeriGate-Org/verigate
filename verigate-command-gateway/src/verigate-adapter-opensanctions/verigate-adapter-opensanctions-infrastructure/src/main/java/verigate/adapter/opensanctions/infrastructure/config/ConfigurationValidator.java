/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import verigate.adapter.opensanctions.infrastructure.constants.EnvironmentConstants;

/**
 * Validates OpenSanctions API configuration.
 */
public class ConfigurationValidator {

  private static final Logger LOGGER = Logger.getLogger(ConfigurationValidator.class.getName());

  /**
   * Validates the OpenSanctions API configuration.
   *
   * @param config the configuration to validate
   * @throws IllegalStateException if configuration is invalid
   */
  public static void validate(OpenSanctionsApiConfiguration config) {
    List<String> missingConfigurations = new ArrayList<>();

    // Check required configurations
    if (isBlank(config.getApiKey())) {
      missingConfigurations.add(EnvironmentConstants.OPENSANCTIONS_API_KEY);
    }

    if (isBlank(config.getBaseUrl())) {
      missingConfigurations.add(EnvironmentConstants.OPENSANCTIONS_BASE_URL);
    }

    // Validate timeout values
    try {
      if (config.getConnectionTimeoutMs() <= 0) {
        missingConfigurations.add(
            EnvironmentConstants.OPENSANCTIONS_CONNECTION_TIMEOUT_MS + " (must be positive)");
      }
    } catch (NumberFormatException e) {
      missingConfigurations.add(
          EnvironmentConstants.OPENSANCTIONS_CONNECTION_TIMEOUT_MS + " (must be numeric)");
    }

    try {
      if (config.getReadTimeoutMs() <= 0) {
        missingConfigurations.add(
            EnvironmentConstants.OPENSANCTIONS_READ_TIMEOUT_MS + " (must be positive)");
      }
    } catch (NumberFormatException e) {
      missingConfigurations.add(
          EnvironmentConstants.OPENSANCTIONS_READ_TIMEOUT_MS + " (must be numeric)");
    }

    // Validate retry configuration
    try {
      if (config.getRetryAttempts() < 0) {
        missingConfigurations.add(
            EnvironmentConstants.OPENSANCTIONS_RETRY_ATTEMPTS + " (must be non-negative)");
      }
    } catch (NumberFormatException e) {
      missingConfigurations.add(
          EnvironmentConstants.OPENSANCTIONS_RETRY_ATTEMPTS + " (must be numeric)");
    }

    try {
      if (config.getRetryDelayMs() < 0) {
        missingConfigurations.add(
            EnvironmentConstants.OPENSANCTIONS_RETRY_DELAY_MS + " (must be non-negative)");
      }
    } catch (NumberFormatException e) {
      missingConfigurations.add(
          EnvironmentConstants.OPENSANCTIONS_RETRY_DELAY_MS + " (must be numeric)");
    }

    // Report validation errors
    if (!missingConfigurations.isEmpty()) {
      String errorMessage =
          "OpenSanctions API configuration validation failed. Missing or invalid: "
              + String.join(", ", missingConfigurations);
      LOGGER.severe(errorMessage);
      throw new IllegalStateException(errorMessage);
    }

    LOGGER.info("OpenSanctions API configuration validation passed");
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
