/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.domain.constants;

/**
 * Constants for the Employment Verification adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String EMPLOYMENT_PROVIDER = "EMPLOYMENT_PROVIDER";

  // API version and service identifiers
  public static final String EMPLOYMENT_API_VERSION = "v1";
  public static final String EMPLOYMENT_SERVICE = "Employment Verification Service";

  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;

  // Employment verification defaults
  public static final String DEFAULT_VERIFICATION_ID_PREFIX = "EMP-";

  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 10;
  public static final int DEFAULT_RATE_LIMIT_BURST = 20;

  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";

  // API endpoints
  public static final String ENDPOINT_VERIFY_EMPLOYMENT = "/verify-employment";
  public static final String ENDPOINT_EMPLOYMENT_STATUS = "/employment-status";
  public static final String ENDPOINT_EMPLOYMENT_HISTORY = "/employment-history";

  // Authentication
  public static final String AUTH_HEADER_NAME = "X-Api-Key";
  public static final String CONTENT_TYPE_JSON = "application/json";

  // Metadata keys for extracting fields from VerifyPartyCommand
  public static final String METADATA_KEY_ID_NUMBER = "idNumber";
  public static final String METADATA_KEY_ID_NUMBER_ALT = "id_number";
  public static final String METADATA_KEY_EMPLOYER_NAME = "employerName";
  public static final String METADATA_KEY_EMPLOYER_NAME_ALT = "employer_name";
  public static final String METADATA_KEY_EMPLOYEE_NUMBER = "employeeNumber";
  public static final String METADATA_KEY_EMPLOYEE_NUMBER_ALT = "employee_number";
  public static final String METADATA_KEY_START_DATE = "startDate";
  public static final String METADATA_KEY_START_DATE_ALT = "start_date";

  // Result map keys
  public static final String RESULT_KEY_OUTCOME = "outcome";
  public static final String RESULT_KEY_DETAILS = "details";
  public static final String RESULT_KEY_EMPLOYMENT_STATUS = "employmentStatus";
  public static final String RESULT_KEY_EMPLOYER_NAME = "employerName";
  public static final String RESULT_KEY_JOB_TITLE = "jobTitle";
  public static final String RESULT_KEY_EMPLOYMENT_TYPE = "employmentType";
  public static final String RESULT_KEY_START_DATE = "startDate";
  public static final String RESULT_KEY_END_DATE = "endDate";
  public static final String RESULT_KEY_DEPARTMENT = "department";
}
