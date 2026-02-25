package verigate.webbff.verification.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public record VerificationRequest(
    @NotNull VerificationType verificationType,
    @NotNull OriginationType originationType,
    @NotNull UUID originationId,
    @NotBlank String requestedBy,
    @NotNull Map<String, Object> metadata) {}
