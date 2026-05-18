package verigate.adapter.dharesponse.domain.models;

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
