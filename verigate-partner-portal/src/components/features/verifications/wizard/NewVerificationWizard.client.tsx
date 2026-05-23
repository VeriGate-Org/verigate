"use client";

import { useState, useMemo } from "react";
import { useRouter } from "next/navigation";
import { Shield, Loader2, CheckCircle2, XCircle, Search } from "lucide-react";
import { Modal } from "@/components/ui/Modal";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { useSubmitVerification } from "@/lib/hooks/useVerification";
import { useTenantFeatures } from "@/lib/tenant/PartnerTenantProvider";
import { getAllTypes, type VerificationTypeInfo } from "@/lib/verification-type-map";
import { config } from "@/lib/config";
import type { VerificationType } from "@/lib/types";

// ── Per-service field definitions ───────────────────────────────────

interface FieldDef {
  key: string;
  label: string;
  placeholder: string;
  required?: boolean;
  mono?: boolean;
  /** Regex the value must match to be considered valid. */
  pattern?: RegExp;
  /** "select" renders a dropdown; default is text input. */
  type?: "text" | "select";
  options?: { value: string; label: string }[];
  /** Span both columns in the 2-col grid. */
  full?: boolean;
}

const COMMON_PERSON_FIELDS: FieldDef[] = [
  { key: "firstName", label: "First name", placeholder: "John", required: true },
  { key: "lastName", label: "Last name", placeholder: "Doe", required: true },
  { key: "idNumber", label: "ID number", placeholder: "13-digit SA ID", required: true, mono: true, pattern: /^\d{13}$/ },
  { key: "dateOfBirth", label: "Date of birth", placeholder: "YYYY-MM-DD" },
];

const SERVICE_FIELDS: Partial<Record<VerificationType, FieldDef[]>> = {
  ID: COMMON_PERSON_FIELDS,
  IDENTITY: COMMON_PERSON_FIELDS,
  AVS: [
    { key: "accountHolder", label: "Account holder name", placeholder: "John Doe", required: true },
    { key: "bank", label: "Bank", placeholder: "Select bank", required: true, type: "select", options: [
      { value: "FNB", label: "FNB" },
      { value: "Standard Bank", label: "Standard Bank" },
      { value: "Absa", label: "Absa" },
      { value: "Nedbank", label: "Nedbank" },
      { value: "Capitec", label: "Capitec" },
    ]},
    { key: "accountNumber", label: "Account number", placeholder: "6-15 digit account number", required: true, mono: true, pattern: /^\d{6,15}$/ },
    { key: "branchCode", label: "Branch code", placeholder: "5-6 digit branch code", required: true, mono: true, pattern: /^\d{5,6}$/ },
  ],
  CREDIT: [
    { key: "idNumber", label: "ID number", placeholder: "13-digit SA ID", required: true, mono: true, pattern: /^\d{13}$/ },
  ],
  INCOME: [
    { key: "idNumber", label: "ID number", placeholder: "13-digit SA ID", required: true, mono: true, pattern: /^\d{13}$/ },
    { key: "employerName", label: "Employer name", placeholder: "Acme Corp", required: true },
    { key: "monthlyGross", label: "Monthly gross (ZAR)", placeholder: "25000", required: true, mono: true },
  ],
  TAX: [
    { key: "taxReference", label: "SARS tax reference", placeholder: "Tax reference number", required: true, mono: true },
    { key: "idNumber", label: "ID number", placeholder: "13-digit SA ID", required: true, mono: true, pattern: /^\d{13}$/ },
  ],
  CIPC: [
    { key: "registrationNumber", label: "Registration number", placeholder: "YYYY/NNNNNN/NN", required: true, mono: true },
    { key: "companyName", label: "Company name", placeholder: "Optional — helps narrow results" },
  ],
  DEEDS: [
    { key: "propertyId", label: "Property ID or title deed", placeholder: "T12345/2024", required: true, mono: true },
    { key: "ownerName", label: "Owner name", placeholder: "Optional — helps narrow results" },
  ],
  EMPLOYMENT: [
    { key: "subjectName", label: "Subject name", placeholder: "John Doe", required: true },
    { key: "employerName", label: "Employer name", placeholder: "Acme Corp", required: true },
    { key: "employeeNumber", label: "Employee number", placeholder: "Optional" },
  ],
  QUALIFICATION: [
    { key: "subjectName", label: "Subject name", placeholder: "John Doe", required: true },
    { key: "idNumber", label: "ID number", placeholder: "13-digit SA ID", required: true, mono: true, pattern: /^\d{13}$/ },
    { key: "institution", label: "Institution", placeholder: "University of Cape Town", required: true },
    { key: "qualification", label: "Qualification", placeholder: "BSc Computer Science", required: true },
  ],
  SANCTIONS: [
    { key: "entityName", label: "Entity name", placeholder: "John Doe or Acme Corp", required: true },
    { key: "entityType", label: "Entity type", placeholder: "Select type", required: true, type: "select", options: [
      { value: "Person", label: "Person" },
      { value: "Company", label: "Company" },
      { value: "Organization", label: "Organisation" },
      { value: "Vessel", label: "Vessel" },
    ]},
    { key: "country", label: "Country", placeholder: "Optional — e.g. South Africa" },
  ],
  NEGATIVE_NEWS: [
    { key: "fullName", label: "Full name", placeholder: "John Doe", required: true },
    { key: "idNumber", label: "ID number", placeholder: "Optional — 13-digit SA ID", mono: true },
  ],
  FRAUD_WATCHLIST: [
    { key: "fullName", label: "Full name", placeholder: "John Doe", required: true },
    { key: "idNumber", label: "ID number", placeholder: "13-digit SA ID", required: true, mono: true, pattern: /^\d{13}$/ },
  ],
  WATCHLIST: [
    { key: "entityName", label: "Entity name", placeholder: "John Doe or Acme Corp", required: true },
    { key: "entityType", label: "Entity type", placeholder: "Select type", required: true, type: "select", options: [
      { value: "Person", label: "Person" },
      { value: "Company", label: "Company" },
      { value: "Organization", label: "Organisation" },
    ]},
  ],
  DOCUMENT: [
    { key: "documentType", label: "Document type", placeholder: "Select type", required: true, type: "select", options: [
      { value: "SA_ID_CARD", label: "SA ID Card" },
      { value: "PASSPORT", label: "Passport" },
      { value: "DRIVERS_LICENCE", label: "Driver\u2019s Licence" },
      { value: "TAX_CLEARANCE", label: "Tax Clearance" },
      { value: "CIPC", label: "CIPC Registration" },
      { value: "UTILITY_BILL", label: "Utility Bill" },
    ]},
    { key: "documentNumber", label: "Document number", placeholder: "ID or document number for matching", required: true, mono: true },
  ],
  VAT_VENDOR: [
    { key: "vatNumber", label: "VAT number", placeholder: "VAT registration number", required: true, mono: true },
    { key: "entityName", label: "Entity name", placeholder: "Optional — helps narrow results" },
  ],
};

