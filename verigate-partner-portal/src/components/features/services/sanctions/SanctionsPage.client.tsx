"use client";

import { useState, useCallback } from "react";
import { ServicePageLayout } from "../ServicePageLayout";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { DataTable } from "@/components/ui/DataTable";
import { EmptyState } from "@/components/ui/EmptyState";
import { Skeleton } from "@/components/ui/Skeleton";
import { cn } from "@/lib/cn";
import {
  ShieldAlert,
  Download,
  CheckCircle,
  Loader2,
  ChevronDown,
  Settings,
} from "lucide-react";
import type { ColumnDef } from "@tanstack/react-table";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface SanctionsMatch {
  id: string;
  score: number;
  caption: string;
  schema: string;
  datasets: string[];
  topics: string[];
  properties: Record<string, string>;
}

interface SanctionsResult {
  correlationId: string;
  provider: string;
  dataset: string;
  totalMatches: number;
  outcome: string;
  matches: SanctionsMatch[];
}

interface SanctionsHistoryRow {
  id: string;
  subject: string;
  entityType: string;
  dataset: string;
  matches: number;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  screenedAt: string;
  actor: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: SanctionsResult = {
  correlationId: "SC-2026-0422",
  provider: "OpenSanctions",
  dataset: "sanctions+pep",
  totalMatches: 2,
  outcome: "SUCCEEDED",
  matches: [
    {
      id: "NK-1234567",
      score: 0.92,
      caption: "Mandla Sipho Tshabalala",
      schema: "Person",
      datasets: ["SA PEP Register", "EU Consolidated"],
      topics: ["role.pep"],
      properties: {
        "Birth date": "1973-04-18",
        Nationality: "South African",
        Position: "Former Deputy Minister of Finance (2014-2017)",
        Sources: "SA Government Gazette, EU Pol Pers DB",
      },
    },
    {
      id: "OFAC-456-A",
      score: 0.71,
      caption: "M. Tshabalala",
      schema: "Person",
      datasets: ["OFAC SDN"],
      topics: ["sanction"],
      properties: {
        "Birth date": "~1973",
        Nationality: "Unknown",
        Listing: "SDN List -- Added 2022-03-14",
        Sources: "US Treasury OFAC",
      },
    },
  ],
};

const CLEAR_RESULT: SanctionsResult = {
  ...DEMO_RESULT,
  correlationId: "SC-2026-0423",
  totalMatches: 0,
  matches: [],
};

const DEMO_HISTORY: SanctionsHistoryRow[] = [
  { id: "SC-2026-0421", subject: "Acme Corp Ltd", entityType: "Company", dataset: "sanctions", matches: 2, status: "warning", statusLabel: "Review", screenedAt: "2026-05-18T13:18:00Z", actor: "Sipho Dlamini" },
  { id: "SC-2026-0420", subject: "Jane Smith", entityType: "Person", dataset: "sanctions+pep", matches: 0, status: "success", statusLabel: "Clear", screenedAt: "2026-05-18T11:42:00Z", actor: "Naledi Nkosi" },
  { id: "SC-2026-0419", subject: "Mandla Tshabalala", entityType: "Person", dataset: "sanctions+pep", matches: 1, status: "warning", statusLabel: "PEP found", screenedAt: "2026-05-18T10:08:00Z", actor: "Arthur Manena" },
  { id: "SC-2026-0418", subject: "MV Stellenbosch", entityType: "Vessel", dataset: "sanctions", matches: 0, status: "success", statusLabel: "Clear", screenedAt: "2026-05-17T16:22:00Z", actor: "System" },
  { id: "SC-2026-0417", subject: "Naledi Nkosi", entityType: "Person", dataset: "sanctions+pep", matches: 0, status: "success", statusLabel: "Clear", screenedAt: "2026-05-17T14:05:00Z", actor: "Naledi Nkosi" },
  { id: "SC-2026-0416", subject: "Pyongyang Trading", entityType: "Company", dataset: "sanctions", matches: 3, status: "danger", statusLabel: "Match", screenedAt: "2026-05-17T09:30:00Z", actor: "Sipho Dlamini" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<SanctionsHistoryRow, unknown>[] = [
  {
    accessorKey: "id",
    header: "ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "subject",
    header: "Subject",
    cell: ({ getValue }) => (
      <span className="font-medium">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "entityType",
    header: "Type",
  },
  {
    accessorKey: "dataset",
    header: "Dataset",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-text-muted">
        {getValue<string>()}
      </span>
    ),
  },
  {
    accessorKey: "matches",
    header: "Matches",
    cell: ({ getValue }) => {
      const val = getValue<number>();
      return (
        <span
          className={cn(
            "font-mono font-semibold",
            val === 0
              ? "text-[#2C974B]"
              : val >= 3
                ? "text-[#E23D36]"
                : "text-[#C28B0B]",
          )}
        >
          {val}
        </span>
      );
    },
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => (
      <Badge variant={row.original.status}>{row.original.statusLabel}</Badge>
    ),
  },
  {
    accessorKey: "screenedAt",
    header: "Screened",
    cell: ({ getValue }) =>
      new Date(getValue<string>()).toLocaleString("en-ZA", {
        dateStyle: "short",
        timeStyle: "short",
      }),
  },
  {
    accessorKey: "actor",
    header: "Actor",
  },
];

/* ------------------------------------------------------------------ */
/*  Result panel                                                       */
/* ------------------------------------------------------------------ */

function ResultPanel({
  status,
  result,
}: {
  status: "idle" | "loading" | "result";
  result: SanctionsResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={ShieldAlert}
        title="No results yet"
        body="Enter subject details on the left and click Screen to check OpenSanctions for matches."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Screening against OpenSanctions...
          </div>
          <div className="space-y-3">
            <Skeleton className="h-3 w-4/5" />
            <Skeleton className="h-3 w-3/5" />
            <Skeleton className="h-3 w-[70%]" />
            <Skeleton className="h-3 w-2/4" />
          </div>
        </CardBody>
      </Card>
    );
  }

  if (!result) return null;

  const clean = result.totalMatches === 0;

  return (
    <div className="space-y-3">
      {/* Summary */}
      <Card>
        <div className="h-[3px] flex">
          <div className="flex-[3] bg-[#E23D36]" />
          <div className="flex-[5] bg-[#1A2E4B]" />
          <div className="flex-[2] bg-accent" />
        </div>
        <div className="p-4 flex items-start justify-between">
          <div>
            <div className="flex items-center gap-2.5 mb-1">
              <span className="text-sm font-semibold text-text">
                Screening summary
              </span>
              <Badge variant={clean ? "success" : result.totalMatches >= 3 ? "danger" : "warning"}>
                {clean ? "Clear" : `${result.totalMatches} match${result.totalMatches > 1 ? "es" : ""}`}
              </Badge>
            </div>
            <span className="font-mono text-xs text-accent">
              {result.correlationId}
            </span>
          </div>
          <Button variant="secondary" size="sm" icon={<Download size={12} />}>
            Export PDF
          </Button>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-4 border-t border-border">
          {[
            { label: "Provider", value: result.provider },
            { label: "Dataset", value: result.dataset, mono: true },
            { label: "Matches", value: String(result.totalMatches), tone: clean ? "#2C974B" : "#E23D36" },
            { label: "Outcome", value: result.outcome, mono: true },
          ].map((c, i) => (
            <div key={c.label} className={cn("px-4 py-3", i > 0 && "border-l border-border")}>
              <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">
                {c.label}
              </div>
              <div
                className={cn(
                  "text-sm font-semibold mt-1",
                  c.mono && "font-mono",
                )}
                style={c.tone ? { color: c.tone } : undefined}
              >
                {c.value}
              </div>
            </div>
          ))}
        </div>
      </Card>

      {/* Clear or match details */}
      {clean ? (
        <div className="flex items-center gap-3.5 p-5 rounded-aws-container border border-[rgba(44,151,75,0.2)] bg-[rgba(44,151,75,0.04)]">
          <div className="w-12 h-12 rounded-full bg-[#2C974B] flex items-center justify-center shrink-0 shadow-[0_0_16px_rgba(44,151,75,0.4)]">
            <CheckCircle size={26} className="text-white" />
          </div>
          <div>
            <div className="text-sm font-semibold text-[#2C974B]">
              No matches found
            </div>
            <div className="text-xs text-text-muted mt-1">
              This subject is clear against OpenSanctions and PEP datasets. You may proceed with onboarding.
            </div>
          </div>
        </div>
      ) : (
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between w-full">
              <span className="text-[13px] font-semibold">
                Match entities ({result.matches.length})
              </span>
              <span className="text-[11px] text-text-muted">
                Sorted by score, descending
              </span>
            </div>
          </CardHeader>
          <div>
            {result.matches.map((m, i) => (
              <div
                key={m.id}
                className={cn("px-4 py-3.5", i > 0 && "border-t border-[#f1f5f9]")}
              >
                {/* Header */}
                <div className="flex items-start justify-between gap-3 mb-2.5">
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-sm font-semibold text-text">
                        {m.caption}
                      </span>
                      <span className="font-mono text-[10px] text-text-muted">
                        {m.id}
                      </span>
                    </div>
                    <div className="flex gap-1.5 flex-wrap">
                      {m.topics.map((t) => (
                        <Badge key={t} variant={t === "sanction" ? "danger" : "warning"} size="sm">
                          {t === "sanction" ? "Sanction" : "PEP"}
                        </Badge>
                      ))}
                      {m.datasets.map((d) => (
                        <span
                          key={d}
                          className="text-[10px] px-2 py-0.5 bg-[#F2F3F3] rounded font-mono text-text-muted"
                        >
                          {d}
                        </span>
                      ))}
                    </div>
                  </div>
                  <div className="text-right shrink-0">
                    <div
                      className={cn(
                        "text-xl font-bold leading-none",
                        m.score >= 0.85 ? "text-[#E23D36]" : "text-[#C28B0B]",
                      )}
                    >
                      {Math.round(m.score * 100)}
                      <span className="text-xs font-normal text-text-muted">
                        /100
                      </span>
                    </div>
                    <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted mt-0.5">
                      Match score
                    </div>
                  </div>
                </div>

                {/* Properties */}
                <div className="bg-surface-alt rounded p-3 grid grid-cols-1 sm:grid-cols-2 gap-1">
                  {Object.entries(m.properties).map(([k, v]) => (
                    <div key={k} className="text-[11px] flex gap-1.5">
                      <span className="text-text-muted capitalize">{k}:</span>
                      <span className="font-medium text-text">{v}</span>
                    </div>
                  ))}
                </div>

                {/* Actions */}
                <div className="flex justify-end gap-1.5 mt-2.5">
                  <Button variant="ghost" size="sm">
                    View entity
                  </Button>
                  <Button variant="secondary" size="sm">
                    Dismiss as false positive
                  </Button>
                  <Button variant="destructive" size="sm">
                    Block onboarding
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Main Sanctions page                                                */
/* ------------------------------------------------------------------ */

const ENTITY_TYPE_OPTIONS = [
  { value: "person", label: "Person" },
  { value: "company", label: "Company" },
  { value: "organization", label: "Organization" },
  { value: "vessel", label: "Vessel" },
];

const COUNTRY_OPTIONS = [
  { value: "", label: "-- Any --" },
  { value: "ZA", label: "South Africa" },
  { value: "US", label: "United States" },
  { value: "GB", label: "United Kingdom" },
  { value: "EU", label: "European Union" },
  { value: "KP", label: "North Korea" },
  { value: "IR", label: "Iran" },
  { value: "RU", label: "Russia" },
];

export function SanctionsPage() {
  const [tab, setTab] = useState("new");
  const [entityName, setEntityName] = useState("");
  const [entityType, setEntityType] = useState("person");
  const [country, setCountry] = useState("");
  const [advancedOpen, setAdvancedOpen] = useState(false);
  const [threshold, setThreshold] = useState(0.7);
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<SanctionsResult | null>(null);

  const canSubmit = entityName.trim().length >= 2;

  const handleSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      if (!canSubmit) return;
      setStatus("loading");
      const timer = setTimeout(() => {
        // Demo: if name contains "tshabalala" or "pyongyang" -> matches
        const hasMatch = /tshabalala|pyongyang/i.test(entityName);
        setResult(hasMatch ? DEMO_RESULT : CLEAR_RESULT);
        setStatus("result");
      }, 1200);
      return () => clearTimeout(timer);
    },
    [canSubmit, entityName],
  );

  const tabs = [
    { label: "New screening", value: "new" },
    { label: "History", value: "history", count: DEMO_HISTORY.length },
  ];

  return (
    <ServicePageLayout
      category="Screening"
      title="Sanctions & PEP Screening"
      description="Screen entities against OpenSanctions, PEP registers, and crime databases. Supports persons, companies, organisations, and vessels."
      tabs={tabs}
      activeTab={tab}
      onTabChange={setTab}
    >
      {tab === "new" && (
        <div className="grid grid-cols-1 lg:grid-cols-[minmax(0,440px)_minmax(0,1fr)] gap-4 items-start">
          {/* Form */}
          <Card>
            <CardHeader>
              <div>
                <div className="text-sm font-semibold text-text">
                  Subject details
                </div>
                <div className="text-[11px] text-text-muted mt-0.5">
                  Select an entity type and enter screening information.
                </div>
              </div>
            </CardHeader>
            <CardBody>
              <form onSubmit={handleSubmit} className="space-y-4">
                <Select
                  label="Entity type"
                  options={ENTITY_TYPE_OPTIONS}
                  value={entityType}
                  onChange={(e) => setEntityType(e.target.value)}
                />

                <Input
                  label="Entity name *"
                  placeholder={
                    entityType === "person"
                      ? "e.g. Mandla Tshabalala"
                      : "e.g. Acme Corp"
                  }
                  value={entityName}
                  onChange={(e) => setEntityName(e.target.value)}
                />

                <Select
                  label="Country"
                  options={COUNTRY_OPTIONS}
                  value={country}
                  onChange={(e) => setCountry(e.target.value)}
                />

                {/* Advanced options */}
                <div className="border border-border rounded overflow-hidden">
                  <button
                    type="button"
                    onClick={() => setAdvancedOpen(!advancedOpen)}
                    className="w-full px-3 py-2.5 bg-surface-alt flex items-center justify-between text-xs font-medium text-text"
                  >
                    <span className="inline-flex items-center gap-1.5">
                      <Settings size={12} className="text-text-muted" />
                      Advanced options
                    </span>
                    <ChevronDown
                      size={12}
                      className={cn(
                        "text-text-muted transition-transform",
                        advancedOpen && "rotate-180",
                      )}
                    />
                  </button>
                  {advancedOpen && (
                    <div className="px-3 py-3 border-t border-border space-y-3">
                      <div>
                        <label className="text-[11px] font-semibold text-text-muted uppercase tracking-wide block mb-1">
                          Match threshold: {threshold.toFixed(2)}
                        </label>
                        <input
                          type="range"
                          min={0}
                          max={1}
                          step={0.01}
                          value={threshold}
                          onChange={(e) => setThreshold(parseFloat(e.target.value))}
                          className="w-full accent-[var(--color-accent)]"
                        />
                        <div className="flex justify-between text-[9px] font-mono text-text-muted mt-0.5">
                          <span>0.00</span>
                          <span>0.50</span>
                          <span>1.00</span>
                        </div>
                      </div>
                    </div>
                  )}
                </div>

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    Screened against <strong className="text-text">OpenSanctions</strong>
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!canSubmit || status === "loading"}
                    icon={<ShieldAlert size={13} />}
                  >
                    {status === "loading" ? "Screening..." : "Screen subject"}
                  </Button>
                </div>
              </form>
            </CardBody>
          </Card>

          {/* Result */}
          <ResultPanel status={status} result={result} />
        </div>
      )}

      {tab === "history" && (
        <Card>
          <CardHeader>
            <div>
              <div className="text-sm font-semibold text-text">
                Screening history
              </div>
              <div className="text-[11px] text-text-muted mt-0.5">
                {DEMO_HISTORY.length} screenings -- last 7 days
              </div>
            </div>
          </CardHeader>
          <CardBody compact>
            <DataTable
              data={DEMO_HISTORY}
              columns={historyColumns}
              pageSize={10}
            />
          </CardBody>
        </Card>
      )}
    </ServicePageLayout>
  );
}
