"use client";

import { useCallback, useState } from "react";
import { Button } from "@/components/ui/Button";
import { RotateCcw } from "lucide-react";

interface RetryButtonProps {
  onRetry: () => Promise<void>;
  maxAttempts?: number;
  label?: string;
}

const BACKOFF_MS = [1000, 2000, 4000];

export function RetryButton({ onRetry, maxAttempts = 3, label = "Retry" }: RetryButtonProps) {
  const [attempt, setAttempt] = useState(0);
  const [retrying, setRetrying] = useState(false);

  const exhausted = attempt >= maxAttempts;

  const handleRetry = useCallback(async () => {
    if (exhausted || retrying) return;

    const delayMs = BACKOFF_MS[Math.min(attempt, BACKOFF_MS.length - 1)];
    setRetrying(true);

    await new Promise((resolve) => setTimeout(resolve, delayMs));

    try {
      await onRetry();
    } finally {
      setAttempt((prev) => prev + 1);
      setRetrying(false);
    }
  }, [onRetry, attempt, exhausted, retrying]);

  if (exhausted) {
    return (
      <span className="text-xs text-text-muted">
        Maximum retries reached. Please try again later.
      </span>
    );
  }

  return (
    <Button
      variant="secondary"
      size="sm"
      onClick={handleRetry}
      loading={retrying}
      disabled={retrying}
      icon={<RotateCcw className="h-3.5 w-3.5" />}
    >
      {label}
    </Button>
  );
}
