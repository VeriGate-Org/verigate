"use client";

import { useState, useEffect } from "react";
import { generateNegativeNewsHistory } from "@/lib/mock-services";
import type { NegativeNewsHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getNegativeNewsHistory } from "@/lib/bff-client";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Clock } from "lucide-react";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

const columns: DataGridColumn<NegativeNewsHistoryItem>[] = [
  { id: "outcome", header: "", width: "3rem", cell: (row) => <StatusIndicator status={row.outcome} iconOnly /> },
  { id: "subjectName", header: "Name", cell: (row) => row.subjectName },
  { id: "entityType", header: "Entity Type", cell: (row) => row.entityType },
  { id: "matchCount", header: "Matches", cell: (row) => row.matchCount },
  { id: "screenedAt", header: "Date", cell: (row) => new Date(row.screenedAt).toLocaleDateString() },
];

const filterDefs: DataGridFilterDef[] = [
  {
    id: "outcome",
    label: "All Outcomes",
    type: "select",
    options: [
      { value: "CLEAR", label: "Clear" },
      { value: "MATCHES_FOUND", label: "Matches Found" },
      { value: "FAILED", label: "Failed" },
    ],
  },
];

export default function NegativeNewsHistory() {
  const [history, setHistory] = useState<NegativeNewsHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 10 });

  const fetchHistory = () => {
    if (config.useMockServices) {
      setHistory(generateNegativeNewsHistory());
      return;
    }
    setIsLoading(true);
    setError(null);
    getNegativeNewsHistory({ limit: 200 })
      .then((res) => {
        const items = res.items as unknown as NegativeNewsHistoryItem[];
        setHistory(items.length > 0 ? items : generateNegativeNewsHistory());
      })
      .catch(() => {
        setHistory(generateNegativeNewsHistory());
      })
      .finally(() => setIsLoading(false));
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const { data, total, totalPages } = processData(history, {
    searchFields: ["subjectName"],
    filterFn: (item, filters) => !filters.outcome || item.outcome === filters.outcome,
  });

  const totalCount = history.length;
  const successCount = history.filter((h) => h.outcome === "CLEAR").length;
  const successRate = totalCount > 0 ? Math.round((successCount / totalCount) * 100) : 0;

  if (!isLoading && history.length === 0 && !error) {
    return (
      <VerificationEmptyState icon={Clock} heading="No history" description="Negative news screenings you perform will appear here." />
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
        searchPlaceholder="Search by name..."
        filterDefs={filterDefs}
        emptyTitle="No results"
        emptyDescription="No results match the current filters."
        pageSizeOptions={[10, 20, 50]}
      />
    </div>
  );
}
