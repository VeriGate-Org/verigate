/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.infrastructure.constants;

/**
 * Environment variable constants for Credit Bureau adapter configuration.
 */
public class EnvironmentConstants {

  // API Authentication
  public static final String CREDITBUREAU_API_KEY = "CREDITBUREAU_API_KEY";
  public static final String CREDITBUREAU_API_URL = "CREDITBUREAU_API_URL";

  // HTTP Configuration
  public static final String CREDITBUREAU_HTTP_TIMEOUT_SECONDS =
      "CREDITBUREAU_HTTP_TIMEOUT_SECONDS";
  public static final String CREDITBUREAU_HTTP_RETRY_ATTEMPTS = "CREDITBUREAU_HTTP_RETRY_ATTEMPTS";
  public static final String CREDITBUREAU_HTTP_RETRY_DELAY_MS = "CREDITBUREAU_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String CREDITBUREAU_RATE_LIMIT_RPS = "CREDITBUREAU_RATE_LIMIT_RPS";
  public static final String CREDITBUREAU_RATE_LIMIT_BURST = "CREDITBUREAU_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String CREDITBUREAU_ENABLE_REQUEST_LOGGING =
      "CREDITBUREAU_ENABLE_REQUEST_LOGGING";
  public static final String CREDITBUREAU_ENABLE_RESPONSE_LOGGING =
      "CREDITBUREAU_ENABLE_RESPONSE_LOGGING";
  public static final String CREDITBUREAU_LOG_LEVEL = "CREDITBUREAU_LOG_LEVEL";

  // Queue names
  public static final String PERFORM_CREDIT_CHECK_IMQ_NAME = "PERFORM_CREDIT_CHECK_IMQ_NAME";
  public static final String PERFORM_CREDIT_CHECK_DLQ_NAME = "PERFORM_CREDIT_CHECK_DLQ_NAME";

  // Default values (fallback URLs)
  public static final String DEFAULT_CREDITBUREAU_API_URL =
      "https://creditbureau-api-dev.verigate.co.za/api/v1";

  private EnvironmentConstants() {
  }
}
