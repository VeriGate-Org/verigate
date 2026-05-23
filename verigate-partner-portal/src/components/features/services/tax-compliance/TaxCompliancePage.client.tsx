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
  Receipt,
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

interface TaxResult {
  runId: string;
  taxStatus: "Compliant" | "Non-compliant";
  clearanceValidUntil: string;
  outstandingReturns: number;
  meta: [string, string][];
}

interface TaxHistoryRow {
  id: string;
  subject: string;
  taxRef: string;
  taxStatus: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  checkedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: TaxResult = {
  runId: "TAX-2026-0034",
  taxStatus: "Compliant",
  clearanceValidUntil: "2027-03-31",
  outstandingReturns: 0,
  meta: [
    ["Tax reference", "0234567890"],
    ["ID Number", "9001015800087"],
    ["Taxpayer name", "Jane Smith"],
    ["Tax year", "2025/2026"],
  ],
};

const DEMO_HISTORY: TaxHistoryRow[] = [
  { id: "TAX-2026-0033", subject: "Mandla Tshabalala", taxRef: "0198765432", taxStatus: "Compliant", status: "success", statusLabel: "Compliant", checkedAt: "2026-05-18T15:20:00Z" },
  { id: "TAX-2026-0032", subject: "Sipho Dlamini", taxRef: "0276543210", taxStatus: "Non-compliant", status: "danger", statusLabel: "Non-compliant", checkedAt: "2026-05-18T13:45:00Z" },
  { id: "TAX-2026-0031", subject: "Lerato Mokoena", taxRef: "0312345678", taxStatus: "Compliant", status: "success", statusLabel: "Compliant", checkedAt: "2026-05-18T10:30:00Z" },
  { id: "TAX-2026-0030", subject: "Pieter van der Merwe", taxRef: "0498765432", taxStatus: "Non-compliant", status: "danger", statusLabel: "Non-compliant", checkedAt: "2026-05-17T16:15:00Z" },
  { id: "TAX-2026-0029", subject: "Naledi Nkosi", taxRef: "0556781234", taxStatus: "Compliant", status: "success", statusLabel: "Compliant", checkedAt: "2026-05-17T11:50:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<TaxHistoryRow, unknown>[] = [
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
    accessorKey: "taxRef",
    header: "Tax Reference",
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
  result: TaxResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter a SARS tax reference number and ID number to check tax compliance status."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Checking tax compliance...
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

  const compliant = result.taxStatus === "Compliant";

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
                Tax compliance result
              </span>
              <Badge variant={compliant ? "success" : "danger"}>
                {result.taxStatus}
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
          <span className="text-[13px] font-semibold">Compliance details</span>
        </CardHeader>
        <div>
          {[
            { label: "Tax status", value: result.taxStatus, pass: compliant },
            { label: "Tax clearance valid until", value: result.clearanceValidUntil, pass: compliant },
            { label: "Outstanding returns", value: String(result.outstandingReturns), pass: result.outstandingReturns === 0 },
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

export function TaxCompliancePage() {
  const [tab, setTab] = useState("new");
  const [taxRef, setTaxRef] = useState("");
  const [idNumber, setIdNumber] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<TaxResult | null>(null);

  const isValid = useMemo(
    () => taxRef.length >= 10 && /^\d{13}$/.test(idNumber),
    [taxRef, idNumber],
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
      category="Regulatory & Compliance"
      title="Tax Compliance"
      description="Check tax compliance status against SARS. Verify tax clearance certificate validity, outstanding returns, and compliance status."
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
                  Taxpayer details
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
                  label="Tax reference number *"
                  placeholder="e.g. 0234567890"
                  value={taxRef}
                  onChange={(e) =>
                    setTaxRef(e.target.value.replace(/\D/g, "").slice(0, 10))
                  }
                  hint="10-digit SARS tax reference number."
                  error={taxRef.length > 0 && taxRef.length < 10}
                  errorMessage="Must be exactly 10 digits."
                  className="font-mono"
                />

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

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 6.00 per lookup -- result in ~3 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!isValid || status === "loading"}
                    icon={<Receipt size={13} />}
                  >
                    {status === "loading" ? "Checking..." : "Check compliance"}
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
                Compliance check history
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
