/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.infrastructure.constants;

/**
 * Environment variable constants for Employment Verification adapter configuration.
 */
public class EnvironmentConstants {

  // API Authentication
  public static final String EMPLOYMENT_API_KEY = "EMPLOYMENT_API_KEY";
  public static final String EMPLOYMENT_API_URL = "EMPLOYMENT_API_URL";

  // HTTP Configuration
  public static final String EMPLOYMENT_HTTP_TIMEOUT_SECONDS = "EMPLOYMENT_HTTP_TIMEOUT_SECONDS";
  public static final String EMPLOYMENT_HTTP_RETRY_ATTEMPTS = "EMPLOYMENT_HTTP_RETRY_ATTEMPTS";
  public static final String EMPLOYMENT_HTTP_RETRY_DELAY_MS = "EMPLOYMENT_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String EMPLOYMENT_RATE_LIMIT_RPS = "EMPLOYMENT_RATE_LIMIT_RPS";
  public static final String EMPLOYMENT_RATE_LIMIT_BURST = "EMPLOYMENT_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String EMPLOYMENT_ENABLE_REQUEST_LOGGING = "EMPLOYMENT_ENABLE_REQUEST_LOGGING";
  public static final String EMPLOYMENT_ENABLE_RESPONSE_LOGGING = "EMPLOYMENT_ENABLE_RESPONSE_LOGGING";
  public static final String EMPLOYMENT_LOG_LEVEL = "EMPLOYMENT_LOG_LEVEL";

  // Queue Configuration
  public static final String VERIFY_EMPLOYMENT_IMQ_NAME = "VERIFY_EMPLOYMENT_IMQ_NAME";
  public static final String VERIFY_EMPLOYMENT_DLQ_NAME = "VERIFY_EMPLOYMENT_DLQ_NAME";

  // Default values (fallback URLs)
  public static final String DEFAULT_EMPLOYMENT_API_URL =
      "https://employment-api-dev.verigate.co.za/api/v1";

  private EnvironmentConstants() {
  }
}
