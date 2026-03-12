"use client";

import * as React from "react";
import { cn } from "@/lib/cn";
import {
  Trash2,
  Copy,
  CheckCircle,
  AlertCircle,
  GitBranch,
  Save,
  Upload,
  Download,
  Settings,
  Fingerprint,
  MapPin,
  ShieldAlert,
  Building2,
  CreditCard,
  Receipt,
  FileSearch,
  Scale,
  Layers,
  GitFork,
  Search,
  Home,
  FileText,
} from "lucide-react";
import { Button } from "@/components/ui/Button";

// Verification step types
export type VerificationStepType = 
  | "id_verification"
  | "address_verification" 
  | "sanctions_check"
  | "company_check"
  | "credit_check"
  | "tax_check"
  | "document_check"
  | "decision"
  | "parallel"
  | "conditional";

export interface VerificationStep {
  id: string;
  type: VerificationStepType;
  name: string;
  config: Record<string, unknown> & {
    weight?: number;
    minScore?: number;
  };
  next?: string;
  onSuccess?: string;
  onFail?: string;
  parallel?: string[];
}

export type AggregationStrategy = "WEIGHTED_AVERAGE" | "MINIMUM_SCORE" | "MAXIMUM_SCORE";
export type RiskDecision = "APPROVE" | "MANUAL_REVIEW" | "REJECT";

export interface RiskTier {
  name: string;
  lowerBound: number;
  upperBound: number;
  decision: RiskDecision;
}

export interface OverrideRule {
  id: string;
  name: string;
  checkType: string;
  signalKey: string;
  operator: "GT" | "LT" | "EQ" | "GTE" | "LTE" | "CONTAINS";
  value: string;
  forcedDecision: RiskDecision;
  priority: number;
}

export interface ScoringConfig {
  strategy: AggregationStrategy;
  tiers: RiskTier[];
  overrideRules: OverrideRule[];
}

export interface Policy {
  id: string;
  name: string;
  description: string;
  version: number;
  steps: VerificationStep[];
  scoringConfig: ScoringConfig;
  status: "draft" | "published" | "archived";
  createdAt: Date;
  updatedAt: Date;
  createdBy: string;
}

const DEFAULT_SCORING_CONFIG: ScoringConfig = {
  strategy: "WEIGHTED_AVERAGE",
  tiers: [
    { name: "LOW_RISK", lowerBound: 80, upperBound: 100, decision: "APPROVE" },
    { name: "MEDIUM_RISK", lowerBound: 50, upperBound: 79, decision: "MANUAL_REVIEW" },
    { name: "HIGH_RISK", lowerBound: 0, upperBound: 49, decision: "REJECT" },
  ],
  overrideRules: [],
};

const STEP_TYPES: Record<VerificationStepType, { label: string; color: string; icon: React.ReactNode }> = {
  id_verification: { label: "ID Verification", color: "bg-blue-500", icon: <Fingerprint className="h-5 w-5" /> },
  address_verification: { label: "Address Verification", color: "bg-purple-500", icon: <MapPin className="h-5 w-5" /> },
  sanctions_check: { label: "Sanctions Check", color: "bg-red-500", icon: <ShieldAlert className="h-5 w-5" /> },
  company_check: { label: "Company Check", color: "bg-green-500", icon: <Building2 className="h-5 w-5" /> },
  credit_check: { label: "Credit Check", color: "bg-yellow-500", icon: <CreditCard className="h-5 w-5" /> },
  tax_check: { label: "Tax Check", color: "bg-orange-500", icon: <Receipt className="h-5 w-5" /> },
  document_check: { label: "Document Check", color: "bg-indigo-500", icon: <FileSearch className="h-5 w-5" /> },
  decision: { label: "Decision Point", color: "bg-gray-500", icon: <Scale className="h-5 w-5" /> },
  parallel: { label: "Parallel Execution", color: "bg-teal-500", icon: <Layers className="h-5 w-5" /> },
  conditional: { label: "Conditional Logic", color: "bg-pink-500", icon: <GitFork className="h-5 w-5" /> },
};

// --- Step-specific configuration schema ---

type FieldType = "toggle" | "select" | "slider" | "number" | "text" | "multiselect";

interface ConfigFieldDef {
  key: string;
  label: string;
  type: FieldType;
  options?: { label: string; value: string }[];
  min?: number;
  max?: number;
  step?: number;
  defaultValue: unknown;
  showWhen?: (config: Record<string, unknown>) => boolean;
}

const VERIFICATION_STEP_TYPES = new Set<VerificationStepType>([
  "id_verification", "address_verification", "sanctions_check",
  "company_check", "credit_check", "tax_check", "document_check",
]);

const COMMON_EXECUTION_FIELDS: ConfigFieldDef[] = [
  { key: "timeoutSeconds", label: "Timeout (seconds)", type: "number", min: 5, max: 120, defaultValue: 30 },
  { key: "retryOnFailure", label: "Retry on failure", type: "toggle", defaultValue: false },
  { key: "maxRetries", label: "Max retries", type: "number", min: 1, max: 5, defaultValue: 3, showWhen: (c) => c.retryOnFailure === true },
];

