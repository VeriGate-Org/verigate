"use client";

import { useState, useMemo } from "react";
import { useRouter } from "next/navigation";
import { Download, Plus, Search, ChevronRight } from "lucide-react";
import { type ColumnDef } from "@tanstack/react-table";
import { PageHeader } from "@/components/ui/PageHeader";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { StatCard } from "@/components/ui/StatCard";
import { FilterChips } from "@/components/ui/FilterChips";
import { DataTable } from "@/components/ui/DataTable";

/* ── Types ─────────────────────────────────────────────── */

type CaseStatus = "OPEN" | "IN_REVIEW" | "ESCALATED" | "RESOLVED";
type CasePriority = "CRITICAL" | "HIGH" | "MEDIUM" | "LOW";

interface CaseRow {
  caseId: string;
  short: string;
  subjectName: string;
  subjectId: string;
  status: CaseStatus;
  priority: CasePriority;
  riskScore: number;
  assignee: string | null;
  createdAt: string;
  verificationId: string;
  reason: string;
}

/* ── Mock data ─────────────────────────────────────────── */

const CASES: CaseRow[] = [
  { caseId: "CS-7b3a91e2", short: "CS-7b3a91e2", subjectName: "Jane Smith", subjectId: "8503125800087", status: "ESCALATED", priority: "HIGH", riskScore: 87, assignee: "Naledi Nkosi", createdAt: "2026-05-18T14:33:02Z", verificationId: "VG-2026-0140", reason: "Credit bureau returned hard fail; adverse listings within 12 months." },
  { caseId: "CS-1d4b8f12", short: "CS-1d4b8f12", subjectName: "Acme Corp Ltd", subjectId: "CIPC 2018/451288/07", status: "IN_REVIEW", priority: "HIGH", riskScore: 78, assignee: "Sipho Dlamini", createdAt: "2026-05-18T13:18:14Z", verificationId: "VG-2026-0141", reason: "Partial sanctions list match \u2014 name score 0.78." },
  { caseId: "CS-9e2c5a45", short: "CS-9e2c5a45", subjectName: "Bob Williams", subjectId: "7806174500083", status: "IN_REVIEW", priority: "MEDIUM", riskScore: 64, assignee: "Arthur Manena", createdAt: "2026-05-18T13:46:21Z", verificationId: "VG-2026-0139", reason: "DHA response delayed past 30-minute SLA." },
  { caseId: "CS-3a8e2f9c", short: "CS-3a8e2f9c", subjectName: "Mandla Tshabalala", subjectId: "8801135500084", status: "OPEN", priority: "MEDIUM", riskScore: 58, assignee: null, createdAt: "2026-05-17T10:24:08Z", verificationId: "VG-2026-0134", reason: "Credit profile mismatch \u2014 score below threshold (-12 vs policy)." },
  { caseId: "CS-6c1f4d80", short: "CS-6c1f4d80", subjectName: "Lerato Mokoena", subjectId: "9006120800086", status: "RESOLVED", priority: "LOW", riskScore: 22, assignee: "Arthur Manena", createdAt: "2026-05-16T08:55:33Z", verificationId: "VG-2026-0138", reason: "Auto-resolved \u2014 passed manual review." },
  { caseId: "CS-2f7d8a31", short: "CS-2f7d8a31", subjectName: "Pieter van der Merwe", subjectId: "7503060800087", status: "OPEN", priority: "LOW", riskScore: 41, assignee: null, createdAt: "2026-05-17T16:08:12Z", verificationId: "VG-2026-0137", reason: "Inconsistent address on file." },
  { caseId: "CS-8b4e1f02", short: "CS-8b4e1f02", subjectName: "Naledi Nkosi", subjectId: "9304117500084", status: "RESOLVED", priority: "LOW", riskScore: 18, assignee: "Naledi Nkosi", createdAt: "2026-05-15T11:42:00Z", verificationId: "VG-2026-0136", reason: "Biometric mismatch retried, resolved on second attempt." },
  { caseId: "CS-5e9d3c47", short: "CS-5e9d3c47", subjectName: "Thandiwe Khumalo", subjectId: "9509223200086", status: "ESCALATED", priority: "CRITICAL", riskScore: 94, assignee: "Sipho Dlamini", createdAt: "2026-05-18T09:14:53Z", verificationId: "VG-2026-0133", reason: "Suspected synthetic identity \u2014 ID issued same week as application." },
  { caseId: "CS-1a2b3c4d", short: "CS-1a2b3c4d", subjectName: "Sipho Dlamini", subjectId: "8211056500088", status: "IN_REVIEW", priority: "MEDIUM", riskScore: 55, assignee: "Arthur Manena", createdAt: "2026-05-17T12:05:42Z", verificationId: "VG-2026-0135", reason: "PEP screening \u2014 extended family member listed as PEP." },
];

