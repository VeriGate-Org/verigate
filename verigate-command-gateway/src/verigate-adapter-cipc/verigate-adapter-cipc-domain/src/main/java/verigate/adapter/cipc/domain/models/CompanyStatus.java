/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

/**
 * Enumeration of possible company status values in CIPC.
 */
public enum CompanyStatus {
  ACTIVE,
  IN_BUSINESS,
  DEREGISTERED,
  UNDER_BUSINESS_RESCUE,
  IN_LIQUIDATION,
  FINAL_DEREGISTRATION,
  UNKNOWN;

  /**
   * Parse a CIPC status description to the corresponding enum value.
   */
  public static CompanyStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return UNKNOWN;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "IN_BUSINESS", "ACTIVE" -> ACTIVE;
        case "DEREGISTERED", "DEREGISTRATION" -> DEREGISTERED;
        case "BUSINESS_RESCUE", "UNDER_BUSINESS_RESCUE" -> UNDER_BUSINESS_RESCUE;
        case "LIQUIDATION", "IN_LIQUIDATION" -> IN_LIQUIDATION;
        case "FINAL_DEREGISTRATION" -> FINAL_DEREGISTRATION;
        default -> UNKNOWN;
      };
    }
  }
}