const STEP_CONFIGS: Record<VerificationStepType, ConfigFieldDef[]> = {
  id_verification: [
    { key: "idType", label: "ID Type", type: "select", defaultValue: "sa_id", options: [
      { label: "SA ID", value: "sa_id" }, { label: "Passport", value: "passport" }, { label: "Foreign ID", value: "foreign" },
    ]},
    { key: "checkDeceased", label: "Check deceased status", type: "toggle", defaultValue: false },
    { key: "checkFraud", label: "Check fraud indicators", type: "toggle", defaultValue: false },
    { key: "nameMatchThreshold", label: "Name match threshold", type: "slider", min: 50, max: 100, step: 5, defaultValue: 80 },
  ],
  address_verification: [
    { key: "proofSource", label: "Proof source", type: "select", defaultValue: "utility_bill", options: [
      { label: "Utility Bill", value: "utility_bill" }, { label: "Bank Statement", value: "bank_statement" },
      { label: "Municipal Account", value: "municipal" }, { label: "Lease Agreement", value: "lease" },
    ]},
    { key: "maxDocumentAge", label: "Max document age (months)", type: "number", min: 1, max: 24, defaultValue: 3 },
    { key: "matchThreshold", label: "Match threshold", type: "slider", min: 50, max: 100, step: 5, defaultValue: 75 },
  ],
  sanctions_check: [
    { key: "screeningLists", label: "Screening lists", type: "multiselect", defaultValue: ["un", "ofac"], options: [
      { label: "UN", value: "un" }, { label: "EU", value: "eu" }, { label: "OFAC", value: "ofac" },
      { label: "UK HMT", value: "uk_hmt" }, { label: "SA FIC", value: "sa_fic" },
    ]},
    { key: "includeAliases", label: "Include aliases", type: "toggle", defaultValue: true },
    { key: "fuzzyMatching", label: "Fuzzy matching", type: "toggle", defaultValue: true },
    { key: "matchThreshold", label: "Match threshold", type: "slider", min: 50, max: 100, step: 5, defaultValue: 80 },
  ],
  company_check: [
    { key: "checkDirectors", label: "Check directors", type: "toggle", defaultValue: true },
    { key: "checkAnnualReturns", label: "Check annual returns", type: "toggle", defaultValue: false },
    { key: "checkBbee", label: "Check B-BBEE status", type: "toggle", defaultValue: false },
    { key: "checkTaxClearance", label: "Check tax clearance", type: "toggle", defaultValue: false },
  ],
  credit_check: [
    { key: "bureau", label: "Bureau", type: "select", defaultValue: "auto", options: [
      { label: "Auto", value: "auto" }, { label: "TransUnion", value: "transunion" },
      { label: "Experian", value: "experian" }, { label: "XDS", value: "xds" },
    ]},
    { key: "minCreditScore", label: "Min credit score", type: "number", min: 0, max: 900, defaultValue: 0 },
    { key: "includePaymentProfile", label: "Include payment profile", type: "toggle", defaultValue: true },
    { key: "includeJudgments", label: "Include judgments", type: "toggle", defaultValue: true },
    { key: "includeEnquiries", label: "Include enquiries", type: "toggle", defaultValue: false },
  ],
  tax_check: [
    { key: "checkCompliance", label: "Check compliance", type: "toggle", defaultValue: true },
    { key: "checkClearance", label: "Check clearance", type: "toggle", defaultValue: true },
    { key: "checkOutstandingReturns", label: "Check outstanding returns", type: "toggle", defaultValue: false },
    { key: "includePaymentHistory", label: "Include payment history", type: "toggle", defaultValue: false },
  ],
  document_check: [
    { key: "acceptedDocuments", label: "Accepted documents", type: "multiselect", defaultValue: ["passport", "id_card"], options: [
      { label: "Passport", value: "passport" }, { label: "Driver's License", value: "drivers_license" },
      { label: "ID Card", value: "id_card" }, { label: "Work Permit", value: "work_permit" },
    ]},
    { key: "requireOcr", label: "Require OCR", type: "toggle", defaultValue: false },
    { key: "verifyIssuingAuth", label: "Verify issuing authority", type: "toggle", defaultValue: false },
    { key: "checkExpiry", label: "Check expiry", type: "toggle", defaultValue: true },
  ],
  decision: [
    { key: "conditionSource", label: "Condition source", type: "select", defaultValue: "composite_score", options: [
      { label: "Composite Score", value: "composite_score" }, { label: "Step Signal", value: "step_signal" },
      { label: "External API", value: "external_api" },
    ]},
    { key: "signalKey", label: "Signal key", type: "text", defaultValue: "" },
    { key: "operator", label: "Operator", type: "select", defaultValue: "GTE", options: [
      { label: ">", value: "GT" }, { label: ">=", value: "GTE" }, { label: "<", value: "LT" },
      { label: "<=", value: "LTE" }, { label: "=", value: "EQ" },
    ]},
    { key: "thresholdValue", label: "Threshold value", type: "text", defaultValue: "" },
  ],
  parallel: [
    { key: "maxConcurrent", label: "Max concurrent", type: "number", min: 2, max: 10, defaultValue: 5 },
    { key: "timeoutSeconds", label: "Timeout (seconds)", type: "number", min: 10, max: 300, defaultValue: 60 },
    { key: "failureStrategy", label: "Failure strategy", type: "select", defaultValue: "fail_fast", options: [
      { label: "Fail Fast", value: "fail_fast" }, { label: "Wait All", value: "wait_all" },
      { label: "Ignore Failures", value: "ignore" },
    ]},
  ],
  conditional: [
    { key: "conditionSource", label: "Condition source", type: "select", defaultValue: "composite_score", options: [
      { label: "Composite Score", value: "composite_score" }, { label: "Step Signal", value: "step_signal" },
      { label: "External API", value: "external_api" },
    ]},
    { key: "signalKey", label: "Signal key", type: "text", defaultValue: "" },
    { key: "operator", label: "Operator", type: "select", defaultValue: "GTE", options: [
      { label: ">", value: "GT" }, { label: ">=", value: "GTE" }, { label: "<", value: "LT" },
      { label: "<=", value: "LTE" }, { label: "=", value: "EQ" },
    ]},
    { key: "thresholdValue", label: "Threshold value", type: "text", defaultValue: "" },
  ],
};

// --- Policy Templates ---

interface PolicyTemplate {
  name: string;
  description: string;
  icon: React.ReactNode;
  steps: VerificationStep[];
}

