import { NextResponse } from "next/server";
import { config } from "@/lib/config";
import { addVerificationWithEvents } from "@/lib/mock-db";
import type { Verification, VerificationEvent } from "@/lib/types";
import { generateSanctionsResponse } from "@/lib/mock-services";

export async function POST(request: Request) {
  if (!config.useMockServices) {
    const body = await request.json().catch(() => ({}));
    try {
      const resp = await fetch(`${config.bffBaseUrl}/api/verify`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(config.bffApiKey ? { "X-API-Key": config.bffApiKey } : {}),
        },
        body: JSON.stringify(body),
      });
      const data = await resp.json();
      return NextResponse.json(data, { status: resp.status });
    } catch {
      return NextResponse.json({ error: "BFF unavailable" }, { status: 502 });
    }
  }

  const body = await request.json().catch(() => ({}));
  const { name = "" } = body || {};

  try {
    const response = generateSanctionsResponse({ name });
    const correlationId = response.correlationId;
    const startedAt = new Date().toISOString();
    const v: Verification = {
      correlationId,
      partnerId: "partner-abc",
      type: "SANCTIONS",
      status: "success",
      provider: "World-Check",
      startedAt,
      completedAt: startedAt,
      durationMs: 800,
    };
    const events: VerificationEvent[] = [
      { ts: startedAt, eventType: "VerificationRequested", source: "VerificationService", correlationId },
      {
        ts: startedAt,
        eventType: "VerificationSucceeded",
        source: "SanctionsVerificationService",
        correlationId,
        detail: {
          pep: response.result.pep,
          sanctionsHitCount: response.result.sanctionsHitCount,
          name,
        },
      },
    ];
    try {
      addVerificationWithEvents(v, events);
    } catch {}

    return NextResponse.json(response);
  } catch (err) {
    const message = err instanceof Error ? err.message : "Screening failed";
    return NextResponse.json({ error: message }, { status: 400 });
  }
}
