/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.enums;

/**
 * Enumeration of possible tax compliance status values returned by the SARS eFiling API.
 */
public enum TaxComplianceStatus {
  COMPLIANT,
  NON_COMPLIANT,
  TCC_EXPIRED,
  TCC_VALID,
  NOT_FOUND,
  ERROR;

  /**
   * Parse a tax compliance status description to the corresponding enum value.
   *
   * @param description the status description from the SARS API response
   * @return the corresponding TaxComplianceStatus enum value
   */
  public static TaxComplianceStatus fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return ERROR;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "GOOD_STANDING", "TAX_COMPLIANT", "IN_GOOD_STANDING", "VALID" -> COMPLIANT;
        case "NOT_IN_GOOD_STANDING", "TAX_NON_COMPLIANT", "OUTSTANDING_RETURNS",
            "OUTSTANDING_DEBT" -> NON_COMPLIANT;
        case "EXPIRED", "CERTIFICATE_EXPIRED", "LAPSED" -> TCC_EXPIRED;
        case "CERTIFICATE_VALID", "ACTIVE_CERTIFICATE", "TCC_ACTIVE" -> TCC_VALID;
        case "NOT_FOUND", "NO_RECORD", "UNKNOWN_TAXPAYER", "NOT_REGISTERED" -> NOT_FOUND;
        default -> ERROR;
      };
    }
  }
}
