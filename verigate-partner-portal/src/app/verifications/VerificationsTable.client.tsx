"use client";
import Link from "next/link";
import { useMemo } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { type VerificationStatus } from "@/lib/types";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { BulkOperationsBar, useBulkSelection, VERIFICATION_BULK_ACTIONS } from "@/components/ui/BulkOperations";
import { Square, CheckSquare, Minus, ChevronUp, ChevronDown } from "lucide-react";
import { useVerificationList } from "@/lib/hooks/useVerification";
import { type VerificationListParams } from "@/lib/verification-api";
import { exportVerifications, retryVerifications, archiveVerifications, deleteVerifications } from "@/lib/bff-client";
import { useToast } from "@/components/ui/Toast";

function parseParams(params: URLSearchParams): VerificationListParams {
  return {
    q: params.get("q") || undefined,
    status: params.get("status") || undefined,
    type: params.get("type") || undefined,
    provider: params.get("provider") || undefined,
    from: params.get("from") || undefined,
    to: params.get("to") || undefined,
    page: params.has("page") ? Number(params.get("page")) : 1,
    pageSize: params.has("pageSize") ? Number(params.get("pageSize")) : 10,
    sortBy: params.get("sortBy") || "startedAt",
    sortDir: params.get("sortDir") || "desc",
  };
}

type SortableKey = "startedAt" | "status" | "type" | "provider";

