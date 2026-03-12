"use client";

import * as React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard, Shield, Building2, Map, CreditCard, ShieldAlert, Search,
  ChevronRight, ChevronDown, Star, X, Settings, HelpCircle, FileText, GitBranch,
  Fingerprint, FileCheck, TrendingUp, DollarSign, Receipt, Briefcase,
  GraduationCap, Newspaper, AlertTriangle, CheckSquare, ClipboardList, Eye,
} from "lucide-react";
import { cn } from "@/lib/cn";

interface NavItem {
  name: string;
  path: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: string;
}

interface NavSection {
  name: string;
  items: NavItem[];
  defaultExpanded?: boolean;
}

const NAV_SECTIONS: NavSection[] = [
  {
    name: "Overview",
    defaultExpanded: true,
    items: [
      { name: "Dashboard", path: "/dashboard", icon: LayoutDashboard },
      { name: "Verification Log", path: "/verifications", icon: Search },
      { name: "Cases", path: "/cases", icon: ClipboardList, badge: "NEW" },
    ],
  },
  {
    name: "Identity & Personal",
    defaultExpanded: true,
    items: [
      { name: "Home Affairs ID", path: "/services/personal-details", icon: Shield },
      { name: "Identity Verification", path: "/services/identity", icon: Fingerprint },
      { name: "Document Verification", path: "/services/document-verification", icon: FileCheck },
    ],
  },
  {
    name: "Financial",
    defaultExpanded: true,
    items: [
      { name: "Bank Account Validation", path: "/services/bank-account", icon: CreditCard },
      { name: "Credit Check", path: "/services/credit-check", icon: TrendingUp },
      { name: "Income Verification", path: "/services/income", icon: DollarSign },
      { name: "Tax Compliance", path: "/services/tax-compliance", icon: Receipt },
    ],
  },
  {
    name: "Business & Compliance",
    defaultExpanded: false,
    items: [
      { name: "Company & Directors", path: "/services/company", icon: Building2 },
      { name: "Deeds Registry", path: "/services/property-ownership", icon: Map },
      { name: "Employment", path: "/services/employment", icon: Briefcase },
      { name: "Qualification", path: "/services/qualification", icon: GraduationCap },
    ],
  },
  {
    name: "Screening",
    defaultExpanded: false,
    items: [
      { name: "Sanctions & PEP", path: "/services/sanctions", icon: ShieldAlert },
      { name: "Negative News", path: "/services/negative-news", icon: Newspaper },
      { name: "Fraud Watchlist", path: "/services/fraud-watchlist", icon: AlertTriangle },
    ],
  },
  {
    name: "Composite",
    defaultExpanded: false,
    items: [
      { name: "Full Verification", path: "/services/full-verification", icon: CheckSquare, badge: "NEW" },
    ],
  },
  {
    name: "Enterprise Features",
    defaultExpanded: false,
    items: [
      { name: "Policy Builder", path: "/policies", icon: GitBranch, badge: "NEW" },
      { name: "Monitoring", path: "/monitoring", icon: Eye, badge: "NEW" },
      { name: "Reports & Analytics", path: "/reports", icon: FileText, badge: "NEW" },
    ],
  },
  {
    name: "Configuration",
    defaultExpanded: false,
    items: [
      { name: "Settings", path: "/settings", icon: Settings },
      { name: "Help & Support", path: "/help", icon: HelpCircle },
    ],
  },
];

