package verigate.webbff.partner.model;

import java.util.List;

public record PolicyResponse(
    String id,
    String partnerId,
    String name,
    String description,
    int version,
    String status,
    List<PolicyRequest.PolicyStep> steps,
    String createdAt,
    String updatedAt) {}