/* ── Helpers ────────────────────────────────────────────── */

const STATUS_MAP: Record<CaseStatus, { label: string; variant: "success" | "danger" | "warning" | "info" }> = {
  OPEN: { label: "Open", variant: "info" },
  IN_REVIEW: { label: "In Review", variant: "warning" },
  ESCALATED: { label: "Escalated", variant: "danger" },
  RESOLVED: { label: "Resolved", variant: "success" },
};

const PRIORITY_MAP: Record<CasePriority, { label: string; color: string }> = {
  CRITICAL: { label: "Critical", color: "#E23D36" },
  HIGH: { label: "High", color: "#C28B0B" },
  MEDIUM: { label: "Medium", color: "#00B3D9" },
  LOW: { label: "Low", color: "#4F5B67" },
};

function fmtAgo(iso: string): string {
  const ago = Date.now() - new Date(iso).getTime();
  const h = Math.floor(ago / 3_600_000);
  if (h < 1) return `${Math.floor(ago / 60_000)}m ago`;
  if (h < 24) return `${h}h ago`;
  return `${Math.floor(h / 24)}d ago`;
}

function Avatar({ name, size = 28 }: { name: string; size?: number }) {
  const initials = name
    .split(" ")
    .map((p) => p[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();

  return (
    <div
      className="rounded-full bg-primary text-white inline-flex items-center justify-center font-semibold shrink-0"
      style={{ width: size, height: size, fontSize: size * 0.36 }}
    >
      {initials}
    </div>
  );
}

/* ── Table columns ─────────────────────────────────────── */

const columns: ColumnDef<CaseRow, unknown>[] = [
  {
    accessorKey: "short",
    header: "Case ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "subjectName",
    header: "Subject",
    cell: ({ row }) => (
      <div className="flex items-center gap-2.5">
        <Avatar name={row.original.subjectName} />
        <div>
          <div className="text-[13px] font-medium text-text">
            {row.original.subjectName}
          </div>
          <div className="font-mono text-[10px] text-text-muted">
            {row.original.subjectId}
          </div>
        </div>
      </div>
    ),
  },
  {
    accessorKey: "priority",
    header: "Priority",
    cell: ({ getValue }) => {
      const p = PRIORITY_MAP[getValue<CasePriority>()];
      return (
        <span
          className="inline-flex items-center gap-1 text-[11px] font-semibold"
          style={{ color: p.color }}
        >
          <span
            className="w-2 h-2 rounded-full"
            style={{ background: p.color }}
          />
          {p.label}
        </span>
      );
    },
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ getValue }) => {
      const s = STATUS_MAP[getValue<CaseStatus>()];
      return <Badge variant={s.variant}>{s.label}</Badge>;
    },
  },
  {
    accessorKey: "assignee",
    header: "Assignee",
    cell: ({ getValue }) => {
      const assignee = getValue<string | null>();
      return assignee ? (
        <span className="text-[13px] text-text">{assignee}</span>
      ) : (
        <span className="text-[13px] text-text-muted italic">Unassigned</span>
      );
    },
  },
  {
    accessorKey: "createdAt",
    header: "Created",
    cell: ({ getValue }) => (
      <span className="text-xs text-text-muted">{fmtAgo(getValue<string>())}</span>
    ),
  },
  {
    id: "chevron",
    header: "",
    enableSorting: false,
    cell: () => (
      <span className="text-text-muted">
        <ChevronRight size={13} />
      </span>
    ),
  },
];

