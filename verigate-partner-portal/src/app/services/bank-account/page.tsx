"use client";

import { useState } from "react";
import dynamic from "next/dynamic";
import { CreditCard, Clock, Upload } from "lucide-react";

const AvsCheck = dynamic(() => import("@/components/services/AvsCheck.client"), { ssr: false });
const BankAccountHistory = dynamic(() => import("@/components/services/bank-account/BankAccountHistory"), { ssr: false });
const BatchUploadPlaceholder = dynamic(() => import("@/components/services/shared/BatchUploadPlaceholder"), { ssr: false });

type Tab = "verify" | "history" | "batch";

const tabs: { id: Tab; label: string; icon: typeof CreditCard }[] = [
  { id: "verify", label: "Verify", icon: CreditCard },
  { id: "history", label: "History", icon: Clock },
  { id: "batch", label: "Batch", icon: Upload },
];

export default function BankAccountServicePage() {
  const [activeTab, setActiveTab] = useState<Tab>("verify");

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Bank account validation</h1>
        <p className="text-sm text-text-muted">
          Validate account ownership across South African banks using AVS.
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

      {activeTab === "verify" && <AvsCheck />}
      {activeTab === "history" && <BankAccountHistory />}
      {activeTab === "batch" && <BatchUploadPlaceholder serviceName="bank account validation" />}
    </div>
  );
}
