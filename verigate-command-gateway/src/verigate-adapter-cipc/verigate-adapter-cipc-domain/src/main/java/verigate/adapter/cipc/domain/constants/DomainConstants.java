/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.constants;

/**
 * Constants for the CIPC adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String CIPC = "CIPC";
  
  // API version and service identifiers
  public static final String CIPC_API_VERSION = "v1";
  public static final String CIPC_SERVICE = "CIPC Public Data";
  
  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;
  
  // Company verification defaults
  public static final String DEFAULT_VERIFICATION_ID_PREFIX = "CIPC-";
  
  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 5;
  public static final int DEFAULT_RATE_LIMIT_BURST = 10;
  
  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";
  
  // Enterprise number validation
  public static final String ENTERPRISE_NUMBER_PATTERN = "\\d{4}/\\d{6}/\\d{2}";
  public static final String ENTERPRISE_NUMBER_EXAMPLE = "2020/939681/07";
  
  // API endpoints
  public static final String ENDPOINT_COMPANY_PROFILE = "/companyprofile";
  public static final String ENDPOINT_COMPANY_INFORMATION = "/information";
  public static final String ENDPOINT_DIRECTORS = "/directors";
  public static final String ENDPOINT_APPOINTMENTS = "/appointments";
  public static final String ENDPOINT_EMPLOYEE_VERIFICATION = "/employee-verification";
  public static final String ENDPOINT_REGISTERED_OFFICE_ADDRESS = "/registered-office-address";
  public static final String ENDPOINT_FILING_HISTORY = "/filing-history";
  
  // Company status values
  public static final String STATUS_ACTIVE = "ACTIVE";
  public static final String STATUS_IN_BUSINESS = "IN_BUSINESS";
  public static final String STATUS_DEREGISTERED = "DEREGISTERED";
  public static final String STATUS_UNDER_BUSINESS_RESCUE = "UNDER_BUSINESS_RESCUE";
  public static final String STATUS_IN_LIQUIDATION = "IN_LIQUIDATION";
  public static final String STATUS_FINAL_DEREGISTRATION = "FINAL_DEREGISTRATION";
  
  // Director status values
  public static final String DIRECTOR_STATUS_ACTIVE = "ACTIVE";
  public static final String DIRECTOR_STATUS_RESIGNED = "RESIGNED";
  public static final String DIRECTOR_STATUS_REMOVED = "REMOVED";
  public static final String DIRECTOR_STATUS_DISQUALIFIED = "DISQUALIFIED";
  public static final String DIRECTOR_STATUS_DECEASED = "DECEASED";
  
  // Authentication
  public static final String AUTH_HEADER_NAME = "Ocp-Apim-Subscription-Key";
  public static final String AUTH_QUERY_PARAM = "subscription-key";
}