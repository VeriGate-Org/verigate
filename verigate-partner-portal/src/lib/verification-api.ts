import { config } from "@/lib/config";
import { type Verification, type VerificationType, type VerificationStatus, type VerificationEvent } from "@/lib/types";
import type { BffVerificationListResponse } from "@/lib/bff-client";
import { getPortalType } from "@/lib/verification-type-map";

export interface VerificationListParams {
  q?: string;
  status?: string;
  type?: string;
  provider?: string;
  from?: string;
  to?: string;
  page?: number;
  pageSize?: number;
  sortBy?: string;
  sortDir?: string;
}

export interface VerificationListResponse {
  total: number;
  items: Verification[];
}

const VALID_TYPES = new Set<VerificationType>([
  "ID", "CIPC", "DEEDS", "AVS", "SANCTIONS",
  "EMPLOYMENT", "NEGATIVE_NEWS", "FRAUD_WATCHLIST",
  "DOCUMENT", "QUALIFICATION", "CREDIT",
  "TAX", "INCOME", "IDENTITY", "WATCHLIST",
]);

function generateMockVerifications(): Verification[] {
  const now = Date.now();
  const statuses = ["in_progress", "success", "soft_fail", "hard_fail"] as const;
  const types: VerificationType[] = [
    "ID", "CIPC", "DEEDS", "AVS", "SANCTIONS",
    "EMPLOYMENT", "CREDIT", "INCOME", "TAX", "IDENTITY",
    "DOCUMENT", "QUALIFICATION", "NEGATIVE_NEWS", "FRAUD_WATCHLIST", "WATCHLIST",
  ];
  const providers = [
    "DHA", "CIPC", "Deeds Registry", "Qlink", "World-Check",
    "EmployVerify", "TransUnion", "PayrollVerify", "SARS", "DHA",
    "DocumentVerify", "SAQA", "MediaScreen", "SAFPS", "World-Check",
  ];
  return Array.from({ length: 32 }).map((_, i) => {
    const status = statuses[i % statuses.length];
    const startedAt = new Date(now - i * 60_000).toISOString();
    return {
      correlationId: `vg-${(1000 + i).toString(16)}`,
      partnerId: "partner-abc",
      type: types[i % types.length],
      status,
      provider: providers[i % providers.length],
      workflowId: `wf-${(i % 5) + 1}`,
      workflowName: `Onboarding v${(i % 3) + 1}`,
      policyVersion: `v${(i % 7) + 1}.0`,
      startedAt,
      completedAt: status === "in_progress" ? undefined : new Date(now - i * 60_000 + 10_000).toISOString(),
      durationMs: status === "in_progress" ? undefined : 10_000,
    } as Verification;
  });
}

function fetchMockVerifications(params: VerificationListParams): VerificationListResponse {
  const q = (params.q || "").toLowerCase();
  const page = params.page || 1;
  const pageSize = params.pageSize || 10;
  const sortBy = params.sortBy || "startedAt";
  const sortDir = (params.sortDir || "desc").toLowerCase() === "asc" ? "asc" : "desc";
  const requestedType = params.type && VALID_TYPES.has(params.type as VerificationType)
    ? (params.type as VerificationType)
    : undefined;

  const all = generateMockVerifications();
  let items = all.filter((v) => {
    if (q && !(`${v.correlationId}`.toLowerCase().includes(q) || v.partnerId.toLowerCase().includes(q))) {
      return false;
    }
    if (params.status && v.status !== params.status) return false;
    if (requestedType && v.type !== requestedType) return false;
    if (params.provider && v.provider !== params.provider) return false;
    return true;
  });

  if (params.from) {
    const fromMs = Date.parse(params.from);
    items = items.filter((v) => Date.parse(v.startedAt) >= fromMs);
  }
  if (params.to) {
    const toMs = Date.parse(params.to);
    items = items.filter((v) => Date.parse(v.startedAt) <= toMs);
  }

  items.sort((a, b) => {
    const dir = sortDir === "asc" ? 1 : -1;
    if (sortBy === "startedAt") return (Date.parse(a.startedAt) - Date.parse(b.startedAt)) * dir;
    if (sortBy === "status") return (a.status.localeCompare(b.status)) * dir;
    if (sortBy === "type") return (a.type.localeCompare(b.type)) * dir;
    if (sortBy === "provider") return ((a.provider || "").localeCompare(b.provider || "")) * dir;
    return (Date.parse(a.startedAt) - Date.parse(b.startedAt)) * dir;
  });

  const start = (page - 1) * pageSize;
  return {
    total: items.length,
    items: items.slice(start, start + pageSize),
  };
}

