package verigate.webbff.verification.model;

import java.util.UUID;

public record VerificationListItem(
    UUID commandId,
    CommandStatus status,
    String createdAt,
    String commandName) {}
