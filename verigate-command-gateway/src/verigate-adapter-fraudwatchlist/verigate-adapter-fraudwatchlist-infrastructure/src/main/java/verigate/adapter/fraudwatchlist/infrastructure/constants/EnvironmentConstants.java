/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.infrastructure.constants;

/**
 * Environment variable constants for Fraud Watchlist adapter configuration.
 */
public class EnvironmentConstants {

  // API Authentication
  public static final String FRAUDWATCHLIST_API_KEY = "FRAUDWATCHLIST_API_KEY";
  public static final String FRAUDWATCHLIST_API_URL = "FRAUDWATCHLIST_API_URL";

  // HTTP Configuration
  public static final String FRAUDWATCHLIST_HTTP_TIMEOUT_SECONDS = "FRAUDWATCHLIST_HTTP_TIMEOUT_SECONDS";
  public static final String FRAUDWATCHLIST_HTTP_RETRY_ATTEMPTS = "FRAUDWATCHLIST_HTTP_RETRY_ATTEMPTS";
  public static final String FRAUDWATCHLIST_HTTP_RETRY_DELAY_MS = "FRAUDWATCHLIST_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String FRAUDWATCHLIST_RATE_LIMIT_RPS = "FRAUDWATCHLIST_RATE_LIMIT_RPS";
  public static final String FRAUDWATCHLIST_RATE_LIMIT_BURST = "FRAUDWATCHLIST_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String FRAUDWATCHLIST_ENABLE_REQUEST_LOGGING = "FRAUDWATCHLIST_ENABLE_REQUEST_LOGGING";
  public static final String FRAUDWATCHLIST_ENABLE_RESPONSE_LOGGING = "FRAUDWATCHLIST_ENABLE_RESPONSE_LOGGING";
  public static final String FRAUDWATCHLIST_LOG_LEVEL = "FRAUDWATCHLIST_LOG_LEVEL";

  // Queue Configuration
  public static final String SCREEN_FRAUD_WATCHLIST_IMQ_NAME = "SCREEN_FRAUD_WATCHLIST_IMQ_NAME";
  public static final String SCREEN_FRAUD_WATCHLIST_DLQ_NAME = "SCREEN_FRAUD_WATCHLIST_DLQ_NAME";

  // Default values (fallback URLs)
  public static final String DEFAULT_FRAUDWATCHLIST_API_URL = "https://fraudwatchlist-api-dev.verigate.co.za/v1";

  private EnvironmentConstants() {
  }
}
