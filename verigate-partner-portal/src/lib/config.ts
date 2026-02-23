export const config = {
  bffBaseUrl: process.env.NEXT_PUBLIC_BFF_BASE_URL || "http://localhost:8080",
  bffApiKey: process.env.BFF_API_KEY || "",
  useMockServices: process.env.NEXT_PUBLIC_USE_MOCK !== "false",
  partnerId: process.env.NEXT_PUBLIC_PARTNER_ID || "partner-portal",
};
