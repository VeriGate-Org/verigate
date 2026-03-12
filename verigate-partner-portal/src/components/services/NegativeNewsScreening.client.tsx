"use client";

import { useCallback, useRef, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { VerificationResultCard } from "@/components/verification/VerificationResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { RetryButton } from "@/components/verification/RetryButton";
import { type NegativeNewsResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { Newspaper } from "lucide-react";

export default function NegativeNewsScreening() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [result, setResult] = useState<NegativeNewsResponse | null>(null);
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
      const data = (await executeVerification("NEGATIVE_NEWS_SCREENING", {
        firstName,
        lastName,
      })) as NegativeNewsResponse;
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
  }, [firstName, lastName]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled =
    loading || firstName.trim().length < 2 || lastName.trim().length < 2;

  const riskColor =
    result?.screening.riskLevel === "LOW"
      ? "bg-success/10 text-success"
      : result?.screening.riskLevel === "MEDIUM"
        ? "bg-warning/10 text-warning"
        : "bg-danger/10 text-danger";

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">
          Negative news screening
        </h1>
        <p className="text-sm text-text-muted">
          Screen individuals against news sources for adverse media coverage.
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
                Provide the individual&apos;s name to screen against adverse
                media.
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

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Screens against Reuters, Bloomberg, local media, court records,
                and regulatory filings.
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
                    {[...Array(3)].map((_, i) => (
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
              status={result.screening.hitCount === 0 ? "verified" : "not_verified"}
              onExport={handleExport}
              fields={[
                { label: "Provider", value: result.provider },
                { label: "Hit count", value: String(result.screening.hitCount) },
                {
                  label: "Risk level",
                  value: (
                    <span
                      className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${riskColor}`}
                    >
                      {result.screening.riskLevel}
                    </span>
                  ),
                },
              ]}
            >
              {result.screening.sources.length > 0 && (
                <div className="mt-4 border-t border-border pt-3">
                  <h4 className="text-xs font-medium text-text-muted uppercase tracking-wide mb-2">
                    Sources
                  </h4>
                  <div className="border border-border rounded overflow-hidden">
                    <table className="w-full">
                      <tbody className="divide-y divide-border">
                        {result.screening.sources.map((source, index) => (
                          <tr key={index} className="hover:bg-background/50">
                            <td className="px-4 py-2 text-sm text-text-muted bg-background/30 w-16">
                              {index + 1}
                            </td>
                            <td className="px-4 py-2 text-sm text-text">
                              {source}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}
            </VerificationResultCard>
          ) : (
            <VerificationEmptyState
              icon={Newspaper}
              heading="No results yet"
              description="Enter subject details and click Screen to see results."
            />
          )}
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Screening negative news"
        message="Scanning adverse media sources for the provided individual."
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
