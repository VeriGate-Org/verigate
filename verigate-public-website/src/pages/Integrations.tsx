import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { 
  Search,
  CheckCircle2,
  Code,
  Zap,
  DollarSign,
  Users,
  BarChart3,
  ShoppingCart,
  Mail,
  MessageSquare,
  Database,
  Cloud,
  Lock,
  Smartphone,
  ArrowRight
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";

const Integrations = () => {
  const [searchQuery, setSearchQuery] = useState("");

  const integrationCategories = [
    {
      category: "CRM Platforms",
      icon: Users,
      description: "Sync verification data with your customer relationship management system",
      integrations: [
        { name: "HubSpot", description: "Automatic contact and lead creation with verification data", popular: true },
        { name: "Salesforce", description: "Web-to-Lead and API integration for seamless CRM sync", popular: true },
        { name: "Pipedrive", description: "Create deals and contacts from verified users" },
        { name: "Zoho CRM", description: "Two-way sync with verification status updates" },
        { name: "Microsoft Dynamics", description: "Enterprise CRM integration with custom fields" },
      ],
    },
    {
      category: "Payment Processors",
      icon: CreditCard,
      description: "Verify customers before payment processing",
      integrations: [
        { name: "Stripe", description: "Add verification before accepting payments", popular: true },
        { name: "PayPal", description: "Verify users for merchant and marketplace accounts" },
        { name: "Square", description: "Point-of-sale verification integration" },
        { name: "Braintree", description: "Payment gateway with identity verification" },
        { name: "Adyen", description: "Global payment processing with KYC" },
      ],
    },
    {
      category: "E-commerce Platforms",
      icon: ShoppingCart,
      description: "Add verification to your online store",
      integrations: [
        { name: "Shopify", description: "One-click app installation for age and identity verification", popular: true },
        { name: "WooCommerce", description: "WordPress plugin for seamless checkout verification" },
        { name: "Magento", description: "Extension for enterprise e-commerce platforms" },
        { name: "BigCommerce", description: "Native integration for online stores" },
        { name: "PrestaShop", description: "Module for European e-commerce sites" },
      ],
    },
    {
      category: "Authentication & Identity",
      icon: Lock,
      description: "Enhance authentication systems with identity verification",
      integrations: [
        { name: "Auth0", description: "Add verification step to user authentication flow", popular: true },
        { name: "Okta", description: "Enterprise identity management integration" },
        { name: "Firebase Auth", description: "Mobile authentication with verification" },
        { name: "AWS Cognito", description: "Cloud-based user pool verification" },
        { name: "Azure AD", description: "Microsoft Active Directory integration" },
      ],
    },
    {
      category: "Analytics & Data",
      icon: BarChart3,
      description: "Track and analyze verification metrics",
      integrations: [
        { name: "Google Analytics", description: "Track verification events and conversions", popular: true },
        { name: "Segment", description: "Customer data platform integration" },
        { name: "Mixpanel", description: "Product analytics with verification data" },
        { name: "Amplitude", description: "Behavioral analytics and cohort analysis" },
        { name: "Heap", description: "Automatic event tracking for verifications" },
      ],
    },
    {
      category: "Marketing Automation",
      icon: Mail,
      description: "Automate marketing based on verification status",
      integrations: [
        { name: "Mailchimp", description: "Email marketing with verification triggers" },
        { name: "SendGrid", description: "Transactional emails for verification events" },
        { name: "Klaviyo", description: "E-commerce marketing automation" },
        { name: "ActiveCampaign", description: "CRM and email marketing integration" },
        { name: "Intercom", description: "Customer messaging with verification context" },
      ],
    },
    {
      category: "Customer Support",
      icon: MessageSquare,
      description: "Enhance support with verification context",
      integrations: [
        { name: "Zendesk", description: "Support tickets with verification history" },
        { name: "Freshdesk", description: "Customer support with identity context" },
        { name: "Help Scout", description: "Help desk with verification data" },
        { name: "Front", description: "Shared inbox with customer verification info" },
        { name: "Crisp", description: "Live chat with identity verification" },
      ],
    },
    {
      category: "Development Tools",
      icon: Code,
      description: "Integrate VeriGate into your development workflow",
      integrations: [
        { name: "GitHub", description: "Webhook integration for development workflows" },
        { name: "GitLab", description: "CI/CD integration for verification testing" },
        { name: "Jira", description: "Project management with verification tracking" },
        { name: "Slack", description: "Real-time verification notifications", popular: true },
        { name: "Discord", description: "Community verification and notifications" },
      ],
    },
    {
      category: "Cloud Infrastructure",
      icon: Cloud,
      description: "Deploy VeriGate on your cloud platform",
      integrations: [
        { name: "AWS", description: "Native integration with AWS services", popular: true },
        { name: "Google Cloud", description: "GCP deployment and integration" },
        { name: "Microsoft Azure", description: "Azure cloud services integration" },
        { name: "Heroku", description: "Easy deployment for web applications" },
        { name: "DigitalOcean", description: "Simple cloud infrastructure integration" },
      ],
    },
    {
      category: "Mobile Development",
      icon: Smartphone,
      description: "SDKs for native mobile applications",
      integrations: [
        { name: "iOS SDK (Swift)", description: "Native iOS integration with biometric support", popular: true },
        { name: "Android SDK (Kotlin)", description: "Native Android verification SDK", popular: true },
        { name: "React Native", description: "Cross-platform mobile development" },
        { name: "Flutter", description: "Dart-based mobile framework support" },
        { name: "Ionic", description: "Hybrid mobile app framework" },
      ],
    },
    {
      category: "Database & Storage",
      icon: Database,
      description: "Store and sync verification data",
      integrations: [
        { name: "MongoDB", description: "NoSQL database integration" },
        { name: "PostgreSQL", description: "Relational database connector" },
        { name: "MySQL", description: "Popular SQL database integration" },
        { name: "Redis", description: "Cache verification results" },
        { name: "Elasticsearch", description: "Search and analytics for verification data" },
      ],
    },
    {
      category: "Automation & Workflow",
      icon: Zap,
      description: "Automate verification workflows",
      integrations: [
        { name: "Zapier", description: "Connect VeriGate to 5,000+ apps", popular: true },
        { name: "Make (Integromat)", description: "Visual workflow automation" },
        { name: "n8n", description: "Open-source workflow automation" },
        { name: "Workato", description: "Enterprise integration platform" },
        { name: "Tray.io", description: "General automation platform" },
      ],
    },
  ];

  const stats = [
    { label: "Total Integrations", value: "100+", icon: CheckCircle2 },
    { label: "API Calls/Day", value: "10M+", icon: Zap },
    { label: "Platforms Supported", value: "50+", icon: Code },
    { label: "Active Customers", value: "500+", icon: Users },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-background to-primary/10">
        <div className="container mx-auto max-w-6xl text-center">
          <Badge className="mb-4" variant="secondary">
            100+ Integrations
          </Badge>
          <h1 className="text-4xl md:text-5xl font-bold mb-6">
            Integrations & Partnerships
          </h1>
          <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
            Connect VeriGate with your favorite tools and platforms. Seamless integration 
            with CRMs, payment processors, analytics, and more.
          </p>

          {/* Search Bar */}
          <div className="relative max-w-2xl mx-auto mb-8">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
            <Input
              type="text"
              placeholder="Search integrations..."
              className="pl-12 pr-4 py-6 text-lg"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          {/* Stats */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 max-w-4xl mx-auto">
            {stats.map((stat, index) => (
              <Card key={index}>
                <CardContent className="pt-6 text-center">
                  <stat.icon className="w-6 h-6 text-primary mx-auto mb-2" />
                  <div className="text-2xl font-bold text-primary mb-1">{stat.value}</div>
                  <div className="text-xs text-muted-foreground">{stat.label}</div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Integration Categories */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="space-y-12">
            {integrationCategories.map((category, catIndex) => (
              <div key={catIndex}>
                <div className="flex items-center gap-3 mb-6">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <category.icon className="w-6 h-6 text-primary" />
                  </div>
                  <div>
                    <h2 className="text-2xl font-bold">{category.category}</h2>
                    <p className="text-sm text-muted-foreground">{category.description}</p>
                  </div>
                </div>

                <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {category.integrations.map((integration, intIndex) => (
                    <Card key={intIndex} className="hover:shadow-lg transition-shadow">
                      <CardHeader>
                        <div className="flex items-start justify-between">
                          <CardTitle className="text-lg">{integration.name}</CardTitle>
                          {integration.popular && (
                            <Badge variant="default" className="text-xs">Popular</Badge>
                          )}
                        </div>
                        <CardDescription className="text-sm">{integration.description}</CardDescription>
                      </CardHeader>
                      <CardContent>
                        <Button variant="outline" size="sm" className="w-full">
                          View Integration
                        </Button>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Custom Integration */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Don't See Your Platform?</h2>
            <p className="text-lg text-muted-foreground">
              We can build custom integrations for enterprise customers
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            <Card>
              <CardHeader>
                <CardTitle>Custom Integration Development</CardTitle>
                <CardDescription>
                  Our team can build custom integrations for your specific needs
                </CardDescription>
              </CardHeader>
              <CardContent>
                <ul className="space-y-2 mb-4">
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Custom API endpoints and webhooks</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>White-label solutions</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Dedicated technical support</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>SLA guarantees</span>
                  </li>
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
                  Join our partner program and integrate VeriGate into your platform
                </CardDescription>
              </CardHeader>
              <CardContent>
                <ul className="space-y-2 mb-4">
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Co-marketing opportunities</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Revenue sharing agreements</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Technical integration support</span>
                  </li>
                  <li className="flex items-start gap-2 text-sm">
                    <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                    <span>Partner portal access</span>
                  </li>
                </ul>
                <Button className="w-full" variant="outline" asChild>
                  <Link to="/partners">Learn About Partnership</Link>
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl text-center">
          <h2 className="text-3xl font-bold mb-4">
            Ready to Get Started?
          </h2>
          <p className="text-xl text-muted-foreground mb-8">
            Integrate VeriGate with your platform in minutes
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" asChild>
              <Link to="/contact">Start Integration</Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link to="/developers">View API Docs</Link>
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Integrations;
