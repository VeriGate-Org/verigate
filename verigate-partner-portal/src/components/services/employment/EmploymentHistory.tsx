"use client";

import { useState, useEffect } from "react";
import { generateEmploymentHistory } from "@/lib/mock-services";
import type { EmploymentHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getEmploymentHistory } from "@/lib/bff-client";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Clock } from "lucide-react";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

const columns: DataGridColumn<EmploymentHistoryItem>[] = [
  { id: "outcome", header: "", width: "3rem", cell: (row) => <StatusIndicator status={row.outcome} iconOnly /> },
  { id: "idNumber", header: "ID Number", cell: (row) => <span className="font-mono text-xs">{row.idNumber}</span> },
  { id: "fullName", header: "Name", cell: (row) => row.fullName },
  { id: "employer", header: "Employer", cell: (row) => row.employer },
  { id: "verifiedAt", header: "Date", cell: (row) => new Date(row.verifiedAt).toLocaleDateString() },
];

const filterDefs: DataGridFilterDef[] = [
  {
    id: "outcome",
    label: "All Outcomes",
    type: "select",
    options: [
      { value: "VERIFIED", label: "Verified" },
      { value: "NOT_VERIFIED", label: "Not Verified" },
      { value: "FAILED", label: "Failed" },
    ],
  },
];

export default function EmploymentHistory() {
  const [history, setHistory] = useState<EmploymentHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 10 });

  const fetchHistory = () => {
    if (config.useMockServices) {
      setHistory(generateEmploymentHistory());
      return;
    }
    setIsLoading(true);
    setError(null);
    getEmploymentHistory({ limit: 200 })
      .then((res) => {
        const items = res.items as unknown as EmploymentHistoryItem[];
        setHistory(items.length > 0 ? items : generateEmploymentHistory());
      })
      .catch(() => {
        setHistory(generateEmploymentHistory());
      })
      .finally(() => setIsLoading(false));
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const { data, total, totalPages } = processData(history, {
    searchFields: ["idNumber", "fullName"],
    filterFn: (item, filters) => !filters.outcome || item.outcome === filters.outcome,
  });

  const totalCount = history.length;
  const successCount = history.filter((h) => h.outcome === "VERIFIED").length;
  const successRate = totalCount > 0 ? Math.round((successCount / totalCount) * 100) : 0;

  if (!isLoading && history.length === 0 && !error) {
    return (
      <VerificationEmptyState icon={Clock} heading="No history" description="Employment verifications you perform will appear here." />
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
        searchPlaceholder="Search by ID number or name..."
        filterDefs={filterDefs}
        emptyTitle="No results"
        emptyDescription="No results match the current filters."
        pageSizeOptions={[10, 20, 50]}
      />
    </div>
  );
}
