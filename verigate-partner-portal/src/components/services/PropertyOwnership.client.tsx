"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
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
import {
  createDeedsWatch,
  deleteDeedsWatch,
  exportDeedsAreaReport,
  generateDeedsAreaReport,
  getDeedsDocumentManifest,
  getDeedsPropertyReport,
  listDeedsWatches,
  listDeedsWatchAlerts,
  updateDeedsWatch,
  type BffDeedsAreaReportResponse,
  type BffDeedsDocumentManifestResponse,
  type BffDeedsPropertyReportResponse,
  type BffDeedsWatchResponse,
  type BffDeedsWatchAlertResponse,
} from "@/lib/bff-client";
import { exportPdf } from "@/lib/utils/export-pdf";
import { Bell, FileDown, Map, Pause, Play, Trash2 } from "lucide-react";

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
  const [areaReport, setAreaReport] = useState<BffDeedsAreaReportResponse | null>(null);
  const [propertyReport, setPropertyReport] = useState<BffDeedsPropertyReportResponse | null>(null);
  const [documentManifest, setDocumentManifest] = useState<BffDeedsDocumentManifestResponse | null>(null);
  const [watches, setWatches] = useState<BffDeedsWatchResponse[]>([]);
  const [watchAlerts, setWatchAlerts] = useState<BffDeedsWatchAlertResponse[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [areaLoading, setAreaLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState<string | null>(null);
  const [watchLoading, setWatchLoading] = useState<string | null>(null);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-property-ownership.pdf");
    }
  }, []);

  const canSubmit = useMemo(() => query.trim().length > 2, [query]);

  const refreshWatchData = useCallback(async () => {
    const [watchItems, alertItems] = await Promise.all([
      listDeedsWatches().catch(() => []),
      listDeedsWatchAlerts().catch(() => []),
    ]);
    setWatches(watchItems);
    setWatchAlerts(alertItems);
  }, []);

  useEffect(() => {
    void refreshWatchData();
  }, [refreshWatchData]);

  const doVerification = useCallback(async () => {
    if (!canSubmit) return;
    setLoading(true);
    setError(null);
    setAreaReport(null);
    setPropertyReport(null);
    setDocumentManifest(null);

    try {
      const data = (await executeVerification("PROPERTY_OWNERSHIP_VERIFICATION", {
        searchType,
        query,
        province,
      })) as PropertyResponse;
      setResult(data);
      await refreshWatchData();
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
  }, [searchType, query, province, canSubmit, refreshWatchData, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled = loading || !canSubmit;

  const handleAreaReport = useCallback(async () => {
    if (!result) return;
    setAreaLoading(true);
    try {
      const report = await generateDeedsAreaReport({
        province: result.criteria.province,
      });
      setAreaReport(report);
      toast({ title: "Area report ready", variant: "success" });
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to build area report";
      toast({ title: "Area report failed", description: message, variant: "error" });
    } finally {
      setAreaLoading(false);
    }
  }, [result, toast]);

  const handleExportCsv = useCallback(async () => {
    if (!result) return;
    try {
      const exportPayload = await exportDeedsAreaReport(
        { province: result.criteria.province },
        "csv",
      );
      const blob = new Blob([exportPayload.content], { type: exportPayload.contentType });
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement("a");
      anchor.href = url;
      anchor.download = exportPayload.fileName;
      anchor.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to export CSV";
      toast({ title: "Export failed", description: message, variant: "error" });
    }
  }, [result, toast]);

  const handleLoadDocuments = useCallback(async (propertyId: string) => {
    setDetailLoading(propertyId);
    try {
      const [documents, report] = await Promise.all([
        getDeedsDocumentManifest(propertyId),
        getDeedsPropertyReport(propertyId),
      ]);
      setDocumentManifest(documents);
      setPropertyReport(report);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to load property details";
      toast({ title: "Property details failed", description: message, variant: "error" });
    } finally {
      setDetailLoading(null);
    }
  }, [toast]);

  const handleCreateWatch = useCallback(async (propertyId: string) => {
    setWatchLoading(propertyId);
    try {
      await createDeedsWatch({
        propertyId,
        monitoringFrequency: "MONTHLY",
      });
      await refreshWatchData();
      toast({ title: "Watch created", description: "Property watch has been added.", variant: "success" });
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to create property watch";
      toast({ title: "Watch failed", description: message, variant: "error" });
    } finally {
      setWatchLoading(null);
    }
  }, [refreshWatchData, toast]);

  const handleWatchStatus = useCallback(async (subjectId: string, status: "ACTIVE" | "PAUSED") => {
    setWatchLoading(subjectId);
    try {
      await updateDeedsWatch(subjectId, { status });
      await refreshWatchData();
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to update property watch";
      toast({ title: "Watch update failed", description: message, variant: "error" });
    } finally {
      setWatchLoading(null);
    }
  }, [refreshWatchData, toast]);

  const handleDeleteWatch = useCallback(async (subjectId: string) => {
    setWatchLoading(subjectId);
    try {
      await deleteDeedsWatch(subjectId);
      await refreshWatchData();
      toast({ title: "Watch removed", variant: "success" });
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to remove property watch";
      toast({ title: "Watch remove failed", description: message, variant: "error" });
    } finally {
      setWatchLoading(null);
    }
  }, [refreshWatchData, toast]);

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
                  <div className="console-card-body flex flex-wrap gap-2 pt-0">
                    <Button
                      variant="secondary"
                      size="sm"
                      onClick={handleExportCsv}
                    >
                      Export CSV
                    </Button>
                    <Button
                      variant="secondary"
                      size="sm"
                      onClick={handleAreaReport}
                      loading={areaLoading}
                    >
                      Generate area report
                    </Button>
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
                            <th scope="col" className="px-3 py-2">Actions</th>
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
                                {item.streetAddress && (
                                  <div className="text-xs text-text-muted">
                                    {item.streetAddress}
                                  </div>
                                )}
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
                              <td className="px-3 py-2">
                                <div className="flex flex-col gap-2">
                                  <Button
                                    variant="secondary"
                                    size="sm"
                                    onClick={() => void handleLoadDocuments(item.propertyId)}
                                    loading={detailLoading === item.propertyId}
                                  >
                                    Documents
                                  </Button>
                                  <Button
                                    variant="secondary"
                                    size="sm"
                                    onClick={() => void handleCreateWatch(item.propertyId)}
                                    loading={watchLoading === item.propertyId}
                                  >
                                    Watch
                                  </Button>
                                </div>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>

                {areaReport && (
                  <div className="console-card">
                    <div className="console-card-header">
                      <div>
                        <div className="text-sm font-semibold text-text">Area report</div>
                        <div className="text-xs text-text-muted">
                          Generated from cached partner deeds searches
                        </div>
                      </div>
                    </div>
                    <div className="console-card-body space-y-4">
                      <div className="grid gap-4 sm:grid-cols-4">
                        <SummaryCard label="Owners" value={areaReport.summary.totalOwners} />
                        <SummaryCard label="Transfers" value={areaReport.summary.totalTransfers} />
                        <SummaryCard label="Bonds" value={areaReport.summary.totalActiveBonds} />
                        <SummaryCard label="Flags" value={areaReport.summary.totalMunicipalFlags} />
                      </div>
                      <div className="overflow-x-auto">
                        <table className="min-w-full text-left text-sm">
                          <thead className="bg-background text-xs uppercase tracking-wide text-text-muted">
                            <tr>
                              <th className="px-3 py-2">Area</th>
                              <th className="px-3 py-2">Properties</th>
                              <th className="px-3 py-2">Owners</th>
                              <th className="px-3 py-2">Bonds</th>
                              <th className="px-3 py-2">Flags</th>
                              <th className="px-3 py-2">Transfers</th>
                            </tr>
                          </thead>
                          <tbody>
                            {areaReport.areas.map((area) => (
                              <tr key={`${area.province}-${area.township}`} className="border-b border-border last:border-0">
                                <td className="px-3 py-2">{area.township}, {area.province}</td>
                                <td className="px-3 py-2">{area.properties}</td>
                                <td className="px-3 py-2">{area.owners}</td>
                                <td className="px-3 py-2">{area.activeBonds}</td>
                                <td className="px-3 py-2">{area.municipalFlags}</td>
                                <td className="px-3 py-2">{area.transfers}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                )}

                {(documentManifest || propertyReport) && (
                  <div className="grid gap-4 xl:grid-cols-[minmax(0,1.15fr)_minmax(0,0.85fr)]">
                    {propertyReport && (
                      <div className="console-card">
                        <div className="console-card-header">
                          <div className="text-sm font-semibold text-text">Property report</div>
                        </div>
                        <div className="console-card-body space-y-4">
                          <div className="grid gap-3 sm:grid-cols-2">
                            <div>
                              <div className="text-xs text-text-muted">Title deed</div>
                              <div className="text-sm text-text">{propertyReport.property.titleDeed}</div>
                            </div>
                            <div>
                              <div className="text-xs text-text-muted">Deed number</div>
                              <div className="text-sm text-text">{propertyReport.property.deedNumber || "—"}</div>
                            </div>
                            <div>
                              <div className="text-xs text-text-muted">Owner</div>
                              <div className="text-sm text-text">{propertyReport.property.ownerName}</div>
                            </div>
                            <div>
                              <div className="text-xs text-text-muted">Street</div>
                              <div className="text-sm text-text">{propertyReport.property.streetAddress || "—"}</div>
                            </div>
                          </div>
                          <div>
                            <div className="text-xs text-text-muted mb-2">Recommended alerts</div>
                            <div className="flex flex-wrap gap-2">
                              {propertyReport.recommendedWatchAlerts.map((alertType) => (
                                <span
                                  key={alertType}
                                  className="rounded border border-border bg-background px-2 py-1 text-xs text-text"
                                >
                                  {alertType}
                                </span>
                              ))}
                            </div>
                          </div>
                        </div>
                      </div>
                    )}

                    {documentManifest && (
                      <div className="console-card">
                        <div className="console-card-header">
                          <div className="text-sm font-semibold text-text">Document manifest</div>
                        </div>
                        <div className="console-card-body space-y-3">
                          {documentManifest.documents.map((document) => (
                            <div
                              key={`${documentManifest.propertyId}-${document.type}`}
                              className="rounded border border-border bg-background p-3"
                            >
                              <div className="flex items-start justify-between gap-3">
                                <div>
                                  <div className="text-sm font-medium text-text">{document.label}</div>
                                  <div className="text-xs text-text-muted">{document.reference}</div>
                                </div>
                                <span className="rounded border border-border px-2 py-1 text-[10px] uppercase tracking-wide text-text-muted">
                                  {document.status.replaceAll("_", " ")}
                                </span>
                              </div>
                              <p className="mt-2 text-xs text-text-muted">{document.note}</p>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                )}

                <div className="console-card">
                  <div className="console-card-header">
                    <div className="flex items-center gap-2 text-sm font-semibold text-text">
                      <Bell className="h-4 w-4" />
                      Active watches
                    </div>
                  </div>
                  <div className="console-card-body space-y-3">
                    {watches.length === 0 ? (
                      <div className="text-sm text-text-muted">
                        No property watches yet. Create one from a property row to track transfers, bonds, and municipal changes.
                      </div>
                    ) : (
                      watches.map((watch) => (
                        <div
                          key={watch.subjectId}
                          className="rounded border border-border bg-background p-3"
                        >
                          <div className="flex flex-wrap items-start justify-between gap-3">
                            <div className="space-y-1">
                              <div className="text-sm font-medium text-text">{watch.subjectName}</div>
                              <div className="text-xs text-text-muted">{watch.titleDeed}</div>
                              <div className="flex flex-wrap gap-2 pt-1">
                                {watch.alertTypes.map((alertType) => (
                                  <span
                                    key={`${watch.subjectId}-${alertType}`}
                                    className="rounded border border-border px-2 py-1 text-[10px] uppercase tracking-wide text-text-muted"
                                  >
                                    {alertType}
                                  </span>
                                ))}
                              </div>
                            </div>
                            <div className="flex flex-wrap items-center gap-2">
                              <span className="rounded border border-border px-2 py-1 text-[10px] uppercase tracking-wide text-text-muted">
                                {watch.status}
                              </span>
                              <Button
                                variant="secondary"
                                size="sm"
                                onClick={() => void handleWatchStatus(
                                  watch.subjectId,
                                  watch.status === "ACTIVE" ? "PAUSED" : "ACTIVE",
                                )}
                                loading={watchLoading === watch.subjectId}
                              >
                                {watch.status === "ACTIVE" ? <Pause className="h-4 w-4" /> : <Play className="h-4 w-4" />}
                              </Button>
                              <Button
                                variant="secondary"
                                size="sm"
                                onClick={() => void handleDeleteWatch(watch.subjectId)}
                                loading={watchLoading === watch.subjectId}
                              >
                                <Trash2 className="h-4 w-4" />
                              </Button>
                            </div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>

                <div className="console-card">
                  <div className="console-card-header">
                    <div className="flex items-center gap-2 text-sm font-semibold text-text">
                      <Bell className="h-4 w-4" />
                      Watch alerts
                    </div>
                  </div>
                  <div className="console-card-body space-y-3">
                    {watchAlerts.length === 0 ? (
                      <div className="text-sm text-text-muted">
                        No cached deeds watch alerts yet. Create a watch from a property row to start tracking changes.
                      </div>
                    ) : (
                      watchAlerts.slice(0, 6).map((alert) => (
                        <div
                          key={alert.alertId}
                          className="rounded border border-border bg-background p-3"
                        >
                          <div className="flex items-start justify-between gap-3">
                            <div>
                              <div className="text-sm font-medium text-text">{alert.title}</div>
                              <div className="text-xs text-text-muted">{alert.description}</div>
                            </div>
                            <span className="rounded border border-border px-2 py-1 text-[10px] uppercase tracking-wide text-text-muted">
                              {alert.severity}
                            </span>
                          </div>
                        </div>
                      ))
                    )}
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
        message="Collecting deeds registry results and cached property intelligence..."
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
