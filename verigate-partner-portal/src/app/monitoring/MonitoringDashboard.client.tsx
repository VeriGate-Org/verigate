"use client";

import * as React from "react";
import { useRouter } from "next/navigation";
import {
  useMonitoredSubjects,
  useCreateMonitoredSubject,
  useUpdateMonitoredSubject,
  useDeleteMonitoredSubject,
  useMonitoringAlerts,
  useAcknowledgeAlert,
} from "@/lib/hooks/useMonitoring";
import type { MonitoringFrequency } from "@/lib/bff-client";
import { Button } from "@/components/ui/Button";
import { cn } from "@/lib/cn";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { Users, Bell, Plus, X, Lightbulb } from "lucide-react";

type Tab = "subjects" | "alerts";

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

export default function MonitoringDashboard() {
  const router = useRouter();
  const [tab, setTab] = React.useState<Tab>("subjects");
  const [statusFilter, setStatusFilter] = React.useState<string>("");
  const [showAddForm, setShowAddForm] = React.useState(false);

  // Add form state
  const [newSubjectName, setNewSubjectName] = React.useState("");
  const [newSubjectIdentifier, setNewSubjectIdentifier] = React.useState("");
  const [newSubjectType, setNewSubjectType] = React.useState("INDIVIDUAL");
  const [newFrequency, setNewFrequency] = React.useState<MonitoringFrequency>("MONTHLY");

  const { data: subjects, isLoading: subjectsLoading } = useMonitoredSubjects(
    statusFilter ? { status: statusFilter } : undefined
  );
  const { data: alerts, isLoading: alertsLoading } = useMonitoringAlerts();
  const createMutation = useCreateMonitoredSubject();
  const updateMutation = useUpdateMonitoredSubject();
  const deleteMutation = useDeleteMonitoredSubject();
  const acknowledgeMutation = useAcknowledgeAlert();
  const [expandedExplanation, setExpandedExplanation] = React.useState<string | null>(null);
  const [explanations, setExplanations] = React.useState<Record<string, string>>({});
  const [loadingExplanation, setLoadingExplanation] = React.useState<string | null>(null);

  const handleExplainAlert = async (alertId: string) => {
    if (expandedExplanation === alertId) {
      setExpandedExplanation(null);
      return;
    }
    if (explanations[alertId]) {
      setExpandedExplanation(alertId);
      return;
    }
    setLoadingExplanation(alertId);
    setExpandedExplanation(alertId);
    try {
      const res = await fetch(`/api/partner/ai/monitoring/alerts/${alertId}/explain`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({}),
      });
      if (res.ok) {
        const data = await res.json();
        setExplanations((prev) => ({ ...prev, [alertId]: data.explanation }));
      }
    } catch {
      setExplanations((prev) => ({ ...prev, [alertId]: "Unable to generate explanation." }));
    } finally {
      setLoadingExplanation(null);
    }
  };

  const handleAddSubject = () => {
    if (!newSubjectName.trim() || !newSubjectIdentifier.trim()) return;
    createMutation.mutate(
      {
        subjectName: newSubjectName,
        subjectIdentifier: newSubjectIdentifier,
        subjectType: newSubjectType,
        monitoringFrequency: newFrequency,
      },
      {
        onSuccess: () => {
          setShowAddForm(false);
          setNewSubjectName("");
          setNewSubjectIdentifier("");
          setNewSubjectType("INDIVIDUAL");
          setNewFrequency("MONTHLY");
        },
      }
    );
  };

  const unacknowledgedCount = (alerts || []).filter((a) => !a.acknowledged).length;

  return (
    <div className="space-y-4">
      {/* Tabs */}
      <div className="flex items-center gap-1 border-b border-border">
        <button
          onClick={() => setTab("subjects")}
          className={cn(
            "flex items-center gap-2 px-4 py-2.5 text-sm font-medium border-b-2 transition-colors",
            tab === "subjects"
              ? "border-accent text-accent"
              : "border-transparent text-text-muted hover:text-text"
          )}
        >
          <Users className="h-4 w-4" />
          Subjects
        </button>
        <button
          onClick={() => setTab("alerts")}
          className={cn(
            "flex items-center gap-2 px-4 py-2.5 text-sm font-medium border-b-2 transition-colors",
            tab === "alerts"
              ? "border-accent text-accent"
              : "border-transparent text-text-muted hover:text-text"
          )}
        >
          <Bell className="h-4 w-4" />
          Alerts
          {unacknowledgedCount > 0 && (
            <span className="ml-1 px-1.5 py-0.5 text-[10px] rounded-full bg-red-500 text-white">
              {unacknowledgedCount}
            </span>
          )}
        </button>
      </div>

      {/* Subjects Tab */}
      {tab === "subjects" && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              {["", "ACTIVE", "PAUSED"].map((s) => (
                <button
                  key={s}
                  onClick={() => setStatusFilter(s)}
                  className={cn(
                    "px-3 py-1.5 text-xs font-medium rounded border transition-colors",
                    statusFilter === s
                      ? "bg-accent/10 text-accent border-accent/30"
                      : "bg-transparent text-text-muted border-border hover:border-text-muted"
                  )}
                >
                  {s || "All"}
                </button>
              ))}
            </div>
            <Button
              variant="primary"
              size="sm"
              onClick={() => setShowAddForm(true)}
            >
              <Plus className="h-4 w-4 mr-1" /> Add Subject
            </Button>
          </div>

          {/* Add Subject Form */}
          {showAddForm && (
            <div className="console-card">
              <div className="console-card-header">
                <div className="text-sm font-semibold text-text">Add Monitored Subject</div>
                <button onClick={() => setShowAddForm(false)} className="text-text-muted hover:text-text">
                  <X className="h-4 w-4" />
                </button>
              </div>
              <div className="console-card-body space-y-3">
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs text-text-muted mb-1">Subject Name</label>
                    <input
                      value={newSubjectName}
                      onChange={(e) => setNewSubjectName(e.target.value)}
                      className="aws-input w-full"
                      placeholder="e.g. John Doe"
                    />
                  </div>
                  <div>
                    <label className="block text-xs text-text-muted mb-1">Identifier (ID number)</label>
                    <input
                      value={newSubjectIdentifier}
                      onChange={(e) => setNewSubjectIdentifier(e.target.value)}
                      className="aws-input w-full"
                      placeholder="e.g. 9001015800087"
                    />
                  </div>
                  <div>
                    <label className="block text-xs text-text-muted mb-1">Subject Type</label>
                    <select
                      value={newSubjectType}
                      onChange={(e) => setNewSubjectType(e.target.value)}
                      className="aws-select w-full"
                    >
                      <option value="INDIVIDUAL">Individual</option>
                      <option value="COMPANY">Company</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-xs text-text-muted mb-1">Check Frequency</label>
                    <select
                      value={newFrequency}
                      onChange={(e) => setNewFrequency(e.target.value as MonitoringFrequency)}
                      className="aws-select w-full"
                    >
                      {FREQUENCIES.map((f) => (
                        <option key={f.value} value={f.value}>{f.label}</option>
                      ))}
                    </select>
                  </div>
                </div>
                <div className="flex justify-end gap-2">
                  <Button variant="ghost" size="sm" onClick={() => setShowAddForm(false)}>
                    Cancel
                  </Button>
                  <Button
                    variant="primary"
                    size="sm"
                    onClick={handleAddSubject}
                    disabled={!newSubjectName.trim() || !newSubjectIdentifier.trim() || createMutation.isPending}
                  >
                    {createMutation.isPending ? "Adding..." : "Add Subject"}
                  </Button>
                </div>
              </div>
            </div>
          )}

          {/* Subjects Table */}
          {subjectsLoading ? (
            <div className="space-y-2">
              {[...Array(3)].map((_, i) => <Skeleton key={i} className="h-12 w-full" />)}
            </div>
          ) : !subjects || subjects.length === 0 ? (
            <div className="console-card">
              <div className="console-card-body flex flex-col items-center py-12 text-center">
                <Users className="h-10 w-10 text-text-muted mb-3" />
                <p className="text-sm font-medium text-text">No monitored subjects</p>
                <p className="text-xs text-text-muted mt-1">
                  Add subjects to begin continuous monitoring and risk detection.
                </p>
                <Button
                  variant="primary"
                  size="sm"
                  className="mt-4"
                  onClick={() => setShowAddForm(true)}
                >
                  <Plus className="h-4 w-4 mr-1" /> Add First Subject
                </Button>
              </div>
            </div>
          ) : (
            <div className="console-card overflow-hidden">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border bg-background/50">
                    <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">Name</th>
                    <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">Identifier</th>
                    <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">Status</th>
                    <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">Frequency</th>
                    <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">Last Score</th>
                    <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">Next Check</th>
                    <th className="text-right px-4 py-2.5 text-xs font-semibold text-text-muted">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {subjects.map((subject) => (
                    <tr
                      key={subject.subjectId}
                      className="hover:bg-hover cursor-pointer"
                      onClick={() => router.push(`/monitoring/${subject.subjectId}`)}
                    >
                      <td className="px-4 py-2.5 font-medium text-text">{subject.subjectName || "-"}</td>
                      <td className="px-4 py-2.5 font-mono text-xs text-text">{subject.subjectIdentifier || "-"}</td>
                      <td className="px-4 py-2.5">
                        <span className={cn("inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border", STATUS_STYLES[subject.status] || "")}>
                          {subject.status}
                        </span>
                      </td>
                      <td className="px-4 py-2.5 text-text-muted">{subject.monitoringFrequency}</td>
                      <td className="px-4 py-2.5 text-text">
                        {subject.lastRiskScore != null ? subject.lastRiskScore : "-"}
                      </td>
                      <td className="px-4 py-2.5 text-xs text-text-muted">
                        {subject.nextCheckAt ? new Date(subject.nextCheckAt).toLocaleDateString() : "-"}
                      </td>
                      <td className="px-4 py-2.5 text-right">
                        <div className="flex items-center justify-end gap-1" onClick={(e) => e.stopPropagation()}>
                          {subject.status === "ACTIVE" && (
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => updateMutation.mutate({ subjectId: subject.subjectId, updates: { status: "PAUSED" } })}
                            >
                              Pause
                            </Button>
                          )}
                          {subject.status === "PAUSED" && (
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => updateMutation.mutate({ subjectId: subject.subjectId, updates: { status: "ACTIVE" } })}
                            >
                              Resume
                            </Button>
                          )}
                          <Button
                            variant="ghost"
                            size="sm"
                            className="text-danger"
                            onClick={() => deleteMutation.mutate(subject.subjectId)}
                          >
                            Remove
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Alerts Tab */}
      {tab === "alerts" && (
        <div className="space-y-4">
          {alertsLoading ? (
            <div className="space-y-2">
              {[...Array(3)].map((_, i) => <Skeleton key={i} className="h-12 w-full" />)}
            </div>
          ) : !alerts || alerts.length === 0 ? (
            <div className="console-card">
              <div className="console-card-body flex flex-col items-center py-12 text-center">
                <Bell className="h-10 w-10 text-text-muted mb-3" />
                <p className="text-sm font-medium text-text">No alerts</p>
                <p className="text-xs text-text-muted mt-1">
                  Alerts will appear here when a monitored subject&apos;s risk profile changes.
                </p>
              </div>
            </div>
          ) : (
            <div className="space-y-2">
              {alerts.map((alert) => (
                <div
                  key={alert.alertId}
                  className={cn(
                    "console-card",
                    alert.acknowledged && "opacity-60"
                  )}
                >
                  <div className="console-card-body">
                    <div className="flex items-start justify-between gap-4">
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <span className={cn("inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border", SEVERITY_STYLES[alert.severity] || "")}>
                            {alert.severity}
                          </span>
                          <span className="text-sm font-medium text-text">{alert.title}</span>
                        </div>
                        {alert.description && (
                          <p className="text-xs text-text-muted mb-2">{alert.description}</p>
                        )}
                        <div className="flex items-center gap-4 text-xs text-text-muted">
                          <span>Subject: {alert.subjectId.slice(0, 8)}...</span>
                          {alert.previousRiskScore != null && alert.currentRiskScore != null && (
                            <span>
                              Score: {alert.previousRiskScore} → {alert.currentRiskScore}
                            </span>
                          )}
                          {alert.previousDecision && alert.currentDecision && (
                            <span>
                              Decision: {alert.previousDecision} → {alert.currentDecision}
                            </span>
                          )}
                          <span>{new Date(alert.createdAt).toLocaleString()}</span>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleExplainAlert(alert.alertId)}
                          disabled={loadingExplanation === alert.alertId}
                        >
                          <Lightbulb className="h-3.5 w-3.5 mr-1" />
                          {expandedExplanation === alert.alertId ? "Hide" : "Explain"}
                        </Button>
                        {alert.acknowledged ? (
                          <span className="text-xs text-text-muted">
                            Acknowledged by {alert.acknowledgedBy}
                          </span>
                        ) : (
                          <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => acknowledgeMutation.mutate(alert.alertId)}
                            disabled={acknowledgeMutation.isPending}
                          >
                            Acknowledge
                          </Button>
                        )}
                      </div>
                    </div>
                    {expandedExplanation === alert.alertId && (
                      <div className="mt-3 pt-3 border-t border-border">
                        {loadingExplanation === alert.alertId ? (
                          <Skeleton className="h-10 w-full" />
                        ) : (
                          <div className="flex items-start gap-2">
                            <Lightbulb className="h-4 w-4 text-accent flex-shrink-0 mt-0.5" />
                            <p className="text-xs text-text">{explanations[alert.alertId]}</p>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
