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
  CreditCard,
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

interface CreditResult {
  runId: string;
  creditScore: number;
  riskGrade: string;
  adverseListings: number;
  paymentProfile: string;
  debtToIncome: number;
  meta: [string, string][];
}

interface CreditHistoryRow {
  id: string;
  subject: string;
  idNumber: string;
  creditScore: number;
  riskGrade: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  checkedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: CreditResult = {
  runId: "CR-2026-0211",
  creditScore: 682,
  riskGrade: "B",
  adverseListings: 1,
  paymentProfile: "Good standing -- 2 late payments in 12 months",
  debtToIncome: 38.5,
  meta: [
    ["Full name", "Mandla Tshabalala"],
    ["ID Number", "7304180500081"],
    ["Bureau", "TransUnion"],
    ["Report date", "2026-05-19"],
  ],
};

const DEMO_HISTORY: CreditHistoryRow[] = [
  { id: "CR-2026-0210", subject: "Jane Smith", idNumber: "9001015800087", creditScore: 745, riskGrade: "A", status: "success", statusLabel: "Low risk", checkedAt: "2026-05-18T15:12:00Z" },
  { id: "CR-2026-0209", subject: "Sipho Dlamini", idNumber: "8211056500088", creditScore: 520, riskGrade: "D", status: "danger", statusLabel: "High risk", checkedAt: "2026-05-18T13:40:00Z" },
  { id: "CR-2026-0208", subject: "Naledi Nkosi", idNumber: "9304117500084", creditScore: 690, riskGrade: "B", status: "success", statusLabel: "Low risk", checkedAt: "2026-05-18T10:22:00Z" },
  { id: "CR-2026-0207", subject: "Pieter van der Merwe", idNumber: "8511256100089", creditScore: 610, riskGrade: "C", status: "warning", statusLabel: "Medium risk", checkedAt: "2026-05-17T16:05:00Z" },
  { id: "CR-2026-0206", subject: "Lerato Mokoena", idNumber: "9501025800082", creditScore: 780, riskGrade: "A", status: "success", statusLabel: "Low risk", checkedAt: "2026-05-17T14:30:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<CreditHistoryRow, unknown>[] = [
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
    accessorKey: "creditScore",
    header: "Score",
    cell: ({ getValue }) => {
      const val = getValue<number>();
      return (
        <span
          className={cn(
            "font-mono font-semibold",
            val >= 700 ? "text-[#2C974B]" : val >= 600 ? "text-[#C28B0B]" : "text-[#E23D36]",
          )}
        >
          {val}
        </span>
      );
    },
  },
  {
    accessorKey: "riskGrade",
    header: "Grade",
    cell: ({ getValue }) => (
      <span className="font-mono font-semibold">{getValue<string>()}</span>
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
    accessorKey: "checkedAt",
    header: "Checked",
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
  result: CreditResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter a 13-digit SA ID number on the left to run a credit check through TransUnion."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Running credit check...
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

  const scoreColor =
    result.creditScore >= 700
      ? "#2C974B"
      : result.creditScore >= 600
        ? "#C28B0B"
        : "#E23D36";

  return (
    <div className="space-y-3">
      {/* Summary header */}
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
                Credit check result
              </span>
              <Badge
                variant={
                  result.riskGrade <= "B"
                    ? "success"
                    : result.riskGrade <= "C"
                      ? "warning"
                      : "danger"
                }
              >
                Grade {result.riskGrade}
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

        {/* Meta grid */}
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

      {/* Score + details */}
      <Card>
        <CardHeader>
          <span className="text-[13px] font-semibold">Credit details</span>
        </CardHeader>
        <div>
          {[
            {
              label: "Credit score",
              value: String(result.creditScore),
              pass: result.creditScore >= 600,
            },
            {
              label: "Risk grade",
              value: result.riskGrade,
              pass: result.riskGrade <= "C",
            },
            {
              label: "Adverse listings",
              value: String(result.adverseListings),
              pass: result.adverseListings === 0,
            },
            {
              label: "Payment profile",
              value: result.paymentProfile,
              pass: true,
            },
            {
              label: "Debt-to-income ratio",
              value: `${result.debtToIncome}%`,
              pass: result.debtToIncome < 50,
            },
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

export function CreditCheckPage() {
  const [tab, setTab] = useState("new");
  const [idNumber, setIdNumber] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<CreditResult | null>(null);

  const isValid = useMemo(
    () =>
      /^\d{13}$/.test(idNumber) &&
      firstName.trim().length >= 2 &&
      lastName.trim().length >= 2,
    [idNumber, firstName, lastName],
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
      category="Banking & Financial"
      title="Credit Check"
      description="Run a credit check against TransUnion SA. Returns credit score, risk grade, adverse listings, and debt-to-income ratio."
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
                  label="First name *"
                  placeholder="e.g. Mandla"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                />

                <Input
                  label="Last name *"
                  placeholder="e.g. Tshabalala"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                />

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 12.00 per lookup -- result in ~3 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!isValid || status === "loading"}
                    icon={<CreditCard size={13} />}
                  >
                    {status === "loading" ? "Checking..." : "Run credit check"}
                  </Button>
                </div>
              </form>
            </CardBody>
          </Card>

          {/* Result panel */}
          <ResultPanel status={status} result={result} />
        </div>
      )}

      {tab === "history" && (
        <Card>
          <CardHeader>
            <div>
              <div className="text-sm font-semibold text-text">
                Credit check history
              </div>
              <div className="text-[11px] text-text-muted mt-0.5">
                {DEMO_HISTORY.length} past checks
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
