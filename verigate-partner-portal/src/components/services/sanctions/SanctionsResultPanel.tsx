"use client";

import { useState } from "react";
import {
  Shield,
  AlertTriangle,
  CheckCircle,
  ChevronDown,
  ChevronUp,
  Download,
  User,
  Building2,
  Globe,
  Ship,
} from "lucide-react";
import type {
  SanctionsScreeningResponse,
  ScoredMatchEntity,
} from "@/lib/types/sanctions-screening";

// ---------------------------------------------------------------------------
// Props
// ---------------------------------------------------------------------------

interface SanctionsResultPanelProps {
  response: SanctionsScreeningResponse;
  onViewEntity: (entityId: string) => void;
  onExportPdf?: () => void;
  onExportCsv?: () => void;
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const SCHEMA_ICONS: Record<string, React.ReactNode> = {
  Person: <User className="h-4 w-4" />,
  Company: <Building2 className="h-4 w-4" />,
  Organization: <Globe className="h-4 w-4" />,
  Vessel: <Ship className="h-4 w-4" />,
};

function schemaIcon(schema: string) {
  return SCHEMA_ICONS[schema] ?? <Shield className="h-4 w-4" />;
}

function outcomeStyle(outcome: SanctionsScreeningResponse["outcome"]) {
  switch (outcome) {
    case "SUCCEEDED":
      return { label: "CLEAR", bg: "bg-success/10", text: "text-success", border: "border-success/30" };
    case "HARD_FAIL":
      return { label: "HARD FAIL", bg: "bg-danger/10", text: "text-danger", border: "border-danger/30" };
    case "SOFT_FAIL":
      return { label: "SOFT FAIL", bg: "bg-warning/10", text: "text-warning", border: "border-warning/30" };
    case "SYSTEM_OUTAGE":
      return { label: "SYSTEM OUTAGE", bg: "bg-danger/10", text: "text-danger", border: "border-danger/30" };
    default:
      return { label: outcome, bg: "bg-neutral/10", text: "text-text-muted", border: "border-border" };
  }
}

function scoreBarColor(score: number): string {
  if (score > 0.7) return "bg-danger";
  if (score >= 0.5) return "bg-warning";
  return "bg-success";
}

function scoreTextColor(score: number): string {
  if (score > 0.7) return "text-danger";
  if (score >= 0.5) return "text-warning";
  return "text-success";
}

function formatFeatureLabel(key: string): string {
  return key
    .replace(/_/g, " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

function formatTimestamp(iso: string): string {
  try {
    return new Date(iso).toLocaleString();
  } catch {
    return iso;
  }
}

// ---------------------------------------------------------------------------
// Dataset badge color mapping
// ---------------------------------------------------------------------------

const DATASET_COLORS: Record<string, string> = {
  un_sc_sanctions: "bg-red-100 text-red-800",
  eu_sanctions: "bg-blue-100 text-blue-800",
  us_ofac: "bg-indigo-100 text-indigo-800",
  uk_sanctions: "bg-sky-100 text-sky-800",
  au_dfat: "bg-teal-100 text-teal-800",
  ca_sema: "bg-amber-100 text-amber-800",
  pep: "bg-purple-100 text-purple-800",
  interpol: "bg-orange-100 text-orange-800",
};

function datasetBadgeClass(dataset: string): string {
  const key = dataset.toLowerCase().replace(/[\s-]/g, "_");
  return DATASET_COLORS[key] ?? "bg-neutral-100 text-neutral-700";
}

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

export default function SanctionsResultPanel({
  response,
  onViewEntity,
  onExportPdf,
  onExportCsv,
}: SanctionsResultPanelProps) {
  const badge = outcomeStyle(response.outcome);

  const highestScore =
    response.matches.length > 0
      ? Math.max(...response.matches.map((m) => m.score))
      : 0;

  const pepHits = response.matches.filter((m) => m.isPep).length;
  const sanctionsHits = response.matches.filter((m) => m.isSanctioned).length;

  const aboveThreshold = response.matches.filter((m) => m.score >= response.threshold);
  const belowThreshold = response.matches.filter((m) => m.score < response.threshold);

  return (
    <div className="space-y-4">
      {/* ----------------------------------------------------------------- */}
      {/* Summary header                                                     */}
      {/* ----------------------------------------------------------------- */}
      <div className="console-card">
        <div className="console-card-header flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Shield className="h-5 w-5 text-text-muted" />
            <div>
              <div className="text-sm font-semibold text-text">Screening Results</div>
              <div className="text-xs text-text-muted">
                {response.totalMatches} match{response.totalMatches !== 1 ? "es" : ""} found
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {onExportPdf && (
              <button
                type="button"
                onClick={onExportPdf}
                className="inline-flex items-center gap-1.5 rounded-md border border-border px-3 py-1.5 text-xs font-medium text-text hover:bg-surface-hover transition-colors"
              >
                <Download className="h-3.5 w-3.5" />
                PDF
              </button>
            )}
            {onExportCsv && (
              <button
                type="button"
                onClick={onExportCsv}
                className="inline-flex items-center gap-1.5 rounded-md border border-border px-3 py-1.5 text-xs font-medium text-text hover:bg-surface-hover transition-colors"
              >
                <Download className="h-3.5 w-3.5" />
                CSV
              </button>
            )}
          </div>
        </div>

        <div className="console-card-body">
          {/* Outcome badge row */}
          <div className="flex flex-wrap items-center gap-3 mb-4">
            <span
              className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold ${badge.bg} ${badge.text} border ${badge.border}`}
            >
              {response.outcome === "SUCCEEDED" ? (
                <CheckCircle className="h-3.5 w-3.5" />
              ) : (
                <AlertTriangle className="h-3.5 w-3.5" />
              )}
              {badge.label}
            </span>
          </div>

          {/* Stats grid */}
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
            <StatCell label="Matches" value={String(response.totalMatches)} />
            <StatCell
              label="Highest Score"
              value={highestScore > 0 ? `${(highestScore * 100).toFixed(0)}%` : "N/A"}
              className={highestScore > 0 ? scoreTextColor(highestScore) : undefined}
            />
            <StatCell label="Dataset" value={response.dataset} />
            <StatCell label="Algorithm" value={response.algorithm} />
            <StatCell
              label="PEP Hits"
              value={String(pepHits)}
              className={pepHits > 0 ? "text-purple-600" : undefined}
            />
            <StatCell
              label="Sanctions Hits"
              value={String(sanctionsHits)}
              className={sanctionsHits > 0 ? "text-danger" : undefined}
            />
          </div>
        </div>
      </div>

      {/* ----------------------------------------------------------------- */}
      {/* Match list -- above threshold                                      */}
      {/* ----------------------------------------------------------------- */}
      {aboveThreshold.length > 0 && (
        <div className="space-y-3">
          {aboveThreshold.map((match) => (
            <MatchCard
              key={match.id}
              match={match}
              threshold={response.threshold}
              onViewEntity={onViewEntity}
            />
          ))}
        </div>
      )}

      {/* ----------------------------------------------------------------- */}
      {/* Match list -- below threshold (reduced opacity)                    */}
      {/* ----------------------------------------------------------------- */}
      {belowThreshold.length > 0 && (
        <div className="space-y-3 opacity-50">
          <p className="text-xs text-text-muted font-medium px-1">
            Below threshold ({(response.threshold * 100).toFixed(0)}%)
          </p>
          {belowThreshold.map((match) => (
            <MatchCard
              key={match.id}
              match={match}
              threshold={response.threshold}
              onViewEntity={onViewEntity}
            />
          ))}
        </div>
      )}

      {/* ----------------------------------------------------------------- */}
      {/* No matches state                                                   */}
      {/* ----------------------------------------------------------------- */}
      {response.matches.length === 0 && (
        <div className="console-card">
          <div className="console-card-body flex flex-col items-center justify-center py-10 text-center">
            <CheckCircle className="h-10 w-10 text-success mb-3" />
            <p className="text-sm font-medium text-text">No matches found</p>
            <p className="text-xs text-text-muted mt-1">
              The screened entity did not match any records in the {response.dataset} dataset.
            </p>
          </div>
        </div>
      )}

      {/* ----------------------------------------------------------------- */}
      {/* Screening metadata footer                                          */}
      {/* ----------------------------------------------------------------- */}
      <div className="console-card">
        <div className="console-card-body">
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3 text-xs">
            <MetaItem label="Provider" value={response.provider} />
            <MetaItem label="Algorithm" value={response.algorithm} />
            <MetaItem label="Threshold" value={`${(response.threshold * 100).toFixed(0)}%`} />
            <MetaItem label="Screened at" value={formatTimestamp(response.screenedAt)} />
            <MetaItem label="Correlation ID" value={response.correlationId} mono />
          </div>
        </div>
      </div>
    </div>
  );
}

// ---------------------------------------------------------------------------
// Sub-components
// ---------------------------------------------------------------------------

function StatCell({
  label,
  value,
  className,
}: {
  label: string;
  value: string;
  className?: string;
}) {
  return (
    <div className="rounded-md border border-border px-3 py-2">
      <div className="text-[10px] uppercase tracking-wider text-text-muted">{label}</div>
      <div className={`text-sm font-semibold truncate ${className ?? "text-text"}`}>{value}</div>
    </div>
  );
}

function MetaItem({
  label,
  value,
  mono = false,
}: {
  label: string;
  value: string;
  mono?: boolean;
}) {
  return (
    <div>
      <div className="text-text-muted">{label}</div>
      <div className={`font-medium text-text truncate ${mono ? "font-mono text-[11px]" : ""}`}>
        {value}
      </div>
    </div>
  );
}

// ---------------------------------------------------------------------------
// Match card
// ---------------------------------------------------------------------------

function MatchCard({
  match,
  threshold,
  onViewEntity,
}: {
  match: ScoredMatchEntity;
  threshold: number;
  onViewEntity: (entityId: string) => void;
}) {
  const [expanded, setExpanded] = useState(false);
  const isBelowThreshold = match.score < threshold;
  const scorePercent = Math.round(match.score * 100);
  const featureEntries = Object.entries(match.features);

  return (
    <div className="console-card">
      {/* Card header */}
      <div className="console-card-header flex items-center justify-between gap-3">
        <div className="flex items-center gap-3 min-w-0">
          <span className="flex-shrink-0 text-text-muted">{schemaIcon(match.schema)}</span>
          <div className="min-w-0">
            <div className="text-sm font-semibold text-text truncate">{match.caption}</div>
            <div className="text-xs text-text-muted">{match.schema}</div>
          </div>
        </div>

        {/* Score bar */}
        <div className="flex items-center gap-3 flex-shrink-0">
          <div className="flex items-center gap-2">
            <div className="w-24 h-2 rounded-full bg-surface overflow-hidden">
              <div
                className={`h-full rounded-full transition-all ${scoreBarColor(match.score)}`}
                style={{ width: `${scorePercent}%` }}
              />
            </div>
            <span className={`text-xs font-semibold tabular-nums ${scoreTextColor(match.score)}`}>
              {scorePercent}%
            </span>
          </div>
        </div>
      </div>

      <div className="console-card-body space-y-3">
        {/* Badges row */}
        <div className="flex flex-wrap items-center gap-1.5">
          {match.isPep && (
            <span className="inline-flex items-center gap-1 rounded-full bg-purple-100 text-purple-800 px-2.5 py-0.5 text-[11px] font-medium">
              PEP
            </span>
          )}
          {match.isSanctioned && (
            <span className="inline-flex items-center gap-1 rounded-full bg-red-100 text-red-800 px-2.5 py-0.5 text-[11px] font-medium">
              Sanctioned
            </span>
          )}
          {match.datasets.map((ds) => (
            <span
              key={ds}
              className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-[11px] font-medium ${datasetBadgeClass(ds)}`}
            >
              {ds}
            </span>
          ))}
          {isBelowThreshold && (
            <span className="inline-flex items-center rounded-full bg-neutral-100 text-neutral-500 px-2.5 py-0.5 text-[11px] font-medium">
              Below threshold
            </span>
          )}
        </div>

