import { NextRequest, NextResponse } from "next/server";
import { config } from "@/lib/config";
import { type Verification, type VerificationEvent } from "@/lib/types";
import { getEvents, getVerification } from "@/lib/mock-db";

function makeDetail(correlationId: string): { item: Verification; events: VerificationEvent[] } {
  const startedAt = new Date(Date.now() - 60_000).toISOString();
  const item: Verification = {
    correlationId,
    partnerId: "partner-abc",
    type: "AVS",
    status: "success",
    provider: "Qlink",
    startedAt,
    completedAt: new Date().toISOString(),
    durationMs: 12_500,
  };
  const events: VerificationEvent[] = [
    { ts: startedAt, eventType: "VerificationRequested", source: "VerificationService", correlationId },
    { ts: new Date(Date.now() - 45_000).toISOString(), eventType: "DomainSpecific", source: "BankAccountVerificationService", correlationId, detail: { request: { account: "****1234" } }, stepSequence: 1 },
    { ts: new Date(Date.now() - 10_000).toISOString(), eventType: "VerificationSucceeded", source: "BankAccountVerificationService", correlationId, detail: { avsStatus: "verified" }, stepSequence: 1 },
  ];
  return { item, events };
}

export async function GET(_: NextRequest, context: { params: Promise<{ correlationId: string }> }) {
  const { correlationId } = await context.params;

  if (!config.useMockServices) {
    try {
      const resp = await fetch(`${config.bffBaseUrl}/api/verifications/${correlationId}`, {
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

  const extra = getEvents(correlationId);
  const stored = getVerification(correlationId);
  if (extra || stored) {
    const detail = extra?.[1]?.detail;
    const detailObject = typeof detail === "object" && detail !== null ? (detail as Record<string, unknown>) : undefined;
    const inferredType = detailObject && typeof detailObject.avsStatus === "string" ? "AVS" : "ID";
    const provider = detailObject && typeof detailObject.provider === "string"
      ? detailObject.provider
      : detailObject && typeof detailObject.bank === "string"
        ? detailObject.bank
        : "DHA";

    const item: Verification =
      stored || {
        correlationId,
        partnerId: "partner-abc",
        type: inferredType,
        status: "success",
        provider,
        startedAt: new Date(Date.now() - 60_000).toISOString(),
        completedAt: new Date().toISOString(),
        durationMs: 12_500,
      };
    return NextResponse.json({ item, events: extra ?? [] });
  }
  return NextResponse.json(makeDetail(correlationId));
}
