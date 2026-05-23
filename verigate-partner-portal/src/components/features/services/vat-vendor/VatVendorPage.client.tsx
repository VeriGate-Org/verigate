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
  Store,
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

interface VatResult {
  runId: string;
  vatStatus: "Active" | "Inactive";
  registrationDate: string;
  tradingNameConfirmed: boolean;
  meta: [string, string][];
}

interface VatHistoryRow {
  id: string;
  vendorName: string;
  vatNumber: string;
  vatStatus: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  checkedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: VatResult = {
  runId: "VAT-2026-0045",
  vatStatus: "Active",
  registrationDate: "2017-06-01",
  tradingNameConfirmed: true,
  meta: [
    ["VAT number", "4120345678"],
    ["Trading name", "Mzansi Tech Solutions"],
    ["Registered name", "Mzansi Tech Solutions (Pty) Ltd"],
    ["SARS region", "Gauteng"],
  ],
};

const DEMO_HISTORY: VatHistoryRow[] = [
  { id: "VAT-2026-0044", vendorName: "Cape Wines Export", vatNumber: "4230987654", vatStatus: "Active", status: "success", statusLabel: "Active", checkedAt: "2026-05-18T15:25:00Z" },
  { id: "VAT-2026-0043", vendorName: "Joburg Steel & Iron", vatNumber: "4111222333", vatStatus: "Inactive", status: "danger", statusLabel: "Inactive", checkedAt: "2026-05-18T13:50:00Z" },
  { id: "VAT-2026-0042", vendorName: "Durban Fresh Produce", vatNumber: "4098765432", vatStatus: "Active", status: "success", statusLabel: "Active", checkedAt: "2026-05-18T10:35:00Z" },
  { id: "VAT-2026-0041", vendorName: "Pretoria Consulting Group", vatNumber: "4345678901", vatStatus: "Active", status: "warning", statusLabel: "Name mismatch", checkedAt: "2026-05-17T16:10:00Z" },
  { id: "VAT-2026-0040", vendorName: "Eastern Cape Logistics", vatNumber: "4567890123", vatStatus: "Active", status: "success", statusLabel: "Active", checkedAt: "2026-05-17T11:40:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<VatHistoryRow, unknown>[] = [
  {
    accessorKey: "id",
    header: "ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "vendorName",
    header: "Vendor",
    cell: ({ getValue }) => (
      <span className="font-medium">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "vatNumber",
    header: "VAT Number",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-text-muted">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "vatStatus",
    header: "VAT Status",
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
  result: VatResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter a VAT number and trading name on the left to verify vendor registration with SARS."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Verifying VAT vendor...
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

  const active = result.vatStatus === "Active";

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
                VAT vendor verification result
              </span>
              <Badge variant={active ? "success" : "danger"}>
                {result.vatStatus}
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
            { label: "VAT status", value: result.vatStatus, pass: active },
            { label: "Registration date", value: result.registrationDate, pass: true },
            { label: "Trading name confirmed", value: result.tradingNameConfirmed ? "Yes" : "No", pass: result.tradingNameConfirmed },
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

export function VatVendorPage() {
  const [tab, setTab] = useState("new");
  const [vatNumber, setVatNumber] = useState("");
  const [tradingName, setTradingName] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<VatResult | null>(null);

  const isValid = useMemo(
    () => vatNumber.length >= 10 && tradingName.trim().length >= 2,
    [vatNumber, tradingName],
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
      title="VAT Vendor Search"
      description="Verify VAT vendor registration status with SARS. Confirm VAT number validity, registration date, and trading name."
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
                  Vendor details
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
                  label="VAT number *"
                  placeholder="e.g. 4120345678"
                  value={vatNumber}
                  onChange={(e) =>
                    setVatNumber(e.target.value.replace(/\D/g, "").slice(0, 10))
                  }
                  hint="10-digit SARS VAT registration number."
                  error={vatNumber.length > 0 && vatNumber.length < 10}
                  errorMessage="Must be exactly 10 digits."
                  className="font-mono"
                />

                <Input
                  label="Trading name *"
                  placeholder="e.g. Mzansi Tech Solutions"
                  value={tradingName}
                  onChange={(e) => setTradingName(e.target.value)}
                />

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 3.00 per lookup -- result in ~2 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!isValid || status === "loading"}
                    icon={<Store size={13} />}
                  >
                    {status === "loading" ? "Verifying..." : "Verify vendor"}
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
