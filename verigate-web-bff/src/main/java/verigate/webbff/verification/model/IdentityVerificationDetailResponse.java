package verigate.webbff.verification.model;

import java.util.UUID;

/**
 * Detailed identity verification response including HANIS NPR data.
 */
public record IdentityVerificationDetailResponse(
    UUID verificationId,
    String status,
    String outcome,
    CitizenDetails citizenDetails,
    DocumentInfo documentInfo,
    MaritalInfo maritalInfo,
    VitalStatusInfo vitalStatus,
    String matchDetails,
    boolean photoAvailable,
    String source,
    String submittedAt,
    String completedAt
) {

  public record CitizenDetails(
      String name,
      String surname,
      String idNumber,
      String citizenshipStatus,
      String birthCountry,
      boolean onHanis,
      boolean onNpr
  ) {}

  public record DocumentInfo(
      boolean smartCardIssued,
      String idIssueDate,
      String idSequenceNo,
      boolean idnBlocked
  ) {}

  public record MaritalInfo(
      String maritalStatus,
      String dateOfMarriage
  ) {}

  public record VitalStatusInfo(
      String status,
      boolean deceased,
      String dateOfDeath
  ) {}
}
