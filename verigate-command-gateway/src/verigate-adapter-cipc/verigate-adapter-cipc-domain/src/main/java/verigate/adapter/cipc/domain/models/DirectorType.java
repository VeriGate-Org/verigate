/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

/**
 * Enumeration of possible director types in CIPC.
 */
public enum DirectorType {
  EXECUTIVE_DIRECTOR,
  NON_EXECUTIVE_DIRECTOR,
  MANAGING_DIRECTOR,
  ALTERNATE_DIRECTOR,
  INDEPENDENT_DIRECTOR,
  CHAIRPERSON,
  MEMBER,
  UNKNOWN;

  /**
   * Parse a CIPC director type description to the corresponding enum value.
   */
  public static DirectorType fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return UNKNOWN;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "EXECUTIVE", "EXECUTIVE_DIRECTOR" -> EXECUTIVE_DIRECTOR;
        case "NON_EXECUTIVE", "NON_EXECUTIVE_DIRECTOR" -> NON_EXECUTIVE_DIRECTOR;
        case "MANAGING", "MANAGING_DIRECTOR" -> MANAGING_DIRECTOR;
        case "ALTERNATE", "ALTERNATE_DIRECTOR" -> ALTERNATE_DIRECTOR;
        case "INDEPENDENT", "INDEPENDENT_DIRECTOR" -> INDEPENDENT_DIRECTOR;
        case "CHAIRPERSON", "CHAIRMAN", "CHAIR" -> CHAIRPERSON;
        case "MEMBER" -> MEMBER;
        default -> UNKNOWN;
      };
    }
  }
}
