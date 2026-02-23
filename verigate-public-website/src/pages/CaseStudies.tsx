import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  TrendingUp,
  Users,
  Clock,
  DollarSign,
  ArrowRight,
  Building2,
  Target,
  Zap,
  Shield
} from "lucide-react";
import { Link } from "react-router-dom";

const CaseStudies = () => {
  const caseStudies = [
    {
      id: "neobank-global",
      company: "Global Neobank",
      industry: "Fintech",
      logo: "🏦",
      size: "500+ employees",
      region: "Europe",
      challenge: "Needed to onboard 100,000+ customers monthly across 30 countries while maintaining compliance",
      solution: "Implemented VeriGate's multi-country KYC with automated AML screening",
      results: [
        { metric: "Onboarding Time", before: "3-5 days", after: "< 2 minutes", improvement: "87% faster" },
        { metric: "Customer Drop-off", before: "45%", after: "8%", improvement: "82% reduction" },
        { metric: "Compliance Costs", before: "$2M/year", after: "$600K/year", improvement: "70% savings" },
        { metric: "Fraud Rate", before: "2.3%", after: "0.3%", improvement: "87% reduction" },
      ],
      testimonial: "VeriGate transformed our onboarding process. We went from days to minutes while actually improving fraud detection. The ROI was immediate.",
      author: "Chief Technology Officer",
      tags: ["KYC", "AML", "Multi-country", "Mobile-first"],
    },
    {
      id: "crypto-exchange",
      company: "Leading Crypto Exchange",
      industry: "Cryptocurrency",
      logo: "₿",
      size: "200+ employees",
      region: "Global",
      challenge: "Required enhanced due diligence for high-value traders while meeting Travel Rule requirements",
      solution: "Deployed VeriGate's crypto-native verification with source of funds checks and ongoing monitoring",
      results: [
        { metric: "KYC Processing", before: "24-48 hours", after: "< 5 minutes", improvement: "95% faster" },
        { metric: "Travel Rule Compliance", before: "Manual", after: "Automated", improvement: "100% coverage" },
        { metric: "High-Risk Detection", before: "75%", after: "99.2%", improvement: "24% increase" },
        { metric: "Support Tickets", before: "500/week", after: "50/week", improvement: "90% reduction" },
      ],
      testimonial: "The crypto-specific features like wallet screening and Travel Rule automation were game-changers. We're now fully compliant across all jurisdictions.",
      author: "Head of Compliance",
      tags: ["Crypto", "Enhanced Due Diligence", "Travel Rule", "Ongoing Monitoring"],
    },
    {
      id: "online-casino",
      company: "European iGaming Platform",
      industry: "Gaming",
      logo: "🎰",
      size: "150+ employees",
      region: "Europe",
      challenge: "Needed instant age verification while preventing self-excluded players and underage access",
      solution: "Integrated VeriGate's gaming solution with self-exclusion database checks and age verification",
      results: [
        { metric: "Age Verification", before: "Manual checks", after: "< 30 seconds", improvement: "Instant" },
        { metric: "Self-Exclusion Blocks", before: "60%", after: "99.5%", improvement: "39% increase" },
        { metric: "Regulatory Fines", before: "€250K/year", after: "€0", improvement: "100% reduction" },
        { metric: "Player Onboarding", before: "10 minutes", after: "90 seconds", improvement: "85% faster" },
      ],
      testimonial: "We've eliminated regulatory fines and can now confidently demonstrate responsible gambling practices. The self-exclusion checking is bulletproof.",
      author: "Compliance Director",
      tags: ["Age Verification", "Self-Exclusion", "Responsible Gaming", "Compliance"],
    },
    {
      id: "telehealth-platform",
      company: "Telehealth Provider",
      industry: "Healthcare",
      logo: "🏥",
      size: "300+ employees",
      region: "United States",
      challenge: "Required HIPAA-compliant patient verification for virtual consultations with instant insurance checks",
      solution: "Implemented VeriGate's healthcare verification with insurance card scanning and HIPAA compliance",
      results: [
        { metric: "Patient Registration", before: "8-10 minutes", after: "< 2 minutes", improvement: "75% faster" },
        { metric: "Insurance Verification", before: "Manual", after: "Automated", improvement: "Real-time" },
        { metric: "No-show Rate", before: "18%", after: "6%", improvement: "67% reduction" },
        { metric: "HIPAA Violations", before: "3/year", after: "0", improvement: "100% compliant" },
      ],
      testimonial: "Patient satisfaction scores increased 40% after implementing instant verification. The HIPAA compliance features give us complete peace of mind.",
      author: "VP of Operations",
      tags: ["HIPAA", "Healthcare", "Insurance Verification", "Patient Onboarding"],
    },
    {
      id: "vacation-rental",
      company: "Vacation Rental Platform",
      industry: "Travel & Hospitality",
      logo: "🏠",
      size: "75+ employees",
      region: "North America",
      challenge: "Property owners needed guest screening while guests wanted seamless check-in experience",
      solution: "Deployed VeriGate's contactless check-in with background checks and property damage protection",
      results: [
        { metric: "Guest Check-in", before: "Key exchange required", after: "Fully contactless", improvement: "100% digital" },
        { metric: "Property Damage", before: "$45K/year", after: "$8K/year", improvement: "82% reduction" },
        { metric: "Guest Satisfaction", before: "3.8/5", after: "4.7/5", improvement: "24% increase" },
        { metric: "Host Complaints", before: "120/month", after: "15/month", improvement: "88% reduction" },
      ],
      testimonial: "Hosts love the screening, guests love the convenience. Win-win. Property damage claims dropped dramatically.",
      author: "CEO",
      tags: ["Travel", "Contactless", "Background Checks", "Guest Screening"],
    },
  ];

  const industries = [
    { name: "Fintech", count: 45, icon: Zap },
    { name: "Banking", count: 32, icon: Building2 },
    { name: "Cryptocurrency", count: 28, icon: Target },
    { name: "Gaming", count: 19, icon: Users },
    { name: "Healthcare", count: 15, icon: Shield },
    { name: "E-commerce", count: 12, icon: TrendingUp },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-background to-primary/10">
        <div className="container mx-auto max-w-6xl text-center">
          <Badge className="mb-4" variant="secondary">
            Customer Success Stories
          </Badge>
          <h1 className="text-4xl md:text-5xl font-bold mb-6">
            Case Studies
            <span className="text-primary block mt-2">Real Results from Real Customers</span>
          </h1>
          <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
            See how leading companies across industries use VeriGate to transform their identity 
            verification processes and drive measurable business results.
          </p>

          {/* Quick Stats */}
          <div className="grid md:grid-cols-4 gap-6 mt-12">
            <Card>
              <CardContent className="pt-6 text-center">
                <div className="text-3xl font-bold text-primary mb-2">500+</div>
                <div className="text-sm text-muted-foreground">Enterprise Customers</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6 text-center">
                <div className="text-3xl font-bold text-primary mb-2">50M+</div>
                <div className="text-sm text-muted-foreground">Verifications Annually</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6 text-center">
                <div className="text-3xl font-bold text-primary mb-2">87%</div>
                <div className="text-sm text-muted-foreground">Average Time Savings</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6 text-center">
                <div className="text-3xl font-bold text-primary mb-2">92%</div>
                <div className="text-sm text-muted-foreground">Fraud Reduction</div>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Industry Filter */}
      <section className="py-8 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="flex flex-wrap gap-3 justify-center">
            {industries.map((industry, index) => (
              <Badge key={index} variant="outline" className="text-sm py-2 cursor-pointer hover:bg-primary hover:text-primary-foreground transition-colors">
                <industry.icon className="w-4 h-4 mr-2" />
                {industry.name} ({industry.count})
              </Badge>
            ))}
          </div>
        </div>
      </section>

      {/* Case Studies Grid */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="space-y-12">
            {caseStudies.map((study, index) => (
              <Card key={index} className="overflow-hidden hover:shadow-xl transition-shadow">
                <div className="grid md:grid-cols-3 gap-6">
                  {/* Left: Company Info */}
                  <div className="md:col-span-1 bg-gradient-to-br from-primary/5 to-primary/10 p-8">
                    <div className="text-6xl mb-4">{study.logo}</div>
                    <h3 className="text-2xl font-bold mb-2">{study.company}</h3>
                    <Badge variant="secondary" className="mb-4">{study.industry}</Badge>
                    <div className="space-y-2 text-sm text-muted-foreground">
                      <div className="flex items-center gap-2">
                        <Users className="w-4 h-4" />
                        {study.size}
                      </div>
                      <div className="flex items-center gap-2">
                        <Building2 className="w-4 h-4" />
                        {study.region}
                      </div>
                    </div>

                    <div className="mt-6 flex flex-wrap gap-2">
                      {study.tags.map((tag, idx) => (
                        <Badge key={idx} variant="outline" className="text-xs">
                          {tag}
                        </Badge>
                      ))}
                    </div>
                  </div>

                  {/* Right: Details */}
                  <div className="md:col-span-2 p-8">
                    <div className="mb-6">
                      <h4 className="text-lg font-semibold mb-2">The Challenge</h4>
                      <p className="text-muted-foreground">{study.challenge}</p>
                    </div>

                    <div className="mb-6">
                      <h4 className="text-lg font-semibold mb-2">The Solution</h4>
                      <p className="text-muted-foreground">{study.solution}</p>
                    </div>

                    <div className="mb-6">
                      <h4 className="text-lg font-semibold mb-4">The Results</h4>
                      <div className="grid grid-cols-2 gap-4">
                        {study.results.map((result, idx) => (
                          <div key={idx} className="border rounded-lg p-4">
                            <div className="text-xs text-muted-foreground mb-1">{result.metric}</div>
                            <div className="flex items-center gap-2 mb-1">
                              <span className="text-sm line-through text-muted-foreground">{result.before}</span>
                              <ArrowRight className="w-3 h-3" />
                              <span className="text-lg font-bold text-primary">{result.after}</span>
                            </div>
                            <Badge variant="secondary" className="text-xs">
                              <TrendingUp className="w-3 h-3 mr-1" />
                              {result.improvement}
                            </Badge>
                          </div>
                        ))}
                      </div>
                    </div>

                    <div className="border-l-4 border-primary pl-4 py-2 bg-primary/5 rounded">
                      <p className="italic text-muted-foreground mb-2">"{study.testimonial}"</p>
                      <p className="text-sm font-semibold">— {study.author}, {study.company}</p>
                    </div>

                    <div className="mt-6">
                      <Button variant="outline" asChild>
                        <Link to={`/case-studies/${study.id}`}>
                          Read Full Case Study <ArrowRight className="w-4 h-4 ml-2" />
                        </Link>
                      </Button>
                    </div>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-white">
        <div className="container mx-auto max-w-6xl text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            Ready to Write Your Success Story?
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join 500+ companies achieving similar results with VeriGate
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

export default CaseStudies;
