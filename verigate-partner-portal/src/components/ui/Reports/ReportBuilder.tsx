"use client";

import * as React from "react";
import { cn } from "@/lib/cn";
import { Button } from "@/components/ui/Button";
import {
  Download,
  FileText,
  Calendar,
  Filter,
  BarChart3,
  TrendingUp,
  Users,
  CheckCircle,
  XCircle,
  Clock
} from "lucide-react";

// Report types
export type ReportType = "verification_summary" | "performance_metrics" | "compliance_audit" | "user_activity" | "custom";

export type DateRange = "today" | "week" | "month" | "quarter" | "year" | "custom";

export type ExportFormat = "pdf" | "excel" | "csv" | "json";

export interface ReportFilter {
  dateRange: DateRange;
  startDate?: Date;
  endDate?: Date;
  status?: string[];
  verificationTypes?: string[];
  users?: string[];
}

export interface Report {
  id: string;
  name: string;
  type: ReportType;
  description: string;
  filter: ReportFilter;
  createdAt: Date;
  createdBy: string;
  scheduled?: {
    frequency: "daily" | "weekly" | "monthly";
    time: string;
    recipients: string[];
  };
}

export interface ReportBuilderProps {
  onGenerate?: (report: Report) => void;
  onSchedule?: (report: Report) => void;
}

const REPORT_TYPES: Record<ReportType, { label: string; description: string; icon: React.ComponentType<{ className?: string }> }> = {
  verification_summary: {
    label: "Verification Summary",
    description: "Overview of all verification activities and outcomes",
    icon: BarChart3,
  },
  performance_metrics: {
    label: "Performance Metrics",
    description: "API performance, response times, and system health",
    icon: TrendingUp,
  },
  compliance_audit: {
    label: "Compliance Audit",
    description: "Regulatory compliance and audit trail reports",
    icon: FileText,
  },
  user_activity: {
    label: "User Activity",
    description: "User actions, permissions, and access patterns",
    icon: Users,
  },
  custom: {
    label: "Custom Report",
    description: "Build your own custom report with specific criteria",
    icon: Filter,
  },
};

