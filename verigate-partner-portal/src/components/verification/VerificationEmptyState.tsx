import { Search } from "lucide-react";
import type { ComponentType } from "react";

interface VerificationEmptyStateProps {
  icon?: ComponentType<{ className?: string }>;
  heading?: string;
  description?: string;
}

export function VerificationEmptyState({
  icon: Icon = Search,
  heading = "No results yet",
  description = "Enter the required details and submit to see verification results.",
}: VerificationEmptyStateProps) {
  return (
    <div className="console-card">
      <div className="console-card-body flex flex-col items-center justify-center py-12 text-center">
        <Icon className="h-10 w-10 text-text-muted/40 mb-3" aria-hidden="true" />
        <div className="text-sm font-medium text-text-muted mb-1">{heading}</div>
        <div className="text-xs text-text-muted max-w-xs">{description}</div>
      </div>
    </div>
  );
}
