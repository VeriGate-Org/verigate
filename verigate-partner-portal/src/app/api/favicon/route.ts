import { NextResponse } from "next/server";

/**
 * Dynamic favicon route.
 *
 * In production this project uses `output: "export"` (static HTML), so this
 * route is pre-rendered at build time and serves the default VeriGate favicon.
 * Tenant-specific favicons are injected client-side by PartnerTenantProvider
 * once branding loads.
 */

export const dynamic = "force-static";

const DEFAULT_PRIMARY = "#0972d3";
const DEFAULT_ACCENT = "#ec7211";

function shieldSvg(primary: string, check: string): string {
  return `<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 28 28">
  <path fill="${primary}" d="M14 2c-3.8 0-7 1.33-7 1.33v7.7c0 5.2 3.4 10.03 7 12.24 3.6-2.21 7-7.04 7-12.24V3.33C21 3.33 17.8 2 14 2Z"/>
  <path d="M8.5 14.5l3.5 3.5 7.5-7.5" fill="none" stroke="${check}" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/>
</svg>`;
}

export async function GET() {
  return new NextResponse(shieldSvg(DEFAULT_PRIMARY, DEFAULT_ACCENT), {
    headers: {
      "Content-Type": "image/svg+xml",
      "Cache-Control": "public, max-age=3600, s-maxage=86400",
    },
  });
}
