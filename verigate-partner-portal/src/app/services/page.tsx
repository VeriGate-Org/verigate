"use client";

import Link from "next/link";
import { ArrowRight } from "lucide-react";
import { PLAN_LABELS, useTenantFeatures } from "@/lib/tenant/PartnerTenantProvider";
import { Feature, type FeatureKey, getRequiredPlan } from "@/lib/tenant-features";

type ServiceCard = {
  name: string;
  description: string;
  path: string;
  feature?: FeatureKey;
};

const SERVICES: ReadonlyArray<ServiceCard> = [
  {
    name: "Home Affairs ID verification",
    description: "Validate South African IDs directly against the DHA registry.",
    path: "/services/personal-details",
  },
  {
    name: "CIPC company & director search",
    description: "Retrieve entity status, filings, and director appointments.",
    path: "/services/company",
  },
  {
    name: "Deeds registry search",
    description: "Surface ownership, bonds, and municipal arrears for properties.",
    path: "/services/property-ownership",
    feature: Feature.DEEDS_REGISTRY,
  },
  {
    name: "Deeds spatial map",
    description: "Explore cached property records and municipal boundaries in a map-style workspace.",
    path: "/services/deeds-map",
    feature: Feature.DEEDS_MAP,
  },
  {
    name: "Street / ERF conversion",
    description: "Convert street references to ERF details and back using provider-independent matching.",
    path: "/services/property-conversion",
    feature: Feature.DEEDS_CONVERSION,
  },
  {
    name: "Property valuation",
    description: "Estimate indicative value from cached transfer history and comparable sales.",
    path: "/services/property-valuation",
    feature: Feature.DEEDS_VALUATION,
  },
  {
    name: "Bank account validation",
    description: "Confirm account ownership and status via AVS feeds.",
    path: "/services/bank-account",
  },
  {
    name: "Sanctions & PEP screening",
    description: "Check for sanctions, watchlist, and politically exposed person hits.",
    path: "/services/sanctions",
  },
  {
    name: "Identity verification",
    description: "Full identity verification including biometric checks and document validation.",
    path: "/services/identity",
  },
  {
    name: "Employment verification",
    description: "Confirm current and historical employment details via payroll data.",
    path: "/services/employment",
  },
  {
    name: "Credit check",
    description: "Retrieve credit bureau reports including scores and payment history.",
    path: "/services/credit-check",
  },
  {
    name: "Negative news screening",
    description: "Scan media and public records for adverse information.",
    path: "/services/negative-news",
    feature: Feature.ADVANCED_SCREENING,
  },
  {
    name: "Fraud watchlist screening",
    description: "Check against SAFPS and industry fraud databases.",
    path: "/services/fraud-watchlist",
    feature: Feature.ADVANCED_SCREENING,
  },
  {
    name: "Document verification",
    description: "AI-powered document analysis with per-field confidence scoring, tampering detection, and SA ID validation.",
    path: "/services/document-verification",
  },
  {
    name: "Document auto-fill",
    description: "Upload multiple documents and let AI extract, validate, and aggregate fields into a pre-filled form.",
    path: "/document-auto-fill",
  },
  {
    name: "Document AI analytics",
    description: "Monitor document processing volume, classification accuracy, and fraud detection metrics.",
    path: "/document-analytics",
  },
  {
    name: "Qualification verification",
    description: "Verify academic qualifications via SAQA and institutional records.",
    path: "/services/qualification",
  },
  {
    name: "Tax compliance",
    description: "Verify SARS tax compliance status and clearance certificates.",
    path: "/services/tax-compliance",
  },
  {
    name: "VAT vendor search",
    description: "Search the SARS VAT vendor register to verify vendor registration status.",
    path: "/services/vat-vendor-search",
  },
  {
    name: "Income verification",
    description: "Validate declared income against payroll and banking records.",
    path: "/services/income",
  },
  {
    name: "Full verification",
    description: "Run a comprehensive multi-check verification suite across all available sources.",
    path: "/services/full-verification",
  },
  {
    name: "Watchlist screening",
    description: "Consolidated screening across global sanctions and watchlist databases.",
    path: "/services/sanctions",
  },
];

export default function ServicesIndexPage() {
  const { hasFeature } = useTenantFeatures();
  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Verification services</h1>
        <p className="text-sm text-text-muted">Launch an on-demand check or embed these endpoints into your onboarding flows.</p>
      </header>

      <div className="grid gap-4 lg:grid-cols-2">
        {SERVICES.map((service) => (
          <article key={service.path} className="console-card">
            <div className="console-card-body flex items-start justify-between gap-4">
              <div className="space-y-1">
                <h2 className="text-sm font-semibold text-text">{service.name}</h2>
                <p className="text-xs text-text-muted">{service.description}</p>
                {service.feature && !hasFeature(service.feature) && (
                  <p className="text-[11px] text-text-muted">
                    Requires {PLAN_LABELS[getRequiredPlan(service.feature)]} plan
                  </p>
                )}
              </div>
              {service.feature && !hasFeature(service.feature) ? (
                <span
                  className="inline-flex items-center gap-1 rounded border border-border px-3 py-1.5 text-xs font-medium text-text-muted"
                  title={`Requires ${PLAN_LABELS[getRequiredPlan(service.feature)]} plan`}
                >
                  Locked <ArrowRight className="h-3.5 w-3.5" />
                </span>
              ) : (
                <Link
                  href={service.path}
                  className="inline-flex items-center gap-1 rounded border border-primary px-3 py-1.5 text-xs font-medium text-primary hover:bg-primary/5"
                >
                  Open <ArrowRight className="h-3.5 w-3.5" />
                </Link>
              )}
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
