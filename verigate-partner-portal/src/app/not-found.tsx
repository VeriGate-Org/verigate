import Link from "next/link";

export default function NotFound() {
  return (
    <div className="min-h-screen flex items-center justify-center p-10 bg-gradient-to-br from-[#0F1A2E] via-[#1A2E4B] to-[#1a3a5c] relative overflow-hidden">
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
            "radial-gradient(ellipse 60% 50% at 50% 50%, rgba(0,179,217,0.10) 0%, transparent 70%)",
        }}
      />

      <div className="relative text-center max-w-[480px] text-white">
        <div className="inline-block mb-6">
          <svg
            width="110"
            height="120"
            viewBox="20 25 112 122"
            className="drop-shadow-[0_8px_24px_rgba(226,61,54,0.4)]"
          >
            <path
              fill="#E23D36"
              d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"
            />
            <line
              x1="46"
              y1="58"
              x2="106"
              y2="102"
              stroke="#fff"
              strokeWidth="13"
              strokeLinecap="round"
            />
            <line
              x1="106"
              y1="58"
              x2="46"
              y2="102"
              stroke="#fff"
              strokeWidth="13"
              strokeLinecap="round"
            />
          </svg>
        </div>

        <div
          className="text-[96px] font-bold leading-none tracking-tight mb-2"
          style={{
            background: "linear-gradient(90deg,#22d3ee,#60a5fa,#06b6d4)",
            WebkitBackgroundClip: "text",
            WebkitTextFillColor: "transparent",
            backgroundClip: "text",
          }}
        >
          404
        </div>

        <div className="text-[22px] font-semibold text-white mb-2.5">
          This verification doesn&apos;t exist
        </div>
        <div className="text-sm text-white/70 leading-relaxed mb-7">
          The page you&apos;re looking for has moved, been deleted, or was never
          here. Check the URL, or head back to your dashboard.
        </div>

        <div className="flex gap-2.5 justify-center flex-wrap">
          <Link
            href="/dashboard"
            className="aws-button aws-button--cta px-5 py-2.5 text-sm no-underline"
          >
            &larr; Back to dashboard
          </Link>
        </div>

        <div className="mt-10 pt-6 border-t border-white/10 text-[11px] text-white/50">
          Error ref &middot;{" "}
          <span className="font-mono">VG-404</span> &middot;{" "}
          VeriGate Partner Portal
        </div>
      </div>
    </div>
  );
}
