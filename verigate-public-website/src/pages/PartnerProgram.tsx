import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Code,
  Handshake,
  ShoppingBag,
  BadgeCheck,
  ArrowRight,
  DollarSign,
  Megaphone,
  Wrench,
  LayoutDashboard,
  UserCheck,
  GraduationCap,
  ClipboardCheck,
  Users,
  Rocket,
} from "lucide-react";
import { Link } from "react-router-dom";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

const PartnerProgram = () => {
  const partnerTypes = [
    {
      type: "Technology Partners",
      icon: Code,
      description:
        "Integrate VeriGate's verification API directly into your platform to offer background screening as a native feature for your customers.",
      idealFor:
        "HRIS providers, ATS platforms, payroll software, recruitment technology providers, and CRM systems operating in South Africa.",
      benefits: [
        "Full API and SDK integration support with dedicated engineering resources",
        "Co-branded documentation and joint marketing materials",
        "Joint go-to-market campaigns and event sponsorships",
        "Early access to new API endpoints, features, and beta programmes",
        "Technical sandbox environment for development and testing",
      ],
    },
    {
      type: "Reseller Partners",
      icon: ShoppingBag,
      description:
        "Sell VeriGate's background screening solutions to your client base under your brand or alongside your existing service offerings.",
      idealFor:
        "System integrators, IT consultancies, managed service providers, HR consulting firms, and value-added resellers across South Africa.",
      benefits: [
        "Volume-based pricing with attractive margins",
        "Dedicated partner manager and sales enablement resources",
        "Deal registration programme with pipeline protection",
        "White-label options for enterprise resellers",
        "Quarterly business reviews and growth planning sessions",
      ],
    },
    {
      type: "Referral Partners",
      icon: Handshake,
      description:
        "Earn commission by referring clients to VeriGate. Ideal for professionals who advise South African businesses on HR, compliance, and risk.",
      idealFor:
        "Accountants, HR consultants, legal firms, B-BBEE advisors, industry associations, and business consultants across South Africa.",
      benefits: [
        "Competitive referral commission on every closed deal",
        "Simple referral process via the partner portal",
        "No technical integration or sales quota required",
        "Fast commission payouts via EFT within 30 days",
        "Dedicated partner support contact for all queries",
      ],
    },
  ];

  const benefits = [
    {
      icon: DollarSign,
      title: "Revenue Share",
      description:
        "Earn competitive commissions and recurring revenue from every client you bring to VeriGate. Transparent payout structure with no hidden caps.",
    },
    {
      icon: Megaphone,
      title: "Co-Marketing",
      description:
        "Joint webinars, case studies, blog features, and event sponsorships to grow your brand alongside ours in the South African market.",
    },
    {
      icon: Wrench,
      title: "Dedicated Support",
      description:
        "A named partner manager plus priority access to technical support, integration engineers, and product specialists.",
    },
    {
      icon: LayoutDashboard,
      title: "Partner Portal",
      description:
        "A dedicated portal to track referrals, commissions, leads, marketing assets, and access technical documentation and training materials.",
    },
  ];

  const applicationSteps = [
    {
      step: 1,
      title: "Apply",
      icon: ClipboardCheck,
      description:
        "Complete the partner application form with details about your business, target market, and partnership goals. Applications typically take 5 minutes.",
    },
    {
      step: 2,
      title: "Review & Align",
      icon: Users,
      description:
        "Our partner team will review your application within 5 business days. We will schedule a call to discuss alignment, mutual goals, and partnership terms.",
    },
    {
      step: 3,
      title: "Onboard & Launch",
      icon: Rocket,
      description:
        "Once approved, you receive access to the partner portal, technical documentation, marketing materials, and a dedicated partner manager to help you get started.",
    },
  ];

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container relative z-10">
          <AnimatedSection>
            <div className="container mx-auto max-w-6xl text-center space-y-6">
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                VeriGate Partner Program
              </h1>
              <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
                Join South Africa's leading background screening partner ecosystem.
                Whether you build technology, resell solutions, or refer clients,
                there is a partnership model designed for you.
              </p>
              <div className="flex flex-col sm:flex-row gap-4 justify-center pt-4">
                <Button size="lg" asChild>
                  <Link to="/contact">
                    Apply to Partner Program
                    <ArrowRight className="w-4 h-4 ml-2" />
                  </Link>
                </Button>
                <Button size="lg" variant="outline" asChild>
                  <a href="#partner-types">Explore Partner Types</a>
                </Button>
              </div>
            </div>
          </AnimatedSection>
        </div>
      </section>

      {/* Quick Stats */}
      <section className="py-12 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <AnimatedSection>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
              {[
                { value: "50+", label: "Active Partners" },
                { value: "R5M+", label: "Partner Revenue (2025)" },
                { value: "3", label: "Partner Types" },
                { value: "200+", label: "Joint Clients" },
              ].map((stat) => (
                <div key={stat.label} className="text-center">
                  <div className="text-4xl md:text-5xl font-bold text-accent mb-2">
                    {stat.value}
                  </div>
                  <div className="text-sm text-muted-foreground">
                    {stat.label}
                  </div>
                </div>
              ))}
            </div>
          </AnimatedSection>
        </div>
      </section>

      {/* Partner Types */}
      <section id="partner-types" className="py-20">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Choose Your Partnership Model
              </h2>
              <p className="text-lg text-muted-foreground">
                Three distinct programmes designed for different types of partners
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="space-y-8">
            {partnerTypes.map((partner) => {
              const Icon = partner.icon;
              return (
                <Card
                  key={partner.type}
                  className="border-border hover:shadow-lg transition-shadow"
                >
                  <CardHeader>
                    <div className="flex items-center gap-3 mb-2">
                      <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                        <Icon className="w-6 h-6 text-accent" />
                      </div>
                      <CardTitle className="text-xl">{partner.type}</CardTitle>
                    </div>
                    <CardDescription className="text-base">
                      {partner.description}
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="grid md:grid-cols-2 gap-6">
                      <div>
                        <h4 className="text-sm font-semibold text-foreground mb-3">
                          Benefits:
                        </h4>
                        <ul className="space-y-2">
                          {partner.benefits.map((benefit) => (
                            <li
                              key={benefit}
                              className="flex items-start gap-2 text-sm"
                            >
                              <BadgeCheck className="w-4 h-4 text-accent flex-shrink-0 mt-0.5" />
                              <span className="text-muted-foreground">
                                {benefit}
                              </span>
                            </li>
                          ))}
                        </ul>
                      </div>
                      <div>
                        <h4 className="text-sm font-semibold text-foreground mb-3">
                          Ideal for:
                        </h4>
                        <p className="text-sm text-muted-foreground">
                          {partner.idealFor}
                        </p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* Benefits */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Partner Benefits
              </h2>
              <p className="text-lg text-muted-foreground">
                Every VeriGate partner receives comprehensive support to grow their
                business
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {benefits.map((benefit) => {
              const Icon = benefit.icon;
              return (
                <Card key={benefit.title} className="border-border text-center">
                  <CardHeader>
                    <div className="mx-auto w-14 h-14 rounded-full bg-accent/10 flex items-center justify-center mb-4">
                      <Icon className="w-7 h-7 text-accent" />
                    </div>
                    <CardTitle className="text-lg">{benefit.title}</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <CardDescription className="text-sm">
                      {benefit.description}
                    </CardDescription>
                  </CardContent>
                </Card>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* How to Apply */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                How to Apply
              </h2>
              <p className="text-lg text-muted-foreground">
                Getting started is simple. Three steps to join the VeriGate partner
                program.
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {applicationSteps.map((step) => (
              <div key={step.step} className="text-center">
                <div className="mx-auto w-16 h-16 rounded-full bg-accent text-accent-foreground font-bold flex items-center justify-center text-2xl mb-4">
                  {step.step}
                </div>
                <h3 className="text-xl font-bold text-foreground mb-2">
                  {step.title}
                </h3>
                <p className="text-sm text-muted-foreground">
                  {step.description}
                </p>
              </div>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* Current Partners */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Trusted by Leading Platforms
              </h2>
              <p className="text-lg text-muted-foreground">
                We partner with established technology providers to deliver seamless
                verification experiences
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {[
              {
                name: "Zoho",
                logo: "/logos/integrations/zoho.svg",
                description:
                  "VeriGate integrates with Zoho Recruit and Zoho People, enabling seamless background checks within Zoho's HR ecosystem.",
              },
              {
                name: "SAP SuccessFactors",
                logo: "/logos/integrations/sap.svg",
                description:
                  "Our SAP integration allows enterprise clients to trigger verifications directly from their SAP talent management workflows.",
              },
              {
                name: "Sage",
                logo: "/logos/integrations/sage.svg",
                description:
                  "VeriGate connects with Sage 300 People and Sage Business Cloud Payroll for automated pre-employment screening.",
              },
            ].map((partner) => (
              <Card key={partner.name} className="border-border text-center">
                <CardHeader>
                  <div className="mx-auto h-16 flex items-center justify-center mb-4">
                    <img
                      src={partner.logo}
                      alt={partner.name}
                      className="h-12 w-auto max-w-[160px] object-contain"
                    />
                  </div>
                  <CardTitle className="text-xl">{partner.name}</CardTitle>
                </CardHeader>
                <CardContent>
                  <CardDescription className="text-sm">
                    {partner.description}
                  </CardDescription>
                </CardContent>
              </Card>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl text-center">
          <AnimatedSection>
            <Card className="border-accent bg-gradient-to-br from-primary/5 to-accent/5">
              <CardHeader className="text-center pb-6">
                <CardTitle className="text-3xl md:text-4xl mb-4">
                  Ready to Partner with VeriGate?
                </CardTitle>
                <CardDescription className="text-lg">
                  Join 50+ partners growing their business with South Africa's
                  leading verification platform. Apply today and our partner team
                  will be in touch within 5 business days.
                </CardDescription>
              </CardHeader>
              <CardContent className="flex flex-col sm:flex-row gap-4 justify-center">
                <Button size="lg" className="min-w-[200px]" asChild>
                  <Link to="/contact">
                    Apply to Partner Program
                    <ArrowRight className="w-4 h-4 ml-2" />
                  </Link>
                </Button>
                <Button
                  size="lg"
                  variant="outline"
                  className="min-w-[200px]"
                  asChild
                >
                  <a href="mailto:partners@verigate.co.za">
                    Email partners@verigate.co.za
                  </a>
                </Button>
              </CardContent>
            </Card>
          </AnimatedSection>
        </div>
      </section>
    </div>
  );
};

export default PartnerProgram;
