import Link from "next/link";

export function AuthShell({
  children,
  footerText,
}: {
  children: React.ReactNode;
  footerText?: "signup" | "setPassword";
}) {
  return (
    <div className="fixed inset-0 flex flex-col items-center justify-center p-5 overflow-y-auto bg-gradient-to-br from-[#0F1A2E] via-[#1A2E4B] to-[#0d2440]">
      {/* Dot pattern */}
      <div
        className="absolute inset-0 pointer-events-none"
        style={{
          backgroundImage:
            "radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)",
          backgroundSize: "24px 24px",
        }}
      />
      {/* Glow */}
      <div
        className="absolute inset-0 pointer-events-none"
        style={{
          background:
            "radial-gradient(ellipse 60% 50% at 50% 50%, rgba(0,179,217,0.12) 0%, transparent 70%)",
        }}
      />

      <div className="relative flex flex-col items-center gap-6">
        {children}

        <div className="text-[11px] text-white/50">
          {footerText === "signup" ? (
            <>
              Already have an account?{" "}
              <Link href="/signin" className="text-accent hover:underline">
                Sign in &rarr;
              </Link>
            </>
          ) : footerText === "setPassword" ? (
            <>
              Need help?{" "}
              <Link href="/signin" className="text-accent hover:underline">
                Back to sign in &rarr;
              </Link>
            </>
          ) : (
            <>
              New to VeriGate?{" "}
              <Link href="/signup" className="text-accent hover:underline">
                Request access &rarr;
              </Link>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export function AuthCard({
  children,
  footer,
}: {
  children: React.ReactNode;
  footer?: React.ReactNode;
}) {
  return (
    <div className="w-[400px] max-w-[calc(100vw-32px)] bg-surface rounded-aws-container shadow-[0_20px_40px_-10px_rgba(0,28,36,0.4)] overflow-hidden border border-border">
      {/* Tri-bar accent */}
      <div className="flex h-[3px]">
        <div className="flex-[3] bg-[#E23D36]" />
        <div className="flex-[5] bg-primary" />
        <div className="flex-[2] bg-accent" />
      </div>
      {children}
      {footer && (
        <div className="px-7 py-3.5 border-t border-[#e9ebed] bg-[#F8FAFC] text-[11px] text-text-muted text-center">
          {footer}
        </div>
      )}
    </div>
  );
}

export function AuthBrandHeader() {
  return (
    <div className="px-7 pt-7 pb-1 text-center">
      <svg
        width="46"
        height="50"
        viewBox="20 25 112 122"
        className="drop-shadow-[0_2px_6px_rgba(226,61,54,0.25)]"
      >
        <path
          fill="#E23D36"
          d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"
        />
        <path
          d="M46 84 L63 102 L106 58"
          fill="none"
          stroke="#FFFFFF"
          strokeWidth="13"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
      <div className="mt-2.5 font-medium text-[22px] text-primary tracking-tight">
        VeriGate
      </div>
      <div className="text-[11px] text-text-muted mt-0.5 tracking-wide">
        Partner Portal
      </div>
    </div>
  );
}
