"use client";

import { useMemo, useState } from "react";
import {
  FileCheck,
  Target,
  Clock,
  AlertTriangle,
  BarChart3,
} from "lucide-react";

type Range = "today" | "7d" | "30d";

/* ── mock data generators (replace with BFF calls when endpoints are live) ── */

function useMockSummary(range: Range) {
  return useMemo(() => {
    const base = range === "today" ? 1 : range === "7d" ? 7 : 30;
    return {
      documentsProcessed: Math.floor(42 * base + Math.random() * 20 * base),
      classificationAccuracy: Math.floor(94 + Math.random() * 5),
      avgProcessingTimeMs: Math.floor(1200 + Math.random() * 800),
      fraudFlagged: Math.floor(2 * base + Math.random() * 3 * base),
    };
  }, [range]);
}

function useMockVolumeByType(range: Range) {
  return useMemo(() => {
    const base = range === "today" ? 1 : range === "7d" ? 7 : 30;
    return [
      { type: "SA ID Card", count: Math.floor(18 * base) },
      { type: "Passport", count: Math.floor(8 * base) },
      { type: "Tax Certificate", count: Math.floor(6 * base) },
      { type: "B-BBEE Certificate", count: Math.floor(5 * base) },
      { type: "CIPC Registration", count: Math.floor(3 * base) },
      { type: "Financial Statement", count: Math.floor(2 * base) },
      { type: "Utility Bill", count: Math.floor(1 * base) },
    ];
  }, [range]);
}

function useMockThroughput() {
  return useMemo(() => {
    return Array.from({ length: 24 }, (_, i) => ({
      hour: `${i.toString().padStart(2, "0")}:00`,
      count: Math.floor(Math.random() * 12 + (i >= 8 && i <= 17 ? 8 : 1)),
    }));
  }, []);
}

function useMockConfidenceDistribution(range: Range) {
  return useMemo(() => {
    const base = range === "today" ? 1 : range === "7d" ? 7 : 30;
    return {
      high: Math.floor(28 * base),
      medium: Math.floor(9 * base),
      low: Math.floor(3 * base),
      manualReview: Math.floor(2 * base),
    };
  }, [range]);
}

/* ── chart helpers ── */

function HorizontalBar({ label, value, max }: { label: string; value: number; max: number }) {
  const pct = max > 0 ? (value / max) * 100 : 0;
  return (
    <div className="flex items-center gap-3">
      <span className="w-36 text-xs text-text-muted truncate text-right">{label}</span>
      <div className="flex-1 h-5 rounded bg-border overflow-hidden">
        <div
          className="h-full rounded bg-primary transition-all duration-300"
          style={{ width: `${pct}%` }}
        />
      </div>
      <span className="w-10 text-xs font-medium text-text tabular-nums text-right">{value}</span>
    </div>
  );
}

function ThroughputChart({ data }: { data: { hour: string; count: number }[] }) {
  const max = Math.max(...data.map((d) => d.count), 1);
  const barWidth = 100 / data.length;

  return (
    <div className="relative h-32">
      <svg viewBox="0 0 100 40" preserveAspectRatio="none" className="w-full h-full">
        {data.map((d, i) => {
          const height = (d.count / max) * 36;
          return (
            <rect
              key={i}
              x={i * barWidth + barWidth * 0.15}
              y={40 - height}
              width={barWidth * 0.7}
              height={height}
              rx={0.3}
              fill="var(--color-primary)"
              opacity={0.8}
            >
              <title>{`${d.hour}: ${d.count} documents`}</title>
            </rect>
          );
        })}
      </svg>
      <div className="flex justify-between px-1 text-[9px] text-text-muted mt-1">
        <span>00:00</span>
        <span>06:00</span>
        <span>12:00</span>
        <span>18:00</span>
        <span>23:00</span>
      </div>
    </div>
  );
}

function DonutChart({ data }: {
  data: { label: string; value: number; color: string }[];
}) {
  const total = data.reduce((s, d) => s + d.value, 0);
  if (total === 0) {
    return (
      <div className="flex items-center justify-center h-32 text-xs text-text-muted">
        No data
      </div>
    );
  }

  const size = 100;
  const center = size / 2;
  const radius = 38;
  const strokeWidth = 14;

  let cumulativePercent = 0;
  const segments = data.map((d) => {
    const percent = d.value / total;
    const startAngle = cumulativePercent * 2 * Math.PI - Math.PI / 2;
    cumulativePercent += percent;
    const endAngle = cumulativePercent * 2 * Math.PI - Math.PI / 2;

    const x1 = center + radius * Math.cos(startAngle);
    const y1 = center + radius * Math.sin(startAngle);
    const x2 = center + radius * Math.cos(endAngle);
    const y2 = center + radius * Math.sin(endAngle);
    const largeArc = percent > 0.5 ? 1 : 0;

    return {
      path: `M ${x1} ${y1} A ${radius} ${radius} 0 ${largeArc} 1 ${x2} ${y2}`,
      color: d.color,
      label: d.label,
      value: d.value,
      percent: Math.round(percent * 100),
    };
  });

  return (
    <div className="flex items-center gap-4">
      <svg viewBox={`0 0 ${size} ${size}`} className="w-28 h-28 shrink-0">
        {segments.map((seg, i) => (
          <path
            key={i}
            d={seg.path}
            fill="none"
            stroke={seg.color}
            strokeWidth={strokeWidth}
            strokeLinecap="round"
          >
            <title>{`${seg.label}: ${seg.value} (${seg.percent}%)`}</title>
          </path>
        ))}
        <text
          x={center}
          y={center}
          textAnchor="middle"
          dominantBaseline="central"
          fill="currentColor"
          fontSize={14}
          fontWeight={600}
        >
          {total}
        </text>
      </svg>
      <div className="space-y-1.5">
        {segments.map((seg, i) => (
          <div key={i} className="flex items-center gap-2 text-xs">
            <span
              className="inline-block h-2.5 w-2.5 rounded-full shrink-0"
              style={{ backgroundColor: seg.color }}
            />
            <span className="text-text-muted">{seg.label}</span>
            <span className="font-medium text-text ml-auto tabular-nums">{seg.percent}%</span>
          </div>
        ))}
      </div>
    </div>
  );
}

