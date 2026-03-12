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
  Settings
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

const STEP_TYPES: Record<VerificationStepType, { label: string; color: string; icon: string }> = {
  id_verification: { label: "ID Verification", color: "bg-blue-500", icon: "👤" },
  address_verification: { label: "Address Verification", color: "bg-purple-500", icon: "🏠" },
  sanctions_check: { label: "Sanctions Check", color: "bg-red-500", icon: "⚠️" },
  company_check: { label: "Company Check", color: "bg-green-500", icon: "🏢" },
  credit_check: { label: "Credit Check", color: "bg-yellow-500", icon: "💳" },
  tax_check: { label: "Tax Check", color: "bg-orange-500", icon: "📊" },
  document_check: { label: "Document Check", color: "bg-indigo-500", icon: "📄" },
  decision: { label: "Decision Point", color: "bg-gray-500", icon: "🔀" },
  parallel: { label: "Parallel Execution", color: "bg-teal-500", icon: "⚡" },
  conditional: { label: "Conditional Logic", color: "bg-pink-500", icon: "❓" },
};

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
              <div className="text-center py-16">
                <GitBranch className="h-12 w-12 text-text-muted mx-auto mb-4" />
                <p className="text-text-muted">No steps added yet</p>
                <p className="text-sm text-text-muted mt-2">Select a step type from the left panel to begin</p>
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

                        <div className="flex items-center gap-2">
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

                    <div className="pt-4 border-t border-border">
                      <div className="flex items-center gap-2 mb-2">
                        <Settings className="h-4 w-4 text-text-muted" />
                        <span className="text-xs font-semibold text-text">Configuration</span>
                      </div>
                      <p className="text-xs text-text-muted">
                        Step-specific configuration options would appear here
                      </p>
                    </div>
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
