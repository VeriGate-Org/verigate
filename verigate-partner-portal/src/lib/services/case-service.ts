import { config } from "@/lib/config";
import type { Case } from "@/lib/bff-client";
import {
  listCases as listCasesBff,
  getCase as getCaseBff,
  updateCase as updateCaseBff,
  addCaseComment as addCaseCommentBff,
} from "@/lib/bff-client";

// ── Mock in-memory store ────────────────────────────────────────────

const SEED_CASES: Case[] = [
  {
    caseId: "case-001",
    verificationId: "ver-101",
    partnerId: "partner-portal",
    status: "OPEN",
    priority: "CRITICAL",
    compositeRiskScore: 23,
    riskTier: "HIGH_RISK",
    subjectName: "John Mokoena",
    subjectId: "sub-001",
    comments: [],
    timeline: [
      { event: "CASE_CREATED", timestamp: "2026-03-12T10:30:00Z", actor: "system" },
    ],
    createdAt: "2026-03-12T10:30:00Z",
    updatedAt: "2026-03-12T10:30:00Z",
  },
  {
    caseId: "case-002",
    verificationId: "ver-102",
    partnerId: "partner-portal",
    status: "IN_REVIEW",
    assignee: "admin@verigate.co.za",
    priority: "HIGH",
    compositeRiskScore: 42,
    riskTier: "HIGH_RISK",
    subjectName: "Thandi Nkosi",
    subjectId: "sub-002",
    comments: [
      { id: "cmt-001", author: "admin@verigate.co.za", text: "Reviewing sanctions match — looks like a false positive.", createdAt: "2026-03-11T15:00:00Z" },
    ],
    timeline: [
      { event: "CASE_CREATED", timestamp: "2026-03-10T09:00:00Z", actor: "system" },
      { event: "STATUS_CHANGED", timestamp: "2026-03-11T14:00:00Z", actor: "admin@verigate.co.za" },
    ],
    createdAt: "2026-03-10T09:00:00Z",
    updatedAt: "2026-03-11T15:00:00Z",
  },
  {
    caseId: "case-003",
    verificationId: "ver-103",
    partnerId: "partner-portal",
    status: "OPEN",
    priority: "MEDIUM",
    compositeRiskScore: 55,
    riskTier: "MEDIUM_RISK",
    subjectName: "Pieter van der Merwe",
    subjectId: "sub-003",
    comments: [],
    timeline: [
      { event: "CASE_CREATED", timestamp: "2026-03-11T16:45:00Z", actor: "system" },
    ],
    createdAt: "2026-03-11T16:45:00Z",
    updatedAt: "2026-03-11T16:45:00Z",
  },
  {
    caseId: "case-004",
    verificationId: "ver-104",
    partnerId: "partner-portal",
    status: "IN_REVIEW",
    assignee: "ops@verigate.co.za",
    priority: "LOW",
    compositeRiskScore: 72,
    riskTier: "MEDIUM_RISK",
    subjectName: "Ayanda Dlamini",
    subjectId: "sub-004",
    comments: [],
    timeline: [
      { event: "CASE_CREATED", timestamp: "2026-03-09T08:20:00Z", actor: "system" },
      { event: "STATUS_CHANGED", timestamp: "2026-03-10T11:00:00Z", actor: "ops@verigate.co.za" },
    ],
    createdAt: "2026-03-09T08:20:00Z",
    updatedAt: "2026-03-10T11:00:00Z",
  },
  {
    caseId: "case-005",
    verificationId: "ver-105",
    partnerId: "partner-portal",
    status: "RESOLVED",
    assignee: "admin@verigate.co.za",
    priority: "HIGH",
    decision: "APPROVE",
    decisionReason: "False positive on sanctions match confirmed after manual review.",
    compositeRiskScore: 85,
    riskTier: "LOW_RISK",
    subjectName: "Sipho Mthembu",
    subjectId: "sub-005",
    comments: [
      { id: "cmt-002", author: "admin@verigate.co.za", text: "Confirmed false positive — different DOB.", createdAt: "2026-03-08T12:00:00Z" },
    ],
    timeline: [
      { event: "CASE_CREATED", timestamp: "2026-03-07T10:00:00Z", actor: "system" },
      { event: "STATUS_CHANGED", timestamp: "2026-03-08T09:00:00Z", actor: "admin@verigate.co.za" },
      { event: "RESOLVED", timestamp: "2026-03-08T12:30:00Z", actor: "admin@verigate.co.za" },
    ],
    createdAt: "2026-03-07T10:00:00Z",
    updatedAt: "2026-03-08T12:30:00Z",
    resolvedAt: "2026-03-08T12:30:00Z",
  },
];

const mockStore: Case[] = [...SEED_CASES];

function delay(ms = 300): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

// ── Mock implementations ────────────────────────────────────────────

async function mockListCases(params?: { status?: string; pageSize?: number }): Promise<Case[]> {
  await delay();
  let result = [...mockStore];
  if (params?.status) {
    result = result.filter((c) => c.status === params.status);
  }
  result.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  if (params?.pageSize) {
    result = result.slice(0, params.pageSize);
  }
  return result;
}

async function mockGetCase(caseId: string): Promise<Case> {
  await delay();
  const found = mockStore.find((c) => c.caseId === caseId);
  if (!found) throw new Error(`Case not found: ${caseId}`);
  return { ...found };
}

async function mockUpdateCase(
  caseId: string,
  updates: Partial<Pick<Case, "status" | "assignee" | "priority" | "decision" | "decisionReason">>,
): Promise<Case> {
  await delay();
  const index = mockStore.findIndex((c) => c.caseId === caseId);
  if (index === -1) throw new Error(`Case not found: ${caseId}`);
  const updated: Case = {
    ...mockStore[index],
    ...updates,
    updatedAt: new Date().toISOString(),
  };
  mockStore[index] = updated;
  return { ...updated };
}

async function mockAddCaseComment(
  caseId: string,
  comment: { author: string; text: string },
): Promise<Case> {
  await delay();
  const index = mockStore.findIndex((c) => c.caseId === caseId);
  if (index === -1) throw new Error(`Case not found: ${caseId}`);
  const updated: Case = {
    ...mockStore[index],
    comments: [
      ...mockStore[index].comments,
      { id: `cmt-${Date.now()}`, ...comment, createdAt: new Date().toISOString() },
    ],
    updatedAt: new Date().toISOString(),
  };
  mockStore[index] = updated;
  return { ...updated };
}

// ── Public dispatch layer ───────────────────────────────────────────

export async function listCases(params?: { status?: string; pageSize?: number }): Promise<Case[]> {
  if (config.useMockServices) return mockListCases(params);
  return listCasesBff(params);
}

export async function getCase(caseId: string): Promise<Case> {
  if (config.useMockServices) return mockGetCase(caseId);
  return getCaseBff(caseId);
}

export async function updateCase(
  caseId: string,
  updates: Partial<Pick<Case, "status" | "assignee" | "priority" | "decision" | "decisionReason">>,
): Promise<Case> {
  if (config.useMockServices) return mockUpdateCase(caseId, updates);
  return updateCaseBff(caseId, updates);
}

export async function addCaseComment(
  caseId: string,
  comment: { author: string; text: string },
): Promise<Case> {
  if (config.useMockServices) return mockAddCaseComment(caseId, comment);
  return addCaseCommentBff(caseId, comment);
}
