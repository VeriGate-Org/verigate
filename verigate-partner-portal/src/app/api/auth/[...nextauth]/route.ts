import { NextResponse } from "next/server";

const payload = { message: "Authentication is disabled in this environment." };

export function GET() {
  return NextResponse.json(payload, { status: 200 });
}

export function POST() {
  return NextResponse.json(payload, { status: 200 });
}
