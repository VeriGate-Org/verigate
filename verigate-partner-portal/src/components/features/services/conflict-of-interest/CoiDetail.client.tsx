"use client";

import { useState } from "react";
import { ArrowLeft, Download, Clock, User } from "lucide-react";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

export type CoiSeverity = "CRITICAL" | "HIGH" | "MEDIUM" | "LOW";
export type CoiStatus = "OPEN" | "INVESTIGATING" | "RESOLVED";

export interface CoiEntity {
  id: string;
  name: string;
  type: "person" | "company";
  role: string;
  riskLevel: "high" | "medium" | "clear";
  /** SVG coordinates */
  cx: number;
  cy: number;
}

export interface CoiRelationship {
  fromId: string;
  toId: string;
  label: string;
}

export interface CoiRiskFactor {
  name: string;
  value: string;
  contrib: number;
}

export interface CoiRecord {
  id: string;
  parties: string;
  severity: CoiSeverity;
  status: CoiStatus;
  detectedAt: string;
  description: string;
  assignee: string | null;
  entities: CoiEntity[];
  relationships: CoiRelationship[];
  riskScore: number;
  riskFactors: CoiRiskFactor[];
}

/* ------------------------------------------------------------------ */
/*  Helpers                                                            */
/* ------------------------------------------------------------------ */

const SEVERITY_MAP: Record<
  CoiSeverity,
  { label: string; variant: "danger" | "warning" | "info" | "pending" }
> = {
  CRITICAL: { label: "Critical", variant: "danger" },
  HIGH: { label: "High", variant: "warning" },
  MEDIUM: { label: "Medium", variant: "info" },
  LOW: { label: "Low", variant: "pending" },
};

const STATUS_MAP: Record<
  CoiStatus,
  { label: string; variant: "info" | "warning" | "success" }
> = {
  OPEN: { label: "Open", variant: "info" },
  INVESTIGATING: { label: "Investigating", variant: "warning" },
  RESOLVED: { label: "Resolved", variant: "success" },
};

const RISK_NODE_COLORS: Record<string, { fill: string; stroke: string }> = {
  high: { fill: "#E23D36", stroke: "#c82f29" },
  medium: { fill: "#C28B0B", stroke: "#a87808" },
  clear: { fill: "#2C974B", stroke: "#237a3c" },
};

