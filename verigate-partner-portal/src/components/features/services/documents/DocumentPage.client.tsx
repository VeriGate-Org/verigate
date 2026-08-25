"use client";

import { useState, useCallback, useRef, useEffect } from "react";
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
  FileText,
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

/**
 * Per-field CIPC cross-validation status. Mirrors FieldMatchStatus in
 * verigate-adapter-document-domain exactly, so wiring in live BFF data later is a
 * type-compatible drop-in, not a rewrite.
 */
type CipcFieldStatus =
  | "MATCH"
  | "MISMATCH"
  | "NOT_EXTRACTED"
  | "NOT_IN_CIPC_RECORD"
  | "NOT_AVAILABLE";

interface CipcFieldComparison {
  field: string;
  label: string;
  extractedValue: string | null;
  cipcValue: string | null;
  status: CipcFieldStatus;
}

/** Mirrors CipcCrossValidationResult in verigate-adapter-document-domain. */
interface CipcCrossValidation {
  cipcLookupPerformed: boolean;
  companyFound: boolean;
  companyActive: boolean;
  unavailableReason: string | null;
  fields: CipcFieldComparison[];
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
  /** Object URL for the uploaded file preview -- used by the side-by-side view. */
  imagePreviewUrl?: string;
  /** Present only for CIPC_REGISTRATION documents. */
  cipcCrossValidation?: CipcCrossValidation;
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

/* ------------------------------------------------------------------ */
/*  CIPC registration demo data                                        */
/* ------------------------------------------------------------------ */

const CIPC_EXTRACTED_FIELDS: ExtractedField[] = [
  { key: "companyName", label: "Company name", value: "Acme Trading (Pty) Ltd", confidence: 0.96 },
  { key: "registrationNumber", label: "Registration number", value: "2020/939681/07", confidence: 0.98, mono: true },
  { key: "companyStatus", label: "Company status", value: "Active", confidence: 0.94 },
  { key: "companyType", label: "Company type", value: "Pty Ltd", confidence: 0.9 },
  { key: "registeredAddress", label: "Registered address", value: "1 Main Street, Cape Town, 8001", confidence: 0.87 },
  { key: "directors", label: "Directors", value: "John Smith, Jane Doe", confidence: 0.85 },
];

/** Clean match: CIPC found the company, active, every field matches. */
const CIPC_DEMO_RESULT: DocResult = {
  id: "DV-2026-1290",
  fileName: "cipc_acme_trading.pdf",
  documentType: "cipc",
  documentTypeLabel: "CIPC Registration",
  fields: CIPC_EXTRACTED_FIELDS,
  enteredIdNumber: "",
  tampering: {
    overall: 92,
    metrics: [
      { key: "fontConsistency", label: "Font consistency", value: 95 },
      { key: "layoutAlignment", label: "Layout alignment", value: 93 },
      { key: "imageQuality", label: "Image quality", value: 90 },
      { key: "securityFeatures", label: "Security features", value: 89 },
      { key: "metadataConsistency", label: "Metadata consistency", value: 91 },
    ],
    flags: [],
  },
  cipcCrossValidation: {
    cipcLookupPerformed: true,
    companyFound: true,
    companyActive: true,
    unavailableReason: null,
    fields: [
      { field: "companyName", label: "Company name", extractedValue: "Acme Trading (Pty) Ltd", cipcValue: "ACME TRADING PROPRIETARY LIMITED", status: "MATCH" },
      { field: "companyStatus", label: "Company status", extractedValue: "Active", cipcValue: "In Business", status: "MATCH" },
      { field: "companyType", label: "Company type", extractedValue: "Pty Ltd", cipcValue: "Private Company", status: "MATCH" },
      { field: "registeredAddress", label: "Registered address", extractedValue: "1 Main Street, Cape Town, 8001", cipcValue: "1 Main Street, Cape Town, Western Cape, 8001", status: "MATCH" },
      { field: "directors", label: "Directors", extractedValue: "John Smith, Jane Doe", cipcValue: "John Smith, Jane Doe", status: "MATCH" },
    ],
  },
};

/** CIPC found the company, but the registered address on file doesn't match. */
const CIPC_MISMATCH_RESULT: DocResult = {
  ...CIPC_DEMO_RESULT,
  id: "DV-2026-1291",
  cipcCrossValidation: {
    cipcLookupPerformed: true,
    companyFound: true,
    companyActive: true,
    unavailableReason: null,
    fields: [
      { field: "companyName", label: "Company name", extractedValue: "Acme Trading (Pty) Ltd", cipcValue: "ACME TRADING PROPRIETARY LIMITED", status: "MATCH" },
      { field: "companyStatus", label: "Company status", extractedValue: "Active", cipcValue: "In Business", status: "MATCH" },
      { field: "companyType", label: "Company type", extractedValue: "Pty Ltd", cipcValue: "Private Company", status: "MATCH" },
      { field: "registeredAddress", label: "Registered address", extractedValue: "1 Main Street, Cape Town, 8001", cipcValue: "88 Ocean Drive, Durban, 4001", status: "MISMATCH" },
      { field: "directors", label: "Directors", extractedValue: "John Smith, Jane Doe", cipcValue: "John Smith, Jane Doe", status: "MATCH" },
    ],
  },
};

/** No CIPC record found for the extracted registration number. */
const CIPC_NOT_FOUND_RESULT: DocResult = {
  ...CIPC_DEMO_RESULT,
  id: "DV-2026-1292",
  cipcCrossValidation: {
    cipcLookupPerformed: true,
    companyFound: false,
    companyActive: false,
    unavailableReason: null,
    fields: [],
  },
};

const DEMO_HISTORY: DocHistoryRow[] = [
  { id: "DV-2026-1284", file: "sa_id_jane_smith.jpg", typeLabel: "SA ID Card", fieldsCount: 8, tampering: 96, status: "success", statusLabel: "Authentic", verifiedAt: "2026-05-18T14:33:00Z" },
  { id: "DV-2026-1283", file: "passport_mandela_t.pdf", typeLabel: "Passport", fieldsCount: 9, tampering: 91, status: "success", statusLabel: "Authentic", verifiedAt: "2026-05-18T13:11:00Z" },
  { id: "DV-2026-1282", file: "utility_bill_naledi.pdf", typeLabel: "Utility Bill", fieldsCount: 5, tampering: 72, status: "warning", statusLabel: "Review", verifiedAt: "2026-05-18T11:50:00Z" },
  { id: "DV-2026-1281", file: "cipc_acme_corp.pdf", typeLabel: "CIPC Reg.", fieldsCount: 7, tampering: 88, status: "success", statusLabel: "Authentic", verifiedAt: "2026-05-18T09:24:00Z" },
  { id: "DV-2026-1291", file: "cipc_acme_mismatch.pdf", typeLabel: "CIPC Reg.", fieldsCount: 6, tampering: 90, status: "warning", statusLabel: "CIPC mismatch", verifiedAt: "2026-05-19T10:02:00Z" },
  { id: "DV-2026-1292", file: "cipc_notfound.pdf", typeLabel: "CIPC Reg.", fieldsCount: 6, tampering: 92, status: "warning", statusLabel: "Not found in CIPC", verifiedAt: "2026-05-19T08:47:00Z" },
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
/*  CIPC cross-validation card                                         */
/* ------------------------------------------------------------------ */

const CIPC_STATUS_BADGE: Record<
  CipcFieldStatus,
  { variant: "success" | "danger" | "neutral" | "warning"; label: string }
> = {
  MATCH: { variant: "success", label: "Match" },
  MISMATCH: { variant: "danger", label: "Mismatch" },
  NOT_EXTRACTED: { variant: "neutral", label: "Not extracted" },
  NOT_IN_CIPC_RECORD: { variant: "neutral", label: "Not on CIPC record" },
  NOT_AVAILABLE: { variant: "warning", label: "Unavailable" },
};

function CipcCrossValidationCard({ data }: { data: CipcCrossValidation }) {
  if (!data.cipcLookupPerformed) {
    return (
      <Card>
        <CardHeader>
          <span className="text-sm font-semibold">CIPC cross-validation</span>
        </CardHeader>
        <CardBody>
          <div className="flex items-center gap-2 px-3 py-2 rounded text-xs font-medium border border-[rgba(194,139,11,0.3)] bg-[rgba(194,139,11,0.05)] text-[#C28B0B]">
            <ShieldAlert size={14} />
            CIPC cross-check unavailable
            {data.unavailableReason ? `: ${data.unavailableReason}` : ""}
          </div>
        </CardBody>
      </Card>
    );
  }

  if (!data.companyFound) {
    return (
      <Card>
        <CardHeader>
          <span className="text-sm font-semibold">CIPC cross-validation</span>
        </CardHeader>
        <CardBody>
          <div className="flex items-center gap-2 px-3 py-2 rounded text-xs font-medium border border-[rgba(226,61,54,0.3)] bg-[rgba(226,61,54,0.05)] text-[#E23D36]">
            <X size={14} />
            No CIPC record found for the extracted registration number
          </div>
        </CardBody>
      </Card>
    );
  }

  const matchedCount = data.fields.filter((f) => f.status === "MATCH").length;

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between w-full">
          <span className="text-sm font-semibold">CIPC cross-validation</span>
          <div className="flex items-center gap-1.5">
            <Badge variant={data.companyActive ? "success" : "danger"}>
              {data.companyActive ? "Active" : "Not active"}
            </Badge>
            <Badge variant="neutral">
              {matchedCount}/{data.fields.length} matched
            </Badge>
          </div>
        </div>
      </CardHeader>
      <CardBody>
        <div className="border border-border rounded overflow-hidden">
          <div className="grid grid-cols-[1fr_1.2fr_1.2fr_auto] gap-2 px-3.5 py-2 text-[10px] font-semibold uppercase tracking-wide text-text-muted bg-surface-alt">
            <span>Field</span>
            <span>Extracted</span>
            <span>CIPC record</span>
            <span>Status</span>
          </div>
          {data.fields.map((f, i) => {
            const badge = CIPC_STATUS_BADGE[f.status];
            return (
              <div
                key={f.field}
                className={cn(
                  "grid grid-cols-[1fr_1.2fr_1.2fr_auto] gap-2 items-center px-3.5 py-2 text-xs",
                  i > 0 && "border-t border-[#f1f5f9]",
                )}
              >
                <span className="text-text-muted font-medium">{f.label}</span>
                <span className="text-text truncate">{f.extractedValue ?? "--"}</span>
                <span className="text-text truncate">{f.cipcValue ?? "--"}</span>
                <Badge variant={badge.variant}>{badge.label}</Badge>
              </div>
            );
          })}
        </div>
      </CardBody>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Document preview (side-by-side view)                               */
/* ------------------------------------------------------------------ */

function DocumentPreview({
  imageUrl,
  fileName,
}: {
  imageUrl?: string;
  fileName: string;
}) {
  const isPdf = fileName.toLowerCase().endsWith(".pdf");

  return (
    <Card>
      <CardHeader>
        <span className="text-sm font-semibold">Document</span>
      </CardHeader>
      <CardBody>
        <div className="rounded border border-border bg-surface-alt flex items-center justify-center overflow-hidden min-h-[280px]">
          {!isPdf && imageUrl ? (
            // eslint-disable-next-line @next/next/no-img-element -- local object URL preview, not a remote/optimizable asset
            <img
              src={imageUrl}
              alt={`Preview of ${fileName}`}
              className="max-w-full max-h-[480px] object-contain"
            />
          ) : (
            <div className="flex flex-col items-center gap-2 py-16 text-text-muted">
              <FileText size={32} />
              <span className="text-[11px]">
                {isPdf
                  ? "PDF preview not yet supported -- showing filename only"
                  : "No preview available"}
              </span>
            </div>
          )}
        </div>
        <div className="mt-2 text-[11px] font-mono text-text-muted truncate">
          {fileName}
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
  const [viewMode, setViewMode] = useState<"report" | "side-by-side">("report");

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
  const cipc = result.cipcCrossValidation;

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
          <div className="flex items-center gap-2">
            {cipc && (
              <div className="flex items-center rounded-aws-token border border-border overflow-hidden text-xs font-medium">
                <button
                  type="button"
                  onClick={() => setViewMode("report")}
                  className={cn(
                    "px-3 py-1.5 transition-colors",
                    viewMode === "report"
                      ? "bg-accent text-white"
                      : "bg-surface text-text-muted hover:bg-surface-alt",
                  )}
                >
                  Report view
                </button>
                <button
                  type="button"
                  onClick={() => setViewMode("side-by-side")}
                  className={cn(
                    "px-3 py-1.5 transition-colors border-l border-border",
                    viewMode === "side-by-side"
                      ? "bg-accent text-white"
                      : "bg-surface text-text-muted hover:bg-surface-alt",
                  )}
                >
                  Side by side
                </button>
              </div>
            )}
            <Button variant="secondary" size="sm" icon={<Download size={12} />}>
              Export PDF
            </Button>
          </div>
        </div>
      </Card>

      {cipc && viewMode === "side-by-side" ? (
        <div className="grid grid-cols-1 lg:grid-cols-[minmax(0,360px)_minmax(0,1fr)] gap-3 items-start">
          <DocumentPreview
            imageUrl={result.imagePreviewUrl}
            fileName={result.fileName}
          />
          <div className="space-y-3">
            <TamperingCard data={result.tampering} />
            <ExtractedFieldsCard
              fields={result.fields}
              enteredIdNumber={result.enteredIdNumber}
              documentType={result.documentType}
            />
            <CipcCrossValidationCard data={cipc} />
          </div>
        </div>
      ) : (
        <div className="space-y-3">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 items-start">
            <TamperingCard data={result.tampering} />
            <ExtractedFieldsCard
              fields={result.fields}
              enteredIdNumber={result.enteredIdNumber}
              documentType={result.documentType}
            />
          </div>
          {cipc && <CipcCrossValidationCard data={cipc} />}
        </div>
      )}
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
  const [previewUrl, setPreviewUrl] = useState<string | undefined>(undefined);
  const [idNumber, setIdNumber] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<DocResult | null>(null);

  // Revoke the object URL when replaced or when the component unmounts, to avoid
  // leaking memory across repeated uploads.
  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl);
    };
  }, [previewUrl]);

