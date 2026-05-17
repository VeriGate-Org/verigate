import type { NextConfig } from "next";

const isStaticExport = process.env.NODE_ENV === "production";

const nextConfig: NextConfig = {
  ...(isStaticExport && { output: "export" }),
  trailingSlash: true,
  images: {
    unoptimized: true,
  },
  async rewrites() {
    return [
      {
        source: "/api/bff/:path*",
        destination: "http://localhost:8080/:path*",
      },
    ];
  },
};

export default nextConfig;
