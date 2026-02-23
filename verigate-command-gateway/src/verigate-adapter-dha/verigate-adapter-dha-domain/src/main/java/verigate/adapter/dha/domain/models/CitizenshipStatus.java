/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

/**
 * Enumeration of citizenship status values from the DHA.
 */
public enum CitizenshipStatus {
  CITIZEN,
  PERMANENT_RESIDENT,
  TEMPORARY_RESIDENT,
  UNKNOWN;

  /**
   * Parse a DHA citizenship status description to the corresponding enum value.
   */
  public static CitizenshipStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return UNKNOWN;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "CITIZEN", "SA_CITIZEN", "SOUTH_AFRICAN" -> CITIZEN;
        case "PERMANENT_RESIDENT", "PR" -> PERMANENT_RESIDENT;
        case "TEMPORARY_RESIDENT", "TR", "TEMP_RESIDENT" -> TEMPORARY_RESIDENT;
        default -> UNKNOWN;
      };
    }
  }
}
