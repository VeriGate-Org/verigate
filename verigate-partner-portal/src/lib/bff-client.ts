import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import type { BffVerificationType } from "./types";
import { config } from "./config";

export class BffApiError extends Error {
  constructor(
    message: string,
    public readonly statusCode?: number,
    public readonly correlationId?: string,
  ) {
    super(message);
    this.name = "BffApiError";
  }
}

const bffApi = axios.create({
  baseURL: config.bffBaseUrl,
  timeout: 30_000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor to attach auth headers and correlation ID
bffApi.interceptors.request.use((reqConfig: InternalAxiosRequestConfig) => {
  if (config.bffApiKey) {
    reqConfig.headers["X-API-Key"] = config.bffApiKey;
  }

  reqConfig.headers["X-Correlation-ID"] = crypto.randomUUID();

  // Attach access token from session storage if available
  try {
    const raw = sessionStorage.getItem("verigate-auth");
    if (raw) {
      const session = JSON.parse(raw);
      if (session.accessToken) {
        reqConfig.headers["Authorization"] = `Bearer ${session.accessToken}`;
      }
    }
  } catch {
    // sessionStorage may be unavailable during SSR/build
  }

  return reqConfig;
});

// Response interceptor for error handling
bffApi.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    const correlationId = error.config?.headers?.["X-Correlation-ID"] as string | undefined;

    if (error.code === "ECONNABORTED") {
      return Promise.reject(
        new BffApiError("Request timed out. Please try again.", undefined, correlationId),
      );
    }

    if (!error.response) {
      return Promise.reject(
        new BffApiError(
          "Unable to connect to the server. Please check your connection.",
          undefined,
          correlationId,
        ),
      );
    }

    const status = error.response.status;
    let message: string;

    if (status === 401) {
      message = "Authentication failed. Please sign in again.";
    } else if (status === 403) {
      message = "You do not have permission to perform this action.";
    } else if (status === 404) {
      message = "The requested resource was not found.";
    } else if (status === 429) {
      message = "Too many requests. Please wait a moment and try again.";
    } else if (status >= 500) {
      message = "A server error occurred. Please try again later.";
    } else {
      message = `Request failed with status ${status}.`;
    }

    return Promise.reject(new BffApiError(message, status, correlationId));
  },
);

export interface BffVerificationSubmission {
  verificationType: BffVerificationType;
  originationType: "ADHOC";
  originationId: string;
  requestedBy: string;
  metadata: Record<string, unknown>;
}

export interface BffVerificationResponse {
  commandId: string;
  status: string;
}

export interface BffVerificationStatusResponse {
  commandId: string;
  status: string;
  errorDetails?: string[];
  auxiliaryData?: Record<string, string>;
}

export function isTerminalVerificationStatus(status: string | undefined): boolean {
  return (
    status === "COMPLETED" ||
    status === "PERMANENT_FAILURE" ||
    status === "INVARIANT_FAILURE" ||
    status === "TRANSIENT_ERROR" ||
    status === "SUCCEEDED" ||
    status === "HARD_FAIL" ||
    status === "SOFT_FAIL" ||
    status === "SYSTEM_OUTAGE"
  );
}

export interface BffVerificationListResponse {
  items: Array<{
    commandId: string;
    status: string;
    createdAt: string;
    commandName: string;
  }>;
  cursor: string | null;
  hasMore: boolean;
}

export async function submitVerification(
  payload: BffVerificationSubmission,
): Promise<BffVerificationResponse> {
  const { data } = await bffApi.post<BffVerificationResponse>("/api/verifications", payload);
  return data;
}

export async function getVerificationStatus(
  commandId: string,
): Promise<BffVerificationStatusResponse> {
  const { data } = await bffApi.get<BffVerificationStatusResponse>(
    `/api/verifications/${commandId}`,
  );
  return data;
}

export async function listVerificationsBff(params?: {
  status?: string;
  cursor?: string;
  limit?: number;
}): Promise<BffVerificationListResponse> {
  const searchParams = new URLSearchParams();
  if (params?.status) searchParams.set("status", params.status);
  if (params?.cursor) searchParams.set("cursor", params.cursor);
  if (params?.limit) searchParams.set("limit", String(params.limit));

  const qs = searchParams.toString();
  const { data } = await bffApi.get<BffVerificationListResponse>(
    `/api/verifications${qs ? `?${qs}` : ""}`,
  );
  return data;
}

// ── Policy APIs ──────────────────────────────────────────────────────

export interface BffPolicyStep {
  type: string;
  name: string;
  config: Record<string, unknown>;
  next?: string;
  onSuccess?: string;
  onFail?: string;
  parallel?: string[];
}

// ── Report APIs ─────────────────────────────────────────────────────

export interface BffReportResponse {
  id: string;
  partnerId: string;
  name: string;
  type: string;
  description: string | null;
  status: string;
  filter: Record<string, unknown> | null;
  schedule: { frequency: string; time: string; recipients: string[] } | null;
  createdAt: string | null;
}

export interface BffReportListResponse {
  items: BffReportResponse[];
}

