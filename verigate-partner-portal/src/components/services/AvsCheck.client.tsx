"use client";

import { useCallback, useRef, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { VerificationResultCard } from "@/components/verification/VerificationResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { RetryButton } from "@/components/verification/RetryButton";
import { type AvsResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { SA_BANKS } from "@/lib/sa-banks";
import { CreditCard } from "lucide-react";

export default function AvsCheck() {
  const [name, setName] = useState("");
  const [surname, setSurname] = useState("");
  const [accountNumber, setAccountNumber] = useState("");
  const [bank, setBank] = useState<string>(SA_BANKS[0] ?? "");
  const [result, setResult] = useState<AvsResponse | null>(null);
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
      const data = (await executeVerification("BANK_ACCOUNT_VERIFICATION", {
        name,
        surname,
        accountNumber,
        bank,
      })) as AvsResponse;
      setResult(data);
      setTimeout(() => resultRef.current?.focus(), 100);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  }, [name, surname, accountNumber, bank]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled = loading || accountNumber.length < 6 || !bank;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Bank account validation</h1>
        <p className="text-sm text-text-muted">
          Validate account ownership across South African banks using AVS.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Account details</div>
              <div className="text-xs text-text-muted">We mask account numbers before storing.</div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <Field label="Bank" description="Select the issuing bank for this account.">
              <select
                id="bank"
                name="bank"
                className="aws-select w-full select-input"
                value={bank}
                onChange={(event) => setBank(event.target.value)}
              >
                {SA_BANKS.map((item) => (
                  <option key={item} value={item}>
                    {item}
                  </option>
                ))}
              </select>
            </Field>

            <Field label="Account number" description="We support cheque and savings accounts.">
              <input
                required
                id="accountNumber"
                name="accountNumber"
                value={accountNumber}
                onChange={(event) => {
                  const digits = event.target.value.replace(/\D/g, "").slice(0, 16);
                  setAccountNumber(digits);
                }}
                className="aws-input w-full"
                inputMode="numeric"
                maxLength={16}
              />
            </Field>

            <Field label="Account holder name (optional)">
              <input
                id="name"
                name="name"
                value={name}
                onChange={(event) => setName(event.target.value)}
                className="aws-input w-full"
              />
            </Field>

            <Field label="Account holder surname (optional)">
              <input
                id="surname"
                name="surname"
                value={surname}
                onChange={(event) => setSurname(event.target.value)}
                className="aws-input w-full"
              />
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                AVS performs a name match against the bank&apos;s core system.
              </p>
              <Button
                type="submit"
                variant="cta"
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Validate
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
                    {[...Array(4)].map((_, i) => (
                      <div key={i} className="flex items-center px-4 py-2.5">
                        <Skeleton className="h-4 w-32 bg-background/50" />
                        <Skeleton className="h-4 w-24 ml-auto" />
                      </div>
                    ))}
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  {[...Array(4)].map((_, i) => (
                    <div key={i} className="border border-border rounded p-3">
                      <Skeleton className="h-3 w-20 mb-2" />
                      <Skeleton className="h-4 w-12" />
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
              title="Validation results"
              reference={result.correlationId}
              status={result.result.accountFound && result.result.accountOpen ? "verified" : "not_verified"}
              onExport={handleExport}
              fields={[
                { label: "AVS status", value: result.result.avsStatus },
                { label: "Bank", value: result.result.bank },
                { label: "Account", value: result.result.accountMasked },
                {
                  label: "Account status",
                  value: result.result.accountStatus === "0" ? "Active" : "Inactive",
                },
              ]}
              matchFields={[
                { label: result.result.accountFound ? "Account found" : "Account not found", matched: result.result.accountFound },
                { label: result.result.accountOpen ? "Account open" : "Account closed", matched: result.result.accountOpen },
                { label: result.result.acceptsDebits ? "Accepts debits" : "No debits", matched: result.result.acceptsDebits },
                { label: result.result.accountTypeMatch ? "Type match" : "Type mismatch", matched: result.result.accountTypeMatch },
                { label: result.result.accountLengthMatch ? "Length match" : "Length mismatch", matched: result.result.accountLengthMatch },
                { label: result.result.nameMatch ? "Name match" : "Name mismatch", matched: result.result.nameMatch },
                { label: result.result.idMatch ? "ID match" : "ID mismatch", matched: result.result.idMatch },
                { label: result.result.emailMatch ? "Email match" : "Email mismatch", matched: result.result.emailMatch },
                { label: result.result.phoneMatch ? "Phone match" : "Phone mismatch", matched: result.result.phoneMatch },
              ]}
            >
              {result.result.errorCode && (
                <div className="mt-4 flex items-start gap-2 px-1 py-2 text-xs text-text-muted border-t border-border">
                  <span className="font-medium">Response:</span>
                  <span>
                    {result.result.errorCode} - {result.result.errorDescription}
                  </span>
                </div>
              )}
            </VerificationResultCard>
          ) : (
            <VerificationEmptyState
              icon={CreditCard}
              heading="No results yet"
              description="Enter account details and click Validate to see results."
            />
          )}
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Validating account"
        message="Running mock AVS check across the selected bank."
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
      {description && <span className="block text-xs text-text-muted">{description}</span>}
      {children}
    </label>
  );
}
