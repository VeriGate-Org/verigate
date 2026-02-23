"use client";

import { useCallback, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { executeVerification } from "@/lib/services/verification-service";
import { type IdentityResponse } from "@/lib/mock-services";

export default function IdentityVerification() {
  const [idNumber, setIdNumber] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [result, setResult] = useState<IdentityResponse | null>(null);
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
      const data = (await executeVerification("IDENTITY_VERIFICATION", {
        idNumber,
        firstName,
        lastName,
      })) as IdentityResponse;
      setResult(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const submitDisabled = loading || idNumber.length !== 13 || !firstName.trim() || !lastName.trim();

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Identity verification</h1>
        <p className="text-sm text-text-muted">
          Perform comprehensive identity verification including biometric matching.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Subject details</div>
              <div className="text-xs text-text-muted">Enter the individual&apos;s identity information.</div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <Field label="ID number" description="13-digit South African ID number.">
              <input
                required
                value={idNumber}
                onChange={(event) => {
                  const digits = event.target.value.replace(/\D/g, "").slice(0, 13);
                  setIdNumber(digits);
                }}
                className="aws-input w-full"
                inputMode="numeric"
                maxLength={13}
                autoComplete="off"
              />
            </Field>

            <Field label="First name">
              <input
                required
                value={firstName}
                onChange={(event) => setFirstName(event.target.value)}
                className="aws-input w-full"
                autoComplete="off"
              />
            </Field>

            <Field label="Last name">
              <input
                required
                value={lastName}
                onChange={(event) => setLastName(event.target.value)}
                className="aws-input w-full"
                autoComplete="off"
              />
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Identity matched against DHA records and biometric data.
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
                  {[...Array(4)].map((_, i) => (
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
                <div className="text-sm font-semibold text-text">Identity results</div>
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
                        Verified
                      </td>
                      <td className="px-4 py-2.5 text-sm">
                        <span
                          className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                            result.identity.verified
                              ? "bg-success/10 text-success"
                              : "bg-danger/10 text-danger"
                          }`}
                        >
                          {result.identity.verified ? "Yes" : "No"}
                        </span>
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Match score
                      </td>
                      <td className="px-4 py-2.5 text-sm font-medium text-text">
                        {result.identity.matchScore}%
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Photo match
                      </td>
                      <td className="px-4 py-2.5 text-sm">
                        <span
                          className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                            result.identity.photoMatch
                              ? "bg-success/10 text-success"
                              : "bg-danger/10 text-danger"
                          }`}
                        >
                          {result.identity.photoMatch ? "Yes" : "No"}
                        </span>
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Liveness check
                      </td>
                      <td className="px-4 py-2.5 text-sm">
                        <span
                          className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                            result.identity.livenessCheck
                              ? "bg-success/10 text-success"
                              : "bg-danger/10 text-danger"
                          }`}
                        >
                          {result.identity.livenessCheck ? "Yes" : "No"}
                        </span>
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
              <div className="text-sm font-semibold text-text">Identity results</div>
            </div>
            <div className="console-card-body flex items-center justify-center py-12">
              <div className="text-center text-sm text-text-muted">
                <div className="mb-2">No results yet</div>
                <div className="text-xs">
                  Enter identity details and click Verify to see results
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
        title="Verifying identity"
        message="Running identity verification including biometric matching against DHA records."
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
