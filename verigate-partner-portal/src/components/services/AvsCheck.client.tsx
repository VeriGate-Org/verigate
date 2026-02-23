"use client";

import { useCallback, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { type AvsResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { SA_BANKS } from "@/lib/sa-banks";

export default function AvsCheck() {
  const [name, setName] = useState("");
  const [surname, setSurname] = useState("");
  const [accountNumber, setAccountNumber] = useState("");
  const [bank, setBank] = useState<string>(SA_BANKS[0] ?? "");
  const [result, setResult] = useState<AvsResponse | null>(null);
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
      const data = await executeVerification("BANK_ACCOUNT_VERIFICATION", { name, surname, accountNumber, bank }) as AvsResponse;
      setResult(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const submitDisabled = loading || accountNumber.length < 6 || !bank;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Bank account validation</h1>
        <p className="text-sm text-text-muted">Validate account ownership across South African banks using AVS.</p>
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
                value={accountNumber}
                onChange={(event) => {
                  const digits = event.target.value.replace(/\D/g, "").slice(0, 16);
                  setAccountNumber(digits);
                }}
                className="aws-input w-full"
                inputMode="numeric"
                maxLength={16}
                autoComplete="off"
              />
            </Field>

            <Field label="Account holder name (optional)">
              <input
                value={name}
                onChange={(event) => setName(event.target.value)}
                className="aws-input w-full"
              />
            </Field>

            <Field label="Account holder surname (optional)">
              <input
                value={surname}
                onChange={(event) => setSurname(event.target.value)}
                className="aws-input w-full"
              />
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">AVS performs a name match against the bank&apos;s core system.</p>
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
              <div className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">{error}</div>
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
              {/* Verification Details Skeleton */}
              <div>
                <Skeleton className="h-4 w-36 mb-2" />
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

              {/* Account Verification Skeleton */}
              <div>
                <Skeleton className="h-4 w-40 mb-2" />
                <div className="grid grid-cols-2 gap-4">
                  <div className="border border-border rounded overflow-hidden">
                    <div className="divide-y divide-border">
                      {[...Array(3)].map((_, i) => (
                        <div key={i} className="flex items-center px-3 py-2">
                          <Skeleton className="h-4 w-28 bg-background/50" />
                          <Skeleton className="h-4 w-12 ml-auto" />
                        </div>
                      ))}
                    </div>
                  </div>
                  <div className="border border-border rounded overflow-hidden">
                    <div className="divide-y divide-border">
                      {[...Array(2)].map((_, i) => (
                        <div key={i} className="flex items-center px-3 py-2">
                          <Skeleton className="h-4 w-28 bg-background/50" />
                          <Skeleton className="h-4 w-12 ml-auto" />
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>

              {/* Data Matching Skeleton */}
              <div>
                <Skeleton className="h-4 w-28 mb-2" />
                <div className="grid grid-cols-2 gap-4">
                  <div className="border border-border rounded overflow-hidden">
                    <div className="divide-y divide-border">
                      {[...Array(2)].map((_, i) => (
                        <div key={i} className="flex items-center px-3 py-2">
                          <Skeleton className="h-4 w-24 bg-background/50" />
                          <Skeleton className="h-4 w-12 ml-auto" />
                        </div>
                      ))}
                    </div>
                  </div>
                  <div className="border border-border rounded overflow-hidden">
                    <div className="divide-y divide-border">
                      {[...Array(2)].map((_, i) => (
                        <div key={i} className="flex items-center px-3 py-2">
                          <Skeleton className="h-4 w-24 bg-background/50" />
                          <Skeleton className="h-4 w-12 ml-auto" />
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>

              {/* Response Code Skeleton */}
              <div className="flex items-center gap-2 px-1 py-2 border-t border-border">
                <Skeleton className="h-3 w-16" />
                <Skeleton className="h-3 w-64" />
              </div>
            </div>
          </div>
        ) : result ? (
          <div className="console-card">
            <div className="console-card-header">
              <div>
                <div className="text-sm font-semibold text-text">Validation results</div>
                <div className="text-xs text-text-muted">Correlation {result.correlationId}</div>
              </div>
              <button 
                onClick={handleExport}
                className="rounded-full border border-[color:var(--color-cta)] bg-[color:var(--color-base-100)] text-[color:var(--color-cta)] hover:bg-[color:var(--color-cta)] hover:text-white px-aws-l py-aws-s text-sm transition-all shadow-sm"
              >
                Export PDF
              </button>
            </div>
            <div className="console-card-body space-y-6 p-4">
              {/* Verification Details Section */}
              <div>
                <h3 className="text-sm font-medium text-text mb-2">Verification Details</h3>
                <div className="border border-border rounded overflow-hidden">
                  <table className="w-full">
                    <tbody className="divide-y divide-border">
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30 w-1/3">
                          AVS status
                        </td>
                        <td className="px-4 py-2.5 text-sm">
                          <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-success/10 text-success">
                            {result.result.avsStatus}
                          </span>
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Bank
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.result.bank}
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Account number
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.result.accountMasked}
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Account status
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.result.accountStatus === "0" ? "Active" : "Inactive"}
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              {/* Account Verification Section - 2 columns */}
              <div>
                <h3 className="text-sm font-medium text-text mb-2">Account Verification</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div className="border border-border rounded overflow-hidden">
                    <table className="w-full">
                      <tbody className="divide-y divide-border">
                        <tr className="hover:bg-background/50">
                          <td className="px-3 py-2 text-sm text-text-muted bg-background/30">
                            Account found
                          </td>
                          <td className="px-3 py-2 text-sm">
                            <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.result.accountFound 
                                ? 'bg-success/10 text-success' 
                                : 'bg-danger/10 text-danger'
                            }`}>
                              {result.result.accountFound ? 'Yes' : 'No'}
                            </span>
                          </td>
                        </tr>
                        <tr className="hover:bg-background/50">
                          <td className="px-3 py-2 text-sm text-text-muted bg-background/30">
                            Account open
                          </td>
                          <td className="px-3 py-2 text-sm">
                            <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.result.accountOpen 
                                ? 'bg-success/10 text-success' 
                                : 'bg-danger/10 text-danger'
                            }`}>
                              {result.result.accountOpen ? 'Yes' : 'No'}
                            </span>
                          </td>
                        </tr>
                        <tr className="hover:bg-background/50">
                          <td className="px-3 py-2 text-sm text-text-muted bg-background/30">
                            Accepts debits
                          </td>
                          <td className="px-3 py-2 text-sm">
                            <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.result.acceptsDebits 
                                ? 'bg-success/10 text-success' 
                                : 'bg-danger/10 text-danger'
                            }`}>
                              {result.result.acceptsDebits ? 'Yes' : 'No'}
                            </span>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                  <div className="border border-border rounded overflow-hidden">
                    <table className="w-full">
                      <tbody className="divide-y divide-border">
                        <tr className="hover:bg-background/50">
                          <td className="px-3 py-2 text-sm text-text-muted bg-background/30">
                            Type match
                          </td>
                          <td className="px-3 py-2 text-sm">
                            <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.result.accountTypeMatch 
                                ? 'bg-success/10 text-success' 
                                : 'bg-danger/10 text-danger'
                            }`}>
                              {result.result.accountTypeMatch ? 'Yes' : 'No'}
                            </span>
                          </td>
                        </tr>
                        <tr className="hover:bg-background/50">
                          <td className="px-3 py-2 text-sm text-text-muted bg-background/30">
                            Length match
                          </td>
                          <td className="px-3 py-2 text-sm">
                            <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.result.accountLengthMatch 
                                ? 'bg-success/10 text-success' 
                                : 'bg-danger/10 text-danger'
                            }`}>
                              {result.result.accountLengthMatch ? 'Yes' : 'No'}
                            </span>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

              {/* Data Matching Section - 2 columns */}
              <div>
                <h3 className="text-sm font-medium text-text mb-2">Data Matching</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div className="border border-border rounded overflow-hidden">
                    <table className="w-full">
                      <tbody className="divide-y divide-border">
                        <tr className="hover:bg-background/50">
                          <td className="px-3 py-2 text-sm text-text-muted bg-background/30">
                            Name match
                          </td>
                          <td className="px-3 py-2 text-sm">
                            <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.result.nameMatch 
                                ? 'bg-success/10 text-success' 
                                : 'bg-danger/10 text-danger'
                            }`}>
                              {result.result.nameMatch ? 'Yes' : 'No'}
                            </span>
                          </td>
                        </tr>
                        <tr className="hover:bg-background/50">
                          <td className="px-3 py-2 text-sm text-text-muted bg-background/30">
                            Email match
                          </td>
                          <td className="px-3 py-2 text-sm">
                            <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.result.emailMatch 
                                ? 'bg-success/10 text-success' 
                                : 'bg-danger/10 text-danger'
                            }`}>
                              {result.result.emailMatch ? 'Yes' : 'No'}
                            </span>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                  <div className="border border-border rounded overflow-hidden">
                    <table className="w-full">
                      <tbody className="divide-y divide-border">
                        <tr className="hover:bg-background/50">
                          <td className="px-3 py-2 text-sm text-text-muted bg-background/30">
                            ID match
                          </td>
                          <td className="px-3 py-2 text-sm">
                            <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.result.idMatch 
                                ? 'bg-success/10 text-success' 
                                : 'bg-danger/10 text-danger'
                            }`}>
                              {result.result.idMatch ? 'Yes' : 'No'}
                            </span>
                          </td>
                        </tr>
                        <tr className="hover:bg-background/50">
                          <td className="px-3 py-2 text-sm text-text-muted bg-background/30">
                            Phone match
                          </td>
                          <td className="px-3 py-2 text-sm">
                            <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.result.phoneMatch 
                                ? 'bg-success/10 text-success' 
                                : 'bg-danger/10 text-danger'
                            }`}>
                              {result.result.phoneMatch ? 'Yes' : 'No'}
                            </span>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

              {/* Response Code */}
              {result.result.errorCode && (
                <div className="flex items-start gap-2 px-1 py-2 text-xs text-text-muted border-t border-border">
                  <span className="font-medium">Response:</span>
                  <span>{result.result.errorCode} - {result.result.errorDescription}</span>
                </div>
              )}
            </div>
          </div>
        ) : (
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Validation results</div>
            </div>
            <div className="console-card-body flex items-center justify-center py-12">
              <div className="text-center text-sm text-text-muted">
                <div className="mb-2">No results yet</div>
                <div className="text-xs">Enter account details and click Validate to see results</div>
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
      <ProcessingDialog open={loading} title="Validating account" message="Running mock AVS check across the selected bank." />
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
