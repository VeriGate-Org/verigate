/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.constants;

/**
 * Environment variable constants for Document Verification adapter configuration.
 */
public class EnvironmentConstants {

  // API Configuration
  public static final String DOCUMENT_API_URL = "DOCUMENT_API_URL";
  public static final String DOCUMENT_S3_BUCKET = "DOCUMENT_S3_BUCKET";

  // HTTP Configuration
  public static final String DOCUMENT_HTTP_TIMEOUT_SECONDS = "DOCUMENT_HTTP_TIMEOUT_SECONDS";
  public static final String DOCUMENT_HTTP_RETRY_ATTEMPTS = "DOCUMENT_HTTP_RETRY_ATTEMPTS";
  public static final String DOCUMENT_HTTP_RETRY_DELAY_MS = "DOCUMENT_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String DOCUMENT_RATE_LIMIT_RPS = "DOCUMENT_RATE_LIMIT_RPS";
  public static final String DOCUMENT_RATE_LIMIT_BURST = "DOCUMENT_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String DOCUMENT_ENABLE_REQUEST_LOGGING = "DOCUMENT_ENABLE_REQUEST_LOGGING";
  public static final String DOCUMENT_ENABLE_RESPONSE_LOGGING = "DOCUMENT_ENABLE_RESPONSE_LOGGING";
  public static final String DOCUMENT_LOG_LEVEL = "DOCUMENT_LOG_LEVEL";

  // Queue names
  public static final String VERIFY_DOCUMENT_IMQ_NAME = "VERIFY_DOCUMENT_IMQ_NAME";
  public static final String VERIFY_DOCUMENT_DLQ_NAME = "VERIFY_DOCUMENT_DLQ_NAME";

  // Default values (fallback URLs)
  public static final String DEFAULT_DOCUMENT_API_URL =
      "https://document-verification-api-dev.verigate.co.za/api/v1";

  private EnvironmentConstants() {
  }
}
