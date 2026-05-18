"use client";

import Link from "next/link";
import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  BarChart3,
  CheckCircle2,
  Clock,
  XCircle,
  AlertTriangle,
  GitBranch,
  ClipboardList,
  Eye,
  FileText,
  Settings,
} from "lucide-react";
import type { VerificationStatus } from "@/lib/types";
import type { Case, MonitoringAlert, Policy } from "@/lib/bff-client";
import { useVerificationList } from "@/lib/hooks/useVerification";
import { listCases } from "@/lib/services/case-service";
import { listMonitoringAlerts } from "@/lib/services/monitoring-service";
import { listPolicies } from "@/lib/services/policy-service";

const STATUS: VerificationStatus[] = [
  "success",
  "in_progress",
  "soft_fail",
  "hard_fail",
];

function computeFromIso(range: "24h" | "7d" | "30d" | "ytd"): string {
  const now = Date.now();
  if (range === "24h") return new Date(now - 24 * 60 * 60 * 1000).toISOString();
  if (range === "7d") return new Date(now - 7 * 24 * 60 * 60 * 1000).toISOString();
  if (range === "30d") return new Date(now - 30 * 24 * 60 * 60 * 1000).toISOString();
  const d = new Date();
  return new Date(d.getFullYear(), 0, 1, 0, 0, 0, 0).toISOString();
}

