import axios from "axios";
import type { BffVerificationType } from "./types";

const bffApi = axios.create({
  baseURL: "/api/bff",
  headers: { "Content-Type": "application/json" },
});

export interface BffVerificationSubmission {
  verificationType: BffVerificationType;
  originationType: "ADHOC";
  originationId: string;
  requestedBy: string;
  metadata: Record<string, unknown>;
}

export interface BffVerificationResponse {
  commandId: string;
  status: string;
}

export interface BffVerificationStatusResponse {
  commandId: string;
  status: string;
  errorDetails?: string[];
  auxiliaryData?: Record<string, string>;
}

export async function submitVerification(
  payload: BffVerificationSubmission
): Promise<BffVerificationResponse> {
  const { data } = await bffApi.post<BffVerificationResponse>("/verify", payload);
  return data;
}

export async function getVerificationStatus(
  commandId: string
): Promise<BffVerificationStatusResponse> {
  const { data } = await bffApi.get<BffVerificationStatusResponse>(`/verify/${commandId}`);
  return data;
}

export async function pollVerificationStatus(
  commandId: string,
  options?: { maxAttempts?: number; intervalMs?: number }
): Promise<BffVerificationStatusResponse> {
  const maxAttempts = options?.maxAttempts ?? 20;
  const intervalMs = options?.intervalMs ?? 1500;

  for (let attempt = 0; attempt < maxAttempts; attempt++) {
    const status = await getVerificationStatus(commandId);

    if (
      status.status === "SUCCEEDED" ||
      status.status === "HARD_FAIL" ||
      status.status === "SOFT_FAIL" ||
      status.status === "SYSTEM_OUTAGE"
    ) {
      return status;
    }

    if (attempt < maxAttempts - 1) {
      await new Promise((resolve) => setTimeout(resolve, intervalMs));
    }
  }

  return getVerificationStatus(commandId);
}
