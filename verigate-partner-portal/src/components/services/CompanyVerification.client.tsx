"use client";

import { useCallback, useMemo, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { type CompanyResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { FileDown } from "lucide-react";

export default function CompanyVerification() {
  const [regNumber, setRegNumber] = useState("");
  const [name, setName] = useState("");
  const [result, setResult] = useState<CompanyResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleExport = useCallback(() => {
    if (typeof window !== "undefined") {
      window.print();
    }
  }, []);

  const canSubmit = useMemo(() => Boolean(regNumber.trim()) || name.trim().length > 2, [regNumber, name]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!canSubmit) return;
    setLoading(true);
    setError(null);

    try {
      const data = await executeVerification("COMPANY_VERIFICATION", { regNumber, name }) as CompanyResponse;
      setResult(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const submitDisabled = loading || !canSubmit;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">CIPC company & director search</h1>
        <p className="text-sm text-text-muted">Pull registration details and current directors for a South African entity.</p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,460px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Search criteria</div>
              <div className="text-xs text-text-muted">Provide a registration number or company name.</div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <Field label="Registration number" description="Format: YYYY/######_##. Optional if company name provided.">
              <input
                value={regNumber}
                onChange={(event) => setRegNumber(event.target.value)}
                className="aws-input w-full"
                placeholder="2014/123456/07"
              />
            </Field>

            <Field label="Company name" description="Minimum three characters if registration number omitted.">
              <input
                value={name}
                onChange={(event) => setName(event.target.value)}
                className="aws-input w-full"
                placeholder="Acme Holdings"
              />
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">Results include entity status, filings, and directors.</p>
              <Button 
                type="submit" 
                variant="cta" 
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Search
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
            <SchemaRow name="entity.status" type="Enum" />
            <SchemaRow name="entity.incorporationDate" type="ISO date" />
            <SchemaRow name="entity.lastAnnualReturn" type="ISO date" />
            <SchemaRow name="directors[]" type="Array<Director>" />
            <SchemaRow name="complianceFlags" type="Object" />
          </div>
        </div>
      </div>

  {result && (
        <div className="space-y-4">
          <div className="console-card">
            <div className="console-card-header">
              <div>
                <div className="text-sm font-semibold text-text">Entity summary</div>
                <div className="text-xs text-text-muted">Reference {result.reference}</div>
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
            <div className="console-card-body grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              <SummaryCard label="Company" value={result.entity.name} />
              <SummaryCard label="Registration" value={result.entity.regNumber} />
              <SummaryCard label="Status" value={result.entity.status} />
              <SummaryCard label="Incorporated" value={formatDate(result.entity.incorporationDate)} />
              <SummaryCard label="Last annual return" value={formatDate(result.entity.lastAnnualReturn)} />
              <SummaryCard label="Registered address" value={result.entity.registeredAddress} />
            </div>
          </div>

          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Compliance flags</div>
            </div>
            <div className="console-card-body grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
              <FlagPill label="Annual return overdue" active={result.complianceFlags.annualReturnOverdue} />
              <FlagPill label="Deregistration pending" active={result.complianceFlags.deregistrationPending} />
              <FlagPill label="Address mismatch" active={result.complianceFlags.addressMismatch} />
            </div>
          </div>

          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Directors ({result.directors.length})</div>
            </div>
            <div className="console-card-body p-0">
              <div className="overflow-x-auto">
                <table className="min-w-full text-left text-sm">
                  <thead className="bg-background text-xs uppercase tracking-wide text-text-muted">
                    <tr>
                      <th className="px-3 py-2">Name</th>
                      <th className="px-3 py-2">ID number</th>
                      <th className="px-3 py-2">Appointed</th>
                      <th className="px-3 py-2">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {result.directors.map((director) => (
                      <tr key={director.idNumber} className="border-b border-border last:border-0">
                        <td className="px-3 py-2 font-medium text-text">{director.name}</td>
                        <td className="px-3 py-2 text-text-muted">{director.idNumber}</td>
                        <td className="px-3 py-2 text-text-muted">{formatDate(director.appointed)}</td>
                        <td className="px-3 py-2 text-text">{director.status}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Raw response</div>
              <div className="text-xs text-text-muted">Generated {formatDateTime(result.generatedAt)}</div>
            </div>
            <div className="console-card-body bg-background">
              <JsonViewer data={result} />
            </div>
          </div>
        </div>
      )}
      <ProcessingDialog open={loading} title="Searching CIPC" message="Generating deterministic company registry data..." />
    </div>
  );
}

function formatDate(iso: string) {
  if (!iso) return "—";
  const date = new Date(iso);
  return Number.isNaN(date.getTime()) ? "—" : date.toLocaleDateString();
}

function formatDateTime(iso: string) {
  if (!iso) return "—";
  const date = new Date(iso);
  return Number.isNaN(date.getTime()) ? "—" : date.toLocaleString();
}

function FlagPill({ label, active }: { label: string; active: boolean }) {
  return (
    <div
      className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-medium ${
        active ? "bg-danger/10 text-danger" : "bg-background text-text-muted"
      }`}
    >
      {label}
    </div>
  );
}

function SummaryCard({ label, value }: { label: string; value: ReactNode }) {
  return (
    <div className="space-y-1 rounded border border-border bg-background p-3">
      <div className="text-xs uppercase tracking-wide text-text-muted">{label}</div>
      <div className="text-sm font-medium text-text">{value}</div>
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
