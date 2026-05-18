"use client";

import { useCallback, useRef, useState } from "react";
import { Button } from "@/components/ui/Button";
import FileUpload from "@/components/ui/FileUpload/FileUpload";
import { ScreenReaderAnnounce } from "@/components/ui/ScreenReaderAnnounce";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { exportPdf } from "@/lib/utils/export-pdf";
import {
  Upload,
  CheckCircle2,
  AlertTriangle,
  FileDown,
  Clock,
  FileText,
  Building2,
  Receipt,
  Award,
  TrendingUp,
  MapPin,
  Fingerprint,
} from "lucide-react";

/* ── types ── */

interface AutoFilledField {
  value: string;
  sourceDocument: string;
  confidence: number;
  needsReview: boolean;
}

interface DocumentUpload {
  id: string;
  documentType: string;
  file: File | null;
  status: "pending" | "uploading" | "processing" | "complete" | "error";
  progress: number;
  error?: string;
}

interface AutoFillResult {
  fields: Record<string, AutoFilledField>;
  totalDocuments: number;
  fieldsAutoFilled: number;
  fieldsNeedReview: number;
}

/* ── document type config ── */

const SUPPORTED_DOCUMENT_TYPES = [
  { type: "id_card", label: "SA ID Document", icon: Fingerprint, description: "Full name, ID number, date of birth" },
  { type: "cipc_registration", label: "CIPC Registration", icon: Building2, description: "Company name, registration number, directors" },
  { type: "tax_certificate", label: "Tax Clearance Certificate", icon: Receipt, description: "Tax number, compliance status" },
  { type: "b_bbee_certificate", label: "B-BBEE Certificate", icon: Award, description: "B-BBEE level, verification status" },
  { type: "financial_statement", label: "Financial Statement", icon: TrendingUp, description: "Annual revenue, financial year" },
  { type: "utility_bill", label: "Utility Bill / Proof of Address", icon: MapPin, description: "Business address" },
];

const FIELD_LABELS: Record<string, string> = {
  fullName: "Full Name",
  idNumber: "ID Number",
  dateOfBirth: "Date of Birth",
  gender: "Gender",
  nationality: "Nationality",
  companyName: "Company Name",
  registrationNumber: "Registration No.",
  companyStatus: "Company Status",
  directors: "Directors",
  taxNumber: "Tax Number",
  taxCompliant: "Tax Compliant",
  bbeeLevel: "B-BBEE Level",
  bbbeeStatus: "B-BBEE Status",
  annualRevenue: "Annual Revenue",
  financialYear: "Financial Year",
  businessAddress: "Business Address",
};

const SOURCE_BADGES: Record<string, { label: string; color: string }> = {
  id_card: { label: "SA ID", color: "bg-blue-500/10 text-blue-600" },
  IDENTITY_DOCUMENT: { label: "SA ID", color: "bg-blue-500/10 text-blue-600" },
  cipc_registration: { label: "CIPC", color: "bg-violet-500/10 text-violet-600" },
  CIPC_REGISTRATION: { label: "CIPC", color: "bg-violet-500/10 text-violet-600" },
  tax_certificate: { label: "Tax Cert", color: "bg-emerald-500/10 text-emerald-600" },
  TAX_CERTIFICATE: { label: "Tax Cert", color: "bg-emerald-500/10 text-emerald-600" },
  b_bbee_certificate: { label: "B-BBEE", color: "bg-orange-500/10 text-orange-600" },
  B_BBEE_CERTIFICATE: { label: "B-BBEE", color: "bg-orange-500/10 text-orange-600" },
  financial_statement: { label: "Financials", color: "bg-cyan-500/10 text-cyan-600" },
  FINANCIAL_STATEMENT: { label: "Financials", color: "bg-cyan-500/10 text-cyan-600" },
  utility_bill: { label: "Utility Bill", color: "bg-pink-500/10 text-pink-600" },
  UTILITY_BILL: { label: "Utility Bill", color: "bg-pink-500/10 text-pink-600" },
};

/* ── mock auto-fill generator ── */

