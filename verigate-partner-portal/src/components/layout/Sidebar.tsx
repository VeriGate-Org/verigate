"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  Home,
  FileSearch,
  Briefcase,
  UserCheck,
  BarChart3,
  Zap,
  Layers,
  CreditCard,
  TrendingUp,
  DollarSign,
  Receipt,
  Shield,
  Building2,
  GraduationCap,
  Search,
  Map,
  MapPin,
  Newspaper,
  AlertTriangle,
  Eye,
  ArrowLeftRight,
  Gauge,
  Activity,
  Settings,
  Users,
  HelpCircle,
  ChevronLeft,
  ChevronRight,
  ScanFace,
  type LucideIcon,
} from "lucide-react";
import { cn } from "@/lib/cn";
import { useSidebar } from "./SidebarContext";

interface NavItem {
  id: string;
  label: string;
  icon: LucideIcon;
  href: string;
  badge?: string | number;
  disabled?: boolean;
  disabledReason?: string;
}

interface NavSection {
  label: string;
  items: NavItem[];
}

const NAV_SECTIONS: NavSection[] = [
  {
    label: "Overview",
    items: [
      { id: "dashboard", icon: Home, label: "Dashboard", href: "/dashboard" },
      { id: "verifications", icon: FileSearch, label: "Verifications", href: "/verifications" },
      { id: "cases", icon: Briefcase, label: "Cases", href: "/cases", badge: "NEW" },
    ],
  },
  {
    label: "Identity & Personal",
    items: [
      { id: "kyc", icon: UserCheck, label: "KYC", href: "/services/kyc" },
      { id: "documents", icon: FileSearch, label: "Document Verification", href: "/services/document-verification" },
      { id: "docinsights", icon: BarChart3, label: "Document Insights", href: "/services/document-analytics", badge: "NEW" },
      { id: "autofill", icon: Zap, label: "Document Auto-Fill", href: "/services/document-auto-fill", badge: "NEW" },
      { id: "bulkid", icon: Layers, label: "Bulk Identity", href: "/services/bulk-identity-verification", badge: "NEW" },
      { id: "biometric", icon: ScanFace, label: "Biometric", href: "/services/biometric", disabled: true, disabledReason: "Coming Soon" },
    ],
  },
  {
    label: "Financial",
    items: [
      { id: "bank", icon: CreditCard, label: "Bank Account", href: "/services/bank-account" },
      { id: "credit", icon: TrendingUp, label: "Credit Check", href: "/services/credit-check" },
      { id: "income", icon: DollarSign, label: "Income Verification", href: "/services/income" },
      { id: "tax", icon: Receipt, label: "Tax Compliance", href: "/services/tax-compliance" },
    ],
  },
  {
    label: "Business & Compliance",
    items: [
      { id: "company", icon: Building2, label: "Company & Directors", href: "/services/company" },
      { id: "employment", icon: Briefcase, label: "Employment", href: "/services/employment" },
      { id: "qualification", icon: GraduationCap, label: "Qualification", href: "/services/qualification" },
      { id: "vat", icon: Search, label: "VAT Vendor Search", href: "/services/vat-vendor-search" },
      { id: "deeds", icon: Map, label: "Deeds Registry", href: "/services/deeds" },
      { id: "property-conversion", icon: ArrowLeftRight, label: "Street/ERF Conversion", href: "/services/property-conversion" },
      { id: "property-valuation", icon: Gauge, label: "Property Valuation", href: "/services/property-valuation" },
      { id: "deeds-map", icon: MapPin, label: "Deeds Map", href: "/services/deeds-map", badge: "NEW" },
      { id: "sanctions", icon: Shield, label: "Sanctions & PEP", href: "/services/sanctions" },
    ],
  },
  {
    label: "Screening",
    items: [
      { id: "negnews", icon: Newspaper, label: "Negative News", href: "/services/negative-news" },
      { id: "fraud", icon: AlertTriangle, label: "Fraud Watchlist", href: "/services/fraud-watchlist" },
    ],
  },
  {
    label: "Enterprise Features",
    items: [
      { id: "policies", icon: Layers, label: "Policy Builder", href: "/policies", badge: "NEW" },
      { id: "conflicts", icon: Eye, label: "Conflict of Interest", href: "/services/conflict-of-interest", badge: "NEW" },
      { id: "monitoring", icon: Shield, label: "Monitoring", href: "/monitoring", badge: "NEW" },
    ],
  },
  {
    label: "Reporting",
    items: [
      { id: "reports", icon: BarChart3, label: "Reports", href: "/reports" },
    ],
  },
  {
    label: "Admin & Settings",
    items: [
      { id: "settings", icon: Settings, label: "Settings", href: "/settings" },
      { id: "admin", icon: Users, label: "User Management", href: "/admin" },
      { id: "system-health", icon: Activity, label: "System Health", href: "/system-health" },
      { id: "help", icon: HelpCircle, label: "Help & Support", href: "/help" },
    ],
  },
];

