"use client";

import { useState, useEffect } from "react";
import { generateScreeningHistory } from "@/lib/mock-services";
import type { ScreeningHistoryItem } from "@/lib/types/sanctions-screening";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

interface SanctionsHistoryProps {
  onViewScreening?: (screeningId: string) => void;
}

const columns: DataGridColumn<ScreeningHistoryItem>[] = [
  {
    id: "outcome",
    header: "Outcome",
    cell: (row) => <StatusIndicator status={row.outcome} />,
  },
  {
    id: "screeningId",
    header: "Reference",
    cell: (row) => <span className="font-mono text-xs text-text-muted">{row.screeningId.slice(0, 12)}...</span>,
  },
  {
    id: "subjectName",
    header: "Subject",
    cell: (row) => <span className="font-medium">{row.subjectName}</span>,
  },
  {
    id: "entityType",
    header: "Type",
    cell: (row) => row.entityType,
  },
  {
    id: "matchCount",
    header: "Matches",
    cell: (row) => row.matchCount,
  },
  {
    id: "screenedAt",
    header: "Date",
    cell: (row) => new Date(row.screenedAt).toLocaleDateString(),
  },
  {
    id: "disposition",
    header: "Disposition",
    cell: (row) => row.disposition ? (
      <span className="px-2 py-0.5 text-xs rounded-full bg-text-muted/10 text-text-muted">
        {row.disposition.replace("_", " ")}
      </span>
    ) : (
      <span className="text-text-muted text-xs">-</span>
    ),
  },
];

const filterDefs: DataGridFilterDef[] = [
  {
    id: "outcome",
    label: "All Outcomes",
    type: "select",
    options: [
      { value: "SUCCEEDED", label: "Clear" },
      { value: "SOFT_FAIL", label: "Review" },
      { value: "HARD_FAIL", label: "Blocked" },
    ],
  },
  {
    id: "entityType",
    label: "All Types",
    type: "select",
    options: [
      { value: "Person", label: "Person" },
      { value: "Company", label: "Company" },
      { value: "Organization", label: "Organization" },
      { value: "Vessel", label: "Vessel" },
    ],
  },
];

export default function SanctionsHistory({ onViewScreening }: SanctionsHistoryProps) {
  const [history, setHistory] = useState<ScreeningHistoryItem[]>([]);
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 10 });

  useEffect(() => {
    setHistory(generateScreeningHistory());
  }, []);

  const { data, total, totalPages } = processData(history, {
    searchFields: ["subjectName"],
    filterFn: (item, filters) => {
      if (filters.outcome && item.outcome !== filters.outcome) return false;
      if (filters.entityType && item.entityType !== filters.entityType) return false;
      return true;
    },
  });

  const pendingCount = history.filter((h) => h.disposition === "PENDING_REVIEW").length;
  const clearedCount = history.filter((h) => h.outcome === "SUCCEEDED").length;
  const blockedCount = history.filter((h) => h.outcome === "HARD_FAIL").length;
  const reviewCount = history.filter((h) => h.outcome === "SOFT_FAIL").length;

  return (
    <div className="space-y-6">
      <div className="flex gap-3 flex-wrap">
        <div className="px-4 py-2 bg-amber-50 border border-amber-200 rounded-lg text-sm">
          <span className="font-medium text-amber-800">Pending:</span> {pendingCount}
        </div>
        <div className="px-4 py-2 bg-blue-50 border border-blue-200 rounded-lg text-sm">
          <span className="font-medium text-blue-800">Review:</span> {reviewCount}
        </div>
        <div className="px-4 py-2 bg-green-50 border border-green-200 rounded-lg text-sm">
          <span className="font-medium text-green-800">Cleared:</span> {clearedCount}
        </div>
        <div className="px-4 py-2 bg-red-50 border border-red-200 rounded-lg text-sm">
          <span className="font-medium text-red-800">Blocked:</span> {blockedCount}
        </div>
      </div>
      <DataGrid
        columns={columns}
        data={data}
        getRowId={(row) => row.screeningId}
        state={state}
        actions={actions}
        totalPages={totalPages}
        totalItems={total}
        searchable
        searchPlaceholder="Search by name..."
        filterDefs={filterDefs}
        onRowClick={onViewScreening ? (row) => onViewScreening(row.screeningId) : undefined}
        emptyTitle="No screening history"
        emptyDescription="No screening history found"
        pageSizeOptions={[10, 20, 50]}
      />
    </div>
  );
}
