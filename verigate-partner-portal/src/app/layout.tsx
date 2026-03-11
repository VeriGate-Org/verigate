import type { Metadata } from "next";
import "./globals.css";
import { ThemeProvider } from "@/components/theme/ThemeProvider";
import QueryProvider from "@/components/QueryProvider";
import AppShell from "@/components/AppShell";

export const metadata: Metadata = {
  title: "VeriGate Partner Portal",
  description: "Configure verification workflows, providers, and review results.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <script
          dangerouslySetInnerHTML={{
            __html: `(function(){try{var t=localStorage.getItem("verigate-theme");if(t==="dark"||(!t&&window.matchMedia("(prefers-color-scheme:dark)").matches)){document.documentElement.setAttribute("data-theme","dark")}}catch(e){}})()`,
          }}
        />
      </head>
      <body className="antialiased min-h-screen bg-background text-text">
        <ThemeProvider>
          <QueryProvider>
            <AppShell>{children}</AppShell>
          </QueryProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
