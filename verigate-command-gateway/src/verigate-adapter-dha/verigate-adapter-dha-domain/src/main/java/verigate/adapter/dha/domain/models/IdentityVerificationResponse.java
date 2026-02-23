/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

/**
 * Represents the response from a DHA identity verification request.
 */
public record IdentityVerificationResponse(
    IdVerificationStatus status,
    CitizenshipStatus citizenshipStatus,
    String maritalStatus,
    VitalStatus vitalStatus,
    String matchDetails
) {

  /**
   * Creates a successful verification response.
   */
  public static IdentityVerificationResponse verified(
      CitizenshipStatus citizenshipStatus,
      String maritalStatus,
      VitalStatus vitalStatus,
      String matchDetails) {
    return new IdentityVerificationResponse(
        IdVerificationStatus.VERIFIED, citizenshipStatus, maritalStatus, vitalStatus, matchDetails);
  }

  /**
   * Creates a not found response.
   */
  public static IdentityVerificationResponse notFound() {
    return new IdentityVerificationResponse(
        IdVerificationStatus.NOT_FOUND, CitizenshipStatus.UNKNOWN, null, VitalStatus.UNKNOWN,
        "Identity not found in DHA records");
  }

  /**
   * Creates a deceased response.
   */
  public static IdentityVerificationResponse deceased(String matchDetails) {
    return new IdentityVerificationResponse(
        IdVerificationStatus.DECEASED, CitizenshipStatus.UNKNOWN, null, VitalStatus.DECEASED,
        matchDetails);
  }

  /**
   * Creates a mismatch response.
   */
  public static IdentityVerificationResponse mismatch(String matchDetails) {
    return new IdentityVerificationResponse(
        IdVerificationStatus.MISMATCH, CitizenshipStatus.UNKNOWN, null, VitalStatus.UNKNOWN,
        matchDetails);
  }

  /**
   * Creates an error response with the specified status and details.
   */
  public static IdentityVerificationResponse error(
      IdVerificationStatus status, String matchDetails) {
    return new IdentityVerificationResponse(
        status, CitizenshipStatus.UNKNOWN, null, VitalStatus.UNKNOWN, matchDetails);
  }

  /**
   * Checks if the response indicates a successful verification.
   */
  public boolean isVerified() {
    return status == IdVerificationStatus.VERIFIED;
  }

  /**
   * Checks if the response indicates the individual is deceased.
   */
  public boolean isDeceased() {
    return status == IdVerificationStatus.DECEASED || vitalStatus == VitalStatus.DECEASED;
  }
}
