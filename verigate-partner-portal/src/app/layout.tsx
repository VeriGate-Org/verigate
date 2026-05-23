import type { Metadata } from "next";
import { Inter, JetBrains_Mono } from "next/font/google";
import "./globals.css";
import { ThemeProvider } from "@/components/providers/ThemeProvider";
import { QueryProvider } from "@/components/providers/QueryProvider";
import { ToastProvider } from "@/components/providers/ToastProvider";
import { ErrorBoundary } from "@/components/providers/ErrorBoundary";
import { AuthProvider } from "@/lib/auth/AuthProvider";
import { PartnerTenantProvider } from "@/lib/tenant/PartnerTenantProvider";

const inter = Inter({ subsets: ["latin"], variable: "--font-inter" });
const jetbrainsMono = JetBrains_Mono({ subsets: ["latin"], variable: "--font-mono" });

export const metadata: Metadata = {
  title: "VeriGate Partner Portal",
  description: "Partner verification management portal",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={`${inter.variable} ${jetbrainsMono.variable} antialiased min-h-screen bg-background text-text`}>
        <ThemeProvider>
          <QueryProvider>
            <ToastProvider>
              <ErrorBoundary>
                <AuthProvider>
                  <PartnerTenantProvider>
                    {children}
                  </PartnerTenantProvider>
                </AuthProvider>
              </ErrorBoundary>
            </ToastProvider>
          </QueryProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
