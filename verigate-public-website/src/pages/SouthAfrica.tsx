import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Shield,
  Scale,
  Landmark,
  Building2,
  Users,
  Briefcase,
  ShieldCheck,
  CircleCheck,
  BadgeCheck,
  FileCheck,
  ArrowRight,
  FileText,
  CreditCard,
  Globe,
} from "lucide-react";
import { Link } from "react-router-dom";
import { SouthAfricaMap } from "@/components/illustrations/SouthAfricaMap";

const bulletIcons = [ShieldCheck, CircleCheck, BadgeCheck, FileCheck];

const SouthAfrica = () => {
  const regulatoryFrameworks = [
    {
      icon: Shield,
      name: "POPIA",
      fullName: "Protection of Personal Information Act",
      description:
        "Full compliance with South Africa's data protection legislation. We manage consent, data minimisation, purpose limitation, and subject access requests.",
    },
    {
      icon: Scale,
      name: "FICA",
      fullName: "Financial Intelligence Centre Act",
      description:
        "KYC/CDD compliance for regulated industries. Customer due diligence, enhanced due diligence, and suspicious transaction reporting.",
    },
    {
      icon: CreditCard,
      name: "NCA",
      fullName: "National Credit Act",
      description:
        "Credit screening compliance including affordability assessments, credit bureau checks, and responsible lending requirements.",
    },
    {
      icon: Building2,
      name: "Companies Act",
      fullName: "Companies Act 71 of 2008",
      description:
        "Business verification through CIPC, director searches, company status checks, and beneficial ownership verification.",
    },
    {
      icon: Users,
      name: "LRA",
      fullName: "Labour Relations Act",
      description:
        "Employment screening aligned with LRA requirements, including fair hiring practices and pre-employment verification guidelines.",
    },
    {
      icon: Briefcase,
      name: "BCEA",
      fullName: "Basic Conditions of Employment Act",
      description:
        "Compliance with employment legislation including right-to-work verification, work permit validation, and employment eligibility.",
    },
  ];

  const dataSources = [
    {
      name: "DHA",
      fullName: "Department of Home Affairs",
      logo: "/logos/sa-sources/dha.svg",
      description: "Identity verification, citizenship status, and marriage records",
      category: "Government",
    },
    {
      name: "SAPS",
      fullName: "South African Police Service",
      logo: "/logos/sa-sources/saps.svg",
      description: "Criminal record checks and police clearance certificates",
      category: "Government",
    },
    {
      name: "SAQA",
      fullName: "South African Qualifications Authority",
      logo: "/logos/sa-sources/saqa.svg",
      description: "Qualification verification, NQF levels, and education authentication",
      category: "Government",
    },
    {
      name: "CIPC",
      fullName: "Companies and Intellectual Property Commission",
      logo: "/logos/sa-sources/cipc.svg",
      description: "Company registrations, director searches, and business verification",
      category: "Government",
    },
    {
      name: "TransUnion SA",
      fullName: "TransUnion South Africa",
      logo: "/logos/sa-sources/transunion.svg",
      description: "Consumer credit reports, credit scores, and financial history",
      category: "Credit Bureau",
    },
    {
      name: "Experian SA",
      fullName: "Experian South Africa",
      logo: "/logos/sa-sources/experian.svg",
      description: "Credit checks, fraud detection, and consumer financial data",
      category: "Credit Bureau",
    },
    {
      name: "XDS",
      fullName: "XDS Data",
      logo: "/logos/sa-sources/xds.png",
      description: "Credit data, alternative lending data, and collections records",
      category: "Credit Bureau",
    },
    {
      name: "HPCSA",
      fullName: "Health Professions Council of South Africa",
      logo: "/logos/sa-sources/hpcsa.png",
      description: "Healthcare professional registration and licence verification",
      category: "Professional Body",
    },
  ];

  const creditBureaus = [
    {
      name: "TransUnion SA",
      logo: "/logos/sa-sources/transunion.svg",
      checks: [
        "Consumer credit reports",
        "Credit scores and risk ratings",
        "Payment history and defaults",
        "Judgments and administration orders",
      ],
    },
    {
      name: "Experian SA",
      logo: "/logos/sa-sources/experian.svg",
      checks: [
        "Comprehensive credit profiles",
        "Fraud indicators and alerts",
        "Income estimation models",
        "Identity verification services",
      ],
    },
    {
      name: "XDS Data",
      logo: "/logos/sa-sources/xds.png",
      checks: [
        "Alternative credit data",
        "Collections and debt review status",
        "Telecommunications payment data",
        "Rental payment history",
      ],
    },
  ];

  const documentTypes = [
    {
      category: "Identity Documents",
      items: [
        "Smart ID Card (new green card)",
        "South African ID Book (green book)",
        "South African Passport",
        "Temporary ID Certificate",
      ],
    },
    {
      category: "Work & Residence",
      items: [
        "Work Permit / Work Visa",
        "General Work Visa",
        "Critical Skills Visa",
        "Permanent Residence Permit",
      ],
    },
    {
      category: "Asylum & Refugee",
      items: [
        "Asylum Seeker Permit (Section 22)",
        "Refugee Status Permit (Section 24)",
        "Asylum Transit Visa",
        "Refugee Travel Document",
      ],
    },
    {
      category: "Driving & Vehicle",
      items: [
        "South African Driver's Licence (credit card format)",
        "Learner's Licence",
        "Professional Driving Permit (PrDP)",
        "International Driving Permit",
      ],
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
                <Globe className="w-3 h-3 mr-1" />
                South Africa
              </Badge>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                Background Screening
                <span className="block text-accent mt-2">Built for South Africa</span>
              </h1>
              <p className="text-xl text-muted-foreground max-w-3xl">
                VeriGate is purpose-built for the South African regulatory landscape.
                Direct integrations with local government databases, credit bureaus, and
                professional bodies ensure accurate, compliant verification every time.
              </p>
              <div className="flex gap-4 flex-wrap">
                <Button size="lg" asChild>
                  <Link to="/request-demo">Request a Demo</Link>
                </Button>
                <Button size="lg" variant="outline" asChild>
                  <Link to="/pricing">View SA Pricing</Link>
                </Button>
              </div>
            </div>
            <div className="hidden lg:flex items-center justify-center">
              <SouthAfricaMap className="w-full max-w-md opacity-90" animate />
            </div>
          </div>
        </div>
      </section>

      {/* SA Stats */}
      <section className="py-12 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {[
              { value: "8+", label: "SA Data Sources" },
              { value: "6", label: "Regulatory Frameworks" },
              { value: "3", label: "Credit Bureaus" },
              { value: "200+", label: "SA Clients" },
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

      {/* Regulatory Frameworks */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              SA Regulatory Compliance
            </h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              VeriGate is designed to meet the requirements of all major South
              African regulatory frameworks
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {regulatoryFrameworks.map((framework, index) => {
              const Icon = framework.icon;
              return (
                <Card key={index} className="border-border hover:shadow-lg transition-shadow">
                  <CardHeader>
                    <div className="flex items-center gap-3 mb-2">
                      <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                        <Icon className="w-6 h-6 text-accent" />
                      </div>
                      <div>
                        <CardTitle className="text-lg">{framework.name}</CardTitle>
                        <p className="text-xs text-muted-foreground">
                          {framework.fullName}
                        </p>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <CardDescription className="text-sm">
                      {framework.description}
                    </CardDescription>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* Data Sources */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              SA Data Sources & Integrations
            </h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Direct, real-time connections to South Africa's authoritative data sources
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {dataSources.map((source, index) => (
              <Card key={index} className="border-border hover:shadow-lg transition-shadow">
                <CardHeader className="pb-2">
                  <div className="flex items-center justify-between mb-2">
                    <img
                      src={source.logo}
                      alt={source.name}
                      className="h-8 w-auto max-w-[100px] object-contain"
                    />
                    <Badge variant="secondary" className="text-xs">
                      {source.category}
                    </Badge>
                  </div>
                  <CardTitle className="text-lg">{source.name}</CardTitle>
                  <p className="text-xs text-muted-foreground">{source.fullName}</p>
                </CardHeader>
                <CardContent>
                  <CardDescription className="text-sm">
                    {source.description}
                  </CardDescription>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Credit Bureaus */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Credit Bureau Integration
            </h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Comprehensive credit screening through all three major South African
              credit bureaus
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {creditBureaus.map((bureau, index) => (
              <Card key={index} className="border-border">
                <CardHeader>
                  <div className="h-12 flex items-center mb-4">
                    <img
                      src={bureau.logo}
                      alt={bureau.name}
                      className="h-10 w-auto max-w-[140px] object-contain"
                    />
                  </div>
                  <CardTitle className="text-xl">{bureau.name}</CardTitle>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {bureau.checks.map((check, idx) => {
                      const BulletIcon = bulletIcons[idx % bulletIcons.length];
                      return (
                        <li key={idx} className="flex items-start gap-2 text-sm">
                          <BulletIcon className="w-4 h-4 text-accent flex-shrink-0 mt-0.5" />
                          <span>{check}</span>
                        </li>
                      );
                    })}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* SA Document Types */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Supported SA Document Types
            </h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Comprehensive coverage of South African identity and supporting
              documents
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {documentTypes.map((docType, index) => (
              <Card key={index} className="border-border">
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <FileText className="w-5 h-5 text-accent" />
                    <CardTitle className="text-base">{docType.category}</CardTitle>
                  </div>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {docType.items.map((item, idx) => {
                      const BulletIcon = bulletIcons[idx % bulletIcons.length];
                      return (
                        <li key={idx} className="flex items-start gap-2 text-sm">
                          <BulletIcon className="w-3 h-3 text-accent flex-shrink-0 mt-1" />
                          <span className="text-muted-foreground">{item}</span>
                        </li>
                      );
                    })}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <Card className="border-accent bg-gradient-to-br from-primary/5 to-accent/5">
            <CardHeader className="text-center pb-6">
              <CardTitle className="text-3xl md:text-4xl mb-4">
                Ready to Screen the South African Way?
              </CardTitle>
              <CardDescription className="text-lg">
                Join 200+ South African organisations using VeriGate for fast,
                accurate, and compliant background screening.
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
                <Link to="/contact">Contact Sales</Link>
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  );
};

export default SouthAfrica;
