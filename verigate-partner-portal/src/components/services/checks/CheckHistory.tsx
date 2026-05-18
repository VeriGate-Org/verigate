"use client";

import { useState, useEffect } from "react";
import { listCheckSessions } from "@/lib/services/check-session-service";
import type { CheckSession } from "@/lib/types/check-session";
import { CHECK_DEFINITIONS } from "@/components/services/checks/checkFieldRegistry";
import { CheckResultCard, type CheckState } from "@/components/services/checks/CheckResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { OutcomeBadge } from "@/components/services/shared/OutcomeBadge";
import {
  Search, ChevronLeft, ChevronRight, Filter, Loader2, AlertCircle, Clock,
  ChevronDown, ChevronUp, RotateCcw,
} from "lucide-react";

interface CheckHistoryProps {
  onRerun?: (subject: { idNumber: string; firstName: string; lastName: string }, checks: string[]) => void;
}

export default function CheckHistory({ onRerun }: CheckHistoryProps) {
  const [sessions, setSessions] = useState<CheckSession[]>([]);
  const [filteredSessions, setFilteredSessions] = useState<CheckSession[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [outcomeFilter, setOutcomeFilter] = useState<string>("all");
  const [currentPage, setCurrentPage] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [expandedId, setExpandedId] = useState<string | null>(null);
  const pageSize = 10;

  const fetchHistory = () => {
    setIsLoading(true);
    setError(null);
    listCheckSessions({ limit: 200 })
      .then((res) => {
        setSessions(res.items);
      })
      .catch((err) => {
        setError(err instanceof Error ? err.message : "Failed to load history");
      })
      .finally(() => setIsLoading(false));
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  useEffect(() => {
    let items = [...sessions];
    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      items = items.filter(
        (s) =>
          s.subject.idNumber.toLowerCase().includes(q) ||
          s.subject.firstName.toLowerCase().includes(q) ||
          s.subject.lastName.toLowerCase().includes(q) ||
          `${s.subject.firstName} ${s.subject.lastName}`.toLowerCase().includes(q),
      );
    }
    if (outcomeFilter === "passed") {
      items = items.filter((s) => s.failed === 0);
    } else if (outcomeFilter === "has_failures") {
      items = items.filter((s) => s.failed > 0);
    }
    setFilteredSessions(items);
    setCurrentPage(1);
  }, [sessions, searchQuery, outcomeFilter]);

  const totalPages = Math.ceil(filteredSessions.length / pageSize);
  const paged = filteredSessions.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  const totalCount = sessions.length;
  const allPassedCount = sessions.filter((s) => s.failed === 0).length;
  const hasFailuresCount = sessions.filter((s) => s.failed > 0).length;

  const sessionOutcomeBadge = (session: CheckSession) => {
    if (session.failed === 0) {
      return <OutcomeBadge label="Passed" type="success" />;
    }
    if (session.passed === 0) {
      return <OutcomeBadge label="Failed" type="danger" />;
    }
    return <OutcomeBadge label="Mixed" type="warning" />;
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-16">
        <Loader2 className="w-6 h-6 animate-spin text-accent" />
        <span className="ml-2 text-sm text-text-muted">Loading screening history...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-danger/40 bg-danger/5 p-4 flex items-start gap-3">
        <AlertCircle className="w-5 h-5 text-danger mt-0.5 flex-shrink-0" />
        <div className="flex-1">
          <p className="text-sm font-medium text-danger">Failed to load screening history</p>
          <p className="text-sm text-danger/80 mt-1">{error}</p>
          <button
            onClick={fetchHistory}
            className="mt-3 px-3 py-1.5 text-sm bg-danger/10 hover:bg-danger/20 text-danger rounded-md border border-danger/30"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  if (sessions.length === 0) {
    return (
      <VerificationEmptyState
        icon={Clock}
        heading="No screening sessions yet"
        description="Screening sessions you run will appear here."
      />
    );
  }

  return (
    <div className="space-y-4">
      {/* Summary chips */}
      <div className="flex gap-3 flex-wrap">
        <SummaryChip label="Total" count={totalCount} className="bg-accent/10 text-accent" />
        <SummaryChip label="All Passed" count={allPassedCount} className="bg-success/10 text-success" />
        <SummaryChip label="Has Failures" count={hasFailuresCount} className="bg-danger/10 text-danger" />
      </div>

      {/* Table card */}
      <div className="console-card overflow-hidden">
        {/* Card header — search & filters */}
        <div className="console-card-header">
          <div className="flex flex-wrap gap-3 items-center w-full">
            <div className="relative flex-1 min-w-[200px]">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-text-muted" />
              <input
                type="text"
                placeholder="Search by name or ID number..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="aws-input w-full pl-10 pr-4 py-2 text-sm"
              />
            </div>
            <div className="flex items-center gap-2">
              <Filter className="w-4 h-4 text-text-muted" />
              <select
                value={outcomeFilter}
                onChange={(e) => setOutcomeFilter(e.target.value)}
                className="aws-select text-sm"
              >
                <option value="all">All Outcomes</option>
                <option value="passed">All Passed</option>
                <option value="has_failures">Has Failures</option>
              </select>
            </div>
          </div>
        </div>

        {/* Table body */}
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-base-200/50">
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Status</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Subject</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Checks</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Passed</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Failed</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Date</th>
                <th className="w-10"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {paged.map((session) => {
                const isExpanded = expandedId === session.sessionId;
                return (
                  <SessionRow
                    key={session.sessionId}
                    session={session}
                    isExpanded={isExpanded}
                    outcomeBadge={sessionOutcomeBadge(session)}
                    onToggle={() => setExpandedId(isExpanded ? null : session.sessionId)}
                    onRerun={onRerun}
                  />
                );
              })}
              {paged.length === 0 && (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-text-muted">
                    No results match the current filters.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination footer */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-4 py-3 border-t border-border">
            <p className="text-xs text-text-muted">
              Showing {(currentPage - 1) * pageSize + 1}&ndash;{Math.min(currentPage * pageSize, filteredSessions.length)} of {filteredSessions.length}
            </p>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                disabled={currentPage === 1}
                className="p-1.5 border border-border rounded disabled:opacity-50 hover:bg-hover"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <span className="text-xs text-text-muted tabular-nums">{currentPage} / {totalPages}</span>
              <button
                onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                disabled={currentPage === totalPages}
                className="p-1.5 border border-border rounded disabled:opacity-50 hover:bg-hover"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// ── Session Row + Expandable Detail ─────────────────────────────────

function SessionRow({
  session,
  isExpanded,
  outcomeBadge,
  onToggle,
  onRerun,
}: {
  session: CheckSession;
  isExpanded: boolean;
  outcomeBadge: React.ReactNode;
  onToggle: () => void;
  onRerun?: (subject: { idNumber: string; firstName: string; lastName: string }, checks: string[]) => void;
}) {
  const fullName = `${session.subject.firstName} ${session.subject.lastName}`;
  return (
    <>
      <tr
        className="hover:bg-hover/50 cursor-pointer"
        onClick={onToggle}
      >
        <td className="px-4 py-2.5">{outcomeBadge}</td>
        <td className="px-4 py-2.5">
          <div className="text-text font-medium">{fullName}</div>
          <div className="font-mono text-xs text-text-muted">{session.subject.idNumber}</div>
        </td>
        <td className="px-4 py-2.5 text-text tabular-nums">{session.totalChecks}</td>
        <td className="px-4 py-2.5 text-success tabular-nums">{session.passed}</td>
        <td className="px-4 py-2.5 text-danger tabular-nums">{session.failed}</td>
        <td className="px-4 py-2.5 text-text-muted">
          {new Date(session.createdAt).toLocaleDateString()}
        </td>
        <td className="px-4 py-2.5">
          {isExpanded ? (
            <ChevronUp className="w-4 h-4 text-text-muted" />
          ) : (
            <ChevronDown className="w-4 h-4 text-text-muted" />
          )}
        </td>
      </tr>
      {isExpanded && (
        <tr>
          <td colSpan={7} className="px-4 py-4 bg-base-200/30">
            <ExpandedSessionDetail session={session} onRerun={onRerun} />
          </td>
        </tr>
      )}
    </>
  );
}

function ExpandedSessionDetail({
  session,
  onRerun,
}: {
  session: CheckSession;
  onRerun?: (subject: { idNumber: string; firstName: string; lastName: string }, checks: string[]) => void;
}) {
  const fullName = `${session.subject.firstName} ${session.subject.lastName}`;
  return (
    <div className="space-y-4">
      {/* Subject info */}
      <div className="flex flex-wrap items-center gap-x-6 gap-y-1 text-sm">
        <span className="text-text-muted">Subject:</span>
        <span className="text-text font-medium">{fullName}</span>
        <span className="font-mono text-xs text-text-muted">{session.subject.idNumber}</span>
        <span className="text-text-muted">
          {new Date(session.createdAt).toLocaleString()}
        </span>
      </div>

      {/* Check result cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {session.checks.map((check) => {
          const def = CHECK_DEFINITIONS[check.checkType];
          if (!def) return null;
          const checkState: CheckState = {
            status: check.status === "success" ? "success" : "error",
            data: check.data,
            error: check.error,
          };
          return (
            <CheckResultCard
              key={check.checkType}
              checkType={check.checkType}
              state={checkState}
              definition={def}
            />
          );
        })}
      </div>

      {/* Re-run button */}
      {onRerun && (
        <button
          type="button"
          onClick={() =>
            onRerun(
              session.subject,
              session.checks.map((c) => c.checkType),
            )
          }
          className="inline-flex items-center gap-1.5 px-3 py-1.5 text-sm font-medium rounded border border-border text-[color:var(--color-accent-strong)] hover:bg-hover"
        >
          <RotateCcw className="w-3.5 h-3.5" />
          Re-run screening
        </button>
      )}
    </div>
  );
}

function SummaryChip({ label, count, className }: { label: string; count: number; className: string }) {
  return (
    <div className={`px-3 py-1.5 rounded-lg text-sm font-medium ${className}`}>
      {label}: {count}
    </div>
  );
}
