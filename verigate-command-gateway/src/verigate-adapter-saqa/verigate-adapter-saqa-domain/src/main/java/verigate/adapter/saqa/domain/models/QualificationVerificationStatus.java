/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.domain.models;

/**
 * Enumeration of possible qualification verification status values from SAQA.
 */
public enum QualificationVerificationStatus {
  VERIFIED,
  NOT_FOUND,
  REVOKED,
  EXPIRED,
  MISMATCH,
  PENDING_VERIFICATION,
  ERROR;

  /**
   * Parse a SAQA status description to the corresponding enum value.
   */
  public static QualificationVerificationStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return ERROR;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "VERIFIED", "VALID", "CONFIRMED" -> VERIFIED;
        case "NOT_FOUND", "UNKNOWN", "NO_RECORD" -> NOT_FOUND;
        case "REVOKED", "CANCELLED", "WITHDRAWN" -> REVOKED;
        case "EXPIRED", "LAPSED" -> EXPIRED;
        case "MISMATCH", "NO_MATCH", "PARTIAL_MATCH" -> MISMATCH;
        case "PENDING_VERIFICATION", "PENDING", "IN_PROGRESS" -> PENDING_VERIFICATION;
        default -> ERROR;
      };
    }
  }
}
