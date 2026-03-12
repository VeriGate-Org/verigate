"use client";

import type { CheckScore } from "@/lib/types";
import { Badge } from "@/components/ui/Badge";

interface RiskAssessmentCardProps {
  compositeRiskScore?: number;
  riskTier?: string;
  riskDecision?: string;
  decisionReason?: string;
  individualScores?: CheckScore[];
}

function scoreColor(score: number): string {
  if (score >= 80) return "text-green-600";
  if (score >= 50) return "text-yellow-600";
  return "text-red-600";
}

function scoreBgColor(score: number): string {
  if (score >= 80) return "bg-green-50 border-green-200";
  if (score >= 50) return "bg-yellow-50 border-yellow-200";
  return "bg-red-50 border-red-200";
}

function decisionBadgeVariant(decision?: string): "success" | "warning" | "danger" | "info" {
  switch (decision) {
    case "APPROVE":
      return "success";
    case "MANUAL_REVIEW":
      return "warning";
    case "REJECT":
      return "danger";
    default:
      return "info";
  }
}

export function RiskAssessmentCard({
  compositeRiskScore,
  riskTier,
  riskDecision,
  decisionReason,
  individualScores,
}: RiskAssessmentCardProps) {
  if (compositeRiskScore == null) {
    return null;
  }

  return (
    <div className="console-card">
      <div className="console-card-header">
        <h3 className="text-aws-heading-s font-medium">Risk Assessment</h3>
        <Badge variant={decisionBadgeVariant(riskDecision)} size="sm">
          {riskDecision || "PENDING"}
        </Badge>
      </div>

      <div className="p-4 space-y-4">
        {/* Composite Score */}
        <div className="flex items-center gap-4">
          <div
            className={`flex items-center justify-center w-16 h-16 rounded-xl border-2 ${scoreBgColor(compositeRiskScore)}`}
          >
            <span className={`text-2xl font-bold ${scoreColor(compositeRiskScore)}`}>
              {compositeRiskScore}
            </span>
          </div>
          <div>
            <div className="text-sm text-text-muted">Composite Risk Score</div>
            {riskTier && (
              <div className="text-sm font-medium mt-1">{riskTier.replace(/_/g, " ")}</div>
            )}
            {decisionReason && (
              <div className="text-xs text-text-muted mt-1">{decisionReason}</div>
            )}
          </div>
        </div>

        {/* Individual Scores */}
        {individualScores && individualScores.length > 0 && (
          <div>
            <h4 className="text-sm font-medium mb-2">Individual Check Scores</h4>
            <div className="space-y-2">
              {individualScores.map((score, i) => (
                <div key={i} className="flex items-center justify-between text-sm">
                  <span className="text-text-muted">
                    {score.verificationType.replace(/_/g, " ")}
                  </span>
                  <div className="flex items-center gap-2">
                    <div className="w-24 h-2 bg-neutral-100 rounded-full overflow-hidden">
                      <div
                        className={`h-full rounded-full ${
                          score.confidenceScore >= 80
                            ? "bg-green-500"
                            : score.confidenceScore >= 50
                              ? "bg-yellow-500"
                              : "bg-red-500"
                        }`}
                        style={{ width: `${score.confidenceScore}%` }}
                      />
                    </div>
                    <span className={`font-medium w-8 text-right ${scoreColor(score.confidenceScore)}`}>
                      {score.confidenceScore}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

/** Compact inline risk score display for table rows */
export function RiskScoreBadge({ score }: { score?: number }) {
  if (score == null) return <span className="text-text-muted">--</span>;

  return (
    <span className={`font-medium ${scoreColor(score)}`}>
      {score}
    </span>
  );
}

export function RiskDecisionBadge({ decision }: { decision?: string }) {
  if (!decision) return <span className="text-text-muted">--</span>;

  return (
    <Badge variant={decisionBadgeVariant(decision)} size="sm">
      {decision}
    </Badge>
  );
}
