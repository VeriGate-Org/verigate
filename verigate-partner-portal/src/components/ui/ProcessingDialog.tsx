"use client";

import { Loader2 } from "lucide-react";

type ProcessingDialogProps = {
  open: boolean;
  title?: string;
  message?: string;
};

export function ProcessingDialog({ open, title = "Processing", message = "Please wait while we complete your request." }: ProcessingDialogProps) {
  if (!open) return null;

  return (
    <div role="status" aria-live="polite" className="pointer-events-none fixed bottom-6 right-6 z-[200] flex min-w-[220px] justify-end">
      <div className="pointer-events-auto flex w-full max-w-sm items-start gap-3 rounded-lg border border-border bg-[color:var(--color-base-100)] p-4 text-sm shadow-xl">
        <Loader2 className="mt-0.5 h-5 w-5 animate-spin text-[color:var(--color-accent-strong)]" />
        <div>
          <div className="font-semibold text-text">{title}</div>
          <p className="mt-1 text-xs text-text-muted">{message}</p>
        </div>
      </div>
    </div>
  );
}
