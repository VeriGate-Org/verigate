"use client";

import { useState, useCallback, useRef, useEffect, useMemo } from "react";
import { ServicePageLayout } from "../ServicePageLayout";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { DataTable } from "@/components/ui/DataTable";
import { StatCard } from "@/components/ui/StatCard";
import { EmptyState } from "@/components/ui/EmptyState";
import { cn } from "@/lib/cn";
import { validateSaId } from "@/lib/utils/sa-id-validation";
import {
  Upload,
  FileUp,
  Download,
  CheckCircle,
  X,
  Loader2,
  FileSearch,
  AlertTriangle,
  ClipboardPaste,
  FileText,
  Eye,
} from "lucide-react";
import type { ColumnDef } from "@tanstack/react-table";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

type Phase = "upload" | "processing" | "results";

interface CsvRow {
  idNumber: string;
  firstName: string;
  lastName: string;
  valid: boolean;
  errors: string[];
}

interface SubjectOutcome {
  idNumber: string;
  name: string;
  status: "passed" | "failed" | "review";
  statusLabel: string;
  detail: string;
}

interface BulkHistoryRow {
  jobId: string;
  filename: string;
  records: number;
  status: "success" | "warning" | "danger" | "pending";
  statusLabel: string;
  date: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_CSV_ROWS: CsvRow[] = [
  { idNumber: "9001015800087", firstName: "Lerato", lastName: "Mokoena", valid: true, errors: [] },
  { idNumber: "7304180500081", firstName: "Mandla", lastName: "Tshabalala", valid: true, errors: [] },
  { idNumber: "8511256100089", firstName: "Pieter", lastName: "van der Merwe", valid: true, errors: [] },
  { idNumber: "9304117500084", firstName: "Naledi", lastName: "Nkosi", valid: true, errors: [] },
  { idNumber: "1234567890", firstName: "Invalid", lastName: "Entry", valid: false, errors: ["ID must be 13 digits"] },
  { idNumber: "8211056500088", firstName: "Sipho", lastName: "Dlamini", valid: true, errors: [] },
  { idNumber: "9106045200083", firstName: "Zanele", lastName: "Mthembu", valid: true, errors: [] },
  { idNumber: "8803125600082", firstName: "Thabo", lastName: "Khumalo", valid: true, errors: [] },
  { idNumber: "7709014900081", firstName: "Nomsa", lastName: "Cele", valid: true, errors: [] },
  { idNumber: "9502287800086", firstName: "Bongani", lastName: "Ndlovu", valid: true, errors: [] },
  { idNumber: "8010305100080", firstName: "Fatima", lastName: "Patel", valid: true, errors: [] },
  { idNumber: "ABC1234567890", firstName: "Bad", lastName: "Data", valid: false, errors: ["ID contains non-numeric characters"] },
];

const DEMO_OUTCOMES: SubjectOutcome[] = [
  { idNumber: "9001015800087", name: "Lerato Mokoena", status: "passed", statusLabel: "Verified", detail: "All checks passed" },
  { idNumber: "7304180500081", name: "Mandla Tshabalala", status: "passed", statusLabel: "Verified", detail: "All checks passed" },
  { idNumber: "8511256100089", name: "Pieter van der Merwe", status: "passed", statusLabel: "Verified", detail: "All checks passed" },
  { idNumber: "9304117500084", name: "Naledi Nkosi", status: "review", statusLabel: "Review", detail: "Name mismatch detected" },
  { idNumber: "8211056500088", name: "Sipho Dlamini", status: "passed", statusLabel: "Verified", detail: "All checks passed" },
  { idNumber: "9106045200083", name: "Zanele Mthembu", status: "failed", statusLabel: "Failed", detail: "Deceased flag on record" },
  { idNumber: "8803125600082", name: "Thabo Khumalo", status: "passed", statusLabel: "Verified", detail: "All checks passed" },
  { idNumber: "7709014900081", name: "Nomsa Cele", status: "passed", statusLabel: "Verified", detail: "All checks passed" },
  { idNumber: "9502287800086", name: "Bongani Ndlovu", status: "passed", statusLabel: "Verified", detail: "All checks passed" },
  { idNumber: "8010305100080", name: "Fatima Patel", status: "review", statusLabel: "Review", detail: "Citizenship status mismatch" },
];

const DEMO_HISTORY: BulkHistoryRow[] = [
  { jobId: "BULK-2026-0034", filename: "onboarding_batch_may.csv", records: 150, status: "success", statusLabel: "Completed", date: "2026-05-20T08:30:00Z" },
  { jobId: "BULK-2026-0033", filename: "staff_verification_q2.csv", records: 42, status: "success", statusLabel: "Completed", date: "2026-05-18T14:15:00Z" },
  { jobId: "BULK-2026-0032", filename: "contractor_check.csv", records: 28, status: "warning", statusLabel: "Partial", date: "2026-05-16T11:42:00Z" },
  { jobId: "BULK-2026-0031", filename: "tenant_screening.csv", records: 95, status: "success", statusLabel: "Completed", date: "2026-05-14T09:20:00Z" },
  { jobId: "BULK-2026-0030", filename: "duplicate_test.csv", records: 5, status: "danger", statusLabel: "Failed", date: "2026-05-12T16:05:00Z" },
  { jobId: "BULK-2026-0029", filename: "april_batch_final.csv", records: 200, status: "success", statusLabel: "Completed", date: "2026-04-30T08:00:00Z" },
];

/* ------------------------------------------------------------------ */
/*  Column definitions                                                 */
/* ------------------------------------------------------------------ */

const outcomeColumns: ColumnDef<SubjectOutcome, unknown>[] = [
  {
    accessorKey: "idNumber",
    header: "ID Number",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "name",
    header: "Name",
    cell: ({ getValue }) => (
      <span className="font-medium">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => {
      const variantMap: Record<string, "success" | "danger" | "warning"> = {
        passed: "success",
        failed: "danger",
        review: "warning",
      };
      return (
        <Badge variant={variantMap[row.original.status]}>
          {row.original.statusLabel}
        </Badge>
      );
    },
  },
  {
    accessorKey: "detail",
    header: "Details",
    cell: ({ getValue }) => (
      <span className="text-xs text-text-muted">{getValue<string>()}</span>
    ),
  },
];

const historyColumns: ColumnDef<BulkHistoryRow, unknown>[] = [
  {
    accessorKey: "jobId",
    header: "Job ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">
        {getValue<string>()}
      </span>
    ),
  },
  {
    accessorKey: "filename",
    header: "Filename",
    cell: ({ getValue }) => (
      <div className="flex items-center gap-1.5">
        <FileText size={12} className="text-text-muted shrink-0" />
        <span className="text-xs font-medium">{getValue<string>()}</span>
      </div>
    ),
  },
  {
    accessorKey: "records",
    header: "Records",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs">
        {getValue<number>().toLocaleString()}
      </span>
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
    accessorKey: "date",
    header: "Date",
    cell: ({ getValue }) =>
      new Date(getValue<string>()).toLocaleString("en-ZA", {
        dateStyle: "short",
        timeStyle: "short",
      }),
  },
];

/* ------------------------------------------------------------------ */
/*  CSV parsing                                                        */
/* ------------------------------------------------------------------ */

function parseCsvText(text: string): CsvRow[] {
  const lines = text.trim().split("\n");
  if (lines.length < 2) return [];

  // Skip header row
  const dataLines = lines.slice(1);
  return dataLines.map((line) => {
    const parts = line.split(",").map((s) => s.trim().replace(/^"|"$/g, ""));
    const idNumber = parts[0] || "";
    const firstName = parts[1] || "";
    const lastName = parts[2] || "";
    const errors: string[] = [];

    if (!idNumber) {
      errors.push("ID number is required");
    } else if (!/^\d+$/.test(idNumber)) {
      errors.push("ID contains non-numeric characters");
    } else if (idNumber.length !== 13) {
      errors.push("ID must be 13 digits");
    } else {
      const validation = validateSaId(idNumber);
      if (!validation.valid) {
        errors.push(...validation.errors);
      }
    }

    return {
      idNumber,
      firstName,
      lastName,
      valid: errors.length === 0,
      errors,
    };
  });
}

/* ------------------------------------------------------------------ */
/*  Upload phase                                                       */
/* ------------------------------------------------------------------ */

function UploadPhase({
  onParsed,
}: {
  onParsed: (rows: CsvRow[], filename: string) => void;
}) {
  const [dragActive, setDragActive] = useState(false);
  const [pasteMode, setPasteMode] = useState(false);
  const [pasteText, setPasteText] = useState("");
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFile = useCallback(
    (file: File) => {
      if (!file.name.endsWith(".csv")) return;
      const reader = new FileReader();
      reader.onload = (e) => {
        const text = e.target?.result as string;
        const rows = parseCsvText(text);
        if (rows.length > 0) {
          onParsed(rows, file.name);
        }
      };
      reader.readAsText(file);
    },
    [onParsed],
  );

  const handleDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();
      setDragActive(false);
      const file = e.dataTransfer.files[0];
      if (file) handleFile(file);
    },
    [handleFile],
  );

  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setDragActive(true);
  }, []);

  const handleDragLeave = useCallback(() => {
    setDragActive(false);
  }, []);

  const handlePasteSubmit = useCallback(() => {
    if (!pasteText.trim()) return;
    const rows = parseCsvText(pasteText);
    if (rows.length > 0) {
      onParsed(rows, "pasted-data.csv");
    }
  }, [pasteText, onParsed]);

  return (
    <div className="space-y-4">
      {/* Drag-drop area */}
      <div
        className={cn(
          "relative border-2 border-dashed rounded-aws-container py-14 px-6 text-center transition-all",
          dragActive
            ? "border-accent bg-accent-soft"
            : "border-[#CBD5E1] bg-surface hover:border-accent/50",
        )}
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
      >
        <input
          ref={fileInputRef}
          type="file"
          accept=".csv"
          className="hidden"
          onChange={(e) => {
            const file = e.target.files?.[0];
            if (file) handleFile(file);
          }}
        />

        <div className="w-16 h-16 rounded-2xl bg-accent-soft inline-flex items-center justify-center mb-3.5">
          <Upload size={28} className="text-accent" />
        </div>

        <div className="text-[17px] font-semibold text-text">
          Drop your CSV file here
        </div>
        <div className="text-[13px] text-text-muted mt-1.5 max-w-[420px] mx-auto leading-relaxed">
          Upload a CSV file with columns: <span className="font-mono text-xs font-semibold">ID Number</span>,{" "}
          <span className="font-mono text-xs font-semibold">First Name</span>,{" "}
          <span className="font-mono text-xs font-semibold">Last Name</span>
        </div>
        <div className="mt-4 flex items-center justify-center gap-3">
          <Button
            variant="cta"
            icon={<FileUp size={13} />}
            onClick={() => fileInputRef.current?.click()}
          >
            Browse files
          </Button>
          <Button
            variant="secondary"
            icon={<ClipboardPaste size={13} />}
            onClick={() => setPasteMode(!pasteMode)}
          >
            {pasteMode ? "Hide paste" : "Paste CSV"}
          </Button>
        </div>
      </div>

      {/* Paste mode */}
      {pasteMode && (
        <Card>
          <CardHeader>
            <div className="flex items-center gap-2">
              <ClipboardPaste size={14} className="text-accent" />
              <span className="text-[13px] font-semibold">
                Paste CSV data
              </span>
            </div>
          </CardHeader>
          <CardBody>
            <div className="space-y-3">
              <textarea
                className="aws-input w-full h-40 font-mono text-xs resize-y"
                placeholder={`ID Number,First Name,Last Name\n9001015800087,Lerato,Mokoena\n7304180500081,Mandla,Tshabalala`}
                value={pasteText}
                onChange={(e) => setPasteText(e.target.value)}
              />
              <div className="flex justify-end">
                <Button
                  variant="cta"
                  size="sm"
                  disabled={!pasteText.trim()}
                  onClick={handlePasteSubmit}
                  icon={<Eye size={12} />}
                >
                  Parse & preview
                </Button>
              </div>
            </div>
          </CardBody>
        </Card>
      )}

      {/* File format hint */}
      <Card>
        <CardBody>
          <div className="flex items-start gap-3">
            <div className="w-8 h-8 rounded-lg bg-accent-soft inline-flex items-center justify-center shrink-0">
              <FileText size={16} className="text-accent" />
            </div>
            <div>
              <div className="text-xs font-semibold text-text">
                Expected CSV format
              </div>
              <div className="mt-1.5 bg-[#f2f3f3] rounded px-3 py-2 font-mono text-[11px] text-text-muted leading-relaxed">
                ID Number,First Name,Last Name
                <br />
                9001015800087,Lerato,Mokoena
                <br />
                7304180500081,Mandla,Tshabalala
              </div>
              <div className="text-[11px] text-text-muted mt-2">
                Maximum 500 records per batch. R 3.50 per identity check.
              </div>
            </div>
          </div>
        </CardBody>
      </Card>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Validation preview                                                 */
/* ------------------------------------------------------------------ */

function ValidationPreview({
  rows,
  filename,
  onConfirm,
  onCancel,
}: {
  rows: CsvRow[];
  filename: string;
  onConfirm: () => void;
  onCancel: () => void;
}) {
  const validCount = rows.filter((r) => r.valid).length;
  const invalidCount = rows.length - validCount;
  const previewRows = rows.slice(0, 5);

  return (
    <div className="space-y-4">
      <Card>
        <div className="h-[3px] flex">
          <div className="flex-[3] bg-[#E23D36]" />
          <div className="flex-[5] bg-[#1A2E4B]" />
          <div className="flex-[2] bg-accent" />
        </div>
        <CardHeader>
          <div>
            <div className="text-sm font-semibold text-text">
              File validation preview
            </div>
            <div className="text-[11px] text-text-muted mt-0.5">
              {filename} &mdash; {rows.length} records detected
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Badge variant="success">
              {validCount} valid
            </Badge>
            {invalidCount > 0 && (
              <Badge variant="danger">
                {invalidCount} invalid
              </Badge>
            )}
          </div>
        </CardHeader>

        {/* Preview table */}
        <div className="overflow-x-auto">
          <table className="aws-table">
            <thead>
              <tr>
                <th className="w-8">#</th>
                <th>ID Number</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Validation</th>
              </tr>
            </thead>
            <tbody>
              {previewRows.map((row, i) => (
                <tr key={i} className={cn(!row.valid && "bg-[rgba(226,61,54,0.04)]")}>
                  <td className="text-xs text-text-muted">{i + 1}</td>
                  <td>
                    <span
                      className={cn(
                        "font-mono text-xs",
                        !row.valid && "text-[#E23D36] font-semibold",
                      )}
                    >
                      {row.idNumber}
                    </span>
                  </td>
                  <td className="text-xs">{row.firstName}</td>
                  <td className="text-xs">{row.lastName}</td>
                  <td>
                    {row.valid ? (
                      <span className="inline-flex items-center gap-1 text-xs text-[#2C974B]">
                        <CheckCircle size={12} />
                        Valid
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 text-xs text-[#E23D36]">
                        <AlertTriangle size={12} />
                        {row.errors[0]}
                      </span>
                    )}
                  </td>
                </tr>
              ))}
              {rows.length > 5 && (
                <tr>
                  <td colSpan={5} className="text-center text-xs text-text-muted py-3">
                    ... and {rows.length - 5} more rows
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="px-4 py-3 border-t border-border flex items-center justify-between">
          <span className="text-[11px] text-text-muted">
            {invalidCount > 0
              ? `${invalidCount} invalid records will be skipped.`
              : "All records are valid."}{" "}
            Estimated cost: R {(validCount * 3.5).toFixed(2)}
          </span>
          <div className="flex items-center gap-2">
            <Button variant="secondary" size="sm" onClick={onCancel}>
              Cancel
            </Button>
            <Button
              variant="cta"
              size="sm"
              disabled={validCount === 0}
              onClick={onConfirm}
              icon={<Upload size={12} />}
            >
              Process {validCount} records
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Processing phase                                                   */
/* ------------------------------------------------------------------ */

function ProcessingPhase({
  totalRecords,
  onComplete,
}: {
  totalRecords: number;
  onComplete: () => void;
}) {
  const [processed, setProcessed] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setProcessed((prev) => {
        const next = Math.min(prev + Math.ceil(totalRecords / 20), totalRecords);
        if (next >= totalRecords) {
          clearInterval(interval);
          setTimeout(onComplete, 600);
        }
        return next;
      });
    }, 200);
    return () => clearInterval(interval);
  }, [totalRecords, onComplete]);

  const pct = totalRecords > 0 ? Math.round((processed / totalRecords) * 100) : 0;

  return (
    <Card>
      <CardBody>
        <div className="py-8 max-w-md mx-auto text-center">
          <div className="w-16 h-16 rounded-2xl bg-accent-soft inline-flex items-center justify-center mb-4">
            <Loader2 size={28} className="text-accent animate-spin" />
          </div>

          <div className="text-[17px] font-semibold text-text mb-1">
            Processing bulk verification
          </div>
          <div className="text-[13px] text-text-muted mb-6">
            Verifying {totalRecords} identity records against DHA. Please do not close this page.
          </div>

          {/* Progress bar */}
          <div className="space-y-2">
            <div className="h-2.5 bg-[#e9ebed] rounded-full overflow-hidden">
              <div
                className="h-full bg-accent rounded-full transition-all duration-300 ease-out"
                style={{ width: `${pct}%` }}
              />
            </div>
            <div className="flex items-center justify-between text-xs">
              <span className="font-mono text-text-muted">
                {processed} / {totalRecords} processed
              </span>
              <span className="font-semibold text-accent">{pct}%</span>
            </div>
          </div>

          {/* Animated status messages */}
          <div className="mt-6 space-y-1.5">
            {processed > 0 && (
              <div className="flex items-center justify-center gap-1.5 text-xs text-text-muted animate-pulse">
                <CheckCircle size={11} className="text-[#2C974B]" />
                Validated ID number format
              </div>
            )}
            {processed > totalRecords * 0.3 && (
              <div className="flex items-center justify-center gap-1.5 text-xs text-text-muted animate-pulse">
                <CheckCircle size={11} className="text-[#2C974B]" />
                DHA lookup in progress
              </div>
            )}
            {processed > totalRecords * 0.7 && (
              <div className="flex items-center justify-center gap-1.5 text-xs text-text-muted animate-pulse">
                <CheckCircle size={11} className="text-[#2C974B]" />
                Compiling results
              </div>
            )}
          </div>
        </div>
      </CardBody>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Results phase                                                      */
/* ------------------------------------------------------------------ */

function ResultsPhase({
  outcomes,
  onNewBatch,
}: {
  outcomes: SubjectOutcome[];
  onNewBatch: () => void;
}) {
  const total = outcomes.length;
  const passed = outcomes.filter((o) => o.status === "passed").length;
  const failed = outcomes.filter((o) => o.status === "failed").length;
  const review = outcomes.filter((o) => o.status === "review").length;

  const handleDownload = useCallback(() => {
    const header = "ID Number,Name,Status,Details";
    const rows = outcomes.map(
      (o) => `${o.idNumber},"${o.name}",${o.statusLabel},"${o.detail}"`,
    );
    const csv = [header, ...rows].join("\n");
    const blob = new Blob([csv], { type: "text/csv" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `bulk-identity-results-${new Date().toISOString().split("T")[0]}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }, [outcomes]);

  return (
    <div className="space-y-4">
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
                Bulk verification complete
              </span>
              <Badge variant="success">Completed</Badge>
            </div>
            <span className="text-xs text-text-muted">
              {total} records processed &mdash;{" "}
              {new Date().toLocaleString("en-ZA", {
                dateStyle: "medium",
                timeStyle: "short",
              })}
            </span>
          </div>
          <div className="flex items-center gap-2">
            <Button
              variant="secondary"
              size="sm"
              onClick={onNewBatch}
            >
              New batch
            </Button>
            <Button
              variant="cta"
              size="sm"
              icon={<Download size={12} />}
              onClick={handleDownload}
            >
              Download CSV
            </Button>
          </div>
        </div>
      </Card>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        <StatCard label="Total" value={total} />
        <StatCard
          label="Passed"
          value={passed}
          helper={`${total > 0 ? Math.round((passed / total) * 100) : 0}% pass rate`}
        />
        <StatCard
          label="Failed"
          value={failed}
          deltaTone="danger"
        />
        <StatCard
          label="Review"
          value={review}
          deltaTone="warning"
        />
      </div>

      {/* Per-subject outcomes */}
      <Card>
        <CardHeader>
          <div>
            <div className="text-sm font-semibold text-text">
              Per-subject outcomes
            </div>
            <div className="text-[11px] text-text-muted mt-0.5">
              {outcomes.length} subjects verified
            </div>
          </div>
        </CardHeader>
        <CardBody compact>
          <DataTable
            data={outcomes}
            columns={outcomeColumns}
            pageSize={10}
          />
        </CardBody>
      </Card>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Main Bulk Identity page                                            */
/* ------------------------------------------------------------------ */

export function BulkIdentityPage() {
  const [tab, setTab] = useState("new");
  const [phase, setPhase] = useState<Phase>("upload");
  const [parsedRows, setParsedRows] = useState<CsvRow[]>([]);
  const [filename, setFilename] = useState("");
  const [showPreview, setShowPreview] = useState(false);

  const validRows = useMemo(
    () => parsedRows.filter((r) => r.valid),
    [parsedRows],
  );

  const handleParsed = useCallback((rows: CsvRow[], name: string) => {
    setParsedRows(rows);
    setFilename(name);
    setShowPreview(true);
  }, []);

  const handleConfirm = useCallback(() => {
    setShowPreview(false);
    setPhase("processing");
  }, []);

  const handleProcessingComplete = useCallback(() => {
    setPhase("results");
  }, []);

  const handleNewBatch = useCallback(() => {
    setParsedRows([]);
    setFilename("");
    setShowPreview(false);
    setPhase("upload");
  }, []);

  const handleCancel = useCallback(() => {
    setParsedRows([]);
    setFilename("");
    setShowPreview(false);
  }, []);

  const tabs = [
    { label: "New batch", value: "new" },
    { label: "History", value: "history", count: DEMO_HISTORY.length },
  ];

  return (
    <ServicePageLayout
      category="Identity \u00B7 Bulk"
      title="Bulk Identity Verification"
      description="Upload a CSV of SA identity numbers to verify multiple subjects against the Department of Home Affairs in a single batch."
      tabs={tabs}
      activeTab={tab}
      onTabChange={setTab}
    >
      {tab === "new" && (
        <>
          {phase === "upload" && !showPreview && (
            <UploadPhase onParsed={handleParsed} />
          )}

          {phase === "upload" && showPreview && (
            <ValidationPreview
              rows={parsedRows}
              filename={filename}
              onConfirm={handleConfirm}
              onCancel={handleCancel}
            />
          )}

          {phase === "processing" && (
            <ProcessingPhase
              totalRecords={validRows.length}
              onComplete={handleProcessingComplete}
            />
          )}

          {phase === "results" && (
            <ResultsPhase
              outcomes={DEMO_OUTCOMES}
              onNewBatch={handleNewBatch}
            />
          )}
        </>
      )}

      {tab === "history" && (
        <Card>
          <CardHeader>
            <div>
              <div className="text-sm font-semibold text-text">
                Bulk job history
              </div>
              <div className="text-[11px] text-text-muted mt-0.5">
                {DEMO_HISTORY.length} past batch jobs
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
