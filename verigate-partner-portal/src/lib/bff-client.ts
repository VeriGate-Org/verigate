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
