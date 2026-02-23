/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.domain.constants;

/**
 * Constants for the QLink adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String QLINK = "QLINK";

  // API version and service identifiers
  public static final String QLINK_API_VERSION = "v1";
  public static final String QLINK_SERVICE = "QLink Bank Verification";

  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;

  // Bank verification defaults
  public static final String DEFAULT_VERIFICATION_ID_PREFIX = "QLINK-";

  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 5;
  public static final int DEFAULT_RATE_LIMIT_BURST = 10;

  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";

  // API endpoints
  public static final String ENDPOINT_VERIFY_BANK_ACCOUNT = "/verify-bank-account";

  // Metadata keys for extracting bank details from VerifyPartyCommand
  public static final String METADATA_ACCOUNT_NUMBER = "accountNumber";
  public static final String METADATA_BRANCH_CODE = "branchCode";
  public static final String METADATA_ACCOUNT_HOLDER_NAME = "accountHolderName";
  public static final String METADATA_ID_NUMBER = "idNumber";

  // Authentication
  public static final String AUTH_HEADER_CLIENT_ID = "X-Client-Id";
  public static final String AUTH_HEADER_AUTHORIZATION = "Authorization";
}
