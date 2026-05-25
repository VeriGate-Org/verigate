import { z } from "zod";

export const bffVerificationResponseSchema = z.object({
  commandId: z.string().uuid(),
  status: z.string(),
});

export const bffVerificationStatusSchema = z.object({
  commandId: z.string().uuid(),
  status: z.string(),
  errorDetails: z.array(z.string()).optional(),
  auxiliaryData: z.record(z.string(), z.string()).optional(),
});

export const bffVerificationListItemSchema = z.object({
  commandId: z.string().uuid(),
  status: z.string(),
  createdAt: z.string(),
  commandName: z.string(),
});

export const bffVerificationListResponseSchema = z.object({
  items: z.array(bffVerificationListItemSchema),
  cursor: z.string().nullable(),
  hasMore: z.boolean(),
});

export type ValidatedBffVerificationResponse = z.infer<typeof bffVerificationResponseSchema>;
export type ValidatedBffVerificationStatus = z.infer<typeof bffVerificationStatusSchema>;
export type ValidatedBffVerificationListResponse = z.infer<typeof bffVerificationListResponseSchema>;

// ── Signup Schema ──────────────────────────────────────────────────

export const COMPANY_TYPES = [
  "Financial Institution",
  "Insurance Company",
  "Legal Practice",
  "Real Estate Agency",
  "Accounting Firm",
  "Recruitment Agency",
  "Government Agency",
  "Other",
] as const;

export const BILLING_PLANS = ["free", "standard", "professional", "enterprise"] as const;

export const VERIFICATION_TYPE_OPTIONS = [
  { value: "ID", label: "ID Verification" },
  { value: "AVS", label: "Address Verification" },
  { value: "SANCTIONS", label: "Sanctions Screening" },
  { value: "CIPC", label: "Company (CIPC)" },
  { value: "CREDIT", label: "Credit Check" },
  { value: "TAX", label: "Tax Compliance" },
  { value: "DOCUMENT", label: "Document Verification" },
  { value: "DEEDS", label: "Deeds Registry" },
] as const;

export const signupStep1Schema = z.object({
  firstName: z.string().min(1, "First name is required"),
  lastName: z.string().min(1, "Last name is required"),
  email: z.string().email("Please enter a valid email address"),
  companyName: z.string().min(1, "Company name is required"),
  companyType: z.string().min(1, "Please select a company type"),
});

export const signupStep2Schema = z.object({
  billingPlan: z.enum(BILLING_PLANS, { message: "Please select a plan" }),
  verificationTypes: z.array(z.string()).min(1, "Select at least one verification type"),
});

export const signupStep3Schema = z.object({
  termsAccepted: z.literal(true, { message: "You must accept the terms of service" }),
  privacyPolicyAccepted: z.literal(true, { message: "You must accept the privacy policy" }),
});

export const signupSchema = signupStep1Schema
  .merge(signupStep2Schema)
  .merge(signupStep3Schema);

export type SignupStep1Data = z.infer<typeof signupStep1Schema>;
export type SignupStep2Data = z.infer<typeof signupStep2Schema>;
export type SignupStep3Data = z.infer<typeof signupStep3Schema>;
export type SignupFormData = z.infer<typeof signupSchema>;
