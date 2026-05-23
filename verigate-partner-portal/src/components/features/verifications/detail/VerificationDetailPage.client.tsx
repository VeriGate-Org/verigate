"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useVerificationDetail, useSubmitVerification } from "@/lib/hooks/useVerification";
import { getVerificationReportUrl } from "@/lib/bff-client";
import { getTypeInfo, getBffType } from "@/lib/verification-type-map";
import { config } from "@/lib/config";
import type { VerificationEvent } from "@/lib/types";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Skeleton";
import { ErrorState } from "@/components/ui/ErrorState";
import { Download, ArrowLeft, RotateCcw } from "lucide-react";

const STATUS_MAP: Record<string, { label: string; variant: "success" | "danger" | "warning" | "info" | "pending" }> = {
  success: { label: "Verified", variant: "success" },
  completed: { label: "Verified", variant: "success" },
  hard_fail: { label: "Failed", variant: "danger" },
  permanent_failure: { label: "Failed", variant: "danger" },
  soft_fail: { label: "Review", variant: "warning" },
  transient_error: { label: "Review", variant: "warning" },
  in_progress: { label: "In Progress", variant: "info" },
  pending: { label: "Pending", variant: "pending" },
};

const TONE_COLORS: Record<string, string> = {
  success: "#2C974B",
  completed: "#2C974B",
  soft_fail: "#C28B0B",
  transient_error: "#C28B0B",
  hard_fail: "#E23D36",
  permanent_failure: "#E23D36",
  in_progress: "#00B3D9",
  pending: "#4F5B67",
};

