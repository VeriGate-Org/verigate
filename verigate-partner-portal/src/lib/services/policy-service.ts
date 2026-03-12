import { config } from "@/lib/config";
import type { Policy, RiskConfig } from "@/lib/bff-client";
import {
  listPolicies as listPoliciesBff,
  getPolicy as getPolicyBff,
  createPolicy as createPolicyBff,
  updatePolicy as updatePolicyBff,
  publishPolicy as publishPolicyBff,
  deletePolicy as deletePolicyBff,
} from "@/lib/bff-client";

// ── Mock in-memory store ────────────────────────────────────────────

const DEFAULT_RISK_CONFIG: RiskConfig = {
  strategy: "WEIGHTED_AVERAGE",
  weights: {
    ID_CHECK: 30,
    SANCTIONS_SCREENING: 25,
    CREDIT_CHECK: 20,
    EMPLOYMENT_VERIFICATION: 15,
    ADDRESS_VERIFICATION: 10,
  },
  tiers: [
    { name: "LOW_RISK", lowerBound: 80, upperBound: 100, decision: "APPROVE" },
    { name: "MEDIUM_RISK", lowerBound: 50, upperBound: 79, decision: "MANUAL_REVIEW" },
    { name: "HIGH_RISK", lowerBound: 0, upperBound: 49, decision: "REJECT" },
  ],
  overrideRules: [
    {
      id: "or-001",
      name: "Sanctions hit auto-reject",
      condition: {
        checkType: "SANCTIONS_SCREENING",
        signalKey: "sanctionsHitCount",
        operator: "GREATER_THAN",
        value: "0",
      },
      forcedDecision: "REJECT",
      priority: 1,
    },
  ],
};

const SEED_POLICIES: Policy[] = [
  {
    policyId: "pol-001",
    partnerId: "partner-portal",
    name: "Standard KYC",
    description:
      "Default Know-Your-Customer workflow for individual onboarding. Runs ID check, sanctions screening, and credit check in sequence.",
    status: "PUBLISHED",
    version: 3,
    steps: [
      { type: "VERIFICATION", name: "ID Check", config: { verificationType: "VERIFICATION_OF_PERSONAL_DETAILS" }, next: "step-2" },
      { type: "VERIFICATION", name: "Sanctions Screening", config: { verificationType: "SANCTIONS_SCREENING" }, next: "step-3" },
      { type: "VERIFICATION", name: "Credit Check", config: { verificationType: "CREDIT_CHECK" } },
    ],
    scoringConfig: DEFAULT_RISK_CONFIG,
    createdAt: "2025-11-15T08:30:00Z",
    updatedAt: "2026-01-20T14:12:00Z",
    publishedAt: "2026-01-20T14:15:00Z",
  },
  {
    policyId: "pol-002",
    partnerId: "partner-portal",
    name: "Enhanced Due Diligence",
    description:
      "High-risk onboarding workflow with additional screening steps: negative news, fraud watchlist, and employment verification.",
    status: "DRAFT",
    version: 1,
    steps: [
      { type: "VERIFICATION", name: "ID Check", config: { verificationType: "VERIFICATION_OF_PERSONAL_DETAILS" }, next: "step-2" },
      { type: "VERIFICATION", name: "Sanctions Screening", config: { verificationType: "SANCTIONS_SCREENING" }, next: "step-3" },
      { type: "VERIFICATION", name: "Negative News", config: { verificationType: "NEGATIVE_NEWS_SCREENING" }, next: "step-4" },
      { type: "VERIFICATION", name: "Fraud Watchlist", config: { verificationType: "FRAUD_WATCHLIST_SCREENING" }, next: "step-5" },
      { type: "VERIFICATION", name: "Employment Check", config: { verificationType: "EMPLOYMENT_VERIFICATION" } },
    ],
    scoringConfig: {
      ...DEFAULT_RISK_CONFIG,
      weights: {
        ID_CHECK: 20,
        SANCTIONS_SCREENING: 25,
        NEGATIVE_NEWS_SCREENING: 20,
        FRAUD_WATCHLIST_SCREENING: 20,
        EMPLOYMENT_VERIFICATION: 15,
      },
    },
    createdAt: "2026-02-10T11:00:00Z",
    updatedAt: "2026-03-05T09:45:00Z",
  },
  {
    policyId: "pol-003",
    partnerId: "partner-portal",
    name: "Business Verification",
    description:
      "Corporate onboarding workflow covering CIPC registration, tax compliance, and director screening.",
    status: "PUBLISHED",
    version: 2,
    steps: [
      { type: "VERIFICATION", name: "Company Check", config: { verificationType: "COMPANY_VERIFICATION" }, next: "step-2" },
      { type: "VERIFICATION", name: "Tax Compliance", config: { verificationType: "TAX_COMPLIANCE_VERIFICATION" }, next: "step-3" },
      { type: "VERIFICATION", name: "Director Sanctions", config: { verificationType: "SANCTIONS_SCREENING" } },
    ],
    scoringConfig: {
      ...DEFAULT_RISK_CONFIG,
      weights: {
        COMPANY_VERIFICATION: 40,
        TAX_COMPLIANCE_VERIFICATION: 35,
        SANCTIONS_SCREENING: 25,
      },
    },
    createdAt: "2025-12-01T10:00:00Z",
    updatedAt: "2026-02-28T16:30:00Z",
    publishedAt: "2026-02-28T16:35:00Z",
  },
  {
    policyId: "pol-004",
    partnerId: "partner-portal",
    name: "Property Transaction",
    description:
      "Verification workflow for property-related transactions including deeds registry and income verification.",
    status: "DRAFT",
    version: 1,
    steps: [
      { type: "VERIFICATION", name: "ID Check", config: { verificationType: "VERIFICATION_OF_PERSONAL_DETAILS" }, next: "step-2" },
      { type: "VERIFICATION", name: "Property Ownership", config: { verificationType: "PROPERTY_OWNERSHIP_VERIFICATION" }, next: "step-3" },
      { type: "VERIFICATION", name: "Income Verification", config: { verificationType: "INCOME_VERIFICATION" } },
    ],
    scoringConfig: DEFAULT_RISK_CONFIG,
    createdAt: "2026-03-01T09:00:00Z",
    updatedAt: "2026-03-08T13:20:00Z",
  },
];

