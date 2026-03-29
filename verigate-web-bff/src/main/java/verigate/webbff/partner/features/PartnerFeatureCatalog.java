package verigate.webbff.partner.features;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class PartnerFeatureCatalog {

  public static final String CORE_VERIFICATIONS = "core_verifications";
  public static final String CASE_MANAGEMENT = "case_management";
  public static final String POLICY_BUILDER = "policy_builder";
  public static final String MONITORING = "monitoring";
  public static final String REPORTING = "reporting";
  public static final String DATA_EXPORT = "data_export";
  public static final String API_KEYS = "api_keys";
  public static final String DEEDS_REGISTRY = "deeds_registry";
  public static final String DEEDS_MAP = "deeds_map";
  public static final String DEEDS_CONVERSION = "deeds_conversion";
  public static final String DEEDS_VALUATION = "deeds_valuation";
  public static final String DEEDS_ADMIN = "deeds_admin";
  public static final String ADVANCED_SCREENING = "advanced_screening";

  public static final List<String> ALL_FEATURES = List.of(
      CORE_VERIFICATIONS,
      CASE_MANAGEMENT,
      POLICY_BUILDER,
      MONITORING,
      REPORTING,
      DATA_EXPORT,
      API_KEYS,
      DEEDS_REGISTRY,
      DEEDS_MAP,
      DEEDS_CONVERSION,
      DEEDS_VALUATION,
      DEEDS_ADMIN,
      ADVANCED_SCREENING);

  private static final List<String> PLAN_ORDER = List.of("free", "standard", "professional", "enterprise");

  private static final Map<String, String> FEATURE_MIN_PLAN = Map.ofEntries(
      Map.entry(CORE_VERIFICATIONS, "free"),
      Map.entry(CASE_MANAGEMENT, "standard"),
      Map.entry(POLICY_BUILDER, "standard"),
      Map.entry(MONITORING, "professional"),
      Map.entry(REPORTING, "standard"),
      Map.entry(DATA_EXPORT, "enterprise"),
      Map.entry(API_KEYS, "enterprise"),
      Map.entry(DEEDS_REGISTRY, "standard"),
      Map.entry(DEEDS_MAP, "professional"),
      Map.entry(DEEDS_CONVERSION, "professional"),
      Map.entry(DEEDS_VALUATION, "professional"),
      Map.entry(DEEDS_ADMIN, "enterprise"),
      Map.entry(ADVANCED_SCREENING, "professional"));

  private static final Map<String, Map<String, Integer>> PLAN_QUOTAS = Map.of(
      "free",
      Map.of(
          "maxUsers", 25,
          "maxApiKeys", 0,
          "maxSavedReports", 5,
          "maxMonitoringSubjects", 50),
      "standard",
      Map.of(
          "maxUsers", 250,
          "maxApiKeys", 2,
          "maxSavedReports", 25,
          "maxMonitoringSubjects", 250),
      "professional",
      Map.of(
          "maxUsers", 2_000,
          "maxApiKeys", 10,
          "maxSavedReports", 200,
          "maxMonitoringSubjects", 2_000),
      "enterprise",
      Map.of(
          "maxUsers", 10_000,
          "maxApiKeys", 100,
          "maxSavedReports", 1_000,
          "maxMonitoringSubjects", 10_000));

  private static final Map<String, String> FEATURE_LABELS = Map.ofEntries(
      Map.entry(CORE_VERIFICATIONS, "Core Verifications"),
      Map.entry(CASE_MANAGEMENT, "Case Management"),
      Map.entry(POLICY_BUILDER, "Policy Builder"),
      Map.entry(MONITORING, "Monitoring"),
      Map.entry(REPORTING, "Reporting"),
      Map.entry(DATA_EXPORT, "Data Export"),
      Map.entry(API_KEYS, "API Keys"),
      Map.entry(DEEDS_REGISTRY, "Deeds Registry"),
      Map.entry(DEEDS_MAP, "Deeds Map"),
      Map.entry(DEEDS_CONVERSION, "Street / ERF Conversion"),
      Map.entry(DEEDS_VALUATION, "Property Valuation"),
      Map.entry(DEEDS_ADMIN, "Deeds Administration"),
      Map.entry(ADVANCED_SCREENING, "Advanced Screening"));

  private static final Map<String, String> PLAN_LABELS = Map.of(
      "free", "Free",
      "standard", "Standard",
      "professional", "Professional",
      "enterprise", "Enterprise");

  private PartnerFeatureCatalog() {}

  public static String normalizePlan(String rawPlan) {
    if (rawPlan == null || rawPlan.isBlank()) {
      return "enterprise";
    }
    String normalized = rawPlan.trim().toLowerCase(Locale.ROOT);
    if ("starter".equals(normalized)) {
      return "standard";
    }
    return PLAN_ORDER.contains(normalized) ? normalized : "enterprise";
  }

  public static boolean planHasFeature(String plan, String feature) {
    String normalizedPlan = normalizePlan(plan);
    String requiredPlan = FEATURE_MIN_PLAN.getOrDefault(feature, "enterprise");
    return PLAN_ORDER.indexOf(normalizedPlan) >= PLAN_ORDER.indexOf(requiredPlan);
  }

  public static boolean partnerHasFeature(String plan, List<String> enabledFeatures, String feature) {
    if (enabledFeatures != null && enabledFeatures.contains(feature)) {
      return true;
    }
    return planHasFeature(plan, feature);
  }

  public static List<String> getResolvedFeatures(String plan, List<String> enabledFeatures) {
    Set<String> resolved = new LinkedHashSet<>();
    for (String feature : ALL_FEATURES) {
      if (partnerHasFeature(plan, enabledFeatures, feature)) {
        resolved.add(feature);
      }
    }
    if (enabledFeatures != null) {
      resolved.addAll(enabledFeatures.stream()
          .filter(ALL_FEATURES::contains)
          .toList());
    }
    return List.copyOf(resolved);
  }

  public static List<String> normalizeEnabledFeatures(List<String> enabledFeatures) {
    if (enabledFeatures == null) {
      return List.of();
    }
    return enabledFeatures.stream()
        .filter(feature -> feature != null && !feature.isBlank())
        .map(feature -> feature.trim().toLowerCase(Locale.ROOT))
        .filter(ALL_FEATURES::contains)
        .distinct()
        .toList();
  }

  public static Map<String, Integer> getPlanQuotas(String plan) {
    return new LinkedHashMap<>(PLAN_QUOTAS.getOrDefault(normalizePlan(plan), PLAN_QUOTAS.get("enterprise")));
  }

  public static String getRequiredPlan(String feature) {
    return FEATURE_MIN_PLAN.getOrDefault(feature, "enterprise");
  }

  public static String getFeatureLabel(String feature) {
    return FEATURE_LABELS.getOrDefault(feature, feature);
  }

  public static String getPlanLabel(String plan) {
    return PLAN_LABELS.getOrDefault(normalizePlan(plan), "Enterprise");
  }

  public static List<String> allPlans() {
    return PLAN_ORDER;
  }

  public static List<String> allFeatures() {
    return ALL_FEATURES;
  }

  public static Map<String, String> featureLabels() {
    return ALL_FEATURES.stream()
        .collect(Collectors.toMap(
            feature -> feature,
            PartnerFeatureCatalog::getFeatureLabel,
            (left, right) -> left,
            LinkedHashMap::new));
  }

  public static Map<String, String> planLabels() {
    return PLAN_ORDER.stream()
        .collect(Collectors.toMap(
            plan -> plan,
            PartnerFeatureCatalog::getPlanLabel,
            (left, right) -> left,
            LinkedHashMap::new));
  }
}