function getInitials(name: string): string {
  return name
    .split(" ")
    .map((p) => p[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();
}

/* ------------------------------------------------------------------ */
/*  Relationship Graph (SVG)                                           */
/* ------------------------------------------------------------------ */

function RelationshipGraph({
  entities,
  relationships,
}: {
  entities: CoiEntity[];
  relationships: CoiRelationship[];
}) {
  const entityMap = new Map(entities.map((e) => [e.id, e]));

  return (
    <Card>
      <CardHeader>
        <div>
          <div className="text-sm font-semibold text-text">
            Relationship graph
          </div>
          <div className="text-[11px] text-text-muted mt-0.5">
            {entities.length} entities &middot; {relationships.length}{" "}
            relationships
          </div>
        </div>
      </CardHeader>
      <CardBody>
        <div className="overflow-x-auto">
          <svg
            viewBox="0 0 680 400"
            className="w-full max-w-[680px] mx-auto"
            style={{ minHeight: 340 }}
          >
            {/* Edges */}
            {relationships.map((rel, i) => {
              const from = entityMap.get(rel.fromId);
              const to = entityMap.get(rel.toId);
              if (!from || !to) return null;

              const midX = (from.cx + to.cx) / 2;
              const midY = (from.cy + to.cy) / 2;
              const dx = to.cx - from.cx;
              const dy = to.cy - from.cy;
              const len = Math.sqrt(dx * dx + dy * dy) || 1;
              const offsetX = (-dy / len) * 10;
              const offsetY = (dx / len) * 10;

              return (
                <g key={`edge-${i}`}>
                  <line
                    x1={from.cx}
                    y1={from.cy}
                    x2={to.cx}
                    y2={to.cy}
                    stroke="#D5DBDB"
                    strokeWidth="1.5"
                    strokeDasharray="6 3"
                  />
                  <rect
                    x={midX + offsetX - 48}
                    y={midY + offsetY - 9}
                    width={96}
                    height={18}
                    rx={9}
                    fill="#ffffff"
                    stroke="#D5DBDB"
                    strokeWidth="1"
                  />
                  <text
                    x={midX + offsetX}
                    y={midY + offsetY + 4}
                    textAnchor="middle"
                    fill="#4F5B67"
                    fontSize="9"
                    fontWeight="500"
                  >
                    {rel.label}
                  </text>
                </g>
              );
            })}

            {/* Nodes */}
            {entities.map((ent) => {
              const colors = RISK_NODE_COLORS[ent.riskLevel];
              const r = ent.type === "company" ? 30 : 26;
              const initials = getInitials(ent.name);

              return (
                <g key={ent.id}>
                  {/* Outer glow for high risk */}
                  {ent.riskLevel === "high" && (
                    <circle
                      cx={ent.cx}
                      cy={ent.cy}
                      r={r + 6}
                      fill="none"
                      stroke="#E23D36"
                      strokeWidth="1"
                      strokeDasharray="3 2"
                      opacity={0.4}
                    />
                  )}
                  <circle
                    cx={ent.cx}
                    cy={ent.cy}
                    r={r}
                    fill={colors.fill}
                    stroke={colors.stroke}
                    strokeWidth="2"
                  />
                  <text
                    x={ent.cx}
                    y={ent.cy + 1}
                    textAnchor="middle"
                    dominantBaseline="middle"
                    fill="#ffffff"
                    fontSize={r > 26 ? "13" : "11"}
                    fontWeight="700"
                    fontFamily="system-ui, sans-serif"
                  >
                    {initials}
                  </text>
                  {/* Label below */}
                  <text
                    x={ent.cx}
                    y={ent.cy + r + 14}
                    textAnchor="middle"
                    fill="#1A2024"
                    fontSize="11"
                    fontWeight="600"
                  >
                    {ent.name}
                  </text>
                  <text
                    x={ent.cx}
                    y={ent.cy + r + 26}
                    textAnchor="middle"
                    fill="#4F5B67"
                    fontSize="9"
                  >
                    {ent.role}
                  </text>
                </g>
              );
            })}

            {/* Legend */}
            <g transform="translate(10, 360)">
              <circle cx={6} cy={6} r={5} fill="#E23D36" />
              <text x={16} y={10} fill="#4F5B67" fontSize="9">
                High risk
              </text>
              <circle cx={86} cy={6} r={5} fill="#C28B0B" />
              <text x={96} y={10} fill="#4F5B67" fontSize="9">
                Medium risk
              </text>
              <circle cx={176} cy={6} r={5} fill="#2C974B" />
              <text x={186} y={10} fill="#4F5B67" fontSize="9">
                Clear
              </text>
            </g>
          </svg>
        </div>
      </CardBody>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Risk scoring panel                                                 */
/* ------------------------------------------------------------------ */

function RiskScoringPanel({
  factors,
  score,
}: {
  factors: CoiRiskFactor[];
  score: number;
}) {
  const color =
    score >= 80
      ? "#E23D36"
      : score >= 60
        ? "#C28B0B"
        : score >= 30
          ? "#00B3D9"
          : "#2C974B";

  return (
    <Card>
      <CardHeader>
        <div>
          <div className="text-sm font-semibold text-text">Risk scoring</div>
          <div className="text-[11px] text-text-muted mt-0.5">
            {factors.length} factors contribute to the conflict risk score
          </div>
        </div>
        <div className="flex items-center gap-2">
          <span
            className="text-[22px] font-bold leading-none"
            style={{ color }}
          >
            {score}
          </span>
          <span className="text-[10px] text-text-muted uppercase tracking-[0.06em]">
            / 100
          </span>
        </div>
      </CardHeader>
      <div className="py-1.5">
        {factors.map((f) => (
          <div
            key={f.name}
            className="flex items-center gap-3.5 px-[18px] py-2 border-b border-[#f1f5f9] text-xs last:border-0"
          >
            <div className="flex-1 font-medium text-text">{f.name}</div>
            <div
              className="w-[120px] font-mono text-[11px]"
              style={{ color: f.contrib > 0 ? "#E23D36" : "#4F5B67" }}
            >
              {f.value}
            </div>
            <div className="w-20 h-1 bg-[#F2F3F3] rounded relative overflow-hidden">
              <div
                className="absolute top-0 bottom-0"
                style={{
                  left: f.contrib > 0 ? "50%" : undefined,
                  right: f.contrib <= 0 ? "50%" : undefined,
                  width: `${Math.abs(f.contrib) * 1.4}%`,
                  background: f.contrib > 0 ? "#E23D36" : "#2C974B",
                }}
              />
            </div>
            <div
              className="w-[50px] text-right font-mono font-semibold"
              style={{ color: f.contrib > 0 ? "#E23D36" : "#2C974B" }}
            >
              {f.contrib > 0 ? "+" : ""}
              {f.contrib}
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Resolution panel                                                   */
/* ------------------------------------------------------------------ */

function ResolutionPanel({
  status,
  onStatusChange,
  onResolve,
}: {
  status: CoiStatus;
  onStatusChange: (status: CoiStatus) => void;
  onResolve: (reason: string) => void;
}) {
  const [reason, setReason] = useState("");

  return (
    <Card>
      <CardHeader>
        <div className="text-sm font-semibold text-text">Resolution</div>
      </CardHeader>
      <CardBody>
        <div className="space-y-3">
          <div>
            <label className="text-[11px] font-semibold text-text-muted uppercase tracking-wide block mb-1">
              Status
            </label>
            <select
              value={status}
              onChange={(e) => onStatusChange(e.target.value as CoiStatus)}
              className="aws-select select-input w-full"
            >
              <option value="OPEN">Open</option>
              <option value="INVESTIGATING">Investigating</option>
              <option value="RESOLVED">Resolved</option>
            </select>
          </div>
          <div>
            <label className="text-[11px] font-semibold text-text-muted uppercase tracking-wide block mb-1">
              Resolution reason
            </label>
            <textarea
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Provide the reason for resolving this conflict..."
              className="aws-textarea w-full px-2.5 py-1.5 text-xs rounded min-h-[80px] resize-y"
            />
          </div>
          <div className="flex justify-end gap-2">
            {status !== "RESOLVED" ? (
              <Button
                variant="cta"
                size="sm"
                onClick={() => onResolve(reason)}
                disabled={!reason.trim()}
              >
                Resolve conflict
              </Button>
            ) : (
              <Button
                variant="primary"
                size="sm"
                onClick={() => onStatusChange("OPEN")}
              >
                Reopen conflict
              </Button>
            )}
          </div>
        </div>
      </CardBody>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Main detail component                                              */
/* ------------------------------------------------------------------ */

export function CoiDetail({
  record,
  onBack,
}: {
  record: CoiRecord;
  onBack: () => void;
}) {
  const [status, setStatus] = useState<CoiStatus>(record.status);

  const severityInfo = SEVERITY_MAP[record.severity];
  const statusInfo = STATUS_MAP[status];

  const handleResolve = (reason: string) => {
    setStatus("RESOLVED");
    // In production this would POST to the API
    void reason;
  };

  return (
    <div className="space-y-4">
      {/* Back link */}
      <button
        onClick={onBack}
        className="inline-flex items-center gap-1 text-accent text-xs font-medium hover:underline"
      >
        <ArrowLeft size={12} /> Back to conflicts
      </button>

      {/* Header card with tri-bar accent */}
      <div className="console-card overflow-hidden">
        <div className="flex h-[3px]">
          <div className="flex-[3] bg-[#E23D36]" />
          <div className="flex-[5] bg-[#1A2E4B]" />
          <div className="flex-[2] bg-[#00B3D9]" />
        </div>
        <div className="p-5 flex flex-wrap justify-between items-start gap-4">
          <div>
            <div className="flex items-center gap-2.5 mb-1 flex-wrap">
              <span className="font-mono text-xs text-accent">{record.id}</span>
              <Badge variant={severityInfo.variant}>
                {severityInfo.label}
              </Badge>
              <Badge variant={statusInfo.variant}>{statusInfo.label}</Badge>
            </div>
            <div className="text-xl font-semibold text-text mt-1">
              {record.parties}
            </div>
            <div className="text-xs text-text-muted mt-1 max-w-xl leading-relaxed">
              {record.description}
            </div>
          </div>
          <div className="flex items-center gap-3 shrink-0">
            <div className="text-right">
              <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted mb-0.5">
                Risk score
              </div>
              <div
                className="text-[28px] font-bold leading-none"
                style={{
                  color:
                    record.riskScore >= 80
                      ? "#E23D36"
                      : record.riskScore >= 60
                        ? "#C28B0B"
                        : record.riskScore >= 30
                          ? "#00B3D9"
                          : "#2C974B",
                }}
              >
                {record.riskScore}
              </div>
            </div>
            <Button
              variant="secondary"
              size="sm"
              icon={<Download size={12} />}
            >
              Export
            </Button>
          </div>
        </div>

        {/* Meta strip */}
        <div className="grid grid-cols-4 border-t border-border">
          {[
            {
              label: "Detected",
              value: new Date(record.detectedAt).toLocaleDateString("en-ZA", {
                dateStyle: "medium",
              }),
              icon: <Clock size={11} className="text-text-muted" />,
            },
            {
              label: "Severity",
              value: severityInfo.label,
              color:
                record.severity === "CRITICAL"
                  ? "#E23D36"
                  : record.severity === "HIGH"
                    ? "#C28B0B"
                    : undefined,
            },
            {
              label: "Entities",
              value: `${record.entities.length} linked`,
            },
            {
              label: "Assignee",
              value: record.assignee ?? "Unassigned",
              icon: <User size={11} className="text-text-muted" />,
            },
          ].map((c, i) => (
            <div
              key={c.label}
              className={`px-4 py-3 ${i > 0 ? "border-l border-border" : ""}`}
            >
              <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">
                {c.label}
              </div>
              <div
                className="text-sm font-semibold mt-1 flex items-center gap-1"
                style={c.color ? { color: c.color } : undefined}
              >
                {c.icon}
                {c.value}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Two-column body */}
      <div className="grid grid-cols-1 lg:grid-cols-[1fr_340px] gap-4">
        {/* Main column */}
        <div className="space-y-3.5">
          <RelationshipGraph
            entities={record.entities}
            relationships={record.relationships}
          />
          <RiskScoringPanel
            factors={record.riskFactors}
            score={record.riskScore}
          />
        </div>

        {/* Sidebar */}
        <div className="space-y-3">
          <ResolutionPanel
            status={status}
            onStatusChange={setStatus}
            onResolve={handleResolve}
          />

          {/* Involved entities list */}
          <Card className="p-[18px]">
            <div className="text-[11px] text-text-muted uppercase tracking-[0.06em] font-semibold mb-2.5">
              Involved entities
            </div>
            <div className="flex flex-col gap-1.5">
              {record.entities.map((ent) => {
                const colors = RISK_NODE_COLORS[ent.riskLevel];
                return (
                  <div
                    key={ent.id}
                    className="flex items-center gap-2.5 p-1.5 rounded bg-[#F8FAFC] text-[11px]"
                  >
                    <span
                      className="w-6 h-6 rounded-full text-white text-[9px] font-bold inline-flex items-center justify-center shrink-0"
                      style={{ background: colors.fill }}
                    >
                      {getInitials(ent.name)}
                    </span>
                    <div className="flex-1 min-w-0">
                      <div className="font-medium text-text truncate">
                        {ent.name}
                      </div>
                      <div className="text-text-muted">{ent.role}</div>
                    </div>
                    <Badge
                      variant={
                        ent.riskLevel === "high"
                          ? "danger"
                          : ent.riskLevel === "medium"
                            ? "warning"
                            : "success"
                      }
                      size="sm"
                    >
                      {ent.riskLevel}
                    </Badge>
                  </div>
                );
              })}
            </div>
          </Card>

          {/* Regulatory notice */}
          <div className="bg-[#F8FAFC] border border-dashed border-[#CBD5E1] rounded-aws-container p-3 text-[11px] text-text-muted leading-relaxed">
            <b className="text-[#1A2E4B]">Companies Act s.75:</b> Directors must
            disclose personal financial interests in matters before the board.
            This conflict record is maintained for regulatory compliance.
          </div>
        </div>
      </div>
    </div>
  );
}
