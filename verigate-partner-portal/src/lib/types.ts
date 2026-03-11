export type VerificationStatus =
  | "in_progress" | "success" | "soft_fail" | "hard_fail"
  | "pending" | "completed" | "transient_error" | "permanent_failure";

export type VerificationType =
  | "ID" | "AVS" | "SANCTIONS" | "CIPC" | "DEEDS"
  | "EMPLOYMENT" | "NEGATIVE_NEWS" | "FRAUD_WATCHLIST"
  | "DOCUMENT" | "QUALIFICATION" | "CREDIT"
  | "TAX" | "INCOME" | "IDENTITY" | "FULL_VERIFICATION" | "WATCHLIST";

/** BFF wire-format verification type enum values. */
export type BffVerificationType =
  | "VERIFICATION_OF_PERSONAL_DETAILS" | "BANK_ACCOUNT_VERIFICATION"
  | "SANCTIONS_SCREENING" | "COMPANY_VERIFICATION"
  | "PROPERTY_OWNERSHIP_VERIFICATION" | "EMPLOYMENT_VERIFICATION"
  | "NEGATIVE_NEWS_SCREENING" | "FRAUD_WATCHLIST_SCREENING"
  | "DOCUMENT_VERIFICATION" | "QUALIFICATION_VERIFICATION"
  | "CREDIT_CHECK" | "TAX_COMPLIANCE_VERIFICATION"
  | "INCOME_VERIFICATION" | "IDENTITY_VERIFICATION"
  | "FULL_VERIFICATION" | "WATCHLIST_SCREENING";

export interface CheckScore {
  verificationType: string;
  outcome: string;
  confidenceScore: number;
  signals: Record<string, string>;
}

export interface Verification {
  correlationId: string;
  partnerId: string;
  type: VerificationType;
  status: VerificationStatus;
  provider?: string;
  // Link to the workflow/policy used for this verification
  workflowId?: string;
  workflowName?: string;
  policyVersion?: string;
  // Risk assessment data
  compositeRiskScore?: number;
  riskTier?: string;
  riskDecision?: string;
  decisionReason?: string;
  individualScores?: CheckScore[];
  startedAt: string;
  completedAt?: string;
  durationMs?: number;
}

export interface VerificationEvent {
  ts: string;
  eventType:
    | "VerificationRequested"
    | "VerificationSucceeded"
    | "VerificationSoftFail"
    | "VerificationHardFail"
    | "VerificationSystemOutage"
    | "DomainSpecific";
  source: string;
  correlationId: string;
  detail?: unknown;
  stepSequence?: number;
}

export interface PartnerPolicySnapshot {
  version: string;
  sequence: Array<{ type: VerificationType; providerPreference?: string }>;
  branching?: Array<{ on: string; then: string }>;
  retryPolicies?: Record<string, { maxRetries: number; strategy: "fixed" | "exponential"; backoffMs: number }>;
}
