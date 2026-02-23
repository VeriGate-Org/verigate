"use client";

import { useEffect, useRef } from "react";
import { useSearchParams } from "next/navigation";

const DOC_LINKS = [
  {
    id: "api-reference",
    title: "API Reference",
    description: "Complete REST API documentation with request/response schemas, authentication, and error codes.",
    href: "#",
  },
  {
    id: "integration-guide",
    title: "Integration Guide",
    description: "Step-by-step guide to integrating VeriGate into your application with code samples.",
    href: "#",
  },
  {
    id: "webhooks",
    title: "Webhooks",
    description: "Configure real-time event notifications for verification status changes and alerts.",
    href: "#",
  },
  {
    id: "sdks",
    title: "SDKs & Libraries",
    description: "Official client libraries for Node.js, Python, Java, and .NET with usage examples.",
    href: "#",
  },
];

const FAQ_ITEMS = [
  {
    question: "How do I get an API key?",
    answer:
      "Navigate to Settings > API Keys and click \"Generate New Key\". The full key is displayed once at creation time. Store it securely -- you will not be able to view it again. If a key is compromised, revoke it immediately and generate a replacement.",
  },
  {
    question: "What verification types are supported?",
    answer:
      "VeriGate supports Home Affairs ID verification, company and director searches via CIPC, deeds registry lookups, bank account validation (AVS), sanctions and PEP screening, employment verification, qualification checks, negative news screening, fraud watchlist checks, and document verification. Each service has its own endpoint and response schema.",
  },
  {
    question: "What are the API rate limits?",
    answer:
      "The default rate limit is 100 requests per minute per API key. Enterprise plans include higher limits (up to 1,000 req/min) and burst capacity. If you hit a rate limit, the API returns HTTP 429 with a Retry-After header. Contact support to request a limit increase.",
  },
  {
    question: "How long is verification data retained?",
    answer:
      "Verification results are retained for 90 days in the active store and then archived to cold storage for 7 years to meet FICA and POPIA compliance requirements. You can export results via the API or the Reports page at any time during the active retention window.",
  },
  {
    question: "How should I handle verification errors?",
    answer:
      "All API errors include a structured error body with a code, message, and correlation ID. Transient errors (5xx) should be retried with exponential backoff. Client errors (4xx) indicate invalid input or authentication issues. The correlation ID can be used when contacting support for fast resolution.",
  },
  {
    question: "How does pricing work?",
    answer:
      "VeriGate uses a per-verification pricing model. Each verification type has its own unit cost. Volume discounts apply automatically at tier thresholds. Your current plan and usage are visible on the Dashboard. For custom pricing or enterprise agreements, contact your account manager.",
  },
];

const SHORTCUTS = [
  { keys: "\u2318 + K", description: "Open search" },
  { keys: "\u2318 + /", description: "Toggle sidebar" },
  { keys: "\u2318 + D", description: "Go to Dashboard" },
  { keys: "\u2318 + S", description: "Go to Settings" },
  { keys: "\u2318 + .", description: "Toggle theme" },
  { keys: "?", description: "Show keyboard shortcuts" },
];

const CHANGELOG = [
  {
    date: "2026-02-18",
    title: "Document verification service launched",
    description:
      "New document verification endpoint for identity documents, proof of address, and bank statements. Supports PDF and image uploads with automated OCR extraction.",
  },
  {
    date: "2026-02-05",
    title: "Policy Builder enhancements",
    description:
      "Added conditional branching, parallel execution nodes, and retry policies to the visual workflow builder. Policies can now be versioned and rolled back.",
  },
  {
    date: "2026-01-20",
    title: "Dark mode and accessibility improvements",
    description:
      "Full dark mode support across all pages. Improved keyboard navigation, ARIA labels, and screen reader compatibility throughout the portal.",
  },
  {
    date: "2026-01-08",
    title: "Bulk verification API",
    description:
      "New batch endpoint for submitting up to 500 verifications in a single request. Results are delivered via webhook or polling. Ideal for onboarding migrations.",
  },
];