export async function generateReport(payload: {
  name: string;
  type: string;
  description?: string;
  filter?: Record<string, unknown>;
}): Promise<BffReportResponse> {
  const { data } = await bffApi.post<BffReportResponse>("/api/partner/reports/generate", payload);
  return data;
}

export async function listReports(): Promise<BffReportListResponse> {
  const { data } = await bffApi.get<BffReportListResponse>("/api/partner/reports");
  return data;
}

export async function scheduleReport(
  reportId: string,
  schedule: { frequency: string; time: string; recipients: string[] },
): Promise<BffReportResponse> {
  const { data } = await bffApi.post<BffReportResponse>(
    `/api/partner/reports/${reportId}/schedule`,
    schedule,
  );
  return data;
}

export async function deleteReport(reportId: string): Promise<void> {
  await bffApi.delete(`/api/partner/reports/${reportId}`);
}

// ── Deeds APIs ──────────────────────────────────────────────────────

export interface BffDeedsAreaReportRequest {
  province?: string;
  township?: string;
  transferDateFrom?: string;
  transferDateTo?: string;
  municipalFlagsOnly?: boolean;
}

export interface BffDeedsAreaBreakdownItem {
  province: string;
  township: string;
  properties: number;
  owners: number;
  activeBonds: number;
  municipalFlags: number;
  transfers: number;
}

export interface BffDeedsTransferItem {
  propertyId: string;
  titleDeed: string;
  ownerName: string;
  province: string;
  township: string;
  transferDate: string | null;
  transferAmount: number | null;
}

export interface BffDeedsSummary {
  totalProperties: number;
  totalOwners: number;
  totalActiveBonds: number;
  totalMunicipalFlags: number;
  totalTransfers: number;
}

export interface BffDeedsAreaReportResponse {
  summary: BffDeedsSummary;
  areas: BffDeedsAreaBreakdownItem[];
  recentTransfers: BffDeedsTransferItem[];
  generatedAt: string;
}

export interface BffDeedsBondSnapshot {
  bondholder: string;
  amount: number | null;
  registered: string | null;
}

export interface BffDeedsTransferSnapshot {
  date: string | null;
  amount: number | null;
}

export interface BffDeedsMunicipalSnapshot {
  accountNumber: string;
  arrears: number | null;
  ratesFlag: boolean;
}

export interface BffDeedsPropertySnapshot {
  propertyId: string;
  erfNumber: number;
  portion: number;
  township: string;
  province: string;
  titleDeed: string;
  deedNumber?: string;
  registrationDate: string | null;
  ownerName: string;
  ownerIdNumber: string;
  streetAddress?: string;
  currentBonds: BffDeedsBondSnapshot[];
  lastTransfer: BffDeedsTransferSnapshot | null;
  municipal: BffDeedsMunicipalSnapshot | null;
}

export interface BffDeedsPropertyTimelineItem {
  observedAt: string;
  ownerName: string;
  ownerIdNumber: string;
  titleDeed: string;
  transferDate: string | null;
  transferAmount: number | null;
  bondCount: number;
  municipalFlag: boolean;
}

export interface BffDeedsDocumentDescriptor {
  type: string;
  label: string;
  reference: string;
  downloadable: boolean;
  status: string;
  note: string;
}

export interface BffDeedsPropertyReportResponse {
  property: BffDeedsPropertySnapshot;
  timeline: BffDeedsPropertyTimelineItem[];
  summary: BffDeedsSummary;
  documents: BffDeedsDocumentDescriptor[];
  recommendedWatchAlerts: string[];
  generatedAt: string;
}

export interface BffDeedsDocumentManifestResponse {
  propertyId: string;
  titleDeed: string;
  documents: BffDeedsDocumentDescriptor[];
  providerStatus: string;
}

export interface BffDeedsWatchResponse {
  subjectId: string;
  propertyId: string;
  subjectName: string;
  titleDeed: string;
  monitoringFrequency: string;
  status: string;
  alertTypes: string[];
  createdAt: string;
}

export interface BffDeedsWatchAlertResponse {
  alertId: string;
  subjectId: string;
  propertyId: string;
  severity: string;
  alertType: string;
  title: string;
  description: string;
  createdAt: string;
}

export interface BffDeedsExportResponse {
  fileName: string;
  contentType: string;
  content: string;
}

export interface BffDeedsScheduleConfig {
  frequency: string;
  time: string;
  recipients: string[];
}

