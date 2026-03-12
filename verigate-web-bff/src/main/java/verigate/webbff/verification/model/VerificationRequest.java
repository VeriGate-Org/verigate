package verigate.webbff.verification.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record VerificationRequest(
    VerificationType verificationType,
    @NotNull OriginationType originationType,
    @NotNull UUID originationId,
    @NotBlank String requestedBy,
    @NotNull Map<String, Object> metadata,
    String policyId,
    List<String> documentS3Keys) {

    /**
     * Returns true if this is a multi-check workflow request (policyId provided).
     */
    public boolean isWorkflowRequest() {
        return policyId != null && !policyId.isBlank();
    }
}
