"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  listCases,
  getCase,
  updateCase,
  addCaseComment,
} from "@/lib/services/case-service";
import type { CaseStatus, CasePriority } from "@/lib/bff-client";

export function useCases(params?: { status?: string; pageSize?: number }) {
  return useQuery({
    queryKey: ["cases", params],
    queryFn: () => listCases(params),
  });
}

export function useCase(caseId: string | null) {
  return useQuery({
    queryKey: ["case", caseId],
    queryFn: () => getCase(caseId!),
    enabled: !!caseId,
  });
}

export function useUpdateCase() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      caseId,
      updates,
    }: {
      caseId: string;
      updates: Partial<{
        status: CaseStatus;
        assignee: string;
        priority: CasePriority;
        decision: string;
        decisionReason: string;
      }>;
    }) => updateCase(caseId, updates),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["cases"] });
      queryClient.invalidateQueries({ queryKey: ["case"] });
    },
  });
}

export function useAddComment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      caseId,
      comment,
    }: {
      caseId: string;
      comment: { author: string; text: string };
    }) => addCaseComment(caseId, comment),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["case"] });
    },
  });
}
