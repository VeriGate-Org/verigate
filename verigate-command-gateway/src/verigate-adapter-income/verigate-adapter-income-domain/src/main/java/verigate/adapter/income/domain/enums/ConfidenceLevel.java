/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.domain.enums;

/**
 * Enumeration of confidence levels for income verification assessments.
 */
public enum ConfidenceLevel {
  HIGH,
  MEDIUM,
  LOW,
  NONE;

  /**
   * Parse a confidence level description to the corresponding enum value.
   *
   * @param description the confidence level description string
   * @return the corresponding enum value
   */
  public static ConfidenceLevel fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return NONE;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "HIGH", "STRONG", "DEFINITIVE", "CERTAIN" -> HIGH;
        case "MEDIUM", "MODERATE", "FAIR", "REASONABLE" -> MEDIUM;
        case "LOW", "WEAK", "UNCERTAIN", "MARGINAL" -> LOW;
        case "NONE", "UNKNOWN", "NOT_AVAILABLE", "NA" -> NONE;
        default -> NONE;
      };
    }
  }
}
