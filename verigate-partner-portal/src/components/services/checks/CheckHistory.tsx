"use client";

import { useState, useEffect } from "react";
import { listCheckSessions } from "@/lib/services/check-session-service";
import type { CheckSession } from "@/lib/types/check-session";
import { CHECK_DEFINITIONS } from "@/components/services/checks/checkFieldRegistry";
import { CheckResultCard, type CheckState } from "@/components/services/checks/CheckResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Clock, RotateCcw } from "lucide-react";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

interface CheckHistoryProps {
  onRerun?: (subject: { idNumber: string; firstName: string; lastName: string }, checks: string[]) => void;
}

const columns: DataGridColumn<CheckSession>[] = [
  {
    id: "outcome",
    header: "Status",
    cell: (session) => {
      if (session.failed === 0) return <StatusIndicator status="Passed" iconOnly />;
      if (session.passed === 0) return <StatusIndicator status="FAILED" iconOnly />;
      return <StatusIndicator status="Mixed" iconOnly />;
    },
  },
  {
    id: "subject",
    header: "Subject",
    cell: (session) => <span className="font-medium">{session.subject.firstName} {session.subject.lastName}</span>,
    secondary: (session) => <span className="font-mono">{session.subject.idNumber}</span>,
  },
  {
    id: "totalChecks",
    header: "Checks",
    cell: (session) => <span className="tabular-nums">{session.totalChecks}</span>,
  },
  {
    id: "passed",
    header: "Passed",
    cell: (session) => <span className="text-success tabular-nums">{session.passed}</span>,
  },
  {
    id: "failed",
    header: "Failed",
    cell: (session) => <span className="text-danger tabular-nums">{session.failed}</span>,
  },
  {
    id: "createdAt",
    header: "Date",
    cell: (session) => new Date(session.createdAt).toLocaleDateString(),
  },
];

const filterDefs: DataGridFilterDef[] = [
  {
    id: "outcome",
    label: "All Outcomes",
    type: "select",
    options: [
      { value: "passed", label: "All Passed" },
      { value: "has_failures", label: "Has Failures" },
    ],
  },
];

export default function CheckHistory({ onRerun }: CheckHistoryProps) {
  const [sessions, setSessions] = useState<CheckSession[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 10 });

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

  const { data, total, totalPages } = processData(sessions, {
    searchFn: (s, q) =>
      s.subject.idNumber.toLowerCase().includes(q) ||
      s.subject.firstName.toLowerCase().includes(q) ||
      s.subject.lastName.toLowerCase().includes(q) ||
      `${s.subject.firstName} ${s.subject.lastName}`.toLowerCase().includes(q),
    filterFn: (item, filters) => {
      if (!filters.outcome) return true;
      if (filters.outcome === "passed") return item.failed === 0;
      if (filters.outcome === "has_failures") return item.failed > 0;
      return true;
    },
  });

  const totalCount = sessions.length;
  const allPassedCount = sessions.filter((s) => s.failed === 0).length;
  const hasFailuresCount = sessions.filter((s) => s.failed > 0).length;

  if (!isLoading && sessions.length === 0 && !error) {
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
      <div className="flex gap-3 flex-wrap">
        <SummaryChip label="Total" count={totalCount} className="bg-accent/10 text-accent" />
        <SummaryChip label="All Passed" count={allPassedCount} className="bg-success/10 text-success" />
        <SummaryChip label="Has Failures" count={hasFailuresCount} className="bg-danger/10 text-danger" />
      </div>

      <DataGrid
        columns={columns}
        data={data}
        getRowId={(row) => row.sessionId}
        state={state}
        actions={actions}
        totalPages={totalPages}
        totalItems={total}
        isLoading={isLoading}
        error={error}
        onRetry={fetchHistory}
        searchable
        searchPlaceholder="Search by name or ID number..."
        filterDefs={filterDefs}
        renderExpandedRow={(session) => <ExpandedSessionDetail session={session} onRerun={onRerun} />}
        emptyTitle="No screening sessions yet"
        emptyDescription="Screening sessions you run will appear here."
        pageSizeOptions={[10, 20, 50]}
      />
    </div>
  );
}

// -- Expanded Session Detail --------------------------------------------------

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

// -- Summary Chip -------------------------------------------------------------

function SummaryChip({ label, count, className }: { label: string; count: number; className: string }) {
  return (
    <div className={`px-3 py-1.5 rounded-lg text-sm font-medium ${className}`}>
      {label}: {count}
    </div>
  );
}
