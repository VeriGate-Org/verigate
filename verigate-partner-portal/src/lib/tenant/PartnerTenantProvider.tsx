"use client";

import * as React from "react";
import { getProfile, type BffProfileResponse } from "@/lib/bff-client";
import {
  Feature,
  FEATURE_LABELS,
  PLAN_LABELS,
  type FeatureKey,
  type Plan,
  getRequiredPlan,
  normalizeEnabledFeatures,
  normalizePlan,
  tenantHasFeature,
} from "@/lib/tenant-features";
import type { TenantBranding } from "@/lib/types/tenant-branding";
import { darken, lighten, withOpacity } from "@/lib/utils/color";
import { config } from "@/lib/config";

interface TenantFeatureContextValue {
  profile: BffProfileResponse;
  loading: boolean;
  hasFeature: (feature: FeatureKey) => boolean;
  requiredPlan: (feature: FeatureKey) => Plan;
  refreshProfile: () => Promise<void>;
  branding: TenantBranding | null;
  isWhiteLabelled: boolean;
  tenantSlug: string | null;
}

const ROOT_DOMAIN = process.env.NEXT_PUBLIC_ROOT_DOMAIN || "localhost:3000";

const defaultProfile: BffProfileResponse = {
  partnerId: "partner-portal",
  name: "VeriGate Partner",
  contactEmail: "",
  billingPlan: "enterprise",
  enabledFeatures: [],
  resolvedFeatures: Object.values(Feature),
  quotas: {
    maxUsers: 10_000,
    maxApiKeys: 100,
    maxSavedReports: 1_000,
    maxMonitoringSubjects: 10_000,
  },
  status: "ACTIVE",
  createdAt: null,
};

const TenantFeatureContext = React.createContext<TenantFeatureContextValue>({
  profile: defaultProfile,
  loading: false,
  hasFeature: () => true,
  requiredPlan: getRequiredPlan,
  refreshProfile: async () => {},
  branding: null,
  isWhiteLabelled: false,
  tenantSlug: null,
});

/**
 * Extract the tenant slug from the current hostname.
 * Returns null if on the root domain (no subdomain).
 */
function detectSlug(): string | null {
  if (typeof window === "undefined") return null;

  const host = window.location.hostname;
  const root = ROOT_DOMAIN.split(":")[0];

  // localhost support: acme.localhost → "acme"
  if (root === "localhost" && host.endsWith(".localhost")) {
    const slug = host.replace(".localhost", "");
    return slug || null;
  }

  // Standard: acme.verigate.co.za → "acme"
  if (host.endsWith("." + root)) {
    const slug = host.slice(0, -(root.length + 1));
    return slug && !slug.includes(".") ? slug : null;
  }

  return null;
}

/** Inject tenant branding CSS variables on the document root. */
function applyBrandingCssVars(branding: TenantBranding) {
  const root = document.documentElement;
  if (branding.primaryColor) {
    root.style.setProperty("--color-accent", branding.primaryColor);
    root.style.setProperty(
      "--color-accent-strong",
      branding.secondaryColor ? branding.secondaryColor : darken(branding.primaryColor, 15),
    );
    root.style.setProperty("--color-accent-border", lighten(branding.primaryColor, 10));
    root.style.setProperty("--color-accent-soft", withOpacity(branding.primaryColor, 0.1));
    root.style.setProperty("--color-accent-muted", withOpacity(branding.primaryColor, 0.15));
  }
  if (branding.accentColor) {
    root.style.setProperty("--color-cta", branding.accentColor);
    root.style.setProperty("--color-cta-hover", darken(branding.accentColor, 10));
  }
}

/** Generate a shield SVG string for use as a dynamic favicon. */
function shieldFaviconSvg(primary: string, check: string): string {
  return `<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 28 28"><path fill="${primary}" d="M14 2c-3.8 0-7 1.33-7 1.33v7.7c0 5.2 3.4 10.03 7 12.24 3.6-2.21 7-7.04 7-12.24V3.33C21 3.33 17.8 2 14 2Z"/><path d="M8.5 14.5l3.5 3.5 7.5-7.5" fill="none" stroke="${check}" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/></svg>`;
}

/** Inject a branded favicon into the document head. */
function applyBrandingFavicon(branding: TenantBranding) {
  if (typeof document === "undefined") return;

  let link = document.querySelector<HTMLLinkElement>('link[rel="icon"]');
  if (!link) {
    link = document.createElement("link");
    link.rel = "icon";
    document.head.appendChild(link);
  }

  if (branding.faviconUrl) {
    link.type = "image/x-icon";
    link.href = branding.faviconUrl;
  } else {
    const svg = shieldFaviconSvg(branding.primaryColor || "#0972d3", "#ffffff");
    link.type = "image/svg+xml";
    link.href = `data:image/svg+xml,${encodeURIComponent(svg)}`;
  }
}

