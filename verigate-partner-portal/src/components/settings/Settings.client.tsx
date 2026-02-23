"use client";

import { useCallback, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { useTheme } from "@/components/theme/ThemeProvider";

const TABS = [
  { id: "profile", label: "Profile" },
  { id: "api-keys", label: "API Keys" },
  { id: "notifications", label: "Notifications" },
  { id: "appearance", label: "Appearance" },
] as const;

type TabId = (typeof TABS)[number]["id"];

interface ApiKey {
  id: string;
  prefix: string;
  status: "active" | "revoked";
  created: string;
}

const INITIAL_API_KEYS: ApiKey[] = [
  { id: "1", prefix: "vg_abc1...", status: "active", created: "2025-11-02" },
  { id: "2", prefix: "vg_def2...", status: "active", created: "2025-12-14" },
  { id: "3", prefix: "vg_ghi3...", status: "revoked", created: "2025-08-20" },
];

interface NotificationPrefs {
  verificationComplete: boolean;
  verificationFailure: boolean;
  weeklySummary: boolean;
  securityAlerts: boolean;
}

const DEFAULT_NOTIFICATIONS: NotificationPrefs = {
  verificationComplete: true,
  verificationFailure: true,
  weeklySummary: false,
  securityAlerts: true,
};

export default function Settings() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const tabParam = searchParams.get("tab") as TabId | null;
  const activeTab: TabId = TABS.some((t) => t.id === tabParam) ? tabParam! : "profile";

  const setTab = useCallback(
    (tab: TabId) => {
      router.push(`/settings?tab=${tab}`);
    },
    [router],
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-1">
        <h1 className="text-xl font-semibold text-text">Settings</h1>
        <p className="text-sm text-text-muted">
          Manage your partner account, API keys, notifications, and appearance preferences.
        </p>
      </div>

      {/* Tab bar */}
      <div className="flex flex-wrap gap-2 border-b border-border pb-px">
        {TABS.map((tab) => {
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setTab(tab.id)}
              className={`rounded-t border px-4 py-2 text-[13px] font-medium transition-colors ${
                isActive
                  ? "border-border border-b-transparent bg-[color:var(--color-base-100)] text-[color:var(--color-accent-strong)]"
                  : "border-transparent text-text-muted hover:text-text"
              }`}
            >
              {tab.label}
            </button>
          );
        })}
      </div>

      {/* Tab content */}
      {activeTab === "profile" && <ProfileTab />}
      {activeTab === "api-keys" && <ApiKeysTab />}
      {activeTab === "notifications" && <NotificationsTab />}
      {activeTab === "appearance" && <AppearanceTab />}
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Profile Tab                                                        */
/* ------------------------------------------------------------------ */

function ProfileTab() {
  return (
    <div className="console-card">
      <div className="console-card-header">
        <div>
          <div className="text-sm font-semibold text-text">Partner profile</div>
          <div className="text-xs text-text-muted">Account details are managed by your organisation administrator.</div>
        </div>
      </div>
      <div className="console-card-body space-y-4">
        <ReadOnlyField label="Partner name" value="VeriGate Demo Partner" />
        <ReadOnlyField label="Email address" value="partner@verigate.co.za" />
        <ReadOnlyField label="Billing plan" value="Enterprise" />
        <div className="pt-2">
          <p className="text-xs text-text-muted">
            To update your profile or billing plan, contact your account manager or reach out to{" "}
            <a href="mailto:support@verigate.co.za" className="text-accent underline">
              support@verigate.co.za
            </a>
            .
          </p>
        </div>
      </div>
    </div>
  );
}

function ReadOnlyField({ label, value }: { label: string; value: string }) {
  return (
    <label className="block space-y-1 text-sm">
      <span className="font-medium text-text">{label}</span>
      <input
        type="text"
        readOnly
        value={value}
        className="aws-input w-full max-w-md cursor-default bg-[color:var(--color-base-200)] text-text-muted"
      />
    </label>
  );
}

/* ------------------------------------------------------------------ */
/*  API Keys Tab                                                       */
/* ------------------------------------------------------------------ */

