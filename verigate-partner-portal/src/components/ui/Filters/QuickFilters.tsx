import * as React from "react";
import { cn } from "@/lib/cn";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { X } from "lucide-react";

export interface QuickFilter {
  id: string;
  label: string;
  value: string;
  type: "status" | "type" | "provider" | "date" | "custom";
  removable?: boolean;
}

interface QuickFiltersProps {
  filters: QuickFilter[];
  onRemoveFilter: (filterId: string) => void;
  onClearAll: () => void;
  className?: string;
}

export const QuickFilters: React.FC<QuickFiltersProps> = ({
  filters,
  onRemoveFilter,
  onClearAll,
  className,
}) => {
  if (filters.length === 0) {
    return null;
  }

  const getFilterVariant = (type: QuickFilter["type"]) => {
    switch (type) {
      case "status":
        return "info" as const;
      case "type":
        return "success" as const;
      case "provider":
        return "warning" as const;
      case "date":
        return "neutral" as const;
      default:
        return "neutral" as const;
    }
  };

  return (
    <div className={cn("flex flex-wrap items-center gap-2", className)}>
      <span className="text-aws-body font-medium text-text-muted">Active filters:</span>
      
      {filters.map((filter) => (
        <Badge
          key={filter.id}
          variant={getFilterVariant(filter.type)}
          size="sm"
          className="gap-1 pr-1"
        >
          <span>{filter.label}</span>
          {filter.removable !== false && (
            <button
              onClick={() => onRemoveFilter(filter.id)}
              className="ml-1 rounded-full p-0.5 hover:bg-black/10 transition-colors"
            >
              <X className="h-3 w-3" />
            </button>
          )}
        </Badge>
      ))}
      
      {filters.length > 1 && (
        <Button
          variant="ghost"
          size="sm"
          onClick={onClearAll}
          className="text-text-muted hover:text-danger"
        >
          Clear all
        </Button>
      )}
    </div>
  );
};

// Predefined quick filter options
export const QUICK_FILTER_OPTIONS = {
  status: [
    { label: "Success", value: "success" },
    { label: "In Progress", value: "in_progress" },
    { label: "Soft Fail", value: "soft_fail" },
    { label: "Hard Fail", value: "hard_fail" },
  ],
  type: [
    { label: "ID Verification", value: "ID" },
    { label: "Company Check", value: "CIPC" },
    { label: "Property Check", value: "DEEDS" },
    { label: "Account Verification", value: "AVS" },
    { label: "Sanctions Screening", value: "SANCTIONS" },
  ],
  timeRange: [
    { label: "Last 24 hours", value: "24h" },
    { label: "Last 7 days", value: "7d" },
    { label: "Last 30 days", value: "30d" },
    { label: "This year", value: "ytd" },
  ],
};

interface QuickFilterMenuProps {
  onSelectFilter: (filter: Omit<QuickFilter, "id">) => void;
  className?: string;
}

export const QuickFilterMenu: React.FC<QuickFilterMenuProps> = ({
  onSelectFilter,
  className,
}) => {
  return (
    <div className={cn("console-card", className)}>
      <div className="console-card-header">
        <h4 className="text-aws-body font-medium">Quick Filters</h4>
      </div>
      <div className="console-card-body space-y-4">
        <div>
          <h5 className="text-xs font-medium text-text-muted uppercase tracking-wide mb-2">
            Status
          </h5>
          <div className="flex flex-wrap gap-2">
            {QUICK_FILTER_OPTIONS.status.map((option) => (
              <button
                key={option.value}
                onClick={() => onSelectFilter({
                  label: option.label,
                  value: option.value,
                  type: "status",
                })}
                className="px-2 py-1 text-xs border border-border rounded-aws-control hover:bg-hover transition-colors"
              >
                {option.label}
              </button>
            ))}
          </div>
        </div>

        <div>
          <h5 className="text-xs font-medium text-text-muted uppercase tracking-wide mb-2">
            Type
          </h5>
          <div className="flex flex-wrap gap-2">
            {QUICK_FILTER_OPTIONS.type.map((option) => (
              <button
                key={option.value}
                onClick={() => onSelectFilter({
                  label: option.label,
                  value: option.value,
                  type: "type",
                })}
                className="px-2 py-1 text-xs border border-border rounded-aws-control hover:bg-hover transition-colors"
              >
                {option.label}
              </button>
            ))}
          </div>
        </div>

        <div>
          <h5 className="text-xs font-medium text-text-muted uppercase tracking-wide mb-2">
            Time Range
          </h5>
          <div className="flex flex-wrap gap-2">
            {QUICK_FILTER_OPTIONS.timeRange.map((option) => (
              <button
                key={option.value}
                onClick={() => onSelectFilter({
                  label: option.label,
                  value: option.value,
                  type: "date",
                })}
                className="px-2 py-1 text-xs border border-border rounded-aws-control hover:bg-hover transition-colors"
              >
                {option.label}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};