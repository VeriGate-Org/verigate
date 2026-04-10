import EnhancedNavigation from "@/components/EnhancedNavigation";
import Hero from "@/components/Hero";
import Features from "@/components/Features";
import TrustIndicators from "@/components/TrustIndicators";
import HowItWorks from "@/components/HowItWorks";
import { CustomerLogos } from "@/components/CustomerLogos";
import { Testimonials } from "@/components/Testimonials";
import { StatsCounter } from "@/components/StatsCounter";
import PageCTA from "@/components/PageCTA";
import Footer from "@/components/Footer";
import { CookieConsent } from "@/components/CookieConsent";
import LiveChat from "@/components/LiveChat";
import { Badge } from "@/components/ui/badge";
import { Shield, Award, Play } from "lucide-react";
import { AnimatedSection } from "@/components/AnimatedSection";

const VideoSection = () => (
  <section className="py-20 px-4">
    <div className="container mx-auto max-w-4xl">
      <AnimatedSection className="text-center mb-10">
        <Badge variant="secondary" className="mb-4">
          <Play className="w-3 h-3 mr-1" />
          See It in Action
        </Badge>
        <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
          Watch How VeriGate Works
        </h2>
        <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
          See how South African organisations use VeriGate to streamline background screening and stay compliant.
        </p>
      </AnimatedSection>
      <AnimatedSection delay={0.2}>
        <div className="relative w-full rounded-xl overflow-hidden shadow-2xl border border-border aspect-video">
          <iframe
            src="https://www.youtube.com/embed/rYkb4d8l7i8"
            title="VeriGate - Enterprise Background Screening Platform"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowFullScreen
            className="absolute inset-0 w-full h-full"
          />
        </div>
      </AnimatedSection>
    </div>
  </section>
);

const CertificationBadges = () => (
  <section className="py-12 px-4 bg-secondary/30">
    <div className="container mx-auto max-w-6xl">
      <div className="text-center mb-8">
        <h2 className="text-2xl font-bold text-foreground mb-2">Certified & Compliant</h2>
        <p className="text-muted-foreground">Meeting the highest standards for data security and regulatory compliance</p>
      </div>
      <div className="flex flex-wrap justify-center gap-6">
        {[
          { name: "POPIA Compliant", description: "Protection of Personal Information Act" },
          { name: "ISO 27001", description: "Information Security Management" },
          { name: "SOC 2 Type II", description: "Service Organisation Controls" },
        ].map((cert) => (
          <div key={cert.name} className="flex items-center gap-3 p-4 border border-border rounded-lg bg-card">
            <Award className="w-8 h-8 text-accent flex-shrink-0" />
            <div>
              <p className="font-semibold text-sm">{cert.name}</p>
              <p className="text-xs text-muted-foreground">{cert.description}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  </section>
);

const Index = () => {
  return (
    <div className="min-h-screen">
      <EnhancedNavigation />
      <Hero />
      <TrustIndicators />
      <CustomerLogos />
      <VideoSection />
      <Features />
      <CertificationBadges />
      <StatsCounter />
      <HowItWorks />
      <Testimonials />
      <PageCTA variant="homepage" />
      <Footer />
      <CookieConsent />
      <LiveChat />
    </div>
  );
};

export default Index;
