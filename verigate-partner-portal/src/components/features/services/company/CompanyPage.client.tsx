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
  Building2,
  CheckCircle,
  X,
  Download,
  FileSearch,
  Loader2,
  Users,
} from "lucide-react";
import type { ColumnDef } from "@tanstack/react-table";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface Director {
  name: string;
  idNumber: string;
  role: string;
  appointedDate: string;
  status: "Active" | "Resigned";
}

interface CompanyResult {
  runId: string;
  registrationStatus: string;
  beeLevel: string;
  registrationDate: string;
  companyType: string;
  directors: Director[];
  meta: [string, string][];
}

interface CompanyHistoryRow {
  id: string;
  companyName: string;
  regNumber: string;
  regStatus: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  checkedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: CompanyResult = {
  runId: "CIPC-2026-0091",
  registrationStatus: "In business",
  beeLevel: "Level 2",
  registrationDate: "2015-08-22",
  companyType: "Private Company (Pty) Ltd",
  directors: [
    { name: "Mandla Tshabalala", idNumber: "7304180500081", role: "Director", appointedDate: "2015-08-22", status: "Active" },
    { name: "Naledi Nkosi", idNumber: "9304117500084", role: "Director", appointedDate: "2018-03-15", status: "Active" },
    { name: "Pieter van der Merwe", idNumber: "8511256100089", role: "Director", appointedDate: "2015-08-22", status: "Resigned" },
  ],
  meta: [
    ["Company name", "Mzansi Tech Solutions (Pty) Ltd"],
    ["Registration number", "2015/123456/07"],
    ["Company type", "Private Company (Pty) Ltd"],
    ["Registration date", "2015-08-22"],
  ],
};

const DEMO_HISTORY: CompanyHistoryRow[] = [
  { id: "CIPC-2026-0090", companyName: "Cape Digital (Pty) Ltd", regNumber: "2018/654321/07", regStatus: "In business", status: "success", statusLabel: "Active", checkedAt: "2026-05-18T14:50:00Z" },
  { id: "CIPC-2026-0089", companyName: "Joburg Investments CC", regNumber: "CK2010/098765", regStatus: "Deregistered", status: "danger", statusLabel: "Deregistered", checkedAt: "2026-05-18T13:15:00Z" },
  { id: "CIPC-2026-0088", companyName: "Pretoria Logistics (Pty) Ltd", regNumber: "2020/234567/07", regStatus: "In business", status: "success", statusLabel: "Active", checkedAt: "2026-05-18T10:40:00Z" },
  { id: "CIPC-2026-0087", companyName: "Durban Exports NPC", regNumber: "2019/345678/08", regStatus: "In business", status: "warning", statusLabel: "AR overdue", checkedAt: "2026-05-17T16:30:00Z" },
  { id: "CIPC-2026-0086", companyName: "Eastern Cape Mining (Pty) Ltd", regNumber: "2012/456789/07", regStatus: "In business", status: "success", statusLabel: "Active", checkedAt: "2026-05-17T11:20:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<CompanyHistoryRow, unknown>[] = [
  {
    accessorKey: "id",
    header: "ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "companyName",
    header: "Company",
    cell: ({ getValue }) => (
      <span className="font-medium">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "regNumber",
    header: "Reg Number",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-text-muted">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "regStatus",
    header: "Reg Status",
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
  result: CompanyResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter a CIPC registration number on the left to verify company details and retrieve the directors list."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Querying CIPC records...
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

  const active = result.registrationStatus === "In business";

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
                Company verification result
              </span>
              <Badge variant={active ? "success" : "danger"}>
                {result.registrationStatus}
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

      {/* Company details */}
      <Card>
        <CardHeader>
          <span className="text-[13px] font-semibold">Company details</span>
        </CardHeader>
        <div>
          {[
            { label: "Registration status", value: result.registrationStatus, pass: active },
            { label: "BEE level", value: result.beeLevel, pass: true },
            { label: "Registration date", value: result.registrationDate, pass: true },
            { label: "Company type", value: result.companyType, pass: true },
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

      {/* Directors */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <Users size={14} className="text-accent" />
            <span className="text-[13px] font-semibold">
              Directors ({result.directors.length})
            </span>
          </div>
        </CardHeader>
        <div>
          {result.directors.map((d, i) => (
            <div
              key={d.idNumber}
              className={cn(
                "px-4 py-3",
                i > 0 && "border-t border-[#f1f5f9]",
              )}
            >
              <div className="flex items-center justify-between mb-1">
                <span className="text-[13px] font-semibold text-text">
                  {d.name}
                </span>
                <Badge
                  variant={d.status === "Active" ? "success" : "neutral"}
                  size="sm"
                >
                  {d.status}
                </Badge>
              </div>
              <div className="flex gap-4 text-[11px] text-text-muted">
                <span>
                  ID: <span className="font-mono">{d.idNumber}</span>
                </span>
                <span>Role: {d.role}</span>
                <span>Appointed: {d.appointedDate}</span>
              </div>
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

export function CompanyPage() {
  const [tab, setTab] = useState("new");
  const [regNumber, setRegNumber] = useState("");
  const [companyName, setCompanyName] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<CompanyResult | null>(null);

  const isValid = useMemo(
    () => regNumber.trim().length >= 10,
    [regNumber],
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
      category="Corporate & Business"
      title="Company & Directors Verification"
      description="Verify company registration with CIPC. Retrieve registration status, directors list, BEE level, and company type."
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
                  Company details
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
                  label="Company registration number *"
                  placeholder="e.g. 2015/123456/07"
                  value={regNumber}
                  onChange={(e) => setRegNumber(e.target.value)}
                  hint="CIPC format: YYYY/NNNNNN/NN or CK format."
                  className="font-mono"
                />

                <Input
                  label="Company name"
                  placeholder="e.g. Mzansi Tech Solutions (Pty) Ltd"
                  value={companyName}
                  onChange={(e) => setCompanyName(e.target.value)}
                  hint="Optional. Helps narrow results."
                />

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 10.00 per lookup -- result in ~5 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!isValid || status === "loading"}
                    icon={<Building2 size={13} />}
                  >
                    {status === "loading" ? "Querying..." : "Verify company"}
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
