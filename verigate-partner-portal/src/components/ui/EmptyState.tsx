import { FileSearch, type LucideIcon } from "lucide-react";
import { Button } from "./Button";
import { cn } from "@/lib/cn";

export interface EmptyStateProps {
  icon?: LucideIcon;
  title: string;
  body?: string;
  action?: string;
  onAction?: () => void;
  className?: string;
}

export function EmptyState({
  icon: Icon = FileSearch,
  title,
  body,
  action,
  onAction,
  className,
}: EmptyStateProps) {
  return (
    <div
      className={cn(
        "bg-surface border border-dashed border-[#CBD5E1] rounded-aws-container py-14 px-6 text-center",
        className,
      )}
    >
      <div className="w-16 h-16 rounded-2xl bg-accent-soft inline-flex items-center justify-center mb-3.5">
        <Icon size={28} className="text-accent" />
      </div>
      <div className="text-[17px] font-semibold text-text">{title}</div>
      {body && (
        <div className="text-[13px] text-text-muted mt-1.5 max-w-[420px] mx-auto leading-relaxed">
          {body}
        </div>
      )}
      {action && onAction && (
        <div className="mt-4">
          <Button variant="cta" onClick={onAction}>
            {action}
          </Button>
        </div>
      )}
    </div>
  );
}
