/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.config;

/**
 * Environment variable constants for SARS Tax Compliance adapter configuration.
 */
public class EnvironmentConstants {

  // API Authentication
  public static final String SARS_API_KEY = "SARS_API_KEY";
  public static final String SARS_API_URL = "SARS_API_URL";

  // HTTP Configuration
  public static final String SARS_HTTP_TIMEOUT_SECONDS = "SARS_HTTP_TIMEOUT_SECONDS";
  public static final String SARS_HTTP_RETRY_ATTEMPTS = "SARS_HTTP_RETRY_ATTEMPTS";
  public static final String SARS_HTTP_RETRY_DELAY_MS = "SARS_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String SARS_RATE_LIMIT_RPS = "SARS_RATE_LIMIT_RPS";
  public static final String SARS_RATE_LIMIT_BURST = "SARS_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String SARS_ENABLE_REQUEST_LOGGING = "SARS_ENABLE_REQUEST_LOGGING";
  public static final String SARS_ENABLE_RESPONSE_LOGGING = "SARS_ENABLE_RESPONSE_LOGGING";
  public static final String SARS_LOG_LEVEL = "SARS_LOG_LEVEL";

  // Queue Configuration
  public static final String VERIFY_TAX_COMPLIANCE_IMQ_NAME = "VERIFY_TAX_COMPLIANCE_IMQ_NAME";
  public static final String VERIFY_TAX_COMPLIANCE_DLQ_NAME = "VERIFY_TAX_COMPLIANCE_DLQ_NAME";

  // Default values (fallback URLs)
  public static final String DEFAULT_SARS_API_URL =
      "https://sars-efiling-api-dev.verigate.co.za/api/v1";

  // VAT Vendor Search - SOAP endpoint
  public static final String SARS_VAT_ENDPOINT_URL = "SARS_VAT_ENDPOINT_URL";
  public static final String SARS_EFILING_SECRET_NAME = "SARS_EFILING_SECRET_NAME";
  public static final String VERIFY_VAT_VENDOR_IMQ_NAME = "VERIFY_VAT_VENDOR_IMQ_NAME";
  public static final String VERIFY_VAT_VENDOR_DLQ_NAME = "VERIFY_VAT_VENDOR_DLQ_NAME";
  public static final String DEFAULT_SARS_VAT_ENDPOINT_URL =
      "https://secure.sarsefiling.co.za/VATVendorSearch/application/VendorService.asmx";

  private EnvironmentConstants() {
  }
}
