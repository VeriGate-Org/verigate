import { NextRequest, NextResponse } from "next/server";
import { config } from "@/lib/config";
import type { TenantBranding } from "@/lib/types/tenant-branding";

const MOCK_TENANTS: Record<string, TenantBranding> = {
  acme: {
    slug: "acme",
    name: "Acme Corp",
    logo: "/tenants/acme/logo.svg",
    logoDark: "/tenants/acme/logo-dark.svg",
    primaryColor: "#1a5276",
    accentColor: "#e67e22",
    tagline: "Trust, verified.",
  },
  fsca: {
    slug: "fsca",
    name: "FSCA",
    logo: "/tenants/fsca/logo.png",
    logoDark: "/tenants/fsca/logo-dark.png",
    faviconUrl: "/tenants/fsca/favicon.png",
    primaryColor: "#4562A1",
    accentColor: "#2D3E6E",
    tagline: "Financial Sector Conduct Authority",
  },
  default: {
    slug: "default",
    name: "VeriGate",
    primaryColor: "#0972d3",
    accentColor: "#ec7211",
  },
};

export async function GET(
  _request: NextRequest,
  { params }: { params: Promise<{ slug: string }> },
) {
  const { slug } = await params;

  if (config.useMockServices) {
    const branding = MOCK_TENANTS[slug] ?? MOCK_TENANTS["default"];
    return NextResponse.json(branding, {
      headers: { "Cache-Control": "public, max-age=300" },
    });
  }

  // Proxy to BFF — fall back to mock tenant data if BFF is unreachable
  const fallback = MOCK_TENANTS[slug] ?? MOCK_TENANTS["default"];

  try {
    const bffUrl = process.env.BFF_BASE_URL || config.bffBaseUrl;
    const res = await fetch(`${bffUrl}/api/public/tenant/${slug}`, {
      headers: {
        "X-API-Key": process.env.BFF_API_KEY || "",
      },
      next: { revalidate: 300 },
    });

    if (!res.ok) {
      return NextResponse.json(fallback, {
        headers: { "Cache-Control": "public, max-age=60" },
      });
    }

    const data = await res.json();
    return NextResponse.json(data, {
      headers: { "Cache-Control": "public, max-age=300" },
    });
  } catch {
    return NextResponse.json(fallback, {
      headers: { "Cache-Control": "public, max-age=60" },
    });
  }
}
