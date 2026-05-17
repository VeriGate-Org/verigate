"use client";

import { useState } from "react";
import dynamic from "next/dynamic";
import { Newspaper, Clock, Upload } from "lucide-react";

const NegativeNewsScreening = dynamic(() => import("@/components/services/NegativeNewsScreening.client"), { ssr: false });
const NegativeNewsHistory = dynamic(() => import("@/components/services/negative-news/NegativeNewsHistory"), { ssr: false });
const BatchUploadPlaceholder = dynamic(() => import("@/components/services/shared/BatchUploadPlaceholder"), { ssr: false });
import { SlaIndicator } from "@/components/services/shared/SlaIndicator";

type Tab = "verify" | "history" | "batch";

const tabs: { id: Tab; label: string; icon: typeof Newspaper }[] = [
  { id: "verify", label: "Verify", icon: Newspaper },
  { id: "history", label: "History", icon: Clock },
  { id: "batch", label: "Batch", icon: Upload },
];

export default function NegativeNewsPage() {
  const [activeTab, setActiveTab] = useState<Tab>("verify");

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <div className="flex items-center gap-3">
          <h1 className="text-xl font-semibold text-text">Negative news screening</h1>
          <SlaIndicator sla="Real-time" realTime />
        </div>
        <p className="text-sm text-text-muted">
          Screen individuals against news sources for adverse media coverage.
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

      {activeTab === "verify" && <NegativeNewsScreening />}
      {activeTab === "history" && <NegativeNewsHistory />}
      {activeTab === "batch" && <BatchUploadPlaceholder serviceName="negative news screening" />}
    </div>
  );
}
