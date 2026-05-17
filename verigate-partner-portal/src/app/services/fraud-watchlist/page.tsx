"use client";

import { useState } from "react";
import dynamic from "next/dynamic";
import { AlertTriangle, Clock, Upload } from "lucide-react";

const FraudWatchlistScreening = dynamic(() => import("@/components/services/FraudWatchlistScreening.client"), { ssr: false });
const FraudWatchlistHistory = dynamic(() => import("@/components/services/fraud-watchlist/FraudWatchlistHistory"), { ssr: false });
const BatchUploadPlaceholder = dynamic(() => import("@/components/services/shared/BatchUploadPlaceholder"), { ssr: false });
import { SlaIndicator } from "@/components/services/shared/SlaIndicator";

type Tab = "verify" | "history" | "batch";

const tabs: { id: Tab; label: string; icon: typeof AlertTriangle }[] = [
  { id: "verify", label: "Verify", icon: AlertTriangle },
  { id: "history", label: "History", icon: Clock },
  { id: "batch", label: "Batch", icon: Upload },
];

export default function FraudWatchlistPage() {
  const [activeTab, setActiveTab] = useState<Tab>("verify");

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <div className="flex items-center gap-3">
          <h1 className="text-xl font-semibold text-text">Fraud watchlist screening</h1>
          <SlaIndicator sla="Real-time" realTime />
        </div>
        <p className="text-sm text-text-muted">
          Check individuals against the South African Fraud Prevention Service (SAFPS) database.
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

      {activeTab === "verify" && <FraudWatchlistScreening />}
      {activeTab === "history" && <FraudWatchlistHistory />}
      {activeTab === "batch" && <BatchUploadPlaceholder serviceName="fraud watchlist screening" />}
    </div>
  );
}
