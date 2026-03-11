package verigate.webbff.admin.model;

public record PartnerResponse(
    String partnerId,
    String name,
    String contactEmail,
    String billingPlan,
    String status,
    String createdAt) {}
