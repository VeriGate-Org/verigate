"use client";

import * as React from "react";
import { Brain, RefreshCw, CheckCircle2, XCircle, AlertTriangle } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { cn } from "@/lib/cn";

interface CaseSummaryResponse {
  summary: string;
  suggestedDisposition: string;
  reasoning: string;
  keySignals: string[];
}

const DISPOSITION_STYLES: Record<string, { bg: string; icon: React.ReactNode }> = {
  APPROVE: { bg: "bg-green-500/10 text-green-600 border-green-500/20", icon: <CheckCircle2 className="h-4 w-4" /> },
  REJECT: { bg: "bg-red-500/10 text-red-600 border-red-500/20", icon: <XCircle className="h-4 w-4" /> },
  ESCALATE: { bg: "bg-yellow-500/10 text-yellow-600 border-yellow-500/20", icon: <AlertTriangle className="h-4 w-4" /> },
};

export default function CaseSummaryCard({ caseId }: { caseId: string }) {
  const [data, setData] = React.useState<CaseSummaryResponse | null>(null);
  const [isLoading, setIsLoading] = React.useState(true);
  const [error, setError] = React.useState<string | null>(null);

  const fetchSummary = React.useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await fetch(`/api/partner/ai/cases/${caseId}/summary`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({}),
      });
      if (!res.ok) throw new Error("Failed to fetch summary");
      const result = await res.json();
      setData(result);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to load AI analysis");
    } finally {
      setIsLoading(false);
    }
  }, [caseId]);

  React.useEffect(() => {
    fetchSummary();
  }, [fetchSummary]);

  if (isLoading) {
    return (
      <div className="console-card">
        <div className="console-card-header">
          <div className="flex items-center gap-2">
            <Brain className="h-4 w-4 text-accent" />
            <span className="text-sm font-semibold text-text">AI Analysis</span>
          </div>
        </div>
        <div className="console-card-body space-y-3">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-3/4" />
          <Skeleton className="h-8 w-32" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="console-card border-danger/20">
        <div className="console-card-body text-center py-4">
          <p className="text-xs text-text-muted">{error}</p>
          <Button variant="ghost" size="sm" className="mt-2" onClick={fetchSummary}>
            Retry
          </Button>
        </div>
      </div>
    );
  }

  if (!data) return null;

  const dispositionStyle = DISPOSITION_STYLES[data.suggestedDisposition] || DISPOSITION_STYLES.ESCALATE;

  return (
    <div className="console-card">
      <div className="console-card-header">
        <div className="flex items-center gap-2">
          <Brain className="h-4 w-4 text-accent" />
          <span className="text-sm font-semibold text-text">AI Analysis</span>
        </div>
        <Button variant="ghost" size="sm" onClick={fetchSummary} title="Regenerate">
          <RefreshCw className="h-3.5 w-3.5" />
        </Button>
      </div>
      <div className="console-card-body space-y-3">
        <p className="text-sm text-text">{data.summary}</p>

        <div className="flex items-center gap-2">
          <span className={cn(
            "inline-flex items-center gap-1.5 px-3 py-1 rounded text-sm font-medium border",
            dispositionStyle.bg
          )}>
            {dispositionStyle.icon}
            {data.suggestedDisposition}
          </span>
        </div>

        {data.reasoning && (
          <p className="text-xs text-text-muted">{data.reasoning}</p>
        )}

        {data.keySignals && data.keySignals.length > 0 && (
          <div>
            <div className="text-xs font-medium text-text-muted mb-1">Key Signals</div>
            <ul className="space-y-1">
              {data.keySignals.map((signal, i) => (
                <li key={i} className="flex items-start gap-2 text-xs text-text">
                  <span className="mt-1 w-1 h-1 rounded-full bg-accent flex-shrink-0" />
                  {signal}
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
}
