package verigate.webbff.partner.model;

import java.util.List;
import java.util.Map;

public record PartnerProfileResponse(
    String partnerId,
    String name,
    String contactEmail,
    String billingPlan,
    List<String> enabledFeatures,
    List<String> resolvedFeatures,
    Map<String, Integer> quotas,
    String status,
    String createdAt) {}
