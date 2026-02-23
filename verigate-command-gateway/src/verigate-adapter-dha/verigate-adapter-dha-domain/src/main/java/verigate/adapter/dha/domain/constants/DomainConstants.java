/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.constants;

/**
 * Constants for the DHA adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String DHA = "DHA";

  // API version and service identifiers
  public static final String DHA_API_VERSION = "v1";
  public static final String DHA_SERVICE = "DHA Identity Verification";

  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;

  // Identity verification defaults
  public static final String DEFAULT_VERIFICATION_ID_PREFIX = "DHA-";

  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 5;
  public static final int DEFAULT_RATE_LIMIT_BURST = 10;

  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";

  // South African ID number validation (13 digits: YYMMDDSSSSCZZ)
  public static final String SA_ID_NUMBER_PATTERN = "\\d{13}";
  public static final String SA_ID_NUMBER_EXAMPLE = "9001015009087";

  // API endpoints
  public static final String ENDPOINT_VERIFY_IDENTITY = "/identity/verify";
  public static final String ENDPOINT_IDENTITY_STATUS = "/identity/status";

  // Authentication
  public static final String AUTH_HEADER_NAME = "X-Api-Key";
  public static final String AUTH_QUERY_PARAM = "api-key";
}
