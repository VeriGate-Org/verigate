import EnhancedNavigation from "@/components/EnhancedNavigation";
import Footer from "@/components/Footer";
import { CookieConsent } from "@/components/CookieConsent";
import LiveChat from "@/components/LiveChat";

interface LayoutProps {
  children: React.ReactNode;
}

const Layout = ({ children }: LayoutProps) => {
  return (
    <div className="min-h-screen flex flex-col">
      <EnhancedNavigation />
      <main className="flex-1">{children}</main>
      <Footer />
      <CookieConsent />
      <LiveChat />
    </div>
  );
};

export default Layout;
