"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import {
  ArrowLeft,
  Plus,
  ChevronRight,
  Play,
  Save,
  Rocket,
  X,
} from "lucide-react";
import { PageHeader } from "@/components/ui/PageHeader";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { cn } from "@/lib/cn";

/* ── Types ───────────────────────────────────────────────── */

interface PolicyStep {
  id: string;
  kind: string;
  service: string;
  weight: number;
  required: boolean;
  threshold: number;
}

interface ScoringTier {
  name: string;
  lowerBound: number;
  upperBound: number;
  decision: string;
  color: string;
}

interface OverrideRule {
  id: string;
  when: string;
  decision: string;
}

interface PolicyData {
  id: string;
  name: string;
  description: string;
  version: number;
  status: "published" | "draft";
  steps: PolicyStep[];
  scoringConfig: {
    strategy: string;
    tiers: ScoringTier[];
    overrideRules: OverrideRule[];
  };
}

/* ── Available services for the selector ─────────────────── */

const AVAILABLE_SERVICES = [
  "ID Verification (DHA)",
  "Sanctions & PEP",
  "Credit Bureau (TransUnion)",
  "Biometric liveness",
  "Document Verification",
  "Criminal Record (SAPS)",
  "Address Verification",
  "Negative News",
];

/* ── Mock data keyed by policy ID ────────────────────────── */

const MOCK_POLICIES: Record<string, PolicyData> = {
  pol_onboard_v3: {
    id: "pol_onboard_v3",
    name: "Standard onboarding",
    description: "KYC + sanctions + credit. Used for all new individual customers.",
    version: 3,
    status: "published",
    steps: [
      { id: "step_1", kind: "verification", service: "ID Verification (DHA)",      weight: 25, required: true,  threshold: 0.90 },
      { id: "step_2", kind: "verification", service: "Sanctions & PEP",            weight: 30, required: true,  threshold: 0.85 },
      { id: "step_3", kind: "verification", service: "Credit Bureau (TransUnion)", weight: 25, required: false, threshold: 0.70 },
      { id: "step_4", kind: "verification", service: "Biometric liveness",         weight: 20, required: false, threshold: 0.80 },
    ],
    scoringConfig: {
      strategy: "WEIGHTED_AVERAGE",
      tiers: [
        { name: "LOW_RISK",    lowerBound: 80, upperBound: 100, decision: "APPROVE",       color: "#2C974B" },
        { name: "MEDIUM_RISK", lowerBound: 50, upperBound: 79,  decision: "MANUAL_REVIEW", color: "#C28B0B" },
        { name: "HIGH_RISK",   lowerBound: 0,  upperBound: 49,  decision: "REJECT",        color: "#E23D36" },
      ],
      overrideRules: [
        { id: "or_1", when: "sanctions.matches > 0",   decision: "REJECT" },
        { id: "or_2", when: "subject.country IN OFAC", decision: "REJECT" },
      ],
    },
  },
  pol_aml_enhanced: {
    id: "pol_aml_enhanced",
    name: "Enhanced AML screening",
    description: "PEP + sanctions + adverse media. Applies to high-risk industries.",
    version: 2,
    status: "published",
    steps: [
      { id: "step_1", kind: "verification", service: "Sanctions & PEP",      weight: 30, required: true,  threshold: 0.90 },
      { id: "step_2", kind: "verification", service: "Negative News",        weight: 25, required: true,  threshold: 0.80 },
      { id: "step_3", kind: "verification", service: "ID Verification (DHA)", weight: 20, required: false, threshold: 0.85 },
      { id: "step_4", kind: "verification", service: "Address Verification", weight: 15, required: false, threshold: 0.70 },
      { id: "step_5", kind: "verification", service: "Document Verification", weight: 10, required: false, threshold: 0.75 },
    ],
    scoringConfig: {
      strategy: "WEIGHTED_AVERAGE",
      tiers: [
        { name: "LOW_RISK",    lowerBound: 80, upperBound: 100, decision: "APPROVE",       color: "#2C974B" },
        { name: "MEDIUM_RISK", lowerBound: 50, upperBound: 79,  decision: "MANUAL_REVIEW", color: "#C28B0B" },
        { name: "HIGH_RISK",   lowerBound: 0,  upperBound: 49,  decision: "REJECT",        color: "#E23D36" },
      ],
      overrideRules: [
        { id: "or_1", when: "sanctions.matches > 0",   decision: "REJECT" },
        { id: "or_2", when: "subject.country IN OFAC", decision: "REJECT" },
      ],
    },
  },
};

