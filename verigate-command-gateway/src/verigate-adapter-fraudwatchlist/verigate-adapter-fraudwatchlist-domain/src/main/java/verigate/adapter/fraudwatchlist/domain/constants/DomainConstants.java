/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.domain.constants;

/**
 * Constants for the Fraud Watchlist adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String FRAUD_WATCHLIST_PROVIDER = "FRAUD_WATCHLIST_PROVIDER";

  // API version and service identifiers
  public static final String FRAUD_WATCHLIST_API_VERSION = "v1";
  public static final String FRAUD_WATCHLIST_SERVICE = "Fraud Watchlist Screening";

  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;

  // Fraud screening defaults
  public static final String DEFAULT_SCREENING_ID_PREFIX = "FWL-";

  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 5;
  public static final int DEFAULT_RATE_LIMIT_BURST = 10;

  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";

  // API endpoints
  public static final String ENDPOINT_FRAUD_CHECK = "/fraud/check";

  // Risk thresholds
  public static final double RISK_THRESHOLD_LOW = 0.3;
  public static final double RISK_THRESHOLD_MEDIUM = 0.6;
  public static final double RISK_THRESHOLD_HIGH = 0.8;
}
