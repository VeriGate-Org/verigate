import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  LayoutDashboard,
  Globe,
  Upload,
  ClipboardList,
  GitBranch,
  ShieldCheck,
  Sparkles,
  CircleDot,
  ArrowRightCircle,
  Star,
  ArrowRight,
  Zap,
  Lock,
  BarChart3,
  Code,
} from "lucide-react";
import { Link } from "react-router-dom";
import { PlatformDashboard } from "@/components/illustrations/PlatformDashboard";

const bulletIcons = [Sparkles, CircleDot, ArrowRightCircle, Star];

const Platform = () => {
  const modules = [
    {
      icon: LayoutDashboard,
      title: "Dashboard",
      description:
        "Central hub for managing all verifications, tracking progress, and viewing analytics across your organisation.",
      features: [
        "Real-time verification status overview with live counters",
        "Team activity feed and user-level audit logs",
        "Customisable widgets and role-based views",
        "Exportable reports and scheduled email summaries",
      ],
    },
    {
      icon: Code,
      title: "API Gateway",
      description:
        "Production-ready RESTful API with OAuth 2.0 authentication, webhooks, rate limiting, and SDKs for major languages.",
      features: [
        "OAuth 2.0 and API key authentication with scoped permissions",
        "Real-time webhooks with automatic retry and signature verification",
        "Rate limiting with configurable thresholds per endpoint",
        "Official SDKs for Python, Node.js, Java, C#, PHP, and Ruby",
      ],
    },
    {
      icon: Upload,
      title: "Bulk Processing",
      description:
        "CSV upload and batch processing with async verification for high-volume clients processing thousands of checks.",
      features: [
        "Drag-and-drop CSV upload with template validation",
        "Batch processing up to 10,000 records per upload",
        "Async verification with progress tracking and email notifications",
        "Downloadable results in CSV, Excel, and PDF formats",
      ],
    },
    {
      icon: ClipboardList,
      title: "Case Tracking",
      description:
        "Real-time verification status tracking, comprehensive audit trails, and quality assurance workflow management.",
      features: [
        "Granular status updates from submission to completion",
        "Full audit trail with timestamps and user attribution",
        "QA review workflow with approval and rejection actions",
        "Case notes, internal comments, and document attachments",
      ],
    },
    {
      icon: GitBranch,
      title: "Workflow Builder",
      description:
        "Create custom verification workflows with conditional logic, approval chains, and automated routing rules.",
      features: [
        "Visual drag-and-drop workflow designer",
        "Conditional logic based on verification results or risk scores",
        "Multi-level approval chains with escalation rules",
        "Reusable workflow templates for common screening scenarios",
      ],
    },
    {
      icon: ShieldCheck,
      title: "Compliance Engine",
      description:
        "Automated POPIA and FICA compliance, consent management, data retention policies, and regulatory reporting.",
      features: [
        "Automated POPIA consent capture and record-keeping",
        "FICA compliance checks with CDD/EDD workflows",
        "Configurable data retention and automatic purging schedules",
        "Regulatory audit reports with one-click export",
      ],
    },
  ];

  const platformFeatures = [
    {
      icon: Zap,
      title: "Sub-24hr Turnaround",
      description:
        "Most verifications completed within 24 hours, with priority processing available for urgent checks.",
    },
    {
      icon: Lock,
      title: "Bank-Grade Security",
      description:
        "ISO 27001 and SOC 2 Type II certified with end-to-end AES-256 encryption and TLS 1.3.",
    },
    {
      icon: BarChart3,
      title: "Real-Time Analytics",
      description:
        "Live dashboards with volume trends, turnaround metrics, compliance scores, and team performance.",
    },
    {
      icon: Globe,
      title: "SA Data Sources",
      description:
        "Direct integrations with DHA, SAPS, SAQA, CIPC, TransUnion, Experian, and XDS.",
    },
  ];

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div className="space-y-6">
              <Badge variant="secondary" className="mb-4">
                VeriGate Platform
              </Badge>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                One Platform for All Your
                <span className="block text-accent mt-2">Background Screening Needs</span>
              </h1>
              <p className="text-xl text-muted-foreground max-w-3xl">
                Six powerful modules working together to deliver fast, accurate, and
                POPIA-compliant background verification for South African organisations.
                From single checks to enterprise-scale batch processing.
              </p>
              <div className="flex gap-4 flex-wrap">
                <Button size="lg" asChild>
                  <Link to="/request-demo">Request a Demo</Link>
                </Button>
                <Button size="lg" variant="outline" asChild>
                  <Link to="/pricing">View Pricing</Link>
                </Button>
              </div>
            </div>
            <div className="hidden lg:flex items-center justify-center">
              <PlatformDashboard className="w-full max-w-md opacity-90" animate />
            </div>
          </div>
        </div>
      </section>

      {/* Platform Stats */}
      <section className="py-12 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {[
              { value: "6", label: "Core Modules" },
              { value: "200+", label: "Active Clients" },
              { value: "99.2%", label: "Accuracy Rate" },
              { value: "99.9%", label: "Uptime SLA" },
            ].map((stat) => (
              <div key={stat.label} className="text-center">
                <div className="text-4xl md:text-5xl font-bold text-accent mb-2">
                  {stat.value}
                </div>
                <div className="text-sm text-muted-foreground">{stat.label}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Modules Grid */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Platform Modules
            </h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Six integrated modules that cover every aspect of the background
              screening lifecycle
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            {modules.map((module, index) => {
              const Icon = module.icon;
              return (
                <Card
                  key={index}
                  className="border-border hover:shadow-lg transition-shadow"
                >
                  <CardHeader>
                    <div className="flex items-center gap-3 mb-2">
                      <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                        <Icon className="w-6 h-6 text-accent" />
                      </div>
                      <CardTitle className="text-xl">{module.title}</CardTitle>
                    </div>
                    <CardDescription className="text-base">
                      {module.description}
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <ul className="space-y-3">
                      {module.features.map((feature, idx) => {
                        const BulletIcon = bulletIcons[idx % bulletIcons.length];
                        return (
                          <li key={idx} className="flex items-start gap-2 text-sm">
                            <BulletIcon className="w-4 h-4 text-accent flex-shrink-0 mt-0.5" />
                            <span>{feature}</span>
                          </li>
                        );
                      })}
                    </ul>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* Platform Features */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Why Choose VeriGate
            </h2>
            <p className="text-lg text-muted-foreground">
              Built for South African compliance, designed for enterprise scale
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {platformFeatures.map((feature, index) => {
              const Icon = feature.icon;
              return (
                <Card key={index} className="border-border text-center">
                  <CardHeader>
                    <div className="mx-auto w-16 h-16 rounded-lg bg-accent/10 flex items-center justify-center mb-4">
                      <Icon className="w-8 h-8 text-accent" />
                    </div>
                    <CardTitle className="text-lg">{feature.title}</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <CardDescription>{feature.description}</CardDescription>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* Integration Section */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-6">
                Integrates with Your Existing Systems
              </h2>
              <p className="text-lg text-muted-foreground mb-6">
                VeriGate connects seamlessly with the HR, payroll, and recruitment
                tools your team already uses. Our open API and pre-built connectors
                make integration straightforward.
              </p>
              <div className="space-y-3 mb-8">
                {[
                  "HRIS and Payroll systems (SAP SuccessFactors, Sage, PaySpace)",
                  "Applicant Tracking Systems (BambooHR, Workday, Lever)",
                  "Custom ERP and CRM integrations via REST API",
                  "Zapier and Make (Integromat) for no-code workflows",
                ].map((item, idx) => (
                  <div key={idx} className="flex items-start gap-2 text-sm">
                    <ArrowRightCircle className="w-4 h-4 text-accent flex-shrink-0 mt-0.5" />
                    <span>{item}</span>
                  </div>
                ))}
              </div>
              <Button variant="outline" asChild>
                <Link to="/integrations">
                  View All Integrations <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
            </div>
            <div className="grid grid-cols-2 gap-4">
              {[
                { name: "SAP SuccessFactors", logo: "/logos/integrations/sap.svg", category: "HRIS" },
                { name: "BambooHR", logo: "/logos/integrations/bamboohr.svg", category: "ATS" },
                { name: "Sage", logo: "/logos/integrations/sage.svg", category: "Payroll" },
                { name: "PaySpace", logo: "/logos/integrations/payspace.svg", category: "Payroll" },
                { name: "Zapier", logo: "/logos/integrations/zapier.svg", category: "Automation" },
                { name: "Slack", logo: "/logos/integrations/slack.svg", category: "Communication" },
              ].map((integration, idx) => (
                <Card key={idx} className="text-center border-border">
                  <CardContent className="pt-6 flex flex-col items-center gap-3">
                    <img
                      src={integration.logo}
                      alt={integration.name}
                      className="h-8 w-auto max-w-[120px] object-contain"
                    />
                    <div>
                      <div className="font-semibold mb-1 text-sm">{integration.name}</div>
                      <Badge variant="secondary" className="text-xs">
                        {integration.category}
                      </Badge>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <Card className="border-accent bg-gradient-to-br from-primary/5 to-accent/5">
            <CardHeader className="text-center pb-6">
              <CardTitle className="text-3xl md:text-4xl mb-4">
                See the Full Platform in Action
              </CardTitle>
              <CardDescription className="text-lg">
                Book a personalised walkthrough of all six modules and see how VeriGate
                integrates with your existing systems.
              </CardDescription>
            </CardHeader>
            <CardContent className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" className="min-w-[200px]" asChild>
                <Link to="/request-demo">
                  Request a Demo
                  <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" className="min-w-[200px]" asChild>
                <Link to="/contact">Talk to an Engineer</Link>
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  );
};

export default Platform;
