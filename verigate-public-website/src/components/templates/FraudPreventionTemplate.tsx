import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  ArrowRight,
  Shield,
  AlertTriangle,
  ShieldAlert,
  Eye,
} from "lucide-react";
import { Link } from "react-router-dom";
import { getCategoryTheme } from "@/lib/categoryTheme";
import { getBulletIcon } from "@/lib/contextIcons";
import { FraudDetection } from "@/components/illustrations/FraudDetection";
import PageCTA from "@/components/PageCTA";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

export interface FraudPreventionData {
  slug: string;
  title: string;
  subtitle: string;
  description: string;
  icon: string;
  threats: { title: string; description: string; severity: string }[];
  detectionMethods: { title: string; description: string; items: string[] }[];
  solutions: { title: string; description: string }[];
  relatedPrevention: { slug: string; title: string; description: string }[];
  ctaTitle: string;
}

interface FraudPreventionTemplateProps {
  data: FraudPreventionData;
}

const severityColor = (severity: string) => {
  switch (severity.toLowerCase()) {
    case "critical":
      return "bg-destructive/10 text-destructive border-destructive/30";
    case "high":
      return "bg-orange-500/10 text-orange-600 border-orange-500/30";
    case "medium":
      return "bg-yellow-500/10 text-yellow-600 border-yellow-500/30";
    case "low":
      return "bg-category-fraud/10 text-category-fraud border-category-fraud/30";
    default:
      return "bg-muted text-muted-foreground border-border";
  }
};

