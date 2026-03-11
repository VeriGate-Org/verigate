import { NextResponse } from "next/server";

const BFF_BASE_URL = process.env.BFF_BASE_URL || "http://localhost:8080";
const BFF_API_KEY = process.env.BFF_API_KEY || "";

function bffHeaders(): Record<string, string> {
  return {
    "Content-Type": "application/json",
    ...(BFF_API_KEY ? { "X-API-Key": BFF_API_KEY } : {}),
  };
}

export async function PUT(
  request: Request,
  { params }: { params: Promise<{ policyId: string }> },
) {
  try {
    const { policyId } = await params;
    const body = await request.json();
    const response = await fetch(`${BFF_BASE_URL}/api/partner/policies/${policyId}`, {
      method: "PUT",
      headers: bffHeaders(),
      body: JSON.stringify(body),
    });
    const data = await response.json();
    return NextResponse.json(data, { status: response.status });
  } catch (err) {
    const message = err instanceof Error ? err.message : "BFF proxy error";
    return NextResponse.json({ error: message }, { status: 502 });
  }
}

export async function DELETE(
  _request: Request,
  { params }: { params: Promise<{ policyId: string }> },
) {
  try {
    const { policyId } = await params;
    const response = await fetch(`${BFF_BASE_URL}/api/partner/policies/${policyId}`, {
      method: "DELETE",
      headers: bffHeaders(),
    });
    if (response.status === 204) {
      return new NextResponse(null, { status: 204 });
    }
    const data = await response.json();
    return NextResponse.json(data, { status: response.status });
  } catch (err) {
    const message = err instanceof Error ? err.message : "BFF proxy error";
    return NextResponse.json({ error: message }, { status: 502 });
  }
}
