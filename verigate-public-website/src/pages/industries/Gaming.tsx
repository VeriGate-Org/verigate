import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  Shield,
  Users,
  Zap,
  AlertTriangle,
  Gamepad2,
  Clock,
  TrendingUp,
  Globe,
  Scale,
  Ban,
  ArrowRight,
  Baby
} from "lucide-react";
import { Link } from "react-router-dom";

const Gaming = () => {
  const challenges = [
    {
      icon: Baby,
      title: "Age Verification",
      description: "Comply with strict age restrictions for gambling and prevent underage access",
      stat: "21+ required in most jurisdictions",
    },
    {
      icon: AlertTriangle,
      title: "Problem Gambling Prevention",
      description: "Identify and protect vulnerable players through responsible gambling measures",
      stat: "2-3% of adults have gambling problems",
    },
    {
      icon: Ban,
      title: "Self-Exclusion Management",
      description: "Enforce multi-operator exclusions and prevent circumvention through new accounts",
      stat: "40% attempt to create new accounts",
    },
    {
      icon: Scale,
      title: "Regulatory Compliance",
      description: "Meet varying gambling regulations across different jurisdictions and licensing authorities",
      stat: "50+ gambling jurisdictions worldwide",
    },
  ];

  const solutions = [
    {
      title: "Age Verification",
      description: "Instant, accurate age checks to prevent underage gambling",
      features: [
        "Document-based verification",
        "Database cross-referencing",
        "Real-time validation",
        "99.5% accuracy",
      ],
      metric: "< 30 sec verification",
    },
    {
      title: "Identity Verification",
      description: "Comprehensive KYC to prevent fraud and money laundering",
      features: [
        "Document authentication",
        "Biometric face matching",
        "Liveness detection",
        "Multi-account detection",
      ],
      metric: "99.2% fraud prevention",
    },
    {
      title: "Responsible Gambling",
      description: "Tools to identify and protect at-risk players",
      features: [
        "Behavioral analysis",
        "Spending pattern monitoring",
        "Self-exclusion verification",
        "Cross-operator checking",
      ],
      metric: "Multi-operator protection",
    },
  ];

  const regulations = [
    {
      name: "UK Gambling Commission",
      region: "United Kingdom",
      requirements: ["Age verification at registration", "Source of funds checks for high spenders", "Self-exclusion scheme participation", "Responsible gambling measures"],
    },
    {
      name: "Malta Gaming Authority (MGA)",
      region: "Malta / EU",
      requirements: ["Player identification and verification", "AML/CFT compliance", "Responsible gaming tools", "Player funds protection"],
    },
    {
      name: "Curacao eGaming",
      region: "Curacao",
      requirements: ["Customer due diligence", "Transaction monitoring", "Responsible gaming policy", "Data protection"],
    },
    {
      name: "State Gaming Boards",
      region: "United States",
      requirements: ["Age verification (21+)", "Geolocation compliance", "Problem gambling programs", "State-specific KYC"],
    },
  ];

  const gamingSegments = [
    {
      segment: "Online Casinos",
      description: "Age verification and KYC for casino gaming platforms",
      requirements: [
        "Age verification (18+ or 21+)",
        "Identity document verification",
        "Source of funds for high rollers",
        "Self-exclusion checking",
        "Geolocation verification",
      ],
      riskLevel: "High",
    },
    {
      segment: "Sports Betting",
      description: "Identity verification for sportsbook and betting platforms",
      requirements: [
        "Real-time age verification",
        "Quick onboarding for live betting",
        "Withdrawal verification",
        "Bonus abuse prevention",
        "Multiple account detection",
      ],
      riskLevel: "High",
    },
    {
      segment: "Daily Fantasy Sports (DFS)",
      description: "Age and eligibility verification for fantasy sports",
      requirements: [
        "Age verification (varies by state)",
        "Geographic eligibility",
        "Professional athlete exclusion",
        "Prize payout verification",
        "Multi-account prevention",
      ],
      riskLevel: "Medium",
    },
    {
      segment: "Poker Platforms",
      description: "Identity verification for online poker rooms",
      requirements: [
        "Player identity verification",
        "Collusion detection support",
        "Tournament payout verification",
        "Bot prevention",
        "Withdrawal authentication",
      ],
      riskLevel: "High",
    },
    {
      segment: "Esports Betting",
      description: "Age verification for esports wagering platforms",
      requirements: [
        "Age verification for betting",
        "Player identity confirmation",
        "Fast verification for live events",
        "Fraud prevention",
        "Underage protection",
      ],
      riskLevel: "Medium",
    },
    {
      segment: "Social Gaming",
      description: "Age-appropriate access for social casino games",
      requirements: [
        "Age verification for real-money features",
        "Parental consent for minors",
        "In-app purchase verification",
        "Limited KYC requirements",
        "Regional compliance",
      ],
      riskLevel: "Low",
    },
  ];

  const responsibleGambling = [
    {
      feature: "Self-Exclusion Verification",
      description: "Check players against national and international self-exclusion databases",
      implementation: "Real-time screening at registration and periodic re-checks",
    },
    {
      feature: "Multi-Account Detection",
      description: "Identify users attempting to create multiple accounts to bypass limits",
      implementation: "Biometric matching, device fingerprinting, behavioral analysis",
    },
    {
      feature: "Affordability Checks",
      description: "Verify source of funds for high-spending players",
      implementation: "Income verification, bank statement validation, wealth screening",
    },
    {
      feature: "Cooling-Off Periods",
      description: "Re-verify identity when players return after self-imposed breaks",
      implementation: "Mandatory verification after timeout periods",
    },
  ];

  const metrics = [
    { label: "Age Verification", value: "99.5%", icon: CheckCircle2 },
    { label: "Verification Speed", value: "< 30 sec", icon: Clock },
    { label: "Fraud Prevention", value: "99.2%", icon: Shield },
    { label: "Global Coverage", value: "190+", icon: Globe },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-green-50 via-background to-green-100 dark:from-green-950 dark:to-background">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center justify-center gap-2 mb-4">
              <Gamepad2 className="w-8 h-8 text-primary" />
              <Badge variant="secondary" className="text-sm">
                Gaming & iGaming
              </Badge>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Identity Verification for
              <span className="text-primary block mt-2">Gaming & Gambling Platforms</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
              Meet strict gaming regulations with fast, accurate age verification. Protect vulnerable players 
              while enabling seamless onboarding for responsible gamers worldwide.
            </p>
            <div className="flex gap-4 justify-center flex-wrap">
              <Button size="lg" asChild>
                <Link to="/contact">Request Demo</Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/case-studies">View Case Studies</Link>
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

      {/* Gaming Industry Challenges */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Gaming Industry Challenges</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Unique verification challenges in the gaming and gambling sector
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {challenges.map((challenge, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-start gap-3">
                    <div className="p-2 bg-destructive/10 rounded-lg">
                      <challenge.icon className="w-6 h-6 text-destructive" />
                    </div>
                    <div className="flex-1">
                      <CardTitle className="text-lg mb-2">{challenge.title}</CardTitle>
                      <CardDescription>{challenge.description}</CardDescription>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <Badge variant="outline">{challenge.stat}</Badge>
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
            <h2 className="text-3xl font-bold mb-4">Gaming-Specific Solutions</h2>
            <p className="text-lg text-muted-foreground">
              Purpose-built verification for gaming platforms
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {solutions.map((solution, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{solution.title}</CardTitle>
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
                    {solution.metric}
                  </Badge>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Gaming Segments */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Gaming Platform Types</h2>
            <p className="text-lg text-muted-foreground">
              Tailored verification for different gaming verticals
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {gamingSegments.map((segment, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-center justify-between mb-2">
                    <CardTitle className="text-lg">{segment.segment}</CardTitle>
                    <Badge variant={segment.riskLevel === 'High' ? 'destructive' : segment.riskLevel === 'Medium' ? 'default' : 'secondary'}>
                      {segment.riskLevel} Risk
                    </Badge>
                  </div>
                  <CardDescription>{segment.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {segment.requirements.map((req, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{req}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Responsible Gambling */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Responsible Gambling Features</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Protect vulnerable players with advanced verification tools
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {responsibleGambling.map((feature, index) => (
              <Card key={index}>
                <CardHeader>
                  <CardTitle className="text-lg flex items-center gap-2">
                    <Shield className="w-5 h-5 text-primary" />
                    {feature.feature}
                  </CardTitle>
                  <CardDescription>{feature.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="text-sm">
                    <span className="font-medium">Implementation:</span>
                    <p className="text-muted-foreground mt-1">{feature.implementation}</p>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Regulatory Compliance */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Gaming Regulatory Compliance</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Meet requirements from major gaming jurisdictions
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {regulations.map((regulation, index) => (
              <Card key={index}>
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <Scale className="w-5 h-5 text-primary" />
                    <CardTitle className="text-lg">{regulation.name}</CardTitle>
                  </div>
                  <Badge variant="outline" className="w-fit text-xs">{regulation.region}</Badge>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {regulation.requirements.map((req, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{req}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Integration Example */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Quick Integration</h2>
            <p className="text-lg text-muted-foreground">
              Add age and identity verification to your gaming platform
            </p>
          </div>

          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>Gaming Platform Verification Example</CardTitle>
              <CardDescription>Age verification with self-exclusion checking</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// Initialize VeriGate for gaming platforms
const verigate = new VeriGate(apiKey, {
  industry: 'gaming',
  jurisdiction: 'UK' // or 'US', 'EU', etc.
});

// Perform age and identity verification
const verification = await verigate.gaming.verify({
  document: idDocument,
  selfie: selfieImage,
  userData: {
    fullName: 'John Doe',
    dateOfBirth: '1990-01-01',
    address: userAddress
  },
  // Gaming-specific checks
  minimumAge: 21, // or 18 depending on jurisdiction
  checkSelfExclusion: true,
  geolocation: userLocation
});

// Check results
if (!verification.ageVerified) {
  return { error: 'Underage - Access Denied' };
}

if (verification.selfExcluded) {
  return { error: 'Self-Excluded Player' };
}

// Check for multiple accounts
const duplicateCheck = await verigate.gaming.checkDuplicates({
  biometricData: verification.biometricTemplate,
  deviceFingerprint: deviceId
});

if (duplicateCheck.found) {
  return { warning: 'Potential duplicate account detected' };
}

// All clear - create account
await createPlayerAccount({ 
  ...userData, 
  verified: true,
  verificationId: verification.id 
});`}</code>
              </pre>
            </CardContent>
          </Card>

          <div className="mt-8 text-center">
            <Button variant="outline" asChild>
              <Link to="/developers">
                View API Documentation <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-white">
        <div className="container mx-auto max-w-6xl text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            Build Responsible Gaming Platforms
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Meet regulatory requirements while enabling fast, seamless player onboarding
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" variant="secondary" asChild>
              <Link to="/contact">Request Demo</Link>
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

export default Gaming;
