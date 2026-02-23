/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

/**
 * Enumeration of vital status values from the DHA.
 */
public enum VitalStatus {
  ALIVE,
  DECEASED,
  UNKNOWN;

  /**
   * Parse a DHA vital status description to the corresponding enum value.
   */
  public static VitalStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return UNKNOWN;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "ALIVE", "LIVING", "ACTIVE" -> ALIVE;
        case "DECEASED", "DEAD" -> DECEASED;
        default -> UNKNOWN;
      };
    }
  }
}
