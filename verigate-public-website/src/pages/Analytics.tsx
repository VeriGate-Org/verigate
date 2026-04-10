import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  BarChart3,
  TrendingUp,
  Clock,
  Sparkles,
  CircleDot,
  ArrowRightCircle,
  Star,
  ShieldCheck,
  ArrowRight,
  FileDown,
  CalendarClock,
  PieChart,
  LineChart,
  Activity,
  Filter,
} from "lucide-react";
import { Link } from "react-router-dom";
import { PlatformDashboard } from "@/components/illustrations/PlatformDashboard";

const bulletIcons = [Sparkles, CircleDot, ArrowRightCircle, Star];

const Analytics = () => {
  const keyMetrics = [
    {
      icon: BarChart3,
      title: "Total Verifications",
      value: "50,000+",
      description: "Verifications processed across all check types with detailed breakdowns by category, status, and time period.",
      color: "text-accent",
      bgColor: "bg-accent/10",
    },
    {
      icon: Clock,
      title: "Average Turnaround",
      value: "< 24hrs",
      description: "Mean turnaround time across all verification types, with drill-down into individual check categories.",
      color: "text-blue-600",
      bgColor: "bg-blue-100 dark:bg-blue-950",
    },
    {
      icon: CircleDot,
      title: "Completion Rate",
      value: "97.8%",
      description: "Percentage of verifications completed successfully on first submission without manual intervention.",
      color: "text-green-600",
      bgColor: "bg-green-100 dark:bg-green-950",
    },
    {
      icon: ShieldCheck,
      title: "Compliance Score",
      value: "99.5%",
      description: "Overall POPIA and FICA compliance adherence across all verification workflows and data handling processes.",
      color: "text-purple-600",
      bgColor: "bg-purple-100 dark:bg-purple-950",
    },
  ];

  const dashboardPreviews = [
    {
      icon: TrendingUp,
      title: "Volume Trends",
      description:
        "Track daily, weekly, and monthly verification volumes with year-over-year comparisons. Identify peak periods and capacity planning opportunities.",
    },
    {
      icon: LineChart,
      title: "Turnaround Charts",
      description:
        "Monitor average turnaround times by verification type. Set SLA benchmarks and receive alerts when times exceed thresholds.",
    },
    {
      icon: PieChart,
      title: "Compliance Heatmap",
      description:
        "Visual heatmap showing compliance scores across departments, regions, and verification types. Identify gaps at a glance.",
    },
    {
      icon: Activity,
      title: "Verification Type Breakdown",
      description:
        "Detailed pie chart and bar chart breakdowns by check type: criminal, identity, qualification, employment, credit, and more.",
    },
  ];

  const features = [
    {
      icon: BarChart3,
      title: "Real-Time Dashboards",
      description:
        "Live dashboards that update automatically as verifications are processed. No manual refresh required.",
      details: [
        "Auto-refreshing data every 30 seconds",
        "Customisable dashboard layouts",
        "Role-based dashboard views",
        "Mobile-responsive design",
      ],
    },
    {
      icon: Filter,
      title: "Custom Reports",
      description:
        "Build custom reports with flexible filters, grouping, and aggregation options to answer any business question.",
      details: [
        "Drag-and-drop report builder",
        "30+ pre-built report templates",
        "Custom date ranges and filters",
        "Multi-dimensional grouping",
      ],
    },
    {
      icon: FileDown,
      title: "CSV & PDF Export",
      description:
        "Export any dashboard or report to CSV for spreadsheet analysis or PDF for executive reporting.",
      details: [
        "One-click CSV export",
        "Branded PDF reports with your logo",
        "Excel-compatible formatting",
        "Bulk data export for BI tools",
      ],
    },
    {
      icon: CalendarClock,
      title: "Scheduled Reporting",
      description:
        "Automate report delivery to stakeholders on a daily, weekly, or monthly schedule.",
      details: [
        "Email delivery to multiple recipients",
        "Daily, weekly, and monthly schedules",
        "Custom report parameters per schedule",
        "Slack and Teams integration for alerts",
      ],
    },
  ];

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div className="space-y-6">
              <Badge variant="secondary" className="mb-4">
                <BarChart3 className="w-3 h-3 mr-1" />
                Analytics & Insights
              </Badge>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                Verification Analytics
                <span className="block text-accent mt-2">& Insights</span>
              </h1>
              <p className="text-xl text-muted-foreground max-w-3xl">
                Gain full visibility into your background screening operations with
                real-time dashboards, custom reports, and actionable insights. Make
                data-driven decisions about your verification processes.
              </p>
              <div className="flex gap-4 flex-wrap">
                <Button size="lg" asChild>
                  <Link to="/request-demo">Request a Demo</Link>
                </Button>
                <Button size="lg" variant="outline" asChild>
                  <Link to="/platform">Explore Platform</Link>
                </Button>
              </div>
            </div>
            <div className="hidden lg:flex items-center justify-center">
              <PlatformDashboard className="w-full max-w-md opacity-90" animate />
            </div>
          </div>
        </div>
      </section>

      {/* Key Metrics Cards */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Key Metrics at a Glance
            </h2>
            <p className="text-lg text-muted-foreground">
              Track the numbers that matter most to your organisation
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {keyMetrics.map((metric, index) => {
              const Icon = metric.icon;
              return (
                <Card key={index} className="border-border hover:shadow-lg transition-shadow">
                  <CardHeader>
                    <div className={`w-12 h-12 rounded-lg ${metric.bgColor} flex items-center justify-center mb-4`}>
                      <Icon className={`w-6 h-6 ${metric.color}`} />
                    </div>
                    <CardTitle className="text-lg">{metric.title}</CardTitle>
                    <div className={`text-3xl font-bold ${metric.color}`}>
                      {metric.value}
                    </div>
                  </CardHeader>
                  <CardContent>
                    <CardDescription>{metric.description}</CardDescription>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* Dashboard Preview */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Dashboard Views
            </h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Pre-built dashboard views that give you instant insight into every
              aspect of your verification operations
            </p>
          </div>

          {/* Dashboard Mockup */}
          <Card className="mb-12 border-border overflow-hidden">
            <div className="bg-foreground/5 p-4 border-b border-border flex items-center gap-2">
              <div className="w-3 h-3 rounded-full bg-red-400" />
              <div className="w-3 h-3 rounded-full bg-yellow-400" />
              <div className="w-3 h-3 rounded-full bg-green-400" />
              <span className="ml-4 text-sm text-muted-foreground">
                VeriGate Analytics Dashboard
              </span>
            </div>
            <CardContent className="p-8">
              <div className="grid md:grid-cols-2 gap-6">
                {dashboardPreviews.map((preview, index) => {
                  const Icon = preview.icon;
                  return (
                    <div
                      key={index}
                      className="p-6 rounded-lg border border-border bg-card"
                    >
                      <div className="flex items-center gap-3 mb-3">
                        <Icon className="w-5 h-5 text-accent" />
                        <h3 className="font-semibold">{preview.title}</h3>
                      </div>
                      <p className="text-sm text-muted-foreground">
                        {preview.description}
                      </p>
                      <div className="mt-4 h-24 bg-gradient-to-r from-accent/10 via-accent/5 to-transparent rounded-lg flex items-center justify-center">
                        <span className="text-xs text-muted-foreground">
                          Chart Preview
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Features */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Analytics Features
            </h2>
            <p className="text-lg text-muted-foreground">
              Everything you need to understand and optimise your screening operations
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            {features.map((feature, index) => {
              const Icon = feature.icon;
              return (
                <Card key={index} className="border-border hover:shadow-lg transition-shadow">
                  <CardHeader>
                    <div className="flex items-center gap-3 mb-2">
                      <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                        <Icon className="w-6 h-6 text-accent" />
                      </div>
                      <CardTitle className="text-xl">{feature.title}</CardTitle>
                    </div>
                    <CardDescription className="text-base">
                      {feature.description}
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <ul className="space-y-2">
                      {feature.details.map((detail, idx) => {
                        const BulletIcon = bulletIcons[idx % bulletIcons.length];
                        return (
                          <li key={idx} className="flex items-center gap-2 text-sm">
                            <BulletIcon className="w-4 h-4 text-accent flex-shrink-0" />
                            <span>{detail}</span>
                          </li>
                        );
                      })}
                    </ul>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <Card className="border-accent bg-gradient-to-br from-primary/5 to-accent/5">
            <CardHeader className="text-center pb-6">
              <CardTitle className="text-3xl md:text-4xl mb-4">
                See Your Verification Data Come to Life
              </CardTitle>
              <CardDescription className="text-lg">
                Request a demo and we'll walk you through the analytics dashboard
                using your own data scenarios.
              </CardDescription>
            </CardHeader>
            <CardContent className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" className="min-w-[200px]" asChild>
                <Link to="/request-demo">
                  Request a Demo
                  <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" className="min-w-[200px]" asChild>
                <Link to="/contact">Contact Sales</Link>
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  );
};

export default Analytics;
