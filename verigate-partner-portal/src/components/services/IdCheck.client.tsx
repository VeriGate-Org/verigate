"use client";

import { useCallback, useRef, useState } from "react";
import type { FormEvent } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { VerificationResultCard } from "@/components/verification/VerificationResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { AnimatedResult } from "@/components/verification/AnimatedResult";
import { RetryButton } from "@/components/verification/RetryButton";
import { ScreenReaderAnnounce } from "@/components/ui/ScreenReaderAnnounce";
import { ServiceField } from "@/components/services/shared/ServiceField";
import { useToast } from "@/components/ui/Toast";
import { type PersonalDetailsResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { validateSaId } from "@/lib/utils/sa-id-validation";
import { exportPdf } from "@/lib/utils/export-pdf";
import { Shield } from "lucide-react";

const REASONS = [
  { value: "fraud_prevention", label: "Fraud prevention" },
  { value: "onboarding", label: "Customer onboarding" },
  { value: "kyc_refresh", label: "KYC refresh" },
  { value: "other", label: "Other" },
];

export default function IdCheck() {
  const [firstName, setFirstName] = useState("");
  const [surname, setSurname] = useState("");
  const [idNumber, setIdNumber] = useState("");
  const [reason, setReason] = useState("");
  const [result, setResult] = useState<PersonalDetailsResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [idError, setIdError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-id-check.pdf");
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

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const validation = validateSaId(idNumber);
    if (!validation.valid) {
      setIdError(validation.errors[0]);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const data = await executeVerification("VERIFICATION_OF_PERSONAL_DETAILS", { firstName, surname, idNumber, reason }) as PersonalDetailsResponse;
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
  };

  const handleRetry = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await executeVerification("VERIFICATION_OF_PERSONAL_DETAILS", { firstName, surname, idNumber, reason }) as PersonalDetailsResponse;
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
  }, [firstName, surname, idNumber, reason, toast]);

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
        <h1 className="text-xl font-semibold text-text">Home Affairs ID verification</h1>
        <p className="text-sm text-text-muted">
          Submit a national ID number for an immediate data pull from the Department of Home Affairs sandbox feed.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Request parameters</div>
              <div className="text-xs text-text-muted">We log each submission for audit trails.</div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <ServiceField
              label="ID number"
              description="13-digit South African ID. We validate format and checksum before calling the provider."
              error={idError}
            >
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

            <ServiceField label="Given name (optional)">
              <input
                id="firstName"
                name="firstName"
                value={firstName}
                onChange={(event) => setFirstName(event.target.value)}
                className="aws-input w-full"
                autoComplete="given-name"
              />
            </ServiceField>

            <ServiceField label="Surname (optional)">
              <input
                id="surname"
                name="surname"
                value={surname}
                onChange={(event) => setSurname(event.target.value)}
                className="aws-input w-full"
                autoComplete="family-name"
              />
            </ServiceField>

            <ServiceField label="Reason for enquiry" description="Stored for audit reporting only.">
              <select
                id="reason"
                name="reason"
                className="aws-select w-full select-input"
                value={reason}
                onChange={(event) => setReason(event.target.value)}
              >
                <option value="">Select reason</option>
                {REASONS.map((item) => (
                  <option key={item.value} value={item.value}>
                    {item.label}
                  </option>
                ))}
              </select>
            </ServiceField>

            <div className="flex items-start justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                By submitting you confirm consent to access this individual&apos;s identity record.
              </p>
              <Button
                type="submit"
                variant="cta"
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Verify ID
              </Button>
            </div>

            {error && (
              <div role="alert" className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">{error}</div>
            )}
          </form>
        </div>

        <div ref={resultRef} tabIndex={-1} className="outline-none">
          <AnimatedResult>
            {error && !result ? (
              <div className="console-card">
                <div className="console-card-body flex items-center justify-between">
                  <span className="text-sm text-danger">{error}</span>
                  <RetryButton onRetry={handleRetry} />
                </div>
              </div>
            ) : result ? (
              <VerificationResultCard
                ref={resultCardRef}
                title="Verification summary"
                reference={result.reference}
                status={result.validation.verified ? "verified" : "not_verified"}
                confidenceScore={result.validation.nameMatchConfidence}
                onExport={handleExport}
                fields={[
                  { label: "Provider", value: result.provider },
                  {
                    label: "Date of birth",
                    value: result.derived.birthDate ? new Date(result.derived.birthDate).toLocaleDateString() : "—",
                  },
                  {
                    label: "Gender",
                    value: formatGender(result.derived.gender),
                  },
                  {
                    label: "Citizenship",
                    value: formatCitizenship(result.derived.citizenship),
                  },
                  {
                    label: "Age",
                    value: result.derived.age == null ? "—" : `${result.derived.age}`,
                  },
                  {
                    label: "Flags",
                    value: formatFlags(result.flags),
                  },
                  ...(result.metadata?.reason ? [{ label: "Reason", value: result.metadata.reason }] : []),
                ]}
              />
            ) : !loading ? (
              <VerificationEmptyState
                icon={Shield}
                heading="No verification results"
                description="Enter an ID number and submit to verify against the Department of Home Affairs."
              />
            ) : null}
          </AnimatedResult>
        </div>
      </div>
      <ProcessingDialog open={loading} title="Verifying ID" message="Fetching Home Affairs sandbox data..." />
    </div>
  );
}

type Flags = PersonalDetailsResponse["flags"];

function formatFlags(flags: Flags) {
  const values = [flags.deceased && "Deceased", flags.restricted && "Restricted"].filter(Boolean);
  return values.length ? values.join(", ") : "None";
}

function formatGender(value: PersonalDetailsResponse["derived"]["gender"]) {
  if (value === "unknown") return "Unknown";
  return value.charAt(0).toUpperCase() + value.slice(1);
}

function formatCitizenship(value: PersonalDetailsResponse["derived"]["citizenship"]) {
  if (value === "unknown") return "Unknown";
  if (value === "SA") return "SA citizen";
  if (value === "Non-SA") return "Non-SA";
  return value;
}
