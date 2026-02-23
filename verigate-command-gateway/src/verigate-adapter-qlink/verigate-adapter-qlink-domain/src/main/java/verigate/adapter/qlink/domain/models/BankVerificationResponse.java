/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.domain.models;

/**
 * Represents the response from a QLink bank account verification request.
 */
public record BankVerificationResponse(
    BankAccountStatus status,
    String accountHolderName,
    String branchName,
    BankAccountType accountType,
    double matchScore
) {

  /**
   * Creates a successful verification response.
   */
  public static BankVerificationResponse success(
      BankAccountStatus status,
      String accountHolderName,
      String branchName,
      BankAccountType accountType,
      double matchScore) {
    return new BankVerificationResponse(status, accountHolderName, branchName, accountType,
        matchScore);
  }

  /**
   * Creates a not found response.
   */
  public static BankVerificationResponse notFound() {
    return new BankVerificationResponse(BankAccountStatus.NOT_FOUND, null, null, null, 0.0);
  }

  /**
   * Creates an error response with the specified status.
   */
  public static BankVerificationResponse error(BankAccountStatus status) {
    return new BankVerificationResponse(status, null, null, null, 0.0);
  }

  /**
   * Checks if the bank account was found and is valid.
   */
  public boolean isValid() {
    return status == BankAccountStatus.VALID;
  }

  /**
   * Checks if the bank account was not found.
   */
  public boolean isNotFound() {
    return status == BankAccountStatus.NOT_FOUND;
  }
}
