/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.domain.models;

/**
 * Enumeration of possible employment type values.
 */
public enum EmploymentType {
  FULL_TIME,
  PART_TIME,
  CONTRACT,
  TEMPORARY,
  INTERN,
  UNKNOWN;

  /**
   * Parse an employment type description to the corresponding enum value.
   */
  public static EmploymentType fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return UNKNOWN;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "FULL_TIME", "FULLTIME", "PERMANENT", "FTE" -> FULL_TIME;
        case "PART_TIME", "PARTTIME", "PTE" -> PART_TIME;
        case "CONTRACT", "CONTRACTOR", "FIXED_TERM" -> CONTRACT;
        case "TEMPORARY", "TEMP", "CASUAL" -> TEMPORARY;
        case "INTERN", "INTERNSHIP", "LEARNERSHIP", "TRAINEE" -> INTERN;
        default -> UNKNOWN;
      };
    }
  }
}
