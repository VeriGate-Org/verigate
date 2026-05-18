"use client";

import { useState, useEffect } from "react";
import { generateTaxComplianceHistory } from "@/lib/mock-services";
import type { TaxComplianceHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getTaxComplianceHistory } from "@/lib/bff-client";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Clock } from "lucide-react";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

const columns: DataGridColumn<TaxComplianceHistoryItem>[] = [
  {
    id: "outcome",
    header: "Status",
    cell: (row) => <StatusIndicator status={row.outcome} />,
  },
  {
    id: "taxNumber",
    header: "Tax Number",
    cell: (row) => <span className="font-mono text-xs">{row.taxNumber}</span>,
  },
  {
    id: "entityName",
    header: "Entity Name",
    cell: (row) => row.entityName,
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
      { value: "COMPLIANT", label: "Compliant" },
      { value: "NON_COMPLIANT", label: "Non-Compliant" },
      { value: "FAILED", label: "Failed" },
    ],
  },
];

export default function TaxComplianceHistory() {
  const [history, setHistory] = useState<TaxComplianceHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 10 });

  const fetchHistory = () => {
    if (config.useMockServices) {
      setHistory(generateTaxComplianceHistory());
      return;
    }
    setIsLoading(true);
    setError(null);
    getTaxComplianceHistory({ limit: 200 })
      .then((res) => {
        const items = res.items as unknown as TaxComplianceHistoryItem[];
        setHistory(items.length > 0 ? items : generateTaxComplianceHistory());
      })
      .catch(() => setHistory(generateTaxComplianceHistory()))
      .finally(() => setIsLoading(false));
  };

  useEffect(() => { fetchHistory(); }, []);

  const { data, total, totalPages } = processData(history, {
    searchFields: ["taxNumber", "entityName"],
    filterFn: (item, filters) => !filters.outcome || item.outcome === filters.outcome,
  });

  const totalCount = history.length;
  const successCount = history.filter((h) => h.outcome === "COMPLIANT").length;
  const successRate = totalCount > 0 ? Math.round((successCount / totalCount) * 100) : 0;

  if (!isLoading && history.length === 0 && !error) {
    return (
      <VerificationEmptyState
        icon={Clock}
        heading="No verification history"
        description="Tax compliance checks you perform will appear here."
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
        searchPlaceholder="Search by tax number or entity name..."
        filterDefs={filterDefs}
        emptyTitle="No results"
        emptyDescription="No results match the current filters."
        pageSizeOptions={[10, 20, 50]}
      />
    </div>
  );
}
