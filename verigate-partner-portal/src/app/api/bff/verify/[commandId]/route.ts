import { NextResponse } from "next/server";

const BFF_BASE_URL = process.env.BFF_BASE_URL || "http://localhost:8080";
const BFF_API_KEY = process.env.BFF_API_KEY || "";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ commandId: string }> }
) {
  try {
    const { commandId } = await params;

    const response = await fetch(`${BFF_BASE_URL}/api/verifications/${commandId}`, {
      headers: {
        ...(BFF_API_KEY ? { "X-API-Key": BFF_API_KEY } : {}),
      },
    });

    const data = await response.json();

    if (!response.ok) {
      return NextResponse.json(data, { status: response.status });
    }

    return NextResponse.json(data);
  } catch (err) {
    const message = err instanceof Error ? err.message : "BFF proxy error";
    return NextResponse.json({ error: message }, { status: 502 });
  }
}
