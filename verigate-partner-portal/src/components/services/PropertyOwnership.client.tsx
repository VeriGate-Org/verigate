"use client";

import { useCallback, useMemo, useRef, useState } from "react";
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
import {
  type PropertyOwnershipResponse as PropertyResponse,
} from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { exportPdf } from "@/lib/utils/export-pdf";
import { FileDown, Map } from "lucide-react";

const PROVINCES = [
  "Gauteng",
  "Western Cape",
  "KwaZulu-Natal",
  "Eastern Cape",
  "Mpumalanga",
  "Limpopo",
  "Free State",
  "North West",
  "Northern Cape",
] as const;

export default function PropertyOwnership() {
  const [searchType, setSearchType] = useState<string>("ownerName");
  const [query, setQuery] = useState<string>("");
  const [province, setProvince] = useState<string>(PROVINCES[0]);
  const [result, setResult] = useState<PropertyResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-property-ownership.pdf");
    }
  }, []);

  const canSubmit = useMemo(() => query.trim().length > 2, [query]);

  const doVerification = useCallback(async () => {
    if (!canSubmit) return;
    setLoading(true);
    setError(null);

    try {
      const data = (await executeVerification("PROPERTY_OWNERSHIP_VERIFICATION", {
        searchType,
        query,
        province,
      })) as PropertyResponse;
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
  }, [searchType, query, province, canSubmit, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled = loading || !canSubmit;

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
        <h1 className="text-xl font-semibold text-text">Deeds registry search</h1>
        <p className="text-sm text-text-muted">
          Search ownership, bonds, and municipal status from the national deeds registry.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,480px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Search criteria</div>
              <div className="text-xs text-text-muted">
                Owner, ID number, or erf/title information.
              </div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <ServiceField label="Search type">
              <select
                id="searchType"
                name="searchType"
                className="aws-select w-full select-input"
                value={searchType}
                onChange={(event) => setSearchType(event.target.value)}
              >
                <option value="ownerName">Owner name</option>
                <option value="ownerId">Owner ID number</option>
                <option value="erf">ERF / title details</option>
              </select>
            </ServiceField>

            <ServiceField label="Query" description="Minimum three characters.">
              <input
                id="query"
                name="query"
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                className="aws-input w-full"
                placeholder={searchType === "ownerId" ? "ID number" : "e.g. John Doe"}
              />
            </ServiceField>

            <ServiceField label="Province">
              <select
                id="province"
                name="province"
                className="aws-select w-full select-input"
                value={province}
                onChange={(event) => setProvince(event.target.value)}
              >
                {PROVINCES.map((item) => (
                  <option key={item} value={item}>
                    {item}
                  </option>
                ))}
              </select>
            </ServiceField>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Results show active bonds and municipal arrears flags.
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
                  <div className="grid grid-cols-3 gap-4">
                    {[...Array(3)].map((_, i) => (
                      <div key={i} className="border border-border rounded p-3">
                        <Skeleton className="h-3 w-20 mb-2" />
                        <Skeleton className="h-5 w-8" />
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
              <div ref={resultCardRef} className="space-y-4">
                <div className="console-card" role="region" aria-label="Property search summary">
                  <div className="console-card-header">
                    <div>
                      <div className="text-sm font-semibold text-text">Summary</div>
                      <div className="text-xs text-text-muted">
                        {result.criteria.query} &bull; {result.criteria.province}
                      </div>
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
                  <div className="console-card-body grid gap-4 sm:grid-cols-3">
                    <SummaryCard label="Properties" value={result.summary.totalProperties} />
                    <SummaryCard label="Active bonds" value={result.summary.totalActiveBonds} />
                    <SummaryCard label="Municipal flags" value={result.summary.totalMunicipalFlags} />
                  </div>
                </div>

                <div className="console-card">
                  <div className="console-card-header">
                    <div className="text-sm font-semibold text-text">Properties</div>
                  </div>
                  <div className="console-card-body p-0">
                    <div className="overflow-x-auto">
                      <table className="min-w-full text-left text-sm">
                        <thead className="bg-background text-xs uppercase tracking-wide text-text-muted">
                          <tr>
                            <th scope="col" className="px-3 py-2">Property</th>
                            <th scope="col" className="px-3 py-2">Owner</th>
                            <th scope="col" className="px-3 py-2">Title deed</th>
                            <th scope="col" className="px-3 py-2">Registration date</th>
                            <th scope="col" className="px-3 py-2">Bonds</th>
                            <th scope="col" className="px-3 py-2">Municipal</th>
                          </tr>
                        </thead>
                        <tbody>
                          {result.items.map((item) => (
                            <tr
                              key={item.propertyId}
                              className="border-b border-border last:border-0"
                            >
                              <td className="px-3 py-2">
                                <div className="font-medium text-text">
                                  ERF {item.erfNumber}/{item.portion}
                                </div>
                                <div className="text-xs text-text-muted">
                                  {item.township}, {item.province}
                                </div>
                              </td>
                              <td className="px-3 py-2">
                                <div className="text-text">{item.ownerName}</div>
                                <div className="text-xs text-text-muted">
                                  {item.ownerIdNumber}
                                </div>
                              </td>
                              <td className="px-3 py-2 text-text">{item.titleDeed}</td>
                              <td className="px-3 py-2 text-text-muted">
                                {formatDate(item.registrationDate)}
                              </td>
                              <td className="px-3 py-2">
                                {item.currentBonds.length ? (
                                  <ul className="space-y-1 text-xs text-text">
                                    {item.currentBonds.map((bond, index) => (
                                      <li key={`${item.propertyId}-bond-${index}`}>
                                        {bond.bondholder} &bull; ZAR{" "}
                                        {bond.amount.toLocaleString()}
                                      </li>
                                    ))}
                                  </ul>
                                ) : (
                                  <span className="text-xs text-text-muted">None</span>
                                )}
                              </td>
                              <td className="px-3 py-2 text-xs text-text">
                                <div>Acct: {item.municipal.accountNumber}</div>
                                <div>
                                  Arrears:{" "}
                                  {item.municipal.arrears
                                    ? `ZAR ${item.municipal.arrears.toLocaleString()}`
                                    : "None"}
                                </div>
                                {item.municipal.ratesFlag && (
                                  <div className="text-danger">Rates flag</div>
                                )}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <VerificationEmptyState
                icon={Map}
                heading="No results yet"
                description="Enter search criteria and click Search to see property records."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Searching deeds"
        message="Generating mock property ownership records..."
      />
    </div>
  );
}

function formatDate(iso: string) {
  if (!iso) return "—";
  const date = new Date(iso);
  return Number.isNaN(date.getTime()) ? "—" : date.toLocaleDateString();
}

function SummaryCard({ label, value }: { label: string; value: number }) {
  return (
    <div className="space-y-1 rounded border border-border bg-background p-3">
      <div className="text-xs uppercase tracking-wide text-text-muted">{label}</div>
      <div className="text-sm font-medium text-text">{value.toLocaleString()}</div>
    </div>
  );
}
