import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { 
  Rocket,
  Bug,
  AlertCircle,
  CheckCircle2,
  Clock,
  Code,
  Zap,
  Shield,
  TrendingUp,
  Package,
  Bell
} from "lucide-react";
import { Link } from "react-router-dom";

const Changelog = () => {
  const releases = [
    {
      version: "v2.5.0",
      date: "2025-01-15",
      type: "major",
      title: "Enhanced Biometric Liveness Detection",
      changes: [
        {
          type: "feature",
          title: "Passive Liveness Detection 2.0",
          description: "Improved passive liveness detection with 99.9% accuracy and 40% faster processing times.",
          impact: "high"
        },
        {
          type: "feature",
          title: "Multi-Face Detection",
          description: "New capability to detect and flag multiple faces in selfie submissions.",
          impact: "medium"
        },
        {
          type: "improvement",
          title: "SDK Performance Optimization",
          description: "JavaScript SDK now 25% smaller with improved initialization speed.",
          impact: "medium"
        },
        {
          type: "fix",
          title: "Webhook Delivery Reliability",
          description: "Fixed intermittent webhook delivery failures for high-volume customers.",
          impact: "high"
        }
      ]
    },
    {
      version: "v2.4.2",
      date: "2025-01-08",
      type: "minor",
      title: "Document Coverage Expansion",
      changes: [
        {
          type: "feature",
          title: "New Document Types",
          description: "Added support for 500+ new document types across 25 countries including driving permits, residence cards, and tax IDs.",
          impact: "high"
        },
        {
          type: "improvement",
          title: "OCR Accuracy Improvements",
          description: "Enhanced OCR engine with 2.3% accuracy improvement for non-Latin scripts.",
          impact: "medium"
        },
        {
          type: "feature",
          title: "Document Expiry Checking",
          description: "Automatic detection and flagging of expired documents.",
          impact: "medium"
        }
      ]
    },
    {
      version: "v2.4.1",
      date: "2025-01-01",
      type: "patch",
      title: "Security & Performance Updates",
      changes: [
        {
          type: "security",
          title: "Enhanced Encryption",
          description: "Upgraded to TLS 1.3 for all API endpoints with improved cipher suites.",
          impact: "high"
        },
        {
          type: "fix",
          title: "Rate Limiting Edge Cases",
          description: "Fixed rate limiting calculation issues affecting burst traffic patterns.",
          impact: "low"
        },
        {
          type: "improvement",
          title: "API Response Times",
          description: "Reduced average API response time by 18ms through database query optimization.",
          impact: "medium"
        }
      ]
    },
    {
      version: "v2.4.0",
      date: "2024-12-20",
      type: "major",
      title: "AML Screening Enhancements",
      changes: [
        {
          type: "feature",
          title: "Enhanced AML Database Coverage",
          description: "Added 15 new sanctions and PEP databases including regional watchlists.",
          impact: "high"
        },
        {
          type: "feature",
          title: "Fuzzy Name Matching",
          description: "Improved name matching algorithm with configurable sensitivity levels.",
          impact: "high"
        },
        {
          type: "feature",
          title: "Adverse Media Screening",
          description: "New adverse media screening across 50+ news sources in 12 languages.",
          impact: "medium"
        },
        {
          type: "improvement",
          title: "AML Dashboard",
          description: "Redesigned AML screening dashboard with better filtering and export options.",
          impact: "medium"
        }
      ]
    },
    {
      version: "v2.3.5",
      date: "2024-12-10",
      type: "minor",
      title: "API & SDK Updates",
      changes: [
        {
          type: "feature",
          title: "Python SDK 2.0",
          description: "Complete rewrite of Python SDK with async/await support and type hints.",
          impact: "high"
        },
        {
          type: "feature",
          title: "Webhook Event Filtering",
          description: "Configure which events trigger webhooks at the endpoint level.",
          impact: "medium"
        },
        {
          type: "deprecation",
          title: "Legacy API v1 Deprecation Notice",
          description: "API v1 will be deprecated on June 1, 2025. Please migrate to v2.",
          impact: "high"
        }
      ]
    },
    {
      version: "v2.3.4",
      date: "2024-12-01",
      type: "patch",
      title: "Bug Fixes & Improvements",
      changes: [
        {
          type: "fix",
          title: "Mobile SDK Camera Issues",
          description: "Fixed camera initialization failures on iOS 17.2 and Android 14.",
          impact: "high"
        },
        {
          type: "fix",
          title: "Dashboard Loading Issues",
          description: "Resolved slow dashboard loading for customers with 10k+ verifications.",
          impact: "medium"
        },
        {
          type: "improvement",
          title: "Error Message Clarity",
          description: "Improved API error messages with more actionable guidance.",
          impact: "low"
        }
      ]
    }
  ];

  const upcomingFeatures = [
    {
      title: "Video KYC Support",
      description: "Live video verification with agent assistance",
      eta: "Q1 2025",
      status: "In Development"
    },
    {
      title: "Blockchain Identity Anchoring",
      description: "Optional blockchain-based proof of verification",
      eta: "Q2 2025",
      status: "Planned"
    },
    {
      title: "Advanced Analytics Dashboard",
      description: "Real-time analytics with custom reporting",
      eta: "Q1 2025",
      status: "Beta Testing"
    },
    {
      title: "White-Label UI Components",
      description: "Customizable verification UI for seamless integration",
      eta: "Q2 2025",
      status: "Planned"
    }
  ];

  const getTypeIcon = (type: string) => {
    switch (type) {
      case "feature":
        return <Rocket className="w-4 h-4" />;
      case "improvement":
        return <TrendingUp className="w-4 h-4" />;
      case "fix":
        return <Bug className="w-4 h-4" />;
      case "security":
        return <Shield className="w-4 h-4" />;
      case "deprecation":
        return <AlertCircle className="w-4 h-4" />;
      default:
        return <Code className="w-4 h-4" />;
    }
  };

  const getTypeColor = (type: string) => {
    switch (type) {
      case "feature":
        return "bg-green-500/10 text-green-500 border-green-500/20";
      case "improvement":
        return "bg-blue-500/10 text-blue-500 border-blue-500/20";
      case "fix":
        return "bg-amber-500/10 text-amber-500 border-amber-500/20";
      case "security":
        return "bg-purple-500/10 text-purple-500 border-purple-500/20";
      case "deprecation":
        return "bg-red-500/10 text-red-500 border-red-500/20";
      default:
        return "bg-muted";
    }
  };

  const getImpactBadge = (impact: string) => {
    const variants: any = {
      high: "destructive",
      medium: "default",
      low: "secondary"
    };
    return <Badge variant={variants[impact]} className="text-xs">{impact.toUpperCase()}</Badge>;
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-primary/10 to-secondary/5">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl text-center">
            <Badge className="mb-4" variant="outline">
              <Package className="w-3 h-3 mr-1" />
              Product Updates
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Changelog
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Stay up to date with new features, improvements, and bug fixes. 
              All changes to the VeriGate platform and APIs.
            </p>
            <div className="flex flex-wrap gap-4 justify-center">
              <Button size="lg" asChild>
                <Link to="/developers/api-reference">
                  <Code className="w-4 h-4 mr-2" />
                  API Reference
                </Link>
              </Button>
              <Button size="lg" variant="outline">
                <Bell className="w-4 h-4 mr-2" />
                Subscribe to Updates
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Version Stats */}
      <section className="py-8 border-b bg-background">
        <div className="container mx-auto">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 text-center max-w-4xl mx-auto">
            <div>
              <div className="text-3xl font-bold text-primary mb-1">v2.5.0</div>
              <div className="text-sm text-muted-foreground">Latest Version</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">45+</div>
              <div className="text-sm text-muted-foreground">Updates in 2024</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">99.9%</div>
              <div className="text-sm text-muted-foreground">Uptime</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">24/7</div>
              <div className="text-sm text-muted-foreground">Support</div>
            </div>
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <Tabs defaultValue="all" className="w-full">
              <TabsList className="grid w-full grid-cols-5 mb-8">
                <TabsTrigger value="all">All</TabsTrigger>
                <TabsTrigger value="features">Features</TabsTrigger>
                <TabsTrigger value="improvements">Improvements</TabsTrigger>
                <TabsTrigger value="fixes">Fixes</TabsTrigger>
                <TabsTrigger value="security">Security</TabsTrigger>
              </TabsList>

              {/* All Releases */}
              <TabsContent value="all" className="space-y-12">
                {releases.map((release, index) => (
                  <div key={index}>
                    <div className="flex items-center gap-4 mb-6">
                      <div className="flex items-center gap-3">
                        <div className="flex items-center gap-2">
                          <Badge variant="outline" className="font-mono">
                            {release.version}
                          </Badge>
                          <Badge variant={release.type === "major" ? "default" : "secondary"}>
                            {release.type}
                          </Badge>
                        </div>
                        <span className="text-muted-foreground">•</span>
                        <div className="flex items-center gap-2 text-sm text-muted-foreground">
                          <Clock className="w-4 h-4" />
                          {new Date(release.date).toLocaleDateString('en-US', { 
                            month: 'long', 
                            day: 'numeric', 
                            year: 'numeric' 
                          })}
                        </div>
                      </div>
                    </div>

                    <h3 className="text-2xl font-bold mb-4">{release.title}</h3>

                    <div className="space-y-3">
                      {release.changes.map((change, changeIndex) => (
                        <Card key={changeIndex} className="overflow-hidden">
                          <CardContent className="p-4">
                            <div className="flex items-start gap-4">
                              <div className={`p-2 rounded ${getTypeColor(change.type)}`}>
                                {getTypeIcon(change.type)}
                              </div>
                              <div className="flex-1">
                                <div className="flex items-center gap-2 mb-1">
                                  <h4 className="font-semibold">{change.title}</h4>
                                  {getImpactBadge(change.impact)}
                                </div>
                                <p className="text-sm text-muted-foreground">
                                  {change.description}
                                </p>
                              </div>
                            </div>
                          </CardContent>
                        </Card>
                      ))}
                    </div>
                  </div>
                ))}
              </TabsContent>

              {/* Features Only */}
              <TabsContent value="features" className="space-y-4">
                {releases.flatMap(release => 
                  release.changes
                    .filter(change => change.type === "feature")
                    .map((change, idx) => (
                      <Card key={idx}>
                        <CardHeader>
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <Rocket className="w-5 h-5 text-green-500" />
                              <CardTitle className="text-lg">{change.title}</CardTitle>
                            </div>
                            <Badge variant="outline" className="font-mono">{release.version}</Badge>
                          </div>
                          <CardDescription>{change.description}</CardDescription>
                        </CardHeader>
                      </Card>
                    ))
                )}
              </TabsContent>

              {/* Improvements Only */}
              <TabsContent value="improvements" className="space-y-4">
                {releases.flatMap(release => 
                  release.changes
                    .filter(change => change.type === "improvement")
                    .map((change, idx) => (
                      <Card key={idx}>
                        <CardHeader>
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <TrendingUp className="w-5 h-5 text-blue-500" />
                              <CardTitle className="text-lg">{change.title}</CardTitle>
                            </div>
                            <Badge variant="outline" className="font-mono">{release.version}</Badge>
                          </div>
                          <CardDescription>{change.description}</CardDescription>
                        </CardHeader>
                      </Card>
                    ))
                )}
              </TabsContent>

              {/* Fixes Only */}
              <TabsContent value="fixes" className="space-y-4">
                {releases.flatMap(release => 
                  release.changes
                    .filter(change => change.type === "fix")
                    .map((change, idx) => (
                      <Card key={idx}>
                        <CardHeader>
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <Bug className="w-5 h-5 text-amber-500" />
                              <CardTitle className="text-lg">{change.title}</CardTitle>
                            </div>
                            <Badge variant="outline" className="font-mono">{release.version}</Badge>
                          </div>
                          <CardDescription>{change.description}</CardDescription>
                        </CardHeader>
                      </Card>
                    ))
                )}
              </TabsContent>

              {/* Security Only */}
              <TabsContent value="security" className="space-y-4">
                {releases.flatMap(release => 
                  release.changes
                    .filter(change => change.type === "security")
                    .map((change, idx) => (
                      <Card key={idx}>
                        <CardHeader>
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <Shield className="w-5 h-5 text-purple-500" />
                              <CardTitle className="text-lg">{change.title}</CardTitle>
                            </div>
                            <Badge variant="outline" className="font-mono">{release.version}</Badge>
                          </div>
                          <CardDescription>{change.description}</CardDescription>
                        </CardHeader>
                      </Card>
                    ))
                )}
              </TabsContent>
            </Tabs>
          </div>
        </div>
      </section>

      {/* Upcoming Features */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <div className="text-center mb-12">
              <Badge className="mb-4" variant="outline">
                <Zap className="w-3 h-3 mr-1" />
                Coming Soon
              </Badge>
              <h2 className="text-3xl font-bold mb-4">Upcoming Features</h2>
              <p className="text-muted-foreground">
                Here's what we're working on next
              </p>
            </div>

            <div className="grid md:grid-cols-2 gap-6">
              {upcomingFeatures.map((feature, index) => (
                <Card key={index}>
                  <CardHeader>
                    <div className="flex items-center justify-between mb-2">
                      <Badge variant="secondary">{feature.status}</Badge>
                      <span className="text-sm text-muted-foreground">{feature.eta}</span>
                    </div>
                    <CardTitle className="text-lg">{feature.title}</CardTitle>
                    <CardDescription>{feature.description}</CardDescription>
                  </CardHeader>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16">
        <div className="container mx-auto">
          <Card className="max-w-3xl mx-auto bg-gradient-to-br from-primary to-primary/80 text-primary-foreground">
            <CardContent className="p-12 text-center">
              <h2 className="text-3xl font-bold mb-4">Stay Updated</h2>
              <p className="text-lg mb-8 text-primary-foreground/90">
                Subscribe to our changelog to get notified about new features, 
                improvements, and important updates.
              </p>
              <div className="flex flex-wrap gap-4 justify-center">
                <Button size="lg" variant="secondary">
                  <Bell className="w-4 h-4 mr-2" />
                  Subscribe to Updates
                </Button>
                <Button 
                  size="lg" 
                  variant="outline"
                  className="bg-transparent border-primary-foreground text-primary-foreground hover:bg-primary-foreground/10"
                  asChild
                >
                  <Link to="/developers/api-reference">
                    View API Docs
                  </Link>
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Changelog;
