"use client";

import { useCallback, useRef, useState } from "react";
import type { FormEvent } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { AnimatedResult } from "@/components/verification/AnimatedResult";
import { RetryButton } from "@/components/verification/RetryButton";
import { ScreenReaderAnnounce } from "@/components/ui/ScreenReaderAnnounce";
import { ServiceField } from "@/components/services/shared/ServiceField";
import { useToast } from "@/components/ui/Toast";
import { executeVerification } from "@/lib/services/verification-service";
import { validateSaId } from "@/lib/utils/sa-id-validation";
import { exportPdf } from "@/lib/utils/export-pdf";
import { type CreditCheckResponse } from "@/lib/mock-services";
import { FileDown, TrendingUp } from "lucide-react";

const RISK_GRADE_COLORS: Record<string, string> = {
  A: "bg-success/10 text-success",
  B: "bg-blue-500/10 text-blue-500",
  C: "bg-yellow-500/10 text-yellow-500",
  D: "bg-orange-500/10 text-orange-500",
  E: "bg-danger/10 text-danger",
};

function formatZAR(amount: number): string {
  return new Intl.NumberFormat("en-ZA", { style: "currency", currency: "ZAR" }).format(amount);
}

export default function CreditCheck() {
  const [idNumber, setIdNumber] = useState("");
  const [consent, setConsent] = useState(false);
  const [result, setResult] = useState<CreditCheckResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [idError, setIdError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-credit-check.pdf");
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
      const data = (await executeVerification("CREDIT_CHECK", {
        idNumber,
        consent,
      })) as CreditCheckResponse;
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
  }, [idNumber, consent, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const validation = validateSaId(idNumber);
    if (!validation.valid) {
      setIdError(validation.errors[0]);
      return;
    }

    await doVerification();
  };

  const submitDisabled = loading || idNumber.length !== 13 || idError !== null || !consent;

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
        <h1 className="text-xl font-semibold text-text">Credit check</h1>
        <p className="text-sm text-text-muted">
          Run a credit bureau check to assess financial risk and credit history.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Subject details</div>
              <div className="text-xs text-text-muted">Enter the ID number and confirm consent.</div>
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

            <label className="flex items-start gap-2 text-sm cursor-pointer">
              <input
                type="checkbox"
                checked={consent}
                onChange={(e) => setConsent(e.target.checked)}
                className="mt-1 h-4 w-4 rounded border-border"
              />
              <span className="text-text-muted">
                I confirm that I have obtained consent from the individual to perform a credit bureau
                enquiry in accordance with the National Credit Act.
              </span>
            </label>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Credit data sourced from registered credit bureaus.
              </p>
              <Button
                type="submit"
                variant="cta"
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Run check
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
                  <div className="grid grid-cols-2 gap-4">
                    {[...Array(6)].map((_, i) => (
                      <div key={i} className="border border-border rounded p-4">
                        <Skeleton className="h-3 w-20 mb-2" />
                        <Skeleton className="h-6 w-16" />
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
              <div ref={resultCardRef} className="console-card" role="region" aria-label="Credit check results">
                <div className="console-card-header">
                  <div>
                    <div className="text-sm font-semibold text-text">Credit check results</div>
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
                <div className="console-card-body space-y-6 p-4">
                  <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                    <div className="border border-border rounded p-4 text-center">
                      <div className="text-xs text-text-muted mb-1">Credit score</div>
                      <div className="text-3xl font-bold text-text">{result.credit.score}</div>
                    </div>
                    <div className="border border-border rounded p-4 text-center">
                      <div className="text-xs text-text-muted mb-1">Risk grade</div>
                      <span
                        className={`inline-flex items-center px-3 py-1 rounded text-lg font-bold ${
                          RISK_GRADE_COLORS[result.credit.riskGrade] ??
                          "bg-text-muted/10 text-text-muted"
                        }`}
                      >
                        {result.credit.riskGrade}
                      </span>
                    </div>
                    <div className="border border-border rounded p-4 text-center">
                      <div className="text-xs text-text-muted mb-1">Accounts in good standing</div>
                      <div className="text-2xl font-semibold text-text">
                        {result.credit.accountsInGoodStanding}
                      </div>
                    </div>
                    <div className="border border-border rounded p-4 text-center">
                      <div className="text-xs text-text-muted mb-1">Defaults</div>
                      <div className="text-2xl font-semibold text-text">
                        {result.credit.defaults}
                      </div>
                    </div>
                    <div className="border border-border rounded p-4 text-center">
                      <div className="text-xs text-text-muted mb-1">Judgements</div>
                      <div className="text-2xl font-semibold text-text">
                        {result.credit.judgements}
                      </div>
                    </div>
                    <div className="border border-border rounded p-4 text-center">
                      <div className="text-xs text-text-muted mb-1">Total debt</div>
                      <div className="text-lg font-semibold text-text">
                        {formatZAR(result.credit.totalDebt)}
                      </div>
                    </div>
                  </div>

                  <div className="flex items-start gap-2 px-1 py-2 text-xs text-text-muted border-t border-border">
                    <span className="font-medium">Provider:</span>
                    <span>{result.provider}</span>
                  </div>
                </div>
              </div>
            ) : (
              <VerificationEmptyState
                icon={TrendingUp}
                heading="No results yet"
                description="Enter an ID number and confirm consent to run a credit check."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Running credit check"
        message="Querying the credit bureau for the subject's credit profile."
      />
    </div>
  );
}
