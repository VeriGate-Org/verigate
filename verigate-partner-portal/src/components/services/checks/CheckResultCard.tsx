"use client";

import Link from "next/link";
import { Loader2, CheckCircle2, XCircle, ArrowRight, RotateCcw, Building2, ScanSearch, Download, ShieldCheck, ShieldAlert, ShieldX, Clock, HelpCircle } from "lucide-react";
import type { CheckDefinition } from "./checkFieldRegistry";
import { VERIFICATION_METHOD_LABELS } from "./checkFieldRegistry";
import { getVerificationReportUrl } from "@/lib/bff-client";

export type CheckState = {
  status: "pending" | "running" | "success" | "error";
  data?: unknown;
  error?: string;
};

interface CheckResultCardProps {
  checkType: string;
  state: CheckState;
  definition: CheckDefinition;
  onRetry?: () => void;
}

/** Extract 1-2 summary lines from raw check result data. */
function summarize(checkType: string, data: unknown): string[] {
  if (!data || typeof data !== "object") return [];
  const d = data as Record<string, unknown>;

  switch (checkType) {
    case "IDENTITY_VERIFICATION": {
      const result = d.result as Record<string, unknown> | undefined;
      if (result) {
        const lines: string[] = [];
        if (result.verified) lines.push("Identity verified");
        else lines.push("Identity not verified");
        if (typeof result.matchScore === "number") lines.push(`Match: ${result.matchScore}%`);
        return lines;
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "CREDIT_CHECK": {
      const result = d.result as Record<string, unknown> | undefined;
      if (result) {
        const lines: string[] = [];
        if (result.creditScore) lines.push(`Score: ${result.creditScore} (${result.riskGrade ?? ""})`);
        if (typeof result.totalDebt === "number")
          lines.push(`Debt: R${(result.totalDebt as number).toLocaleString()}`);
        return lines;
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "BANK_ACCOUNT_VERIFICATION": {
      const result = d.result as Record<string, unknown> | undefined;
      if (result) {
        const lines: string[] = [];
        if (result.accountFound) lines.push("Account found");
        else lines.push("Account not found");
        if (result.nameMatch) lines.push("Name matched");
        return lines;
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "FRAUD_WATCHLIST_SCREENING": {
      const result = d.result as Record<string, unknown> | undefined;
      if (result) {
        if (result.listed) return ["Listed on watchlist"];
        return ["Clear - not listed"];
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "NEGATIVE_NEWS_SCREENING": {
      const result = d.result as Record<string, unknown> | undefined;
      if (result) {
        const count = typeof result.articleCount === "number" ? result.articleCount : 0;
        return [count > 0 ? `${count} article(s) found` : "No adverse media found"];
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "SANCTIONS_SCREENING": {
      const results = d.results as Array<unknown> | undefined;
      if (results) {
        return [results.length > 0 ? `${results.length} match(es)` : "0 matches - clear"];
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "EMPLOYMENT_VERIFICATION": {
      const result = d.result as Record<string, unknown> | undefined;
      if (result) {
        const lines: string[] = [];
        if (result.employed) lines.push("Currently employed");
        else lines.push("Not currently employed");
        if (result.employer) lines.push(`Employer: ${result.employer}`);
        return lines;
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "QUALIFICATION_VERIFICATION": {
      const result = d.result as Record<string, unknown> | undefined;
      if (result) {
        if (result.verified) return ["Qualification verified"];
        return ["Qualification not verified"];
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "INCOME_VERIFICATION": {
      const result = d.result as Record<string, unknown> | undefined;
      if (result) {
        const lines: string[] = [];
        if (typeof result.grossIncome === "number")
          lines.push(`Gross: R${(result.grossIncome as number).toLocaleString()}/mo`);
        return lines;
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "TAX_COMPLIANCE_VERIFICATION": {
      const result = d.result as Record<string, unknown> | undefined;
      if (result) {
        if (result.compliant) return ["Tax compliant"];
        return ["Tax non-compliant"];
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "VERIFICATION_OF_PERSONAL_DETAILS": {
      const subject = d.subject as Record<string, unknown> | undefined;
      if (subject) {
        const lines: string[] = [];
        if (subject.firstName) lines.push(`${subject.firstName} ${subject.surname ?? ""}`);
        if (subject.idNumber) lines.push(`ID: ${subject.idNumber}`);
        return lines;
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "COMPANY_VERIFICATION": {
      const company = d.company as Record<string, unknown> | undefined;
      if (company) {
        const lines: string[] = [];
        if (company.name) lines.push(String(company.name));
        if (company.status) lines.push(`Status: ${company.status}`);
        return lines;
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "PROPERTY_OWNERSHIP_VERIFICATION": {
      const summary = d.summary as Record<string, unknown> | undefined;
      const items = d.items as unknown[] | undefined;
      if (summary || items) {
        const lines: string[] = [];
        const count = items?.length ?? summary?.totalProperties ?? 0;
        lines.push(`${count} propert${count === 1 ? "y" : "ies"} found`);
        return lines;
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "VAT_VENDOR_VERIFICATION": {
      const vendor = d.vendor as Record<string, unknown> | undefined;
      if (vendor) {
        const lines: string[] = [];
        if (vendor.vendorName) lines.push(String(vendor.vendorName));
        if (vendor.status) lines.push(`Status: ${vendor.status}`);
        return lines;
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    case "DOCUMENT_VERIFICATION__ASYLUM":
    case "DOCUMENT_VERIFICATION__WORK_VISA":
    case "DOCUMENT_VERIFICATION__PASSPORT": {
      // Check for DHA-specific outcome in auxiliaryData
      const dhaOutcome = d.dhaOutcome as string | undefined;
      if (dhaOutcome) {
        const lines: string[] = [];
        lines.push(`DHA: ${dhaOutcome.replace(/_/g, " ")}`);
        if (d.dhaIsAuthentic === "true") lines.push("Authentic: Yes");
        if (d.dhaIsCurrentlyValid === "true") lines.push("Currently valid: Yes");
        const confidence = d.dhaConfidenceScore as string | undefined;
        if (confidence) lines.push(`Confidence: ${Math.round(parseFloat(confidence) * 100)}%`);
        return lines;
      }
      const result = d.result as Record<string, unknown> | undefined;
      if (result) {
        const lines: string[] = [];
        const outcome = result.outcome ?? result.overallOutcome;
        if (outcome === "VERIFIED") lines.push("Document verified");
        else if (outcome) lines.push(`Outcome: ${outcome}`);
        const confidence = result.overallConfidence as number | undefined;
        if (typeof confidence === "number") lines.push(`Confidence: ${confidence}%`);
        return lines.length > 0 ? lines : ["Completed"];
      }
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }

    default: {
      if (d.status === "COMPLETED" || d.status === "SUCCEEDED") return ["Completed"];
      return [];
    }
  }
}

const DHA_OUTCOME_STYLES: Record<string, { icon: typeof ShieldCheck; colorClass: string }> = {
  VERIFIED:        { icon: ShieldCheck, colorClass: "bg-success/10 text-success" },
  PARTIAL_MATCH:   { icon: ShieldAlert, colorClass: "bg-warning/10 text-warning" },
  EXPIRED:         { icon: Clock,       colorClass: "bg-warning/10 text-warning" },
  INVALID:         { icon: ShieldX,     colorClass: "bg-danger/10 text-danger" },
  NOT_FOUND:       { icon: XCircle,     colorClass: "bg-danger/10 text-danger" },
  UNABLE_TO_VERIFY:{ icon: HelpCircle,  colorClass: "bg-[color:var(--color-base-200)] text-text-muted" },
  INCONCLUSIVE:    { icon: HelpCircle,  colorClass: "bg-[color:var(--color-base-200)] text-text-muted" },
};

function isDhaPermitCheck(checkType: string): boolean {
  return checkType === "DOCUMENT_VERIFICATION__ASYLUM" || checkType === "DOCUMENT_VERIFICATION__WORK_VISA";
}

function getDhaOutcome(data: unknown): string | undefined {
  if (!data || typeof data !== "object") return undefined;
  return (data as Record<string, unknown>).dhaOutcome as string | undefined;
}

function getCommandId(data: unknown): string | undefined {
  if (!data || typeof data !== "object") return undefined;
  const d = data as Record<string, unknown>;
  return (d.commandId ?? d.verificationId) as string | undefined;
}

function hasReport(data: unknown): boolean {
  if (!data || typeof data !== "object") return false;
  return !!(data as Record<string, unknown>).reportDocumentId;
}

export function CheckResultCard({ checkType, state, definition, onRetry }: CheckResultCardProps) {
  const summaryLines = state.status === "success" ? summarize(checkType, state.data) : [];
  const dhaOutcome = state.status === "success" && isDhaPermitCheck(checkType) ? getDhaOutcome(state.data) : undefined;
  const outcomeStyle = dhaOutcome ? DHA_OUTCOME_STYLES[dhaOutcome] : undefined;
  const commandId = state.status === "success" ? getCommandId(state.data) : undefined;
  const showDownload = state.status === "success" && isDhaPermitCheck(checkType) && hasReport(state.data) && commandId;

  const handleDownloadReport = async () => {
    if (!commandId) return;
    try {
      const { downloadUrl } = await getVerificationReportUrl(commandId);
      window.open(downloadUrl, "_blank");
    } catch {
      // Silently fail - user can retry
    }
  };

  return (
    <div className="console-card">
      <div className="console-card-body space-y-3">
        {/* Header row */}
        <div className="flex items-start justify-between gap-2">
          <div className="text-sm font-semibold text-text">{definition.label}</div>
          {state.status === "running" && (
            <span className="inline-flex items-center gap-1 px-2 py-0.5 text-[11px] font-medium rounded-full bg-warning/10 text-warning">
              <Loader2 className="h-3 w-3 animate-spin" />
              Running
            </span>
          )}
          {state.status === "success" && !dhaOutcome && (
            <span className="inline-flex items-center gap-1 px-2 py-0.5 text-[11px] font-medium rounded-full bg-success/10 text-success">
              <CheckCircle2 className="h-3 w-3" />
              Done
            </span>
          )}
          {state.status === "success" && dhaOutcome && outcomeStyle && (
            <span className={`inline-flex items-center gap-1 px-2 py-0.5 text-[11px] font-medium rounded-full ${outcomeStyle.colorClass}`}>
              <outcomeStyle.icon className="h-3 w-3" />
              {dhaOutcome.replace(/_/g, " ")}
            </span>
          )}
          {state.status === "error" && (
            <span className="inline-flex items-center gap-1 px-2 py-0.5 text-[11px] font-medium rounded-full bg-danger/10 text-danger">
              <XCircle className="h-3 w-3" />
              Failed
            </span>
          )}
          {state.status === "pending" && (
            <span className="inline-flex items-center gap-1 px-2 py-0.5 text-[11px] font-medium rounded-full bg-[color:var(--color-base-200)] text-text-muted">
              Pending
            </span>
          )}
        </div>

        {/* Verification method */}
        <div className="flex items-center gap-1 text-[10px] text-text-muted">
          {definition.verificationMethod === "authority" ? (
            <Building2 className="h-2.5 w-2.5" />
          ) : (
            <ScanSearch className="h-2.5 w-2.5" />
          )}
          {VERIFICATION_METHOD_LABELS[definition.verificationMethod]}
        </div>

        {/* Summary */}
        {state.status === "running" && (
          <p className="text-xs text-text-muted">Running verification...</p>
        )}

        {state.status === "success" && summaryLines.length > 0 && (
          <div className="space-y-0.5">
            {summaryLines.map((line, i) => (
              <p key={i} className="text-xs text-text-muted">{line}</p>
            ))}
          </div>
        )}

        {state.status === "error" && (
          <div className="space-y-2">
            <p className="text-xs text-danger">{state.error || "Verification failed"}</p>
            {onRetry && (
              <button
                type="button"
                onClick={onRetry}
                className="inline-flex items-center gap-1 text-xs text-[color:var(--color-accent-strong)] hover:underline font-medium"
              >
                <RotateCcw className="h-3 w-3" />
                Retry
              </button>
            )}
          </div>
        )}

        {/* Action links */}
        {state.status === "success" && (
          <div className="flex items-center gap-3">
            <Link
              href={definition.servicePath}
              className="inline-flex items-center gap-1 text-xs text-[color:var(--color-accent-strong)] hover:underline font-medium"
            >
              View details
              <ArrowRight className="h-3 w-3" />
            </Link>
            {showDownload && (
              <button
                type="button"
                onClick={handleDownloadReport}
                className="inline-flex items-center gap-1 text-xs text-[color:var(--color-accent-strong)] hover:underline font-medium"
              >
                <Download className="h-3 w-3" />
                Download Report
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
