import { NextResponse } from "next/server";
import { config } from "@/lib/config";
import { addVerificationWithEvents } from "@/lib/mock-db";
import type { Verification, VerificationEvent } from "@/lib/types";
import { generatePersonalDetailsResponse } from "@/lib/mock-services";

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
  const { firstName = "", surname = "", idNumber = "", reason = "" } = body || {};

  try {
    const response = generatePersonalDetailsResponse({ firstName, surname, idNumber, reason });
    const correlationId = response.reference;
    const startedAt = new Date().toISOString();
    const verified = response.validation.verified;
    const v: Verification = {
      correlationId,
      partnerId: "partner-abc",
      type: "ID",
      status: verified ? "success" : "hard_fail",
      provider: response.provider,
      startedAt,
      completedAt: startedAt,
      durationMs: 800,
    };
    const events: VerificationEvent[] = [
      { ts: startedAt, eventType: "VerificationRequested", source: "VerificationService", correlationId },
      {
        ts: startedAt,
        eventType: verified ? "VerificationSucceeded" : "VerificationHardFail",
        source: "PersonalDetailsService",
        correlationId,
        detail: {
          idNumber,
          nameMatchConfidence: response.validation.nameMatchConfidence,
          verified,
        },
      },
    ];
    try {
      addVerificationWithEvents(v, events);
    } catch {}

    return NextResponse.json(response);
  } catch (err) {
    const message = err instanceof Error ? err.message : "Verification failed";
    return NextResponse.json({ error: message }, { status: 400 });
  }
}