function generateMockAutoFill(documents: DocumentUpload[]): AutoFillResult {
  const completedDocs = documents.filter((d) => d.status === "complete");
  const fields: Record<string, AutoFilledField> = {};

  for (const doc of completedDocs) {
    const conf = () => 0.88 + Math.random() * 0.12;
    switch (doc.documentType) {
      case "id_card":
        fields.fullName = { value: "Thabo J. Mokoena", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        fields.idNumber = { value: "8501015009087", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        fields.dateOfBirth = { value: "1985-01-01", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        fields.gender = { value: "Male", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        fields.nationality = { value: "South African", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        break;
      case "cipc_registration":
        fields.companyName = { value: "Karisani Technologies (Pty) Ltd", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        fields.registrationNumber = { value: "2019/123456/07", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        fields.directors = { value: "T.J. Mokoena, S.N. Dlamini", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        break;
      case "tax_certificate":
        fields.taxNumber = { value: "9012345678", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        fields.taxCompliant = { value: "Compliant", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        break;
      case "b_bbee_certificate":
        fields.bbeeLevel = { value: "Level 1", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        break;
      case "financial_statement":
        fields.annualRevenue = { value: "R 12,450,000", sourceDocument: doc.documentType, confidence: 0.72, needsReview: true };
        fields.financialYear = { value: "2024", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        break;
      case "utility_bill":
        fields.businessAddress = { value: "42 Innovation Drive, Sandton, 2196", sourceDocument: doc.documentType, confidence: conf(), needsReview: false };
        break;
    }
  }

  const allFields = Object.values(fields);
  return {
    fields,
    totalDocuments: completedDocs.length,
    fieldsAutoFilled: allFields.filter((f) => !f.needsReview).length,
    fieldsNeedReview: allFields.filter((f) => f.needsReview).length,
  };
}

/* ── sub-components ── */

function SourceBadge({ source }: { source: string }) {
  const badge = SOURCE_BADGES[source] ?? { label: source, color: "bg-gray-500/10 text-gray-600" };
  return (
    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-[10px] font-medium ${badge.color}`}>
      {badge.label}
    </span>
  );
}

function ConfidenceBar({ confidence }: { confidence: number }) {
  const pct = Math.round(confidence * 100);
  const color = confidence >= 0.95 ? "bg-green-500" : confidence >= 0.80 ? "bg-amber-500" : "bg-red-500";
  const textColor = confidence >= 0.95 ? "text-green-600" : confidence >= 0.80 ? "text-amber-600" : "text-red-600";
  return (
    <div className="flex items-center gap-1.5">
      <div className="w-12 h-1.5 rounded-full bg-border overflow-hidden">
        <div className={`h-full rounded-full ${color}`} style={{ width: `${pct}%` }} />
      </div>
      <span className={`text-[10px] font-medium tabular-nums ${textColor}`}>{pct}%</span>
    </div>
  );
}

/* ── main component ── */

export default function DocumentAutoFill() {
  const [step, setStep] = useState<"upload" | "processing" | "results">("upload");
  const [documents, setDocuments] = useState<DocumentUpload[]>(
    SUPPORTED_DOCUMENT_TYPES.map((dt) => ({
      id: dt.type,
      documentType: dt.type,
      file: null,
      status: "pending" as const,
      progress: 0,
    }))
  );
  const [result, setResult] = useState<AutoFillResult | null>(null);
  const resultRef = useRef<HTMLDivElement>(null);

  const handleFileSelect = useCallback((docType: string, file: File) => {
    setDocuments((prev) =>
      prev.map((d) =>
        d.documentType === docType
          ? { ...d, file, status: "complete" as const, progress: 100 }
          : d
      )
    );
  }, []);

  const handleFileClear = useCallback((docType: string) => {
    setDocuments((prev) =>
      prev.map((d) =>
        d.documentType === docType
          ? { ...d, file: null, status: "pending" as const, progress: 0 }
          : d
      )
    );
  }, []);

  const uploadedCount = documents.filter((d) => d.status === "complete").length;

  const handleProcess = useCallback(async () => {
    setStep("processing");

    // Simulate sequential processing
    for (const doc of documents) {
      if (doc.status === "complete") {
        setDocuments((prev) =>
          prev.map((d) =>
            d.documentType === doc.documentType
              ? { ...d, status: "processing" as const }
              : d
          )
        );
        await new Promise((r) => setTimeout(r, 800 + Math.random() * 400));
        setDocuments((prev) =>
          prev.map((d) =>
            d.documentType === doc.documentType
              ? { ...d, status: "complete" as const }
              : d
          )
        );
      }
    }

    const autoFillResult = generateMockAutoFill(documents);
    setResult(autoFillResult);
    setStep("results");
    setTimeout(() => resultRef.current?.scrollIntoView({ behavior: "smooth" }), 200);
  }, [documents]);

  const handleExport = useCallback(async () => {
    if (resultRef.current) {
      await exportPdf(resultRef.current, "verigate-document-auto-fill.pdf");
    }
  }, []);

  const handleReset = useCallback(() => {
    setStep("upload");
    setResult(null);
    setDocuments(
      SUPPORTED_DOCUMENT_TYPES.map((dt) => ({
        id: dt.type,
        documentType: dt.type,
        file: null,
        status: "pending" as const,
        progress: 0,
      }))
    );
  }, []);

  const srMessage = step === "processing"
    ? "Processing documents..."
    : step === "results"
      ? "Auto-fill complete"
      : "";

  return (
    <div className="space-y-6">
      <ScreenReaderAnnounce message={srMessage} />

      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Document Auto-Fill</h1>
        <p className="text-sm text-text-muted">
          Upload multiple documents and let AI extract, validate, and aggregate fields into a
          pre-filled form. Supports identity documents, company registrations, tax certificates, and more.
        </p>
      </header>

      {/* Step indicator */}
      <div className="flex items-center gap-3">
        {[
          { num: 1, label: "Upload Documents", active: step === "upload" },
          { num: 2, label: "AI Processing", active: step === "processing" },
          { num: 3, label: "Review & Export", active: step === "results" },
        ].map((s, i) => (
          <div key={s.num} className="flex items-center gap-2">
            {i > 0 && <div className="h-px w-8 bg-border" />}
            <div
              className={`flex h-6 w-6 items-center justify-center rounded-full text-xs font-medium ${
                s.active
                  ? "bg-primary text-white"
                  : step === "results" || (step === "processing" && s.num < 2)
                    ? "bg-success/10 text-success"
                    : "bg-border text-text-muted"
              }`}
            >
              {(step === "results" || (step === "processing" && s.num === 1)) && s.num < 3 ? (
                <CheckCircle2 className="h-3.5 w-3.5" />
              ) : (
                s.num
              )}
            </div>
            <span className={`text-xs font-medium ${s.active ? "text-text" : "text-text-muted"}`}>
              {s.label}
            </span>
          </div>
        ))}
      </div>

      {/* Upload step */}
      {step === "upload" && (
        <div className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {SUPPORTED_DOCUMENT_TYPES.map((dt) => {
              const doc = documents.find((d) => d.documentType === dt.type);
              const Icon = dt.icon;
              const isUploaded = doc?.status === "complete";

              return (
                <div key={dt.type} className={`console-card ${isUploaded ? "border-success/40" : ""}`}>
                  <div className="console-card-header">
                    <div className="flex items-center gap-2">
                      <div className={`flex h-7 w-7 items-center justify-center rounded-lg ${isUploaded ? "bg-success/10" : "bg-border"}`}>
                        {isUploaded ? (
                          <CheckCircle2 className="h-4 w-4 text-success" />
                        ) : (
                          <Icon className="h-4 w-4 text-text-muted" />
                        )}
                      </div>
                      <div>
                        <div className="text-sm font-medium text-text">{dt.label}</div>
                        <div className="text-[10px] text-text-muted">{dt.description}</div>
                      </div>
                    </div>
                  </div>
                  <div className="console-card-body">
                    <FileUpload
                      accept="image/*,.pdf"
                      maxSize={10 * 1024 * 1024}
                      progress={doc?.progress ?? 0}
                      uploading={doc?.status === "uploading"}
                      onFileSelect={(file) => handleFileSelect(dt.type, file)}
                      onClear={() => handleFileClear(dt.type)}
                    />
                  </div>
                </div>
              );
            })}
          </div>

          <div className="flex items-center justify-between">
            <p className="text-xs text-text-muted">
              {uploadedCount} of {SUPPORTED_DOCUMENT_TYPES.length} documents uploaded
            </p>
            <Button
              variant="cta"
              size="md"
              disabled={uploadedCount < 1}
              onClick={handleProcess}
              icon={<Upload className="h-4 w-4" />}
            >
              Process {uploadedCount} Document{uploadedCount !== 1 ? "s" : ""}
            </Button>
          </div>
        </div>
      )}

      {/* Processing step */}
      {step === "processing" && (
        <div className="console-card">
          <div className="console-card-body space-y-3">
            {documents.filter((d) => d.file).map((doc) => {
              const dt = SUPPORTED_DOCUMENT_TYPES.find((t) => t.type === doc.documentType);
              const Icon = dt?.icon ?? FileText;
              return (
                <div key={doc.documentType} className="flex items-center gap-3">
                  <div className={`flex h-7 w-7 items-center justify-center rounded-lg ${
                    doc.status === "complete" ? "bg-success/10" : "bg-border"
                  }`}>
                    {doc.status === "complete" ? (
                      <CheckCircle2 className="h-4 w-4 text-success" />
                    ) : doc.status === "processing" ? (
                      <Clock className="h-4 w-4 text-primary animate-pulse" />
                    ) : (
                      <Icon className="h-4 w-4 text-text-muted" />
                    )}
                  </div>
                  <div className="flex-1">
                    <div className="text-sm font-medium text-text">{dt?.label}</div>
                    <div className="text-xs text-text-muted">
                      {doc.status === "processing" ? "Analyzing with AI..." : doc.status === "complete" ? "Complete" : "Waiting"}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Results step */}
      {step === "results" && result && (
        <div ref={resultRef} className="space-y-4">
          {/* Summary banner */}
          <div className="console-card border-success/40">
            <div className="console-card-body">
              <div className="flex flex-wrap items-center justify-between gap-4">
                <div className="flex flex-wrap items-center gap-4">
                  <div className="flex items-center gap-2 text-sm font-semibold text-success">
                    <CheckCircle2 className="h-5 w-5" />
                    Auto-Fill Complete
                  </div>
                  <div className="flex flex-wrap gap-3 text-xs">
                    <span className="rounded-full bg-success/10 text-success px-2.5 py-1 font-medium">
                      {result.fieldsAutoFilled}/{result.fieldsAutoFilled + result.fieldsNeedReview} FIELDS AUTO-FILLED
                    </span>
                    {result.fieldsNeedReview > 0 && (
                      <span className="rounded-full bg-amber-500/10 text-amber-600 px-2.5 py-1 font-medium">
                        {result.fieldsNeedReview} NEEDS REVIEW
                      </span>
                    )}
                    <span className="rounded-full bg-blue-500/10 text-blue-600 px-2.5 py-1 font-medium">
                      <Clock className="h-3 w-3 inline mr-1" />
                      Time saved: ~45 min
                    </span>
                  </div>
                </div>
                <div className="flex gap-2">
                  <Button variant="secondary" size="sm" onClick={handleReset}>
                    New Session
                  </Button>
                  <Button
                    variant="cta"
                    size="sm"
                    onClick={handleExport}
                    icon={<FileDown className="h-4 w-4" />}
                  >
                    Export PDF
                  </Button>
                </div>
              </div>
            </div>
          </div>

          {/* Auto-filled form */}
          <div className="console-card">
            <div className="console-card-header">
              <span className="text-sm font-semibold text-text">Extracted Fields</span>
              <span className="text-xs text-text-muted">
                {result.totalDocuments} documents processed
              </span>
            </div>
            <div className="console-card-body p-0">
              <div className="divide-y divide-border">
                {Object.entries(result.fields).map(([key, field]) => (
                  <div
                    key={key}
                    className={`flex items-center gap-3 px-4 py-3 ${
                      field.needsReview ? "bg-amber-500/5" : ""
                    }`}
                  >
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2">
                        <span className="text-xs text-text-muted uppercase tracking-wide">
                          {FIELD_LABELS[key] ?? key}
                        </span>
                        {field.needsReview && (
                          <span className="inline-flex items-center gap-1 rounded-full bg-amber-500/10 text-amber-600 px-1.5 py-0.5 text-[10px] font-medium">
                            <AlertTriangle className="h-2.5 w-2.5" />
                            NEEDS REVIEW
                          </span>
                        )}
                      </div>
                      <div className="text-sm font-medium text-text mt-0.5">{field.value}</div>
                    </div>
                    <div className="flex items-center gap-3 shrink-0">
                      <SourceBadge source={field.sourceDocument} />
                      <ConfidenceBar confidence={field.confidence} />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      <ProcessingDialog
        open={step === "processing"}
        title="Processing documents"
        message="Running AI-powered extraction on each document. This may take a moment."
      />
    </div>
  );
}
