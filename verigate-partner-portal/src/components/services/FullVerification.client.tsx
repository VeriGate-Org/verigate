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
import { type FullVerificationResponse } from "@/lib/mock-services";
import { CheckSquare, FileDown } from "lucide-react";

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
  const [idError, setIdError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-full-verification.pdf");
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

  const toggleSubType = (subType: string) => {
    setSelectedSubTypes((prev) =>
      prev.includes(subType) ? prev.filter((s) => s !== subType) : [...prev, subType]
    );
  };

  const doVerification = useCallback(async () => {
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
  }, [idNumber, firstName, lastName, selectedSubTypes, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const validation = validateSaId(idNumber);
    if (!validation.valid) {
      setIdError(validation.errors[0]);
      return;
    }

    await doVerification();
  };

  const submitDisabled =
    loading || idNumber.length !== 13 || idError !== null || !firstName.trim() || !lastName.trim() || selectedSubTypes.length === 0;

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

            <div className="space-y-1">
              <span className="block text-sm font-medium text-text">Verification types</span>
              <span className="block text-xs text-text-muted">
                Select at least one check to include.
              </span>
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
            ) : error && !result ? (
              <div className="console-card">
                <div className="console-card-body flex items-center justify-between">
                  <span className="text-sm text-danger">{error}</span>
                  <RetryButton onRetry={doVerification} />
                </div>
              </div>
            ) : result ? (
              <div ref={resultCardRef} className="console-card" role="region" aria-label="Full verification results">
                <div className="console-card-header">
                  <div>
                    <div className="text-sm font-semibold text-text">Verification results</div>
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

                  <div className="border border-border rounded overflow-hidden">
                    <table className="w-full">
                      <thead>
                        <tr className="bg-background/50 border-b border-border">
                          <th scope="col" className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase tracking-wider">Type</th>
                          <th scope="col" className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase tracking-wider">Status</th>
                          <th scope="col" className="px-4 py-2.5 text-left text-xs font-medium text-text-muted uppercase tracking-wider">Provider</th>
                          <th scope="col" className="px-4 py-2.5 text-right text-xs font-medium text-text-muted uppercase tracking-wider">Duration</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-border">
                        {result.results.map((item, idx) => (
                          <tr key={idx} className="hover:bg-background/50">
                            <td className="px-4 py-2.5 text-sm font-medium text-text">{item.type}</td>
                            <td className="px-4 py-2.5 text-sm">
                              <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${item.status === "PASSED" ? "bg-success/10 text-success" : "bg-danger/10 text-danger"}`}>
                                {item.status}
                              </span>
                            </td>
                            <td className="px-4 py-2.5 text-sm text-text-muted">{item.provider}</td>
                            <td className="px-4 py-2.5 text-sm text-text-muted text-right">{item.durationMs}ms</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  <div className="flex items-start gap-2 px-1 py-2 text-xs text-text-muted border-t border-border">
                    <span className="font-medium">Provider:</span>
                    <span>{result.provider}</span>
                  </div>
                </div>
              </div>
            ) : (
              <VerificationEmptyState
                icon={CheckSquare}
                heading="No results yet"
                description="Enter details, select verification types, and click Run verification to see results."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Running full verification"
        message="Executing all selected verification checks in parallel."
      />
    </div>
  );
}
