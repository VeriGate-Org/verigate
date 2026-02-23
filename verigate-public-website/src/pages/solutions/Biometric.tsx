import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  ScanFace,
  Shield,
  Zap,
  Eye,
  Fingerprint,
  Smartphone,
  Lock,
  AlertTriangle,
  Clock,
  ArrowRight,
  Video,
  UserCheck,
  Cpu
} from "lucide-react";
import { Link } from "react-router-dom";

const Biometric = () => {
  const verificationProcess = [
    {
      step: 1,
      title: "Face Capture",
      description: "High-quality selfie capture with real-time guidance and quality checks",
      icon: ScanFace,
      duration: "2-3 seconds",
    },
    {
      step: 2,
      title: "Liveness Detection",
      description: "3D liveness check to prevent spoofing with photos, videos, or masks",
      icon: Video,
      duration: "1-2 seconds",
    },
    {
      step: 3,
      title: "Face Matching",
      description: "AI compares selfie to ID photo with advanced neural networks",
      icon: Eye,
      duration: "< 1 second",
    },
    {
      step: 4,
      title: "Authentication",
      description: "Instant verification with confidence score and fraud risk assessment",
      icon: CheckCircle2,
      duration: "< 1 second",
    },
  ];

  const livenessTypes = [
    {
      type: "Active Liveness",
      description: "User performs actions like blinking, smiling, or turning head",
      features: [
        "Blink detection",
        "Head movement tracking",
        "Expression analysis",
        "Challenge-response verification",
      ],
      accuracy: "99.9%",
      speed: "3-5 seconds",
    },
    {
      type: "Passive Liveness",
      description: "Silent verification without user interaction using 3D depth analysis",
      features: [
        "3D face mapping",
        "Texture analysis",
        "Depth detection",
        "Micro-movement tracking",
      ],
      accuracy: "99.7%",
      speed: "1-2 seconds",
    },
  ];

  const spoofingProtection = [
    {
      threat: "Photo Attacks",
      description: "Detects printed photos or digital images held up to camera",
      protection: "Texture analysis, 3D depth mapping, reflection detection",
      icon: AlertTriangle,
    },
    {
      threat: "Video Replay",
      description: "Identifies pre-recorded videos being played back",
      protection: "Motion pattern analysis, screen detection, moiré pattern recognition",
      icon: Video,
    },
    {
      threat: "3D Masks",
      description: "Detects realistic 3D masks and silicone replicas",
      protection: "Depth analysis, skin texture validation, micro-expression detection",
      icon: ScanFace,
    },
    {
      threat: "Deepfakes",
      description: "Identifies AI-generated synthetic videos and faces",
      protection: "Neural network analysis, pixel-level inconsistency detection, temporal analysis",
      icon: Cpu,
    },
  ];

  const features = [
    {
      icon: Zap,
      title: "Lightning Fast",
      description: "Complete biometric verification in under 5 seconds with real-time results",
    },
    {
      icon: Shield,
      title: "Highly Secure",
      description: "99.9% accuracy with multi-layer anti-spoofing protection",
    },
    {
      icon: Smartphone,
      title: "Device Agnostic",
      description: "Works on any device with a camera - mobile, tablet, desktop, or kiosk",
    },
    {
      icon: Lock,
      title: "Privacy First",
      description: "Encrypted biometric templates, GDPR compliant, no storage of raw images",
    },
    {
      icon: Eye,
      title: "High Accuracy",
      description: "Advanced AI models with false accept rate < 0.1%",
    },
    {
      icon: UserCheck,
      title: "User Friendly",
      description: "Intuitive interface with real-time guidance and accessibility features",
    },
  ];

  const useCases = [
    {
      title: "Identity Verification",
      description: "Match user's face to their ID document for KYC compliance",
      benefit: "99.9% accuracy",
      icon: UserCheck,
    },
    {
      title: "Account Recovery",
      description: "Secure password reset and account access using facial recognition",
      benefit: "95% cost reduction",
      icon: Lock,
    },
    {
      title: "Transaction Authentication",
      description: "Confirm high-risk transactions with biometric verification",
      benefit: "87% fraud reduction",
      icon: Shield,
    },
    {
      title: "Access Control",
      description: "Physical or digital access management with touchless authentication",
      benefit: "Contactless security",
      icon: Fingerprint,
    },
  ];

  const technicalSpecs = [
    { spec: "False Accept Rate (FAR)", value: "< 0.1%", description: "Chance of accepting wrong person" },
    { spec: "False Reject Rate (FRR)", value: "< 1%", description: "Chance of rejecting correct person" },
    { spec: "Verification Speed", value: "< 5 seconds", description: "End-to-end processing time" },
    { spec: "Liveness Accuracy", value: "99.9%", description: "Anti-spoofing detection rate" },
  ];

  const metrics = [
    { label: "Accuracy", value: "99.9%", icon: CheckCircle2 },
    { label: "Verification Time", value: "< 5 sec", icon: Clock },
    { label: "Anti-Spoofing", value: "99.9%", icon: Shield },
    { label: "False Accept", value: "< 0.1%", icon: AlertTriangle },
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
                Biometric Authentication
              </Badge>
              <h1 className="text-4xl md:text-5xl font-bold mb-6">
                Advanced Facial Recognition
                <span className="text-primary block mt-2">& Liveness Detection</span>
              </h1>
              <p className="text-xl text-muted-foreground mb-8">
                Verify users with industry-leading facial recognition and 3D liveness detection. 
                Prevent fraud with 99.9% accuracy and sub-5 second verification times.
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
                  <ScanFace className="w-32 h-32 text-primary mx-auto mb-4" />
                  <p className="text-lg font-semibold">99.9% Liveness Accuracy</p>
                  <p className="text-muted-foreground">Advanced anti-spoofing protection</p>
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
            <h2 className="text-3xl font-bold mb-4">How Biometric Verification Works</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Four-step process for secure facial authentication
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
                    <Badge variant="secondary" className="text-xs">
                      <Clock className="w-3 h-3 mr-1" />
                      {step.duration}
                    </Badge>
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

      {/* Liveness Detection Types */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Advanced Liveness Detection</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Choose between active and passive liveness checks for optimal security
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            {livenessTypes.map((liveness, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Video className="w-6 h-6 text-primary" />
                    {liveness.type}
                  </CardTitle>
                  <CardDescription>{liveness.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <ul className="space-y-2">
                      {liveness.features.map((feature, idx) => (
                        <li key={idx} className="flex items-start gap-2 text-sm">
                          <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                          <span>{feature}</span>
                        </li>
                      ))}
                    </ul>
                    <div className="flex gap-4 pt-4 border-t">
                      <div>
                        <div className="text-sm text-muted-foreground">Accuracy</div>
                        <Badge variant="default">{liveness.accuracy}</Badge>
                      </div>
                      <div>
                        <div className="text-sm text-muted-foreground">Speed</div>
                        <Badge variant="secondary">{liveness.speed}</Badge>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Anti-Spoofing Protection */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Comprehensive Anti-Spoofing</h2>
            <p className="text-lg text-muted-foreground">
              Multi-layer protection against all types of presentation attacks
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {spoofingProtection.map((protection, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-center gap-3 mb-2">
                    <div className="p-2 bg-destructive/10 rounded-lg">
                      <protection.icon className="w-5 h-5 text-destructive" />
                    </div>
                    <CardTitle className="text-lg">{protection.threat}</CardTitle>
                  </div>
                  <CardDescription>{protection.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="text-sm">
                    <span className="font-medium">Protection Method:</span>
                    <p className="text-muted-foreground mt-1">{protection.protection}</p>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Technical Specifications */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Technical Performance</h2>
            <p className="text-lg text-muted-foreground">
              Industry-leading accuracy and speed metrics
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {technicalSpecs.map((spec, index) => (
              <Card key={index}>
                <CardHeader className="text-center">
                  <CardTitle className="text-3xl font-bold text-primary">{spec.value}</CardTitle>
                  <CardDescription className="font-semibold mt-2">{spec.spec}</CardDescription>
                </CardHeader>
                <CardContent className="text-center">
                  <p className="text-xs text-muted-foreground">{spec.description}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Key Features */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Why Choose VeriGate Biometrics</h2>
            <p className="text-lg text-muted-foreground">
              Advanced features for secure, user-friendly authentication
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
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Common Use Cases</h2>
            <p className="text-lg text-muted-foreground">
              Biometric authentication for every security need
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {useCases.map((useCase, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-center gap-3 mb-2">
                    <useCase.icon className="w-6 h-6 text-primary" />
                    <CardTitle>{useCase.title}</CardTitle>
                  </div>
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
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Simple Integration</h2>
            <p className="text-lg text-muted-foreground">
              Add biometric authentication in minutes
            </p>
          </div>

          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>Biometric Verification Example</CardTitle>
              <CardDescription>Verify user's face with liveness detection</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// Initialize VeriGate SDK
const verigate = new VeriGate('your_api_key');

// Perform biometric verification
const result = await verigate.biometric.verify({
  selfieImage: selfieFile,
  referenceImage: idPhotoFile, // Optional: for face matching
  livenessCheck: 'passive', // 'active' or 'passive'
  returnConfidenceScore: true
});

// Handle results
if (result.isLive && result.isMatch) {
  console.log('Verification successful!');
  console.log('Liveness Score:', result.livenessScore);
  console.log('Match Confidence:', result.matchConfidence);
  console.log('Overall Score:', result.overallConfidence);
  
  // Check for spoofing attempts
  if (result.spoofingDetected) {
    console.log('Warning: Spoofing attempt detected');
    console.log('Attack Type:', result.attackType);
  }
  
  // Access biometric template (encrypted)
  console.log('Template ID:', result.templateId);
} else {
  console.log('Verification failed');
  if (!result.isLive) {
    console.log('Liveness check failed');
  }
  if (!result.isMatch) {
    console.log('Face matching failed');
  }
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
            Secure Your Platform with Biometric Authentication
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join industry leaders using VeriGate for secure, frictionless user verification
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

export default Biometric;
