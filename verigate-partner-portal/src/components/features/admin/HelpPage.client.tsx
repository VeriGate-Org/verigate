"use client";

import { useState } from "react";
import { PageHeader } from "@/components/ui/PageHeader";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { cn } from "@/lib/cn";
import {
  FileSearch,
  Zap,
  User,
  Settings,
  Search,
  ChevronRight,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";

/* ------------------------------------------------------------------ */
/*  Data                                                               */
/* ------------------------------------------------------------------ */

interface QuickCard {
  icon: LucideIcon;
  label: string;
  sub: string;
  tone: string;
}

const QUICK_CARDS: QuickCard[] = [
  { icon: FileSearch, label: "Documentation", sub: "API reference + guides", tone: "#00B3D9" },
  { icon: Zap, label: "Quick tutorials", sub: "5-min walkthroughs", tone: "#2C974B" },
  { icon: User, label: "Talk to support", sub: "Mon\u2013Fri \u00B7 08:00\u201318:00 SAST", tone: "#C28B0B" },
  { icon: Settings, label: "Status page", sub: "Live system health", tone: "#1A2E4B" },
];

const ARTICLES = [
  { title: "Getting started with VeriGate", body: "A 5-minute walkthrough of your first verification." },
  { title: "Setting up API keys & webhooks", body: "Integrate VeriGate into your onboarding flow." },
  { title: "POPIA compliance checklist", body: "Everything you need to be a registered operator." },
  { title: "Policy Builder fundamentals", body: "Build a verification policy from scratch." },
  { title: "Reading a verification report", body: "How risk scores, signals, and decisions combine." },
  { title: "Troubleshooting failed verifications", body: "Most common failures and how to resolve them." },
];

const TICKET_CATEGORIES = [
  { value: "", label: "What can we help with?" },
  { value: "api", label: "API / integration issue" },
  { value: "verification", label: "Verification failed unexpectedly" },
  { value: "billing", label: "Billing question" },
  { value: "feature", label: "Feature request" },
  { value: "compliance", label: "Compliance / POPIA question" },
];

/* ------------------------------------------------------------------ */
/*  Main component                                                     */
/* ------------------------------------------------------------------ */

export function HelpPage() {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");
  const [description, setDescription] = useState("");

  return (
    <div className="space-y-3.5">
      <PageHeader
        category="Admin \u00B7 Help"
        title="Help & Support"
        description="Docs, tutorials, and live support for your team."
      />

      {/* Search hero */}
      <div className="relative rounded-aws-container overflow-hidden" style={{ background: "linear-gradient(160deg, #0F1A2E, #1A2E4B 60%, #1a3a5c)" }}>
        <div
          className="absolute inset-0 opacity-60"
          style={{
            backgroundImage: "radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)",
            backgroundSize: "20px 20px",
          }}
        />
        <div className="relative text-center py-8 px-7">
          <div className="text-xl font-semibold text-white mb-3">How can we help?</div>
          <div className="relative max-w-[540px] mx-auto">
            <Search size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-text-muted" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search docs, runbooks, troubleshooting\u2026"
              className="w-full py-3 px-4 pl-[38px] rounded-md border-none text-sm font-sans outline-none"
            />
          </div>
        </div>
      </div>

      {/* Quick-link cards */}
      <div className="grid grid-cols-4 gap-3 max-lg:grid-cols-2">
        {QUICK_CARDS.map((c) => {
          const Icon = c.icon;
          return (
            <div
              key={c.label}
              className="console-card p-4 cursor-pointer transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md"
              style={{ borderColor: undefined }}
              onMouseEnter={(e) => {
                e.currentTarget.style.borderColor = c.tone;
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.borderColor = "";
              }}
            >
              <div
                className="w-9 h-9 rounded-lg inline-flex items-center justify-center mb-2.5"
                style={{ background: `${c.tone}1a` }}
              >
                <Icon size={18} style={{ color: c.tone }} />
              </div>
              <div className="text-[13px] font-semibold">{c.label}</div>
              <div className="text-[11px] text-text-muted mt-0.5">{c.sub}</div>
            </div>
          );
        })}
      </div>

      {/* Articles + ticket form */}
      <div className="grid grid-cols-[1.6fr_1fr] gap-3.5 max-lg:grid-cols-1">
        {/* Popular articles */}
        <Card>
          <CardHeader>
            <div className="text-sm font-semibold">Popular articles</div>
          </CardHeader>
          <div>
            {ARTICLES.map((a, i) => (
              <div
                key={a.title}
                className={cn(
                  "flex items-center justify-between px-[18px] py-3 cursor-pointer hover:bg-[#F8FAFC] transition-colors",
                  i > 0 && "border-t border-[#f1f5f9]",
                )}
              >
                <div>
                  <div className="text-[13px] font-medium">{a.title}</div>
                  <div className="text-[11px] text-text-muted mt-0.5">{a.body}</div>
                </div>
                <ChevronRight size={13} className="text-text-muted shrink-0 ml-3" />
              </div>
            ))}
          </div>
        </Card>

        {/* Right column: ticket + contact */}
        <div className="flex flex-col gap-3">
          <Card>
            <CardBody>
              <div className="text-[13px] font-semibold">Open a ticket</div>
              <div className="text-[11px] text-text-muted mt-0.5 mb-3">
                Average first response: <b>34 min</b>
              </div>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="aws-select select-input w-full mb-2 text-xs"
              >
                {TICKET_CATEGORIES.map((o) => (
                  <option key={o.value} value={o.value}>
                    {o.label}
                  </option>
                ))}
              </select>
              <textarea
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Describe the issue\u2026"
                rows={4}
                className="aws-input w-full resize-y text-xs"
              />
              <Button variant="cta" className="w-full mt-2">
                Open ticket &rarr;
              </Button>
            </CardBody>
          </Card>

          <div className="bg-[#F8FAFC] border border-dashed border-[#CBD5E1] rounded-aws-container p-3.5 text-[11px] text-text-muted leading-relaxed">
            <b className="text-primary">Phone:</b> +27 82 211 8921
            <br />
            <b className="text-primary">Email:</b> support@verigate.co.za
            <br />
            <b className="text-primary">Hours:</b> Mon&ndash;Fri 08:00&ndash;18:00 SAST
          </div>
        </div>
      </div>
    </div>
  );
}
