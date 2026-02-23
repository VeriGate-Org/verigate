/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.enums;

/**
 * Enumeration of tax clearance certificate types issued by SARS.
 */
public enum TaxClearanceType {
  GOOD_STANDING,
  TENDER,
  EMIGRATION,
  FOREIGN_INVESTMENT;

  /**
   * Parse a clearance type description to the corresponding enum value.
   *
   * @param description the clearance type description
   * @return the corresponding TaxClearanceType enum value
   */
  public static TaxClearanceType fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return GOOD_STANDING;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "GOOD_STANDING", "GS", "TAX_GOOD_STANDING" -> GOOD_STANDING;
        case "TENDER", "BID", "GOVERNMENT_TENDER", "PROCUREMENT" -> TENDER;
        case "EMIGRATION", "EMIGRATE", "TAX_EMIGRATION" -> EMIGRATION;
        case "FOREIGN_INVESTMENT", "FIA", "FOREIGN_INVEST",
            "INVESTMENT_ALLOWANCE" -> FOREIGN_INVESTMENT;
        default -> GOOD_STANDING;
      };
    }
  }
}
