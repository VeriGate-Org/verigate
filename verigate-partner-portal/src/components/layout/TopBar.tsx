"use client";

import { usePathname } from "next/navigation";
import {
  Search,
  Bell,
  ChevronRight,
  ChevronDown,
  Menu,
  Moon,
  Sun,
} from "lucide-react";
import { useAuth } from "@/lib/auth/AuthProvider";
import { useSidebar } from "./SidebarContext";
import { useState, useRef, useEffect } from "react";

function buildBreadcrumbs(pathname: string): string[] {
  const segments = pathname.split("/").filter(Boolean);
  return segments.map((seg) =>
    seg
      .split("-")
      .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
      .join(" "),
  );
}

export function TopBar() {
  const pathname = usePathname();
  const { user, signOut } = useAuth();
  const { setMobileOpen } = useSidebar();
  const breadcrumbs = buildBreadcrumbs(pathname);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const initial = user?.email?.charAt(0).toUpperCase() || "U";
  const displayName = user?.email?.split("@")[0] || "User";

  return (
    <header className="h-[60px] bg-surface border-b border-border flex items-center px-6 gap-6 shrink-0">
      {/* Mobile menu button */}
      <button
        className="md:hidden p-1 text-text-muted hover:text-text"
        onClick={() => setMobileOpen(true)}
      >
        <Menu size={18} />
      </button>

      {/* Breadcrumbs */}
      <nav className="flex-1 flex items-center gap-1.5 text-[13px] min-w-0">
        {breadcrumbs.map((crumb, i) => (
          <span key={i} className="flex items-center gap-1.5">
            {i > 0 && <ChevronRight size={12} className="text-text-muted shrink-0" />}
            <span
              className={
                i === breadcrumbs.length - 1
                  ? "font-semibold text-text truncate"
                  : "text-text-muted truncate"
              }
            >
              {crumb}
            </span>
          </span>
        ))}
      </nav>

      {/* Search */}
      <div className="relative w-80 hidden lg:block">
        <span className="absolute left-2.5 top-1/2 -translate-y-1/2 inline-flex">
          <Search size={14} className="text-text-muted" />
        </span>
        <input
          placeholder="Search verifications, IDs, or correlation numbers\u2026"
          className="aws-input w-full pl-8 pr-3 py-[7px] text-[13px] rounded"
        />
      </div>

      {/* Theme toggle */}
      <button
        title="Toggle theme"
        className="w-8 h-8 border border-border rounded bg-surface inline-flex items-center justify-center hover:border-accent transition-colors"
        onClick={() => {
          const html = document.documentElement;
          const isDark = html.getAttribute("data-theme") === "dark";
          html.setAttribute("data-theme", isDark ? "light" : "dark");
          try {
            localStorage.setItem("verigate-theme", isDark ? "light" : "dark");
          } catch {
            // ignore
          }
        }}
      >
        <Moon size={15} className="text-text-muted hidden [html[data-theme=dark]_&]:block" />
        <Sun size={15} className="text-text-muted [html[data-theme=dark]_&]:hidden" />
      </button>

      {/* Notifications */}
      <button
        title="Notifications"
        className="w-8 h-8 border border-border rounded bg-surface inline-flex items-center justify-center relative hover:border-accent transition-colors"
      >
        <Bell size={15} className="text-text-muted" />
        <span className="absolute top-1.5 right-1.5 w-1.5 h-1.5 rounded-full bg-[#E23D36]" />
      </button>

      {/* User menu */}
      <div ref={dropdownRef} className="relative">
        <button
          onClick={() => setDropdownOpen(!dropdownOpen)}
          className="flex items-center gap-2 pl-4 border-l border-border cursor-pointer"
        >
          <div className="w-[30px] h-[30px] rounded-full bg-primary text-white flex items-center justify-center text-xs font-semibold">
            {initial}
          </div>
          <div className="flex flex-col text-left hidden sm:flex">
            <span className="text-xs font-semibold text-text">{displayName}</span>
            <span className="text-[10px] text-text-muted">
              {user?.role === "admin" ? "Admin" : "Partner"}
            </span>
          </div>
          <ChevronDown size={12} className="text-text-muted" />
        </button>

        {dropdownOpen && (
          <div className="absolute right-0 top-full mt-1 w-48 bg-surface border border-border rounded-aws-container shadow-[var(--aws-elevation-raised)] py-1 z-50">
            <button
              onClick={() => {
                setDropdownOpen(false);
                signOut();
              }}
              className="w-full text-left px-4 py-2 text-[13px] text-text hover:bg-hover transition-colors"
            >
              Sign out
            </button>
          </div>
        )}
      </div>
    </header>
  );
}
