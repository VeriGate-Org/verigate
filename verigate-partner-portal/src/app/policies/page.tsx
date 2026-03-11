"use client";

import * as React from "react";
import { PolicyBuilder } from "@/components/ui/PolicyBuilder";
import type { Policy } from "@/components/ui/PolicyBuilder";
import { createPolicy, publishPolicy, updatePolicy } from "@/lib/bff-client";
import { useToast } from "@/components/ui/Toast";

export default function PoliciesPage() {
  const { toast } = useToast();
  const [saving, setSaving] = React.useState(false);

  const handleSave = async (policy: Policy) => {
    setSaving(true);
    try {
      const steps = policy.steps.map((s) => ({
        type: s.type,
        name: s.name,
        config: s.config,
        next: s.next,
        onSuccess: s.onSuccess,
        onFail: s.onFail,
        parallel: s.parallel,
      }));
      if (policy.id) {
        await updatePolicy(policy.id, { name: policy.name, description: policy.description, steps });
      } else {
        await createPolicy({ name: policy.name, description: policy.description, steps });
      }
      toast({ title: "Policy saved", description: "Draft saved successfully.", variant: "success" });
    } catch (err) {
      toast({ title: "Save failed", description: err instanceof Error ? err.message : "Could not save policy.", variant: "error" });
    } finally {
      setSaving(false);
    }
  };

  const handlePublish = async (policy: Policy) => {
    if (!policy.id) {
      toast({ title: "Save first", description: "Please save the policy before publishing.", variant: "error" });
      return;
    }
    setSaving(true);
    try {
      await publishPolicy(policy.id);
      toast({ title: "Policy published", description: `Version ${policy.version + 1} is now live.`, variant: "success" });
    } catch (err) {
      toast({ title: "Publish failed", description: err instanceof Error ? err.message : "Could not publish policy.", variant: "error" });
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="h-screen pt-14 pl-60">
      <PolicyBuilder onSave={handleSave} onPublish={handlePublish} />
      {saving && (
        <div className="fixed bottom-4 left-1/2 -translate-x-1/2 rounded-lg border border-border bg-surface px-4 py-2 text-sm text-text-muted shadow-lg">
          Saving…
        </div>
      )}
    </div>
  );
}
