"use client";

import { useCallback, useRef, useState } from "react";
import { Button } from "@/components/ui/Button";
import { ScreenReaderAnnounce } from "@/components/ui/ScreenReaderAnnounce";
import { useToast } from "@/components/ui/Toast";
import { validateSaId } from "@/lib/utils/sa-id-validation";
import { mockBulkCreate, mockBulkResults } from "@/lib/mock-services";
import {
  type BillingGroupSelection,
  type BulkVerificationJob,
  type BulkVerificationResult,
  type BulkResultsSummary,
  DEFAULT_BILLING_GROUPS,
  BILLING_GROUP_LABELS,
} from "@/lib/types/bulk-identity-verification";
import { Upload, FileText, Download, ChevronDown, ChevronRight } from "lucide-react";

type Phase = "upload" | "processing" | "results";

export default function BulkIdentityVerification() {
  const [phase, setPhase] = useState<Phase>("upload");
  const [idNumbers, setIdNumbers] = useState<string[]>([]);
  const [csvText, setCsvText] = useState("");
  const [billingGroups, setBillingGroups] = useState<BillingGroupSelection>(DEFAULT_BILLING_GROUPS);
  const [job, setJob] = useState<BulkVerificationJob | null>(null);
  const [results, setResults] = useState<BulkVerificationResult[]>([]);
  const [summary, setSummary] = useState<BulkResultsSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [validationErrors, setValidationErrors] = useState<string[]>([]);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { toast } = useToast();

  const handleFileUpload = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      const text = event.target?.result as string;
      setCsvText(text);
      parseIds(text);
    };
    reader.readAsText(file);
  }, []);

  const parseIds = useCallback((text: string) => {
    const lines = text.split(/[\n\r]+/).map(l => l.trim()).filter(Boolean);
    const ids: string[] = [];
    const errors: string[] = [];

    for (const line of lines) {
      // Extract 13-digit numbers from each line
      const match = line.match(/(\d{13})/);
      if (match) {
        const id = match[1];
        const validation = validateSaId(id);
        if (validation.valid) {
          ids.push(id);
        } else {
          errors.push(`${id}: ${validation.errors[0]}`);
        }
      } else if (/\d/.test(line)) {
        errors.push(`Invalid format: ${line.substring(0, 30)}`);
      }
    }

    setIdNumbers(ids);
    setValidationErrors(errors.slice(0, 10)); // Show first 10 errors
  }, []);

  const handleTextChange = useCallback((text: string) => {
    setCsvText(text);
    parseIds(text);
  }, [parseIds]);

  const handleSubmit = useCallback(async () => {
    if (idNumbers.length === 0) return;

    setLoading(true);
    setError(null);

    try {
      const response = await mockBulkCreate({ idNumbers, billingGroups });
      setJob(response.job);
      setPhase("processing");
      toast({ title: "Bulk job submitted", variant: "success" });

      // Simulate processing completion after a delay
      setTimeout(async () => {
        try {
          const resultResponse = await mockBulkResults(idNumbers);
          setResults(resultResponse.results);
          setSummary(resultResponse.summary);
          setPhase("results");
          toast({ title: "Bulk verification complete", variant: "success" });
        } catch {
          setError("Failed to retrieve results");
        }
        setLoading(false);
      }, 2000);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Submission failed";
      setError(message);
      setLoading(false);
      toast({ title: "Submission failed", description: message, variant: "error" });
    }
  }, [idNumbers, billingGroups, toast]);

  const handleReset = useCallback(() => {
    setPhase("upload");
    setIdNumbers([]);
    setCsvText("");
    setJob(null);
    setResults([]);
    setSummary(null);
    setError(null);
    setValidationErrors([]);
  }, []);

  const srMessage = loading ? "Processing bulk verification" : phase === "results" ? "Results ready" : "";

  return (
    <div className="space-y-6">
      <ScreenReaderAnnounce message={srMessage} />

      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Bulk identity verification</h1>
        <p className="text-sm text-text-muted">
          Upload a CSV file of South African ID numbers for bulk verification against the HANIS National Population Register.
        </p>
      </header>

      {phase === "upload" && (
        <UploadPhase
          csvText={csvText}
          idNumbers={idNumbers}
          validationErrors={validationErrors}
          billingGroups={billingGroups}
          loading={loading}
          error={error}
          fileInputRef={fileInputRef}
          onFileUpload={handleFileUpload}
          onTextChange={handleTextChange}
          onBillingGroupChange={setBillingGroups}
          onSubmit={handleSubmit}
        />
      )}

      {phase === "processing" && job && (
        <ProcessingPhase job={job} />
      )}

      {phase === "results" && summary && (
        <ResultsPhase
          results={results}
          summary={summary}
          job={job}
          onReset={handleReset}
        />
      )}
    </div>
  );
}

