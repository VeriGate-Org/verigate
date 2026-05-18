"use client";

import { useState } from "react";
import dynamic from "next/dynamic";
import { FileCheck, Clock, Upload } from "lucide-react";
import { SlaIndicator } from "@/components/services/shared/SlaIndicator";

const DocumentVerification = dynamic(() => import("@/components/services/DocumentVerification.client"), { ssr: false });
const DocumentVerificationHistory = dynamic(() => import("@/components/services/document-verification/DocumentVerificationHistory"), { ssr: false });
const BulkDocumentVerification = dynamic(() => import("@/components/services/document-verification/BulkDocumentVerification"), { ssr: false });

type Tab = "verify" | "history" | "batch";

const tabs: { id: Tab; label: string; icon: typeof FileCheck }[] = [
  { id: "verify", label: "Verify", icon: FileCheck },
  { id: "history", label: "History", icon: Clock },
  { id: "batch", label: "Batch", icon: Upload },
];

export default function DocumentVerificationPage() {
  const [activeTab, setActiveTab] = useState<Tab>("verify");

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <div className="flex items-center gap-3">
          <h1 className="text-xl font-semibold text-text">
            Document verification
          </h1>
          <SlaIndicator sla="Real-time" realTime />
        </div>
        <p className="text-sm text-text-muted">
          Verify documents using AI-powered analysis or submit permits to DHA for registry lookup.
        </p>
      </header>

      {/* Tab bar */}
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

      {/* Tab content */}
      {activeTab === "verify" && <DocumentVerification />}
      {activeTab === "history" && <DocumentVerificationHistory />}
      {activeTab === "batch" && <BulkDocumentVerification />}
    </div>
  );
}
