"use client";

import { useQuery } from "@tanstack/react-query";
import { getServiceHistory, getServiceUptime, getIncidents } from "@/lib/bff-client";

export function useServiceHistory(serviceId: string | null, range: string) {
  return useQuery({
    queryKey: ["health-history", serviceId, range],
    queryFn: () => getServiceHistory(serviceId!, range),
    enabled: !!serviceId,
    staleTime: 60_000,
  });
}

export function useServiceUptime(range: string) {
  return useQuery({
    queryKey: ["health-uptime", range],
    queryFn: () => getServiceUptime(range),
    staleTime: 60_000,
  });
}

export function useIncidents(range: string) {
  return useQuery({
    queryKey: ["health-incidents", range],
    queryFn: () => getIncidents(range),
    staleTime: 60_000,
  });
}