function ApiKeysTab() {
  const [keys, setKeys] = useState<ApiKey[]>(INITIAL_API_KEYS);

  const handleGenerate = useCallback(() => {
    const randomSuffix = Math.random().toString(36).substring(2, 8);
    const mockFullKey = `vg_live_${randomSuffix}${"x".repeat(24)}`;
    alert(`New API key generated (copy it now, it will not be shown again):\n\n${mockFullKey}`);
    const newKey: ApiKey = {
      id: String(Date.now()),
      prefix: `vg_${randomSuffix.slice(0, 4)}...`,
      status: "active",
      created: new Date().toISOString().split("T")[0],
    };
    setKeys((prev) => [newKey, ...prev]);
  }, []);

  const handleRevoke = useCallback((id: string) => {
    const confirmed = window.confirm(
      "Are you sure you want to revoke this API key? This action cannot be undone and any integrations using this key will stop working.",
    );
    if (!confirmed) return;
    setKeys((prev) => prev.filter((k) => k.id !== id));
  }, []);

  return (
    <div className="space-y-4">
      <div className="console-card">
        <div className="console-card-header">
          <div>
            <div className="text-sm font-semibold text-text">API keys</div>
            <div className="text-xs text-text-muted">
              Manage the keys used to authenticate API requests from your integrations.
            </div>
          </div>
          <button onClick={handleGenerate} className="aws-button aws-button--primary text-xs">
            Generate New Key
          </button>
        </div>
        <div className="console-card-body p-0">
          <div className="overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead className="bg-[color:var(--color-base-200)] text-xs uppercase tracking-wide text-text-muted">
                <tr>
                  <th className="px-4 py-2.5">Prefix</th>
                  <th className="px-4 py-2.5">Status</th>
                  <th className="px-4 py-2.5">Created</th>
                  <th className="px-4 py-2.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {keys.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="px-4 py-8 text-center text-xs text-text-muted">
                      No API keys configured. Generate one to get started.
                    </td>
                  </tr>
                ) : (
                  keys.map((key) => (
                    <tr key={key.id} className="border-b border-border last:border-0">
                      <td className="px-4 py-2.5 font-mono text-sm text-text">{key.prefix}</td>
                      <td className="px-4 py-2.5">
                        <span
                          className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                            key.status === "active"
                              ? "bg-success/10 text-success"
                              : "bg-[color:var(--color-base-200)] text-text-muted"
                          }`}
                        >
                          {key.status === "active" ? "Active" : "Revoked"}
                        </span>
                      </td>
                      <td className="px-4 py-2.5 text-text-muted">{key.created}</td>
                      <td className="px-4 py-2.5 text-right">
                        {key.status === "active" && (
                          <button
                            onClick={() => handleRevoke(key.id)}
                            className="aws-button aws-button--destructive text-xs"
                          >
                            Revoke
                          </button>
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div className="console-card">
        <div className="console-card-header">
          <div className="text-sm font-semibold text-text">Usage notes</div>
        </div>
        <div className="console-card-body space-y-2 text-sm text-text-muted">
          <ul className="list-disc space-y-1 pl-5">
            <li>API keys grant full access to the VeriGate API on behalf of your partner account.</li>
            <li>Store keys securely and never expose them in client-side code or public repositories.</li>
            <li>Revoked keys take effect immediately. Rotate keys periodically for best security practice.</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Notifications Tab                                                  */
/* ------------------------------------------------------------------ */

function NotificationsTab() {
  const [prefs, setPrefs] = useState<NotificationPrefs>(DEFAULT_NOTIFICATIONS);

  const toggle = useCallback((key: keyof NotificationPrefs) => {
    setPrefs((prev) => ({ ...prev, [key]: !prev[key] }));
  }, []);

  const NOTIFICATION_OPTIONS: { key: keyof NotificationPrefs; label: string; description: string }[] = [
    {
      key: "verificationComplete",
      label: "Email on verification complete",
      description: "Receive an email each time a verification check completes successfully.",
    },
    {
      key: "verificationFailure",
      label: "Email on verification failure",
      description: "Receive an email when a verification results in a soft or hard failure.",
    },
    {
      key: "weeklySummary",
      label: "Weekly summary report",
      description: "Get a weekly digest of verification volume, success rates, and trends.",
    },
    {
      key: "securityAlerts",
      label: "Security alerts",
      description: "Receive immediate alerts for suspicious activity, failed auth attempts, or policy changes.",
    },
  ];

  return (
    <div className="console-card">
      <div className="console-card-header">
        <div>
          <div className="text-sm font-semibold text-text">Notification preferences</div>
          <div className="text-xs text-text-muted">Choose which emails and alerts you receive.</div>
        </div>
      </div>
      <div className="console-card-body space-y-5">
        {NOTIFICATION_OPTIONS.map((opt) => (
          <label
            key={opt.key}
            className="flex cursor-pointer items-start gap-3 rounded border border-transparent p-2 transition-colors hover:bg-hover"
          >
            {/* Toggle switch */}
            <span className="relative mt-0.5 inline-flex h-5 w-9 shrink-0">
              <input
                type="checkbox"
                checked={prefs[opt.key]}
                onChange={() => toggle(opt.key)}
                className="peer sr-only"
              />
              <span
                className={`block h-5 w-9 rounded-full transition-colors ${
                  prefs[opt.key] ? "bg-accent" : "bg-[color:var(--color-base-300)]"
                }`}
              />
              <span
                className={`absolute left-0.5 top-0.5 h-4 w-4 rounded-full bg-[color:var(--color-base-100)] shadow transition-transform ${
                  prefs[opt.key] ? "translate-x-4" : "translate-x-0"
                }`}
              />
            </span>
            <span className="space-y-0.5">
              <span className="block text-sm font-medium text-text">{opt.label}</span>
              <span className="block text-xs text-text-muted">{opt.description}</span>
            </span>
          </label>
        ))}

        <div className="pt-2 text-xs text-text-muted">
          Changes are saved automatically. Email delivery may take up to 5 minutes to take effect.
        </div>
      </div>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Appearance Tab                                                     */
/* ------------------------------------------------------------------ */

type ThemeOption = "light" | "dark" | "system";

function AppearanceTab() {
  const { theme, setTheme } = useTheme();
  const [selected, setSelected] = useState<ThemeOption>(() => {
    if (typeof window !== "undefined") {
      const stored = window.localStorage.getItem("verigate-theme");
      if (stored === "light" || stored === "dark") return stored;
    }
    return "system";
  });

  const handleChange = useCallback(
    (option: ThemeOption) => {
      setSelected(option);
      if (option === "system") {
        const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
        setTheme(prefersDark ? "dark" : "light");
        window.localStorage.removeItem("verigate-theme");
      } else {
        setTheme(option);
      }
    },
    [setTheme],
  );

  const THEME_OPTIONS: { value: ThemeOption; label: string; description: string }[] = [
    { value: "light", label: "Light", description: "Classic light interface with white backgrounds." },
    { value: "dark", label: "Dark", description: "Reduced-brightness interface for low-light environments." },
    { value: "system", label: "System", description: "Automatically match your operating system preference." },
  ];

  return (
    <div className="console-card">
      <div className="console-card-header">
        <div>
          <div className="text-sm font-semibold text-text">Theme</div>
          <div className="text-xs text-text-muted">Select a colour scheme for the partner portal.</div>
        </div>
      </div>
      <div className="console-card-body">
        <div className="grid gap-3 sm:grid-cols-3">
          {THEME_OPTIONS.map((opt) => {
            const isActive = selected === opt.value;
            return (
              <button
                key={opt.value}
                onClick={() => handleChange(opt.value)}
                className={`flex flex-col items-start gap-2 rounded-lg border p-4 text-left transition-all ${
                  isActive
                    ? "border-accent bg-accent-soft shadow-sm"
                    : "border-border bg-[color:var(--color-base-100)] hover:border-accent/50"
                }`}
              >
                {/* Theme preview swatch */}
                <div
                  className={`h-10 w-full rounded border ${
                    opt.value === "light"
                      ? "border-gray-300 bg-[color:var(--color-base-100)]"
                      : opt.value === "dark"
                        ? "border-gray-600 bg-gray-800"
                        : "bg-gradient-to-r from-white to-gray-800 border-gray-400"
                  }`}
                />
                <div>
                  <div className="text-sm font-medium text-text">{opt.label}</div>
                  <div className="text-xs text-text-muted">{opt.description}</div>
                </div>
                {isActive && (
                  <span className="inline-flex items-center rounded-full bg-accent px-2 py-0.5 text-[10px] font-medium text-white">
                    Active
                  </span>
                )}
              </button>
            );
          })}
        </div>

        <div className="mt-4 text-xs text-text-muted">
          Current resolved theme: <span className="font-medium text-text">{theme}</span>
        </div>
      </div>
    </div>
  );
}
