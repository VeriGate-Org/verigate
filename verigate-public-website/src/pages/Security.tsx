import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  Shield, 
  Lock, 
  FileCheck, 
  Server, 
  Eye, 
  Download, 
  CheckCircle2,
  AlertCircle,
  Globe,
  Users,
  Database,
  Key
} from "lucide-react";

const Security = () => {
  const certifications = [
    {
      title: "ISO 27001",
      description: "Information Security Management",
      status: "Certified",
      issueDate: "2023",
      expiryDate: "2026",
      certificateId: "ISO27001-2023-VG-001",
    },
    {
      title: "SOC 2 Type II",
      description: "Security, Availability, and Confidentiality",
      status: "Certified",
      issueDate: "2024",
      expiryDate: "2025",
      certificateId: "SOC2-2024-VG-002",
    },
    {
      title: "GDPR",
      description: "General Data Protection Regulation",
      status: "Compliant",
      issueDate: "Ongoing",
      expiryDate: "N/A",
      certificateId: "GDPR-COMP-VG-003",
    },
    {
      title: "PCI DSS",
      description: "Payment Card Industry Data Security",
      status: "Level 1",
      issueDate: "2024",
      expiryDate: "2025",
      certificateId: "PCI-DSS-2024-VG-004",
    },
  ];

  const securityFeatures = [
    {
      icon: Lock,
      title: "End-to-End Encryption",
      description: "All data is encrypted at rest and in transit using AES-256 and TLS 1.3",
    },
    {
      icon: Key,
      title: "Multi-Factor Authentication",
      description: "Required MFA for all user accounts and admin access",
    },
    {
      icon: Server,
      title: "Infrastructure Security",
      description: "SOC 2 certified data centers with 24/7 monitoring",
    },
    {
      icon: Eye,
      title: "Access Controls",
      description: "Role-based access control (RBAC) with audit logging",
    },
    {
      icon: Database,
      title: "Data Residency",
      description: "Choose where your data is stored - US, EU, or Asia-Pacific",
    },
    {
      icon: FileCheck,
      title: "Regular Audits",
      description: "Annual third-party security audits and penetration testing",
    },
  ];

  const complianceFrameworks = [
    {
      region: "Europe",
      frameworks: ["GDPR", "eIDAS", "PSD2", "AML5", "AMLD6"],
    },
    {
      region: "United States",
      frameworks: ["CCPA", "GLBA", "FCRA", "SOX", "HIPAA"],
    },
    {
      region: "Asia-Pacific",
      frameworks: ["PDPA (Singapore)", "Privacy Act (Australia)", "PIPA (South Korea)"],
    },
    {
      region: "Global",
      frameworks: ["ISO 27001", "SOC 2", "PCI DSS", "CSA STAR"],
    },
  ];

  const securityPractices = [
    {
      title: "Penetration Testing",
      description: "Quarterly penetration tests by certified security firms",
      icon: Shield,
    },
    {
      title: "Bug Bounty Program",
      description: "Active bug bounty program with responsible disclosure policy",
      icon: AlertCircle,
    },
    {
      title: "Incident Response",
      description: "24/7 security operations center with < 1 hour response time",
      icon: CheckCircle2,
    },
    {
      title: "Security Training",
      description: "Mandatory security awareness training for all employees",
      icon: Users,
    },
  ];

  const downloadableResources = [
    {
      title: "Security Whitepaper",
      description: "Comprehensive overview of our security architecture",
      size: "2.4 MB",
      type: "PDF",
    },
    {
      title: "SOC 2 Type II Report",
      description: "Latest SOC 2 Type II audit report (NDA required)",
      size: "1.8 MB",
      type: "PDF",
    },
    {
      title: "Data Processing Agreement",
      description: "GDPR-compliant DPA for enterprise customers",
      size: "156 KB",
      type: "PDF",
    },
    {
      title: "Compliance Matrix",
      description: "Detailed compliance framework mapping",
      size: "892 KB",
      type: "PDF",
    },
  ];

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container relative z-10">
          <div className="container mx-auto max-w-6xl text-center space-y-6">
            <div className="inline-flex items-center gap-2 py-2 rounded-full bg-accent/10 text-accent mb-4">
              <Shield className="w-5 h-5" />
              <span className="text-sm font-semibold">Enterprise-Grade Security</span>
            </div>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              Security & Compliance
              <span className="block text-accent mt-2">You Can Trust</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-2xl">
              We maintain the highest standards of security and compliance to protect your data and ensure regulatory adherence.
            </p>
          </div>
        </div>
      </section>

      {/* Certifications */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Certifications & Attestations
            </h2>
            <p className="text-lg text-muted-foreground">
              Independently verified security and compliance certifications
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {certifications.map((cert) => (
              <Card key={cert.title} className="border-border relative overflow-hidden">
                <div className="absolute top-0 right-0 w-20 h-20 bg-accent/10 rounded-bl-full" />
                <CardHeader>
                  <div className="flex items-start justify-between mb-2">
                    <Shield className="w-10 h-10 text-accent" />
                    <Badge variant="secondary">{cert.status}</Badge>
                  </div>
                  <CardTitle className="text-xl">{cert.title}</CardTitle>
                  <CardDescription>{cert.description}</CardDescription>
                </CardHeader>
                <CardContent className="space-y-2 text-sm text-muted-foreground">
                  <div className="flex justify-between">
                    <span>Issue Date:</span>
                    <span className="font-medium text-foreground">{cert.issueDate}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Valid Until:</span>
                    <span className="font-medium text-foreground">{cert.expiryDate}</span>
                  </div>
                  <div className="pt-2 border-t text-xs">
                    ID: {cert.certificateId}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Security Features */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Security Features
            </h2>
            <p className="text-lg text-muted-foreground">
              Multiple layers of security protect your data
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {securityFeatures.map((feature) => {
              const Icon = feature.icon;
              return (
                <Card key={feature.title} className="border-border">
                  <CardHeader>
                    <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center mb-4">
                      <Icon className="w-6 h-6 text-accent" />
                    </div>
                    <CardTitle className="text-lg">{feature.title}</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <CardDescription>{feature.description}</CardDescription>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* Compliance Frameworks */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Global Compliance Coverage
            </h2>
            <p className="text-lg text-muted-foreground">
              We meet regulatory requirements across all major jurisdictions
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {complianceFrameworks.map((region) => (
              <Card key={region.region} className="border-border">
                <CardHeader>
                  <div className="flex items-center gap-3 mb-2">
                    <Globe className="w-6 h-6 text-accent" />
                    <CardTitle>{region.region}</CardTitle>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="flex flex-wrap gap-2">
                    {region.frameworks.map((framework) => (
                      <Badge key={framework} variant="outline" className="border-accent text-accent">
                        {framework}
                      </Badge>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Security Practices */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Security Practices
            </h2>
            <p className="text-lg text-muted-foreground">
              Proactive measures to ensure continuous security
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {securityPractices.map((practice) => {
              const Icon = practice.icon;
              return (
                <Card key={practice.title} className="border-border text-center">
                  <CardHeader>
                    <div className="mx-auto w-16 h-16 rounded-full bg-accent/10 flex items-center justify-center mb-4">
                      <Icon className="w-8 h-8 text-accent" />
                    </div>
                    <CardTitle className="text-lg">{practice.title}</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <CardDescription>{practice.description}</CardDescription>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* Encryption Standards */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-6">
                Military-Grade Encryption
              </h2>
              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <CheckCircle2 className="w-6 h-6 text-accent flex-shrink-0 mt-1" />
                  <div>
                    <h3 className="font-semibold mb-1">AES-256 Encryption</h3>
                    <p className="text-muted-foreground">All data at rest is encrypted using AES-256, the same standard used by governments and military organizations.</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <CheckCircle2 className="w-6 h-6 text-accent flex-shrink-0 mt-1" />
                  <div>
                    <h3 className="font-semibold mb-1">TLS 1.3 in Transit</h3>
                    <p className="text-muted-foreground">Data in transit is protected with TLS 1.3, ensuring end-to-end encryption for all communications.</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <CheckCircle2 className="w-6 h-6 text-accent flex-shrink-0 mt-1" />
                  <div>
                    <h3 className="font-semibold mb-1">Key Management</h3>
                    <p className="text-muted-foreground">Secure key rotation and management using industry-standard Hardware Security Modules (HSMs).</p>
                  </div>
                </div>
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <Card className="border-border text-center p-6">
                <div className="text-4xl font-bold text-accent mb-2">256-bit</div>
                <p className="text-sm text-muted-foreground">Encryption Strength</p>
              </Card>
              <Card className="border-border text-center p-6">
                <div className="text-4xl font-bold text-accent mb-2">TLS 1.3</div>
                <p className="text-sm text-muted-foreground">Transport Security</p>
              </Card>
              <Card className="border-border text-center p-6">
                <div className="text-4xl font-bold text-accent mb-2">100%</div>
                <p className="text-sm text-muted-foreground">Data Encrypted</p>
              </Card>
              <Card className="border-border text-center p-6">
                <div className="text-4xl font-bold text-accent mb-2">24/7</div>
                <p className="text-sm text-muted-foreground">Monitoring</p>
              </Card>
            </div>
          </div>
        </div>
      </section>

      {/* Downloadable Resources */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Security Documentation
            </h2>
            <p className="text-lg text-muted-foreground">
              Download our security and compliance documentation
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {downloadableResources.map((resource) => (
              <Card key={resource.title} className="border-border">
                <CardHeader>
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <CardTitle className="text-lg mb-2">{resource.title}</CardTitle>
                      <CardDescription>{resource.description}</CardDescription>
                    </div>
                    <Download className="w-5 h-5 text-accent flex-shrink-0" />
                  </div>
                </CardHeader>
                <CardContent className="flex items-center justify-between">
                  <div className="flex items-center gap-4 text-sm text-muted-foreground">
                    <Badge variant="outline">{resource.type}</Badge>
                    <span>{resource.size}</span>
                  </div>
                  <Button variant="outline" size="sm">
                    Download
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl text-center">
          <Shield className="w-16 h-16 text-accent mx-auto mb-6" />
          <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
            Questions About Security?
          </h2>
          <p className="text-lg text-muted-foreground mb-8">
            Our security team is available to answer your questions and provide additional documentation.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Button size="lg">
              Contact Security Team
            </Button>
            <Button size="lg" variant="outline">
              View Trust Center
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Security;
