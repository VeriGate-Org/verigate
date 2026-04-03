package verigate.webbff.partner.model;

import java.util.List;

public record PartnerProfileUpdateRequest(
    String name,
    String contactEmail,
    String billingPlan,
    List<String> enabledFeatures,
    // Branding fields
    String logo,
    String logoDark,
    String primaryColor,
    String accentColor,
    String faviconUrl,
    String tagline) {}
