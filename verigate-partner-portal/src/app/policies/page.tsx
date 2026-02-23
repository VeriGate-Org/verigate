"use client";

import * as React from "react";
import { PolicyBuilder } from "@/components/ui/PolicyBuilder";
import type { Policy } from "@/components/ui/PolicyBuilder";

export default function PoliciesPage() {
  const handleSave = (policy: Policy) => {
    console.log("Saving policy:", policy);
    // In production, this would call an API
  };

  const handlePublish = (policy: Policy) => {
    console.log("Publishing policy:", policy);
    // In production, this would call an API
  };

  return (
    <div className="h-screen pt-14 pl-60">
      <PolicyBuilder onSave={handleSave} onPublish={handlePublish} />
    </div>
  );
}
