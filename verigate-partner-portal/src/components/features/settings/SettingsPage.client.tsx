"use client";

import { useState, useCallback, useEffect } from "react";
import {
  Building2,
  Users,
  Palette,
  CreditCard,
  Sparkles,
  KeyRound,
  Bell,
  MapPin,
  ShieldCheck,
  Plus,
  Copy,
  Check,
  MoreHorizontal,
  Upload,
  Eye,
  EyeOff,
  RotateCcw,
  ExternalLink,
  Download,
  FileText,
} from "lucide-react";
import { cn } from "@/lib/cn";
import { PageHeader } from "@/components/ui/PageHeader";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { Modal } from "@/components/ui/Modal";
import { getInvoices, downloadInvoicePdf } from "@/lib/bff-client";
import type { InvoiceSummary } from "@/lib/bff-client";

/* -------------------------------------------------------------------------- */
/*  Types                                                                     */
/* -------------------------------------------------------------------------- */

type SectionId =
  | "account"
  | "team"
  | "branding"
  | "billing"
  | "plan"
  | "api"
  | "notify"
  | "deeds"
  | "compliance";

interface NavItem {
  id: SectionId;
  label: string;
  icon: React.ReactNode;
}

/* -------------------------------------------------------------------------- */
/*  Shared inline components                                                  */
/* -------------------------------------------------------------------------- */

function Toggle({
  on,
  onToggle,
  label,
}: {
  on: boolean;
  onToggle: () => void;
  label?: string;
}) {
  return (
    <button
      type="button"
      role="switch"
      aria-checked={on}
      aria-label={label}
      onClick={onToggle}
      className={cn(
        "relative inline-flex h-5 w-9 shrink-0 cursor-pointer items-center rounded-full transition-colors duration-200",
        on ? "bg-accent" : "bg-[#CBD5E1]",
      )}
    >
      <span
        className={cn(
          "inline-block h-4 w-4 rounded-full bg-white shadow-sm transition-transform duration-200",
          on ? "translate-x-[18px]" : "translate-x-[2px]",
        )}
      />
    </button>
  );
}

function SettingRow({
  title,
  desc,
  control,
}: {
  title: string;
  desc: React.ReactNode;
  control: React.ReactNode;
}) {
  return (
    <div className="flex items-center justify-between gap-4 px-[18px] py-[14px] border-b border-[#f1f5f9]">
      <div className="flex-1 min-w-0">
        <div className="text-[13px] font-medium text-text">{title}</div>
        <div className="text-[11px] text-text-muted mt-0.5 max-w-[460px]">
          {desc}
        </div>
      </div>
      <div className="shrink-0">{control}</div>
    </div>
  );
}

function SectionHeader({
  title,
  desc,
  actions,
}: {
  title: string;
  desc: string;
  actions?: React.ReactNode;
}) {
  return (
    <div className="flex items-center justify-between px-[18px] py-[14px] border-b border-[#e9ebed]">
      <div>
        <div className="text-sm font-semibold text-text">{title}</div>
        <div className="text-[11px] text-text-muted mt-0.5">{desc}</div>
      </div>
      {actions && <div className="shrink-0">{actions}</div>}
    </div>
  );
}

