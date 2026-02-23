package verigate.webbff.admin.model;

import java.time.LocalDateTime;

public record PartnerResponse(
    String partnerId,
    String name,
    String contactEmail,
    String billingPlan,
    LocalDateTime createdAt) {}