export interface BffDeedsSavedReportResponse {
  id: string;
  name: string;
  reportType: string;
  status: string;
  filter: BffDeedsAreaReportRequest | null;
  schedule: BffDeedsScheduleConfig | null;
  exportFormat: string;
  currentReport: boolean;
  autoRefresh: boolean;
  latestSummary: BffDeedsSummary | null;
  lastGeneratedAt: string | null;
  nextRunAt: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface BffDeedsExportHistoryResponse {
  id: string;
  reportId: string | null;
  reportName: string;
  format: string;
  status: string;
  recordCount: number;
  generatedAt: string | null;
  createdAt: string | null;
}

export interface BffDeedsTeamMemberResponse {
  id: string;
  name: string;
  email: string;
  role: string;
  permissions: string[];
  status: string;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface BffDeedsAuditEventResponse {
  id: string;
  category: string;
  action: string;
  actor: string;
  targetId: string | null;
  targetType: string | null;
  detail: string | null;
  createdAt: string | null;
}

export interface BffDeedsCoordinate {
  lat: number;
  lng: number;
}

export interface BffDeedsBoundaryResponse {
  boundaryId: string;
  province: string;
  municipality: string;
  label: string;
  polygon: BffDeedsCoordinate[];
}

export interface BffDeedsMapPropertyResponse {
  propertyId: string;
  label: string;
  province: string;
  township: string;
  streetAddress: string;
  titleDeed: string;
  centroid: BffDeedsCoordinate;
  outline: BffDeedsCoordinate[];
  ownerName: string;
  municipalFlag: boolean;
}

export interface BffDeedsMapSearchResponse {
  boundaries: BffDeedsBoundaryResponse[];
  properties: BffDeedsMapPropertyResponse[];
  generatedAt: string;
}

export interface BffDeedsConversionCandidate {
  propertyId: string;
  streetAddress: string;
  township: string;
  province: string;
  erfNumber: number;
  portion: number;
  titleDeed: string;
  confidence: number;
  reason: string;
}

export interface BffDeedsConversionResponse {
  direction: string;
  normalizedInput: string;
  candidates: BffDeedsConversionCandidate[];
  generatedAt: string;
}

export interface BffDeedsComparableSale {
  propertyId: string;
  titleDeed: string;
  township: string;
  transferDate: string | null;
  transferAmount: number | null;
  similarityScore: number | null;
}

export interface BffDeedsValuationResponse {
  propertyId: string;
  estimatedValue: number;
  lowerBound: number;
  upperBound: number;
  confidenceBand: string;
  methodology: string;
  disclaimer: string;
  comparableSales: BffDeedsComparableSale[];
  generatedAt: string;
}

export interface BffDeedsOperationsRefreshResponse {
  refreshedReports: number;
  recalculatedWatches: number;
  nextScheduledRefreshAt: string;
  generatedAt: string;
}

export async function generateDeedsAreaReport(
  payload: BffDeedsAreaReportRequest,
): Promise<BffDeedsAreaReportResponse> {
  const { data } = await bffApi.post<BffDeedsAreaReportResponse>("/api/partner/deeds/reports/area", payload);
  return data;
}

export async function exportDeedsAreaReport(
  payload: BffDeedsAreaReportRequest,
  format: "csv" | "json" = "csv",
): Promise<BffDeedsExportResponse> {
  const { data } = await bffApi.post<BffDeedsExportResponse>(
    `/api/partner/deeds/reports/area/export?format=${format}`,
    payload,
  );
  return data;
}

export async function listDeedsSavedReports(): Promise<BffDeedsSavedReportResponse[]> {
  const { data } = await bffApi.get<BffDeedsSavedReportResponse[]>("/api/partner/deeds/reports/saved");
  return data;
}

export async function createDeedsSavedReport(payload: {
  name: string;
  reportType: string;
  filter?: BffDeedsAreaReportRequest;
  schedule?: BffDeedsScheduleConfig;
  exportFormat?: string;
  currentReport?: boolean;
  autoRefresh?: boolean;
}): Promise<BffDeedsSavedReportResponse> {
  const { data } = await bffApi.post<BffDeedsSavedReportResponse>(
    "/api/partner/deeds/reports/saved",
    payload,
  );
  return data;
}

export async function updateDeedsSavedReport(
  reportId: string,
  payload: {
    name?: string;
    filter?: BffDeedsAreaReportRequest;
    schedule?: BffDeedsScheduleConfig;
    exportFormat?: string;
    status?: string;
    currentReport?: boolean;
    autoRefresh?: boolean;
  },
): Promise<BffDeedsSavedReportResponse> {
  const { data } = await bffApi.patch<BffDeedsSavedReportResponse>(
    `/api/partner/deeds/reports/saved/${reportId}`,
    payload,
  );
  return data;
}

export async function refreshDeedsSavedReport(reportId: string): Promise<BffDeedsSavedReportResponse> {
  const { data } = await bffApi.post<BffDeedsSavedReportResponse>(
    `/api/partner/deeds/reports/saved/${reportId}/refresh`,
  );
  return data;
}

export async function exportDeedsSavedReport(
  reportId: string,
  format: "csv" | "json" = "csv",
): Promise<BffDeedsExportResponse> {
  const { data } = await bffApi.post<BffDeedsExportResponse>(
    `/api/partner/deeds/reports/saved/${reportId}/export?format=${format}`,
  );
  return data;
}

export async function deleteDeedsSavedReport(reportId: string): Promise<void> {
  await bffApi.delete(`/api/partner/deeds/reports/saved/${reportId}`);
}

export async function listDeedsExportHistory(): Promise<BffDeedsExportHistoryResponse[]> {
  const { data } = await bffApi.get<BffDeedsExportHistoryResponse[]>("/api/partner/deeds/exports");
  return data;
}

export async function listDeedsTeamMembers(): Promise<BffDeedsTeamMemberResponse[]> {
  const { data } = await bffApi.get<BffDeedsTeamMemberResponse[]>("/api/partner/deeds/team");
  return data;
}

export async function createDeedsTeamMember(payload: {
  name: string;
  email: string;
  role: string;
  permissions?: string[];
  status?: string;
}): Promise<BffDeedsTeamMemberResponse> {
  const { data } = await bffApi.post<BffDeedsTeamMemberResponse>("/api/partner/deeds/team", payload);
  return data;
}

export async function updateDeedsTeamMember(
  memberId: string,
  payload: {
    name?: string;
    email?: string;
    role?: string;
    permissions?: string[];
    status?: string;
  },
): Promise<BffDeedsTeamMemberResponse> {
  const { data } = await bffApi.patch<BffDeedsTeamMemberResponse>(`/api/partner/deeds/team/${memberId}`, payload);
  return data;
}

export async function deleteDeedsTeamMember(memberId: string): Promise<void> {
  await bffApi.delete(`/api/partner/deeds/team/${memberId}`);
}

export async function listDeedsAuditEvents(): Promise<BffDeedsAuditEventResponse[]> {
  const { data } = await bffApi.get<BffDeedsAuditEventResponse[]>("/api/partner/deeds/audit");
  return data;
}

export async function searchDeedsMap(payload: {
  province?: string;
  township?: string;
  streetName?: string;
  query?: string;
}): Promise<BffDeedsMapSearchResponse> {
  const { data } = await bffApi.post<BffDeedsMapSearchResponse>("/api/partner/deeds/map/search", payload);
  return data;
}

export async function convertDeedsProperty(payload: {
  province?: string;
  township?: string;
  streetName?: string;
  erfNumber?: string;
  portion?: string;
  direction: string;
}): Promise<BffDeedsConversionResponse> {
  const { data } = await bffApi.post<BffDeedsConversionResponse>("/api/partner/deeds/conversion", payload);
  return data;
}

export async function estimateDeedsValue(payload: {
  propertyId?: string;
  province?: string;
  township?: string;
  titleDeed?: string;
  erfNumber?: number;
  portion?: number;
}): Promise<BffDeedsValuationResponse> {
  const { data } = await bffApi.post<BffDeedsValuationResponse>("/api/partner/deeds/valuation", payload);
  return data;
}

export async function runDeedsRefreshCycle(): Promise<BffDeedsOperationsRefreshResponse> {
  const { data } = await bffApi.post<BffDeedsOperationsRefreshResponse>("/api/partner/deeds/operations/refresh");
  return data;
}

export async function getDeedsPropertyReport(
  propertyId: string,
): Promise<BffDeedsPropertyReportResponse> {
  const { data } = await bffApi.get<BffDeedsPropertyReportResponse>(
    `/api/partner/deeds/reports/property/${encodeURIComponent(propertyId)}`,
  );
  return data;
}

export async function getDeedsDocumentManifest(
  propertyId: string,
): Promise<BffDeedsDocumentManifestResponse> {
  const { data } = await bffApi.get<BffDeedsDocumentManifestResponse>(
    `/api/partner/deeds/documents/manifest?propertyId=${encodeURIComponent(propertyId)}`,
  );
  return data;
}

export async function listDeedsWatches(): Promise<BffDeedsWatchResponse[]> {
  const { data } = await bffApi.get<BffDeedsWatchResponse[]>("/api/partner/deeds/watches");
  return data;
}

export async function createDeedsWatch(payload: {
  propertyId: string;
  monitoringFrequency?: string;
  alertTypes?: string[];
}): Promise<BffDeedsWatchResponse> {
  const { data } = await bffApi.post<BffDeedsWatchResponse>("/api/partner/deeds/watches", payload);
  return data;
}

export async function updateDeedsWatch(
  subjectId: string,
  payload: {
    status?: string;
    monitoringFrequency?: string;
    alertTypes?: string[];
  },
): Promise<BffDeedsWatchResponse> {
  const { data } = await bffApi.patch<BffDeedsWatchResponse>(
    `/api/partner/deeds/watches/${subjectId}`,
    payload,
  );
  return data;
}

export async function deleteDeedsWatch(subjectId: string): Promise<void> {
  await bffApi.delete(`/api/partner/deeds/watches/${subjectId}`);
}

export async function listDeedsWatchAlerts(): Promise<BffDeedsWatchAlertResponse[]> {
  const { data } = await bffApi.get<BffDeedsWatchAlertResponse[]>("/api/partner/deeds/watches/alerts");
  return data;
}

// ── Profile APIs ────────────────────────────────────────────────────

export interface BffProfileResponse {
  partnerId: string;
  name: string;
  contactEmail: string;
  billingPlan: string;
  enabledFeatures: string[];
  resolvedFeatures: string[];
  quotas: Record<string, number>;
  status: string;
  createdAt: string | null;
  // Branding (whitelabelling)
  logo?: string | null;
  logoDark?: string | null;
  primaryColor?: string | null;
  accentColor?: string | null;
  faviconUrl?: string | null;
  tagline?: string | null;
}

export async function getProfile(): Promise<BffProfileResponse> {
  const { data } = await bffApi.get<BffProfileResponse>("/api/partner/profile");
  return data;
}

export async function updateProfile(payload: {
  name?: string;
  contactEmail?: string;
  billingPlan?: string;
  enabledFeatures?: string[];
  logo?: string;
  logoDark?: string;
  primaryColor?: string;
  accentColor?: string;
  faviconUrl?: string;
  tagline?: string;
}): Promise<BffProfileResponse> {
  const { data } = await bffApi.put<BffProfileResponse>("/api/partner/profile", payload);
  return data;
}

export async function getAdminPartnerProfile(partnerId: string): Promise<BffProfileResponse> {
  const { data } = await bffApi.get<BffProfileResponse>(`/api/admin/partners/${partnerId}/profile`);
  return data;
}

export async function updateAdminPartnerEntitlements(
  partnerId: string,
  payload: {
    billingPlan?: string;
    enabledFeatures?: string[];
  },
): Promise<BffProfileResponse> {
  const { data } = await bffApi.put<BffProfileResponse>(
    `/api/admin/partners/${partnerId}/entitlements`,
    payload,
  );
  return data;
}

// ── API Key APIs ────────────────────────────────────────────────────

export interface BffApiKeyItem {
  keyPrefix: string;
  status: string;
  createdAt: string | null;
  createdBy: string | null;
}

export interface BffApiKeyListResponse {
  partnerId: string;
  keys: BffApiKeyItem[];
}

export interface BffApiKeyGenerateResponse {
  apiKey: string;
  partnerId: string;
  keyPrefix: string;
  status: string;
  createdAt: string;
}

export async function listApiKeys(): Promise<BffApiKeyListResponse> {
  const { data } = await bffApi.get<BffApiKeyListResponse>("/api/partner/api-keys");
  return data;
}

export async function generateApiKey(): Promise<BffApiKeyGenerateResponse> {
  const { data } = await bffApi.post<BffApiKeyGenerateResponse>("/api/partner/api-keys");
  return data;
}

export async function revokeApiKey(keyPrefix: string): Promise<void> {
  await bffApi.delete(`/api/partner/api-keys/${keyPrefix}`);
}

// ── Notification APIs ───────────────────────────────────────────────

export interface BffNotificationPreferences {
  verificationComplete: boolean;
  verificationFailure: boolean;
  weeklySummary: boolean;
  securityAlerts: boolean;
}

export async function getNotifications(): Promise<BffNotificationPreferences> {
  const { data } = await bffApi.get<BffNotificationPreferences>("/api/partner/notifications");
  return data;
}

export async function updateNotifications(
  prefs: BffNotificationPreferences,
): Promise<BffNotificationPreferences> {
  const { data } = await bffApi.put<BffNotificationPreferences>("/api/partner/notifications", prefs);
  return data;
}

// ── Bulk Verification APIs ──────────────────────────────────────────

export interface BffBulkActionResponse {
  action: string;
  processed: number;
  failed: number;
  message: string;
}

export async function exportVerifications(
  commandIds: string[],
  format?: string,
): Promise<BffBulkActionResponse> {
  const { data } = await bffApi.post<BffBulkActionResponse>("/api/partner/verifications/export", {
    commandIds,
    format: format ?? "csv",
  });
  return data;
}

export async function retryVerifications(commandIds: string[]): Promise<BffBulkActionResponse> {
  const { data } = await bffApi.post<BffBulkActionResponse>("/api/partner/verifications/retry", {
    commandIds,
  });
  return data;
}

export async function archiveVerifications(commandIds: string[]): Promise<BffBulkActionResponse> {
  const { data } = await bffApi.post<BffBulkActionResponse>("/api/partner/verifications/archive", {
    commandIds,
  });
  return data;
}

export async function deleteVerifications(commandIds: string[]): Promise<BffBulkActionResponse> {
  const { data } = await bffApi.delete<BffBulkActionResponse>("/api/partner/verifications", {
    data: { commandIds },
  });
  return data;
}

export async function pollVerificationStatus(
  commandId: string,
  options?: { maxAttempts?: number; intervalMs?: number },
): Promise<BffVerificationStatusResponse> {
  const maxAttempts = options?.maxAttempts ?? 20;
  const intervalMs = options?.intervalMs ?? 1500;

  for (let attempt = 0; attempt < maxAttempts; attempt++) {
    const status = await getVerificationStatus(commandId);

    if (isTerminalVerificationStatus(status.status)) {
      return status;
    }

    if (attempt < maxAttempts - 1) {
      await new Promise((resolve) => setTimeout(resolve, intervalMs));
    }
  }

  return getVerificationStatus(commandId);
}

// --- Risk Config ---

export interface RiskTier {
  name: string;
  lowerBound: number;
  upperBound: number;
  decision: string;
}

export interface OverrideRule {
  id: string;
  name: string;
  condition: {
    checkType: string;
    signalKey: string;
    operator: string;
    value: string;
  };
  forcedDecision: string;
  priority: number;
}

export interface RiskConfig {
  strategy: string;
  weights: Record<string, number>;
  tiers: RiskTier[];
  overrideRules: OverrideRule[];
}

export async function getRiskConfig(): Promise<RiskConfig> {
  const { data } = await bffApi.get<RiskConfig>("/risk-config");
  return data;
}

export async function updateRiskConfig(config: RiskConfig): Promise<RiskConfig> {
  const { data } = await bffApi.put<RiskConfig>("/risk-config", config);
  return data;
}

export async function getDefaultRiskConfig(): Promise<RiskConfig> {
  const { data } = await bffApi.get<RiskConfig>("/risk-config/default");
  return data;
}

// --- Policies ---

export interface Policy {
  policyId?: string;
  partnerId?: string;
  name?: string;
  description?: string;
  status?: string;
  version?: number;
  steps?: Array<Record<string, unknown>>;
  scoringConfig?: RiskConfig;
  createdAt?: string;
  updatedAt?: string;
  publishedAt?: string;
}

export async function getPolicy(policyId: string): Promise<Policy> {
  const { data } = await bffApi.get<Policy>(`/policies/${policyId}`);
  return data;
}

export async function listPolicies(): Promise<Policy[]> {
  const { data } = await bffApi.get<Policy[]>("/policies");
  return data;
}

export async function createPolicy(policy: Policy): Promise<Policy> {
  const { data } = await bffApi.post<Policy>("/policies", policy);
  return data;
}

export async function updatePolicy(policyId: string, policy: Policy): Promise<Policy> {
  const { data } = await bffApi.put<Policy>(`/policies/${policyId}`, policy);
  return data;
}

export async function publishPolicy(policyId: string): Promise<Policy> {
  const { data } = await bffApi.post<Policy>(`/policies/${policyId}/publish`);
  return data;
}

export async function deletePolicy(policyId: string): Promise<void> {
  await bffApi.delete(`/policies/${policyId}`);
}

// --- Documents ---

export interface PresignedUrlRequest {
  fileName: string;
  contentType: string;
  documentType: string;
}

export interface PresignedUrlResponse {
  uploadUrl: string;
  s3BucketName: string;
  s3ObjectKey: string;
}

export async function getDocumentPresignedUrl(
  request: PresignedUrlRequest
): Promise<PresignedUrlResponse> {
  const { data } = await bffApi.post<PresignedUrlResponse>("/api/partner/documents/presigned-url", request);
  return data;
}

export async function uploadFileToS3(
  uploadUrl: string,
  file: File,
  onProgress?: (percent: number) => void
): Promise<void> {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.open("PUT", uploadUrl);
    xhr.setRequestHeader("Content-Type", file.type);

    xhr.upload.onprogress = (e) => {
      if (e.lengthComputable && onProgress) {
        onProgress(Math.round((e.loaded / e.total) * 100));
      }
    };

    xhr.onload = () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        resolve();
      } else {
        reject(new Error(`Upload failed: ${xhr.status}`));
      }
    };

