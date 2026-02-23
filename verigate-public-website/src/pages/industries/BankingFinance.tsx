import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  Shield,
  TrendingUp,
  Clock,
  DollarSign,
  Users,
  FileCheck,
  AlertTriangle,
  Scale,
  Landmark,
  ArrowRight,
  BarChart3
} from "lucide-react";
import { Link } from "react-router-dom";

const BankingFinance = () => {
  const challenges = [
    {
      icon: AlertTriangle,
      title: "Regulatory Compliance",
      description: "Navigate complex KYC/AML regulations across multiple jurisdictions while maintaining customer experience",
      impact: "Non-compliance fines average $4M+ per incident",
    },
    {
      icon: Clock,
      title: "Slow Onboarding",
      description: "Traditional identity verification takes days, causing customer drop-off and lost revenue",
      impact: "60% of customers abandon applications during manual verification",
    },
    {
      icon: Shield,
      title: "Identity Fraud",
      description: "Synthetic identities and document fraud cost financial institutions billions annually",
      impact: "$20B+ lost to identity fraud in banking annually",
    },
    {
      icon: Users,
      title: "Customer Experience",
      description: "Balance security requirements with seamless, digital-first customer expectations",
      impact: "40% of customers switch banks due to poor digital experience",
    },
  ];

  const solutions = [
    {
      title: "Instant KYC Verification",
      description: "Verify customer identities in under 5 seconds with 99.8% accuracy",
      features: [
        "Automated document verification",
        "Biometric face matching",
        "Real-time database checks",
        "Instant risk assessment",
      ],
      result: "87% faster account opening",
    },
    {
      title: "Comprehensive AML Screening",
      description: "Screen against global sanctions, PEP lists, and watchlists in real-time",
      features: [
        "100+ database sources",
        "Daily updates",
        "Ongoing monitoring",
        "Automated alerts",
      ],
      result: "95% reduction in false positives",
    },
    {
      title: "Transaction Monitoring",
      description: "Continuous verification for high-risk transactions and account changes",
      features: [
        "Step-up authentication",
        "Biometric verification",
        "Behavior analysis",
        "Fraud detection",
      ],
      result: "92% fraud prevention rate",
    },
  ];

  const regulations = [
    {
      name: "Bank Secrecy Act (BSA)",
      region: "United States",
      requirements: "Customer Identification Program (CIP), Suspicious Activity Reports (SAR), Currency Transaction Reports (CTR)",
    },
    {
      name: "KYC/AML Regulations",
      region: "Global",
      requirements: "Customer due diligence, Enhanced due diligence for high-risk customers, Ongoing monitoring",
    },
    {
      name: "Dodd-Frank Act",
      region: "United States",
      requirements: "Enhanced consumer protection, Risk management standards, Stress testing requirements",
    },
    {
      name: "Basel III",
      region: "International",
      requirements: "Capital adequacy, Stress testing, Market liquidity risk management",
    },
    {
      name: "Payment Services Directive 2 (PSD2)",
      region: "European Union",
      requirements: "Strong Customer Authentication (SCA), Open banking, Transaction monitoring",
    },
    {
      name: "Anti-Money Laundering Act (AMLA)",
      region: "United States",
      requirements: "Beneficial ownership reporting, Enhanced AML programs, Whistleblower protections",
    },
  ];

  const metrics = [
    {
      metric: "Onboarding Time",
      before: "3-5 days",
      after: "< 5 minutes",
      improvement: "87% faster",
      icon: Clock,
    },
    {
      metric: "Customer Drop-off",
      before: "60%",
      after: "8%",
      improvement: "86% reduction",
      icon: Users,
    },
    {
      metric: "Fraud Detection",
      before: "75%",
      after: "99.2%",
      improvement: "24% increase",
      icon: Shield,
    },
    {
      metric: "Compliance Costs",
      before: "$500k/year",
      after: "$150k/year",
      improvement: "70% reduction",
      icon: DollarSign,
    },
  ];

  const useCases = [
    {
      title: "Digital Account Opening",
      description: "Enable customers to open accounts entirely online with instant verification",
      benefits: ["Reduce onboarding from days to minutes", "24/7 availability", "Mobile-first experience", "No branch visits required"],
    },
    {
      title: "Loan Application Processing",
      description: "Verify applicant identity and income documents automatically",
      benefits: ["Instant document verification", "Automated income validation", "Faster loan approval", "Reduced manual review"],
    },
    {
      title: "Wire Transfer Authentication",
      description: "Secure high-value transactions with biometric verification",
      benefits: ["Step-up authentication", "Fraud prevention", "Regulatory compliance", "Real-time verification"],
    },
    {
      title: "Customer Due Diligence (CDD)",
      description: "Automated ongoing monitoring and periodic re-verification",
      benefits: ["Continuous screening", "Automatic alerts", "Risk-based approach", "Audit trail"],
    },
  ];

  const roi = {
    annualVerifications: 100000,
    costPerVerification: 15,
    newCostPerVerification: 0.50,
    savingsPerVerification: 14.50,
    annualSavings: 1450000,
    fraudReduction: 920000,
    totalAnnualBenefit: 2370000,
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-blue-50 via-background to-blue-100 dark:from-blue-950 dark:to-background">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center justify-center gap-2 mb-4">
              <Landmark className="w-8 h-8 text-primary" />
              <Badge variant="secondary" className="text-sm">
                Banking & Financial Services
              </Badge>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Identity Verification for
              <span className="text-primary block mt-2">Banking & Financial Institutions</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
              Meet stringent KYC/AML requirements while delivering fast, seamless digital onboarding. 
              Trusted by leading banks to process millions of verifications with 99.8% accuracy.
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

          {/* Key Metrics Dashboard */}
          <div className="grid md:grid-cols-4 gap-6">
            {metrics.map((item, index) => (
              <Card key={index}>
                <CardHeader className="pb-3">
                  <div className="flex items-center justify-between">
                    <item.icon className="w-5 h-5 text-primary" />
                    <Badge variant="secondary" className="text-xs">{item.improvement}</Badge>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="text-sm font-medium text-muted-foreground mb-1">{item.metric}</div>
                  <div className="flex items-baseline gap-2">
                    <span className="text-xs line-through text-muted-foreground">{item.before}</span>
                    <ArrowRight className="w-3 h-3" />
                    <span className="text-lg font-bold text-primary">{item.after}</span>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Industry Challenges */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Banking Industry Challenges</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Financial institutions face unique identity verification challenges
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
                  <Badge variant="outline" className="text-xs">
                    <AlertTriangle className="w-3 h-3 mr-1" />
                    {challenge.impact}
                  </Badge>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* VeriGate Solutions */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">VeriGate Solutions for Banking</h2>
            <p className="text-lg text-muted-foreground">
              Comprehensive identity verification tailored for financial services
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {solutions.map((solution, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle>{solution.title}</CardTitle>
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
                    <TrendingUp className="w-3 h-3 mr-1" />
                    {solution.result}
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
            <h2 className="text-3xl font-bold mb-4">Regulatory Compliance Coverage</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Meet all major banking regulations with automated compliance checks
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {regulations.map((regulation, index) => (
              <Card key={index}>
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <Scale className="w-5 h-5 text-primary" />
                    <CardTitle className="text-base">{regulation.name}</CardTitle>
                  </div>
                  <Badge variant="outline" className="w-fit text-xs">{regulation.region}</Badge>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground">{regulation.requirements}</p>
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

      {/* Use Cases */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Banking Use Cases</h2>
            <p className="text-lg text-muted-foreground">
              Identity verification across the customer lifecycle
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {useCases.map((useCase, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{useCase.title}</CardTitle>
                  <CardDescription>{useCase.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {useCase.benefits.map((benefit, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{benefit}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* ROI Calculator */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Return on Investment</h2>
            <p className="text-lg text-muted-foreground">
              Real cost savings for a mid-size bank processing 100,000 verifications annually
            </p>
          </div>

          <Card className="bg-gradient-to-br from-primary/5 to-primary/10">
            <CardContent className="pt-6">
              <div className="grid md:grid-cols-2 gap-8">
                <div>
                  <h3 className="font-semibold mb-4 flex items-center gap-2">
                    <BarChart3 className="w-5 h-5 text-primary" />
                    Cost Analysis
                  </h3>
                  <div className="space-y-3">
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-muted-foreground">Annual Verifications:</span>
                      <span className="font-bold">{roi.annualVerifications.toLocaleString()}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-muted-foreground">Current Cost/Verification:</span>
                      <span className="font-bold line-through">${roi.costPerVerification}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-muted-foreground">VeriGate Cost/Verification:</span>
                      <span className="font-bold text-primary">${roi.newCostPerVerification}</span>
                    </div>
                    <div className="flex justify-between items-center pt-3 border-t">
                      <span className="text-sm font-medium">Savings per Verification:</span>
                      <span className="font-bold text-green-600">${roi.savingsPerVerification}</span>
                    </div>
                  </div>
                </div>

                <div>
                  <h3 className="font-semibold mb-4 flex items-center gap-2">
                    <DollarSign className="w-5 h-5 text-primary" />
                    Annual Benefits
                  </h3>
                  <div className="space-y-3">
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-muted-foreground">Verification Savings:</span>
                      <span className="font-bold text-green-600">${roi.annualSavings.toLocaleString()}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-muted-foreground">Fraud Reduction:</span>
                      <span className="font-bold text-green-600">${roi.fraudReduction.toLocaleString()}</span>
                    </div>
                    <div className="flex justify-between items-center pt-3 border-t">
                      <span className="font-semibold">Total Annual Benefit:</span>
                      <span className="text-2xl font-bold text-green-600">${roi.totalAnnualBenefit.toLocaleString()}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div className="mt-6 p-4 bg-white dark:bg-gray-800 rounded-lg">
                <p className="text-sm text-center">
                  <strong>ROI Payback Period:</strong> Less than 2 months • 
                  <strong className="ml-2">3-Year ROI:</strong> <span className="text-green-600 font-bold">$7.1M+</span>
                </p>
              </div>
            </CardContent>
          </Card>

          <div className="mt-6 text-center">
            <Button asChild>
              <Link to="/contact">Calculate Your ROI</Link>
            </Button>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-white">
        <div className="container mx-auto max-w-6xl text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            Ready to Transform Your Banking Operations?
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join leading financial institutions using VeriGate for compliant, fast identity verification
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" variant="secondary" asChild>
              <Link to="/contact">Schedule Demo</Link>
            </Button>
            <Button size="lg" variant="outline" className="bg-white/10 hover:bg-white/20 border-white text-white" asChild>
              <Link to="/case-studies">View Case Studies</Link>
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default BankingFinance;
