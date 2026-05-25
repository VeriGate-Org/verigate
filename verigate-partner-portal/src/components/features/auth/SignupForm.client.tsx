"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { Button } from "@/components/ui/Button";
import { AuthShell, AuthCard, AuthBrandHeader } from "./AuthShell";
import { registerPartner } from "@/lib/bff-client";
import {
  signupStep1Schema,
  signupStep2Schema,
  signupStep3Schema,
  COMPANY_TYPES,
  BILLING_PLANS,
  VERIFICATION_TYPE_OPTIONS,
  type SignupStep1Data,
  type SignupStep2Data,
  type SignupStep3Data,
  type SignupFormData,
} from "@/lib/schemas";
import { PLAN_LABELS } from "@/lib/tenant-features";

const PLAN_DESCRIPTIONS: Record<string, string> = {
  free: "Basic verifications for small teams",
  standard: "Core platform features and integrations",
  professional: "Advanced analytics and monitoring",
  enterprise: "Full platform access with premium support",
};

export function SignupForm() {
  const router = useRouter();
  const [step, setStep] = useState(1);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState<Partial<SignupFormData>>({
    verificationTypes: [],
  });

  // Step 1 form
  const step1Form = useForm<SignupStep1Data>({
    resolver: zodResolver(signupStep1Schema),
    defaultValues: {
      firstName: formData.firstName || "",
      lastName: formData.lastName || "",
      email: formData.email || "",
      companyName: formData.companyName || "",
      companyType: formData.companyType || "",
    },
  });

  // Step 2 form
  const step2Form = useForm<SignupStep2Data>({
    resolver: zodResolver(signupStep2Schema),
    defaultValues: {
      billingPlan: formData.billingPlan || undefined,
      verificationTypes: formData.verificationTypes || [],
    },
  });

  // Step 3 form
  const step3Form = useForm<SignupStep3Data>({
    resolver: zodResolver(signupStep3Schema),
    defaultValues: {
      termsAccepted: false as unknown as true,
      privacyPolicyAccepted: false as unknown as true,
    },
  });

  const handleStep1 = (data: SignupStep1Data) => {
    setFormData((prev) => ({ ...prev, ...data }));
    setStep(2);
  };

  const handleStep2 = (data: SignupStep2Data) => {
    setFormData((prev) => ({ ...prev, ...data }));
    setStep(3);
  };

  const handleStep3 = async (data: { termsAccepted: boolean; privacyPolicyAccepted: boolean }) => {
    const completeData: SignupFormData = {
      ...(formData as SignupStep1Data & SignupStep2Data),
      termsAccepted: data.termsAccepted as true,
      privacyPolicyAccepted: data.privacyPolicyAccepted as true,
    };

    setError("");
    setLoading(true);
    try {
      await registerPartner(completeData);
      setFormData(completeData);
      setStep(4);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Registration failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const stepLabels = ["Contact", "Plan", "Review", "Done"];

  return (
    <AuthShell footerText="signup">
      <AuthCard
        footer={
          step < 4 ? (
            <>
              Step {step} of 3 &middot;{" "}
              {stepLabels[step - 1]}
            </>
          ) : undefined
        }
      >
        <AuthBrandHeader />

        {/* Step indicator */}
        {step < 4 && (
          <div className="px-7 pb-2 flex items-center gap-1.5">
            {[1, 2, 3].map((s) => (
              <div
                key={s}
                className={`h-1 flex-1 rounded-full transition-colors ${
                  s <= step ? "bg-accent" : "bg-[#e9ebed]"
                }`}
              />
            ))}
          </div>
        )}

        <div className="px-7 pt-3 pb-6">
          {error && (
            <div className="mb-3.5 bg-[rgba(226,61,54,0.06)] border border-[rgba(226,61,54,0.35)] text-[#E23D36] rounded px-3 py-2 text-xs flex gap-2 items-start leading-relaxed">
              <span className="shrink-0">&times;</span>
              <span>{error}</span>
            </div>
          )}

          {/* Step 1: Contact & Company */}
          {step === 1 && (
            <form onSubmit={step1Form.handleSubmit(handleStep1)} className="flex flex-col gap-3.5">
              <div className="text-center text-sm font-semibold text-text">
                Contact &amp; Company
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label htmlFor="firstName" className="block text-[11px] font-medium text-text mb-1">
                    First name
                  </label>
                  <input
                    id="firstName"
                    type="text"
                    className="aws-input w-full rounded"
                    {...step1Form.register("firstName")}
                  />
                  {step1Form.formState.errors.firstName && (
                    <span className="text-[11px] text-[#E23D36] mt-0.5 block">
                      {step1Form.formState.errors.firstName.message}
                    </span>
                  )}
                </div>
                <div>
                  <label htmlFor="lastName" className="block text-[11px] font-medium text-text mb-1">
                    Last name
                  </label>
                  <input
                    id="lastName"
                    type="text"
                    className="aws-input w-full rounded"
                    {...step1Form.register("lastName")}
                  />
                  {step1Form.formState.errors.lastName && (
                    <span className="text-[11px] text-[#E23D36] mt-0.5 block">
                      {step1Form.formState.errors.lastName.message}
                    </span>
                  )}
                </div>
              </div>

              <div>
                <label htmlFor="signupEmail" className="block text-[11px] font-medium text-text mb-1">
                  Email address
                </label>
                <input
                  id="signupEmail"
                  type="email"
                  placeholder="you@company.com"
                  className="aws-input w-full rounded"
                  {...step1Form.register("email")}
                />
                {step1Form.formState.errors.email && (
                  <span className="text-[11px] text-[#E23D36] mt-0.5 block">
                    {step1Form.formState.errors.email.message}
                  </span>
                )}
              </div>

              <div>
                <label htmlFor="companyName" className="block text-[11px] font-medium text-text mb-1">
                  Company name
                </label>
                <input
                  id="companyName"
                  type="text"
                  className="aws-input w-full rounded"
                  {...step1Form.register("companyName")}
                />
                {step1Form.formState.errors.companyName && (
                  <span className="text-[11px] text-[#E23D36] mt-0.5 block">
                    {step1Form.formState.errors.companyName.message}
                  </span>
                )}
              </div>

              <div>
                <label htmlFor="companyType" className="block text-[11px] font-medium text-text mb-1">
                  Company type
                </label>
                <select
                  id="companyType"
                  className="aws-input w-full rounded"
                  {...step1Form.register("companyType")}
                >
                  <option value="">Select a type...</option>
                  {COMPANY_TYPES.map((type) => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
                </select>
                {step1Form.formState.errors.companyType && (
                  <span className="text-[11px] text-[#E23D36] mt-0.5 block">
                    {step1Form.formState.errors.companyType.message}
                  </span>
                )}
              </div>

              <Button variant="primary" className="w-full py-2.5 text-sm">
                Continue &rarr;
              </Button>
            </form>
          )}

          {/* Step 2: Plan & Preferences */}
          {step === 2 && (
            <form onSubmit={step2Form.handleSubmit(handleStep2)} className="flex flex-col gap-3.5">
              <div className="text-center text-sm font-semibold text-text">
                Plan &amp; Preferences
              </div>

              <div>
                <label className="block text-[11px] font-medium text-text mb-2">
                  Billing plan
                </label>
                <div className="grid grid-cols-2 gap-2">
                  {BILLING_PLANS.map((plan) => {
                    const selected = step2Form.watch("billingPlan") === plan;
                    return (
                      <label
                        key={plan}
                        className={`relative flex flex-col p-3 rounded border cursor-pointer transition-colors ${
                          selected
                            ? "border-accent bg-[rgba(0,179,217,0.06)]"
                            : "border-[#e9ebed] hover:border-[#CBD5E1]"
                        }`}
                      >
                        <input
                          type="radio"
                          value={plan}
                          className="sr-only"
                          {...step2Form.register("billingPlan")}
                        />
                        <span className="text-[12px] font-semibold text-text">
                          {PLAN_LABELS[plan]}
                        </span>
                        <span className="text-[10px] text-text-muted mt-0.5">
                          {PLAN_DESCRIPTIONS[plan]}
                        </span>
                        {selected && (
                          <span className="absolute top-2 right-2 w-3 h-3 bg-accent rounded-full flex items-center justify-center">
                            <span className="text-white text-[8px]">&check;</span>
                          </span>
                        )}
                      </label>
                    );
                  })}
                </div>
                {step2Form.formState.errors.billingPlan && (
                  <span className="text-[11px] text-[#E23D36] mt-1 block">
                    {step2Form.formState.errors.billingPlan.message}
                  </span>
                )}
              </div>

              <div>
                <label className="block text-[11px] font-medium text-text mb-2">
                  Verification types you need
                </label>
                <div className="grid grid-cols-2 gap-1.5">
                  {VERIFICATION_TYPE_OPTIONS.map((vt) => {
                    const selectedTypes = step2Form.watch("verificationTypes") || [];
                    const isChecked = selectedTypes.includes(vt.value);
                    return (
                      <label
                        key={vt.value}
                        className={`flex items-center gap-2 p-2 rounded border cursor-pointer transition-colors text-[11px] ${
                          isChecked
                            ? "border-accent bg-[rgba(0,179,217,0.06)]"
                            : "border-[#e9ebed] hover:border-[#CBD5E1]"
                        }`}
                      >
                        <input
                          type="checkbox"
                          value={vt.value}
                          className="accent-accent"
                          {...step2Form.register("verificationTypes")}
                        />
                        <span className="text-text">{vt.label}</span>
                      </label>
                    );
                  })}
                </div>
                {step2Form.formState.errors.verificationTypes && (
                  <span className="text-[11px] text-[#E23D36] mt-1 block">
                    {step2Form.formState.errors.verificationTypes.message}
                  </span>
                )}
              </div>

              <div className="flex gap-2.5">
                <Button
                  type="button"
                  variant="secondary"
                  className="flex-1 py-2.5 text-sm"
                  onClick={() => setStep(1)}
                >
                  &larr; Back
                </Button>
                <Button variant="primary" className="flex-1 py-2.5 text-sm">
                  Continue &rarr;
                </Button>
              </div>
            </form>
          )}

          {/* Step 3: Terms & Review */}
          {step === 3 && (
            <form onSubmit={step3Form.handleSubmit(handleStep3)} className="flex flex-col gap-3.5">
              <div className="text-center text-sm font-semibold text-text">
                Review &amp; Submit
              </div>

              {/* Summary */}
              <div className="rounded border border-[#e9ebed] divide-y divide-[#e9ebed]">
                <div className="px-3 py-2">
                  <div className="text-[10px] uppercase tracking-wide text-text-muted mb-1">Contact</div>
                  <div className="text-[12px] text-text">
                    {formData.firstName} {formData.lastName}
                  </div>
                  <div className="text-[11px] text-text-muted">{formData.email}</div>
                </div>
                <div className="px-3 py-2">
                  <div className="text-[10px] uppercase tracking-wide text-text-muted mb-1">Company</div>
                  <div className="text-[12px] text-text">{formData.companyName}</div>
                  <div className="text-[11px] text-text-muted">{formData.companyType}</div>
                </div>
                <div className="px-3 py-2">
                  <div className="text-[10px] uppercase tracking-wide text-text-muted mb-1">Plan</div>
                  <div className="text-[12px] text-text">
                    {formData.billingPlan ? PLAN_LABELS[formData.billingPlan] : ""}
                  </div>
                </div>
                <div className="px-3 py-2">
                  <div className="text-[10px] uppercase tracking-wide text-text-muted mb-1">Verifications</div>
                  <div className="text-[11px] text-text flex flex-wrap gap-1">
                    {(formData.verificationTypes || []).map((vt) => {
                      const label = VERIFICATION_TYPE_OPTIONS.find((o) => o.value === vt)?.label || vt;
                      return (
                        <span
                          key={vt}
                          className="inline-block px-1.5 py-0.5 bg-[rgba(0,179,217,0.08)] text-accent rounded text-[10px]"
                        >
                          {label}
                        </span>
                      );
                    })}
                  </div>
                </div>
              </div>

              {/* Terms checkboxes */}
              <div className="flex flex-col gap-2">
                <label className="text-[11px] text-text inline-flex gap-2 items-start cursor-pointer">
                  <input
                    type="checkbox"
                    className="accent-accent mt-0.5"
                    {...step3Form.register("termsAccepted")}
                  />
                  <span>
                    I agree to the{" "}
                    <span className="text-accent cursor-pointer hover:underline">
                      Terms of Service
                    </span>
                  </span>
                </label>
                {step3Form.formState.errors.termsAccepted && (
                  <span className="text-[11px] text-[#E23D36] ml-5">
                    {step3Form.formState.errors.termsAccepted.message}
                  </span>
                )}
                <label className="text-[11px] text-text inline-flex gap-2 items-start cursor-pointer">
                  <input
                    type="checkbox"
                    className="accent-accent mt-0.5"
                    {...step3Form.register("privacyPolicyAccepted")}
                  />
                  <span>
                    I agree to the{" "}
                    <span className="text-accent cursor-pointer hover:underline">
                      Privacy Policy
                    </span>
                  </span>
                </label>
                {step3Form.formState.errors.privacyPolicyAccepted && (
                  <span className="text-[11px] text-[#E23D36] ml-5">
                    {step3Form.formState.errors.privacyPolicyAccepted.message}
                  </span>
                )}
              </div>

              <div className="flex gap-2.5">
                <Button
                  type="button"
                  variant="secondary"
                  className="flex-1 py-2.5 text-sm"
                  onClick={() => setStep(2)}
                >
                  &larr; Back
                </Button>
                <Button
                  variant="primary"
                  className="flex-1 py-2.5 text-sm"
                  disabled={loading}
                >
                  {loading ? "Submitting\u2026" : "Create account"}
                </Button>
              </div>
            </form>
          )}

          {/* Step 4: Confirmation */}
          {step === 4 && (
            <div className="flex flex-col items-center gap-4 py-4">
              <div className="w-14 h-14 rounded-full bg-[rgba(22,163,74,0.1)] flex items-center justify-center">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#16A34A" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                  <polyline points="20 6 9 17 4 12" />
                </svg>
              </div>

              <div className="text-center">
                <div className="text-sm font-semibold text-text mb-1">
                  Registration successful
                </div>
                <div className="text-[12px] text-text-muted leading-relaxed max-w-[280px]">
                  We&apos;ve sent a temporary password to{" "}
                  <strong className="text-text">{formData.email}</strong>.
                  Check your email and use it to sign in.
                </div>
              </div>

              <Link href="/signin" className="w-full">
                <Button variant="primary" className="w-full py-2.5 text-sm">
                  Sign in &rarr;
                </Button>
              </Link>
            </div>
          )}
        </div>
      </AuthCard>
    </AuthShell>
  );
}