    xhr.onerror = () => reject(new Error("Upload failed"));
    xhr.send(file);
  });
}

// --- DHA Permit Submission ---

export interface DhaPermitSubmissionRequest {
  documentType: string;
  permitNumber: string;
  nationality: string;
  employerName?: string;
  s3ObjectKeys: string[];
  s3BucketName: string;
}

export interface DhaPermitSubmissionResponse {
  commandId: string;
  status: string;
  emailSent: boolean;
}

export async function submitDhaPermitVerification(
  request: DhaPermitSubmissionRequest,
): Promise<DhaPermitSubmissionResponse> {
  const { data } = await bffApi.post<DhaPermitSubmissionResponse>(
    "/api/partner/documents/dha-permit-submission",
    request,
  );
  return data;
}

// --- Cases ---

export type CaseStatus = "OPEN" | "IN_REVIEW" | "RESOLVED" | "ESCALATED";
export type CasePriority = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";

export interface CaseComment {
  id: string;
  author: string;
  text: string;
  createdAt: string;
}

export interface CaseTimelineEntry {
  event: string;
  timestamp: string;
  actor: string;
}

export interface Case {
  caseId: string;
  verificationId?: string;
  workflowId?: string;
  partnerId?: string;
  status: CaseStatus;
  assignee?: string;
  priority: CasePriority;
  decision?: string;
  decisionReason?: string;
  compositeRiskScore: number;
  riskTier?: string;
  subjectName?: string;
  subjectId?: string;
  comments: CaseComment[];
  timeline: CaseTimelineEntry[];
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
}