function SectionFooter({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex justify-end gap-2 px-[18px] py-3 border-t border-[#e9ebed] bg-[#F8FAFC]">
      {children}
    </div>
  );
}

function UsageMeter({
  label,
  used,
  included,
  unit,
}: {
  label: string;
  used: number;
  included: number;
  unit: string;
}) {
  const pct = (used / included) * 100;
  const barColor =
    pct > 90
      ? "bg-[#E23D36]"
      : pct > 70
        ? "bg-[#C28B0B]"
        : "bg-accent";
  return (
    <div className="border border-[#e9ebed] rounded-md p-3">
      <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">
        {label}
      </div>
      <div className="text-lg font-bold text-text mt-1">
        {used.toLocaleString()}
        <span className="text-xs font-normal text-text-muted">
          {" "}
          / {included.toLocaleString()}
        </span>
      </div>
      <div className="h-1 bg-[#F2F3F3] rounded-full mt-2 overflow-hidden">
        <div
          className={cn("h-full rounded-full transition-all", barColor)}
          style={{ width: `${Math.min(pct, 100)}%` }}
        />
      </div>
      <div className="text-[10px] text-text-muted mt-1">{unit}</div>
    </div>
  );
}

function Initials({ name }: { name: string }) {
  const initials = name
    .split(" ")
    .map((p) => p[0])
    .slice(0, 2)
    .join("");
  return (
    <div className="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center text-[11px] font-semibold shrink-0">
      {initials}
    </div>
  );
}

/* -------------------------------------------------------------------------- */
/*  Mock Data                                                                 */
/* -------------------------------------------------------------------------- */

const TEAM_MEMBERS = [
  {
    name: "Arthur Manena",
    email: "arthur@verigate.co.za",
    role: "Owner",
    status: "Active" as const,
  },
  {
    name: "Naledi Nkosi",
    email: "naledi@verigate.co.za",
    role: "Admin",
    status: "Active" as const,
  },
  {
    name: "Sipho Dlamini",
    email: "sipho@verigate.co.za",
    role: "Operator",
    status: "Active" as const,
  },
  {
    name: "Lerato Mokoena",
    email: "lerato@verigate.co.za",
    role: "Operator",
    status: "Invited" as const,
  },
];

const API_KEYS = [
  {
    name: "Production",
    prefix: "vg_live_a7f9",
    created: "12 Mar 2026",
    lastUsed: "2 days ago",
    env: "live" as const,
    status: "Active" as const,
  },
  {
    name: "Sandbox",
    prefix: "vg_test_b3c1",
    created: "12 Mar 2026",
    lastUsed: "Yesterday",
    env: "test" as const,
    status: "Active" as const,
  },
];

const DEEDS_PROVIDERS = [
  {
    name: "DRDLR Deeds Office",
    region: "National",
    status: "success" as const,
    statusLabel: "Connected",
    endpoint: "deeds.drdlr.gov.za",
  },
  {
    name: "Surveyor-General GIS",
    region: "WC, GP, KZN",
    status: "success" as const,
    statusLabel: "Connected",
    endpoint: "sg.westerncape.gov.za",
  },
  {
    name: "City of Cape Town Municipal",
    region: "WC",
    status: "success" as const,
    statusLabel: "Connected",
    endpoint: "gis.capetown.gov.za",
  },
  {
    name: "City of Johannesburg",
    region: "GP",
    status: "warning" as const,
    statusLabel: "Degraded",
    endpoint: "gis.joburg.org.za",
  },
  {
    name: "eThekwini Municipality",
    region: "KZN",
    status: "pending" as const,
    statusLabel: "Not configured",
    endpoint: "\u2014",
  },
];

const PLAN_FEATURES = [
  { feat: "Partner Portal", on: true },
  { feat: "Policy Builder", on: true },
  { feat: "Bulk uploads (5k/job)", on: true },
  { feat: "API + webhook access", on: true },
  { feat: "Priority support \u00b7 4h", on: true },
  { feat: "Continuous monitoring", on: false, gated: "Enterprise" },
  { feat: "Composite Reports", on: false, gated: "Enterprise" },
  { feat: "AI Assistant", on: false, gated: "Enterprise" },
  { feat: "Custom SLAs \u00b7 99.95%", on: false, gated: "Enterprise" },
];

/* -------------------------------------------------------------------------- */
/*  Section components                                                        */
/* -------------------------------------------------------------------------- */

function AccountSection() {
  return (
    <>
      <SectionHeader
        title="Organisation"
        desc="How your account appears across the portal and on reports."
      />
      <div className="p-[18px] grid grid-cols-1 sm:grid-cols-2 gap-3.5">
        <Input
          label="Organisation name"
          defaultValue="VeriGate (Pty) Ltd"
        />
        <Input
          label="Registration number"
          defaultValue="2025/525145/07"
          className="font-mono"
        />
        <Input label="Primary contact" defaultValue="Arthur Manena" />
        <Input
          label="Contact email"
          defaultValue="arthur@verigate.co.za"
          type="email"
        />
      </div>
      <SectionFooter>
        <Button variant="ghost">Discard</Button>
        <Button variant="primary">Save changes</Button>
      </SectionFooter>
    </>
  );
}

function TeamSection() {
  const [inviteOpen, setInviteOpen] = useState(false);

  return (
    <>
      <SectionHeader
        title="Team members"
        desc="3 active seats \u00b7 1 invited"
        actions={
          <Button
            variant="cta"
            size="sm"
            icon={<Plus size={13} />}
            onClick={() => setInviteOpen(true)}
          >
            Invite member
          </Button>
        }
      />

      {/* Team table */}
      <div className="divide-y divide-[#f1f5f9]">
        {TEAM_MEMBERS.map((m) => (
          <div
            key={m.email}
            className="flex items-center gap-3.5 px-[18px] py-3"
          >
            <Initials name={m.name} />
            <div className="flex-1 min-w-0">
              <div className="text-[13px] font-semibold text-text truncate">
                {m.name}
              </div>
              <div className="text-[11px] text-text-muted">{m.email}</div>
            </div>
            <span className="text-xs text-text-muted hidden sm:block">
              {m.role}
            </span>
            <Badge
              variant={m.status === "Active" ? "success" : "pending"}
              size="sm"
            >
              {m.status}
            </Badge>
            <button
              type="button"
              className="p-1.5 text-text-muted hover:text-text rounded transition-colors"
              aria-label={`Actions for ${m.name}`}
            >
              <MoreHorizontal size={16} />
            </button>
          </div>
        ))}
      </div>

      {/* Invite modal */}
      <Modal
        open={inviteOpen}
        onClose={() => setInviteOpen(false)}
        title="Invite team member"
      >
        <div className="space-y-3 mt-3">
          <Input label="Email address" placeholder="colleague@company.co.za" type="email" />
          <Select
            label="Role"
            options={[
              { value: "admin", label: "Admin" },
              { value: "operator", label: "Operator" },
              { value: "viewer", label: "Viewer" },
            ]}
            defaultValue="operator"
          />
          <div className="flex justify-end gap-2 pt-2">
            <Button variant="ghost" onClick={() => setInviteOpen(false)}>
              Cancel
            </Button>
            <Button variant="cta" onClick={() => setInviteOpen(false)}>
              Send invite
            </Button>
          </div>
        </div>
      </Modal>
    </>
  );
}

function BrandingSection() {
  const [primaryColor, setPrimaryColor] = useState("#E23D36");
  const [accentColor, setAccentColor] = useState("#00B3D9");
  const [whiteLabelEnabled, setWhiteLabelEnabled] = useState(false);
  const [subjectBrand, setSubjectBrand] = useState(true);

  return (
    <>
      <SectionHeader
        title="Branding & white-label"
        desc="How your tenant appears in the portal, on reports, and in subject-facing flows."
      />

      <div className="p-[18px] grid grid-cols-1 sm:grid-cols-2 gap-4">
        {/* Logo upload */}
        <div>
          <label className="block text-[11px] font-semibold text-text-muted uppercase tracking-wide mb-1.5">
            Logo
          </label>
          <div className="flex gap-3 items-center">
            <div className="w-16 h-16 rounded-lg bg-primary flex items-center justify-center shrink-0">
              <ShieldCheck size={28} className="text-white" />
            </div>
            <div>
              <Button variant="secondary" size="sm" icon={<Upload size={12} />}>
                Upload new
              </Button>
              <div className="text-[10px] text-text-muted mt-1">
                PNG / SVG \u00b7 Max 400 KB
              </div>
            </div>
          </div>
        </div>

        {/* Primary color */}
        <div>
          <label className="block text-[11px] font-semibold text-text-muted uppercase tracking-wide mb-1.5">
            Primary brand colour
          </label>
          <div className="flex gap-2 items-center">
            <div
              className="w-9 h-9 rounded-md border border-border shrink-0"
              style={{ background: primaryColor }}
            />
            <input
              type="text"
              value={primaryColor}
              onChange={(e) => setPrimaryColor(e.target.value)}
              className="aws-input flex-1 font-mono text-[13px]"
            />
          </div>
        </div>

        {/* Accent color */}
        <div>
          <label className="block text-[11px] font-semibold text-text-muted uppercase tracking-wide mb-1.5">
            Accent colour
          </label>
          <div className="flex gap-2 items-center">
            <div
              className="w-9 h-9 rounded-md border border-border shrink-0"
              style={{ background: accentColor }}
            />
            <input
              type="text"
              value={accentColor}
              onChange={(e) => setAccentColor(e.target.value)}
              className="aws-input flex-1 font-mono text-[13px]"
            />
          </div>
        </div>

        {/* Tenant name */}
        <Input
          label="Tenant display name"
          defaultValue="VeriGate Partner Portal"
        />

        {/* Tagline */}
        <div className="sm:col-span-2">
          <Input
            label="Tagline (optional)"
            placeholder="Realtime Risk Intelligence"
          />
        </div>
      </div>

      {/* Preview strip */}
      <div className="mx-[18px] mb-4 rounded-md overflow-hidden border border-border">
        <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted px-3 py-2 bg-[#F8FAFC] border-b border-[#e9ebed]">
          Preview
        </div>
        <div className="flex h-10">
          <div className="flex-1" style={{ background: primaryColor }} />
          <div className="flex-1" style={{ background: accentColor }} />
          <div className="flex-1 bg-white flex items-center justify-center text-xs text-text-muted">
            Surface
          </div>
        </div>
      </div>

      <SettingRow
        title="White-label mode"
        desc={
          <>
            Hide the VeriGate logo across the portal and use your branding
            only. Requires <b>Enterprise</b> plan.
          </>
        }
        control={
          <Toggle
            on={whiteLabelEnabled}
            onToggle={() => setWhiteLabelEnabled(!whiteLabelEnabled)}
            label="Toggle white-label mode"
          />
        }
      />
      <SettingRow
        title="Subject-facing brand"
        desc="Apply this branding to email notifications and the subject capture flow."
        control={
          <Toggle
            on={subjectBrand}
            onToggle={() => setSubjectBrand(!subjectBrand)}
            label="Toggle subject-facing brand"
          />
        }
      />

      <SectionFooter>
        <Button variant="ghost">Discard</Button>
        <Button variant="primary">Save branding</Button>
      </SectionFooter>
    </>
  );
}

function BillingSection() {
  const [autoRecharge, setAutoRecharge] = useState(true);
  const [invoices, setInvoices] = useState<InvoiceSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getInvoices()
      .then(setInvoices)
      .catch((err) => setError(err.message ?? "Failed to load invoices"))
      .finally(() => setLoading(false));
  }, []);

  const handleDownloadPdf = async (invoiceId: string) => {
    try {
      const { downloadUrl } = await downloadInvoicePdf(invoiceId);
      window.open(downloadUrl, "_blank");
    } catch {
      // silently fail - user can retry
    }
  };

  const statusVariant = (status: string) => {
    switch (status) {
      case "PAID": return "success" as const;
      case "ISSUED": return "info" as const;
      case "OVERDUE": return "danger" as const;
      case "DRAFT": return "pending" as const;
      case "CANCELLED":
      case "VOID": return "neutral" as const;
      default: return "neutral" as const;
    }
  };

  return (
    <>
      <SectionHeader
        title="Invoices"
        desc="View and download your billing invoices."
      />

      <div className="border-b border-[#e9ebed]">
        {loading ? (
          <div className="p-6 text-center text-sm text-text-muted">Loading invoices...</div>
        ) : error ? (
          <div className="p-6 text-center text-sm text-danger">{error}</div>
        ) : invoices.length === 0 ? (
          <div className="p-6 text-center text-sm text-text-muted">
            <FileText size={24} className="mx-auto mb-2 text-text-muted/50" />
            No invoices yet
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-[#e9ebed] text-left text-[11px] font-semibold uppercase tracking-wide text-text-muted">
                  <th className="px-4 py-2.5">Invoice #</th>
                  <th className="px-4 py-2.5">Period</th>
                  <th className="px-4 py-2.5">Status</th>
                  <th className="px-4 py-2.5 text-right">Total</th>
                  <th className="px-4 py-2.5">Due date</th>
                  <th className="px-4 py-2.5" />
                </tr>
              </thead>
              <tbody>
                {invoices.map((inv) => (
                  <tr key={inv.invoiceId} className="border-b border-[#e9ebed] hover:bg-[#fafafa]">
                    <td className="px-4 py-2.5 font-medium">{inv.invoiceNumber}</td>
                    <td className="px-4 py-2.5 text-text-muted">{inv.billingPeriod}</td>
                    <td className="px-4 py-2.5">
                      <Badge variant={statusVariant(inv.status)} size="sm">{inv.status}</Badge>
                    </td>
                    <td className="px-4 py-2.5 text-right font-medium">R {inv.total}</td>
                    <td className="px-4 py-2.5 text-text-muted">{inv.dueDate}</td>
                    <td className="px-4 py-2.5 text-right">
                      <button
                        onClick={() => handleDownloadPdf(inv.invoiceId)}
                        className="inline-flex items-center gap-1 text-xs text-accent hover:text-accent/80"
                      >
                        <Download size={12} />
                        PDF
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <div className="p-[18px] grid grid-cols-1 sm:grid-cols-2 gap-3.5">
        <Input label="Invoice email" defaultValue="finance@verigate.co.za" type="email" />
        <Input label="Billing address" defaultValue="12 Long St, Cape Town, 8001" />
      </div>

      <SettingRow
        title="Auto-recharge"
        desc="Top up R 5,000 when balance falls below R 1,000."
        control={
          <Toggle
            on={autoRecharge}
            onToggle={() => setAutoRecharge(!autoRecharge)}
            label="Toggle auto-recharge"
          />
        }
      />
      <SettingRow
        title="Payment method"
        desc="Visa \u2022\u2022\u2022\u2022 4242 \u00b7 Expires 09/27"
        control={
          <Button variant="link" size="sm">
            Change <ExternalLink size={11} className="ml-0.5 inline" />
          </Button>
        }
      />
    </>
  );
}

function PlanSection() {
  return (
    <>
      <SectionHeader
        title="Plan & features"
        desc="Your current plan and what's included."
      />

      {/* Current plan hero */}
      <div className="p-5 bg-gradient-to-br from-[rgba(0,179,217,0.05)] to-[rgba(0,179,217,0.01)] border-b border-[#e9ebed] flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="text-[10px] font-bold uppercase tracking-wide text-accent">
            Current plan
          </div>
          <div className="text-2xl font-bold text-primary mt-1">Business</div>
          <div className="text-xs text-text-muted">
            Renews 1 June 2026 \u00b7 R 1,499 / month
          </div>
        </div>
        <div className="flex gap-2">
          <Button variant="secondary" size="sm">
            View invoices
          </Button>
          <Button variant="cta" size="sm">
            Upgrade to Enterprise
          </Button>
        </div>
      </div>

      {/* Usage meters */}
      <div className="p-[18px] grid grid-cols-1 sm:grid-cols-3 gap-3.5">
        <UsageMeter
          label="KYC checks"
          used={38}
          included={50}
          unit="this month"
        />
        <UsageMeter
          label="Team seats"
          used={4}
          included={5}
          unit="used"
        />
        <UsageMeter
          label="API calls"
          used={1842}
          included={10000}
          unit="this month"
        />
      </div>

      {/* Feature checklist */}
      <div className="px-[18px] pb-[18px] pt-[14px] border-t border-[#e9ebed]">
        <div className="text-xs font-semibold text-text mb-2.5">
          Features included
        </div>
        <div className="divide-y divide-[#f1f5f9]">
          {PLAN_FEATURES.map((f) => (
            <div
              key={f.feat}
              className="flex items-center justify-between py-[7px] text-xs"
            >
              <span
                className={cn(
                  "inline-flex items-center gap-2",
                  f.on ? "text-text" : "text-[#94A3B8]",
                )}
              >
                <span
                  className={cn(
                    "font-bold",
                    f.on ? "text-[#2C974B]" : "text-[#CBD5E1]",
                  )}
                >
                  {f.on ? "\u2713" : "\u2715"}
                </span>
                {f.feat}
              </span>
              {f.gated && (
                <Badge variant="info" size="sm">
                  Requires {f.gated}
                </Badge>
              )}
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

function ApiKeysSection() {
  const [generatedKey, setGeneratedKey] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);
  const [revokeConfirm, setRevokeConfirm] = useState<string | null>(null);
  const [revealedKey, setRevealedKey] = useState<string | null>(null);

  const handleGenerate = useCallback(() => {
    const key = `vg_live_${crypto.getRandomValues(new Uint8Array(16)).reduce((s, b) => s + b.toString(16).padStart(2, "0"), "")}`;
    setGeneratedKey(key);
    setCopied(false);
  }, []);

  const handleCopy = useCallback(async () => {
    if (generatedKey) {
      await navigator.clipboard.writeText(generatedKey);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  }, [generatedKey]);

  return (
    <>
      <SectionHeader
        title="API keys"
        desc="Keep your secret keys secret. We hash and store only the first 6 characters."
        actions={
          <Button
            variant="cta"
            size="sm"
            icon={<Plus size={13} />}
            onClick={handleGenerate}
          >
            Generate key
          </Button>
        }
      />

      {/* Generated key banner */}
      {generatedKey && (
        <div className="mx-[18px] mt-[18px] p-3 rounded-md bg-[rgba(0,179,217,0.06)] border border-accent">
          <div className="text-[11px] font-semibold text-accent uppercase tracking-wide mb-1.5">
            New key generated \u2014 copy it now, it won&apos;t be shown again
          </div>
          <div className="flex gap-2">
            <input
              readOnly
              value={generatedKey}
              className="aws-input flex-1 font-mono text-xs bg-white"
            />
            <Button
              variant="secondary"
              size="sm"
              icon={copied ? <Check size={13} /> : <Copy size={13} />}
              onClick={handleCopy}
            >
              {copied ? "Copied" : "Copy"}
            </Button>
          </div>
        </div>
      )}

      {/* Key list */}
      <div className="divide-y divide-[#f1f5f9]">
        {API_KEYS.map((k) => (
          <div
            key={k.name}
            className="flex items-center gap-3.5 px-[18px] py-[14px]"
          >
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2">
                <span className="text-[13px] font-semibold text-text">
                  {k.name}
                </span>
                <span
                  className={cn(
                    "text-[10px] px-1.5 py-0.5 rounded font-bold uppercase tracking-wide text-white",
                    k.env === "live" ? "bg-[#2C974B]" : "bg-accent",
                  )}
                >
                  {k.env}
                </span>
              </div>
              <div className="font-mono text-xs text-text mt-1">
                {k.prefix}
                <span className="text-text-muted">
                  {revealedKey === k.name
                    ? "d8e2f1a9b7c3d4e5f6a7b8c9d0e1f2a3"
                    : "\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022"}
                </span>
              </div>
              <div className="text-[11px] text-text-muted mt-0.5">
                Created {k.created} \u00b7 Last used {k.lastUsed}
              </div>
            </div>
            <Badge variant={k.status === "Active" ? "success" : "danger"} size="sm">
              {k.status}
            </Badge>
            <Button
              variant="secondary"
              size="sm"
              icon={
                revealedKey === k.name ? (
                  <EyeOff size={12} />
                ) : (
                  <Eye size={12} />
                )
              }
              onClick={() =>
                setRevealedKey(revealedKey === k.name ? null : k.name)
              }
            >
              {revealedKey === k.name ? "Hide" : "Reveal"}
            </Button>
            <Button
              variant="ghost"
              size="sm"
              className="!text-[#E23D36]"
              icon={<RotateCcw size={12} />}
              onClick={() => setRevokeConfirm(k.name)}
            >
              Revoke
            </Button>
          </div>
        ))}
      </div>

      {/* Revoke confirmation modal */}
      <Modal
        open={revokeConfirm !== null}
        onClose={() => setRevokeConfirm(null)}
        title="Revoke API key"
      >
        <div className="mt-3 space-y-3">
          <p className="text-[13px] text-text-muted">
            Are you sure you want to revoke the{" "}
            <strong className="text-text">{revokeConfirm}</strong> key? Any
            applications using this key will immediately lose access.
          </p>
          <div className="flex justify-end gap-2 pt-1">
            <Button variant="ghost" onClick={() => setRevokeConfirm(null)}>
              Cancel
            </Button>
            <Button
              variant="destructive"
              onClick={() => setRevokeConfirm(null)}
            >
              Revoke key
            </Button>
          </div>
        </div>
      </Modal>
    </>
  );
}

function NotificationsSection() {
  const [emailOnVerification, setEmailOnVerification] = useState(true);
  const [emailOnEscalation, setEmailOnEscalation] = useState(true);
  const [emailOnMonitoring, setEmailOnMonitoring] = useState(false);
  const [slackWebhook, setSlackWebhook] = useState(true);
  const [weeklyDigest, setWeeklyDigest] = useState(true);

  return (
    <>
      <SectionHeader
        title="Notifications"
        desc="How you hear about completed checks, escalations, and SLAs."
      />
      <SettingRow
        title="Email on verification complete"
        desc="Receive an email each time a verification completes, including pass and fail outcomes."
        control={
          <Toggle
            on={emailOnVerification}
            onToggle={() => setEmailOnVerification(!emailOnVerification)}
            label="Toggle verification email"
          />
        }
      />
      <SettingRow
        title="Email on case escalation"
        desc="Immediate notification when a case is escalated to manual review."
        control={
          <Toggle
            on={emailOnEscalation}
            onToggle={() => setEmailOnEscalation(!emailOnEscalation)}
            label="Toggle escalation email"
          />
        }
      />
      <SettingRow
        title="Email on monitoring alert"
        desc="Triggered when continuous monitoring detects a status change on a subject."
        control={
          <Toggle
            on={emailOnMonitoring}
            onToggle={() => setEmailOnMonitoring(!emailOnMonitoring)}
            label="Toggle monitoring email"
          />
        }
      />
      <SettingRow
        title="Slack webhook enabled"
        desc={
          <>
            Live POST to{" "}
            <span className="font-mono text-accent">
              https://hooks.slack.com/services/T00/B00/xxx
            </span>{" "}
            on every verification state change.
          </>
        }
        control={
          <Toggle
            on={slackWebhook}
            onToggle={() => setSlackWebhook(!slackWebhook)}
            label="Toggle Slack webhook"
          />
        }
      />
      <SettingRow
        title="Weekly digest"
        desc="Summary of all verifications, escalations, and billing, sent every Monday at 08:00 SAST."
        control={
          <Toggle
            on={weeklyDigest}
            onToggle={() => setWeeklyDigest(!weeklyDigest)}
            label="Toggle weekly digest"
          />
        }
      />
    </>
  );
}

function DeedsSection() {
  const [providerFallback, setProviderFallback] = useState(true);

  return (
    <>
      <SectionHeader
        title="Deeds operations"
        desc="Provider-independent deeds operations. Configure connectors, cache rules, and rate limits."
      />

      {/* Stats */}
      <div className="p-[18px] grid grid-cols-1 sm:grid-cols-3 gap-3.5">
        {[
          { label: "Lookups today", value: "1,284", color: "text-text" },
          { label: "Cache hit rate", value: "78%", color: "text-[#2C974B]" },
          { label: "Avg. latency", value: "2.1 s", color: "text-accent" },
        ].map((s) => (
          <div key={s.label} className="border border-[#e9ebed] rounded-md p-3">
            <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">
              {s.label}
            </div>
            <div className={cn("text-xl font-bold mt-1", s.color)}>
              {s.value}
            </div>
          </div>
        ))}
      </div>

      {/* Provider connectors */}
      <div className="px-[18px] pb-[18px] pt-[14px] border-t border-[#e9ebed]">
        <div className="text-xs font-semibold text-text mb-2.5">
          Provider connectors
        </div>
        <div className="divide-y divide-[#f1f5f9]">
          {DEEDS_PROVIDERS.map((p) => {
            const dotColor =
              p.status === "success"
                ? "bg-[#2C974B]"
                : p.status === "warning"
                  ? "bg-[#C28B0B]"
                  : "bg-[#CBD5E1]";
            const dotGlow =
              p.status === "success"
                ? "shadow-[0_0_6px_#2C974B]"
                : "";
            return (
              <div
                key={p.name}
                className="flex items-center gap-3 py-2.5"
              >
                <span
                  className={cn(
                    "w-2 h-2 rounded-full shrink-0",
                    dotColor,
                    dotGlow,
                  )}
                />
                <div className="flex-1 min-w-0">
                  <div className="text-[13px] font-medium text-text">
                    {p.name}
                  </div>
                  <div className="text-[11px] text-text-muted font-mono">
                    {p.endpoint} \u00b7 {p.region}
                  </div>
                </div>
                <Badge variant={p.status} size="sm">
                  {p.statusLabel}
                </Badge>
                <Button variant="ghost" size="sm">
                  Configure
                </Button>
              </div>
            );
          })}
        </div>
      </div>

      <SettingRow
        title="Cache TTL"
        desc="How long deeds responses are cached before re-querying upstream."
        control={
          <select className="aws-select select-input text-xs pr-8">
            <option>24 hours (recommended)</option>
            <option>12 hours</option>
            <option>1 hour</option>
            <option>No cache</option>
          </select>
        }
      />
      <SettingRow
        title="Provider fallback"
        desc="On primary failure, retry against secondary providers automatically."
        control={
          <Toggle
            on={providerFallback}
            onToggle={() => setProviderFallback(!providerFallback)}
            label="Toggle provider fallback"
          />
        }
      />
      <SettingRow
        title="Rate limit per minute"
        desc="Maximum upstream calls per provider. Excess queued."
        control={
          <input
            defaultValue="60"
            className="aws-input w-20 text-right font-mono text-xs"
          />
        }
      />
    </>
  );
}

function ComplianceSection() {
  const [subjectAccess, setSubjectAccess] = useState(true);

  return (
    <>
      <SectionHeader
        title="POPIA & compliance"
        desc="VeriGate is a Registered Operator under the Protection of Personal Information Act."
      />
      <SettingRow
        title="Data processing agreement"
        desc="Acknowledge and accept the POPIA data processing agreement for this tenant."
        control={
          <Toggle
            on={true}
            onToggle={() => {}}
            label="Toggle data processing agreement"
          />
        }
      />
      <SettingRow
        title="Data retention period"
        desc="How long verification data is retained after a check completes."
        control={
          <select className="aws-select select-input text-xs pr-8">
            <option value="30">30 days</option>
            <option value="60">60 days</option>
            <option value="90">90 days</option>
            <option value="180">180 days</option>
            <option value="365">365 days</option>
          </select>
        }
      />
      <SettingRow
        title="Consent retention"
        desc="How long subject consent records are retained after a verification completes."
        control={
          <select className="aws-select select-input text-xs pr-8">
            <option>7 years (recommended)</option>
            <option>5 years</option>
          </select>
        }
      />
      <SettingRow
        title="Subject access requests"
        desc="Automate POPIA s.23 data-access requests via the partner portal."
        control={
          <Toggle
            on={subjectAccess}
            onToggle={() => setSubjectAccess(!subjectAccess)}
            label="Toggle subject access requests"
          />
        }
      />
      <SettingRow
        title="Information Officer"
        desc="Arthur Manena \u00b7 io@verigate.co.za"
        control={
          <Button variant="link" size="sm">
            Update <ExternalLink size={11} className="ml-0.5 inline" />
          </Button>
        }
      />

      {/* Consent template */}
      <div className="px-[18px] py-[14px] border-t border-[#e9ebed]">
        <label className="block text-[11px] font-semibold text-text-muted uppercase tracking-wide mb-1.5">
          Consent template
        </label>
        <textarea
          className="aws-input w-full min-h-[100px] resize-y text-xs"
          defaultValue={`I, the undersigned, hereby consent to VeriGate (Pty) Ltd processing my personal information for the purpose of identity verification, in accordance with the Protection of Personal Information Act 4 of 2013 (POPIA). I understand that my data will be retained for the period specified in the data retention policy and that I may request access to or deletion of my data at any time.`}
        />
        <div className="text-[10px] text-text-muted mt-1">
          This template is shown to subjects before data capture.
        </div>
      </div>

      <SettingRow
        title="Audit log retention"
        desc="How long system audit logs are retained for compliance purposes."
        control={
          <select className="aws-select select-input text-xs pr-8" defaultValue="2555">
            <option value="365">1 year</option>
            <option value="730">2 years</option>
            <option value="1825">5 years</option>
            <option value="2555">7 years (recommended)</option>
          </select>
        }
      />
    </>
  );
}

/* -------------------------------------------------------------------------- */
/*  Navigation items                                                          */
/* -------------------------------------------------------------------------- */

const NAV_ITEMS: NavItem[] = [
  { id: "account", label: "Account", icon: <Building2 size={15} /> },
  { id: "team", label: "Team & roles", icon: <Users size={15} /> },
  { id: "branding", label: "Branding", icon: <Palette size={15} /> },
  { id: "billing", label: "Billing", icon: <CreditCard size={15} /> },
  { id: "plan", label: "Plan & features", icon: <Sparkles size={15} /> },
  { id: "api", label: "API & webhooks", icon: <KeyRound size={15} /> },
  { id: "notify", label: "Notifications", icon: <Bell size={15} /> },
  { id: "deeds", label: "Deeds operations", icon: <MapPin size={15} /> },
  { id: "compliance", label: "POPIA & compliance", icon: <ShieldCheck size={15} /> },
];

const SECTION_MAP: Record<SectionId, React.FC> = {
  account: AccountSection,
  team: TeamSection,
  branding: BrandingSection,
  billing: BillingSection,
  plan: PlanSection,
  api: ApiKeysSection,
  notify: NotificationsSection,
  deeds: DeedsSection,
  compliance: ComplianceSection,
};

/* -------------------------------------------------------------------------- */
/*  Main export                                                               */
/* -------------------------------------------------------------------------- */

export function SettingsPage() {
  const [section, setSection] = useState<SectionId>("account");
  const ActiveSection = SECTION_MAP[section];

  return (
    <div className="space-y-aws-l">
      <PageHeader
        category="Settings"
        title="Configuration"
        description="Manage your organisation, team, billing, and integrations."
      />

      <div className="grid grid-cols-1 md:grid-cols-[200px_1fr] gap-4">
        {/* Sub-navigation */}
        <nav className="console-card py-2 h-fit">
          {NAV_ITEMS.map((item) => (
            <button
              key={item.id}
              type="button"
              onClick={() => setSection(item.id)}
              className={cn(
                "w-full flex items-center gap-2.5 text-left px-3.5 py-2 text-[13px] transition-colors border-l-[3px]",
                section === item.id
                  ? "bg-[rgba(0,179,217,0.10)] border-l-accent text-accent font-semibold"
                  : "border-l-transparent text-text hover:bg-[rgba(0,0,0,0.03)]",
              )}
            >
              <span
                className={cn(
                  "shrink-0",
                  section === item.id ? "text-accent" : "text-text-muted",
                )}
              >
                {item.icon}
              </span>
              {item.label}
            </button>
          ))}
        </nav>

        {/* Content pane */}
        <div className="console-card overflow-hidden">
          <ActiveSection />
        </div>
      </div>
    </div>
  );
}