const POLICY_TEMPLATES: PolicyTemplate[] = [
  {
    name: "Standard KYC",
    description: "Individual onboarding baseline with ID, sanctions, and credit checks",
    icon: <Fingerprint className="h-6 w-6 text-blue-500" />,
    steps: [
      { id: "tpl-1", type: "id_verification", name: "ID Verification", config: { weight: 0.4, minScore: 60, idType: "sa_id", checkDeceased: true, checkFraud: true, nameMatchThreshold: 80, timeoutSeconds: 30, retryOnFailure: false } },
      { id: "tpl-2", type: "sanctions_check", name: "Sanctions Check", config: { weight: 0.3, minScore: 70, screeningLists: ["un", "eu", "ofac", "uk_hmt", "sa_fic"], includeAliases: true, fuzzyMatching: true, matchThreshold: 80, timeoutSeconds: 30, retryOnFailure: false } },
      { id: "tpl-3", type: "credit_check", name: "Credit Check", config: { weight: 0.3, minScore: 50, bureau: "auto", minCreditScore: 300, includePaymentProfile: true, includeJudgments: true, includeEnquiries: false, timeoutSeconds: 30, retryOnFailure: false } },
    ],
  },
  {
    name: "Enhanced Due Diligence",
    description: "High-risk individual screening with comprehensive checks",
    icon: <Search className="h-6 w-6 text-amber-500" />,
    steps: [
      { id: "tpl-1", type: "id_verification", name: "ID Verification", config: { weight: 0.25, minScore: 70, idType: "sa_id", checkDeceased: true, checkFraud: true, nameMatchThreshold: 90, timeoutSeconds: 30, retryOnFailure: true, maxRetries: 2 } },
      { id: "tpl-2", type: "sanctions_check", name: "Sanctions Check", config: { weight: 0.25, minScore: 80, screeningLists: ["un", "eu", "ofac", "uk_hmt", "sa_fic"], includeAliases: true, fuzzyMatching: true, matchThreshold: 85, timeoutSeconds: 30, retryOnFailure: true, maxRetries: 2 } },
      { id: "tpl-3", type: "document_check", name: "Document Check", config: { weight: 0.2, minScore: 60, acceptedDocuments: ["passport", "id_card"], requireOcr: true, verifyIssuingAuth: true, checkExpiry: true, timeoutSeconds: 60, retryOnFailure: false } },
      { id: "tpl-4", type: "credit_check", name: "Credit Check", config: { weight: 0.15, minScore: 50, bureau: "auto", minCreditScore: 300, includePaymentProfile: true, includeJudgments: true, includeEnquiries: true, timeoutSeconds: 30, retryOnFailure: false } },
      { id: "tpl-5", type: "address_verification", name: "Address Verification", config: { weight: 0.15, minScore: 60, proofSource: "utility_bill", maxDocumentAge: 3, matchThreshold: 80, timeoutSeconds: 30, retryOnFailure: false } },
    ],
  },
  {
    name: "Business Verification",
    description: "Corporate onboarding with company, tax, and sanctions checks",
    icon: <Building2 className="h-6 w-6 text-green-500" />,
    steps: [
      { id: "tpl-1", type: "company_check", name: "Company Check", config: { weight: 0.4, minScore: 60, checkDirectors: true, checkAnnualReturns: true, checkBbee: false, checkTaxClearance: true, timeoutSeconds: 60, retryOnFailure: false } },
      { id: "tpl-2", type: "tax_check", name: "Tax Check", config: { weight: 0.3, minScore: 60, checkCompliance: true, checkClearance: true, checkOutstandingReturns: true, includePaymentHistory: false, timeoutSeconds: 30, retryOnFailure: false } },
      { id: "tpl-3", type: "sanctions_check", name: "Sanctions Check", config: { weight: 0.3, minScore: 70, screeningLists: ["un", "eu", "ofac", "uk_hmt", "sa_fic"], includeAliases: true, fuzzyMatching: true, matchThreshold: 80, timeoutSeconds: 30, retryOnFailure: false } },
    ],
  },
  {
    name: "Property Transaction",
    description: "Property-related workflows with ID, address, and credit verification",
    icon: <Home className="h-6 w-6 text-purple-500" />,
    steps: [
      { id: "tpl-1", type: "id_verification", name: "ID Verification", config: { weight: 0.35, minScore: 60, idType: "sa_id", checkDeceased: true, checkFraud: true, nameMatchThreshold: 85, timeoutSeconds: 30, retryOnFailure: false } },
      { id: "tpl-2", type: "address_verification", name: "Address Verification", config: { weight: 0.35, minScore: 60, proofSource: "utility_bill", maxDocumentAge: 3, matchThreshold: 80, timeoutSeconds: 30, retryOnFailure: false } },
      { id: "tpl-3", type: "credit_check", name: "Credit Check", config: { weight: 0.3, minScore: 50, bureau: "auto", minCreditScore: 500, includePaymentProfile: true, includeJudgments: true, includeEnquiries: true, timeoutSeconds: 30, retryOnFailure: false } },
    ],
  },
];

// --- Canvas summary helper ---

