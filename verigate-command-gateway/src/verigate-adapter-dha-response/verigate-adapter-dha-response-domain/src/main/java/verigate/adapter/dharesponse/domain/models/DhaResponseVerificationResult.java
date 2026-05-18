package verigate.adapter.dharesponse.domain.models;

import java.time.Instant;
import java.util.Map;

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
