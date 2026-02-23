/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.domain.models;

/**
 * Represents the response from an employment verification request.
 */
public record EmploymentVerificationResponse(
    EmploymentStatus status,
    String employerName,
    EmploymentType employmentType,
    String jobTitle,
    String startDate,
    String endDate,
    String department
) {

  /**
   * Creates a successful employed response with full employment details.
   */
  public static EmploymentVerificationResponse employed(
      String employerName,
      EmploymentType employmentType,
      String jobTitle,
      String startDate,
      String endDate,
      String department) {
    return new EmploymentVerificationResponse(
        EmploymentStatus.EMPLOYED, employerName, employmentType, jobTitle,
        startDate, endDate, department);
  }

  /**
   * Creates a terminated employment response.
   */
  public static EmploymentVerificationResponse terminated(
      String employerName,
      String jobTitle,
      String startDate,
      String endDate) {
    return new EmploymentVerificationResponse(
        EmploymentStatus.TERMINATED, employerName, null, jobTitle,
        startDate, endDate, null);
  }

  /**
   * Creates a not found response when no employment record exists.
   */
  public static EmploymentVerificationResponse notFound() {
    return new EmploymentVerificationResponse(
        EmploymentStatus.NOT_FOUND, null, null, null, null, null, null);
  }

  /**
   * Creates an error response when verification could not be completed.
   */
  public static EmploymentVerificationResponse error(String errorMessage) {
    return new EmploymentVerificationResponse(
        EmploymentStatus.UNVERIFIABLE, null, null, null, null, null, null);
  }

  /**
   * Checks if the response indicates the person is currently employed.
   */
  public boolean isEmployed() {
    return status == EmploymentStatus.EMPLOYED;
  }

  /**
   * Checks if the response indicates the employment was terminated.
   */
  public boolean isTerminated() {
    return status == EmploymentStatus.TERMINATED;
  }

  /**
   * Checks if the response indicates the person was not found.
   */
  public boolean isNotFound() {
    return status == EmploymentStatus.NOT_FOUND;
  }

  /**
   * Checks if the response indicates the verification could not be completed.
   */
  public boolean isUnverifiable() {
    return status == EmploymentStatus.UNVERIFIABLE;
  }
}
