import { Zap, Clock } from "lucide-react";

interface SlaIndicatorProps {
  sla: string;
  realTime?: boolean;
}

export function SlaIndicator({ sla, realTime }: SlaIndicatorProps) {
  if (realTime) {
    return (
      <span className="inline-flex items-center gap-1 px-2 py-0.5 text-[11px] font-medium rounded-full bg-success/10 text-success">
        <Zap className="w-3 h-3" />
        {sla}
      </span>
    );
  }

  return (
    <span className="inline-flex items-center gap-1 px-2 py-0.5 text-[11px] font-medium rounded-full bg-warning/10 text-warning">
      <Clock className="w-3 h-3" />
      {sla}
    </span>
  );
}
