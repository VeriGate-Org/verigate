import * as React from "react";
import { cn } from "@/lib/cn";
import { Button } from "@/components/ui/Button";
import { ConfirmationDialog } from "@/components/ui/Modal/Modal";
import {
  Download,
  Trash2,
  Archive,
  RefreshCw,
  CheckSquare,
  Square,
  Minus
} from "lucide-react";

export interface BulkAction {
  id: string;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  variant?: "default" | "destructive";
  requiresConfirmation?: boolean;
  confirmationTitle?: string;
  confirmationDescription?: string;
}

interface BulkOperationsBarProps {
  selectedCount: number;
  totalCount: number;
  actions: BulkAction[];
  onAction: (actionId: string) => void;
  onSelectAll: () => void;
  onDeselectAll: () => void;
  onToggleAll: () => void;
  className?: string;
  loading?: boolean;
}

export const BulkOperationsBar: React.FC<BulkOperationsBarProps> = ({
  selectedCount,
  totalCount,
  actions,
  onAction,
  onSelectAll,
  onDeselectAll,
  onToggleAll,
  className,
  loading = false,
}) => {
  const [confirmingAction, setConfirmingAction] = React.useState<BulkAction | null>(null);
  const [actionLoading, setActionLoading] = React.useState<string | null>(null);

  const handleAction = async (action: BulkAction) => {
    if (action.requiresConfirmation) {
      setConfirmingAction(action);
    } else {
      setActionLoading(action.id);
      try {
        await onAction(action.id);
      } finally {
        setActionLoading(null);
      }
    }
  };

  const handleConfirmedAction = async () => {
    if (!confirmingAction) return;
    
    setActionLoading(confirmingAction.id);
    try {
      await onAction(confirmingAction.id);
    } finally {
      setActionLoading(null);
      setConfirmingAction(null);
    }
  };

  const getSelectionIcon = () => {
    if (selectedCount === 0) {
      return <Square className="h-4 w-4" />;
    } else if (selectedCount === totalCount) {
      return <CheckSquare className="h-4 w-4 text-accent" />;
    } else {
      return <Minus className="h-4 w-4 text-accent" />;
    }
  };

  const getSelectionText = () => {
    if (selectedCount === 0) {
      return "Select items";
    } else if (selectedCount === totalCount) {
      return `All ${totalCount} items selected`;
    } else {
      return `${selectedCount} of ${totalCount} items selected`;
    }
  };

  if (selectedCount === 0) {
    return null;
  }

  return (
    <>
      <div className={cn(
        "flex items-center justify-between p-4 bg-accent-soft border border-accent/20 rounded-aws-container",
        className
      )}>
        <div className="flex items-center gap-4">
          <button
            onClick={onToggleAll}
            className="flex items-center gap-2 text-accent hover:text-accent-strong transition-colors"
          >
            {getSelectionIcon()}
            <span className="text-aws-body font-medium">
              {getSelectionText()}
            </span>
          </button>

          {selectedCount < totalCount && (
            <Button
              variant="ghost"
              size="sm"
              onClick={onSelectAll}
              className="text-accent hover:text-accent-strong"
            >
              Select all {totalCount}
            </Button>
          )}

          <Button
            variant="ghost"
            size="sm"
            onClick={onDeselectAll}
            className="text-text-muted hover:text-text"
          >
            Clear selection
          </Button>
        </div>

        <div className="flex items-center gap-2">
          {actions.map((action) => {
            const isLoading = actionLoading === action.id;
            return (
              <Button
                key={action.id}
                variant={action.variant === "destructive" ? "destructive" : "secondary"}
                size="sm"
                onClick={() => handleAction(action)}
                disabled={loading || isLoading}
                className="gap-2"
              >
                {isLoading ? (
                  <RefreshCw className="h-4 w-4 animate-spin" />
                ) : (
                  <action.icon className="h-4 w-4" />
                )}
                {action.label}
              </Button>
            );
          })}
        </div>
      </div>

      <ConfirmationDialog
        open={!!confirmingAction}
        onOpenChange={(open) => !open && setConfirmingAction(null)}
        title={confirmingAction?.confirmationTitle || `Confirm ${confirmingAction?.label}`}
        description={
          confirmingAction?.confirmationDescription || 
          `Are you sure you want to ${confirmingAction?.label.toLowerCase()} ${selectedCount} item${selectedCount !== 1 ? 's' : ''}?`
        }
        onConfirm={handleConfirmedAction}
        variant={confirmingAction?.variant === "destructive" ? "destructive" : "default"}
        loading={!!actionLoading}
      />
    </>
  );
};

// Selection hook for managing bulk selection state
export function useBulkSelection<T>(items: T[], getId?: (item: T) => string) {
  const resolveId = React.useCallback(
    (item: T): string => {
      if (getId) return getId(item);
      return (item as unknown as { id: string }).id;
    },
    [getId]
  );

  const [selectedIds, setSelectedIds] = React.useState<Set<string>>(new Set<string>());

  const selectedItems = React.useMemo(
    () => items.filter(item => selectedIds.has(resolveId(item))),
    [items, selectedIds, resolveId]
  );

  const selectedCount = selectedIds.size;
  const totalCount = items.length;
  const isAllSelected = totalCount > 0 && selectedCount === totalCount;
  const isPartiallySelected = selectedCount > 0 && selectedCount < totalCount;

  const toggleItem = React.useCallback((id: string) => {
    setSelectedIds(prev => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  }, []);

  const selectAll = React.useCallback(() => {
    setSelectedIds(new Set<string>(items.map(item => resolveId(item))));
  }, [items, resolveId]);

  const deselectAll = React.useCallback(() => {
    setSelectedIds(new Set<string>());
  }, []);

  const toggleAll = React.useCallback(() => {
    if (isAllSelected) {
      deselectAll();
    } else {
      selectAll();
    }
  }, [isAllSelected, selectAll, deselectAll]);

  const isSelected = React.useCallback((id: string) => {
    return selectedIds.has(id);
  }, [selectedIds]);

  // Reset selection when items change significantly
  React.useEffect(() => {
    const validIds = new Set<string>(items.map(item => resolveId(item)));
    setSelectedIds(prev => {
      const next = new Set<string>();
      for (const id of prev) {
        if (validIds.has(id)) {
          next.add(id);
        }
      }
      if (next.size === prev.size && [...next].every(id => prev.has(id))) {
        return prev;
      }
      return next;
    });
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [items.length, resolveId]);

  return {
    selectedIds,
    selectedItems,
    selectedCount,
    totalCount,
    isAllSelected,
    isPartiallySelected,
    toggleItem,
    selectAll,
    deselectAll,
    toggleAll,
    isSelected,
  };
}

// Common bulk actions for verifications
export const VERIFICATION_BULK_ACTIONS: BulkAction[] = [
  {
    id: "export",
    label: "Export",
    icon: Download,
  },
  {
    id: "retry",
    label: "Retry",
    icon: RefreshCw,
    requiresConfirmation: true,
    confirmationTitle: "Retry Verifications",
    confirmationDescription: "This will retry all selected verifications. This action cannot be undone.",
  },
  {
    id: "archive",
    label: "Archive",
    icon: Archive,
    requiresConfirmation: true,
    confirmationTitle: "Archive Verifications",
    confirmationDescription: "Archived verifications will be moved to the archive and removed from the main list.",
  },
  {
    id: "delete",
    label: "Delete",
    icon: Trash2,
    variant: "destructive" as const,
    requiresConfirmation: true,
    confirmationTitle: "Delete Verifications",
    confirmationDescription: "This will permanently delete the selected verifications. This action cannot be undone.",
  },
];