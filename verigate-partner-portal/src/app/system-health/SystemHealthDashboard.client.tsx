"use client";

import * as React from "react";
import { useSystemHealth } from "@/lib/hooks/useSystemHealth";
import { useAuth } from "@/lib/auth/AuthProvider";
import { useQueryClient } from "@tanstack/react-query";
import type {
  ExternalServiceHealth,
  DynamoDbTableHealth,
  SqsQueueHealth,
  HealthStatus,
} from "@/lib/bff-client";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { cn } from "@/lib/cn";
import {
  Activity,
  CheckCircle,
  AlertTriangle,
  XCircle,
  RefreshCw,
  Database,
  Server,
  Globe,
  Radio,
  Cpu,
  HelpCircle,
  BarChart3,
  Zap,
} from "lucide-react";
import HealthHistoryPanel from "./HealthHistoryPanel.client";

const STATUS_CONFIG: Record<string, { icon: typeof CheckCircle; color: string; bg: string }> = {
  HEALTHY: { icon: CheckCircle, color: "text-green-600", bg: "bg-green-500/10" },
  DEGRADED: { icon: AlertTriangle, color: "text-yellow-600", bg: "bg-yellow-500/10" },
  DOWN: { icon: XCircle, color: "text-red-600", bg: "bg-red-500/10" },
  ERROR: { icon: XCircle, color: "text-red-600", bg: "bg-red-500/10" },
  UNCONFIGURED: { icon: HelpCircle, color: "text-gray-400", bg: "bg-gray-500/10" },
  ACTIVE: { icon: CheckCircle, color: "text-green-600", bg: "bg-green-500/10" },
};

function StatusBadge({ status }: { status: string }) {
  const config = STATUS_CONFIG[status] || STATUS_CONFIG.DOWN;
  const Icon = config.icon;
  return (
    <span className={cn("inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium", config.bg, config.color)}>
      <Icon className="h-3 w-3" />
      {status}
    </span>
  );
}

function ProbeIndicator({ success, label, latencyMs, detail }: {
  success: boolean;
  label: string;
  latencyMs: number;
  detail: string;
}) {
  return (
    <div className="flex items-center gap-2" title={detail}>
      <span className={cn("h-2 w-2 rounded-full", success ? "bg-green-500" : "bg-red-500")} />
      <span className="text-xs text-text-muted">{label}</span>
      {latencyMs > 0 && (
        <span className="text-xs text-text-muted">{latencyMs}ms</span>
      )}
    </div>
  );
}

type Tab = "live" | "history";

