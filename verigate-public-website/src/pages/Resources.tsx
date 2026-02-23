import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { 
  Download,
  FileText,
  BookOpen,
  BarChart3,
  Shield,
  Search,
  Star,
  TrendingUp,
  CheckCircle2,
  Lock
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";

const Resources = () => {
  const [searchQuery, setSearchQuery] = useState("");

  const whitepapers = [
    {
      id: 1,
      title: "Identity Verification Best Practices 2025",
      description: "A comprehensive guide to implementing secure and compliant identity verification in your platform.",
      pages: 24,
      downloads: "12.5k",
      category: "Best Practices",
      featured: true,
      topics: ["KYC", "Security", "Compliance", "Implementation"]
    },
    {
      id: 2,
      title: "AML Compliance Guide for Financial Institutions",
      description: "Everything you need to know about AML regulations, screening processes, and compliance requirements.",
      pages: 32,
      downloads: "8.3k",
      category: "Compliance",
      featured: true,
      topics: ["AML", "Banking", "Regulations", "Risk Management"]
    },
    {
      id: 3,
      title: "The ROI of Identity Verification Automation",
      description: "Calculate the business value and return on investment from automating your verification processes.",
      pages: 18,
      downloads: "6.7k",
      category: "Business Case",
      featured: false,
      topics: ["ROI", "Automation", "Cost Savings", "Efficiency"]
    },
    {
      id: 4,
      title: "Biometric Authentication: A Technical Deep Dive",
      description: "Explore the technology behind facial recognition, liveness detection, and biometric security.",
      pages: 28,
      downloads: "5.2k",
      category: "Technical",
      featured: false,
      topics: ["Biometrics", "AI", "Liveness", "Security"]
    },
    {
      id: 5,
      title: "GDPR & Privacy in Identity Verification",
      description: "Navigate GDPR requirements and implement privacy-first identity verification processes.",
      pages: 22,
      downloads: "9.1k",
      category: "Privacy",
      featured: false,
      topics: ["GDPR", "Privacy", "Data Protection", "Compliance"]
    }
  ];

  const ebooks = [
    {
      id: 1,
      title: "Complete KYC Implementation Guide",
      description: "Step-by-step guide to implementing KYC from scratch, including technical integration and compliance considerations.",
      pages: 45,
      chapters: 8,
      downloads: "15.2k",
      level: "Intermediate"
    },
    {
      id: 2,
      title: "Fraud Prevention Playbook",
      description: "Proven strategies and tactics for detecting and preventing identity fraud across different industries.",
      pages: 38,
      chapters: 6,
      downloads: "11.8k",
      level: "Advanced"
    },
    {
      id: 3,
      title: "Digital Identity for Developers",
      description: "A developer's guide to implementing secure digital identity solutions with code examples and best practices.",
      pages: 52,
      chapters: 10,
      downloads: "8.9k",
      level: "Intermediate"
    }
  ];

  const reports = [
    {
      id: 1,
      title: "State of Identity Verification 2025",
      description: "Annual industry report analyzing trends, adoption rates, and future predictions for identity verification.",
      year: 2025,
      pages: 64,
      downloads: "22.4k",
      featured: true
    },
    {
      id: 2,
      title: "Identity Fraud Trends Report Q4 2024",
      description: "Quarterly analysis of identity fraud patterns, attack vectors, and emerging threats.",
      year: 2024,
      pages: 28,
      downloads: "14.7k",
      featured: false
    },
    {
      id: 3,
      title: "Fintech Compliance Benchmark Study",
      description: "Industry benchmarking data on compliance costs, verification rates, and best practices from 500+ fintech companies.",
      year: 2024,
      pages: 42,
      downloads: "9.3k",
      featured: false
    }
  ];

  const guides = [
    {
      id: 1,
      title: "Getting Started with VeriGate",
      description: "Quick start guide for new users",
      type: "Quick Start",
      time: "15 min read"
    },
    {
      id: 2,
      title: "API Integration Checklist",
      description: "Complete checklist for API integration",
      type: "Checklist",
      time: "10 min read"
    },
    {
      id: 3,
      title: "Compliance Requirements by Country",
      description: "Country-specific compliance guide",
      type: "Reference",
      time: "Reference"
    },
    {
      id: 4,
      title: "Security Configuration Best Practices",
      description: "Security hardening guide",
      type: "Best Practices",
      time: "20 min read"
    }
  ];

  const templates = [
    {
      id: 1,
      title: "RFP Template for Identity Verification",
      description: "Ready-to-use RFP template",
      format: "DOCX"
    },
    {
      id: 2,
      title: "Vendor Comparison Spreadsheet",
      description: "Compare identity verification vendors",
      format: "XLSX"
    },
    {
      id: 3,
      title: "Compliance Audit Checklist",
      description: "Internal compliance audit template",
      format: "PDF"
    },
    {
      id: 4,
      title: "Implementation Timeline Template",
      description: "Project planning template",
      format: "XLSX"
    }
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-primary/10 to-secondary/5">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl text-center">
            <Badge className="mb-4" variant="outline">
              <BookOpen className="w-3 h-3 mr-1" />
              Resource Library
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Resources & Downloads
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Free whitepapers, ebooks, reports, and guides to help you implement 
              world-class identity verification and stay compliant.
            </p>
            
            {/* Search */}
            <div className="max-w-2xl mx-auto relative">
              <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
              <Input
                type="search"
                placeholder="Search resources..."
                className="pl-12 h-12 text-lg"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
          </div>
        </div>
      </section>

      {/* Stats */}
      <section className="py-8 border-b bg-background">
        <div className="container mx-auto">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 text-center max-w-4xl mx-auto">
            <div>
              <div className="text-3xl font-bold text-primary mb-1">25+</div>
              <div className="text-sm text-muted-foreground">Resources</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">150k+</div>
              <div className="text-sm text-muted-foreground">Downloads</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">100%</div>
              <div className="text-sm text-muted-foreground">Free</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">Weekly</div>
              <div className="text-sm text-muted-foreground">New Content</div>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Resources */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <div className="flex items-center gap-2 mb-8">
              <Star className="w-5 h-5 text-primary fill-primary" />
              <h2 className="text-3xl font-bold">Featured Resources</h2>
            </div>

            <div className="grid md:grid-cols-2 gap-6">
              {whitepapers.filter(w => w.featured).map((whitepaper) => (
                <Card key={whitepaper.id} className="hover:shadow-lg transition-shadow">
                  <CardHeader>
                    <div className="flex items-start justify-between mb-3">
                      <div className="p-3 bg-primary/10 rounded-lg">
                        <FileText className="w-8 h-8 text-primary" />
                      </div>
                      <Badge>Featured</Badge>
                    </div>
                    <CardTitle className="text-xl mb-2">{whitepaper.title}</CardTitle>
                    <CardDescription>{whitepaper.description}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="flex items-center gap-4 text-sm text-muted-foreground mb-4">
                      <div className="flex items-center gap-1">
                        <FileText className="w-4 h-4" />
                        {whitepaper.pages} pages
                      </div>
                      <div className="flex items-center gap-1">
                        <Download className="w-4 h-4" />
                        {whitepaper.downloads} downloads
                      </div>
                    </div>
                    <div className="flex flex-wrap gap-2 mb-4">
                      {whitepaper.topics.map((topic, idx) => (
                        <Badge key={idx} variant="outline" className="text-xs">
                          {topic}
                        </Badge>
                      ))}
                    </div>
                    <Button className="w-full">
                      <Download className="w-4 h-4 mr-2" />
                      Download Whitepaper
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Resource Tabs */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <Tabs defaultValue="whitepapers" className="w-full">
              <TabsList className="grid w-full grid-cols-5">
                <TabsTrigger value="whitepapers">Whitepapers</TabsTrigger>
                <TabsTrigger value="ebooks">eBooks</TabsTrigger>
                <TabsTrigger value="reports">Reports</TabsTrigger>
                <TabsTrigger value="guides">Guides</TabsTrigger>
                <TabsTrigger value="templates">Templates</TabsTrigger>
              </TabsList>

              {/* Whitepapers Tab */}
              <TabsContent value="whitepapers" className="mt-8">
                <div className="grid md:grid-cols-2 gap-6">
                  {whitepapers.map((whitepaper) => (
                    <Card key={whitepaper.id}>
                      <CardHeader>
                        <div className="flex items-center gap-3 mb-3">
                          <div className="p-2 bg-primary/10 rounded">
                            <FileText className="w-6 h-6 text-primary" />
                          </div>
                          <Badge variant="secondary">{whitepaper.category}</Badge>
                        </div>
                        <CardTitle className="text-lg">{whitepaper.title}</CardTitle>
                        <CardDescription>{whitepaper.description}</CardDescription>
                      </CardHeader>
                      <CardContent>
                        <div className="flex items-center gap-4 text-sm text-muted-foreground mb-4">
                          <span>{whitepaper.pages} pages</span>
                          <span>•</span>
                          <span>{whitepaper.downloads} downloads</span>
                        </div>
                        <Button variant="outline" className="w-full">
                          <Download className="w-4 h-4 mr-2" />
                          Download PDF
                        </Button>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </TabsContent>

              {/* eBooks Tab */}
              <TabsContent value="ebooks" className="mt-8">
                <div className="grid md:grid-cols-3 gap-6">
                  {ebooks.map((ebook) => (
                    <Card key={ebook.id}>
                      <CardHeader>
                        <div className="p-3 bg-primary/10 rounded-lg mb-3 w-fit">
                          <BookOpen className="w-8 h-8 text-primary" />
                        </div>
                        <CardTitle className="text-lg mb-2">{ebook.title}</CardTitle>
                        <CardDescription>{ebook.description}</CardDescription>
                      </CardHeader>
                      <CardContent>
                        <div className="space-y-2 text-sm text-muted-foreground mb-4">
                          <div className="flex items-center justify-between">
                            <span>Pages:</span>
                            <span className="font-medium">{ebook.pages}</span>
                          </div>
                          <div className="flex items-center justify-between">
                            <span>Chapters:</span>
                            <span className="font-medium">{ebook.chapters}</span>
                          </div>
                          <div className="flex items-center justify-between">
                            <span>Level:</span>
                            <Badge variant="outline" className="text-xs">{ebook.level}</Badge>
                          </div>
                        </div>
                        <Button className="w-full">
                          <Download className="w-4 h-4 mr-2" />
                          Download eBook
                        </Button>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </TabsContent>

              {/* Reports Tab */}
              <TabsContent value="reports" className="mt-8">
                <div className="grid md:grid-cols-2 gap-6">
                  {reports.map((report) => (
                    <Card key={report.id}>
                      <CardHeader>
                        <div className="flex items-center gap-3 mb-3">
                          <div className="p-2 bg-primary/10 rounded">
                            <BarChart3 className="w-6 h-6 text-primary" />
                          </div>
                          {report.featured && <Badge>Featured</Badge>}
                        </div>
                        <CardTitle className="text-lg">{report.title}</CardTitle>
                        <CardDescription>{report.description}</CardDescription>
                      </CardHeader>
                      <CardContent>
                        <div className="flex items-center gap-4 text-sm text-muted-foreground mb-4">
                          <span>{report.year}</span>
                          <span>•</span>
                          <span>{report.pages} pages</span>
                          <span>•</span>
                          <span>{report.downloads} downloads</span>
                        </div>
                        <Button variant="outline" className="w-full">
                          <Download className="w-4 h-4 mr-2" />
                          Download Report
                        </Button>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </TabsContent>

              {/* Guides Tab */}
              <TabsContent value="guides" className="mt-8">
                <div className="grid md:grid-cols-2 gap-6">
                  {guides.map((guide) => (
                    <Card key={guide.id}>
                      <CardHeader>
                        <div className="flex items-center gap-3 mb-2">
                          <CheckCircle2 className="w-5 h-5 text-primary" />
                          <Badge variant="secondary">{guide.type}</Badge>
                        </div>
                        <CardTitle className="text-lg">{guide.title}</CardTitle>
                        <CardDescription>{guide.description}</CardDescription>
                      </CardHeader>
                      <CardContent>
                        <div className="flex items-center justify-between">
                          <span className="text-sm text-muted-foreground">{guide.time}</span>
                          <Button variant="outline" size="sm">
                            <Download className="w-3 h-3 mr-2" />
                            Download
                          </Button>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </TabsContent>

              {/* Templates Tab */}
              <TabsContent value="templates" className="mt-8">
                <div className="grid md:grid-cols-2 gap-6">
                  {templates.map((template) => (
                    <Card key={template.id}>
                      <CardHeader>
                        <div className="flex items-center gap-3 mb-2">
                          <FileText className="w-5 h-5 text-primary" />
                          <Badge variant="outline">{template.format}</Badge>
                        </div>
                        <CardTitle className="text-lg">{template.title}</CardTitle>
                        <CardDescription>{template.description}</CardDescription>
                      </CardHeader>
                      <CardContent>
                        <Button variant="outline" className="w-full">
                          <Download className="w-4 h-4 mr-2" />
                          Download Template
                        </Button>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </TabsContent>
            </Tabs>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16">
        <div className="container mx-auto">
          <Card className="container mx-auto max-w-6xl bg-gradient-to-br from-primary to-primary/80 text-primary-foreground">
            <CardContent className="p-12 text-center">
              <div className="max-w-2xl mx-auto">
                <h2 className="text-3xl font-bold mb-4">Can't Find What You Need?</h2>
                <p className="text-lg mb-8 text-primary-foreground/90">
                  Our team is here to help. Contact us for custom resources, consultations, 
                  or specific industry guidance.
                </p>
                <div className="flex flex-wrap gap-4 justify-center">
                  <Button size="lg" variant="secondary" asChild>
                    <Link to="/contact">
                      Contact Us
                    </Link>
                  </Button>
                  <Button 
                    size="lg" 
                    variant="outline" 
                    className="bg-transparent border-primary-foreground text-primary-foreground hover:bg-primary-foreground/10"
                    asChild
                  >
                    <Link to="/help">
                      Visit Help Center
                    </Link>
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Resources;
