/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.constants;

/**
 * Environment variable constants for Negative News adapter configuration.
 */
public class EnvironmentConstants {

  // API Authentication
  public static final String NEGATIVENEWS_API_URL = "NEGATIVENEWS_API_URL";
  public static final String NEGATIVENEWS_API_KEY = "NEGATIVENEWS_API_KEY";

  // HTTP Configuration
  public static final String NEGATIVENEWS_HTTP_TIMEOUT_SECONDS =
      "NEGATIVENEWS_HTTP_TIMEOUT_SECONDS";
  public static final String NEGATIVENEWS_HTTP_RETRY_ATTEMPTS =
      "NEGATIVENEWS_HTTP_RETRY_ATTEMPTS";
  public static final String NEGATIVENEWS_HTTP_RETRY_DELAY_MS =
      "NEGATIVENEWS_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String NEGATIVENEWS_RATE_LIMIT_RPS = "NEGATIVENEWS_RATE_LIMIT_RPS";
  public static final String NEGATIVENEWS_RATE_LIMIT_BURST = "NEGATIVENEWS_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String NEGATIVENEWS_ENABLE_REQUEST_LOGGING =
      "NEGATIVENEWS_ENABLE_REQUEST_LOGGING";
  public static final String NEGATIVENEWS_ENABLE_RESPONSE_LOGGING =
      "NEGATIVENEWS_ENABLE_RESPONSE_LOGGING";
  public static final String NEGATIVENEWS_LOG_LEVEL = "NEGATIVENEWS_LOG_LEVEL";

  // Queue names
  public static final String SCREEN_NEGATIVE_NEWS_IMQ_NAME = "SCREEN_NEGATIVE_NEWS_IMQ_NAME";
  public static final String SCREEN_NEGATIVE_NEWS_DLQ_NAME = "SCREEN_NEGATIVE_NEWS_DLQ_NAME";

  // Default values (fallback URLs)
  public static final String DEFAULT_NEGATIVENEWS_API_URL =
      "https://negativenews-api.verigate.io/v1";

  private EnvironmentConstants() {
  }
}
