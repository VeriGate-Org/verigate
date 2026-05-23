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
  Banknote,
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

interface IncomeResult {
  runId: string;
  verifiedIncome: string;
  employmentConfirmed: boolean;
  incomeBand: string;
  payslipMatch: boolean;
  meta: [string, string][];
}

interface IncomeHistoryRow {
  id: string;
  subject: string;
  idNumber: string;
  employer: string;
  verifiedIncome: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  verifiedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: IncomeResult = {
  runId: "INC-2026-0065",
  verifiedIncome: "R 42,500.00",
  employmentConfirmed: true,
  incomeBand: "R 35,001 - R 50,000",
  payslipMatch: true,
  meta: [
    ["Full name", "Naledi Nkosi"],
    ["ID Number", "9304117500084"],
    ["Employer", "Discovery Health (Pty) Ltd"],
    ["Monthly gross declared", "R 42,000.00"],
  ],
};

const DEMO_HISTORY: IncomeHistoryRow[] = [
  { id: "INC-2026-0064", subject: "Thabo Mokoena", idNumber: "8806125800086", employer: "MTN SA", verifiedIncome: "R 35,200.00", status: "success", statusLabel: "Confirmed", verifiedAt: "2026-05-18T14:55:00Z" },
  { id: "INC-2026-0063", subject: "Jane Smith", idNumber: "9001015800087", employer: "Accenture SA", verifiedIncome: "R 58,000.00", status: "success", statusLabel: "Confirmed", verifiedAt: "2026-05-18T12:20:00Z" },
  { id: "INC-2026-0062", subject: "Sipho Dlamini", idNumber: "8211056500088", employer: "Vodacom", verifiedIncome: "R 28,000.00", status: "warning", statusLabel: "Discrepancy", verifiedAt: "2026-05-18T10:10:00Z" },
  { id: "INC-2026-0061", subject: "Anele Zulu", idNumber: "9108235800083", employer: "Shoprite Holdings", verifiedIncome: "R 18,500.00", status: "success", statusLabel: "Confirmed", verifiedAt: "2026-05-17T16:40:00Z" },
  { id: "INC-2026-0060", subject: "Pieter Botha", idNumber: "8511256100089", employer: "Old Mutual", verifiedIncome: "N/A", status: "danger", statusLabel: "Not found", verifiedAt: "2026-05-17T11:05:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<IncomeHistoryRow, unknown>[] = [
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
    accessorKey: "employer",
    header: "Employer",
  },
  {
    accessorKey: "verifiedIncome",
    header: "Verified Income",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => (
      <Badge variant={row.original.status}>{row.original.statusLabel}</Badge>
    ),
  },
  {
    accessorKey: "verifiedAt",
    header: "Verified",
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
  result: IncomeResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter subject and employer details on the left to verify income against payroll records."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Verifying income...
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
                Income verification result
              </span>
              <Badge variant={result.employmentConfirmed ? "success" : "danger"}>
                {result.employmentConfirmed ? "Confirmed" : "Not confirmed"}
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

      <Card>
        <CardHeader>
          <span className="text-[13px] font-semibold">Verification details</span>
        </CardHeader>
        <div>
          {[
            { label: "Verified income", value: result.verifiedIncome, pass: true },
            { label: "Employment confirmed", value: result.employmentConfirmed ? "Yes" : "No", pass: result.employmentConfirmed },
            { label: "Income band", value: result.incomeBand, pass: true },
            { label: "Payslip match", value: result.payslipMatch ? "Yes" : "No", pass: result.payslipMatch },
          ].map((row) => (
            <div
              key={row.label}
              className="flex items-center gap-2.5 px-4 py-2.5 border-b border-[#f1f5f9] last:border-b-0 text-xs"
            >
              {row.pass ? (
                <CheckCircle size={14} className="text-[#2C974B] shrink-0" />
              ) : (
                <X size={14} className="text-[#E23D36] shrink-0" />
              )}
              <span className="flex-1 text-text">{row.label}</span>
              <span className="font-mono text-text-muted">{row.value}</span>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Main page                                                          */
/* ------------------------------------------------------------------ */

export function IncomePage() {
  const [tab, setTab] = useState("new");
  const [idNumber, setIdNumber] = useState("");
  const [employerName, setEmployerName] = useState("");
  const [monthlyGross, setMonthlyGross] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<IncomeResult | null>(null);

  const isValid = useMemo(
    () =>
      /^\d{13}$/.test(idNumber) &&
      employerName.trim().length >= 2 &&
      monthlyGross.length > 0,
    [idNumber, employerName, monthlyGross],
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
    { label: "New verification", value: "new" },
    { label: "History", value: "history", count: DEMO_HISTORY.length },
  ];

  return (
    <ServicePageLayout
      category="Employment & Income"
      title="Income Verification"
      description="Verify declared income against employer payroll records. Confirm employment status, income band, and payslip accuracy."
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
                  label="Employer name *"
                  placeholder="e.g. Discovery Health (Pty) Ltd"
                  value={employerName}
                  onChange={(e) => setEmployerName(e.target.value)}
                />

                <Input
                  label="Monthly gross (ZAR) *"
                  placeholder="e.g. 42000"
                  value={monthlyGross}
                  onChange={(e) =>
                    setMonthlyGross(e.target.value.replace(/\D/g, ""))
                  }
                  hint="Declared monthly gross income in ZAR."
                  className="font-mono"
                />

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 8.00 per lookup -- result in ~4 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!isValid || status === "loading"}
                    icon={<Banknote size={13} />}
                  >
                    {status === "loading" ? "Verifying..." : "Verify income"}
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
                Verification history
              </div>
              <div className="text-[11px] text-text-muted mt-0.5">
                {DEMO_HISTORY.length} past verifications
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
