import type { ReactNode } from "react";
import { Button } from "@/components/ui/Button";
import { FileDown } from "lucide-react";
import { ConfidenceScore } from "./ConfidenceScore";
import { FieldMatchIndicator } from "./FieldMatchIndicator";

export interface ResultField {
  label: string;
  value: ReactNode;
}

export interface VerificationResultCardProps {
  title: string;
  reference?: string;
  status: "verified" | "not_verified" | "error";
  confidenceScore?: number;
  fields: ResultField[];
  matchFields?: Array<{ label: string; matched: boolean }>;
  onExport?: () => void;
  children?: ReactNode;
}

export function VerificationResultCard({
  title,
  reference,
  status,
  confidenceScore,
  fields,
  matchFields,
  onExport,
  children,
}: VerificationResultCardProps) {
  const statusBadge = {
    verified: { text: "Verified", className: "bg-success/10 text-success" },
    not_verified: { text: "Not verified", className: "bg-danger/10 text-danger" },
    error: { text: "Error", className: "bg-danger/10 text-danger" },
  }[status];

  return (
    <div className="console-card" role="region" aria-label={title}>
      <div className="console-card-header">
        <div>
          <div className="flex items-center gap-2">
            <span className="text-sm font-semibold text-text">{title}</span>
            <span
              className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${statusBadge.className}`}
            >
              {statusBadge.text}
            </span>
          </div>
          {reference && (
            <div className="text-xs text-text-muted mt-0.5">Reference {reference}</div>
          )}
        </div>
        {onExport && (
          <Button
            variant="secondary"
            size="sm"
            onClick={onExport}
            icon={<FileDown className="h-4 w-4" />}
          >
            Export PDF
          </Button>
        )}
      </div>

      <div className="console-card-body">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-start">
          {confidenceScore != null && (
            <div className="flex-shrink-0">
              <ConfidenceScore score={confidenceScore} label="Confidence" />
            </div>
          )}

          <dl className="grid flex-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
            {fields.map((field) => (
              <div
                key={field.label}
                className="space-y-1 rounded border border-border bg-background p-3"
              >
                <dt className="text-xs uppercase tracking-wide text-text-muted">
                  {field.label}
                </dt>
                <dd className="text-sm font-medium text-text">{field.value}</dd>
              </div>
            ))}
          </dl>
        </div>

        {matchFields && matchFields.length > 0 && (
          <div className="mt-4 flex flex-wrap gap-3 border-t border-border pt-3">
            {matchFields.map((mf) => (
              <FieldMatchIndicator key={mf.label} matched={mf.matched} label={mf.label} />
            ))}
          </div>
        )}

        {children}
      </div>
    </div>
  );
}
