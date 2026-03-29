"use client";

import { usePathname } from "next/navigation";
import { AuthProvider } from "@/lib/auth/AuthProvider";
import { AuthGuard } from "@/lib/auth/AuthGuard";
import {
  FEATURE_LABELS,
  PLAN_LABELS,
  PartnerTenantProvider,
  useTenantFeatures,
} from "@/lib/tenant/PartnerTenantProvider";
import { Feature, type FeatureKey } from "@/lib/tenant-features";
import TopNav from "@/components/TopNav";
import Sidebar from "@/components/Sidebar";
import type { ReactNode } from "react";

const CHROME_HIDDEN_PATHS = ["/signin"];
const ROUTE_FEATURES: Array<{ path: string; feature: FeatureKey }> = [
  { path: "/policies", feature: Feature.POLICY_BUILDER },
  { path: "/monitoring", feature: Feature.MONITORING },
  { path: "/reports", feature: Feature.REPORTING },
  { path: "/services/property-ownership", feature: Feature.DEEDS_REGISTRY },
  { path: "/services/deeds-map", feature: Feature.DEEDS_MAP },
  { path: "/services/property-conversion", feature: Feature.DEEDS_CONVERSION },
  { path: "/services/property-valuation", feature: Feature.DEEDS_VALUATION },
];

export default function AppShell({ children }: { children: ReactNode }) {
  return (
    <AuthProvider>
      <AuthGuard>
        <PartnerTenantProvider>
          <ShellChrome>{children}</ShellChrome>
        </PartnerTenantProvider>
      </AuthGuard>
    </AuthProvider>
  );
}

function ShellChrome({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const { hasFeature, loading, requiredPlan } = useTenantFeatures();
  const hideChrome = CHROME_HIDDEN_PATHS.some(
    (p) => pathname === p || pathname.startsWith(p + "/"),
  );
  const matchedFeature = ROUTE_FEATURES.find(
    (item) => pathname === item.path || pathname.startsWith(`${item.path}/`),
  )?.feature;
  const isBlocked = Boolean(matchedFeature) && !loading && !hasFeature(matchedFeature!);
  const blockedContent = matchedFeature ? (
    <div className="console-card">
      <div className="console-card-body space-y-2 py-10">
        <div className="text-lg font-semibold text-text">Plan upgrade required</div>
        <p className="text-sm text-text-muted">
          {FEATURE_LABELS[matchedFeature]} requires the {PLAN_LABELS[requiredPlan(matchedFeature)]} plan or a tenant feature override.
        </p>
      </div>
    </div>
  ) : null;

  return (
    hideChrome ? (
      isBlocked ? blockedContent : children
    ) : (
      <>
        <a
          href="#main-content"
          className="sr-only focus:not-sr-only focus:fixed focus:top-2 focus:left-2 focus:z-[999] focus:rounded focus:bg-accent focus:px-4 focus:py-2 focus:text-sm focus:font-medium focus:text-white focus:shadow-lg"
        >
          Skip to content
        </a>
        <TopNav />
        <div className="flex pt-14">
          <Sidebar />
          <main id="main-content" className="flex-1 md:ml-60 p-6 bg-background min-h-screen">
            {isBlocked ? blockedContent : children}
          </main>
        </div>
      </>
    )
  );
}
