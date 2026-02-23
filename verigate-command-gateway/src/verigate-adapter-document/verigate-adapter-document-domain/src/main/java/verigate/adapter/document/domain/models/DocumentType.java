/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.models;

/**
 * Enumeration of supported document types for verification.
 */
public enum DocumentType {
  IDENTITY_DOCUMENT,
  PASSPORT,
  DRIVERS_LICENSE,
  BANK_STATEMENT,
  PAYSLIP,
  UTILITY_BILL,
  TAX_CERTIFICATE,
  PROOF_OF_RESIDENCE;

  /**
   * Parse a document type description to the corresponding enum value.
   */
  public static DocumentType fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return IDENTITY_DOCUMENT;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "ID", "ID_DOCUMENT", "IDENTITY", "IDENTITY_DOCUMENT" -> IDENTITY_DOCUMENT;
        case "PASSPORT", "TRAVEL_DOCUMENT" -> PASSPORT;
        case "DRIVERS_LICENSE", "DRIVING_LICENSE", "DL" -> DRIVERS_LICENSE;
        case "BANK_STATEMENT", "STATEMENT" -> BANK_STATEMENT;
        case "PAYSLIP", "PAY_SLIP", "SALARY_SLIP" -> PAYSLIP;
        case "UTILITY_BILL", "UTILITIES" -> UTILITY_BILL;
        case "TAX_CERTIFICATE", "TAX_CERT" -> TAX_CERTIFICATE;
        case "PROOF_OF_RESIDENCE", "POR", "PROOF_OF_ADDRESS" -> PROOF_OF_RESIDENCE;
        default -> IDENTITY_DOCUMENT;
      };
    }
  }
}
