"use client";

import { useMemo, useCallback } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import type { DataGridState, DataGridActions } from "./DataGridColumn.types";

interface UseDataGridUrlOptions {
  basePath: string;
  filterKeys?: string[];
  totalItems: number;
  defaults?: {
    pageSize?: number;
    sortColumn?: string;
    sortDirection?: "asc" | "desc";
  };
}

export function useDataGridUrl(options: UseDataGridUrlOptions) {
  const params = useSearchParams();
  const router = useRouter();

  const {
    basePath,
    filterKeys = [],
    totalItems,
    defaults,
  } = options;

  const state: DataGridState = useMemo(() => {
    const filtersObj: Record<string, string> = {};
    for (const key of filterKeys) {
      filtersObj[key] = params.get(key) || "";
    }
    return {
      currentPage: params.has("page") ? Number(params.get("page")) : 1,
      pageSize: params.has("pageSize")
        ? Number(params.get("pageSize"))
        : defaults?.pageSize ?? 10,
      sortColumn: params.get("sortBy") || defaults?.sortColumn || "",
      sortDirection:
        (params.get("sortDir") as "asc" | "desc") ||
        defaults?.sortDirection ||
        "desc",
      searchQuery: params.get("q") || "",
      filters: filtersObj,
    };
  }, [params, filterKeys, defaults]);

  const totalPages = useMemo(
    () => Math.max(1, Math.ceil(totalItems / state.pageSize)),
    [totalItems, state.pageSize],
  );

  const navigate = useCallback(
    (updates: Record<string, string>) => {
      const sp = new URLSearchParams(params.toString());
      for (const [k, v] of Object.entries(updates)) {
        if (v) sp.set(k, v);
        else sp.delete(k);
      }
      router.push(`${basePath}?${sp.toString()}`);
    },
    [params, router, basePath],
  );

  const actions: DataGridActions = useMemo(
    () => ({
      setCurrentPage: (page: number) => navigate({ page: String(page) }),
      setPageSize: (size: number) =>
        navigate({ pageSize: String(size), page: "1" }),
      setSort: (column: string) => {
        const newDir =
          state.sortColumn === column && state.sortDirection === "asc"
            ? "desc"
            : "asc";
        navigate({ sortBy: column, sortDir: newDir, page: "1" });
      },
      setSearchQuery: (q: string) => navigate({ q, page: "1" }),
      setFilter: (key: string, value: string) =>
        navigate({ [key]: value, page: "1" }),
      resetFilters: () => {
        const updates: Record<string, string> = { q: "", page: "1" };
        for (const key of filterKeys) {
          updates[key] = "";
        }
        navigate(updates);
      },
    }),
    [navigate, state.sortColumn, state.sortDirection, filterKeys],
  );

  return { state, actions, totalPages };
}
