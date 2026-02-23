import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  FileCheck,
  Globe,
  Zap,
  Shield,
  ScanLine,
  Brain,
  Eye,
  AlertTriangle,
  Clock,
  ArrowRight,
  FileText,
  CreditCard,
  Smartphone
} from "lucide-react";
import { Link } from "react-router-dom";

const DocumentVerification = () => {
  const verificationProcess = [
    {
      step: 1,
      title: "Document Capture",
      description: "High-quality image capture with auto-crop and edge detection",
      icon: Smartphone,
      features: ["Auto-focus guidance", "Glare detection", "Quality checks"],
    },
    {
      step: 2,
      title: "Authenticity Check",
      description: "AI verifies security features, holograms, and anti-fraud elements",
      icon: Shield,
      features: ["MRZ validation", "Barcode verification", "Template matching"],
    },
    {
      step: 3,
      title: "Data Extraction",
      description: "OCR technology extracts and structures document data with high accuracy",
      icon: ScanLine,
      features: ["Name extraction", "Date parsing", "Address recognition"],
    },
    {
      step: 4,
      title: "Validation",
      description: "Cross-reference extracted data with known patterns and databases",
      icon: CheckCircle2,
      features: ["Format validation", "Checksum verification", "Expiry checks"],
    },
  ];

  const documentTypes = [
    {
      category: "Identity Documents",
      icon: CreditCard,
      types: [
        "Passports (all countries)",
        "National ID Cards",
        "Driver's Licenses",
        "Residence Permits",
        "Visa Documents",
        "Travel Documents",
      ],
      count: "3,500+ variations",
    },
    {
      category: "Financial Documents",
      icon: FileText,
      types: [
        "Bank Statements",
        "Credit Card Statements",
        "Tax Returns",
        "Payslips",
        "Investment Statements",
        "Financial Reports",
      ],
      count: "1,000+ variations",
    },
    {
      category: "Proof of Address",
      icon: FileCheck,
      types: [
        "Utility Bills",
        "Rental Agreements",
        "Mortgage Documents",
        "Insurance Documents",
        "Government Letters",
        "Council Tax Bills",
      ],
      count: "500+ variations",
    },
  ];

  const securityFeatures = [
    {
      feature: "Hologram Detection",
      description: "Identifies and validates holographic security features",
      accuracy: "99.7%",
    },
    {
      feature: "Microprint Analysis",
      description: "Detects microscopic text used in authentic documents",
      accuracy: "99.5%",
    },
    {
      feature: "UV Light Validation",
      description: "Simulated UV detection for invisible security marks",
      accuracy: "98.9%",
    },
    {
      feature: "Watermark Recognition",
      description: "Identifies embedded watermarks and patterns",
      accuracy: "99.3%",
    },
    {
      feature: "Pattern Matching",
      description: "Compares document against known authentic templates",
      accuracy: "99.8%",
    },
    {
      feature: "Tampering Detection",
      description: "AI detects photo swaps, alterations, and forgeries",
      accuracy: "99.6%",
    },
  ];

  const features = [
    {
      icon: Brain,
      title: "AI-Powered OCR",
      description: "Advanced machine learning for 99.8% accuracy in data extraction across all languages",
    },
    {
      icon: Zap,
      title: "Instant Processing",
      description: "Extract and validate document data in under 3 seconds",
    },
    {
      icon: Globe,
      title: "Global Coverage",
      description: "Support for 5,000+ document types from 190+ countries",
    },
    {
      icon: Shield,
      title: "Fraud Prevention",
      description: "Multi-layer security checks to detect forgeries and tampering",
    },
    {
      icon: Eye,
      title: "Quality Assurance",
      description: "Automatic image quality validation and enhancement",
    },
    {
      icon: FileCheck,
      title: "Format Flexibility",
      description: "Accept images, PDFs, and scanned documents",
    },
  ];

  const useCases = [
    {
      title: "Customer Onboarding",
      description: "Verify identity documents during account opening and KYC processes",
      benefit: "87% faster onboarding",
    },
    {
      title: "Age Verification",
      description: "Confirm age for age-restricted products and services",
      benefit: "99.5% accuracy",
    },
    {
      title: "Address Validation",
      description: "Verify proof of address for compliance and fraud prevention",
      benefit: "50% cost reduction",
    },
    {
      title: "Financial Verification",
      description: "Validate income documents for lending and credit decisions",
      benefit: "2x faster processing",
    },
  ];

  const metrics = [
    { label: "Extraction Accuracy", value: "99.8%", icon: CheckCircle2 },
    { label: "Processing Speed", value: "< 3 seconds", icon: Clock },
    { label: "Document Types", value: "5,000+", icon: FileCheck },
    { label: "Countries", value: "190+", icon: Globe },
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
                Document Verification
              </Badge>
              <h1 className="text-4xl md:text-5xl font-bold mb-6">
                AI-Powered Document
                <span className="text-primary block mt-2">Verification & Data Extraction</span>
              </h1>
              <p className="text-xl text-muted-foreground mb-8">
                Verify any document type with 99.8% accuracy. Extract, validate, and structure data 
                from 5,000+ document types across 190+ countries in under 3 seconds.
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
                  <FileCheck className="w-32 h-32 text-primary mx-auto mb-4" />
                  <p className="text-lg font-semibold">5,000+ Document Types</p>
                  <p className="text-muted-foreground">From 190+ countries worldwide</p>
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
            <h2 className="text-3xl font-bold mb-4">How Document Verification Works</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Four-step process for comprehensive document verification
            </p>
          </div>

          <div className="grid md:grid-cols-4 gap-6">
            {verificationProcess.map((step, index) => (
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
                    <ul className="space-y-1">
                      {step.features.map((feature, idx) => (
                        <li key={idx} className="text-xs text-muted-foreground flex items-start gap-1">
                          <span className="text-primary">•</span>
                          <span>{feature}</span>
                        </li>
                      ))}
                    </ul>
                  </CardContent>
                </Card>
                {index < verificationProcess.length - 1 && (
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
            <h2 className="text-3xl font-bold mb-4">Comprehensive Document Support</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Verify any document type with our extensive global coverage
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {documentTypes.map((docType, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <docType.icon className="w-6 h-6 text-primary" />
                    <CardTitle>{docType.category}</CardTitle>
                  </div>
                  <Badge variant="outline" className="w-fit">{docType.count}</Badge>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {docType.types.map((type, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{type}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Security Features */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Advanced Fraud Detection</h2>
            <p className="text-lg text-muted-foreground">
              Multi-layer security validation to detect forgeries and tampering
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {securityFeatures.map((security, index) => (
              <Card key={index}>
                <CardHeader>
                  <CardTitle className="text-lg flex items-center justify-between">
                    <span>{security.feature}</span>
                    <Badge variant="secondary">{security.accuracy}</Badge>
                  </CardTitle>
                  <CardDescription>{security.description}</CardDescription>
                </CardHeader>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Key Features */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Powerful Features</h2>
            <p className="text-lg text-muted-foreground">
              Everything you need for document verification
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

      {/* Use Cases */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Common Use Cases</h2>
            <p className="text-lg text-muted-foreground">
              Document verification for every business need
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
                  <Badge variant="secondary" className="text-primary font-semibold">
                    {useCase.benefit}
                  </Badge>
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
            <h2 className="text-3xl font-bold mb-4">Easy Integration</h2>
            <p className="text-lg text-muted-foreground">
              Start verifying documents in minutes
            </p>
          </div>

          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>Document Verification Example</CardTitle>
              <CardDescription>Verify and extract data from any document</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// Initialize VeriGate SDK
const verigate = new VeriGate('your_api_key');

// Verify document
const result = await verigate.documents.verify({
  documentImage: imageFile,
  documentType: 'passport', // auto-detect available
  extractData: true
});

// Handle results
if (result.isAuthentic) {
  console.log('Document verified successfully!');
  console.log('Document Type:', result.type);
  console.log('Country:', result.country);
  console.log('Confidence:', result.confidence);
  
  // Access extracted data
  const data = result.extractedData;
  console.log('Name:', data.fullName);
  console.log('DOB:', data.dateOfBirth);
  console.log('Document Number:', data.documentNumber);
  console.log('Expiry Date:', data.expiryDate);
  
  // Check for tampering
  if (result.fraudDetection.detected) {
    console.log('Warning: Tampering detected');
    console.log('Details:', result.fraudDetection.details);
  }
} else {
  console.log('Document verification failed');
  console.log('Reason:', result.failureReason);
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
            Start Verifying Documents Today
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join hundreds of companies using VeriGate for accurate, fast document verification
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

export default DocumentVerification;
