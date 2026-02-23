/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.models;

/**
 * Enumeration of possible document verification status values.
 */
public enum DocumentVerificationStatus {
  VERIFIED,
  MISMATCH,
  SUSPECTED_FRAUD,
  UNREADABLE,
  EXPIRED,
  NOT_FOUND,
  PENDING,
  ERROR;

  /**
   * Parse a status description to the corresponding enum value.
   */
  public static DocumentVerificationStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return ERROR;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "VALID", "MATCH", "CONFIRMED" -> VERIFIED;
        case "NO_MATCH", "DATA_MISMATCH", "MISMATCH" -> MISMATCH;
        case "FRAUD", "FRAUDULENT", "SUSPECTED_FRAUD" -> SUSPECTED_FRAUD;
        case "ILLEGIBLE", "CORRUPT", "UNREADABLE" -> UNREADABLE;
        case "DOCUMENT_EXPIRED", "EXPIRED" -> EXPIRED;
        case "MISSING", "NOT_FOUND" -> NOT_FOUND;
        case "IN_PROGRESS", "PROCESSING", "PENDING" -> PENDING;
        case "FAILURE", "FAILED", "ERROR" -> ERROR;
        default -> ERROR;
      };
    }
  }
}