/** Fallback fields when a service type has no specific config. */
const DEFAULT_FIELDS: FieldDef[] = COMMON_PERSON_FIELDS;

function getFieldsForService(type: VerificationType): FieldDef[] {
  return SERVICE_FIELDS[type] ?? DEFAULT_FIELDS;
}

function isFieldValid(field: FieldDef, value: string): boolean {
  if (!field.required && !value) return true;
  if (field.required && !value.trim()) return false;
  if (field.pattern && !field.pattern.test(value)) return false;
  return true;
}

// ── Helpers ─────────────────────────────────────────────────────────

const CATEGORY_LABELS: Record<VerificationTypeInfo["category"], string> = {
  identity: "Identity",
  financial: "Financial",
  business: "Business",
  screening: "Screening",
  biometric: "Biometric",
  composite: "Composite",
};

function deduplicateByRoute(types: VerificationTypeInfo[]): VerificationTypeInfo[] {
  const seen = new Set<string>();
  return types.filter((t) => {
    if (seen.has(t.route)) return false;
    seen.add(t.route);
    return true;
  });
}

function StepIndicator({ n, label, current }: { n: number; label: string; current: number }) {
  const done = current > n;
  const active = current === n;
  return (
    <div className="flex items-center gap-2 flex-1">
      <div
        className={`w-6 h-6 rounded-full flex items-center justify-center text-[11px] font-semibold ${
          done
            ? "bg-[#2C974B] text-white"
            : active
              ? "bg-primary text-white"
              : "bg-surface border border-border text-text-muted"
        }`}
      >
        {done ? "\u2713" : n}
      </div>
      <span className={`text-xs ${active ? "text-text font-semibold" : "text-text-muted"}`}>
        {label}
      </span>
    </div>
  );
}

