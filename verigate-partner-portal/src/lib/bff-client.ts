import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import type { BffVerificationType } from "./types";
import { config } from "./config";

export class BffApiError extends Error {
  constructor(
    message: string,
    public readonly statusCode?: number,
    public readonly correlationId?: string,
  ) {
    super(message);
    this.name = "BffApiError";
  }
}

const bffApi = axios.create({
  baseURL: config.bffBaseUrl,
  timeout: 30_000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor to attach auth headers and correlation ID
bffApi.interceptors.request.use((reqConfig: InternalAxiosRequestConfig) => {
  if (config.bffApiKey) {
    reqConfig.headers["X-API-Key"] = config.bffApiKey;
  }

  reqConfig.headers["X-Correlation-ID"] = crypto.randomUUID();

  // Attach access token from session storage if available
  try {
    const raw = sessionStorage.getItem("verigate-auth");
    if (raw) {
      const session = JSON.parse(raw);
      if (session.accessToken) {
        reqConfig.headers["Authorization"] = `Bearer ${session.accessToken}`;
      }
    }
  } catch {
    // sessionStorage may be unavailable during SSR/build
  }

  return reqConfig;
});

// Response interceptor for error handling
bffApi.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    const correlationId = error.config?.headers?.["X-Correlation-ID"] as string | undefined;

    if (error.code === "ECONNABORTED") {
      return Promise.reject(
        new BffApiError("Request timed out. Please try again.", undefined, correlationId),
      );
    }

    if (!error.response) {
      return Promise.reject(
        new BffApiError(
          "Unable to connect to the server. Please check your connection.",
          undefined,
          correlationId,
        ),
      );
    }

    const status = error.response.status;
    let message: string;

    if (status === 401) {
      message = "Authentication failed. Please sign in again.";
    } else if (status === 403) {
      message = "You do not have permission to perform this action.";
    } else if (status === 404) {
      message = "The requested resource was not found.";
    } else if (status === 429) {
      message = "Too many requests. Please wait a moment and try again.";
    } else if (status >= 500) {
      message = "A server error occurred. Please try again later.";
    } else {
      message = `Request failed with status ${status}.`;
    }

    return Promise.reject(new BffApiError(message, status, correlationId));
  },
);

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

export interface BffVerificationListResponse {
  items: Array<{
    commandId: string;
    status: string;
    createdAt: string;
    commandName: string;
  }>;
  cursor: string | null;
  hasMore: boolean;
}

export async function submitVerification(
  payload: BffVerificationSubmission,
): Promise<BffVerificationResponse> {
  const { data } = await bffApi.post<BffVerificationResponse>("/api/verifications", payload);
  return data;
}

export async function getVerificationStatus(
  commandId: string,
): Promise<BffVerificationStatusResponse> {
  const { data } = await bffApi.get<BffVerificationStatusResponse>(
    `/api/verifications/${commandId}`,
  );
  return data;
}

export async function listVerificationsBff(params?: {
  status?: string;
  cursor?: string;
  limit?: number;
}): Promise<BffVerificationListResponse> {
  const searchParams = new URLSearchParams();
  if (params?.status) searchParams.set("status", params.status);
  if (params?.cursor) searchParams.set("cursor", params.cursor);
  if (params?.limit) searchParams.set("limit", String(params.limit));

  const qs = searchParams.toString();
  const { data } = await bffApi.get<BffVerificationListResponse>(
    `/api/verifications${qs ? `?${qs}` : ""}`,
  );
  return data;
}

// ── Policy APIs ──────────────────────────────────────────────────────

export interface BffPolicyStep {
  type: string;
  name: string;
  config: Record<string, unknown>;
  next?: string;
  onSuccess?: string;
  onFail?: string;
  parallel?: string[];
}

