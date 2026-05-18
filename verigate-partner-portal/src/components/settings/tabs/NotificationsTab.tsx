"use client";

import { useCallback, useEffect, useState } from "react";
import { useToast } from "@/components/ui/Toast";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import {
  getNotifications,
  updateNotifications,
  type BffNotificationPreferences,
} from "@/lib/bff-client";

export default function NotificationsTab() {
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
          <div className="space-y-3">
            <Skeleton className="h-12 w-full" />
            <Skeleton className="h-12 w-full" />
            <Skeleton className="h-12 w-full" />
            <Skeleton className="h-12 w-full" />
          </div>
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
