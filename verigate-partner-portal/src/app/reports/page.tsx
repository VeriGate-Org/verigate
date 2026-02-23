"use client";

import * as React from "react";
import { ReportBuilder } from "@/components/ui/Reports";
import type { Report } from "@/components/ui/Reports";
import { FileText, Calendar, Download } from "lucide-react";

export default function ReportsPage() {
  const [savedReports, setSavedReports] = React.useState<Report[]>([]);

  const handleGenerate = (report: Report) => {
    console.log("Generating report:", report);
    setSavedReports(prev => [report, ...prev]);
    // In production, this would call an API to generate the report
  };

  const handleSchedule = (report: Report) => {
    console.log("Scheduling report:", report);
    setSavedReports(prev => [report, ...prev]);
    // In production, this would call an API to schedule the report
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

      {/* Report Builder */}
      <ReportBuilder onGenerate={handleGenerate} onSchedule={handleSchedule} />

      {/* Recent Reports */}
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
