"use client";

import * as React from "react";
import { useRouter } from "next/navigation";
import { PolicyBuilder } from "@/components/ui/PolicyBuilder";
import type { Policy } from "@/components/ui/PolicyBuilder";
import { createPolicy, publishPolicy } from "@/lib/services/policy-service";

export default function NewPolicyPage() {
  const router = useRouter();

  const handleSave = async (policy: Policy) => {
    try {
      const created = await createPolicy({
        name: policy.name,
        description: policy.description,
        steps: policy.steps as unknown as Array<Record<string, unknown>>,
        scoringConfig: policy.scoringConfig as unknown as import("@/lib/bff-client").RiskConfig,
      });

      if (created.policyId) {
        router.push(`/policies/${created.policyId}`);
      }
    } catch (err) {
      alert(`Failed to save policy: ${err instanceof Error ? err.message : "Unknown error"}`);
    }
  };

  const handlePublish = async (policy: Policy) => {
    try {
      const created = await createPolicy({
        name: policy.name,
        description: policy.description,
        steps: policy.steps as unknown as Array<Record<string, unknown>>,
        scoringConfig: policy.scoringConfig as unknown as import("@/lib/bff-client").RiskConfig,
      });

      if (created.policyId) {
        await publishPolicy(created.policyId);
        router.push("/policies");
      }
    } catch (err) {
      alert(`Failed to publish policy: ${err instanceof Error ? err.message : "Unknown error"}`);
    }
  };

  return (
    <div className="h-[calc(100vh-3.5rem)]">
      <PolicyBuilder onSave={handleSave} onPublish={handlePublish} />
    </div>
  );
}
