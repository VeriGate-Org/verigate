"use client";

import { useCallback, useRef, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { VerificationResultCard } from "@/components/verification/VerificationResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { RetryButton } from "@/components/verification/RetryButton";
import { type EmploymentResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { Briefcase } from "lucide-react";

export default function EmploymentVerification() {
  const [idNumber, setIdNumber] = useState("");
  const [employerName, setEmployerName] = useState("");
  const [employeeNumber, setEmployeeNumber] = useState("");
  const [result, setResult] = useState<EmploymentResponse | null>(null);
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
      const data = (await executeVerification("EMPLOYMENT_VERIFICATION", {
        idNumber,
        employerName,
        employeeNumber,
      })) as EmploymentResponse;
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
  }, [idNumber, employerName, employeeNumber]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled = loading || idNumber.length !== 13;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">
          Employment verification
        </h1>
        <p className="text-sm text-text-muted">
          Verify employment status and history for an individual.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">
                Employee details
              </div>
              <div className="text-xs text-text-muted">
                Provide the individual&apos;s ID and employment information.
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
              label="Employer name (optional)"
              description="Name of the employer to verify against."
            >
              <input
                id="employerName"
                name="employerName"
                value={employerName}
                onChange={(event) => setEmployerName(event.target.value)}
                className="aws-input w-full"
              />
            </Field>

            <Field
              label="Employee number (optional)"
              description="Internal employee or payroll number."
            >
              <input
                id="employeeNumber"
                name="employeeNumber"
                value={employeeNumber}
                onChange={(event) => setEmployeeNumber(event.target.value)}
                className="aws-input w-full"
              />
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Employment records are verified against payroll and HR
                databases.
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
              title="Employment results"
              reference={result.reference}
              status={result.employment.verified ? "verified" : "not_verified"}
              onExport={handleExport}
              fields={[
                { label: "Provider", value: result.provider },
                { label: "Status", value: result.employment.status },
                {
                  label: "Start date",
                  value: new Date(result.employment.startDate).toLocaleDateString(),
                },
                { label: "Job title", value: result.employment.jobTitle },
                { label: "Department", value: result.employment.department },
              ]}
            />
          ) : (
            <VerificationEmptyState
              icon={Briefcase}
              heading="No results yet"
              description="Enter employee details and click Verify to see results."
            />
          )}
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Verifying employment"
        message="Running employment verification against payroll and HR databases."
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