/* ── main component ── */

export default function DocumentAnalytics() {
  const [range, setRange] = useState<Range>("today");

  const summary = useMockSummary(range);
  const volumeByType = useMockVolumeByType(range);
  const throughput = useMockThroughput();
  const confidenceDist = useMockConfidenceDistribution(range);

  const rangeOptions: { label: string; value: Range }[] = [
    { label: "Today", value: "today" },
    { label: "7 Days", value: "7d" },
    { label: "30 Days", value: "30d" },
  ];

  const maxVolume = Math.max(...volumeByType.map((v) => v.count), 1);

  const kpis = [
    {
      label: "Documents Processed",
      value: summary.documentsProcessed.toLocaleString(),
      icon: FileCheck,
      iconColor: "text-blue-600",
      iconBg: "bg-blue-50",
      valueColor: "text-blue-700",
    },
    {
      label: "Classification Accuracy",
      value: `${summary.classificationAccuracy}%`,
      icon: Target,
      iconColor: "text-green-600",
      iconBg: "bg-green-50",
      valueColor: "text-green-700",
    },
    {
      label: "Avg Processing Time",
      value: `${(summary.avgProcessingTimeMs / 1000).toFixed(1)}s`,
      icon: Clock,
      iconColor: "text-orange-600",
      iconBg: "bg-orange-50",
      valueColor: "text-orange-700",
    },
    {
      label: "Fraud Flagged",
      value: summary.fraudFlagged.toString(),
      icon: AlertTriangle,
      iconColor: "text-red-600",
      iconBg: "bg-red-50",
      valueColor: "text-red-700",
    },
  ];

  const donutData = [
    { label: "High (>95%)", value: confidenceDist.high, color: "#2c974b" },
    { label: "Medium (80-95%)", value: confidenceDist.medium, color: "#c28b0b" },
    { label: "Low (<80%)", value: confidenceDist.low, color: "#d13212" },
    { label: "Manual Review", value: confidenceDist.manualReview, color: "#6b7280" },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-1">
        <h1 className="text-xl font-semibold text-text">Document AI Analytics</h1>
        <p className="text-sm text-text-muted">
          Monitor document processing performance, classification accuracy, and fraud detection.
        </p>
      </div>

      {/* Time range selector */}
      <div className="flex flex-wrap items-center gap-2">
        {rangeOptions.map(({ label, value }) => (
          <button
            key={value}
            onClick={() => setRange(value)}
            className={`rounded border px-3 py-1.5 text-[13px] font-medium transition-colors ${
              range === value
                ? "border-primary bg-primary text-white"
                : "border-border bg-[color:var(--color-base-100)] text-text hover:border-primary"
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {/* KPI cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        {kpis.map((kpi) => {
          const Icon = kpi.icon;
          return (
            <div key={kpi.label} className="console-card">
              <div className="console-card-body space-y-2">
                <div className="flex items-start justify-between">
                  <div className="text-xs uppercase tracking-wide text-text-muted font-medium">
                    {kpi.label}
                  </div>
                  <div className={`flex h-8 w-8 items-center justify-center rounded-lg ${kpi.iconBg}`}>
                    <Icon className={`h-5 w-5 ${kpi.iconColor}`} />
                  </div>
                </div>
                <div className={`text-2xl font-semibold ${kpi.valueColor}`}>{kpi.value}</div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Charts grid */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
        {/* Volume by Document Type */}
        <div className="console-card">
          <div className="console-card-header">
            <div className="flex items-center gap-2">
              <BarChart3 className="h-4 w-4 text-text-muted" />
              <h2 className="text-sm font-semibold text-text">Processing Volume by Type</h2>
            </div>
          </div>
          <div className="console-card-body space-y-2">
            {volumeByType.map((item) => (
              <HorizontalBar
                key={item.type}
                label={item.type}
                value={item.count}
                max={maxVolume}
              />
            ))}
          </div>
        </div>

        {/* Confidence Distribution */}
        <div className="console-card">
          <div className="console-card-header">
            <h2 className="text-sm font-semibold text-text">Confidence Distribution</h2>
          </div>
          <div className="console-card-body flex items-center justify-center">
            <DonutChart data={donutData} />
          </div>
        </div>
      </div>

      {/* Throughput chart */}
      <div className="console-card">
        <div className="console-card-header">
          <h2 className="text-sm font-semibold text-text">Hourly Processing Throughput (24h)</h2>
        </div>
        <div className="console-card-body">
          <ThroughputChart data={throughput} />
        </div>
      </div>
    </div>
  );
}
