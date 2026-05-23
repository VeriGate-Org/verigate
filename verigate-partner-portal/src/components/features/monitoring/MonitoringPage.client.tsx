"use client";

import { useState, useMemo } from "react";
import { Plus, ChevronRight, ArrowLeft, RotateCcw, StopCircle } from "lucide-react";
import { type ColumnDef } from "@tanstack/react-table";
import { PageHeader } from "@/components/ui/PageHeader";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { StatCard } from "@/components/ui/StatCard";
import { DataTable } from "@/components/ui/DataTable";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { FilterChips } from "@/components/ui/FilterChips";
import { cn } from "@/lib/cn";

/* ── Mock data ───────────────────────────────────────────── */

interface MonitoredSubject {
  id: string;
  subject: string;
  type: "Person" | "Company";
  policy: string;
  freq: string;
  nextRun: string;
  alerts7d: number;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  onboard: string;
  country: string;
}

const MONITORED: MonitoredSubject[] = [
  { id: "M-2026-0042", subject: "Jane Smith",          type: "Person",  policy: "pol_aml_enhanced", freq: "Daily",   nextRun: "6h",  alerts7d: 1,  status: "success", statusLabel: "Clear",     onboard: "2026-04-12", country: "ZA" },
  { id: "M-2026-0041", subject: "Acme Corp Ltd",       type: "Company", policy: "pol_corp_kyb",     freq: "Weekly",  nextRun: "2d",  alerts7d: 3,  status: "warning", statusLabel: "Match",     onboard: "2026-03-08", country: "ZA" },
  { id: "M-2026-0040", subject: "Mandla Tshabalala",   type: "Person",  policy: "pol_aml_enhanced", freq: "Daily",   nextRun: "14h", alerts7d: 8,  status: "danger",  statusLabel: "Escalated", onboard: "2025-11-22", country: "ZA" },
  { id: "M-2026-0039", subject: "Naledi Nkosi",        type: "Person",  policy: "pol_onboard_v3",   freq: "Monthly", nextRun: "12d", alerts7d: 0,  status: "success", statusLabel: "Clear",     onboard: "2025-08-04", country: "ZA" },
  { id: "M-2026-0038", subject: "Pyongyang Trading",   type: "Company", policy: "pol_aml_enhanced", freq: "Daily",   nextRun: "3h",  alerts7d: 12, status: "danger",  statusLabel: "Escalated", onboard: "2026-01-15", country: "ZA" },
  { id: "M-2026-0037", subject: "Sipho Dlamini",       type: "Person",  policy: "pol_onboard_v3",   freq: "Weekly",  nextRun: "4d",  alerts7d: 0,  status: "success", statusLabel: "Clear",     onboard: "2025-09-19", country: "ZA" },
];

interface AlertEntry {
  date: string;
  kind: string;
  detail: string;
  tone: "success" | "warning" | "danger";
}

const ALERT_HISTORY: AlertEntry[] = [
  { date: "2026-05-18", kind: "Sanctions list update", detail: "Subject added to EU consolidated list -- partial match score 0.78.", tone: "warning" },
  { date: "2026-05-16", kind: "PEP designation",       detail: "Cousin (2nd degree) confirmed as municipal council member.",        tone: "warning" },
  { date: "2026-05-12", kind: "Adverse media",         detail: "News article mentioning subject in fraud investigation.",            tone: "danger" },
  { date: "2026-05-05", kind: "Routine refresh",       detail: "All checks clear. No changes since last refresh.",                  tone: "success" },
  { date: "2026-04-28", kind: "Routine refresh",       detail: "All checks clear. No changes since last refresh.",                  tone: "success" },
];

/* ── Subject Detail View ─────────────────────────────────── */

const toneColor: Record<string, string> = {
  success: "bg-[#2C974B]",
  warning: "bg-[#C28B0B]",
  danger:  "bg-[#E23D36]",
};

