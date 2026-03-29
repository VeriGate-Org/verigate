export const Feature = {
  CORE_VERIFICATIONS: "core_verifications",
  CASE_MANAGEMENT: "case_management",
  POLICY_BUILDER: "policy_builder",
  MONITORING: "monitoring",
  REPORTING: "reporting",
  DATA_EXPORT: "data_export",
  API_KEYS: "api_keys",
  DEEDS_REGISTRY: "deeds_registry",
  DEEDS_MAP: "deeds_map",
  DEEDS_CONVERSION: "deeds_conversion",
  DEEDS_VALUATION: "deeds_valuation",
  DEEDS_ADMIN: "deeds_admin",
  ADVANCED_SCREENING: "advanced_screening",
} as const;

export type FeatureKey = (typeof Feature)[keyof typeof Feature];
export type Plan = "free" | "standard" | "professional" | "enterprise";

const PLAN_ORDER: Record<Plan, number> = {
  free: 0,
  standard: 1,
  professional: 2,
  enterprise: 3,
};

export const FEATURE_MIN_PLAN: Record<FeatureKey, Plan> = {
  core_verifications: "free",
  case_management: "standard",
  policy_builder: "standard",
  monitoring: "professional",
  reporting: "standard",
  data_export: "enterprise",
  api_keys: "enterprise",
  deeds_registry: "standard",
  deeds_map: "professional",
  deeds_conversion: "professional",
  deeds_valuation: "professional",
  deeds_admin: "enterprise",
  advanced_screening: "professional",
};

export const FEATURE_LABELS: Record<FeatureKey, string> = {
  core_verifications: "Core Verifications",
  case_management: "Case Management",
  policy_builder: "Policy Builder",
  monitoring: "Monitoring",
  reporting: "Reporting",
  data_export: "Data Export",
  api_keys: "API Keys",
  deeds_registry: "Deeds Registry",
  deeds_map: "Deeds Map",
  deeds_conversion: "Street / ERF Conversion",
  deeds_valuation: "Property Valuation",
  deeds_admin: "Deeds Administration",
  advanced_screening: "Advanced Screening",
};

export const PLAN_LABELS: Record<Plan, string> = {
  free: "Free",
  standard: "Standard",
  professional: "Professional",
  enterprise: "Enterprise",
};

export function normalizePlan(value: string | null | undefined): Plan {
  const normalized = value?.trim().toLowerCase();
  if (normalized === "starter") return "standard";
  if (normalized === "free" || normalized === "standard" || normalized === "professional" || normalized === "enterprise") {
    return normalized;
  }
  return "enterprise";
}

export function normalizeEnabledFeatures(features: string[] | null | undefined): FeatureKey[] {
  if (!features) return [];
  return features
    .map((feature) => feature.trim().toLowerCase())
    .filter((feature): feature is FeatureKey => feature in FEATURE_LABELS)
    .filter((feature, index, all) => all.indexOf(feature) === index);
}

export function planHasFeature(plan: Plan, feature: FeatureKey): boolean {
  return PLAN_ORDER[plan] >= PLAN_ORDER[FEATURE_MIN_PLAN[feature]];
}

export function tenantHasFeature(
  tenant: { billingPlan: string; enabledFeatures?: string[] | null },
  feature: FeatureKey,
): boolean {
  const plan = normalizePlan(tenant.billingPlan);
  const enabledFeatures = normalizeEnabledFeatures(tenant.enabledFeatures);
  if (enabledFeatures.includes(feature)) return true;
  return planHasFeature(plan, feature);
}

export function getRequiredPlan(feature: FeatureKey): Plan {
  return FEATURE_MIN_PLAN[feature];
}

export function getResolvedFeatures(
  billingPlan: string,
  enabledFeatures?: string[] | null,
): FeatureKey[] {
  const normalizedPlan = normalizePlan(billingPlan);
  const overrides = normalizeEnabledFeatures(enabledFeatures);
  return (Object.values(Feature) as FeatureKey[]).filter(
    (feature) => overrides.includes(feature) || planHasFeature(normalizedPlan, feature),
  );
}
