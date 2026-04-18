"use client";

import { useQuery } from "@tanstack/react-query";
import { getSystemHealth } from "@/lib/bff-client";

export function useSystemHealth(enabled = true) {
  return useQuery({
    queryKey: ["system-health"],
    queryFn: getSystemHealth,
    staleTime: 30_000,
    enabled,
  });
}
