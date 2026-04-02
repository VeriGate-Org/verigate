package verigate.webbff.verification.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Request DTO for creating a bulk verification job.
 */
public record BulkVerificationRequest(
    @NotEmpty @Size(max = 200000) List<String> idNumbers,
    BillingGroupsDto billingGroups
) {
  public record BillingGroupsDto(
      boolean demographics,
      boolean identity,
      boolean maritalStatus,
      boolean deathInfo,
      boolean smartCard,
      boolean photo,
      boolean address,
      boolean fingerprint,
      boolean reserved1,
      boolean reserved2
  ) {}
}
