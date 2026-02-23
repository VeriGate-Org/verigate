import { NextResponse } from "next/server";
import { config } from "@/lib/config";
import { addVerificationWithEvents } from "@/lib/mock-db";
import type { Verification, VerificationEvent } from "@/lib/types";

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

  const { idNumber } = await request.json();
  if (!idNumber) return NextResponse.json({ error: "idNumber required" }, { status: 400 });
  const correlationId = `adhoc-id-${Date.now()}`;
  const startedAt = new Date().toISOString();
  const v: Verification = { correlationId, partnerId: "partner-abc", type: "ID", status: "success", provider: "DHA", startedAt, completedAt: startedAt, durationMs: 1200 };
  const events: VerificationEvent[] = [
    { ts: startedAt, eventType: "VerificationRequested", source: "VerificationService", correlationId },
    { ts: startedAt, eventType: "VerificationSucceeded", source: "PersonalDetailsService", correlationId, detail: { idNumber, match: true } },
  ];
  addVerificationWithEvents(v, events);
  return NextResponse.json({ correlationId, result: { match: true } });
}