        {/* Collapsible feature breakdown */}
        {featureEntries.length > 0 && (
          <div>
            <button
              type="button"
              onClick={() => setExpanded((prev) => !prev)}
              className="flex items-center gap-1.5 text-xs font-medium text-text-muted hover:text-text transition-colors"
            >
              {expanded ? (
                <ChevronUp className="h-3.5 w-3.5" />
              ) : (
                <ChevronDown className="h-3.5 w-3.5" />
              )}
              Feature breakdown ({featureEntries.length})
            </button>

            {expanded && (
              <div className="mt-2 border border-border rounded overflow-hidden divide-y divide-border">
                {featureEntries.map(([key, value]) => (
                  <div
                    key={key}
                    className="flex items-center justify-between px-4 py-2 text-sm"
                  >
                    <span className="text-text-muted">{formatFeatureLabel(key)}</span>
                    <div className="flex items-center gap-2">
                      <div className="w-16 h-1.5 rounded-full bg-surface overflow-hidden">
                        <div
                          className={`h-full rounded-full ${scoreBarColor(value)}`}
                          style={{ width: `${Math.round(value * 100)}%` }}
                        />
                      </div>
                      <span className={`text-xs font-medium tabular-nums ${scoreTextColor(value)}`}>
                        {(value * 100).toFixed(0)}%
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* View details button */}
        <div className="flex justify-end pt-1">
          <button
            type="button"
            onClick={() => onViewEntity(match.id)}
            className="inline-flex items-center gap-1.5 rounded-md border border-border px-3 py-1.5 text-xs font-medium text-text hover:bg-surface-hover transition-colors"
          >
            View Details
          </button>
        </div>
      </div>
    </div>
  );
}
