/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.domain.models;

/**
 * Enumeration of possible employment status values.
 */
public enum EmploymentStatus {
  EMPLOYED,
  TERMINATED,
  NOT_FOUND,
  UNVERIFIABLE,
  ON_LEAVE,
  SUSPENDED;

  /**
   * Parse an employment status description to the corresponding enum value.
   */
  public static EmploymentStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return UNVERIFIABLE;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "ACTIVE", "CURRENTLY_EMPLOYED", "CURRENT" -> EMPLOYED;
        case "INACTIVE", "LEFT", "RESIGNED", "DISMISSED", "FIRED" -> TERMINATED;
        case "LEAVE", "MATERNITY_LEAVE", "SICK_LEAVE", "SABBATICAL" -> ON_LEAVE;
        case "SUSPENDED", "DISCIPLINARY" -> SUSPENDED;
        case "NOT_FOUND", "UNKNOWN_EMPLOYEE", "NO_RECORD" -> NOT_FOUND;
        default -> UNVERIFIABLE;
      };
    }
  }
}
