"use client";
import Image from "next/image";
import Link from "next/link";
import { usePathname } from "next/navigation";
import * as DropdownMenu from "@radix-ui/react-dropdown-menu";
import { Menu, Bell, HelpCircle, Search, Moon, Sun } from "lucide-react";
import { useTheme } from "@/components/theme/ThemeProvider";
import { Breadcrumb, type BreadcrumbItem } from "@/components/ui/Navigation/Breadcrumb";
import { useAuth, useUser } from "@/lib/auth";
import { useTenantFeatures } from "@/lib/tenant/PartnerTenantProvider";
import * as React from "react";

const ENV_LABEL = process.env.NEXT_PUBLIC_ENV || "Sandbox";
const LOGO_SRC = process.env.NEXT_PUBLIC_LOGO || "/verigate-logo.svg";

function DefaultShield({ isDark }: { isDark: boolean }) {
  const shieldColor = isDark ? "#f37353" : "#E23D36";
  const strokeColor = isDark ? "#1f2933" : "#FFFFFF";
  return (
    <span className="flex h-8 w-8 items-center justify-center" aria-hidden>
      <svg width="32" height="32" viewBox="0 0 28 28" role="img" aria-label="VeriGate logo" shapeRendering="geometricPrecision">
        <path fill={shieldColor} d="M14 2c-3.8 0-7 1.33-7 1.33v7.7c0 5.2 3.4 10.03 7 12.24 3.6-2.21 7-7.04 7-12.24V3.33C21 3.33 17.8 2 14 2Z" />
        <path d="M8.5 14.5l3.5 3.5 7.5-7.5" fill="none" stroke={strokeColor} strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    </span>
  );
}

function LogoMark({ src, isDark, brandingLogo, brandingLogoDark }: { src: string; isDark: boolean; brandingLogo?: string; brandingLogoDark?: string }) {
  // Prefer tenant branding logo
  const customLogo = isDark && brandingLogoDark ? brandingLogoDark : brandingLogo;
  if (customLogo) {
    return <Image src={customLogo} alt="Logo" className="h-8 w-auto" width={128} height={32} priority />;
  }

  if (src === "/verigate-logo.svg") {
    return <DefaultShield isDark={isDark} />;
  }

  return <Image src={src} alt="VeriGate" className="h-8 w-auto" width={128} height={128} priority />;
}

// Enhanced breadcrumb mapping
const ROUTE_BREADCRUMBS: Record<string, BreadcrumbItem[]> = {
  "/dashboard": [
    { label: "Dashboard", current: true },
  ],
  "/verifications": [
    { label: "Verification Log", current: true },
  ],
  "/services/personal-details": [
    { label: "Services", href: "/dashboard" },
    { label: "Home Affairs ID", current: true },
  ],
  "/services/company": [
    { label: "Services", href: "/dashboard" },
    { label: "Company & Directors", current: true },
  ],
  "/services/property-ownership": [
    { label: "Services", href: "/dashboard" },
    { label: "Deeds Registry", current: true },
  ],
  "/services/bank-account": [
    { label: "Services", href: "/dashboard" },
    { label: "Bank Account Validation", current: true },
  ],
  "/services/sanctions": [
    { label: "Services", href: "/dashboard" },
    { label: "Sanctions & PEP", current: true },
  ],
  "/services/identity": [
    { label: "Services", href: "/dashboard" },
    { label: "Identity Verification", current: true },
  ],
  "/services/document-verification": [
    { label: "Services", href: "/dashboard" },
    { label: "Document Verification", current: true },
  ],
  "/services/credit-check": [
    { label: "Services", href: "/dashboard" },
    { label: "Credit Check", current: true },
  ],
  "/services/income": [
    { label: "Services", href: "/dashboard" },
    { label: "Income Verification", current: true },
  ],
  "/services/tax-compliance": [
    { label: "Services", href: "/dashboard" },
    { label: "Tax Compliance", current: true },
  ],
  "/services/employment": [
    { label: "Services", href: "/dashboard" },
    { label: "Employment Verification", current: true },
  ],
  "/services/qualification": [
    { label: "Services", href: "/dashboard" },
    { label: "Qualification Verification", current: true },
  ],
  "/services/negative-news": [
    { label: "Services", href: "/dashboard" },
    { label: "Negative News Screening", current: true },
  ],
  "/services/fraud-watchlist": [
    { label: "Services", href: "/dashboard" },
    { label: "Fraud Watchlist Screening", current: true },
  ],
  "/services/full-verification": [
    { label: "Services", href: "/dashboard" },
    { label: "Full Verification", current: true },
  ],
  "/settings": [
    { label: "Settings", current: true },
  ],
  "/help": [
    { label: "Help & Support", current: true },
  ],
  "/policies": [
    { label: "Enterprise", href: "/reports" },
    { label: "Policy Builder", current: true },
  ],
  "/reports": [
    { label: "Enterprise", href: "/policies" },
    { label: "Reports & Analytics", current: true },
  ],
};

