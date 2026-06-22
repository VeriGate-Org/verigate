package verigate.adapter.dharesponse.domain.models;

/** Parsed representation of a DHA email response with verification result. */
public record ParsedDhaResponse(
    String commandId,
    String permitNumber,
    String partnerId,
    String senderEmail,
    String subject,
    String body,
    String rawEmailS3Key,
    DhaResponseVerificationResult verificationResult
) {}
