"use client";

import { useCallback, useRef, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { VerificationResultCard } from "@/components/verification/VerificationResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { RetryButton } from "@/components/verification/RetryButton";
import { type FraudWatchlistResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { AlertTriangle } from "lucide-react";

export default function FraudWatchlistScreening() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [idNumber, setIdNumber] = useState("");
  const [result, setResult] = useState<FraudWatchlistResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);

  const handleExport = useCallback(() => {
    if (typeof window !== "undefined") {
      window.print();
    }
  }, []);

  const doVerification = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = (await executeVerification("FRAUD_WATCHLIST_SCREENING", {
        firstName,
        lastName,
        idNumber,
      })) as FraudWatchlistResponse;
      setResult(data);
      setTimeout(() => resultRef.current?.focus(), 100);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Screening failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  }, [firstName, lastName, idNumber]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
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
                id="firstName"
                name="firstName"
                value={firstName}
                onChange={(event) => setFirstName(event.target.value)}
                className="aws-input w-full"
                autoComplete="given-name"
              />
            </Field>

            <Field
              label="Last name"
              description="Legal last name of the individual."
            >
              <input
                required
                id="lastName"
                name="lastName"
                value={lastName}
                onChange={(event) => setLastName(event.target.value)}
                className="aws-input w-full"
                autoComplete="family-name"
              />
            </Field>

            <Field
              label="ID Number"
              description="13-digit South African ID number."
            >
              <input
                required
                id="idNumber"
                name="idNumber"
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
          ) : error && !result ? (
            <div className="console-card">
              <div className="console-card-body flex items-center justify-between">
                <span className="text-sm text-danger">{error}</span>
                <RetryButton onRetry={doVerification} />
              </div>
            </div>
          ) : result ? (
            <VerificationResultCard
              title="Screening results"
              reference={result.reference}
              status={result.watchlist.listed ? "not_verified" : "verified"}
              onExport={handleExport}
              fields={[
                { label: "Provider", value: result.provider },
                { label: "List type", value: result.watchlist.listType },
                {
                  label: "Date added",
                  value: result.watchlist.dateAdded
                    ? new Date(result.watchlist.dateAdded).toLocaleDateString()
                    : "N/A",
                },
                {
                  label: "Case reference",
                  value: result.watchlist.caseReference ?? "N/A",
                },
              ]}
              matchFields={[
                {
                  label: result.watchlist.listed ? "Listed on watchlist" : "Not listed",
                  matched: !result.watchlist.listed,
                },
              ]}
            />
          ) : (
            <VerificationEmptyState
              icon={AlertTriangle}
              heading="No results yet"
              description="Enter subject details and click Screen to see results."
            />
          )}
        </div>
      </div>

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