export interface BffPolicyResponse {
  id: string;
  partnerId: string;
  name: string;
  description: string | null;
  version: number;
  status: string;
  steps: BffPolicyStep[] | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface BffPolicyListResponse {
  items: BffPolicyResponse[];
}

export async function listPolicies(): Promise<BffPolicyListResponse> {
  const { data } = await bffApi.get<BffPolicyListResponse>("/api/partner/policies");
  return data;
}

export async function createPolicy(payload: {
  name: string;
  description?: string;
  steps: BffPolicyStep[];
}): Promise<BffPolicyResponse> {
  const { data } = await bffApi.post<BffPolicyResponse>("/api/partner/policies", payload);
  return data;
}

export async function updatePolicy(
  policyId: string,
  payload: { name: string; description?: string; steps: BffPolicyStep[] },
): Promise<BffPolicyResponse> {
  const { data } = await bffApi.put<BffPolicyResponse>(`/api/partner/policies/${policyId}`, payload);
  return data;
}

export async function deletePolicy(policyId: string): Promise<void> {
  await bffApi.delete(`/api/partner/policies/${policyId}`);
}

export async function publishPolicy(policyId: string): Promise<BffPolicyResponse> {
  const { data } = await bffApi.post<BffPolicyResponse>(`/api/partner/policies/${policyId}/publish`);
  return data;
}

// ── Report APIs ─────────────────────────────────────────────────────

export interface BffReportResponse {
  id: string;
  partnerId: string;
  name: string;
  type: string;
  description: string | null;
  status: string;
  filter: Record<string, unknown> | null;
  schedule: { frequency: string; time: string; recipients: string[] } | null;
  createdAt: string | null;
}

export interface BffReportListResponse {
  items: BffReportResponse[];
}

export async function generateReport(payload: {
  name: string;
  type: string;
  description?: string;
  filter?: Record<string, unknown>;
}): Promise<BffReportResponse> {
  const { data } = await bffApi.post<BffReportResponse>("/api/partner/reports/generate", payload);
  return data;
}

export async function listReports(): Promise<BffReportListResponse> {
  const { data } = await bffApi.get<BffReportListResponse>("/api/partner/reports");
  return data;
}

export async function scheduleReport(
  reportId: string,
  schedule: { frequency: string; time: string; recipients: string[] },
): Promise<BffReportResponse> {
  const { data } = await bffApi.post<BffReportResponse>(
    `/api/partner/reports/${reportId}/schedule`,
    schedule,
  );
  return data;
}

export async function deleteReport(reportId: string): Promise<void> {
  await bffApi.delete(`/api/partner/reports/${reportId}`);
}

// ── Profile APIs ────────────────────────────────────────────────────

export interface BffProfileResponse {
  partnerId: string;
  name: string;
  contactEmail: string;
  billingPlan: string;
  status: string;
  createdAt: string | null;
}

export async function getProfile(): Promise<BffProfileResponse> {
  const { data } = await bffApi.get<BffProfileResponse>("/api/partner/profile");
  return data;
}

export async function updateProfile(payload: {
  name?: string;
  contactEmail?: string;
}): Promise<BffProfileResponse> {
  const { data } = await bffApi.put<BffProfileResponse>("/api/partner/profile", payload);
  return data;
}

// ── API Key APIs ────────────────────────────────────────────────────

export interface BffApiKeyItem {
  keyPrefix: string;
  status: string;
  createdAt: string | null;
  createdBy: string | null;
}

export interface BffApiKeyListResponse {
  partnerId: string;
  keys: BffApiKeyItem[];
}

export interface BffApiKeyGenerateResponse {
  apiKey: string;
  partnerId: string;
  keyPrefix: string;
  status: string;
  createdAt: string;
}

export async function listApiKeys(): Promise<BffApiKeyListResponse> {
  const { data } = await bffApi.get<BffApiKeyListResponse>("/api/partner/api-keys");
  return data;
}

export async function generateApiKey(): Promise<BffApiKeyGenerateResponse> {
  const { data } = await bffApi.post<BffApiKeyGenerateResponse>("/api/partner/api-keys");
  return data;
}

export async function revokeApiKey(keyPrefix: string): Promise<void> {
  await bffApi.delete(`/api/partner/api-keys/${keyPrefix}`);
}

// ── Notification APIs ───────────────────────────────────────────────

export interface BffNotificationPreferences {
  verificationComplete: boolean;
  verificationFailure: boolean;
  weeklySummary: boolean;
  securityAlerts: boolean;
}

export async function getNotifications(): Promise<BffNotificationPreferences> {
  const { data } = await bffApi.get<BffNotificationPreferences>("/api/partner/notifications");
  return data;
}

export async function updateNotifications(
  prefs: BffNotificationPreferences,
): Promise<BffNotificationPreferences> {
  const { data } = await bffApi.put<BffNotificationPreferences>("/api/partner/notifications", prefs);
  return data;
}

// ── Bulk Verification APIs ──────────────────────────────────────────

export interface BffBulkActionResponse {
  action: string;
  processed: number;
  failed: number;
  message: string;
}

export async function exportVerifications(
  commandIds: string[],
  format?: string,
): Promise<BffBulkActionResponse> {
  const { data } = await bffApi.post<BffBulkActionResponse>("/api/partner/verifications/export", {
    commandIds,
    format: format ?? "csv",
  });
  return data;
}

export async function retryVerifications(commandIds: string[]): Promise<BffBulkActionResponse> {
  const { data } = await bffApi.post<BffBulkActionResponse>("/api/partner/verifications/retry", {
    commandIds,
  });
  return data;
}

export async function archiveVerifications(commandIds: string[]): Promise<BffBulkActionResponse> {
  const { data } = await bffApi.post<BffBulkActionResponse>("/api/partner/verifications/archive", {
    commandIds,
  });
  return data;
}

export async function deleteVerifications(commandIds: string[]): Promise<BffBulkActionResponse> {
  const { data } = await bffApi.delete<BffBulkActionResponse>("/api/partner/verifications", {
    data: { commandIds },
  });
  return data;
}

export async function pollVerificationStatus(
  commandId: string,
  options?: { maxAttempts?: number; intervalMs?: number },
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
