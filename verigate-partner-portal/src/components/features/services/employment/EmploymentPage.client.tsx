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
  Briefcase,
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

interface EmploymentResult {
  runId: string;
  employmentConfirmed: boolean;
  jobTitle: string;
  startDate: string;
  currentSalaryBand: string;
  meta: [string, string][];
}

interface EmploymentHistoryRow {
  id: string;
  subject: string;
  idNumber: string;
  employer: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  verifiedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: EmploymentResult = {
  runId: "EMP-2026-0043",
  employmentConfirmed: true,
  jobTitle: "Senior Software Engineer",
  startDate: "2022-01-10",
  currentSalaryBand: "R 55,001 - R 75,000",
  meta: [
    ["Full name", "Thabo Mokoena"],
    ["ID Number", "8806125800086"],
    ["Employer", "Takealot Online (Pty) Ltd"],
    ["Employee number", "TKL-00892"],
  ],
};

const DEMO_HISTORY: EmploymentHistoryRow[] = [
  { id: "EMP-2026-0042", subject: "Jane Smith", idNumber: "9001015800087", employer: "Amazon Dev Centre SA", status: "success", statusLabel: "Confirmed", verifiedAt: "2026-05-18T15:10:00Z" },
  { id: "EMP-2026-0041", subject: "Sipho Dlamini", idNumber: "8211056500088", employer: "Woolworths Holdings", status: "danger", statusLabel: "Not found", verifiedAt: "2026-05-18T13:22:00Z" },
  { id: "EMP-2026-0040", subject: "Lerato Mokoena", idNumber: "9501025800082", employer: "Discovery Ltd", status: "success", statusLabel: "Confirmed", verifiedAt: "2026-05-18T10:45:00Z" },
  { id: "EMP-2026-0039", subject: "Anele Zulu", idNumber: "9108235800083", employer: "Sasol Ltd", status: "warning", statusLabel: "Partial match", verifiedAt: "2026-05-17T16:08:00Z" },
  { id: "EMP-2026-0038", subject: "Pieter Botha", idNumber: "8511256100089", employer: "Naspers Ltd", status: "success", statusLabel: "Confirmed", verifiedAt: "2026-05-17T11:35:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<EmploymentHistoryRow, unknown>[] = [
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
    accessorKey: "employer",
    header: "Employer",
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
  result: EmploymentResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter subject and employer details on the left to verify employment status."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Verifying employment...
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
                Employment verification result
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
            { label: "Employment confirmed", value: result.employmentConfirmed ? "Yes" : "No", pass: result.employmentConfirmed },
            { label: "Job title", value: result.jobTitle, pass: true },
            { label: "Start date", value: result.startDate, pass: true },
            { label: "Current salary band", value: result.currentSalaryBand, pass: true },
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

export function EmploymentPage() {
  const [tab, setTab] = useState("new");
  const [idNumber, setIdNumber] = useState("");
  const [employerName, setEmployerName] = useState("");
  const [employeeNumber, setEmployeeNumber] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<EmploymentResult | null>(null);

  const isValid = useMemo(
    () =>
      /^\d{13}$/.test(idNumber) &&
      employerName.trim().length >= 2 &&
      employeeNumber.trim().length >= 1,
    [idNumber, employerName, employeeNumber],
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
      title="Employment Verification"
      description="Verify employment status against employer records. Confirm job title, start date, and salary band."
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
                  Employee details
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
                  placeholder="e.g. Takealot Online (Pty) Ltd"
                  value={employerName}
                  onChange={(e) => setEmployerName(e.target.value)}
                />

                <Input
                  label="Employee number *"
                  placeholder="e.g. TKL-00892"
                  value={employeeNumber}
                  onChange={(e) => setEmployeeNumber(e.target.value)}
                  hint="Internal employee or payroll number."
                  className="font-mono"
                />

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 7.50 per lookup -- result in ~3 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!isValid || status === "loading"}
                    icon={<Briefcase size={13} />}
                  >
                    {status === "loading" ? "Verifying..." : "Verify employment"}
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
