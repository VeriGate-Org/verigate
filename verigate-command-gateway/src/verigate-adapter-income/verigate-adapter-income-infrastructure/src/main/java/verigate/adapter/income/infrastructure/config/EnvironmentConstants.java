/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.infrastructure.config;

/**
 * Environment variable constants for Income Verification adapter configuration.
 */
public class EnvironmentConstants {

  // API Authentication
  public static final String INCOME_API_KEY = "INCOME_API_KEY";
  public static final String INCOME_API_URL = "INCOME_API_URL";

  // HTTP Configuration
  public static final String INCOME_HTTP_TIMEOUT_SECONDS = "INCOME_HTTP_TIMEOUT_SECONDS";
  public static final String INCOME_HTTP_RETRY_ATTEMPTS = "INCOME_HTTP_RETRY_ATTEMPTS";
  public static final String INCOME_HTTP_RETRY_DELAY_MS = "INCOME_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String INCOME_RATE_LIMIT_RPS = "INCOME_RATE_LIMIT_RPS";
  public static final String INCOME_RATE_LIMIT_BURST = "INCOME_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String INCOME_ENABLE_REQUEST_LOGGING = "INCOME_ENABLE_REQUEST_LOGGING";
  public static final String INCOME_ENABLE_RESPONSE_LOGGING = "INCOME_ENABLE_RESPONSE_LOGGING";
  public static final String INCOME_LOG_LEVEL = "INCOME_LOG_LEVEL";

  // Queue Configuration
  public static final String VERIFY_INCOME_IMQ_NAME = "VERIFY_INCOME_IMQ_NAME";
  public static final String VERIFY_INCOME_DLQ_NAME = "VERIFY_INCOME_DLQ_NAME";

  // Default values (fallback URLs)
  public static final String DEFAULT_INCOME_API_URL =
      "https://income-api-dev.verigate.co.za/api/v1";

  private EnvironmentConstants() {
  }
}
