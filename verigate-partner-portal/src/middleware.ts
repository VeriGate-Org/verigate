import { NextRequest, NextResponse } from "next/server";

const ROOT_DOMAIN = process.env.NEXT_PUBLIC_ROOT_DOMAIN || "localhost:3000";

/** Hostnames that should never be treated as tenant subdomains. */
const SKIP_HOSTS = [".amplifyapp.com", ".cloudfront.net", ".vercel.app"];

/**
 * Extract the tenant slug from the request hostname.
 *
 * Examples:
 *   acme.verigate.co.za        → "acme"
 *   acme.localhost:3000         → "acme"
 *   verigate.co.za              → null
 *   localhost:3000              → null
 *   acme.dev.verigate.co.za     → "acme"
 */
function extractSlug(hostname: string): string | null {
  // Strip port for comparison
  const host = hostname.split(":")[0];
  const root = ROOT_DOMAIN.split(":")[0];

  // Skip preview / deployment domains
  if (SKIP_HOSTS.some((s) => host.endsWith(s))) return null;

  // localhost support: acme.localhost → "acme"
  if (root === "localhost" && host.endsWith(".localhost")) {
    const slug = host.replace(".localhost", "");
    return slug || null;
  }

  // Standard: acme.verigate.co.za → "acme"
  if (host.endsWith("." + root)) {
    const slug = host.slice(0, -(root.length + 1));
    // Ignore nested subdomains like "a.b" — we only want single slugs
    return slug && !slug.includes(".") ? slug : null;
  }

  return null;
}

export function middleware(request: NextRequest) {
  const hostname = request.headers.get("host") || "";
  const slug = extractSlug(hostname);

  const response = NextResponse.next();

  if (slug) {
    response.headers.set("x-tenant-slug", slug);
  }

  return response;
}

export const config = {
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico).*)"],
};
