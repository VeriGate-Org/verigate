/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.domain.models;

/**
 * Enumeration of bank account types returned by QLink verification.
 */
public enum BankAccountType {
  SAVINGS,
  CHEQUE,
  TRANSMISSION,
  BOND,
  CREDIT_CARD;

  /**
   * Parse a QLink account type description to the corresponding enum value.
   */
  public static BankAccountType fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return null;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "SAVINGS", "SAVING" -> SAVINGS;
        case "CHEQUE", "CHECKING", "CURRENT" -> CHEQUE;
        case "TRANSMISSION" -> TRANSMISSION;
        case "BOND", "MORTGAGE", "HOME_LOAN" -> BOND;
        case "CREDIT_CARD", "CREDIT" -> CREDIT_CARD;
        default -> null;
      };
    }
  }
}
