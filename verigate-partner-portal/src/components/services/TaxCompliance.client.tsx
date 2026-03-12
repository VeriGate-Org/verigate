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
import { exportPdf } from "@/lib/utils/export-pdf";
import { type TaxComplianceResponse } from "@/lib/mock-services";
import { Receipt } from "lucide-react";

export default function TaxCompliance() {
  const [taxReferenceNumber, setTaxReferenceNumber] = useState("");
  const [result, setResult] = useState<TaxComplianceResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-tax-compliance.pdf");
    }
  }, []);

  const doVerification = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = (await executeVerification("TAX_COMPLIANCE_VERIFICATION", {
        taxReferenceNumber,
      })) as TaxComplianceResponse;
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
  }, [taxReferenceNumber, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled = loading || taxReferenceNumber.length < 10;

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
            <ServiceField label="Tax reference number" description="SARS tax reference (minimum 10 characters).">
              <input
                required
                id="taxReferenceNumber"
                name="taxReferenceNumber"
                value={taxReferenceNumber}
                onChange={(event) => setTaxReferenceNumber(event.target.value)}
                className="aws-input w-full"
                minLength={10}
              />
            </ServiceField>

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
                title="Compliance results"
                reference={result.reference}
                status={result.compliance.compliant ? "verified" : "not_verified"}
                onExport={handleExport}
                fields={[
                  { label: "Provider", value: result.provider },
                  { label: "Status", value: result.compliance.status },
                  {
                    label: "Last filing date",
                    value: new Date(result.compliance.lastFilingDate).toLocaleDateString("en-ZA"),
                  },
                  {
                    label: "Outstanding returns",
                    value: String(result.compliance.outstandingReturns),
                  },
                ]}
                matchFields={[
                  {
                    label: result.compliance.compliant ? "Tax compliant" : "Not compliant",
                    matched: result.compliance.compliant,
                  },
                  {
                    label: result.compliance.taxClearanceCertificate
                      ? "Tax clearance certificate"
                      : "No tax clearance certificate",
                    matched: result.compliance.taxClearanceCertificate,
                  },
                ]}
              />
            ) : (
              <VerificationEmptyState
                icon={Receipt}
                heading="No results yet"
                description="Enter a tax reference number and click Verify to see results."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Verifying tax compliance"
        message="Checking compliance status against SARS records."
      />
    </div>
  );
}
