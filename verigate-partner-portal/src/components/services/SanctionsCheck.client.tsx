"use client";

import { useCallback, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { type SanctionsResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { FileDown } from "lucide-react";

export default function SanctionsCheck() {
  const [name, setName] = useState("");
  const [result, setResult] = useState<SanctionsResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleExport = useCallback(() => {
    if (typeof window !== "undefined") {
      window.print();
    }
  }, []);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const data = await executeVerification("SANCTIONS_SCREENING", { name }) as SanctionsResponse;
      setResult(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Screening failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const submitDisabled = loading || name.trim().length < 3;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Sanctions & PEP screening</h1>
        <p className="text-sm text-text-muted">Run a deterministic World-Check style search for politically exposed persons and sanctions hits.</p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Subject</div>
              <div className="text-xs text-text-muted">Provide a full name for screening.</div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <Field label="Full name" description="We recommend including middle names where possible.">
              <input
                value={name}
                onChange={(event) => setName(event.target.value)}
                className="aws-input w-full"
                placeholder="e.g. John Michael Doe"
              />
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">Sandbox returns a consistent PEP result for testing.</p>
              <Button 
                type="submit" 
                variant="cta" 
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Screen
              </Button>
            </div>

            {error && <div className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">{error}</div>}
          </form>
        </div>

        <div className="console-card">
          <div className="console-card-header">
            <div className="text-sm font-semibold text-text">Response fields</div>
          </div>
          <div className="console-card-body space-y-2 text-xs text-text-muted">
            <SchemaRow name="result.pep" type="Boolean" />
            <SchemaRow name="result.sanctionsHitCount" type="Integer" />
          </div>
        </div>
      </div>

      {result && (
        <div className="space-y-4">
          <div className="console-card">
            <div className="console-card-header">
              <div>
                <div className="text-sm font-semibold text-text">Screening summary</div>
                <div className="text-xs text-text-muted">Correlation {result.correlationId}</div>
              </div>
              <Button 
                variant="secondary" 
                size="sm" 
                onClick={handleExport} 
                icon={<FileDown className="h-4 w-4" />}
              >
                Export PDF
              </Button>
            </div>
            <div className="console-card-body grid gap-4 sm:grid-cols-2">
              <SummaryCard label="PEP" value={result.result.pep ? "Yes" : "No"} tone={result.result.pep ? "alert" : "muted"} />
              <SummaryCard label="Sanctions hits" value={String(result.result.sanctionsHitCount)} tone={result.result.sanctionsHitCount ? "alert" : "muted"} />
            </div>
          </div>

          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Raw response</div>
            </div>
            <div className="console-card-body bg-background">
              <JsonViewer data={result} />
            </div>
          </div>
        </div>
      )}
      <ProcessingDialog open={loading} title="Screening subject" message="Checking mock sanctions & PEP datasets..." />
    </div>
  );
}

function SummaryCard({ label, value, tone }: { label: string; value: string; tone: "alert" | "muted" }) {
  const styles = tone === "alert" ? "bg-danger/10 text-danger" : "bg-background text-text";
  return (
    <div className="space-y-1 rounded border border-border bg-background p-3">
      <div className="text-xs uppercase tracking-wide text-text-muted">{label}</div>
      <span className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-medium ${styles}`}>{value}</span>
    </div>
  );
}

interface SchemaRowProps {
  name: string;
  type: string;
}

function SchemaRow({ name, type }: SchemaRowProps) {
  return (
    <div className="flex items-center justify-between rounded border border-transparent px-2 py-1 hover:border-border">
      <span className="font-medium text-text">{name}</span>
      <span>{type}</span>
    </div>
  );
}

interface FieldProps {
  label: string;
  description?: string;
  children: ReactNode;
}

function Field({ label, description, children }: FieldProps) {
  return (
    <label className="block space-y-1 text-sm">
      <span className="font-medium text-text">{label}</span>
      {description && <span className="block text-xs text-text-muted">{description}</span>}
      {children}
    </label>
  );
}
