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
  AlertTriangle,
  DollarSign,
  Users,
  Lock,
  Scale,
  Bitcoin,
  ArrowRight,
  TrendingUp,
  Eye
} from "lucide-react";
import { Link } from "react-router-dom";

const CryptoWeb3 = () => {
  const challenges = [
    {
      icon: Scale,
      title: "Regulatory Uncertainty",
      description: "Navigate evolving crypto regulations across different jurisdictions while maintaining global operations",
      stat: "150+ countries with varying crypto regulations",
    },
    {
      icon: AlertTriangle,
      title: "Fraud & Money Laundering",
      description: "Combat sophisticated crypto fraud, mixer services, and illicit transaction patterns",
      stat: "$20B+ in illicit crypto transactions annually",
    },
    {
      icon: Globe,
      title: "Cross-Border Complexity",
      description: "Verify users from 190+ countries while meeting local KYC/AML requirements",
      stat: "76% of crypto users trade cross-border",
    },
    {
      icon: Users,
      title: "User Experience",
      description: "Balance decentralization ethos with necessary compliance without losing users",
      stat: "40% drop-off during KYC verification",
    },
  ];

  const solutions = [
    {
      title: "Crypto-Native KYC",
      description: "Fast verification designed for crypto platforms with high-risk assessment",
      features: [
        "Enhanced due diligence (EDD)",
        "Source of funds verification",
        "Wallet address screening",
        "Risk-based approach",
      ],
      metric: "< 5 min verification",
    },
    {
      title: "Travel Rule Compliance",
      description: "FATF Travel Rule compliance for cross-border crypto transactions",
      features: [
        "Beneficiary verification",
        "Originator screening",
        "Transaction monitoring",
        "Automated reporting",
      ],
      metric: "100% FATF compliant",
    },
    {
      title: "On-Chain Analysis Integration",
      description: "Combine identity verification with blockchain intelligence",
      features: [
        "Wallet risk scoring",
        "Transaction pattern analysis",
        "Mixer/tumbler detection",
        "Sanctions screening",
      ],
      metric: "Real-time risk assessment",
    },
  ];

  const regulations = [
    {
      name: "FATF Travel Rule",
      region: "Global",
      description: "Requires VASPs to share originator and beneficiary information for transactions over $1,000/€1,000",
      requirements: ["Customer data collection", "Counterparty verification", "Transaction monitoring"],
    },
    {
      name: "MiCA (Markets in Crypto-Assets)",
      region: "European Union",
      description: "Comprehensive EU framework for crypto asset regulation",
      requirements: ["Authorization requirements", "Customer due diligence", "Transaction reporting"],
    },
    {
      name: "BSA/AML (FinCEN Guidance)",
      region: "United States",
      description: "Anti-money laundering regulations for cryptocurrency exchanges and wallet providers",
      requirements: ["Customer identification", "Suspicious activity reporting", "Record keeping"],
    },
    {
      name: "5AMLD & 6AMLD",
      region: "European Union",
      description: "Extended AML directives covering virtual currencies and wallet providers",
      requirements: ["Enhanced due diligence", "Beneficial ownership transparency", "Whistleblower protection"],
    },
  ];

  const web3UseCases = [
    {
      title: "Centralized Exchanges (CEX)",
      description: "Full KYC/AML compliance for crypto trading platforms",
      features: [
        "Account opening verification",
        "Withdrawal limits based on KYC level",
        "Enhanced due diligence for high-value traders",
        "Ongoing transaction monitoring",
      ],
      examples: "Coinbase, Kraken, Binance-type platforms",
    },
    {
      title: "Decentralized Exchanges (DEX)",
      description: "Optional KYC for compliant DeFi protocols",
      features: [
        "Tiered access based on verification",
        "Compliance-as-a-service",
        "Privacy-preserving verification",
        "Smart contract integration",
      ],
      examples: "Uniswap, dYdX with compliance layers",
    },
    {
      title: "NFT Marketplaces",
      description: "Age and identity verification for NFT trading",
      features: [
        "Creator verification",
        "Buyer/seller KYC",
        "High-value transaction verification",
        "Anti-fraud measures",
      ],
      examples: "OpenSea, Rarible, Magic Eden",
    },
    {
      title: "DeFi Lending Protocols",
      description: "Identity verification for undercollateralized lending",
      features: [
        "Credit identity verification",
        "On-chain reputation linking",
        "Sybil attack prevention",
        "Default risk assessment",
      ],
      examples: "Aave Arc, Maple Finance, TrueFi",
    },
    {
      title: "Crypto Payment Processors",
      description: "Merchant and customer verification for crypto payments",
      features: [
        "Merchant onboarding",
        "Transaction velocity monitoring",
        "Chargeback prevention",
        "Regulatory compliance",
      ],
      examples: "BitPay, Coinbase Commerce alternatives",
    },
    {
      title: "Web3 Wallets",
      description: "Identity linking for non-custodial wallet services",
      features: [
        "Optional KYC for fiat on-ramps",
        "Social recovery verification",
        "High-value transaction authentication",
        "Cross-platform identity",
      ],
      examples: "MetaMask, Trust Wallet, Phantom",
    },
  ];

  const riskTiers = [
    {
      tier: "Standard Risk",
      description: "Low transaction volumes, known jurisdictions",
      limits: "Up to $10,000 daily",
      requirements: ["Basic KYC", "Document verification", "Liveness check"],
      color: "secondary",
    },
    {
      tier: "Enhanced Risk",
      description: "Higher volumes or medium-risk jurisdictions",
      limits: "$10,000 - $100,000 daily",
      requirements: ["Enhanced due diligence", "Source of funds", "Ongoing monitoring"],
      color: "default",
    },
    {
      tier: "High Risk",
      description: "Very high volumes or high-risk jurisdictions",
      limits: "$100,000+ daily",
      requirements: ["Full EDD", "Wealth verification", "Real-time monitoring", "Compliance review"],
      color: "destructive",
    },
  ];

  const metrics = [
    { label: "Verification Speed", value: "< 5 min", icon: Zap },
    { label: "Global Coverage", value: "190+", icon: Globe },
    { label: "Fraud Detection", value: "99.2%", icon: Shield },
    { label: "Travel Rule", value: "100%", icon: CheckCircle2 },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-orange-50 via-background to-orange-100 dark:from-orange-950 dark:to-background">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center justify-center gap-2 mb-4">
              <Bitcoin className="w-8 h-8 text-primary" />
              <Badge variant="secondary" className="text-sm">
                Cryptocurrency & Web3
              </Badge>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Identity Verification for
              <span className="text-primary block mt-2">Crypto Exchanges & Web3 Platforms</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
              Meet global crypto regulations with fast, privacy-respecting identity verification. 
              FATF Travel Rule compliant, supporting 190+ countries with enhanced due diligence for high-risk users.
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

      {/* Crypto Industry Challenges */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Crypto Industry Challenges</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Unique compliance challenges facing cryptocurrency platforms
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
            <h2 className="text-3xl font-bold mb-4">Crypto-Specific Solutions</h2>
            <p className="text-lg text-muted-foreground">
              Purpose-built verification for cryptocurrency platforms
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

      {/* Regulatory Compliance */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Crypto Regulatory Compliance</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Stay compliant with evolving global cryptocurrency regulations
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
                  <p className="text-sm text-muted-foreground mb-3">{regulation.description}</p>
                  <div className="space-y-1">
                    <div className="text-xs font-medium">Requirements:</div>
                    {regulation.requirements.map((req, idx) => (
                      <div key={idx} className="text-xs text-muted-foreground flex items-start gap-1">
                        <span className="text-primary">•</span>
                        <span>{req}</span>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Web3 Use Cases */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Web3 Platform Types</h2>
            <p className="text-lg text-muted-foreground">
              Verification solutions for every crypto platform
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {web3UseCases.map((useCase, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{useCase.title}</CardTitle>
                  <CardDescription>{useCase.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2 mb-3">
                    {useCase.features.map((feature, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{feature}</span>
                      </li>
                    ))}
                  </ul>
                  <div className="text-xs text-muted-foreground pt-3 border-t">
                    Examples: {useCase.examples}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Risk-Based Tiers */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Risk-Based Verification Tiers</h2>
            <p className="text-lg text-muted-foreground">
              Tiered KYC based on transaction volume and risk profile
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {riskTiers.map((tier, index) => (
              <Card key={index}>
                <CardHeader>
                  <Badge variant={tier.color as any} className="w-fit mb-2">{tier.tier}</Badge>
                  <CardDescription>{tier.description}</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <div className="text-sm font-medium mb-1">Transaction Limits:</div>
                    <div className="text-lg font-bold text-primary">{tier.limits}</div>
                  </div>
                  <div>
                    <div className="text-sm font-medium mb-2">Requirements:</div>
                    <ul className="space-y-1">
                      {tier.requirements.map((req, idx) => (
                        <li key={idx} className="text-xs text-muted-foreground flex items-start gap-1">
                          <span className="text-primary">•</span>
                          <span>{req}</span>
                        </li>
                      ))}
                    </ul>
                  </div>
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
            <h2 className="text-3xl font-bold mb-4">Easy Integration</h2>
            <p className="text-lg text-muted-foreground">
              Add KYC to your crypto platform in minutes
            </p>
          </div>

          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>Crypto Exchange KYC Example</CardTitle>
              <CardDescription>Verify users with enhanced due diligence</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// Initialize VeriGate for crypto platforms
const verigate = new VeriGate(apiKey, {
  industry: 'cryptocurrency',
  riskProfile: 'enhanced'
});

// Perform enhanced KYC verification
const verification = await verigate.kyc.verify({
  document: idDocument,
  selfie: selfieImage,
  userData: {
    fullName: 'John Doe',
    dateOfBirth: '1990-01-01',
    nationality: 'US'
  },
  // Crypto-specific fields
  walletAddress: '0x742d35Cc6634C0532925a3b8...',
  sourceOfFunds: 'employment',
  estimatedVolume: 'high', // low, medium, high
  purposeOfAccount: 'trading'
});

// Enhanced screening for crypto
const amlCheck = await verigate.aml.screen({
  ...userData,
  walletScreening: true,
  enhancedDueDiligence: true
});

// Check results
if (verification.approved && amlCheck.riskLevel !== 'high') {
  // Set appropriate transaction limits
  const limits = getTierLimits(verification.riskTier);
  await createUserAccount({ ...userData, limits });
}`}</code>
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
            Build Compliant Crypto Platforms
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Meet global regulations while providing seamless user experience for your crypto platform
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

export default CryptoWeb3;