/* Fallback for any policyId not in mock data */
function getPolicy(policyId: string): PolicyData {
  return (
    MOCK_POLICIES[policyId] ?? {
      ...MOCK_POLICIES.pol_onboard_v3,
      id: policyId,
      name: policyId === "pol_new" ? "Untitled policy" : `Policy ${policyId}`,
      status: "draft" as const,
    }
  );
}

/* ── Decision badge variant helper ───────────────────────── */

function decisionVariant(decision: string): "success" | "warning" | "danger" {
  if (decision === "APPROVE") return "success";
  if (decision === "REJECT") return "danger";
  return "warning";
}

/* ── Component ───────────────────────────────────────────── */

export function PolicyDetailPage({ policyId }: { policyId: string }) {
  const router = useRouter();
  const [policy, setPolicy] = useState<PolicyData>(() => getPolicy(policyId));
  const [selectedStepId, setSelectedStepId] = useState<string | null>(null);

  const totalWeight = policy.steps.reduce((sum, s) => sum + s.weight, 0);
  const selectedStep = policy.steps.find((s) => s.id === selectedStepId) ?? null;

  /* ── Editing helpers ─────────────────────────────────── */

  const updateStep = (id: string, patch: Partial<PolicyStep>) => {
    setPolicy((prev) => ({
      ...prev,
      steps: prev.steps.map((s) => (s.id === id ? { ...s, ...patch } : s)),
    }));
  };

  const removeStep = (id: string) => {
    setPolicy((prev) => ({
      ...prev,
      steps: prev.steps.filter((s) => s.id !== id),
    }));
    if (selectedStepId === id) setSelectedStepId(null);
  };

  const addStep = () => {
    const newStep: PolicyStep = {
      id: `step_${Date.now()}`,
      kind: "verification",
      service: AVAILABLE_SERVICES[0],
      weight: Math.max(0, 100 - totalWeight),
      required: false,
      threshold: 0.75,
    };
    setPolicy((prev) => ({ ...prev, steps: [...prev.steps, newStep] }));
    setSelectedStepId(newStep.id);
  };

  const statusTone = policy.status === "published" ? "success" : "warning";

  return (
    <div className="space-y-aws-m">
      {/* ── Header card with colour bar ─────────────────── */}
      <Card className="overflow-hidden">
        <div className="flex h-[3px]">
          <div className="flex-[3] bg-[#E23D36]" />
          <div className="flex-[5] bg-[#1A2E4B]" />
          <div className="flex-[2] bg-accent" />
        </div>
        <div className="p-5 flex justify-between items-start">
          <div className="flex-1 min-w-0">
            <button
              onClick={() => router.push("/policies")}
              className="text-[11px] font-medium text-accent hover:underline mb-1.5 inline-flex items-center gap-1"
            >
              <ArrowLeft size={11} />
              All policies
            </button>
            <h1 className="text-[22px] font-semibold text-text tracking-tight">
              {policy.name}
            </h1>
            <p className="text-xs text-text-muted mt-1">{policy.description}</p>
            <div className="flex items-center gap-2 mt-2">
              <span className="font-mono text-[11px] text-text-muted">
                {policy.id} &middot; v{policy.version}
              </span>
              <Badge variant={statusTone}>
                {policy.status === "published" ? "Published" : "Draft"}
              </Badge>
            </div>
          </div>
          <div className="flex gap-2 shrink-0">
            <Button variant="ghost" icon={<Play size={13} />}>
              Test policy
            </Button>
            <Button variant="secondary" icon={<Save size={13} />}>
              Save draft
            </Button>
            <Button variant="cta" icon={<Rocket size={13} />}>
              Publish v{policy.version + 1}
            </Button>
          </div>
        </div>
      </Card>

      {/* ── Two-column editor ───────────────────────────── */}
      <div className="grid grid-cols-1 xl:grid-cols-[1fr_340px] gap-4 items-start">
        {/* Canvas column */}
        <div className="space-y-3.5">
          {/* Verification steps flow */}
          <Card>
            <CardHeader>
              <div className="flex justify-between items-center">
                <div>
                  <div className="text-[13px] font-semibold">Verification steps</div>
                  <div className="text-[11px] text-text-muted mt-0.5">
                    {policy.steps.length} step(s) &middot; total weight {totalWeight}/100
                  </div>
                </div>
                <Button
                  variant="secondary"
                  size="sm"
                  icon={<Plus size={11} />}
                  onClick={addStep}
                >
                  Add step
                </Button>
              </div>
            </CardHeader>
            <CardBody>
              <div className="overflow-x-auto py-2">
                <div className="flex items-center gap-1.5 min-w-fit">
                  {/* IN node */}
                  <div className="flex flex-col items-center gap-1.5">
                    <div className="w-9 h-9 rounded-full bg-[#1A2E4B] text-white flex items-center justify-center text-[11px] font-semibold">
                      IN
                    </div>
                    <span className="text-[10px] text-text-muted">Subject</span>
                  </div>

                  {policy.steps.map((s, i) => (
                    <div key={s.id} className="contents">
                      <ChevronRight size={14} className="text-border shrink-0" />
                      <button
                        onClick={() => setSelectedStepId(s.id)}
                        className={cn(
                          "min-w-[170px] rounded-md border p-3 text-left transition-all cursor-pointer",
                          selectedStepId === s.id
                            ? "bg-accent/5 border-accent"
                            : "bg-surface-alt border-border hover:border-accent/50",
                        )}
                      >
                        <div className="flex justify-between items-center mb-1.5">
                          <span className="text-[9px] font-bold text-text-muted uppercase tracking-wide">
                            Step {i + 1}
                          </span>
                          {s.required && (
                            <span className="text-[9px] font-semibold text-[#E23D36]">
                              REQUIRED
                            </span>
                          )}
                        </div>
                        <div className="text-xs font-semibold text-text">{s.service}</div>
                        <div className="mt-1.5 flex items-center gap-1.5 text-[10px] text-text-muted">
                          <span className="font-mono">w{s.weight}</span>
                          <span>&middot;</span>
                          <span className="font-mono">t{s.threshold.toFixed(2)}</span>
                        </div>
                      </button>
                    </div>
                  ))}

                  <ChevronRight size={14} className="text-border shrink-0" />

                  {/* OUT node */}
                  <div className="flex flex-col items-center gap-1.5">
                    <div className="w-9 h-9 rounded-full bg-[#2C974B] text-white flex items-center justify-center text-[11px] font-semibold">
                      OUT
                    </div>
                    <span className="text-[10px] text-text-muted">Decision</span>
                  </div>

                  {/* Add step placeholder */}
                  <button
                    onClick={addStep}
                    className="ml-2 px-3.5 py-3 border border-dashed border-border rounded-md text-accent text-[11px] font-medium hover:border-accent transition-colors cursor-pointer"
                  >
                    + Step
                  </button>
                </div>
              </div>
            </CardBody>
          </Card>

          {/* Scoring and decisions */}
          <Card>
            <CardHeader>
              <div className="text-[13px] font-semibold">Scoring &amp; decisions</div>
              <div className="text-[11px] text-text-muted mt-0.5">
                Strategy:{" "}
                <span className="font-semibold">
                  {policy.scoringConfig.strategy.replace(/_/g, " ").toLowerCase()}
                </span>
              </div>
            </CardHeader>
            <CardBody>
              {/* Tier visualisation bar */}
              <div className="relative h-7 rounded overflow-hidden mb-2"
                style={{
                  background: "linear-gradient(to right, #E23D36 0% 49%, #C28B0B 49% 79%, #2C974B 79% 100%)",
                }}
              >
                {[49, 79].map((b) => (
                  <div
                    key={b}
                    className="absolute top-0 bottom-0 w-0.5 bg-white/60"
                    style={{ left: `${b}%` }}
                  />
                ))}
                {policy.scoringConfig.tiers.map((t) => (
                  <div
                    key={t.name}
                    className="absolute flex items-center justify-center text-white text-[10px] font-semibold"
                    style={{
                      left: `${t.lowerBound}%`,
                      width: `${t.upperBound - t.lowerBound}%`,
                      top: 4,
                      bottom: 4,
                      textShadow: "0 1px 2px rgba(0,0,0,0.2)",
                    }}
                  >
                    {t.decision}
                  </div>
                ))}
              </div>

              <div className="flex justify-between text-[10px] text-text-muted font-mono mb-3">
                <span>0</span>
                <span>50</span>
                <span>100</span>
              </div>

              {policy.scoringConfig.tiers.map((t) => (
                <div
                  key={t.name}
                  className="flex items-center gap-3 py-1.5 border-t border-border-light text-xs"
                >
                  <span
                    className="w-2 h-2 rounded-full shrink-0"
                    style={{ background: t.color }}
                  />
                  <span className="flex-1 font-medium">
                    {t.name.replace(/_/g, " ")}
                  </span>
                  <span className="font-mono text-text-muted">
                    {t.lowerBound}&ndash;{t.upperBound}
                  </span>
                  <Badge variant={decisionVariant(t.decision)} size="sm">
                    {t.decision}
                  </Badge>
                </div>
              ))}
            </CardBody>
          </Card>

          {/* Override rules */}
          <Card>
            <CardHeader>
              <div className="flex justify-between items-center">
                <div className="text-[13px] font-semibold">Override rules</div>
                <Button variant="link" size="sm">+ Add rule</Button>
              </div>
            </CardHeader>
            <CardBody compact>
              {policy.scoringConfig.overrideRules.map((r) => (
                <div
                  key={r.id}
                  className="flex items-center gap-3 px-4 py-2 border-t border-border-light text-xs"
                >
                  <span className="text-[10px] px-1.5 py-0.5 bg-surface-alt rounded font-mono text-text-muted">
                    WHEN
                  </span>
                  <code className="flex-1 font-mono text-text">{r.when}</code>
                  <span className="text-[10px] px-1.5 py-0.5 bg-surface-alt rounded font-mono text-text-muted">
                    THEN
                  </span>
                  <Badge variant="danger" size="sm">{r.decision}</Badge>
                </div>
              ))}
            </CardBody>
          </Card>
        </div>

        {/* ── Inspector sidebar ──────────────────────────── */}
        <Card className="p-4 sticky top-4 self-start">
          <div className="text-[11px] font-semibold uppercase tracking-wide text-text-muted mb-3">
            {selectedStep ? "Step inspector" : "Quick stats"}
          </div>

          {selectedStep ? (
            <div className="space-y-3">
              {/* Service selector */}
              <div>
                <label className="block text-[11px] font-medium text-text mb-1">
                  Service
                </label>
                <select
                  value={selectedStep.service}
                  onChange={(e) =>
                    updateStep(selectedStep.id, { service: e.target.value })
                  }
                  className="aws-input w-full py-1.5 px-2.5 text-xs rounded"
                >
                  {AVAILABLE_SERVICES.map((x) => (
                    <option key={x} value={x}>{x}</option>
                  ))}
                </select>
              </div>

              {/* Weight slider */}
              <div>
                <label className="flex justify-between text-[11px] font-medium text-text mb-1">
                  Weight
                  <span className="font-mono text-text-muted">{selectedStep.weight}</span>
                </label>
                <input
                  type="range"
                  min={0}
                  max={50}
                  value={selectedStep.weight}
                  onChange={(e) =>
                    updateStep(selectedStep.id, { weight: parseInt(e.target.value) })
                  }
                  className="w-full accent-accent"
                />
              </div>

              {/* Threshold slider */}
              <div>
                <label className="flex justify-between text-[11px] font-medium text-text mb-1">
                  Threshold
                  <span className="font-mono text-text-muted">
                    {selectedStep.threshold.toFixed(2)}
                  </span>
                </label>
                <input
                  type="range"
                  min={0}
                  max={1}
                  step={0.01}
                  value={selectedStep.threshold}
                  onChange={(e) =>
                    updateStep(selectedStep.id, {
                      threshold: parseFloat(e.target.value),
                    })
                  }
                  className="w-full accent-accent"
                />
              </div>

              {/* Required checkbox */}
              <label className="flex items-center gap-2 text-xs cursor-pointer">
                <input
                  type="checkbox"
                  checked={selectedStep.required}
                  onChange={(e) =>
                    updateStep(selectedStep.id, { required: e.target.checked })
                  }
                  className="accent-accent"
                />
                Required step (a fail here rejects regardless of total score)
              </label>

              {/* Remove step */}
              <Button
                variant="ghost"
                size="sm"
                className="text-[#E23D36] w-full"
                icon={<X size={12} />}
                onClick={() => removeStep(selectedStep.id)}
              >
                Remove step
              </Button>
            </div>
          ) : (
            <div className="space-y-2.5">
              {[
                { label: "Steps", value: String(policy.steps.length), warn: false },
                { label: "Total weight", value: `${totalWeight}/100`, warn: totalWeight !== 100 },
                { label: "Required steps", value: String(policy.steps.filter((s) => s.required).length), warn: false },
                { label: "Tiers", value: String(policy.scoringConfig.tiers.length), warn: false },
                { label: "Override rules", value: String(policy.scoringConfig.overrideRules.length), warn: false },
              ].map((s) => (
                <div
                  key={s.label}
                  className="flex justify-between py-1.5 border-b border-border-light text-xs"
                >
                  <span className="text-text-muted">{s.label}</span>
                  <span
                    className={cn(
                      "font-semibold font-mono",
                      s.warn ? "text-[#E23D36]" : "text-text",
                    )}
                  >
                    {s.value}
                  </span>
                </div>
              ))}
              <div className="mt-2 p-2.5 bg-surface-alt rounded text-[11px] text-text-muted leading-relaxed">
                <span className="font-semibold text-[#1A2E4B]">Tip:</span> click any step
                in the canvas to edit weights, thresholds, and required behaviour. Total
                weight should sum to 100 for weighted-average scoring.
              </div>
            </div>
          )}
        </Card>
      </div>
    </div>
  );
}