export async function listCases(params?: {
  status?: string;
  pageSize?: number;
}): Promise<Case[]> {
  const queryParams = new URLSearchParams();
  if (params?.status) queryParams.set("status", params.status);
  if (params?.pageSize) queryParams.set("pageSize", String(params.pageSize));
  const query = queryParams.toString();
  const { data } = await bffApi.get<Case[]>(`/cases${query ? `?${query}` : ""}`);
  return data;
}

export async function getCase(caseId: string): Promise<Case> {
  const { data } = await bffApi.get<Case>(`/cases/${caseId}`);
  return data;
}

export async function updateCase(
  caseId: string,
  updates: Partial<Pick<Case, "status" | "assignee" | "priority" | "decision" | "decisionReason">>
): Promise<Case> {
  const { data } = await bffApi.patch<Case>(`/cases/${caseId}`, updates);
  return data;
}

export async function addCaseComment(
  caseId: string,
  comment: { author: string; text: string }
): Promise<Case> {
  const { data } = await bffApi.post<Case>(`/cases/${caseId}/comments`, comment);
  return data;
}

// --- Monitoring ---

export type MonitoringFrequency = "DAILY" | "WEEKLY" | "MONTHLY" | "QUARTERLY";
export type MonitoringStatus = "ACTIVE" | "PAUSED" | "REMOVED";
export type AlertSeverity = "HIGH" | "MEDIUM" | "LOW";

