/**
 * HANIS NPR identity verification types matching the BFF response DTOs.
 */

export interface HanisIdentityVerificationResponse {
  reference: string;
  provider: string;
  status: HanisVerificationStatus;
  outcome: HanisVerificationOutcome;
  citizenDetails: HanisCitizenDetails;
  documentInfo: HanisDocumentInfo;
  maritalInfo: HanisMaritalInfo;
  vitalStatus: HanisVitalStatusInfo;
  matchDetails: string;
  photoAvailable: boolean;
  photoBase64?: string;
  source: "hanis" | "dha" | "mock";
  transactionId: string;
  generatedAt: string;
}

export type HanisVerificationStatus =
  | "VERIFIED"
  | "NOT_FOUND"
  | "DECEASED"
  | "BLOCKED"
  | "MISMATCH"
  | "EXPIRED_ID"
  | "ERROR";

export type HanisVerificationOutcome =
  | "SUCCEEDED"
  | "HARD_FAIL"
  | "SOFT_FAIL"
  | "SYSTEM_OUTAGE";

export interface HanisCitizenDetails {
  name: string;
  surname: string;
  idNumber: string;
  citizenshipStatus: string;
  birthCountry: string;
  onHanis: boolean;
  onNpr: boolean;
}

export interface HanisDocumentInfo {
  smartCardIssued: boolean;
  idIssueDate: string;
  idSequenceNo: string;
  idnBlocked: boolean;
}

export interface HanisMaritalInfo {
  maritalStatus: string;
  dateOfMarriage: string;
}

export interface HanisVitalStatusInfo {
  status: string;
  deceased: boolean;
  dateOfDeath: string;
}
