"use client";

import * as React from "react";
import { useParams, useRouter } from "next/navigation";
import { PolicyBuilder } from "@/components/ui/PolicyBuilder";
import type { Policy as BuilderPolicy } from "@/components/ui/PolicyBuilder";
import { getPolicy, updatePolicy, publishPolicy } from "@/lib/bff-client";
import { Skeleton } from "@/components/ui/Loading/Skeleton";

export default function EditPolicyPage() {
  const params = useParams<{ policyId: string }>();
  const router = useRouter();
  const [initialPolicy, setInitialPolicy] = React.useState<BuilderPolicy | null>(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState<string | null>(null);

  React.useEffect(() => {
    async function load() {
      try {
        const data = await getPolicy(params.policyId);
        setInitialPolicy({
          id: data.policyId || params.policyId,
          name: data.name || "",
          description: data.description || "",
          version: (data.version as number) ?? 1,
          steps: (data.steps as unknown as BuilderPolicy["steps"]) || [],
          scoringConfig: (data.scoringConfig as unknown as BuilderPolicy["scoringConfig"]) || {
            strategy: "WEIGHTED_AVERAGE",
            tiers: [
              { name: "LOW_RISK", lowerBound: 80, upperBound: 100, decision: "APPROVE" },
              { name: "MEDIUM_RISK", lowerBound: 50, upperBound: 79, decision: "MANUAL_REVIEW" },
              { name: "HIGH_RISK", lowerBound: 0, upperBound: 49, decision: "REJECT" },
            ],
            overrideRules: [],
          },
          status: ((data.status || "draft").toLowerCase()) as BuilderPolicy["status"],
          createdAt: data.createdAt ? new Date(data.createdAt) : new Date(),
          updatedAt: data.updatedAt ? new Date(data.updatedAt) : new Date(),
          createdBy: "current-user",
        });
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to load policy");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [params.policyId]);

  const handleSave = async (policy: BuilderPolicy) => {
    try {
      await updatePolicy(params.policyId, {
        name: policy.name,
        description: policy.description,
        steps: policy.steps as unknown as Array<Record<string, unknown>>,
        scoringConfig: policy.scoringConfig as unknown as import("@/lib/bff-client").RiskConfig,
      });
    } catch (err) {
      alert(`Failed to save policy: ${err instanceof Error ? err.message : "Unknown error"}`);
    }
  };

  const handlePublish = async (policy: BuilderPolicy) => {
    try {
      await updatePolicy(params.policyId, {
        name: policy.name,
        description: policy.description,
        steps: policy.steps as unknown as Array<Record<string, unknown>>,
        scoringConfig: policy.scoringConfig as unknown as import("@/lib/bff-client").RiskConfig,
      });
      await publishPolicy(params.policyId);
      router.push("/policies");
    } catch (err) {
      alert(`Failed to publish policy: ${err instanceof Error ? err.message : "Unknown error"}`);
    }
  };

  if (loading) {
    return (
      <div className="p-8 space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-4 w-32" />
        <Skeleton className="h-96 w-full" />
      </div>
    );
  }

  if (error || !initialPolicy) {
    return (
      <div className="p-8">
        <div className="rounded border border-danger/40 bg-danger/5 px-4 py-3 text-sm text-danger">
          {error || "Policy not found"}
        </div>
      </div>
    );
  }

  return (
    <div className="h-[calc(100vh-3.5rem)]">
      <PolicyBuilder
        initialPolicy={initialPolicy}
        onSave={handleSave}
        onPublish={handlePublish}
      />
    </div>
  );
}