export interface MonitoredSubject {
  subjectId: string;
  partnerId?: string;
  policyId?: string;
  subjectType?: string;
  subjectName?: string;
  subjectIdentifier?: string;
  metadata?: Record<string, unknown>;
  monitoringFrequency: MonitoringFrequency;
  status: MonitoringStatus;
  lastCheckedAt?: string;
  nextCheckAt?: string;
  lastRiskScore?: number;
  lastRiskDecision?: string;
  createdAt: string;
  updatedAt: string;
}

export interface MonitoringAlert {
  alertId: string;
  subjectId: string;
  partnerId?: string;
  severity: AlertSeverity;
  alertType: string;
  title: string;
  description?: string;
  previousRiskScore?: number;
  currentRiskScore?: number;
  previousDecision?: string;
  currentDecision?: string;
  acknowledged: boolean;
  acknowledgedBy?: string;
  acknowledgedAt?: string;
  createdAt: string;
}

export async function listMonitoredSubjects(params?: {
  status?: string;
  pageSize?: number;
}): Promise<MonitoredSubject[]> {
  const queryParams = new URLSearchParams();
  if (params?.status) queryParams.set("status", params.status);
  if (params?.pageSize) queryParams.set("pageSize", String(params.pageSize));
  const query = queryParams.toString();
  const { data } = await bffApi.get<MonitoredSubject[]>(`/monitoring/subjects${query ? `?${query}` : ""}`);
  return data;
}

