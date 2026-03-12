"use client";

import * as React from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { listPolicies, deletePolicy, publishPolicy } from "@/lib/bff-client";
import type { Policy } from "@/lib/bff-client";
import { Button } from "@/components/ui/Button";
import { cn } from "@/lib/cn";
import { Plus, Pencil, Trash2, Upload, FileText } from "lucide-react";

const STATUS_STYLES: Record<string, string> = {
  DRAFT: "bg-yellow-500/10 text-yellow-600 border-yellow-500/20",
  PUBLISHED: "bg-green-500/10 text-green-600 border-green-500/20",
  ARCHIVED: "bg-gray-500/10 text-gray-500 border-gray-500/20",
};

export default function PoliciesList() {
  const router = useRouter();
  const queryClient = useQueryClient();

  const { data: policies = [], isLoading, error } = useQuery({
    queryKey: ["policies"],
    queryFn: listPolicies,
  });

  const deleteMutation = useMutation({
    mutationFn: (policyId: string) => deletePolicy(policyId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["policies"] }),
  });

  const publishMutation = useMutation({
    mutationFn: (policyId: string) => publishPolicy(policyId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["policies"] }),
  });

  const handleDelete = (policy: Policy) => {
    if (!policy.policyId) return;
    if (!confirm(`Delete policy "${policy.name}"? This cannot be undone.`)) return;
    deleteMutation.mutate(policy.policyId);
  };

  const handlePublish = (policy: Policy) => {
    if (!policy.policyId) return;
    if (!confirm(`Publish policy "${policy.name}"? Published policies cannot be edited.`)) return;
    publishMutation.mutate(policy.policyId);
  };

  if (isLoading) {
    return (
      <div className="space-y-4">
        {[1, 2, 3].map((i) => (
          <div key={i} className="console-card animate-pulse">
            <div className="console-card-body p-4">
              <div className="h-5 w-48 bg-border rounded mb-2" />
              <div className="h-3 w-32 bg-border rounded" />
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded border border-danger/40 bg-danger/5 px-4 py-3 text-sm text-danger">
        Failed to load policies: {error instanceof Error ? error.message : "Unknown error"}
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-text">Verification Policies</h1>
          <p className="text-sm text-text-muted mt-1">
            Configure verification workflows with custom steps and risk scoring.
          </p>
        </div>
        <Link href="/policies/new">
          <Button variant="primary" size="sm">
            <Plus className="h-4 w-4 mr-2" />
            Create Policy
          </Button>
        </Link>
      </div>

      {policies.length === 0 ? (
        <div className="console-card">
          <div className="console-card-body flex flex-col items-center justify-center py-16">
            <FileText className="h-12 w-12 text-text-muted mb-4" />
            <p className="text-text-muted font-medium">No policies yet</p>
            <p className="text-sm text-text-muted mt-1 mb-4">
              Create your first verification policy to get started.
            </p>
            <Link href="/policies/new">
              <Button variant="primary" size="sm">
                <Plus className="h-4 w-4 mr-2" />
                Create Policy
              </Button>
            </Link>
          </div>
        </div>
      ) : (
        <div className="console-card">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-border">
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">
                    Name
                  </th>
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">
                    Status
                  </th>
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">
                    Version
                  </th>
                  <th className="text-left text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">
                    Updated
                  </th>
                  <th className="text-right text-xs font-semibold text-text-muted uppercase tracking-wider px-4 py-3">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {policies.map((policy) => (
                  <tr key={policy.policyId} className="hover:bg-hover transition-colors">
                    <td className="px-4 py-3">
                      <Link
                        href={`/policies/${policy.policyId}`}
                        className="text-sm font-medium text-text hover:text-accent transition-colors"
                      >
                        {policy.name || "Untitled Policy"}
                      </Link>
                      {policy.description && (
                        <p className="text-xs text-text-muted mt-0.5 truncate max-w-xs">
                          {policy.description}
                        </p>
                      )}
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={cn(
                          "inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border",
                          STATUS_STYLES[policy.status || "DRAFT"] || STATUS_STYLES.DRAFT
                        )}
                      >
                        {policy.status || "DRAFT"}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-sm text-text-muted">
                      v{policy.version ?? 1}
                    </td>
                    <td className="px-4 py-3 text-sm text-text-muted">
                      {policy.updatedAt
                        ? new Date(policy.updatedAt).toLocaleDateString()
                        : "-"}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center justify-end gap-1">
                        {policy.status === "DRAFT" && (
                          <>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => router.push(`/policies/${policy.policyId}`)}
                              title="Edit"
                            >
                              <Pencil className="h-3.5 w-3.5" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => handlePublish(policy)}
                              title="Publish"
                              disabled={publishMutation.isPending}
                            >
                              <Upload className="h-3.5 w-3.5" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => handleDelete(policy)}
                              title="Delete"
                              disabled={deleteMutation.isPending}
                            >
                              <Trash2 className="h-3.5 w-3.5 text-danger" />
                            </Button>
                          </>
                        )}
                        {policy.status === "PUBLISHED" && (
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => router.push(`/policies/${policy.policyId}`)}
                            title="View"
                          >
                            <FileText className="h-3.5 w-3.5" />
                          </Button>
                        )}
                      </div>
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
