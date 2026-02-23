/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

/**
 * Enumeration of possible director status values in CIPC.
 */
public enum DirectorStatus {
  ACTIVE,
  RESIGNED,
  REMOVED,
  DISQUALIFIED,
  DECEASED,
  UNKNOWN;

  /**
   * Parse a CIPC director status description to the corresponding enum value.
   */
  public static DirectorStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return UNKNOWN;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "ACTIVE", "CURRENT" -> ACTIVE;
        case "RESIGNED", "RESIGNATION" -> RESIGNED;
        case "REMOVED", "REMOVAL" -> REMOVED;
        case "DISQUALIFIED", "DISQUALIFICATION" -> DISQUALIFIED;
        case "DECEASED", "DEATH" -> DECEASED;
        default -> UNKNOWN;
      };
    }
  }
}
