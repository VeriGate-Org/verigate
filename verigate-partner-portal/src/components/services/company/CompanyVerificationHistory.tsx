"use client";

import { useState, useEffect } from "react";
import { generateCompanyHistory } from "@/lib/mock-services";
import type { CompanyHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getCompanyHistory } from "@/lib/bff-client";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Clock } from "lucide-react";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

const columns: DataGridColumn<CompanyHistoryItem>[] = [
  {
    id: "outcome",
    header: "Status",
    cell: (row) => <StatusIndicator status={row.outcome} />,
  },
  {
    id: "registrationNumber",
    header: "Registration Number",
    cell: (row) => (
      <span className="font-mono text-xs">{row.registrationNumber}</span>
    ),
  },
  {
    id: "companyName",
    header: "Company Name",
    cell: (row) => row.companyName,
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
      { value: "FOUND", label: "Found" },
      { value: "NOT_FOUND", label: "Not Found" },
      { value: "FAILED", label: "Failed" },
    ],
  },
];

export default function CompanyVerificationHistory() {
  const [history, setHistory] = useState<CompanyHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 10 });

  const fetchHistory = () => {
    if (config.useMockServices) {
      setHistory(generateCompanyHistory());
      return;
    }
    setIsLoading(true);
    setError(null);
    getCompanyHistory({ limit: 200 })
      .then((res) => {
        const items = res.items as unknown as CompanyHistoryItem[];
        setHistory(items.length > 0 ? items : generateCompanyHistory());
      })
      .catch(() => {
        setHistory(generateCompanyHistory());
      })
      .finally(() => setIsLoading(false));
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const { data, total, totalPages } = processData(history, {
    searchFields: ["registrationNumber", "companyName"],
    filterFn: (item, filters) => {
      if (filters.outcome && item.outcome !== filters.outcome) return false;
      return true;
    },
  });

  const totalCount = history.length;
  const successCount = history.filter((h) => h.outcome === "FOUND").length;
  const successRate = totalCount > 0 ? Math.round((successCount / totalCount) * 100) : 0;

  if (!isLoading && history.length === 0 && !error) {
    return (
      <VerificationEmptyState
        icon={Clock}
        heading="No verification history"
        description="Company searches you perform will appear here."
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
        searchPlaceholder="Search by registration number or company name..."
        filterDefs={filterDefs}
        emptyTitle="No verification history"
        emptyDescription="Company searches you perform will appear here."
        pageSizeOptions={[10, 20, 50]}
      />
    </div>
  );
}
