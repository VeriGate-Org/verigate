"use client";

import { useState, useMemo, useCallback } from "react";
import { ServicePageLayout } from "../ServicePageLayout";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { DataTable } from "@/components/ui/DataTable";
import { EmptyState } from "@/components/ui/EmptyState";
import { Skeleton } from "@/components/ui/Skeleton";
import { cn } from "@/lib/cn";
import {
  Newspaper,
  CheckCircle,
  X,
  Download,
  FileSearch,
  Loader2,
  AlertTriangle,
} from "lucide-react";
import type { ColumnDef } from "@tanstack/react-table";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface NewsSource {
  name: string;
  date: string;
  headline: string;
}

interface NegativeNewsResult {
  runId: string;
  articlesFound: number;
  riskLevel: "Low" | "Medium" | "High";
  sources: NewsSource[];
  mostRecentDate: string;
  meta: [string, string][];
}

interface NegativeNewsHistoryRow {
  id: string;
  subject: string;
  articlesFound: number;
  riskLevel: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  screenedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: NegativeNewsResult = {
  runId: "NEG-2026-0019",
  articlesFound: 3,
  riskLevel: "Medium",
  sources: [
    { name: "Daily Maverick", date: "2026-04-12", headline: "Former executive linked to procurement irregularities at state entity" },
    { name: "News24", date: "2026-02-08", headline: "CIPC investigation into company directorship disclosures" },
    { name: "Business Day", date: "2025-11-20", headline: "Board member named in forensic audit report" },
  ],
  mostRecentDate: "2026-04-12",
  meta: [
    ["Full name", "Mandla Tshabalala"],
    ["ID Number", "7304180500081"],
    ["Search scope", "SA media + global"],
    ["Time range", "Last 24 months"],
  ],
};

const DEMO_HISTORY: NegativeNewsHistoryRow[] = [
  { id: "NEG-2026-0018", subject: "Jane Smith", articlesFound: 0, riskLevel: "Low", status: "success", statusLabel: "Clear", screenedAt: "2026-05-18T15:00:00Z" },
  { id: "NEG-2026-0017", subject: "Sipho Dlamini", articlesFound: 5, riskLevel: "High", status: "danger", statusLabel: "High risk", screenedAt: "2026-05-18T13:30:00Z" },
  { id: "NEG-2026-0016", subject: "Lerato Mokoena", articlesFound: 1, riskLevel: "Low", status: "success", statusLabel: "Clear", screenedAt: "2026-05-18T10:15:00Z" },
  { id: "NEG-2026-0015", subject: "Pieter van der Merwe", articlesFound: 2, riskLevel: "Medium", status: "warning", statusLabel: "Review", screenedAt: "2026-05-17T16:45:00Z" },
  { id: "NEG-2026-0014", subject: "Naledi Nkosi", articlesFound: 0, riskLevel: "Low", status: "success", statusLabel: "Clear", screenedAt: "2026-05-17T11:10:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<NegativeNewsHistoryRow, unknown>[] = [
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
    accessorKey: "articlesFound",
    header: "Articles",
    cell: ({ getValue }) => {
      const val = getValue<number>();
      return (
        <span
          className={cn(
            "font-mono font-semibold",
            val === 0 ? "text-[#2C974B]" : val >= 4 ? "text-[#E23D36]" : "text-[#C28B0B]",
          )}
        >
          {val}
        </span>
      );
    },
  },
  {
    accessorKey: "riskLevel",
    header: "Risk Level",
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
];

/* ------------------------------------------------------------------ */
/*  Result panel                                                       */
/* ------------------------------------------------------------------ */

function ResultPanel({
  status,
  result,
}: {
  status: "idle" | "loading" | "result";
  result: NegativeNewsResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter a full name (and optional ID number) on the left to screen for negative news coverage."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Scanning news sources...
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

  const clean = result.articlesFound === 0;
  const riskVariant: "success" | "warning" | "danger" =
    result.riskLevel === "Low" ? "success" : result.riskLevel === "Medium" ? "warning" : "danger";

  return (
    <div className="space-y-3">
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
                Negative news screening result
              </span>
              <Badge variant={riskVariant}>
                {result.riskLevel} risk
              </Badge>
            </div>
            <span className="font-mono text-xs text-accent">
              {result.runId}
            </span>
          </div>
          <Button variant="secondary" size="sm" icon={<Download size={12} />}>
            Export PDF
          </Button>
        </div>

        <div className="grid grid-cols-2 border-t border-border">
          {result.meta.map(([key, val], i) => (
            <div
              key={key}
              className={cn(
                "px-4 py-2.5",
                i >= 2 && "border-t border-[#f1f5f9]",
                i % 2 === 1 && "border-l border-border",
              )}
            >
              <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">
                {key}
              </div>
              <div className="text-[13px] font-medium text-text mt-0.5">
                {val}
              </div>
            </div>
          ))}
        </div>
      </Card>

      {/* Summary stats */}
      <Card>
        <CardHeader>
          <span className="text-[13px] font-semibold">Screening summary</span>
        </CardHeader>
        <div>
          {[
            { label: "Articles found", value: String(result.articlesFound), pass: clean },
            { label: "Risk level", value: result.riskLevel, pass: result.riskLevel === "Low" },
            { label: "Most recent article", value: result.mostRecentDate || "N/A", pass: clean },
          ].map((row) => (
            <div
              key={row.label}
              className="flex items-center gap-2.5 px-4 py-2.5 border-b border-[#f1f5f9] last:border-b-0 text-xs"
            >
              {row.pass ? (
                <CheckCircle size={14} className="text-[#2C974B] shrink-0" />
              ) : (
                <AlertTriangle size={14} className="text-[#C28B0B] shrink-0" />
              )}
              <span className="flex-1 text-text">{row.label}</span>
              <span className="font-mono text-text-muted">{row.value}</span>
            </div>
          ))}
        </div>
      </Card>

      {/* Sources list */}
      {result.sources.length > 0 && (
        <Card>
          <CardHeader>
            <span className="text-[13px] font-semibold">
              Sources ({result.sources.length})
            </span>
          </CardHeader>
          <div>
            {result.sources.map((s, i) => (
              <div
                key={i}
                className={cn("px-4 py-3", i > 0 && "border-t border-[#f1f5f9]")}
              >
                <div className="flex items-center justify-between mb-1">
                  <span className="text-[13px] font-semibold text-text">
                    {s.name}
                  </span>
                  <span className="text-[11px] font-mono text-text-muted">
                    {s.date}
                  </span>
                </div>
                <p className="text-xs text-text-muted leading-relaxed">
                  {s.headline}
                </p>
              </div>
            ))}
          </div>
        </Card>
      )}
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Main page                                                          */
/* ------------------------------------------------------------------ */

export function NegativeNewsPage() {
  const [tab, setTab] = useState("new");
  const [fullName, setFullName] = useState("");
  const [idNumber, setIdNumber] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<NegativeNewsResult | null>(null);

  const isValid = useMemo(
    () => fullName.trim().length >= 2,
    [fullName],
  );

  const handleSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      if (!isValid) return;
      setStatus("loading");
      const timer = setTimeout(() => {
        setResult(DEMO_RESULT);
        setStatus("result");
      }, 1200);
      return () => clearTimeout(timer);
    },
    [isValid],
  );

  const tabs = [
    { label: "New screening", value: "new" },
    { label: "History", value: "history", count: DEMO_HISTORY.length },
  ];

  return (
    <ServicePageLayout
      category="Screening"
      title="Negative News Screening"
      description="Screen individuals or entities against local and global news sources. Identifies adverse media coverage, fraud allegations, and reputational risks."
      tabs={tabs}
      activeTab={tab}
      onTabChange={setTab}
    >
      {tab === "new" && (
        <div className="grid grid-cols-1 lg:grid-cols-[minmax(0,440px)_minmax(0,1fr)] gap-4 items-start">
          <Card>
            <CardHeader>
              <div>
                <div className="text-sm font-semibold text-text">
                  Subject details
                </div>
                <div className="text-[11px] text-text-muted mt-0.5">
                  Required fields are marked with{" "}
                  <span className="text-[#E23D36]">*</span>.
                </div>
              </div>
            </CardHeader>
            <CardBody>
              <form onSubmit={handleSubmit} className="space-y-4">
                <Input
                  label="Full name *"
                  placeholder="e.g. Mandla Tshabalala"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                />

                <Input
                  label="ID Number (optional)"
                  placeholder="13-digit SA ID number"
                  value={idNumber}
                  onChange={(e) =>
                    setIdNumber(e.target.value.replace(/\D/g, "").slice(0, 13))
                  }
                  hint="Providing an ID number improves result accuracy."
                  className="font-mono"
                />

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 4.50 per lookup -- result in ~6 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!isValid || status === "loading"}
                    icon={<Newspaper size={13} />}
                  >
                    {status === "loading" ? "Screening..." : "Screen subject"}
                  </Button>
                </div>
              </form>
            </CardBody>
          </Card>

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
                {DEMO_HISTORY.length} past screenings
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
