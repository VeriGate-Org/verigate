package verigate.webbff.verification.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStatusEnum;

public record VerificationStatusResponse(
    UUID commandId,
    VerificationCommandStatusEnum status,
    List<String> errorDetails,
    Map<String, String> auxiliaryData) {}