function getStepSummary(step: VerificationStep): string {
  const c = step.config;
  const parts: string[] = [];

  switch (step.type) {
    case "id_verification": {
      const idLabels: Record<string, string> = { sa_id: "SA ID", passport: "Passport", foreign: "Foreign" };
      if (c.idType) parts.push(idLabels[c.idType as string] ?? String(c.idType));
      if (c.checkDeceased) parts.push("Deceased ✓");
      if (c.checkFraud) parts.push("Fraud ✓");
      if (c.nameMatchThreshold !== undefined && c.nameMatchThreshold !== 80) parts.push(`Match ≥${c.nameMatchThreshold}%`);
      break;
    }
    case "address_verification": {
      const srcLabels: Record<string, string> = { utility_bill: "Utility", bank_statement: "Bank", municipal: "Municipal", lease: "Lease" };
      if (c.proofSource) parts.push(srcLabels[c.proofSource as string] ?? String(c.proofSource));
      if (c.maxDocumentAge) parts.push(`≤${c.maxDocumentAge}mo`);
      if (c.matchThreshold !== undefined && c.matchThreshold !== 75) parts.push(`Match ≥${c.matchThreshold}%`);
      break;
    }
    case "sanctions_check": {
      const lists = c.screeningLists as string[] | undefined;
      if (lists?.length) parts.push(`${lists.length} lists`);
      if (c.fuzzyMatching) parts.push("Fuzzy ✓");
      if (c.matchThreshold !== undefined && c.matchThreshold !== 80) parts.push(`Match ≥${c.matchThreshold}%`);
      break;
    }
    case "company_check": {
      if (c.checkDirectors) parts.push("Directors ✓");
      if (c.checkAnnualReturns) parts.push("Returns ✓");
      if (c.checkBbee) parts.push("B-BBEE ✓");
      if (c.checkTaxClearance) parts.push("Tax ✓");
      break;
    }
    case "credit_check": {
      const bureauLabels: Record<string, string> = { auto: "Auto", transunion: "TransUnion", experian: "Experian", xds: "XDS" };
      if (c.bureau) parts.push(bureauLabels[c.bureau as string] ?? String(c.bureau));
      if (c.minCreditScore && (c.minCreditScore as number) > 0) parts.push(`Score ≥${c.minCreditScore}`);
      if (c.includeJudgments) parts.push("Judgments ✓");
      break;
    }
    case "tax_check": {
      if (c.checkCompliance) parts.push("Compliance ✓");
      if (c.checkClearance) parts.push("Clearance ✓");
      if (c.checkOutstandingReturns) parts.push("Returns ✓");
      break;
    }
    case "document_check": {
      const docs = c.acceptedDocuments as string[] | undefined;
      if (docs?.length) parts.push(`${docs.length} doc types`);
      if (c.requireOcr) parts.push("OCR ✓");
      if (c.checkExpiry) parts.push("Expiry ✓");
      break;
    }
    case "decision":
    case "conditional": {
      if (c.operator && c.thresholdValue) parts.push(`${c.signalKey || "score"} ${c.operator} ${c.thresholdValue}`);
      break;
    }
    case "parallel": {
      if (c.maxConcurrent) parts.push(`Max ${c.maxConcurrent}`);
      const stratLabels: Record<string, string> = { fail_fast: "Fail fast", wait_all: "Wait all", ignore: "Ignore" };
      if (c.failureStrategy) parts.push(stratLabels[c.failureStrategy as string] ?? String(c.failureStrategy));
      break;
    }
  }

  return parts.join(" · ");
}

export interface PolicyBuilderProps {
  initialPolicy?: Policy;
  onSave?: (policy: Policy) => void;
  onPublish?: (policy: Policy) => void;
}

