"use client";

import { useEffect, useState } from "react";
import { getProfile, type BffProfileResponse } from "@/lib/bff-client";
import { SkeletonCard } from "@/components/ui/Loading/Skeleton";

export function ReadOnlyField({ label, value }: { label: string; value: string }) {
  return (
    <label className="block space-y-1 text-sm">
      <span className="font-medium text-text">{label}</span>
      <input
        type="text"
        readOnly
        value={value}
        className="aws-input w-full max-w-md cursor-default bg-[color:var(--color-base-200)] text-text-muted"
      />
    </label>
  );
}

export default function ProfileTab() {
  const [profile, setProfile] = useState<BffProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getProfile()
      .then(setProfile)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <SkeletonCard />;
  }

  return (
    <div className="console-card">
      <div className="console-card-header">
        <div>
          <div className="text-sm font-semibold text-text">Partner profile</div>
          <div className="text-xs text-text-muted">Account details are managed by your organisation administrator.</div>
        </div>
      </div>
      <div className="console-card-body space-y-4">
        <ReadOnlyField label="Partner name" value={profile?.name ?? "—"} />
        <ReadOnlyField label="Email address" value={profile?.contactEmail ?? "—"} />
        <ReadOnlyField label="Billing plan" value={profile?.billingPlan ?? "—"} />
        <div className="pt-2">
          <p className="text-xs text-text-muted">
            To update your profile or billing plan, contact your account manager or reach out to{" "}
            <a href="mailto:support@verigate.co.za" className="text-accent underline">
              support@verigate.co.za
            </a>
            .
          </p>
        </div>
      </div>
    </div>
  );
}
