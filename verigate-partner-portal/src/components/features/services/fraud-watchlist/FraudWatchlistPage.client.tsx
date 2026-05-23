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
  ShieldBan,
  CheckCircle,
  X,
  Download,
  FileSearch,
  Loader2,
} from "lucide-react";
import type { ColumnDef } from "@tanstack/react-table";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface FraudResult {
  runId: string;
  matchFound: boolean;
  matchScore: number;
  listedDate: string;
  reason: string;
  source: string;
  meta: [string, string][];
}

interface FraudHistoryRow {
  id: string;
  subject: string;
  idNumber: string;
  matchFound: boolean;
  matchScore: number;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  screenedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: FraudResult = {
  runId: "FRD-2026-0056",
  matchFound: true,
  matchScore: 87,
  listedDate: "2024-09-14",
  reason: "Application fraud -- forged income documentation",
  source: "SAFPS (SA Fraud Prevention Service)",
  meta: [
    ["Full name", "Sipho Dlamini"],
    ["ID Number", "8211056500088"],
    ["Screening provider", "SAFPS"],
    ["Screened at", "2026-05-19T10:30:00Z"],
  ],
};

const DEMO_HISTORY: FraudHistoryRow[] = [
  { id: "FRD-2026-0055", subject: "Jane Smith", idNumber: "9001015800087", matchFound: false, matchScore: 0, status: "success", statusLabel: "Clear", screenedAt: "2026-05-18T14:45:00Z" },
  { id: "FRD-2026-0054", subject: "Mandla Tshabalala", idNumber: "7304180500081", matchFound: false, matchScore: 0, status: "success", statusLabel: "Clear", screenedAt: "2026-05-18T12:30:00Z" },
  { id: "FRD-2026-0053", subject: "Sipho Dlamini", idNumber: "8211056500088", matchFound: true, matchScore: 87, status: "danger", statusLabel: "Match", screenedAt: "2026-05-18T10:05:00Z" },
  { id: "FRD-2026-0052", subject: "Lerato Mokoena", idNumber: "9501025800082", matchFound: false, matchScore: 0, status: "success", statusLabel: "Clear", screenedAt: "2026-05-17T15:55:00Z" },
  { id: "FRD-2026-0051", subject: "Pieter van der Merwe", idNumber: "8511256100089", matchFound: true, matchScore: 62, status: "warning", statusLabel: "Possible", screenedAt: "2026-05-17T10:20:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<FraudHistoryRow, unknown>[] = [
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
    accessorKey: "idNumber",
    header: "ID Number",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-text-muted">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "matchScore",
    header: "Score",
    cell: ({ getValue }) => {
      const val = getValue<number>();
      return (
        <span
          className={cn(
            "font-mono font-semibold",
            val === 0 ? "text-[#2C974B]" : val >= 80 ? "text-[#E23D36]" : "text-[#C28B0B]",
          )}
        >
          {val > 0 ? `${val}%` : "--"}
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
];

/* ------------------------------------------------------------------ */
/*  Result panel                                                       */
/* ------------------------------------------------------------------ */

function ResultPanel({
  status,
  result,
}: {
  status: "idle" | "loading" | "result";
  result: FraudResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter an ID number and full name on the left to screen against the SAFPS fraud watchlist."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Screening fraud watchlist...
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

  const clean = !result.matchFound;

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
                Fraud watchlist result
              </span>
              <Badge variant={clean ? "success" : "danger"}>
                {clean ? "Clear" : "Match found"}
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

      {clean ? (
        <div className="flex items-center gap-3.5 p-5 rounded-aws-container border border-[rgba(44,151,75,0.2)] bg-[rgba(44,151,75,0.04)]">
          <div className="w-12 h-12 rounded-full bg-[#2C974B] flex items-center justify-center shrink-0 shadow-[0_0_16px_rgba(44,151,75,0.4)]">
            <CheckCircle size={26} className="text-white" />
          </div>
          <div>
            <div className="text-sm font-semibold text-[#2C974B]">
              No watchlist match found
            </div>
            <div className="text-xs text-text-muted mt-1">
              This subject is not listed on the SAFPS fraud database. You may proceed.
            </div>
          </div>
        </div>
      ) : (
        <Card>
          <CardHeader>
            <span className="text-[13px] font-semibold">Match details</span>
          </CardHeader>
          <div>
            {[
              { label: "Match found", value: "Yes", pass: false },
              { label: "Match score", value: `${result.matchScore}%`, pass: false },
              { label: "Listed date", value: result.listedDate, pass: false },
              { label: "Reason", value: result.reason, pass: false },
              { label: "Source", value: result.source, pass: false },
            ].map((row) => (
              <div
                key={row.label}
                className="flex items-center gap-2.5 px-4 py-2.5 border-b border-[#f1f5f9] last:border-b-0 text-xs"
              >
                <X size={14} className="text-[#E23D36] shrink-0" />
                <span className="flex-1 text-text">{row.label}</span>
                <span className="font-mono text-text-muted">{row.value}</span>
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

export function FraudWatchlistPage() {
  const [tab, setTab] = useState("new");
  const [idNumber, setIdNumber] = useState("");
  const [fullName, setFullName] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<FraudResult | null>(null);

  const isValid = useMemo(
    () => /^\d{13}$/.test(idNumber) && fullName.trim().length >= 2,
    [idNumber, fullName],
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
      title="Fraud Watchlist Screening"
      description="Screen individuals against the SA Fraud Prevention Service (SAFPS) database. Detect listed fraudsters, forged documentation, and application fraud."
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
                  label="ID Number *"
                  placeholder="13-digit SA ID number"
                  value={idNumber}
                  onChange={(e) =>
                    setIdNumber(e.target.value.replace(/\D/g, "").slice(0, 13))
                  }
                  hint="The 13-digit South African identity number."
                  error={idNumber.length > 0 && idNumber.length < 13}
                  errorMessage="Must be exactly 13 digits."
                  className="font-mono"
                />

                <Input
                  label="Full name *"
                  placeholder="e.g. Sipho Dlamini"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                />

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 5.50 per lookup -- result in ~2 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!isValid || status === "loading"}
                    icon={<ShieldBan size={13} />}
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
