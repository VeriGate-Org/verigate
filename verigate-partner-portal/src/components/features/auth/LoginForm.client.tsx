"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { useAuth } from "@/lib/auth/AuthProvider";
import { Button } from "@/components/ui/Button";
import { AuthShell, AuthCard, AuthBrandHeader } from "./AuthShell";

interface LoginFields {
  email: string;
  password: string;
}

export function LoginForm() {
  const router = useRouter();
  const { signIn, isAuthenticated } = useAuth();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFields>({
    defaultValues: { email: "", password: "" },
  });

  // If already authenticated, redirect
  if (isAuthenticated) {
    router.replace("/dashboard");
    return null;
  }

  const onSubmit = async (data: LoginFields) => {
    setError("");
    setLoading(true);
    try {
      const result = await signIn(data.email, data.password);
      if (result?.challenge === "newPasswordRequired") {
        // Store session token temporarily for the set-password page
        sessionStorage.setItem(
          "verigate-new-password-session",
          JSON.stringify({ session: result.session, email: result.email }),
        );
        router.replace(`/set-password?email=${encodeURIComponent(result.email)}`);
        return;
      }
      router.replace("/dashboard");
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Sign in failed. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthShell>
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
              Sign in to your account
            </div>

            {error && (
              <div className="bg-[rgba(226,61,54,0.06)] border border-[rgba(226,61,54,0.35)] text-[#E23D36] rounded px-3 py-2 text-xs flex gap-2 items-start leading-relaxed">
                <span className="shrink-0">&times;</span>
                <span>{error}</span>
              </div>
            )}

            <div>
              <label
                htmlFor="email"
                className="block text-[11px] font-medium text-text mb-1"
              >
                Email address
              </label>
              <input
                id="email"
                type="email"
                autoComplete="email"
                placeholder="you@company.com"
                className="aws-input w-full rounded"
                {...register("email", { required: "Email is required" })}
              />
              {errors.email && (
                <span className="text-[11px] text-[#E23D36] mt-1">
                  {errors.email.message}
                </span>
              )}
            </div>

            <div>
              <label
                htmlFor="password"
                className="block text-[11px] font-medium text-text mb-1"
              >
                Password
              </label>
              <input
                id="password"
                type="password"
                autoComplete="current-password"
                className="aws-input w-full rounded"
                {...register("password", { required: "Password is required" })}
              />
              {errors.password && (
                <span className="text-[11px] text-[#E23D36] mt-1">
                  {errors.password.message}
                </span>
              )}
              <div className="mt-2 flex items-center justify-between">
                <label className="text-[11px] text-text-muted inline-flex gap-1.5 items-center cursor-pointer">
                  <input
                    type="checkbox"
                    defaultChecked
                    className="accent-accent"
                  />
                  Remember me for 30 days
                </label>
                <button
                  type="button"
                  className="bg-transparent border-none text-accent text-[11px] font-medium cursor-pointer p-0"
                >
                  Forgot password?
                </button>
              </div>
            </div>

            <Button
              variant="primary"
              className="w-full py-2.5 text-sm"
              disabled={loading}
            >
              {loading ? "Signing in\u2026" : "Sign in \u2192"}
            </Button>

            <div className="relative text-center my-1">
              <span className="bg-surface px-2.5 text-[10px] text-text-muted uppercase tracking-wide relative z-10">
                or
              </span>
              <span className="absolute top-1/2 left-0 right-0 h-px bg-[#e9ebed]" />
            </div>

            <button
              type="button"
              className="w-full py-2.5 bg-surface border border-[#CBD5E1] rounded-aws-control text-[13px] font-medium text-primary cursor-pointer inline-flex items-center justify-center gap-2"
            >
              <svg width="14" height="14" viewBox="0 0 24 24">
                <path
                  fill="#4285F4"
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                />
                <path
                  fill="#34A853"
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                />
                <path
                  fill="#FBBC05"
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                />
                <path
                  fill="#EA4335"
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                />
              </svg>
              Continue with Google SSO
            </button>
          </div>
        </form>
      </AuthCard>
    </AuthShell>
  );
}
