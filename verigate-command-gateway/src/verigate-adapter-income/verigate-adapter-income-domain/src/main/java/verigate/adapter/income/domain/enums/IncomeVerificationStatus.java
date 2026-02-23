/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.domain.enums;

/**
 * Enumeration of possible income verification status values.
 */
public enum IncomeVerificationStatus {
  VERIFIED,
  INSUFFICIENT_EVIDENCE,
  MISMATCH,
  UNVERIFIABLE,
  PENDING,
  ERROR;

  /**
   * Parse an income verification status description to the corresponding enum value.
   *
   * @param description the status description string
   * @return the corresponding enum value
   */
  public static IncomeVerificationStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return UNVERIFIABLE;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "CONFIRMED", "MATCHED", "VALID", "APPROVED" -> VERIFIED;
        case "INSUFFICIENT", "INCOMPLETE", "PARTIAL", "MISSING_DATA" -> INSUFFICIENT_EVIDENCE;
        case "MISMATCH", "DISCREPANCY", "DOES_NOT_MATCH", "INCONSISTENT" -> MISMATCH;
        case "UNVERIFIABLE", "UNABLE_TO_VERIFY", "NOT_AVAILABLE" -> UNVERIFIABLE;
        case "PENDING", "IN_PROGRESS", "AWAITING_REVIEW" -> PENDING;
        case "ERROR", "FAILED", "SYSTEM_ERROR", "TIMEOUT" -> ERROR;
        default -> UNVERIFIABLE;
      };
    }
  }
}