export function Sidebar() {
  const pathname = usePathname();
  const { collapsed, toggle, mobileOpen, setMobileOpen } = useSidebar();

  function isActive(href: string) {
    if (href === "/dashboard") return pathname === "/dashboard" || pathname === "/dashboard/";
    return pathname.startsWith(href);
  }

  return (
    <>
      {/* Mobile overlay */}
      {mobileOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/30 md:hidden"
          onClick={() => setMobileOpen(false)}
        />
      )}

      <aside
        className={cn(
          "flex flex-col h-full bg-surface border-r border-border shrink-0 transition-all duration-200 overflow-hidden z-50",
          collapsed ? "w-16" : "w-[250px]",
          "max-md:fixed max-md:inset-y-0 max-md:left-0",
          mobileOpen ? "max-md:translate-x-0" : "max-md:-translate-x-full",
          "md:translate-x-0 md:relative",
        )}
      >
        {/* Brand header */}
        <div
          className={cn(
            "border-b border-border flex items-center shrink-0",
            collapsed ? "justify-center py-4" : "justify-between px-4 py-4",
          )}
        >
          <div className="flex items-center gap-2.5">
            <svg width="22" height="24" viewBox="20 25 112 122" className="shrink-0">
              <path
                fill="#E23D36"
                d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"
              />
              <path
                d="M46 84 L63 102 L106 58"
                fill="none"
                stroke="#FFFFFF"
                strokeWidth="13"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
            {!collapsed && (
              <span className="font-medium text-[17px] tracking-tight text-primary">
                VeriGate
              </span>
            )}
          </div>
          {!collapsed && (
            <button
              onClick={toggle}
              title="Collapse navigation"
              className="p-1 text-text-muted hover:text-text transition-colors"
            >
              <ChevronLeft size={14} strokeWidth={2.5} />
            </button>
          )}
        </div>

        {/* Collapsed expand button */}
        {collapsed && (
          <button
            onClick={toggle}
            className="p-2.5 text-text-muted hover:text-accent transition-colors mx-auto mt-2"
            title="Expand navigation"
          >
            <ChevronRight size={14} strokeWidth={2.5} />
          </button>
        )}

        {/* Navigation */}
        <nav className="flex-1 overflow-y-auto py-2">
          {NAV_SECTIONS.map((section) => (
            <div key={section.label}>
              {!collapsed && (
                <div className="text-[10px] uppercase tracking-[0.12em] text-text-muted/60 px-4 pt-3 pb-1 font-semibold">
                  {section.label}
                </div>
              )}
              {section.items.map((item) => {
                const active = isActive(item.href);
                const Icon = item.icon;

                if (item.disabled) {
                  return (
                    <div
                      key={item.id}
                      title={item.disabledReason || "Disabled"}
                      className={cn(
                        "w-full flex items-center gap-2.5 py-[7px] text-[13px] opacity-45 cursor-not-allowed",
                        collapsed ? "justify-center px-0" : "px-4",
                        "border-l-[3px] border-transparent",
                      )}
                    >
                      <Icon size={15} className="shrink-0 text-text-muted" />
                      {!collapsed && (
                        <>
                          <span className="flex-1 text-text-muted">{item.label}</span>
                          {item.disabledReason && (
                            <span className="text-[9px] uppercase tracking-wide font-semibold text-text-muted">
                              {item.disabledReason}
                            </span>
                          )}
                        </>
                      )}
                    </div>
                  );
                }

                return (
                  <Link
                    key={item.id}
                    href={item.href}
                    onClick={() => setMobileOpen(false)}
                    title={item.label}
                    className={cn(
                      "w-full flex items-center gap-2.5 py-[7px] text-[13px] transition-all",
                      collapsed ? "justify-center px-0" : "px-4",
                      "border-l-[3px]",
                      active
                        ? "text-primary bg-accent-soft border-l-accent font-semibold"
                        : "text-text-muted border-transparent hover:bg-[#F8FAFC] hover:text-text",
                    )}
                  >
                    <Icon
                      size={15}
                      className={cn(
                        "shrink-0",
                        active ? "text-accent" : "text-[#64748B]",
                      )}
                    />
                    {!collapsed && (
                      <>
                        <span className="flex-1">{item.label}</span>
                        {typeof item.badge === "number" && (
                          <span className="text-[10px] px-[7px] py-px bg-[#E23D36] text-white rounded-full font-semibold">
                            {item.badge}
                          </span>
                        )}
                        {typeof item.badge === "string" && (
                          <span className="text-[9px] px-1.5 py-0.5 bg-accent text-white rounded font-bold tracking-wide">
                            {item.badge}
                          </span>
                        )}
                      </>
                    )}
                  </Link>
                );
              })}
            </div>
          ))}
        </nav>

        {/* Footer */}
        {!collapsed && (
          <div className="border-t border-border px-4 py-3 text-[10px] text-text-muted/60 bg-[#F8FAFC] shrink-0">
            <div className="flex items-center gap-1.5 mb-0.5">
              <span className="w-1.5 h-1.5 rounded-full bg-[#2C974B] shadow-[0_0_6px_#2C974B]" />
              <span>All services operational</span>
            </div>
            <div className="font-mono text-text-muted">
              v2026.04 &middot; Build #1428
            </div>
          </div>
        )}
      </aside>
    </>
  );
}
