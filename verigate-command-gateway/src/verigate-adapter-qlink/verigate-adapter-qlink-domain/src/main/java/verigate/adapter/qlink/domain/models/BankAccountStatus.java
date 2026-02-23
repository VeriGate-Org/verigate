/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.domain.models;

/**
 * Enumeration of possible bank account status values from QLink verification.
 */
public enum BankAccountStatus {
  VALID,
  INVALID,
  DORMANT,
  CLOSED,
  NOT_FOUND;

  /**
   * Parse a QLink status description to the corresponding enum value.
   */
  public static BankAccountStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return NOT_FOUND;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "ACTIVE", "OPEN", "VALID" -> VALID;
        case "INACTIVE", "INVALID" -> INVALID;
        case "DORMANT", "SUSPENDED" -> DORMANT;
        case "CLOSED", "TERMINATED" -> CLOSED;
        case "NOT_FOUND", "UNKNOWN" -> NOT_FOUND;
        default -> NOT_FOUND;
      };
    }
  }
}
