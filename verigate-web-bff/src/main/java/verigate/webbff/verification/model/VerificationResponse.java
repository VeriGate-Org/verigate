package verigate.webbff.verification.model;

import java.util.UUID;

public record VerificationResponse(UUID commandId, CommandStatus status) {}
