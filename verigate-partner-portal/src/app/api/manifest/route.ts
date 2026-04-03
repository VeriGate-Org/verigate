import { NextRequest, NextResponse } from "next/server";

/**
 * Dynamic PWA manifest — generates manifest.json with tenant name and theme color.
 */
export async function GET(request: NextRequest) {
  const slug = request.headers.get("x-tenant-slug");
  let name = "VeriGate Partner Portal";
  let themeColor = "#0972d3";

  if (slug) {
    try {
      const origin = request.nextUrl.origin;
      const res = await fetch(`${origin}/api/tenant/${slug}`);
      if (res.ok) {
        const data = await res.json();
        if (data.name) name = `${data.name} — VeriGate`;
        if (data.primaryColor) themeColor = data.primaryColor;
      }
    } catch {
      // Fall through to defaults
    }
  }

  const manifest = {
    name,
    short_name: name.split(" — ")[0],
    start_url: "/dashboard",
    display: "standalone",
    background_color: "#ffffff",
    theme_color: themeColor,
    icons: [
      {
        src: "/api/favicon",
        sizes: "any",
        type: "image/svg+xml",
      },
    ],
  };

  return NextResponse.json(manifest, {
    headers: { "Cache-Control": "public, max-age=3600" },
  });
}
