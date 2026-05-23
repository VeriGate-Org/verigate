"use client";

import { useState, useMemo, useCallback } from "react";
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
  Landmark,
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

interface AvsResult {
  runId: string;
  accountMatch: boolean;
  nameMatchScore: number;
  accountType: string;
  accountOpenDate: string;
  meta: [string, string][];
}

interface AvsHistoryRow {
  id: string;
  accountHolder: string;
  bank: string;
  accountNumber: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  verifiedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: AvsResult = {
  runId: "AVS-2026-0087",
  accountMatch: true,
  nameMatchScore: 94,
  accountType: "Cheque / Current",
  accountOpenDate: "2019-03-15",
  meta: [
    ["Account holder", "Thabo Mokoena"],
    ["Bank", "FNB"],
    ["Account number", "62845901234"],
    ["Branch code", "250655"],
  ],
};

const DEMO_HISTORY: AvsHistoryRow[] = [
  { id: "AVS-2026-0086", accountHolder: "Lerato Nkosi", bank: "Standard Bank", accountNumber: "0012345678", status: "success", statusLabel: "Matched", verifiedAt: "2026-05-18T15:32:00Z" },
  { id: "AVS-2026-0085", accountHolder: "Pieter Botha", bank: "Absa", accountNumber: "4078912345", status: "success", statusLabel: "Matched", verifiedAt: "2026-05-18T14:10:00Z" },
  { id: "AVS-2026-0084", accountHolder: "Sipho Dlamini", bank: "Nedbank", accountNumber: "1023456789", status: "danger", statusLabel: "No match", verifiedAt: "2026-05-18T11:45:00Z" },
  { id: "AVS-2026-0083", accountHolder: "Naledi Mahlangu", bank: "Capitec", accountNumber: "1234509876", status: "warning", statusLabel: "Partial", verifiedAt: "2026-05-17T16:20:00Z" },
  { id: "AVS-2026-0082", accountHolder: "Johan van Wyk", bank: "FNB", accountNumber: "62890123456", status: "success", statusLabel: "Matched", verifiedAt: "2026-05-17T09:55:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<AvsHistoryRow, unknown>[] = [
  {
    accessorKey: "id",
    header: "ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "accountHolder",
    header: "Account Holder",
    cell: ({ getValue }) => (
      <span className="font-medium">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "bank",
    header: "Bank",
  },
  {
    accessorKey: "accountNumber",
    header: "Account No.",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-text-muted">{getValue<string>()}</span>
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
  result: AvsResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter account holder details on the left and submit to run a bank account verification (AVS)."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Running account verification...
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
                Verification result
              </span>
              <Badge variant={result.accountMatch ? "success" : "danger"}>
                {result.accountMatch ? "Account matched" : "No match"}
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

      {/* Result details */}
      <Card>
        <CardHeader>
          <span className="text-[13px] font-semibold">Verification details</span>
        </CardHeader>
        <div>
          {[
            { label: "Account match", value: result.accountMatch ? "Yes" : "No", pass: result.accountMatch },
            { label: "Name match score", value: `${result.nameMatchScore}%`, pass: result.nameMatchScore >= 80 },
            { label: "Account type", value: result.accountType, pass: true },
            { label: "Account open date", value: result.accountOpenDate, pass: true },
          ].map((row, i) => (
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

const BANK_OPTIONS = [
  { value: "fnb", label: "FNB" },
  { value: "standard-bank", label: "Standard Bank" },
  { value: "absa", label: "Absa" },
  { value: "nedbank", label: "Nedbank" },
  { value: "capitec", label: "Capitec" },
];

export function BankAccountPage() {
  const [tab, setTab] = useState("new");
  const [accountHolder, setAccountHolder] = useState("");
  const [bank, setBank] = useState("fnb");
  const [accountNumber, setAccountNumber] = useState("");
  const [branchCode, setBranchCode] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<AvsResult | null>(null);

  const canSubmit = useMemo(
    () =>
      accountHolder.trim().length >= 2 &&
      accountNumber.length >= 6 &&
      branchCode.length >= 5,
    [accountHolder, accountNumber, branchCode],
  );

  const handleSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      if (!canSubmit) return;
      setStatus("loading");
      const timer = setTimeout(() => {
        setResult(DEMO_RESULT);
        setStatus("result");
      }, 1200);
      return () => clearTimeout(timer);
    },
    [canSubmit],
  );

  const tabs = [
    { label: "New verification", value: "new" },
    { label: "History", value: "history", count: DEMO_HISTORY.length },
  ];

  return (
    <ServicePageLayout
      category="Banking & Financial"
      title="Bank Account Verification (AVS)"
      description="Verify bank account details against SA banking records. Confirm account holder name, account type, and account status."
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
                  Account details
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
                  label="Account holder *"
                  placeholder="e.g. Thabo Mokoena"
                  value={accountHolder}
                  onChange={(e) => setAccountHolder(e.target.value)}
                />

                <Select
                  label="Bank *"
                  options={BANK_OPTIONS}
                  value={bank}
                  onChange={(e) => setBank(e.target.value)}
                />

                <Input
                  label="Account number *"
                  placeholder="e.g. 62845901234"
                  value={accountNumber}
                  onChange={(e) =>
                    setAccountNumber(e.target.value.replace(/\D/g, "").slice(0, 15))
                  }
                  className="font-mono"
                  hint="6 to 15 digit account number."
                />

                <Input
                  label="Branch code *"
                  placeholder="e.g. 250655"
                  value={branchCode}
                  onChange={(e) =>
                    setBranchCode(e.target.value.replace(/\D/g, "").slice(0, 6))
                  }
                  className="font-mono"
                  hint="5 or 6 digit universal branch code."
                />

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 3.50 per lookup -- result in ~2 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!canSubmit || status === "loading"}
                    icon={<Landmark size={13} />}
                  >
                    {status === "loading" ? "Verifying..." : "Verify account"}
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
