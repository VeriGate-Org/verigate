const authDisabled = process.env.NEXT_PUBLIC_AUTH_DISABLED !== "false";
const useMockExplicit = process.env.NEXT_PUBLIC_USE_MOCK !== "false";

export const config = {
  bffBaseUrl: process.env.NEXT_PUBLIC_BFF_BASE_URL || "http://localhost:8080",
  useMockServices: useMockExplicit,
  partnerId: process.env.NEXT_PUBLIC_PARTNER_ID || "partner-portal",
  authDisabled,
};
