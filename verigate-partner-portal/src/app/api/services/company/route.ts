import { NextResponse } from "next/server";
import { config } from "@/lib/config";
import { addVerificationWithEvents } from "@/lib/mock-db";
import type { Verification, VerificationEvent } from "@/lib/types";
import { generateCompanyResponse } from "@/lib/mock-services";

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
  const { regNumber = "", name = "" } = body || {};

  try {
    const response = generateCompanyResponse({ regNumber, name });
    const correlationId = response.reference;
    const startedAt = new Date().toISOString();
    const v: Verification = {
      correlationId,
      partnerId: "partner-abc",
      type: "CIPC",
      status: "success",
      provider: response.provider,
      startedAt,
      completedAt: startedAt,
      durationMs: 950,
    };
    const events: VerificationEvent[] = [
      { ts: startedAt, eventType: "VerificationRequested", source: "VerificationService", correlationId },
      {
        ts: startedAt,
        eventType: "VerificationSucceeded",
        source: "CompanyVerificationService",
        correlationId,
        detail: { regNumber: response.entity.regNumber, status: response.entity.status },
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
