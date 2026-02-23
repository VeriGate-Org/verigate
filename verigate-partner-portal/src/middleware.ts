import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

const AUTH_DISABLED = process.env.NEXT_PUBLIC_AUTH_DISABLED !== "false";

/**
 * Applies standard security headers to every matched response.
 */
function applySecurityHeaders(response: NextResponse) {
  response.headers.set("X-Frame-Options", "DENY");
  response.headers.set("X-Content-Type-Options", "nosniff");
  response.headers.set("Referrer-Policy", "strict-origin-when-cross-origin");
  response.headers.set(
    "Permissions-Policy",
    "camera=(), microphone=(), geolocation=(), payment=()"
  );
  response.headers.set(
    "Content-Security-Policy",
    "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: blob:; font-src 'self' data:; connect-src 'self' http://localhost:* https://*;"
  );

  // HSTS only in production
  if (process.env.NODE_ENV === "production") {
    response.headers.set(
      "Strict-Transport-Security",
      "max-age=63072000; includeSubDomains; preload"
    );
  }

  return response;
}

/**
 * Middleware for authentication enforcement and security headers.
 * When NEXT_PUBLIC_AUTH_DISABLED=true (default), authentication is skipped
 * but security headers are still applied.
 */
export default function middleware(request: NextRequest) {
  if (AUTH_DISABLED) {
    const response = NextResponse.next();
    response.headers.set("X-Auth-Status", "disabled");
    return applySecurityHeaders(response);
  }

  const sessionToken =
    request.cookies.get("next-auth.session-token")?.value ||
    request.cookies.get("__Secure-next-auth.session-token")?.value;

  if (!sessionToken) {
    const signInUrl = new URL("/api/auth/signin", request.url);
    signInUrl.searchParams.set("callbackUrl", request.url);
    return NextResponse.redirect(signInUrl);
  }

  const response = NextResponse.next();
  response.headers.set("X-Partner-Session", "active");
  return applySecurityHeaders(response);
}

export const config = {
  matcher: [
    "/dashboard/:path*",
    "/services/:path*",
    "/verifications/:path*",
    "/policies/:path*",
    "/settings/:path*",
    "/help/:path*",
    "/reports/:path*",
  ],
};
