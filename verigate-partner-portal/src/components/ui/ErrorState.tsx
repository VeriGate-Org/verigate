import { X } from "lucide-react";
import { Button } from "./Button";
import { cn } from "@/lib/cn";

export interface ErrorStateProps {
  title?: string;
  body?: string;
  errorCode?: string;
  onRetry?: () => void;
  className?: string;
}

export function ErrorState({
  title = "Something went wrong",
  body = "We couldn\u2019t load this data. Try again, or contact support if the problem persists.",
  errorCode,
  onRetry,
  className,
}: ErrorStateProps) {
  return (
    <div
      className={cn(
        "bg-surface border border-[rgba(226,61,54,0.25)] rounded-aws-container py-10 px-7 text-center max-w-[520px] mx-auto",
        className,
      )}
    >
      <div className="w-16 h-16 rounded-2xl bg-[rgba(226,61,54,0.08)] inline-flex items-center justify-center mb-3.5">
        <X size={28} className="text-[#E23D36]" strokeWidth={2.5} />
      </div>
      <div className="text-[17px] font-semibold text-text">{title}</div>
      <div className="text-[13px] text-text-muted mt-1.5 leading-relaxed">
        {body}
      </div>
      {errorCode && (
        <div className="mt-2.5 text-[11px] text-text-muted font-mono">
          Error ref: {errorCode}
        </div>
      )}
      <div className="mt-4 flex gap-2 justify-center">
        <Button variant="secondary">Contact support</Button>
        {onRetry && (
          <Button variant="primary" onClick={onRetry}>
            Try again &rarr;
          </Button>
        )}
      </div>
    </div>
  );
}
