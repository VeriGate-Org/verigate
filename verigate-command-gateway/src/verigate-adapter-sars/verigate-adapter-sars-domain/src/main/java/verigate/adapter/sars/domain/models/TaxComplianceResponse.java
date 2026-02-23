/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.models;

import java.time.LocalDateTime;
import verigate.adapter.sars.domain.enums.TaxComplianceStatus;

/**
 * Represents the response from a SARS tax compliance verification request.
 *
 * @param status the overall tax compliance status
 * @param certificate the Tax Clearance Certificate details, if available
 * @param reason a human-readable reason or description of the compliance status
 * @param verifiedAt the timestamp when the verification was performed
 */
public record TaxComplianceResponse(
    TaxComplianceStatus status,
    TaxClearanceCertificate certificate,
    String reason,
    LocalDateTime verifiedAt
) {

  /**
   * Creates a compliant response with a valid TCC.
   *
   * @param certificate the valid Tax Clearance Certificate
   * @return a new compliant TaxComplianceResponse
   */
  public static TaxComplianceResponse compliant(TaxClearanceCertificate certificate) {
    return new TaxComplianceResponse(
        TaxComplianceStatus.COMPLIANT,
        certificate,
        "Taxpayer is in good standing with SARS",
        LocalDateTime.now());
  }

  /**
   * Creates a compliant response with a valid TCC and specific reason.
   *
   * @param certificate the valid Tax Clearance Certificate
   * @param reason the reason for the compliance status
   * @return a new compliant TaxComplianceResponse
   */
  public static TaxComplianceResponse compliantWithReason(
      TaxClearanceCertificate certificate, String reason) {
    return new TaxComplianceResponse(
        TaxComplianceStatus.COMPLIANT, certificate, reason, LocalDateTime.now());
  }

  /**
   * Creates a non-compliant response.
   *
   * @param reason the reason for non-compliance
   * @return a new non-compliant TaxComplianceResponse
   */
  public static TaxComplianceResponse nonCompliant(String reason) {
    return new TaxComplianceResponse(
        TaxComplianceStatus.NON_COMPLIANT, null, reason, LocalDateTime.now());
  }

  /**
   * Creates a TCC expired response.
   *
   * @param certificate the expired Tax Clearance Certificate
   * @return a new TCC expired TaxComplianceResponse
   */
  public static TaxComplianceResponse tccExpired(TaxClearanceCertificate certificate) {
    return new TaxComplianceResponse(
        TaxComplianceStatus.TCC_EXPIRED,
        certificate,
        "Tax Clearance Certificate has expired",
        LocalDateTime.now());
  }

  /**
   * Creates a TCC valid response.
   *
   * @param certificate the valid Tax Clearance Certificate
   * @return a new TCC valid TaxComplianceResponse
   */
  public static TaxComplianceResponse tccValid(TaxClearanceCertificate certificate) {
    return new TaxComplianceResponse(
        TaxComplianceStatus.TCC_VALID,
        certificate,
        "Tax Clearance Certificate is valid",
        LocalDateTime.now());
  }

  /**
   * Creates a not found response when no taxpayer record exists.
   *
   * @return a new not found TaxComplianceResponse
   */
  public static TaxComplianceResponse notFound() {
    return new TaxComplianceResponse(
        TaxComplianceStatus.NOT_FOUND,
        null,
        "No taxpayer record found for the provided details",
        LocalDateTime.now());
  }

  /**
   * Creates an error response when verification could not be completed.
   *
   * @param errorMessage the error message describing the failure
   * @return a new error TaxComplianceResponse
   */
  public static TaxComplianceResponse error(String errorMessage) {
    return new TaxComplianceResponse(
        TaxComplianceStatus.ERROR, null, errorMessage, LocalDateTime.now());
  }

  /**
   * Checks if the response indicates tax compliance.
   *
   * @return true if the taxpayer is compliant or has a valid TCC
   */
  public boolean isCompliant() {
    return status == TaxComplianceStatus.COMPLIANT || status == TaxComplianceStatus.TCC_VALID;
  }

  /**
   * Checks if the response indicates the taxpayer was not found.
   *
   * @return true if the taxpayer record was not found
   */
  public boolean isNotFound() {
    return status == TaxComplianceStatus.NOT_FOUND;
  }

  /**
   * Checks if the response indicates an error occurred.
   *
   * @return true if an error occurred during verification
   */
  public boolean isError() {
    return status == TaxComplianceStatus.ERROR;
  }
}
