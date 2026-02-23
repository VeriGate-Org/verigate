/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.domain.constants;

/**
 * Constants for the Income Verification adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String INCOME_PROVIDER = "INCOME_PROVIDER";

  // API version and service identifiers
  public static final String INCOME_API_VERSION = "v1";
  public static final String INCOME_SERVICE = "Income Verification Service";

  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;

  // Income verification defaults
  public static final String DEFAULT_VERIFICATION_ID_PREFIX = "INC-";

  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 10;
  public static final int DEFAULT_RATE_LIMIT_BURST = 20;

  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";

  // API endpoints
  public static final String ENDPOINT_VERIFY_INCOME = "/verify-income";
  public static final String ENDPOINT_INCOME_ASSESSMENT = "/income-assessment";
  public static final String ENDPOINT_BANK_STATEMENT_ANALYSIS = "/bank-statement-analysis";

  // Authentication
  public static final String AUTH_HEADER_NAME = "X-Api-Key";
  public static final String CONTENT_TYPE_JSON = "application/json";

  // Metadata keys for extracting fields from VerifyPartyCommand
  public static final String METADATA_KEY_ID_NUMBER = "idNumber";
  public static final String METADATA_KEY_ID_NUMBER_ALT = "id_number";
  public static final String METADATA_KEY_EMPLOYER_NAME = "employerName";
  public static final String METADATA_KEY_EMPLOYER_NAME_ALT = "employer_name";
  public static final String METADATA_KEY_DECLARED_MONTHLY_INCOME = "declaredMonthlyIncome";
  public static final String METADATA_KEY_DECLARED_MONTHLY_INCOME_ALT = "declared_monthly_income";
  public static final String METADATA_KEY_INCOME_SOURCE_TYPE = "incomeSourceType";
  public static final String METADATA_KEY_INCOME_SOURCE_TYPE_ALT = "income_source_type";
  public static final String METADATA_KEY_BANK_ACCOUNT_NUMBER = "bankAccountNumber";
  public static final String METADATA_KEY_BANK_ACCOUNT_NUMBER_ALT = "bank_account_number";

  // Result map keys
  public static final String RESULT_KEY_OUTCOME = "outcome";
  public static final String RESULT_KEY_DETAILS = "details";
  public static final String RESULT_KEY_VERIFICATION_STATUS = "verificationStatus";
  public static final String RESULT_KEY_VERIFIED_MONTHLY_INCOME = "verifiedMonthlyIncome";
  public static final String RESULT_KEY_DECLARED_MONTHLY_INCOME = "declaredMonthlyIncome";
  public static final String RESULT_KEY_VARIANCE = "variance";
  public static final String RESULT_KEY_CONFIDENCE_LEVEL = "confidenceLevel";
  public static final String RESULT_KEY_EVIDENCE_SOURCES = "evidenceSources";
  public static final String RESULT_KEY_AFFORDABILITY_CONFIRMED = "affordabilityConfirmed";
  public static final String RESULT_KEY_REASON = "reason";

  // Variance thresholds for income matching
  public static final String VARIANCE_THRESHOLD_PERCENTAGE = "10.0";

  private DomainConstants() {
  }
}
