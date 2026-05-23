"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { PageHeader } from "@/components/ui/PageHeader";
import { Card, CardHeader } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Modal } from "@/components/ui/Modal";
import { useSubmitVerification } from "@/lib/hooks/useVerification";
import { config } from "@/lib/config";
import {
  Plus,
  FileSearch,
  UserCheck,
  Shield,
  CheckCircle,
  Download,
  ChevronRight,
  User,
  Loader2,
  XCircle,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface FunnelStage {
  id: string;
  label: string;
  count: number;
  color: string;
  icon: LucideIcon;
}

interface PhoneStep {
  n: number;
  title: string;
  sub: string;
  cta: string;
  dotIdx: number;
  done?: boolean;
}

interface KycRow {
  id: string;
  subject: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  updated: string;
}

/* ------------------------------------------------------------------ */
/*  Data                                                               */
/* ------------------------------------------------------------------ */

const FUNNEL: FunnelStage[] = [
  { id: "init", label: "Initiated", count: 412, color: "#1A2E4B", icon: Plus },
  { id: "id", label: "ID document", count: 388, color: "#1A2E4B", icon: FileSearch },
  { id: "live", label: "Liveness & selfie", count: 361, color: "#00B3D9", icon: UserCheck },
  { id: "dha", label: "DHA confirmed", count: 348, color: "#00B3D9", icon: Shield },
  { id: "verified", label: "Verified", count: 332, color: "#2C974B", icon: CheckCircle },
];

const PHONE_STEPS: PhoneStep[] = [
  { n: 1, title: "Verify your identity", sub: "We need to confirm who you are. This takes about 2 minutes.", cta: "Get started", dotIdx: 0 },
  { n: 2, title: "Capture your ID", sub: "Hold your green ID book or smart ID card flat in good light.", cta: "Capture", dotIdx: 1 },
  { n: 3, title: "Take a quick selfie", sub: "Look at the camera. We use this to confirm the ID is yours.", cta: "Take selfie", dotIdx: 2 },
  { n: 4, title: "Verified.", sub: "Thanks \u2014 your identity has been verified with the Department of Home Affairs.", cta: "Done", dotIdx: 3, done: true },
];

const KPI_STATS = [
  { label: "KYC checks this week", value: "412", sub: "+18% WoW", tone: "#2C974B" },
  { label: "Pass rate", value: "94.2%", sub: "Industry avg 91%", tone: "#2C974B" },
  { label: "Avg. time to verify", value: "2.4m", sub: "P95 under 9m", tone: "#00B3D9" },
  { label: "Drop-off in funnel", value: "19.4%", sub: "\u22122.1pt vs last week", tone: "#2C974B" },
];

const KYC_ROWS: KycRow[] = [
  { id: "KYC-2026-0143", subject: "Lerato Mokoena", status: "success", statusLabel: "Verified", updated: "2 min ago" },
  { id: "KYC-2026-0142", subject: "Jane Smith", status: "success", statusLabel: "Verified", updated: "14 min ago" },
  { id: "KYC-2026-0141", subject: "Mandla Tshabalala", status: "warning", statusLabel: "Review", updated: "1 hr ago" },
  { id: "KYC-2026-0140", subject: "Pieter van der Merwe", status: "success", statusLabel: "Verified", updated: "2 hr ago" },
  { id: "KYC-2026-0139", subject: "Sipho Dlamini", status: "danger", statusLabel: "Failed", updated: "3 hr ago" },
];

const WORKFLOW_ROWS_STATIC = [
  { k: "Verification sources", v: "DHA \u00B7 SAPS \u00B7 TransUnion \u00B7 CIPC" },
  { k: "Required documents", v: "SA ID book or smart ID card" },
  { k: "Liveness model", v: "Active prompt-based (eye blink + head turn)" },
  { k: "Consent retention", v: "7 years (POPIA-recommended)" },
];

/* ------------------------------------------------------------------ */
/*  Phone screen component                                             */
/* ------------------------------------------------------------------ */

function PhoneScreen({ step }: { step: number }) {
  const s = PHONE_STEPS[step];
  return (
    <div className="flex-1 flex flex-col" style={{ padding: "24px 18px 18px" }}>
      {/* Brand row */}
      <div className="flex items-center gap-1.5 mb-4">
        <svg width="16" height="18" viewBox="20 25 112 122">
          <path fill="#E23D36" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z" />
          <path d="M46 84 L63 102 L106 58" fill="none" stroke="#FFFFFF" strokeWidth="13" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
        <span className="text-xs font-medium text-primary tracking-tight">VeriGate</span>
      </div>

      {/* Step content */}
      <div className="flex-1 flex flex-col">
        {/* Illustration */}
        <div
          className="h-[150px] rounded-xl mb-4 flex items-center justify-center relative overflow-hidden"
          style={{
            background: s.done
              ? "linear-gradient(135deg, rgba(44,151,75,0.10), rgba(44,151,75,0.02))"
              : "rgba(0,179,217,0.06)",
          }}
        >
          {step === 0 && <Shield size={56} className="text-accent" strokeWidth={1.5} />}
          {step === 1 && (
            <div className="w-[150px] h-[92px] bg-primary rounded-lg relative overflow-hidden flex flex-col" style={{ padding: 8 }}>
              <div className="flex gap-1.5 items-center mb-1">
                <span style={{ fontSize: 6, color: "#fff", fontWeight: 700, letterSpacing: "0.04em" }}>REPUBLIC OF SOUTH AFRICA</span>
              </div>
              <div className="flex gap-1.5 flex-1">
                <div className="w-9 h-11 rounded-sm" style={{ background: "rgba(255,255,255,0.12)" }} />
                <div className="flex-1 flex flex-col gap-[3px]">
                  <div className="h-1 rounded-sm" style={{ width: "70%", background: "rgba(255,255,255,0.5)" }} />
                  <div className="h-1 rounded-sm" style={{ width: "50%", background: "rgba(255,255,255,0.3)" }} />
                  <div className="h-1 rounded-sm" style={{ width: "60%", background: "rgba(255,255,255,0.3)" }} />
                  <div className="h-1 rounded-sm" style={{ width: "80%", background: "rgba(255,255,255,0.3)" }} />
                </div>
              </div>
              <span className="font-mono text-accent mt-1" style={{ fontSize: 7 }}>9001015800087</span>
              {/* Scanner sweep */}
              <div
                className="absolute left-0 right-0 h-0.5"
                style={{ top: 40, background: "linear-gradient(90deg, transparent, #00B3D9, transparent)", boxShadow: "0 0 8px #00B3D9" }}
              />
            </div>
          )}
          {step === 2 && (
            <div className="relative w-[100px] h-[100px] rounded-full bg-[#F2F3F3] flex items-center justify-center">
              <User size={48} className="text-text-muted" strokeWidth={1.5} />
              <div className="absolute -inset-1.5 rounded-full border-2 border-dashed border-accent animate-spin" style={{ animationDuration: "8s" }} />
            </div>
          )}
          {step === 3 && (
            <div
              className="w-[72px] h-[72px] rounded-full bg-[#2C974B] flex items-center justify-center"
              style={{ boxShadow: "0 0 30px rgba(44,151,75,0.5)" }}
            >
              <CheckCircle size={40} className="text-white" strokeWidth={2.5} />
            </div>
          )}
        </div>

        <div className="text-base font-semibold text-text mb-1.5 tracking-tight">{s.title}</div>
        <div className="text-[11px] text-text-muted leading-relaxed mb-3">{s.sub}</div>

        {step === 3 && (
          <div className="rounded-md p-2 mb-3" style={{ background: "rgba(44,151,75,0.08)", border: "1px solid rgba(44,151,75,0.2)" }}>
            <div className="text-[9px] text-text-muted uppercase tracking-wide font-semibold">Reference</div>
            <div className="font-mono text-[10px] text-text mt-0.5">VG-2026-0143</div>
          </div>
        )}

        {/* Progress dots */}
        <div className="flex gap-1 justify-center mb-3.5">
          {PHONE_STEPS.map((_, i) => (
            <span
              key={i}
              className="h-1.5 rounded-full transition-all duration-200"
              style={{
                width: i === s.dotIdx ? 16 : 6,
                background: i <= s.dotIdx ? "#00B3D9" : "#E2E8F0",
              }}
            />
          ))}
        </div>

        <button
          className="w-full py-[11px] text-white border-none rounded-lg text-[13px] font-semibold cursor-pointer"
          style={{ background: s.done ? "#2C974B" : "#1A2E4B" }}
        >
          {s.cta}
        </button>
      </div>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  KYC Wizard                                                         */
/* ------------------------------------------------------------------ */

const KYC_CHECKS = [
  { label: "Home Affairs ID", provider: "DHA", icon: Shield },
  { label: "Liveness & Selfie", provider: "Biometric", icon: UserCheck },
  { label: "Sanctions & PEP", provider: "OpenSanctions", icon: FileSearch },
  { label: "Basic Credit", provider: "TransUnion", icon: CheckCircle },
];

function KycWizard({ open, onClose }: { open: boolean; onClose: () => void }) {
  const router = useRouter();
  const [step, setStep] = useState<"form" | "processing">("form");
  const [subject, setSubject] = useState({ firstName: "", lastName: "", idNumber: "", dateOfBirth: "" });
  const [biometric, setBiometric] = useState(true);
  const [consent, setConsent] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const mutation = useSubmitVerification({
    onSuccess: (data) => {
      setTimeout(() => {
        handleClose();
        router.push(`/verifications/${data.commandId}`);
      }, 1500);
    },
  });

  const valid =
    subject.firstName.trim().length >= 2 &&
    subject.lastName.trim().length >= 2 &&
    /^\d{13}$/.test(subject.idNumber) &&
    consent;

  const handleClose = () => {
    if (mutation.isPending) return;
    setStep("form");
    setSubject({ firstName: "", lastName: "", idNumber: "", dateOfBirth: "" });
    setBiometric(true);
    setConsent(false);
    setSubmitError(null);
    mutation.reset();
    onClose();
  };

  const handleSubmit = () => {
    setSubmitError(null);
    setStep("processing");

    const metadata: Record<string, unknown> = {
      firstName: subject.firstName,
      lastName: subject.lastName,
      idNumber: subject.idNumber,
      bundle: "KYC",
      biometric,
    };
    if (subject.dateOfBirth) metadata.dateOfBirth = subject.dateOfBirth;

    mutation.mutate(
      {
        verificationType: "IDENTITY_VERIFICATION",
        originationType: "ADHOC",
        originationId: crypto.randomUUID(),
        requestedBy: config.partnerId,
        metadata,
      },
      {
        onError: (err) => {
          setSubmitError(err instanceof Error ? err.message : "Submission failed.");
        },
      },
    );
  };

  return (
    <Modal open={open} onClose={handleClose} title="Start KYC verification" wide>
      {step === "form" && (
        <>
          {/* Bundled checks preview */}
          <div className="mb-4">
            <div className="text-[10px] text-text-muted uppercase tracking-wide font-semibold mb-2">
              This bundle runs the following checks
            </div>
            <div className="grid grid-cols-2 gap-2">
              {KYC_CHECKS.map((c) => {
                const Icon = c.icon;
                return (
                  <div key={c.label} className="flex items-center gap-2.5 p-2.5 bg-[#F8FAFC] border border-[#e9ebed] rounded-md">
                    <div className="w-7 h-7 rounded-md bg-accent/10 flex items-center justify-center shrink-0">
                      <Icon size={14} className="text-accent" />
                    </div>
                    <div>
                      <div className="text-[12px] font-semibold text-text">{c.label}</div>
                      <div className="text-[10px] text-text-muted">{c.provider}</div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Subject fields */}
          <div className="text-[10px] text-text-muted uppercase tracking-wide font-semibold mb-2">
            Subject details
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-[11px] font-medium text-text mb-1">First name *</label>
              <input
                value={subject.firstName}
                onChange={(e) => setSubject({ ...subject, firstName: e.target.value })}
                placeholder="John"
                className="aws-input w-full rounded"
              />
            </div>
            <div>
              <label className="block text-[11px] font-medium text-text mb-1">Last name *</label>
              <input
                value={subject.lastName}
                onChange={(e) => setSubject({ ...subject, lastName: e.target.value })}
                placeholder="Doe"
                className="aws-input w-full rounded"
              />
            </div>
            <div>
              <label className="block text-[11px] font-medium text-text mb-1">ID number *</label>
              <input
                value={subject.idNumber}
                onChange={(e) => setSubject({ ...subject, idNumber: e.target.value })}
                placeholder="13-digit SA ID"
                className="aws-input w-full rounded font-mono"
              />
            </div>
            <div>
              <label className="block text-[11px] font-medium text-text mb-1">Date of birth</label>
              <input
                value={subject.dateOfBirth}
                onChange={(e) => setSubject({ ...subject, dateOfBirth: e.target.value })}
                placeholder="YYYY-MM-DD"
                className="aws-input w-full rounded"
              />
            </div>
          </div>

          {/* Biometric toggle */}
          <label className="flex items-center gap-2 p-2.5 mt-3 bg-[#F8FAFC] rounded-md text-xs cursor-pointer border border-[#e9ebed]">
            <input
              type="checkbox"
              checked={biometric}
              onChange={(e) => setBiometric(e.target.checked)}
              className="accent-accent"
            />
            <span className="flex-1">Include biometric capture (face liveness + selfie match)</span>
          </label>

          {/* POPIA consent */}
          <label
            className={`flex items-start gap-2.5 p-3 mt-3 border rounded-md cursor-pointer text-xs leading-relaxed ${
              consent ? "border-accent bg-accent-soft" : "border-border bg-surface"
            }`}
          >
            <input
              type="checkbox"
              checked={consent}
              onChange={(e) => setConsent(e.target.checked)}
              className="accent-accent mt-0.5"
            />
            <span>
              I confirm the subject has provided POPIA-compliant consent for this KYC
              verification, and that data will be processed per VeriGate&apos;s POPIA notice.
            </span>
          </label>

          {/* Footer */}
          <div className="flex justify-end items-center mt-5 pt-3.5 border-t border-[#e9ebed]">
            <div className="flex gap-2">
              <Button variant="secondary" onClick={handleClose}>Cancel</Button>
              <Button variant="cta" disabled={!valid} onClick={handleSubmit}>
                Start KYC &rarr;
              </Button>
            </div>
          </div>
        </>
      )}

      {step === "processing" && (
        <div className="flex flex-col items-center justify-center py-10 text-center">
          {mutation.isPending && (
            <>
              <Loader2 size={40} className="text-accent animate-spin mb-4" />
              <div className="text-sm font-semibold text-text mb-1">Running KYC checks...</div>
              <div className="text-xs text-text-muted leading-relaxed max-w-xs">
                Running ID verification, liveness, sanctions screening, and credit check.
                This may take up to 30 seconds.
              </div>
            </>
          )}
          {mutation.isSuccess && (
            <>
              <CheckCircle size={40} className="text-[#2C974B] mb-4" />
              <div className="text-sm font-semibold text-text mb-1">KYC submitted</div>
              <div className="text-xs text-text-muted">
                Command ID: <span className="font-mono text-accent">{mutation.data?.commandId}</span>
              </div>
              <div className="text-xs text-text-muted mt-1">Redirecting to detail page...</div>
            </>
          )}
          {mutation.isError && (
            <>
              <XCircle size={40} className="text-[#E23D36] mb-4" />
              <div className="text-sm font-semibold text-text mb-1">Submission failed</div>
              <div className="text-xs text-text-muted leading-relaxed max-w-xs mb-4">
                {submitError || "An unexpected error occurred."}
              </div>
              <div className="flex gap-2">
                <Button variant="secondary" onClick={() => { setStep("form"); mutation.reset(); setSubmitError(null); }}>
                  &larr; Back
                </Button>
                <Button variant="primary" onClick={handleSubmit}>Retry &rarr;</Button>
              </div>
            </>
          )}
        </div>
      )}
    </Modal>
  );
}

/* ------------------------------------------------------------------ */
/*  Main KYC page                                                      */
/* ------------------------------------------------------------------ */

export function KycPage() {
  const [phoneStep, setPhoneStep] = useState(1);
  const [biometricStrict, setBiometricStrict] = useState(true);
  const [retryEnabled, setRetryEnabled] = useState(true);
  const [kycWizardOpen, setKycWizardOpen] = useState(false);

  return (
    <div className="space-y-4">
      {/* Header */}
      <PageHeader
        category="Identity & Personal"
        title="KYC Verification"
        description="Automated identity verification with document scanning, liveness detection, and biometric matching against DHA records."
        actions={
          <div className="flex gap-2">
            <Button variant="secondary" icon={<Download size={13} />}>KYC report</Button>
            <Button variant="cta" icon={<Plus size={13} />} onClick={() => setKycWizardOpen(true)}>Start KYC &rarr;</Button>
          </div>
        }
      />

      {/* KPI cards */}
      <div className="grid grid-cols-4 gap-3.5 max-lg:grid-cols-2">
        {KPI_STATS.map((s) => (
          <div key={s.label} className="console-card p-4">
            <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">{s.label}</div>
            <div className="text-[26px] font-bold text-text mt-1 leading-tight">{s.value}</div>
            <div className="text-[11px] font-semibold mt-1" style={{ color: s.tone }}>{s.sub}</div>
          </div>
        ))}
      </div>

      {/* Funnel + Phone preview */}
      <div className="grid grid-cols-[1.6fr_1fr] gap-4 items-stretch max-lg:grid-cols-1">
        {/* KYC Funnel */}
        <Card className="flex flex-col">
          <CardHeader>
            <div className="flex items-center justify-between w-full">
              <div>
                <div className="text-sm font-semibold">KYC funnel</div>
                <div className="text-[11px] text-text-muted mt-0.5">
                  This week &middot; 412 initiated &rarr; 332 verified &middot; 80.6% completion
                </div>
              </div>
              <Button variant="link">View raw events &rarr;</Button>
            </div>
          </CardHeader>
          <div className="flex-1 px-[18px] py-[22px] flex flex-col gap-2.5">
            {FUNNEL.map((stage, i) => {
              const pct = (stage.count / FUNNEL[0].count) * 100;
              const drop =
                i > 0
                  ? (((FUNNEL[i - 1].count - stage.count) / FUNNEL[i - 1].count) * 100).toFixed(1)
                  : null;
              const Icon = stage.icon;
              return (
                <div key={stage.id}>
                  {drop && parseFloat(drop) > 0 && (
                    <div className="flex items-center gap-1.5 ml-[30px] mb-1 text-[10px] text-[#E23D36] font-semibold">
                      <span className="w-px h-2 bg-[#E2E8F0]" />
                      <span>&darr; {drop}% drop-off</span>
                    </div>
                  )}
                  <div className="flex items-center gap-3">
                    <div
                      className="w-7 h-7 rounded-md flex items-center justify-center shrink-0"
                      style={{ background: `${stage.color}15` }}
                    >
                      <Icon size={14} style={{ color: stage.color }} />
                    </div>
                    <div className="flex-1 relative h-[30px]">
                      <div className="absolute inset-0 bg-[#F8FAFC] rounded" />
                      <div
                        className="absolute top-0 bottom-0 left-0 rounded transition-all duration-400"
                        style={{ width: `${pct}%`, background: stage.color, opacity: 0.92 }}
                      />
                      <div className="relative h-full flex items-center justify-between px-3 text-xs font-semibold">
                        <span className={pct > 35 ? "text-white" : "text-text"}>{stage.label}</span>
                        <span className="font-mono text-[11px] font-bold text-primary">{stage.count}</span>
                      </div>
                    </div>
                    <span className="w-12 text-right text-[11px] text-text-muted font-mono">{pct.toFixed(1)}%</span>
                  </div>
                </div>
              );
            })}
          </div>
        </Card>

        {/* Phone preview */}
        <div className="rounded-aws-container relative overflow-hidden flex flex-col" style={{ background: "linear-gradient(160deg, #0F1A2E 0%, #1A2E4B 60%, #1a3a5c 100%)" }}>
          <div
            className="absolute inset-0"
            style={{
              backgroundImage: "radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)",
              backgroundSize: "20px 20px",
            }}
          />
          <div className="relative px-4 pt-5 mb-3.5 flex items-center justify-between">
            <div>
              <div className="text-[10px] text-accent uppercase tracking-widest font-semibold">Subject preview</div>
              <div className="text-[13px] text-white font-semibold mt-[3px]">What your customer sees</div>
            </div>
            <span className="text-[10px] font-mono" style={{ color: "rgba(255,255,255,0.6)" }}>iOS Safari</span>
          </div>
          <div className="relative flex-1 flex items-center justify-center pb-5">
            <div className="flex flex-col items-center gap-2.5">
              {/* iPhone bezel */}
              <div
                className="relative"
                style={{
                  width: 260,
                  height: 510,
                  background: "#1A1A2E",
                  borderRadius: 36,
                  padding: 10,
                  boxShadow: "0 20px 40px -10px rgba(0,28,36,0.4), inset 0 0 0 2px rgba(255,255,255,0.06)",
                }}
              >
                <div className="w-full h-full rounded-[28px] bg-white overflow-hidden relative flex flex-col">
                  {/* Notch */}
                  <div
                    className="absolute top-0 left-1/2 -translate-x-1/2 z-10"
                    style={{ width: 90, height: 22, background: "#1A1A2E", borderRadius: "0 0 14px 14px" }}
                  />
                  {/* Status bar */}
                  <div className="h-8 flex items-center justify-between text-[10px] font-semibold text-text" style={{ padding: "6px 22px 0" }}>
                    <span>9:41</span>
                    <span />
                  </div>
                  <PhoneScreen step={phoneStep} />
                </div>
              </div>
              {/* Step navigation */}
              <div className="flex items-center gap-2">
                <button
                  onClick={() => setPhoneStep(Math.max(0, phoneStep - 1))}
                  disabled={phoneStep === 0}
                  className="w-7 h-7 rounded-full border border-border bg-white text-primary disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer text-sm"
                >
                  &lsaquo;
                </button>
                <span className="text-[11px] text-text-muted min-w-[70px] text-center">
                  Step {phoneStep + 1} of {PHONE_STEPS.length}
                </span>
                <button
                  onClick={() => setPhoneStep(Math.min(PHONE_STEPS.length - 1, phoneStep + 1))}
                  disabled={phoneStep === PHONE_STEPS.length - 1}
                  className="w-7 h-7 rounded-full border border-border bg-white text-primary disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer text-sm"
                >
                  &rsaquo;
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Workflow config + Recent KYC */}
      <div className="grid grid-cols-[1fr_1.6fr] gap-4 max-lg:grid-cols-1">
        {/* Workflow configuration */}
        <Card>
          <CardHeader>
            <div>
              <div className="text-sm font-semibold">Workflow configuration</div>
              <div className="text-[11px] text-text-muted mt-0.5">Default policy for new KYC checks</div>
            </div>
          </CardHeader>
          <div className="py-1.5">
            {[
              ...WORKFLOW_ROWS_STATIC.slice(0, 2),
              { k: "Biometric threshold", v: biometricStrict ? "0.90 (strict)" : "0.78 (lenient)" },
              WORKFLOW_ROWS_STATIC[2],
              { k: "Retry policy", v: retryEnabled ? "3 attempts \u00B7 5 min lockout" : "Disabled \u2014 single attempt" },
              WORKFLOW_ROWS_STATIC[3],
            ].map((r) => (
              <div key={r.k} className="flex justify-between gap-3 px-[18px] py-2 border-b border-[#f1f5f9] text-xs">
                <span className="text-text-muted">{r.k}</span>
                <span className="text-text font-medium text-right">{r.v}</span>
              </div>
            ))}
          </div>
          <div className="px-[18px] py-2.5 bg-[#F8FAFC] flex flex-wrap gap-1.5 items-center">
            <button
              onClick={() => setBiometricStrict(!biometricStrict)}
              className="px-2.5 py-1 text-[11px] rounded-full border border-border bg-white text-text cursor-pointer hover:bg-[#F8FAFC]"
            >
              Toggle biometric strictness
            </button>
            <button
              onClick={() => setRetryEnabled(!retryEnabled)}
              className="px-2.5 py-1 text-[11px] rounded-full border border-border bg-white text-text cursor-pointer hover:bg-[#F8FAFC]"
            >
              Toggle retries
            </button>
            <Button variant="link" className="ml-auto text-[11px]">Edit workflow &rarr;</Button>
          </div>
        </Card>

        {/* Recent KYC verifications */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between w-full">
              <div>
                <div className="text-sm font-semibold">Recent KYC verifications</div>
                <div className="text-[11px] text-text-muted mt-0.5">{KYC_ROWS.length} most recent</div>
              </div>
              <Button variant="link">View all &rarr;</Button>
            </div>
          </CardHeader>
          <div className="overflow-x-auto">
            <table className="w-full border-collapse text-xs">
              <thead>
                <tr className="bg-[#F2F3F3]">
                  {["ID", "Subject", "Status", "Updated", ""].map((h) => (
                    <th
                      key={h}
                      className="px-3.5 py-2 text-left text-[10px] text-text-muted uppercase tracking-wide font-semibold border-b border-border"
                    >
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {KYC_ROWS.map((r) => (
                  <tr
                    key={r.id}
                    className="cursor-pointer transition-colors hover:bg-[#F8FAFC]"
                  >
                    <td className="px-3.5 py-2.5 border-b border-border font-mono text-[11px] text-accent">{r.id}</td>
                    <td className="px-3.5 py-2.5 border-b border-border">{r.subject}</td>
                    <td className="px-3.5 py-2.5 border-b border-border">
                      <Badge variant={r.status}>{r.statusLabel}</Badge>
                    </td>
                    <td className="px-3.5 py-2.5 border-b border-border text-text-muted">{r.updated}</td>
                    <td className="px-3.5 py-2.5 border-b border-border text-right">
                      <ChevronRight size={12} className="text-text-muted" />
                    </td>
                  </tr>
                ))}
                {KYC_ROWS.length === 0 && (
                  <tr>
                    <td colSpan={5} className="px-6 py-6 text-center text-text-muted text-xs">
                      No KYC verifications yet &mdash; start your first.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </Card>
      </div>

      <KycWizard open={kycWizardOpen} onClose={() => setKycWizardOpen(false)} />
    </div>
  );
}
