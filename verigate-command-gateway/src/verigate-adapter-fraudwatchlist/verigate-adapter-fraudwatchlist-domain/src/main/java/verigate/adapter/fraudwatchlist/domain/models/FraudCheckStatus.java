/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.domain.models;

/**
 * Enumeration of possible fraud check status values.
 */
public enum FraudCheckStatus {
  CLEAR,
  FLAGGED,
  CONFIRMED_FRAUD,
  SUSPECTED_FRAUD,
  NOT_FOUND,
  ERROR;

  /**
   * Parse a fraud check status description to the corresponding enum value.
   */
  public static FraudCheckStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return ERROR;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "CLEAR", "NO_MATCH", "CLEAN" -> CLEAR;
        case "FLAGGED", "ALERT", "WARNING" -> FLAGGED;
        case "CONFIRMED_FRAUD", "CONFIRMED", "FRAUD" -> CONFIRMED_FRAUD;
        case "SUSPECTED_FRAUD", "SUSPECTED", "SUSPECT" -> SUSPECTED_FRAUD;
        case "NOT_FOUND", "NO_RECORD", "UNKNOWN" -> NOT_FOUND;
        default -> ERROR;
      };
    }
  }
}
