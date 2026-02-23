/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.constants;

import java.util.Set;

/**
 * Constants for the Document Verification adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String DOCUMENT_VERIFICATION_PROVIDER = "DOCUMENT_VERIFICATION_PROVIDER";

  // API version and service identifiers
  public static final String DOCUMENT_API_VERSION = "v1";
  public static final String DOCUMENT_SERVICE = "Document Verification";

  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;

  // Document verification defaults
  public static final String DEFAULT_VERIFICATION_ID_PREFIX = "DOC-";
  public static final double DEFAULT_CONFIDENCE_THRESHOLD = 0.85;

  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 5;
  public static final int DEFAULT_RATE_LIMIT_BURST = 10;

  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";

  // Metadata keys
  public static final String METADATA_DOCUMENT_REFERENCE = "documentReference";
  public static final String METADATA_DOCUMENT_REFERENCE_ALT = "document_reference";
  public static final String METADATA_DOCUMENT_TYPE = "documentType";
  public static final String METADATA_DOCUMENT_TYPE_ALT = "document_type";
  public static final String METADATA_SUBJECT_ID_NUMBER = "subjectIdNumber";
  public static final String METADATA_SUBJECT_ID_NUMBER_ALT = "subject_id_number";
  public static final String METADATA_SUBJECT_NAME = "subjectName";
  public static final String METADATA_SUBJECT_NAME_ALT = "subject_name";
  public static final String METADATA_S3_BUCKET_NAME = "s3BucketName";
  public static final String METADATA_S3_BUCKET_NAME_ALT = "s3_bucket_name";
  public static final String METADATA_S3_OBJECT_KEY = "s3ObjectKey";
  public static final String METADATA_S3_OBJECT_KEY_ALT = "s3_object_key";

  // Supported document types
  public static final Set<String> SUPPORTED_DOCUMENT_TYPES = Set.of(
      "IDENTITY_DOCUMENT",
      "PASSPORT",
      "DRIVERS_LICENSE",
      "BANK_STATEMENT",
      "PAYSLIP",
      "UTILITY_BILL",
      "TAX_CERTIFICATE",
      "PROOF_OF_RESIDENCE"
  );

  // API endpoints
  public static final String ENDPOINT_VERIFY_DOCUMENT = "/verify";
  public static final String ENDPOINT_DOCUMENT_STATUS = "/status";

  // Result keys
  public static final String RESULT_OUTCOME = "outcome";
  public static final String RESULT_STATUS = "status";
  public static final String RESULT_DOCUMENT_TYPE = "documentType";
  public static final String RESULT_CONFIDENCE_SCORE = "confidenceScore";
  public static final String RESULT_MATCH_DETAILS = "matchDetails";
  public static final String RESULT_EXTRACTED_FIELDS = "extractedFields";

  private DomainConstants() {
  }
}
