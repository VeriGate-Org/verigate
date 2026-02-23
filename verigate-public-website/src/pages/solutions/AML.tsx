import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  Shield,
  AlertTriangle,
  Globe,
  Zap,
  Search,
  Database,
  RefreshCw,
  TrendingUp,
  Users,
  FileSearch,
  Clock,
  ArrowRight,
  Eye,
  Ban
} from "lucide-react";
import { Link } from "react-router-dom";

const AML = () => {
  const screeningProcess = [
    {
      step: 1,
      title: "Data Collection",
      description: "Collect customer information including name, DOB, nationality, and address",
      icon: Users,
      duration: "Real-time",
    },
    {
      step: 2,
      title: "Database Screening",
      description: "Screen against global watchlists, sanctions, and PEP databases",
      icon: Database,
      duration: "< 2 seconds",
    },
    {
      step: 3,
      title: "Match Analysis",
      description: "AI-powered fuzzy matching to identify true positives and reduce false alerts",
      icon: Search,
      duration: "1-2 seconds",
    },
    {
      step: 4,
      title: "Risk Assessment",
      description: "Assign risk scores and generate comprehensive screening reports",
      icon: AlertTriangle,
      duration: "< 1 second",
    },
  ];

  const databases = [
    {
      category: "Sanctions Lists",
      sources: [
        "OFAC (US Office of Foreign Assets Control)",
        "UN Security Council Sanctions",
        "EU Consolidated Financial Sanctions",
        "HM Treasury Financial Sanctions (UK)",
        "DFAT (Australian Sanctions)",
      ],
      coverage: "200+ sanctioned countries and entities",
    },
    {
      category: "PEP Databases",
      sources: [
        "Politically Exposed Persons (Global)",
        "Relatives and Close Associates (RCA)",
        "State-Owned Enterprises (SOE)",
        "Government Officials",
        "International Organization Leaders",
      ],
      coverage: "2M+ PEP profiles worldwide",
    },
    {
      category: "Watchlists",
      sources: [
        "Interpol Most Wanted",
        "FBI Most Wanted",
        "Europol Most Wanted",
        "Law Enforcement Watchlists",
        "Regulatory Enforcement Lists",
      ],
      coverage: "50+ national and international lists",
    },
    {
      category: "Adverse Media",
      sources: [
        "Financial Crime News",
        "Regulatory Actions",
        "Court Records",
        "Legal Proceedings",
        "Negative News Coverage",
      ],
      coverage: "100K+ sources in 100+ languages",
    },
  ];

  const riskCategories = [
    {
      level: "High Risk",
      color: "destructive",
      criteria: ["Sanctions match", "Active PEP", "Recent adverse media", "High-risk jurisdiction"],
      action: "Enhanced due diligence required",
    },
    {
      level: "Medium Risk",
      color: "warning",
      criteria: ["Former PEP", "RCA match", "Historical adverse media", "Medium-risk jurisdiction"],
      action: "Standard due diligence recommended",
    },
    {
      level: "Low Risk",
      color: "secondary",
      criteria: ["No matches found", "Low-risk jurisdiction", "Clean screening history"],
      action: "Simplified due diligence acceptable",
    },
  ];

  const features = [
    {
      icon: RefreshCw,
      title: "Ongoing Monitoring",
      description: "Continuous screening of your customer base against daily-updated databases",
    },
    {
      icon: Eye,
      title: "Real-Time Alerts",
      description: "Instant notifications when customers match new sanctions or watchlist entries",
    },
    {
      icon: FileSearch,
      title: "Case Management",
      description: "Built-in workflow for investigating and documenting screening results",
    },
    {
      icon: TrendingUp,
      title: "Risk Scoring",
      description: "AI-powered risk assessment with customizable scoring models",
    },
    {
      icon: Globe,
      title: "Global Coverage",
      description: "Comprehensive screening across 200+ countries and jurisdictions",
    },
    {
      icon: Zap,
      title: "Lightning Fast",
      description: "Sub-5 second screening with 99.9% uptime SLA",
    },
  ];

  const complianceRegulations = [
    {
      name: "Bank Secrecy Act (BSA)",
      region: "United States",
      description: "Comply with US AML requirements including customer due diligence and reporting",
    },
    {
      name: "6AMLD",
      region: "European Union",
      description: "Meet the Sixth Anti-Money Laundering Directive requirements",
    },
    {
      name: "FATF Recommendations",
      region: "Global",
      description: "Align with Financial Action Task Force international standards",
    },
    {
      name: "UK Money Laundering Regulations",
      region: "United Kingdom",
      description: "Comply with UK MLR 2017 and subsequent amendments",
    },
  ];

  const metrics = [
    { label: "Screening Speed", value: "< 5 seconds", icon: Clock },
    { label: "Database Updates", value: "Daily", icon: RefreshCw },
    { label: "Countries Covered", value: "200+", icon: Globe },
    { label: "Data Sources", value: "100+", icon: Database },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-destructive/5 via-background to-destructive/10">
        <div className="container mx-auto max-w-6xl">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div>
              <Badge className="mb-4" variant="secondary">
                AML Screening
              </Badge>
              <h1 className="text-4xl md:text-5xl font-bold mb-6">
                Anti-Money Laundering
                <span className="text-primary block mt-2">Screening & Compliance</span>
              </h1>
              <p className="text-xl text-muted-foreground mb-8">
                Screen customers against global sanctions, PEP databases, and watchlists in real-time. 
                Stay compliant with automated monitoring and instant risk alerts.
              </p>
              
              <div className="flex gap-4 flex-wrap mb-8">
                <Button size="lg" asChild>
                  <Link to="/contact">Get Started</Link>
                </Button>
                <Button size="lg" variant="outline" asChild>
                  <Link to="/contact">Request Demo</Link>
                </Button>
              </div>

              {/* Key Metrics */}
              <div className="grid grid-cols-2 gap-4">
                {metrics.map((metric, index) => (
                  <div key={index} className="flex items-center gap-3">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <metric.icon className="w-5 h-5 text-primary" />
                    </div>
                    <div>
                      <div className="font-bold text-lg">{metric.value}</div>
                      <div className="text-sm text-muted-foreground">{metric.label}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="relative">
              <div className="aspect-square bg-gradient-to-br from-destructive/20 to-destructive/5 rounded-2xl p-8 flex items-center justify-center">
                <div className="text-center">
                  <AlertTriangle className="w-32 h-32 text-destructive mx-auto mb-4" />
                  <p className="text-lg font-semibold">100+ Global Data Sources</p>
                  <p className="text-muted-foreground">Updated daily for maximum accuracy</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* How Screening Works */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">How AML Screening Works</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Comprehensive screening in under 5 seconds with AI-powered accuracy
            </p>
          </div>

          <div className="grid md:grid-cols-4 gap-6">
            {screeningProcess.map((step, index) => (
              <div key={index} className="relative">
                <Card className="h-full hover:shadow-lg transition-shadow">
                  <CardHeader>
                    <div className="flex items-center gap-3 mb-3">
                      <div className="w-10 h-10 rounded-full bg-primary text-primary-foreground flex items-center justify-center font-bold">
                        {step.step}
                      </div>
                      <step.icon className="w-6 h-6 text-primary" />
                    </div>
                    <CardTitle className="text-lg">{step.title}</CardTitle>
                    <CardDescription>{step.description}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <Badge variant="secondary" className="text-xs">
                      <Clock className="w-3 h-3 mr-1" />
                      {step.duration}
                    </Badge>
                  </CardContent>
                </Card>
                {index < screeningProcess.length - 1 && (
                  <div className="hidden md:block absolute top-1/2 -right-3 transform -translate-y-1/2 z-10">
                    <ArrowRight className="w-6 h-6 text-primary" />
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Database Coverage */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Comprehensive Database Coverage</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Screen against 100+ global data sources updated daily
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {databases.map((db, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <Database className="w-5 h-5 text-primary" />
                    <CardTitle>{db.category}</CardTitle>
                  </div>
                  <Badge variant="outline" className="w-fit">{db.coverage}</Badge>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {db.sources.map((source, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{source}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Risk Categories */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Intelligent Risk Assessment</h2>
            <p className="text-lg text-muted-foreground">
              AI-powered risk scoring with automated categorization
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {riskCategories.map((risk, index) => (
              <Card key={index} className={`border-${risk.color}`}>
                <CardHeader>
                  <Badge variant={risk.color as any} className="w-fit mb-2">{risk.level}</Badge>
                  <CardTitle className="text-lg">Recommended Action</CardTitle>
                  <CardDescription className="font-semibold">{risk.action}</CardDescription>
                </CardHeader>
                <CardContent>
                  <p className="text-sm font-medium mb-2">Criteria:</p>
                  <ul className="space-y-1">
                    {risk.criteria.map((criterion, idx) => (
                      <li key={idx} className="text-sm text-muted-foreground flex items-start gap-2">
                        <span className="text-xs">•</span>
                        <span>{criterion}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Key Features */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Powerful AML Features</h2>
            <p className="text-lg text-muted-foreground">
              Everything you need for comprehensive AML compliance
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {features.map((feature, index) => (
              <div key={index} className="text-center">
                <div className="inline-flex p-4 bg-primary/10 rounded-lg mb-4">
                  <feature.icon className="w-8 h-8 text-primary" />
                </div>
                <h3 className="text-lg font-semibold mb-2">{feature.title}</h3>
                <p className="text-muted-foreground text-sm">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Compliance Regulations */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Regulatory Compliance</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Meet AML requirements across all major jurisdictions
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {complianceRegulations.map((regulation, index) => (
              <Card key={index}>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Shield className="w-5 h-5 text-primary" />
                    {regulation.name}
                  </CardTitle>
                  <Badge variant="outline" className="w-fit">{regulation.region}</Badge>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground">{regulation.description}</p>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="mt-8 text-center">
            <Button variant="outline" asChild>
              <Link to="/security">
                View Compliance Documentation <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* API Example */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Simple Integration</h2>
            <p className="text-lg text-muted-foreground">
              Start screening in minutes with our API
            </p>
          </div>

          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>AML Screening Example</CardTitle>
              <CardDescription>Screen a customer against global databases</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// Initialize VeriGate SDK
const verigate = new VeriGate('your_api_key');

// Perform AML screening
const screening = await verigate.aml.screen({
  firstName: 'John',
  lastName: 'Doe',
  dateOfBirth: '1990-01-01',
  nationality: 'US',
  address: {
    country: 'US',
    city: 'New York'
  }
});

// Handle results
if (screening.matches.length > 0) {
  console.log('Potential matches found:', screening.matches);
  console.log('Risk Level:', screening.riskLevel);
  
  // Review matches
  screening.matches.forEach(match => {
    console.log(\`Match: \${match.name}\`);
    console.log(\`Type: \${match.type}\`); // sanctions, pep, watchlist
    console.log(\`Confidence: \${match.confidence}%\`);
  });
} else {
  console.log('No matches found - Low risk');
}`}</code>
              </pre>
            </CardContent>
          </Card>

          <div className="mt-8 text-center">
            <Button variant="outline" asChild>
              <Link to="/developers">
                View API Documentation <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-white">
        <div className="container mx-auto max-w-6xl text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            Stay Compliant with Automated AML Screening
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Protect your business from financial crime with real-time screening and monitoring
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

export default AML;
