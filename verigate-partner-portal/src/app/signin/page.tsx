"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth, CognitoAuthError } from "@/lib/auth";
import {
  forgotPassword,
  confirmForgotPassword,
} from "@/lib/auth/cognito-client";

type View = "signin" | "forgot" | "reset";

export default function SignInPage() {
  const router = useRouter();
  const { signIn, isAuthenticated } = useAuth();
  const [view, setView] = useState<View>("signin");

  // Sign-in form
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  // Forgot password
  const [resetEmail, setResetEmail] = useState("");
  const [resetCode, setResetCode] = useState("");
  const [newPassword, setNewPassword] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  // Redirect if already authenticated
  if (isAuthenticated) {
    router.replace("/dashboard");
    return null;
  }

  async function handleSignIn(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await signIn(email, password);
      router.replace("/dashboard");
    } catch (err) {
      if (err instanceof CognitoAuthError) {
        if (err.code === "NotAuthorizedException") {
          setError("Incorrect email or password.");
        } else if (err.code === "UserNotFoundException") {
          setError("No account found with that email.");
        } else if (err.code === "UserNotConfirmedException") {
          setError("Please confirm your account before signing in.");
        } else {
          setError(err.message);
        }
      } else {
        setError("Unable to connect. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  }

  async function handleForgotPassword(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    setMessage("");
    setLoading(true);
    try {
      await forgotPassword(resetEmail);
      setMessage("A verification code has been sent to your email.");
      setView("reset");
    } catch (err) {
      if (err instanceof CognitoAuthError) {
        if (err.code === "UserNotFoundException") {
          setError("No account found with that email.");
        } else if (err.code === "LimitExceededException") {
          setError("Too many attempts. Please try again later.");
        } else {
          setError(err.message);
        }
      } else {
        setError("Unable to connect. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  }

  async function handleResetPassword(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    setMessage("");
    setLoading(true);
    try {
      await confirmForgotPassword(resetEmail, resetCode, newPassword);
      setMessage("Password reset successfully. Please sign in.");
      setView("signin");
      setPassword("");
      setEmail(resetEmail);
    } catch (err) {
      if (err instanceof CognitoAuthError) {
        if (err.code === "CodeMismatchException") {
          setError("Invalid verification code.");
        } else if (err.code === "ExpiredCodeException") {
          setError("Verification code has expired. Please request a new one.");
        } else if (err.code === "InvalidPasswordException") {
          setError("Password does not meet requirements: 12+ characters, uppercase, lowercase, number, and symbol.");
        } else {
          setError(err.message);
        }
      } else {
        setError("Unable to connect. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4">
      <div className="w-full max-w-sm space-y-6">
        {/* Logo */}
        <div className="flex flex-col items-center gap-2">
          <svg width="40" height="40" viewBox="0 0 28 28" shapeRendering="geometricPrecision">
            <path fill="#E23D36" d="M14 2c-3.8 0-7 1.33-7 1.33v7.7c0 5.2 3.4 10.03 7 12.24 3.6-2.21 7-7.04 7-12.24V3.33C21 3.33 17.8 2 14 2Z" />
            <path d="M8.5 14.5l3.5 3.5 7.5-7.5" fill="none" stroke="#FFFFFF" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
          <h1 className="text-lg font-semibold text-text">VeriGate Partner Portal</h1>
        </div>

        {error && (
          <div className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-sm text-danger">
            {error}
          </div>
        )}

        {message && (
          <div className="rounded border border-success/40 bg-success/5 px-3 py-2 text-sm text-success">
            {message}
          </div>
        )}

        {/* Sign In Form */}
        {view === "signin" && (
          <form onSubmit={handleSignIn} className="console-card">
            <div className="console-card-header">
              <h2 className="text-sm font-semibold text-text">Sign in to your account</h2>
            </div>
            <div className="console-card-body space-y-4">
              <div>
                <label htmlFor="email" className="block text-xs font-medium text-text-muted mb-1">
                  Email address
                </label>
                <input
                  id="email"
                  type="email"
                  required
                  autoComplete="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="aws-input w-full"
                  placeholder="you@company.com"
                />
              </div>
              <div>
                <label htmlFor="password" className="block text-xs font-medium text-text-muted mb-1">
                  Password
                </label>
                <input
                  id="password"
                  type="password"
                  required
                  autoComplete="current-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="aws-input w-full"
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="aws-btn-primary w-full py-2 text-sm font-medium disabled:opacity-50"
              >
                {loading ? "Signing in..." : "Sign in"}
              </button>
              <button
                type="button"
                onClick={() => {
                  setView("forgot");
                  setError("");
                  setMessage("");
                  setResetEmail(email);
                }}
                className="w-full text-center text-xs text-primary hover:underline"
              >
                Forgot your password?
              </button>
            </div>
          </form>
        )}

        {/* Forgot Password Form */}
        {view === "forgot" && (
          <form onSubmit={handleForgotPassword} className="console-card">
            <div className="console-card-header">
              <h2 className="text-sm font-semibold text-text">Reset your password</h2>
            </div>
            <div className="console-card-body space-y-4">
              <p className="text-xs text-text-muted">
                Enter your email and we&apos;ll send a verification code.
              </p>
              <div>
                <label htmlFor="reset-email" className="block text-xs font-medium text-text-muted mb-1">
                  Email address
                </label>
                <input
                  id="reset-email"
                  type="email"
                  required
                  value={resetEmail}
                  onChange={(e) => setResetEmail(e.target.value)}
                  className="aws-input w-full"
                  placeholder="you@company.com"
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="aws-btn-primary w-full py-2 text-sm font-medium disabled:opacity-50"
              >
                {loading ? "Sending..." : "Send verification code"}
              </button>
              <button
                type="button"
                onClick={() => {
                  setView("signin");
                  setError("");
                  setMessage("");
                }}
                className="w-full text-center text-xs text-primary hover:underline"
              >
                Back to sign in
              </button>
            </div>
          </form>
        )}

        {/* Reset Password Form */}
        {view === "reset" && (
          <form onSubmit={handleResetPassword} className="console-card">
            <div className="console-card-header">
              <h2 className="text-sm font-semibold text-text">Enter new password</h2>
            </div>
            <div className="console-card-body space-y-4">
              <div>
                <label htmlFor="code" className="block text-xs font-medium text-text-muted mb-1">
                  Verification code
                </label>
                <input
                  id="code"
                  type="text"
                  required
                  value={resetCode}
                  onChange={(e) => setResetCode(e.target.value)}
                  className="aws-input w-full"
                  placeholder="123456"
                  autoComplete="one-time-code"
                />
              </div>
              <div>
                <label htmlFor="new-password" className="block text-xs font-medium text-text-muted mb-1">
                  New password
                </label>
                <input
                  id="new-password"
                  type="password"
                  required
                  minLength={12}
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  className="aws-input w-full"
                  autoComplete="new-password"
                />
                <p className="mt-1 text-xs text-text-muted">
                  Minimum 12 characters with uppercase, lowercase, number, and symbol.
                </p>
              </div>
              <button
                type="submit"
                disabled={loading}
                className="aws-btn-primary w-full py-2 text-sm font-medium disabled:opacity-50"
              >
                {loading ? "Resetting..." : "Reset password"}
              </button>
              <button
                type="button"
                onClick={() => {
                  setView("signin");
                  setError("");
                  setMessage("");
                }}
                className="w-full text-center text-xs text-primary hover:underline"
              >
                Back to sign in
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
