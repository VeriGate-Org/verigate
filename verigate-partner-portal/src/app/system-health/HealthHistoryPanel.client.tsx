"use client";

import * as React from "react";
import { useServiceHistory, useServiceUptime, useIncidents } from "@/lib/hooks/useHealthHistory";
import { useSystemHealth } from "@/lib/hooks/useSystemHealth";
import { TrendChart, MetricCard } from "@/components/ui/Charts/Charts";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { cn } from "@/lib/cn";
import {
  Activity,
  CheckCircle,
  XCircle,
  AlertTriangle,
  Clock,
} from "lucide-react";
import type { IncidentResponse } from "@/lib/bff-client";

const RANGES = [
  { label: "24h", value: "24h" },
  { label: "7d", value: "7d" },
  { label: "30d", value: "30d" },
] as const;

export default function HealthHistoryPanel() {
  const [range, setRange] = React.useState<string>("24h");
  const [selectedService, setSelectedService] = React.useState<string | null>(null);

  const { data: healthData } = useSystemHealth();
  const { data: uptimeData, isLoading: uptimeLoading } = useServiceUptime(range);
  const { data: historyData, isLoading: historyLoading } = useServiceHistory(selectedService, range);
  const { data: incidentsData, isLoading: incidentsLoading } = useIncidents(range);

  // Build service list from live health data
  const serviceOptions = React.useMemo(() => {
    if (!healthData) return [];
    const options: { id: string; name: string }[] = [];
    for (const ext of healthData.externalIntegrations) {
      options.push({ id: `ext:${ext.id}`, name: ext.name });
    }
    if (healthData.infrastructure.dynamoDbTables) {
      for (const t of healthData.infrastructure.dynamoDbTables) {
        options.push({ id: `infra:dynamodb:${t.tableName}`, name: `DynamoDB: ${t.tableName}` });
      }
    }
    if (healthData.infrastructure.sqsQueues) {
      for (const q of healthData.infrastructure.sqsQueues) {
        options.push({ id: `infra:sqs:${q.queueName}`, name: `SQS: ${q.queueName}` });
      }
    }
    return options;
  }, [healthData]);

  // Auto-select first service
  React.useEffect(() => {
    if (!selectedService && serviceOptions.length > 0) {
      setSelectedService(serviceOptions[0].id);
    }
  }, [serviceOptions, selectedService]);

  // Find selected service uptime from uptime data
  const selectedUptime = React.useMemo(() => {
    if (!uptimeData || !selectedService) return null;
    return uptimeData.find((u) => u.serviceId === selectedService) ?? null;
  }, [uptimeData, selectedService]);

  // Build trend chart data
  const trendData = React.useMemo(() => {
    if (!historyData?.dataPoints) return [];
    return historyData.dataPoints.map((dp) => ({
      label: new Date(dp.checkedAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
      value: dp.latencyMs,
    }));
  }, [historyData]);

  return (
    <div className="space-y-6">
      {/* Controls */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4">
        <div className="flex items-center gap-2">
          <label className="text-xs font-medium text-text-muted">Service:</label>
          <select
            value={selectedService ?? ""}
            onChange={(e) => setSelectedService(e.target.value)}
            className="text-sm border border-border rounded px-2 py-1 bg-background text-text"
          >
            {serviceOptions.map((s) => (
              <option key={s.id} value={s.id}>
                {s.name}
              </option>
            ))}
          </select>
        </div>
        <div className="flex items-center gap-1">
          <label className="text-xs font-medium text-text-muted mr-1">Range:</label>
          {RANGES.map((r) => (
            <button
              key={r.value}
              onClick={() => setRange(r.value)}
              className={cn(
                "px-3 py-1 text-xs rounded font-medium transition-colors",
                range === r.value
                  ? "bg-accent text-white"
                  : "bg-background border border-border text-text-muted hover:bg-hover",
              )}
            >
              {r.label}
            </button>
          ))}
        </div>
      </div>

      {/* Uptime Summary Cards */}
      {uptimeLoading ? (
        <div className="grid grid-cols-3 gap-4">
          {[...Array(3)].map((_, i) => (
            <Skeleton key={i} className="h-24 w-full" />
          ))}
        </div>
      ) : selectedUptime ? (
        <div className="grid grid-cols-3 gap-4">
          <MetricCard
            title="Uptime"
            value={`${selectedUptime.uptimePercentage}%`}
            icon={CheckCircle}
          />
          <MetricCard
            title="Total Checks"
            value={selectedUptime.totalChecks}
            icon={Activity}
          />
          <MetricCard
            title="Healthy Checks"
            value={selectedUptime.healthyChecks}
            icon={CheckCircle}
          />
        </div>
      ) : (
        <div className="grid grid-cols-3 gap-4">
          <MetricCard title="Uptime" value="--" icon={CheckCircle} />
          <MetricCard title="Total Checks" value={0} icon={Activity} />
          <MetricCard title="Healthy Checks" value={0} icon={CheckCircle} />
        </div>
      )}

      {/* Latency Trend Chart */}
      <div>
        <h3 className="text-sm font-semibold text-text mb-2">Latency Trend (ms)</h3>
        {historyLoading ? (
          <Skeleton className="h-[200px] w-full" />
        ) : trendData.length > 1 ? (
          <TrendChart data={trendData} height={200} />
        ) : (
          <div className="console-card">
            <div className="console-card-body flex flex-col items-center py-12 text-center">
              <Activity className="h-8 w-8 text-text-muted mb-2" />
              <p className="text-sm text-text-muted">
                No latency data available for this time range. Snapshots are captured every 15 minutes.
              </p>
            </div>
          </div>
        )}
      </div>

      {/* Incident Timeline */}
      <div>
        <h3 className="text-sm font-semibold text-text mb-2">Recent Incidents</h3>
        {incidentsLoading ? (
          <Skeleton className="h-32 w-full" />
        ) : incidentsData && incidentsData.length > 0 ? (
          <div className="console-card">
            <div className="console-card-body p-0 divide-y divide-border">
              {incidentsData.map((incident, idx) => (
                <IncidentRow key={idx} incident={incident} />
              ))}
            </div>
          </div>
        ) : (
          <div className="console-card">
            <div className="console-card-body flex flex-col items-center py-8 text-center">
              <CheckCircle className="h-8 w-8 text-green-500 mb-2" />
              <p className="text-sm text-text-muted">
                No incidents in this time range.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

function IncidentRow({ incident }: { incident: IncidentResponse }) {
  const statusConfig =
    incident.status === "DOWN"
      ? { icon: XCircle, color: "text-red-600", bg: "bg-red-500/10", dot: "bg-red-500" }
      : { icon: AlertTriangle, color: "text-yellow-600", bg: "bg-yellow-500/10", dot: "bg-yellow-500" };
  const Icon = statusConfig.icon;

  return (
    <div className="px-4 py-3 flex items-start gap-3">
      <div className={cn("mt-0.5 rounded-full p-1", statusConfig.bg)}>
        <Icon className={cn("h-4 w-4", statusConfig.color)} />
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2">
          <span className="text-sm font-medium text-text">{incident.serviceName}</span>
          <span
            className={cn(
              "inline-flex items-center px-1.5 py-0.5 rounded text-xs font-medium",
              statusConfig.bg,
              statusConfig.color,
            )}
          >
            {incident.status}
          </span>
        </div>
        <div className="flex items-center gap-2 mt-1 text-xs text-text-muted">
          <Clock className="h-3 w-3" />
          <span>
            {new Date(incident.startedAt).toLocaleString()} →{" "}
            {new Date(incident.endedAt).toLocaleString()}
          </span>
        </div>
        <p className="text-xs text-text-muted mt-0.5">
          Duration: {incident.durationMinutes} minutes
        </p>
      </div>
    </div>
  );
}
