"use client";

import { useCallback, useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { useTheme } from "@/components/theme/ThemeProvider";
import { useToast } from "@/components/ui/Toast";
import {
  getProfile,
  listApiKeys,
  generateApiKey,
  revokeApiKey,
  getNotifications,
  updateNotifications,
  createDeedsTeamMember,
  deleteDeedsTeamMember,
  listDeedsAuditEvents,
  listDeedsTeamMembers,
  runDeedsRefreshCycle,
  updateDeedsTeamMember,
  type BffProfileResponse,
  type BffNotificationPreferences,
  type BffDeedsAuditEventResponse,
  type BffDeedsTeamMemberResponse,
} from "@/lib/bff-client";

const TABS = [
  { id: "profile", label: "Profile" },
  { id: "api-keys", label: "API Keys" },
  { id: "notifications", label: "Notifications" },
  { id: "deeds-ops", label: "Deeds Ops" },
  { id: "appearance", label: "Appearance" },
] as const;

type TabId = (typeof TABS)[number]["id"];

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

      {activeTab === "profile" && <ProfileTab />}
      {activeTab === "api-keys" && <ApiKeysTab />}
      {activeTab === "notifications" && <NotificationsTab />}
      {activeTab === "deeds-ops" && <DeedsOpsTab />}
      {activeTab === "appearance" && <AppearanceTab />}
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Profile Tab                                                        */
/* ------------------------------------------------------------------ */

function ProfileTab() {
  const [profile, setProfile] = useState<BffProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getProfile()
      .then(setProfile)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="console-card">
        <div className="console-card-body py-8 text-center text-sm text-text-muted">Loading profile…</div>
      </div>
    );
  }

  return (
    <div className="console-card">
      <div className="console-card-header">
        <div>
          <div className="text-sm font-semibold text-text">Partner profile</div>
          <div className="text-xs text-text-muted">Account details are managed by your organisation administrator.</div>
        </div>
      </div>
      <div className="console-card-body space-y-4">
        <ReadOnlyField label="Partner name" value={profile?.name ?? "—"} />
        <ReadOnlyField label="Email address" value={profile?.contactEmail ?? "—"} />
        <ReadOnlyField label="Billing plan" value={profile?.billingPlan ?? "—"} />
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

interface ApiKeyRow {
  keyPrefix: string;
  status: string;
  createdAt: string | null;
  createdBy: string | null;
}

function ApiKeysTab() {
  const { toast } = useToast();
  const [keys, setKeys] = useState<ApiKeyRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [generatedKey, setGeneratedKey] = useState<string | null>(null);

  const loadKeys = useCallback(() => {
    setLoading(true);
    listApiKeys()
      .then((res) => setKeys(res.keys))
      .catch(() => toast({ title: "Failed to load API keys", variant: "error" }))
      .finally(() => setLoading(false));
  }, [toast]);

  useEffect(() => { loadKeys(); }, [loadKeys]);

  const handleGenerate = useCallback(async () => {
    try {
      const res = await generateApiKey();
      setGeneratedKey(res.apiKey);
      loadKeys();
      toast({ title: "API key generated", description: "Copy it now — it won't be shown again.", variant: "success" });
    } catch (err) {
      toast({ title: "Generation failed", description: err instanceof Error ? err.message : "Could not generate key.", variant: "error" });
    }
  }, [loadKeys, toast]);

  const handleRevoke = useCallback(async (prefix: string) => {
    const confirmed = window.confirm(
      "Are you sure you want to revoke this API key? This action cannot be undone and any integrations using this key will stop working.",
    );
    if (!confirmed) return;
    try {
      await revokeApiKey(prefix);
      loadKeys();
      toast({ title: "API key revoked", variant: "success" });
    } catch (err) {
      toast({ title: "Revocation failed", description: err instanceof Error ? err.message : "Could not revoke key.", variant: "error" });
    }
  }, [loadKeys, toast]);

  return (
    <div className="space-y-4">
      {/* One-time key display modal */}
      {generatedKey && (
        <div className="console-card border-success/40 bg-success/5">
          <div className="console-card-body space-y-2">
            <div className="text-sm font-semibold text-text">Your new API key</div>
            <p className="text-xs text-text-muted">
              Copy this key now. For security, it will not be displayed again.
            </p>
            <div className="flex items-center gap-2">
              <code className="flex-1 rounded border border-border bg-[color:var(--color-base-200)] px-3 py-2 font-mono text-sm text-text">
                {generatedKey}
              </code>
              <button
                onClick={() => {
                  navigator.clipboard.writeText(generatedKey);
                  toast({ title: "Copied to clipboard", variant: "success" });
                }}
                className="aws-button aws-button--primary text-xs"
              >
                Copy
              </button>
            </div>
            <button
              onClick={() => setGeneratedKey(null)}
              className="text-xs text-text-muted hover:text-text"
            >
              Dismiss
            </button>
          </div>
        </div>
      )}

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
                {loading ? (
                  <tr>
                    <td colSpan={4} className="px-4 py-8 text-center text-xs text-text-muted">
                      Loading API keys…
                    </td>
                  </tr>
                ) : keys.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="px-4 py-8 text-center text-xs text-text-muted">
                      No API keys configured. Generate one to get started.
                    </td>
                  </tr>
                ) : (
                  keys.map((key) => (
                    <tr key={key.keyPrefix} className="border-b border-border last:border-0">
                      <td className="px-4 py-2.5 font-mono text-sm text-text">{key.keyPrefix}…</td>
                      <td className="px-4 py-2.5">
                        <span
                          className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                            key.status === "ACTIVE"
                              ? "bg-success/10 text-success"
                              : "bg-[color:var(--color-base-200)] text-text-muted"
                          }`}
                        >
                          {key.status === "ACTIVE" ? "Active" : "Revoked"}
                        </span>
                      </td>
                      <td className="px-4 py-2.5 text-text-muted">{key.createdAt ?? "—"}</td>
                      <td className="px-4 py-2.5 text-right">
                        {key.status === "ACTIVE" && (
                          <button
                            onClick={() => handleRevoke(key.keyPrefix)}
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
  const { toast } = useToast();
  const [prefs, setPrefs] = useState<BffNotificationPreferences>({
    verificationComplete: true,
    verificationFailure: true,
    weeklySummary: false,
    securityAlerts: true,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getNotifications()
      .then(setPrefs)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const toggle = useCallback(
    async (key: keyof BffNotificationPreferences) => {
      const updated = { ...prefs, [key]: !prefs[key] };
      setPrefs(updated);
      try {
        await updateNotifications(updated);
      } catch {
        setPrefs(prefs);
        toast({ title: "Failed to save", description: "Could not update notification preferences.", variant: "error" });
      }
    },
    [prefs, toast],
  );

  const NOTIFICATION_OPTIONS: { key: keyof BffNotificationPreferences; label: string; description: string }[] = [
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
        {loading ? (
          <div className="py-4 text-center text-sm text-text-muted">Loading preferences…</div>
        ) : (
          NOTIFICATION_OPTIONS.map((opt) => (
            <label
              key={opt.key}
              className="flex cursor-pointer items-start gap-3 rounded border border-transparent p-2 transition-colors hover:bg-hover"
            >
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
          ))
        )}

        <div className="pt-2 text-xs text-text-muted">
          Changes are saved automatically. Email delivery may take up to 5 minutes to take effect.
        </div>
      </div>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Deeds Ops Tab                                                      */
/* ------------------------------------------------------------------ */

function DeedsOpsTab() {
  const { toast } = useToast();
  const [team, setTeam] = useState<BffDeedsTeamMemberResponse[]>([]);
  const [audit, setAudit] = useState<BffDeedsAuditEventResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [memberName, setMemberName] = useState("");
  const [memberEmail, setMemberEmail] = useState("");
  const [memberRole, setMemberRole] = useState("deeds_operator");
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(() => {
    setLoading(true);
    Promise.all([listDeedsTeamMembers(), listDeedsAuditEvents()])
      .then(([teamData, auditData]) => {
        setTeam(teamData);
        setAudit(auditData);
      })
      .catch(() => toast({ title: "Failed to load deeds operations data", variant: "error" }))
      .finally(() => setLoading(false));
  }, [toast]);

  useEffect(() => {
    void load();
  }, [load]);

  const handleCreateMember = useCallback(async () => {
    if (!memberName.trim() || !memberEmail.trim()) return;
    try {
      await createDeedsTeamMember({
        name: memberName,
        email: memberEmail,
        role: memberRole,
        status: "ACTIVE",
      });
      setMemberName("");
      setMemberEmail("");
      setMemberRole("deeds_operator");
      load();
      toast({ title: "Team member added", variant: "success" });
    } catch (err) {
      toast({ title: "Add member failed", description: err instanceof Error ? err.message : "Could not add member.", variant: "error" });
    }
  }, [load, memberEmail, memberName, memberRole, toast]);

  const toggleMemberStatus = useCallback(async (member: BffDeedsTeamMemberResponse) => {
    try {
      await updateDeedsTeamMember(member.id, {
        status: member.status === "ACTIVE" ? "PAUSED" : "ACTIVE",
      });
      load();
    } catch (err) {
      toast({ title: "Update failed", description: err instanceof Error ? err.message : "Could not update member.", variant: "error" });
    }
  }, [load, toast]);

  const handleDeleteMember = useCallback(async (memberId: string) => {
    try {
      await deleteDeedsTeamMember(memberId);
      load();
    } catch (err) {
      toast({ title: "Delete failed", description: err instanceof Error ? err.message : "Could not remove member.", variant: "error" });
    }
  }, [load, toast]);

  const handleRefresh = useCallback(async () => {
    setRefreshing(true);
    try {
      const result = await runDeedsRefreshCycle();
      toast({
        title: "Refresh cycle complete",
        description: `${result.refreshedReports} reports refreshed, ${result.recalculatedWatches} watches recalculated.`,
        variant: "success",
      });
      load();
    } catch (err) {
      toast({ title: "Refresh failed", description: err instanceof Error ? err.message : "Could not run refresh cycle.", variant: "error" });
    } finally {
      setRefreshing(false);
    }
  }, [load, toast]);

  return (
    <div className="space-y-6">
      <div className="console-card">
        <div className="console-card-header">
          <div>
            <div className="text-sm font-semibold text-text">Deeds refresh jobs</div>
            <div className="text-xs text-text-muted">
              Trigger the provider-independent refresh cycle for saved reports and cached watches.
            </div>
          </div>
          <button onClick={handleRefresh} className="aws-button aws-button--primary text-xs" disabled={refreshing}>
            {refreshing ? "Running…" : "Run refresh cycle"}
          </button>
        </div>
      </div>

      <div className="console-card">
        <div className="console-card-header">
          <div>
            <div className="text-sm font-semibold text-text">Deeds team</div>
            <div className="text-xs text-text-muted">Manage delegated deeds admins, operators, and viewers.</div>
          </div>
        </div>
        <div className="console-card-body space-y-4">
          <div className="grid gap-3 md:grid-cols-4">
            <input className="aws-input" placeholder="Name" value={memberName} onChange={(e) => setMemberName(e.target.value)} />
            <input className="aws-input" placeholder="Email" value={memberEmail} onChange={(e) => setMemberEmail(e.target.value)} />
            <select className="aws-select" value={memberRole} onChange={(e) => setMemberRole(e.target.value)}>
              <option value="deeds_admin">Deeds admin</option>
              <option value="deeds_operator">Deeds operator</option>
              <option value="deeds_viewer">Deeds viewer</option>
            </select>
            <button onClick={handleCreateMember} className="aws-button aws-button--primary text-xs">
              Add member
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead className="bg-[color:var(--color-base-200)] text-xs uppercase tracking-wide text-text-muted">
                <tr>
                  <th className="px-4 py-2.5">Name</th>
                  <th className="px-4 py-2.5">Role</th>
                  <th className="px-4 py-2.5">Status</th>
                  <th className="px-4 py-2.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr><td colSpan={4} className="px-4 py-8 text-center text-xs text-text-muted">Loading deeds team…</td></tr>
                ) : team.length === 0 ? (
                  <tr><td colSpan={4} className="px-4 py-8 text-center text-xs text-text-muted">No delegated deeds users configured.</td></tr>
                ) : (
                  team.map((member) => (
                    <tr key={member.id} className="border-b border-border last:border-0">
                      <td className="px-4 py-2.5">
                        <div className="font-medium text-text">{member.name}</div>
                        <div className="text-xs text-text-muted">{member.email}</div>
                      </td>
                      <td className="px-4 py-2.5 text-text-muted">{member.role}</td>
                      <td className="px-4 py-2.5 text-text-muted">{member.status}</td>
                      <td className="px-4 py-2.5 text-right space-x-2">
                        <button onClick={() => toggleMemberStatus(member)} className="aws-button aws-button--secondary text-xs">
                          {member.status === "ACTIVE" ? "Pause" : "Resume"}
                        </button>
                        <button onClick={() => handleDeleteMember(member.id)} className="aws-button aws-button--destructive text-xs">
                          Remove
                        </button>
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
          <div>
            <div className="text-sm font-semibold text-text">Deeds audit log</div>
            <div className="text-xs text-text-muted">Recent provider-independent deeds operations and admin actions.</div>
          </div>
        </div>
        <div className="console-card-body space-y-3">
          {audit.length === 0 ? (
            <div className="text-sm text-text-muted">No deeds audit events recorded yet.</div>
          ) : (
            audit.slice(0, 12).map((event) => (
              <div key={event.id} className="rounded border border-border bg-[color:var(--color-base-200)] p-3">
                <div className="flex flex-wrap items-start justify-between gap-2">
                  <div>
                    <div className="text-sm font-medium text-text">{event.action}</div>
                    <div className="text-xs text-text-muted">{event.category} • {event.actor}</div>
                  </div>
                  <div className="text-xs text-text-muted">{event.createdAt ?? "—"}</div>
                </div>
                {event.targetId && (
                  <div className="mt-2 text-xs text-text-muted">
                    {event.targetType}: {event.targetId}
                  </div>
                )}
              </div>
            ))
          )}
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
