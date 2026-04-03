"use client";

import { useState } from "react";
import dynamic from "next/dynamic";
import { Shield, Clock, Upload, Eye } from "lucide-react";

const SanctionsCheck = dynamic(() => import("@/components/services/SanctionsCheck.client"), { ssr: false });
const SanctionsHistory = dynamic(() => import("@/components/services/sanctions/SanctionsHistory"), { ssr: false });
const SanctionsBatch = dynamic(() => import("@/components/services/sanctions/SanctionsBatch"), { ssr: false });
const SanctionsMonitoring = dynamic(() => import("@/components/services/sanctions/SanctionsMonitoring"), { ssr: false });

type Tab = "screen" | "history" | "batch" | "monitoring";

const tabs: { id: Tab; label: string; icon: typeof Shield }[] = [
  { id: "screen", label: "Screen", icon: Shield },
  { id: "history", label: "History", icon: Clock },
  { id: "batch", label: "Batch", icon: Upload },
  { id: "monitoring", label: "Monitoring", icon: Eye },
];

export default function SanctionsServicePage() {
  const [activeTab, setActiveTab] = useState<Tab>("screen");

  return (
    <div className="space-y-6">
      {/* Tab bar */}
      <div className="border-b border-gray-200">
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
                    ? "border-blue-600 text-blue-600"
                    : "border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300"
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
      {activeTab === "screen" && <SanctionsCheck />}
      {activeTab === "history" && <SanctionsHistory />}
      {activeTab === "batch" && <SanctionsBatch />}
      {activeTab === "monitoring" && <SanctionsMonitoring />}
    </div>
  );
}
