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
