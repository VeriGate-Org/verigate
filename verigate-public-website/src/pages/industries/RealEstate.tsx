import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  Home,
  Shield,
  FileCheck,
  Users,
  DollarSign,
  Clock,
  AlertTriangle,
  Key,
  Building2,
  ArrowRight,
  Lock,
  Handshake
} from "lucide-react";
import { Link } from "react-router-dom";

const RealEstate = () => {
  const challenges = [
    {
      icon: AlertTriangle,
      title: "Rental Fraud",
      description: "Prevent fake listings, wire fraud, and fraudulent rental applications",
      stat: "$1.3B lost to rental scams annually",
    },
    {
      icon: FileCheck,
      title: "Tenant Screening",
      description: "Verify tenant identity, employment, and rental history efficiently",
      stat: "15% of rental applications contain false information",
    },
    {
      icon: Lock,
      title: "Transaction Security",
      description: "Secure high-value property transactions and prevent wire fraud",
      stat: "$350M lost to real estate wire fraud",
    },
    {
      icon: Users,
      title: "Remote Transactions",
      description: "Enable secure property transactions without in-person meetings",
      stat: "60% increase in remote property transactions",
    },
  ];

  const solutions = [
    {
      title: "Tenant Verification",
      description: "Comprehensive identity and background checks for renters",
      features: [
        "ID document verification",
        "Employment confirmation",
        "Income verification",
        "Credit check integration",
      ],
      metric: "< 5 min screening",
    },
    {
      title: "Buyer Authentication",
      description: "Verify buyers for property purchases and viewings",
      features: [
        "Identity verification",
        "Proof of funds",
        "Pre-qualification checks",
        "Agent protection",
      ],
      metric: "Fraud prevention",
    },
    {
      title: "Digital Signing",
      description: "Secure identity verification for e-signatures",
      features: [
        "Signer verification",
        "Document authentication",
        "Audit trails",
        "Legal compliance",
      ],
      metric: "Legally binding",
    },
  ];

  const realEstateSegments = [
    {
      segment: "Property Management",
      description: "Tenant screening and lease management",
      requirements: [
        "Tenant identity verification",
        "Employment & income checks",
        "Rental history validation",
        "Pet verification",
        "Co-signer authentication",
      ],
    },
    {
      segment: "Vacation Rental Management",
      description: "Short-term rental guest verification",
      requirements: [
        "Guest identity confirmation",
        "Booking fraud prevention",
        "Damage liability verification",
        "Local registration compliance",
        "Party prevention screening",
      ],
    },
    {
      segment: "Real Estate Agencies",
      description: "Buyer and seller verification",
      requirements: [
        "Buyer identity verification",
        "Proof of funds validation",
        "Seller ownership confirmation",
        "Agent credentialing",
        "Viewing access authorization",
      ],
    },
    {
      segment: "Mortgage & Lending",
      description: "Borrower identity and income verification",
      requirements: [
        "Borrower identity",
        "Income document validation",
        "Employment verification",
        "Asset verification",
        "Fraud prevention",
      ],
    },
    {
      segment: "Real Estate Crowdfunding",
      description: "Investor verification and accreditation",
      requirements: [
        "Accredited investor verification",
        "Identity confirmation",
        "Source of funds",
        "KYC/AML compliance",
        "Investment eligibility",
      ],
    },
    {
      segment: "Property Tech Platforms",
      description: "User verification for marketplaces",
      requirements: [
        "Landlord verification",
        "Tenant screening",
        "Agent credentialing",
        "Payment verification",
        "Review authenticity",
      ],
    },
  ];

  const useCases = [
    {
      title: "Rental Application Processing",
      description: "Fast, automated tenant screening for rental properties",
      benefits: ["Instant ID verification", "Employment checks", "Income validation", "Credit integration"],
    },
    {
      title: "Property Viewing Authorization",
      description: "Verify potential buyers before property access",
      benefits: ["Agent safety", "Serious buyer validation", "Appointment confirmation", "Fraud prevention"],
    },
    {
      title: "Digital Lease Signing",
      description: "Secure identity verification for remote lease execution",
      benefits: ["Signer authentication", "Legal compliance", "Audit trails", "Notarization support"],
    },
    {
      title: "Wire Transfer Verification",
      description: "Prevent wire fraud in property transactions",
      benefits: ["Buyer identity confirmation", "Bank account verification", "Transfer authentication", "Fraud alerts"],
    },
  ];

  const fraudPrevention = [
    {
      threat: "Rental Listing Scams",
      description: "Fake landlords collecting deposits for non-existent properties",
      solution: "Property ownership verification, landlord identity checks",
    },
    {
      threat: "Application Fraud",
      description: "False employment, income, or identity information",
      solution: "Document verification, income validation, employer confirmation",
    },
    {
      threat: "Wire Fraud",
      description: "Redirecting closing funds through fraudulent wire instructions",
      solution: "Multi-factor authentication, phone verification, secure channels",
    },
    {
      threat: "Occupancy Fraud",
      description: "Subletting or unauthorized occupants",
      solution: "Lease verification, occupant tracking, periodic re-verification",
    },
  ];

  const metrics = [
    { label: "Fraud Prevention", value: "92%", icon: Shield },
    { label: "Screening Time", value: "< 5 min", icon: Clock },
    { label: "Verification Accuracy", value: "99.5%", icon: CheckCircle2 },
    { label: "Cost Savings", value: "70%", icon: DollarSign },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-amber-50 via-background to-amber-100 dark:from-amber-950 dark:to-background">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center justify-center gap-2 mb-4">
              <Home className="w-8 h-8 text-primary" />
              <Badge variant="secondary" className="text-sm">
                Real Estate Solutions
              </Badge>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Identity Verification for
              <span className="text-primary block mt-2">Real Estate & Property Management</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
              Prevent rental fraud and streamline tenant screening. Verify identities for secure property 
              transactions, from rental applications to high-value sales with 99.5% accuracy.
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

      {/* Real Estate Challenges */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Real Estate Industry Challenges</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Critical verification needs in property transactions
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
            <h2 className="text-3xl font-bold mb-4">Real Estate Verification Solutions</h2>
            <p className="text-lg text-muted-foreground">
              Comprehensive verification for property professionals
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

      {/* Real Estate Segments */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Real Estate Verticals</h2>
            <p className="text-lg text-muted-foreground">
              Verification for every real estate segment
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {realEstateSegments.map((segment, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{segment.segment}</CardTitle>
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

      {/* Fraud Prevention */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Real Estate Fraud Prevention</h2>
            <p className="text-lg text-muted-foreground">
              Protect against common real estate scams
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {fraudPrevention.map((fraud, index) => (
              <Card key={index}>
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <Shield className="w-5 h-5 text-primary" />
                    <CardTitle className="text-lg">{fraud.threat}</CardTitle>
                  </div>
                  <CardDescription>{fraud.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="text-sm">
                    <span className="font-medium">Solution:</span>
                    <p className="text-muted-foreground mt-1">{fraud.solution}</p>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Use Cases */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Common Use Cases</h2>
            <p className="text-lg text-muted-foreground">
              Verification throughout the real estate process
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

      {/* Integration Example */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Simple Integration</h2>
            <p className="text-lg text-muted-foreground">
              Add tenant screening to your platform
            </p>
          </div>

          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>Rental Application Verification</CardTitle>
              <CardDescription>Automated tenant screening process</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// Initialize VeriGate for real estate
const verigate = new VeriGate(apiKey, {
  industry: 'realEstate'
});

// Process rental application
const application = await verigate.realEstate.screenTenant({
  // Identity verification
  idDocument: applicantIdImage,
  selfie: applicantSelfie,
  applicantData: {
    firstName: 'Jane',
    lastName: 'Smith',
    dateOfBirth: '1990-05-20',
    ssn: 'XXX-XX-1234' // Last 4 digits
  },
  
  // Employment verification
  employment: {
    employer: 'Tech Corp',
    jobTitle: 'Software Engineer',
    salary: 85000,
    employmentLetter: employmentDocImage
  },
  
  // Income verification
  incomeDocuments: [
    payStub1Image,
    payStub2Image,
    bankStatementImage
  ],
  
  // Rental requirements
  monthlyRent: 2000,
  securityDeposit: 2000,
  moveInDate: '2024-02-01'
});

// Check results
if (application.approved) {
  // Identity verified
  console.log('ID Verified:', application.identityVerified);
  
  // Income qualification (3x rent rule)
  console.log('Income Qualified:', application.incomeQualified);
  console.log('Income-to-Rent Ratio:', application.incomeRatio);
  
  // Employment confirmed
  console.log('Employment Verified:', application.employmentVerified);
  
  // Generate screening report
  const report = await verigate.realEstate.generateReport({
    applicationId: application.id,
    includeCredit: true, // Optional credit check
    includeBackground: true // Optional background check
  });
  
  // Send approval
  await sendApprovalEmail({
    applicant: applicantData,
    property: propertyInfo,
    leaseTerms: leaseData
  });
} else {
  // Handle rejection
  console.log('Rejection Reason:', application.rejectionReason);
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
            Streamline Your Property Operations
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join property managers and real estate professionals using VeriGate for fast, secure tenant screening
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

export default RealEstate;
