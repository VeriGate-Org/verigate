/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.constants;

/**
 * Constants for the SARS Tax Compliance Verification adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String SARS_PROVIDER = "SARS_PROVIDER";

  // API version and service identifiers
  public static final String SARS_API_VERSION = "v1";
  public static final String SARS_SERVICE = "SARS Tax Compliance Verification Service";

  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;

  // Tax compliance verification defaults
  public static final String DEFAULT_VERIFICATION_ID_PREFIX = "SARS-";

  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 5;
  public static final int DEFAULT_RATE_LIMIT_BURST = 10;

  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";

  // API endpoints
  public static final String ENDPOINT_VERIFY_TAX_COMPLIANCE = "/tax-compliance/verify";
  public static final String ENDPOINT_TAX_CLEARANCE_STATUS = "/tax-clearance/status";
  public static final String ENDPOINT_TAX_CLEARANCE_CERTIFICATE = "/tax-clearance/certificate";

  // Authentication
  public static final String AUTH_HEADER_NAME = "X-Api-Key";
  public static final String CONTENT_TYPE_JSON = "application/json";

  // Metadata keys for extracting fields from VerifyPartyCommand
  public static final String METADATA_KEY_ID_NUMBER = "idNumber";
  public static final String METADATA_KEY_ID_NUMBER_ALT = "id_number";
  public static final String METADATA_KEY_TAX_REFERENCE_NUMBER = "taxReferenceNumber";
  public static final String METADATA_KEY_TAX_REFERENCE_NUMBER_ALT = "tax_reference_number";
  public static final String METADATA_KEY_COMPANY_REGISTRATION_NUMBER =
      "companyRegistrationNumber";
  public static final String METADATA_KEY_COMPANY_REGISTRATION_NUMBER_ALT =
      "company_registration_number";
  public static final String METADATA_KEY_CLEARANCE_TYPE = "clearanceType";
  public static final String METADATA_KEY_CLEARANCE_TYPE_ALT = "clearance_type";

  // Result map keys
  public static final String RESULT_KEY_OUTCOME = "outcome";
  public static final String RESULT_KEY_DETAILS = "details";
  public static final String RESULT_KEY_TAX_COMPLIANCE_STATUS = "taxComplianceStatus";
  public static final String RESULT_KEY_CERTIFICATE_NUMBER = "certificateNumber";
  public static final String RESULT_KEY_CERTIFICATE_ISSUE_DATE = "certificateIssueDate";
  public static final String RESULT_KEY_CERTIFICATE_EXPIRY_DATE = "certificateExpiryDate";
  public static final String RESULT_KEY_CLEARANCE_TYPE = "clearanceType";
  public static final String RESULT_KEY_CERTIFICATE_VALID = "certificateValid";
  public static final String RESULT_KEY_REASON = "reason";

  // VAT Vendor Search - Metadata keys
  public static final String METADATA_KEY_VAT_NUMBER = "vatNumber";
  public static final String METADATA_KEY_VAT_NUMBER_ALT = "vat_number";
  public static final String METADATA_KEY_VENDOR_DESCRIPTION = "vendorDescription";
  public static final String METADATA_KEY_VENDOR_DESCRIPTION_ALT = "vendor_description";

  // VAT Vendor Search - Result map keys
  public static final String RESULT_KEY_VAT_VENDOR_STATUS = "vatVendorStatus";
  public static final String RESULT_KEY_VAT_NUMBER = "vatNumber";
  public static final String RESULT_KEY_VENDOR_NAME = "vendorName";
  public static final String RESULT_KEY_TRADING_NAME = "tradingName";
  public static final String RESULT_KEY_REGISTRATION_DATE = "registrationDate";
  public static final String RESULT_KEY_ACTIVITY_CODE = "activityCode";
  public static final String RESULT_KEY_PHYSICAL_ADDRESS = "physicalAddress";

  // VAT Vendor Search - SOAP constants
  public static final String SARS_VAT_SOAP_NAMESPACE = "http://www.sars.gov.za";
  public static final String SARS_VAT_SOAP_ACTION = "http://www.sars.gov.za/Search";
}