export default function Help() {
  const searchParams = useSearchParams();
  const sectionParam = searchParams.get("section");

  const docsRef = useRef<HTMLDivElement>(null);
  const faqRef = useRef<HTMLDivElement>(null);
  const supportRef = useRef<HTMLDivElement>(null);
  const shortcutsRef = useRef<HTMLDivElement>(null);
  const changelogRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!sectionParam) return;
    const refMap: Record<string, React.RefObject<HTMLDivElement | null>> = {
      docs: docsRef,
      faq: faqRef,
      support: supportRef,
      shortcuts: shortcutsRef,
      changelog: changelogRef,
    };
    const target = refMap[sectionParam];
    if (target?.current) {
      target.current.scrollIntoView({ behavior: "smooth", block: "start" });
    }
  }, [sectionParam]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-1">
        <h1 className="text-xl font-semibold text-text">Help & Support</h1>
        <p className="text-sm text-text-muted">
          Documentation, frequently asked questions, and ways to reach the VeriGate support team.
        </p>
      </div>

      {/* Quick navigation */}
      <div className="flex flex-wrap gap-2">
        {[
          { label: "Documentation", section: "docs" },
          { label: "FAQ", section: "faq" },
          { label: "Support", section: "support" },
          { label: "Shortcuts", section: "shortcuts" },
          { label: "Changelog", section: "changelog" },
        ].map((item) => (
          <a
            key={item.section}
            href={`?section=${item.section}`}
            className="rounded border border-border bg-[color:var(--color-base-100)] px-3 py-1.5 text-[13px] font-medium text-text transition-colors hover:border-accent hover:text-accent"
          >
            {item.label}
          </a>
        ))}
      </div>

      {/* Documentation Links */}
      <div ref={docsRef}>
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Documentation</div>
              <div className="text-xs text-text-muted">Explore guides, references, and integration resources.</div>
            </div>
          </div>
          <div className="console-card-body">
            <div className="grid gap-3 sm:grid-cols-2">
              {DOC_LINKS.map((doc) => (
                <a
                  key={doc.id}
                  href={doc.href}
                  className="flex flex-col rounded-lg border border-border bg-[color:var(--color-base-100)] px-4 py-3 transition-all hover:border-accent hover:bg-accent-soft/50 hover:shadow-sm"
                >
                  <span className="text-sm font-medium text-text">{doc.title}</span>
                  <span className="mt-1 text-xs text-text-muted">{doc.description}</span>
                </a>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* FAQ */}
      <div ref={faqRef}>
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Frequently asked questions</div>
              <div className="text-xs text-text-muted">Common queries about the VeriGate platform.</div>
            </div>
          </div>
          <div className="console-card-body space-y-1">
            {FAQ_ITEMS.map((item, idx) => (
              <details
                key={idx}
                className="group rounded border border-transparent transition-colors open:border-border open:bg-[color:var(--color-base-200)]"
              >
                <summary className="flex cursor-pointer items-center gap-2 px-3 py-2.5 text-sm font-medium text-text hover:text-accent select-none list-none">
                  <svg
                    className="h-4 w-4 shrink-0 text-text-muted transition-transform group-open:rotate-90"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth={2}
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
                  </svg>
                  {item.question}
                </summary>
                <div className="px-3 pb-3 pl-9 text-sm text-text-muted leading-relaxed">{item.answer}</div>
              </details>
            ))}
          </div>
        </div>
      </div>

      {/* Support Contact */}
      <div ref={supportRef}>
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Contact support</div>
              <div className="text-xs text-text-muted">Reach the VeriGate support team directly.</div>
            </div>
          </div>
          <div className="console-card-body">
            <div className="grid gap-4 sm:grid-cols-3">
              <div className="space-y-1 rounded border border-border bg-[color:var(--color-base-200)] p-4">
                <div className="text-xs uppercase tracking-wide text-text-muted">Email</div>
                <a href="mailto:support@verigate.co.za" className="text-sm font-medium text-accent hover:underline">
                  support@verigate.co.za
                </a>
              </div>
              <div className="space-y-1 rounded border border-border bg-[color:var(--color-base-200)] p-4">
                <div className="text-xs uppercase tracking-wide text-text-muted">Phone</div>
                <a href="tel:+27110000000" className="text-sm font-medium text-text">
                  +27 11 000 0000
                </a>
              </div>
              <div className="space-y-1 rounded border border-border bg-[color:var(--color-base-200)] p-4">
                <div className="text-xs uppercase tracking-wide text-text-muted">Business hours</div>
                <div className="text-sm font-medium text-text">Mon - Fri, 8:00 - 17:00 SAST</div>
              </div>
            </div>
            <div className="mt-4 text-xs text-text-muted">
              For critical production issues outside business hours, email{" "}
              <a href="mailto:urgent@verigate.co.za" className="text-accent underline">
                urgent@verigate.co.za
              </a>{" "}
              with your partner ID and correlation ID for priority routing.
            </div>
          </div>
        </div>
      </div>

      {/* Keyboard Shortcuts */}
      <div ref={shortcutsRef}>
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Keyboard shortcuts</div>
              <div className="text-xs text-text-muted">Navigate the portal faster with keyboard commands.</div>
            </div>
          </div>
          <div className="console-card-body p-0">
            <div className="overflow-x-auto">
              <table className="min-w-full text-left text-sm">
                <thead className="bg-[color:var(--color-base-200)] text-xs uppercase tracking-wide text-text-muted">
                  <tr>
                    <th className="px-4 py-2.5">Shortcut</th>
                    <th className="px-4 py-2.5">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {SHORTCUTS.map((s, idx) => (
                    <tr key={idx} className="border-b border-border last:border-0">
                      <td className="px-4 py-2.5">
                        <kbd className="inline-flex items-center gap-1 rounded border border-border bg-[color:var(--color-base-200)] px-2 py-0.5 font-mono text-xs text-text">
                          {s.keys}
                        </kbd>
                      </td>
                      <td className="px-4 py-2.5 text-text-muted">{s.description}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      {/* Changelog */}
      <div ref={changelogRef}>
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Changelog</div>
              <div className="text-xs text-text-muted">Recent updates and improvements to the VeriGate platform.</div>
            </div>
          </div>
          <div className="console-card-body space-y-4">
            {CHANGELOG.map((entry, idx) => (
              <div key={idx} className="relative pl-6 before:absolute before:left-1.5 before:top-2 before:h-2 before:w-2 before:rounded-full before:bg-accent">
                {/* Connecting line between dots */}
                {idx < CHANGELOG.length - 1 && (
                  <div className="absolute left-[9px] top-5 bottom-[-12px] w-px bg-border" />
                )}
                <div className="text-xs text-text-muted">
                  {new Date(entry.date).toLocaleDateString("en-ZA", {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                  })}
                </div>
                <div className="mt-0.5 text-sm font-medium text-text">{entry.title}</div>
                <div className="mt-1 text-xs text-text-muted leading-relaxed">{entry.description}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
