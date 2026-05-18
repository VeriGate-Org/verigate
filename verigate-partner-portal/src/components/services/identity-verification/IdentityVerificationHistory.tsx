"use client";

import { useState, useEffect } from "react";
import { generateIdentityVerificationHistory } from "@/lib/mock-services";
import type { IdentityVerificationHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getIdentityVerificationHistory } from "@/lib/bff-client";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Clock } from "lucide-react";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

const columns: DataGridColumn<IdentityVerificationHistoryItem>[] = [
  { id: "outcome", header: "Status", cell: (row) => <StatusIndicator status={row.outcome} /> },
  { id: "idNumber", header: "ID Number", cell: (row) => <span className="font-mono text-xs">{row.idNumber}</span> },
  { id: "fullName", header: "Name", cell: (row) => row.fullName },
  { id: "verifiedAt", header: "Date", cell: (row) => new Date(row.verifiedAt).toLocaleDateString() },
];

const filterDefs: DataGridFilterDef[] = [
  {
    id: "outcome",
    label: "All Outcomes",
    type: "select",
    options: [
      { value: "VERIFIED", label: "Verified" },
      { value: "NOT_FOUND", label: "Not Found" },
      { value: "DECEASED", label: "Deceased" },
      { value: "FAILED", label: "Failed" },
    ],
  },
];

export default function IdentityVerificationHistory() {
  const [history, setHistory] = useState<IdentityVerificationHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 10 });

  const fetchHistory = () => {
    if (config.useMockServices) {
      setHistory(generateIdentityVerificationHistory());
      return;
    }
    setIsLoading(true);
    setError(null);
    getIdentityVerificationHistory({ limit: 200 })
      .then((res) => {
        const items = res.items as unknown as IdentityVerificationHistoryItem[];
        setHistory(items.length > 0 ? items : generateIdentityVerificationHistory());
      })
      .catch(() => {
        setHistory(generateIdentityVerificationHistory());
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
  const verifiedCount = history.filter((h) => h.outcome === "VERIFIED").length;
  const notFoundCount = history.filter((h) => h.outcome === "NOT_FOUND").length;
  const deceasedCount = history.filter((h) => h.outcome === "DECEASED").length;
  const failedCount = history.filter((h) => h.outcome === "FAILED").length;

  if (!isLoading && history.length === 0 && !error) {
    return (
      <VerificationEmptyState
        icon={Clock}
        heading="No verification history"
        description="Identity verifications you perform will appear here."
      />
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex gap-3 flex-wrap">
        <SummaryChip label="Total" count={totalCount} className="bg-accent/10 text-accent" />
        <SummaryChip label="Verified" count={verifiedCount} className="bg-success/10 text-success" />
        <SummaryChip label="Not Found" count={notFoundCount} className="bg-warning/10 text-warning" />
        <SummaryChip label="Deceased" count={deceasedCount} className="bg-danger/10 text-danger" />
        <SummaryChip label="Failed" count={failedCount} className="bg-danger/10 text-danger" />
      </div>
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

function SummaryChip({ label, count, className }: { label: string; count: number; className: string }) {
  return (
    <div className={`px-3 py-1.5 rounded-lg text-sm font-medium ${className}`}>
      {label}: {count}
    </div>
  );
}
