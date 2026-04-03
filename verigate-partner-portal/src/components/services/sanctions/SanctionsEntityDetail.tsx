"use client";

import { useCallback, useEffect, useState } from "react";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { generateEntityDetail, generateAdjacentEntities } from "@/lib/mock-services";
import type {
  EntityDetail,
  AdjacentEntity,
  DispositionAction,
} from "@/lib/types/sanctions-screening";
import {
  ArrowLeft,
  Shield,
  User,
  Landmark,
  Link2,
  Database,
  Loader2,
  CheckCircle2,
  XCircle,
  AlertTriangle,
} from "lucide-react";

interface SanctionsEntityDetailProps {
  entityId: string;
  onBack: () => void;
}

export default function SanctionsEntityDetail({ entityId, onBack }: SanctionsEntityDetailProps) {
  const [entity, setEntity] = useState<EntityDetail | null>(null);
  const [adjacentEntities, setAdjacentEntities] = useState<AdjacentEntity[]>([]);
  const [loading, setLoading] = useState(true);
  const [disposition, setDisposition] = useState<DispositionAction | null>(null);
  const [reason, setReason] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    let cancelled = false;

    async function fetchData() {
      setLoading(true);
      try {
        const [detail, adjacent] = await Promise.all([
          generateEntityDetail(entityId),
          generateAdjacentEntities(entityId),
        ]);
        if (!cancelled) {
          setEntity(detail);
          setAdjacentEntities(adjacent);
        }
      } catch {
        // Entity detail fetch failed silently
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    fetchData();
    return () => {
      cancelled = true;
    };
  }, [entityId]);

  const handleSubmitDisposition = useCallback(async () => {
    if (!disposition || !reason.trim()) return;
    setSubmitting(true);
    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 800));
    setSubmitting(false);
    setSubmitted(true);
  }, [disposition, reason]);

  if (loading) {
    return <LoadingSkeleton onBack={onBack} />;
  }

  if (!entity) {
    return (
      <div className="space-y-4">
        <button
          onClick={onBack}
          className="flex items-center gap-1.5 text-sm text-text-muted hover:text-text transition-colors"
        >
          <ArrowLeft className="h-4 w-4" />
          Back to results
        </button>
        <div className="console-card">
          <div className="console-card-body text-center py-8">
            <p className="text-sm text-text-muted">Entity not found.</p>
          </div>
        </div>
      </div>
    );
  }

  const scorePercent = Math.round(entity.score * 100);
  const scoreColor =
    scorePercent >= 80 ? "bg-danger" : scorePercent >= 50 ? "bg-warning" : "bg-success";

  return (
    <div className="space-y-6">
      {/* Back button */}
      <button
        onClick={onBack}
        className="flex items-center gap-1.5 text-sm text-text-muted hover:text-text transition-colors"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to results
      </button>

      {/* Entity Header */}
      <div className="console-card">
        <div className="console-card-body space-y-3 p-4">
          <div className="flex items-start justify-between gap-4">
            <div className="space-y-1">
              <h1 className="text-lg font-semibold text-text">{entity.caption}</h1>
              <div className="flex items-center gap-2 flex-wrap">
                <span className="inline-flex items-center rounded-full bg-surface-secondary px-2.5 py-0.5 text-xs font-medium text-text-muted">
                  {entity.schema}
                </span>
                {entity.isSanctioned && (
                  <span className="inline-flex items-center rounded-full bg-danger/10 px-2.5 py-0.5 text-xs font-medium text-danger">
                    Sanctioned
                  </span>
                )}
                {entity.isPep && (
                  <span className="inline-flex items-center rounded-full bg-warning/10 px-2.5 py-0.5 text-xs font-medium text-warning">
                    PEP
                  </span>
                )}
              </div>
            </div>
            <div className="text-right shrink-0">
              <div className="text-2xl font-bold text-text">{scorePercent}%</div>
              <div className="text-xs text-text-muted">Match score</div>
            </div>
          </div>

          {/* Score bar */}
          <div className="space-y-1">
            <div className="h-2 w-full rounded-full bg-surface-secondary overflow-hidden">
              <div
                className={`h-full rounded-full transition-all ${scoreColor}`}
                style={{ width: `${scorePercent}%` }}
              />
            </div>
          </div>

          <div className="text-xs text-text-muted font-mono">ID: {entity.id}</div>
        </div>
      </div>

      {/* Identity Section */}
      <DetailCard title="Identity" icon={<User className="h-4 w-4" />}>
        <div className="divide-y divide-border">
          {/* Names and Aliases */}
          {entity.aliases.length > 0 && (
            <div className="px-4 py-3">
              <div className="text-xs font-medium text-text-muted mb-1.5">Names / Aliases</div>
              <div className="flex flex-wrap gap-1.5">
                {entity.aliases.map((alias, i) => (
                  <span
                    key={i}
                    className="inline-flex items-center rounded bg-surface-secondary px-2 py-0.5 text-xs text-text"
                  >
                    {alias}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* DOB */}
          {entity.properties.birthDate && (
            <DetailRow label="Date of birth" value={entity.properties.birthDate.join(", ")} />
          )}

          {/* Nationality */}
          {entity.properties.nationality && (
            <DetailRow label="Nationality" value={entity.properties.nationality.join(", ")} />
          )}

          {/* Gender */}
          {entity.properties.gender && (
            <DetailRow label="Gender" value={entity.properties.gender.join(", ")} />
          )}

          {/* Identifiers */}
          {entity.identifiers.length > 0 && (
            <div className="px-4 py-3">
              <div className="text-xs font-medium text-text-muted mb-2">Identifiers</div>
              <div className="space-y-1.5">
                {entity.identifiers.map((ident, i) => (
                  <div key={i} className="flex items-center justify-between text-sm">
                    <span className="text-text-muted">
                      {ident.type}
                      {ident.country ? ` (${ident.country})` : ""}
                    </span>
                    <span className="font-mono text-text">{ident.value}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </DetailCard>

      {/* Sanctions Designations Section */}
      {entity.sanctions.length > 0 && (
        <DetailCard title="Sanctions Designations" icon={<Shield className="h-4 w-4" />}>
          <div className="space-y-3 p-4">
            {entity.sanctions.map((sanction, i) => {
              const statusStyle =
                sanction.status === "active"
                  ? "bg-danger/10 text-danger"
                  : sanction.status === "delisted"
                    ? "bg-success/10 text-success"
                    : "bg-surface-secondary text-text-muted";

              return (
                <div key={i} className="rounded border border-border p-3 space-y-2">
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <div className="text-sm font-medium text-text">{sanction.program}</div>
                      <div className="text-xs text-text-muted">{sanction.authority}</div>
                    </div>
                    <span
                      className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium capitalize ${statusStyle}`}
                    >
                      {sanction.status}
                    </span>
                  </div>
                  {sanction.reason && (
                    <p className="text-xs text-text-muted">{sanction.reason}</p>
                  )}
                  <div className="flex gap-4 text-xs text-text-muted">
                    {sanction.startDate && <span>Listed: {sanction.startDate}</span>}
                    {sanction.endDate && <span>Delisted: {sanction.endDate}</span>}
                  </div>
                </div>
              );
            })}
          </div>
        </DetailCard>
      )}

      {/* PEP Positions Section */}
      {entity.positions.length > 0 && (
        <DetailCard title="PEP Positions" icon={<Landmark className="h-4 w-4" />}>
          <div className="divide-y divide-border">
            {entity.positions.map((pos, i) => (
              <div key={i} className="px-4 py-3 space-y-1">
                <div className="text-sm font-medium text-text">{pos.role}</div>
                <div className="text-xs text-text-muted">{pos.organization}</div>
                <div className="flex gap-4 text-xs text-text-muted">
                  {pos.country && <span>{pos.country}</span>}
                  {pos.startDate && <span>From: {pos.startDate}</span>}
                  {pos.endDate && <span>To: {pos.endDate}</span>}
                </div>
              </div>
            ))}
          </div>
        </DetailCard>
      )}

      {/* Related Entities Section */}
      {adjacentEntities.length > 0 && (
        <DetailCard title="Related Entities" icon={<Link2 className="h-4 w-4" />}>
          <div className="divide-y divide-border">
            {adjacentEntities.map((adj) => (
              <div key={adj.id} className="px-4 py-3">
                <div className="flex items-center justify-between gap-2">
                  <div>
                    <div className="text-sm font-medium text-text">{adj.caption}</div>
                    <div className="text-xs text-text-muted font-mono">{adj.id}</div>
                  </div>
                  <div className="text-right shrink-0">
                    <span className="inline-flex items-center rounded-full bg-surface-secondary px-2 py-0.5 text-xs text-text-muted">
                      {adj.schema}
                    </span>
                  </div>
                </div>
                <div className="mt-1 text-xs text-text-muted">{adj.relationship}</div>
              </div>
            ))}
          </div>
        </DetailCard>
      )}

      {/* Data Provenance Section */}
      <DetailCard title="Data Provenance" icon={<Database className="h-4 w-4" />}>
        <div className="divide-y divide-border">
          {entity.firstSeen && <DetailRow label="First seen" value={entity.firstSeen} />}
          {entity.lastChange && <DetailRow label="Last changed" value={entity.lastChange} />}
          {entity.datasets.length > 0 && (
            <div className="px-4 py-3">
              <div className="text-xs font-medium text-text-muted mb-1.5">Datasets</div>
              <div className="flex flex-wrap gap-1.5">
                {entity.datasets.map((ds, i) => (
                  <span
                    key={i}
                    className="inline-flex items-center rounded bg-surface-secondary px-2 py-0.5 text-xs text-text"
                  >
                    {ds}
                  </span>
                ))}
              </div>
            </div>
          )}
          {entity.referents.length > 0 && (
            <div className="px-4 py-3">
              <div className="text-xs font-medium text-text-muted mb-1.5">Referents</div>
              <div className="space-y-1">
                {entity.referents.map((ref, i) => (
                  <div key={i} className="text-xs font-mono text-text">
                    {ref}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </DetailCard>

      {/* Disposition Form */}
      <div className="console-card">
        <div className="console-card-header">
          <span className="text-sm font-semibold text-text">Disposition</span>
        </div>
        <div className="console-card-body p-4 space-y-4">
          {submitted ? (
            <div className="flex items-center gap-2 rounded border border-success/40 bg-success/5 px-4 py-3">
              <CheckCircle2 className="h-4 w-4 text-success" />
              <span className="text-sm text-success">
                Disposition submitted successfully.
              </span>
            </div>
          ) : (
            <>
              <fieldset className="space-y-2">
                <legend className="text-sm font-medium text-text mb-2">Action</legend>
                <label className="flex items-center gap-2 text-sm text-text cursor-pointer">
                  <input
                    type="radio"
                    name="disposition"
                    value="CONFIRMED_MATCH"
                    checked={disposition === "CONFIRMED_MATCH"}
                    onChange={() => setDisposition("CONFIRMED_MATCH")}
                    className="text-cta"
                  />
                  <CheckCircle2 className="h-3.5 w-3.5 text-danger" />
                  Confirm Match
                </label>
                <label className="flex items-center gap-2 text-sm text-text cursor-pointer">
                  <input
                    type="radio"
                    name="disposition"
                    value="FALSE_POSITIVE"
                    checked={disposition === "FALSE_POSITIVE"}
                    onChange={() => setDisposition("FALSE_POSITIVE")}
                    className="text-cta"
                  />
                  <XCircle className="h-3.5 w-3.5 text-success" />
                  False Positive
                </label>
                <label className="flex items-center gap-2 text-sm text-text cursor-pointer">
                  <input
                    type="radio"
                    name="disposition"
                    value="ESCALATED"
                    checked={disposition === "ESCALATED"}
                    onChange={() => setDisposition("ESCALATED")}
                    className="text-cta"
                  />
                  <AlertTriangle className="h-3.5 w-3.5 text-warning" />
                  Escalate
                </label>
              </fieldset>

              <div className="space-y-1.5">
                <label htmlFor="disposition-reason" className="text-sm font-medium text-text">
                  Reason
                </label>
                <textarea
                  id="disposition-reason"
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                  placeholder="Provide justification for the selected action..."
                  rows={3}
                  className="aws-input w-full resize-none"
                />
              </div>

              <div className="flex justify-end">
                <Button
                  variant="cta"
                  size="md"
                  disabled={!disposition || !reason.trim() || submitting}
                  loading={submitting}
                  onClick={handleSubmitDisposition}
                >
                  Submit disposition
                </Button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

// ----- Internal Components -----

function DetailCard({
  title,
  icon,
  children,
}: {
  title: string;
  icon?: React.ReactNode;
  children: React.ReactNode;
}) {
  return (
    <div className="console-card">
      <div className="console-card-header">
        <div className="flex items-center gap-2">
          {icon}
          <span className="text-sm font-semibold text-text">{title}</span>
        </div>
      </div>
      <div className="console-card-body">
        <div className="border border-border rounded overflow-hidden">{children}</div>
      </div>
    </div>
  );
}

function DetailRow({ label, value }: { label: string; value?: string }) {
  if (!value) return null;
  return (
    <div className="flex items-center justify-between px-4 py-2.5 text-sm">
      <span className="text-text-muted">{label}</span>
      <span className="font-medium text-text">{value}</span>
    </div>
  );
}

function LoadingSkeleton({ onBack }: { onBack: () => void }) {
  return (
    <div className="space-y-6">
      <button
        onClick={onBack}
        className="flex items-center gap-1.5 text-sm text-text-muted hover:text-text transition-colors"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to results
      </button>

      {/* Header skeleton */}
      <div className="console-card">
        <div className="console-card-body p-4 space-y-3">
          <div className="flex items-start justify-between">
            <div className="space-y-2">
              <Skeleton className="h-6 w-48" />
              <div className="flex gap-2">
                <Skeleton className="h-5 w-16 rounded-full" />
                <Skeleton className="h-5 w-20 rounded-full" />
              </div>
            </div>
            <div className="text-right">
              <Skeleton className="h-8 w-16 ml-auto" />
              <Skeleton className="h-3 w-20 mt-1" />
            </div>
          </div>
          <Skeleton className="h-2 w-full rounded-full" />
          <Skeleton className="h-3 w-40" />
        </div>
      </div>

      {/* Section skeletons */}
      {[...Array(4)].map((_, i) => (
        <div key={i} className="console-card">
          <div className="console-card-header">
            <Skeleton className="h-5 w-32" />
          </div>
          <div className="console-card-body p-4">
            <div className="border border-border rounded overflow-hidden divide-y divide-border">
              {[...Array(3)].map((_, j) => (
                <div key={j} className="flex items-center justify-between px-4 py-2.5">
                  <Skeleton className="h-4 w-28" />
                  <Skeleton className="h-4 w-20" />
                </div>
              ))}
            </div>
          </div>
        </div>
      ))}

      {/* Loading indicator */}
      <div className="flex items-center justify-center gap-2 py-4">
        <Loader2 className="h-4 w-4 animate-spin text-text-muted" />
        <span className="text-sm text-text-muted">Loading entity details...</span>
      </div>
    </div>
  );
}
