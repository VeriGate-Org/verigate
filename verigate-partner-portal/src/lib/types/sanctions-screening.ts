// Sanctions Screening TypeScript Types

export type EntityType = "Person" | "Company" | "Organization" | "Vessel";

export interface SanctionsScreeningRequest {
  entityType: EntityType;
  // Person fields
  firstName?: string;
  lastName?: string;
  dateOfBirth?: string;
  nationality?: string;
  gender?: string;
  idNumber?: string;
  // Company/Organisation fields
  name?: string;
  registrationNumber?: string;
  jurisdiction?: string;
  taxId?: string;
  // Vessel fields
  imoNumber?: string;
  mmsi?: string;
  flagState?: string;
  // Common
  address?: string;
  // Screening options
  dataset?: string;
  algorithm?: string;
  threshold?: number;
  topics?: string[];
}

export interface ScoredMatchEntity {
  id: string;
  caption: string;
  schema: string;
  score: number;
  datasets: string[];
  properties: Record<string, string[]>;
  features: Record<string, number>;
  isPep: boolean;
  isSanctioned: boolean;
  target?: boolean;
  firstSeen?: string;
  lastSeen?: string;
  lastChange?: string;
}

export interface SanctionsScreeningResponse {
  correlationId: string;
  outcome: "SUCCEEDED" | "HARD_FAIL" | "SOFT_FAIL" | "SYSTEM_OUTAGE";
  matches: ScoredMatchEntity[];
  totalMatches: number;
  algorithm: string;
  screenedAt: string;
  provider: string;
  dataset: string;
  threshold: number;
}

export interface EntityDetail extends ScoredMatchEntity {
  sanctions: SanctionDesignation[];
  positions: PepPosition[];
  aliases: string[];
  identifiers: EntityIdentifier[];
  referents: string[];
}

export interface SanctionDesignation {
  authority: string;
  program: string;
  startDate?: string;
  endDate?: string;
  reason?: string;
  status: string;
}

export interface PepPosition {
  role: string;
  organization: string;
  country?: string;
  startDate?: string;
  endDate?: string;
}

export interface EntityIdentifier {
  type: string;
  value: string;
  country?: string;
}

export interface AdjacentEntity {
  id: string;
  caption: string;
  schema: string;
  relationship: string;
  datasets: string[];
}

export type DispositionAction =
  | "CONFIRMED_MATCH"
  | "FALSE_POSITIVE"
  | "ESCALATED"
  | "PENDING_REVIEW";

export interface DispositionRequest {
  entityId: string;
  screeningId: string;
  action: DispositionAction;
  reason: string;
  reviewedBy?: string;
}

export interface ScreeningHistoryItem {
  screeningId: string;
  subjectName: string;
  entityType: EntityType;
  outcome: string;
  matchCount: number;
  screenedAt: string;
  disposition?: DispositionAction;
  provider: string;
}