// ── Wizard ──────────────────────────────────────────────────────────

export function NewVerificationWizard({
  open,
  onClose,
}: {
  open: boolean;
  onClose: () => void;
}) {
  const router = useRouter();
  const { hasFeature } = useTenantFeatures();
  const [step, setStep] = useState(1);
  const [selectedType, setSelectedType] = useState<VerificationType | null>(null);
  const [serviceSearch, setServiceSearch] = useState("");
  const [fields, setFields] = useState<Record<string, string>>({});
  const [consent, setConsent] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  // Available services from type map, filtered by tenant features
  const availableServices = useMemo(() => {
    const all = deduplicateByRoute(getAllTypes());
    return all.filter(
      (svc) => svc.provider !== "Coming Soon" && hasFeature("core_verifications"),
    );
  }, [hasFeature]);

  const filteredServices = useMemo(() => {
    if (!serviceSearch) return availableServices;
    const q = serviceSearch.toLowerCase();
    return availableServices.filter(
      (svc) =>
        svc.label.toLowerCase().includes(q) ||
        svc.shortLabel.toLowerCase().includes(q) ||
        svc.provider.toLowerCase().includes(q),
    );
  }, [availableServices, serviceSearch]);

  const grouped = useMemo(() => {
    const groups: Record<string, VerificationTypeInfo[]> = {};
    for (const svc of filteredServices) {
      if (!groups[svc.category]) groups[svc.category] = [];
      groups[svc.category].push(svc);
    }
    return Object.entries(groups).map(([cat, services]) => ({
      category: cat as VerificationTypeInfo["category"],
      services,
    }));
  }, [filteredServices]);

  const selectedService = availableServices.find((s) => s.portalType === selectedType);
  const currentFields = selectedType ? getFieldsForService(selectedType) : [];

  const allFieldsValid = currentFields.every((f) => isFieldValid(f, fields[f.key] ?? ""));

  const mutation = useSubmitVerification({
    onSuccess: (data) => {
      setTimeout(() => {
        handleClose();
        router.push(`/verifications/${data.commandId}`);
      }, 1500);
    },
  });

  const handleClose = () => {
    if (mutation.isPending) return;
    setStep(1);
    setSelectedType(null);
    setServiceSearch("");
    setFields({});
    setConsent(false);
    setSubmitError(null);
    mutation.reset();
    onClose();
  };

  const handleServiceSelect = (type: VerificationType) => {
    if (type !== selectedType) {
      setSelectedType(type);
      setFields({}); // reset fields when service changes
    }
  };

  const setField = (key: string, value: string) => {
    setFields((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = () => {
    if (!selectedService) return;
    setSubmitError(null);
    setStep(4);

    // Build metadata from filled fields
    const metadata: Record<string, unknown> = {};
    for (const f of currentFields) {
      const val = fields[f.key];
      if (val) metadata[f.key] = val;
    }

    mutation.mutate(
      {
        verificationType: selectedService.bffType,
        originationType: "ADHOC",
        originationId: crypto.randomUUID(),
        requestedBy: config.partnerId,
        metadata,
      },
      {
        onError: (err) => {
          setSubmitError(
            err instanceof Error ? err.message : "Submission failed. Please try again.",
          );
        },
      },
    );
  };

  return (
    <Modal open={open} onClose={handleClose} title="Start a new verification" wide>
      {/* Stepper */}
      <div className="flex items-center gap-3 pb-3.5 mb-4 border-b border-[#e9ebed]">
        <StepIndicator n={1} label="Service" current={step} />
        <span className="w-6 h-px bg-border" />
        <StepIndicator n={2} label="Details" current={step} />
        <span className="w-6 h-px bg-border" />
        <StepIndicator n={3} label="Consent & Review" current={step} />
        <span className="w-6 h-px bg-border" />
        <StepIndicator n={4} label="Processing" current={step} />
      </div>

      <div className="min-h-[260px]">
        {/* Step 1 — Service selection */}
        {step === 1 && (
          <div className="space-y-3">
            <div className="flex items-center justify-between gap-3">
              <div className="text-[13px] font-semibold">Choose a service</div>
              <div className="relative w-52">
                <Search
                  size={13}
                  className="absolute left-2.5 top-1/2 -translate-y-1/2 text-text-muted pointer-events-none"
                />
                <input
                  value={serviceSearch}
                  onChange={(e) => setServiceSearch(e.target.value)}
                  placeholder="Filter services\u2026"
                  className="aws-input w-full pl-8 pr-3 py-1 text-xs rounded"
                />
              </div>
            </div>

            <div className="max-h-[320px] overflow-y-auto -mx-1 px-1 space-y-3">
              {grouped.length === 0 && (
                <div className="text-center py-8 text-xs text-text-muted">
                  No services match your search.
                </div>
              )}
              {grouped.map(({ category, services }) => (
                <div key={category}>
                  <div className="text-[10px] text-text-muted uppercase tracking-wide font-semibold mb-1.5 px-0.5">
                    {CATEGORY_LABELS[category]}
                  </div>
                  <div className="space-y-1.5">
                    {services.map((svc) => {
                      const Icon = svc.icon;
                      const selected = selectedType === svc.portalType;
                      return (
                        <button
                          key={svc.portalType}
                          onClick={() => handleServiceSelect(svc.portalType)}
                          className={`w-full flex items-center gap-3 p-2.5 border rounded-md text-left transition-all ${
                            selected
                              ? "border-accent bg-accent-soft"
                              : "border-border bg-surface hover:border-accent/40"
                          }`}
                        >
                          <div
                            className={`w-8 h-8 rounded-md flex items-center justify-center shrink-0 ${
                              selected ? "bg-accent/10 text-accent" : "bg-[#F2F3F3] text-text-muted"
                            }`}
                          >
                            <Icon className="w-4 h-4" />
                          </div>
                          <div className="flex-1 min-w-0">
                            <div className="text-[13px] font-semibold text-text truncate">
                              {svc.label}
                            </div>
                            <div className="text-[11px] text-text-muted">
                              {svc.provider}
                            </div>
                          </div>
                          <div
                            className={`w-3.5 h-3.5 rounded-full border-2 shrink-0 relative ${
                              selected ? "border-accent bg-accent" : "border-[#CBD5E1]"
                            }`}
                          >
                            {selected && (
                              <span className="absolute inset-[2px] rounded-full bg-surface" />
                            )}
                          </div>
                        </button>
                      );
                    })}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Step 2 — Service-specific fields */}
        {step === 2 && selectedService && (
          <div>
            <div className="flex items-center gap-2 mb-3">
              {(() => { const Icon = selectedService.icon; return <Icon className="w-4 h-4 text-accent" />; })()}
              <span className="text-[13px] font-semibold text-text">{selectedService.label}</span>
              <Badge variant="info" size="sm">{selectedService.provider}</Badge>
            </div>
            <div className="grid grid-cols-2 gap-3.5">
              {currentFields.map((f) => (
                <div key={f.key} className={f.full ? "col-span-2" : ""}>
                  <label className="block text-[11px] font-medium text-text mb-1">
                    {f.label}{f.required ? " *" : ""}
                  </label>
                  {f.type === "select" && f.options ? (
                    <select
                      value={fields[f.key] ?? ""}
                      onChange={(e) => setField(f.key, e.target.value)}
                      className="aws-select w-full rounded text-[13px]"
                    >
                      <option value="">{f.placeholder}</option>
                      {f.options.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                          {opt.label}
                        </option>
                      ))}
                    </select>
                  ) : (
                    <input
                      value={fields[f.key] ?? ""}
                      onChange={(e) => setField(f.key, e.target.value)}
                      placeholder={f.placeholder}
                      className={`aws-input w-full rounded ${f.mono ? "font-mono" : ""}`}
                    />
                  )}
                </div>
              ))}
              <div className="col-span-2 flex gap-2.5 p-3 bg-accent-soft border border-accent/20 rounded-md text-xs text-primary leading-relaxed">
                <Shield size={16} className="text-accent shrink-0 mt-0.5" />
                <span>
                  <b>POPIA notice:</b> the subject must be informed and have given
                  written or digital consent before submitting.
                </span>
              </div>
            </div>
          </div>
        )}

        {/* Step 3 — Consent & Review */}
        {step === 3 && selectedService && (
          <div className="space-y-3.5">
            <div className="grid grid-cols-2 gap-3.5">
              {/* Service summary */}
              <div className="bg-[#F8FAFC] border border-[#e9ebed] rounded-md p-3.5">
                <div className="text-[10px] text-text-muted uppercase tracking-wide font-semibold mb-2">
                  Service
                </div>
                <div className="text-sm font-semibold text-text">
                  {selectedService.label}
                </div>
                <div className="flex items-center gap-1.5 mt-1">
                  <Badge variant="info" size="sm">{selectedService.provider}</Badge>
                  <span className="text-[10px] text-text-muted uppercase">
                    {CATEGORY_LABELS[selectedService.category]}
                  </span>
                </div>
              </div>

              {/* Field summary */}
              <div className="bg-[#F8FAFC] border border-[#e9ebed] rounded-md p-3.5">
                <div className="text-[10px] text-text-muted uppercase tracking-wide font-semibold mb-2">
                  Details
                </div>
                <div className="space-y-1">
                  {currentFields.filter((f) => fields[f.key]).map((f) => (
                    <div key={f.key} className="flex justify-between text-xs">
                      <span className="text-text-muted">{f.label}</span>
                      <span className={`text-text font-medium truncate max-w-[140px] text-right ${f.mono ? "font-mono" : ""}`}>
                        {fields[f.key]}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
            <label
              className={`flex items-start gap-2.5 p-3 border rounded-md cursor-pointer text-xs leading-relaxed ${
                consent
                  ? "border-accent bg-accent-soft"
                  : "border-border bg-surface"
              }`}
            >
              <input
                type="checkbox"
                checked={consent}
                onChange={(e) => setConsent(e.target.checked)}
                className="accent-accent mt-0.5"
              />
              <span>
                I confirm the subject has provided POPIA-compliant consent for
                this verification, and that data will be processed per
                VeriGate&apos;s POPIA notice.
              </span>
            </label>
          </div>
        )}

        {/* Step 4 — Processing */}
        {step === 4 && (
          <div className="flex flex-col items-center justify-center py-8 text-center">
            {mutation.isPending && (
              <>
                <Loader2 size={40} className="text-accent animate-spin mb-4" />
                <div className="text-sm font-semibold text-text mb-1">
                  Submitting verification...
                </div>
                <div className="text-xs text-text-muted leading-relaxed max-w-xs">
                  Sending request to {selectedService?.provider ?? "provider"}. This
                  may take up to 30 seconds depending on response times.
                </div>
              </>
            )}

            {mutation.isSuccess && (
              <>
                <CheckCircle2 size={40} className="text-[#2C974B] mb-4" />
                <div className="text-sm font-semibold text-text mb-1">
                  Verification submitted
                </div>
                <div className="text-xs text-text-muted leading-relaxed max-w-xs">
                  Command ID:{" "}
                  <span className="font-mono text-accent">
                    {mutation.data?.commandId}
                  </span>
                </div>
                <div className="text-xs text-text-muted mt-1">
                  Redirecting to detail page...
                </div>
              </>
            )}

            {mutation.isError && (
              <>
                <XCircle size={40} className="text-[#E23D36] mb-4" />
                <div className="text-sm font-semibold text-text mb-1">
                  Submission failed
                </div>
                <div className="text-xs text-text-muted leading-relaxed max-w-xs mb-4">
                  {submitError || "An unexpected error occurred. Please try again."}
                </div>
                <div className="flex gap-2">
                  <Button
                    variant="secondary"
                    onClick={() => {
                      setStep(3);
                      mutation.reset();
                      setSubmitError(null);
                    }}
                  >
                    &larr; Back to review
                  </Button>
                  <Button variant="primary" onClick={handleSubmit}>
                    Retry &rarr;
                  </Button>
                </div>
              </>
            )}
          </div>
        )}
      </div>

      {/* Footer */}
      {step <= 3 && (
        <div className="flex justify-end items-center mt-5 pt-3.5 border-t border-[#e9ebed]">
          <div className="flex gap-2">
            <Button variant="secondary" onClick={handleClose}>
              Cancel
            </Button>
            {step > 1 && (
              <Button variant="ghost" onClick={() => setStep(step - 1)}>
                &larr; Back
              </Button>
            )}
            {step === 1 && (
              <Button
                variant="primary"
                onClick={() => setStep(2)}
                disabled={!selectedType}
              >
                Next &rarr;
              </Button>
            )}
            {step === 2 && (
              <Button
                variant="primary"
                onClick={() => setStep(3)}
                disabled={!allFieldsValid}
              >
                Next &rarr;
              </Button>
            )}
            {step === 3 && (
              <Button variant="cta" disabled={!consent} onClick={handleSubmit}>
                Start verification &rarr;
              </Button>
            )}
          </div>
        </div>
      )}
    </Modal>
  );
}
