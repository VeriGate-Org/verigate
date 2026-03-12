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
import { type SanctionsResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { exportPdf } from "@/lib/utils/export-pdf";
import { ShieldAlert } from "lucide-react";

export default function SanctionsCheck() {
  const [name, setName] = useState("");
  const [result, setResult] = useState<SanctionsResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-sanctions-check.pdf");
    }
  }, []);

  const doVerification = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = (await executeVerification("SANCTIONS_SCREENING", {
        name,
      })) as SanctionsResponse;
      setResult(data);
      toast({ title: "Screening complete", variant: "success" });
      setTimeout(() => resultRef.current?.focus(), 100);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Screening failed";
      setError(message);
      setResult(null);
      toast({ title: "Screening failed", description: message, variant: "error" });
    } finally {
      setLoading(false);
    }
  }, [name, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled = loading || name.trim().length < 3;

  const isClean = result
    ? !result.result.pep && result.result.sanctionsHitCount === 0
    : false;

  const srMessage = loading
    ? "Loading screening results"
    : error
      ? "Screening failed"
      : result
        ? "Screening complete"
        : "";

  return (
    <div className="space-y-6">
      <ScreenReaderAnnounce message={srMessage} />
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Sanctions & PEP screening</h1>
        <p className="text-sm text-text-muted">
          Run a deterministic World-Check style search for politically exposed persons and sanctions
          hits.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Subject</div>
              <div className="text-xs text-text-muted">Provide a full name for screening.</div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <ServiceField label="Full name" description="We recommend including middle names where possible.">
              <input
                required
                id="name"
                name="name"
                value={name}
                onChange={(event) => setName(event.target.value)}
                className="aws-input w-full"
                placeholder="e.g. John Michael Doe"
              />
            </ServiceField>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Sandbox returns a consistent PEP result for testing.
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
                    {[...Array(2)].map((_, i) => (
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
              <VerificationResultCard
                ref={resultCardRef}
                title="Screening summary"
                reference={result.correlationId}
                status={isClean ? "verified" : "not_verified"}
                onExport={handleExport}
                fields={[
                  {
                    label: "Sanctions hits",
                    value: String(result.result.sanctionsHitCount),
                  },
                ]}
                matchFields={[
                  {
                    label: result.result.pep ? "Politically exposed person" : "Not a PEP",
                    matched: !result.result.pep,
                  },
                  {
                    label:
                      result.result.sanctionsHitCount > 0
                        ? `${result.result.sanctionsHitCount} sanctions hit(s)`
                        : "No sanctions hits",
                    matched: result.result.sanctionsHitCount === 0,
                  },
                ]}
              />
            ) : (
              <VerificationEmptyState
                icon={ShieldAlert}
                heading="No results yet"
                description="Enter a name and click Screen to check for sanctions and PEP matches."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Screening subject"
        message="Checking mock sanctions & PEP datasets..."
      />
    </div>
  );
}