export async function createMonitoredSubject(
  subject: Partial<MonitoredSubject>
): Promise<MonitoredSubject> {
  const { data } = await bffApi.post<MonitoredSubject>("/monitoring/subjects", subject);
  return data;
}

export async function getMonitoredSubject(subjectId: string): Promise<MonitoredSubject> {
  const { data } = await bffApi.get<MonitoredSubject>(`/monitoring/subjects/${subjectId}`);
  return data;
}

export async function updateMonitoredSubject(
  subjectId: string,
  updates: Partial<Pick<MonitoredSubject, "status" | "monitoringFrequency">>
): Promise<MonitoredSubject> {
  const { data } = await bffApi.patch<MonitoredSubject>(`/monitoring/subjects/${subjectId}`, updates);
  return data;
}

export async function deleteMonitoredSubject(subjectId: string): Promise<void> {
  await bffApi.delete(`/monitoring/subjects/${subjectId}`);
}

export async function listMonitoringAlerts(params?: {
  subjectId?: string;
  severity?: string;
  pageSize?: number;
}): Promise<MonitoringAlert[]> {
  const queryParams = new URLSearchParams();
  if (params?.subjectId) queryParams.set("subjectId", params.subjectId);
  if (params?.severity) queryParams.set("severity", params.severity);
  if (params?.pageSize) queryParams.set("pageSize", String(params.pageSize));
  const query = queryParams.toString();
  const { data } = await bffApi.get<MonitoringAlert[]>(`/monitoring/alerts${query ? `?${query}` : ""}`);
  return data;
}

export async function acknowledgeAlert(alertId: string): Promise<MonitoringAlert> {
  const { data } = await bffApi.post<MonitoringAlert>(`/monitoring/alerts/${alertId}/acknowledge`);
  return data;
}

// --- System Health ---

export type HealthStatus = "HEALTHY" | "DEGRADED" | "DOWN" | "UNCONFIGURED";

export interface ProbeResult {
  success: boolean;
  latencyMs: number;
  detail: string;
}

export interface ExternalServiceHealth {
  id: string;
  name: string;
  protocol: string;
  url: string;
  dns: ProbeResult;
  tcp: ProbeResult;
  http: ProbeResult;
  overallStatus: HealthStatus;
  totalLatencyMs: number;
}

export interface DynamoDbTableHealth {
  tableName: string;
  status: string;
  itemCount: number | null;
  tableSizeBytes: number | null;
  latencyMs: number;
  error: string | null;
}

