package verigate.webbff.partner.features;

import java.util.List;
import java.util.Map;

public record PartnerEntitlements(
    String partnerId,
    String billingPlan,
    List<String> enabledFeatures,
    List<String> resolvedFeatures,
    Map<String, Integer> quotas) {}
