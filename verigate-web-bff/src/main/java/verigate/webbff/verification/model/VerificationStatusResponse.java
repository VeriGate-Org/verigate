package verigate.webbff.verification.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record VerificationStatusResponse(
    UUID commandId,
    CommandStatus status,
    List<String> errorDetails,
    Map<String, String> auxiliaryData) {}
