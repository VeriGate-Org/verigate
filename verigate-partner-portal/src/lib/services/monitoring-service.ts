import { config } from "@/lib/config";
import type { MonitoringAlert } from "@/lib/bff-client";
import {
  listMonitoringAlerts as listMonitoringAlertsBff,
  acknowledgeAlert as acknowledgeAlertBff,
} from "@/lib/bff-client";

// ── Mock in-memory store ────────────────────────────────────────────

const SEED_ALERTS: MonitoringAlert[] = [
  {
    alertId: "alert-001",
    subjectId: "sub-001",
    partnerId: "partner-portal",
    severity: "HIGH",
    alertType: "SANCTIONS_HIT",
    title: "New sanctions match detected",
    description: "Subject John Mokoena matched against updated OFAC SDN list entry.",
    previousRiskScore: 75,
    currentRiskScore: 23,
    previousDecision: "APPROVE",
    currentDecision: "REJECT",
    acknowledged: false,
    createdAt: "2026-03-12T10:25:00Z",
  },
  {
    alertId: "alert-002",
    subjectId: "sub-003",
    partnerId: "partner-portal",
    severity: "MEDIUM",
    alertType: "RISK_SCORE_CHANGE",
    title: "Risk score degradation",
    description: "Pieter van der Merwe risk score dropped from 82 to 55 after employment verification update.",
    previousRiskScore: 82,
    currentRiskScore: 55,
    previousDecision: "APPROVE",
    currentDecision: "MANUAL_REVIEW",
    acknowledged: false,
    createdAt: "2026-03-11T17:00:00Z",
  },
  {
    alertId: "alert-003",
    subjectId: "sub-006",
    partnerId: "partner-portal",
    severity: "HIGH",
    alertType: "PEP_STATUS_CHANGE",
    title: "PEP status detected",
    description: "Ongoing monitoring detected that Naledi Khumalo has been listed as a Politically Exposed Person.",
    previousRiskScore: 90,
    currentRiskScore: 35,
    previousDecision: "APPROVE",
    currentDecision: "REJECT",
    acknowledged: false,
    createdAt: "2026-03-12T08:15:00Z",
  },
  {
    alertId: "alert-004",
    subjectId: "sub-007",
    partnerId: "partner-portal",
    severity: "LOW",
    alertType: "ADDRESS_CHANGE",
    title: "Address discrepancy detected",
    description: "Registered address for Bongani Zulu no longer matches credit bureau records.",
    previousRiskScore: 88,
    currentRiskScore: 78,
    previousDecision: "APPROVE",
    currentDecision: "APPROVE",
    acknowledged: true,
    acknowledgedBy: "ops@verigate.co.za",
    acknowledgedAt: "2026-03-10T14:30:00Z",
    createdAt: "2026-03-10T09:00:00Z",
  },
  {
    alertId: "alert-005",
    subjectId: "sub-002",
    partnerId: "partner-portal",
    severity: "MEDIUM",
    alertType: "NEGATIVE_NEWS",
    title: "Negative news article found",
    description: "Monitoring detected a news article mentioning Thandi Nkosi in connection with a regulatory inquiry.",
    previousRiskScore: 70,
    currentRiskScore: 42,
    previousDecision: "MANUAL_REVIEW",
    currentDecision: "MANUAL_REVIEW",
    acknowledged: false,
    createdAt: "2026-03-11T12:30:00Z",
  },
];

const mockStore: MonitoringAlert[] = [...SEED_ALERTS];

function delay(ms = 300): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

// ── Mock implementations ────────────────────────────────────────────

async function mockListMonitoringAlerts(params?: {
  subjectId?: string;
  severity?: string;
  pageSize?: number;
}): Promise<MonitoringAlert[]> {
  await delay();
  let result = [...mockStore];
  if (params?.subjectId) {
    result = result.filter((a) => a.subjectId === params.subjectId);
  }
  if (params?.severity) {
    result = result.filter((a) => a.severity === params.severity);
  }
  result.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  if (params?.pageSize) {
    result = result.slice(0, params.pageSize);
  }
  return result;
}

async function mockAcknowledgeAlert(alertId: string): Promise<MonitoringAlert> {
  await delay();
  const index = mockStore.findIndex((a) => a.alertId === alertId);
  if (index === -1) throw new Error(`Alert not found: ${alertId}`);
  const updated: MonitoringAlert = {
    ...mockStore[index],
    acknowledged: true,
    acknowledgedBy: "current-user",
    acknowledgedAt: new Date().toISOString(),
  };
  mockStore[index] = updated;
  return { ...updated };
}

// ── Public dispatch layer ───────────────────────────────────────────

export async function listMonitoringAlerts(params?: {
  subjectId?: string;
  severity?: string;
  pageSize?: number;
}): Promise<MonitoringAlert[]> {
  if (config.useMockServices) return mockListMonitoringAlerts(params);
  return listMonitoringAlertsBff(params);
}

export async function acknowledgeAlert(alertId: string): Promise<MonitoringAlert> {
  if (config.useMockServices) return mockAcknowledgeAlert(alertId);
  return acknowledgeAlertBff(alertId);
}
