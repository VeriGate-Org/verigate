"use client";

import { useCallback, useMemo } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { User, ShieldCheck, Palette, Key, Bell, Building2, Sun } from "lucide-react";
import { useTenantFeatures } from "@/lib/tenant/PartnerTenantProvider";
import { Feature } from "@/lib/tenant-features";
import {
  ProfileTab,
  PlanFeaturesTab,
  BrandingTab,
  ApiKeysTab,
  NotificationsTab,
  DeedsOpsTab,
  AppearanceTab,
} from "./tabs";

const ALL_TABS = [
  { id: "profile", label: "Profile", icon: User },
  { id: "plan-features", label: "Entitlements", icon: ShieldCheck },
  { id: "branding", label: "Branding", icon: Palette },
  { id: "api-keys", label: "API Keys", icon: Key },
  { id: "notifications", label: "Notifications", icon: Bell },
  { id: "deeds-ops", label: "Deeds Ops", icon: Building2, feature: Feature.DEEDS_ADMIN },
  { id: "appearance", label: "Appearance", icon: Sun },
] as const;

type TabId = (typeof ALL_TABS)[number]["id"];

const TAB_COMPONENTS: Record<TabId, React.ComponentType> = {
  profile: ProfileTab,
  "plan-features": PlanFeaturesTab,
  branding: BrandingTab,
  "api-keys": ApiKeysTab,
  notifications: NotificationsTab,
  "deeds-ops": DeedsOpsTab,
  appearance: AppearanceTab,
};

export default function Settings() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const { hasFeature } = useTenantFeatures();

  const tabs = useMemo(
    () => ALL_TABS.filter((tab) => !("feature" in tab) || hasFeature(tab.feature)),
    [hasFeature],
  );

  const tabParam = searchParams.get("tab") as TabId | null;
  const activeTab: TabId = tabs.some((t) => t.id === tabParam) ? tabParam! : "profile";

  const setTab = useCallback(
    (tab: TabId) => {
      router.push(`/settings?tab=${tab}`);
    },
    [router],
  );

  const ActiveComponent = TAB_COMPONENTS[activeTab];

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-1">
        <h1 className="text-xl font-semibold text-text">Settings</h1>
        <p className="text-sm text-text-muted">
          Manage your partner account, API keys, notifications, and appearance preferences.
        </p>
      </div>

      <div className="border-b border-border">
        <nav className="flex gap-0 -mb-px">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setTab(tab.id)}
                className={`flex items-center gap-2 px-4 py-3 text-sm font-medium border-b-2 transition-colors ${
                  isActive
                    ? "border-accent text-accent"
                    : "border-transparent text-text-muted hover:text-text hover:border-border"
                }`}
              >
                <Icon className="w-4 h-4" />
                {tab.label}
              </button>
            );
          })}
        </nav>
      </div>

      <ActiveComponent />
    </div>
  );
}
