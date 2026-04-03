import { NextRequest, NextResponse } from "next/server";

const DEFAULT_COLOR = "#E23D36";

/**
 * Dynamic favicon — renders a shield SVG with the tenant's primary color.
 * If the tenant has a faviconUrl, redirects to it instead.
 */
export async function GET(request: NextRequest) {
  const slug = request.headers.get("x-tenant-slug");
  let primaryColor = DEFAULT_COLOR;

  if (slug) {
    try {
      const origin = request.nextUrl.origin;
      const res = await fetch(`${origin}/api/tenant/${slug}`);
      if (res.ok) {
        const data = await res.json();
        if (data.faviconUrl) {
          return NextResponse.redirect(data.faviconUrl);
        }
        if (data.primaryColor) {
          primaryColor = data.primaryColor;
        }
      }
    } catch {
      // Fall through to default SVG
    }
  }

  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 28 28">
  <path fill="${primaryColor}" d="M14 2c-3.8 0-7 1.33-7 1.33v7.7c0 5.2 3.4 10.03 7 12.24 3.6-2.21 7-7.04 7-12.24V3.33C21 3.33 17.8 2 14 2Z"/>
  <path d="M8.5 14.5l3.5 3.5 7.5-7.5" fill="none" stroke="#fff" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/>
</svg>`;

  return new NextResponse(svg, {
    headers: {
      "Content-Type": "image/svg+xml",
      "Cache-Control": "public, max-age=3600",
    },
  });
}
