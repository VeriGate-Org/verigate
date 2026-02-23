import { config } from "@/lib/config";
import { submitVerification, pollVerificationStatus } from "@/lib/bff-client";
import type { BffVerificationType } from "@/lib/types";
import {
  mockPersonalDetails,
  mockAvs,
  mockSanctions,
  mockCompany,
  mockPropertyOwnership,
  mockEmployment,
  mockNegativeNews,
  mockFraudWatchlist,
  mockDocumentVerification,
  mockQualification,
  mockCreditCheck,
  mockTaxCompliance,
  mockIncome,
  mockIdentity,
  mockFullVerification,
  type PersonalDetailsRequest,
  type AvsRequest,
  type SanctionsRequest,
  type CompanyRequest,
  type PropertyOwnershipRequest,
} from "@/lib/mock-services";

/**
 * Unified verification dispatch layer.
 * Switches between mock and live mode based on config.useMockServices.
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function executeVerification(bffType: BffVerificationType, params: Record<string, any>): Promise<any> {
  if (config.useMockServices) {
    return executeMockVerification(bffType, params);
  }
  return executeLiveVerification(bffType, params);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
async function executeLiveVerification(bffType: BffVerificationType, params: Record<string, any>) {
  const submission = {
    verificationType: bffType,
    originationType: "ADHOC" as const,
    originationId: crypto.randomUUID(),
    requestedBy: config.partnerId,
    metadata: params,
  };

  const { commandId } = await submitVerification(submission);
  const result = await pollVerificationStatus(commandId);
  return result;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
async function executeMockVerification(bffType: BffVerificationType, params: Record<string, any>) {
  switch (bffType) {
    case "VERIFICATION_OF_PERSONAL_DETAILS":
      return mockPersonalDetails(params as PersonalDetailsRequest);
    case "BANK_ACCOUNT_VERIFICATION":
      return mockAvs(params as AvsRequest);
    case "SANCTIONS_SCREENING":
    case "WATCHLIST_SCREENING":
      return mockSanctions(params as SanctionsRequest);
    case "COMPANY_VERIFICATION":
      return mockCompany(params as CompanyRequest);
    case "PROPERTY_OWNERSHIP_VERIFICATION":
      return mockPropertyOwnership(params as PropertyOwnershipRequest);
    case "EMPLOYMENT_VERIFICATION":
      return mockEmployment(params);
    case "NEGATIVE_NEWS_SCREENING":
      return mockNegativeNews(params);
    case "FRAUD_WATCHLIST_SCREENING":
      return mockFraudWatchlist(params);
    case "DOCUMENT_VERIFICATION":
      return mockDocumentVerification(params);
    case "QUALIFICATION_VERIFICATION":
      return mockQualification(params);
    case "CREDIT_CHECK":
      return mockCreditCheck(params);
    case "TAX_COMPLIANCE_VERIFICATION":
      return mockTaxCompliance(params);
    case "INCOME_VERIFICATION":
      return mockIncome(params);
    case "IDENTITY_VERIFICATION":
      return mockIdentity(params);
    case "FULL_VERIFICATION":
      return mockFullVerification(params);
    default:
      throw new Error(`Unsupported verification type: ${bffType}`);
  }
}