/** Remove injected branding CSS variables. */
function clearBrandingCssVars() {
  const root = document.documentElement;
  const vars = [
    "--color-accent",
    "--color-accent-strong",
    "--color-accent-border",
    "--color-accent-soft",
    "--color-accent-muted",
    "--color-cta",
    "--color-cta-hover",
  ];
  vars.forEach((v) => root.style.removeProperty(v));
}

export function PartnerTenantProvider({ children }: { children: React.ReactNode }) {
  const [profile, setProfile] = React.useState<BffProfileResponse>(defaultProfile);
  const [loading, setLoading] = React.useState(true);
  const [branding, setBranding] = React.useState<TenantBranding | null>(null);
  const [tenantSlug, setTenantSlug] = React.useState<string | null>(null);

  // ── Subdomain detection + branding fetch ────────────────────────
  React.useEffect(() => {
    const slug = detectSlug();
    setTenantSlug(slug);

    if (!slug) return;

    const bffUrl = config.bffBaseUrl;

    fetch(`${bffUrl}/api/public/tenant/${slug}`, {
      headers: { "Content-Type": "application/json" },
    })
      .then((res) => (res.ok ? res.json() : null))
      .then((data: TenantBranding | null) => {
        if (data) {
          setBranding(data);
          applyBrandingCssVars(data);
          applyBrandingFavicon(data);
        }
      })
      .catch(() => {
        // Branding fetch failure is non-critical — keep default styling
      });

    return () => clearBrandingCssVars();
  }, []);

  // ── Profile fetch + branding sync ───────────────────────────────
  const refreshProfile = React.useCallback(async () => {
    setLoading(true);
    try {
      const data = await getProfile();
      setProfile({
        ...data,
        billingPlan: normalizePlan(data.billingPlan),
        enabledFeatures: normalizeEnabledFeatures(data.enabledFeatures),
        resolvedFeatures: data.resolvedFeatures?.length
          ? normalizeEnabledFeatures(data.resolvedFeatures)
          : Object.values(Feature).filter((feature) =>
              tenantHasFeature(
                {
                  billingPlan: data.billingPlan,
                  enabledFeatures: data.enabledFeatures,
                },
                feature,
              ),
            ),
      });

      // Sync branding from profile so Settings saves take effect immediately
      const hasColors = data.primaryColor || data.accentColor;
      const hasAssets = data.logo || data.logoDark;
      if (hasColors || hasAssets) {
        const profileBranding: TenantBranding = {
          slug: data.partnerId ?? "",
          name: data.name ?? "",
          logo: data.logo ?? undefined,
          logoDark: data.logoDark ?? undefined,
          primaryColor: data.primaryColor ?? undefined,
          secondaryColor: data.secondaryColor ?? undefined,
          accentColor: data.accentColor ?? undefined,
          faviconUrl: data.faviconUrl ?? undefined,
          tagline: data.tagline ?? undefined,
          loginBackgroundUrl: data.loginBackgroundUrl ?? undefined,
          supportEmail: data.supportEmail ?? undefined,
        };
        setBranding(profileBranding);
        applyBrandingCssVars(profileBranding);
        applyBrandingFavicon(profileBranding);
      }
    } catch {
      setProfile(defaultProfile);
    } finally {
      setLoading(false);
    }
  }, []);

  React.useEffect(() => {
    void refreshProfile();
  }, [refreshProfile]);

  const hasFeature = React.useCallback(
    (feature: FeatureKey) =>
      tenantHasFeature(
        {
          billingPlan: profile.billingPlan,
          enabledFeatures: profile.enabledFeatures,
        },
        feature,
      ),
    [profile.billingPlan, profile.enabledFeatures],
  );

  const value = React.useMemo(
    () => ({
      profile,
      loading,
      hasFeature,
      requiredPlan: getRequiredPlan,
      refreshProfile,
      branding,
      isWhiteLabelled: !!branding && tenantSlug !== "default",
      tenantSlug,
    }),
    [profile, loading, hasFeature, refreshProfile, branding, tenantSlug],
  );

  return (
    <TenantFeatureContext.Provider value={value}>{children}</TenantFeatureContext.Provider>
  );
}

export function useTenantFeatures() {
  return React.useContext(TenantFeatureContext);
}

export { FEATURE_LABELS, PLAN_LABELS };