export interface SqsQueueHealth {
  queueName: string;
  status: string;
  approximateMessageCount: number | null;
  dlqMessageCount: number | null;
  latencyMs: number;
  error: string | null;
}

export interface KinesisStreamHealth {
  streamName: string;
  status: string;
  shardCount: number | null;
  latencyMs: number;
  error: string | null;
}

export interface BedrockHealth {
  region: string;
  status: string;
  latencyMs: number;
  error: string | null;
}

export interface InfrastructureHealth {
  dynamoDbTables: DynamoDbTableHealth[];
  sqsQueues: SqsQueueHealth[];
  kinesisStream: KinesisStreamHealth | null;
  bedrock: BedrockHealth | null;
}

export interface SystemHealthSummary {
  total: number;
  healthy: number;
  degraded: number;
  down: number;
  unconfigured: number;
}

export interface SystemHealthResponse {
  overallStatus: HealthStatus;
  summary: SystemHealthSummary;
  externalIntegrations: ExternalServiceHealth[];
  infrastructure: InfrastructureHealth;
  checkedAt: string;
}

export async function getSystemHealth(): Promise<SystemHealthResponse> {
  const { data } = await bffApi.get<SystemHealthResponse>("/api/admin/system-health");
  return data;
}

// --- Health History ---

export interface HealthDataPoint {
  checkedAt: string;
  status: HealthStatus;
  latencyMs: number;
}

export interface ServiceHistoryResponse {
  serviceId: string;
  dataPoints: HealthDataPoint[];
}

export interface UptimeResponse {
  serviceId: string;
  uptimePercentage: number;
  totalChecks: number;
  healthyChecks: number;
  timeRange: string;
}

export interface IncidentResponse {
  serviceId: string;
  serviceName: string;
  startedAt: string;
  endedAt: string;
  durationMinutes: number;
  status: string;
}

export async function getServiceHistory(
  serviceId: string,
  range: string,
): Promise<ServiceHistoryResponse> {
  const { data } = await bffApi.get<ServiceHistoryResponse>(
    `/api/admin/system-health/history`,
    { params: { serviceId, range } },
  );
  return data;
}

export async function getServiceUptime(range: string): Promise<UptimeResponse[]> {
  const { data } = await bffApi.get<UptimeResponse[]>(
    `/api/admin/system-health/uptime`,
    { params: { range } },
  );
  return data;
}

export async function getIncidents(range: string): Promise<IncidentResponse[]> {
  const { data } = await bffApi.get<IncidentResponse[]>(
    `/api/admin/system-health/incidents`,
    { params: { range } },
  );
  return data;
}

// --- Sanctions Screening ---

export interface SanctionsEntityResponse {
  id: string;
  caption: string;
  schema: string;
  score: number;
  datasets: string[];
  properties: Record<string, string[]>;
  features: Record<string, number>;
  target?: boolean;
  firstSeen?: string;
  lastSeen?: string;
  lastChange?: string;
  referents?: string[];
}

export interface SanctionsAdjacentResponse {
  entities: SanctionsEntityResponse[];
}

export interface SanctionsDispositionRequest {
  entityId: string;
  screeningId: string;
  action: "CONFIRMED_MATCH" | "FALSE_POSITIVE" | "ESCALATED" | "PENDING_REVIEW";
  reason: string;
  reviewedBy?: string;
}

export interface SanctionsDispositionResponse {
  dispositionId: string;
  entityId: string;
  action: string;
  createdAt: string;
}

export async function getSanctionsEntity(entityId: string): Promise<SanctionsEntityResponse> {
  const { data } = await bffApi.get<SanctionsEntityResponse>(`/sanctions/entities/${entityId}`);
  return data;
}

export async function getAdjacentEntities(entityId: string): Promise<SanctionsEntityResponse[]> {
  const { data } = await bffApi.get<SanctionsAdjacentResponse>(`/sanctions/entities/${entityId}/adjacent`);
  return data.entities;
}

export async function submitDisposition(
  disposition: SanctionsDispositionRequest
): Promise<SanctionsDispositionResponse> {
  const { data } = await bffApi.post<SanctionsDispositionResponse>("/sanctions/dispositions", disposition);
  return data;
}

export async function getScreeningHistory(params?: {
  limit?: number;
  offset?: number;
}): Promise<{ items: Array<{
  screeningId: string;
  subjectName: string;
  entityType: string;
  outcome: string;
  matchCount: number;
  screenedAt: string;
  disposition?: string;
  provider: string;
}>; total: number }> {
  const queryParams = new URLSearchParams();
  if (params?.limit) queryParams.set("limit", String(params.limit));
  if (params?.offset) queryParams.set("offset", String(params.offset));
  const query = queryParams.toString();
  const { data } = await bffApi.get(`/sanctions/history${query ? `?${query}` : ""}`);
  return data as { items: Array<{
    screeningId: string;
    subjectName: string;
    entityType: string;
    outcome: string;
    matchCount: number;
    screenedAt: string;
    disposition?: string;
    provider: string;
  }>; total: number };
}