// ----- Upload Phase -----

function UploadPhase({
  csvText, idNumbers, validationErrors, billingGroups, loading, error,
  fileInputRef, onFileUpload, onTextChange, onBillingGroupChange, onSubmit,
}: {
  csvText: string;
  idNumbers: string[];
  validationErrors: string[];
  billingGroups: BillingGroupSelection;
  loading: boolean;
  error: string | null;
  fileInputRef: React.RefObject<HTMLInputElement | null>;
  onFileUpload: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onTextChange: (text: string) => void;
  onBillingGroupChange: (groups: BillingGroupSelection) => void;
  onSubmit: () => void;
}) {
  return (
    <div className="grid gap-4 lg:grid-cols-2">
      {/* Left: Upload */}
      <div className="console-card">
        <div className="console-card-header">
          <div>
            <div className="text-sm font-semibold text-text">Upload ID numbers</div>
            <div className="text-xs text-text-muted">CSV file or paste ID numbers (one per line).</div>
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
            <p className="text-sm text-text-muted">
              Drop a CSV file here or click to browse
            </p>
            <p className="text-xs text-text-muted mt-1">Max 200,000 ID numbers</p>
            <input
              ref={fileInputRef}
              type="file"
              accept=".csv,.txt"
              className="hidden"
              onChange={onFileUpload}
            />
          </div>

          <div className="text-xs text-text-muted text-center">or paste directly</div>

          <textarea
            value={csvText}
            onChange={(e) => onTextChange(e.target.value)}
            placeholder="9001015009087&#10;8502115009083&#10;7803025009089"
            className="aws-input w-full h-32 font-mono text-xs"
          />

          {idNumbers.length > 0 && (
            <div className="flex items-center justify-between text-sm">
              <span className="text-success font-medium">{idNumbers.length} valid ID numbers</span>
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

      {/* Right: Billing Groups + Submit */}
      <div className="space-y-4">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Billing groups</div>
              <div className="text-xs text-text-muted">Select the data categories to include.</div>
            </div>
          </div>
          <div className="console-card-body">
            <div className="grid grid-cols-2 gap-2">
              {(Object.keys(billingGroups) as Array<keyof BillingGroupSelection>).map((key) => (
                <label key={key} className="flex items-center gap-2 text-sm text-text cursor-pointer">
                  <input
                    type="checkbox"
                    checked={billingGroups[key]}
                    onChange={(e) => onBillingGroupChange({ ...billingGroups, [key]: e.target.checked })}
                    className="rounded border-border"
                  />
                  {BILLING_GROUP_LABELS[key]}
                </label>
              ))}
            </div>
          </div>
        </div>

        <div className="console-card">
          <div className="console-card-body flex items-center justify-between">
            <div>
              <div className="text-sm font-medium text-text">
                {idNumbers.length > 0 ? `${idNumbers.length} IDs ready` : "No IDs loaded"}
              </div>
              <div className="text-xs text-text-muted">
                {idNumbers.length > 0
                  ? `Estimated cost: ${idNumbers.length} units`
                  : "Upload a file or paste ID numbers to begin"}
              </div>
            </div>
            <Button
              variant="cta"
              size="md"
              disabled={loading || idNumbers.length === 0}
              loading={loading}
              onClick={onSubmit}
            >
              Submit bulk job
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

// ----- Processing Phase -----

function ProcessingPhase({ job }: { job: BulkVerificationJob }) {
  const steps = [
    { label: "Uploaded", done: true },
    { label: "Queued", done: job.status !== "UPLOADED" },
    { label: "Processing", done: job.status === "COMPLETED" || job.status === "DOWNLOADED" },
    { label: "Ready", done: job.status === "COMPLETED" || job.status === "DOWNLOADED" },
  ];

  return (
    <div className="console-card max-w-lg mx-auto">
      <div className="console-card-header">
        <div className="text-sm font-semibold text-text">Processing bulk verification</div>
      </div>
      <div className="console-card-body space-y-4">
        <div className="flex items-center justify-between">
          {steps.map((step, i) => (
            <div key={step.label} className="flex items-center gap-2">
              <div className={`h-3 w-3 rounded-full ${step.done ? "bg-success" : "bg-border animate-pulse"}`} />
              <span className={`text-xs ${step.done ? "text-success" : "text-text-muted"}`}>
                {step.label}
              </span>
              {i < steps.length - 1 && (
                <div className={`h-px w-8 ${step.done ? "bg-success" : "bg-border"}`} />
              )}
            </div>
          ))}
        </div>
        <div className="text-xs text-text-muted text-center">
          Job ID: {job.jobId} &middot; {job.idCount} IDs submitted
        </div>
      </div>
    </div>
  );
}

// ----- Results Phase -----

function ResultsPhase({
  results, summary, job, onReset,
}: {
  results: BulkVerificationResult[];
  summary: BulkResultsSummary;
  job: BulkVerificationJob | null;
  onReset: () => void;
}) {
  const [expandedRow, setExpandedRow] = useState<number | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>("all");

  const filteredResults = results.filter((r) => {
    if (statusFilter === "all") return true;
    if (statusFilter === "verified") return r.success;
    if (statusFilter === "not_found") return r.errorCode === 800;
    if (statusFilter === "deceased") return r.dateOfDeath !== "";
    if (statusFilter === "errors") return r.errorCode !== 0 && r.errorCode !== 800;
    return true;
  });

  return (
    <div className="space-y-4">
      {/* Summary Bar */}
      <div className="console-card">
        <div className="console-card-body flex flex-wrap items-center gap-4">
          <SummaryBadge label="Total" count={summary.total} className="bg-base-200 text-text" />
          <SummaryBadge label="Verified" count={summary.verified} className="bg-success/10 text-success" />
          <SummaryBadge label="Not Found" count={summary.notFound} className="bg-warning/10 text-warning" />
          <SummaryBadge label="Deceased" count={summary.deceased} className="bg-danger/10 text-danger" />
          <SummaryBadge label="Errors" count={summary.errors} className="bg-danger/10 text-danger" />
          <div className="ml-auto flex gap-2">
            <Button variant="secondary" size="sm" onClick={onReset} icon={<Upload className="h-4 w-4" />}>
              New job
            </Button>
          </div>
        </div>
      </div>

      {/* Filter */}
      <div className="flex gap-2 text-xs">
        {["all", "verified", "not_found", "deceased", "errors"].map((f) => (
          <button
            key={f}
            onClick={() => setStatusFilter(f)}
            className={`px-3 py-1.5 rounded-full border transition-colors ${
              statusFilter === f
                ? "bg-accent text-white border-accent"
                : "bg-transparent text-text-muted border-border hover:border-accent"
            }`}
          >
            {f === "all" ? "All" : f === "not_found" ? "Not Found" : f.charAt(0).toUpperCase() + f.slice(1)}
          </button>
        ))}
      </div>

      {/* Results Table */}
      <div className="console-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-base-200/50">
                <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase">ID Number</th>
                <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase">Name</th>
                <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase">Surname</th>
                <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase">Status</th>
                <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase">Gender</th>
                <th className="px-4 py-2.5 w-8" />
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {filteredResults.map((r, idx) => (
                <ResultRow
                  key={r.idNumber + idx}
                  result={r}
                  expanded={expandedRow === idx}
                  onToggle={() => setExpandedRow(expandedRow === idx ? null : idx)}
                />
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
  );
}

function SummaryBadge({ label, count, className }: { label: string; count: number; className: string }) {
  return (
    <div className={`px-3 py-1.5 rounded-lg text-sm font-medium ${className}`}>
      {label}: {count}
    </div>
  );
}

function ResultRow({
  result, expanded, onToggle,
}: {
  result: BulkVerificationResult;
  expanded: boolean;
  onToggle: () => void;
}) {
  const statusBadge = result.success
    ? { text: "Verified", className: "text-success" }
    : result.errorCode === 800
      ? { text: "Not Found", className: "text-warning" }
      : result.dateOfDeath
        ? { text: "Deceased", className: "text-danger" }
        : { text: `Error (${result.errorCode})`, className: "text-danger" };

  return (
    <>
      <tr className="hover:bg-hover/50 cursor-pointer" onClick={onToggle}>
        <td className="px-4 py-2.5 font-mono text-xs">{result.idNumber}</td>
        <td className="px-4 py-2.5">{result.names || "—"}</td>
        <td className="px-4 py-2.5">{result.surname || "—"}</td>
        <td className={`px-4 py-2.5 font-medium ${statusBadge.className}`}>{statusBadge.text}</td>
        <td className="px-4 py-2.5">{result.gender || "—"}</td>
        <td className="px-4 py-2.5">
          {expanded ? <ChevronDown className="h-4 w-4 text-text-muted" /> : <ChevronRight className="h-4 w-4 text-text-muted" />}
        </td>
      </tr>
      {expanded && (
        <tr>
          <td colSpan={6} className="px-4 py-3 bg-base-200/30">
            <div className="grid grid-cols-2 md:grid-cols-3 gap-3 text-xs">
              <DetailItem label="Date of Birth" value={result.dateOfBirth} />
              <DetailItem label="Birth Country" value={result.birthCountry} />
              <DetailItem label="Citizen Status" value={result.citizenStatus} />
              <DetailItem label="Nationality" value={result.nationality} />
              <DetailItem label="Smart Card" value={result.smartCardIssued ? "Yes" : "No"} />
              <DetailItem label="ID Card Issue Date" value={result.idCardIssueDate} />
              <DetailItem label="ID Book" value={result.idBookIssued ? "Yes" : "No"} />
              <DetailItem label="ID Blocked" value={result.idBlocked ? "Yes" : "No"} highlight={result.idBlocked} />
              <DetailItem label="Marital Status" value={result.maritalStatus} />
              <DetailItem label="Maiden Name" value={result.maidenName} />
              <DetailItem label="Marriage Date" value={result.marriageDate} />
              <DetailItem label="Divorce Date" value={result.divorceDate} />
              {result.dateOfDeath && (
                <>
                  <DetailItem label="Date of Death" value={result.dateOfDeath} highlight />
                  <DetailItem label="Death Place" value={result.deathPlace} />
                  <DetailItem label="Cause of Death" value={result.causeOfDeath} />
                </>
              )}
            </div>
          </td>
        </tr>
      )}
    </>
  );
}

function DetailItem({ label, value, highlight = false }: { label: string; value?: string; highlight?: boolean }) {
  if (!value) return null;
  return (
    <div>
      <div className="text-text-muted">{label}</div>
      <div className={`font-medium ${highlight ? "text-danger" : "text-text"}`}>{value}</div>
    </div>
  );
}
