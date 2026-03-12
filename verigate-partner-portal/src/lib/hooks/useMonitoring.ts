"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  listMonitoredSubjects,
  getMonitoredSubject,
  createMonitoredSubject,
  updateMonitoredSubject,
  deleteMonitoredSubject,
  listMonitoringAlerts,
  acknowledgeAlert,
} from "@/lib/bff-client";
import type { MonitoringStatus, MonitoringFrequency } from "@/lib/bff-client";

export function useMonitoredSubjects(params?: { status?: string; pageSize?: number }) {
  return useQuery({
    queryKey: ["monitoring-subjects", params],
    queryFn: () => listMonitoredSubjects(params),
  });
}

export function useMonitoredSubject(subjectId: string | null) {
  return useQuery({
    queryKey: ["monitoring-subject", subjectId],
    queryFn: () => getMonitoredSubject(subjectId!),
    enabled: !!subjectId,
  });
}

export function useCreateMonitoredSubject() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createMonitoredSubject,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["monitoring-subjects"] });
    },
  });
}

export function useUpdateMonitoredSubject() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      subjectId,
      updates,
    }: {
      subjectId: string;
      updates: Partial<{ status: MonitoringStatus; monitoringFrequency: MonitoringFrequency }>;
    }) => updateMonitoredSubject(subjectId, updates),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["monitoring-subjects"] });
      queryClient.invalidateQueries({ queryKey: ["monitoring-subject"] });
    },
  });
}

export function useDeleteMonitoredSubject() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteMonitoredSubject,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["monitoring-subjects"] });
    },
  });
}

export function useMonitoringAlerts(params?: { subjectId?: string; severity?: string; pageSize?: number }) {
  return useQuery({
    queryKey: ["monitoring-alerts", params],
    queryFn: () => listMonitoringAlerts(params),
  });
}

export function useAcknowledgeAlert() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: acknowledgeAlert,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["monitoring-alerts"] });
    },
  });
}
