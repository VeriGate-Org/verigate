import { ArrowUpRight, ArrowDownRight } from "lucide-react";
import { cn } from "@/lib/cn";

const toneColors: Record<string, string> = {
  success: "text-[#2C974B]",
  warning: "text-[#C28B0B]",
  danger: "text-[#D13212]",
  muted: "text-text-muted",
};

export interface StatCardProps {
  label: string;
  value: string | number;
  delta?: string;
  deltaTone?: "success" | "warning" | "danger" | "muted";
  helper?: string;
  className?: string;
}

export function StatCard({
  label,
  value,
  delta,
  deltaTone = "success",
  helper,
  className,
}: StatCardProps) {
  const isNegative = delta?.startsWith("-");
  const DeltaIcon = isNegative ? ArrowDownRight : ArrowUpRight;

  return (
    <div className={cn("console-card p-4", className)}>
      <div className="text-[11px] font-semibold uppercase tracking-wide text-text-muted">
        {label}
      </div>
      <div className="flex items-baseline gap-2 mt-1.5">
        <span className="text-[28px] font-bold text-text leading-none">
          {value}
        </span>
        {delta && (
          <span
            className={cn(
              "text-xs font-semibold inline-flex items-center gap-0.5",
              toneColors[deltaTone],
            )}
          >
            <DeltaIcon size={12} />
            {delta}
          </span>
        )}
      </div>
      {helper && (
        <div className="text-[11px] text-text-muted mt-1.5">{helper}</div>
      )}
    </div>
  );
}