function getAuthHeaders(): Record<string, string> {
  const headers: Record<string, string> = { "Content-Type": "application/json" };
  try {
    const raw = sessionStorage.getItem("verigate-auth");
    if (raw) {
      const session = JSON.parse(raw);
      if (session.idToken) {
        headers["Authorization"] = `Bearer ${session.idToken}`;
      }
    }
  } catch {
    // sessionStorage may be unavailable during SSR/build
  }
  return headers;
}

// ── BFF response transformers ───────────────────────────────────────

function mapBffStatus(bffStatus: string): VerificationStatus {
  switch (bffStatus.toUpperCase()) {
    case "COMPLETED":
    case "SUCCEEDED":
      return "success";
    case "PERMANENT_FAILURE":
    case "INVARIANT_FAILURE":
    case "HARD_FAIL":
      return "hard_fail";
    case "TRANSIENT_ERROR":
    case "SOFT_FAIL":
    case "SYSTEM_OUTAGE":
      return "soft_fail";
    case "PENDING":
      return "pending";
    case "IN_PROGRESS":
    case "PROCESSING":
    default:
      return "in_progress";
  }
}

function mapBffItemToVerification(item: BffVerificationListResponse["items"][number]): Verification {
  const portalType = getPortalType(item.commandName as never) ?? "ID";
  return {
    correlationId: item.commandId,
    partnerId: config.partnerId,
    type: portalType,
    status: mapBffStatus(item.status),
    startedAt: item.createdAt,
  };
}

function transformBffListResponse(bff: BffVerificationListResponse): VerificationListResponse {
  return {
    total: bff.items.length,
    items: bff.items.map(mapBffItemToVerification),
  };
}

async function fetchBffVerifications(params: VerificationListParams): Promise<VerificationListResponse> {
  const sp = new URLSearchParams();
  if (params.q) sp.set("q", params.q);
  if (params.status) sp.set("status", params.status);
  if (params.type) sp.set("type", params.type);
  if (params.provider) sp.set("provider", params.provider);
  if (params.from) sp.set("from", params.from);
  if (params.to) sp.set("to", params.to);
  if (params.page) sp.set("page", String(params.page));
  if (params.pageSize) sp.set("pageSize", String(params.pageSize));
  if (params.sortBy) sp.set("sortBy", params.sortBy);
  if (params.sortDir) sp.set("sortDir", params.sortDir);

  const qs = sp.toString();
  const url = `${config.bffBaseUrl}/api/verifications${qs ? `?${qs}` : ""}`;
  const resp = await fetch(url, { headers: getAuthHeaders() });
  if (!resp.ok) {
    throw new Error(`BFF returned ${resp.status}`);
  }
  const body = await resp.json();

  // If response has BFF shape (items with commandId + cursor), transform it
  if (Array.isArray(body.items) && body.items.length > 0 && "commandId" in body.items[0]) {
    return transformBffListResponse(body as BffVerificationListResponse);
  }

  // Already in portal shape
  return body as VerificationListResponse;
}

export async function listVerifications(params: VerificationListParams): Promise<VerificationListResponse> {
  if (config.useMockServices) {
    return fetchMockVerifications(params);
  }
  return fetchBffVerifications(params);
}

export function parseSearchParams(searchParams: string): VerificationListParams {
  const sp = new URLSearchParams(searchParams);
  return {
    q: sp.get("q") || undefined,
    status: sp.get("status") || undefined,
    type: sp.get("type") || undefined,
    provider: sp.get("provider") || undefined,
    from: sp.get("from") || undefined,
    to: sp.get("to") || undefined,
    page: sp.has("page") ? Number(sp.get("page")) : undefined,
    pageSize: sp.has("pageSize") ? Number(sp.get("pageSize")) : undefined,
    sortBy: sp.get("sortBy") || undefined,
    sortDir: sp.get("sortDir") || undefined,
  };
}

// ── Single verification detail ──────────────────────────────────────

export interface VerificationDetail {
  verification: Verification;
  events: VerificationEvent[];
}

