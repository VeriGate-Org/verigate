/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.domain.models;

/**
 * Represents a request to verify a bank account via QLink.
 */
public record BankVerificationRequest(
    String accountNumber,
    String branchCode,
    String accountHolderName,
    String idNumber
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String accountNumber;
    private String branchCode;
    private String accountHolderName;
    private String idNumber;

    public Builder accountNumber(String accountNumber) {
      this.accountNumber = accountNumber;
      return this;
    }

    public Builder branchCode(String branchCode) {
      this.branchCode = branchCode;
      return this;
    }

    public Builder accountHolderName(String accountHolderName) {
      this.accountHolderName = accountHolderName;
      return this;
    }

    public Builder idNumber(String idNumber) {
      this.idNumber = idNumber;
      return this;
    }

    /**
     * Builds the immutable bank verification request instance.
     */
    public BankVerificationRequest build() {
      return new BankVerificationRequest(accountNumber, branchCode, accountHolderName, idNumber);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