function SubjectDetail({
  subject,
  onBack,
}: {
  subject: MonitoredSubject;
  onBack: () => void;
}) {
  return (
    <div className="space-y-aws-m">
      {/* Header card */}
      <Card className="overflow-hidden">
        <div className="flex h-[3px]">
          <div className="flex-[3] bg-[#E23D36]" />
          <div className="flex-[5] bg-[#1A2E4B]" />
          <div className="flex-[2] bg-accent" />
        </div>
        <div className="p-5 flex justify-between items-start">
          <div className="flex items-center gap-3.5">
            <div className="w-12 h-12 rounded-full bg-[#1A2E4B] text-white flex items-center justify-center text-sm font-semibold">
              {subject.subject.split(" ").map((p) => p[0]).slice(0, 2).join("")}
            </div>
            <div>
              <div className="flex items-center gap-2.5 mb-1">
                <span className="font-mono text-xs text-accent">{subject.id}</span>
                <Badge variant={subject.status}>{subject.statusLabel}</Badge>
              </div>
              <div className="text-lg font-semibold text-text">{subject.subject}</div>
              <div className="text-xs text-text-muted mt-1">
                Under {subject.freq.toLowerCase()} monitoring &middot; policy{" "}
                <span className="font-mono text-accent font-semibold">{subject.policy}</span>{" "}
                &middot; onboarded {subject.onboard}
              </div>
            </div>
          </div>
          <div className="flex gap-2">
            <Button variant="secondary" icon={<RotateCcw size={13} />}>Re-run now</Button>
            <Button variant="ghost" className="text-[#E23D36]" icon={<StopCircle size={13} />}>
              Stop monitoring
            </Button>
          </div>
        </div>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-[1.6fr_1fr] gap-4">
        {/* Alert history */}
        <Card>
          <CardHeader>
            <div className="text-sm font-semibold">Alert history</div>
            <div className="text-[11px] text-text-muted mt-0.5">Last 30 days</div>
          </CardHeader>
          <CardBody compact>
            {ALERT_HISTORY.map((a, i) => (
              <div
                key={i}
                className={cn(
                  "flex gap-3.5 px-4 py-3",
                  i > 0 && "border-t border-border-light",
                )}
              >
                <div
                  className={cn(
                    "w-3 h-3 rounded-full border-2 shrink-0 mt-1",
                    a.tone === "success" && "border-[#2C974B]",
                    a.tone === "warning" && "border-[#C28B0B]",
                    a.tone === "danger"  && "border-[#E23D36]",
                  )}
                />
                <div className="flex-1 min-w-0">
                  <div className="flex justify-between items-center mb-0.5">
                    <span className="text-[13px] font-semibold">{a.kind}</span>
                    <span className="text-[11px] font-mono text-text-muted">{a.date}</span>
                  </div>
                  <div className="text-xs text-text-muted leading-relaxed">{a.detail}</div>
                </div>
              </div>
            ))}
          </CardBody>
        </Card>

        {/* Settings sidebar */}
        <Card className="p-4 self-start">
          <div className="text-[11px] font-semibold uppercase tracking-wide text-text-muted mb-3">
            Monitoring settings
          </div>
          {[
            ["Frequency", subject.freq],
            ["Next refresh", subject.nextRun + " from now"],
            ["Datasets", "Sanctions \u00b7 PEP \u00b7 Adverse media"],
            ["Notify", "arthur@verigate.co.za \u00b7 #compliance"],
            ["Auto-create case", "On any non-routine alert"],
          ].map(([k, v]) => (
            <div
              key={k}
              className="flex justify-between py-1.5 border-b border-border-light text-xs"
            >
              <span className="text-text-muted">{k}</span>
              <span className="text-text font-medium text-right max-w-[200px]">{v}</span>
            </div>
          ))}
        </Card>
      </div>

      <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={13} />}>
        Back to monitoring
      </Button>
    </div>
  );
}

/* ── Main Monitoring Page ────────────────────────────────── */

