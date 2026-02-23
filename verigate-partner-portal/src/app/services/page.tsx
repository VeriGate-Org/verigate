import Link from "next/link";
import { ArrowRight } from "lucide-react";

const SERVICES = [
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
  },
  {
    name: "Fraud watchlist screening",
    description: "Check against SAFPS and industry fraud databases.",
    path: "/services/fraud-watchlist",
  },
  {
    name: "Document verification",
    description: "Authenticate and validate uploaded identity documents.",
    path: "/services/document-verification",
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
] as const;

export default function ServicesIndexPage() {
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
              </div>
              <Link
                href={service.path}
                className="inline-flex items-center gap-1 rounded border border-primary px-3 py-1.5 text-xs font-medium text-primary hover:bg-primary/5"
              >
                Open <ArrowRight className="h-3.5 w-3.5" />
              </Link>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
