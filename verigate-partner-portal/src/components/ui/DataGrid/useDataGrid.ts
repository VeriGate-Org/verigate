"use client";

import { useState, useCallback, useMemo } from "react";
import type {
  DataGridState,
  DataGridActions,
  DataGridFilters,
  ProcessDataOptions,
  ProcessDataResult,
} from "./DataGridColumn.types";

interface UseDataGridOptions {
  defaultPageSize?: number;
  defaultSortColumn?: string;
  defaultSortDirection?: "asc" | "desc";
}

export function useDataGrid(options?: UseDataGridOptions) {
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSizeState] = useState(options?.defaultPageSize ?? 10);
  const [sortColumn, setSortColumn] = useState(options?.defaultSortColumn ?? "");
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">(
    options?.defaultSortDirection ?? "asc",
  );
  const [searchQuery, setSearchQueryState] = useState("");
  const [filters, setFiltersState] = useState<DataGridFilters>({});

  const setPageSize = useCallback((size: number) => {
    setPageSizeState(size);
    setCurrentPage(1);
  }, []);

  const setSort = useCallback(
    (column: string) => {
      if (sortColumn === column) {
        setSortDirection((d) => (d === "asc" ? "desc" : "asc"));
      } else {
        setSortColumn(column);
        setSortDirection("asc");
      }
      setCurrentPage(1);
    },
    [sortColumn],
  );

  const setSearchQuery = useCallback((q: string) => {
    setSearchQueryState(q);
    setCurrentPage(1);
  }, []);

  const setFilter = useCallback((key: string, value: string) => {
    setFiltersState((prev) => ({ ...prev, [key]: value }));
    setCurrentPage(1);
  }, []);

  const resetFilters = useCallback(() => {
    setFiltersState({});
    setSearchQueryState("");
    setCurrentPage(1);
  }, []);

  const state: DataGridState = useMemo(
    () => ({ currentPage, pageSize, sortColumn, sortDirection, searchQuery, filters }),
    [currentPage, pageSize, sortColumn, sortDirection, searchQuery, filters],
  );

  const actions: DataGridActions = useMemo(
    () => ({
      setCurrentPage,
      setPageSize,
      setSort,
      setSearchQuery,
      setFilter,
      resetFilters,
    }),
    [setPageSize, setSort, setSearchQuery, setFilter, resetFilters],
  );

  const processData = useCallback(
    <TRow>(items: TRow[], opts: ProcessDataOptions<TRow>): ProcessDataResult<TRow> => {
      let result = [...items];

      // Search
      if (searchQuery) {
        const q = searchQuery.toLowerCase();
        if (opts.searchFn) {
          result = result.filter((item) => opts.searchFn!(item, q));
        } else if (opts.searchFields) {
          result = result.filter((item) =>
            opts.searchFields!.some((field) => {
              const val = (item as Record<string, unknown>)[field];
              return val != null && String(val).toLowerCase().includes(q);
            }),
          );
        }
      }

      // Filter
      if (opts.filterFn) {
        result = result.filter((item) => opts.filterFn!(item, filters));
      }

      // Sort
      if (sortColumn) {
        result.sort((a, b) => {
          if (opts.sortFn) return opts.sortFn(a, b, sortColumn, sortDirection);
          const aVal = String((a as Record<string, unknown>)[sortColumn] ?? "");
          const bVal = String((b as Record<string, unknown>)[sortColumn] ?? "");
          const cmp = aVal.localeCompare(bVal);
          return sortDirection === "asc" ? cmp : -cmp;
        });
      }

      const total = result.length;
      const totalPages = Math.max(1, Math.ceil(total / pageSize));

      // Paginate
      const start = (currentPage - 1) * pageSize;
      result = result.slice(start, start + pageSize);

      return { data: result, total, totalPages };
    },
    [searchQuery, filters, sortColumn, sortDirection, currentPage, pageSize],
  );

  return { state, actions, processData };
}
