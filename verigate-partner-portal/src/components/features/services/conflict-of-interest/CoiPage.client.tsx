"use client";

import { useState, useMemo } from "react";
import { PageHeader } from "@/components/ui/PageHeader";
import { StatCard } from "@/components/ui/StatCard";
import { FilterChips, type FilterChip } from "@/components/ui/FilterChips";
import { DataTable } from "@/components/ui/DataTable";
import { Badge } from "@/components/ui/Badge";
import type { ColumnDef } from "@tanstack/react-table";
import {
  CoiDetail,
  type CoiRecord,
  type CoiSeverity,
  type CoiStatus,
} from "./CoiDetail.client";

/* ------------------------------------------------------------------ */
/*  Mock data                                                          */
/* ------------------------------------------------------------------ */

const MOCK_CONFLICTS: CoiRecord[] = [
  {
    id: "COI-2026-0041",
    parties: "Thabo Mokoena / Ingwe Holdings (Pty) Ltd",
    severity: "CRITICAL",
    status: "OPEN",
    detectedAt: "2026-05-18T09:14:00Z",
    description:
      "Thabo Mokoena serves as non-executive director of Ingwe Holdings while simultaneously holding a 12% equity stake in Vuka Minerals, a direct competitor bidding on the same Limpopo mining tender. This constitutes a material conflict under the Companies Act s.75.",
    assignee: "Naledi Nkosi",
    riskScore: 92,
    entities: [
      { id: "e1", name: "Thabo Mokoena", type: "person", role: "Director", riskLevel: "high", cx: 340, cy: 80 },
      { id: "e2", name: "Ingwe Holdings", type: "company", role: "Mining conglomerate", riskLevel: "medium", cx: 140, cy: 180 },
      { id: "e3", name: "Vuka Minerals", type: "company", role: "Competitor", riskLevel: "high", cx: 540, cy: 180 },
      { id: "e4", name: "Lindiwe Mokoena", type: "person", role: "Spouse", riskLevel: "medium", cx: 480, cy: 80 },
      { id: "e5", name: "Limpopo Tender Board", type: "company", role: "Government entity", riskLevel: "clear", cx: 340, cy: 290 },
    ],
    relationships: [
      { fromId: "e1", toId: "e2", label: "Director of" },
      { fromId: "e1", toId: "e3", label: "Shareholder (12%)" },
      { fromId: "e1", toId: "e4", label: "Spouse" },
      { fromId: "e4", toId: "e3", label: "CFO" },
      { fromId: "e2", toId: "e5", label: "Tender applicant" },
      { fromId: "e3", toId: "e5", label: "Tender applicant" },
    ],
    riskFactors: [
      { name: "Direct equity in competitor", value: "\u2717 12% stake", contrib: +30 },
      { name: "Competing tender bid", value: "\u2717 Same tender", contrib: +25 },
      { name: "Spousal executive role", value: "\u26A0 CFO at competitor", contrib: +20 },
      { name: "Board disclosure filed", value: "\u2717 Not disclosed", contrib: +15 },
      { name: "Duration of relationship", value: "\u26A0 3+ years", contrib: +10 },
      { name: "Prior compliance record", value: "\u2713 Clean", contrib: -8 },
    ],
  },
  {
    id: "COI-2026-0040",
    parties: "Sipho Dlamini / Umzansi Financial Services",
    severity: "HIGH",
    status: "INVESTIGATING",
    detectedAt: "2026-05-17T14:22:00Z",
    description:
      "Sipho Dlamini is a compliance officer at partner firm while his brother, Bongani Dlamini, is CEO of Umzansi Financial Services, a client undergoing enhanced due diligence review.",
    assignee: "Arthur Manena",
    riskScore: 76,
    entities: [
      { id: "e1", name: "Sipho Dlamini", type: "person", role: "Compliance officer", riskLevel: "high", cx: 200, cy: 100 },
      { id: "e2", name: "Bongani Dlamini", type: "person", role: "CEO", riskLevel: "high", cx: 480, cy: 100 },
      { id: "e3", name: "Umzansi Financial", type: "company", role: "Client under EDD", riskLevel: "medium", cx: 480, cy: 250 },
      { id: "e4", name: "VeriGate Partners", type: "company", role: "Employer", riskLevel: "clear", cx: 200, cy: 250 },
    ],
    relationships: [
      { fromId: "e1", toId: "e2", label: "Family member" },
      { fromId: "e2", toId: "e3", label: "CEO of" },
      { fromId: "e1", toId: "e4", label: "Employee of" },
      { fromId: "e4", toId: "e3", label: "EDD review" },
    ],
    riskFactors: [
      { name: "Family member in client entity", value: "\u2717 Brother is CEO", contrib: +25 },
      { name: "Direct oversight of review", value: "\u26A0 Assigned reviewer", contrib: +20 },
      { name: "Client under EDD", value: "\u26A0 Enhanced DD", contrib: +15 },
      { name: "Disclosure to management", value: "\u2713 Reported", contrib: -10 },
      { name: "Recusal offered", value: "\u2717 Not yet", contrib: +12 },
    ],
  },
  {
    id: "COI-2026-0039",
    parties: "Precious Nkosi / Ndalo Tech Solutions",
    severity: "MEDIUM",
    status: "INVESTIGATING",
    detectedAt: "2026-05-16T11:08:00Z",
    description:
      "Precious Nkosi sits on the procurement committee and her husband holds a 5% shareholding in Ndalo Tech Solutions, which has submitted a bid for the company's IT modernisation contract.",
    assignee: "Sipho Dlamini",
    riskScore: 58,
    entities: [
      { id: "e1", name: "Precious Nkosi", type: "person", role: "Procurement committee", riskLevel: "medium", cx: 200, cy: 120 },
      { id: "e2", name: "Ndalo Tech", type: "company", role: "IT bidder", riskLevel: "medium", cx: 480, cy: 120 },
      { id: "e3", name: "David Nkosi", type: "person", role: "Spouse", riskLevel: "medium", cx: 340, cy: 60 },
      { id: "e4", name: "Procurement Board", type: "company", role: "Internal body", riskLevel: "clear", cx: 200, cy: 270 },
      { id: "e5", name: "IT Modernisation", type: "company", role: "Contract", riskLevel: "clear", cx: 480, cy: 270 },
    ],
    relationships: [
      { fromId: "e1", toId: "e3", label: "Spouse" },
      { fromId: "e3", toId: "e2", label: "Shareholder (5%)" },
      { fromId: "e1", toId: "e4", label: "Committee member" },
      { fromId: "e2", toId: "e5", label: "Bid submitted" },
      { fromId: "e4", toId: "e5", label: "Evaluating" },
    ],
    riskFactors: [
      { name: "Spousal equity in bidder", value: "\u26A0 5% stake", contrib: +18 },
      { name: "Procurement committee role", value: "\u26A0 Active member", contrib: +15 },
      { name: "Disclosure on file", value: "\u2713 Disclosed", contrib: -8 },
      { name: "Equity threshold", value: "\u26A0 Below 10%", contrib: +8 },
      { name: "Contract value", value: "\u26A0 R4.2m", contrib: +12 },
      { name: "Independent review", value: "\u2713 Requested", contrib: -5 },
    ],
  },
  {
    id: "COI-2026-0038",
    parties: "Johan van Wyk / Kaap Agri Investments",
    severity: "HIGH",
    status: "OPEN",
    detectedAt: "2026-05-15T08:45:00Z",
    description:
      "Johan van Wyk is a board member of the regulated entity and serves simultaneously as a director of Kaap Agri Investments, a company that supplies agricultural inputs to several subsidiaries of the regulated entity.",
    assignee: null,
    riskScore: 71,
    entities: [
      { id: "e1", name: "Johan van Wyk", type: "person", role: "Board member", riskLevel: "high", cx: 340, cy: 70 },
      { id: "e2", name: "Kaap Agri Inv.", type: "company", role: "Supplier", riskLevel: "high", cx: 540, cy: 190 },
      { id: "e3", name: "Regulated Entity", type: "company", role: "Primary entity", riskLevel: "clear", cx: 140, cy: 190 },
      { id: "e4", name: "Subsidiary A", type: "company", role: "Subsidiary", riskLevel: "clear", cx: 240, cy: 300 },
      { id: "e5", name: "Subsidiary B", type: "company", role: "Subsidiary", riskLevel: "clear", cx: 440, cy: 300 },
    ],
    relationships: [
      { fromId: "e1", toId: "e3", label: "Board member" },
      { fromId: "e1", toId: "e2", label: "Director of" },
      { fromId: "e2", toId: "e4", label: "Supplier to" },
      { fromId: "e2", toId: "e5", label: "Supplier to" },
      { fromId: "e3", toId: "e4", label: "Parent co." },
      { fromId: "e3", toId: "e5", label: "Parent co." },
    ],
    riskFactors: [
      { name: "Dual directorship", value: "\u2717 Conflicting roles", contrib: +25 },
      { name: "Supply chain dependency", value: "\u26A0 2 subsidiaries", contrib: +18 },
      { name: "Contract value (annual)", value: "\u26A0 R8.1m", contrib: +15 },
      { name: "Board disclosure", value: "\u2717 Not filed", contrib: +12 },
      { name: "Cooling-off period", value: "\u2713 N/A", contrib: -5 },
    ],
  },
  {
    id: "COI-2026-0037",
    parties: "Lerato Mahlangu / Tsogo Property Fund",
    severity: "LOW",
    status: "RESOLVED",
    detectedAt: "2026-05-14T16:30:00Z",
    description:
      "Lerato Mahlangu holds a minor unit trust position in Tsogo Property Fund which was identified as a client during routine screening. The position is below material threshold and was disclosed proactively.",
    assignee: "Arthur Manena",
    riskScore: 22,
    entities: [
      { id: "e1", name: "Lerato Mahlangu", type: "person", role: "Analyst", riskLevel: "clear", cx: 240, cy: 120 },
      { id: "e2", name: "Tsogo Property", type: "company", role: "Client", riskLevel: "clear", cx: 440, cy: 120 },
      { id: "e3", name: "Unit Trust", type: "company", role: "Investment vehicle", riskLevel: "clear", cx: 340, cy: 250 },
    ],
    relationships: [
      { fromId: "e1", toId: "e3", label: "Unit holder (0.3%)" },
      { fromId: "e3", toId: "e2", label: "Invests in" },
    ],
    riskFactors: [
      { name: "Equity exposure", value: "\u2713 Below 1%", contrib: -5 },
      { name: "Proactive disclosure", value: "\u2713 Self-reported", contrib: -10 },
      { name: "Client interaction", value: "\u2713 No direct access", contrib: -8 },
      { name: "Indirect investment vehicle", value: "\u26A0 Unit trust", contrib: +8 },
    ],
  },
  {
    id: "COI-2026-0036",
    parties: "Mandla Sithole / Khula Mining Corp",
    severity: "MEDIUM",
    status: "OPEN",
    detectedAt: "2026-05-13T10:12:00Z",
    description:
      "Mandla Sithole is a senior risk assessor who previously served as a consultant to Khula Mining Corp within the past 24 months. Khula Mining is now subject to an ongoing AML investigation that Sithole has been assigned to review.",
    assignee: null,
    riskScore: 55,
    entities: [
      { id: "e1", name: "Mandla Sithole", type: "person", role: "Risk assessor", riskLevel: "medium", cx: 200, cy: 100 },
      { id: "e2", name: "Khula Mining", type: "company", role: "AML investigation", riskLevel: "high", cx: 480, cy: 100 },
      { id: "e3", name: "Consulting Co.", type: "company", role: "Prior employer", riskLevel: "clear", cx: 340, cy: 250 },
    ],
    relationships: [
      { fromId: "e1", toId: "e3", label: "Former consultant" },
      { fromId: "e3", toId: "e2", label: "Contracted to" },
      { fromId: "e1", toId: "e2", label: "Assigned reviewer" },
    ],
    riskFactors: [
      { name: "Prior business relationship", value: "\u26A0 Within 24 months", contrib: +18 },
      { name: "Assigned to review case", value: "\u2717 Active reviewer", contrib: +20 },
      { name: "Financial interest", value: "\u2713 None current", contrib: -8 },
      { name: "Cooling-off period", value: "\u26A0 Not met (24mo)", contrib: +15 },
      { name: "Disclosure", value: "\u2717 Not filed", contrib: +10 },
    ],
  },
  {
    id: "COI-2026-0035",
    parties: "Fatima Patel / Cape Coastal Developments",
    severity: "LOW",
    status: "RESOLVED",
    detectedAt: "2026-05-12T14:20:00Z",
    description:
      "Fatima Patel's cousin is employed in an administrative capacity at Cape Coastal Developments. The relationship was identified during periodic screening and does not involve decision-making authority or financial oversight.",
    assignee: "Naledi Nkosi",
    riskScore: 18,
    entities: [
      { id: "e1", name: "Fatima Patel", type: "person", role: "Account manager", riskLevel: "clear", cx: 220, cy: 130 },
      { id: "e2", name: "Cape Coastal Dev.", type: "company", role: "Client", riskLevel: "clear", cx: 460, cy: 130 },
      { id: "e3", name: "Zaheer Patel", type: "person", role: "Admin assistant", riskLevel: "clear", cx: 460, cy: 270 },
    ],
    relationships: [
      { fromId: "e1", toId: "e3", label: "Extended family" },
      { fromId: "e3", toId: "e2", label: "Employed by" },
      { fromId: "e1", toId: "e2", label: "Account manager" },
    ],
    riskFactors: [
      { name: "Family relationship", value: "\u26A0 Extended (cousin)", contrib: +8 },
      { name: "Decision-making authority", value: "\u2713 None", contrib: -10 },
      { name: "Financial oversight", value: "\u2713 None", contrib: -8 },
      { name: "Disclosure filed", value: "\u2713 Proactive", contrib: -5 },
    ],
  },
];