export default function TopNav() {
  const pathname = usePathname();
  const { theme, toggleTheme } = useTheme();
  const isDark = theme === "dark";
  const { signOut } = useAuth();
  const user = useUser();
  const { branding, isWhiteLabelled } = useTenantFeatures();
  const brandName = isWhiteLabelled && branding?.name ? branding.name : "VeriGate";
  
  const toggleSidebar = () => {
    if (typeof document !== "undefined") {
      const body = document.body;
      const isDesktop = window.innerWidth >= 768;

      if (isDesktop) {
        // On desktop, toggle between visible and hidden
        body.classList.toggle("sidebar-hidden");
      } else {
        // On mobile, toggle between hidden and visible
        body.classList.toggle("sidebar-open");
      }
    }
  };

  // Get breadcrumbs for current route
  const breadcrumbItems = ROUTE_BREADCRUMBS[pathname] || [];

  return (
    <header className="fixed top-0 left-0 right-0 z-50 h-14 bg-[color:var(--color-base-100)] border-b border-border">
      <div className="flex h-full items-center justify-between px-4">
        <div className="flex items-center gap-3 min-w-0 flex-1">
          <button
            onClick={toggleSidebar}
            className="p-2 rounded hover:bg-hover"
            aria-label="Toggle sidebar menu"
          >
            <Menu className="h-4 w-4 text-text" />
          </button>

          <Link href="/dashboard" className="flex items-center gap-2" aria-label={`${brandName} home`}>
            <LogoMark
              src={LOGO_SRC}
              isDark={isDark}
              brandingLogo={branding?.logo}
              brandingLogoDark={branding?.logoDark}
            />
            {isWhiteLabelled && !branding?.logo && (
              <span className="hidden sm:inline text-sm font-semibold text-text">{brandName}</span>
            )}
          </Link>

          {/* Breadcrumb Navigation */}
          {breadcrumbItems.length > 0 && (
            <div className="hidden md:block pl-4 border-l border-border">
              <Breadcrumb items={breadcrumbItems} showHome={false} />
            </div>
          )}
        </div>

        <div className="flex items-center gap-3">
          <button
            aria-label="Search"
            className="hidden md:flex min-w-[280px] items-center gap-2 px-3 py-2 rounded border text-xs transition-colors border-[color:var(--color-accent-border)] text-[color:var(--color-accent-strong)] hover:bg-[color:var(--color-accent-soft)]"
          >
            <Search className="h-3.5 w-3.5 text-[color:var(--color-accent-strong)]" />
            <span>Search</span>
          </button>

          <span
            className="px-2.5 py-1 rounded-full text-[11px] font-medium uppercase tracking-wide border"
            style={{
              backgroundColor: "var(--color-accent-soft)",
              color: "var(--color-accent-strong)",
              borderColor: "var(--color-accent-border)",
            }}
          >
            {ENV_LABEL}
          </span>

          <button
            onClick={toggleTheme}
            aria-label="Toggle dark mode"
            className="p-2 rounded hover:bg-hover transition"
          >
            {isDark ? (
              <Sun className="h-4 w-4 text-[color:var(--color-accent-strong)]" />
            ) : (
              <Moon className="h-4 w-4 text-[color:var(--color-accent-strong)]" />
            )}
          </button>

          <DropdownMenu.Root>
            <DropdownMenu.Trigger asChild>
              <button aria-label="Notifications" className="relative p-2 rounded hover:bg-hover">
                <Bell className="h-4 w-4 text-[color:var(--color-accent-strong)]" />
                {/* Notification badge */}
                <span className="absolute top-1 right-1 flex h-2 w-2">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-orange-400 opacity-75"></span>
                  <span className="relative inline-flex rounded-full h-2 w-2 bg-orange-500"></span>
                </span>
              </button>
            </DropdownMenu.Trigger>
            <DropdownMenu.Portal>
              <DropdownMenu.Content
                align="end"
                sideOffset={6}
                className="min-w-96 max-w-md rounded-md border border-border bg-[color:var(--color-base-100)] p-0 text-sm shadow-lg"
              >
                {/* Header */}
                <div className="flex items-center justify-between px-4 py-3 border-b border-border">
                  <div className="font-semibold text-text">Notifications</div>
                  <div className="flex items-center gap-2">
                    <span className="text-xs text-text-muted">3 unread</span>
                    <button className="text-xs text-primary hover:underline font-medium">
                      Mark all as read
                    </button>
                  </div>
                </div>

                {/* Notifications List */}
                <div className="max-h-96 overflow-y-auto">
                  {/* Unread Notification 1 */}
                  <DropdownMenu.Item asChild>
                    <a href="#" className="flex gap-3 px-4 py-3 hover:bg-hover cursor-pointer border-b border-border bg-blue-50/30">
                      <div className="flex-shrink-0 mt-1">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-green-100">
                          <span className="text-lg">✅</span>
                        </div>
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="flex items-start justify-between gap-2">
                          <div className="font-medium text-text">Verification Completed</div>
                          <span className="flex h-2 w-2 rounded-full bg-orange-500 flex-shrink-0 mt-1.5"></span>
                        </div>
                        <p className="text-xs text-text-muted mt-0.5">
                          ID verification for John Doe completed successfully
                        </p>
                        <div className="text-xs text-text-muted mt-1">2 minutes ago</div>
                      </div>
                    </a>
                  </DropdownMenu.Item>

                  {/* Unread Notification 2 */}
                  <DropdownMenu.Item asChild>
                    <a href="#" className="flex gap-3 px-4 py-3 hover:bg-hover cursor-pointer border-b border-border bg-blue-50/30">
                      <div className="flex-shrink-0 mt-1">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-orange-100">
                          <span className="text-lg">⚠️</span>
                        </div>
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="flex items-start justify-between gap-2">
                          <div className="font-medium text-text">Verification Failed</div>
                          <span className="flex h-2 w-2 rounded-full bg-orange-500 flex-shrink-0 mt-1.5"></span>
                        </div>
                        <p className="text-xs text-text-muted mt-0.5">
                          AVS check failed for account ending in 4532
                        </p>
                        <div className="text-xs text-text-muted mt-1">15 minutes ago</div>
                      </div>
                    </a>
                  </DropdownMenu.Item>

                  {/* Unread Notification 3 */}
                  <DropdownMenu.Item asChild>
                    <a href="#" className="flex gap-3 px-4 py-3 hover:bg-hover cursor-pointer border-b border-border bg-blue-50/30">
                      <div className="flex-shrink-0 mt-1">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100">
                          <span className="text-lg">📊</span>
                        </div>
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="flex items-start justify-between gap-2">
                          <div className="font-medium text-text">Daily Report Ready</div>
                          <span className="flex h-2 w-2 rounded-full bg-orange-500 flex-shrink-0 mt-1.5"></span>
                        </div>
                        <p className="text-xs text-text-muted mt-0.5">
                          Your verification summary for today is available
                        </p>
                        <div className="text-xs text-text-muted mt-1">1 hour ago</div>
                      </div>
                    </a>
                  </DropdownMenu.Item>

                  {/* Read Notification 1 */}
                  <DropdownMenu.Item asChild>
                    <a href="#" className="flex gap-3 px-4 py-3 hover:bg-hover cursor-pointer border-b border-border">
                      <div className="flex-shrink-0 mt-1">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-purple-100">
                          <span className="text-lg">🔑</span>
                        </div>
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="font-medium text-text-muted">API Key Generated</div>
                        <p className="text-xs text-text-muted mt-0.5">
                          New API key created for Production environment
                        </p>
                        <div className="text-xs text-text-muted mt-1">3 hours ago</div>
                      </div>
                    </a>
                  </DropdownMenu.Item>

                  {/* Read Notification 2 */}
                  <DropdownMenu.Item asChild>
                    <a href="#" className="flex gap-3 px-4 py-3 hover:bg-hover cursor-pointer border-b border-border">
                      <div className="flex-shrink-0 mt-1">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-green-100">
                          <span className="text-lg">👥</span>
                        </div>
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="font-medium text-text-muted">Team Member Added</div>
                        <p className="text-xs text-text-muted mt-0.5">
                          Sarah Johnson joined your organization
                        </p>
                        <div className="text-xs text-text-muted mt-1">Yesterday</div>
                      </div>
                    </a>
                  </DropdownMenu.Item>

                  {/* Read Notification 3 */}
                  <DropdownMenu.Item asChild>
                    <a href="#" className="flex gap-3 px-4 py-3 hover:bg-hover cursor-pointer">
                      <div className="flex-shrink-0 mt-1">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100">
                          <span className="text-lg">🎉</span>
                        </div>
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="font-medium text-text-muted">Welcome to VeriGate</div>
                        <p className="text-xs text-text-muted mt-0.5">
                          Get started with our quick setup guide
                        </p>
                        <div className="text-xs text-text-muted mt-1">2 days ago</div>
                      </div>
                    </a>
                  </DropdownMenu.Item>
                </div>

                {/* Footer */}
                <div className="border-t border-border">
                  <DropdownMenu.Item asChild>
                    <a href="#" className="flex items-center justify-center px-4 py-3 hover:bg-hover cursor-pointer text-primary font-medium">
                      View all notifications
                    </a>
                  </DropdownMenu.Item>
                </div>
              </DropdownMenu.Content>
            </DropdownMenu.Portal>
          </DropdownMenu.Root>

          <DropdownMenu.Root>
            <DropdownMenu.Trigger asChild>
              <button className="flex items-center gap-2 px-1.5 py-1 rounded-full hover:bg-hover" aria-label="User menu">
                <Image
                  src="/profile-avatar-orange.svg"
                  alt="Profile"
                  width={28}
                  height={28}
                  className="h-7 w-7 rounded-full"
                  priority
                />
              </button>
            </DropdownMenu.Trigger>
            <DropdownMenu.Portal>
              <DropdownMenu.Content
                align="end"
                sideOffset={6}
                className="min-w-64 rounded-md border border-border bg-[color:var(--color-base-100)] p-1 text-sm shadow-lg"
              >
                {/* User Profile Header */}
                <div className="px-3 py-2.5 border-b border-border">
                  <div className="flex items-center gap-3">
                    <Image
                      src="/profile-avatar-orange.svg"
                      alt="Profile"
                      width={40}
                      height={40}
                      className="h-10 w-10 rounded-full"
                    />
                    <div className="flex-1 min-w-0">
                      <div className="font-semibold text-text truncate">{user?.partnerName || "Partner User"}</div>
                      <div className="text-xs text-text-muted truncate">{user?.email || "user@partner.com"}</div>
                    </div>
                  </div>
                </div>

                {/* Account Options */}
                <DropdownMenu.Item asChild>
                  <Link href="/settings?tab=profile" className="flex items-center gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">👤</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">My Profile</div>
                      <div className="text-xs text-text-muted">View and edit your profile</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Item asChild>
                  <Link href="/settings?tab=profile" className="flex items-center gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">⚙️</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">Account Settings</div>
                      <div className="text-xs text-text-muted">Manage preferences and security</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Item asChild>
                  <Link href="/settings?tab=api-keys" className="flex items-center gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">🔑</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">API Keys</div>
                      <div className="text-xs text-text-muted">Manage your API credentials</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Item asChild>
                  <Link href="/settings?tab=profile" className="flex items-center gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">🏢</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">Organization</div>
                      <div className="text-xs text-text-muted">Team members and billing</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Separator className="h-px bg-border my-1" />

                {/* Preferences */}
                <DropdownMenu.Item asChild>
                  <Link href="/settings?tab=notifications" className="flex items-center gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">🔔</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">Notifications</div>
                      <div className="text-xs text-text-muted">Email and alert preferences</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Item asChild>
                  <Link href="/settings?tab=appearance" className="flex items-center gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">🎨</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">Appearance</div>
                      <div className="text-xs text-text-muted">Theme and display options</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Separator className="h-px bg-border my-1" />

                {/* Sign Out */}
                <DropdownMenu.Item asChild>
                  <button
                    onClick={() => { signOut(); window.location.href = "/signin"; }}
                    className="flex items-center gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer w-full text-left"
                  >
                    <span className="text-lg">🚪</span>
                    <div className="font-medium text-danger">Sign Out</div>
                  </button>
                </DropdownMenu.Item>
              </DropdownMenu.Content>
            </DropdownMenu.Portal>
          </DropdownMenu.Root>

          <DropdownMenu.Root>
            <DropdownMenu.Trigger asChild>
              <button 
                aria-label="Help" 
                className="p-2 rounded hover:bg-hover"
              >
                <HelpCircle className="h-4 w-4 text-[color:var(--color-accent-strong)]" />
              </button>
            </DropdownMenu.Trigger>
            <DropdownMenu.Portal>
              <DropdownMenu.Content
                align="end"
                sideOffset={6}
                className="min-w-64 rounded-md border border-border bg-[color:var(--color-base-100)] p-1 text-sm shadow-lg"
              >
                <div className="px-3 py-2 border-b border-border">
                  <div className="font-semibold text-text">Help & Resources</div>
                  <div className="text-xs text-text-muted mt-0.5">Quick access to documentation and support</div>
                </div>
                
                <DropdownMenu.Item asChild>
                  <Link href="/help?section=docs" className="flex items-start gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">📚</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">Documentation</div>
                      <div className="text-xs text-text-muted">API guides and integration docs</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Item asChild>
                  <Link href="/help?section=tutorials" className="flex items-start gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">🎓</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">Tutorials</div>
                      <div className="text-xs text-text-muted">Step-by-step learning resources</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Item asChild>
                  <Link href="/help?section=support" className="flex items-start gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">💬</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">Support Chat</div>
                      <div className="text-xs text-text-muted">Get help from our support team</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Item asChild>
                  <Link href="/help?section=support" className="flex items-start gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">🐛</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">Report Issue</div>
                      <div className="text-xs text-text-muted">Submit bug reports or feedback</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Separator className="h-px bg-border my-1" />

                <DropdownMenu.Item asChild>
                  <Link href="/help?section=shortcuts" className="flex items-start gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">⚡</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">Keyboard Shortcuts</div>
                      <div className="text-xs text-text-muted">View all available shortcuts</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>

                <DropdownMenu.Item asChild>
                  <Link href="/help?section=changelog" className="flex items-start gap-3 rounded px-3 py-2.5 hover:bg-hover cursor-pointer">
                    <span className="text-lg">📋</span>
                    <div className="flex-1">
                      <div className="font-medium text-text">What&apos;s New</div>
                      <div className="text-xs text-text-muted">Latest features and updates</div>
                    </div>
                  </Link>
                </DropdownMenu.Item>
              </DropdownMenu.Content>
            </DropdownMenu.Portal>
          </DropdownMenu.Root>
        </div>
      </div>
    </header>
  );
}
