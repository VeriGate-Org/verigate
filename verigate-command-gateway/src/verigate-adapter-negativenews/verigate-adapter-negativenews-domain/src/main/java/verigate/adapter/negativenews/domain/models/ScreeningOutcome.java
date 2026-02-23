/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.domain.models;

/**
 * Enumeration of possible screening outcome values for negative news screening.
 */
public enum ScreeningOutcome {
  CLEAR,
  ADVERSE_FOUND,
  INCONCLUSIVE,
  ERROR;

  /**
   * Parse a screening outcome description to the corresponding enum value.
   *
   * @param description the description to parse
   * @return the corresponding ScreeningOutcome, defaults to ERROR if unknown
   */
  public static ScreeningOutcome fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return ERROR;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "CLEAN", "NO_RESULTS", "CLEAR" -> CLEAR;
        case "ADVERSE", "ADVERSE_FOUND", "FOUND" -> ADVERSE_FOUND;
        case "INCONCLUSIVE", "PENDING", "UNKNOWN" -> INCONCLUSIVE;
        default -> ERROR;
      };
    }
  }
}
