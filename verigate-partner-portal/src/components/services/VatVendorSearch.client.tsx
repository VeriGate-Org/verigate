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
import { exportPdf } from "@/lib/utils/export-pdf";
import { type VatVendorSearchResponse } from "@/lib/mock-services";
import { Search } from "lucide-react";

export default function VatVendorSearch() {
  const [vatNumber, setVatNumber] = useState("");
  const [vendorDescription, setVendorDescription] = useState("");
  const [result, setResult] = useState<VatVendorSearchResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-vat-vendor-search.pdf");
    }
  }, []);

  const doVerification = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const params: Record<string, string> = { vatNumber };
      if (vendorDescription.trim()) {
        params.vendorDescription = vendorDescription.trim();
      }
      const data = (await executeVerification("VAT_VENDOR_VERIFICATION", params)) as VatVendorSearchResponse;
      setResult(data);
      toast({ title: "Search complete", variant: "success" });
      setTimeout(() => resultRef.current?.focus(), 100);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Search failed";
      setError(message);
      setResult(null);
      toast({ title: "Search failed", description: message, variant: "error" });
    } finally {
      setLoading(false);
    }
  }, [vatNumber, vendorDescription, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled = loading || vatNumber.length < 10;

  const srMessage = loading
    ? "Loading search results"
    : error
      ? "Search failed"
      : result
        ? "Search complete"
        : "";

  return (
    <div className="space-y-6">
      <ScreenReaderAnnounce message={srMessage} />
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">VAT vendor search</h1>
        <p className="text-sm text-text-muted">
          Search the SARS VAT vendor register to verify vendor registration status.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Search details</div>
              <div className="text-xs text-text-muted">Enter the VAT number to search.</div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <ServiceField label="VAT number" description="10-digit SARS VAT registration number.">
              <input
                required
                id="vatNumber"
                name="vatNumber"
                value={vatNumber}
                onChange={(event) => setVatNumber(event.target.value)}
                className="aws-input w-full"
                minLength={10}
                maxLength={10}
                pattern="\d{10}"
                placeholder="e.g. 4123456789"
              />
            </ServiceField>

            <ServiceField label="Vendor name (optional)" description="Filter by vendor name or description.">
              <input
                id="vendorDescription"
                name="vendorDescription"
                value={vendorDescription}
                onChange={(event) => setVendorDescription(event.target.value)}
                className="aws-input w-full"
                placeholder="e.g. Acme Trading"
              />
            </ServiceField>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Verified against SARS eFiling VAT vendor register.
              </p>
              <Button
                type="submit"
                variant="cta"
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Search
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
                      {[...Array(6)].map((_, i) => (
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
                title="VAT vendor results"
                reference={result.reference}
                status={result.vendor.status === "Active" ? "verified" : "not_verified"}
                onExport={handleExport}
                fields={[
                  { label: "Provider", value: result.provider },
                  { label: "VAT number", value: result.subject.vatNumber },
                  { label: "Vendor name", value: result.vendor.vendorName },
                  { label: "Trading name", value: result.vendor.tradingName },
                  {
                    label: "Registration date",
                    value: new Date(result.vendor.registrationDate).toLocaleDateString("en-ZA"),
                  },
                  { label: "Activity code", value: result.vendor.activityCode },
                  { label: "Physical address", value: result.vendor.physicalAddress },
                ]}
                matchFields={[
                  {
                    label: result.vendor.status === "Active" ? "VAT vendor active" : `VAT vendor ${result.vendor.status.toLowerCase()}`,
                    matched: result.vendor.status === "Active",
                  },
                ]}
              />
            ) : (
              <VerificationEmptyState
                icon={Search}
                heading="No results yet"
                description="Enter a VAT number and click Search to see vendor details."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Searching VAT vendor register"
        message="Querying SARS eFiling for vendor registration details."
      />
    </div>
  );
}
