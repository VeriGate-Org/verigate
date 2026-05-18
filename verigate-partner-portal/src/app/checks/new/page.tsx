"use client";

import { useState, useCallback } from "react";
import dynamic from "next/dynamic";
import { ClipboardCheck, Clock } from "lucide-react";

const NewCheckForm = dynamic(() => import("@/components/services/checks/NewCheckForm"), { ssr: false });
const CheckHistory = dynamic(() => import("@/components/services/checks/CheckHistory"), { ssr: false });

type Tab = "new" | "history";

const tabs: { id: Tab; label: string; icon: typeof ClipboardCheck }[] = [
  { id: "new", label: "New", icon: ClipboardCheck },
  { id: "history", label: "History", icon: Clock },
];

export default function ChecksPage() {
  const [activeTab, setActiveTab] = useState<Tab>("new");
  const [prefillSubject, setPrefillSubject] = useState<{ idNumber: string; firstName: string; lastName: string } | undefined>();
  const [prefillChecks, setPrefillChecks] = useState<string[] | undefined>();

  const handleRerun = useCallback(
    (subject: { idNumber: string; firstName: string; lastName: string }, checks: string[]) => {
      setPrefillSubject(subject);
      setPrefillChecks(checks);
      setActiveTab("new");
    },
    [],
  );

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <div className="flex items-center gap-3">
          <ClipboardCheck className="h-6 w-6 text-[color:var(--color-accent-strong)]" />
          <h1 className="text-xl font-bold text-text">Checks</h1>
        </div>
        <p className="text-sm text-text-muted">
          Run multiple checks for a subject at once.
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

      {activeTab === "new" && (
        <NewCheckForm prefillSubject={prefillSubject} prefillChecks={prefillChecks} />
      )}
      {activeTab === "history" && <CheckHistory onRerun={handleRerun} />}
    </div>
  );
}
