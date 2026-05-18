"use client";
import Link from "next/link";
import { useVerificationList } from "@/lib/hooks/useVerification";
import { type VerificationListParams } from "@/lib/verification-api";
import { exportVerifications, retryVerifications, archiveVerifications, deleteVerifications } from "@/lib/bff-client";
import { useToast } from "@/components/ui/Toast";
import {
  BulkOperationsBar,
  useBulkSelection,
  VERIFICATION_BULK_ACTIONS,
} from "@/components/ui/BulkOperations";
import { DataGrid, useDataGridUrl, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn } from "@/components/ui/DataGrid";
import type { Verification } from "@/lib/types";

const FILTER_KEYS = ["status", "type", "provider", "from", "to"];

const columns: DataGridColumn<Verification>[] = [
  {
    id: "correlationId",
    header: "Correlation ID",
    cell: (v) => (
      <Link
        href={`/verifications/${v.correlationId}`}
        className="text-accent hover:text-accent-strong font-medium"
        onClick={(e) => e.stopPropagation()}
      >
        {v.correlationId}
      </Link>
    ),
  },
  {
    id: "partnerId",
    header: "Partner",
    cell: (v) => <span className="text-text-muted">{v.partnerId}</span>,
  },
  {
    id: "type",
    header: "Type",
    sortable: true,
    cell: (v) => <span className="font-medium">{v.type}</span>,
  },
  {
    id: "status",
    header: "Status",
    sortable: true,
    cell: (v) => <StatusIndicator status={v.status} />,
  },
  {
    id: "provider",
    header: "Provider",
    sortable: true,
    cell: (v) => <span className="text-text-muted">{v.provider || "\u2014"}</span>,
  },
  {
    id: "workflow",
    header: "Workflow",
    cell: (v) =>
      v.workflowId ? (
        <div>
          <span className="text-text">
            {v.workflowName || v.workflowId}
          </span>
          {v.policyVersion && (
            <div className="text-xs text-text-muted mt-1">
              v{v.policyVersion}
            </div>
          )}
        </div>
      ) : (
        <span className="text-text-muted">{"\u2014"}</span>
      ),
  },
  {
    id: "startedAt",
    header: "Started",
    sortable: true,
    cell: (v) => (
      <span className="text-text-muted">
        {new Date(v.startedAt).toLocaleString()}
      </span>
    ),
  },
];

export default function VerificationsTable() {
  const { toast } = useToast();

  // Build query params from URL state
  const { state, actions, totalPages: urlTotalPages } = useDataGridUrl({
    basePath: "/verifications",
    filterKeys: FILTER_KEYS,
    totalItems: 0, // Will be updated after data loads
    defaults: { pageSize: 10, sortColumn: "startedAt", sortDirection: "desc" },
  });

  const queryParams: VerificationListParams = {
    q: state.searchQuery || undefined,
    status: state.filters.status || undefined,
    type: state.filters.type || undefined,
    provider: state.filters.provider || undefined,
    from: state.filters.from || undefined,
    to: state.filters.to || undefined,
    page: state.currentPage,
    pageSize: state.pageSize,
    sortBy: state.sortColumn || "startedAt",
    sortDir: state.sortDirection,
  };

  const { data, isLoading, error, refetch } = useVerificationList(queryParams);

  const items = (data?.items ?? []) as Verification[];
  const total = data?.total ?? 0;
  const totalPages = Math.max(1, Math.ceil(total / state.pageSize));

  const {
    selectedCount,
    totalCount,
    toggleItem,
    selectAll,
    deselectAll,
    toggleAll,
    isSelected,
    selectedItems,
  } = useBulkSelection(items, (v) => v.correlationId);

  const handleBulkAction = async (actionId: string) => {
    const ids = selectedItems.map((v) => v.correlationId);
    try {
      let result;
      switch (actionId) {
        case "export":
          result = await exportVerifications(ids, "csv");
          break;
        case "retry":
          result = await retryVerifications(ids);
          break;
        case "archive":
          result = await archiveVerifications(ids);
          break;
        case "delete":
          result = await deleteVerifications(ids);
          break;
        default:
          return;
      }
      toast({ title: result.message, variant: "success" });
    } catch (err) {
      toast({
        title: `${actionId} failed`,
        description: err instanceof Error ? err.message : `Could not ${actionId} verifications.`,
        variant: "error",
      });
    }
    deselectAll();
  };

  return (
    <div className="space-y-4">
      <BulkOperationsBar
        selectedCount={selectedCount}
        totalCount={totalCount}
        actions={VERIFICATION_BULK_ACTIONS}
        onAction={handleBulkAction}
        onSelectAll={selectAll}
        onDeselectAll={deselectAll}
        onToggleAll={toggleAll}
        loading={isLoading}
      />

      {error && (
        <div className="console-card console-card--danger text-sm text-danger">
          <div className="console-card-body flex items-center justify-between">
            <span>{error instanceof Error ? error.message : "Failed to load verifications"}</span>
            <button
              onClick={() => refetch()}
              className="rounded border border-danger/40 px-3 py-1 text-xs font-medium hover:bg-danger/10"
            >
              Retry
            </button>
          </div>
        </div>
      )}

      <DataGrid
        columns={columns}
        data={items}
        getRowId={(v) => v.correlationId}
        state={state}
        actions={actions}
        totalPages={totalPages}
        totalItems={total}
        isLoading={isLoading}
        title="Verifications"
        description="Recent verification jobs and their status"
        bulkActions={VERIFICATION_BULK_ACTIONS}
        onBulkAction={handleBulkAction}
        emptyTitle="No verifications found"
        emptyDescription="Try adjusting your filters or search criteria."
        pageSizeOptions={[10, 20, 50, 100]}
        onRefresh={() => refetch()}
      />
    </div>
  );
}
