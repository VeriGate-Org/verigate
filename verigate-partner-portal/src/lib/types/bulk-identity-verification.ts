/**
 * Types for HANIS bulk identity verification.
 */

export type BulkJobStatus =
  | "CREATED"
  | "UPLOADED"
  | "PROCESSING"
  | "COMPLETED"
  | "DOWNLOADED"
  | "EXPIRED"
  | "FAILED";

export interface BillingGroupSelection {
  demographics: boolean;
  identity: boolean;
  maritalStatus: boolean;
  deathInfo: boolean;
  smartCard: boolean;
  photo: boolean;
  address: boolean;
  fingerprint: boolean;
  reserved1: boolean;
  reserved2: boolean;
}

export interface BulkVerificationJob {
  jobId: string;
  partnerId: string;
  status: BulkJobStatus;
  requestId: string | null;
  billingGroups: string;
  idCount: number;
  createdAt: string;
  uploadedAt: string | null;
  completedAt: string | null;
  errorCode: number;
  errorDescription: string | null;
}

export interface BulkVerificationResult {
  idNumber: string;
  names: string;
  surname: string;
  gender: string;
  dateOfBirth: string;
  birthCountry: string;
  citizenStatus: string;
  nationality: string;
  smartCardIssued: boolean;
  idCardIssueDate: string;
  idBookIssued: boolean;
  idBookIssueDate: string;
  idBlocked: boolean;
  maritalStatus: string;
  maidenName: string;
  marriageDate: string;
  divorceDate: string;
  dateOfDeath: string;
  deathPlace: string;
  causeOfDeath: string;
  errorCode: number;
  success: boolean;
}

export interface BulkResultsSummary {
  total: number;
  verified: number;
  notFound: number;
  deceased: number;
  errors: number;
}

export const DEFAULT_BILLING_GROUPS: BillingGroupSelection = {
  demographics: true,
  identity: true,
  maritalStatus: true,
  deathInfo: true,
  smartCard: true,
  photo: false,
  address: false,
  fingerprint: false,
  reserved1: false,
  reserved2: false,
};

export const BILLING_GROUP_LABELS: Record<keyof BillingGroupSelection, string> = {
  demographics: "Demographics",
  identity: "Identity",
  maritalStatus: "Marital Status",
  deathInfo: "Death Information",
  smartCard: "Smart Card",
  photo: "Photo",
  address: "Address",
  fingerprint: "Fingerprint",
  reserved1: "Reserved 1",
  reserved2: "Reserved 2",
};
