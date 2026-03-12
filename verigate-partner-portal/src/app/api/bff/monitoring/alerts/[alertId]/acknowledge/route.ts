import { NextResponse } from "next/server";

const BFF_BASE_URL = process.env.BFF_BASE_URL || "http://localhost:8080";
const BFF_API_KEY = process.env.BFF_API_KEY || "";

function bffHeaders(): Record<string, string> {
  return {
    "Content-Type": "application/json",
    ...(BFF_API_KEY ? { "X-API-Key": BFF_API_KEY } : {}),
  };
}

export async function POST(
  _request: Request,
  { params }: { params: Promise<{ alertId: string }> }
) {
  try {
    const { alertId } = await params;
    const response = await fetch(
      `${BFF_BASE_URL}/api/partner/monitoring/alerts/${alertId}/acknowledge`,
      {
        method: "POST",
        headers: bffHeaders(),
      }
    );
    const data = await response.json();
    return NextResponse.json(data, { status: response.status });
  } catch (err) {
    const message = err instanceof Error ? err.message : "BFF proxy error";
    return NextResponse.json({ error: message }, { status: 502 });
  }
}
