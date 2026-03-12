"use client";

import * as React from "react";
import Link from "next/link";
import { useCases } from "@/lib/hooks/useCases";
import { cn } from "@/lib/cn";
import { Skeleton } from "@/components/ui/Loading/Skeleton";

const STATUS_STYLES: Record<string, string> = {
  OPEN: "bg-blue-500/10 text-blue-600 border-blue-500/20",
  IN_REVIEW: "bg-yellow-500/10 text-yellow-600 border-yellow-500/20",
  RESOLVED: "bg-green-500/10 text-green-600 border-green-500/20",
  ESCALATED: "bg-red-500/10 text-red-600 border-red-500/20",
};

const PRIORITY_STYLES: Record<string, string> = {
  LOW: "bg-gray-500/10 text-gray-500",
  MEDIUM: "bg-yellow-500/10 text-yellow-600",
  HIGH: "bg-orange-500/10 text-orange-600",
  CRITICAL: "bg-red-500/10 text-red-600",
};

export default function CasesTable() {
  const [statusFilter, setStatusFilter] = React.useState<string>("");
  const { data: cases = [], isLoading, error } = useCases(
    statusFilter ? { status: statusFilter } : undefined
  );

  if (isLoading) {
    return (
      <div className="space-y-3">
        {[1, 2, 3, 4].map((i) => (
          <div key={i} className="console-card animate-pulse">
            <div className="console-card-body p-4">
              <Skeleton className="h-5 w-48 mb-2" />
              <Skeleton className="h-3 w-32" />
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded border border-danger/40 bg-danger/5 px-4 py-3 text-sm text-danger">
        Failed to load cases: {error instanceof Error ? error.message : "Unknown error"}
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Filters */}
      <div className="flex items-center gap-2">
        <span className="text-xs text-text-muted">Status:</span>
        {["", "OPEN", "IN_REVIEW", "ESCALATED", "RESOLVED"].map((status) => (
          <button
            key={status}
            onClick={() => setStatusFilter(status)}
            className={cn(
              "px-3 py-1 text-xs rounded-full border transition-colors",
              statusFilter === status
                ? "bg-accent text-white border-accent"
                : "border-border text-text-muted hover:border-accent hover:text-text"
            )}
          >
            {status || "All"}
          </button>
        ))}
      </div>

      {cases.length === 0 ? (
        <div className="console-card">
          <div className="console-card-body flex flex-col items-center justify-center py-16">
            <p className="text-text-muted font-medium">No cases found</p>
            <p className="text-sm text-text-muted mt-1">
              Cases are automatically created when a verification result requires manual review.
            </p>
          </div>
        </div>
      ) : (
        <div className="console-card">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-border">
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">Case ID</th>
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">Subject</th>
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">Status</th>
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">Priority</th>
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">Risk Score</th>
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">Assignee</th>
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">Created</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {cases.map((c) => (
                  <tr key={c.caseId} className="hover:bg-hover transition-colors">
                    <td className="px-4 py-3">
                      <Link
                        href={`/cases/${c.caseId}`}
                        className="text-sm font-mono text-accent hover:underline"
                      >
                        {c.caseId.slice(0, 8)}...
                      </Link>
                    </td>
                    <td className="px-4 py-3">
                      <div className="text-sm font-medium text-text">{c.subjectName || "-"}</div>
                      <div className="text-xs text-text-muted">{c.subjectId || ""}</div>
                    </td>
                    <td className="px-4 py-3">
                      <span className={cn("inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border", STATUS_STYLES[c.status] || "")}>
                        {c.status}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={cn("inline-flex items-center px-2 py-0.5 rounded text-xs font-medium", PRIORITY_STYLES[c.priority] || "")}>
                        {c.priority}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-sm text-text">{c.compositeRiskScore}</td>
                    <td className="px-4 py-3 text-sm text-text-muted">{c.assignee || "Unassigned"}</td>
                    <td className="px-4 py-3 text-sm text-text-muted">
                      {new Date(c.createdAt).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
