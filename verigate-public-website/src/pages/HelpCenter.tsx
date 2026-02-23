import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { 
  Book,
  Code,
  Shield,
  Users,
  CreditCard,
  Settings,
  HelpCircle,
  FileText,
  Search,
  ArrowRight,
  CheckCircle2,
  MessageSquare,
  Video,
  Download
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";

const HelpCenter = () => {
  const [searchQuery, setSearchQuery] = useState("");

  const categories = [
    {
      icon: Code,
      title: "Developer Documentation",
      description: "API reference, SDKs, webhooks, and integration guides",
      articles: 45,
      link: "/developers",
      popular: ["API Authentication", "Webhooks Setup", "Error Handling", "Rate Limits"],
    },
    {
      icon: Shield,
      title: "Security & Compliance",
      description: "Data protection, certifications, and regulatory compliance",
      articles: 28,
      link: "/security",
      popular: ["GDPR Compliance", "Data Encryption", "SOC 2 Certification", "HIPAA Requirements"],
    },
    {
      icon: Users,
      title: "Account Management",
      description: "Account setup, billing, user management, and settings",
      articles: 32,
      link: "/help/account",
      popular: ["Create Account", "Add Team Members", "Billing & Invoices", "API Keys"],
    },
    {
      icon: CreditCard,
      title: "Pricing & Billing",
      description: "Plans, pricing, payment methods, and invoicing",
      articles: 18,
      link: "/help/billing",
      popular: ["Pricing Plans", "Payment Methods", "Usage Tracking", "Refund Policy"],
    },
    {
      icon: Settings,
      title: "Platform Setup",
      description: "Configuration, customization, and optimization guides",
      articles: 38,
      link: "/help/setup",
      popular: ["Initial Setup", "Webhooks Config", "Custom Branding", "Test Mode"],
    },
    {
      icon: HelpCircle,
      title: "Troubleshooting",
      description: "Common issues, error codes, and solutions",
      articles: 42,
      link: "/help/troubleshooting",
      popular: ["Failed Verifications", "Webhook Errors", "API Timeouts", "Document Issues"],
    },
  ];

  const quickLinks = [
    {
      title: "Getting Started Guide",
      description: "Complete walkthrough from signup to first verification",
      icon: Book,
      link: "/help/getting-started",
      duration: "10 min read",
    },
    {
      title: "API Quickstart",
      description: "Make your first API call in under 5 minutes",
      icon: Code,
      link: "/developers/quickstart",
      duration: "5 min",
    },
    {
      title: "Video Tutorials",
      description: "Watch step-by-step video guides",
      icon: Video,
      link: "/help/videos",
      duration: "15+ videos",
    },
    {
      title: "Sample Code",
      description: "Copy-paste examples in multiple languages",
      icon: Download,
      link: "/developers/examples",
      duration: "9 languages",
    },
  ];

  const popularArticles = [
    { title: "How to integrate VeriGate in 5 minutes", views: "12.5K", category: "Getting Started" },
    { title: "Understanding verification statuses", views: "8.2K", category: "Troubleshooting" },
    { title: "Setting up webhooks for real-time updates", views: "7.8K", category: "Developer" },
    { title: "KYC vs AML: What's the difference?", views: "6.9K", category: "Compliance" },
    { title: "Pricing and billing explained", views: "6.1K", category: "Billing" },
    { title: "How to handle failed verifications", views: "5.7K", category: "Troubleshooting" },
    { title: "GDPR compliance checklist", views: "5.3K", category: "Compliance" },
    { title: "Testing your integration", views: "4.8K", category: "Developer" },
  ];

  const supportChannels = [
    {
      channel: "Live Chat",
      description: "Chat with our support team",
      availability: "24/7",
      responseTime: "< 2 minutes",
      icon: MessageSquare,
      action: "Start Chat",
    },
    {
      channel: "Email Support",
      description: "support@verigate.com",
      availability: "24/7",
      responseTime: "< 4 hours",
      icon: FileText,
      action: "Send Email",
    },
    {
      channel: "Developer Forum",
      description: "Community-driven support",
      availability: "24/7",
      responseTime: "Community-based",
      icon: Users,
      action: "Visit Forum",
    },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section with Search */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-background to-primary/10">
        <div className="container mx-auto max-w-6xl text-center">
          <h1 className="text-4xl md:text-5xl font-bold mb-6">
            How can we help you?
          </h1>
          <p className="text-xl text-muted-foreground mb-8">
            Search our knowledge base or browse categories below
          </p>
          
          {/* Search Bar */}
          <div className="relative max-w-2xl mx-auto">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
            <Input
              type="text"
              placeholder="Search for articles, guides, and documentation..."
              className="pl-12 pr-4 py-6 text-lg"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          <div className="mt-4 flex flex-wrap gap-2 justify-center">
            <Badge variant="outline" className="cursor-pointer hover:bg-primary hover:text-primary-foreground">
              API Integration
            </Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-primary hover:text-primary-foreground">
              KYC Setup
            </Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-primary hover:text-primary-foreground">
              Webhooks
            </Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-primary hover:text-primary-foreground">
              Pricing
            </Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-primary hover:text-primary-foreground">
              GDPR
            </Badge>
          </div>
        </div>
      </section>

      {/* Quick Links */}
      <section className="py-12">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-8">
            <h2 className="text-2xl font-bold mb-2">Quick Start Guides</h2>
            <p className="text-muted-foreground">Get up and running quickly</p>
          </div>

          <div className="grid md:grid-cols-4 gap-6">
            {quickLinks.map((link, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow cursor-pointer" asChild>
                <Link to={link.link}>
                  <CardHeader>
                    <link.icon className="w-8 h-8 text-primary mb-2" />
                    <CardTitle className="text-lg">{link.title}</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <CardDescription className="mb-2">{link.description}</CardDescription>
                    <Badge variant="secondary" className="text-xs">{link.duration}</Badge>
                  </CardContent>
                </Link>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Categories */}
      <section className="py-12 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-8">
            <h2 className="text-2xl font-bold mb-2">Browse by Category</h2>
            <p className="text-muted-foreground">Find answers organized by topic</p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {categories.map((category, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow" asChild>
                <Link to={category.link}>
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="p-2 bg-primary/10 rounded-lg">
                        <category.icon className="w-6 h-6 text-primary" />
                      </div>
                      <Badge variant="secondary">{category.articles} articles</Badge>
                    </div>
                    <CardTitle className="mt-4">{category.title}</CardTitle>
                    <CardDescription>{category.description}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="text-sm text-muted-foreground">
                      <div className="font-medium mb-2">Popular:</div>
                      <ul className="space-y-1">
                        {category.popular.slice(0, 3).map((article, idx) => (
                          <li key={idx} className="flex items-start gap-2">
                            <CheckCircle2 className="w-3 h-3 text-primary mt-1 flex-shrink-0" />
                            <span className="text-xs">{article}</span>
                          </li>
                        ))}
                      </ul>
                    </div>
                  </CardContent>
                </Link>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Popular Articles */}
      <section className="py-12">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-8">
            <h2 className="text-2xl font-bold mb-2">Most Popular Articles</h2>
            <p className="text-muted-foreground">Our most-read documentation</p>
          </div>

          <div className="grid md:grid-cols-2 gap-4 max-w-4xl mx-auto">
            {popularArticles.map((article, index) => (
              <Card key={index} className="hover:shadow-md transition-shadow cursor-pointer">
                <CardContent className="p-4">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <h3 className="font-semibold mb-1 hover:text-primary transition-colors">
                        {article.title}
                      </h3>
                      <div className="flex items-center gap-2 text-xs text-muted-foreground">
                        <Badge variant="outline" className="text-xs">{article.category}</Badge>
                        <span>•</span>
                        <span>{article.views} views</span>
                      </div>
                    </div>
                    <ArrowRight className="w-4 h-4 text-muted-foreground ml-2 flex-shrink-0" />
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Support Channels */}
      <section className="py-12 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-8">
            <h2 className="text-2xl font-bold mb-2">Still Need Help?</h2>
            <p className="text-muted-foreground">Contact our support team</p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {supportChannels.map((channel, index) => (
              <Card key={index}>
                <CardHeader>
                  <div className="flex items-center gap-3 mb-2">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <channel.icon className="w-6 h-6 text-primary" />
                    </div>
                    <CardTitle className="text-lg">{channel.channel}</CardTitle>
                  </div>
                  <CardDescription>{channel.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2 mb-4 text-sm">
                    <div className="flex justify-between">
                      <span className="text-muted-foreground">Availability:</span>
                      <span className="font-medium">{channel.availability}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-muted-foreground">Response Time:</span>
                      <span className="font-medium">{channel.responseTime}</span>
                    </div>
                  </div>
                  <Button className="w-full" variant={index === 0 ? "default" : "outline"}>
                    {channel.action}
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl text-center">
          <h2 className="text-3xl font-bold mb-4">
            Can't Find What You're Looking For?
          </h2>
          <p className="text-xl text-muted-foreground mb-8">
            Our support team is here to help 24/7
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" asChild>
              <Link to="/contact">Contact Support</Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link to="/faq">View FAQ</Link>
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default HelpCenter;