function generateMockEvents(v: Verification): VerificationEvent[] {
  const events: VerificationEvent[] = [
    {
      ts: v.startedAt,
      eventType: "VerificationRequested",
      source: v.provider || "VeriGate",
      correlationId: v.correlationId,
      detail: { type: v.type, provider: v.provider },
      stepSequence: 1,
    },
  ];

  if (v.status === "success" || v.status === "completed") {
    events.push({
      ts: v.completedAt || new Date(Date.parse(v.startedAt) + 10_000).toISOString(),
      eventType: "VerificationSucceeded",
      source: v.provider || "VeriGate",
      correlationId: v.correlationId,
      detail: { confidenceScore: 95 },
      stepSequence: 2,
    });
  } else if (v.status === "soft_fail" || v.status === "transient_error") {
    events.push({
      ts: v.completedAt || new Date(Date.parse(v.startedAt) + 10_000).toISOString(),
      eventType: "VerificationSoftFail",
      source: v.provider || "VeriGate",
      correlationId: v.correlationId,
      detail: { reason: "Partial match — manual review recommended" },
      stepSequence: 2,
    });
  } else if (v.status === "hard_fail" || v.status === "permanent_failure") {
    events.push({
      ts: v.completedAt || new Date(Date.parse(v.startedAt) + 10_000).toISOString(),
      eventType: "VerificationHardFail",
      source: v.provider || "VeriGate",
      correlationId: v.correlationId,
      detail: { reason: "Identity mismatch — no match found in source system" },
      stepSequence: 2,
    });
  }

  return events;
}

function fetchMockVerification(correlationId: string): VerificationDetail | null {
  const all = generateMockVerifications();
  const v = all.find((item) => item.correlationId === correlationId);
  if (!v) return null;
  return { verification: v, events: generateMockEvents(v) };
}

async function fetchBffVerification(correlationId: string): Promise<VerificationDetail> {
  const url = `${config.bffBaseUrl}/api/verifications/${correlationId}`;
  const resp = await fetch(url, { headers: getAuthHeaders() });
  if (!resp.ok) {
    if (resp.status === 404) return null as unknown as VerificationDetail;
    throw new Error(`BFF returned ${resp.status}`);
  }
  const body = await resp.json();

  // If response has BFF shape (commandId at top level), transform it
  if ("commandId" in body && !("verification" in body)) {
    const portalType = getPortalType(body.commandName as never) ?? "ID";
    const status = mapBffStatus(body.status);
    const verification: Verification = {
      correlationId: body.commandId,
      partnerId: config.partnerId,
      type: portalType,
      status,
      startedAt: body.createdAt ?? new Date().toISOString(),
      completedAt: body.completedAt,
      provider: body.provider,
    };

    const events: VerificationEvent[] = [
      {
        ts: verification.startedAt,
        eventType: "VerificationRequested",
        source: verification.provider || "VeriGate",
        correlationId: verification.correlationId,
        detail: { type: verification.type },
        stepSequence: 1,
      },
    ];

    // Map BFF events if present
    if (Array.isArray(body.events)) {
      for (const evt of body.events) {
        events.push({
          ts: evt.timestamp ?? evt.ts ?? verification.startedAt,
          eventType: evt.eventType ?? "DomainSpecific",
          source: evt.source ?? verification.provider ?? "VeriGate",
          correlationId: verification.correlationId,
          detail: evt.detail ?? evt.data,
          stepSequence: evt.stepSequence,
        });
      }
    } else if (status === "success") {
      events.push({
        ts: verification.completedAt ?? new Date().toISOString(),
        eventType: "VerificationSucceeded",
        source: verification.provider || "VeriGate",
        correlationId: verification.correlationId,
        detail: body.auxiliaryData ?? { outcome: "pass" },
        stepSequence: 2,
      });
    } else if (status === "hard_fail" || status === "permanent_failure") {
      events.push({
        ts: verification.completedAt ?? new Date().toISOString(),
        eventType: "VerificationHardFail",
        source: verification.provider || "VeriGate",
        correlationId: verification.correlationId,
        detail: { reason: body.errorDetails?.join("; ") ?? "Verification failed" },
        stepSequence: 2,
      });
    } else if (status === "soft_fail" || status === "transient_error") {
      events.push({
        ts: verification.completedAt ?? new Date().toISOString(),
        eventType: "VerificationSoftFail",
        source: verification.provider || "VeriGate",
        correlationId: verification.correlationId,
        detail: { reason: body.errorDetails?.join("; ") ?? "Partial match — manual review recommended" },
        stepSequence: 2,
      });
    }

    return { verification, events };
  }

  // Already in portal shape
  return body as VerificationDetail;
}

export async function getVerificationDetail(correlationId: string): Promise<VerificationDetail | null> {
  if (config.useMockServices) {
    return fetchMockVerification(correlationId);
  }
  return fetchBffVerification(correlationId);
}
