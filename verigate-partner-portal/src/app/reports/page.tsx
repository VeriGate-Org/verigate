"use client";

import * as React from "react";
import { ReportBuilder } from "@/components/ui/Reports";
import type { Report } from "@/components/ui/Reports";
import {
  createDeedsSavedReport,
  deleteDeedsSavedReport,
  exportDeedsSavedReport,
  generateReport,
  listDeedsExportHistory,
  listDeedsSavedReports,
  refreshDeedsSavedReport,
  scheduleReport,
  updateDeedsSavedReport,
  type BffDeedsExportHistoryResponse,
  type BffDeedsSavedReportResponse,
} from "@/lib/bff-client";
import { useToast } from "@/components/ui/Toast";
import { Calendar, Download, FileText, Loader2, RefreshCcw, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/Button";

const DEEDS_TEMPLATES = [
  {
    key: "area",
    name: "Area activity current report",
    reportType: "AREA_ACTIVITY",
    description: "Saved current report for provincial and township-level ownership changes.",
    filter: {},
  },
  {
    key: "municipal",
    name: "Municipal flags current report",
    reportType: "MUNICIPAL_FLAGS",
    description: "Highlights municipal arrears flags and unpaid-property candidates from cached deeds data.",
    filter: { municipalFlagsOnly: true },
  },
  {
    key: "transfers",
    name: "Transfer activity current report",
    reportType: "TRANSFER_ACTIVITY",
    description: "Tracks transfer movement in the cached deeds portfolio for export and follow-up.",
    filter: {},
  },
] as const;

export default function ReportsPage() {
  const { toast } = useToast();
  const [savedReports, setSavedReports] = React.useState<Report[]>([]);
  const [deedsReports, setDeedsReports] = React.useState<BffDeedsSavedReportResponse[]>([]);
  const [exportHistory, setExportHistory] = React.useState<BffDeedsExportHistoryResponse[]>([]);
  const [loading, setLoading] = React.useState(false);
  const [deedsLoading, setDeedsLoading] = React.useState(false);
  const [activeDeedsAction, setActiveDeedsAction] = React.useState<string | null>(null);

  const loadDeedsData = React.useCallback(async () => {
    const [reports, exports] = await Promise.all([
      listDeedsSavedReports(),
      listDeedsExportHistory(),
    ]);
    setDeedsReports(reports);
    setExportHistory(exports);
  }, []);

  React.useEffect(() => {
    void loadDeedsData();
  }, [loadDeedsData]);

  const handleGenerate = async (report: Report) => {
    setLoading(true);
    try {
      await generateReport({
        name: report.name,
        type: report.type,
        description: report.description,
        filter: report.filter as unknown as Record<string, unknown>,
      });
      setSavedReports((prev) => [report, ...prev]);
      toast({ title: "Report generated", description: `"${report.name}" is ready.`, variant: "success" });
    } catch (err) {
      toast({ title: "Generation failed", description: err instanceof Error ? err.message : "Could not generate report.", variant: "error" });
    } finally {
      setLoading(false);
    }
  };

  const handleSchedule = async (report: Report) => {
    setLoading(true);
    try {
      const generated = await generateReport({
        name: report.name,
        type: report.type,
        description: report.description,
        filter: report.filter as unknown as Record<string, unknown>,
      });
      if (report.scheduled) {
        await scheduleReport(generated.id, {
          frequency: report.scheduled.frequency,
          time: report.scheduled.time,
          recipients: report.scheduled.recipients,
        });
      }
      setSavedReports((prev) => [report, ...prev]);
      toast({ title: "Report scheduled", description: `"${report.name}" will run ${report.scheduled?.frequency ?? "on demand"}.`, variant: "success" });
    } catch (err) {
      toast({ title: "Scheduling failed", description: err instanceof Error ? err.message : "Could not schedule report.", variant: "error" });
    } finally {
      setLoading(false);
    }
  };

  const handleCreateDeedsTemplate = async (
    template: (typeof DEEDS_TEMPLATES)[number],
  ) => {
    setDeedsLoading(true);
    try {
      await createDeedsSavedReport({
        name: template.name,
        reportType: template.reportType,
        filter: template.filter,
        exportFormat: "csv",
        currentReport: true,
        autoRefresh: true,
        schedule: {
          frequency: "MONTHLY",
          time: "09:00",
          recipients: [],
        },
      });
      await loadDeedsData();
      toast({ title: "Deeds report saved", description: template.name, variant: "success" });
    } catch (err) {
      toast({ title: "Save failed", description: err instanceof Error ? err.message : "Could not save deeds report.", variant: "error" });
    } finally {
      setDeedsLoading(false);
    }
  };

  const handleRefreshDeeds = async (reportId: string) => {
    setActiveDeedsAction(reportId);
    try {
      await refreshDeedsSavedReport(reportId);
      await loadDeedsData();
      toast({ title: "Report refreshed", variant: "success" });
    } catch (err) {
      toast({ title: "Refresh failed", description: err instanceof Error ? err.message : "Could not refresh deeds report.", variant: "error" });
    } finally {
      setActiveDeedsAction(null);
    }
  };

  const handleExportDeeds = async (reportId: string, format: "csv" | "json" = "csv") => {
    setActiveDeedsAction(reportId);
    try {
      const file = await exportDeedsSavedReport(reportId, format);
      downloadContent(file.content, file.contentType, file.fileName);
      await loadDeedsData();
    } catch (err) {
      toast({ title: "Export failed", description: err instanceof Error ? err.message : "Could not export deeds report.", variant: "error" });
    } finally {
      setActiveDeedsAction(null);
    }
  };

  const handleToggleDeedsStatus = async (report: BffDeedsSavedReportResponse) => {
    setActiveDeedsAction(report.id);
    try {
      await updateDeedsSavedReport(report.id, {
        status: report.status === "ACTIVE" ? "PAUSED" : "ACTIVE",
      });
      await loadDeedsData();
    } catch (err) {
      toast({ title: "Update failed", description: err instanceof Error ? err.message : "Could not update deeds report.", variant: "error" });
    } finally {
      setActiveDeedsAction(null);
    }
  };

  const handleDeleteDeeds = async (reportId: string) => {
    setActiveDeedsAction(reportId);
    try {
      await deleteDeedsSavedReport(reportId);
      await loadDeedsData();
      toast({ title: "Report removed", variant: "success" });
    } catch (err) {
      toast({ title: "Delete failed", description: err instanceof Error ? err.message : "Could not remove deeds report.", variant: "error" });
    } finally {
      setActiveDeedsAction(null);
    }
  };

  return (
    <div className="p-8 space-y-8 max-w-7xl">
      <div>
        <h1 className="text-2xl font-semibold text-text mb-2 flex items-center gap-2">
          <FileText className="h-6 w-6 text-accent" />
          Enterprise Reporting
        </h1>
        <p className="text-text-muted text-aws-body">
          Generate comprehensive reports, save current deeds views, and manage recurring exports.
        </p>
      </div>

      <div className="grid gap-8 xl:grid-cols-[minmax(0,1.15fr)_minmax(0,0.85fr)]">
        <div className="space-y-8">
          <ReportBuilder onGenerate={handleGenerate} onSchedule={handleSchedule} />

          <div className="console-card">
            <div className="console-card-header">
              <div>
                <h2 className="text-lg font-semibold text-text">Deeds Current Reports</h2>
                <p className="text-xs text-text-muted">
                  Save auto-refreshing deeds reports from cached platform data while the provider contract is pending.
                </p>
              </div>
            </div>
            <div className="console-card-body space-y-4">
              <div className="grid gap-4 md:grid-cols-3">
                {DEEDS_TEMPLATES.map((template) => (
                  <div key={template.key} className="rounded border border-border bg-background p-4">
                    <div className="text-sm font-medium text-text">{template.name}</div>
                    <p className="mt-2 text-xs text-text-muted">{template.description}</p>
                    <Button
                      className="mt-4"
                      variant="secondary"
                      size="sm"
                      onClick={() => void handleCreateDeedsTemplate(template)}
                      loading={deedsLoading}
                    >
                      Save current report
                    </Button>
                  </div>
                ))}
              </div>

              {deedsReports.length === 0 ? (
                <div className="rounded border border-dashed border-border p-6 text-sm text-text-muted">
                  No saved deeds reports yet.
                </div>
              ) : (
                <div className="space-y-3">
                  {deedsReports.map((report) => (
                    <div
                      key={report.id}
                      className="rounded border border-border bg-background p-4"
                    >
                      <div className="flex flex-wrap items-start justify-between gap-3">
                        <div className="space-y-1">
                          <div className="flex flex-wrap items-center gap-2">
                            <h3 className="text-sm font-medium text-text">{report.name}</h3>
                            <span className="rounded border border-border px-2 py-1 text-[10px] uppercase tracking-wide text-text-muted">
                              {report.status}
                            </span>
                            {report.currentReport && (
                              <span className="rounded border border-border px-2 py-1 text-[10px] uppercase tracking-wide text-text-muted">
                                Current report
                              </span>
                            )}
                          </div>
                          <div className="text-xs text-text-muted">
                            {report.reportType} • Last generated {formatDateTime(report.lastGeneratedAt)}
                          </div>
                          <div className="text-xs text-text-muted">
                            Next run {formatDateTime(report.nextRunAt)} • Export {report.exportFormat.toUpperCase()}
                          </div>
                        </div>

                        <div className="flex flex-wrap gap-2">
                          <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => void handleRefreshDeeds(report.id)}
                            loading={activeDeedsAction === report.id}
                          >
                            <RefreshCcw className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => void handleExportDeeds(report.id)}
                            loading={activeDeedsAction === report.id}
                          >
                            <Download className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => void handleToggleDeedsStatus(report)}
                            loading={activeDeedsAction === report.id}
                          >
                            {report.status === "ACTIVE" ? "Pause" : "Resume"}
                          </Button>
                          <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => void handleDeleteDeeds(report.id)}
                            loading={activeDeedsAction === report.id}
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      </div>

                      {report.latestSummary && (
                        <div className="mt-4 grid gap-3 sm:grid-cols-4">
                          <MetricCard label="Properties" value={report.latestSummary.totalProperties} />
                          <MetricCard label="Owners" value={report.latestSummary.totalOwners} />
                          <MetricCard label="Bonds" value={report.latestSummary.totalActiveBonds} />
                          <MetricCard label="Flags" value={report.latestSummary.totalMunicipalFlags} />
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="space-y-8">
          {loading && (
            <div className="flex items-center justify-center gap-2 py-4 text-sm text-text-muted">
              <Loader2 className="h-4 w-4 animate-spin" />
              Processing report…
            </div>
          )}

          {savedReports.length > 0 && (
            <div className="console-card">
              <div className="console-card-header">
                <h2 className="text-lg font-semibold text-text">Recent Reports</h2>
              </div>
              <div className="console-card-body">
                <div className="space-y-3">
                  {savedReports.map((report) => (
                    <div
                      key={report.id}
                      className="flex items-center justify-between p-4 bg-base-200 rounded-aws-control"
                    >
                      <div className="flex items-center gap-3">
                        <FileText className="h-5 w-5 text-accent" />
                        <div>
                          <h4 className="font-medium text-text">{report.name}</h4>
                          <p className="text-xs text-text-muted">
                            {report.description} • Created {report.createdAt.toLocaleString()}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        {report.scheduled && (
                          <div className="flex items-center gap-1 text-xs text-text-muted">
                            <Calendar className="h-3 w-3" />
                            Scheduled
                          </div>
                        )}
                        <button className="p-2 hover:bg-hover rounded">
                          <Download className="h-4 w-4 text-accent" />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          <div className="console-card">
            <div className="console-card-header">
              <h2 className="text-lg font-semibold text-text">Deeds Export History</h2>
            </div>
            <div className="console-card-body space-y-3">
              {exportHistory.length === 0 ? (
                <div className="text-sm text-text-muted">
                  No deeds exports yet. Export a saved current report to create an auditable history.
                </div>
              ) : (
                exportHistory.slice(0, 8).map((item) => (
                  <div
                    key={item.id}
                    className="rounded border border-border bg-background p-3"
                  >
                    <div className="flex flex-wrap items-start justify-between gap-3">
                      <div>
                        <div className="text-sm font-medium text-text">{item.reportName}</div>
                        <div className="text-xs text-text-muted">
                          {item.format.toUpperCase()} • {item.recordCount.toLocaleString()} rows • Generated {formatDateTime(item.generatedAt)}
                        </div>
                      </div>
                      <span className="rounded border border-border px-2 py-1 text-[10px] uppercase tracking-wide text-text-muted">
                        {item.status}
                      </span>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function downloadContent(content: string, contentType: string, fileName: string) {
  const blob = new Blob([content], { type: contentType });
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement("a");
  anchor.href = url;
  anchor.download = fileName;
  anchor.click();
  URL.revokeObjectURL(url);
}

function formatDateTime(value: string | null | undefined) {
  if (!value) return "—";
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? "—" : date.toLocaleString();
}

function MetricCard({ label, value }: { label: string; value: number }) {
  return (
    <div className="rounded border border-border bg-base-200 p-3">
      <div className="text-[10px] uppercase tracking-wide text-text-muted">{label}</div>
      <div className="mt-1 text-sm font-medium text-text">{value.toLocaleString()}</div>
    </div>
  );
}
