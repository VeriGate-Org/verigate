/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.domain.enums;

/**
 * Enumeration of possible income source types.
 */
public enum IncomeSourceType {
  SALARY,
  COMMISSION,
  RENTAL,
  INVESTMENT,
  SELF_EMPLOYMENT,
  PENSION,
  OTHER;

  /**
   * Parse an income source type description to the corresponding enum value.
   *
   * @param description the source type description string
   * @return the corresponding enum value
   */
  public static IncomeSourceType fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return OTHER;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "SALARY", "WAGE", "WAGES", "EMPLOYMENT_INCOME", "BASIC_SALARY" -> SALARY;
        case "COMMISSION", "COMMISSIONS", "SALES_COMMISSION", "BONUS" -> COMMISSION;
        case "RENTAL", "RENT", "RENTAL_INCOME", "PROPERTY_INCOME" -> RENTAL;
        case "INVESTMENT", "INVESTMENTS", "DIVIDEND", "DIVIDENDS", "INTEREST" -> INVESTMENT;
        case "SELF_EMPLOYMENT", "SELF_EMPLOYED", "FREELANCE", "BUSINESS_INCOME" -> SELF_EMPLOYMENT;
        case "PENSION", "RETIREMENT", "ANNUITY", "PENSION_INCOME" -> PENSION;
        default -> OTHER;
      };
    }
  }
}
