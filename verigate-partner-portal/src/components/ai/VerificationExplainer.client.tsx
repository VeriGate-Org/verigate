"use client";

import * as React from "react";
import { Lightbulb, RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";

interface ExplanationResponse {
  explanation: string;
  whatWasChecked: string;
  whatItMeans: string;
  recommendedAction: string;
}

export default function VerificationExplainer({ commandId }: { commandId: string }) {
  const [data, setData] = React.useState<ExplanationResponse | null>(null);
  const [isLoading, setIsLoading] = React.useState(true);
  const [error, setError] = React.useState<string | null>(null);

  const fetchExplanation = React.useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await fetch(`/api/partner/ai/verifications/${commandId}/explain`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({}),
      });
      if (!res.ok) throw new Error("Failed to fetch explanation");
      const result = await res.json();
      setData(result);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to load explanation");
    } finally {
      setIsLoading(false);
    }
  }, [commandId]);

  React.useEffect(() => {
    fetchExplanation();
  }, [fetchExplanation]);

  if (isLoading) {
    return (
      <div className="console-card">
        <div className="console-card-header">
          <div className="flex items-center gap-2">
            <Lightbulb className="h-4 w-4 text-accent" />
            <span className="text-sm font-semibold text-text">AI Explanation</span>
          </div>
        </div>
        <div className="console-card-body space-y-2">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-5/6" />
          <Skeleton className="h-4 w-2/3" />
        </div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="console-card border-danger/20">
        <div className="console-card-body text-center py-4">
          <p className="text-xs text-text-muted">{error || "No explanation available"}</p>
          <Button variant="ghost" size="sm" className="mt-2" onClick={fetchExplanation}>
            Retry
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="console-card">
      <div className="console-card-header">
        <div className="flex items-center gap-2">
          <Lightbulb className="h-4 w-4 text-accent" />
          <span className="text-sm font-semibold text-text">AI Explanation</span>
        </div>
        <Button variant="ghost" size="sm" onClick={fetchExplanation} title="Regenerate">
          <RefreshCw className="h-3.5 w-3.5" />
        </Button>
      </div>
      <div className="console-card-body space-y-3">
        <p className="text-sm text-text">{data.explanation}</p>

        {data.whatWasChecked && (
          <div>
            <div className="text-xs font-medium text-text-muted mb-0.5">What was checked</div>
            <p className="text-xs text-text">{data.whatWasChecked}</p>
          </div>
        )}

        {data.whatItMeans && (
          <div>
            <div className="text-xs font-medium text-text-muted mb-0.5">What it means</div>
            <p className="text-xs text-text">{data.whatItMeans}</p>
          </div>
        )}

        {data.recommendedAction && (
          <div>
            <div className="text-xs font-medium text-text-muted mb-0.5">Recommended action</div>
            <p className="text-xs text-text">{data.recommendedAction}</p>
          </div>
        )}
      </div>
    </div>
  );
}
