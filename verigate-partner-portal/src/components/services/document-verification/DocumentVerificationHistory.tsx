"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { generateDocumentVerificationHistory } from "@/lib/mock-services";
import type { DocumentVerificationHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getDocumentVerificationHistory } from "@/lib/bff-client";
import { DOCUMENT_TYPE_LABELS } from "@/components/services/document-verification/documentFieldConfigs";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Clock } from "lucide-react";
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn, DataGridFilterDef } from "@/components/ui/DataGrid";

const columns: DataGridColumn<DocumentVerificationHistoryItem>[] = [
  {
    id: "outcome",
    header: "",
    width: "3rem",
    cell: (row) => <StatusIndicator status={row.outcome} iconOnly />,
  },
  {
    id: "documentTypeLabel",
    header: "Document Type",
    cell: (row) => row.documentTypeLabel,
  },
  {
    id: "documentNumber",
    header: "Document Number",
    cell: (row) => (
      <span className="font-mono text-xs text-text-muted">
        {row.documentNumber}
      </span>
    ),
  },
  {
    id: "verifiedAt",
    header: "Date",
    cell: (row) => new Date(row.verifiedAt).toLocaleDateString(),
  },
];

export default function DocumentVerificationHistory() {
  const [history, setHistory] = useState<DocumentVerificationHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();
  const { state, actions, processData } = useDataGrid({ defaultPageSize: 10 });

  const fetchHistory = () => {
    if (config.useMockServices) {
      setHistory(generateDocumentVerificationHistory());
      return;
    }
    setIsLoading(true);
    setError(null);
    getDocumentVerificationHistory({ limit: 200 })
      .then((res) => {
        const items = res.items as unknown as DocumentVerificationHistoryItem[];
        setHistory(items.length > 0 ? items : generateDocumentVerificationHistory());
      })
      .catch(() => {
        setHistory(generateDocumentVerificationHistory());
      })
      .finally(() => setIsLoading(false));
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const uniqueDocTypes = Array.from(new Set(history.map((h) => h.documentType)));

  const allFilterDefs: DataGridFilterDef[] = [
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
    {
      id: "docType",
      label: "All Document Types",
      type: "select",
      options: uniqueDocTypes.map((dt) => ({
        value: dt,
        label: DOCUMENT_TYPE_LABELS[dt] ?? dt,
      })),
    },
  ];

  const { data, total, totalPages } = processData(history, {
    searchFields: ["documentNumber"],
    filterFn: (item, filters) => {
      if (filters.outcome && item.outcome !== filters.outcome) return false;
      if (filters.docType && item.documentType !== filters.docType) return false;
      return true;
    },
  });

  const totalCount = history.length;
  const successCount = history.filter((h) => h.outcome === "VERIFIED").length;
  const successRate = totalCount > 0 ? Math.round((successCount / totalCount) * 100) : 0;

  if (!isLoading && history.length === 0 && !error) {
    return (
      <VerificationEmptyState
        icon={Clock}
        heading="No verification history"
        description="Document verifications you perform will appear here."
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
        searchPlaceholder="Search by document number..."
        filterDefs={allFilterDefs}
        onRowClick={(row) => router.push(`/services/document-verification/${row.verificationId}`)}
        emptyTitle="No verification history"
        emptyDescription="Document verifications you perform will appear here."
        pageSizeOptions={[10, 20, 50]}
      />
    </div>
  );
}
