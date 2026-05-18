"use client";

import { useCallback, useEffect, useState } from "react";
import { useToast } from "@/components/ui/Toast";
import { SkeletonCard } from "@/components/ui/Loading/Skeleton";
import { useTenantFeatures } from "@/lib/tenant/PartnerTenantProvider";
import { darken, lighten, withOpacity } from "@/lib/utils/color";
import { getProfile, updateProfile } from "@/lib/bff-client";

export default function BrandingTab() {
  const { toast } = useToast();
  const { refreshProfile } = useTenantFeatures();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    logo: "",
    logoDark: "",
    primaryColor: "#0972d3",
    accentColor: "#ec7211",
    faviconUrl: "",
    tagline: "",
  });

  useEffect(() => {
    getProfile()
      .then((profile) => {
        setForm({
          logo: profile.logo ?? "",
          logoDark: profile.logoDark ?? "",
          primaryColor: profile.primaryColor ?? "#0972d3",
          accentColor: profile.accentColor ?? "#ec7211",
          faviconUrl: profile.faviconUrl ?? "",
          tagline: profile.tagline ?? "",
        });
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handleSave = useCallback(async () => {
    setSaving(true);
    try {
      await updateProfile({
        logo: form.logo || undefined,
        logoDark: form.logoDark || undefined,
        primaryColor: form.primaryColor || undefined,
        accentColor: form.accentColor || undefined,
        faviconUrl: form.faviconUrl || undefined,
        tagline: form.tagline || undefined,
      });
      await refreshProfile();
      toast({ title: "Branding saved", variant: "success" });
    } catch (err) {
      toast({
        title: "Save failed",
        description: err instanceof Error ? err.message : "Could not save branding.",
        variant: "error",
      });
    } finally {
      setSaving(false);
    }
  }, [form, refreshProfile, toast]);

  const update = (key: keyof typeof form, value: string) =>
    setForm((prev) => ({ ...prev, [key]: value }));

  if (loading) {
    return <SkeletonCard />;
  }

  return (
    <div className="space-y-6">
      <div className="console-card">
        <div className="console-card-header">
          <div>
            <div className="text-sm font-semibold text-text">Brand identity</div>
            <div className="text-xs text-text-muted">
              Customise the portal appearance for your organisation. Partners on a custom subdomain will see this branding.
            </div>
          </div>
          <button onClick={handleSave} disabled={saving} className="aws-button aws-button--primary text-xs">
            {saving ? "Saving..." : "Save changes"}
          </button>
        </div>
        <div className="console-card-body space-y-5">
          {/* Logo URLs */}
          <div className="grid gap-4 md:grid-cols-2">
            <label className="block space-y-1 text-sm">
              <span className="font-medium text-text">Logo URL (light mode)</span>
              <input
                type="url"
                className="aws-input w-full"
                placeholder="https://example.com/logo.svg"
                value={form.logo}
                onChange={(e) => update("logo", e.target.value)}
              />
              <span className="text-xs text-text-muted">SVG or PNG recommended. Leave blank for default VeriGate shield.</span>
            </label>
            <label className="block space-y-1 text-sm">
              <span className="font-medium text-text">Logo URL (dark mode)</span>
              <input
                type="url"
                className="aws-input w-full"
                placeholder="https://example.com/logo-dark.svg"
                value={form.logoDark}
                onChange={(e) => update("logoDark", e.target.value)}
              />
              <span className="text-xs text-text-muted">Optional. Falls back to the light mode logo.</span>
            </label>
          </div>

          {/* Color Pickers */}
          <div className="grid gap-4 md:grid-cols-2">
            <label className="block space-y-1 text-sm">
              <span className="font-medium text-text">Primary colour</span>
              <div className="flex items-center gap-2">
                <input
                  type="color"
                  className="h-9 w-12 cursor-pointer rounded border border-border"
                  value={form.primaryColor}
                  onChange={(e) => update("primaryColor", e.target.value)}
                />
                <input
                  type="text"
                  className="aws-input flex-1"
                  value={form.primaryColor}
                  onChange={(e) => update("primaryColor", e.target.value)}
                  pattern="^#[0-9a-fA-F]{6}$"
                  placeholder="#0972d3"
                />
              </div>
              <span className="text-xs text-text-muted">Used for buttons, links, and accent elements.</span>
            </label>
            <label className="block space-y-1 text-sm">
              <span className="font-medium text-text">Accent colour</span>
              <div className="flex items-center gap-2">
                <input
                  type="color"
                  className="h-9 w-12 cursor-pointer rounded border border-border"
                  value={form.accentColor}
                  onChange={(e) => update("accentColor", e.target.value)}
                />
                <input
                  type="text"
                  className="aws-input flex-1"
                  value={form.accentColor}
                  onChange={(e) => update("accentColor", e.target.value)}
                  pattern="^#[0-9a-fA-F]{6}$"
                  placeholder="#ec7211"
                />
              </div>
              <span className="text-xs text-text-muted">Used for call-to-action buttons and highlights.</span>
            </label>
          </div>

          {/* Favicon + Tagline */}
          <div className="grid gap-4 md:grid-cols-2">
            <label className="block space-y-1 text-sm">
              <span className="font-medium text-text">Favicon URL</span>
              <input
                type="url"
                className="aws-input w-full"
                placeholder="https://example.com/favicon.ico"
                value={form.faviconUrl}
                onChange={(e) => update("faviconUrl", e.target.value)}
              />
              <span className="text-xs text-text-muted">Leave blank for auto-generated shield favicon.</span>
            </label>
            <label className="block space-y-1 text-sm">
              <span className="font-medium text-text">Tagline</span>
              <input
                type="text"
                className="aws-input w-full"
                placeholder="Trust, verified."
                value={form.tagline}
                onChange={(e) => update("tagline", e.target.value)}
              />
              <span className="text-xs text-text-muted">Short slogan shown alongside your brand name.</span>
            </label>
          </div>
        </div>
      </div>

      {/* Live Preview */}
      <div className="console-card">
        <div className="console-card-header">
          <div className="text-sm font-semibold text-text">Live preview</div>
        </div>
        <div className="console-card-body">
          <div className="rounded-lg border border-border overflow-hidden">
            {/* Preview header bar */}
            <div className="flex items-center gap-3 px-4 py-3 border-b border-border bg-[color:var(--color-base-100)]">
              {form.logo ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img src={form.logo} alt="Preview logo" className="h-7 w-auto" />
              ) : (
                <svg width="28" height="28" viewBox="0 0 28 28" className="flex-shrink-0">
                  <path fill={form.primaryColor} d="M14 2c-3.8 0-7 1.33-7 1.33v7.7c0 5.2 3.4 10.03 7 12.24 3.6-2.21 7-7.04 7-12.24V3.33C21 3.33 17.8 2 14 2Z" />
                  <path d="M8.5 14.5l3.5 3.5 7.5-7.5" fill="none" stroke="#fff" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              )}
              <span className="text-sm font-semibold text-text">{form.tagline || "Partner Portal"}</span>
            </div>

            {/* Preview content */}
            <div className="p-4 space-y-3 bg-[color:var(--color-base-200)]">
              <div className="flex gap-2">
                <button
                  className="rounded px-3 py-1.5 text-xs font-medium text-white"
                  style={{ backgroundColor: form.primaryColor }}
                >
                  Primary Button
                </button>
                <button
                  className="rounded px-3 py-1.5 text-xs font-medium text-white"
                  style={{ backgroundColor: form.accentColor }}
                >
                  CTA Button
                </button>
              </div>
              <div className="flex gap-3 text-xs">
                <div className="rounded border p-2" style={{ borderColor: lighten(form.primaryColor, 10), backgroundColor: withOpacity(form.primaryColor, 0.1) }}>
                  <span style={{ color: darken(form.primaryColor, 15) }}>Accent soft</span>
                </div>
                <div className="rounded border p-2" style={{ borderColor: lighten(form.primaryColor, 10), color: darken(form.primaryColor, 15) }}>
                  Link text
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
