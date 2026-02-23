import type { Verification, VerificationEvent } from "@/lib/types";

const extraVerifications: Verification[] = [];
const extraEvents: Record<string, VerificationEvent[]> = {};

export function addVerificationWithEvents(v: Verification, events: VerificationEvent[]) {
  extraVerifications.unshift(v);
  extraEvents[v.correlationId] = events;
}

export function listExtraVerifications() {
  return extraVerifications;
}

export function getEvents(correlationId: string): VerificationEvent[] | undefined {
  return extraEvents[correlationId];
}

export function getVerification(correlationId: string): Verification | undefined {
  return extraVerifications.find((v) => v.correlationId === correlationId);
}

