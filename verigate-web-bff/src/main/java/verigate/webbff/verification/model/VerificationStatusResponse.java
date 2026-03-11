package verigate.webbff.verification.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record VerificationStatusResponse(
    UUID commandId,
    CommandStatus status,
    List<String> errorDetails,
    Map<String, String> auxiliaryData,
    Integer compositeRiskScore,
    String riskTier,
    String riskDecision,
    String decisionReason,
    List<CheckScore> individualScores) {

    /**
     * Constructor for single-check (no risk) responses — backwards compatible.
     */
    public VerificationStatusResponse(UUID commandId, CommandStatus status,
                                       List<String> errorDetails,
                                       Map<String, String> auxiliaryData) {
        this(commandId, status, errorDetails, auxiliaryData,
            null, null, null, null, null);
    }

    public record CheckScore(
        String verificationType,
        String outcome,
        int confidenceScore,
        Map<String, String> signals) {}
}
