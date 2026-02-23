/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.domain.models;

/**
 * Represents the response from a credit check request.
 */
public record CreditCheckResponse(
    CreditCheckStatus status,
    CreditAssessment assessment,
    boolean affordabilityConfirmed,
    String riskLevel,
    String summary
) {

  /**
   * Creates a successful (passed) credit check response.
   */
  public static CreditCheckResponse passed(
      CreditAssessment assessment, boolean affordabilityConfirmed,
      String riskLevel, String summary) {
    return new CreditCheckResponse(
        CreditCheckStatus.PASSED, assessment, affordabilityConfirmed, riskLevel, summary);
  }

  /**
   * Creates a failed credit check response.
   */
  public static CreditCheckResponse failed(
      CreditAssessment assessment, String riskLevel, String summary) {
    return new CreditCheckResponse(
        CreditCheckStatus.FAILED, assessment, false, riskLevel, summary);
  }

  /**
   * Creates a review-required credit check response.
   */
  public static CreditCheckResponse reviewRequired(
      CreditAssessment assessment, boolean affordabilityConfirmed,
      String riskLevel, String summary) {
    return new CreditCheckResponse(
        CreditCheckStatus.REVIEW_REQUIRED, assessment, affordabilityConfirmed, riskLevel, summary);
  }

  /**
   * Creates an insufficient-data credit check response.
   */
  public static CreditCheckResponse insufficientData(String summary) {
    return new CreditCheckResponse(
        CreditCheckStatus.INSUFFICIENT_DATA, null, false, "UNKNOWN", summary);
  }

  /**
   * Creates an error credit check response.
   */
  public static CreditCheckResponse error(String summary) {
    return new CreditCheckResponse(CreditCheckStatus.ERROR, null, false, "UNKNOWN", summary);
  }
}
