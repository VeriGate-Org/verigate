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
  config: Record<string, unknown>;
  next?: string;
  onSuccess?: string;
  onFail?: string;
  parallel?: string[];
}

export interface Policy {
  id: string;
  name: string;
  description: string;
  version: number;
  steps: VerificationStep[];
  status: "draft" | "published" | "archived";
  createdAt: Date;
  updatedAt: Date;
  createdBy: string;
}

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
    status: "draft",
    createdAt: new Date(),
    updatedAt: new Date(),
    createdBy: "current-user",
  });

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

  const validatePolicy = (): boolean => {
    const errors: string[] = [];

    if (policy.steps.length === 0) {
      errors.push("Policy must have at least one step");
    }

    if (!policy.name || policy.name.trim() === "") {
      errors.push("Policy name is required");
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
      </div>
    </div>
  );
};
