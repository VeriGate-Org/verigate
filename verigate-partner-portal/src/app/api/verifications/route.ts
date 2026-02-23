import { NextResponse } from "next/server";
import { config } from "@/lib/config";
import { type Verification, type VerificationType } from "@/lib/types";
import { listExtraVerifications } from "@/lib/mock-db";

function mockVerifications(): Verification[] {
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

function applyPagination<T>(items: T[], page: number, pageSize: number) {
  const start = (page - 1) * pageSize;
  return {
    total: items.length,
    items: items.slice(start, start + pageSize),
  };
}

const VALID_TYPES = new Set<VerificationType>([
  "ID", "CIPC", "DEEDS", "AVS", "SANCTIONS",
  "EMPLOYMENT", "NEGATIVE_NEWS", "FRAUD_WATCHLIST",
  "DOCUMENT", "QUALIFICATION", "CREDIT",
  "TAX", "INCOME", "IDENTITY", "FULL_VERIFICATION", "WATCHLIST",
]);

export async function GET(request: Request) {
  if (!config.useMockServices) {
    const { searchParams } = new URL(request.url);
    const qs = searchParams.toString();
    const url = `${config.bffBaseUrl}/api/verifications${qs ? `?${qs}` : ""}`;
    try {
      const resp = await fetch(url, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          ...(config.bffApiKey ? { "X-API-Key": config.bffApiKey } : {}),
        },
      });
      const data = await resp.json();
      return NextResponse.json(data, { status: resp.status });
    } catch {
      return NextResponse.json({ error: "BFF unavailable" }, { status: 502 });
    }
  }

  const { searchParams } = new URL(request.url);
  const q = (searchParams.get("q") || "").toLowerCase();
  const status = searchParams.get("status");
  const typeParam = searchParams.get("type");
  const provider = searchParams.get("provider");
  const from = searchParams.get("from");
  const to = searchParams.get("to");
  const page = Number(searchParams.get("page") || 1);
  const pageSize = Number(searchParams.get("pageSize") || 10);
  const sortBy = searchParams.get("sortBy") || "startedAt";
  const sortDir = (searchParams.get("sortDir") || "desc").toLowerCase() === "asc" ? "asc" : "desc";
  const requestedType = typeParam && VALID_TYPES.has(typeParam as VerificationType) ? (typeParam as VerificationType) : undefined;
  const all = [...listExtraVerifications(), ...mockVerifications()];
  let items = all.filter((v) => {
    if (q && !(`${v.correlationId}`.toLowerCase().includes(q) || v.partnerId.toLowerCase().includes(q))) {
      return false;
    }
    if (status && v.status !== status) return false;
    if (requestedType && v.type !== requestedType) return false;
    if (provider && v.provider !== provider) return false;
    return true;
  });
  if (from) {
    const fromMs = Date.parse(from);
    items = items.filter((v) => Date.parse(v.startedAt) >= fromMs);
  }
  if (to) {
    const toMs = Date.parse(to);
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
  const paged = applyPagination(items, page, pageSize);
  return NextResponse.json(paged);
}
