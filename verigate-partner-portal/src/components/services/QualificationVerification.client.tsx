"use client";

import { useCallback, useRef, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { VerificationResultCard } from "@/components/verification/VerificationResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { RetryButton } from "@/components/verification/RetryButton";
import { type QualificationResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { GraduationCap } from "lucide-react";

const QUALIFICATION_TYPES = [
  { value: "bachelors", label: "Bachelor's Degree" },
  { value: "masters", label: "Master's Degree" },
  { value: "diploma", label: "Diploma" },
  { value: "certificate", label: "Certificate" },
  { value: "doctorate", label: "Doctorate" },
];

export default function QualificationVerification() {
  const [idNumber, setIdNumber] = useState("");
  const [qualificationType, setQualificationType] = useState<string>(
    QUALIFICATION_TYPES[0]?.value ?? ""
  );
  const [institution, setInstitution] = useState("");
  const [result, setResult] = useState<QualificationResponse | null>(null);
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
      const data = (await executeVerification("QUALIFICATION_VERIFICATION", {
        idNumber,
        qualificationType,
        institution,
      })) as QualificationResponse;
      setResult(data);
      setTimeout(() => resultRef.current?.focus(), 100);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  }, [idNumber, qualificationType, institution]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled = loading || idNumber.length !== 13;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">
          Qualification verification
        </h1>
        <p className="text-sm text-text-muted">
          Verify educational qualifications through SAQA and institutional
          records.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">
                Qualification details
              </div>
              <div className="text-xs text-text-muted">
                Provide the individual&apos;s ID and qualification information.
              </div>
            </div>
          </div>

          <form
            className="console-card-body space-y-4"
            onSubmit={handleSubmit}
          >
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

            <Field
              label="Qualification type"
              description="Select the type of qualification to verify."
            >
              <select
                id="qualificationType"
                name="qualificationType"
                className="aws-select w-full select-input"
                value={qualificationType}
                onChange={(event) => setQualificationType(event.target.value)}
              >
                {QUALIFICATION_TYPES.map((item) => (
                  <option key={item.value} value={item.value}>
                    {item.label}
                  </option>
                ))}
              </select>
            </Field>

            <Field
              label="Institution (optional)"
              description="Name of the educational institution."
            >
              <input
                id="institution"
                name="institution"
                value={institution}
                onChange={(event) => setInstitution(event.target.value)}
                className="aws-input w-full"
              />
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Qualifications are verified through SAQA and institutional
                records.
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
              title="Qualification results"
              reference={result.reference}
              status={result.qualification.verified ? "verified" : "not_verified"}
              onExport={handleExport}
              fields={[
                { label: "Provider", value: result.provider },
                { label: "Qualification type", value: result.qualification.qualificationType },
                { label: "Institution", value: result.qualification.institution },
                { label: "Year completed", value: String(result.qualification.yearCompleted) },
                { label: "Status", value: result.qualification.status },
              ]}
            />
          ) : (
            <VerificationEmptyState
              icon={GraduationCap}
              heading="No results yet"
              description="Enter qualification details and click Verify to see results."
            />
          )}
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Verifying qualification"
        message="Checking qualification records through SAQA and institutional databases."
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
