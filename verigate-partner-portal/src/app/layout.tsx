import type { Metadata } from "next";
// Using system fonts like OmniCheck - no Google Fonts needed
import "./globals.css";
import TopNav from "@/components/TopNav";
import Sidebar from "@/components/Sidebar";
import { ThemeProvider } from "@/components/theme/ThemeProvider";
import QueryProvider from "@/components/QueryProvider";

// No custom fonts - using system fonts like OmniCheck

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
            <TopNav />
            <div className="flex pt-14">
              <Sidebar />
              <main className="flex-1 md:ml-60 p-6 bg-background min-h-screen">
                {children}
              </main>
            </div>
          </QueryProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
