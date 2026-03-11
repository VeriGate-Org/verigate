package verigate.webbff.verification.model;

import java.util.UUID;

public record VerificationResponse(UUID commandId, CommandStatus status, UUID workflowId) {

    /**
     * Constructor for single-check responses (no workflow).
     */
    public VerificationResponse(UUID commandId, CommandStatus status) {
        this(commandId, status, null);
    }
}
