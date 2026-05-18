"use client";

import { cn } from "@/lib/cn";
import type { TamperingIndicators } from "@/lib/mock-services";
import { ShieldCheck, ShieldAlert, AlertTriangle } from "lucide-react";

interface TamperingAnalysisCardProps {
  indicators: TamperingIndicators;
}

const METRIC_LABELS: Record<string, string> = {
  fontConsistency: "Font consistency",
  layoutAlignment: "Layout alignment",
  imageQuality: "Image quality",
  securityFeatures: "Security features",
  metadataConsistency: "Metadata consistency",
};

function barColor(score: number): string {
  if (score >= 90) return "bg-green-500";
  if (score >= 75) return "bg-amber-500";
  return "bg-red-500";
}

function textColor(score: number): string {
  if (score >= 90) return "text-green-600";
  if (score >= 75) return "text-amber-600";
  return "text-red-600";
}

export function TamperingAnalysisCard({ indicators }: TamperingAnalysisCardProps) {
  const overall = indicators.overallTamperingScore;
  const passed = overall >= 80;

  const metrics = [
    { key: "fontConsistency", value: indicators.fontConsistency },
    { key: "layoutAlignment", value: indicators.layoutAlignment },
    { key: "imageQuality", value: indicators.imageQuality },
    { key: "securityFeatures", value: indicators.securityFeatures },
    { key: "metadataConsistency", value: indicators.metadataConsistency },
  ];

  return (
    <div className="console-card">
      <div className="console-card-header">
        <div className="flex items-center gap-2">
          {passed ? (
            <ShieldCheck className="h-4 w-4 text-green-600" />
          ) : (
            <ShieldAlert className="h-4 w-4 text-red-600" />
          )}
          <span className="text-sm font-semibold text-text">
            Tampering analysis
          </span>
        </div>
        <span
          className={cn(
            "inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium",
            passed
              ? "bg-green-500/10 text-green-600"
              : "bg-red-500/10 text-red-600"
          )}
        >
          {overall}% — {passed ? "Pass" : "Fail"}
        </span>
      </div>

      <div className="console-card-body space-y-4">
        <div className="space-y-3">
          {metrics.map(({ key, value }) => (
            <div key={key} className="space-y-1">
              <div className="flex items-center justify-between text-xs">
                <span className="font-medium text-text-muted">
                  {METRIC_LABELS[key]}
                </span>
                <span className={cn("font-semibold tabular-nums", textColor(value))}>
                  {value}%
                </span>
              </div>
              <div className="h-1.5 w-full rounded-full bg-border">
                <div
                  className={cn("h-full rounded-full transition-all", barColor(value))}
                  style={{ width: `${value}%` }}
                />
              </div>
            </div>
          ))}
        </div>

        {indicators.flags.length > 0 && (
          <div className="space-y-2">
            <span className="text-xs font-semibold text-text-muted">
              Flags
            </span>
            {indicators.flags.map((flag, i) => (
              <div
                key={i}
                className="flex items-center gap-2 rounded-md border border-amber-500/30 bg-amber-500/5 px-3 py-2 text-xs font-medium text-amber-600"
              >
                <AlertTriangle className="h-3.5 w-3.5 flex-shrink-0" />
                {flag}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
