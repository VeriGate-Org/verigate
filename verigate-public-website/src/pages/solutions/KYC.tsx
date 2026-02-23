import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  Shield,
  Globe,
  Zap,
  FileCheck,
  UserCheck,
  MapPin,
  Calendar,
  AlertCircle,
  TrendingUp,
  Clock,
  ArrowRight
} from "lucide-react";
import { Link } from "react-router-dom";

const KYC = () => {
  const verificationSteps = [
    {
      step: 1,
      title: "Document Capture",
      description: "User uploads government-issued ID via web, mobile, or API",
      icon: FileCheck,
      duration: "< 10 seconds",
    },
    {
      step: 2,
      title: "AI Analysis",
      description: "Our AI extracts data, verifies authenticity, checks for tampering",
      icon: Zap,
      duration: "2-3 seconds",
    },
    {
      step: 3,
      title: "Biometric Match",
      description: "Facial recognition matches selfie to ID photo with liveness detection",
      icon: UserCheck,
      duration: "1-2 seconds",
    },
    {
      step: 4,
      title: "Results Delivered",
      description: "Instant verification results with confidence scores and extracted data",
      icon: CheckCircle2,
      duration: "< 1 second",
    },
  ];

  const supportedDocuments = [
    { region: "North America", types: "Passports, Driver's Licenses, National IDs, Resident Permits", countries: "2 countries, 500+ document variations" },
    { region: "Europe", types: "Passports, National IDs, Residence Permits, Driver's Licenses", countries: "44 countries, 1,200+ document variations" },
    { region: "Asia Pacific", types: "Passports, National IDs, Driver's Licenses, Work Permits", countries: "56 countries, 1,800+ document variations" },
    { region: "Latin America", types: "Passports, National IDs, Voter IDs, Driver's Licenses", countries: "33 countries, 900+ document variations" },
    { region: "Middle East & Africa", types: "Passports, National IDs, Residence Cards, Emirates IDs", countries: "55 countries, 1,100+ document variations" },
  ];

  const verificationMethods = [
    {
      title: "Identity Verification",
      description: "Verify that the person is who they claim to be using government-issued documents",
      checks: [
        "Document authenticity validation",
        "Face match with liveness detection",
        "Data extraction and validation",
        "Tampering and fraud detection",
      ],
    },
    {
      title: "Age Verification",
      description: "Confirm users meet minimum age requirements for age-restricted services",
      checks: [
        "Date of birth extraction",
        "Age calculation and validation",
        "Document expiry check",
        "Minor protection compliance",
      ],
    },
    {
      title: "Address Verification",
      description: "Validate residential address through document checks and utility bills",
      checks: [
        "Proof of address validation",
        "Geographic consistency checks",
        "Utility bill verification",
        "Address standardization",
      ],
    },
    {
      title: "PEP & Sanctions Screening",
      description: "Screen against Politically Exposed Persons and sanctions lists",
      checks: [
        "Global watchlist screening",
        "PEP database matching",
        "Sanctions list verification",
        "Adverse media monitoring",
      ],
    },
  ];

  const complianceFrameworks = [
    {
      name: "GDPR",
      region: "European Union",
      description: "Full compliance with EU data protection regulations for identity verification",
    },
    {
      name: "KYC/AML",
      region: "Global",
      description: "Meet Know Your Customer and Anti-Money Laundering requirements worldwide",
    },
    {
      name: "CCPA",
      region: "United States",
      description: "California Consumer Privacy Act compliant identity verification",
    },
    {
      name: "AUSTRAC",
      region: "Australia",
      description: "Australian Transaction Reports and Analysis Centre compliance",
    },
    {
      name: "PIPEDA",
      region: "Canada",
      description: "Personal Information Protection and Electronic Documents Act compliance",
    },
    {
      name: "POPI",
      region: "South Africa",
      description: "Protection of Personal Information Act compliant verification",
    },
  ];

  const metrics = [
    { label: "Verification Speed", value: "< 5 seconds", icon: Clock },
    { label: "Accuracy Rate", value: "99.8%", icon: CheckCircle2 },
    { label: "Countries Supported", value: "190+", icon: Globe },
    { label: "Document Types", value: "5,000+", icon: FileCheck },
  ];

  const pricingTiers = [
    {
      tier: "Starter",
      price: "$0.50",
      description: "Per verification",
      features: ["Basic KYC verification", "Document validation", "API access", "Email support"],
    },
    {
      tier: "Professional",
      price: "$0.35",
      description: "Per verification",
      features: ["Advanced KYC + AML", "Biometric matching", "Priority API access", "24/7 support", "Custom webhooks"],
      popular: true,
    },
    {
      tier: "Enterprise",
      price: "Custom",
      description: "Volume pricing",
      features: ["Everything in Pro", "Dedicated infrastructure", "SLA guarantees", "Custom integrations", "Account manager"],
    },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-background to-primary/10">
        <div className="container mx-auto max-w-6xl">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div>
              <Badge className="mb-4" variant="secondary">
                KYC Verification
              </Badge>
              <h1 className="text-4xl md:text-5xl font-bold mb-6">
                Know Your Customer
                <span className="text-primary block mt-2">Verification Made Simple</span>
              </h1>
              <p className="text-xl text-muted-foreground mb-8">
                Verify customer identities in seconds with our AI-powered KYC solution. 
                Supporting 5,000+ document types across 190+ countries with 99.8% accuracy.
              </p>
              
              <div className="flex gap-4 flex-wrap mb-8">
                <Button size="lg" asChild>
                  <Link to="/contact">Get Started</Link>
                </Button>
                <Button size="lg" variant="outline" asChild>
                  <Link to="/contact">Try Demo</Link>
                </Button>
              </div>

              {/* Key Metrics */}
              <div className="grid grid-cols-2 gap-4">
                {metrics.map((metric, index) => (
                  <div key={index} className="flex items-center gap-3">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <metric.icon className="w-5 h-5 text-primary" />
                    </div>
                    <div>
                      <div className="font-bold text-lg">{metric.value}</div>
                      <div className="text-sm text-muted-foreground">{metric.label}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="relative">
              <div className="aspect-square bg-gradient-to-br from-primary/20 to-primary/5 rounded-2xl p-8 flex items-center justify-center">
                <div className="text-center">
                  <Shield className="w-32 h-32 text-primary mx-auto mb-4" />
                  <p className="text-lg font-semibold">Trusted by 500+ Enterprises</p>
                  <p className="text-muted-foreground">Processing 50M+ verifications annually</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">How KYC Verification Works</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Complete identity verification in under 5 seconds with our 4-step process
            </p>
          </div>

          <div className="grid md:grid-cols-4 gap-6">
            {verificationSteps.map((step, index) => (
              <div key={index} className="relative">
                <Card className="h-full hover:shadow-lg transition-shadow">
                  <CardHeader>
                    <div className="flex items-center gap-3 mb-3">
                      <div className="w-10 h-10 rounded-full bg-primary text-primary-foreground flex items-center justify-center font-bold">
                        {step.step}
                      </div>
                      <step.icon className="w-6 h-6 text-primary" />
                    </div>
                    <CardTitle className="text-lg">{step.title}</CardTitle>
                    <CardDescription>{step.description}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <Badge variant="secondary" className="text-xs">
                      <Clock className="w-3 h-3 mr-1" />
                      {step.duration}
                    </Badge>
                  </CardContent>
                </Card>
                {index < verificationSteps.length - 1 && (
                  <div className="hidden md:block absolute top-1/2 -right-3 transform -translate-y-1/2 z-10">
                    <ArrowRight className="w-6 h-6 text-primary" />
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Supported Documents */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Global Document Coverage</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              We support 5,000+ document types from 190+ countries worldwide
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {supportedDocuments.map((region, index) => (
              <Card key={index}>
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <MapPin className="w-5 h-5 text-primary" />
                    <CardTitle className="text-lg">{region.region}</CardTitle>
                  </div>
                  <CardDescription className="text-xs">{region.countries}</CardDescription>
                </CardHeader>
                <CardContent>
                  <p className="text-sm">{region.types}</p>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="mt-8 text-center">
            <Button variant="outline" asChild>
              <Link to="/resources">
                View Complete Document List <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Verification Methods */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Comprehensive Verification Methods</h2>
            <p className="text-lg text-muted-foreground">
              Multiple verification layers for maximum security and compliance
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {verificationMethods.map((method, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle>{method.title}</CardTitle>
                  <CardDescription>{method.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {method.checks.map((check, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{check}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Compliance Coverage */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Global Compliance Coverage</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Meet regulatory requirements across all major jurisdictions
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {complianceFrameworks.map((framework, index) => (
              <Card key={index}>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Shield className="w-5 h-5 text-primary" />
                    {framework.name}
                  </CardTitle>
                  <Badge variant="outline" className="w-fit">{framework.region}</Badge>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground">{framework.description}</p>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="mt-8 text-center">
            <Button variant="outline" asChild>
              <Link to="/security">
                View Security & Compliance <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Pricing Preview */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Transparent Pricing</h2>
            <p className="text-lg text-muted-foreground">
              Simple, predictable pricing that scales with your business
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {pricingTiers.map((tier, index) => (
              <Card key={index} className={tier.popular ? "border-primary shadow-lg scale-105" : ""}>
                <CardHeader>
                  {tier.popular && (
                    <Badge className="w-fit mb-2">Most Popular</Badge>
                  )}
                  <CardTitle>{tier.tier}</CardTitle>
                  <div className="mt-4">
                    <span className="text-4xl font-bold">{tier.price}</span>
                    <span className="text-muted-foreground ml-2">{tier.description}</span>
                  </div>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2 mb-6">
                    {tier.features.map((feature, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{feature}</span>
                      </li>
                    ))}
                  </ul>
                  <Button className="w-full" variant={tier.popular ? "default" : "outline"} asChild>
                    <Link to="/contact">Get Started</Link>
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="mt-8 text-center">
            <Button variant="link" asChild>
              <Link to="/pricing">
                View Complete Pricing <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Customer Testimonials - To be added */}
      {/* <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center">
            <h2 className="text-3xl font-bold mb-4">Trusted by Industry Leaders</h2>
            <p className="text-muted-foreground">Customer testimonials coming soon</p>
          </div>
        </div>
      </section> */}

      {/* Integration Guide */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Easy Integration</h2>
            <p className="text-lg text-muted-foreground">
              Get started in minutes with our developer-friendly API
            </p>
          </div>

          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>Quick Start Example</CardTitle>
              <CardDescription>Verify a user's identity with just a few lines of code</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// Initialize VeriGate SDK
const verigate = new VeriGate('your_api_key');

// Start KYC verification
const verification = await verigate.kyc.verify({
  documentFront: frontImageFile,
  documentBack: backImageFile,
  selfie: selfieImageFile,
  userData: {
    firstName: 'John',
    lastName: 'Doe',
    dateOfBirth: '1990-01-01'
  }
});

// Get results
if (verification.status === 'approved') {
  console.log('Verification successful!');
  console.log('Confidence Score:', verification.confidence);
  console.log('Extracted Data:', verification.data);
}`}</code>
              </pre>
            </CardContent>
          </Card>

          <div className="mt-8 text-center">
            <Button variant="outline" asChild>
              <Link to="/developers">
                View Full Documentation <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-white">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            Ready to Streamline Your KYC Process?
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join hundreds of companies using VeriGate for fast, accurate identity verification
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" variant="secondary" asChild>
              <Link to="/contact">Request a Demo</Link>
            </Button>
            <Button size="lg" variant="outline" className="bg-white/10 hover:bg-white/20 border-white text-white" asChild>
              <Link to="/pricing">View Pricing</Link>
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default KYC;
