"use client";

import { Button } from "@/components/ui/Button";

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <div className="min-h-screen flex items-center justify-center p-10 bg-background">
      <div className="bg-surface border border-[rgba(226,61,54,0.25)] rounded-aws-container py-10 px-7 text-center max-w-[520px]">
        <div className="w-16 h-16 rounded-2xl bg-[rgba(226,61,54,0.08)] inline-flex items-center justify-center mb-3.5">
          <svg
            width="28"
            height="28"
            viewBox="0 0 24 24"
            fill="none"
            stroke="#E23D36"
            strokeWidth="2.5"
            strokeLinecap="round"
          >
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </div>
        <div className="text-[17px] font-semibold text-text">
          Something went wrong
        </div>
        <div className="text-[13px] text-text-muted mt-1.5 leading-relaxed">
          {error.message ||
            "An unexpected error occurred. Try again, or contact support if the problem persists."}
        </div>
        {error.digest && (
          <div className="mt-2.5 text-[11px] text-text-muted font-mono">
            Error ref: {error.digest}
          </div>
        )}
        <div className="mt-4 flex gap-2 justify-center">
          <Button variant="primary" onClick={reset}>
            Try again &rarr;
          </Button>
        </div>
      </div>
    </div>
  );
}
