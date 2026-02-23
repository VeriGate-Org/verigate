/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.domain.constants;

/**
 * Constants for the Credit Bureau adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String CREDIT_BUREAU_PROVIDER = "CREDIT_BUREAU_PROVIDER";

  // API version and service identifiers
  public static final String CREDIT_BUREAU_API_VERSION = "v1";
  public static final String CREDIT_BUREAU_SERVICE = "Credit Bureau";

  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;

  // Credit check defaults
  public static final String DEFAULT_VERIFICATION_ID_PREFIX = "CB-";

  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 5;
  public static final int DEFAULT_RATE_LIMIT_BURST = 10;

  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";

  // API endpoints
  public static final String ENDPOINT_CREDIT_CHECK = "/credit/check";

  // Credit score thresholds
  public static final int SCORE_THRESHOLD_EXCELLENT = 750;
  public static final int SCORE_THRESHOLD_GOOD = 700;
  public static final int SCORE_THRESHOLD_FAIR = 650;
  public static final int SCORE_THRESHOLD_POOR = 600;

  // Debt-to-income ratio limits
  public static final double MAX_DEBT_TO_INCOME_RATIO = 0.40;

  // Authentication
  public static final String AUTH_HEADER_API_KEY = "X-Api-Key";
}
