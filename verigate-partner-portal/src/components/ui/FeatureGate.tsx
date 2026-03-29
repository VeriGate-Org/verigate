"use client";

import type { ReactNode } from "react";
import { FEATURE_LABELS, PLAN_LABELS, type FeatureKey } from "@/lib/tenant-features";
import { useTenantFeatures } from "@/lib/tenant/PartnerTenantProvider";

export function FeatureGate({
  feature,
  children,
  fallback,
}: {
  feature: FeatureKey;
  children: ReactNode;
  fallback?: ReactNode;
}) {
  const { hasFeature, requiredPlan, loading } = useTenantFeatures();

  if (loading) {
    return null;
  }

  if (!hasFeature(feature)) {
    return (
      <>
        {fallback ?? (
          <div className="console-card">
            <div className="console-card-body space-y-2 py-8">
              <div className="text-sm font-semibold text-text">Feature not available</div>
              <p className="text-sm text-text-muted">
                {FEATURE_LABELS[feature]} requires the {PLAN_LABELS[requiredPlan(feature)]} plan or a tenant feature override.
              </p>
            </div>
          </div>
        )}
      </>
    );
  }

  return <>{children}</>;
}
