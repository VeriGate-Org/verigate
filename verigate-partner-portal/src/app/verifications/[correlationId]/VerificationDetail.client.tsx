"use client";

import Link from "next/link";
import { ArrowLeft, Clock, CheckCircle2, XCircle, AlertTriangle, Activity } from "lucide-react";
import { Badge } from "@/components/ui/Badge";
import { useVerificationDetail } from "@/lib/hooks/useVerification";
import type { VerificationStatus, VerificationEvent } from "@/lib/types";

interface Props {
  correlationId: string;
}

export default function VerificationDetail({ correlationId }: Props) {
  const { data, isLoading, error, refetch } = useVerificationDetail(correlationId);

  if (isLoading) {
    return (
      <div className="space-y-6">
        <BackLink />
        <div className="console-card">
          <div className="console-card-body py-12 text-center text-sm text-text-muted">
            Loading verification details…
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="space-y-6">
        <BackLink />
        <div className="console-card border-danger/40 bg-danger/5 text-sm text-danger">
          <div className="console-card-body flex items-center justify-between">
            <span>{error instanceof Error ? error.message : "Failed to load verification"}</span>
            <button
              onClick={() => refetch()}
              className="rounded border border-danger/40 px-3 py-1 text-xs font-medium hover:bg-danger/10"
            >
              Retry
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (!data || !data.verification) {
    return (
      <div className="space-y-6">
        <BackLink />
        <div className="console-card">
          <div className="console-card-body py-12 text-center">
            <p className="text-sm text-text-muted">Verification not found</p>
            <p className="text-xs text-text-muted mt-1">
              No verification with ID <span className="font-mono font-medium text-text">{correlationId}</span> was found.
            </p>
          </div>
        </div>
      </div>
    );
  }

  const { verification: v, events } = data;

  return (
    <div className="space-y-6">
      <BackLink />

      {/* Header */}
      <div className="flex flex-col gap-1">
        <div className="flex items-center gap-3">
          <h1 className="text-xl font-semibold text-text font-mono">{v.correlationId}</h1>
          <StatusBadge status={v.status} />
        </div>
        <p className="text-sm text-text-muted">
          {v.type} verification via {v.provider || "unknown provider"}
        </p>
      </div>

      {/* Details grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {/* Left: Core details */}
        <div className="console-card lg:col-span-2">
          <div className="console-card-header">
            <h2 className="text-sm font-semibold text-text">Details</h2>
          </div>
          <div className="console-card-body">
            <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4 text-sm">
              <DetailRow label="Correlation ID" value={v.correlationId} mono />
              <DetailRow label="Partner ID" value={v.partnerId} />
              <DetailRow label="Type" value={v.type} />
              <DetailRow label="Provider" value={v.provider || "—"} />
              <DetailRow label="Status" value={<StatusBadge status={v.status} />} />
              <DetailRow label="Started" value={formatFull(v.startedAt)} />
              <DetailRow label="Completed" value={v.completedAt ? formatFull(v.completedAt) : "—"} />
              <DetailRow label="Duration" value={formatDuration(v.durationMs)} />

              {v.workflowId && (
                <>
                  <DetailRow label="Workflow" value={v.workflowName || v.workflowId} />
                  <DetailRow label="Policy version" value={v.policyVersion || "—"} />
                </>
              )}
            </dl>
          </div>
        </div>

        {/* Right: Risk assessment */}
        <div className="console-card">
          <div className="console-card-header">
            <h2 className="text-sm font-semibold text-text">Risk assessment</h2>
          </div>
          <div className="console-card-body space-y-4">
            {v.compositeRiskScore != null ? (
              <>
                <div className="text-center">
                  <div className={`text-3xl font-bold ${riskScoreColor(v.compositeRiskScore)}`}>
                    {v.compositeRiskScore}%
                  </div>
                  <div className="text-xs text-text-muted mt-1">Composite risk score</div>
                </div>

                {v.riskTier && (
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-text-muted">Risk tier</span>
                    <span className={`font-medium ${riskTierColor(v.riskTier)}`}>
                      {v.riskTier.replace("_", " ")}
                    </span>
                  </div>
                )}

                {v.riskDecision && (
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-text-muted">Decision</span>
                    <span className="font-medium text-text">{v.riskDecision}</span>
                  </div>
                )}

                {v.decisionReason && (
                  <div className="text-sm">
                    <span className="text-text-muted">Reason</span>
                    <p className="mt-1 text-text">{v.decisionReason}</p>
                  </div>
                )}

                {v.individualScores && v.individualScores.length > 0 && (
                  <div className="space-y-2 pt-2 border-t border-border">
                    <div className="text-xs font-medium text-text-muted uppercase tracking-wide">
                      Individual scores
                    </div>
                    {v.individualScores.map((score, i) => (
                      <div key={i} className="flex items-center justify-between text-sm">
                        <span className="text-text">{score.verificationType}</span>
                        <div className="flex items-center gap-2">
                          <span className={`font-medium ${riskScoreColor(score.confidenceScore)}`}>
                            {score.confidenceScore}%
                          </span>
                          <span className="text-xs text-text-muted">{score.outcome}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </>
            ) : (
              <div className="text-center py-4 text-xs text-text-muted">
                No risk assessment data available
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Event timeline */}
      <div className="console-card">
        <div className="console-card-header">
          <h2 className="text-sm font-semibold text-text">Event timeline</h2>
          <span className="text-xs text-text-muted">{events.length} events</span>
        </div>
        <div className="console-card-body">
          {events.length === 0 ? (
            <div className="py-4 text-center text-xs text-text-muted">No events recorded</div>
          ) : (
            <div className="relative space-y-0">
              {events.map((event, i) => (
                <div key={i} className="flex gap-4">
                  {/* Timeline line + dot */}
                  <div className="flex flex-col items-center">
                    <div className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-full ${eventIconBg(event.eventType)}`}>
                      <EventIcon eventType={event.eventType} />
                    </div>
                    {i < events.length - 1 && (
                      <div className="w-px flex-1 bg-border" />
                    )}
                  </div>

                  {/* Content */}
                  <div className="pb-6 min-w-0">
                    <div className="flex items-baseline gap-2">
                      <span className="text-sm font-medium text-text">{eventLabel(event.eventType)}</span>
                      {event.stepSequence && (
                        <span className="text-xs text-text-muted">Step {event.stepSequence}</span>
                      )}
                    </div>
                    <div className="text-xs text-text-muted mt-0.5">
                      {formatFull(event.ts)} &middot; {event.source}
                    </div>
                    {event.detail != null && (
                      <div className="mt-1.5 rounded border border-border bg-background px-3 py-2 text-xs text-text-muted font-mono whitespace-pre-wrap">
                        {typeof event.detail === "string" ? event.detail : JSON.stringify(event.detail, null, 2)}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

// ── Sub-components & helpers ────────────────────────────────────────

function BackLink() {
  return (
    <Link
      href="/verifications"
      className="inline-flex items-center gap-1.5 text-sm text-text-muted hover:text-text transition-colors"
    >
      <ArrowLeft className="h-4 w-4" />
      Back to verifications
    </Link>
  );
}

function DetailRow({ label, value, mono }: { label: string; value: React.ReactNode; mono?: boolean }) {
  return (
    <div>
      <dt className="text-xs text-text-muted">{label}</dt>
      <dd className={`mt-0.5 text-text ${mono ? "font-mono" : ""}`}>{value}</dd>
    </div>
  );
}

function StatusBadge({ status }: { status: VerificationStatus }) {
  const variantMap: Record<VerificationStatus, "success" | "warning" | "danger" | "info" | "pending"> = {
    success: "success",
    completed: "success",
    in_progress: "info",
    pending: "pending",
    soft_fail: "warning",
    transient_error: "warning",
    hard_fail: "danger",
    permanent_failure: "danger",
  };
  const labelMap: Record<VerificationStatus, string> = {
    success: "Success",
    completed: "Completed",
    in_progress: "In Progress",
    pending: "Pending",
    soft_fail: "Soft Fail",
    transient_error: "Transient Error",
    hard_fail: "Hard Fail",
    permanent_failure: "Permanent Failure",
  };
  return <Badge variant={variantMap[status]} size="sm">{labelMap[status]}</Badge>;
}

function EventIcon({ eventType }: { eventType: VerificationEvent["eventType"] }) {
  const cls = "h-4 w-4";
  switch (eventType) {
    case "VerificationRequested": return <Activity className={`${cls} text-blue-600`} />;
    case "VerificationSucceeded": return <CheckCircle2 className={`${cls} text-green-600`} />;
    case "VerificationSoftFail": return <AlertTriangle className={`${cls} text-amber-600`} />;
    case "VerificationHardFail": return <XCircle className={`${cls} text-red-600`} />;
    case "VerificationSystemOutage": return <XCircle className={`${cls} text-red-600`} />;
    default: return <Clock className={`${cls} text-gray-500`} />;
  }
}

function eventIconBg(eventType: VerificationEvent["eventType"]): string {
  switch (eventType) {
    case "VerificationRequested": return "bg-blue-50";
    case "VerificationSucceeded": return "bg-green-50";
    case "VerificationSoftFail": return "bg-amber-50";
    case "VerificationHardFail": return "bg-red-50";
    case "VerificationSystemOutage": return "bg-red-50";
    default: return "bg-gray-50";
  }
}

function eventLabel(eventType: VerificationEvent["eventType"]): string {
  switch (eventType) {
    case "VerificationRequested": return "Verification requested";
    case "VerificationSucceeded": return "Verification succeeded";
    case "VerificationSoftFail": return "Soft failure";
    case "VerificationHardFail": return "Hard failure";
    case "VerificationSystemOutage": return "System outage";
    case "DomainSpecific": return "Domain event";
    default: return eventType;
  }
}

function formatFull(iso: string): string {
  try {
    const d = new Date(iso);
    return `${d.toLocaleDateString()} ${d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", second: "2-digit" })}`;
  } catch {
    return iso;
  }
}

function formatDuration(ms?: number): string {
  if (!ms && ms !== 0) return "—";
  if (ms < 1000) return `${ms} ms`;
  const seconds = ms / 1000;
  if (seconds < 60) return `${seconds.toFixed(1)} s`;
  const minutes = Math.floor(seconds / 60);
  const remain = Math.round(seconds % 60);
  return `${minutes}m ${remain}s`;
}

function riskScoreColor(score: number): string {
  if (score >= 80) return "text-green-600";
  if (score >= 50) return "text-amber-600";
  return "text-red-600";
}

function riskTierColor(tier: string): string {
  if (tier.includes("LOW")) return "text-green-600";
  if (tier.includes("MEDIUM")) return "text-amber-600";
  return "text-red-600";
}