export default function Sidebar() {
  const pathname = usePathname();
  const [searchQuery, setSearchQuery] = React.useState("");
  const [expandedSections, setExpandedSections] = React.useState<Set<string>>(
    new Set(NAV_SECTIONS.filter(s => s.defaultExpanded).map(s => s.name))
  );
  const [favorites, setFavorites] = React.useState<Set<string>>(new Set());

  // Load favorites from localStorage
  React.useEffect(() => {
    const stored = localStorage.getItem("nav-favorites");
    if (stored) {
      try {
        setFavorites(new Set(JSON.parse(stored)));
      } catch {
        // Ignore parse errors
      }
    }
  }, []);

  // Save favorites to localStorage
  const toggleFavorite = React.useCallback((path: string) => {
    setFavorites(prev => {
      const next = new Set(prev);
      if (next.has(path)) {
        next.delete(path);
      } else {
        next.add(path);
      }
      localStorage.setItem("nav-favorites", JSON.stringify(Array.from(next)));
      return next;
    });
  }, []);

  const toggleSection = React.useCallback((sectionName: string) => {
    setExpandedSections(prev => {
      const next = new Set(prev);
      if (next.has(sectionName)) {
        next.delete(sectionName);
      } else {
        next.add(sectionName);
      }
      return next;
    });
  }, []);

  // Filter items based on search
  const filteredSections = React.useMemo(() => {
    if (!searchQuery) return NAV_SECTIONS;
    
    const query = searchQuery.toLowerCase();
    return NAV_SECTIONS.map(section => ({
      ...section,
      items: section.items.filter(item => 
        item.name.toLowerCase().includes(query)
      ),
    })).filter(section => section.items.length > 0);
  }, [searchQuery]);

  // Get favorite items
  const favoriteItems = React.useMemo(() => {
    return NAV_SECTIONS.flatMap(section => section.items)
      .filter(item => favorites.has(item.path));
  }, [favorites]);

  return (
    <aside className="sidebar fixed left-0 top-14 bottom-0 w-60 bg-[color:var(--color-base-100)] border-r border-border overflow-y-auto transition-transform duration-200 ease-in-out">
      <div className="px-3 py-4">
        {/* Search */}
        <div className="mb-4">
          <div className="relative">
            <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 h-3.5 w-3.5 text-text-muted" />
            <input
              type="text"
              placeholder="Search navigation..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-8 pr-8 py-1.5 text-xs border border-border rounded bg-base-100 text-text placeholder:text-text-muted focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
            />
            {searchQuery && (
              <button
                onClick={() => setSearchQuery("")}
                className="absolute right-2 top-1/2 -translate-y-1/2 text-text-muted hover:text-text"
              >
                <X className="h-3.5 w-3.5" />
              </button>
            )}
          </div>
        </div>

        {/* Favorites Section */}
        {favoriteItems.length > 0 && !searchQuery && (
          <div className="mb-4">
            <div className="text-[11px] text-text-muted uppercase tracking-[0.12em] mb-2 font-semibold">
              Favorites
            </div>
            <nav className="space-y-0.5">
              {favoriteItems.map((item) => {
                const isActive = pathname === item.path || pathname.startsWith(`${item.path}/`);
                const Icon = item.icon;
                return (
                  <div key={item.path} className="relative group">
                    <Link
                      href={item.path}
                      className={cn(
                        "flex items-center gap-2.5 px-2.5 py-2 text-[13px] font-medium rounded transition-colors border border-transparent",
                        isActive
                          ? "bg-[color:var(--color-accent-soft)] text-[color:var(--color-accent-strong)] border-[color:var(--color-accent-border)]"
                          : "text-text hover:bg-hover hover:text-[color:var(--color-accent-strong)]"
                      )}
                    >
                      <Icon className="h-4 w-4" />
                      <span className="truncate flex-1">{item.name}</span>
                    </Link>
                    <button
                      onClick={() => toggleFavorite(item.path)}
                      className="absolute right-1 top-1/2 -translate-y-1/2 p-1 opacity-0 group-hover:opacity-100 transition-opacity"
                      aria-label="Remove from favorites"
                    >
                      <Star className="h-3 w-3 fill-accent text-accent" />
                    </button>
                  </div>
                );
              })}
            </nav>
          </div>
        )}

        {/* Navigation Sections */}
        <div className="space-y-4">
          {filteredSections.map((section) => {
            const isExpanded = expandedSections.has(section.name) || !!searchQuery;
            
            return (
              <div key={section.name}>
                <button
                  onClick={() => toggleSection(section.name)}
                  className="w-full flex items-center justify-between text-[11px] text-text-muted uppercase tracking-[0.12em] mb-2 font-semibold hover:text-text transition-colors"
                  aria-expanded={isExpanded}
                >
                  <span>{section.name}</span>
                  {!searchQuery && (
                    isExpanded ? (
                      <ChevronDown className="h-3 w-3" />
                    ) : (
                      <ChevronRight className="h-3 w-3" />
                    )
                  )}
                </button>

                {isExpanded && (
                  <nav className="space-y-0.5">
                    {section.items.map((item) => {
                      const isActive = pathname === item.path || pathname.startsWith(`${item.path}/`);
                      const Icon = item.icon;
                      const isFavorite = favorites.has(item.path);

                      return (
                        <div key={item.path} className="relative group">
                          <Link
                            href={item.path}
                            className={cn(
                              "flex items-center gap-2.5 px-2.5 py-2 text-[13px] font-medium rounded transition-colors border border-transparent",
                              isActive
                                ? "bg-[color:var(--color-accent-soft)] text-[color:var(--color-accent-strong)] border-[color:var(--color-accent-border)]"
                                : "text-text hover:bg-hover hover:text-[color:var(--color-accent-strong)]"
                            )}
                          >
                            <Icon className="h-4 w-4" />
                            <span className="truncate flex-1">{item.name}</span>
                            {item.badge && (
                              <span className="px-1.5 py-0.5 text-[10px] rounded bg-accent text-white">
                                {item.badge}
                              </span>
                            )}
                          </Link>
                          <button
                            onClick={() => toggleFavorite(item.path)}
                            className="absolute right-1 top-1/2 -translate-y-1/2 p-1 opacity-0 group-hover:opacity-100 transition-opacity"
                            aria-label={isFavorite ? "Remove from favorites" : "Add to favorites"}
                          >
                            <Star className={cn(
                              "h-3 w-3",
                              isFavorite ? "fill-accent text-accent" : "text-text-muted"
                            )} />
                          </button>
                        </div>
                      );
                    })}
                  </nav>
                )}
              </div>
            );
          })}
        </div>

        {/* No Results */}
        {searchQuery && filteredSections.length === 0 && (
          <div className="text-center py-8 text-text-muted text-sm">
            No results found for &quot;{searchQuery}&quot;
          </div>
        )}
      </div>
    </aside>
  );
}
