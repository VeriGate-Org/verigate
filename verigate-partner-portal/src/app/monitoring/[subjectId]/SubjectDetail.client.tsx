"use client";

import * as React from "react";
import { useParams, useRouter } from "next/navigation";
import {
  useMonitoredSubject,
  useUpdateMonitoredSubject,
  useDeleteMonitoredSubject,
  useMonitoringAlerts,
  useAcknowledgeAlert,
} from "@/lib/hooks/useMonitoring";
import type { MonitoringFrequency } from "@/lib/bff-client";
import { Button } from "@/components/ui/Button";
import { cn } from "@/lib/cn";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { ArrowLeft, Bell } from "lucide-react";

const STATUS_STYLES: Record<string, string> = {
  ACTIVE: "bg-green-500/10 text-green-600 border-green-500/20",
  PAUSED: "bg-yellow-500/10 text-yellow-600 border-yellow-500/20",
  REMOVED: "bg-gray-500/10 text-gray-500 border-gray-500/20",
};

const SEVERITY_STYLES: Record<string, string> = {
  HIGH: "bg-red-500/10 text-red-600 border-red-500/20",
  MEDIUM: "bg-yellow-500/10 text-yellow-600 border-yellow-500/20",
  LOW: "bg-blue-500/10 text-blue-600 border-blue-500/20",
};

const FREQUENCIES: { value: MonitoringFrequency; label: string }[] = [
  { value: "DAILY", label: "Daily" },
  { value: "WEEKLY", label: "Weekly" },
  { value: "MONTHLY", label: "Monthly" },
  { value: "QUARTERLY", label: "Quarterly" },
];

