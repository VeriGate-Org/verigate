"use client";

import * as React from "react";
import { cn } from "@/lib/cn";
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
  TableEmpty,
  TableLoading,
} from "@/components/ui/Table/Table";
import { TablePagination } from "@/components/ui/Table/TablePagination";
import { TableToolbar } from "@/components/ui/Table/TableToolbar";
import {
  BulkOperationsBar,
  useBulkSelection,
  type BulkAction,
} from "@/components/ui/BulkOperations";
import { Square, CheckSquare, AlertCircle } from "lucide-react";
import type {
  DataGridColumn,
  DataGridFilterDef,
  DataGridState,
  DataGridActions,
} from "./DataGridColumn.types";

export interface DataGridProps<TRow> {
  columns: DataGridColumn<TRow>[];
  data: TRow[];
  getRowId: (row: TRow) => string;
  state: DataGridState;
  actions: DataGridActions;
  totalPages: number;
  totalItems: number;

  // Loading / error
  isLoading?: boolean;
  error?: string | null;
  onRetry?: () => void;

  // Toolbar
  title?: string;
  description?: string;
  searchable?: boolean;
  searchPlaceholder?: string;
  filterDefs?: DataGridFilterDef[];
  toolbarActions?: React.ReactNode;

  // Bulk selection
  bulkActions?: BulkAction[];
  onBulkAction?: (actionId: string) => void;

  // Row interaction
  onRowClick?: (row: TRow) => void;
  renderExpandedRow?: (row: TRow) => React.ReactNode;

  // Empty state
  emptyTitle?: string;
  emptyDescription?: string;

  // Pagination
  pageSizeOptions?: number[];

  // Toolbar extras
  onRefresh?: () => void;
  onExport?: () => void;

  // Density
  density?: "compact" | "comfortable";
}

