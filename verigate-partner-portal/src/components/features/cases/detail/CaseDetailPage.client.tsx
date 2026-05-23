"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { ArrowLeft, Send } from "lucide-react";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";

/* ── Types ─────────────────────────────────────────────── */

type CaseStatus = "OPEN" | "IN_REVIEW" | "ESCALATED" | "RESOLVED";
type CasePriority = "CRITICAL" | "HIGH" | "MEDIUM" | "LOW";

interface CaseData {
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

interface TimelineEvent {
  event: string;
  actor: string;
  ts: string;
  tone: "info" | "danger" | "warning" | "success" | "pending";
}

interface Comment {
  author: string;
  text: string;
  ts: string;
  system?: boolean;
}

interface RiskFactor {
  name: string;
  value: string;
  contrib: number;
}

/* ── Mock data ─────────────────────────────────────────── */

const CASES_LOOKUP: Record<string, CaseData> = {
  "CS-7b3a91e2": { caseId: "CS-7b3a91e2", short: "CS-7b3a91e2", subjectName: "Jane Smith", subjectId: "8503125800087", status: "ESCALATED", priority: "HIGH", riskScore: 87, assignee: "Naledi Nkosi", createdAt: "2026-05-18T14:33:02Z", verificationId: "VG-2026-0140", reason: "Credit bureau returned hard fail; adverse listings within 12 months." },
  "CS-1d4b8f12": { caseId: "CS-1d4b8f12", short: "CS-1d4b8f12", subjectName: "Acme Corp Ltd", subjectId: "CIPC 2018/451288/07", status: "IN_REVIEW", priority: "HIGH", riskScore: 78, assignee: "Sipho Dlamini", createdAt: "2026-05-18T13:18:14Z", verificationId: "VG-2026-0141", reason: "Partial sanctions list match \u2014 name score 0.78." },
  "CS-9e2c5a45": { caseId: "CS-9e2c5a45", short: "CS-9e2c5a45", subjectName: "Bob Williams", subjectId: "7806174500083", status: "IN_REVIEW", priority: "MEDIUM", riskScore: 64, assignee: "Arthur Manena", createdAt: "2026-05-18T13:46:21Z", verificationId: "VG-2026-0139", reason: "DHA response delayed past 30-minute SLA." },
  "CS-3a8e2f9c": { caseId: "CS-3a8e2f9c", short: "CS-3a8e2f9c", subjectName: "Mandla Tshabalala", subjectId: "8801135500084", status: "OPEN", priority: "MEDIUM", riskScore: 58, assignee: null, createdAt: "2026-05-17T10:24:08Z", verificationId: "VG-2026-0134", reason: "Credit profile mismatch \u2014 score below threshold (-12 vs policy)." },
  "CS-6c1f4d80": { caseId: "CS-6c1f4d80", short: "CS-6c1f4d80", subjectName: "Lerato Mokoena", subjectId: "9006120800086", status: "RESOLVED", priority: "LOW", riskScore: 22, assignee: "Arthur Manena", createdAt: "2026-05-16T08:55:33Z", verificationId: "VG-2026-0138", reason: "Auto-resolved \u2014 passed manual review." },
  "CS-2f7d8a31": { caseId: "CS-2f7d8a31", short: "CS-2f7d8a31", subjectName: "Pieter van der Merwe", subjectId: "7503060800087", status: "OPEN", priority: "LOW", riskScore: 41, assignee: null, createdAt: "2026-05-17T16:08:12Z", verificationId: "VG-2026-0137", reason: "Inconsistent address on file." },
  "CS-8b4e1f02": { caseId: "CS-8b4e1f02", short: "CS-8b4e1f02", subjectName: "Naledi Nkosi", subjectId: "9304117500084", status: "RESOLVED", priority: "LOW", riskScore: 18, assignee: "Naledi Nkosi", createdAt: "2026-05-15T11:42:00Z", verificationId: "VG-2026-0136", reason: "Biometric mismatch retried, resolved on second attempt." },
  "CS-5e9d3c47": { caseId: "CS-5e9d3c47", short: "CS-5e9d3c47", subjectName: "Thandiwe Khumalo", subjectId: "9509223200086", status: "ESCALATED", priority: "CRITICAL", riskScore: 94, assignee: "Sipho Dlamini", createdAt: "2026-05-18T09:14:53Z", verificationId: "VG-2026-0133", reason: "Suspected synthetic identity \u2014 ID issued same week as application." },
  "CS-1a2b3c4d": { caseId: "CS-1a2b3c4d", short: "CS-1a2b3c4d", subjectName: "Sipho Dlamini", subjectId: "8211056500088", status: "IN_REVIEW", priority: "MEDIUM", riskScore: 55, assignee: "Arthur Manena", createdAt: "2026-05-17T12:05:42Z", verificationId: "VG-2026-0135", reason: "PEP screening \u2014 extended family member listed as PEP." },
};

const MOCK_TIMELINE: TimelineEvent[] = [
  { event: "Case opened", actor: "System", ts: "2026-05-18T14:33:02Z", tone: "info" },
  { event: "Assigned to Naledi Nkosi", actor: "Arthur Manena", ts: "2026-05-18T14:42:00Z", tone: "info" },
  { event: "Comment added", actor: "Naledi Nkosi", ts: "2026-05-18T15:11:00Z", tone: "pending" },
  { event: "Escalated to compliance", actor: "Arthur Manena", ts: "2026-05-18T16:42:00Z", tone: "danger" },
  { event: "TransUnion confirmation requested", actor: "System", ts: "2026-05-18T16:43:12Z", tone: "info" },
];

const MOCK_COMMENTS: Comment[] = [
  { author: "System", text: "Case auto-created from verification VG-2026-0140.", ts: "2026-05-18T14:33:02Z", system: true },
  { author: "Naledi Nkosi", text: "Reviewing the credit listing; will request supporting docs from subject.", ts: "2026-05-18T15:11:00Z" },
  { author: "Arthur Manena", text: "Escalating to compliance \u2014 confirm with TransUnion before resolving.", ts: "2026-05-18T16:42:00Z" },
];

const RISK_FACTORS: RiskFactor[] = [
  { name: "ID verification (DHA)", value: "\u2713 Pass", contrib: -5 },
  { name: "Sanctions / PEP", value: "\u2713 Clear", contrib: -10 },
  { name: "Biometric match", value: "\u2713 0.94", contrib: -8 },
  { name: "Credit bureau", value: "\u2717 Hard fail", contrib: +35 },
  { name: "Adverse listings (12mo)", value: "\u26A0 2 found", contrib: +25 },
  { name: "Address consistency", value: "\u26A0 Mismatch", contrib: +15 },
  { name: "Velocity flags", value: "\u2713 Normal", contrib: -8 },
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

const TONE_COLORS: Record<string, string> = {
  info: "#00B3D9",
  danger: "#E23D36",
  warning: "#C28B0B",
  success: "#2C974B",
  pending: "#4F5B67",
};

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

/* ── Risk gauge (half-circle SVG) ──────────────────────── */

function RiskGauge({ score }: { score: number }) {
  const color =
    score >= 80 ? "#E23D36" : score >= 60 ? "#C28B0B" : score >= 30 ? "#00B3D9" : "#2C974B";
  const r = 50;
  const c = Math.PI * r;

  return (
    <div className="relative" style={{ width: 130, height: 70 }}>
      <svg width={130} height={130}>
        <path
          d={`M 15,65 A ${r} ${r} 0 0 1 115,65`}
          fill="none"
          stroke="#F2F3F3"
          strokeWidth="9"
          strokeLinecap="round"
        />
        <path
          d={`M 15,65 A ${r} ${r} 0 0 1 115,65`}
          fill="none"
          stroke={color}
          strokeWidth="9"
          strokeLinecap="round"
          strokeDasharray={`${(score / 100) * c} ${c}`}
        />
      </svg>
      <div className="absolute top-[22px] left-0 right-0 text-center">
        <div className="text-[32px] font-bold text-text leading-none">{score}</div>
        <div className="text-[9px] text-text-muted uppercase tracking-[0.08em] mt-0.5">
          Composite
        </div>
      </div>
    </div>
  );
}

/* ── Sub-components ────────────────────────────────────── */

function ActivityTimeline({ events }: { events: TimelineEvent[] }) {
  return (
    <div className="flex flex-col gap-2.5 relative">
      <div className="absolute left-[5px] top-1.5 bottom-1.5 w-px bg-[#e9ebed]" />
      {events.map((t, i) => (
        <div key={i} className="flex gap-2.5 relative">
          <span
            className="w-[11px] h-[11px] rounded-full bg-surface border-2 shrink-0 z-10 mt-[3px]"
            style={{ borderColor: TONE_COLORS[t.tone] }}
          />
          <div className="flex-1 min-w-0">
            <div className="text-xs text-text">{t.event}</div>
            <div className="text-[10px] text-text-muted mt-0.5">
              {t.actor} &middot;{" "}
              {new Date(t.ts).toLocaleString("en-ZA", {
                dateStyle: "short",
                timeStyle: "short",
              })}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

function CommentsSection({
  comments,
  onAddComment,
}: {
  comments: Comment[];
  onAddComment: (text: string) => void;
}) {
  const [text, setText] = useState("");

  const handleSubmit = () => {
    if (!text.trim()) return;
    onAddComment(text.trim());
    setText("");
  };

  return (
    <Card>
      <CardHeader>
        <div className="text-sm font-semibold text-text">
          Comments ({comments.filter((c) => !c.system).length})
        </div>
      </CardHeader>
      <CardBody>
        <div className="flex flex-col gap-2.5">
          {comments.map((cm, i) => (
            <div key={i} className="flex gap-2.5">
              <Avatar name={cm.system ? "VeriGate" : cm.author} size={28} />
              <div
                className={`flex-1 rounded-md border p-2.5 ${
                  cm.system
                    ? "bg-[#F8FAFC] border-[#e9ebed]"
                    : "bg-surface border-border"
                }`}
              >
                <div className="flex justify-between mb-1">
                  <span
                    className={`text-[11px] font-semibold ${
                      cm.system ? "text-text-muted" : "text-text"
                    }`}
                  >
                    {cm.author}
                  </span>
                  <span className="text-[10px] text-text-muted">
                    {new Date(cm.ts).toLocaleString("en-ZA", {
                      dateStyle: "medium",
                      timeStyle: "short",
                    })}
                  </span>
                </div>
                <div className="text-xs text-text leading-relaxed">{cm.text}</div>
              </div>
            </div>
          ))}

          {/* New comment input */}
          <div className="flex gap-2 pt-2 border-t border-[#f1f5f9]">
            <Avatar name="Arthur Manena" size={28} />
            <input
              value={text}
              onChange={(e) => setText(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
              placeholder="Add a comment\u2026"
              className="aws-input flex-1 px-2.5 py-1.5 text-xs rounded"
            />
            <Button
              variant="primary"
              size="sm"
              icon={<Send size={12} />}
              onClick={handleSubmit}
              disabled={!text.trim()}
            >
              Comment
            </Button>
          </div>
        </div>
      </CardBody>
    </Card>
  );
}

function RiskFactorsPanel({ factors, score }: { factors: RiskFactor[]; score: number }) {
  return (
    <Card>
      <CardHeader>
        <div>
          <div className="text-sm font-semibold text-text">Risk factors</div>
          <div className="text-[11px] text-text-muted mt-0.5">
            {factors.length} signals contribute to the composite score
          </div>
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
              className="w-[110px] font-mono text-[11px]"
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

function AiSummaryCard({ caseData }: { caseData: CaseData }) {
  return (
    <div className="bg-gradient-to-br from-[rgba(0,179,217,0.06)] to-[rgba(0,179,217,0.02)] border border-[rgba(0,179,217,0.25)] rounded-aws-container p-3.5">
      <div className="flex items-center gap-2 mb-2">
        <span className="w-[22px] h-[22px] rounded-md bg-[rgba(0,179,217,0.15)] inline-flex items-center justify-center text-accent text-[11px] font-bold">
          AI
        </span>
        <span className="text-xs font-semibold text-[#0099bb] uppercase tracking-[0.08em]">
          AI summary
        </span>
        <span className="ml-auto text-[10px] text-text-muted font-mono">
          Generated 2m ago
        </span>
      </div>
      <div className="text-[13px] text-text leading-relaxed">
        This case was opened because the credit bureau returned a{" "}
        <b>hard fail</b> with two adverse listings within the last 12 months.
        Identity (DHA), sanctions, and biometrics all passed cleanly. The
        composite risk score is{" "}
        <b style={{ color: "#E23D36" }}>{caseData.riskScore}</b> &mdash; driven
        primarily by credit findings (+60) and address inconsistency (+15).{" "}
        <b>Recommended action:</b> request supporting documents from subject and
        verify with TransUnion before approving onboard.
      </div>
    </div>
  );
}

/* ── Resolution panel ──────────────────────────────────── */

function ResolutionPanel({
  status,
  onStatusChange,
  onResolve,
}: {
  status: CaseStatus;
  onStatusChange: (status: CaseStatus) => void;
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
              onChange={(e) => onStatusChange(e.target.value as CaseStatus)}
              className="aws-input w-full px-2.5 py-1.5 text-xs rounded"
            >
              <option value="OPEN">Open</option>
              <option value="IN_REVIEW">In Review</option>
              <option value="ESCALATED">Escalated</option>
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
              placeholder="Provide the reason for this resolution\u2026"
              className="aws-input w-full px-2.5 py-1.5 text-xs rounded min-h-[80px] resize-y"
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
                Resolve case
              </Button>
            ) : (
              <Button
                variant="primary"
                size="sm"
                onClick={() => onStatusChange("OPEN")}
              >
                Reopen case
              </Button>
            )}
          </div>
        </div>
      </CardBody>
    </Card>
  );
}

/* ── Main page component ───────────────────────────────── */

export function CaseDetailPage({ caseId }: { caseId: string }) {
  const router = useRouter();
  const caseData = CASES_LOOKUP[caseId] ?? null;

  const [status, setStatus] = useState<CaseStatus>(caseData?.status ?? "OPEN");
  const [comments, setComments] = useState<Comment[]>(MOCK_COMMENTS);

  if (!caseData) {
    return (
      <div className="text-center py-20 text-text-muted">
        Case not found.
      </div>
    );
  }

  const statusInfo = STATUS_MAP[status];
  const priorityInfo = PRIORITY_MAP[caseData.priority];

  const handleAddComment = (text: string) => {
    setComments((prev) => [
      ...prev,
      {
        author: "Arthur Manena (you)",
        text,
        ts: new Date().toISOString(),
      },
    ]);
  };

  const handleResolve = (reason: string) => {
    setStatus("RESOLVED");
    setComments((prev) => [
      ...prev,
      {
        author: "System",
        text: `Case resolved: ${reason}`,
        ts: new Date().toISOString(),
        system: true,
      },
    ]);
  };

  return (
    <div className="space-y-4">
      {/* Back link */}
      <button
        onClick={() => router.push("/cases")}
        className="inline-flex items-center gap-1 text-accent text-xs font-medium hover:underline"
      >
        <ArrowLeft size={12} /> Back to cases
      </button>

      {/* Header card */}
      <div className="console-card overflow-hidden">
        <div className="flex h-[3px]">
          <div className="flex-[3] bg-[#E23D36]" />
          <div className="flex-[5] bg-primary" />
          <div className="flex-[2] bg-accent" />
        </div>
        <div className="p-5 flex flex-wrap justify-between items-start gap-4">
          <div className="flex gap-4 items-center">
            <Avatar name={caseData.subjectName} size={56} />
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <span className="font-mono text-xs text-accent">
                  {caseData.short}
                </span>
                <Badge variant={statusInfo.variant}>{statusInfo.label}</Badge>
                <span
                  className="text-[11px] inline-flex items-center gap-1"
                  style={{ color: priorityInfo.color }}
                >
                  <span
                    className="w-1.5 h-1.5 rounded-full"
                    style={{ background: priorityInfo.color }}
                  />
                  {priorityInfo.label} priority
                </span>
              </div>
              <div className="text-xl font-semibold text-text">
                {caseData.subjectName}
              </div>
              <div className="text-xs text-text-muted mt-0.5 font-mono">
                {caseData.subjectId}
              </div>
            </div>
          </div>

          <div className="flex items-center gap-4">
            <RiskGauge score={caseData.riskScore} />
            <div className="flex gap-2">
              <Button variant="secondary" size="sm">
                Reassign
              </Button>
              {status !== "RESOLVED" ? (
                <Button
                  variant="cta"
                  size="sm"
                  onClick={() => setStatus("RESOLVED")}
                >
                  Resolve
                </Button>
              ) : (
                <Button
                  variant="primary"
                  size="sm"
                  onClick={() => setStatus("OPEN")}
                >
                  Reopen
                </Button>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Two-column body */}
      <div className="grid grid-cols-1 lg:grid-cols-[1fr_340px] gap-4">
        {/* Main column */}
        <div className="space-y-3.5">
          <AiSummaryCard caseData={caseData} />
          <RiskFactorsPanel factors={RISK_FACTORS} score={caseData.riskScore} />
          <CommentsSection comments={comments} onAddComment={handleAddComment} />
          <ResolutionPanel
            status={status}
            onStatusChange={setStatus}
            onResolve={handleResolve}
          />
        </div>

        {/* Sidebar */}
        <div className="space-y-3">
          {/* Linked records */}
          <Card className="p-[18px]">
            <div className="text-[11px] text-text-muted uppercase tracking-[0.06em] font-semibold mb-2.5">
              Linked records
            </div>
            <div className="flex flex-col gap-1.5">
              {[
                { label: "Verification", id: caseData.verificationId },
                { label: "Workflow", id: "WF-onboard-standard-v3" },
                { label: "Subject ID", id: caseData.subjectId },
              ].map((r) => (
                <div
                  key={r.label}
                  className="flex items-center gap-2 p-1.5 rounded bg-[#F8FAFC] text-[11px]"
                >
                  <span className="text-text-muted min-w-[70px]">
                    {r.label}
                  </span>
                  <span className="flex-1 font-mono text-accent">{r.id}</span>
                </div>
              ))}
            </div>
          </Card>

          {/* Activity timeline */}
          <Card className="p-[18px]">
            <div className="text-[11px] text-text-muted uppercase tracking-[0.06em] font-semibold mb-3">
              Activity
            </div>
            <ActivityTimeline events={MOCK_TIMELINE} />
          </Card>

          {/* SLA */}
          <Card className="p-[18px]">
            <div className="flex justify-between items-center mb-2.5">
              <span className="text-[11px] text-text-muted uppercase tracking-[0.06em] font-semibold">
                Resolution SLA
              </span>
              <Badge variant="warning" size="sm">
                22h left
              </Badge>
            </div>
            <div className="h-1.5 bg-[#F2F3F3] rounded-sm overflow-hidden mb-2">
              <div className="w-[54%] h-full bg-[#C28B0B]" />
            </div>
            <div className="text-[11px] text-text-muted leading-relaxed">
              Target: 48h &middot; Elapsed: 26h &middot; 6h to escalation tier
              2.
            </div>
          </Card>

          {/* POPIA notice */}
          <div className="bg-[#F8FAFC] border border-dashed border-[#CBD5E1] rounded-aws-container p-3 text-[11px] text-text-muted leading-relaxed">
            <b className="text-primary">POPIA s.18:</b> Subject was notified of
            this verification at consent capture. All comments and access events
            are logged.
          </div>
        </div>
      </div>
    </div>
  );
}
