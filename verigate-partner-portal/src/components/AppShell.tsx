"use client";

import { usePathname } from "next/navigation";
import { AuthProvider } from "@/lib/auth/AuthProvider";
import { AuthGuard } from "@/lib/auth/AuthGuard";
import TopNav from "@/components/TopNav";
import Sidebar from "@/components/Sidebar";
import type { ReactNode } from "react";

const CHROME_HIDDEN_PATHS = ["/signin"];

export default function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const hideChrome = CHROME_HIDDEN_PATHS.some(
    (p) => pathname === p || pathname.startsWith(p + "/"),
  );

  return (
    <AuthProvider>
      <AuthGuard>
        {hideChrome ? (
          children
        ) : (
          <>
            <a
              href="#main-content"
              className="sr-only focus:not-sr-only focus:fixed focus:top-2 focus:left-2 focus:z-[999] focus:rounded focus:bg-accent focus:px-4 focus:py-2 focus:text-sm focus:font-medium focus:text-white focus:shadow-lg"
            >
              Skip to content
            </a>
            <TopNav />
            <div className="flex pt-14">
              <Sidebar />
              <main id="main-content" className="flex-1 md:ml-60 p-6 bg-background min-h-screen">
                {children}
              </main>
            </div>
          </>
        )}
      </AuthGuard>
    </AuthProvider>
  );
}
