"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useForm } from "react-hook-form";
import { respondToNewPasswordChallenge, type AuthTokens } from "@/lib/auth/cognito-client";
import { Button } from "@/components/ui/Button";
import { AuthShell, AuthCard, AuthBrandHeader } from "./AuthShell";

interface SetPasswordFields {
  newPassword: string;
  confirmPassword: string;
}

const PASSWORD_RULES = [
  { label: "At least 12 characters", test: (p: string) => p.length >= 12 },
  { label: "One uppercase letter", test: (p: string) => /[A-Z]/.test(p) },
  { label: "One lowercase letter", test: (p: string) => /[a-z]/.test(p) },
  { label: "One number", test: (p: string) => /[0-9]/.test(p) },
  { label: "One special character", test: (p: string) => /[^A-Za-z0-9]/.test(p) },
];

export function SetPasswordForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [session, setSession] = useState("");
  const [email, setEmail] = useState("");

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<SetPasswordFields>({
    defaultValues: { newPassword: "", confirmPassword: "" },
  });

  const watchedPassword = watch("newPassword");

  useEffect(() => {
    const emailParam = searchParams.get("email") || "";
    try {
      const raw = sessionStorage.getItem("verigate-new-password-session");
      if (raw) {
        const data = JSON.parse(raw);
        setSession(data.session || "");
        setEmail(data.email || emailParam);
        return;
      }
    } catch {
      // ignore
    }
    if (!emailParam) {
      router.replace("/signin");
    } else {
      setEmail(emailParam);
    }
  }, [searchParams, router]);

  const onSubmit = async (data: SetPasswordFields) => {
    if (!session) {
      setError("Session expired. Please sign in again.");
      return;
    }

    setError("");
    setLoading(true);
    try {
      const tokens: AuthTokens = await respondToNewPasswordChallenge(
        session,
        email,
        data.newPassword,
      );
      // Save tokens to sessionStorage
      sessionStorage.setItem(
        "verigate-auth",
        JSON.stringify({
          accessToken: tokens.accessToken,
          idToken: tokens.idToken,
          refreshToken: tokens.refreshToken,
          expiresAt: Date.now() + tokens.expiresIn * 1000,
        }),
      );
      // Clean up the challenge session
      sessionStorage.removeItem("verigate-new-password-session");
      // Force a full page load so AuthProvider picks up the new tokens
      window.location.href = "/dashboard";
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Failed to set password. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  };

  if (!email) return null;

  return (
    <AuthShell footerText="setPassword">
      <AuthCard
        footer={
          <>
            Secure sign-in protected by Cognito &middot; POPIA-compliant &middot;
            v2026.04
          </>
        }
      >
        <form onSubmit={handleSubmit(onSubmit)}>
          <AuthBrandHeader />
          <div className="px-7 pt-5 pb-6 flex flex-col gap-3.5">
            <div className="text-center text-sm font-semibold text-text">
              Set your password
            </div>
            <div className="text-center text-[11px] text-text-muted">
              Create a permanent password for <strong>{email}</strong>
            </div>

            {error && (
              <div className="bg-[rgba(226,61,54,0.06)] border border-[rgba(226,61,54,0.35)] text-[#E23D36] rounded px-3 py-2 text-xs flex gap-2 items-start leading-relaxed">
                <span className="shrink-0">&times;</span>
                <span>{error}</span>
              </div>
            )}

            <div>
              <label
                htmlFor="newPassword"
                className="block text-[11px] font-medium text-text mb-1"
              >
                New password
              </label>
              <input
                id="newPassword"
                type="password"
                autoComplete="new-password"
                className="aws-input w-full rounded"
                {...register("newPassword", {
                  required: "Password is required",
                  minLength: { value: 12, message: "Minimum 12 characters" },
                  validate: (value) => {
                    const failing = PASSWORD_RULES.filter((r) => !r.test(value));
                    return failing.length === 0 || failing.map((r) => r.label).join(", ");
                  },
                })}
              />
              {errors.newPassword && (
                <span className="text-[11px] text-[#E23D36] mt-1 block">
                  {errors.newPassword.message}
                </span>
              )}
              {/* Password strength indicators */}
              <div className="mt-2 flex flex-col gap-1">
                {PASSWORD_RULES.map((rule) => {
                  const passes = rule.test(watchedPassword || "");
                  return (
                    <div
                      key={rule.label}
                      className={`text-[10px] flex items-center gap-1.5 ${
                        passes ? "text-[#16A34A]" : "text-text-muted"
                      }`}
                    >
                      <span>{passes ? "\u2713" : "\u2022"}</span>
                      <span>{rule.label}</span>
                    </div>
                  );
                })}
              </div>
            </div>

            <div>
              <label
                htmlFor="confirmPassword"
                className="block text-[11px] font-medium text-text mb-1"
              >
                Confirm password
              </label>
              <input
                id="confirmPassword"
                type="password"
                autoComplete="new-password"
                className="aws-input w-full rounded"
                {...register("confirmPassword", {
                  required: "Please confirm your password",
                  validate: (value) =>
                    value === watchedPassword || "Passwords do not match",
                })}
              />
              {errors.confirmPassword && (
                <span className="text-[11px] text-[#E23D36] mt-1 block">
                  {errors.confirmPassword.message}
                </span>
              )}
            </div>

            <Button
              variant="primary"
              className="w-full py-2.5 text-sm"
              disabled={loading}
            >
              {loading ? "Setting password\u2026" : "Set password \u2192"}
            </Button>
          </div>
        </form>
      </AuthCard>
    </AuthShell>
  );
}
