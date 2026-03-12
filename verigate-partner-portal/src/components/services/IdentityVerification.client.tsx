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
import { type IdentityResponse } from "@/lib/mock-services";
import { Fingerprint } from "lucide-react";

export default function IdentityVerification() {
  const [idNumber, setIdNumber] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [result, setResult] = useState<IdentityResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [idError, setIdError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

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

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-identity-verification.pdf");
    }
  }, []);

  const doVerification = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = (await executeVerification("IDENTITY_VERIFICATION", {
        idNumber,
        firstName,
        lastName,
      })) as IdentityResponse;
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
  }, [idNumber, firstName, lastName, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const validation = validateSaId(idNumber);
    if (!validation.valid) {
      setIdError(validation.errors[0]);
      return;
    }

    await doVerification();
  };

  const submitDisabled = loading || idNumber.length !== 13 || idError !== null || !firstName.trim() || !lastName.trim();

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

            <ServiceField label="First name">
              <input
                required
                id="firstName"
                name="firstName"
                value={firstName}
                onChange={(event) => setFirstName(event.target.value)}
                className="aws-input w-full"
                autoComplete="given-name"
              />
            </ServiceField>

            <ServiceField label="Last name">
              <input
                required
                id="lastName"
                name="lastName"
                value={lastName}
                onChange={(event) => setLastName(event.target.value)}
                className="aws-input w-full"
                autoComplete="family-name"
              />
            </ServiceField>

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
                ref={resultCardRef}
                title="Identity results"
                reference={result.reference}
                status={result.identity.verified ? "verified" : "not_verified"}
                confidenceScore={result.identity.matchScore}
                onExport={handleExport}
                fields={[
                  { label: "Provider", value: result.provider },
                ]}
                matchFields={[
                  { label: result.identity.photoMatch ? "Photo matched" : "Photo not matched", matched: result.identity.photoMatch },
                  { label: result.identity.livenessCheck ? "Liveness passed" : "Liveness failed", matched: result.identity.livenessCheck },
                ]}
              />
            ) : (
              <VerificationEmptyState
                icon={Fingerprint}
                heading="No results yet"
                description="Enter identity details and click Verify to see results."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Verifying identity"
        message="Running identity verification including biometric matching against DHA records."
      />
    </div>
  );
}
