import { NextResponse } from "next/server";
import { config } from "@/lib/config";
import { generatePropertyOwnershipResponse } from "@/lib/mock-services";

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
  const searchType = String(body.searchType || "");
  const query = String(body.query || "");
  const province = String(body.province || "Gauteng");

  try {
    const response = generatePropertyOwnershipResponse({ searchType, query, province });
    return NextResponse.json(response);
  } catch (err) {
    const message = err instanceof Error ? err.message : "Search failed";
    return NextResponse.json({ error: message }, { status: 400 });
  }
}
