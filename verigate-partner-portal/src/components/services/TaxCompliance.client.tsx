"use client";

import { useCallback, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { executeVerification } from "@/lib/services/verification-service";
import { type TaxComplianceResponse } from "@/lib/mock-services";

export default function TaxCompliance() {
  const [taxReferenceNumber, setTaxReferenceNumber] = useState("");
  const [result, setResult] = useState<TaxComplianceResponse | null>(null);
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
      const data = (await executeVerification("TAX_COMPLIANCE_VERIFICATION", {
        taxReferenceNumber,
      })) as TaxComplianceResponse;
      setResult(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const submitDisabled = loading || taxReferenceNumber.length < 10;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Tax compliance verification</h1>
        <p className="text-sm text-text-muted">
          Verify tax compliance status with the South African Revenue Service.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Tax details</div>
              <div className="text-xs text-text-muted">Enter the SARS tax reference number.</div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <Field label="Tax reference number" description="SARS tax reference (minimum 10 characters).">
              <input
                required
                value={taxReferenceNumber}
                onChange={(event) => setTaxReferenceNumber(event.target.value)}
                className="aws-input w-full"
                minLength={10}
                autoComplete="off"
              />
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Compliance status verified against SARS records.
              </p>
              <Button
                type="submit"
                variant="cta"
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Verify
              </Button>
            </div>

            {error && (
              <div className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">
                {error}
              </div>
            )}
          </form>
        </div>

        {/* Results Panel */}
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
              <div className="border border-border rounded overflow-hidden">
                <div className="divide-y divide-border">
                  {[...Array(5)].map((_, i) => (
                    <div key={i} className="flex items-center px-4 py-2.5">
                      <Skeleton className="h-4 w-32 bg-background/50" />
                      <Skeleton className="h-4 w-24 ml-auto" />
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        ) : result ? (
          <div className="console-card">
            <div className="console-card-header">
              <div>
                <div className="text-sm font-semibold text-text">Compliance results</div>
                <div className="text-xs text-text-muted">Reference {result.reference}</div>
              </div>
              <button
                onClick={handleExport}
                className="rounded-full border border-[color:var(--color-cta)] bg-[color:var(--color-base-100)] text-[color:var(--color-cta)] hover:bg-[color:var(--color-cta)] hover:text-white px-aws-l py-aws-s text-sm transition-all shadow-sm"
              >
                Export PDF
              </button>
            </div>
            <div className="console-card-body space-y-6 p-4">
              <div className="border border-border rounded overflow-hidden">
                <table className="w-full">
                  <tbody className="divide-y divide-border">
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30 w-1/3">
                        Status
                      </td>
                      <td className="px-4 py-2.5 text-sm font-medium text-text">
                        {result.compliance.status}
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Compliant
                      </td>
                      <td className="px-4 py-2.5 text-sm">
                        <span
                          className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                            result.compliance.compliant
                              ? "bg-success/10 text-success"
                              : "bg-danger/10 text-danger"
                          }`}
                        >
                          {result.compliance.compliant ? "Yes" : "No"}
                        </span>
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Last filing date
                      </td>
                      <td className="px-4 py-2.5 text-sm font-medium text-text">
                        {new Date(result.compliance.lastFilingDate).toLocaleDateString("en-ZA")}
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Tax clearance certificate
                      </td>
                      <td className="px-4 py-2.5 text-sm">
                        <span
                          className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                            result.compliance.taxClearanceCertificate
                              ? "bg-success/10 text-success"
                              : "bg-danger/10 text-danger"
                          }`}
                        >
                          {result.compliance.taxClearanceCertificate ? "Yes" : "No"}
                        </span>
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Outstanding returns
                      </td>
                      <td className="px-4 py-2.5 text-sm font-medium text-text">
                        {result.compliance.outstandingReturns}
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              {/* Provider info */}
              <div className="flex items-start gap-2 px-1 py-2 text-xs text-text-muted border-t border-border">
                <span className="font-medium">Provider:</span>
                <span>{result.provider}</span>
              </div>
            </div>
          </div>
        ) : (
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Compliance results</div>
            </div>
            <div className="console-card-body flex items-center justify-center py-12">
              <div className="text-center text-sm text-text-muted">
                <div className="mb-2">No results yet</div>
                <div className="text-xs">
                  Enter a tax reference number and click Verify to see results
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      {result && (
        <div className="console-card">
          <div className="console-card-header">
            <div className="text-sm font-semibold text-text">Raw response</div>
          </div>
          <div className="console-card-body bg-background">
            <JsonViewer data={result} />
          </div>
        </div>
      )}
      <ProcessingDialog
        open={loading}
        title="Verifying tax compliance"
        message="Checking compliance status against SARS records."
      />
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
