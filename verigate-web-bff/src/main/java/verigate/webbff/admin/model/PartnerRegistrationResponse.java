package verigate.webbff.admin.model;

public record PartnerRegistrationResponse(
    String partnerId,
    String email,
    String status,
    String message) {}
