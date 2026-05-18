"use client";

import { useState, useEffect } from "react";
import { generateCreditCheckHistory } from "@/lib/mock-services";
import type { CreditCheckHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getCreditCheckHistory } from "@/lib/bff-client";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Clock } from "lucide-react";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

const columns: DataGridColumn<CreditCheckHistoryItem>[] = [
  {
    id: "outcome",
    header: "Status",
    cell: (row) => <StatusIndicator status={row.outcome} />,
  },
  {
    id: "idNumber",
    header: "ID Number",
    cell: (row) => <span className="font-mono text-xs">{row.idNumber}</span>,
  },
  {
    id: "fullName",
    header: "Name",
    cell: (row) => row.fullName,
  },
  {
    id: "riskGrade",
    header: "Risk Grade",
    cell: (row) => row.riskGrade,
  },
  {
    id: "verifiedAt",
    header: "Date",
    cell: (row) => new Date(row.verifiedAt).toLocaleDateString(),
  },
];

const filterDefs: DataGridFilterDef[] = [
  {
    id: "outcome",
    label: "All Outcomes",
    type: "select",
    options: [
      { value: "COMPLETED", label: "Completed" },
      { value: "FAILED", label: "Failed" },
    ],
  },
];

export default function CreditCheckHistory() {
  const [history, setHistory] = useState<CreditCheckHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 10 });

  const fetchHistory = () => {
    if (config.useMockServices) {
      setHistory(generateCreditCheckHistory());
      return;
    }
    setIsLoading(true);
    setError(null);
    getCreditCheckHistory({ limit: 200 })
      .then((res) => {
        const items = res.items as unknown as CreditCheckHistoryItem[];
        setHistory(items.length > 0 ? items : generateCreditCheckHistory());
      })
      .catch(() => setHistory(generateCreditCheckHistory()))
      .finally(() => setIsLoading(false));
  };

  useEffect(() => { fetchHistory(); }, []);

  const { data, total, totalPages } = processData(history, {
    searchFields: ["idNumber", "fullName"],
    filterFn: (item, filters) => !filters.outcome || item.outcome === filters.outcome,
  });

  const totalCount = history.length;
  const successCount = history.filter((h) => h.outcome === "COMPLETED").length;
  const successRate = totalCount > 0 ? Math.round((successCount / totalCount) * 100) : 0;

  if (!isLoading && history.length === 0 && !error) {
    return (
      <VerificationEmptyState
        icon={Clock}
        heading="No verification history"
        description="Credit checks you perform will appear here."
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
        searchPlaceholder="Search by ID number or name..."
        filterDefs={filterDefs}
        emptyTitle="No results"
        emptyDescription="No results match the current filters."
        pageSizeOptions={[10, 20, 50]}
      />
    </div>
  );
}
