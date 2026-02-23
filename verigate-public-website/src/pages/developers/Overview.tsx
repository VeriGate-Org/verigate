import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  Code,
  Book,
  Zap,
  Terminal,
  Package,
  Webhook,
  Key,
  CheckCircle2,
  ArrowRight,
  Play,
  Download,
  FileCode,
  Github,
  ExternalLink,
  Activity,
  Layers,
  Shield
} from "lucide-react";
import { Link } from "react-router-dom";

const DeveloperOverview = () => {
  const resources = [
    {
      icon: Book,
      title: "API Reference",
      description: "Complete documentation for all API endpoints",
      link: "/developers/api-reference",
      badge: "Most Popular"
    },
    {
      icon: Package,
      title: "SDKs & Libraries",
      description: "Client libraries for 9 programming languages",
      link: "/developers/sdks",
      badge: "9 Languages"
    },
    {
      icon: Play,
      title: "API Playground",
      description: "Test API endpoints in real-time",
      link: "/developers/playground",
      badge: "Interactive"
    },
    {
      icon: Webhook,
      title: "Webhooks",
      description: "Real-time event notifications",
      link: "/developers/webhooks",
      badge: "Real-time"
    },
    {
      icon: FileCode,
      title: "Code Examples",
      description: "Ready-to-use code snippets",
      link: "/developers/examples",
      badge: "100+ Examples"
    },
    {
      icon: Activity,
      title: "API Status",
      description: "System uptime and performance",
      link: "/status",
      badge: "99.9% Uptime"
    }
  ];

  const quickLinks = [
    {
      title: "Authentication",
      description: "Learn how to authenticate API requests",
      link: "/developers/api-reference#authentication"
    },
    {
      title: "Rate Limits",
      description: "Understand API rate limiting",
      link: "/developers/api-reference#rate-limits"
    },
    {
      title: "Error Codes",
      description: "Complete error code reference",
      link: "/developers/api-reference#errors"
    },
    {
      title: "Changelog",
      description: "Latest API updates and changes",
      link: "/changelog"
    }
  ];

  const popularEndpoints = [
    {
      method: "POST",
      endpoint: "/v1/verifications",
      description: "Create a new verification"
    },
    {
      method: "GET",
      endpoint: "/v1/verifications/:id",
      description: "Retrieve verification status"
    },
    {
      method: "POST",
      endpoint: "/v1/aml/screenings",
      description: "Run AML screening"
    },
    {
      method: "GET",
      endpoint: "/v1/documents/supported",
      description: "Get supported document types"
    }
  ];

  const sdkLanguages = [
    { name: "JavaScript", icon: "📜", stars: "2.5k", version: "v2.1.0" },
    { name: "Python", icon: "🐍", stars: "1.8k", version: "v1.9.0" },
    { name: "Ruby", icon: "💎", stars: "890", version: "v1.5.0" },
    { name: "PHP", icon: "🐘", stars: "1.2k", version: "v2.0.0" },
    { name: "Java", icon: "☕", stars: "1.5k", version: "v3.0.1" },
    { name: "Go", icon: "🔷", stars: "980", version: "v1.4.0" },
    { name: "C#", icon: "🔹", stars: "750", version: "v1.3.0" },
    { name: "Swift", icon: "🦅", stars: "620", version: "v1.2.0" },
    { name: "Kotlin", icon: "🟣", stars: "540", version: "v1.1.0" }
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-primary/10 to-secondary/5">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl text-center">
            <Badge className="mb-4" variant="outline">
              <Code className="w-3 h-3 mr-1" />
              Developer Portal
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6 bg-clip-text text-transparent bg-gradient-to-r from-primary to-secondary">
              Build with VeriGate
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Everything you need to integrate identity verification into your application. 
              Comprehensive APIs, SDKs for 9 languages, and world-class developer experience.
            </p>
            <div className="flex flex-wrap gap-4 justify-center">
              <Button size="lg" asChild>
                <Link to="/developers/api-reference">
                  <Book className="w-4 h-4 mr-2" />
                  API Documentation
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/developers/playground">
                  <Play className="w-4 h-4 mr-2" />
                  Try API Playground
                </Link>
              </Button>
              <Button size="lg" variant="ghost" asChild>
                <a href="https://github.com/verigate" target="_blank" rel="noopener noreferrer">
                  <Github className="w-4 h-4 mr-2" />
                  View on GitHub
                </a>
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Quick Stats */}
      <section className="py-8 border-b">
        <div className="container mx-auto">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 text-center">
            <div>
              <div className="text-3xl font-bold text-primary mb-1">99.9%</div>
              <div className="text-sm text-muted-foreground">API Uptime</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">&lt;100ms</div>
              <div className="text-sm text-muted-foreground">Avg Response</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">9</div>
              <div className="text-sm text-muted-foreground">SDK Languages</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">190+</div>
              <div className="text-sm text-muted-foreground">Countries</div>
            </div>
          </div>
        </div>
      </section>

      {/* Main Resources */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Developer Resources</h2>
            <p className="text-muted-foreground max-w-2xl">
              Everything you need to get started, from comprehensive documentation to interactive tools
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
            {resources.map((resource, index) => (
              <Card key={index} className="hover:shadow-lg transition-all hover:-translate-y-1">
                <CardHeader>
                  <div className="flex items-start justify-between mb-2">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <resource.icon className="w-6 h-6 text-primary" />
                    </div>
                    {resource.badge && (
                      <Badge variant="secondary" className="text-xs">
                        {resource.badge}
                      </Badge>
                    )}
                  </div>
                  <CardTitle className="text-xl">{resource.title}</CardTitle>
                  <CardDescription>{resource.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <Button variant="ghost" className="w-full justify-between" asChild>
                    <Link to={resource.link}>
                      View Resource
                      <ArrowRight className="w-4 h-4" />
                    </Link>
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Popular Endpoints */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8 text-center">Popular API Endpoints</h2>
            
            <Card>
              <CardContent className="p-0">
                <div className="divide-y">
                  {popularEndpoints.map((endpoint, index) => (
                    <div key={index} className="p-4 hover:bg-muted/50 transition-colors">
                      <div className="flex items-start gap-4">
                        <Badge 
                          variant={endpoint.method === "GET" ? "secondary" : "default"}
                          className="font-mono mt-1"
                        >
                          {endpoint.method}
                        </Badge>
                        <div className="flex-1">
                          <code className="text-sm font-mono text-primary">
                            {endpoint.endpoint}
                          </code>
                          <p className="text-sm text-muted-foreground mt-1">
                            {endpoint.description}
                          </p>
                        </div>
                        <Button variant="ghost" size="sm" asChild>
                          <Link to="/developers/api-reference">
                            <ExternalLink className="w-4 h-4" />
                          </Link>
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            <div className="text-center mt-6">
              <Button variant="outline" asChild>
                <Link to="/developers/api-reference">
                  View Complete API Reference
                  <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* SDKs Section */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Official SDKs</h2>
            <p className="text-muted-foreground max-w-2xl">
              Use our official SDKs to integrate VeriGate in your preferred programming language
            </p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4 max-w-4xl mx-auto mb-8">
            {sdkLanguages.map((lang, index) => (
              <Card key={index} className="text-center hover:shadow-md transition-shadow">
                <CardContent className="p-6">
                  <div className="text-4xl mb-3">{lang.icon}</div>
                  <h3 className="font-semibold mb-1">{lang.name}</h3>
                  <p className="text-xs text-muted-foreground mb-2">{lang.version}</p>
                  <div className="flex items-center justify-center text-xs text-muted-foreground">
                    <span className="mr-1">⭐</span>
                    {lang.stars}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="text-center">
            <Button asChild>
              <Link to="/developers/sdks">
                <Package className="w-4 h-4 mr-2" />
                View All SDKs & Installation Guides
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Quick Links */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8 text-center">Quick Links</h2>
            
            <div className="grid md:grid-cols-2 gap-4">
              {quickLinks.map((link, index) => (
                <Card key={index} className="hover:shadow-md transition-shadow">
                  <CardHeader className="pb-3">
                    <CardTitle className="text-lg flex items-center justify-between">
                      {link.title}
                      <ArrowRight className="w-4 h-4 text-muted-foreground" />
                    </CardTitle>
                    <CardDescription>{link.description}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <Button variant="ghost" size="sm" className="px-0" asChild>
                      <Link to={link.link}>Learn More</Link>
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Get Started CTA */}
      <section className="py-16">
        <div className="container mx-auto">
          <Card className="bg-gradient-to-br from-primary to-primary/80 text-primary-foreground">
            <CardContent className="p-12 text-center">
              <h2 className="text-3xl font-bold mb-4">Ready to Get Started?</h2>
              <p className="text-lg mb-8 text-primary-foreground/90 max-w-2xl mx-auto">
                Sign up for free API keys and start verifying identities in minutes
              </p>
              <div className="flex flex-wrap gap-4 justify-center">
                <Button size="lg" variant="secondary" asChild>
                  <Link to="/contact">
                    <Key className="w-4 h-4 mr-2" />
                    Get API Keys
                  </Link>
                </Button>
                <Button size="lg" variant="outline" className="bg-transparent border-primary-foreground text-primary-foreground hover:bg-primary-foreground/10" asChild>
                  <Link to="/developers/playground">
                    <Terminal className="w-4 h-4 mr-2" />
                    Try Playground
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

export default DeveloperOverview;
