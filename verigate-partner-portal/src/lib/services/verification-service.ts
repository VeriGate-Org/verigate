import { config } from "@/lib/config";
import {
  submitVerification,
  pollVerificationStatus,
  type BffVerificationStatusResponse,
} from "@/lib/bff-client";
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
  type PropertyOwnershipResponse,
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
  return deserializeLiveVerificationResult(bffType, params, result);
}

function deserializeLiveVerificationResult(
  bffType: BffVerificationType,
  params: Record<string, unknown>,
  result: BffVerificationStatusResponse,
) {
  if (bffType === "PROPERTY_OWNERSHIP_VERIFICATION") {
    return deserializePropertyOwnershipResult(params, result);
  }

  return result;
}

function deserializePropertyOwnershipResult(
  params: Record<string, unknown>,
  result: BffVerificationStatusResponse,
): PropertyOwnershipResponse {
  const payloadJson = result.auxiliaryData?.searchResultJson;
  if (payloadJson) {
    return JSON.parse(payloadJson) as PropertyOwnershipResponse;
  }

  return {
    summary: {
      totalProperties: Number(result.auxiliaryData?.recordCount ?? 0),
      totalActiveBonds: 0,
      totalMunicipalFlags: 0,
    },
    items: [],
    criteria: {
      searchType: String(params.searchType ?? "ownerId"),
      query: String(params.query ?? ""),
      province: String(params.province ?? ""),
    },
  };
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
