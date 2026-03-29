"use client";

import * as React from "react";
import { getProfile, type BffProfileResponse } from "@/lib/bff-client";
import {
  Feature,
  FEATURE_LABELS,
  PLAN_LABELS,
  type FeatureKey,
  type Plan,
  getRequiredPlan,
  normalizeEnabledFeatures,
  normalizePlan,
  tenantHasFeature,
} from "@/lib/tenant-features";

interface TenantFeatureContextValue {
  profile: BffProfileResponse;
  loading: boolean;
  hasFeature: (feature: FeatureKey) => boolean;
  requiredPlan: (feature: FeatureKey) => Plan;
  refreshProfile: () => Promise<void>;
}

const defaultProfile: BffProfileResponse = {
  partnerId: "partner-portal",
  name: "VeriGate Partner",
  contactEmail: "",
  billingPlan: "enterprise",
  enabledFeatures: [],
  resolvedFeatures: Object.values(Feature),
  quotas: {
    maxUsers: 10_000,
    maxApiKeys: 100,
    maxSavedReports: 1_000,
    maxMonitoringSubjects: 10_000,
  },
  status: "ACTIVE",
  createdAt: null,
};

const TenantFeatureContext = React.createContext<TenantFeatureContextValue>({
  profile: defaultProfile,
  loading: false,
  hasFeature: () => true,
  requiredPlan: getRequiredPlan,
  refreshProfile: async () => {},
});

export function PartnerTenantProvider({ children }: { children: React.ReactNode }) {
  const [profile, setProfile] = React.useState<BffProfileResponse>(defaultProfile);
  const [loading, setLoading] = React.useState(true);

  const refreshProfile = React.useCallback(async () => {
    setLoading(true);
    try {
      const data = await getProfile();
      setProfile({
        ...data,
        billingPlan: normalizePlan(data.billingPlan),
        enabledFeatures: normalizeEnabledFeatures(data.enabledFeatures),
        resolvedFeatures: data.resolvedFeatures?.length
          ? normalizeEnabledFeatures(data.resolvedFeatures)
          : Object.values(Feature).filter((feature) =>
              tenantHasFeature(
                {
                  billingPlan: data.billingPlan,
                  enabledFeatures: data.enabledFeatures,
                },
                feature,
              ),
            ),
      });
    } catch {
      setProfile(defaultProfile);
    } finally {
      setLoading(false);
    }
  }, []);

  React.useEffect(() => {
    void refreshProfile();
  }, [refreshProfile]);

  const hasFeature = React.useCallback(
    (feature: FeatureKey) =>
      tenantHasFeature(
        {
          billingPlan: profile.billingPlan,
          enabledFeatures: profile.enabledFeatures,
        },
        feature,
      ),
    [profile.billingPlan, profile.enabledFeatures],
  );

  const value = React.useMemo(
    () => ({
      profile,
      loading,
      hasFeature,
      requiredPlan: getRequiredPlan,
      refreshProfile,
    }),
    [profile, loading, hasFeature, refreshProfile],
  );

  return (
    <TenantFeatureContext.Provider value={value}>{children}</TenantFeatureContext.Provider>
  );
}

export function useTenantFeatures() {
  return React.useContext(TenantFeatureContext);
}

export { FEATURE_LABELS, PLAN_LABELS };
