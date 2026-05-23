"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  submitVerification,
  getVerificationStatus,
  pollVerificationStatus,
  isTerminalVerificationStatus,
} from "@/lib/bff-client";
import type { BffVerificationSubmission, BffVerificationStatusResponse } from "@/lib/bff-client";
import {
  listVerifications,
  getVerificationDetail,
  type VerificationListParams,
} from "@/lib/verification-api";

export function useSubmitVerification(options?: {
  onSuccess?: (data: BffVerificationStatusResponse) => void;
}) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (payload: BffVerificationSubmission) => {
      const { commandId } = await submitVerification(payload);
      return pollVerificationStatus(commandId);
    },
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["verification-list"] });
      options?.onSuccess?.(data);
    },
  });
}

export function useVerificationStatus(
  commandId: string | null,
  options?: { enabled?: boolean },
) {
  return useQuery({
    queryKey: ["verification-status", commandId],
    queryFn: () => getVerificationStatus(commandId!),
    enabled: !!commandId && options?.enabled !== false,
    refetchInterval: (query) => {
      const status = query.state.data?.status;
      if (isTerminalVerificationStatus(status)) {
        return false;
      }
      return 1500;
    },
  });
}

export function useVerificationList(params: VerificationListParams) {
  return useQuery({
    queryKey: ["verification-list", params],
    queryFn: () => listVerifications(params),
  });
}

export function useVerificationDetail(correlationId: string | null) {
  return useQuery({
    queryKey: ["verification-detail", correlationId],
    queryFn: () => getVerificationDetail(correlationId!),
    enabled: !!correlationId,
  });
}