export const PolicyBuilder: React.FC<PolicyBuilderProps> = ({
  initialPolicy,
  onSave,
  onPublish,
}) => {
  const [policy, setPolicy] = React.useState<Policy>(initialPolicy || {
    id: "new-policy",
    name: "New Policy",
    description: "",
    version: 1,
    steps: [],
    scoringConfig: { ...DEFAULT_SCORING_CONFIG },
    status: "draft",
    createdAt: new Date(),
    updatedAt: new Date(),
    createdBy: "current-user",
  });
  const [showScoringPanel, setShowScoringPanel] = React.useState(false);

  const [selectedStep, setSelectedStep] = React.useState<string | null>(null);
  const [validationErrors, setValidationErrors] = React.useState<string[]>([]);

  const addStep = (type: VerificationStepType) => {
    const newStep: VerificationStep = {
      id: `step-${Date.now()}`,
      type,
      name: STEP_TYPES[type].label,
      config: {},
    };

    setPolicy(prev => ({
      ...prev,
      steps: [...prev.steps, newStep],
      updatedAt: new Date(),
    }));
  };

  const removeStep = (stepId: string) => {
    setPolicy(prev => ({
      ...prev,
      steps: prev.steps.filter(s => s.id !== stepId),
      updatedAt: new Date(),
    }));
    setSelectedStep(null);
  };

  const duplicateStep = (stepId: string) => {
    const step = policy.steps.find(s => s.id === stepId);
    if (!step) return;

    const newStep: VerificationStep = {
      ...step,
      id: `step-${Date.now()}`,
      name: `${step.name} (Copy)`,
    };

    setPolicy(prev => ({
      ...prev,
      steps: [...prev.steps, newStep],
      updatedAt: new Date(),
    }));
  };

  const CONTROL_STEP_TYPES = new Set(["decision", "parallel", "conditional"]);

  const validatePolicy = (): boolean => {
    const errors: string[] = [];

    if (!policy.name || policy.name.trim() === "") {
      errors.push("Policy name is required");
    }

    const verificationSteps = policy.steps.filter(s => !CONTROL_STEP_TYPES.has(s.type));
    if (verificationSteps.length === 0) {
      errors.push("Policy must have at least one verification step");
    }

    // Validate weight bounds (0-1) for non-control steps
    for (const step of verificationSteps) {
      const weight = step.config.weight;
      if (weight !== undefined && (weight < 0 || weight > 1)) {
        errors.push(`Step "${step.name}" has invalid weight: must be between 0 and 1`);
      }
    }

    // Validate tier coverage (0-100, no gaps or overlaps)
    const tiers = policy.scoringConfig.tiers;
    if (tiers.length > 0) {
      for (const tier of tiers) {
        if (tier.lowerBound < 0 || tier.upperBound > 100) {
          errors.push(`Tier "${tier.name}" bounds must be between 0 and 100`);
        }
        if (tier.lowerBound >= tier.upperBound) {
          errors.push(`Tier "${tier.name}" lower bound must be less than upper bound`);
        }
      }

      // Check for gaps and overlaps in tiers
      const sortedTiers = [...tiers].sort((a, b) => a.lowerBound - b.lowerBound);
      for (let i = 1; i < sortedTiers.length; i++) {
        const prev = sortedTiers[i - 1];
        const curr = sortedTiers[i];
        if (curr.lowerBound < prev.upperBound) {
          errors.push(`Tiers "${prev.name}" and "${curr.name}" overlap`);
        } else if (curr.lowerBound > prev.upperBound + 1) {
          errors.push(`Gap between tiers "${prev.name}" and "${curr.name}"`);
        }
      }
    }

    setValidationErrors(errors);
    return errors.length === 0;
  };

  const handleSave = () => {
    if (validatePolicy()) {
      onSave?.(policy);
      alert("Policy saved successfully!");
    }
  };

  const handlePublish = () => {
    if (validatePolicy()) {
      const publishedPolicy = {
        ...policy,
        status: "published" as const,
        updatedAt: new Date(),
      };
      setPolicy(publishedPolicy);
      onPublish?.(publishedPolicy);
      alert("Policy published successfully!");
    }
  };

  return (
    <div className="h-full flex flex-col">
      {/* Header */}
      <div className="bg-base-100 border-b border-border px-6 py-4">
        <div className="flex items-center justify-between">
          <div>
            <input
              type="text"
              value={policy.name}
              onChange={(e) => setPolicy(prev => ({ ...prev, name: e.target.value }))}
              className="text-xl font-semibold text-text bg-transparent border-none focus:outline-none focus:ring-2 focus:ring-accent rounded px-2 py-1"
              placeholder="Policy Name"
            />
            <p className="text-sm text-text-muted mt-1">Version {policy.version} • {policy.status}</p>
          </div>

          <div className="flex items-center gap-2">
            <Button variant="secondary" size="sm" onClick={() => { setShowScoringPanel(!showScoringPanel); setSelectedStep(null); }}>
              <Settings className="h-4 w-4 mr-2" />
              Scoring Config
            </Button>
            <Button variant="secondary" size="sm" onClick={() => alert("Export functionality")}>
              <Download className="h-4 w-4 mr-2" />
              Export
            </Button>
            <Button variant="secondary" size="sm" onClick={handleSave}>
              <Save className="h-4 w-4 mr-2" />
              Save Draft
            </Button>
            <Button variant="primary" size="sm" onClick={handlePublish}>
              <Upload className="h-4 w-4 mr-2" />
              Publish
            </Button>
          </div>
        </div>

        {validationErrors.length > 0 && (
          <div className="mt-4 bg-danger/10 border border-danger/20 rounded-aws-control p-3">
            <div className="flex items-start gap-2">
              <AlertCircle className="h-4 w-4 text-danger mt-0.5" />
              <div>
                <p className="text-sm font-medium text-danger">Validation Errors</p>
                <ul className="text-xs text-danger/80 mt-1 space-y-1">
                  {validationErrors.map((error, i) => (
                    <li key={i}>• {error}</li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        )}
      </div>

      <div className="flex-1 flex overflow-hidden">
        {/* Step Palette */}
        <div className="w-64 bg-base-100 border-r border-border overflow-y-auto">
          <div className="p-4">
            <h3 className="text-sm font-semibold text-text mb-3">Add Steps</h3>
            <div className="space-y-2">
              {(Object.entries(STEP_TYPES) as [VerificationStepType, typeof STEP_TYPES[VerificationStepType]][]).map(([type, info]) => (
                <button
                  key={type}
                  onClick={() => addStep(type)}
                  className="w-full flex items-center gap-3 p-3 rounded-aws-control border border-border hover:border-accent hover:bg-accent-soft transition-colors text-left"
                >
                  <div className={cn("w-8 h-8 rounded flex items-center justify-center text-white text-sm", info.color)}>
                    {info.icon}
                  </div>
                  <span className="text-sm text-text">{info.label}</span>
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Canvas */}
        <div className="flex-1 bg-base-200 overflow-auto p-8">
          <div className="max-w-4xl mx-auto space-y-4">
            {policy.steps.length === 0 ? (
              <div className="py-8">
                <div className="text-center mb-8">
                  <GitBranch className="h-12 w-12 text-text-muted mx-auto mb-4" />
                  <p className="text-text font-medium">Start with a template or build from scratch</p>
                  <p className="text-sm text-text-muted mt-1">Choose a pre-configured workflow below, or add steps from the left panel</p>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  {POLICY_TEMPLATES.map((tpl) => (
                    <button
                      key={tpl.name}
                      onClick={() => {
                        const stamped = tpl.steps.map((s, i) => ({ ...s, id: `step-${Date.now()}-${i}`, config: { ...s.config } }));
                        setPolicy(prev => ({ ...prev, steps: stamped, updatedAt: new Date() }));
                      }}
                      className="text-left console-card hover:ring-2 hover:ring-accent transition-all"
                    >
                      <div className="console-card-body">
                        <div className="flex items-center gap-3 mb-2">
                          <span>{tpl.icon}</span>
                          <div>
                            <h4 className="font-medium text-text text-sm">{tpl.name}</h4>
                            <p className="text-xs text-text-muted">{tpl.steps.length} steps</p>
                          </div>
                        </div>
                        <p className="text-xs text-text-muted">{tpl.description}</p>
                      </div>
                    </button>
                  ))}
                  <button
                    onClick={() => {}}
                    className="text-left console-card border-dashed hover:ring-2 hover:ring-accent transition-all"
                  >
                    <div className="console-card-body flex items-center justify-center h-full">
                      <div className="text-center">
                        <FileText className="h-6 w-6 text-text-muted mx-auto" />
                        <h4 className="font-medium text-text text-sm mt-2">Blank Policy</h4>
                        <p className="text-xs text-text-muted mt-1">Start from scratch</p>
                      </div>
                    </div>
                  </button>
                </div>
              </div>
            ) : (
              policy.steps.map((step, index) => (
                <div key={step.id}>
                  <div
                    className={cn(
                      "console-card cursor-pointer transition-all",
                      selectedStep === step.id && "ring-2 ring-accent"
                    )}
                    onClick={() => setSelectedStep(step.id)}
                  >
                    <div className="console-card-body">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                          <div className={cn("w-10 h-10 rounded flex items-center justify-center text-white", STEP_TYPES[step.type].color)}>
                            {STEP_TYPES[step.type].icon}
                          </div>
                          <div>
                            <h4 className="font-medium text-text">{step.name}</h4>
                            <p className="text-xs text-text-muted">{STEP_TYPES[step.type].label}</p>
                          </div>
                        </div>

                        <div className="flex items-center gap-2 shrink-0">
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={(e) => {
                              e.stopPropagation();
                              duplicateStep(step.id);
                            }}
                          >
                            <Copy className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={(e) => {
                              e.stopPropagation();
                              removeStep(step.id);
                            }}
                          >
                            <Trash2 className="h-4 w-4 text-danger" />
                          </Button>
                        </div>
                      </div>

                      {(() => {
                        const summary = getStepSummary(step);
                        if (!summary) return null;
                        return (
                          <div className="mt-2 pt-2 border-t border-border">
                            <p className="text-xs text-text-muted truncate">{summary}</p>
                            {!CONTROL_STEP_TYPES.has(step.type) && (step.config.weight !== undefined || step.config.timeoutSeconds !== undefined) && (
                              <p className="text-[10px] text-text-muted mt-0.5">
                                {step.config.weight !== undefined && <span>⚖ {(step.config.weight as number).toFixed(1)}</span>}
                                {step.config.weight !== undefined && step.config.timeoutSeconds !== undefined && <span> · </span>}
                                {step.config.timeoutSeconds !== undefined && <span>⏱ {step.config.timeoutSeconds as number}s</span>}
                              </p>
                            )}
                          </div>
                        );
                      })()}

                      {step.type === "conditional" && (
                        <div className="mt-4 pt-4 border-t border-border">
                          <div className="grid grid-cols-2 gap-4">
                            <div className="flex items-center gap-2">
                              <CheckCircle className="h-4 w-4 text-success" />
                              <span className="text-xs text-text-muted">On Success</span>
                            </div>
                            <div className="flex items-center gap-2">
                              <AlertCircle className="h-4 w-4 text-danger" />
                              <span className="text-xs text-text-muted">On Failure</span>
                            </div>
                          </div>
                        </div>
                      )}
                    </div>
                  </div>

                  {index < policy.steps.length - 1 && (
                    <div className="flex justify-center py-2">
                      <div className="w-0.5 h-8 bg-border" />
                    </div>
                  )}
                </div>
              ))
            )}
          </div>
        </div>

        {/* Properties Panel */}
        {selectedStep && (
          <div className="w-80 bg-base-100 border-l border-border overflow-y-auto">
            <div className="p-4">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-sm font-semibold text-text">Properties</h3>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => setSelectedStep(null)}
                >
                  ✕
                </Button>
              </div>

              {(() => {
                const step = policy.steps.find(s => s.id === selectedStep);
                if (!step) return null;

                const updateStepConfig = (key: string, value: unknown) => {
                  setPolicy(prev => ({
                    ...prev,
                    steps: prev.steps.map(s =>
                      s.id === selectedStep ? { ...s, config: { ...s.config, [key]: value } } : s
                    ),
                  }));
                };

                return (
                  <div className="space-y-4">
                    <div>
                      <label className="text-xs text-text-muted mb-1 block">Step Name</label>
                      <input
                        type="text"
                        value={step.name}
                        onChange={(e) => {
                          setPolicy(prev => ({
                            ...prev,
                            steps: prev.steps.map(s =>
                              s.id === selectedStep ? { ...s, name: e.target.value } : s
                            ),
                          }));
                        }}
                        className="aws-input w-full"
                      />
                    </div>

                    <div>
                      <label className="text-xs text-text-muted mb-1 block">Type</label>
                      <div className="text-sm text-text">{STEP_TYPES[step.type].label}</div>
                    </div>

                    {/* Risk Scoring Config per Step */}
                    {step.type !== "decision" && step.type !== "parallel" && step.type !== "conditional" && (
                      <div className="pt-4 border-t border-border space-y-4">
                        <div className="flex items-center gap-2 mb-1">
                          <Settings className="h-4 w-4 text-text-muted" />
                          <span className="text-xs font-semibold text-text">Risk Scoring</span>
                        </div>

                        <div>
                          <div className="flex items-center justify-between mb-1">
                            <label className="text-xs text-text-muted">Weight</label>
                            <span className="text-xs font-medium text-text">{(step.config.weight ?? 1.0).toFixed(1)}</span>
                          </div>
                          <input
                            type="range"
                            min="0"
                            max="1"
                            step="0.1"
                            value={step.config.weight ?? 1.0}
                            onChange={(e) => updateStepConfig("weight", parseFloat(e.target.value))}
                            className="w-full accent-[color:var(--color-accent)]"
                          />
                          <div className="flex justify-between text-[10px] text-text-muted">
                            <span>0.0</span>
                            <span>1.0</span>
                          </div>
                        </div>

                        <div>
                          <div className="flex items-center justify-between mb-1">
                            <label className="text-xs text-text-muted">Min Score Threshold</label>
                            <span className="text-xs font-medium text-text">{step.config.minScore ?? 0}</span>
                          </div>
                          <input
                            type="range"
                            min="0"
                            max="100"
                            step="5"
                            value={step.config.minScore ?? 0}
                            onChange={(e) => updateStepConfig("minScore", parseInt(e.target.value))}
                            className="w-full accent-[color:var(--color-accent)]"
                          />
                          <div className="flex justify-between text-[10px] text-text-muted">
                            <span>0</span>
                            <span>100</span>
                          </div>
                          <p className="text-[10px] text-text-muted mt-1">
                            If this check scores below the threshold, the entire workflow may be flagged.
                          </p>
                        </div>
                      </div>
                    )}

                    <StepConfigFields step={step} onUpdate={updateStepConfig} />
                  </div>
                );
              })()}
            </div>
          </div>
        )}

        {/* Scoring Config Panel */}
        {showScoringPanel && !selectedStep && (
          <ScoringConfigPanel
            config={policy.scoringConfig}
            onChange={(scoringConfig) => setPolicy(prev => ({ ...prev, scoringConfig, updatedAt: new Date() }))}
            onClose={() => setShowScoringPanel(false)}
          />
        )}
      </div>
    </div>
  );
};

/* ------------------------------------------------------------------ */
/*  Step Config Fields (dynamic renderer)                             */
/* ------------------------------------------------------------------ */

function StepConfigFields({
  step,
  onUpdate,
}: {
  step: VerificationStep;
  onUpdate: (key: string, value: unknown) => void;
}) {
  const fields = STEP_CONFIGS[step.type] ?? [];
  const isVerification = VERIFICATION_STEP_TYPES.has(step.type);
  const allFields = isVerification ? [...fields, ...COMMON_EXECUTION_FIELDS] : fields;

  const getVal = (key: string, defaultValue: unknown) =>
    step.config[key] !== undefined ? step.config[key] : defaultValue;

  return (
    <div className="pt-4 border-t border-border space-y-4">
      <div className="flex items-center gap-2 mb-1">
        <Settings className="h-4 w-4 text-text-muted" />
        <span className="text-xs font-semibold text-text">Configuration</span>
      </div>

      {allFields.map((field) => {
        if (field.showWhen && !field.showWhen(step.config)) return null;

        switch (field.type) {
          case "toggle":
            return (
              <label key={field.key} className="flex items-center justify-between cursor-pointer">
                <span className="text-xs text-text">{field.label}</span>
                <input
                  type="checkbox"
                  checked={!!getVal(field.key, field.defaultValue)}
                  onChange={(e) => onUpdate(field.key, e.target.checked)}
                  className="accent-[color:var(--color-accent)] h-4 w-4"
                />
              </label>
            );

          case "select":
            return (
              <div key={field.key}>
                <label className="text-xs text-text-muted mb-1 block">{field.label}</label>
                <select
                  value={String(getVal(field.key, field.defaultValue))}
                  onChange={(e) => onUpdate(field.key, e.target.value)}
                  className="aws-select w-full text-xs"
                >
                  {field.options?.map((opt) => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                  ))}
                </select>
              </div>
            );

          case "slider":
            return (
              <div key={field.key}>
                <div className="flex items-center justify-between mb-1">
                  <label className="text-xs text-text-muted">{field.label}</label>
                  <span className="text-xs font-medium text-text">{String(getVal(field.key, field.defaultValue))}</span>
                </div>
                <input
                  type="range"
                  min={field.min ?? 0}
                  max={field.max ?? 100}
                  step={field.step ?? 5}
                  value={Number(getVal(field.key, field.defaultValue))}
                  onChange={(e) => onUpdate(field.key, parseInt(e.target.value))}
                  className="w-full accent-[color:var(--color-accent)]"
                />
                <div className="flex justify-between text-[10px] text-text-muted">
                  <span>{field.min ?? 0}</span>
                  <span>{field.max ?? 100}</span>
                </div>
              </div>
            );

          case "number":
            return (
              <div key={field.key}>
                <label className="text-xs text-text-muted mb-1 block">{field.label}</label>
                <input
                  type="number"
                  min={field.min}
                  max={field.max}
                  value={Number(getVal(field.key, field.defaultValue))}
                  onChange={(e) => onUpdate(field.key, parseInt(e.target.value) || 0)}
                  className="aws-input w-full text-xs"
                />
              </div>
            );

          case "text":
            return (
              <div key={field.key}>
                <label className="text-xs text-text-muted mb-1 block">{field.label}</label>
                <input
                  type="text"
                  value={String(getVal(field.key, field.defaultValue))}
                  onChange={(e) => onUpdate(field.key, e.target.value)}
                  className="aws-input w-full text-xs"
                  placeholder={field.label}
                />
              </div>
            );

          case "multiselect": {
            const selected = (getVal(field.key, field.defaultValue) as string[]) || [];
            return (
              <div key={field.key}>
                <label className="text-xs text-text-muted mb-1 block">{field.label}</label>
                <div className="space-y-1">
                  {field.options?.map((opt) => (
                    <label key={opt.value} className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={selected.includes(opt.value)}
                        onChange={(e) => {
                          const next = e.target.checked
                            ? [...selected, opt.value]
                            : selected.filter((v) => v !== opt.value);
                          onUpdate(field.key, next);
                        }}
                        className="accent-[color:var(--color-accent)] h-3.5 w-3.5"
                      />
                      <span className="text-xs text-text">{opt.label}</span>
                    </label>
                  ))}
                </div>
              </div>
            );
          }

          default:
            return null;
        }
      })}

      {isVerification && (
        <p className="text-[10px] text-text-muted mt-2 border-t border-border pt-2">
          Execution settings apply when this step runs in the verification pipeline.
        </p>
      )}
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Scoring Config Panel                                              */
/* ------------------------------------------------------------------ */

function ScoringConfigPanel({
  config,
  onChange,
  onClose,
}: {
  config: ScoringConfig;
  onChange: (config: ScoringConfig) => void;
  onClose: () => void;
}) {
  const updateTier = (index: number, field: keyof RiskTier, value: string | number) => {
    const tiers = [...config.tiers];
    tiers[index] = { ...tiers[index], [field]: value };
    onChange({ ...config, tiers });
  };

  const addOverrideRule = () => {
    const rule: OverrideRule = {
      id: `rule-${Date.now()}`,
      name: "New Rule",
      checkType: "",
      signalKey: "",
      operator: "GT",
      value: "",
      forcedDecision: "REJECT",
      priority: config.overrideRules.length + 1,
    };
    onChange({ ...config, overrideRules: [...config.overrideRules, rule] });
  };

  const updateRule = (index: number, field: keyof OverrideRule, value: string | number) => {
    const rules = [...config.overrideRules];
    rules[index] = { ...rules[index], [field]: value };
    onChange({ ...config, overrideRules: rules });
  };

  const removeRule = (index: number) => {
    onChange({ ...config, overrideRules: config.overrideRules.filter((_, i) => i !== index) });
  };

  return (
    <div className="w-96 bg-base-100 border-l border-border overflow-y-auto">
      <div className="p-4 space-y-6">
        <div className="flex items-center justify-between">
          <h3 className="text-sm font-semibold text-text">Scoring Configuration</h3>
          <Button variant="ghost" size="sm" onClick={onClose}>✕</Button>
        </div>

        {/* Aggregation Strategy */}
        <div>
          <label className="text-xs font-medium text-text mb-2 block">Aggregation Strategy</label>
          <select
            value={config.strategy}
            onChange={(e) => onChange({ ...config, strategy: e.target.value as AggregationStrategy })}
            className="aws-select w-full text-sm"
          >
            <option value="WEIGHTED_AVERAGE">Weighted Average</option>
            <option value="MINIMUM_SCORE">Minimum Score</option>
            <option value="MAXIMUM_SCORE">Maximum Score</option>
          </select>
          <p className="text-[10px] text-text-muted mt-1">
            {config.strategy === "WEIGHTED_AVERAGE" && "Composite score = sum of (score × weight) / sum of weights"}
            {config.strategy === "MINIMUM_SCORE" && "Composite score = lowest individual check score"}
            {config.strategy === "MAXIMUM_SCORE" && "Composite score = highest individual check score"}
          </p>
        </div>

        {/* Risk Tiers */}
        <div>
          <label className="text-xs font-medium text-text mb-2 block">Risk Tiers</label>
          <div className="space-y-3">
            {config.tiers.map((tier, i) => (
              <div key={i} className="border border-border rounded-aws-control p-3 space-y-2">
                <input
                  type="text"
                  value={tier.name}
                  onChange={(e) => updateTier(i, "name", e.target.value)}
                  className="aws-input w-full text-xs"
                  placeholder="Tier name"
                />
                <div className="flex items-center gap-2">
                  <input
                    type="number"
                    min={0}
                    max={100}
                    value={tier.lowerBound}
                    onChange={(e) => updateTier(i, "lowerBound", parseInt(e.target.value) || 0)}
                    className="aws-input w-16 text-xs text-center"
                  />
                  <span className="text-xs text-text-muted">to</span>
                  <input
                    type="number"
                    min={0}
                    max={100}
                    value={tier.upperBound}
                    onChange={(e) => updateTier(i, "upperBound", parseInt(e.target.value) || 0)}
                    className="aws-input w-16 text-xs text-center"
                  />
                  <select
                    value={tier.decision}
                    onChange={(e) => updateTier(i, "decision", e.target.value)}
                    className="aws-select flex-1 text-xs"
                  >
                    <option value="APPROVE">APPROVE</option>
                    <option value="MANUAL_REVIEW">MANUAL_REVIEW</option>
                    <option value="REJECT">REJECT</option>
                  </select>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Override Rules */}
        <div>
          <div className="flex items-center justify-between mb-2">
            <label className="text-xs font-medium text-text">Override Rules</label>
            <Button variant="secondary" size="sm" onClick={addOverrideRule}>
              + Add Rule
            </Button>
          </div>
          <p className="text-[10px] text-text-muted mb-2">
            Override rules force a decision when a specific signal condition is met, regardless of the composite score.
          </p>
          <div className="space-y-3">
            {config.overrideRules.map((rule, i) => (
              <div key={rule.id} className="border border-border rounded-aws-control p-3 space-y-2">
                <div className="flex items-center justify-between">
                  <input
                    type="text"
                    value={rule.name}
                    onChange={(e) => updateRule(i, "name", e.target.value)}
                    className="aws-input flex-1 text-xs mr-2"
                    placeholder="Rule name"
                  />
                  <Button variant="ghost" size="sm" onClick={() => removeRule(i)}>
                    <Trash2 className="h-3 w-3 text-danger" />
                  </Button>
                </div>
                <div className="grid grid-cols-2 gap-2">
                  <input
                    type="text"
                    value={rule.signalKey}
                    onChange={(e) => updateRule(i, "signalKey", e.target.value)}
                    className="aws-input text-xs"
                    placeholder="Signal key"
                  />
                  <select
                    value={rule.operator}
                    onChange={(e) => updateRule(i, "operator", e.target.value)}
                    className="aws-select text-xs"
                  >
                    <option value="GT">&gt;</option>
                    <option value="GTE">&gt;=</option>
                    <option value="LT">&lt;</option>
                    <option value="LTE">&lt;=</option>
                    <option value="EQ">=</option>
                    <option value="CONTAINS">contains</option>
                  </select>
                </div>
                <div className="grid grid-cols-2 gap-2">
                  <input
                    type="text"
                    value={rule.value}
                    onChange={(e) => updateRule(i, "value", e.target.value)}
                    className="aws-input text-xs"
                    placeholder="Value"
                  />
                  <select
                    value={rule.forcedDecision}
                    onChange={(e) => updateRule(i, "forcedDecision", e.target.value)}
                    className="aws-select text-xs"
                  >
                    <option value="APPROVE">APPROVE</option>
                    <option value="MANUAL_REVIEW">MANUAL_REVIEW</option>
                    <option value="REJECT">REJECT</option>
                  </select>
                </div>
              </div>
            ))}
            {config.overrideRules.length === 0 && (
              <p className="text-xs text-text-muted italic">No override rules configured.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
