"use client";
import Link from "next/link";
import { useEffect, useMemo, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { type Verification, type VerificationStatus } from "@/lib/types";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { BulkOperationsBar, useBulkSelection, VERIFICATION_BULK_ACTIONS } from "@/components/ui/BulkOperations";
import { QuickFilters, type QuickFilter } from "@/components/ui/Filters/QuickFilters";
import { FilterBuilder, type FilterGroup } from "@/components/ui/Filters/FilterBuilder";
import { Filter, ChevronDown, Square, CheckSquare, Minus } from "lucide-react";
import { listVerifications, parseSearchParams } from "@/lib/verification-api";

export default function VerificationsTable() {
  const params = useSearchParams();
  const router = useRouter();
  const [items, setItems] = useState<Verification[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [density, setDensity] = useState<"compact" | "comfortable">("comfortable");
  const [showAdvancedFilters, setShowAdvancedFilters] = useState(false);
  const [quickFilters, setQuickFilters] = useState<QuickFilter[]>([]);
  const [filterGroups, setFilterGroups] = useState<FilterGroup[]>([]);
  
  const page = Number(params.get("page") || 1);
  const pageSize = Number(params.get("pageSize") || 10);

  // Bulk selection
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

  const query = useMemo(() => {
    const sp = new URLSearchParams();
    ["q", "status", "type", "provider", "from", "to", "page", "pageSize"].forEach((k) => {
      const v = params.get(k);
      if (v) sp.set(k, v);
    });
    if (!sp.get("page")) sp.set("page", String(page));
    if (!sp.get("pageSize")) sp.set("pageSize", String(pageSize));
    return sp.toString();
  }, [params, page, pageSize]);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        setLoading(true);
        const data = await listVerifications(parseSearchParams(query));
        if (!cancelled) {
          setItems(data.items);
          setTotal(data.total);
        }
      } catch {
        if (!cancelled) {
          setItems([]);
          setTotal(0);
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [query]);

  const handlePageChange = (newPage: number) => {
    const newParams = new URLSearchParams(params);
    newParams.set("page", String(newPage));
    router.push(`/verifications?${newParams.toString()}`);
  };

  const handlePageSizeChange = (newPageSize: number) => {
    const newParams = new URLSearchParams(params);
    newParams.set("pageSize", String(newPageSize));
    newParams.set("page", "1");
    router.push(`/verifications?${newParams.toString()}`);
  };

  const handleBulkAction = async (actionId: string) => {
    console.log(`Performing bulk action: ${actionId} on ${selectedCount} items`, selectedItems);
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // For demo, just show what would happen
    switch (actionId) {
      case "export":
        alert(`Exporting ${selectedCount} verifications...`);
        break;
      case "retry":
        alert(`Retrying ${selectedCount} verifications...`);
        break;
      case "archive":
        alert(`Archiving ${selectedCount} verifications...`);
        break;
      case "delete":
        alert(`Deleting ${selectedCount} verifications...`);
        break;
    }
    
    deselectAll();
  };

  const removeQuickFilter = (filterId: string) => {
    setQuickFilters(prev => prev.filter(f => f.id !== filterId));
  };

  const clearAllFilters = () => {
    setQuickFilters([]);
  };

  const totalPages = Math.ceil(total / pageSize);

  const getSelectionIcon = () => {
    if (selectedCount === 0) {
      return <Square className="h-4 w-4" />;
    } else if (selectedCount === totalCount) {
      return <CheckSquare className="h-4 w-4 text-accent" />;
    } else {
      return <Minus className="h-4 w-4 text-accent" />;
    }
  };

  const availableFields = [
    { key: "correlationId", label: "Correlation ID", type: "text" as const },
    { key: "partnerId", label: "Partner ID", type: "text" as const },
    { key: "type", label: "Type", type: "select" as const, options: [
      { value: "ID", label: "Home Affairs ID" },
      { value: "IDENTITY", label: "Identity" },
      { value: "DOCUMENT", label: "Document" },
      { value: "AVS", label: "Bank Account" },
      { value: "CREDIT", label: "Credit Check" },
      { value: "INCOME", label: "Income" },
      { value: "TAX", label: "Tax Compliance" },
      { value: "CIPC", label: "Company" },
      { value: "DEEDS", label: "Deeds Registry" },
      { value: "EMPLOYMENT", label: "Employment" },
      { value: "QUALIFICATION", label: "Qualification" },
      { value: "SANCTIONS", label: "Sanctions & PEP" },
      { value: "NEGATIVE_NEWS", label: "Negative News" },
      { value: "FRAUD_WATCHLIST", label: "Fraud Watchlist" },
      { value: "FULL_VERIFICATION", label: "Full Verification" },
      { value: "WATCHLIST", label: "Watchlist" },
    ]},
    { key: "status", label: "Status", type: "select" as const, options: [
      { value: "success", label: "Success" },
      { value: "in_progress", label: "In Progress" },
      { value: "soft_fail", label: "Soft Fail" },
      { value: "hard_fail", label: "Hard Fail" },
      { value: "pending", label: "Pending" },
      { value: "completed", label: "Completed" },
      { value: "transient_error", label: "Transient Error" },
      { value: "permanent_failure", label: "Permanent Failure" },
    ]},
    { key: "provider", label: "Provider", type: "text" as const },
    { key: "startedAt", label: "Started Date", type: "date" as const },
  ];

  return (
    <div className="space-y-4">
      {/* Bulk Operations Bar */}
      <BulkOperationsBar
        selectedCount={selectedCount}
        totalCount={totalCount}
        actions={VERIFICATION_BULK_ACTIONS}
        onAction={handleBulkAction}
        onSelectAll={selectAll}
        onDeselectAll={deselectAll}
        onToggleAll={toggleAll}
        loading={loading}
      />

      {/* Quick Filters */}
      {quickFilters.length > 0 && (
        <QuickFilters
          filters={quickFilters}
          onRemoveFilter={removeQuickFilter}
          onClearAll={clearAllFilters}
        />
      )}

      {/* Main Table Card */}
      <div className="console-card">
        {/* Enhanced Toolbar */}
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
          <div className="flex items-center gap-aws-s">
            <Button
              variant="secondary"
              size="sm"
              onClick={() => setShowAdvancedFilters(!showAdvancedFilters)}
              className="gap-2"
            >
              <Filter className="h-4 w-4" />
              Advanced Filters
              <ChevronDown className={`h-4 w-4 transition-transform ${showAdvancedFilters ? 'rotate-180' : ''}`} />
            </Button>
            <Button
              variant={density === "compact" ? "primary" : "secondary"}
              size="sm"
              onClick={() => setDensity(density === "compact" ? "comfortable" : "compact")}
            >
              {density === "compact" ? "Compact" : "Comfortable"}
            </Button>
          </div>
        </div>

        {/* Advanced Filters */}
        {showAdvancedFilters && (
          <div className="border-b border-border p-4">
            <FilterBuilder
              groups={filterGroups}
              onGroupsChange={setFilterGroups}
              availableFields={availableFields}
            />
          </div>
        )}
        
        {/* Table */}
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
                <th>Type</th>
                <th>Status</th>
                <th>Provider</th>
                <th>Workflow</th>
                <th>Started</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                Array.from({ length: pageSize }).map((_, index) => (
                  <tr key={index}>
                    <td colSpan={8} className="text-center py-aws-xxl text-text-muted">
                      <div className="animate-pulse bg-background h-4 rounded"></div>
                    </td>
                  </tr>
                ))
              ) : items.length === 0 ? (
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
        
        {/* Pagination */}
        {!loading && items.length > 0 && (
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