export interface CheckSessionResult {
  checkType: string;
  label: string;
  status: "success" | "error";
  summary: string[];
  error?: string;
  data?: unknown;
}

export interface CheckSession {
  sessionId: string;
  createdAt: string;
  subject: {
    idNumber: string;
    firstName: string;
    lastName: string;
  };
  checks: CheckSessionResult[];
  totalChecks: number;
  passed: number;
  failed: number;
}

export interface CheckSessionListResponse {
  items: CheckSession[];
  cursor: string | null;
  hasMore: boolean;
}
