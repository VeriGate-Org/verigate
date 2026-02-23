/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

import java.time.LocalDate;

/**
 * Represents the result of a directorship verification check against CIPC data.
 * Encapsulates whether the subject was found as a director, their current status,
 * and details about their appointment.
 */
public record DirectorshipVerificationResult(
    boolean directorFound,
    boolean directorActive,
    boolean companyActive,
    String directorDesignation,
    DirectorType directorType,
    LocalDate appointmentDate,
    LocalDate resignationDate,
    String failureReason
) {

  /**
   * Creates a successful verification result for an active director.
   *
   * @param designation the director's designation or role
   * @param directorType the type of director
   * @param appointmentDate when the director was appointed
   * @return a successful verification result
   */
  public static DirectorshipVerificationResult success(
      String designation,
      DirectorType directorType,
      LocalDate appointmentDate) {
    return new DirectorshipVerificationResult(
        true,
        true,
        true,
        designation,
        directorType,
        appointmentDate,
        null,
        null);
  }

  /**
   * Creates a verification result indicating the subject was not found among the company directors.
   *
   * @param subjectIdNumber the identity number that was searched for
   * @param enterpriseNumber the enterprise number of the company
   * @return a director-not-found verification result
   */
  public static DirectorshipVerificationResult directorNotFound(
      String subjectIdNumber,
      String enterpriseNumber) {
    String reason = String.format(
        "No director found with identity number %s for enterprise %s",
        subjectIdNumber,
        enterpriseNumber);
    return new DirectorshipVerificationResult(
        false,
        false,
        true,
        null,
        null,
        null,
        null,
        reason);
  }

  /**
   * Creates a verification result indicating the director was found but is no longer active.
   *
   * @param director the matched director record
   * @return an inactive-director verification result
   */
  public static DirectorshipVerificationResult directorInactive(Director director) {
    String reason = String.format(
        "Director %s is not active. Current status: %s",
        director.getFullName(),
        director.directorStatus());
    return new DirectorshipVerificationResult(
        true,
        false,
        true,
        director.designation(),
        director.directorType(),
        director.appointmentDate(),
        director.resignationDate(),
        reason);
  }

  /**
   * Creates a verification result indicating the company itself is not active.
   *
   * @param companyStatus the current company status
   * @param enterpriseNumber the enterprise number of the company
   * @return a company-inactive verification result
   */
  public static DirectorshipVerificationResult companyInactive(
      CompanyStatus companyStatus,
      String enterpriseNumber) {
    String reason = String.format(
        "Company %s is not active. Current status: %s",
        enterpriseNumber,
        companyStatus);
    return new DirectorshipVerificationResult(
        false,
        false,
        false,
        null,
        null,
        null,
        null,
        reason);
  }

  /**
   * Checks if the verification was successful (director found, active, and company active).
   *
   * @return true if the verification passed all checks
   */
  public boolean isSuccessful() {
    return directorFound && directorActive && companyActive;
  }
}
