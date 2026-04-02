/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

/**
 * Represents a single result from a bulk verification job.
 * Each record corresponds to one ID number in the bulk submission.
 */
public record BulkVerificationResult(
    String idNumber,
    String names,
    String surname,
    String gender,
    String dateOfBirth,
    String birthCountry,
    String citizenStatus,
    String nationality,
    boolean smartCardIssued,
    String idCardIssueDate,
    boolean idBookIssued,
    String idBookIssueDate,
    boolean idBlocked,
    String maritalStatus,
    String maidenName,
    String marriageDate,
    String divorceDate,
    String dateOfDeath,
    String deathPlace,
    String causeOfDeath,
    int errorCode
) {

  /**
   * Checks if this result indicates a successful verification.
   */
  public boolean isSuccess() {
    return errorCode == 0;
  }

  /**
   * Checks if the individual is recorded as deceased.
   */
  public boolean isDeceased() {
    return dateOfDeath != null && !dateOfDeath.isBlank();
  }
}
