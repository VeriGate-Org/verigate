"use client";

import { useCallback, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { type FraudWatchlistResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";

export default function FraudWatchlistScreening() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [idNumber, setIdNumber] = useState("");
  const [result, setResult] = useState<FraudWatchlistResponse | null>(null);
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
      const data = (await executeVerification("FRAUD_WATCHLIST_SCREENING", {
        firstName,
        lastName,
        idNumber,
      })) as FraudWatchlistResponse;
      setResult(data);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Screening failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const submitDisabled =
    loading ||
    firstName.trim().length < 2 ||
    lastName.trim().length < 2 ||
    idNumber.length !== 13;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">
          Fraud watchlist screening
        </h1>
        <p className="text-sm text-text-muted">
          Check individuals against the South African Fraud Prevention Service
          (SAFPS) database.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">
                Subject details
              </div>
              <div className="text-xs text-text-muted">
                Provide identity details to screen against the SAFPS fraud
                database.
              </div>
            </div>
          </div>

          <form
            className="console-card-body space-y-4"
            onSubmit={handleSubmit}
          >
            <Field
              label="First name"
              description="Legal first name of the individual."
            >
              <input
                required
                value={firstName}
                onChange={(event) => setFirstName(event.target.value)}
                className="aws-input w-full"
                autoComplete="off"
              />
            </Field>

            <Field
              label="Last name"
              description="Legal last name of the individual."
            >
              <input
                required
                value={lastName}
                onChange={(event) => setLastName(event.target.value)}
                className="aws-input w-full"
                autoComplete="off"
              />
            </Field>

            <Field
              label="ID Number"
              description="13-digit South African ID number."
            >
              <input
                required
                value={idNumber}
                onChange={(event) => {
                  const digits = event.target.value
                    .replace(/\D/g, "")
                    .slice(0, 13);
                  setIdNumber(digits);
                }}
                className="aws-input w-full"
                inputMode="numeric"
                maxLength={13}
                autoComplete="off"
              />
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Screens against the SAFPS fraud prevention database.
              </p>
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
              <div>
                <Skeleton className="h-4 w-36 mb-2" />
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
          </div>
        ) : result ? (
          <div className="console-card">
            <div className="console-card-header">
              <div>
                <div className="text-sm font-semibold text-text">
                  Screening results
                </div>
                <div className="text-xs text-text-muted">
                  Reference {result.reference}
                </div>
              </div>
              <button
                onClick={handleExport}
                className="rounded-full border border-[color:var(--color-cta)] bg-[color:var(--color-base-100)] text-[color:var(--color-cta)] hover:bg-[color:var(--color-cta)] hover:text-white px-aws-l py-aws-s text-sm transition-all shadow-sm"
              >
                Export PDF
              </button>
            </div>
            <div className="console-card-body space-y-6 p-4">
              {/* Watchlist Details Section */}
              <div>
                <h3 className="text-sm font-medium text-text mb-2">
                  Watchlist Details
                </h3>
                <div className="border border-border rounded overflow-hidden">
                  <table className="w-full">
                    <tbody className="divide-y divide-border">
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30 w-1/3">
                          Listed
                        </td>
                        <td className="px-4 py-2.5 text-sm">
                          <span
                            className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.watchlist.listed
                                ? "bg-danger/10 text-danger"
                                : "bg-success/10 text-success"
                            }`}
                          >
                            {result.watchlist.listed ? "Yes" : "No"}
                          </span>
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          List type
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.watchlist.listType}
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Date added
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.watchlist.dateAdded
                            ? new Date(
                                result.watchlist.dateAdded
                              ).toLocaleDateString()
                            : "N/A"}
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Case reference
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.watchlist.caseReference ?? "N/A"}
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">
                Screening results
              </div>
            </div>
            <div className="console-card-body flex items-center justify-center py-12">
              <div className="text-center text-sm text-text-muted">
                <div className="mb-2">No results yet</div>
                <div className="text-xs">
                  Enter subject details and click Screen to see results
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
        title="Screening fraud watchlist"
        message="Checking against the SAFPS fraud prevention database."
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
      {description && (
        <span className="block text-xs text-text-muted">{description}</span>
      )}
      {children}
    </label>
  );
}
