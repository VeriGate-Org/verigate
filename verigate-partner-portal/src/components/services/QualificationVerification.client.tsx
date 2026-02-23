"use client";

import { useCallback, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { type QualificationResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";

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

  const handleExport = useCallback(() => {
    if (typeof window !== "undefined") {
      window.print();
    }
  }, []);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const data = (await executeVerification("QUALIFICATION_VERIFICATION", {
        idNumber,
        qualificationType,
        institution,
      })) as QualificationResponse;
      setResult(data);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
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
                autoComplete="off"
              />
            </Field>

            <Field
              label="Qualification type"
              description="Select the type of qualification to verify."
            >
              <select
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
                value={institution}
                onChange={(event) => setInstitution(event.target.value)}
                className="aws-input w-full"
                autoComplete="off"
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

        {/* Results Panel */}
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
              <div>
                <Skeleton className="h-4 w-36 mb-2" />
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
          </div>
        ) : result ? (
          <div className="console-card">
            <div className="console-card-header">
              <div>
                <div className="text-sm font-semibold text-text">
                  Verification results
                </div>
                <div className="text-xs text-text-muted">
                  Reference {result.reference}
                </div>
              </div>
              <button
                onClick={handleExport}
                className="rounded-full border border-[color:var(--color-cta)] bg-[color:var(--color-base-100)] text-[color:var(--color-cta)] hover:bg-[color:var(--color-cta)] hover:text-white px-aws-l py-aws-s text-sm transition-all shadow-sm"
              >
                Export PDF
              </button>
            </div>
            <div className="console-card-body space-y-6 p-4">
              {/* Qualification Details Section */}
              <div>
                <h3 className="text-sm font-medium text-text mb-2">
                  Qualification Details
                </h3>
                <div className="border border-border rounded overflow-hidden">
                  <table className="w-full">
                    <tbody className="divide-y divide-border">
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30 w-1/3">
                          Verified
                        </td>
                        <td className="px-4 py-2.5 text-sm">
                          <span
                            className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.qualification.verified
                                ? "bg-success/10 text-success"
                                : "bg-danger/10 text-danger"
                            }`}
                          >
                            {result.qualification.verified ? "Yes" : "No"}
                          </span>
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Qualification type
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.qualification.qualificationType}
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Institution
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.qualification.institution}
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Year completed
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.qualification.yearCompleted}
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Status
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.qualification.status}
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">
                Verification results
              </div>
            </div>
            <div className="console-card-body flex items-center justify-center py-12">
              <div className="text-center text-sm text-text-muted">
                <div className="mb-2">No results yet</div>
                <div className="text-xs">
                  Enter qualification details and click Verify to see results
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      {result && (
        <div className="console-card">
          <div className="console-card-header">
            <div className="text-sm font-semibold text-text">Raw response</div>
          </div>
          <div className="console-card-body bg-background">
            <JsonViewer data={result} />
          </div>
        </div>
      )}
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
