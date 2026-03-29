package verigate.webbff.partner.model;

import java.util.List;

public record PartnerProfileUpdateRequest(
    String name,
    String contactEmail,
    String billingPlan,
    List<String> enabledFeatures) {}
