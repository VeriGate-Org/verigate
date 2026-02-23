import * as React from "react";
import { cn } from "@/lib/cn";
import { Button } from "../Button";
import { Search, Filter, Download, RefreshCw, Settings2 } from "lucide-react";

interface TableToolbarProps {
  title?: string;
  description?: string;
  searchValue?: string;
  onSearchChange?: (value: string) => void;
  searchPlaceholder?: string;
  onRefresh?: () => void;
  onExport?: () => void;
  onFilter?: () => void;
  onSettings?: () => void;
  isLoading?: boolean;
  itemCount?: number;
  selectedCount?: number;
  actions?: React.ReactNode;
  filters?: React.ReactNode;
  className?: string;
}

export const TableToolbar: React.FC<TableToolbarProps> = ({
  title,
  description,
  searchValue = "",
  onSearchChange,
  searchPlaceholder = "Search...",
  onRefresh,
  onExport,
  onFilter,
  onSettings,
  isLoading,
  itemCount,
  selectedCount,
  actions,
  filters,
  className,
}) => {
  return (
    <div className={cn("console-card-header flex-col gap-aws-m", className)}>
      {/* Header row */}
      <div className="flex w-full items-center justify-between">
        <div className="flex-1 min-w-0">
          {title && (
            <h2 className="text-aws-heading-s font-medium text-text truncate">
              {title}
              {itemCount !== undefined && (
                <span className="ml-aws-s text-text-muted font-normal">
                  ({itemCount.toLocaleString()})
                </span>
              )}
            </h2>
          )}
          {description && (
            <p className="text-aws-body text-text-muted mt-aws-2xs">{description}</p>
          )}
          {selectedCount !== undefined && selectedCount > 0 && (
            <div className="mt-aws-xs">
              <span className="text-aws-body text-accent font-medium">
                {selectedCount} item{selectedCount !== 1 ? "s" : ""} selected
              </span>
            </div>
          )}
        </div>

        <div className="flex items-center gap-aws-s ml-aws-m">
          {onRefresh && (
            <Button
              variant="secondary"
              size="sm"
              onClick={onRefresh}
              disabled={isLoading}
              className="aws-button"
            >
              <RefreshCw className={cn("h-4 w-4", { "animate-spin": isLoading })} />
              <span className="sr-only">Refresh</span>
            </Button>
          )}
          
          {onExport && (
            <Button
              variant="secondary"
              size="sm"
              onClick={onExport}
              className="aws-button"
            >
              <Download className="h-4 w-4" />
              Export
            </Button>
          )}
          
          {onSettings && (
            <Button
              variant="secondary"
              size="sm"
              onClick={onSettings}
              className="aws-button"
            >
              <Settings2 className="h-4 w-4" />
              <span className="sr-only">Settings</span>
            </Button>
          )}
          
          {actions}
        </div>
      </div>

      {/* Search and filter row */}
      {(onSearchChange || onFilter || filters) && (
        <div className="flex w-full items-center gap-aws-m">
          {onSearchChange && (
            <div className="relative flex-1 max-w-md">
              <Search className="absolute left-aws-s top-1/2 h-4 w-4 -translate-y-1/2 text-text-muted" />
              <input
                type="text"
                value={searchValue}
                onChange={(e) => onSearchChange(e.target.value)}
                placeholder={searchPlaceholder}
                className="aws-input pl-10 w-full"
              />
            </div>
          )}
          
          {onFilter && (
            <Button
              variant="secondary"
              size="sm"
              onClick={onFilter}
              className="aws-button"
            >
              <Filter className="h-4 w-4" />
              Filter
            </Button>
          )}
          
          {filters && <div className="flex items-center gap-aws-s">{filters}</div>}
        </div>
      )}
    </div>
  );
};