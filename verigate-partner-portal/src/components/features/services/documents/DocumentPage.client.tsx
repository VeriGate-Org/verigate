"use client";

import { useState, useCallback, useRef } from "react";
import { ServicePageLayout } from "../ServicePageLayout";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Select } from "@/components/ui/Select";
import { Input } from "@/components/ui/Input";
import { DataTable } from "@/components/ui/DataTable";
import { EmptyState } from "@/components/ui/EmptyState";
import { Skeleton } from "@/components/ui/Skeleton";
import { cn } from "@/lib/cn";
import {
  FileCheck,
  Download,
  Upload,
  CheckCircle,
  ShieldAlert,
  X,
  Loader2,
} from "lucide-react";
import type { ColumnDef } from "@tanstack/react-table";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface ExtractedField {
  key: string;
  label: string;
  value: string;
  confidence: number;
  mono?: boolean;
}

interface TamperingMetric {
  key: string;
  label: string;
  value: number;
}

interface DocResult {
  id: string;
  fileName: string;
  documentType: string;
  documentTypeLabel: string;
  fields: ExtractedField[];
  enteredIdNumber: string;
  tampering: {
    overall: number;
    metrics: TamperingMetric[];
    flags: string[];
  };
}

interface DocHistoryRow {
  id: string;
  file: string;
  typeLabel: string;
  fieldsCount: number;
  tampering: number;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  verifiedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: DocResult = {
  id: "DV-2026-1285",
  fileName: "sa_id_demo.jpg",
  documentType: "id_card",
  documentTypeLabel: "SA ID Card",
  fields: [
    { key: "idNumber", label: "ID number", value: "9001015800087", confidence: 0.98, mono: true },
    { key: "surname", label: "Surname", value: "Mokoena", confidence: 0.96 },
    { key: "forenames", label: "Forenames", value: "Lerato", confidence: 0.94 },
    { key: "gender", label: "Gender", value: "Female", confidence: 0.99 },
    { key: "dateOfBirth", label: "Date of birth", value: "1990-01-01", confidence: 0.97, mono: true },
    { key: "countryOfBirth", label: "Country of birth", value: "South Africa", confidence: 0.95 },
    { key: "citizenship", label: "Citizenship", value: "South African", confidence: 0.93 },
    { key: "issueDate", label: "Date of issue", value: "2010-04-12", confidence: 0.89, mono: true },
  ],
  enteredIdNumber: "9001015800087",
  tampering: {
    overall: 94,
    metrics: [
      { key: "fontConsistency", label: "Font consistency", value: 97 },
      { key: "layoutAlignment", label: "Layout alignment", value: 96 },
      { key: "imageQuality", label: "Image quality", value: 88 },
      { key: "securityFeatures", label: "Security features", value: 95 },
      { key: "metadataConsistency", label: "Metadata consistency", value: 94 },
    ],
    flags: [],
  },
};

const TAMPERED_RESULT: DocResult = {
  ...DEMO_RESULT,
  id: "DV-2026-1286",
  tampering: {
    overall: 54,
    metrics: [
      { key: "fontConsistency", label: "Font consistency", value: 41 },
      { key: "layoutAlignment", label: "Layout alignment", value: 78 },
      { key: "imageQuality", label: "Image quality", value: 62 },
      { key: "securityFeatures", label: "Security features", value: 39 },
      { key: "metadataConsistency", label: "Metadata consistency", value: 51 },
    ],
    flags: [
      "Font on ID number does not match issuer template (Calibri vs Helvetica Bold).",
      "EXIF metadata was overwritten -- original capture device cannot be confirmed.",
      "Edge pixels around the photo show resampling artifacts consistent with image splicing.",
    ],
  },
};

const DEMO_HISTORY: DocHistoryRow[] = [
  { id: "DV-2026-1284", file: "sa_id_jane_smith.jpg", typeLabel: "SA ID Card", fieldsCount: 8, tampering: 96, status: "success", statusLabel: "Authentic", verifiedAt: "2026-05-18T14:33:00Z" },
  { id: "DV-2026-1283", file: "passport_mandela_t.pdf", typeLabel: "Passport", fieldsCount: 9, tampering: 91, status: "success", statusLabel: "Authentic", verifiedAt: "2026-05-18T13:11:00Z" },
  { id: "DV-2026-1282", file: "utility_bill_naledi.pdf", typeLabel: "Utility Bill", fieldsCount: 5, tampering: 72, status: "warning", statusLabel: "Review", verifiedAt: "2026-05-18T11:50:00Z" },
  { id: "DV-2026-1281", file: "cipc_acme_corp.pdf", typeLabel: "CIPC Reg.", fieldsCount: 7, tampering: 88, status: "success", statusLabel: "Authentic", verifiedAt: "2026-05-18T09:24:00Z" },
  { id: "DV-2026-1280", file: "drivers_pieter.jpg", typeLabel: "Driver's License", fieldsCount: 7, tampering: 54, status: "danger", statusLabel: "Tampered", verifiedAt: "2026-05-17T16:42:00Z" },
  { id: "DV-2026-1279", file: "tax_cert_sipho.pdf", typeLabel: "Tax Clearance", fieldsCount: 6, tampering: 94, status: "success", statusLabel: "Authentic", verifiedAt: "2026-05-17T14:08:00Z" },
];

/* ------------------------------------------------------------------ */
/*  Doc type options                                                   */
/* ------------------------------------------------------------------ */

const DOC_TYPE_OPTIONS = [
  { value: "id_card", label: "SA ID Card" },
  { value: "passport", label: "Passport" },
  { value: "drivers_license", label: "Driver's License" },
  { value: "b_bbee", label: "B-BBEE Certificate" },
  { value: "cipc", label: "CIPC Registration" },
  { value: "tax_certificate", label: "Tax Clearance" },
  { value: "financial_stmt", label: "Financial Statement" },
  { value: "utility_bill", label: "Utility Bill" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<DocHistoryRow, unknown>[] = [
  {
    accessorKey: "id",
    header: "ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "file",
    header: "File",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "typeLabel",
    header: "Type",
  },
  {
    accessorKey: "fieldsCount",
    header: "Fields",
    cell: ({ getValue }) => (
      <span className="font-mono text-text-muted">{getValue<number>()}</span>
    ),
  },
  {
    accessorKey: "tampering",
    header: "Tampering",
    cell: ({ getValue }) => {
      const val = getValue<number>();
      const color =
        val >= 90 ? "#2C974B" : val >= 75 ? "#C28B0B" : "#E23D36";
      return (
        <div className="flex items-center gap-2">
          <div className="w-12 h-1 rounded-full bg-[#F2F3F3] overflow-hidden">
            <div
              className="h-full rounded-full"
              style={{ width: `${val}%`, backgroundColor: color }}
            />
          </div>
          <span className="font-mono font-semibold text-xs" style={{ color }}>
            {val}%
          </span>
        </div>
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
/*  Drop zone                                                          */
/* ------------------------------------------------------------------ */

function DropZone({
  file,
  onFile,
}: {
  file: File | null;
  onFile: (f: File) => void;
}) {
  const [dragging, setDragging] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  return (
    <label
      onDragOver={(e) => {
        e.preventDefault();
        setDragging(true);
      }}
      onDragLeave={() => setDragging(false)}
      onDrop={(e) => {
        e.preventDefault();
        setDragging(false);
        if (e.dataTransfer.files[0]) onFile(e.dataTransfer.files[0]);
      }}
      className={cn(
        "flex flex-col items-center justify-center gap-2 p-6 rounded-lg border-2 border-dashed cursor-pointer transition-all text-center",
        dragging && "border-accent bg-accent-soft",
        file && !dragging && "border-[rgba(44,151,75,0.4)] bg-[rgba(44,151,75,0.04)]",
        !file && !dragging && "border-[#CBD5E1] bg-surface-alt hover:border-accent",
      )}
    >
      <input
        ref={inputRef}
        type="file"
        accept="image/*,application/pdf"
        className="hidden"
        onChange={(e) => e.target.files?.[0] && onFile(e.target.files[0])}
      />
      {file ? (
        <>
          <div className="w-10 h-10 rounded-full bg-[#2C974B] flex items-center justify-center">
            <CheckCircle size={20} className="text-white" />
          </div>
          <div className="text-xs font-semibold text-text">{file.name}</div>
          <div className="text-[10px] text-text-muted">
            {(file.size / 1024).toFixed(0)} KB -- Ready to verify
          </div>
        </>
      ) : (
        <>
          <div className="w-10 h-10 rounded-full bg-accent-soft flex items-center justify-center">
            <Upload size={20} className="text-accent" />
          </div>
          <div className="text-xs font-semibold text-text">
            Drop document or click to upload
          </div>
          <div className="text-[10px] text-text-muted">
            JPEG, PNG, or PDF -- Max 10 MB
          </div>
        </>
      )}
    </label>
  );
}

/* ------------------------------------------------------------------ */
/*  Tampering analysis card                                            */
/* ------------------------------------------------------------------ */

function TamperingCard({ data }: { data: DocResult["tampering"] }) {
  const passed = data.overall >= 80;

  function barColor(v: number): string {
    if (v >= 90) return "#2C974B";
    if (v >= 75) return "#C28B0B";
    return "#E23D36";
  }

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between w-full">
          <div className="flex items-center gap-2">
            {passed ? (
              <ShieldAlert size={15} className="text-[#2C974B]" />
            ) : (
              <X size={15} className="text-[#E23D36]" />
            )}
            <span className="text-sm font-semibold">Tampering analysis</span>
          </div>
          <Badge variant={passed ? "success" : "danger"}>
            {data.overall}% -- {passed ? "Pass" : "Fail"}
          </Badge>
        </div>
      </CardHeader>
      <CardBody>
        <div className="space-y-3">
          {data.metrics.map((m) => (
            <div key={m.key}>
              <div className="flex justify-between text-[11px] mb-1">
                <span className="text-text-muted font-medium">{m.label}</span>
                <span
                  className="font-mono font-semibold"
                  style={{ color: barColor(m.value) }}
                >
                  {m.value}%
                </span>
              </div>
              <div className="h-[5px] bg-[#F2F3F3] rounded-full overflow-hidden">
                <div
                  className="h-full rounded-full transition-all duration-300"
                  style={{
                    width: `${m.value}%`,
                    backgroundColor: barColor(m.value),
                  }}
                />
              </div>
            </div>
          ))}

          {data.flags.length > 0 && (
            <div className="space-y-2 mt-3">
              <div className="text-[11px] font-semibold uppercase tracking-wide text-[#C28B0B]">
                Flags ({data.flags.length})
              </div>
              {data.flags.map((f, i) => (
                <div
                  key={i}
                  className="flex gap-2 px-3 py-2 rounded border border-[rgba(194,139,11,0.3)] bg-[rgba(194,139,11,0.05)] text-[11px] text-text leading-relaxed"
                >
                  <span className="text-[#C28B0B] shrink-0">!</span>
                  {f}
                </div>
              ))}
            </div>
          )}
        </div>
      </CardBody>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Extracted fields card                                              */
/* ------------------------------------------------------------------ */

function ExtractedFieldsCard({
  fields,
  enteredIdNumber,
  documentType,
}: {
  fields: ExtractedField[];
  enteredIdNumber: string;
  documentType: string;
}) {
  const extractedId = fields.find((f) => f.key === "idNumber")?.value;
  const showIdMatch = documentType === "id_card" && enteredIdNumber && extractedId;
  const idMatches = extractedId === enteredIdNumber;

  function confColor(c: number): string {
    if (c >= 0.9) return "#2C974B";
    if (c >= 0.75) return "#C28B0B";
    return "#E23D36";
  }

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between w-full">
          <span className="text-sm font-semibold">Extracted fields</span>
          <span className="text-[11px] text-text-muted">
            {fields.length} detected
          </span>
        </div>
      </CardHeader>
      <CardBody>
        <div className="space-y-3">
          {showIdMatch && (
            <div
              className={cn(
                "flex items-center gap-2 px-3 py-2 rounded text-xs font-medium border",
                idMatches
                  ? "border-[rgba(44,151,75,0.3)] bg-[rgba(44,151,75,0.05)] text-[#2C974B]"
                  : "border-[rgba(226,61,54,0.3)] bg-[rgba(226,61,54,0.05)] text-[#E23D36]",
              )}
            >
              {idMatches ? (
                <CheckCircle size={14} />
              ) : (
                <X size={14} />
              )}
              {idMatches
                ? "Extracted ID number matches the entered ID"
                : `Extracted ID (${extractedId}) does not match entered ID (${enteredIdNumber})`}
            </div>
          )}

          <div className="border border-border rounded">
            {fields.map((f, i) => (
              <div
                key={f.key}
                className={cn(
                  "flex items-center justify-between px-3.5 py-2 text-xs",
                  i > 0 && "border-t border-[#f1f5f9]",
                )}
              >
                <span className="text-text-muted font-medium">{f.label}</span>
                <div className="flex items-center gap-2.5">
                  <span
                    className={cn(
                      "text-text",
                      f.mono && "font-mono",
                    )}
                  >
                    {f.value}
                  </span>
                  <span
                    className="text-[10px] font-bold font-mono px-1.5 py-0.5 rounded-full"
                    style={{
                      color: confColor(f.confidence),
                      backgroundColor: `${confColor(f.confidence)}1a`,
                    }}
                  >
                    {Math.round(f.confidence * 100)}%
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </CardBody>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Result panel                                                       */
/* ------------------------------------------------------------------ */

function ResultPanel({
  status,
  result,
}: {
  status: "idle" | "loading" | "result";
  result: DocResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileCheck}
        title="Upload a document to begin"
        body="We'll extract every field with confidence scoring and analyse the document for tampering signals."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Extracting fields and analysing for tampering...
          </div>
          <div className="space-y-3">
            <Skeleton className="h-3 w-4/5" />
            <Skeleton className="h-3 w-3/5" />
            <Skeleton className="h-3 w-[70%]" />
            <Skeleton className="h-3 w-[55%]" />
            <Skeleton className="h-3 w-3/5" />
          </div>
        </CardBody>
      </Card>
    );
  }

  if (!result) return null;

  const passed = result.tampering.overall >= 80;

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
              <Badge variant={passed ? "success" : "danger"}>
                {passed ? "Authentic" : "Tampered"}
              </Badge>
            </div>
            <span className="font-mono text-xs text-accent">{result.id}</span>
          </div>
          <Button variant="secondary" size="sm" icon={<Download size={12} />}>
            Export PDF
          </Button>
        </div>
      </Card>

      {/* Tampering + Extracted fields */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3 items-start">
        <TamperingCard data={result.tampering} />
        <ExtractedFieldsCard
          fields={result.fields}
          enteredIdNumber={result.enteredIdNumber}
          documentType={result.documentType}
        />
      </div>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Main Document page                                                 */
/* ------------------------------------------------------------------ */

export function DocumentPage() {
  const [tab, setTab] = useState("new");
  const [docType, setDocType] = useState("id_card");
  const [file, setFile] = useState<File | null>(null);
  const [idNumber, setIdNumber] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<DocResult | null>(null);

  const handleSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      if (!file) return;
      setStatus("loading");
      const timer = setTimeout(() => {
        const tampered = /tamp|fake|fraud/i.test(file.name);
        setResult({
          ...(tampered ? TAMPERED_RESULT : DEMO_RESULT),
          fileName: file.name,
          enteredIdNumber: idNumber || DEMO_RESULT.enteredIdNumber,
        });
        setStatus("result");
      }, 1500);
      return () => clearTimeout(timer);
    },
    [file, idNumber],
  );

  const tabs = [
    { label: "Verify document", value: "new" },
    { label: "History", value: "history", count: DEMO_HISTORY.length },
  ];

  return (
    <ServicePageLayout
      category="Identity"
      title="Document Verification"
      description="OCR extraction and tampering analysis for South African identity, business, financial, and proof-of-address documents."
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
                  Upload &amp; verify
                </div>
                <div className="text-[11px] text-text-muted mt-0.5">
                  Choose document type, attach the file, and we will extract fields and scan for tampering.
                </div>
              </div>
            </CardHeader>
            <CardBody>
              <form onSubmit={handleSubmit} className="space-y-4">
                <Select
                  label="Document type"
                  options={DOC_TYPE_OPTIONS}
                  value={docType}
                  onChange={(e) => setDocType(e.target.value)}
                />

                <DropZone file={file} onFile={setFile} />

                {docType === "id_card" && (
                  <Input
                    label="ID number to cross-check"
                    placeholder="13-digit SA ID"
                    value={idNumber}
                    onChange={(e) =>
                      setIdNumber(
                        e.target.value.replace(/\D/g, "").slice(0, 13),
                      )
                    }
                    hint="We'll confirm the extracted ID number matches."
                    className="font-mono"
                  />
                )}

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    OCR + tampering analysis -- ~3 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!file || status === "loading"}
                    icon={<FileCheck size={13} />}
                  >
                    {status === "loading" ? "Analysing..." : "Verify document"}
                  </Button>
                </div>
              </form>
            </CardBody>
          </Card>

          {/* Result */}
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
                {DEMO_HISTORY.length} documents -- last 7 days
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
