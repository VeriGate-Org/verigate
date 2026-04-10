import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ArrowRight, Mail, Bell } from "lucide-react";
import { Link } from "react-router-dom";
import { AnimatedSection } from "@/components/AnimatedSection";

type CTAVariant =
  | "homepage"
  | "verification"
  | "compliance"
  | "fraud"
  | "industry"
  | "blog"
  | "careers"
  | "pricing"
  | "platform";

interface PageCTAProps {
  variant: CTAVariant;
}

const ctaContent: Record<CTAVariant, {
  title: string;
  description: string;
  primaryLabel: string;
  primaryHref: string;
  secondaryLabel?: string;
  secondaryHref?: string;
  showEmailInput?: boolean;
  isMailto?: boolean;
}> = {
  homepage: {
    title: "Ready to Streamline Your Verification Process?",
    description: "Join 200+ South African organisations who trust VeriGate for secure, compliant background screening.",
    primaryLabel: "Request a Demo",
    primaryHref: "/request-demo",
    secondaryLabel: "View Pricing",
    secondaryHref: "/pricing",
  },
  verification: {
    title: "Start Screening with VeriGate",
    description: "Fast, accurate verification checks powered by direct integrations with South Africa's authoritative data sources.",
    primaryLabel: "Get Started",
    primaryHref: "/request-demo",
    secondaryLabel: "Compare Plans",
    secondaryHref: "/compare-plans",
  },
  compliance: {
    title: "Ensure Full Compliance",
    description: "Stay ahead of South African regulatory requirements with VeriGate's automated compliance checks. POPIA, FICA, and sector-specific regulations covered.",
    primaryLabel: "Request Compliance Review",
    primaryHref: "/request-demo",
    secondaryLabel: "View Resources",
    secondaryHref: "/resources",
  },
  fraud: {
    title: "Protect Your Organisation",
    description: "Defend against fraud with VeriGate's multi-layered detection and prevention platform. Built for South African threats, POPIA compliant.",
    primaryLabel: "Schedule Security Demo",
    primaryHref: "/request-demo",
    secondaryLabel: "View Resources",
    secondaryHref: "/resources",
  },
  industry: {
    title: "Built for Your Industry",
    description: "Join leading South African organisations using VeriGate for compliant, fast verification. Trusted by 200+ enterprises across the country.",
    primaryLabel: "Schedule Industry Demo",
    primaryHref: "/request-demo",
    secondaryLabel: "ROI Calculator",
    secondaryHref: "/roi-calculator",
  },
  blog: {
    title: "Stay Updated",
    description: "Subscribe for the latest insights on background screening, compliance, and verification in South Africa.",
    primaryLabel: "Subscribe",
    primaryHref: "#",
    showEmailInput: true,
  },
  careers: {
    title: "Don't See a Role That Fits?",
    description: "We're always looking for talented people who share our passion for building secure, compliant verification solutions for South Africa.",
    primaryLabel: "Send Your CV",
    primaryHref: "mailto:careers@verigate.co.za?subject=General Application",
    secondaryLabel: "Set Up Job Alert",
    secondaryHref: "mailto:careers@verigate.co.za?subject=Job Alert Request",
    isMailto: true,
  },
  pricing: {
    title: "Ready to Get Started?",
    description: "Join 200+ South African organisations already using VeriGate for background screening.",
    primaryLabel: "Request a Demo",
    primaryHref: "/request-demo",
    secondaryLabel: "Contact Sales",
    secondaryHref: "/contact",
  },
  platform: {
    title: "See It in Action",
    description: "Book a personalised demo and see how VeriGate can streamline your background screening process.",
    primaryLabel: "Request a Demo",
    primaryHref: "/request-demo",
    secondaryLabel: "Technical Support",
    secondaryHref: "/technical-support",
  },
};

const PageCTA = ({ variant }: PageCTAProps) => {
  const content = ctaContent[variant];

  if (content.showEmailInput) {
    return (
      <section className="py-24 px-4 bg-gradient-hero relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <AnimatedSection className="max-w-2xl mx-auto text-center space-y-8">
            <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold text-primary-foreground">
              {content.title}
            </h2>
            <p className="text-xl text-primary-foreground/90">
              {content.description}
            </p>
            <div className="flex gap-4 max-w-md mx-auto">
              <Input
                type="email"
                placeholder="Enter your email"
                className="bg-primary-foreground text-foreground"
              />
              <Button size="lg" className="bg-primary-foreground text-primary hover:bg-primary-foreground/90 flex-shrink-0">
                <Mail className="w-4 h-4 mr-2" />
                {content.primaryLabel}
              </Button>
            </div>
          </AnimatedSection>
        </div>
      </section>
    );
  }

  return (
    <section className="py-24 px-4 bg-gradient-hero relative overflow-hidden">
      <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
      <div className="container mx-auto max-w-6xl relative z-10">
        <AnimatedSection className="max-w-2xl space-y-8">
          <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold text-primary-foreground">
            {content.title}
          </h2>
          <p className="text-xl text-primary-foreground/90">
            {content.description}
          </p>

          <div className="flex flex-col sm:flex-row gap-4 items-start pt-4">
            <Button
              size="lg"
              className="min-w-[200px] bg-primary-foreground text-primary hover:bg-primary-foreground/90 shadow-lg hover:scale-105 transition-transform duration-200"
              asChild
            >
              {content.isMailto ? (
                <a href={content.primaryHref}>
                  {variant === "careers" && <Mail className="mr-2 w-4 h-4" />}
                  {content.primaryLabel}
                  {variant !== "careers" && <ArrowRight className="ml-2 w-5 h-5" />}
                </a>
              ) : (
                <Link to={content.primaryHref}>
                  {content.primaryLabel}
                  <ArrowRight className="ml-2 w-5 h-5" />
                </Link>
              )}
            </Button>
            {content.secondaryLabel && content.secondaryHref && (
              <Button
                size="lg"
                variant="outline"
                className="min-w-[200px] border-2 border-primary-foreground/30 text-primary-foreground hover:bg-primary-foreground/10 hover:scale-105 transition-transform duration-200"
                asChild
              >
                {content.isMailto ? (
                  <a href={content.secondaryHref}>
                    {variant === "careers" && <Bell className="mr-2 w-4 h-4" />}
                    {content.secondaryLabel}
                  </a>
                ) : (
                  <Link to={content.secondaryHref}>{content.secondaryLabel}</Link>
                )}
              </Button>
            )}
          </div>
        </AnimatedSection>
      </div>
    </section>
  );
};

export default PageCTA;
