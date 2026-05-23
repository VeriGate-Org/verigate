"use client";

import { useState, useMemo } from "react";
import { useRouter } from "next/navigation";
import { Plus, Download, Layers } from "lucide-react";
import { PageHeader } from "@/components/ui/PageHeader";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { FilterChips } from "@/components/ui/FilterChips";
import { cn } from "@/lib/cn";

/* ── Mock data ───────────────────────────────────────────── */

interface PolicySummary {
  id: string;
  name: string;
  description: string;
  version: number;
  status: "published" | "draft" | "archived";
  steps: number;
  lastRun: string;
  runs: number;
  decision: string;
}

const POLICIES: PolicySummary[] = [
  { id: "pol_onboard_v3",   name: "Standard onboarding",    description: "KYC + sanctions + credit. Used for all new individual customers.",      version: 3, status: "published", steps: 4, lastRun: "2 min ago",   runs: 1842,  decision: "APPROVE 91% \u00b7 REVIEW 7% \u00b7 REJECT 2%" },
  { id: "pol_aml_enhanced",  name: "Enhanced AML screening", description: "PEP + sanctions + adverse media. Applies to high-risk industries.",   version: 2, status: "published", steps: 5, lastRun: "14 min ago",  runs: 412,   decision: "APPROVE 78% \u00b7 REVIEW 18% \u00b7 REJECT 4%" },
  { id: "pol_corp_kyb",      name: "Corporate KYB",          description: "Company + directors + UBO + sanctions. For business onboarding.",      version: 5, status: "published", steps: 6, lastRun: "1 hr ago",    runs: 318,   decision: "APPROVE 84% \u00b7 REVIEW 12% \u00b7 REJECT 4%" },
  { id: "pol_property",      name: "Property transaction",   description: "Deeds + valuation + KYC. For real-estate compliance partners.",        version: 1, status: "draft",     steps: 4, lastRun: "\u2014",      runs: 0,     decision: "\u2014" },
  { id: "pol_gaming",        name: "Gaming registration",    description: "Age + ID + AML lite. For licensed gaming operators.",                   version: 2, status: "published", steps: 3, lastRun: "4 hr ago",    runs: 5024,  decision: "APPROVE 95% \u00b7 REVIEW 3% \u00b7 REJECT 2%" },
  { id: "pol_archived",      name: "Onboarding v2 (legacy)", description: "Replaced by v3 in April 2026.",                                        version: 2, status: "archived",  steps: 3, lastRun: "32 days ago", runs: 18204, decision: "\u2014" },
];

const STATUS_TONES: Record<string, "success" | "warning" | "pending"> = {
  published: "success",
  draft: "warning",
  archived: "pending",
};

const STATUS_LABELS: Record<string, string> = {
  published: "Published",
  draft: "Draft",
  archived: "Archived",
};

const FILTER_CHIPS = [
  { label: "All",       value: "all",       count: POLICIES.length },
  { label: "Published", value: "published", count: POLICIES.filter((p) => p.status === "published").length },
  { label: "Drafts",    value: "draft",     count: POLICIES.filter((p) => p.status === "draft").length },
  { label: "Archived",  value: "archived",  count: POLICIES.filter((p) => p.status === "archived").length },
];

/* ── Component ───────────────────────────────────────────── */

export function PolicyListPage() {
  const router = useRouter();
  const [filter, setFilter] = useState("all");

  const filtered = useMemo(
    () => filter === "all" ? POLICIES : POLICIES.filter((p) => p.status === filter),
    [filter],
  );

  return (
    <div className="space-y-aws-m">
      <PageHeader
        category="Enterprise \u00b7 Policies"
        title="Policy Builder"
        description="Compose verification workflows from service steps, scoring strategies, and override rules. Test before publishing; version every change."
        actions={
          <>
            <Button variant="secondary" icon={<Download size={13} />}>
              Import policy
            </Button>
            <Button
              variant="cta"
              icon={<Plus size={13} />}
              onClick={() => router.push("/policies/pol_new")}
            >
              New policy
            </Button>
          </>
        }
      />

      <FilterChips chips={FILTER_CHIPS} value={filter} onChange={setFilter} />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-3.5">
        {filtered.map((p) => (
          <button
            key={p.id}
            onClick={() => router.push(`/policies/${p.id}`)}
            className={cn(
              "console-card p-5 text-left transition-all duration-200",
              "hover:-translate-y-0.5 hover:shadow-lg hover:border-accent/50",
            )}
          >
            <div className="flex justify-between items-start mb-2">
              <div className="flex items-center gap-2.5">
                <div className="w-9 h-9 rounded-md bg-accent/10 flex items-center justify-center">
                  <Layers size={18} className="text-accent" />
                </div>
                <div>
                  <div className="text-sm font-semibold text-text">{p.name}</div>
                  <div className="text-[11px] font-mono text-text-muted mt-0.5">
                    {p.id} &middot; v{p.version}
                  </div>
                </div>
              </div>
              <Badge variant={STATUS_TONES[p.status]}>
                {STATUS_LABELS[p.status]}
              </Badge>
            </div>

            <div className="text-xs text-text-muted leading-relaxed mb-3 min-h-[38px]">
              {p.description}
            </div>

            <div className="flex justify-between items-center pt-2.5 border-t border-border-light text-[11px] text-text-muted">
              <span>{p.steps} steps &middot; last run {p.lastRun}</span>
              <span className="font-mono">{p.runs.toLocaleString()} runs</span>
            </div>

            {p.decision !== "\u2014" && (
              <div className="mt-2 text-[11px] text-text-muted">
                <span className="font-semibold text-text">Decisions:</span> {p.decision}
              </div>
            )}
          </button>
        ))}
      </div>
    </div>
  );
}