const FraudPreventionTemplate = ({ data }: FraudPreventionTemplateProps) => {
  const theme = getCategoryTheme("fraud");

  return (
    <>
      {/* Hero Section */}
      <section className="pt-24 pb-16 bg-gradient-to-br from-secondary via-background to-category-fraud/5 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <AnimatedSection>
              <Badge className="mb-4 text-sm bg-category-fraud/10 text-category-fraud border-category-fraud/30">
                <ShieldAlert className="w-3.5 h-3.5 mr-1.5" />
                Fraud Prevention
              </Badge>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground">
                {data.title}
                <span className="block mt-2 text-primary">{data.subtitle}</span>
              </h1>
              <p className="text-xl text-muted-foreground mb-8 max-w-2xl">
                {data.description}
              </p>

              <div className="flex flex-col sm:flex-row gap-4">
                <Button size="lg" asChild>
                  <Link to="/request-demo">
                    Request a Demo
                    <ArrowRight className="ml-2 w-4 h-4" />
                  </Link>
                </Button>
                <Button size="lg" variant="outline" asChild>
                  <Link to="/pricing">View Pricing</Link>
                </Button>
              </div>
            </AnimatedSection>

            <AnimatedSection delay={0.2} className="hidden lg:flex justify-center">
              <FraudDetection className="w-full max-w-sm" />
            </AnimatedSection>
          </div>
        </div>
      </section>

      {/* Threat Overview */}
      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Threat Overview
            </h2>
            <p className="text-lg text-muted-foreground">
              Understanding the fraud landscape facing South African
              organisations
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-2 gap-6"
            staggerDelay={0.1}
            containerDelay={0.2}
          >
            {data.threats.map((threat, index) => (
              <Card
                key={index}
                className="border-border/50 hover:shadow-lg transition-all duration-200"
              >
                <CardHeader>
                  <div className="flex items-start gap-3">
                    <div className="w-10 h-10 rounded-lg bg-destructive/10 flex items-center justify-center flex-shrink-0">
                      <AlertTriangle className="w-5 h-5 text-destructive" />
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <CardTitle className="text-lg">
                          {threat.title}
                        </CardTitle>
                        <Badge
                          variant="outline"
                          className={`text-xs ${severityColor(threat.severity)}`}
                        >
                          {threat.severity}
                        </Badge>
                      </div>
                      <CardDescription className="leading-relaxed">
                        {threat.description}
                      </CardDescription>
                    </div>
                  </div>
                </CardHeader>
              </Card>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* Detection Methods */}
      <section className="py-20 px-4 bg-amber-50/30 dark:bg-amber-950/20">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Detection Methods
            </h2>
            <p className="text-lg text-muted-foreground">
              Multi-layered detection techniques powered by AI and verified
              South African data sources
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-2 lg:grid-cols-3 gap-8"
            staggerDelay={0.12}
            containerDelay={0.2}
          >
            {data.detectionMethods.map((method, index) => (
              <Card
                key={index}
                className={`border-border/50 ${theme.cardHoverBorder} transition-all duration-200 hover:shadow-xl ${theme.cardHoverShadow} hover:-translate-y-1 group bg-gradient-to-br from-card to-card/50 hover:from-card hover:to-category-fraud/5`}
              >
                <CardHeader>
                  <div className={`w-12 h-12 rounded-lg ${theme.iconBg} flex items-center justify-center mb-3 group-hover:bg-category-fraud/20 group-hover:scale-110 transition-all duration-200`}>
                    <Eye className={`w-6 h-6 ${theme.iconColor}`} />
                  </div>
                  <CardTitle className="text-xl">{method.title}</CardTitle>
                  <CardDescription>{method.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2.5">
                    {method.items.map((item, idx) => {
                      const BulletIcon = getBulletIcon("fraud", idx);
                      return (
                        <li
                          key={idx}
                          className="flex items-start gap-2.5 text-sm"
                        >
                          <BulletIcon className={`w-4 h-4 ${theme.iconColor} flex-shrink-0 mt-0.5`} />
                          <span className="text-muted-foreground">{item}</span>
                        </li>
                      );
                    })}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* Solutions */}
      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              VeriGate Solutions
            </h2>
            <p className="text-lg text-muted-foreground">
              Comprehensive fraud prevention tools tailored for the South
              African market
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-2 gap-6"
            staggerDelay={0.1}
            containerDelay={0.2}
          >
            {data.solutions.map((solution, index) => (
              <Card
                key={index}
                className={`border-border/50 ${theme.cardHoverBorder} transition-all duration-200 hover:shadow-lg group`}
              >
                <CardContent className="p-6">
                  <div className="flex items-start gap-4">
                    <div className={`w-10 h-10 rounded-full ${theme.iconBg} flex items-center justify-center flex-shrink-0 group-hover:bg-category-fraud/20 transition-colors duration-200`}>
                      <Shield className={`w-5 h-5 ${theme.iconColor}`} />
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-foreground mb-2">
                        {solution.title}
                      </h3>
                      <p className="text-muted-foreground text-sm leading-relaxed">
                        {solution.description}
                      </p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* Related Prevention */}
      {data.relatedPrevention.length > 0 && (
        <section className="py-20 px-4 bg-amber-50/30 dark:bg-amber-950/20">
          <div className="container mx-auto max-w-6xl">
            <AnimatedSection className="max-w-2xl mb-16">
              <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
                Related Prevention Areas
              </h2>
              <p className="text-lg text-muted-foreground">
                Explore other fraud prevention capabilities
              </p>
            </AnimatedSection>

            <StaggeredList
              className="grid md:grid-cols-3 gap-6"
              staggerDelay={0.1}
              containerDelay={0.2}
            >
              {data.relatedPrevention.map((related, index) => (
                <Link
                  key={index}
                  to={`/fraud-prevention/${related.slug}`}
                  className="group"
                >
                  <Card className={`h-full border-border/50 ${theme.cardHoverBorder} transition-all duration-200 hover:shadow-lg group-hover:-translate-y-1`}>
                    <CardHeader>
                      <div className="flex items-center gap-2 mb-2">
                        <ShieldAlert className={`w-5 h-5 ${theme.iconColor}`} />
                        <CardTitle className="text-lg">
                          {related.title}
                        </CardTitle>
                      </div>
                      <CardDescription className="leading-relaxed">
                        {related.description}
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <span className="inline-flex items-center gap-1.5 text-sm font-medium text-category-fraud group-hover:gap-2.5 transition-all duration-200">
                        Learn more
                        <ArrowRight className="w-4 h-4" />
                      </span>
                    </CardContent>
                  </Card>
                </Link>
              ))}
            </StaggeredList>
          </div>
        </section>
      )}

      {/* CTA Section */}
      <PageCTA variant="fraud" />
    </>
  );
};

export default FraudPreventionTemplate;
