/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.domain.models;

/**
 * Enumeration of possible credit check status values.
 */
public enum CreditCheckStatus {
  PASSED,
  FAILED,
  REVIEW_REQUIRED,
  INSUFFICIENT_DATA,
  BUREAU_UNAVAILABLE,
  ERROR;

  /**
   * Parse a credit check status description to the corresponding enum value.
   */
  public static CreditCheckStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return ERROR;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "PASS", "APPROVED", "PASSED" -> PASSED;
        case "FAIL", "DECLINED", "REJECTED", "FAILED" -> FAILED;
        case "REVIEW", "PENDING_REVIEW", "MANUAL_REVIEW", "REVIEW_REQUIRED" -> REVIEW_REQUIRED;
        case "NO_DATA", "INSUFFICIENT", "INSUFFICIENT_DATA" -> INSUFFICIENT_DATA;
        case "UNAVAILABLE", "SERVICE_DOWN", "BUREAU_UNAVAILABLE" -> BUREAU_UNAVAILABLE;
        default -> ERROR;
      };
    }
  }
}
