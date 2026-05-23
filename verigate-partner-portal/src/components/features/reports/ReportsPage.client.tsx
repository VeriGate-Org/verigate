"use client";

import { Download, FileText } from "lucide-react";
import { PageHeader } from "@/components/ui/PageHeader";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { StatCard } from "@/components/ui/StatCard";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { cn } from "@/lib/cn";

/* ── Mock data ───────────────────────────────────────────── */

const DAYS = [
  "Mon 12", "Tue 13", "Wed 14", "Thu 15", "Fri 16", "Sat 17", "Sun 18",
  "Mon 19", "Tue 20", "Wed 21", "Thu 22", "Fri 23", "Sat 24", "Sun 25",
];
const COMPLETED = [42, 51, 48, 63, 72, 28, 12, 55, 60, 44, 70, 68, 25, 14];
const REVIEW    = [5,  6,  4,  7,  9,  3,  2,  6,  8,  5,  7,  10, 3,  2];
const FAILED    = [3,  4,  2,  5,  6,  2,  1,  3,  5,  3,  4,  6,  1,  1];
const MAX_VALUE = Math.max(...COMPLETED) * 1.15;

interface ProviderRow {
  name: string;
  checks: number;
  avg: string;
  sla: number;
  status: "success" | "warning" | "danger";
}

const PROVIDERS: ProviderRow[] = [
  { name: "DHA",        checks: 142, avg: "34 s",   sla: 99.3, status: "success" },
  { name: "SAPS-AFIS",  checks: 88,  avg: "42 min", sla: 92.1, status: "warning" },
  { name: "TransUnion",  checks: 71,  avg: "6.2 s",  sla: 100,  status: "success" },
  { name: "OFAC + EU",  checks: 65,  avg: "1.1 s",  sla: 100,  status: "success" },
  { name: "Umalusi",    checks: 22,  avg: "3.4 hr", sla: 71.2, status: "danger" },
];

const STATUS_LABELS: Record<string, string> = {
  success: "Healthy",
  warning: "Watch",
  danger: "Degraded",
};

interface ProductMixItem {
  name: string;
  pct: number;
  color: string;
}

const PRODUCT_MIX: ProductMixItem[] = [
  { name: "KYC",       pct: 54, color: "#1A2E4B" },
  { name: "Sanctions", pct: 18, color: "#00B3D9" },
  { name: "Credit",    pct: 12, color: "#2C974B" },
  { name: "Criminal",  pct: 10, color: "#C28B0B" },
  { name: "Other",     pct: 6,  color: "#4F5B67" },
];

/* ── Bar helper for SVG chart ────────────────────────────── */

const BAR_COLORS = {
  completed: "#1A2E4B",
  review: "#C28B0B",
  failed: "#E23D36",
};

function BarGroup({
  day,
  completed,
  review,
  failed,
  maxValue,
  x,
  barWidth,
}: {
  day: string;
  completed: number;
  review: number;
  failed: number;
  maxValue: number;
  x: number;
  barWidth: number;
}) {
  const chartH = 140;
  const gap = 2;
  const singleBar = (barWidth - gap * 2) / 3;

  const barH = (v: number) => (v / maxValue) * chartH;

  return (
    <g>
      <rect
        x={x}
        y={chartH - barH(completed)}
        width={singleBar}
        height={barH(completed)}
        rx={2}
        fill={BAR_COLORS.completed}
      />
      <rect
        x={x + singleBar + gap}
        y={chartH - barH(review)}
        width={singleBar}
        height={barH(review)}
        rx={2}
        fill={BAR_COLORS.review}
      />
      <rect
        x={x + (singleBar + gap) * 2}
        y={chartH - barH(failed)}
        width={singleBar}
        height={barH(failed)}
        rx={2}
        fill={BAR_COLORS.failed}
      />
      <text
        x={x + barWidth / 2}
        y={chartH + 16}
        textAnchor="middle"
        className="fill-text-muted text-[10px]"
      >
        {day.split(" ")[0]}
      </text>
      <text
        x={x + barWidth / 2}
        y={chartH + 28}
        textAnchor="middle"
        className="fill-text-muted text-[9px] font-mono"
      >
        {completed}/{review}/{failed}
      </text>
    </g>
  );
}

/* ── SLA progress bar ────────────────────────────────────── */

function SlaBar({ pct, status }: { pct: number; status: string }) {
  const bg =
    status === "danger"
      ? "#E23D36"
      : status === "warning"
        ? "#C28B0B"
        : "#2C974B";

  return (
    <div className="flex items-center gap-2">
      <div className="w-[60px] h-1.5 bg-surface-alt rounded-full overflow-hidden">
        <div
          className="h-full rounded-full transition-all"
          style={{ width: `${pct}%`, background: bg }}
        />
      </div>
      <span className="font-mono text-xs text-text-muted">{pct}%</span>
    </div>
  );
}