let mockStore: Policy[] = [...SEED_POLICIES];

function delay(ms = 300): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

// ── Mock implementations ────────────────────────────────────────────

async function mockListPolicies(): Promise<Policy[]> {
  await delay();
  return [...mockStore];
}

async function mockGetPolicy(policyId: string): Promise<Policy> {
  await delay();
  const policy = mockStore.find((p) => p.policyId === policyId);
  if (!policy) throw new Error(`Policy not found: ${policyId}`);
  return { ...policy };
}

async function mockCreatePolicy(policy: Policy): Promise<Policy> {
  await delay();
  const created: Policy = {
    ...policy,
    policyId: `pol-${String(mockStore.length + 1).padStart(3, "0")}`,
    partnerId: config.partnerId,
    status: "DRAFT",
    version: 1,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };
  mockStore.push(created);
  return { ...created };
}

async function mockUpdatePolicy(policyId: string, policy: Policy): Promise<Policy> {
  await delay();
  const index = mockStore.findIndex((p) => p.policyId === policyId);
  if (index === -1) throw new Error(`Policy not found: ${policyId}`);
  if (mockStore[index].status === "PUBLISHED") {
    throw new Error("Cannot update a published policy.");
  }
  const updated: Policy = {
    ...mockStore[index],
    ...policy,
    policyId,
    updatedAt: new Date().toISOString(),
  };
  mockStore[index] = updated;
  return { ...updated };
}

async function mockPublishPolicy(policyId: string): Promise<Policy> {
  await delay();
  const index = mockStore.findIndex((p) => p.policyId === policyId);
  if (index === -1) throw new Error(`Policy not found: ${policyId}`);
  const published: Policy = {
    ...mockStore[index],
    status: "PUBLISHED",
    version: (mockStore[index].version ?? 1) + 1,
    publishedAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };
  mockStore[index] = published;
  return { ...published };
}

async function mockDeletePolicy(policyId: string): Promise<void> {
  await delay();
  const index = mockStore.findIndex((p) => p.policyId === policyId);
  if (index === -1) throw new Error(`Policy not found: ${policyId}`);
  mockStore = mockStore.filter((p) => p.policyId !== policyId);
}

// ── Public dispatch layer ───────────────────────────────────────────

export async function listPolicies(): Promise<Policy[]> {
  if (config.useMockServices) return mockListPolicies();
  return listPoliciesBff();
}

export async function getPolicy(policyId: string): Promise<Policy> {
  if (config.useMockServices) return mockGetPolicy(policyId);
  return getPolicyBff(policyId);
}

export async function createPolicy(policy: Policy): Promise<Policy> {
  if (config.useMockServices) return mockCreatePolicy(policy);
  return createPolicyBff(policy);
}

export async function updatePolicy(policyId: string, policy: Policy): Promise<Policy> {
  if (config.useMockServices) return mockUpdatePolicy(policyId, policy);
  return updatePolicyBff(policyId, policy);
}

export async function publishPolicy(policyId: string): Promise<Policy> {
  if (config.useMockServices) return mockPublishPolicy(policyId);
  return publishPolicyBff(policyId);
}

export async function deletePolicy(policyId: string): Promise<void> {
  if (config.useMockServices) return mockDeletePolicy(policyId);
  return deletePolicyBff(policyId);
}
