/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.infrastructure.constants;

/**
 * Environment variable constants for SAQA adapter configuration.
 */
public class EnvironmentConstants {

  // API Authentication
  public static final String SAQA_API_KEY = "SAQA_API_KEY";
  public static final String SAQA_API_URL = "SAQA_API_URL";

  // HTTP Configuration
  public static final String SAQA_HTTP_TIMEOUT_SECONDS = "SAQA_HTTP_TIMEOUT_SECONDS";
  public static final String SAQA_HTTP_RETRY_ATTEMPTS = "SAQA_HTTP_RETRY_ATTEMPTS";
  public static final String SAQA_HTTP_RETRY_DELAY_MS = "SAQA_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String SAQA_RATE_LIMIT_RPS = "SAQA_RATE_LIMIT_RPS";
  public static final String SAQA_RATE_LIMIT_BURST = "SAQA_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String SAQA_ENABLE_REQUEST_LOGGING = "SAQA_ENABLE_REQUEST_LOGGING";
  public static final String SAQA_ENABLE_RESPONSE_LOGGING = "SAQA_ENABLE_RESPONSE_LOGGING";
  public static final String SAQA_LOG_LEVEL = "SAQA_LOG_LEVEL";

  // Queue Configuration
  public static final String VERIFY_QUALIFICATION_IMQ_NAME = "VERIFY_QUALIFICATION_IMQ_NAME";
  public static final String VERIFY_QUALIFICATION_DLQ_NAME = "VERIFY_QUALIFICATION_DLQ_NAME";

  // Default values (fallback URLs)
  public static final String DEFAULT_SAQA_API_URL =
      "https://api.saqa.org.za/qualifications/v1";

  private EnvironmentConstants() {
  }
}
