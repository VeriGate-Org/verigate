/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

/**
 * Enumeration of possible identity verification status values from the DHA.
 */
public enum IdVerificationStatus {
  VERIFIED,
  NOT_FOUND,
  DECEASED,
  MISMATCH,
  EXPIRED_ID,
  BLOCKED;

  /**
   * Parse a DHA status description to the corresponding enum value.
   */
  public static IdVerificationStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return NOT_FOUND;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "VERIFIED", "MATCHED", "VALID" -> VERIFIED;
        case "NOT_FOUND", "NO_RECORD", "UNKNOWN" -> NOT_FOUND;
        case "DECEASED", "DEAD" -> DECEASED;
        case "MISMATCH", "NO_MATCH", "DETAILS_MISMATCH" -> MISMATCH;
        case "EXPIRED_ID", "EXPIRED", "ID_EXPIRED" -> EXPIRED_ID;
        case "BLOCKED", "SUSPENDED", "BARRED" -> BLOCKED;
        default -> NOT_FOUND;
      };
    }
  }
}
