package verigate.webbff.partner.model;

public record PartnerProfileResponse(
    String partnerId,
    String name,
    String contactEmail,
    String billingPlan,
    String status,
    String createdAt) {}
