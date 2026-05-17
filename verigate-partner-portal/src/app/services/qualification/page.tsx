"use client";

import { useState } from "react";
import dynamic from "next/dynamic";
import { GraduationCap, Clock, Upload } from "lucide-react";

const QualificationVerification = dynamic(() => import("@/components/services/QualificationVerification.client"), { ssr: false });
const QualificationHistory = dynamic(() => import("@/components/services/qualification/QualificationHistory"), { ssr: false });
const BatchUploadPlaceholder = dynamic(() => import("@/components/services/shared/BatchUploadPlaceholder"), { ssr: false });

type Tab = "verify" | "history" | "batch";

const tabs: { id: Tab; label: string; icon: typeof GraduationCap }[] = [
  { id: "verify", label: "Verify", icon: GraduationCap },
  { id: "history", label: "History", icon: Clock },
  { id: "batch", label: "Batch", icon: Upload },
];

export default function QualificationPage() {
  const [activeTab, setActiveTab] = useState<Tab>("verify");

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Qualification verification</h1>
        <p className="text-sm text-text-muted">
          Verify educational qualifications through SAQA and institutional records.
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

      {activeTab === "verify" && <QualificationVerification />}
      {activeTab === "history" && <QualificationHistory />}
      {activeTab === "batch" && <BatchUploadPlaceholder serviceName="qualification verification" />}
    </div>
  );
}