export default function SubjectDetail() {
  const params = useParams<{ subjectId: string }>();
  const router = useRouter();
  const { data: subject, isLoading, error } = useMonitoredSubject(params.subjectId);
  const { data: alerts } = useMonitoringAlerts({ subjectId: params.subjectId });
  const updateMutation = useUpdateMonitoredSubject();
  const deleteMutation = useDeleteMonitoredSubject();
  const acknowledgeMutation = useAcknowledgeAlert();

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-4 w-32" />
        <Skeleton className="h-64 w-full" />
      </div>
    );
  }

  if (error || !subject) {
    return (
      <div className="rounded border border-danger/40 bg-danger/5 px-4 py-3 text-sm text-danger">
        {error instanceof Error ? error.message : "Subject not found"}
      </div>
    );
  }

  const handleFrequencyChange = (freq: MonitoringFrequency) => {
    updateMutation.mutate({ subjectId: params.subjectId, updates: { monitoringFrequency: freq } });
  };

  const handleToggleStatus = () => {
    const newStatus = subject.status === "ACTIVE" ? "PAUSED" : "ACTIVE";
    updateMutation.mutate({ subjectId: params.subjectId, updates: { status: newStatus } });
  };

  const handleRemove = () => {
    deleteMutation.mutate(params.subjectId, {
      onSuccess: () => router.push("/monitoring"),
    });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="sm" onClick={() => router.push("/monitoring")}>
          <ArrowLeft className="h-4 w-4 mr-1" /> Back
        </Button>
        <div>
          <h1 className="text-xl font-semibold text-text">{subject.subjectName || "Unknown Subject"}</h1>
          <p className="text-sm text-text-muted">
            {subject.subjectType} &middot; Added {new Date(subject.createdAt).toLocaleDateString()}
          </p>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
        {/* Main content */}
        <div className="space-y-6">
          {/* Subject Info */}
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Subject Information</div>
            </div>
            <div className="console-card-body">
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="text-text-muted">Name</span>
                  <p className="font-medium text-text">{subject.subjectName || "-"}</p>
                </div>
                <div>
                  <span className="text-text-muted">Identifier</span>
                  <p className="font-mono text-xs text-text">{subject.subjectIdentifier || "-"}</p>
                </div>
                <div>
                  <span className="text-text-muted">Type</span>
                  <p className="font-medium text-text">{subject.subjectType || "-"}</p>
                </div>
                <div>
                  <span className="text-text-muted">Subject ID</span>
                  <p className="font-mono text-xs text-text">{subject.subjectId}</p>
                </div>
              </div>
            </div>
          </div>

          {/* Risk Status */}
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Current Risk</div>
            </div>
            <div className="console-card-body">
              <div className="flex items-center gap-8">
                <div className="text-center">
                  <div className="text-3xl font-bold text-text">
                    {subject.lastRiskScore != null ? subject.lastRiskScore : "-"}
                  </div>
                  <div className="text-xs text-text-muted">Last Risk Score</div>
                </div>
                <div>
                  <span className="inline-flex items-center px-3 py-1 rounded text-sm font-medium bg-yellow-500/10 text-yellow-600">
                    {subject.lastRiskDecision || "PENDING"}
                  </span>
                </div>
                <div className="text-sm">
                  <div className="text-text-muted">Last Checked</div>
                  <div className="text-text">
                    {subject.lastCheckedAt
                      ? new Date(subject.lastCheckedAt).toLocaleString()
                      : "Never"}
                  </div>
                </div>
                <div className="text-sm">
                  <div className="text-text-muted">Next Check</div>
                  <div className="text-text">
                    {subject.nextCheckAt
                      ? new Date(subject.nextCheckAt).toLocaleString()
                      : "-"}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Alert History */}
          <div className="console-card">
            <div className="console-card-header">
              <div className="flex items-center gap-2">
                <Bell className="h-4 w-4" />
                <span className="text-sm font-semibold text-text">Alert History</span>
              </div>
            </div>
            <div className="console-card-body">
              {!alerts || alerts.length === 0 ? (
                <p className="text-sm text-text-muted text-center py-6">
                  No alerts recorded for this subject.
                </p>
              ) : (
                <div className="space-y-2">
                  {alerts.map((alert) => (
                    <div
                      key={alert.alertId}
                      className={cn(
                        "border border-border rounded p-3",
                        alert.acknowledged && "opacity-60"
                      )}
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <div className="flex items-center gap-2 mb-1">
                            <span className={cn("inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border", SEVERITY_STYLES[alert.severity] || "")}>
                              {alert.severity}
                            </span>
                            <span className="text-sm font-medium text-text">{alert.title}</span>
                          </div>
                          {alert.description && (
                            <p className="text-xs text-text-muted">{alert.description}</p>
                          )}
                          <div className="flex items-center gap-4 text-xs text-text-muted mt-1">
                            {alert.previousRiskScore != null && alert.currentRiskScore != null && (
                              <span>Score: {alert.previousRiskScore} → {alert.currentRiskScore}</span>
                            )}
                            <span>{new Date(alert.createdAt).toLocaleString()}</span>
                          </div>
                        </div>
                        {!alert.acknowledged && (
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => acknowledgeMutation.mutate(alert.alertId)}
                          >
                            Ack
                          </Button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-4">
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Configuration</div>
            </div>
            <div className="console-card-body space-y-3">
              <div>
                <span className="text-xs text-text-muted">Status</span>
                <div className="mt-1">
                  <span className={cn("inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border", STATUS_STYLES[subject.status] || "")}>
                    {subject.status}
                  </span>
                </div>
              </div>
              <div>
                <span className="text-xs text-text-muted">Check Frequency</span>
                <select
                  value={subject.monitoringFrequency}
                  onChange={(e) => handleFrequencyChange(e.target.value as MonitoringFrequency)}
                  className="aws-select w-full mt-1"
                  disabled={subject.status !== "ACTIVE"}
                >
                  {FREQUENCIES.map((f) => (
                    <option key={f.value} value={f.value}>{f.label}</option>
                  ))}
                </select>
              </div>
              {subject.policyId && (
                <div>
                  <span className="text-xs text-text-muted">Policy ID</span>
                  <p className="font-mono text-xs text-text mt-1">{subject.policyId}</p>
                </div>
              )}

              <div className="pt-3 border-t border-border space-y-2">
                <Button
                  variant="secondary"
                  size="sm"
                  className="w-full"
                  onClick={handleToggleStatus}
                >
                  {subject.status === "ACTIVE" ? "Pause Monitoring" : "Resume Monitoring"}
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  className="w-full text-danger"
                  onClick={handleRemove}
                >
                  Remove Subject
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
