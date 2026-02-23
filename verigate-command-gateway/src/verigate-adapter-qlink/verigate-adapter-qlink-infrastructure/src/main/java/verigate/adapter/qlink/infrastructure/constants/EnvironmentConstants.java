/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.infrastructure.constants;

/**
 * Environment variable constants for QLink adapter configuration.
 */
public class EnvironmentConstants {

  // API Authentication (secrets provisioned via Terraform)
  public static final String QLINK_CLIENT_ID = "QLINK_CLIENT_ID";
  public static final String QLINK_USERNAME = "QLINK_USERNAME";
  public static final String QLINK_PASSWORD = "QLINK_PASSWORD";
  public static final String QLINK_API_URL = "QLINK_API_URL";

  // HTTP Configuration
  public static final String QLINK_HTTP_TIMEOUT_SECONDS = "QLINK_HTTP_TIMEOUT_SECONDS";
  public static final String QLINK_HTTP_RETRY_ATTEMPTS = "QLINK_HTTP_RETRY_ATTEMPTS";
  public static final String QLINK_HTTP_RETRY_DELAY_MS = "QLINK_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String QLINK_RATE_LIMIT_RPS = "QLINK_RATE_LIMIT_RPS";
  public static final String QLINK_RATE_LIMIT_BURST = "QLINK_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String QLINK_ENABLE_REQUEST_LOGGING = "QLINK_ENABLE_REQUEST_LOGGING";
  public static final String QLINK_ENABLE_RESPONSE_LOGGING = "QLINK_ENABLE_RESPONSE_LOGGING";
  public static final String QLINK_LOG_LEVEL = "QLINK_LOG_LEVEL";

  // Default values (fallback URLs)
  public static final String DEFAULT_QLINK_API_URL =
      "https://api.qlink.co.za/bank-verification/v1";

  private EnvironmentConstants() {
  }
}
