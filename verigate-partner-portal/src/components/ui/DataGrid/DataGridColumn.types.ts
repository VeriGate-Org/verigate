import { type ReactNode } from "react";

export interface DataGridColumn<TRow> {
  id: string;
  header: string;
  cell: (row: TRow, index: number) => ReactNode;
  secondary?: (row: TRow, index: number) => ReactNode;
  sortable?: boolean;
  width?: string;
  align?: "left" | "center" | "right";
  className?: string;
  hidden?: boolean;
}

export interface DataGridFilterDef {
  id: string;
  label: string;
  type: "select" | "search" | "date-range";
  options?: Array<{ value: string; label: string }>;
  placeholder?: string;
}

export type DataGridFilters = Record<string, string>;

export interface DataGridState {
  currentPage: number;
  pageSize: number;
  sortColumn: string;
  sortDirection: "asc" | "desc";
  searchQuery: string;
  filters: DataGridFilters;
}

export interface DataGridActions {
  setCurrentPage: (page: number) => void;
  setPageSize: (size: number) => void;
  setSort: (column: string) => void;
  setSearchQuery: (query: string) => void;
  setFilter: (key: string, value: string) => void;
  resetFilters: () => void;
}

export interface ProcessDataOptions<TRow> {
  searchFields?: string[];
  searchFn?: (item: TRow, query: string) => boolean;
  filterFn?: (item: TRow, filters: DataGridFilters) => boolean;
  sortFn?: (a: TRow, b: TRow, column: string, direction: "asc" | "desc") => number;
}

export interface ProcessDataResult<TRow> {
  data: TRow[];
  total: number;
  totalPages: number;
}
