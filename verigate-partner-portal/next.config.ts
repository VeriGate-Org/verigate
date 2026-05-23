import type { NextConfig } from "next";

// Static export disabled: dynamic routes ([correlationId], [caseId], etc.)
// are incompatible with output:"export". Enable STATIC_EXPORT=1 only when
// deploying behind an SPA-fallback-aware host (S3+CloudFront, Vercel, etc.).
const isStaticExport = process.env.STATIC_EXPORT === "1";

const nextConfig: NextConfig = {
  ...(isStaticExport && { output: "export" }),
  trailingSlash: true,
  images: {
    unoptimized: true,
  },
  async rewrites() {
    const bffBase = process.env.BFF_BASE_URL || "http://localhost:8080";
    return [
      {
        source: "/api/bff/:path*",
        destination: `${bffBase}/:path*`,
      },
    ];
  },
};

export default nextConfig;
