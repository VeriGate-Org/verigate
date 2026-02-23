import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2, 
  XCircle, 
  AlertCircle,
  FileText,
  Download,
  Shield,
  Activity,
  Clock,
  Server,
  Globe,
  Lock
} from "lucide-react";

const TrustCenter = () => {
  const systemStatus = {
    overall: "operational",
    lastUpdated: "2 minutes ago",
    components: [
      {
        name: "API Services",
        status: "operational",
        uptime: "99.99%",
        latency: "45ms",
      },
      {
        name: "Document Verification",
        status: "operational",
        uptime: "99.98%",
        latency: "120ms",
      },
      {
        name: "Biometric Services",
        status: "operational",
        uptime: "99.97%",
        latency: "180ms",
      },
      {
        name: "AML Screening",
        status: "operational",
        uptime: "99.96%",
        latency: "95ms",
      },
      {
        name: "Dashboard & Portal",
        status: "operational",
        uptime: "99.99%",
        latency: "35ms",
      },
    ],
  };

  const uptimeHistory = [
    { period: "Last 24 hours", uptime: "100%" },
    { period: "Last 7 days", uptime: "99.98%" },
    { period: "Last 30 days", uptime: "99.95%" },
    { period: "Last 90 days", uptime: "99.94%" },
  ];

  const securityReports = [
    {
      title: "SOC 2 Type II Report",
      description: "Annual security audit covering security, availability, and confidentiality",
      date: "January 2025",
      type: "Audit Report",
      restricted: true,
    },
    {
      title: "Penetration Test Results",
      description: "Q4 2024 penetration testing report with remediation status",
      date: "December 2024",
      type: "Security Test",
      restricted: true,
    },
    {
      title: "Security Whitepaper",
      description: "Comprehensive overview of our security architecture and practices",
      date: "November 2024",
      type: "Whitepaper",
      restricted: false,
    },
    {
      title: "Incident Response Plan",
      description: "Our approach to security incidents and disaster recovery",
      date: "October 2024",
      type: "Policy Document",
      restricted: false,
    },
  ];

  const complianceDocs = [
    {
      title: "Data Processing Agreement",
      description: "GDPR-compliant DPA for enterprise customers",
      format: "PDF",
      size: "156 KB",
    },
    {
      title: "Privacy Policy",
      description: "How we collect, use, and protect your data",
      format: "PDF",
      size: "245 KB",
    },
    {
      title: "Terms of Service",
      description: "Legal terms governing use of VeriGate services",
      format: "PDF",
      size: "198 KB",
    },
    {
      title: "Subprocessor List",
      description: "Third-party service providers we work with",
      format: "PDF",
      size: "89 KB",
    },
    {
      title: "Cookie Policy",
      description: "Information about cookies and tracking technologies",
      format: "PDF",
      size: "112 KB",
    },
    {
      title: "Data Retention Policy",
      description: "How long we keep different types of data",
      format: "PDF",
      size: "134 KB",
    },
  ];

  const incidents = [
    {
      date: "No incidents in the last 90 days",
      severity: "none",
      message: "All systems operating normally with no security incidents or major outages.",
    },
  ];

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "operational":
        return <CheckCircle2 className="w-5 h-5 text-green-500" />;
      case "degraded":
        return <AlertCircle className="w-5 h-5 text-yellow-500" />;
      case "outage":
        return <XCircle className="w-5 h-5 text-red-500" />;
      default:
        return <CheckCircle2 className="w-5 h-5 text-green-500" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case "operational":
        return "text-green-500";
      case "degraded":
        return "text-yellow-500";
      case "outage":
        return "text-red-500";
      default:
        return "text-green-500";
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container relative z-10">
          <div className="container mx-auto max-w-6xl text-center space-y-6">
            <div className="inline-flex items-center gap-2 py-2 rounded-full bg-green-500/10 text-green-600 mb-4">
              <Activity className="w-5 h-5" />
              <span className="text-sm font-semibold">All Systems Operational</span>
            </div>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              Trust Center
            </h1>
            <p className="text-xl text-muted-foreground max-w-2xl">
              Real-time system status, security reports, and compliance documentation all in one place.
            </p>
          </div>
        </div>
      </section>

      {/* System Status */}
      <section className="py-12 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="flex items-center justify-between mb-8">
            <div>
              <h2 className="text-2xl font-bold text-foreground mb-2">System Status</h2>
              <p className="text-sm text-muted-foreground flex items-center gap-2">
                <Clock className="w-4 h-4" />
                Last updated: {systemStatus.lastUpdated}
              </p>
            </div>
            <Badge className="bg-green-500 text-white">All Systems Operational</Badge>
          </div>
          
          <div className="grid grid-cols-1 gap-4">
            {systemStatus.components.map((component) => (
              <Card key={component.name} className="border-border">
                <CardContent className="pt-6">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-4 flex-1">
                      {getStatusIcon(component.status)}
                      <div className="flex-1">
                        <h3 className="font-semibold">{component.name}</h3>
                        <p className={`text-sm capitalize ${getStatusColor(component.status)}`}>
                          {component.status}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center gap-6 text-sm">
                      <div className="text-center">
                        <div className="text-muted-foreground text-xs mb-1">Uptime</div>
                        <div className="font-semibold">{component.uptime}</div>
                      </div>
                      <div className="text-center">
                        <div className="text-muted-foreground text-xs mb-1">Latency</div>
                        <div className="font-semibold">{component.latency}</div>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Uptime History */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Uptime History
            </h2>
            <p className="text-lg text-muted-foreground">
              Our track record of reliability and availability
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {uptimeHistory.map((item) => (
              <Card key={item.period} className="border-border text-center">
                <CardHeader>
                  <CardDescription className="text-sm">{item.period}</CardDescription>
                  <CardTitle className="text-4xl text-accent">{item.uptime}</CardTitle>
                </CardHeader>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Security Reports */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Security Reports
            </h2>
            <p className="text-lg text-muted-foreground">
              Independent security audits and assessments
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {securityReports.map((report) => (
              <Card key={report.title} className="border-border">
                <CardHeader>
                  <div className="flex items-start justify-between mb-2">
                    <Shield className="w-8 h-8 text-accent" />
                    {report.restricted && (
                      <Badge variant="outline" className="border-accent text-accent">
                        <Lock className="w-3 h-3 mr-1" />
                        NDA Required
                      </Badge>
                    )}
                  </div>
                  <CardTitle className="text-lg">{report.title}</CardTitle>
                  <CardDescription>{report.description}</CardDescription>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-muted-foreground">Type:</span>
                    <Badge variant="secondary">{report.type}</Badge>
                  </div>
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-muted-foreground">Date:</span>
                    <span className="font-medium">{report.date}</span>
                  </div>
                  <Button 
                    variant={report.restricted ? "outline" : "default"} 
                    className="w-full mt-4"
                  >
                    {report.restricted ? "Request Access" : "Download"}
                    <Download className="w-4 h-4 ml-2" />
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Compliance Documents */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Compliance Documentation
            </h2>
            <p className="text-lg text-muted-foreground">
              Legal and compliance documents for your review
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {complianceDocs.map((doc) => (
              <Card key={doc.title} className="border-border hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-center gap-3 mb-2">
                    <FileText className="w-6 h-6 text-accent" />
                    <Badge variant="outline">{doc.format}</Badge>
                  </div>
                  <CardTitle className="text-base">{doc.title}</CardTitle>
                  <CardDescription className="text-sm">{doc.description}</CardDescription>
                </CardHeader>
                <CardContent className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">{doc.size}</span>
                  <Button variant="ghost" size="sm">
                    <Download className="w-4 h-4" />
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Incident History */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Incident History
            </h2>
            <p className="text-lg text-muted-foreground">
              Transparency about security incidents and outages
            </p>
          </div>
          
          <Card className="border-border">
            <CardContent className="pt-6">
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 rounded-full bg-green-500/10 flex items-center justify-center flex-shrink-0">
                  <CheckCircle2 className="w-6 h-6 text-green-500" />
                </div>
                <div className="flex-1">
                  <h3 className="font-semibold mb-1">{incidents[0].date}</h3>
                  <p className="text-muted-foreground">{incidents[0].message}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Infrastructure */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Infrastructure & Redundancy
            </h2>
            <p className="text-lg text-muted-foreground">
              Built for reliability and performance
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card className="border-border text-center">
              <CardHeader>
                <Server className="w-12 h-12 text-accent mx-auto mb-4" />
                <CardTitle>Multi-Region</CardTitle>
                <CardDescription>
                  Services deployed across multiple geographic regions for redundancy
                </CardDescription>
              </CardHeader>
            </Card>
            
            <Card className="border-border text-center">
              <CardHeader>
                <Globe className="w-12 h-12 text-accent mx-auto mb-4" />
                <CardTitle>Global CDN</CardTitle>
                <CardDescription>
                  Content delivered from edge locations worldwide for optimal performance
                </CardDescription>
              </CardHeader>
            </Card>
            
            <Card className="border-border text-center">
              <CardHeader>
                <Activity className="w-12 h-12 text-accent mx-auto mb-4" />
                <CardTitle>Real-Time Monitoring</CardTitle>
                <CardDescription>
                  24/7 automated monitoring with instant alerting and response
                </CardDescription>
              </CardHeader>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl text-center">
          <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
            Subscribe to Status Updates
          </h2>
          <p className="text-lg text-muted-foreground mb-8">
            Get notified immediately about any system incidents or planned maintenance
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Button size="lg">
              Subscribe via Email
            </Button>
            <Button size="lg" variant="outline">
              RSS Feed
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default TrustCenter;
