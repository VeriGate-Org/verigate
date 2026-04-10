import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import {
  Search,
  Sparkles,
  CircleDot,
  ArrowRightCircle,
  Star,
  Code,
  Zap,
  Users,
  BarChart3,
  MessageSquare,
  Database,
  Lock,
  ArrowRight,
  Globe,
  Webhook
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";

const bulletIcons = [Sparkles, CircleDot, ArrowRightCircle, Star];

const Integrations = () => {
  const [searchQuery, setSearchQuery] = useState("");

  const integrationCategories = [
    {
      category: "HRIS & ATS Platforms",
      icon: Users,
      description: "Integrate verification directly into your HR and recruitment workflows",
      integrations: [
        { name: "Zoho People", logo: "/logos/integrations/zoho.svg", description: "Trigger verifications from Zoho People recruitment pipeline", popular: true },
        { name: "SAP SuccessFactors", logo: "/logos/integrations/sap.svg", description: "Enterprise HRIS integration with automated screening", popular: true },
        { name: "Sage HR", logo: "/logos/integrations/sage.svg", description: "SA-focused HR platform integration for employee onboarding" },
        { name: "BambooHR", logo: "/logos/integrations/bamboohr.svg", description: "Automated background checks from BambooHR candidate profiles" },
        { name: "PaySpace", logo: "/logos/integrations/payspace.svg", description: "South African payroll and HR integration" },
      ],
    },
    {
      category: "REST API",
      icon: Code,
      description: "Build custom integrations with our comprehensive REST API",
      integrations: [
        { name: "REST API v2", description: "Full-featured API with OAuth 2.0 authentication and webhook support", popular: true },
        { name: "Webhooks", description: "Real-time notifications for verification status changes", popular: true },
        { name: "Batch API", description: "Submit and process bulk verifications programmatically" },
        { name: "SDKs", description: "Client libraries for JavaScript, Python, C#, Java, and PHP" },
      ],
    },
    {
      category: "CRM Platforms",
      icon: Database,
      description: "Sync verification data with your customer relationship management system",
      integrations: [
        { name: "Zoho CRM", logo: "/logos/integrations/zoho.svg", description: "Two-way sync with verification status updates", popular: true },
        { name: "Salesforce", logo: "/logos/integrations/salesforce.svg", description: "Custom object integration for compliance tracking" },
        { name: "HubSpot", logo: "/logos/integrations/hubspot.svg", description: "Contact and deal enrichment with verification data" },
        { name: "Microsoft Dynamics", logo: "/logos/integrations/microsoft.svg", description: "Enterprise CRM integration with custom fields" },
      ],
    },
    {
      category: "Automation & Workflow",
      icon: Zap,
      description: "Automate verification workflows with popular automation tools",
      integrations: [
        { name: "Zapier", logo: "/logos/integrations/zapier.svg", description: "Connect VeriGate to 5,000+ apps with no-code automation", popular: true },
        { name: "Make (Integromat)", logo: "/logos/integrations/make.svg", description: "Visual workflow automation for complex processes" },
        { name: "Microsoft Power Automate", logo: "/logos/integrations/microsoft.svg", description: "Enterprise workflow automation" },
      ],
    },
    {
      category: "Communication",
      icon: MessageSquare,
      description: "Get verification updates in your team communication tools",
      integrations: [
        { name: "Slack", logo: "/logos/integrations/slack.svg", description: "Real-time verification notifications in Slack channels", popular: true },
        { name: "Microsoft Teams", logo: "/logos/integrations/microsoft.svg", description: "Verification alerts and status updates in Teams" },
        { name: "Email (SMTP)", description: "Automated email notifications for completed verifications" },
      ],
    },
    {
      category: "Analytics & Reporting",
      icon: BarChart3,
      description: "Export verification data to your analytics and BI tools",
      integrations: [
        { name: "Power BI", logo: "/logos/integrations/power-bi.svg", description: "Verification dashboards and analytics reporting" },
        { name: "Google Sheets", logo: "/logos/integrations/google.svg", description: "Automatic export of verification results to spreadsheets" },
        { name: "CSV/PDF Export", description: "Download verification reports in standard formats" },
      ],
    },
  ];

  const apiFeatures = [
    { title: "OAuth 2.0 Authentication", description: "Secure token-based API access with refresh tokens" },
    { title: "Webhook Notifications", description: "Real-time callbacks for verification status changes" },
    { title: "Rate Limiting", description: "100 req/s for Professional, custom limits for Enterprise" },
    { title: "SDKs & Libraries", description: "Official SDKs for JavaScript, Python, C#, Java, PHP" },
  ];

  const codeExample = `// Submit a verification request
const response = await fetch('https://api.verigate.co.za/v2/verifications', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer YOUR_API_KEY',
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    candidate: {
      firstName: 'John',
      lastName: 'Smith',
      idNumber: '8501015800089',
    },
    checks: ['criminal', 'identity', 'qualification'],
    consentRef: 'CONSENT-2026-001',
  }),
});

// Response
{
  "id": "ver_abc123",
  "status": "processing",
  "checks": [
    { "type": "criminal", "status": "pending", "eta": "3-5 days" },
    { "type": "identity", "status": "pending", "eta": "instant" },
    { "type": "qualification", "status": "pending", "eta": "2-5 days" }
  ]
}`;

  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-background to-primary/10">
        <div className="container mx-auto max-w-6xl text-center">
          <Badge className="mb-4" variant="secondary">
            Integrations & API
          </Badge>
          <h1 className="text-4xl md:text-5xl font-bold mb-6">
            Integrations & API
          </h1>
          <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
            Connect VeriGate with your HRIS, ATS, CRM, and custom systems. Our REST API and pre-built integrations make it easy to embed verification into any workflow.
          </p>

          {/* Search Bar */}
          <div className="relative max-w-2xl mx-auto">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
            <Input
              type="text"
              placeholder="Search integrations..."
              className="pl-12 pr-4 py-6 text-lg"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
        </div>
      </section>

      {/* API Code Example */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="grid md:grid-cols-2 gap-8 items-start">
            <div>
              <h2 className="text-3xl font-bold mb-4">Powerful REST API</h2>
              <p className="text-lg text-muted-foreground mb-6">
                Submit verification requests, receive real-time webhook notifications, and download reports — all via our RESTful API.
              </p>
              <div className="space-y-4">
                {apiFeatures.map((feature, idx) => {
                  const BulletIcon = bulletIcons[idx % bulletIcons.length];
                  return (
                    <div key={feature.title} className="flex items-start gap-3">
                      <BulletIcon className="w-5 h-5 text-accent flex-shrink-0 mt-0.5" />
                      <div>
                        <p className="font-medium">{feature.title}</p>
                        <p className="text-sm text-muted-foreground">{feature.description}</p>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
            <div className="bg-primary rounded-lg p-6 overflow-x-auto">
              <pre className="text-sm text-primary-foreground font-mono whitespace-pre">
                {codeExample}
              </pre>
            </div>
          </div>
        </div>
      </section>

      {/* Integration Categories */}
      <section className="py-16 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Pre-built Integrations</h2>
            <p className="text-lg text-muted-foreground">
              Connect VeriGate with your existing tools in minutes
            </p>
          </div>

          <div className="space-y-12">
            {integrationCategories.map((category, catIndex) => (
              <div key={catIndex}>
                <div className="flex items-center gap-3 mb-6">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <category.icon className="w-6 h-6 text-primary" />
                  </div>
                  <div>
                    <h3 className="text-2xl font-bold">{category.category}</h3>
                    <p className="text-sm text-muted-foreground">{category.description}</p>
                  </div>
                </div>

                <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {category.integrations.map((integration, intIndex) => (
                    <Card key={intIndex} className="hover:shadow-lg transition-shadow">
                      <CardHeader>
                        <div className="flex items-start gap-3">
                          {integration.logo ? (
                            <img
                              src={integration.logo}
                              alt={integration.name}
                              className="w-10 h-10 object-contain flex-shrink-0"
                            />
                          ) : (
                            <div className="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center flex-shrink-0">
                              <category.icon className="w-5 h-5 text-primary" />
                            </div>
                          )}
                          <div className="flex-1 min-w-0">
                            <div className="flex items-start justify-between gap-2">
                              <CardTitle className="text-lg">{integration.name}</CardTitle>
                              {integration.popular && (
                                <Badge variant="default" className="text-xs flex-shrink-0">Popular</Badge>
                              )}
                            </div>
                            <CardDescription className="text-sm mt-1">{integration.description}</CardDescription>
                          </div>
                        </div>
                      </CardHeader>
                    </Card>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Custom Integration CTA */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="grid md:grid-cols-2 gap-8">
            <Card>
              <CardHeader>
                <CardTitle>Custom Integration</CardTitle>
                <CardDescription>
                  Need to integrate with a platform not listed here? Our team can build custom integrations for Enterprise clients.
                </CardDescription>
              </CardHeader>
              <CardContent>
                <ul className="space-y-2 mb-4">
                  {["Custom API endpoints", "ERP and HRIS connectors", "Dedicated technical support", "SLA guarantees"].map((item, idx) => {
                    const BulletIcon = bulletIcons[idx % bulletIcons.length];
                    return (
                      <li key={item} className="flex items-start gap-2 text-sm">
                        <BulletIcon className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{item}</span>
                      </li>
                    );
                  })}
                </ul>
                <Button className="w-full" asChild>
                  <Link to="/contact">Request Custom Integration</Link>
                </Button>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Become a Technology Partner</CardTitle>
                <CardDescription>
                  Join our partner programme and integrate VeriGate into your platform.
                </CardDescription>
              </CardHeader>
              <CardContent>
                <ul className="space-y-2 mb-4">
                  {["Co-marketing opportunities", "Revenue sharing", "Technical integration support", "Partner portal access"].map((item, idx) => {
                    const BulletIcon = bulletIcons[idx % bulletIcons.length];
                    return (
                      <li key={item} className="flex items-start gap-2 text-sm">
                        <BulletIcon className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{item}</span>
                      </li>
                    );
                  })}
                </ul>
                <Button className="w-full" variant="outline" asChild>
                  <Link to="/partner-program">Learn About Partnership</Link>
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Integrations;
