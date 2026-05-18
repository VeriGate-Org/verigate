"use client";

import { cn } from "@/lib/cn";
import type { ExtractedFieldWithConfidence } from "@/lib/mock-services";
import { CheckCircle2, XCircle } from "lucide-react";

interface ExtractedFieldsCardProps {
  fields: Record<string, ExtractedFieldWithConfidence>;
  enteredIdNumber?: string;
  documentType: string;
}

function humanizeFieldName(key: string): string {
  return key
    .replace(/([A-Z])/g, " $1")
    .replace(/^./, (c) => c.toUpperCase())
    .trim();
}

function confidenceBadge(confidence: number) {
  const pct = Math.round(confidence * 100);
  const color =
    pct >= 90
      ? "bg-green-500/10 text-green-600"
      : pct >= 75
        ? "bg-amber-500/10 text-amber-600"
        : "bg-red-500/10 text-red-600";

  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full px-2 py-0.5 text-[10px] font-semibold tabular-nums",
        color
      )}
    >
      {pct}%
    </span>
  );
}

export function ExtractedFieldsCard({
  fields,
  enteredIdNumber,
  documentType,
}: ExtractedFieldsCardProps) {
  const entries = Object.entries(fields);
  if (entries.length === 0) return null;

  const extractedId = fields.idNumber?.value;
  const showIdMatch =
    documentType === "id_card" &&
    enteredIdNumber &&
    extractedId != null;
  const idMatches = showIdMatch && extractedId === enteredIdNumber;

  return (
    <div className="console-card">
      <div className="console-card-header">
        <span className="text-sm font-semibold text-text">
          Extracted fields
        </span>
        <span className="text-xs text-text-muted">
          {entries.length} fields detected
        </span>
      </div>

      <div className="console-card-body space-y-3">
        {showIdMatch && (
          <div
            className={cn(
              "flex items-center gap-2 rounded-md border px-3 py-2 text-xs font-medium",
              idMatches
                ? "border-green-500/30 bg-green-500/5 text-green-600"
                : "border-red-500/30 bg-red-500/5 text-red-600"
            )}
          >
            {idMatches ? (
              <CheckCircle2 className="h-3.5 w-3.5 flex-shrink-0" />
            ) : (
              <XCircle className="h-3.5 w-3.5 flex-shrink-0" />
            )}
            {idMatches
              ? "Extracted ID number matches the entered ID number"
              : `Extracted ID (${extractedId}) does not match entered ID (${enteredIdNumber})`}
          </div>
        )}

        <div className="border border-border rounded overflow-hidden">
          <div className="divide-y divide-border">
            {entries.map(([key, field]) => (
              <div
                key={key}
                className="flex items-center justify-between px-4 py-2.5 text-xs"
              >
                <span className="font-medium text-text-muted">
                  {humanizeFieldName(key)}
                </span>
                <div className="flex items-center gap-2">
                  <span className="text-text">
                    {field.value ?? "—"}
                  </span>
                  {confidenceBadge(field.confidence)}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
