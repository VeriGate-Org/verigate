"use client";

import { useCallback, useRef, useState } from "react";
import { Button } from "@/components/ui/Button";
import { ScreenReaderAnnounce } from "@/components/ui/ScreenReaderAnnounce";
import { useToast } from "@/components/ui/Toast";
import {
  type BulkDocumentItem,
  type BulkDocumentResult,
  type BulkDocumentSummary,
  type BulkDocumentPhase,
  mockBulkDocumentVerification,
} from "@/lib/mock-services";
import {
  KNOWN_DOCUMENT_TYPES,
  DOCUMENT_TYPE_LABELS,
} from "@/components/services/document-verification/documentFieldConfigs";
import { Upload, Download } from "lucide-react";

export default function BulkDocumentVerification() {
  const [phase, setPhase] = useState<BulkDocumentPhase>("upload");
  const [items, setItems] = useState<BulkDocumentItem[]>([]);
  const [csvText, setCsvText] = useState("");
  const [jobId] = useState(() => `bulk-doc-${Date.now()}`);
  const [results, setResults] = useState<BulkDocumentResult[]>([]);
  const [summary, setSummary] = useState<BulkDocumentSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [validationErrors, setValidationErrors] = useState<string[]>([]);
  const [statusFilter, setStatusFilter] = useState<string>("all");
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { toast } = useToast();

  const parseCsv = useCallback((text: string) => {
    const lines = text.split(/[\n\r]+/).map((l) => l.trim()).filter(Boolean);
    const parsed: BulkDocumentItem[] = [];
    const errors: string[] = [];

    for (let i = 0; i < lines.length; i++) {
      const line = lines[i];
      // Skip header row
      if (i === 0 && line.toLowerCase().includes("document_type")) continue;

      const parts = line.split(",").map((p) => p.trim());
      if (parts.length < 2) {
        errors.push(`Line ${i + 1}: Expected at least 2 columns (document_type,document_number)`);
        continue;
      }

      const [docType, docNumber] = parts;
      if (!KNOWN_DOCUMENT_TYPES.has(docType)) {
        errors.push(`Line ${i + 1}: Unknown document type "${docType}"`);
        continue;
      }
      if (!docNumber || docNumber.length === 0) {
        errors.push(`Line ${i + 1}: Document number is empty`);
        continue;
      }

      parsed.push({ documentType: docType, documentNumber: docNumber });
    }

    setItems(parsed);
    setValidationErrors(errors.slice(0, 10));
  }, []);

  const handleFileUpload = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = (event) => {
        const text = event.target?.result as string;
        setCsvText(text);
        parseCsv(text);
      };
      reader.readAsText(file);
    },
    [parseCsv]
  );

  const handleTextChange = useCallback(
    (text: string) => {
      setCsvText(text);
      parseCsv(text);
    },
    [parseCsv]
  );

  const handleSubmit = useCallback(async () => {
    if (items.length === 0) return;

    setLoading(true);
    setError(null);
    setPhase("processing");
    toast({ title: "Batch job submitted", variant: "success" });

    try {
      const response = await mockBulkDocumentVerification(items);
      setResults(response.results);
      setSummary(response.summary);
      setPhase("results");
      toast({ title: "Batch verification complete", variant: "success" });
    } catch (err) {
      const message = err instanceof Error ? err.message : "Batch verification failed";
      setError(message);
      setPhase("upload");
      toast({ title: "Batch failed", description: message, variant: "error" });
    } finally {
      setLoading(false);
    }
  }, [items, toast]);

  const handleReset = useCallback(() => {
    setPhase("upload");
    setItems([]);
    setCsvText("");
    setResults([]);
    setSummary(null);
    setError(null);
    setValidationErrors([]);
    setStatusFilter("all");
  }, []);

  const filteredResults = results.filter((r) => {
    if (statusFilter === "all") return true;
    if (statusFilter === "verified") return r.status === "VERIFIED";
    if (statusFilter === "not_verified") return r.status === "NOT_VERIFIED";
    if (statusFilter === "errors") return r.status === "ERROR";
    return true;
  });

  const srMessage = loading
    ? "Processing batch verification"
    : phase === "results"
      ? "Batch results ready"
      : "";

  return (
    <div className="space-y-6">
      <ScreenReaderAnnounce message={srMessage} />

      {phase === "upload" && (
        <div className="grid gap-4 lg:grid-cols-2">
          {/* Upload area */}
          <div className="console-card">
            <div className="console-card-header">
              <div>
                <div className="text-sm font-semibold text-text">Upload documents</div>
                <div className="text-xs text-text-muted">
                  CSV format: document_type,document_number (one per line).
                </div>
              </div>
            </div>
            <div className="console-card-body space-y-4">
              <div
                className="border-2 border-dashed border-border rounded-lg p-6 text-center cursor-pointer hover:border-accent transition-colors"
                onClick={() => fileInputRef.current?.click()}
                onDragOver={(e) => e.preventDefault()}
                onDrop={(e) => {
                  e.preventDefault();
                  const file = e.dataTransfer.files[0];
                  if (file && fileInputRef.current) {
                    const dt = new DataTransfer();
                    dt.items.add(file);
                    fileInputRef.current.files = dt.files;
                    fileInputRef.current.dispatchEvent(new Event("change", { bubbles: true }));
                  }
                }}
              >
                <Upload className="mx-auto h-8 w-8 text-text-muted mb-2" />
                <p className="text-sm text-text-muted">Drop a CSV file here or click to browse</p>
                <p className="text-xs text-text-muted mt-1">Max 1,000 documents per batch</p>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept=".csv,.txt"
                  className="hidden"
                  onChange={handleFileUpload}
                />
              </div>

              <div className="text-xs text-text-muted text-center">or paste directly</div>

              <textarea
                value={csvText}
                onChange={(e) => handleTextChange(e.target.value)}
                placeholder={"document_type,document_number\nid_card,9001015009087\npassport,A12345678\ndrivers_license,DL12345678"}
                className="aws-input w-full h-32 font-mono text-xs"
              />

              {items.length > 0 && (
                <div className="flex items-center justify-between text-sm">
                  <span className="text-success font-medium">{items.length} valid documents</span>
                  {validationErrors.length > 0 && (
                    <span className="text-warning">{validationErrors.length} errors</span>
                  )}
                </div>
              )}

              {validationErrors.length > 0 && (
                <div className="text-xs text-danger space-y-1 max-h-20 overflow-y-auto">
                  {validationErrors.map((err, i) => (
                    <div key={i}>{err}</div>
                  ))}
                </div>
              )}

              {error && (
                <div className="text-xs text-danger border border-danger/40 bg-danger/5 rounded px-3 py-2">
                  {error}
                </div>
              )}
            </div>
          </div>

          {/* Template + Submit */}
          <div className="space-y-4">
            <div className="console-card">
              <div className="console-card-header">
                <div>
                  <div className="text-sm font-semibold text-text">CSV template</div>
                  <div className="text-xs text-text-muted">Download a template to get started.</div>
                </div>
              </div>
              <div className="console-card-body space-y-3">
                <div className="text-xs text-text-muted font-mono bg-base-200/50 rounded p-3 overflow-x-auto">
                  document_type,document_number<br />
                  id_card,9001015009087<br />
                  passport,A12345678<br />
                  drivers_license,DL12345678
                </div>
                <p className="text-xs text-text-muted">
                  Valid document types: id_card, passport, drivers_license, asylum_seeker_permit,
                  general_work_permit, b_bbee_certificate, cipc_registration, tax_certificate,
                  financial_statement, utility_bill.
                </p>
                <button
                  onClick={() => {
                    const csv = "document_type,document_number\nid_card,9001015009087\npassport,A12345678\n";
                    const blob = new Blob([csv], { type: "text/csv" });
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement("a");
                    a.href = url;
                    a.download = "document-verification-template.csv";
                    a.click();
                    URL.revokeObjectURL(url);
                  }}
                  className="flex items-center gap-1.5 px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  <Download className="w-4 h-4" /> Download template
                </button>
              </div>
            </div>

            <div className="console-card">
              <div className="console-card-body flex items-center justify-between">
                <div>
                  <div className="text-sm font-medium text-text">
                    {items.length > 0 ? `${items.length} documents ready` : "No documents loaded"}
                  </div>
                  <div className="text-xs text-text-muted">
                    {items.length > 0
                      ? `Estimated cost: ${items.length} units`
                      : "Upload a file or paste CSV to begin"}
                  </div>
                </div>
                <Button
                  variant="cta"
                  size="md"
                  disabled={loading || items.length === 0}
                  loading={loading}
                  onClick={handleSubmit}
                >
                  Start Verification
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}

      {phase === "processing" && (
        <div className="console-card max-w-lg mx-auto">
          <div className="console-card-header">
            <div className="text-sm font-semibold text-text">Processing batch verification</div>
          </div>
          <div className="console-card-body space-y-4">
            <div className="flex items-center justify-between">
              {[
                { label: "Uploaded", done: true },
                { label: "Queued", done: true },
                { label: "Processing", done: false },
                { label: "Complete", done: false },
              ].map((step, i, arr) => (
                <div key={step.label} className="flex items-center gap-2">
                  <div className={`h-3 w-3 rounded-full ${step.done ? "bg-success" : "bg-border animate-pulse"}`} />
                  <span className={`text-xs ${step.done ? "text-success" : "text-text-muted"}`}>
                    {step.label}
                  </span>
                  {i < arr.length - 1 && (
                    <div className={`h-px w-8 ${step.done ? "bg-success" : "bg-border"}`} />
                  )}
                </div>
              ))}
            </div>
            <div className="text-xs text-text-muted text-center">
              Job ID: {jobId} &middot; {items.length} documents submitted
            </div>
          </div>
        </div>
      )}

      {phase === "results" && summary && (
        <div className="space-y-4">
          {/* Summary Bar */}
          <div className="console-card">
            <div className="console-card-body flex flex-wrap items-center gap-4">
              <SummaryBadge label="Total" count={summary.total} className="bg-base-200 text-text" />
              <SummaryBadge label="Verified" count={summary.verified} className="bg-success/10 text-success" />
              <SummaryBadge label="Not Verified" count={summary.notVerified} className="bg-warning/10 text-warning" />
              <SummaryBadge label="Errors" count={summary.errors} className="bg-danger/10 text-danger" />
              <div className="ml-auto">
                <Button variant="secondary" size="sm" onClick={handleReset} icon={<Upload className="h-4 w-4" />}>
                  New Batch
                </Button>
              </div>
            </div>
          </div>

          {/* Filter */}
          <div className="flex gap-2 text-xs">
            {["all", "verified", "not_verified", "errors"].map((f) => (
              <button
                key={f}
                onClick={() => setStatusFilter(f)}
                className={`px-3 py-1.5 rounded-full border transition-colors ${
                  statusFilter === f
                    ? "bg-accent text-white border-accent"
                    : "bg-transparent text-text-muted border-border hover:border-accent"
                }`}
              >
                {f === "all"
                  ? "All"
                  : f === "not_verified"
                    ? "Not Verified"
                    : f.charAt(0).toUpperCase() + f.slice(1)}
              </button>
            ))}
          </div>

          {/* Results Table */}
          <div className="console-card overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border bg-base-200/50">
                    <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase">Document Type</th>
                    <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase">Document Number</th>
                    <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase">Status</th>
                    <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase">Confidence</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {filteredResults.map((r, idx) => (
                    <tr key={`${r.documentNumber}-${idx}`} className="hover:bg-hover/50">
                      <td className="px-4 py-2.5">
                        {DOCUMENT_TYPE_LABELS[r.documentType] ?? r.documentType}
                      </td>
                      <td className="px-4 py-2.5 font-mono text-xs">{r.documentNumber}</td>
                      <td className="px-4 py-2.5">
                        <StatusBadge status={r.status} />
                      </td>
                      <td className="px-4 py-2.5 tabular-nums text-gray-600">
                        {r.status === "ERROR" ? "—" : `${Math.round(r.confidence * 100)}%`}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {filteredResults.length === 0 && (
              <div className="text-center py-8 text-text-muted text-sm">
                No results match the selected filter.
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

function SummaryBadge({ label, count, className }: { label: string; count: number; className: string }) {
  return (
    <div className={`px-3 py-1.5 rounded-lg text-sm font-medium ${className}`}>
      {label}: {count}
    </div>
  );
}

function StatusBadge({ status }: { status: string }) {
  switch (status) {
    case "VERIFIED":
      return <span className="px-2 py-0.5 text-xs rounded-full bg-green-100 text-green-800">Verified</span>;
    case "NOT_VERIFIED":
      return <span className="px-2 py-0.5 text-xs rounded-full bg-amber-100 text-amber-800">Not Verified</span>;
    case "ERROR":
      return <span className="px-2 py-0.5 text-xs rounded-full bg-red-100 text-red-800">Error</span>;
    default:
      return <span className="px-2 py-0.5 text-xs rounded-full bg-gray-100 text-gray-800">{status}</span>;
  }
}
