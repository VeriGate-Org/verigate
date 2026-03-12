"use client";

import { useCallback, useRef, useState } from "react";
import type { FormEvent } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { VerificationResultCard } from "@/components/verification/VerificationResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { AnimatedResult } from "@/components/verification/AnimatedResult";
import { RetryButton } from "@/components/verification/RetryButton";
import { ScreenReaderAnnounce } from "@/components/ui/ScreenReaderAnnounce";
import { ServiceField } from "@/components/services/shared/ServiceField";
import { useToast } from "@/components/ui/Toast";
import { executeVerification } from "@/lib/services/verification-service";
import { validateSaId } from "@/lib/utils/sa-id-validation";
import { exportPdf } from "@/lib/utils/export-pdf";
import { type IncomeResponse } from "@/lib/mock-services";
import { DollarSign } from "lucide-react";

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
  const [idError, setIdError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-income-verification.pdf");
    }
  }, []);

  const handleIdChange = useCallback((value: string) => {
    const digits = value.replace(/\D/g, "").slice(0, 13);
    setIdNumber(digits);
    if (digits.length === 13) {
      const validation = validateSaId(digits);
      setIdError(validation.valid ? null : validation.errors[0]);
    } else {
      setIdError(null);
    }
  }, []);

  const doVerification = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = (await executeVerification("INCOME_VERIFICATION", {
        idNumber,
        employerName,
        period,
      })) as IncomeResponse;
      setResult(data);
      toast({ title: "Verification complete", variant: "success" });
      setTimeout(() => resultRef.current?.focus(), 100);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
      toast({ title: "Verification failed", description: message, variant: "error" });
    } finally {
      setLoading(false);
    }
  }, [idNumber, employerName, period, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const validation = validateSaId(idNumber);
    if (!validation.valid) {
      setIdError(validation.errors[0]);
      return;
    }

    await doVerification();
  };

  const submitDisabled = loading || idNumber.length !== 13 || idError !== null;

  const srMessage = loading
    ? "Loading verification results"
    : error
      ? "Verification failed"
      : result
        ? "Verification complete"
        : "";

  return (
    <div className="space-y-6">
      <ScreenReaderAnnounce message={srMessage} />
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
            <ServiceField label="ID number" description="13-digit South African ID number." error={idError}>
              <input
                required
                id="idNumber"
                name="idNumber"
                value={idNumber}
                onChange={(event) => handleIdChange(event.target.value)}
                className={`aws-input w-full ${idError ? "border-danger" : ""}`}
                inputMode="numeric"
                maxLength={13}
                aria-invalid={!!idError}
                aria-describedby={idError ? "idNumber-error" : undefined}
              />
            </ServiceField>

            <ServiceField label="Employer name (optional)" description="Current or most recent employer.">
              <input
                id="employerName"
                name="employerName"
                value={employerName}
                onChange={(event) => setEmployerName(event.target.value)}
                className="aws-input w-full"
              />
            </ServiceField>

            <ServiceField label="Period" description="Select the income verification period.">
              <select
                id="period"
                name="period"
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
            </ServiceField>

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
              <div role="alert" className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">
                {error}
              </div>
            )}
          </form>
        </div>

        <div ref={resultRef} tabIndex={-1} className="outline-none">
          <AnimatedResult>
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
            ) : error && !result ? (
              <div className="console-card">
                <div className="console-card-body flex items-center justify-between">
                  <span className="text-sm text-danger">{error}</span>
                  <RetryButton onRetry={doVerification} />
                </div>
              </div>
            ) : result ? (
              <VerificationResultCard
                ref={resultCardRef}
                title="Income results"
                reference={result.reference}
                status={result.income.verified ? "verified" : "not_verified"}
                onExport={handleExport}
                fields={[
                  { label: "Provider", value: result.provider },
                  { label: "Gross monthly", value: formatZAR(result.income.grossMonthly) },
                  { label: "Net monthly", value: formatZAR(result.income.netMonthly) },
                  { label: "Period", value: result.income.period },
                  { label: "Pay frequency", value: result.income.payFrequency },
                ]}
              />
            ) : (
              <VerificationEmptyState
                icon={DollarSign}
                heading="No results yet"
                description="Enter an ID number and click Verify to see income results."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Verifying income"
        message="Querying payroll and employment records for income data."
      />
    </div>
  );
}
