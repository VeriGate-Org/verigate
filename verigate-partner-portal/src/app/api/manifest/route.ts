import { NextResponse } from "next/server";

/**
 * Dynamic manifest route.
 *
 * In production this project uses `output: "export"` (static HTML), so this
 * route is pre-rendered at build time and serves the default VeriGate manifest.
 * Tenant-specific manifest data is available via the branding context.
 */

export const dynamic = "force-static";

const DEFAULT_PRIMARY = "#1A2E4B";

export async function GET() {
  const manifest = {
    name: "VeriGate Partner Portal",
    short_name: "VeriGate",
    description: "Verification and compliance platform",
    start_url: "/dashboard",
    display: "standalone",
    background_color: "#ffffff",
    theme_color: DEFAULT_PRIMARY,
    categories: ["business", "security"],
    icons: [
      {
        src: "/api/favicon",
        sizes: "any",
        type: "image/svg+xml",
      },
    ],
  };

  return NextResponse.json(manifest, {
    headers: {
      "Content-Type": "application/manifest+json",
      "Cache-Control": "public, max-age=3600, s-maxage=86400",
    },
  });
}
