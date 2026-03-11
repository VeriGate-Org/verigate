"use client";

import * as React from "react";
import { ReportBuilder } from "@/components/ui/Reports";
import type { Report } from "@/components/ui/Reports";
import { FileText, Calendar, Download, Loader2 } from "lucide-react";
import { generateReport, scheduleReport, listReports, type BffReportResponse } from "@/lib/bff-client";
import { useToast } from "@/components/ui/Toast";

export default function ReportsPage() {
  const { toast } = useToast();
  const [savedReports, setSavedReports] = React.useState<Report[]>([]);
  const [loading, setLoading] = React.useState(false);

  const handleGenerate = async (report: Report) => {
    setLoading(true);
    try {
      await generateReport({
        name: report.name,
        type: report.type,
        description: report.description,
        filter: report.filter as unknown as Record<string, unknown>,
      });
      setSavedReports(prev => [report, ...prev]);
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
      setSavedReports(prev => [report, ...prev]);
      toast({ title: "Report scheduled", description: `"${report.name}" will run ${report.scheduled?.frequency ?? "on demand"}.`, variant: "success" });
    } catch (err) {
      toast({ title: "Scheduling failed", description: err instanceof Error ? err.message : "Could not schedule report.", variant: "error" });
    } finally {
      setLoading(false);
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
          Generate comprehensive reports and schedule automated delivery
        </p>
      </div>

      <ReportBuilder onGenerate={handleGenerate} onSchedule={handleSchedule} />

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
    </div>
  );
}