export default function VerificationsTable() {
  const params = useSearchParams();
  const router = useRouter();
  const { toast } = useToast();

  const queryParams = useMemo(() => parseParams(params), [params]);
  const { data, isLoading, error, refetch } = useVerificationList(queryParams);

  const items = data?.items ?? [];
  const total = data?.total ?? 0;
  const page = queryParams.page || 1;
  const pageSize = queryParams.pageSize || 10;
  const sortBy = queryParams.sortBy || "startedAt";
  const sortDir = queryParams.sortDir || "desc";
  const totalPages = Math.ceil(total / pageSize);

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

  const navigate = (updates: Record<string, string>) => {
    const sp = new URLSearchParams(params);
    for (const [k, v] of Object.entries(updates)) {
      if (v) sp.set(k, v);
      else sp.delete(k);
    }
    router.push(`/verifications?${sp.toString()}`);
  };

  const handleSort = (column: SortableKey) => {
    const newDir = sortBy === column && sortDir === "asc" ? "desc" : "asc";
    navigate({ sortBy: column, sortDir: newDir, page: "1" });
  };

  const handlePageChange = (newPage: number) => {
    navigate({ page: String(newPage) });
  };

  const handlePageSizeChange = (newPageSize: number) => {
    navigate({ pageSize: String(newPageSize), page: "1" });
  };

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

  const getSelectionIcon = () => {
    if (selectedCount === 0) return <Square className="h-4 w-4" />;
    if (selectedCount === totalCount) return <CheckSquare className="h-4 w-4 text-accent" />;
    return <Minus className="h-4 w-4 text-accent" />;
  };

  const renderSortIcon = (column: SortableKey) => {
    if (sortBy !== column) return null;
    return sortDir === "asc"
      ? <ChevronUp className="inline h-3.5 w-3.5 ml-1" />
      : <ChevronDown className="inline h-3.5 w-3.5 ml-1" />;
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
        <div className="console-card border-danger/40 bg-danger/5 text-sm text-danger">
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

      <div className="console-card">
        <div className="console-card-header">
          <div>
            <h2 className="text-aws-heading-s font-medium text-text">
              Verifications
              <span className="ml-aws-s text-text-muted font-normal">
                ({total.toLocaleString()})
              </span>
            </h2>
            <p className="text-aws-body text-text-muted mt-aws-2xs">
              Recent verification jobs and their status
            </p>
          </div>
        </div>

        <div className="overflow-hidden">
          <table className="aws-table">
            <thead>
              <tr>
                <th className="w-12">
                  <button
                    onClick={toggleAll}
                    className="flex items-center justify-center w-full h-full"
                  >
                    {getSelectionIcon()}
                  </button>
                </th>
                <th>Correlation ID</th>
                <th>Partner</th>
                <th>
                  <button onClick={() => handleSort("type")} className="inline-flex items-center hover:text-text">
                    Type{renderSortIcon("type")}
                  </button>
                </th>
                <th>
                  <button onClick={() => handleSort("status")} className="inline-flex items-center hover:text-text">
                    Status{renderSortIcon("status")}
                  </button>
                </th>
                <th>
                  <button onClick={() => handleSort("provider")} className="inline-flex items-center hover:text-text">
                    Provider{renderSortIcon("provider")}
                  </button>
                </th>
                <th>Workflow</th>
                <th>
                  <button onClick={() => handleSort("startedAt")} className="inline-flex items-center hover:text-text">
                    Started{renderSortIcon("startedAt")}
                  </button>
                </th>
              </tr>
            </thead>
            <tbody>
              {isLoading ? (
                Array.from({ length: pageSize }).map((_, index) => (
                  <tr key={index}>
                    <td colSpan={8} className="text-center py-aws-xxl text-text-muted">
                      <div className="animate-pulse bg-background h-4 rounded" />
                    </td>
                  </tr>
                ))
              ) : items.length === 0 && !error ? (
                <tr>
                  <td colSpan={8} className="text-center py-aws-xxl text-text-muted">
                    <p className="text-text-muted mb-aws-s">No verifications found</p>
                    <p className="text-aws-body text-text-muted">
                      Try adjusting your filters or search criteria.
                    </p>
                  </td>
                </tr>
              ) : (
                items.map((verification) => (
                  <tr key={verification.correlationId}>
                    <td>
                      <button
                        onClick={() => toggleItem(verification.correlationId)}
                        className="flex items-center justify-center w-full h-full"
                      >
                        {isSelected(verification.correlationId) ? (
                          <CheckSquare className="h-4 w-4 text-accent" />
                        ) : (
                          <Square className="h-4 w-4" />
                        )}
                      </button>
                    </td>
                    <td>
                      <Link
                        href={`/verifications/${verification.correlationId}`}
                        className="text-accent hover:text-accent-strong font-medium"
                      >
                        {verification.correlationId}
                      </Link>
                    </td>
                    <td>
                      <span className="text-text-muted">{verification.partnerId}</span>
                    </td>
                    <td>
                      <span className="font-medium">{verification.type}</span>
                    </td>
                    <td>
                      <StatusBadge status={verification.status} />
                    </td>
                    <td>
                      <span className="text-text-muted">{verification.provider || "—"}</span>
                    </td>
                    <td>
                      {verification.workflowId ? (
                        <div>
                          <span className="text-text">
                            {verification.workflowName || verification.workflowId}
                          </span>
                          {verification.policyVersion && (
                            <div className="text-xs text-text-muted mt-1">
                              v{verification.policyVersion}
                            </div>
                          )}
                        </div>
                      ) : (
                        <span className="text-text-muted">—</span>
                      )}
                    </td>
                    <td>
                      <span className="text-text-muted">
                        {new Date(verification.startedAt).toLocaleString()}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {!isLoading && items.length > 0 && (
          <div className="flex items-center justify-between px-aws-l py-aws-m border-t border-border">
            <div className="flex items-center gap-aws-l">
              <div className="flex items-center gap-aws-s">
                <span className="text-aws-body text-text-muted">Rows per page:</span>
                <select
                  value={pageSize}
                  onChange={(e) => handlePageSizeChange(Number(e.target.value))}
                  className="aws-select text-aws-body"
                >
                  <option value={10}>10</option>
                  <option value={20}>20</option>
                  <option value={50}>50</option>
                  <option value={100}>100</option>
                </select>
              </div>

              <div className="text-aws-body text-text-muted">
                Showing {((page - 1) * pageSize + 1).toLocaleString()}-{Math.min(page * pageSize, total).toLocaleString()} of{" "}
                {total.toLocaleString()} items
              </div>
            </div>

            {totalPages > 1 && (
              <div className="flex items-center gap-aws-xs">
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => handlePageChange(page - 1)}
                  disabled={page === 1}
                  className="px-aws-s"
                >
                  Previous
                </Button>

                <span className="px-aws-m text-aws-body">
                  Page {page} of {totalPages}
                </span>

                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => handlePageChange(page + 1)}
                  disabled={page === totalPages}
                  className="px-aws-s"
                >
                  Next
                </Button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

function StatusBadge({ status }: { status: VerificationStatus }) {
  const variantMap: Record<VerificationStatus, "success" | "warning" | "danger" | "info" | "pending"> = {
    success: "success",
    completed: "success",
    in_progress: "info",
    pending: "pending",
    soft_fail: "warning",
    transient_error: "warning",
    hard_fail: "danger",
    permanent_failure: "danger",
  };

  const labelMap: Record<VerificationStatus, string> = {
    success: "Success",
    completed: "Completed",
    in_progress: "In Progress",
    pending: "Pending",
    soft_fail: "Soft Fail",
    transient_error: "Transient Error",
    hard_fail: "Hard Fail",
    permanent_failure: "Permanent Failure",
  };

  return (
    <Badge variant={variantMap[status]} size="sm">
      {labelMap[status]}
    </Badge>
  );
}