  const handleFile = useCallback(
    (f: File) => {
      if (previewUrl) URL.revokeObjectURL(previewUrl);
      setFile(f);
      setPreviewUrl(f.type.startsWith("image/") ? URL.createObjectURL(f) : undefined);
    },
    [previewUrl],
  );

  const handleSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      if (!file) return;
      setStatus("loading");
      const timer = setTimeout(() => {
        let base: DocResult;
        if (docType === "cipc") {
          if (/mismatch/i.test(file.name)) base = CIPC_MISMATCH_RESULT;
          else if (/notfound|not-found/i.test(file.name)) base = CIPC_NOT_FOUND_RESULT;
          else base = CIPC_DEMO_RESULT;
        } else {
          const tampered = /tamp|fake|fraud/i.test(file.name);
          base = tampered ? TAMPERED_RESULT : DEMO_RESULT;
        }
        setResult({
          ...base,
          fileName: file.name,
          enteredIdNumber:
            idNumber || (docType === "id_card" ? DEMO_RESULT.enteredIdNumber : ""),
          imagePreviewUrl: previewUrl,
        });
        setStatus("result");
      }, 1500);
      return () => clearTimeout(timer);
    },
    [file, idNumber, docType, previewUrl],
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

                <DropZone file={file} onFile={handleFile} />

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
