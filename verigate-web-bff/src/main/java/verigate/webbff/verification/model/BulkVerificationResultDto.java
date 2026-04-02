package verigate.webbff.verification.model;

/**
 * Response DTO for an individual result within a bulk verification job.
 */
public record BulkVerificationResultDto(
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
    int errorCode,
    boolean success
) {}