/* ── Filter chips ──────────────────────────────────────── */

function buildFilterChips(cases: CaseRow[]) {
  return [
    { label: "All", value: "all", count: cases.length },
    { label: "Open", value: "OPEN", count: cases.filter((c) => c.status === "OPEN").length },
    { label: "In Review", value: "IN_REVIEW", count: cases.filter((c) => c.status === "IN_REVIEW").length },
    { label: "Escalated", value: "ESCALATED", count: cases.filter((c) => c.status === "ESCALATED").length },
    { label: "Resolved", value: "RESOLVED", count: cases.filter((c) => c.status === "RESOLVED").length },
  ];
}

/* ── KPI calculations ──────────────────────────────────── */

function computeKpis(cases: CaseRow[]) {
  const total = cases.length;
  const open = cases.filter((c) => c.status === "OPEN" || c.status === "IN_REVIEW" || c.status === "ESCALATED").length;
  const critical = cases.filter((c) => c.priority === "CRITICAL" || c.priority === "HIGH").length;
  const avgHours = "18.4h";
  return { total, open, critical, avgHours };
}

/* ── Page component ────────────────────────────────────── */

export function CasesPage() {
  const router = useRouter();
  const [statusFilter, setStatusFilter] = useState("all");
  const [query, setQuery] = useState("");

  const cases = CASES;
  const chips = useMemo(() => buildFilterChips(cases), [cases]);
  const kpis = useMemo(() => computeKpis(cases), [cases]);

  const filtered = useMemo(() => {
    let result = cases;
    if (statusFilter !== "all") {
      result = result.filter((c) => c.status === statusFilter);
    }
    if (query) {
      const q = query.toLowerCase();
      result = result.filter(
        (c) =>
          c.subjectName.toLowerCase().includes(q) ||
          c.short.toLowerCase().includes(q) ||
          (c.assignee ?? "").toLowerCase().includes(q),
      );
    }
    return result;
  }, [cases, statusFilter, query]);

  return (
    <div className="space-y-aws-m">
      <PageHeader
        category="Overview"
        title="Cases"
        description={`Cases are automatically created when a verification result requires manual review. ${kpis.open} active.`}
        actions={
          <>
            <Button variant="secondary" icon={<Download size={13} />}>
              Export
            </Button>
            <Button variant="cta" icon={<Plus size={13} />}>
              New case
            </Button>
          </>
        }
      />

      {/* KPI strip */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        <StatCard label="Total Cases" value={kpis.total} delta="+3" deltaTone="muted" helper="this week" />
        <StatCard label="Open" value={kpis.open} delta="+2" deltaTone="warning" helper="requiring attention" />
        <StatCard label="Critical / High" value={kpis.critical} delta="+1" deltaTone="danger" helper="escalated priority" />
        <StatCard label="Avg Resolution" value={kpis.avgHours} helper="target: 48h" />
      </div>

      {/* Filter bar */}
      <div className="flex justify-between items-center gap-4">
        <FilterChips chips={chips} value={statusFilter} onChange={setStatusFilter} />
        <div className="relative w-72">
          <span className="absolute left-2.5 top-1/2 -translate-y-1/2 inline-flex">
            <Search size={13} className="text-text-muted" />
          </span>
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search subject, case ID, assignee\u2026"
            className="aws-input w-full pl-8 pr-3 py-1.5 text-xs rounded"
          />
        </div>
      </div>

      {/* Data table */}
      <DataTable
        data={filtered}
        columns={columns}
        onRowClick={(row) => router.push(`/cases/${row.caseId}`)}
        pageSize={10}
        emptyMessage="No cases match your filters."
      />
    </div>
  );
}
