/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.domain.constants;

/**
 * Constants for the SAQA adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String SAQA_PROVIDER = "SAQA_PROVIDER";

  // API version and service identifiers
  public static final String SAQA_API_VERSION = "v1";
  public static final String SAQA_SERVICE = "SAQA Qualification Verification";

  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;

  // Qualification verification defaults
  public static final String DEFAULT_VERIFICATION_ID_PREFIX = "SAQA-";

  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 5;
  public static final int DEFAULT_RATE_LIMIT_BURST = 10;

  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";

  // API endpoints
  public static final String ENDPOINT_QUALIFICATIONS_VERIFY = "/qualifications/verify";

  // Match confidence threshold
  public static final double MATCH_CONFIDENCE_THRESHOLD = 0.8;

  // Authentication
  public static final String AUTH_HEADER_NAME = "X-Api-Key";
}