/* ------------------------------------------------------------------ */
/*  Column definitions                                                 */
/* ------------------------------------------------------------------ */

interface CoiListRow {
  id: string;
  parties: string;
  severity: CoiSeverity;
  status: CoiStatus;
  detectedAt: string;
  riskScore: number;
  assignee: string | null;
}

const SEVERITY_VARIANT: Record<
  CoiSeverity,
  "danger" | "warning" | "info" | "pending"
> = {
  CRITICAL: "danger",
  HIGH: "warning",
  MEDIUM: "info",
  LOW: "pending",
};

const STATUS_VARIANT: Record<CoiStatus, "info" | "warning" | "success"> = {
  OPEN: "info",
  INVESTIGATING: "warning",
  RESOLVED: "success",
};

const STATUS_LABEL: Record<CoiStatus, string> = {
  OPEN: "Open",
  INVESTIGATING: "Investigating",
  RESOLVED: "Resolved",
};

const columns: ColumnDef<CoiListRow, unknown>[] = [
  {
    accessorKey: "id",
    header: "Conflict ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">
        {getValue<string>()}
      </span>
    ),
  },
  {
    accessorKey: "parties",
    header: "Parties",
    cell: ({ getValue }) => (
      <span className="font-medium text-text">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "severity",
    header: "Severity",
    cell: ({ getValue }) => {
      const sev = getValue<CoiSeverity>();
      return <Badge variant={SEVERITY_VARIANT[sev]}>{sev}</Badge>;
    },
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ getValue }) => {
      const st = getValue<CoiStatus>();
      return <Badge variant={STATUS_VARIANT[st]}>{STATUS_LABEL[st]}</Badge>;
    },
  },
  {
    accessorKey: "riskScore",
    header: "Risk",
    cell: ({ getValue }) => {
      const score = getValue<number>();
      const color =
        score >= 80
          ? "#E23D36"
          : score >= 60
            ? "#C28B0B"
            : score >= 30
              ? "#00B3D9"
              : "#2C974B";
      return (
        <span className="font-mono font-semibold" style={{ color }}>
          {score}
        </span>
      );
    },
  },
  {
    accessorKey: "detectedAt",
    header: "Detected",
    cell: ({ getValue }) =>
      new Date(getValue<string>()).toLocaleDateString("en-ZA", {
        dateStyle: "medium",
      }),
  },
  {
    accessorKey: "assignee",
    header: "Assignee",
    cell: ({ getValue }) => {
      const val = getValue<string | null>();
      return val ? (
        <span className="text-text">{val}</span>
      ) : (
        <span className="text-text-muted italic">Unassigned</span>
      );
    },
  },
];

