import { cn } from "@/lib/cn";
import {
  CheckCircle2,
  Clock,
  AlertTriangle,
  XCircle,
  MinusCircle,
  Send,
  type LucideIcon,
} from "lucide-react";

export type StatusCategory =
  | "success"
  | "in_progress"
  | "warning"
  | "error"
  | "inactive"
  | "awaiting";

interface StatusConfig {
  icon: LucideIcon;
  label: string;
  category: StatusCategory;
}

const CATEGORY_STYLES: Record<StatusCategory, string> = {
  success: "bg-success/10 text-success border-success/20",
  in_progress: "bg-info/10 text-info border-info/20",
  warning: "bg-warning/10 text-warning border-warning/20",
  error: "bg-danger/10 text-danger border-danger/20",
  inactive: "bg-text-muted/10 text-text-muted border-text-muted/20",
  awaiting: "bg-info/10 text-info border-info/20",
};

const CATEGORY_ICONS: Record<StatusCategory, LucideIcon> = {
  success: CheckCircle2,
  in_progress: Clock,
  warning: AlertTriangle,
  error: XCircle,
  inactive: MinusCircle,
  awaiting: Send,
};

const statusRegistry = new Map<string, StatusConfig>([
  // success
  ["success", { icon: CheckCircle2, label: "Success", category: "success" }],
  ["completed", { icon: CheckCircle2, label: "Completed", category: "success" }],
  ["VERIFIED", { icon: CheckCircle2, label: "Verified", category: "success" }],
  ["CONFIRMED", { icon: CheckCircle2, label: "Active", category: "success" }],
  ["ACTIVE", { icon: CheckCircle2, label: "Active", category: "success" }],
  ["RESOLVED", { icon: CheckCircle2, label: "Resolved", category: "success" }],
  ["FOUND", { icon: CheckCircle2, label: "Found", category: "success" }],
  ["COMPLIANT", { icon: CheckCircle2, label: "Compliant", category: "success" }],
  ["CLEAR", { icon: CheckCircle2, label: "Clear", category: "success" }],
  ["SUCCEEDED", { icon: CheckCircle2, label: "Clear", category: "success" }],
  ["Passed", { icon: CheckCircle2, label: "Passed", category: "success" }],

  // in_progress
  ["in_progress", { icon: Clock, label: "In Progress", category: "in_progress" }],
  ["pending", { icon: Clock, label: "Pending", category: "in_progress" }],
  ["OPEN", { icon: Clock, label: "Open", category: "in_progress" }],
  ["IN_REVIEW", { icon: Clock, label: "In Review", category: "in_progress" }],
  ["PENDING_REVIEW", { icon: Clock, label: "Pending Review", category: "in_progress" }],

  // warning
  ["soft_fail", { icon: AlertTriangle, label: "Soft Fail", category: "warning" }],
  ["transient_error", { icon: AlertTriangle, label: "Transient Error", category: "warning" }],
  ["NOT_VERIFIED", { icon: AlertTriangle, label: "Not Verified", category: "warning" }],
  ["NOT_FOUND", { icon: AlertTriangle, label: "Not Found", category: "warning" }],
  ["NON_COMPLIANT", { icon: AlertTriangle, label: "Non-Compliant", category: "warning" }],
  ["MATCHES_FOUND", { icon: AlertTriangle, label: "Matches Found", category: "warning" }],
  ["MEDIUM", { icon: AlertTriangle, label: "Medium", category: "warning" }],
  ["HIGH", { icon: AlertTriangle, label: "High", category: "warning" }],
  ["SOFT_FAIL", { icon: AlertTriangle, label: "Review", category: "warning" }],
  ["Mixed", { icon: AlertTriangle, label: "Mixed", category: "warning" }],

  // error
  ["hard_fail", { icon: XCircle, label: "Hard Fail", category: "error" }],
  ["permanent_failure", { icon: XCircle, label: "Permanent Failure", category: "error" }],
  ["FAILED", { icon: XCircle, label: "Failed", category: "error" }],
  ["ESCALATED", { icon: XCircle, label: "Escalated", category: "error" }],
  ["CRITICAL", { icon: XCircle, label: "Critical", category: "error" }],
  ["LISTED", { icon: XCircle, label: "Listed", category: "error" }],
  ["DECEASED", { icon: XCircle, label: "Deceased", category: "error" }],
  ["HARD_FAIL", { icon: XCircle, label: "Blocked", category: "error" }],

  // inactive
  ["DEACTIVATED", { icon: MinusCircle, label: "Deactivated", category: "inactive" }],
  ["PAUSED", { icon: MinusCircle, label: "Paused", category: "inactive" }],
  ["REMOVED", { icon: MinusCircle, label: "Removed", category: "inactive" }],
  ["LOW", { icon: MinusCircle, label: "Low", category: "inactive" }],

  // awaiting
  ["INVITED", { icon: Send, label: "Invited", category: "awaiting" }],
  ["FORCE_CHANGE_PASSWORD", { icon: Send, label: "Invited", category: "awaiting" }],
]);

const FALLBACK_CONFIG: StatusConfig = {
  icon: MinusCircle,
  label: "Unknown",
  category: "inactive",
};

export function getStatusConfig(status: string): StatusConfig {
  return statusRegistry.get(status) ?? { ...FALLBACK_CONFIG, label: status };
}

export function registerStatus(
  status: string,
  config: { icon?: LucideIcon; label: string; category: StatusCategory },
) {
  statusRegistry.set(status, {
    icon: config.icon ?? CATEGORY_ICONS[config.category],
    label: config.label,
    category: config.category,
  });
}

interface StatusIndicatorProps {
  status: string;
  label?: string;
  size?: "sm" | "md";
  showIcon?: boolean;
  iconOnly?: boolean;
  className?: string;
}

const ICON_ONLY_COLORS: Record<StatusCategory, string> = {
  success: "text-[var(--color-icon-success)]",
  in_progress: "text-[var(--color-icon-in-progress)]",
  warning: "text-[var(--color-icon-warning)]",
  error: "text-[var(--color-icon-error)]",
  inactive: "text-[var(--color-icon-inactive)]",
  awaiting: "text-[var(--color-icon-awaiting)]",
};

export function StatusIndicator({
  status,
  label: labelOverride,
  size = "sm",
  showIcon = true,
  iconOnly = false,
  className,
}: StatusIndicatorProps) {
  const config = getStatusConfig(status);
  const Icon = config.icon;
  const displayLabel = labelOverride ?? config.label;

  if (iconOnly) {
    const iconOnlySizes = { sm: "w-4 h-4", md: "w-5 h-5" };
    return (
      <span title={displayLabel} className={cn("flex items-center justify-center w-full", className)}>
        <Icon className={cn(iconOnlySizes[size], ICON_ONLY_COLORS[config.category])} />
      </span>
    );
  }

  const sizeStyles = {
    sm: "px-2 py-0.5 text-xs gap-1.5",
    md: "px-2.5 py-1 text-sm gap-2",
  };

  const iconSizes = {
    sm: "w-3 h-3",
    md: "w-3.5 h-3.5",
  };

  return (
    <span
      className={cn(
        "inline-flex items-center rounded-aws-token font-medium border",
        sizeStyles[size],
        CATEGORY_STYLES[config.category],
        className,
      )}
    >
      {showIcon && <Icon className={iconSizes[size]} />}
      {displayLabel}
    </span>
  );
}
