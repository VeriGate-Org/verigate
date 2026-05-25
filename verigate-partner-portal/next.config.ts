import type { NextConfig } from "next";

const isStaticExport = process.env.STATIC_EXPORT === "1";

const nextConfig: NextConfig = {
  ...(isStaticExport && { output: "export" }),
  trailingSlash: true,
  images: {
    unoptimized: true,
  },
  ...(!isStaticExport && {
    async rewrites() {
      const bffBase = process.env.BFF_BASE_URL || "http://localhost:8080";
      return [
        {
          source: "/api/bff/:path*",
          destination: `${bffBase}/:path*`,
        },
      ];
    },
  }),
};

export default nextConfig;
