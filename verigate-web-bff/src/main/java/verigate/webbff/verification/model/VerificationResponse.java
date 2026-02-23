package verigate.webbff.verification.model;

import java.util.UUID;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStatusEnum;

public record VerificationResponse(UUID commandId, VerificationCommandStatusEnum status) {}
