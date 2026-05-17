import { Check, X, Clock } from "lucide-react";

type OutcomeType = "success" | "danger" | "warning" | "neutral";

interface OutcomeBadgeProps {
  label: string;
  type: OutcomeType;
}

export function OutcomeBadge({ label, type }: OutcomeBadgeProps) {
  const config = {
    success: {
      icon: Check,
      iconBg: "bg-success",
      textColor: "text-success",
      pillBg: "bg-success/10",
    },
    danger: {
      icon: X,
      iconBg: "bg-danger",
      textColor: "text-danger",
      pillBg: "bg-danger/10",
    },
    warning: {
      icon: Clock,
      iconBg: "bg-warning",
      textColor: "text-warning",
      pillBg: "bg-warning/10",
    },
    neutral: {
      icon: Clock,
      iconBg: "bg-base-300",
      textColor: "text-text-muted",
      pillBg: "bg-base-200",
    },
  };

  const { icon: Icon, iconBg, textColor, pillBg } = config[type];

  return (
    <span className={`inline-flex items-center gap-1.5 px-2 py-0.5 text-xs rounded-full ${pillBg} ${textColor}`}>
      <span className={`inline-flex items-center justify-center w-3.5 h-3.5 rounded-full ${iconBg}`}>
        <Icon className="w-2.5 h-2.5 text-white" strokeWidth={3} />
      </span>
      {label}
    </span>
  );
}