/* ── Component ───────────────────────────────────────────── */

export function ReportsPage() {
  const barWidth = 50;
  const chartPadding = 20;
  const svgWidth = DAYS.length * barWidth + chartPadding * 2;
  const svgHeight = 180;

  return (
    <div className="space-y-aws-m">
      <PageHeader
        category="Reports"
        title="Weekly verification report"
        description="Volume, pass rate, and provider performance for the week of 12\u201325 May 2026."
        actions={
          <>
            <select className="aws-input py-1.5 px-2.5 text-xs rounded">
              <option>This week</option>
              <option>Last week</option>
              <option>This month</option>
              <option>Last quarter</option>
            </select>
            <Button variant="secondary" icon={<Download size={13} />}>
              Export PDF
            </Button>
            <Button variant="cta" icon={<FileText size={13} />}>
              Generate Report
            </Button>
          </>
        }
      />

      {/* KPI strip */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3.5">
        <StatCard
          label="Total this period"
          value="316"
          delta="+18% WoW"
          deltaTone="success"
        />
        <StatCard
          label="Pass rate"
          value="93.4%"
          delta="+0.6pt"
          deltaTone="success"
        />
        <StatCard
          label="Manual reviews"
          value="36"
          delta="+11% WoW"
          deltaTone="danger"
        />
        <StatCard
          label="Avg. cost / check"
          value="R 78"
          delta="-R 4"
          deltaTone="success"
        />
      </div>

      {/* Daily volume chart */}
      <Card className="p-5">
        <div className="flex justify-between items-center mb-4">
          <div>
            <div className="text-sm font-semibold">Daily volume</div>
            <div className="text-[11px] text-text-muted mt-0.5">
              Completed &middot; Manual review &middot; Failed
            </div>
          </div>
          <div className="flex items-center gap-4 text-[11px]">
            <span className="inline-flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-sm bg-[#1A2E4B]" />
              Completed
            </span>
            <span className="inline-flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-sm bg-[#C28B0B]" />
              Review
            </span>
            <span className="inline-flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-sm bg-[#E23D36]" />
              Failed
            </span>
          </div>
        </div>

        <div className="overflow-x-auto">
          <svg
            width={svgWidth}
            height={svgHeight}
            viewBox={`0 0 ${svgWidth} ${svgHeight}`}
            className="mx-auto block"
          >
            {DAYS.map((day, i) => (
              <BarGroup
                key={day}
                day={day}
                completed={COMPLETED[i]}
                review={REVIEW[i]}
                failed={FAILED[i]}
                maxValue={MAX_VALUE}
                x={chartPadding + i * barWidth}
                barWidth={barWidth}
              />
            ))}
          </svg>
        </div>
      </Card>

      {/* Two-up: provider performance + product mix */}
      <div className="grid grid-cols-1 lg:grid-cols-[1.4fr_1fr] gap-4">
        {/* Provider performance table */}
        <Card>
          <CardHeader>
            <div className="text-sm font-semibold">Provider performance</div>
            <div className="text-[11px] text-text-muted mt-0.5">
              SLA = 30 min, except SAPS (1 hr)
            </div>
          </CardHeader>
          <div className="overflow-x-auto">
            <table className="aws-table">
              <thead>
                <tr>
                  {["Provider", "Checks", "Avg. response", "Within SLA", ""].map(
                    (h) => (
                      <th key={h}>{h}</th>
                    ),
                  )}
                </tr>
              </thead>
              <tbody>
                {PROVIDERS.map((p) => (
                  <tr key={p.name}>
                    <td className="font-medium">{p.name}</td>
                    <td className="font-mono">{p.checks}</td>
                    <td className="font-mono text-text-muted">{p.avg}</td>
                    <td>
                      <SlaBar pct={p.sla} status={p.status} />
                    </td>
                    <td>
                      <Badge variant={p.status} size="sm">
                        {STATUS_LABELS[p.status]}
                      </Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>

        {/* Product mix */}
        <Card className="p-4">
          <div className="text-sm font-semibold">Product mix</div>
          <div className="text-[11px] text-text-muted mt-0.5 mb-4">
            Share of weekly volume
          </div>
          {PRODUCT_MIX.map((p) => (
            <div key={p.name} className="mb-2.5">
              <div className="flex justify-between text-xs mb-1">
                <span>{p.name}</span>
                <span className="font-mono text-text-muted">{p.pct}%</span>
              </div>
              <div className="h-1.5 bg-surface-alt rounded-full overflow-hidden">
                <div
                  className="h-full rounded-full transition-all"
                  style={{ width: `${p.pct}%`, background: p.color }}
                />
              </div>
            </div>
          ))}
        </Card>
      </div>
    </div>
  );
}
