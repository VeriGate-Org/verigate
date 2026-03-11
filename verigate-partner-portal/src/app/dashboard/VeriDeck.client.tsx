"use client";

import Link from "next/link";
import { useMemo, useState } from "react";
import { BarChart3, CheckCircle2, Clock, XCircle } from "lucide-react";
import type { VerificationStatus } from "@/lib/types";
import { useVerificationList } from "@/lib/hooks/useVerification";

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
  ];

  const featureLinks = [
    {
      name: "Home Affairs ID verification",
      description: "Validate IDs directly with DHA",
      href: "/services/personal-details",
    },
    {
      name: "Identity verification",
      description: "Biometric identity matching",
      href: "/services/identity",
    },
    {
      name: "Bank account validation",
      description: "Account verification via AVS",
      href: "/services/bank-account",
    },
    {
      name: "Credit check",
      description: "Credit bureau risk assessment",
      href: "/services/credit-check",
    },
    {
      name: "Company & director search",
      description: "CIPC entity and directorship data",
      href: "/services/company",
    },
    {
      name: "Employment verification",
      description: "Verify employment status and history",
      href: "/services/employment",
    },
    {
      name: "Sanctions & PEP screening",
      description: "World-Check style name screening",
      href: "/services/sanctions",
    },
    {
      name: "Fraud watchlist",
      description: "SAFPS fraud prevention database",
      href: "/services/fraud-watchlist",
    },
    {
      name: "Full verification",
      description: "Comprehensive multi-check workflow",
      href: "/services/full-verification",
    },
  ] as const;

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

      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-aws-m">
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

      <div className="console-card">
        <div className="console-card-header">
          <div className="text-sm font-semibold text-text">Quick actions</div>
          <span className="text-xs text-text-muted">Launch a verification</span>
        </div>
          <div className="console-card-body grid gap-aws-m md:grid-cols-2 xl:grid-cols-3">
            {featureLinks.map((feature) => (
              <Link
                key={feature.href}
                href={feature.href}
                className="flex flex-col rounded-aws-control border border-border px-aws-m py-aws-s text-aws-body text-text hover:border-accent hover:bg-accent-soft/50 transition-all duration-aws-quick"
              >
                <span className="font-medium">{feature.name}</span>
                <span className="text-aws-body text-text-muted mt-aws-2xs">{feature.description}</span>
              </Link>
            ))}
          </div>
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

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
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

        <div className="console-card">
          <div className="console-card-header">
            <h2 className="text-sm font-semibold text-text">Live activity</h2>
            <div className="flex items-center gap-2 text-xs text-text-muted">
              <span className="inline-flex h-2 w-2 animate-pulse rounded-full bg-success" />
              Live
            </div>
          </div>
          <div className="console-card-body space-y-3">
            {recent.slice(0, 6).map((v) => (
              <div key={`${v.correlationId}-activity`} className="flex items-center justify-between gap-3 text-sm">
                <div className="min-w-0">
                  <div className="truncate font-medium text-text">{v.type}</div>
                  <div className="truncate text-xs text-text-muted">{formatTimestamp(v.startedAt)}</div>
                </div>
                <StatusBadge status={v.status} />
              </div>
            ))}
            {recent.length === 0 && !loading && (
              <div className="text-xs text-text-muted">No recent activity.</div>
            )}
            {loading && (
              <div className="text-xs text-text-muted">Refreshing activity…</div>
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
