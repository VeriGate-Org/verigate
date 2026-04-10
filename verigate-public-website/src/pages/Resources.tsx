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
  Search,
  Star,
  CircleDot,
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";

const Resources = () => {
  const [searchQuery, setSearchQuery] = useState("");

  const whitepapers = [
    {
      id: 1,
      title: "Background Screening Best Practices for South African Employers",
      description: "A comprehensive guide to implementing compliant background screening processes aligned with POPIA, FICA, and the Labour Relations Act.",
      pages: 28,
      downloads: "8.2k",
      category: "Best Practices",
      featured: true,
      topics: ["POPIA", "FICA", "Compliance", "HR Best Practices"]
    },
    {
      id: 2,
      title: "AML & FICA Compliance Guide for Financial Services",
      description: "Everything South African financial institutions need to know about FICA amendments, SARB requirements, and screening obligations.",
      pages: 34,
      downloads: "6.1k",
      category: "Compliance",
      featured: true,
      topics: ["AML", "FICA", "SARB", "Financial Services"]
    },
    {
      id: 3,
      title: "The ROI of Automated Background Screening",
      description: "Calculate the business value and cost savings from automating your verification and screening processes with VeriGate.",
      pages: 18,
      downloads: "4.5k",
      category: "Business Case",
      featured: false,
      topics: ["ROI", "Automation", "Cost Savings", "Efficiency"]
    },
    {
      id: 4,
      title: "Identity Verification in South Africa: DHA, CIPC & Credit Bureaus",
      description: "A technical overview of integrating with DHA, CIPC, TransUnion SA, Experian SA, and XDS for comprehensive identity verification.",
      pages: 26,
      downloads: "3.8k",
      category: "Technical",
      featured: false,
      topics: ["DHA", "CIPC", "Credit Bureaus", "Identity"]
    },
    {
      id: 5,
      title: "POPIA Compliance in Background Screening",
      description: "Navigate POPIA requirements when processing personal information for employment screening and tenant verification.",
      pages: 22,
      downloads: "7.3k",
      category: "Privacy",
      featured: false,
      topics: ["POPIA", "Privacy", "Data Protection", "Consent"]
    }
  ];

  const ebooks = [
    {
      id: 1,
      title: "Complete Guide to Criminal Record Checks in South Africa",
      description: "Step-by-step guide to SAPS criminal checks, police clearance certificates, and interpreting results for hiring decisions.",
      pages: 42,
      chapters: 7,
      downloads: "11.4k",
      level: "Intermediate"
    },
    {
      id: 2,
      title: "Fraud Prevention Playbook for SA Businesses",
      description: "Proven strategies for detecting identity fraud, qualification fraud, and document forgery in South African recruitment.",
      pages: 36,
      chapters: 6,
      downloads: "8.9k",
      level: "Advanced"
    },
    {
      id: 3,
      title: "Qualification Verification: SAQA, HPCSA & Professional Bodies",
      description: "A practical guide to verifying qualifications through SAQA, HPCSA, ECSA, and other South African professional bodies.",
      pages: 48,
      chapters: 9,
      downloads: "6.7k",
      level: "Intermediate"
    }
  ];

  const reports = [
    {
      id: 1,
      title: "State of Background Screening in South Africa 2026",
      description: "Annual industry report analysing verification trends, adoption rates, and compliance developments across SA industries.",
      year: 2026,
      pages: 58,
      downloads: "15.2k",
      featured: true
    },
    {
      id: 2,
      title: "Employment Fraud Trends Report: South Africa Q1 2026",
      description: "Quarterly analysis of employment fraud patterns, qualification forgery rates, and identity fraud in SA hiring.",
      year: 2026,
      pages: 24,
      downloads: "9.8k",
      featured: false
    },
    {
      id: 3,
      title: "Financial Services Compliance Benchmark: SA Market",
      description: "Benchmarking data on compliance costs, verification rates, and screening best practices from 150+ SA financial services firms.",
      year: 2025,
      pages: 38,
      downloads: "7.1k",
      featured: false
    }
  ];

  const guides = [
    {
      id: 1,
      title: "Getting Started with VeriGate",
      description: "Quick start guide for new VeriGate users — from account setup to your first verification",
      type: "Quick Start",
      time: "15 min read"
    },
    {
      id: 2,
      title: "API Integration Checklist",
      description: "Complete checklist for integrating VeriGate's REST API into your systems",
      type: "Checklist",
      time: "10 min read"
    },
    {
      id: 3,
      title: "SA Regulatory Framework Reference",
      description: "Quick reference for POPIA, FICA, NCA, and other SA regulations affecting background screening",
      type: "Reference",
      time: "Reference"
    },
    {
      id: 4,
      title: "Security & Data Protection Configuration",
      description: "Best practices for securing your VeriGate account and managing data in compliance with POPIA",
      type: "Best Practices",
      time: "20 min read"
    }
  ];

  const templates = [
    {
      id: 1,
      title: "Background Screening RFP Template",
      description: "Ready-to-use RFP template for procuring background screening services",
      format: "DOCX"
    },
    {
      id: 2,
      title: "Vendor Comparison Spreadsheet",
      description: "Compare background screening providers across key criteria",
      format: "XLSX"
    },
    {
      id: 3,
      title: "POPIA Compliance Audit Checklist",
      description: "Internal audit template for screening process POPIA compliance",
      format: "PDF"
    },
    {
      id: 4,
      title: "Implementation Timeline Template",
      description: "Project planning template for VeriGate rollout",
      format: "XLSX"
    }
  ];

  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-primary/10 to-secondary/5">
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
            compliant background screening and stay ahead of SA regulatory changes.
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
      </section>

      {/* Stats */}
      <section className="py-8 border-b bg-background">
        <div className="container mx-auto max-w-4xl">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 text-center">
            <div>
              <div className="text-3xl font-bold text-primary mb-1">20+</div>
              <div className="text-sm text-muted-foreground">Resources</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">85k+</div>
              <div className="text-sm text-muted-foreground">Downloads</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">100%</div>
              <div className="text-sm text-muted-foreground">Free</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">Monthly</div>
              <div className="text-sm text-muted-foreground">New Content</div>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Resources */}
      <section className="py-16">
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
      </section>

      {/* Resource Tabs */}
      <section className="py-16 bg-muted/50">
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
                        <span>·</span>
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
                        <span>·</span>
                        <span>{report.pages} pages</span>
                        <span>·</span>
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
                        <CircleDot className="w-5 h-5 text-primary" />
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
      </section>

      {/* CTA Section */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <Card className="bg-gradient-to-br from-primary to-primary/80 text-primary-foreground">
            <CardContent className="p-12 text-center">
              <div className="max-w-2xl mx-auto">
                <h2 className="text-3xl font-bold mb-4">Can't Find What You Need?</h2>
                <p className="text-lg mb-8 text-primary-foreground/90">
                  Our team is here to help. Contact us for custom resources, consultations,
                  or specific industry guidance for South African compliance requirements.
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
                    <Link to="/technical-support">
                      Visit Support Centre
                    </Link>
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  );
};

export default Resources;