function EventTimeline({ events }: { events: VerificationEvent[] }) {
  return (
    <div className="py-2">
      {events.map((evt, i) => {
        const isLast = i === events.length - 1;
        const isSuccess = evt.eventType.includes("Succeed");
        const isFail = evt.eventType.includes("Fail") || evt.eventType.includes("Hard");
        const color = isSuccess ? "#2C974B" : isFail ? "#E23D36" : "#00B3D9";

        return (
          <div key={i} className="flex gap-3.5 px-5 py-3 relative">
            {!isLast && (
              <div
                className="absolute left-[29px] top-[30px] bottom-0 w-px bg-[#e9ebed]"
              />
            )}
            <div
              className="w-3.5 h-3.5 rounded-full bg-surface border-[3px] mt-1 shrink-0 z-10"
              style={{ borderColor: color }}
            />
            <div className="flex-1 min-w-0">
              <div className="flex items-baseline justify-between gap-3">
                <div className="flex items-center gap-2.5">
                  <span className="text-[13px] font-semibold text-text">
                    {evt.eventType.replace(/([A-Z])/g, " $1").trim()}
                  </span>
                  <Badge
                    variant={isSuccess ? "success" : isFail ? "danger" : "info"}
                    size="sm"
                  >
                    {isSuccess ? "Pass" : isFail ? "Fail" : "Processing"}
                  </Badge>
                </div>
                <span className="text-[11px] text-text-muted font-mono shrink-0">
                  {new Date(evt.ts).toLocaleTimeString("en-ZA")} &middot;{" "}
                  {evt.source}
                </span>
              </div>
              {evt.detail != null && (
                <div className="text-xs text-text-muted mt-1 leading-relaxed">
                  {String(typeof evt.detail === "object" ? JSON.stringify(evt.detail) : evt.detail)}
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
}

export function VerificationDetailPage({
  correlationId,
}: {
  correlationId: string;
}) {
  const router = useRouter();
  const { data, isLoading, isError, error, refetch } = useVerificationDetail(correlationId);
  const [downloading, setDownloading] = useState(false);

  const rerunMutation = useSubmitVerification({
    onSuccess: (result) => {
      router.push(`/verifications/${result.commandId}`);
    },
  });

  const handleDownloadPdf = async () => {
    setDownloading(true);
    try {
      const report = await getVerificationReportUrl(correlationId);
      window.open(report.downloadUrl, "_blank");
    } catch {
      // Report endpoint may not be available yet
    } finally {
      setDownloading(false);
    }
  };

  const handleRerun = () => {
    if (!data?.verification) return;
    const v = data.verification;
    rerunMutation.mutate({
      verificationType: getBffType(v.type),
      originationType: "ADHOC",
      originationId: crypto.randomUUID(),
      requestedBy: config.partnerId,
      metadata: {},
    });
  };

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-6 w-48" />
        <Skeleton className="h-32 w-full" />
        <Skeleton className="h-64 w-full" />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="space-y-4">
        <button
          onClick={() => router.push("/verifications")}
          className="inline-flex items-center gap-1 text-accent text-xs font-medium hover:underline"
        >
          <ArrowLeft size={12} /> Back to verifications
        </button>
        <ErrorState
          title="Failed to load verification"
          body={
            error instanceof Error
              ? error.message
              : "We couldn\u2019t load this verification. Please try again."
          }
          onRetry={() => refetch()}
        />
      </div>
    );
  }

  if (!data) {
    return (
      <div className="text-center py-20 text-text-muted">
        Verification not found.
      </div>
    );
  }

  const v = data.verification;
  const events = data.events;
  const statusInfo = STATUS_MAP[v.status] || { label: v.status, variant: "neutral" as const };
  const typeInfo = getTypeInfo(v.type);
  const toneColor = TONE_COLORS[v.status] || "#4F5B67";

  return (
    <div className="space-y-4">
      <button
        onClick={() => router.push("/verifications")}
        className="inline-flex items-center gap-1 text-accent text-xs font-medium hover:underline"
      >
        <ArrowLeft size={12} /> Back to verifications
      </button>

      {/* Header card */}
      <div className="console-card overflow-hidden">
        <div className="flex h-[3px]">
          <div className="flex-[3] bg-[#E23D36]" />
          <div className="flex-[5] bg-primary" />
          <div className="flex-[2] bg-accent" />
        </div>
        <div className="p-5 flex justify-between items-start">
          <div className="flex gap-4 items-center">
            <div className="w-14 h-14 rounded-full bg-primary text-white flex items-center justify-center text-lg font-semibold shrink-0">
              {v.correlationId.slice(0, 2).toUpperCase()}
            </div>
            <div>
              <div className="flex items-center gap-2.5 mb-1">
                <span className="font-mono text-xs text-accent">
                  {v.correlationId}
                </span>
                <span className="text-[11px] px-2 py-0.5 bg-[#F2F3F3] rounded font-semibold tracking-wide">
                  {typeInfo?.shortLabel || v.type}
                </span>
                <Badge variant={statusInfo.variant}>{statusInfo.label}</Badge>
              </div>
              <div className="text-xl font-semibold text-text">
                {v.correlationId}
              </div>
              <div className="text-xs text-text-muted mt-0.5">
                Provider: {v.provider} &middot; Started{" "}
                {new Date(v.startedAt).toLocaleString("en-ZA")}
                {v.durationMs
                  ? ` · Duration ${(v.durationMs / 1000).toFixed(1)}s`
                  : ""}
              </div>
            </div>
          </div>
          <div className="flex gap-2">
            <Button
              variant="secondary"
              icon={<Download size={13} />}
              onClick={handleDownloadPdf}
              disabled={downloading}
            >
              {downloading ? "Downloading\u2026" : "Download PDF"}
            </Button>
            <Button
              variant="secondary"
              icon={<RotateCcw size={13} />}
              onClick={handleRerun}
              disabled={rerunMutation.isPending}
            >
              {rerunMutation.isPending ? "Re-running\u2026" : "Re-run"}
            </Button>
          </div>
        </div>
      </div>

      {/* Timeline + sidebar */}
      <div className="grid grid-cols-1 lg:grid-cols-[1.6fr_1fr] gap-4">
        <div className="console-card overflow-hidden">
          <div className="px-[18px] py-3.5 border-b border-[#e9ebed] flex justify-between items-center">
            <div>
              <div className="text-sm font-semibold text-text">
                Verification timeline
              </div>
              <div className="text-[11px] text-text-muted mt-0.5">
                {events.length} event{events.length !== 1 ? "s" : ""}
              </div>
            </div>
          </div>
          <EventTimeline events={events} />

          {/* Final result bar */}
          <div
            className="px-5 py-3.5 border-t-2 flex items-center justify-between gap-3"
            style={{
              borderColor: toneColor,
              background:
                v.status === "hard_fail" || v.status === "permanent_failure"
                  ? "rgba(226,61,54,0.05)"
                  : v.status === "soft_fail" || v.status === "transient_error"
                    ? "rgba(194,139,11,0.05)"
                    : "rgba(44,151,75,0.05)",
            }}
          >
            <div>
              <div className="text-[11px] text-text-muted uppercase tracking-wide font-semibold">
                Final result
              </div>
              <div
                className="text-base font-semibold mt-0.5"
                style={{ color: toneColor }}
              >
                {v.status === "success" || v.status === "completed"
                  ? "\u2713 Verified"
                  : v.status === "soft_fail" || v.status === "transient_error"
                    ? "\u26A0 Manual review required"
                    : v.status === "hard_fail" || v.status === "permanent_failure"
                      ? "\u2717 Failed"
                      : "In progress"}
              </div>
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-3">
          <div className="console-card p-[18px]">
            <div className="text-[11px] text-text-muted uppercase tracking-wide font-semibold mb-2.5">
              Verification details
            </div>
            {[
              ["Correlation ID", v.correlationId],
              ["Type", typeInfo?.label || v.type],
              ["Provider", v.provider],
              ["Status", statusInfo.label],
              ["Policy", v.policyVersion || "—"],
              ["Workflow", v.workflowName || "—"],
            ].map(([k, val]) => (
              <div
                key={k}
                className="flex justify-between py-1.5 border-b border-[#f1f5f9] text-xs last:border-0"
              >
                <span className="text-text-muted">{k}</span>
                <span className="text-text font-medium text-right">{val}</span>
              </div>
            ))}
          </div>

          <div className="bg-[#F8FAFC] border border-dashed border-[#CBD5E1] rounded-aws-container p-3 text-[11px] text-text-muted leading-relaxed">
            <b className="text-primary">POPIA audit log:</b> All record access
            is logged. Subject was notified per POPIA s.18 on consent capture.
          </div>
        </div>
      </div>
    </div>
  );
}
