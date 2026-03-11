import { config } from "@/lib/config";
import { type Verification, type VerificationType } from "@/lib/types";

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
  "TAX", "INCOME", "IDENTITY", "FULL_VERIFICATION", "WATCHLIST",
]);

function generateMockVerifications(): Verification[] {
  const now = Date.now();
  const statuses = ["in_progress", "success", "soft_fail", "hard_fail"] as const;
  const types: VerificationType[] = [
    "ID", "CIPC", "DEEDS", "AVS", "SANCTIONS",
    "EMPLOYMENT", "CREDIT", "INCOME", "TAX", "IDENTITY",
    "DOCUMENT", "QUALIFICATION", "NEGATIVE_NEWS", "FRAUD_WATCHLIST", "FULL_VERIFICATION", "WATCHLIST",
  ];
  const providers = [
    "DHA", "CIPC", "Deeds Registry", "Qlink", "World-Check",
    "EmployVerify", "TransUnion", "PayrollVerify", "SARS", "DHA",
    "DocumentVerify", "SAQA", "MediaScreen", "SAFPS", "VeriGate", "World-Check",
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
  const resp = await fetch(url, {
    headers: {
      "Content-Type": "application/json",
      ...(config.bffApiKey ? { "X-API-Key": config.bffApiKey } : {}),
    },
  });
  if (!resp.ok) {
    throw new Error(`BFF returned ${resp.status}`);
  }
  return resp.json();
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
