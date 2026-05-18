"use client";

import * as React from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useCases } from "@/lib/hooks/useCases";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

interface CaseRow {
  caseId: string;
  subjectName?: string;
  subjectId?: string;
  status: string;
  priority: string;
  compositeRiskScore?: number;
  assignee?: string;
  createdAt: string;
}

const columns: DataGridColumn<CaseRow>[] = [
  {
    id: "caseId",
    header: "Case ID",
    sortable: true,
    cell: (row) => (
      <Link
        href={`/cases/${row.caseId}`}
        className="text-sm font-mono text-accent hover:underline"
        onClick={(e) => e.stopPropagation()}
      >
        {row.caseId.slice(0, 8)}...
      </Link>
    ),
  },
  {
    id: "subjectName",
    header: "Subject",
    sortable: true,
    cell: (row) => (
      <span className="text-sm font-medium text-text">
        {row.subjectName || "-"}
      </span>
    ),
    secondary: (row) =>
      row.subjectId ? <span>{row.subjectId}</span> : null,
  },
  {
    id: "status",
    header: "Status",
    sortable: true,
    cell: (row) => <StatusIndicator status={row.status} />,
  },
  {
    id: "priority",
    header: "Priority",
    sortable: true,
    cell: (row) => <StatusIndicator status={row.priority} />,
  },
  {
    id: "compositeRiskScore",
    header: "Risk Score",
    sortable: true,
    cell: (row) => (
      <span className="text-sm text-text">{row.compositeRiskScore}</span>
    ),
  },
  {
    id: "assignee",
    header: "Assignee",
    cell: (row) => (
      <span className="text-sm text-text-muted">
        {row.assignee || "Unassigned"}
      </span>
    ),
  },
  {
    id: "createdAt",
    header: "Created",
    sortable: true,
    cell: (row) => (
      <span className="text-sm text-text-muted">
        {new Date(row.createdAt).toLocaleDateString()}
      </span>
    ),
  },
];

const filterDefs: DataGridFilterDef[] = [
  {
    id: "status",
    label: "All Statuses",
    type: "select",
    options: [
      { value: "OPEN", label: "Open" },
      { value: "IN_REVIEW", label: "In Review" },
      { value: "ESCALATED", label: "Escalated" },
      { value: "RESOLVED", label: "Resolved" },
    ],
  },
];

export default function CasesTable() {
  const [statusFilter, setStatusFilter] = React.useState<string>("");
  const { data: cases = [], isLoading, error } = useCases(
    statusFilter ? { status: statusFilter } : undefined,
  );
  const router = useRouter();
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 20 });

  // Sync the DataGrid filter with the API filter
  React.useEffect(() => {
    setStatusFilter(state.filters.status || "");
  }, [state.filters.status]);

  const { data, total, totalPages } = processData(cases as CaseRow[], {
    searchFields: ["subjectName", "caseId", "assignee"],
    filterFn: () => true, // Filtering done server-side via useCases
  });

  return (
    <DataGrid
      columns={columns}
      data={data}
      getRowId={(row) => row.caseId}
      state={state}
      actions={actions}
      totalPages={totalPages}
      totalItems={total}
      isLoading={isLoading}
      error={error instanceof Error ? error.message : error ? String(error) : null}
      title="Cases"
      description="Cases are automatically created when a verification result requires manual review."
      searchable
      searchPlaceholder="Search cases..."
      filterDefs={filterDefs}
      onRowClick={(row) => router.push(`/cases/${row.caseId}`)}
      emptyTitle="No cases found"
      emptyDescription="Cases are automatically created when a verification result requires manual review."
      pageSizeOptions={[10, 20, 50]}
    />
  );
}
