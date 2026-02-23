import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  Shield, 
  Zap, 
  Globe, 
  CheckCircle2,
  FileCheck,
  Users,
  Eye,
  Target,
  ArrowRight,
  Fingerprint,
  ScanFace,
  FileSearch,
  AlertTriangle,
  Code,
  Cloud,
  Lock,
  TrendingUp
} from "lucide-react";
import { Link } from "react-router-dom";

const Product = () => {
  const capabilities = [
    {
      icon: FileCheck,
      title: "Document Verification",
      description: "AI-powered verification of 5,000+ document types from 190+ countries with 99.8% accuracy.",
      features: ["ID Cards & Passports", "Driver's Licenses", "Utility Bills", "Bank Statements"],
    },
    {
      icon: ScanFace,
      title: "Biometric Authentication",
      description: "Advanced facial recognition and liveness detection to prevent fraud and deepfakes.",
      features: ["3D Liveness Detection", "Face Matching", "Anti-Spoofing", "Passive Verification"],
    },
    {
      icon: Shield,
      title: "KYC Compliance",
      description: "Comprehensive Know Your Customer verification meeting global regulatory requirements.",
      features: ["Identity Verification", "Age Verification", "Address Verification", "PEP Screening"],
    },
    {
      icon: AlertTriangle,
      title: "AML Screening",
      description: "Real-time screening against global watchlists, sanctions, and PEP databases.",
      features: ["Sanctions Lists", "PEP Database", "Adverse Media", "Ongoing Monitoring"],
    },
  ];

  const features = [
    {
      icon: Zap,
      title: "Lightning Fast",
      description: "Average verification time under 5 seconds with real-time results.",
    },
    {
      icon: Globe,
      title: "Global Coverage",
      description: "Support for 190+ countries and 5,000+ document types worldwide.",
    },
    {
      icon: Lock,
      title: "Bank-Grade Security",
      description: "ISO 27001, SOC 2 Type II certified with end-to-end encryption.",
    },
    {
      icon: Code,
      title: "Developer Friendly",
      description: "RESTful API, webhooks, and SDKs for all major programming languages.",
    },
    {
      icon: Cloud,
      title: "Scalable Infrastructure",
      description: "Process millions of verifications with 99.9% uptime SLA.",
    },
    {
      icon: TrendingUp,
      title: "Continuous Improvement",
      description: "AI models constantly learning and improving accuracy rates.",
    },
  ];

  const integrations = [
    { name: "Salesforce", category: "CRM" },
    { name: "HubSpot", category: "CRM" },
    { name: "Stripe", category: "Payments" },
    { name: "Auth0", category: "Identity" },
    { name: "Okta", category: "Identity" },
    { name: "Twilio", category: "Communication" },
    { name: "Segment", category: "Analytics" },
    { name: "Zapier", category: "Automation" },
  ];

  const useCases = [
    {
      title: "Financial Services",
      description: "Comply with KYC/AML regulations while onboarding customers seamlessly.",
      metrics: "87% faster onboarding",
      link: "/industries/banking-finance",
    },
    {
      title: "Cryptocurrency",
      description: "Verify users globally while meeting regulatory requirements across jurisdictions.",
      metrics: "95% fraud reduction",
      link: "/industries/crypto-web3",
    },
    {
      title: "Gaming & iGaming",
      description: "Age verification and fraud prevention for responsible gaming compliance.",
      metrics: "99.2% accuracy",
      link: "/industries/gaming",
    },
    {
      title: "Healthcare",
      description: "Secure patient verification and HIPAA-compliant identity management.",
      metrics: "100% HIPAA compliant",
      link: "/industries/healthcare",
    },
  ];

  const comparisonData = [
    { feature: "Verification Speed", verigate: "< 5 seconds", competitors: "10-30 seconds" },
    { feature: "Global Coverage", verigate: "190+ countries", competitors: "100-150 countries" },
    { feature: "Document Types", verigate: "5,000+", competitors: "2,000-3,000" },
    { feature: "Accuracy Rate", verigate: "99.8%", competitors: "95-98%" },
    { feature: "API Response Time", verigate: "< 500ms", competitors: "1-3 seconds" },
    { feature: "Uptime SLA", verigate: "99.9%", competitors: "99.5%" },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section with Video */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-background to-primary/10">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <Badge className="mb-4" variant="secondary">
              Enterprise Identity Verification Platform
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Complete Identity Verification
              <span className="text-primary block mt-2">Built for Global Scale</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mb-8">
              Verify identities in seconds with our AI-powered platform. Trusted by 500+ enterprises 
              to process over 50 million verifications annually across 190+ countries.
            </p>
            <div className="flex gap-4 flex-wrap">
              <Button size="lg" asChild>
                <Link to="/contact">Get Started</Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/contact">Watch Demo</Link>
              </Button>
            </div>
          </div>

          {/* Product Demo Video Placeholder */}
          <div className="relative aspect-video bg-gradient-to-br from-gray-900 to-gray-800 rounded-lg overflow-hidden shadow-2xl">
            <div className="absolute inset-0 flex items-center justify-center">
              <div className="text-center text-white">
                <div className="w-20 h-20 bg-primary rounded-full flex items-center justify-center mx-auto mb-4">
                  <Eye className="w-10 h-10" />
                </div>
                <p className="text-lg font-semibold">Product Demo Video</p>
                <p className="text-sm text-gray-300 mt-2">Watch how VeriGate verifies identities in real-time</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Key Capabilities */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Comprehensive Verification Suite</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Everything you need to verify identities, prevent fraud, and stay compliant
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {capabilities.map((capability, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-start justify-between">
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-primary/10 rounded-lg">
                        <capability.icon className="w-6 h-6 text-primary" />
                      </div>
                      <div>
                        <CardTitle>{capability.title}</CardTitle>
                      </div>
                    </div>
                  </div>
                  <CardDescription className="mt-3">{capability.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {capability.features.map((feature, idx) => (
                      <li key={idx} className="flex items-center gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0" />
                        <span>{feature}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="mt-8 text-center">
            <Button variant="outline" asChild>
              <Link to="/solutions/kyc">
                Explore All Solutions <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Product Features */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Why Choose VeriGate</h2>
            <p className="text-lg text-muted-foreground">
              Built for developers, trusted by enterprises
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {features.map((feature, index) => (
              <div key={index} className="text-center">
                <div className="inline-flex p-4 bg-primary/10 rounded-lg mb-4">
                  <feature.icon className="w-8 h-8 text-primary" />
                </div>
                <h3 className="text-lg font-semibold mb-2">{feature.title}</h3>
                <p className="text-muted-foreground text-sm">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Integration Ecosystem */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Seamless Integrations</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Connect VeriGate with your existing tools and workflows
            </p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 mb-8">
            {integrations.map((integration, index) => (
              <Card key={index} className="text-center hover:shadow-md transition-shadow">
                <CardContent className="pt-6">
                  <div className="font-semibold mb-1">{integration.name}</div>
                  <Badge variant="secondary" className="text-xs">{integration.category}</Badge>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="text-center">
            <Button variant="outline" asChild>
              <Link to="/integrations">
                View All Integrations <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Use Cases */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Built for Your Industry</h2>
            <p className="text-lg text-muted-foreground">
              Tailored solutions for every vertical
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {useCases.map((useCase, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle>{useCase.title}</CardTitle>
                  <CardDescription>{useCase.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="flex items-center justify-between">
                    <Badge variant="secondary">{useCase.metrics}</Badge>
                    <Button variant="ghost" size="sm" asChild>
                      <Link to={useCase.link}>
                        Learn More <ArrowRight className="w-4 h-4 ml-2" />
                      </Link>
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Comparison Table */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">How We Compare</h2>
            <p className="text-lg text-muted-foreground">
              VeriGate vs. Industry Average
            </p>
          </div>

          <Card>
            <CardContent className="p-0">
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-gray-50 dark:bg-gray-900">
                    <tr>
                      <th className="px-6 py-4 text-left font-semibold">Feature</th>
                      <th className="px-6 py-4 text-center font-semibold text-primary">VeriGate</th>
                      <th className="px-6 py-4 text-center font-semibold text-muted-foreground">Industry Average</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y">
                    {comparisonData.map((row, index) => (
                      <tr key={index} className="hover:bg-gray-50 dark:hover:bg-gray-900/50">
                        <td className="px-6 py-4 font-medium">{row.feature}</td>
                        <td className="px-6 py-4 text-center">
                          <Badge variant="default">{row.verigate}</Badge>
                        </td>
                        <td className="px-6 py-4 text-center text-muted-foreground">
                          {row.competitors}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-white">
        <div className="container mx-auto max-w-6xl">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            Ready to Get Started?
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join 500+ companies already using VeriGate to verify identities at scale
          </p>
          <div className="flex gap-4 flex-wrap">
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

export default Product;
