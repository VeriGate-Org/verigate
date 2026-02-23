import EnhancedNavigation from "@/components/EnhancedNavigation";
import Hero from "@/components/Hero";
import Features from "@/components/Features";
import TrustIndicators from "@/components/TrustIndicators";
import HowItWorks from "@/components/HowItWorks";
import { CustomerLogos } from "@/components/CustomerLogos";
import { Testimonials } from "@/components/Testimonials";
import { StatsCounter } from "@/components/StatsCounter";
import CTA from "@/components/CTA";
import Footer from "@/components/Footer";
import { CookieConsent } from "@/components/CookieConsent";
import LiveChat from "@/components/LiveChat";

const Index = () => {
  return (
    <div className="min-h-screen">
      <EnhancedNavigation />
      <Hero />
      <TrustIndicators />
      <CustomerLogos />
      <Features />
      <StatsCounter />
      <HowItWorks />
      <Testimonials />
      <CTA />
      <Footer />
      <CookieConsent />
      <LiveChat />
    </div>
  );
};

export default Index;
