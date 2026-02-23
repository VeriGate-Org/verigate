/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import verigate.adapter.income.domain.enums.ConfidenceLevel;
import verigate.adapter.income.domain.enums.IncomeVerificationStatus;

/**
 * Represents the response from an income verification request.
 *
 * @param status the overall verification status
 * @param assessment the detailed income assessment with variance analysis
 * @param reason a human-readable reason for the verification outcome
 * @param verifiedAt the timestamp when verification was completed
 */
public record IncomeVerificationResponse(
    IncomeVerificationStatus status,
    IncomeAssessment assessment,
    String reason,
    LocalDateTime verifiedAt
) {

  /**
   * Creates a successful verified response with full income assessment details.
   *
   * @param assessment the verified income assessment
   * @param reason the reason for the successful verification
   * @return a verified response
   */
  public static IncomeVerificationResponse verified(IncomeAssessment assessment, String reason) {
    return new IncomeVerificationResponse(
        IncomeVerificationStatus.VERIFIED, assessment, reason, LocalDateTime.now());
  }

  /**
   * Creates a mismatch response when declared income does not match verified income.
   *
   * @param assessment the income assessment showing the mismatch
   * @param reason the reason for the mismatch
   * @return a mismatch response
   */
  public static IncomeVerificationResponse mismatch(IncomeAssessment assessment, String reason) {
    return new IncomeVerificationResponse(
        IncomeVerificationStatus.MISMATCH, assessment, reason, LocalDateTime.now());
  }

  /**
   * Creates an insufficient evidence response when not enough data is available.
   *
   * @param declaredMonthlyIncome the declared income for the assessment
   * @param reason the reason for insufficient evidence
   * @return an insufficient evidence response
   */
  public static IncomeVerificationResponse insufficientEvidence(
      BigDecimal declaredMonthlyIncome, String reason) {
    return new IncomeVerificationResponse(
        IncomeVerificationStatus.INSUFFICIENT_EVIDENCE,
        IncomeAssessment.insufficientEvidence(declaredMonthlyIncome),
        reason,
        LocalDateTime.now());
  }

  /**
   * Creates an unverifiable response when income cannot be verified.
   *
   * @param declaredMonthlyIncome the declared income for the assessment
   * @param reason the reason verification is not possible
   * @return an unverifiable response
   */
  public static IncomeVerificationResponse unverifiable(
      BigDecimal declaredMonthlyIncome, String reason) {
    return new IncomeVerificationResponse(
        IncomeVerificationStatus.UNVERIFIABLE,
        IncomeAssessment.insufficientEvidence(declaredMonthlyIncome),
        reason,
        LocalDateTime.now());
  }

  /**
   * Creates a pending response when verification is still in progress.
   *
   * @param declaredMonthlyIncome the declared income for the assessment
   * @param reason the reason verification is pending
   * @return a pending response
   */
  public static IncomeVerificationResponse pending(
      BigDecimal declaredMonthlyIncome, String reason) {
    return new IncomeVerificationResponse(
        IncomeVerificationStatus.PENDING,
        IncomeAssessment.insufficientEvidence(declaredMonthlyIncome),
        reason,
        LocalDateTime.now());
  }

  /**
   * Creates an error response when verification could not be completed.
   *
   * @param declaredMonthlyIncome the declared income for the assessment
   * @param errorMessage the error message
   * @return an error response
   */
  public static IncomeVerificationResponse error(
      BigDecimal declaredMonthlyIncome, String errorMessage) {
    return new IncomeVerificationResponse(
        IncomeVerificationStatus.ERROR,
        IncomeAssessment.error(declaredMonthlyIncome),
        errorMessage,
        LocalDateTime.now());
  }

  /**
   * Checks if the response indicates the income was successfully verified.
   */
  public boolean isVerified() {
    return status == IncomeVerificationStatus.VERIFIED;
  }

  /**
   * Checks if the response indicates a mismatch between declared and verified income.
   */
  public boolean isMismatch() {
    return status == IncomeVerificationStatus.MISMATCH;
  }

  /**
   * Checks if the response indicates insufficient evidence for verification.
   */
  public boolean isInsufficientEvidence() {
    return status == IncomeVerificationStatus.INSUFFICIENT_EVIDENCE;
  }

  /**
   * Checks if the response indicates the income is unverifiable.
   */
  public boolean isUnverifiable() {
    return status == IncomeVerificationStatus.UNVERIFIABLE;
  }

  /**
   * Checks if the response indicates an error occurred during verification.
   */
  public boolean isError() {
    return status == IncomeVerificationStatus.ERROR;
  }
}
