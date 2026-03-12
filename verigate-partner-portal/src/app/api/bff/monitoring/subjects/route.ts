import { NextResponse } from "next/server";

const BFF_BASE_URL = process.env.BFF_BASE_URL || "http://localhost:8080";
const BFF_API_KEY = process.env.BFF_API_KEY || "";

function bffHeaders(): Record<string, string> {
  return {
    "Content-Type": "application/json",
    ...(BFF_API_KEY ? { "X-API-Key": BFF_API_KEY } : {}),
  };
}

export async function GET(request: Request) {
  try {
    const { searchParams } = new URL(request.url);
    const params = new URLSearchParams();
    if (searchParams.get("status")) params.set("status", searchParams.get("status")!);
    if (searchParams.get("pageSize")) params.set("pageSize", searchParams.get("pageSize")!);
    const query = params.toString();

    const response = await fetch(
      `${BFF_BASE_URL}/api/partner/monitoring/subjects${query ? `?${query}` : ""}`,
      { headers: bffHeaders() }
    );
    const data = await response.json();
    return NextResponse.json(data, { status: response.status });
  } catch (err) {
    const message = err instanceof Error ? err.message : "BFF proxy error";
    return NextResponse.json({ error: message }, { status: 502 });
  }
}

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const response = await fetch(
      `${BFF_BASE_URL}/api/partner/monitoring/subjects`,
      {
        method: "POST",
        headers: bffHeaders(),
        body: JSON.stringify(body),
      }
    );
    const data = await response.json();
    return NextResponse.json(data, { status: response.status });
  } catch (err) {
    const message = err instanceof Error ? err.message : "BFF proxy error";
    return NextResponse.json({ error: message }, { status: 502 });
  }
}
