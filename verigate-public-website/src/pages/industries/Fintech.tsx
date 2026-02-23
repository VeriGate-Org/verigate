import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  Smartphone,
  Zap,
  Clock,
  TrendingUp,
  Users,
  Shield,
  Globe,
  Rocket,
  CreditCard,
  ArrowRight,
  Wallet
} from "lucide-react";
import { Link } from "react-router-dom";

const Fintech = () => {
  const challenges = [
    {
      icon: Clock,
      title: "Speed to Market",
      description: "Launch new products quickly while maintaining robust identity verification and compliance",
      stat: "80% of fintech users expect instant account creation",
    },
    {
      icon: Users,
      title: "User Experience",
      description: "Balance security with seamless, mobile-first onboarding that doesn't frustrate users",
      stat: "25% drop-off rate for every additional verification step",
    },
    {
      icon: Shield,
      title: "Fraud Prevention",
      description: "Combat sophisticated fraud while keeping false positives low to avoid rejecting good customers",
      stat: "$4.2B lost to fintech fraud in 2023",
    },
    {
      icon: Globe,
      title: "Global Expansion",
      description: "Scale internationally with verification that works across 190+ countries and jurisdictions",
      stat: "76% of fintechs plan international expansion",
    },
  ];

  const solutions = [
    {
      title: "Instant Mobile Onboarding",
      description: "Enable account creation in under 60 seconds with smartphone-based verification",
      features: [
        "Selfie + ID capture in one flow",
        "Real-time liveness detection",
        "Automated document verification",
        "Instant results",
      ],
      metric: "< 60 seconds",
      icon: Smartphone,
    },
    {
      title: "API-First Integration",
      description: "Developer-friendly REST APIs and SDKs for seamless integration",
      features: [
        "RESTful API design",
        "SDKs for iOS, Android, Web",
        "Webhooks for real-time updates",
        "Comprehensive documentation",
      ],
      metric: "< 1 hour integration",
      icon: Rocket,
    },
    {
      title: "Scalable Infrastructure",
      description: "Handle spikes from zero to millions without performance degradation",
      features: [
        "Auto-scaling architecture",
        "99.9% uptime SLA",
        "Global CDN",
        "Load balancing",
      ],
      metric: "99.9% uptime",
      icon: Zap,
    },
  ];

  const fintechSegments = [
    {
      segment: "Digital Banking & Neobanks",
      description: "Instant account opening with full KYC compliance for digital-first banks",
      keyFeatures: ["Mobile onboarding", "KYC/AML screening", "Ongoing monitoring", "Multi-currency support"],
      customers: "Chime, Revolut, N26 type platforms",
    },
    {
      segment: "Payment Processors",
      description: "Verify merchants and prevent payment fraud in real-time",
      keyFeatures: ["Merchant verification", "Transaction monitoring", "Risk scoring", "Real-time decisioning"],
      customers: "Stripe, Square, PayPal alternatives",
    },
    {
      segment: "Lending Platforms",
      description: "Automate identity and income verification for faster loan approvals",
      keyFeatures: ["Identity verification", "Income document validation", "Credit risk assessment", "Automated underwriting"],
      customers: "LendingClub, Avant, SoFi type platforms",
    },
    {
      segment: "Investment Apps",
      description: "Comply with securities regulations while onboarding investors seamlessly",
      keyFeatures: ["Accredited investor verification", "KYC compliance", "Tax form automation", "Risk profiling"],
      customers: "Robinhood, Acorns, Stash competitors",
    },
    {
      segment: "Buy Now Pay Later (BNPL)",
      description: "Instant credit decisions with identity verification at checkout",
      keyFeatures: ["Real-time verification", "Age verification", "Fraud prevention", "One-click checkout"],
      customers: "Affirm, Klarna, Afterpay alternatives",
    },
    {
      segment: "Cryptocurrency Exchanges",
      description: "Meet regulatory requirements for crypto trading platforms",
      keyFeatures: ["Enhanced due diligence", "Source of funds verification", "Travel Rule compliance", "Ongoing monitoring"],
      customers: "Coinbase, Kraken, Binance type platforms",
    },
  ];

  const integrationFeatures = [
    {
      title: "Mobile SDKs",
      description: "Native iOS and Android SDKs for seamless in-app verification",
      languages: ["Swift", "Kotlin", "React Native", "Flutter"],
      icon: Smartphone,
    },
    {
      title: "Web Components",
      description: "Drop-in JavaScript widgets for web applications",
      languages: ["JavaScript", "TypeScript", "React", "Vue", "Angular"],
      icon: Globe,
    },
    {
      title: "Backend APIs",
      description: "RESTful APIs with comprehensive server-side support",
      languages: ["Node.js", "Python", "Ruby", "PHP", "Java", "Go"],
      icon: Rocket,
    },
  ];

  const metrics = [
    { label: "Onboarding Time", value: "< 60 sec", icon: Clock },
    { label: "Conversion Rate", value: "94%", icon: TrendingUp },
    { label: "API Response", value: "< 500ms", icon: Zap },
    { label: "False Positive", value: "< 2%", icon: Shield },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-purple-50 via-background to-purple-100 dark:from-purple-950 dark:to-background">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center justify-center gap-2 mb-4">
              <Rocket className="w-8 h-8 text-primary" />
              <Badge variant="secondary" className="text-sm">
                Fintech Solutions
              </Badge>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Identity Verification for
              <span className="text-primary block mt-2">Fast-Moving Fintech Companies</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
              Ship features faster with instant identity verification. Onboard users in under 60 seconds 
              with developer-friendly APIs built for fintech innovation.
            </p>
            <div className="flex gap-4 justify-center flex-wrap">
              <Button size="lg" asChild>
                <Link to="/contact">Start Building</Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/developers">View API Docs</Link>
              </Button>
            </div>
          </div>

          {/* Key Metrics */}
          <div className="grid md:grid-cols-4 gap-6">
            {metrics.map((metric, index) => (
              <Card key={index} className="text-center">
                <CardHeader className="pb-2">
                  <metric.icon className="w-6 h-6 text-primary mx-auto" />
                </CardHeader>
                <CardContent>
                  <div className="text-3xl font-bold text-primary mb-1">{metric.value}</div>
                  <div className="text-sm text-muted-foreground">{metric.label}</div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Fintech Challenges */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Fintech-Specific Challenges</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Move fast without compromising security or compliance
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {challenges.map((challenge, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-start gap-3">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <challenge.icon className="w-6 h-6 text-primary" />
                    </div>
                    <div className="flex-1">
                      <CardTitle className="text-lg mb-2">{challenge.title}</CardTitle>
                      <CardDescription>{challenge.description}</CardDescription>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <Badge variant="secondary">{challenge.stat}</Badge>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Solutions */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Built for Fintech Speed</h2>
            <p className="text-lg text-muted-foreground">
              Verification infrastructure that scales with your growth
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {solutions.map((solution, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-center gap-2 mb-3">
                    <solution.icon className="w-6 h-6 text-primary" />
                    <CardTitle className="text-lg">{solution.title}</CardTitle>
                  </div>
                  <CardDescription>{solution.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2 mb-4">
                    {solution.features.map((feature, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{feature}</span>
                      </li>
                    ))}
                  </ul>
                  <Badge variant="default" className="w-full justify-center">
                    <Zap className="w-3 h-3 mr-1" />
                    {solution.metric}
                  </Badge>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Fintech Segments */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Solutions by Fintech Vertical</h2>
            <p className="text-lg text-muted-foreground">
              Tailored verification for every fintech segment
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {fintechSegments.map((segment, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{segment.segment}</CardTitle>
                  <CardDescription>{segment.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    <div>
                      <div className="text-xs font-medium text-muted-foreground mb-2">Key Features:</div>
                      <div className="flex flex-wrap gap-1">
                        {segment.keyFeatures.map((feature, idx) => (
                          <Badge key={idx} variant="secondary" className="text-xs">
                            {feature}
                          </Badge>
                        ))}
                      </div>
                    </div>
                    <div className="text-xs text-muted-foreground pt-2 border-t">
                      Similar to: {segment.customers}
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Developer Experience */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Developer-First Integration</h2>
            <p className="text-lg text-muted-foreground">
              Built by developers, for developers
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6 mb-12">
            {integrationFeatures.map((feature, index) => (
              <Card key={index}>
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <feature.icon className="w-5 h-5 text-primary" />
                    <CardTitle className="text-lg">{feature.title}</CardTitle>
                  </div>
                  <CardDescription>{feature.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="flex flex-wrap gap-1">
                    {feature.languages.map((lang, idx) => (
                      <Badge key={idx} variant="outline" className="text-xs">
                        {lang}
                      </Badge>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* Code Example */}
          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>Quick Start Example</CardTitle>
              <CardDescription>Integrate verification in minutes, not weeks</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// 1. Install SDK
npm install @verigate/sdk

// 2. Initialize
import VeriGate from '@verigate/sdk';
const verigate = new VeriGate(process.env.VERIGATE_API_KEY);

// 3. Verify user
const result = await verigate.verify({
  documentImage: req.files.id_front,
  selfieImage: req.files.selfie,
  userData: {
    firstName: 'John',
    lastName: 'Doe'
  }
});

// 4. Get instant results
if (result.status === 'approved') {
  // User verified - create account
  await createUserAccount(result.data);
} else {
  // Handle rejection
  console.log(result.reason);
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

      {/* Pricing for Startups */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Startup-Friendly Pricing</h2>
            <p className="text-lg text-muted-foreground">
              Pay as you grow with volume discounts
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            <Card>
              <CardHeader>
                <Badge className="w-fit mb-2">For Startups</Badge>
                <CardTitle>Early Stage</CardTitle>
                <div className="mt-4">
                  <span className="text-4xl font-bold">$0.50</span>
                  <span className="text-muted-foreground">/verification</span>
                </div>
              </CardHeader>
              <CardContent>
                <ul className="space-y-2 mb-6">
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Up to 10K verifications/month</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>All verification types</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Email support</span>
                  </li>
                </ul>
                <Button className="w-full" variant="outline" asChild>
                  <Link to="/contact">Get Started</Link>
                </Button>
              </CardContent>
            </Card>

            <Card className="border-primary shadow-lg scale-105">
              <CardHeader>
                <Badge className="w-fit mb-2">Most Popular</Badge>
                <CardTitle>Growth</CardTitle>
                <div className="mt-4">
                  <span className="text-4xl font-bold">$0.35</span>
                  <span className="text-muted-foreground">/verification</span>
                </div>
              </CardHeader>
              <CardContent>
                <ul className="space-y-2 mb-6">
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>10K - 100K verifications/month</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Priority support</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Custom webhooks</span>
                  </li>
                </ul>
                <Button className="w-full" asChild>
                  <Link to="/contact">Get Started</Link>
                </Button>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <Badge className="w-fit mb-2" variant="secondary">For Scale</Badge>
                <CardTitle>Enterprise</CardTitle>
                <div className="mt-4">
                  <span className="text-4xl font-bold">Custom</span>
                </div>
              </CardHeader>
              <CardContent>
                <ul className="space-y-2 mb-6">
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>100K+ verifications/month</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Dedicated infrastructure</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Account manager</span>
                  </li>
                </ul>
                <Button className="w-full" variant="outline" asChild>
                  <Link to="/contact">Contact Sales</Link>
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-white">
        <div className="container mx-auto max-w-6xl text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            Ship Faster with VeriGate
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join hundreds of fintech companies building the future of finance with our verification platform
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" variant="secondary" asChild>
              <Link to="/contact">Start Building</Link>
            </Button>
            <Button size="lg" variant="outline" className="bg-white/10 hover:bg-white/20 border-white text-white" asChild>
              <Link to="/developers">View API Docs</Link>
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Fintech;
