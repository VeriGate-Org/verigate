"use client";

import * as React from "react";
import { useParams, useRouter } from "next/navigation";
import { useCase, useUpdateCase, useAddComment } from "@/lib/hooks/useCases";
import { Button } from "@/components/ui/Button";
import { cn } from "@/lib/cn";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { ArrowLeft, MessageSquare, Clock } from "lucide-react";

const STATUS_STYLES: Record<string, string> = {
  OPEN: "bg-blue-500/10 text-blue-600 border-blue-500/20",
  IN_REVIEW: "bg-yellow-500/10 text-yellow-600 border-yellow-500/20",
  RESOLVED: "bg-green-500/10 text-green-600 border-green-500/20",
  ESCALATED: "bg-red-500/10 text-red-600 border-red-500/20",
};

export default function CaseDetail() {
  const params = useParams<{ caseId: string }>();
  const router = useRouter();
  const { data: caseData, isLoading, error } = useCase(params.caseId);
  const updateMutation = useUpdateCase();
  const commentMutation = useAddComment();
  const [commentText, setCommentText] = React.useState("");

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-4 w-32" />
        <Skeleton className="h-64 w-full" />
      </div>
    );
  }

  if (error || !caseData) {
    return (
      <div className="rounded border border-danger/40 bg-danger/5 px-4 py-3 text-sm text-danger">
        {error instanceof Error ? error.message : "Case not found"}
      </div>
    );
  }

  const handleStatusChange = (newStatus: string) => {
    updateMutation.mutate({ caseId: params.caseId, updates: { status: newStatus as "OPEN" | "IN_REVIEW" | "RESOLVED" | "ESCALATED" } });
  };

  const handleResolve = (decision: string) => {
    updateMutation.mutate({
      caseId: params.caseId,
      updates: { status: "RESOLVED", decision, decisionReason: `Resolved as ${decision}` },
    });
  };

  const handleAddComment = () => {
    if (!commentText.trim()) return;
    commentMutation.mutate(
      { caseId: params.caseId, comment: { author: "current-user", text: commentText } },
      { onSuccess: () => setCommentText("") }
    );
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="sm" onClick={() => router.push("/cases")}>
          <ArrowLeft className="h-4 w-4 mr-1" /> Back
        </Button>
        <div>
          <h1 className="text-xl font-semibold text-text">Case {caseData.caseId.slice(0, 8)}...</h1>
          <p className="text-sm text-text-muted">Created {new Date(caseData.createdAt).toLocaleString()}</p>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
        {/* Main content */}
        <div className="space-y-6">
          {/* Subject Info */}
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Subject Information</div>
            </div>
            <div className="console-card-body">
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="text-text-muted">Name</span>
                  <p className="font-medium text-text">{caseData.subjectName || "-"}</p>
                </div>
                <div>
                  <span className="text-text-muted">ID</span>
                  <p className="font-medium text-text">{caseData.subjectId || "-"}</p>
                </div>
                <div>
                  <span className="text-text-muted">Verification ID</span>
                  <p className="font-mono text-xs text-text">{caseData.verificationId || "-"}</p>
                </div>
                <div>
                  <span className="text-text-muted">Workflow ID</span>
                  <p className="font-mono text-xs text-text">{caseData.workflowId || "-"}</p>
                </div>
              </div>
            </div>
          </div>

          {/* Risk Card */}
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Risk Assessment</div>
            </div>
            <div className="console-card-body">
              <div className="flex items-center gap-8">
                <div className="text-center">
                  <div className="text-3xl font-bold text-text">{caseData.compositeRiskScore}</div>
                  <div className="text-xs text-text-muted">Composite Score</div>
                </div>
                <div>
                  <span className="inline-flex items-center px-3 py-1 rounded text-sm font-medium bg-yellow-500/10 text-yellow-600">
                    {caseData.riskTier || "UNKNOWN"}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Comments */}
          <div className="console-card">
            <div className="console-card-header">
              <div className="flex items-center gap-2">
                <MessageSquare className="h-4 w-4" />
                <span className="text-sm font-semibold text-text">Comments</span>
              </div>
            </div>
            <div className="console-card-body space-y-4">
              {(caseData.comments || []).map((comment, i) => (
                <div key={i} className="border border-border rounded p-3">
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-xs font-medium text-text">{comment.author}</span>
                    <span className="text-xs text-text-muted">
                      {new Date(comment.createdAt).toLocaleString()}
                    </span>
                  </div>
                  <p className="text-sm text-text">{comment.text}</p>
                </div>
              ))}
              <div className="flex gap-2">
                <input
                  type="text"
                  value={commentText}
                  onChange={(e) => setCommentText(e.target.value)}
                  placeholder="Add a comment..."
                  className="aws-input flex-1"
                  onKeyDown={(e) => e.key === "Enter" && handleAddComment()}
                />
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={handleAddComment}
                  disabled={!commentText.trim() || commentMutation.isPending}
                >
                  Add
                </Button>
              </div>
            </div>
          </div>

          {/* Timeline */}
          <div className="console-card">
            <div className="console-card-header">
              <div className="flex items-center gap-2">
                <Clock className="h-4 w-4" />
                <span className="text-sm font-semibold text-text">Timeline</span>
              </div>
            </div>
            <div className="console-card-body">
              <div className="space-y-3">
                {(caseData.timeline || []).map((entry, i) => (
                  <div key={i} className="flex items-start gap-3">
                    <div className="w-2 h-2 rounded-full bg-accent mt-1.5 flex-shrink-0" />
                    <div>
                      <p className="text-sm text-text">{entry.event}</p>
                      <p className="text-xs text-text-muted">
                        {new Date(entry.timestamp).toLocaleString()} by {entry.actor}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-4">
          {/* Status & Actions */}
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Actions</div>
            </div>
            <div className="console-card-body space-y-3">
              <div>
                <span className="text-xs text-text-muted">Status</span>
                <div className="mt-1">
                  <span className={cn("inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border", STATUS_STYLES[caseData.status] || "")}>
                    {caseData.status}
                  </span>
                </div>
              </div>
              <div>
                <span className="text-xs text-text-muted">Assignee</span>
                <p className="text-sm text-text">{caseData.assignee || "Unassigned"}</p>
              </div>
              {caseData.decision && (
                <div>
                  <span className="text-xs text-text-muted">Decision</span>
                  <p className="text-sm font-medium text-text">{caseData.decision}</p>
                </div>
              )}

              {caseData.status !== "RESOLVED" && (
                <div className="pt-3 border-t border-border space-y-2">
                  {caseData.status === "OPEN" && (
                    <Button variant="secondary" size="sm" className="w-full" onClick={() => handleStatusChange("IN_REVIEW")}>
                      Start Review
                    </Button>
                  )}
                  <Button variant="secondary" size="sm" className="w-full" onClick={() => handleStatusChange("ESCALATED")}>
                    Escalate
                  </Button>
                  <Button variant="primary" size="sm" className="w-full" onClick={() => handleResolve("APPROVE")}>
                    Resolve: Approve
                  </Button>
                  <Button variant="ghost" size="sm" className="w-full text-danger" onClick={() => handleResolve("REJECT")}>
                    Resolve: Reject
                  </Button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
