"use client";

import { useState, useEffect } from "react";
import {
  listMonitoredSubjects,
  listMonitoringAlerts,
  acknowledgeAlert,
  type MonitoredSubject,
  type MonitoringAlert,
} from "@/lib/bff-client";
import { config } from "@/lib/config";
import { Eye, Bell, RefreshCw, Plus, Loader2, Check, AlertTriangle } from "lucide-react";

export default function SanctionsMonitoring() {
  const [subjects, setSubjects] = useState<MonitoredSubject[]>([]);
  const [alerts, setAlerts] = useState<MonitoringAlert[]>([]);
  const [loading, setLoading] = useState(true);
  const [frequency, setFrequency] = useState<string>("DAILY");

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    setLoading(true);
    try {
      if (config.useMockServices) {
        // Mock data
        setSubjects([
          { subjectId: "sub-001", subjectName: "John Smith", subjectType: "Person", monitoringFrequency: "DAILY", status: "ACTIVE", lastCheckedAt: "2025-03-19T10:00:00Z", nextCheckAt: "2025-03-20T10:00:00Z", createdAt: "2025-01-01T00:00:00Z", updatedAt: "2025-03-19T10:00:00Z" },
          { subjectId: "sub-002", subjectName: "Acme Trading Ltd", subjectType: "Company", monitoringFrequency: "WEEKLY", status: "ACTIVE", lastCheckedAt: "2025-03-14T08:00:00Z", nextCheckAt: "2025-03-21T08:00:00Z", createdAt: "2025-02-01T00:00:00Z", updatedAt: "2025-03-14T08:00:00Z" },
          { subjectId: "sub-003", subjectName: "Maria Gonzalez", subjectType: "Person", monitoringFrequency: "MONTHLY", status: "PAUSED", lastCheckedAt: "2025-02-20T06:00:00Z", nextCheckAt: "2025-03-20T06:00:00Z", createdAt: "2025-01-15T00:00:00Z", updatedAt: "2025-02-20T06:00:00Z" },
        ]);
        setAlerts([
          { alertId: "alert-001", subjectId: "sub-001", severity: "HIGH", alertType: "NEW_MATCH", title: "New sanctions list match detected", description: "Subject matched against EU consolidated sanctions list update", previousRiskScore: 20, currentRiskScore: 85, acknowledged: false, createdAt: "2025-03-19T10:15:00Z" },
          { alertId: "alert-002", subjectId: "sub-002", severity: "MEDIUM", alertType: "SCORE_CHANGE", title: "Risk score increased", description: "Match score increased from 0.65 to 0.78", previousRiskScore: 45, currentRiskScore: 65, acknowledged: false, createdAt: "2025-03-18T14:30:00Z" },
          { alertId: "alert-003", subjectId: "sub-001", severity: "LOW", alertType: "DATA_UPDATE", title: "Entity data updated", description: "Source dataset refreshed with new information", acknowledged: true, acknowledgedBy: "admin@partner.com", acknowledgedAt: "2025-03-17T09:00:00Z", createdAt: "2025-03-17T08:00:00Z" },
        ]);
      } else {
        const [s, a] = await Promise.all([
          listMonitoredSubjects(),
          listMonitoringAlerts(),
        ]);
        setSubjects(s);
        setAlerts(a);
      }
    } catch (error) {
      console.error("Failed to load monitoring data", error);
    } finally {
      setLoading(false);
    }
  }

  async function handleAcknowledge(alertId: string) {
    try {
      if (!config.useMockServices) {
        await acknowledgeAlert(alertId);
      }
      setAlerts((prev) =>
        prev.map((a) =>
          a.alertId === alertId
            ? { ...a, acknowledged: true, acknowledgedAt: new Date().toISOString() }
            : a
        )
      );
    } catch (error) {
      console.error("Failed to acknowledge alert", error);
    }
  }

  const activeSubjects = subjects.filter((s) => s.status === "ACTIVE").length;
  const newAlerts = alerts.filter((a) => !a.acknowledged).length;

  const severityColor = (severity: string) => {
    switch (severity) {
      case "HIGH": return "bg-red-100 text-red-800 border-red-200";
      case "MEDIUM": return "bg-amber-100 text-amber-800 border-amber-200";
      case "LOW": return "bg-blue-100 text-blue-800 border-blue-200";
      default: return "bg-gray-100 text-gray-800 border-gray-200";
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 className="w-6 h-6 animate-spin text-gray-400" />
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Dashboard stats */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <div className="bg-white border border-gray-200 rounded-lg p-4">
          <div className="flex items-center gap-2 text-gray-600 mb-1">
            <Eye className="w-4 h-4" />
            <span className="text-sm">Monitored</span>
          </div>
          <p className="text-2xl font-semibold">{activeSubjects}</p>
        </div>
        <div className="bg-white border border-gray-200 rounded-lg p-4">
          <div className="flex items-center gap-2 text-gray-600 mb-1">
            <Bell className="w-4 h-4" />
            <span className="text-sm">New Alerts</span>
          </div>
          <p className="text-2xl font-semibold text-red-600">{newAlerts}</p>
        </div>
        <div className="bg-white border border-gray-200 rounded-lg p-4">
          <div className="flex items-center gap-2 text-gray-600 mb-1">
            <AlertTriangle className="w-4 h-4" />
            <span className="text-sm">Pending Review</span>
          </div>
          <p className="text-2xl font-semibold text-amber-600">{newAlerts}</p>
        </div>
        <div className="bg-white border border-gray-200 rounded-lg p-4">
          <div className="flex items-center gap-2 text-gray-600 mb-1">
            <RefreshCw className="w-4 h-4" />
            <span className="text-sm">Last Update</span>
          </div>
          <p className="text-sm font-medium">{new Date().toLocaleDateString()}</p>
        </div>
      </div>

      {/* Alerts */}
      <div className="bg-white border border-gray-200 rounded-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold">Alerts</h3>
          <button onClick={loadData} className="flex items-center gap-1.5 text-sm text-gray-600 hover:text-gray-800">
            <RefreshCw className="w-4 h-4" /> Refresh
          </button>
        </div>
        <div className="space-y-3">
          {alerts.map((alert) => (
            <div key={alert.alertId} className={`border rounded-lg p-4 ${alert.acknowledged ? "opacity-60" : ""}`}>
              <div className="flex items-start justify-between">
                <div className="flex items-start gap-3">
                  <span className={`px-2 py-0.5 text-xs rounded-full border ${severityColor(alert.severity)}`}>
                    {alert.severity}
                  </span>
                  <div>
                    <p className="font-medium text-sm">{alert.title}</p>
                    {alert.description && <p className="text-xs text-gray-500 mt-1">{alert.description}</p>}
                    <p className="text-xs text-gray-400 mt-1">{new Date(alert.createdAt).toLocaleString()}</p>
                  </div>
                </div>
                {!alert.acknowledged ? (
                  <button
                    onClick={() => handleAcknowledge(alert.alertId)}
                    className="flex items-center gap-1 text-xs px-3 py-1.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                  >
                    <Check className="w-3 h-3" /> Acknowledge
                  </button>
                ) : (
                  <span className="text-xs text-gray-400">Acknowledged</span>
                )}
              </div>
            </div>
          ))}
          {alerts.length === 0 && (
            <p className="text-center text-gray-500 py-4">No alerts</p>
          )}
        </div>
      </div>

      {/* Monitored subjects */}
      <div className="bg-white border border-gray-200 rounded-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold">Monitored Subjects</h3>
          <button className="flex items-center gap-1.5 text-sm px-3 py-1.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
            <Plus className="w-4 h-4" /> Add Subject
          </button>
        </div>

        {/* Schedule config */}
        <div className="flex items-center gap-3 mb-4 p-3 bg-gray-50 rounded-lg">
          <label className="text-sm text-gray-600">Default frequency:</label>
          <select
            value={frequency}
            onChange={(e) => setFrequency(e.target.value)}
            className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm"
          >
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="MONTHLY">Monthly</option>
            <option value="QUARTERLY">Quarterly</option>
          </select>
          <button className="flex items-center gap-1 text-sm text-blue-600 hover:underline ml-auto">
            <RefreshCw className="w-3 h-3" /> Run Immediate Rescan
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Name</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Type</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Frequency</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Status</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Last Checked</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Next Check</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {subjects.map((subject) => (
                <tr key={subject.subjectId} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium">{subject.subjectName || subject.subjectId}</td>
                  <td className="px-4 py-3 text-gray-600">{subject.subjectType || "Person"}</td>
                  <td className="px-4 py-3 text-gray-600">{subject.monitoringFrequency}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-0.5 text-xs rounded-full ${
                      subject.status === "ACTIVE" ? "bg-green-100 text-green-800" : "bg-gray-100 text-gray-800"
                    }`}>
                      {subject.status}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-gray-600 text-xs">
                    {subject.lastCheckedAt ? new Date(subject.lastCheckedAt).toLocaleString() : "-"}
                  </td>
                  <td className="px-4 py-3 text-gray-600 text-xs">
                    {subject.nextCheckAt ? new Date(subject.nextCheckAt).toLocaleString() : "-"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
