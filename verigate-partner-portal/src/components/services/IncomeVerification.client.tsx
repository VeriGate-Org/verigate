"use client";

import { useCallback, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { executeVerification } from "@/lib/services/verification-service";
import { type IncomeResponse } from "@/lib/mock-services";

const PERIOD_OPTIONS = ["Last 3 months", "Last 6 months", "Last 12 months"];

function formatZAR(amount: number): string {
  return new Intl.NumberFormat("en-ZA", { style: "currency", currency: "ZAR" }).format(amount);
}

export default function IncomeVerification() {
  const [idNumber, setIdNumber] = useState("");
  const [employerName, setEmployerName] = useState("");
  const [period, setPeriod] = useState<string>(PERIOD_OPTIONS[0]);
  const [result, setResult] = useState<IncomeResponse | null>(null);
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
      const data = (await executeVerification("INCOME_VERIFICATION", {
        idNumber,
        employerName,
        period,
      })) as IncomeResponse;
      setResult(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const submitDisabled = loading || idNumber.length !== 13;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Income verification</h1>
        <p className="text-sm text-text-muted">
          Verify income details through payroll and employment records.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Subject details</div>
              <div className="text-xs text-text-muted">Enter the individual&apos;s information.</div>
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

            <Field label="Employer name (optional)" description="Current or most recent employer.">
              <input
                value={employerName}
                onChange={(event) => setEmployerName(event.target.value)}
                className="aws-input w-full"
              />
            </Field>

            <Field label="Period" description="Select the income verification period.">
              <select
                className="aws-select w-full select-input"
                value={period}
                onChange={(event) => setPeriod(event.target.value)}
              >
                {PERIOD_OPTIONS.map((item) => (
                  <option key={item} value={item}>
                    {item}
                  </option>
                ))}
              </select>
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Income data sourced from payroll records.
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
                <div className="text-sm font-semibold text-text">Income results</div>
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
                            result.income.verified
                              ? "bg-success/10 text-success"
                              : "bg-danger/10 text-danger"
                          }`}
                        >
                          {result.income.verified ? "Yes" : "No"}
                        </span>
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Gross monthly income
                      </td>
                      <td className="px-4 py-2.5 text-sm font-medium text-text">
                        {formatZAR(result.income.grossMonthly)}
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Net monthly income
                      </td>
                      <td className="px-4 py-2.5 text-sm font-medium text-text">
                        {formatZAR(result.income.netMonthly)}
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Period
                      </td>
                      <td className="px-4 py-2.5 text-sm font-medium text-text">
                        {result.income.period}
                      </td>
                    </tr>
                    <tr className="hover:bg-background/50">
                      <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                        Pay frequency
                      </td>
                      <td className="px-4 py-2.5 text-sm font-medium text-text">
                        {result.income.payFrequency}
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
              <div className="text-sm font-semibold text-text">Income results</div>
            </div>
            <div className="console-card-body flex items-center justify-center py-12">
              <div className="text-center text-sm text-text-muted">
                <div className="mb-2">No results yet</div>
                <div className="text-xs">
                  Enter an ID number and click Verify to see results
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
        title="Verifying income"
        message="Querying payroll and employment records for income data."
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