export default function VeriDeck() {
  const [range, setRange] = useState<"24h" | "7d" | "30d" | "ytd">("24h");

  const fromIso = useMemo(() => computeFromIso(range), [range]);

  const prevWindow = useMemo(() => {
    const start = Date.parse(fromIso);
    const end = Date.now();
    const span = Math.max(1, end - start);
    return {
      from: new Date(start - span).toISOString(),
      to: new Date(start - 1).toISOString(),
    };
  }, [fromIso]);

  const recentQuery = useVerificationList({ pageSize: 15, sortBy: "startedAt", sortDir: "desc", from: fromIso });
  const rangeQuery = useVerificationList({ pageSize: 1000, sortBy: "startedAt", sortDir: "asc", from: fromIso });
  const totalQuery = useVerificationList({ pageSize: 1, from: fromIso });
  const prevWindowQuery = useVerificationList({ pageSize: 1, from: prevWindow.from, to: prevWindow.to });

  const successQuery = useVerificationList({ status: "success", pageSize: 1, from: fromIso });
  const inProgressQuery = useVerificationList({ status: "in_progress", pageSize: 1, from: fromIso });
  const softFailQuery = useVerificationList({ status: "soft_fail", pageSize: 1, from: fromIso });
  const hardFailQuery = useVerificationList({ status: "hard_fail", pageSize: 1, from: fromIso });

  const alertsQuery = useQuery({
    queryKey: ["monitoring-alerts", { pageSize: 100 }],
    queryFn: () => listMonitoringAlerts({ pageSize: 100 }),
  });
  const openCasesQuery = useQuery({
    queryKey: ["cases", { status: "OPEN", pageSize: 5 }],
    queryFn: () => listCases({ status: "OPEN", pageSize: 5 }),
  });
  const inReviewCasesQuery = useQuery({
    queryKey: ["cases", { status: "IN_REVIEW", pageSize: 5 }],
    queryFn: () => listCases({ status: "IN_REVIEW", pageSize: 5 }),
  });
  const policiesQuery = useQuery({
    queryKey: ["policies"],
    queryFn: () => listPolicies(),
  });

  const loading = recentQuery.isLoading || rangeQuery.isLoading || totalQuery.isLoading;
  const error = recentQuery.error || rangeQuery.error || totalQuery.error;
  const recent = recentQuery.data?.items ?? [];
  const rangeItems = useMemo(() => rangeQuery.data?.items ?? [], [rangeQuery.data?.items]);
  const prevWindowTotal = prevWindowQuery.data?.total ?? null;

  const totals: Record<string, number> = useMemo(() => ({
    total: totalQuery.data?.total ?? 0,
    success: successQuery.data?.total ?? 0,
    in_progress: inProgressQuery.data?.total ?? 0,
    soft_fail: softFailQuery.data?.total ?? 0,
    hard_fail: hardFailQuery.data?.total ?? 0,
  }), [totalQuery.data, successQuery.data, inProgressQuery.data, softFailQuery.data, hardFailQuery.data]);

  const successRate = useMemo(() => {
    const total = totals.total || 0;
    const success = totals.success || 0;
    return total ? Math.round((success / total) * 100) : 0;
  }, [totals]);

  const volumeDeltaPct = useMemo(() => {
    if (prevWindowTotal == null) return null;
    const current = totals.total || 0;
    if (!prevWindowTotal) return null;
    return Math.round(((current - prevWindowTotal) / prevWindowTotal) * 100);
  }, [totals, prevWindowTotal]);

  const trend = useMemo(() => {
    if (!fromIso || rangeItems.length === 0) return null as null | { labels: string[]; counts: number[]; successRates: number[] };
    const start = Date.parse(fromIso);
    const end = Date.now();
    const bins = 12;
    const step = Math.max(1, Math.floor((end - start) / bins));
    const counts = Array.from({ length: bins }, () => 0);
    const succ = Array.from({ length: bins }, () => 0);
    for (const v of rangeItems) {
      const ts = Date.parse(v.startedAt);
      if (isNaN(ts) || ts < start || ts > end) continue;
      let idx = Math.floor((ts - start) / step);
      if (idx >= bins) idx = bins - 1;
      counts[idx]++;
      if (v.status === "success") succ[idx]++;
    }
    const labels = Array.from({ length: bins }, (_, i) => {
      const t = new Date(start + i * step);
      return range === "24h" ? `${t.getHours()}:00` : `${t.getMonth() + 1}/${t.getDate()}`;
    });
    const successRates = counts.map((c, i) => (c ? Math.round((succ[i] / c) * 100) : 0));
    return { labels, counts, successRates };
  }, [fromIso, rangeItems, range]);

  const providerStats = useMemo(() => {
    const map = new Map<string, { total: number; success: number; durationSum: number; durationCount: number }>();
    for (const v of rangeItems) {
      const key = v.provider || "—";
      const m = map.get(key) || { total: 0, success: 0, durationSum: 0, durationCount: 0 };
      m.total += 1;
      if (v.status === "success") m.success += 1;
      if (typeof v.durationMs === "number") {
        m.durationSum += v.durationMs;
        m.durationCount += 1;
      }
      map.set(key, m);
    }
    const rows = Array.from(map.entries()).map(([provider, m]) => ({
      provider,
      volume: m.total,
      successPct: m.total ? Math.round((m.success / m.total) * 100) : 0,
      avgMs: m.durationCount ? Math.round(m.durationSum / m.durationCount) : undefined,
    }));
    rows.sort((a, b) => b.volume - a.volume);
    return rows;
  }, [rangeItems]);

  const trendHighlights = useMemo(() => {
    if (!trend) return null;
    const { counts, labels, successRates } = trend;
    if (!counts.length) return null;
    const peakIndex = counts.reduce((acc, value, idx, arr) => (value > arr[acc] ? idx : acc), 0);
    const latestIndex = counts.length - 1;
    return {
      peakLabel: labels[peakIndex],
      peakVolume: counts[peakIndex],
      latestVolume: counts[latestIndex],
      latestSuccess: successRates[latestIndex],
    };
  }, [trend]);

  const alertStats = useMemo(() => {
    const alerts: MonitoringAlert[] = alertsQuery.data ?? [];
    const unacknowledged = alerts.filter((a) => !a.acknowledged);
    const highSeverity = unacknowledged.filter((a) => a.severity === "HIGH");
    return { total: unacknowledged.length, high: highSeverity.length };
  }, [alertsQuery.data]);

  const pendingCases = useMemo(() => {
    const open: Case[] = openCasesQuery.data ?? [];
    const inReview: Case[] = inReviewCasesQuery.data ?? [];
    return [...open, ...inReview]
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 5);
  }, [openCasesQuery.data, inReviewCasesQuery.data]);

  const pendingCaseCount = (openCasesQuery.data?.length ?? 0) + (inReviewCasesQuery.data?.length ?? 0);
  const casesError = openCasesQuery.error || inReviewCasesQuery.error;

  const policyStats = useMemo(() => {
    const policies: Policy[] = policiesQuery.data ?? [];
    const published = policies.filter((p) => p.status === "PUBLISHED");
    const draft = policies.filter((p) => p.status === "DRAFT");
    const recentPolicies = [...policies]
      .sort((a, b) => new Date(b.updatedAt ?? "").getTime() - new Date(a.updatedAt ?? "").getTime())
      .slice(0, 3);
    return { total: policies.length, published: published.length, draft: draft.length, recent: recentPolicies };
  }, [policiesQuery.data]);

  const rangeOptions: { label: string; value: typeof range }[] = [
    { label: "Last 24 hours", value: "24h" },
    { label: "Last 7 days", value: "7d" },
    { label: "Last 30 days", value: "30d" },
    { label: "Year to date", value: "ytd" },
  ];

  const statusMeta: Record<VerificationStatus, { label: string }> = {
    success: { label: "Success" },
    in_progress: { label: "In progress" },
    soft_fail: { label: "Soft fail" },
    hard_fail: { label: "Hard fail" },
    pending: { label: "Pending" },
    completed: { label: "Completed" },
    transient_error: { label: "Transient error" },
    permanent_failure: { label: "Permanent failure" },
  };

  const statusBreakdown = STATUS.map((status) => {
    const count = totals[status] || 0;
    const pct = totals.total ? Math.round((count / totals.total) * 100) : 0;
    return { status, count, pct };
  });

  const totalVerifications = totals.total || 0;
  const kpis = [
    {
      label: "Total verifications",
      value: fmtNum(totalVerifications),
      helper: volumeDeltaPct == null ? "Previous window unavailable" : `${volumeDeltaPct >= 0 ? "+" : ""}${volumeDeltaPct}% vs prior window`,
      icon: BarChart3,
      iconColor: "text-blue-600",
      iconBgColor: "bg-blue-50",
      valueColor: "text-blue-700",
    },
    {
      label: "Success rate",
      value: `${successRate}%`,
      helper: `${fmtNum(totals.success || 0)} successful`,
      icon: CheckCircle2,
      iconColor: "text-green-600",
      iconBgColor: "bg-green-50",
      valueColor: "text-green-700",
    },
    {
      label: "In progress",
      value: fmtNum(totals.in_progress || 0),
      helper: `${fmtNum(totalVerifications - (totals.success || 0))} remaining checks`,
      icon: Clock,
      iconColor: "text-orange-600",
      iconBgColor: "bg-orange-50",
      valueColor: "text-orange-700",
    },
    {
      label: "Hard failures",
      value: fmtNum(totals.hard_fail || 0),
      helper: `${fmtNum(totals.soft_fail || 0)} soft fails`,
      icon: XCircle,
      iconColor: "text-red-600",
      iconBgColor: "bg-red-50",
      valueColor: "text-red-700",
    },
    {
      label: "Active alerts",
      value: alertsQuery.error ? "—" : fmtNum(alertStats.total),
      helper: alertsQuery.error
        ? "Unable to load alerts"
        : `${alertStats.high} high severity`,
      icon: AlertTriangle,
      iconColor: "text-amber-600",
      iconBgColor: "bg-amber-50",
      valueColor: "text-amber-700",
    },
  ];

  const shortcuts = [
    { label: "Policies", description: "Manage verification workflows", href: "/policies", icon: GitBranch, color: "text-violet-600", bgColor: "bg-violet-50" },
    { label: "Cases", description: "Review flagged subjects", href: "/cases", icon: ClipboardList, color: "text-blue-600", bgColor: "bg-blue-50" },
    { label: "Monitoring", description: "Continuous screening alerts", href: "/monitoring", icon: Eye, color: "text-emerald-600", bgColor: "bg-emerald-50" },
    { label: "Reports", description: "Analytics and exports", href: "/reports", icon: FileText, color: "text-orange-600", bgColor: "bg-orange-50" },
    { label: "Settings", description: "Partner configuration", href: "/settings", icon: Settings, color: "text-gray-600", bgColor: "bg-gray-100" },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-1">
        <h1 className="text-xl font-semibold text-text">Verification overview</h1>
        <p className="text-sm text-text-muted">Monitor throughput and outcome performance across your checks.</p>
      </div>

      <div className="flex flex-wrap items-center gap-2">
        {rangeOptions.map(({ label, value }) => {
          const active = range === value;
          return (
            <button
              key={value}
              onClick={() => setRange(value)}
              className={`rounded border px-3 py-1.5 text-[13px] font-medium transition-colors ${
                active ? "border-primary bg-primary text-white" : "border-border bg-[color:var(--color-base-100)] text-text hover:border-primary"
              }`}
            >
              {label}
            </button>
          );
        })}
      </div>

      {error && (
        <div className="console-card border-danger/40 bg-danger/5 text-sm text-danger">
          <div className="console-card-body flex items-center justify-between">
            <span>{error instanceof Error ? error.message : "Failed to load dashboard"}</span>
            <button
              onClick={() => {
                recentQuery.refetch();
                rangeQuery.refetch();
                totalQuery.refetch();
              }}
              className="rounded border border-danger/40 px-3 py-1 text-xs font-medium hover:bg-danger/10"
            >
              Retry
            </button>
          </div>
        </div>
      )}

      {/* KPI cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-5 gap-aws-m">
        {kpis.map((kpi) => {
          const Icon = kpi.icon;
          return (
            <div key={kpi.label} className="console-card">
              <div className="console-card-body space-y-aws-s">
                <div className="flex items-start justify-between">
                  <div className="text-xs uppercase tracking-wide text-text-muted font-medium">{kpi.label}</div>
                  <div className={`flex h-8 w-8 items-center justify-center rounded-lg ${kpi.iconBgColor}`}>
                    <Icon className={`h-5 w-5 ${kpi.iconColor}`} />
                  </div>
                </div>
                <div className={`text-aws-heading-l font-semibold ${kpi.valueColor}`}>{kpi.value}</div>
                <div className="text-aws-body text-text-muted">{kpi.helper}</div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Enterprise shortcuts */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-5 gap-aws-m">
        {shortcuts.map((s) => {
          const Icon = s.icon;
          return (
            <Link
              key={s.href}
              href={s.href}
              className="console-card hover:border-accent transition-all duration-aws-quick"
            >
              <div className="console-card-body flex flex-col items-center text-center space-y-2 py-4">
                <div className={`flex h-10 w-10 items-center justify-center rounded-lg ${s.bgColor}`}>
                  <Icon className={`h-5 w-5 ${s.color}`} />
                </div>
                <div className="text-sm font-medium text-text">{s.label}</div>
                <div className="text-xs text-text-muted">{s.description}</div>
              </div>
            </Link>
          );
        })}
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-3 gap-4">
        <div className="console-card xl:col-span-2">
          <div className="console-card-header">
            <h2 className="text-sm font-semibold text-text">Recent verifications</h2>
            <Link href="/verifications" className="text-xs font-medium text-primary hover:underline">
              View all
            </Link>
          </div>
          <div className="console-card-body p-0">
            <div className="overflow-x-auto">
              <table className="min-w-full text-left text-sm">
                <thead className="bg-background text-xs uppercase tracking-wide text-text-muted">
                  <tr>
                    <th className="px-3 py-2">Type</th>
                    <th className="px-3 py-2">Provider</th>
                    <th className="px-3 py-2">Status</th>
                    <th className="px-3 py-2">Started</th>
                  </tr>
                </thead>
                <tbody>
                  {loading && recent.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="px-3 py-6 text-center text-xs text-text-muted">
                        Loading verifications…
                      </td>
                    </tr>
                  ) : recent.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="px-3 py-6 text-center text-xs text-text-muted">
                        No verifications in this range.
                      </td>
                    </tr>
                  ) : (
                    recent.slice(0, 12).map((v) => (
                      <tr key={v.correlationId} className="border-b border-border last:border-0">
                        <td className="px-3 py-2 font-medium text-text">{v.type}</td>
                        <td className="px-3 py-2 text-text-muted">{v.provider || "—"}</td>
                        <td className="px-3 py-2"><StatusBadge status={v.status} /></td>
                        <td className="px-3 py-2 text-text-muted">{formatTimestamp(v.startedAt)}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div className="console-card">
          <div className="console-card-header">
            <h2 className="text-sm font-semibold text-text">Status breakdown</h2>
          </div>
          <div className="console-card-body space-y-3">
            <div className="text-xs text-text-muted">Total volume: {fmtNum(totalVerifications)}</div>
            <ul className="space-y-2">
              {statusBreakdown.map(({ status, count, pct }) => (
                <li key={status} className="space-y-1">
                  <div className="flex items-center justify-between text-sm text-text">
                    <span>{statusMeta[status].label}</span>
                    <span className="text-xs text-text-muted">{fmtNum(count)} · {pct}%</span>
                  </div>
                  <div className="h-2 w-full rounded bg-border">
                    <div
                      className="h-2 rounded"
                      style={{
                        width: `${Math.max(pct, 4)}%`,
                        backgroundColor:
                          status === "success"
                            ? "#2c974b"
                            : status === "in_progress"
                            ? "#0972d3"
                            : status === "soft_fail"
                            ? "#c28b0b"
                            : "#d13212",
                      }}
                    />
                  </div>
                </li>
              ))}
            </ul>
            {trendHighlights && (
              <div className="rounded border border-border bg-background px-3 py-2 text-xs text-text-muted">
                Peak volume at <span className="font-medium text-text">{trendHighlights.peakLabel}</span> with {fmtNum(trendHighlights.peakVolume)} checks; latest slot delivered {fmtNum(trendHighlights.latestVolume)} checks at {trendHighlights.latestSuccess}% success.
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Bottom grid: Provider Performance, Pending Cases, Policy Overview */}
      <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
        <div className="console-card">
          <div className="console-card-header">
            <h2 className="text-sm font-semibold text-text">Provider performance</h2>
            <span className="text-xs text-text-muted">Top integrations in window</span>
          </div>
          <div className="console-card-body p-0">
            <div className="overflow-x-auto">
              <table className="min-w-full text-left text-sm">
                <thead className="bg-background text-xs uppercase tracking-wide text-text-muted">
                  <tr>
                    <th className="px-3 py-2">Provider</th>
                    <th className="px-3 py-2">Volume</th>
                    <th className="px-3 py-2">Success</th>
                    <th className="px-3 py-2">Avg duration</th>
                  </tr>
                </thead>
                <tbody>
                  {providerStats.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="px-3 py-6 text-center text-xs text-text-muted">
                        No provider activity in this range.
                      </td>
                    </tr>
                  ) : (
                    providerStats.slice(0, 8).map((row) => (
                      <tr key={row.provider} className="border-b border-border last:border-0">
                        <td className="px-3 py-2 font-medium text-text">{row.provider}</td>
                        <td className="px-3 py-2 text-text-muted">{fmtNum(row.volume)}</td>
                        <td className="px-3 py-2 text-text-muted">{row.successPct}%</td>
                        <td className="px-3 py-2 text-text-muted">{formatDuration(row.avgMs)}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Pending Cases */}
        <div className="console-card">
          <div className="console-card-header">
            <div className="flex items-center gap-2">
              <h2 className="text-sm font-semibold text-text">Pending cases</h2>
              {pendingCaseCount > 0 && (
                <span className="inline-flex items-center rounded-full bg-blue-500/10 px-2 py-0.5 text-xs font-medium text-blue-600">
                  {pendingCaseCount}
                </span>
              )}
            </div>
            <Link href="/cases" className="text-xs font-medium text-primary hover:underline">
              View all &rarr;
            </Link>
          </div>
          <div className="console-card-body space-y-2">
            {casesError ? (
              <div className="flex flex-col items-center gap-2 py-4 text-center">
                <span className="text-xs text-text-muted">Unable to load cases</span>
                <button
                  onClick={() => { openCasesQuery.refetch(); inReviewCasesQuery.refetch(); }}
                  className="rounded border border-border px-3 py-1 text-xs font-medium text-text hover:bg-background"
                >
                  Retry
                </button>
              </div>
            ) : openCasesQuery.isLoading || inReviewCasesQuery.isLoading ? (
              <div className="py-4 text-center text-xs text-text-muted">Loading cases…</div>
            ) : pendingCases.length === 0 ? (
              <div className="py-4 text-center text-xs text-text-muted">No pending cases</div>
            ) : (
              pendingCases.map((c) => (
                <Link
                  key={c.caseId}
                  href={`/cases/${c.caseId}`}
                  className="flex items-center justify-between gap-3 rounded border border-border px-3 py-2 text-sm hover:border-accent hover:bg-accent-soft/50 transition-all duration-aws-quick"
                >
                  <div className="min-w-0 flex-1">
                    <div className="truncate font-medium text-text">{c.subjectName || c.subjectId || "Unknown"}</div>
                    <div className="text-xs text-text-muted">{formatTimeAgo(c.createdAt)}</div>
                  </div>
                  <div className="flex items-center gap-2 shrink-0">
                    <span className={`inline-flex rounded-full px-2 py-0.5 text-xs font-medium ${priorityBadgeStyle(c.priority)}`}>
                      {c.priority}
                    </span>
                    <span className="text-xs text-text-muted">{c.compositeRiskScore}%</span>
                  </div>
                </Link>
              ))
            )}
          </div>
        </div>

        {/* Policy Overview */}
        <div className="console-card">
          <div className="console-card-header">
            <h2 className="text-sm font-semibold text-text">Policies</h2>
            <Link href="/policies" className="text-xs font-medium text-primary hover:underline">
              Manage &rarr;
            </Link>
          </div>
          <div className="console-card-body space-y-3">
            {policiesQuery.error ? (
              <div className="flex flex-col items-center gap-2 py-4 text-center">
                <span className="text-xs text-text-muted">Unable to load policies</span>
                <button
                  onClick={() => policiesQuery.refetch()}
                  className="rounded border border-border px-3 py-1 text-xs font-medium text-text hover:bg-background"
                >
                  Retry
                </button>
              </div>
            ) : policiesQuery.isLoading ? (
              <div className="py-4 text-center text-xs text-text-muted">Loading policies…</div>
            ) : policyStats.total === 0 ? (
              <div className="flex flex-col items-center gap-2 py-4 text-center">
                <span className="text-xs text-text-muted">No policies configured</span>
                <Link href="/policies/new" className="text-xs font-medium text-primary hover:underline">
                  Create your first policy &rarr;
                </Link>
              </div>
            ) : (
              <>
                <div className="flex items-center gap-3 text-xs">
                  <span className="inline-flex items-center gap-1">
                    <span className="inline-block h-2 w-2 rounded-full bg-green-500" />
                    <span className="text-text-muted">{policyStats.published} published</span>
                  </span>
                  <span className="inline-flex items-center gap-1">
                    <span className="inline-block h-2 w-2 rounded-full bg-yellow-500" />
                    <span className="text-text-muted">{policyStats.draft} draft</span>
                  </span>
                </div>
                <ul className="space-y-2">
                  {policyStats.recent.map((p) => (
                    <li key={p.policyId}>
                      <Link
                        href={`/policies/${p.policyId}`}
                        className="flex items-center justify-between gap-2 rounded border border-border px-3 py-2 text-sm hover:border-accent hover:bg-accent-soft/50 transition-all duration-aws-quick"
                      >
                        <span className="truncate font-medium text-text">{p.name}</span>
                        <div className="flex items-center gap-2 shrink-0">
                          <span className={`inline-flex rounded-full border px-2 py-0.5 text-xs font-medium ${policyStatusStyle(p.status)}`}>
                            {p.status}
                          </span>
                          <span className="text-xs text-text-muted">{formatShortDate(p.updatedAt)}</span>
                        </div>
                      </Link>
                    </li>
                  ))}
                </ul>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

function StatusBadge({ status }: { status: VerificationStatus }) {
  const toneMap: Record<string, string> = {
    success: "bg-success/10 text-success",
    completed: "bg-success/10 text-success",
    in_progress: "bg-primary/10 text-primary",
    pending: "bg-primary/10 text-primary",
    soft_fail: "bg-amber-100 text-amber-700",
    transient_error: "bg-amber-100 text-amber-700",
    hard_fail: "bg-danger/10 text-danger",
    permanent_failure: "bg-danger/10 text-danger",
  };

  const labelMap: Record<string, string> = {
    success: "Success",
    completed: "Completed",
    in_progress: "In progress",
    pending: "Pending",
    soft_fail: "Soft fail",
    transient_error: "Transient error",
    hard_fail: "Hard fail",
    permanent_failure: "Permanent failure",
  };

  const tone = toneMap[status] || "bg-border text-text-muted";
  const label = labelMap[status] || status;

  return <span className={`inline-flex min-w-[88px] justify-center rounded-full px-2 py-1 text-xs font-medium ${tone}`}>{label}</span>;
}

function fmtNum(n?: number) {
  if (n == null) return "0";
  if (n >= 1000) return `${(n / 1000).toFixed(1)}k`;
  return n.toString();
}

function formatTimestamp(iso: string) {
  try {
    const date = new Date(iso);
    return `${date.toLocaleDateString()} ${date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}`;
  } catch {
    return iso;
  }
}

function formatDuration(ms?: number) {
  if (!ms && ms !== 0) return "—";
  if (ms < 1000) return `${ms} ms`;
  const seconds = ms / 1000;
  if (seconds < 60) return `${seconds.toFixed(1)} s`;
  const minutes = Math.floor(seconds / 60);
  const remain = Math.round(seconds % 60);
  return `${minutes}m ${remain}s`;
}

function formatTimeAgo(iso: string): string {
  try {
    const diff = Date.now() - new Date(iso).getTime();
    const mins = Math.floor(diff / 60000);
    if (mins < 1) return "just now";
    if (mins < 60) return `${mins}m ago`;
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) return `${hrs}h ago`;
    const days = Math.floor(hrs / 24);
    return `${days}d ago`;
  } catch {
    return iso;
  }
}

function formatShortDate(iso?: string): string {
  if (!iso) return "—";
  try {
    const d = new Date(iso);
    return `${d.getDate()} ${d.toLocaleString("default", { month: "short" })}`;
  } catch {
    return iso;
  }
}

function priorityBadgeStyle(priority?: string): string {
  switch (priority) {
    case "CRITICAL": return "bg-red-500/10 text-red-600";
    case "HIGH": return "bg-orange-500/10 text-orange-600";
    case "MEDIUM": return "bg-yellow-500/10 text-yellow-600";
    case "LOW": return "bg-gray-500/10 text-gray-500";
    default: return "bg-gray-500/10 text-gray-500";
  }
}

function policyStatusStyle(status?: string): string {
  switch (status) {
    case "PUBLISHED": return "bg-green-500/10 text-green-600 border-green-500/20";
    case "DRAFT": return "bg-yellow-500/10 text-yellow-600 border-yellow-500/20";
    case "ARCHIVED": return "bg-gray-500/10 text-gray-500 border-gray-500/20";
    default: return "bg-gray-500/10 text-gray-500 border-gray-500/20";
  }
}
