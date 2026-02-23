"use client";

import { useCallback, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { type PersonalDetailsResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { FileDown } from "lucide-react";

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
      const data = await executeVerification("VERIFICATION_OF_PERSONAL_DETAILS", { firstName, surname, idNumber, reason }) as PersonalDetailsResponse;
      setResult(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const summaryItems = result
    ? ([
        {
          label: "Status",
          value: (
            <span
              className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${
                result.validation.verified ? "bg-success/10 text-success" : "bg-danger/10 text-danger"
              }`}
            >
              {result.validation.verified ? "Verified" : "Not verified"}
            </span>
          ),
        },
        { label: "Provider", value: result.provider },
        {
          label: "Name match",
          value: `${Math.round(result.validation.nameMatchConfidence ?? 0)}%`,
        },
        {
          label: "Date of birth",
          value: result.derived.birthDate ? new Date(result.derived.birthDate).toLocaleDateString() : "—",
        },
        {
          label: "Gender • Citizenship",
          value: `${formatGender(result.derived.gender)} • ${formatCitizenship(result.derived.citizenship)}`,
        },
        {
          label: "Age",
          value: result.derived.age == null ? "—" : `${result.derived.age}`,
        },
        {
          label: "Flags",
          value: formatFlags(result.flags),
        },
        result.metadata?.reason
          ? {
              label: "Reason",
              value: result.metadata.reason,
            }
          : null,
      ] as Array<{ label: string; value: ReactNode } | null>)
        .filter((item): item is { label: string; value: ReactNode } => Boolean(item))
    : [];

  const submitDisabled = loading || idNumber.length !== 13;

  return (
    <div className="space-y-6">
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
            <Field
              label="ID number"
              description="13-digit South African ID. We validate format before calling the provider."
            >
              <input
                required
                value={idNumber}
                onChange={(event) => {
                  const digits = event.target.value.replace(/\D/g, "").slice(0, 13);
                  setIdNumber(digits);
                }}
                className="aws-input w-full"
                inputMode="numeric"
                maxLength={13}
                autoComplete="off"
              />
            </Field>

            <Field label="Given name (optional)">
              <input
                value={firstName}
                onChange={(event) => setFirstName(event.target.value)}
                className="aws-input w-full"
              />
            </Field>

            <Field label="Surname (optional)">
              <input
                value={surname}
                onChange={(event) => setSurname(event.target.value)}
                className="aws-input w-full"
              />
            </Field>

            <Field label="Reason for enquiry" description="Stored for audit reporting only.">
              <select
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
            </Field>

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
              <div className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">{error}</div>
            )}
          </form>
        </div>

        <div className="space-y-4">
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Service notes</div>
            </div>
            <div className="console-card-body space-y-3 text-sm text-text-muted">
              <p>The sandbox response is deterministic and mirrors the production payload structure.</p>
              <ul className="list-disc space-y-1 pl-5">
                <li>Checksum validation runs locally; failures return an error before hitting the provider.</li>
                <li>ID numbers ending in 9 simulate a deceased flag; ending in 99 also sets the restricted flag.</li>
                <li>Every call is recorded in the verification log with the reference ID for follow-up.</li>
              </ul>
            </div>
          </div>

          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Response fields</div>
            </div>
            <div className="console-card-body space-y-2 text-xs text-text-muted">
              <SchemaRow name="validation.verified" type="Boolean" />
              <SchemaRow name="validation.nameMatchConfidence" type="Integer" />
              <SchemaRow name="derived.birthDate" type="ISO date" />
              <SchemaRow name="derived.gender" type="Enum" />
              <SchemaRow name="derived.citizenship" type="Enum" />
              <SchemaRow name="flags.deceased" type="Boolean" />
              <SchemaRow name="flags.restricted" type="Boolean" />
            </div>
          </div>
        </div>
      </div>

      {result && (
        <div className="space-y-4">
          <div className="console-card">
            <div className="console-card-header">
              <div>
                <div className="text-sm font-semibold text-text">Verification summary</div>
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
            <div className="console-card-body">
              <dl className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                {summaryItems.map((item) => (
                  <div key={item.label} className="space-y-1 rounded border border-border bg-background p-3">
                    <dt className="text-xs uppercase tracking-wide text-text-muted">{item.label}</dt>
                    <dd className="text-sm font-medium text-text">{item.value}</dd>
                  </div>
                ))}
              </dl>
            </div>
          </div>

          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Raw response</div>
              <div className="text-xs text-text-muted">Generated {new Date(result.generatedAt).toLocaleString()}</div>
            </div>
            <div className="console-card-body bg-background">
              <JsonViewer data={result} />
            </div>
          </div>
        </div>
      )}
      <ProcessingDialog open={loading} title="Verifying ID" message="Fetching Home Affairs sandbox data..." />
    </div>
  );
}

type Flags = PersonalDetailsResponse["flags"];

type SchemaRowProps = { name: string; type: string };

function SchemaRow({ name, type }: SchemaRowProps) {
  return (
    <div className="flex items-center justify-between rounded border border-transparent px-2 py-1 hover:border-border">
      <span className="font-medium text-text">{name}</span>
      <span>{type}</span>
    </div>
  );
}

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

interface FieldProps {
  label: string;
  description?: string;
  children: ReactNode;
}

function Field({ label, description, children }: FieldProps) {
  return (
    <label className="block space-y-1 text-sm">
      <span className="font-medium text-text">{label}</span>
      {description && <span className="block text-xs text-text-muted">{description}</span>}
      {children}
    </label>
  );
}
