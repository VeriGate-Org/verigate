/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates CIPC adapter configuration on startup.
 */
public class ConfigurationValidator {

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationValidator.class);

  /**
   * Validates the CIPC configuration and logs configuration summary.
   *
   * @param config the configuration to validate
   * @throws IllegalStateException if configuration is invalid
   */
  public static void validate(CipcApiConfiguration config) {
    logger.info("Validating CIPC adapter configuration...");

    try {
      // Validate required configuration
      config.validate();

      // Log configuration summary (with sensitive data masked)
      logConfigurationSummary(config);

      logger.info("CIPC adapter configuration validation completed successfully");
    } catch (IllegalStateException e) {
      logger.error(
          "CIPC adapter configuration validation failed: {}",
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during CIPC configuration validation: {}",
          e.getMessage(),
          e);
      throw new IllegalStateException("Configuration validation failed", e);
    }
  }

  /**
   * Logs a summary of the configuration (with sensitive data masked).
   */
  private static void logConfigurationSummary(CipcApiConfiguration config) {
    logger.info("CIPC Adapter Configuration Summary:");
    logger.info("  API Key: {}", config.getMaskedApiKey());
    logger.info("  Base URL: {}", config.getMaskedBaseUrl());
    logger.info("  HTTP Timeout: {} seconds", config.getHttpTimeoutSeconds());
    logger.info("  HTTP Retry Attempts: {}", config.getHttpRetryAttempts());
    logger.info("  HTTP Retry Delay: {} ms", config.getHttpRetryDelayMs());
    logger.info("  Rate Limit RPS: {}", config.getRateLimitRps());
    logger.info("  Rate Limit Burst: {}", config.getRateLimitBurst());
    logger.info(
        "  Request Logging: {}",
        config.isRequestLoggingEnabled() ? "enabled" : "disabled");
    logger.info(
        "  Response Logging: {}",
        config.isResponseLoggingEnabled() ? "enabled" : "disabled");
    logger.info("  Log Level: {}", config.getLogLevel());
  }
}
