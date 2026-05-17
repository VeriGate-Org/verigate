"use client";

import { useState } from "react";
import dynamic from "next/dynamic";
import { Building2, Clock, Upload } from "lucide-react";

const CompanyVerification = dynamic(() => import("@/components/services/CompanyVerification.client"), { ssr: false });
const CompanyVerificationHistory = dynamic(() => import("@/components/services/company/CompanyVerificationHistory"), { ssr: false });
const BatchUploadPlaceholder = dynamic(() => import("@/components/services/shared/BatchUploadPlaceholder"), { ssr: false });

type Tab = "verify" | "history" | "batch";

const tabs: { id: Tab; label: string; icon: typeof Building2 }[] = [
  { id: "verify", label: "Verify", icon: Building2 },
  { id: "history", label: "History", icon: Clock },
  { id: "batch", label: "Batch", icon: Upload },
];

export default function CompanyVerificationPage() {
  const [activeTab, setActiveTab] = useState<Tab>("verify");

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">CIPC company & director search</h1>
        <p className="text-sm text-text-muted">
          Pull registration details and current directors for a South African entity.
        </p>
      </header>

      <div className="border-b border-border">
        <nav className="flex gap-0 -mb-px">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-4 py-3 text-sm font-medium border-b-2 transition-colors ${
                  isActive
                    ? "border-accent text-accent"
                    : "border-transparent text-text-muted hover:text-text hover:border-border"
                }`}
              >
                <Icon className="w-4 h-4" />
                {tab.label}
              </button>
            );
          })}
        </nav>
      </div>

      {activeTab === "verify" && <CompanyVerification />}
      {activeTab === "history" && <CompanyVerificationHistory />}
      {activeTab === "batch" && <BatchUploadPlaceholder serviceName="company verification" />}
    </div>
  );
}
