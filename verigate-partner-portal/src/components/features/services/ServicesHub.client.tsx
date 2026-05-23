"use client";

import { useMemo, useState } from "react";
import { PageHeader } from "@/components/ui/PageHeader";
import { FilterChips } from "@/components/ui/FilterChips";
import { ServiceCard } from "./ServiceCard";
import { getAllTypes, type VerificationTypeInfo } from "@/lib/verification-type-map";
import { useTenantFeatures } from "@/lib/tenant/PartnerTenantProvider";
import { Search } from "lucide-react";

const CATEGORY_META: Record<
  VerificationTypeInfo["category"],
  { label: string; description: string }
> = {
  identity: {
    label: "Identity",
    description: "Verify individuals against government and biometric sources.",
  },
  financial: {
    label: "Financial",
    description: "Bank account, credit, income, and tax checks.",
  },
  business: {
    label: "Business",
    description: "Company registry, employment, and qualification verification.",
  },
  screening: {
    label: "Screening",
    description: "Sanctions, PEP, watchlist, and adverse media screening.",
  },
  biometric: {
    label: "Biometric",
    description: "Facial recognition and liveness detection services.",
  },
  composite: {
    label: "Composite",
    description: "Combined verification workflows.",
  },
};

const CATEGORY_ORDER: VerificationTypeInfo["category"][] = [
  "identity",
  "financial",
  "business",
  "screening",
  "biometric",
  "composite",
];

/** Descriptions for service cards, keyed by portalType. */
const SERVICE_DESCRIPTIONS: Record<string, string> = {
  ID: "Verify identity details against the Department of Home Affairs database.",
  IDENTITY: "Full identity verification with document scanning and DHA confirmation.",
  DOCUMENT: "OCR extraction and tampering analysis for SA identity and business documents.",
  AVS: "Confirm account holder, bank, branch, and that the account is active.",
  CREDIT: "TransUnion + Experian credit profile, score, and adverse listings.",
  INCOME: "Confirm gross and net income via payslip OCR, employer verification, or bank statements.",
  TAX: "SARS tax clearance status verification via the TCS PIN process.",
  CIPC: "CIPC business registry lookup including directors and UBO information.",
  DEEDS: "Deeds registry property ownership search and history.",
  EMPLOYMENT: "Employment history, dates, and roles via direct employer outreach.",
  QUALIFICATION: "SAQA and institution-direct verification of degrees and certificates.",
  SANCTIONS: "Screen against sanctions lists, PEP registers, and crime databases.",
  NEGATIVE_NEWS: "Adverse media search across 16,000+ South African and international sources.",
  FRAUD_WATCHLIST: "Internal blacklist of confirmed fraudsters shared across VeriGate partners.",
  WATCHLIST: "Consolidated watchlist screening against OpenSanctions.",
  VAT_VENDOR: "SARS VAT vendor validation for counterparty due diligence.",
  BIOMETRIC: "Facial recognition verification against stored biometric templates.",
  LIVENESS: "Active liveness detection to prevent spoofing with photos or masks.",
};

/** Deduplicate services that share the same route (e.g. ID / IDENTITY, SANCTIONS / WATCHLIST). */
function deduplicateByRoute(types: VerificationTypeInfo[]): VerificationTypeInfo[] {
  const seen = new Set<string>();
  return types.filter((t) => {
    if (seen.has(t.route)) return false;
    seen.add(t.route);
    return true;
  });
}

export function ServicesHub() {
  const { hasFeature } = useTenantFeatures();
  const [search, setSearch] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("all");

  const allTypes = useMemo(() => deduplicateByRoute(getAllTypes()), []);

  const grouped = useMemo(() => {
    const filtered = allTypes.filter((t) => {
      const matchesSearch =
        !search ||
        t.label.toLowerCase().includes(search.toLowerCase()) ||
        t.shortLabel.toLowerCase().includes(search.toLowerCase());
      const matchesCategory =
        categoryFilter === "all" || t.category === categoryFilter;
      return matchesSearch && matchesCategory;
    });

    const groups: Record<string, VerificationTypeInfo[]> = {};
    for (const t of filtered) {
      const cat = t.category;
      if (!groups[cat]) groups[cat] = [];
      groups[cat].push(t);
    }

    return CATEGORY_ORDER.filter((cat) => groups[cat]?.length).map((cat) => ({
      category: cat,
      meta: CATEGORY_META[cat],
      services: groups[cat],
    }));
  }, [allTypes, search, categoryFilter]);

  const categoryChips = [
    { label: "All services", value: "all" },
    ...CATEGORY_ORDER.filter((cat) =>
      allTypes.some((t) => t.category === cat),
    ).map((cat) => ({
      label: CATEGORY_META[cat].label,
      value: cat,
      count: allTypes.filter((t) => t.category === cat).length,
    })),
  ];

  return (
    <div className="space-y-aws-l">
      <PageHeader
        category="Services"
        title="Verification Services"
        description="Run individual checks, view history, and manage service configurations."
      />

      <div className="flex flex-col sm:flex-row sm:items-center gap-3">
        <FilterChips
          chips={categoryChips}
          value={categoryFilter}
          onChange={setCategoryFilter}
          className="flex-1"
        />
        <div className="relative w-full sm:w-60 shrink-0">
          <Search
            size={14}
            className="absolute left-3 top-1/2 -translate-y-1/2 text-text-muted pointer-events-none"
          />
          <input
            type="text"
            placeholder="Search services..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="aws-input w-full pl-9 text-xs"
          />
        </div>
      </div>

      {grouped.length === 0 && (
        <div className="text-center py-16 text-sm text-text-muted">
          No services match your search.
        </div>
      )}

      {grouped.map((group) => (
        <div key={group.category}>
          <div className="mb-3">
            <h2 className="text-sm font-semibold text-text">
              {group.meta.label}
            </h2>
            <p className="text-xs text-text-muted mt-0.5">
              {group.meta.description}
            </p>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3">
            {group.services.map((svc) => {
              const isDisabled =
                svc.provider === "Coming Soon" ||
                !hasFeature("core_verifications");

              return (
                <ServiceCard
                  key={svc.portalType}
                  name={svc.label}
                  shortLabel={svc.shortLabel}
                  description={
                    SERVICE_DESCRIPTIONS[svc.portalType] || svc.label
                  }
                  category={group.meta.label}
                  icon={svc.icon}
                  route={svc.route}
                  provider={svc.provider}
                  disabled={isDisabled}
                />
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
}