export function DataGrid<TRow>({
  columns,
  data,
  getRowId,
  state,
  actions,
  totalPages,
  totalItems,
  isLoading,
  error,
  onRetry,
  title,
  description,
  searchable,
  searchPlaceholder,
  filterDefs,
  toolbarActions,
  bulkActions,
  onBulkAction,
  onRowClick,
  renderExpandedRow,
  emptyTitle = "No items found",
  emptyDescription = "Try adjusting your filters or search criteria.",
  pageSizeOptions,
  onRefresh,
  onExport,
  density = "comfortable",
}: DataGridProps<TRow>) {
  const [expandedRowId, setExpandedRowId] = React.useState<string | null>(null);

  const bulk = bulkActions
    ? // eslint-disable-next-line react-hooks/rules-of-hooks
      useBulkSelection(data, getRowId)
    : null;

  const visibleColumns = React.useMemo(
    () => columns.filter((c) => !c.hidden),
    [columns],
  );
  const colSpan = visibleColumns.length + (bulkActions ? 1 : 0);

  const filterNodes = filterDefs?.length ? (
    <>
      {filterDefs.map((fd) => (
        <select
          key={fd.id}
          value={state.filters[fd.id] || ""}
          onChange={(e) => actions.setFilter(fd.id, e.target.value)}
          className="aws-select text-sm"
        >
          <option value="">{fd.label}</option>
          {fd.options?.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      ))}
    </>
  ) : undefined;

  const handleRowClick = React.useCallback(
    (row: TRow) => {
      if (renderExpandedRow) {
        const rowId = getRowId(row);
        setExpandedRowId((prev) => (prev === rowId ? null : rowId));
      } else if (onRowClick) {
        onRowClick(row);
      }
    },
    [renderExpandedRow, onRowClick, getRowId],
  );

  const isRowClickable = !!onRowClick || !!renderExpandedRow;

  return (
    <div className="space-y-4">
      {/* Bulk operations bar */}
      {bulk && bulkActions && onBulkAction && bulk.selectedCount > 0 && (
        <BulkOperationsBar
          selectedCount={bulk.selectedCount}
          totalCount={bulk.totalCount}
          actions={bulkActions}
          onAction={onBulkAction}
          onSelectAll={bulk.selectAll}
          onDeselectAll={bulk.deselectAll}
          onToggleAll={bulk.toggleAll}
          loading={isLoading}
        />
      )}

      {/* Error banner */}
      {error && (
        <div className="rounded-lg border border-danger/40 bg-danger/5 p-4 flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-danger mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <p className="text-sm font-medium text-danger">
              Failed to load data
            </p>
            <p className="text-sm text-danger/80 mt-1">{error}</p>
            {onRetry && (
              <button
                onClick={onRetry}
                className="mt-3 px-3 py-1.5 text-sm bg-danger/10 hover:bg-danger/20 text-danger rounded-md border border-danger/30"
              >
                Retry
              </button>
            )}
          </div>
        </div>
      )}

      {/* Card with toolbar + table + pagination */}
      <div>
        {(title ||
          searchable ||
          filterDefs?.length ||
          toolbarActions ||
          onRefresh ||
          onExport) && (
          <TableToolbar
            title={title}
            description={description}
            searchValue={searchable ? state.searchQuery : undefined}
            onSearchChange={searchable ? actions.setSearchQuery : undefined}
            searchPlaceholder={searchPlaceholder}
            onRefresh={onRefresh}
            onExport={onExport}
            isLoading={isLoading}
            itemCount={totalItems}
            selectedCount={bulk?.selectedCount}
            actions={toolbarActions}
            filters={filterNodes}
          />
        )}

        <div className="[&>div]:rounded-none [&>div]:border-x-0 [&>div]:border-b-0">
          <Table
            density={density}
            sortColumn={state.sortColumn}
            sortDirection={state.sortDirection}
            onSort={actions.setSort}
          >
            <TableHeader>
              <TableRow>
                {bulkActions && bulk && (
                  <TableHead width="48px">
                    <button
                      onClick={bulk.toggleAll}
                      className="flex items-center justify-center w-full"
                    >
                      {bulk.selectedCount === 0 ? (
                        <Square className="h-4 w-4" />
                      ) : bulk.isAllSelected ? (
                        <CheckSquare className="h-4 w-4 text-accent" />
                      ) : (
                        <CheckSquare className="h-4 w-4 text-accent opacity-60" />
                      )}
                    </button>
                  </TableHead>
                )}
                {visibleColumns.map((col) => (
                  <TableHead
                    key={col.id}
                    sortKey={col.id}
                    sortable={col.sortable}
                    width={col.width}
                    className={cn(
                      col.align === "right" && "text-right",
                      col.align === "center" && "text-center",
                      col.className,
                    )}
                  >
                    {col.header}
                  </TableHead>
                ))}
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableLoading colSpan={colSpan} />
              ) : data.length === 0 && !error ? (
                <TableEmpty colSpan={colSpan}>
                  <p className="text-text-muted font-medium mb-1">
                    {emptyTitle}
                  </p>
                  <p className="text-sm text-text-muted">{emptyDescription}</p>
                </TableEmpty>
              ) : (
                data.map((row, idx) => {
                  const rowId = getRowId(row);
                  const isExpanded = expandedRowId === rowId;
                  return (
                    <React.Fragment key={rowId}>
                      <TableRow
                        clickable={isRowClickable}
                        selected={bulk?.isSelected(rowId)}
                        onClick={() => handleRowClick(row)}
                      >
                        {bulkActions && bulk && (
                          <TableCell
                            width="48px"
                            onClick={(e) => {
                              e.stopPropagation();
                              bulk.toggleItem(rowId);
                            }}
                          >
                            <button className="flex items-center justify-center w-full">
                              {bulk.isSelected(rowId) ? (
                                <CheckSquare className="h-4 w-4 text-accent" />
                              ) : (
                                <Square className="h-4 w-4" />
                              )}
                            </button>
                          </TableCell>
                        )}
                        {visibleColumns.map((col) => (
                          <TableCell
                            key={col.id}
                            width={col.width}
                            className={cn(
                              col.align === "right" && "text-right",
                              col.align === "center" && "text-center",
                              col.className,
                            )}
                          >
                            <div>{col.cell(row, idx)}</div>
                            {col.secondary && (
                              <div className="text-xs text-text-muted mt-0.5">
                                {col.secondary(row, idx)}
                              </div>
                            )}
                          </TableCell>
                        ))}
                      </TableRow>
                      {isExpanded && renderExpandedRow && (
                        <TableRow>
                          <TableCell
                            colSpan={colSpan}
                            className="bg-base-200/30 p-4"
                          >
                            {renderExpandedRow(row)}
                          </TableCell>
                        </TableRow>
                      )}
                    </React.Fragment>
                  );
                })
              )}
            </TableBody>
          </Table>
        </div>

        {pageSizeOptions && !isLoading && data.length > 0 && (
          <TablePagination
            currentPage={state.currentPage}
            totalPages={totalPages}
            totalItems={totalItems}
            pageSize={state.pageSize}
            onPageChange={actions.setCurrentPage}
            onPageSizeChange={actions.setPageSize}
            pageSizeOptions={pageSizeOptions}
          />
        )}
      </div>
    </div>
  );
}
