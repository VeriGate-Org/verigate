import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  Shield,
  Heart,
  Lock,
  FileCheck,
  Users,
  Clock,
  AlertTriangle,
  Database,
  Stethoscope,
  ArrowRight,
  UserCheck,
  Activity
} from "lucide-react";
import { Link } from "react-router-dom";

const Healthcare = () => {
  const challenges = [
    {
      icon: Lock,
      title: "HIPAA Compliance",
      description: "Strict patient privacy requirements for Protected Health Information (PHI)",
      stat: "Fines up to $50,000 per violation",
    },
    {
      icon: AlertTriangle,
      title: "Medical Identity Theft",
      description: "Prevent fraudulent access to prescriptions, insurance benefits, and medical records",
      stat: "$13.4B lost to healthcare fraud annually",
    },
    {
      icon: Users,
      title: "Patient Verification",
      description: "Accurately identify patients across facilities to prevent medical errors",
      stat: "7-10% patient misidentification rate",
    },
    {
      icon: FileCheck,
      title: "Provider Credentialing",
      description: "Verify healthcare professional licenses and credentials efficiently",
      stat: "120+ days average credentialing time",
    },
  ];

  const solutions = [
    {
      title: "HIPAA-Compliant Verification",
      description: "Secure patient identity verification meeting all HIPAA requirements",
      features: [
        "End-to-end encryption",
        "Audit trails for compliance",
        "PHI data protection",
        "BAA agreements available",
      ],
      metric: "100% HIPAA compliant",
    },
    {
      title: "Patient Onboarding",
      description: "Streamline telehealth and online patient registration",
      features: [
        "Mobile ID capture",
        "Insurance card verification",
        "Consent management",
        "Medical history linking",
      ],
      metric: "< 2 min registration",
    },
    {
      title: "Provider Verification",
      description: "Automated credentialing and license verification",
      features: [
        "Medical license validation",
        "Board certification checks",
        "DEA number verification",
        "Sanctions screening",
      ],
      metric: "60% faster credentialing",
    },
  ];

  const healthcareSegments = [
    {
      segment: "Telehealth Platforms",
      description: "Secure patient verification for virtual healthcare services",
      requirements: [
        "Patient identity verification",
        "Insurance eligibility checks",
        "Prescription authorization",
        "HIPAA-compliant data handling",
        "Age verification for minors",
      ],
    },
    {
      segment: "Health Insurance",
      description: "Member verification and fraud prevention",
      requirements: [
        "Beneficiary identity verification",
        "Dependent verification",
        "Claims fraud prevention",
        "Provider network validation",
        "Enrollment verification",
      ],
    },
    {
      segment: "Pharmacy & Prescriptions",
      description: "Prescription verification and controlled substance monitoring",
      requirements: [
        "Patient ID verification",
        "Prescription validation",
        "Controlled substance checks",
        "Doctor-shopping prevention",
        "Insurance verification",
      ],
    },
    {
      segment: "Medical Devices & Apps",
      description: "User verification for connected health devices",
      requirements: [
        "Age verification",
        "Patient consent",
        "Medical professional credentials",
        "Data privacy compliance",
        "FDA compliance support",
      ],
    },
    {
      segment: "Hospital Systems",
      description: "Enterprise patient identification across facilities",
      requirements: [
        "Master patient index (MPI)",
        "Duplicate record prevention",
        "Emergency access protocols",
        "Multi-facility recognition",
        "Interoperability support",
      ],
    },
    {
      segment: "Healthcare Staffing",
      description: "Provider credentialing and background verification",
      requirements: [
        "License verification (state boards)",
        "Board certifications",
        "Malpractice history",
        "Background checks",
        "Ongoing monitoring",
      ],
    },
  ];

  const regulations = [
    {
      name: "HIPAA (Health Insurance Portability and Accountability Act)",
      region: "United States",
      requirements: "PHI protection, patient consent, audit trails, breach notification, business associate agreements",
    },
    {
      name: "HITECH Act",
      region: "United States",
      requirements: "Electronic health records security, breach notification rules, increased penalties",
    },
    {
      name: "FDA 21 CFR Part 11",
      region: "United States",
      requirements: "Electronic signatures, electronic records, audit trails for clinical trials",
    },
    {
      name: "GDPR for Healthcare",
      region: "European Union",
      requirements: "Data protection, patient consent, right to be forgotten, data portability",
    },
  ];

  const useCases = [
    {
      title: "Telehealth Patient Registration",
      description: "Instant patient verification for virtual doctor visits",
      benefits: ["Sub-2 minute registration", "Insurance card scanning", "Automated eligibility checks", "HIPAA-compliant storage"],
    },
    {
      title: "Prescription Fulfillment",
      description: "Verify patient identity for prescription pickups and deliveries",
      benefits: ["Photo ID verification", "Controlled substance compliance", "Prevent prescription fraud", "Age verification"],
    },
    {
      title: "Medical Professional Onboarding",
      description: "Fast credentialing for healthcare providers",
      benefits: ["License verification automation", "Board certification checks", "Multi-state license tracking", "Ongoing monitoring"],
    },
    {
      title: "Patient Portal Access",
      description: "Secure authentication for online medical records",
      benefits: ["Multi-factor authentication", "Biometric login", "Device recognition", "Session management"],
    },
  ];

  const metrics = [
    { label: "HIPAA Compliance", value: "100%", icon: Shield },
    { label: "Verification Speed", value: "< 2 min", icon: Clock },
    { label: "Fraud Prevention", value: "98%", icon: Lock },
    { label: "Data Encryption", value: "AES-256", icon: Database },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-blue-50 via-background to-blue-100 dark:from-blue-950 dark:to-background">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center justify-center gap-2 mb-4">
              <Heart className="w-8 h-8 text-primary" />
              <Badge variant="secondary" className="text-sm">
                Healthcare Solutions
              </Badge>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Identity Verification for
              <span className="text-primary block mt-2">Healthcare & Medical Platforms</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
              HIPAA-compliant patient and provider verification. Prevent medical identity theft while 
              enabling seamless telehealth experiences with 100% secure, encrypted verification.
            </p>
            <div className="flex gap-4 justify-center flex-wrap">
              <Button size="lg" asChild>
                <Link to="/contact">Request Demo</Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/security">HIPAA Compliance</Link>
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

      {/* Healthcare Challenges */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Healthcare Identity Challenges</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Critical verification needs in the healthcare industry
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
            <h2 className="text-3xl font-bold mb-4">Healthcare Verification Solutions</h2>
            <p className="text-lg text-muted-foreground">
              Secure, compliant identity verification for healthcare
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
                    <Shield className="w-3 h-3 mr-1" />
                    {solution.metric}
                  </Badge>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Healthcare Segments */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Healthcare Platform Types</h2>
            <p className="text-lg text-muted-foreground">
              Verification solutions for every healthcare vertical
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {healthcareSegments.map((segment, index) => (
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

      {/* Regulatory Compliance */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Healthcare Regulatory Compliance</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Meet all major healthcare data protection regulations
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {regulations.map((regulation, index) => (
              <Card key={index}>
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <Shield className="w-5 h-5 text-primary" />
                    <CardTitle className="text-lg">{regulation.name}</CardTitle>
                  </div>
                  <Badge variant="outline" className="w-fit text-xs">{regulation.region}</Badge>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground">{regulation.requirements}</p>
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
            <h2 className="text-3xl font-bold mb-4">Healthcare Use Cases</h2>
            <p className="text-lg text-muted-foreground">
              Common verification scenarios in healthcare
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

      {/* API Example */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">HIPAA-Compliant Integration</h2>
            <p className="text-lg text-muted-foreground">
              Secure patient verification with full audit trails
            </p>
          </div>

          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>Telehealth Patient Verification</CardTitle>
              <CardDescription>HIPAA-compliant patient onboarding</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// Initialize VeriGate with HIPAA mode
const verigate = new VeriGate(apiKey, {
  industry: 'healthcare',
  hipaaCompliant: true,
  baaRequired: true // Business Associate Agreement
});

// Verify patient for telehealth appointment
const verification = await verigate.healthcare.verifyPatient({
  idDocument: patientIdImage,
  insuranceCard: insuranceCardImage,
  selfie: patientSelfie,
  patientData: {
    firstName: 'Jane',
    lastName: 'Doe',
    dateOfBirth: '1985-03-15',
    insuranceProvider: 'Blue Cross',
    memberId: 'BC123456789'
  },
  consentGiven: true, // HIPAA consent
  purposeOfVisit: 'General consultation'
});

// Verify insurance eligibility
const insurance = await verigate.healthcare.verifyInsurance({
  insuranceCard: verification.insuranceData,
  memberInfo: verification.patientInfo
});

// Results with full audit trail
if (verification.approved && insurance.eligible) {
  await createPatientRecord({
    patientId: verification.patientId,
    verificationId: verification.id,
    insuranceVerified: true,
    hipaaConsent: verification.consentTimestamp,
    auditLog: verification.auditTrail
  });
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
            Secure Your Healthcare Platform
          </h2>
          <p className="text-xl mb-8 opacity-90">
            HIPAA-compliant verification trusted by leading healthcare providers
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" variant="secondary" asChild>
              <Link to="/contact">Request Demo</Link>
            </Button>
            <Button size="lg" variant="outline" className="bg-white/10 hover:bg-white/20 border-white text-white" asChild>
              <Link to="/security">View Compliance</Link>
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Healthcare;
