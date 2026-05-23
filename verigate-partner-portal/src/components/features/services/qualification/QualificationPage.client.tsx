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
  GraduationCap,
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

interface QualificationResult {
  runId: string;
  qualificationConfirmed: boolean;
  institutionVerified: boolean;
  yearAwarded: string;
  nqfLevel: string;
  meta: [string, string][];
}

interface QualificationHistoryRow {
  id: string;
  subject: string;
  idNumber: string;
  institution: string;
  qualification: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  verifiedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: QualificationResult = {
  runId: "QUAL-2026-0028",
  qualificationConfirmed: true,
  institutionVerified: true,
  yearAwarded: "2018",
  nqfLevel: "NQF 7 (Bachelor's Degree)",
  meta: [
    ["Full name", "Naledi Nkosi"],
    ["ID Number", "9304117500084"],
    ["Institution", "University of Cape Town"],
    ["Qualification", "BSc Computer Science"],
  ],
};

const DEMO_HISTORY: QualificationHistoryRow[] = [
  { id: "QUAL-2026-0027", subject: "Thabo Mokoena", idNumber: "8806125800086", institution: "University of Pretoria", qualification: "BCom Accounting", status: "success", statusLabel: "Confirmed", verifiedAt: "2026-05-18T14:40:00Z" },
  { id: "QUAL-2026-0026", subject: "Sipho Dlamini", idNumber: "8211056500088", institution: "Wits University", qualification: "BA LLB", status: "danger", statusLabel: "Not found", verifiedAt: "2026-05-18T12:05:00Z" },
  { id: "QUAL-2026-0025", subject: "Lerato Mokoena", idNumber: "9501025800082", institution: "Stellenbosch University", qualification: "MBA", status: "success", statusLabel: "Confirmed", verifiedAt: "2026-05-18T09:30:00Z" },
  { id: "QUAL-2026-0024", subject: "Jane Smith", idNumber: "9001015800087", institution: "UNISA", qualification: "National Diploma: IT", status: "warning", statusLabel: "Partial", verifiedAt: "2026-05-17T15:50:00Z" },
  { id: "QUAL-2026-0023", subject: "Anele Zulu", idNumber: "9108235800083", institution: "University of KwaZulu-Natal", qualification: "BSc Engineering", status: "success", statusLabel: "Confirmed", verifiedAt: "2026-05-17T10:15:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<QualificationHistoryRow, unknown>[] = [
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
    accessorKey: "institution",
    header: "Institution",
  },
  {
    accessorKey: "qualification",
    header: "Qualification",
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
  result: QualificationResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter subject and institution details on the left to verify a qualification against SAQA / NLRD."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Verifying qualification...
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
                Qualification verification result
              </span>
              <Badge variant={result.qualificationConfirmed ? "success" : "danger"}>
                {result.qualificationConfirmed ? "Confirmed" : "Not confirmed"}
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
            { label: "Qualification confirmed", value: result.qualificationConfirmed ? "Yes" : "No", pass: result.qualificationConfirmed },
            { label: "Institution verified", value: result.institutionVerified ? "Yes" : "No", pass: result.institutionVerified },
            { label: "Year awarded", value: result.yearAwarded, pass: true },
            { label: "NQF level", value: result.nqfLevel, pass: true },
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

export function QualificationPage() {
  const [tab, setTab] = useState("new");
  const [idNumber, setIdNumber] = useState("");
  const [institution, setInstitution] = useState("");
  const [qualificationName, setQualificationName] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<QualificationResult | null>(null);

  const isValid = useMemo(
    () =>
      /^\d{13}$/.test(idNumber) &&
      institution.trim().length >= 2 &&
      qualificationName.trim().length >= 2,
    [idNumber, institution, qualificationName],
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
      category="Education & Qualifications"
      title="Qualification Verification"
      description="Verify academic qualifications against SAQA and the National Learners Records Database (NLRD). Confirm institution, qualification, and NQF level."
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
                  label="Institution *"
                  placeholder="e.g. University of Cape Town"
                  value={institution}
                  onChange={(e) => setInstitution(e.target.value)}
                />

                <Input
                  label="Qualification name *"
                  placeholder="e.g. BSc Computer Science"
                  value={qualificationName}
                  onChange={(e) => setQualificationName(e.target.value)}
                />

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 15.00 per lookup -- result in ~5 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!isValid || status === "loading"}
                    icon={<GraduationCap size={13} />}
                  >
                    {status === "loading" ? "Verifying..." : "Verify qualification"}
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
