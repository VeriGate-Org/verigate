"use client";

import { useCallback, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { executeVerification } from "@/lib/services/verification-service";
import { type FullVerificationResponse } from "@/lib/mock-services";

const SUB_TYPE_OPTIONS = [
  "Identity",
  "Credit",
  "Employment",
  "Sanctions",
  "Qualification",
  "Tax Compliance",
];

export default function FullVerification() {
  const [idNumber, setIdNumber] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [selectedSubTypes, setSelectedSubTypes] = useState<string[]>([]);
  const [result, setResult] = useState<FullVerificationResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleExport = useCallback(() => {
    if (typeof window !== "undefined") {
      window.print();
    }
  }, []);

  const toggleSubType = (subType: string) => {
    setSelectedSubTypes((prev) =>
      prev.includes(subType) ? prev.filter((s) => s !== subType) : [...prev, subType]
    );
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const data = (await executeVerification("FULL_VERIFICATION", {
        idNumber,
        firstName,
        lastName,
        subTypes: selectedSubTypes,
      })) as FullVerificationResponse;
      setResult(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const submitDisabled =
    loading || idNumber.length !== 13 || !firstName.trim() || !lastName.trim() || selectedSubTypes.length === 0;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Full verification</h1>
        <p className="text-sm text-text-muted">
          Run a comprehensive verification across multiple checks in a single request.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Subject details</div>
              <div className="text-xs text-text-muted">
                Enter identity information and select verification types.
              </div>
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

            <div className="space-y-1">
              <span className="block text-sm font-medium text-text">Verification types</span>
              <span className="block text-xs text-text-muted">Select at least one check to include.</span>
              <div className="grid grid-cols-2 gap-2 pt-1">
                {SUB_TYPE_OPTIONS.map((subType) => (
                  <label key={subType} className="flex items-center gap-2 text-sm cursor-pointer">
                    <input
                      type="checkbox"
                      checked={selectedSubTypes.includes(subType)}
                      onChange={() => toggleSubType(subType)}
                      className="h-4 w-4 rounded border-border"
                    />
                    <span className="text-text">{subType}</span>
                  </label>
                ))}
              </div>
            </div>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Runs all selected checks in a single batch.
              </p>
              <Button
                type="submit"
                variant="cta"
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Run verification
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
              <Skeleton className="h-12 w-32 mx-auto rounded" />
              <div className="border border-border rounded overflow-hidden">
                <div className="divide-y divide-border">
                  {[...Array(4)].map((_, i) => (
                    <div key={i} className="flex items-center px-4 py-2.5">
                      <Skeleton className="h-4 w-24 bg-background/50" />
                      <Skeleton className="h-4 w-16 ml-auto" />
                      <Skeleton className="h-4 w-20 ml-4" />
                      <Skeleton className="h-4 w-12 ml-4" />
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
                <div className="text-sm font-semibold text-text">Verification results</div>
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
              {/* Overall Status Badge */}
              <div className="flex justify-center">
                <span
                  className={`inline-flex items-center px-6 py-2 rounded text-lg font-bold ${
                    result.overallStatus === "PASSED"
                      ? "bg-success/10 text-success"
                      : "bg-danger/10 text-danger"
                  }`}
                >
                  {result.overallStatus}
                </span>
              </div>

              {/* Results Table */}
              <div className="border border-border rounded overflow-hidden">
                <table className="w-full">
                  <thead>
                    <tr className="bg-background/50 border-b border-border">
                      <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase tracking-wider">
                        Type
                      </th>
                      <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase tracking-wider">
                        Status
                      </th>
                      <th className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase tracking-wider">
                        Provider
                      </th>
                      <th className="px-4 py-2.5 text-right text-xs font-medium text-text-muted uppercase tracking-wider">
                        Duration
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-border">
                    {result.results.map((item, idx) => (
                      <tr key={idx} className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm font-medium text-text">{item.type}</td>
                        <td className="px-4 py-2.5 text-sm">
                          <span
                            className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              item.status === "PASSED"
                                ? "bg-success/10 text-success"
                                : "bg-danger/10 text-danger"
                            }`}
                          >
                            {item.status}
                          </span>
                        </td>
                        <td className="px-4 py-2.5 text-sm text-text-muted">{item.provider}</td>
                        <td className="px-4 py-2.5 text-sm text-text-muted text-right">
                          {item.durationMs}ms
                        </td>
                      </tr>
                    ))}
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
              <div className="text-sm font-semibold text-text">Verification results</div>
            </div>
            <div className="console-card-body flex items-center justify-center py-12">
              <div className="text-center text-sm text-text-muted">
                <div className="mb-2">No results yet</div>
                <div className="text-xs">
                  Enter details, select verification types, and click Run verification to see results
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
        title="Running full verification"
        message="Executing all selected verification checks in parallel."
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
