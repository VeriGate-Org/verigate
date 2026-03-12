"use client";

import { useCallback, useMemo, useRef, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { VerificationResultCard } from "@/components/verification/VerificationResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { RetryButton } from "@/components/verification/RetryButton";
import { type CompanyResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { Building2 } from "lucide-react";

export default function CompanyVerification() {
  const [regNumber, setRegNumber] = useState("");
  const [name, setName] = useState("");
  const [result, setResult] = useState<CompanyResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);

  const handleExport = useCallback(() => {
    if (typeof window !== "undefined") {
      window.print();
    }
  }, []);

  const canSubmit = useMemo(
    () => Boolean(regNumber.trim()) || name.trim().length > 2,
    [regNumber, name]
  );

  const doVerification = useCallback(async () => {
    if (!canSubmit) return;
    setLoading(true);
    setError(null);

    try {
      const data = (await executeVerification("COMPANY_VERIFICATION", {
        regNumber,
        name,
      })) as CompanyResponse;
      setResult(data);
      setTimeout(() => resultRef.current?.focus(), 100);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  }, [regNumber, name, canSubmit]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled = loading || !canSubmit;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">CIPC company & director search</h1>
        <p className="text-sm text-text-muted">
          Pull registration details and current directors for a South African entity.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,460px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Search criteria</div>
              <div className="text-xs text-text-muted">
                Provide a registration number or company name.
              </div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <Field
              label="Registration number"
              description="Format: YYYY/######_##. Optional if company name provided."
            >
              <input
                id="regNumber"
                name="regNumber"
                value={regNumber}
                onChange={(event) => setRegNumber(event.target.value)}
                className="aws-input w-full"
                placeholder="2014/123456/07"
              />
            </Field>

            <Field
              label="Company name"
              description="Minimum three characters if registration number omitted."
            >
              <input
                id="companyName"
                name="companyName"
                value={name}
                onChange={(event) => setName(event.target.value)}
                className="aws-input w-full"
                placeholder="Acme Holdings"
              />
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Results include entity status, filings, and directors.
              </p>
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

            {error && (
              <div className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">
                {error}
              </div>
            )}
          </form>
        </div>

        <div ref={resultRef} tabIndex={-1} className="outline-none">
          {loading ? (
            <div className="console-card">
              <div className="console-card-header">
                <div>
                  <Skeleton className="h-5 w-32 mb-2" />
                  <Skeleton className="h-3 w-48" />
                </div>
                <Skeleton className="h-9 w-28 rounded-full" />
              </div>
              <div className="console-card-body space-y-6 p-4">
                <div className="grid grid-cols-2 gap-4">
                  {[...Array(6)].map((_, i) => (
                    <div key={i} className="border border-border rounded p-3">
                      <Skeleton className="h-3 w-20 mb-2" />
                      <Skeleton className="h-4 w-28" />
                    </div>
                  ))}
                </div>
              </div>
            </div>
          ) : error && !result ? (
            <div className="console-card">
              <div className="console-card-body flex items-center justify-between">
                <span className="text-sm text-danger">{error}</span>
                <RetryButton onRetry={doVerification} />
              </div>
            </div>
          ) : result ? (
            <VerificationResultCard
              title="Entity summary"
              reference={result.reference}
              status="verified"
              onExport={handleExport}
              fields={[
                { label: "Company", value: result.entity.name },
                { label: "Registration", value: result.entity.regNumber },
                { label: "Status", value: result.entity.status },
                { label: "Incorporated", value: formatDate(result.entity.incorporationDate) },
                {
                  label: "Last annual return",
                  value: formatDate(result.entity.lastAnnualReturn),
                },
                { label: "Registered address", value: result.entity.registeredAddress },
              ]}
              matchFields={[
                {
                  label: result.complianceFlags.annualReturnOverdue
                    ? "Annual return overdue"
                    : "Annual return current",
                  matched: !result.complianceFlags.annualReturnOverdue,
                },
                {
                  label: result.complianceFlags.deregistrationPending
                    ? "Deregistration pending"
                    : "No deregistration pending",
                  matched: !result.complianceFlags.deregistrationPending,
                },
                {
                  label: result.complianceFlags.addressMismatch
                    ? "Address mismatch"
                    : "Address verified",
                  matched: !result.complianceFlags.addressMismatch,
                },
              ]}
            >
              {/* Directors Table */}
              <div className="mt-4 border-t border-border pt-3">
                <h4 className="text-xs font-medium text-text-muted uppercase tracking-wide mb-2">
                  Directors ({result.directors.length})
                </h4>
                <div className="overflow-x-auto border border-border rounded">
                  <table className="min-w-full text-left text-sm">
                    <thead className="bg-background text-xs uppercase tracking-wide text-text-muted">
                      <tr>
                        <th scope="col" className="px-3 py-2">Name</th>
                        <th scope="col" className="px-3 py-2">ID number</th>
                        <th scope="col" className="px-3 py-2">Appointed</th>
                        <th scope="col" className="px-3 py-2">Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {result.directors.map((director) => (
                        <tr
                          key={director.idNumber}
                          className="border-b border-border last:border-0"
                        >
                          <td className="px-3 py-2 font-medium text-text">
                            {director.name}
                          </td>
                          <td className="px-3 py-2 text-text-muted">
                            {director.idNumber}
                          </td>
                          <td className="px-3 py-2 text-text-muted">
                            {formatDate(director.appointed)}
                          </td>
                          <td className="px-3 py-2 text-text">{director.status}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </VerificationResultCard>
          ) : (
            <VerificationEmptyState
              icon={Building2}
              heading="No results yet"
              description="Enter a registration number or company name and click Search to see results."
            />
          )}
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Searching CIPC"
        message="Generating deterministic company registry data..."
      />
    </div>
  );
}

function formatDate(iso: string) {
  if (!iso) return "—";
  const date = new Date(iso);
  return Number.isNaN(date.getTime()) ? "—" : date.toLocaleDateString();
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