/* ------------------------------------------------------------------ */
/*  Main page component                                                */
/* ------------------------------------------------------------------ */

export function CoiPage() {
  const [filter, setFilter] = useState("all");
  const [selectedId, setSelectedId] = useState<string | null>(null);

  const rows: CoiListRow[] = useMemo(
    () =>
      MOCK_CONFLICTS.map((c) => ({
        id: c.id,
        parties: c.parties,
        severity: c.severity,
        status: c.status,
        detectedAt: c.detectedAt,
        riskScore: c.riskScore,
        assignee: c.assignee,
      })),
    [],
  );

  const filtered = useMemo(() => {
    if (filter === "all") return rows;
    return rows.filter(
      (r) => r.status === filter.toUpperCase(),
    );
  }, [rows, filter]);

  const openCount = rows.filter((r) => r.status === "OPEN").length;
  const investigatingCount = rows.filter(
    (r) => r.status === "INVESTIGATING",
  ).length;
  const criticalCount = rows.filter(
    (r) => r.severity === "CRITICAL",
  ).length;

  const resolvedRows = MOCK_CONFLICTS.filter((c) => c.status === "RESOLVED");
  const avgResolutionDays =
    resolvedRows.length > 0
      ? Math.round(
          resolvedRows.reduce((acc, c) => {
            const detected = new Date(c.detectedAt).getTime();
            const resolved = detected + 3 * 24 * 60 * 60 * 1000; // mock: ~3 days
            return acc + (resolved - detected) / (1000 * 60 * 60 * 24);
          }, 0) / resolvedRows.length,
        )
      : 0;

  const filterChips: FilterChip[] = [
    { label: "All", value: "all", count: rows.length },
    { label: "Open", value: "open", count: openCount },
    { label: "Investigating", value: "investigating", count: investigatingCount },
    { label: "Resolved", value: "resolved", count: rows.filter((r) => r.status === "RESOLVED").length },
  ];

  // Show detail view if a conflict is selected
  const selectedRecord = selectedId
    ? MOCK_CONFLICTS.find((c) => c.id === selectedId) ?? null
    : null;

  if (selectedRecord) {
    return (
      <div className="space-y-aws-l">
        <CoiDetail
          record={selectedRecord}
          onBack={() => setSelectedId(null)}
        />
      </div>
    );
  }

  return (
    <div className="space-y-aws-l">
      <PageHeader
        category="Enterprise \u00b7 Compliance"
        title="Conflict of Interest"
        description="Detect, investigate, and resolve conflicts of interest across entities, directors, and related parties. Compliant with Companies Act s.75 and King IV requirements."
      />

      {/* Stat cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
        <StatCard
          label="Total conflicts"
          value={rows.length}
          helper="Across all statuses"
        />
        <StatCard
          label="Open"
          value={openCount}
          deltaTone={openCount > 0 ? "warning" : "success"}
          helper="Awaiting investigation"
        />
        <StatCard
          label="Critical severity"
          value={criticalCount}
          deltaTone={criticalCount > 0 ? "danger" : "success"}
          helper="Requires immediate action"
        />
        <StatCard
          label="Avg resolution"
          value={`${avgResolutionDays}d`}
          deltaTone="muted"
          helper="Mean time to resolve"
        />
      </div>

      {/* Filter chips */}
      <FilterChips chips={filterChips} value={filter} onChange={setFilter} />

      {/* Data table */}
      <DataTable
        data={filtered}
        columns={columns}
        pageSize={10}
        onRowClick={(row) => setSelectedId(row.id)}
        emptyMessage="No conflicts match the selected filter."
      />
    </div>
  );
}
