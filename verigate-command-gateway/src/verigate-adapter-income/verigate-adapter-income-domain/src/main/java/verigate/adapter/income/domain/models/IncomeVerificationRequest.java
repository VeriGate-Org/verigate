/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.domain.models;

import java.math.BigDecimal;
import verigate.adapter.income.domain.enums.IncomeSourceType;

/**
 * Represents a request to verify income details.
 *
 * @param idNumber the identity number of the person whose income is being verified
 * @param employerName the name of the employer (for salary-based income)
 * @param declaredMonthlyIncome the monthly income amount declared by the applicant
 * @param sourceType the type of income source being verified
 * @param bankAccountNumber the bank account number for bank statement analysis
 */
public record IncomeVerificationRequest(
    String idNumber,
    String employerName,
    BigDecimal declaredMonthlyIncome,
    IncomeSourceType sourceType,
    String bankAccountNumber
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String idNumber;
    private String employerName;
    private BigDecimal declaredMonthlyIncome;
    private IncomeSourceType sourceType;
    private String bankAccountNumber;

    public Builder idNumber(String idNumber) {
      this.idNumber = idNumber;
      return this;
    }

    public Builder employerName(String employerName) {
      this.employerName = employerName;
      return this;
    }

    public Builder declaredMonthlyIncome(BigDecimal declaredMonthlyIncome) {
      this.declaredMonthlyIncome = declaredMonthlyIncome;
      return this;
    }

    public Builder sourceType(IncomeSourceType sourceType) {
      this.sourceType = sourceType;
      return this;
    }

    public Builder bankAccountNumber(String bankAccountNumber) {
      this.bankAccountNumber = bankAccountNumber;
      return this;
    }

    /**
     * Builds the immutable income verification request instance.
     */
    public IncomeVerificationRequest build() {
      return new IncomeVerificationRequest(
          idNumber, employerName, declaredMonthlyIncome, sourceType, bankAccountNumber);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
