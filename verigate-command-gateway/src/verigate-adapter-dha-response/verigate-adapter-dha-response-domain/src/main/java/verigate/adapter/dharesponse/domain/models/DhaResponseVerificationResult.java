package verigate.adapter.dharesponse.domain.models;

import java.time.Instant;
import java.util.Map;

/** Result of verifying a DHA response, including extracted fields and confidence score. */
public record DhaResponseVerificationResult(
    DhaPermitVerificationOutcome outcome,
    String permitNumber,
    String permitType,
    String holderName,
    String nationality,
    boolean isAuthentic,
    boolean isCurrentlyValid,
    String expiryDate,
    String employerMatch,
    String additionalNotes,
    double confidenceScore,
    Map<String, String> rawExtractedFields,
    Instant responseReceivedAt
) {}
