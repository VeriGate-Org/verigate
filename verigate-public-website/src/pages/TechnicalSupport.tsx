import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Mail,
  Phone,
  TicketCheck,
  Clock,
  CircleCheck,
  ArrowRight,
  BookOpen,
  Code,
  LayoutDashboard,
  AlertTriangle,
  ShieldCheck,
  Headphones,
  Zap,
  Building2,
  Rocket,
  Wrench,
  HelpCircle,
} from "lucide-react";
import { Link } from "react-router-dom";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

const TechnicalSupport = () => {
  const supportChannels = [
    {
      icon: Mail,
      title: "Email Support",
      contact: "support@verigate.co.za",
      description:
        "Send detailed support requests with screenshots and error logs for thorough troubleshooting. Our team triages every ticket and provides a detailed response.",
      availability: "24/7 submission, responses during business hours",
      href: "mailto:support@verigate.co.za",
      action: "Send Email",
    },
    {
      icon: Phone,
      title: "Phone Support",
      contact: "+27 21 555 0124",
      description:
        "Speak directly with a support engineer for urgent issues, complex technical questions, or when you need real-time guidance on integration or platform usage.",
      availability: "Mon-Fri, 08:00-17:00 SAST",
      href: "tel:+27215550124",
      action: "Call Us",
    },
    {
      icon: TicketCheck,
      title: "Dashboard Ticket System",
      contact: "In-Platform Support",
      description:
        "Raise tickets directly from your VeriGate dashboard with automatic context capture, priority routing based on your plan tier, and real-time status tracking.",
      availability: "24/7 with auto-acknowledgement",
      href: "#",
      action: "Open Dashboard",
    },
  ];

  const slaTiers = [
    {
      icon: Zap,
      plan: "Starter",
      responseTime: "24 hours",
      channels: "Email only",
      hours: "Business hours (Mon-Fri, 08:00-17:00 SAST)",
      features: [
        "Email support with ticket tracking",
        "Access to knowledge base and documentation",
        "Community forum access",
        "Standard onboarding guide",
      ],
    },
    {
      icon: Building2,
      plan: "Professional",
      responseTime: "4 hours",
      channels: "Email & Phone",
      hours: "Extended hours (Mon-Fri, 07:00-19:00 SAST)",
      features: [
        "Priority email and phone support",
        "API integration assistance",
        "Dedicated onboarding session",
        "Quarterly account reviews",
      ],
      highlighted: true,
    },
    {
      icon: Rocket,
      plan: "Enterprise",
      responseTime: "2 hours",
      channels: "Email, Phone & Dashboard (24/7)",
      hours: "24/7 including weekends and public holidays",
      features: [
        "24/7 premium support with named engineers",
        "Dedicated account manager",
        "Custom integration development assistance",
        "On-site training and support available",
      ],
    },
  ];

  const integrationAssistance = [
    {
      title: "API Setup & Configuration",
      description:
        "Our engineers help you configure API authentication (OAuth 2.0 or API keys), set up webhooks with signature verification, and implement error handling and retry logic.",
    },
    {
      title: "SDK Integration Support",
      description:
        "Guidance on integrating our official SDKs for Python, Node.js, Java, C#, PHP, and Ruby into your application, including quick-start templates and code samples.",
    },
    {
      title: "HRIS & ATS Connectors",
      description:
        "Assistance connecting VeriGate to your existing HR systems including SAP SuccessFactors, BambooHR, Sage, PaySpace, and custom ERP integrations.",
    },
    {
      title: "Custom Workflow Design",
      description:
        "Help designing and implementing custom verification workflows with conditional logic, approval chains, and compliance rules tailored to your organisation.",
    },
  ];

  const knowledgeBaseCategories = [
    {
      icon: BookOpen,
      title: "Getting Started",
      description: "Account setup, first verification, dashboard orientation, and team onboarding",
      articleCount: 12,
    },
    {
      icon: Code,
      title: "API Integration",
      description: "REST API reference, authentication, endpoints, webhooks, SDKs, and code examples",
      articleCount: 28,
    },
    {
      icon: LayoutDashboard,
      title: "Dashboard Guide",
      description: "Navigation, reporting, user management, bulk uploads, and settings configuration",
      articleCount: 18,
    },
    {
      icon: AlertTriangle,
      title: "Troubleshooting",
      description: "Common errors, failed verifications, webhook issues, timeout handling, and fixes",
      articleCount: 24,
    },
    {
      icon: ShieldCheck,
      title: "Compliance",
      description: "POPIA requirements, FICA guidelines, data retention policies, and consent management",
      articleCount: 15,
    },
  ];

  const faqQuickLinks = [
    "How do I reset my API keys?",
    "What do verification status codes mean?",
    "How do I set up bulk upload processing?",
    "Why is my webhook not receiving events?",
    "How do I add team members to my account?",
    "What file formats are accepted for document uploads?",
  ];

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <AnimatedSection>
            <div className="max-w-4xl space-y-6">
              <Badge variant="secondary" className="mb-4">
                <Headphones className="w-3 h-3 mr-1" />
                Support
              </Badge>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                Technical Support
                <span className="block text-accent mt-2">& Help</span>
              </h1>
              <p className="text-xl text-muted-foreground max-w-3xl">
                Get the help you need, when you need it. Our technical support team
                is here to help you get the most out of VeriGate with fast response
                times and expert guidance.
              </p>
            </div>
          </AnimatedSection>
        </div>
      </section>

      {/* Support Channels */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Contact Support
              </h2>
              <p className="text-lg text-muted-foreground">
                Reach our support team through the channel that works best for you
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="grid md:grid-cols-3 gap-6">
            {supportChannels.map((channel) => {
              const Icon = channel.icon;
              return (
                <Card key={channel.title} className="border-border hover:shadow-lg transition-shadow">
                  <CardHeader>
                    <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center mb-4">
                      <Icon className="w-6 h-6 text-accent" />
                    </div>
                    <CardTitle className="text-xl">{channel.title}</CardTitle>
                    <a
                      href={channel.href}
                      className="text-accent font-semibold hover:underline"
                    >
                      {channel.contact}
                    </a>
                  </CardHeader>
                  <CardContent className="space-y-3">
                    <CardDescription>{channel.description}</CardDescription>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Clock className="w-4 h-4 flex-shrink-0" />
                      <span>{channel.availability}</span>
                    </div>
                    <Button variant="outline" size="sm" asChild>
                      <a href={channel.href}>
                        {channel.action}
                        <ArrowRight className="w-4 h-4 ml-2" />
                      </a>
                    </Button>
                  </CardContent>
                </Card>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* SLA Tiers */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Support SLA by Plan
              </h2>
              <p className="text-lg text-muted-foreground">
                Response times and support levels based on your subscription plan
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="grid md:grid-cols-3 gap-6">
            {slaTiers.map((tier) => {
              const Icon = tier.icon;
              return (
                <Card
                  key={tier.plan}
                  className={`border-border ${
                    tier.highlighted ? "ring-2 ring-accent shadow-lg" : ""
                  }`}
                >
                  {tier.highlighted && (
                    <div className="bg-accent text-accent-foreground text-center py-2 text-sm font-semibold rounded-t-lg">
                      Most Popular
                    </div>
                  )}
                  <CardHeader className="text-center">
                    <div className="mx-auto w-14 h-14 rounded-lg bg-accent/10 flex items-center justify-center mb-4">
                      <Icon className="w-7 h-7 text-accent" />
                    </div>
                    <CardTitle className="text-2xl">{tier.plan}</CardTitle>
                    <CardDescription className="text-sm">
                      {tier.channels}
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="text-center mb-6">
                      <div className="text-3xl font-bold text-accent mb-1">
                        {tier.responseTime}
                      </div>
                      <div className="text-sm text-muted-foreground">
                        response time
                      </div>
                    </div>

                    <div className="mb-4 text-center">
                      <Badge variant="secondary" className="text-xs">
                        <Clock className="w-3 h-3 mr-1" />
                        {tier.hours}
                      </Badge>
                    </div>

                    <ul className="space-y-3">
                      {tier.features.map((feature) => (
                        <li
                          key={feature}
                          className="flex items-start gap-2 text-sm"
                        >
                          <CircleCheck className="w-4 h-4 text-accent flex-shrink-0 mt-0.5" />
                          <span>{feature}</span>
                        </li>
                      ))}
                    </ul>
                  </CardContent>
                </Card>
              );
            })}
          </StaggeredList>

          <div className="text-center mt-8">
            <Link
              to="/pricing"
              className="text-accent hover:underline font-medium"
            >
              Compare all plans and pricing
            </Link>
          </div>
        </div>
      </section>

      {/* Integration Assistance */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <div className="flex items-center justify-center gap-3 mb-4">
                <Wrench className="w-6 h-6 text-accent" />
                <h2 className="text-3xl md:text-4xl font-bold text-foreground">
                  Integration Assistance
                </h2>
              </div>
              <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
                Need help integrating VeriGate into your systems? Our engineering team
                provides hands-on support for all integration scenarios.
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="grid md:grid-cols-2 gap-6">
            {integrationAssistance.map((item) => (
              <Card key={item.title} className="border-border hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{item.title}</CardTitle>
                </CardHeader>
                <CardContent>
                  <CardDescription>{item.description}</CardDescription>
                </CardContent>
              </Card>
            ))}
          </StaggeredList>

          <div className="text-center mt-8">
            <Button variant="outline" asChild>
              <Link to="/integrations">
                View API & Integrations
                <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Knowledge Base */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Knowledge Base
              </h2>
              <p className="text-lg text-muted-foreground">
                Browse our self-service documentation by category
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {knowledgeBaseCategories.map((category) => {
              const Icon = category.icon;
              return (
                <Card
                  key={category.title}
                  className="border-border hover:shadow-lg transition-shadow cursor-pointer"
                >
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="w-10 h-10 rounded-lg bg-accent/10 flex items-center justify-center">
                        <Icon className="w-5 h-5 text-accent" />
                      </div>
                      <Badge variant="secondary" className="text-xs">
                        {category.articleCount} articles
                      </Badge>
                    </div>
                    <CardTitle className="text-lg mt-4">{category.title}</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <CardDescription>{category.description}</CardDescription>
                  </CardContent>
                </Card>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* FAQ Quick Links */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Frequently Asked Questions
              </h2>
              <p className="text-lg text-muted-foreground">
                Quick answers to common technical questions
              </p>
            </div>
          </AnimatedSection>

          <AnimatedSection delay={0.2}>
            <Card className="border-border">
              <CardContent className="pt-6">
                <ul className="space-y-4">
                  {faqQuickLinks.map((question) => (
                    <li key={question}>
                      <Link
                        to="/faqs"
                        className="flex items-center gap-3 p-3 rounded-lg hover:bg-accent/5 transition-colors group"
                      >
                        <HelpCircle className="w-5 h-5 text-accent flex-shrink-0" />
                        <span className="text-foreground group-hover:text-accent transition-colors">
                          {question}
                        </span>
                        <ArrowRight className="w-4 h-4 text-muted-foreground ml-auto flex-shrink-0 group-hover:text-accent transition-colors" />
                      </Link>
                    </li>
                  ))}
                </ul>

                <div className="text-center mt-6 pt-6 border-t border-border">
                  <Button variant="outline" asChild>
                    <Link to="/faqs">
                      View All FAQs
                      <ArrowRight className="w-4 h-4 ml-2" />
                    </Link>
                  </Button>
                </div>
              </CardContent>
            </Card>
          </AnimatedSection>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <Card className="border-accent bg-gradient-to-br from-primary/5 to-accent/5">
              <CardHeader className="text-center pb-6">
                <div className="mx-auto w-16 h-16 rounded-full bg-accent/10 flex items-center justify-center mb-4">
                  <Headphones className="w-8 h-8 text-accent" />
                </div>
                <CardTitle className="text-3xl md:text-4xl mb-4">
                  Need Help Right Now?
                </CardTitle>
                <CardDescription className="text-lg">
                  Our support team is standing by. Reach out via email, phone, or your
                  dashboard and we'll get you sorted.
                </CardDescription>
              </CardHeader>
              <CardContent className="flex flex-col sm:flex-row gap-4 justify-center">
                <Button size="lg" className="min-w-[200px]" asChild>
                  <a href="mailto:support@verigate.co.za">
                    <Mail className="w-4 h-4 mr-2" />
                    Email Support
                  </a>
                </Button>
                <Button size="lg" variant="outline" className="min-w-[200px]" asChild>
                  <a href="tel:+27215550124">
                    <Phone className="w-4 h-4 mr-2" />
                    Call +27 21 555 0124
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

export default TechnicalSupport;
