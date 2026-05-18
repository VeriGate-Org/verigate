"use client";

import { useState, useEffect } from "react";
import { generatePropertyHistory } from "@/lib/mock-services";
import type { PropertyHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getPropertyHistory } from "@/lib/bff-client";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Clock } from "lucide-react";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

const columns: DataGridColumn<PropertyHistoryItem>[] = [
  {
    id: "outcome",
    header: "",
    width: "3rem",
    cell: (row) => <StatusIndicator status={row.outcome} iconOnly />,
  },
  {
    id: "query",
    header: "Query",
    cell: (row) => row.query,
  },
  {
    id: "searchType",
    header: "Search Type",
    cell: (row) => row.searchType,
  },
  {
    id: "resultCount",
    header: "Results",
    cell: (row) => row.resultCount,
  },
  {
    id: "searchedAt",
    header: "Date",
    cell: (row) => new Date(row.searchedAt).toLocaleDateString(),
  },
];

const filterDefs: DataGridFilterDef[] = [
  {
    id: "outcome",
    label: "All Outcomes",
    type: "select",
    options: [
      { value: "FOUND", label: "Found" },
      { value: "NOT_FOUND", label: "Not Found" },
      { value: "FAILED", label: "Failed" },
    ],
  },
];

export default function PropertyHistory() {
  const [history, setHistory] = useState<PropertyHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 10 });

  const fetchHistory = () => {
    if (config.useMockServices) {
      setHistory(generatePropertyHistory());
      return;
    }
    setIsLoading(true);
    setError(null);
    getPropertyHistory({ limit: 200 })
      .then((res) => {
        const items = res.items as unknown as PropertyHistoryItem[];
        setHistory(items.length > 0 ? items : generatePropertyHistory());
      })
      .catch(() => setHistory(generatePropertyHistory()))
      .finally(() => setIsLoading(false));
  };

  useEffect(() => { fetchHistory(); }, []);

  const { data, total, totalPages } = processData(history, {
    searchFields: ["query"],
    filterFn: (item, filters) => !filters.outcome || item.outcome === filters.outcome,
  });

  const totalCount = history.length;
  const successCount = history.filter((h) => h.outcome === "FOUND").length;
  const successRate = totalCount > 0 ? Math.round((successCount / totalCount) * 100) : 0;

  if (!isLoading && history.length === 0 && !error) {
    return (
      <VerificationEmptyState
        icon={Clock}
        heading="No history"
        description="Property searches you perform will appear here."
      />
    );
  }

  return (
    <div className="space-y-4">
      <p className="text-sm text-text-muted">
        <span className="font-medium text-text">{totalCount}</span> verifications
        {totalCount > 0 && (
          <>
            <span className="mx-1.5 text-border">&middot;</span>
            <span className="font-medium text-text">{successRate}%</span> success rate
          </>
        )}
      </p>
      <DataGrid
        columns={columns}
        data={data}
        getRowId={(row) => row.verificationId}
        state={state}
        actions={actions}
        totalPages={totalPages}
        totalItems={total}
        isLoading={isLoading}
        error={error}
        onRetry={fetchHistory}
        searchable
        searchPlaceholder="Search by query or owner name..."
        filterDefs={filterDefs}
        emptyTitle="No results"
        emptyDescription="No results match the current filters."
        pageSizeOptions={[10, 20, 50]}
      />
    </div>
  );
}