export default function SystemHealthDashboard() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [autoRefresh, setAutoRefresh] = React.useState(false);
  const [activeTab, setActiveTab] = React.useState<Tab>("live");
  const { data, isLoading, isFetching, refetch } = useSystemHealth();

  React.useEffect(() => {
    if (!autoRefresh) return;
    const interval = setInterval(() => {
      refetch();
    }, 60_000);
    return () => clearInterval(interval);
  }, [autoRefresh, refetch]);

  if (user?.role !== "admin") {
    return (
      <div className="console-card">
        <div className="console-card-body flex flex-col items-center py-12 text-center">
          <XCircle className="h-10 w-10 text-red-500 mb-3" />
          <p className="text-sm font-medium text-text">Access Denied</p>
          <p className="text-xs text-text-muted mt-1">
            System Health is only available to administrators.
          </p>
        </div>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="space-y-4">
        <div className="grid grid-cols-4 gap-4">
          {[...Array(4)].map((_, i) => <Skeleton key={i} className="h-24 w-full" />)}
        </div>
        <Skeleton className="h-64 w-full" />
      </div>
    );
  }

  if (!data) {
    return (
      <div className="console-card">
        <div className="console-card-body flex flex-col items-center py-12 text-center">
          <Activity className="h-10 w-10 text-text-muted mb-3" />
          <p className="text-sm font-medium text-text">Unable to load health data</p>
          <Button variant="secondary" size="sm" className="mt-4" onClick={() => refetch()}>
            Retry
          </Button>
        </div>
      </div>
    );
  }

  const { summary, externalIntegrations, infrastructure, checkedAt } = data;

  return (
    <div className="space-y-6">
      {/* Tab Navigation */}
      <div className="flex items-center gap-1 border-b border-border">
        <button
          onClick={() => setActiveTab("live")}
          className={cn(
            "flex items-center gap-1.5 px-4 py-2 text-sm font-medium border-b-2 -mb-px transition-colors",
            activeTab === "live"
              ? "border-accent text-accent"
              : "border-transparent text-text-muted hover:text-text hover:border-border",
          )}
        >
          <Zap className="h-3.5 w-3.5" />
          Live Status
        </button>
        <button
          onClick={() => setActiveTab("history")}
          className={cn(
            "flex items-center gap-1.5 px-4 py-2 text-sm font-medium border-b-2 -mb-px transition-colors",
            activeTab === "history"
              ? "border-accent text-accent"
              : "border-transparent text-text-muted hover:text-text hover:border-border",
          )}
        >
          <BarChart3 className="h-3.5 w-3.5" />
          History & Trends
        </button>
      </div>

      {activeTab === "history" ? (
        <HealthHistoryPanel />
      ) : (
      <>
      {/* Controls */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <StatusBadge status={data.overallStatus} />
          <span className="text-xs text-text-muted">
            Last checked: {new Date(checkedAt).toLocaleString()}
          </span>
        </div>
        <div className="flex items-center gap-2">
          <label className="flex items-center gap-2 text-xs text-text-muted cursor-pointer">
            <input
              type="checkbox"
              checked={autoRefresh}
              onChange={(e) => setAutoRefresh(e.target.checked)}
              className="rounded border-border"
            />
            Auto-refresh (60s)
          </label>
          <Button
            variant="secondary"
            size="sm"
            onClick={() => {
              queryClient.invalidateQueries({ queryKey: ["system-health"] });
              refetch();
            }}
            disabled={isFetching}
          >
            <RefreshCw className={cn("h-3.5 w-3.5 mr-1", isFetching && "animate-spin")} />
            {isFetching ? "Checking..." : "Refresh Now"}
          </Button>
        </div>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <SummaryCard label="Total Checks" value={summary.total} icon={Activity} color="text-text" />
        <SummaryCard label="Healthy" value={summary.healthy} icon={CheckCircle} color="text-green-600" />
        <SummaryCard label="Degraded" value={summary.degraded} icon={AlertTriangle} color="text-yellow-600" />
        <SummaryCard label="Down" value={summary.down} icon={XCircle} color="text-red-600" />
      </div>

      {/* External Integrations */}
      <div className="console-card">
        <div className="console-card-header">
          <div className="flex items-center gap-2">
            <Globe className="h-4 w-4" />
            <span className="text-sm font-semibold text-text">External Integrations</span>
            <span className="text-xs text-text-muted">({externalIntegrations.length})</span>
          </div>
        </div>
        <div className="console-card-body p-0">
          {externalIntegrations.length === 0 ? (
            <div className="py-8 text-center text-sm text-text-muted">
              No external services configured.
            </div>
          ) : (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border bg-background/50">
                  <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">Service</th>
                  <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">Protocol</th>
                  <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">DNS</th>
                  <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">TCP</th>
                  <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">HTTP</th>
                  <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">Latency</th>
                  <th className="text-left px-4 py-2.5 text-xs font-semibold text-text-muted">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {externalIntegrations.map((service) => (
                  <ExternalServiceRow key={service.id} service={service} />
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* AWS Infrastructure */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {/* DynamoDB Tables */}
        <div className="console-card">
          <div className="console-card-header">
            <div className="flex items-center gap-2">
              <Database className="h-4 w-4" />
              <span className="text-sm font-semibold text-text">DynamoDB Tables</span>
            </div>
          </div>
          <div className="console-card-body p-0">
            {!infrastructure.dynamoDbTables || infrastructure.dynamoDbTables.length === 0 ? (
              <div className="py-6 text-center text-sm text-text-muted">No tables probed.</div>
            ) : (
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border bg-background/50">
                    <th className="text-left px-4 py-2 text-xs font-semibold text-text-muted">Table</th>
                    <th className="text-left px-4 py-2 text-xs font-semibold text-text-muted">Status</th>
                    <th className="text-right px-4 py-2 text-xs font-semibold text-text-muted">Items</th>
                    <th className="text-right px-4 py-2 text-xs font-semibold text-text-muted">Latency</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {infrastructure.dynamoDbTables.map((table) => (
                    <DynamoTableRow key={table.tableName} table={table} />
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>

        {/* SQS Queues */}
        <div className="console-card">
          <div className="console-card-header">
            <div className="flex items-center gap-2">
              <Server className="h-4 w-4" />
              <span className="text-sm font-semibold text-text">SQS Queues</span>
            </div>
          </div>
          <div className="console-card-body p-0">
            {!infrastructure.sqsQueues || infrastructure.sqsQueues.length === 0 ? (
              <div className="py-6 text-center text-sm text-text-muted">No queues probed.</div>
            ) : (
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border bg-background/50">
                    <th className="text-left px-4 py-2 text-xs font-semibold text-text-muted">Queue</th>
                    <th className="text-left px-4 py-2 text-xs font-semibold text-text-muted">Status</th>
                    <th className="text-right px-4 py-2 text-xs font-semibold text-text-muted">Messages</th>
                    <th className="text-right px-4 py-2 text-xs font-semibold text-text-muted">DLQ</th>
                    <th className="text-right px-4 py-2 text-xs font-semibold text-text-muted">Latency</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {infrastructure.sqsQueues.map((queue) => (
                    <SqsQueueRow key={queue.queueName} queue={queue} />
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>

      {/* Kinesis & Bedrock */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {infrastructure.kinesisStream && infrastructure.kinesisStream.status !== "UNCONFIGURED" && (
          <div className="console-card">
            <div className="console-card-header">
              <div className="flex items-center gap-2">
                <Radio className="h-4 w-4" />
                <span className="text-sm font-semibold text-text">Kinesis Stream</span>
              </div>
            </div>
            <div className="console-card-body">
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="text-text-muted">Stream</span>
                  <p className="font-mono text-xs text-text">{infrastructure.kinesisStream.streamName}</p>
                </div>
                <div>
                  <span className="text-text-muted">Status</span>
                  <div className="mt-1"><StatusBadge status={infrastructure.kinesisStream.status} /></div>
                </div>
                <div>
                  <span className="text-text-muted">Shards</span>
                  <p className="text-text">{infrastructure.kinesisStream.shardCount ?? "-"}</p>
                </div>
                <div>
                  <span className="text-text-muted">Latency</span>
                  <p className="text-text">{infrastructure.kinesisStream.latencyMs}ms</p>
                </div>
              </div>
              {infrastructure.kinesisStream.error && (
                <p className="mt-2 text-xs text-red-600">{infrastructure.kinesisStream.error}</p>
              )}
            </div>
          </div>
        )}

        {infrastructure.bedrock && (
          <div className="console-card">
            <div className="console-card-header">
              <div className="flex items-center gap-2">
                <Cpu className="h-4 w-4" />
                <span className="text-sm font-semibold text-text">Amazon Bedrock</span>
              </div>
            </div>
            <div className="console-card-body">
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="text-text-muted">Region</span>
                  <p className="text-text">{infrastructure.bedrock.region}</p>
                </div>
                <div>
                  <span className="text-text-muted">Status</span>
                  <div className="mt-1"><StatusBadge status={infrastructure.bedrock.status} /></div>
                </div>
                <div>
                  <span className="text-text-muted">Latency</span>
                  <p className="text-text">{infrastructure.bedrock.latencyMs}ms</p>
                </div>
              </div>
              {infrastructure.bedrock.error && (
                <p className="mt-2 text-xs text-red-600">{infrastructure.bedrock.error}</p>
              )}
            </div>
          </div>
        )}
      </div>
      </>
      )}
    </div>
  );
}

function SummaryCard({ label, value, icon: Icon, color }: {
  label: string;
  value: number;
  icon: typeof Activity;
  color: string;
}) {
  return (
    <div className="console-card">
      <div className="console-card-body">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-xs text-text-muted">{label}</p>
            <p className={cn("text-2xl font-bold mt-1", color)}>{value}</p>
          </div>
          <Icon className={cn("h-8 w-8 opacity-20", color)} />
        </div>
      </div>
    </div>
  );
}

function ExternalServiceRow({ service }: { service: ExternalServiceHealth }) {
  return (
    <tr className="hover:bg-hover">
      <td className="px-4 py-2.5">
        <div>
          <span className="font-medium text-text">{service.name}</span>
          {service.url && (
            <p className="text-xs text-text-muted font-mono truncate max-w-[200px]" title={service.url}>
              {service.url}
            </p>
          )}
        </div>
      </td>
      <td className="px-4 py-2.5 text-text-muted">{service.protocol}</td>
      <td className="px-4 py-2.5">
        <ProbeIndicator
          success={service.dns.success}
          label="DNS"
          latencyMs={service.dns.latencyMs}
          detail={service.dns.detail}
        />
      </td>
      <td className="px-4 py-2.5">
        <ProbeIndicator
          success={service.tcp.success}
          label="TCP"
          latencyMs={service.tcp.latencyMs}
          detail={service.tcp.detail}
        />
      </td>
      <td className="px-4 py-2.5">
        <ProbeIndicator
          success={service.http.success}
          label="HTTP"
          latencyMs={service.http.latencyMs}
          detail={service.http.detail}
        />
      </td>
      <td className="px-4 py-2.5 text-xs text-text-muted">{service.totalLatencyMs}ms</td>
      <td className="px-4 py-2.5">
        <StatusBadge status={service.overallStatus} />
      </td>
    </tr>
  );
}

function DynamoTableRow({ table }: { table: DynamoDbTableHealth }) {
  return (
    <tr className="hover:bg-hover">
      <td className="px-4 py-2 font-mono text-xs text-text">{table.tableName}</td>
      <td className="px-4 py-2">
        <StatusBadge status={table.status} />
      </td>
      <td className="px-4 py-2 text-right text-text-muted">
        {table.itemCount != null ? table.itemCount.toLocaleString() : "-"}
      </td>
      <td className="px-4 py-2 text-right text-text-muted">{table.latencyMs}ms</td>
    </tr>
  );
}

function SqsQueueRow({ queue }: { queue: SqsQueueHealth }) {
  return (
    <tr className="hover:bg-hover">
      <td className="px-4 py-2 font-mono text-xs text-text">{queue.queueName}</td>
      <td className="px-4 py-2">
        <StatusBadge status={queue.status} />
      </td>
      <td className="px-4 py-2 text-right text-text-muted">
        {queue.approximateMessageCount != null ? queue.approximateMessageCount.toLocaleString() : "-"}
      </td>
      <td className="px-4 py-2 text-right">
        {queue.dlqMessageCount != null ? (
          <span className={cn("text-text-muted", queue.dlqMessageCount > 0 && "text-red-600 font-medium")}>
            {queue.dlqMessageCount.toLocaleString()}
          </span>
        ) : "-"}
      </td>
      <td className="px-4 py-2 text-right text-text-muted">{queue.latencyMs}ms</td>
    </tr>
  );
}