export const ReportBuilder: React.FC<ReportBuilderProps> = ({
  onGenerate,
  onSchedule,
}) => {
  const [selectedType, setSelectedType] = React.useState<ReportType | null>(null);
  const [reportName, setReportName] = React.useState("");
  const [dateRange, setDateRange] = React.useState<DateRange>("month");
  const [exportFormat, setExportFormat] = React.useState<ExportFormat>("pdf");
  const [isScheduling, setIsScheduling] = React.useState(false);

  const handleGenerate = () => {
    if (!selectedType) {
      alert("Please select a report type");
      return;
    }

    const report: Report = {
      id: `report-${Date.now()}`,
      name: reportName || REPORT_TYPES[selectedType].label,
      type: selectedType,
      description: REPORT_TYPES[selectedType].description,
      filter: {
        dateRange,
      },
      createdAt: new Date(),
      createdBy: "current-user",
    };

    onGenerate?.(report);
    alert(`Generating ${exportFormat.toUpperCase()} report: ${report.name}`);
  };

  const handleSchedule = () => {
    if (!selectedType) {
      alert("Please select a report type");
      return;
    }

    const report: Report = {
      id: `report-${Date.now()}`,
      name: reportName || REPORT_TYPES[selectedType].label,
      type: selectedType,
      description: REPORT_TYPES[selectedType].description,
      filter: {
        dateRange,
      },
      createdAt: new Date(),
      createdBy: "current-user",
      scheduled: {
        frequency: "monthly",
        time: "09:00",
        recipients: ["user@example.com"],
      },
    };

    onSchedule?.(report);
    alert(`Report scheduled: ${report.name}`);
    setIsScheduling(false);
  };

  return (
    <div className="space-y-6">
      {/* Report Type Selection */}
      <div>
        <h3 className="text-lg font-semibold text-text mb-4">Select Report Type</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {(Object.entries(REPORT_TYPES) as [ReportType, typeof REPORT_TYPES[ReportType]][]).map(([type, info]) => {
            const Icon = info.icon;
            return (
              <button
                key={type}
                onClick={() => setSelectedType(type)}
                className={cn(
                  "p-4 rounded-aws-control border-2 transition-all text-left",
                  selectedType === type
                    ? "border-accent bg-accent-soft"
                    : "border-border hover:border-accent/50 hover:bg-hover"
                )}
              >
                <div className="flex items-start gap-3">
                  <div className={cn(
                    "p-2 rounded-aws-control",
                    selectedType === type ? "bg-accent text-white" : "bg-base-200 text-accent"
                  )}>
                    <Icon className="h-5 w-5" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <h4 className="font-medium text-text mb-1">{info.label}</h4>
                    <p className="text-xs text-text-muted line-clamp-2">{info.description}</p>
                  </div>
                </div>
              </button>
            );
          })}
        </div>
      </div>

      {selectedType && (
        <>
          {/* Report Configuration */}
          <div className="console-card">
            <div className="console-card-body space-y-4">
              <h3 className="text-lg font-semibold text-text">Configure Report</h3>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="text-xs text-text-muted mb-1 block">Report Name</label>
                  <input
                    type="text"
                    value={reportName}
                    onChange={(e) => setReportName(e.target.value)}
                    placeholder={REPORT_TYPES[selectedType].label}
                    className="aws-input w-full"
                  />
                </div>

                <div>
                  <label className="text-xs text-text-muted mb-1 block">Date Range</label>
                  <select
                    value={dateRange}
                    onChange={(e) => setDateRange(e.target.value as DateRange)}
                    className="aws-select w-full"
                  >
                    <option value="today">Today</option>
                    <option value="week">Last 7 Days</option>
                    <option value="month">Last 30 Days</option>
                    <option value="quarter">Last Quarter</option>
                    <option value="year">Last Year</option>
                    <option value="custom">Custom Range</option>
                  </select>
                </div>

                <div>
                  <label className="text-xs text-text-muted mb-1 block">Export Format</label>
                  <select
                    value={exportFormat}
                    onChange={(e) => setExportFormat(e.target.value as ExportFormat)}
                    className="aws-select w-full"
                  >
                    <option value="pdf">PDF Document</option>
                    <option value="excel">Excel Spreadsheet</option>
                    <option value="csv">CSV File</option>
                    <option value="json">JSON Data</option>
                  </select>
                </div>
              </div>

              <div className="flex items-center gap-3 pt-4">
                <Button onClick={handleGenerate}>
                  <Download className="h-4 w-4 mr-2" />
                  Generate Now
                </Button>
                <Button variant="secondary" onClick={() => setIsScheduling(!isScheduling)}>
                  <Calendar className="h-4 w-4 mr-2" />
                  Schedule Report
                </Button>
              </div>

              {isScheduling && (
                <div className="pt-4 border-t border-border space-y-4">
                  <h4 className="text-sm font-semibold text-text">Schedule Settings</h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="text-xs text-text-muted mb-1 block">Frequency</label>
                      <select className="aws-select w-full">
                        <option value="daily">Daily</option>
                        <option value="weekly">Weekly</option>
                        <option value="monthly">Monthly</option>
                      </select>
                    </div>
                    <div>
                      <label className="text-xs text-text-muted mb-1 block">Time</label>
                      <input type="time" className="aws-input w-full" defaultValue="09:00" />
                    </div>
                    <div className="md:col-span-2">
                      <label className="text-xs text-text-muted mb-1 block">Recipients (comma-separated emails)</label>
                      <input
                        type="text"
                        placeholder="user@example.com, admin@example.com"
                        className="aws-input w-full"
                      />
                    </div>
                  </div>
                  <Button onClick={handleSchedule}>Save Schedule</Button>
                </div>
              )}
            </div>
          </div>

          {/* Report Preview */}
          <div className="console-card">
            <div className="console-card-header">
              <h3 className="text-base font-semibold text-text">Report Preview</h3>
            </div>
            <div className="console-card-body">
              <div className="space-y-6">
                {/* Sample Metrics */}
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                  <div className="p-4 bg-base-200 rounded-aws-control">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-xs text-text-muted">Total Verifications</span>
                      <BarChart3 className="h-4 w-4 text-accent" />
                    </div>
                    <div className="text-2xl font-semibold text-text">12,543</div>
                    <div className="text-xs text-success mt-1">+15.3% from previous period</div>
                  </div>

                  <div className="p-4 bg-base-200 rounded-aws-control">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-xs text-text-muted">Success Rate</span>
                      <CheckCircle className="h-4 w-4 text-success" />
                    </div>
                    <div className="text-2xl font-semibold text-text">94.2%</div>
                    <div className="text-xs text-success mt-1">+2.1% improvement</div>
                  </div>

                  <div className="p-4 bg-base-200 rounded-aws-control">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-xs text-text-muted">Avg Response Time</span>
                      <Clock className="h-4 w-4 text-warning" />
                    </div>
                    <div className="text-2xl font-semibold text-text">245ms</div>
                    <div className="text-xs text-success mt-1">-12ms faster</div>
                  </div>

                  <div className="p-4 bg-base-200 rounded-aws-control">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-xs text-text-muted">Failed Checks</span>
                      <XCircle className="h-4 w-4 text-danger" />
                    </div>
                    <div className="text-2xl font-semibold text-text">728</div>
                    <div className="text-xs text-danger mt-1">+3.2% increase</div>
                  </div>
                </div>

                <div className="border-t border-border pt-4">
                  <p className="text-sm text-text-muted text-center">
                    This is a preview of the data that will be included in your report.
                    <br />
                    Actual report will contain detailed breakdowns, charts, and analysis.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};
