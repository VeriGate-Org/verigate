"use client";

import { useMutation, useQuery } from "@tanstack/react-query";
import { submitVerification, getVerificationStatus, pollVerificationStatus } from "@/lib/bff-client";
import type { BffVerificationSubmission } from "@/lib/bff-client";

export function useSubmitVerification() {
  return useMutation({
    mutationFn: async (payload: BffVerificationSubmission) => {
      const { commandId } = await submitVerification(payload);
      return pollVerificationStatus(commandId);
    },
  });
}

export function useVerificationStatus(commandId: string | null, options?: { enabled?: boolean }) {
  return useQuery({
    queryKey: ["verification-status", commandId],
    queryFn: () => getVerificationStatus(commandId!),
    enabled: !!commandId && (options?.enabled !== false),
    refetchInterval: (query) => {
      const status = query.state.data?.status;
      if (
        status === "SUCCEEDED" ||
        status === "HARD_FAIL" ||
        status === "SOFT_FAIL" ||
        status === "SYSTEM_OUTAGE"
      ) {
        return false;
      }
      return 1500;
    },
  });
}