const columns: ColumnDef<MonitoredSubject, unknown>[] = [
  {
    accessorKey: "subject",
    header: "Subject",
    cell: ({ row }) => {
      const m = row.original;
      return (
        <div className="flex items-center gap-2.5">
          <div className="w-[26px] h-[26px] rounded-full bg-[#1A2E4B] text-white flex items-center justify-center text-[10px] font-semibold shrink-0">
            {m.subject.split(" ").map((p) => p[0]).slice(0, 2).join("")}
          </div>
          <div>
            <div className="text-[13px] font-medium">{m.subject}</div>
            <div className="text-[10px] font-mono text-text-muted">
              {m.id} &middot; onboard {m.onboard}
            </div>
          </div>
        </div>
      );
    },
  },
  {
    accessorKey: "type",
    header: "Type",
    cell: ({ getValue }) => (
      <span className="text-[13px] text-text-muted">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "policy",
    header: "Policy",
    cell: ({ getValue }) => (
      <span className="font-mono text-[11px] text-accent">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "freq",
    header: "Frequency",
    cell: ({ getValue }) => (
      <span className="text-[13px]">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "nextRun",
    header: "Next",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-text-muted">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "alerts7d",
    header: "Alerts (7d)",
    cell: ({ getValue }) => {
      const n = getValue<number>();
      return (
        <span
          className={cn(
            "font-mono text-xs font-semibold",
            n === 0 ? "text-[#2C974B]" : n > 5 ? "text-[#E23D36]" : "text-[#C28B0B]",
          )}
        >
          {n}
        </span>
      );
    },
  },
  {
    accessorKey: "statusLabel",
    header: "Status",
    cell: ({ row }) => {
      const m = row.original;
      return <Badge variant={m.status}>{m.statusLabel}</Badge>;
    },
  },
  {
    id: "actions",
    header: "",
    cell: () => <ChevronRight size={14} className="text-text-muted" />,
    enableSorting: false,
  },
];

const FILTER_CHIPS = [
  { label: "All", value: "all", count: MONITORED.length },
  { label: "Clear", value: "success", count: MONITORED.filter((m) => m.status === "success").length },
  { label: "Match", value: "warning", count: MONITORED.filter((m) => m.status === "warning").length },
  { label: "Escalated", value: "danger", count: MONITORED.filter((m) => m.status === "danger").length },
];

export function MonitoringPage() {
  const [selectedSubject, setSelectedSubject] = useState<MonitoredSubject | null>(null);
  const [filter, setFilter] = useState("all");

  const filteredData = useMemo(
    () => filter === "all" ? MONITORED : MONITORED.filter((m) => m.status === filter),
    [filter],
  );

  const totalAlerts = MONITORED.reduce((s, m) => s + m.alerts7d, 0);
  const escalated = MONITORED.filter((m) => m.status === "danger").length;

  if (selectedSubject) {
    return (
      <SubjectDetail
        subject={selectedSubject}
        onBack={() => setSelectedSubject(null)}
      />
    );
  }

  return (
    <div className="space-y-aws-m">
      <PageHeader
        category="Enterprise \u00b7 Monitoring"
        title="Ongoing monitoring"
        description="Subjects under continuous re-screening. Alerts fire when sanctions lists, PEP registers, or adverse media change."
        actions={
          <Button variant="cta" icon={<Plus size={13} />}>
            Monitor subject
          </Button>
        }
      />

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3.5">
        <StatCard label="Subjects monitored" value={MONITORED.length} />
        <StatCard label="Alerts (7 days)" value={totalAlerts} deltaTone="warning" />
        <StatCard label="Escalated" value={escalated} deltaTone="danger" />
        <StatCard label="Last refresh" value="14m ago" />
      </div>

      <FilterChips chips={FILTER_CHIPS} value={filter} onChange={setFilter} />

      <Card>
        <CardHeader>
          <div className="text-sm font-semibold">Monitored subjects</div>
          <div className="text-[11px] text-text-muted mt-0.5">
            Click any row to drill into the subject&apos;s alert history.
          </div>
        </CardHeader>
        <DataTable
          data={filteredData}
          columns={columns}
          onRowClick={(row) => setSelectedSubject(row)}
          pageSize={10}
          emptyMessage="No monitored subjects match these filters."
        />
      </Card>
    </div>
  );
}
