"use client";

import { FEATURE_LABELS, PLAN_LABELS, useTenantFeatures } from "@/lib/tenant/PartnerTenantProvider";
import { type FeatureKey } from "@/lib/tenant-features";
import { ReadOnlyField } from "./ProfileTab";

export default function PlanFeaturesTab() {
  const { profile } = useTenantFeatures();
  const enabledOverrides = profile.enabledFeatures ?? [];
  const resolvedFeatures = profile.resolvedFeatures ?? [];

  return (
    <div className="space-y-6">
      <div className="console-card">
        <div className="console-card-header">
          <div>
            <div className="text-sm font-semibold text-text">Tenant entitlements</div>
            <div className="text-xs text-text-muted">
              Billing plans and feature overrides are managed by platform administrators and are read-only here.
            </div>
          </div>
        </div>
        <div className="console-card-body space-y-4">
          <ReadOnlyField label="Billing plan" value={PLAN_LABELS[profile.billingPlan as keyof typeof PLAN_LABELS] ?? profile.billingPlan ?? "—"} />

          <div className="space-y-2">
            <div className="text-sm font-medium text-text">Resolved features</div>
            <div className="grid gap-3 md:grid-cols-2">
              {resolvedFeatures.length > 0 ? (
                resolvedFeatures.map((feature) => (
                  <div key={feature} className="rounded border border-border bg-background p-3">
                    <div className="text-sm font-medium text-text">
                      {FEATURE_LABELS[feature as FeatureKey] ?? feature}
                    </div>
                    <div className="text-xs text-text-muted">Enabled for this tenant.</div>
                  </div>
                ))
              ) : (
                <div className="rounded border border-dashed border-border bg-background p-3 text-sm text-text-muted">
                  No resolved features configured.
                </div>
              )}
            </div>
          </div>

          <div className="space-y-2">
            <div className="text-sm font-medium text-text">Explicit feature overrides</div>
            <div className="grid gap-3 md:grid-cols-2">
              {enabledOverrides.length > 0 ? (
                enabledOverrides.map((feature) => (
                  <div key={feature} className="rounded border border-border bg-background p-3">
                    <div className="text-sm font-medium text-text">
                      {FEATURE_LABELS[feature as FeatureKey] ?? feature}
                    </div>
                    <div className="text-xs text-text-muted">Granted by platform-admin override.</div>
                  </div>
                ))
              ) : (
                <div className="rounded border border-dashed border-border bg-background p-3 text-sm text-text-muted">
                  No explicit overrides configured.
                </div>
              )}
            </div>
          </div>

          <div className="rounded border border-border bg-[color:var(--color-base-200)] p-4 text-xs text-text-muted">
            Quotas:
            {" "}
            {Object.entries(profile.quotas ?? {}).map(([key, value]) => `${key}: ${value}`).join(" • ")}
          </div>

          <div className="rounded border border-border bg-background p-4 text-xs text-text-muted">
            Contact the VeriGate platform team if you need a plan change or a feature override for this tenant.
          </div>
        </div>
      </div>
    </div>
  );
}
