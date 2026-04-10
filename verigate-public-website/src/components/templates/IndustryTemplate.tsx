import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  ArrowRight,
  AlertTriangle,
  TrendingUp,
  Scale,
  BarChart3,
} from "lucide-react";
import { Link } from "react-router-dom";
import { getCategoryTheme } from "@/lib/categoryTheme";
import { getBulletIcon } from "@/lib/contextIcons";
import { IndustryNetwork } from "@/components/illustrations/IndustryNetwork";
import PageCTA from "@/components/PageCTA";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

export interface IndustryData {
  slug: string;
  title: string;
  subtitle: string;
  description: string;
  icon: string;
  badgeText: string;
  metrics: { metric: string; before: string; after: string; improvement: string }[];
  challenges: { title: string; description: string; impact: string }[];
  solutions: { title: string; description: string; features: string[]; result: string }[];
  regulations: { name: string; description: string }[];
  useCases: { title: string; description: string; benefits: string[] }[];
  roi: { title: string; stats: { label: string; value: string }[] };
  ctaTitle: string;
}

interface IndustryTemplateProps {
  data: IndustryData;
}

const IndustryTemplate = ({ data }: IndustryTemplateProps) => {
  const theme = getCategoryTheme("industry");

  return (
    <>
      {/* Hero Section */}
      <section className="pt-24 pb-16 bg-gradient-to-br from-secondary via-background to-category-industry/5 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <AnimatedSection>
              <Badge className="mb-4 text-sm bg-category-industry/10 text-category-industry border-category-industry/30">
                {data.badgeText}
              </Badge>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground">
                {data.title}
                <span className="block mt-2 text-primary">{data.subtitle}</span>
              </h1>
              <p className="text-xl text-muted-foreground max-w-2xl mb-8">
                {data.description}
              </p>
              <div className="flex gap-4 flex-wrap">
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
              <IndustryNetwork className="w-full max-w-sm" />
            </AnimatedSection>
          </div>

          {/* Key Metrics Dashboard */}
          {data.metrics.length > 0 && (
            <AnimatedSection delay={0.3} className="mt-12">
              <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
                {data.metrics.map((item, index) => (
                  <Card key={index} className="text-center">
                    <CardHeader className="pb-3">
                      <Badge variant="secondary" className="mx-auto text-xs">
                        {item.improvement}
                      </Badge>
                    </CardHeader>
                    <CardContent>
                      <div className="text-sm font-medium text-muted-foreground mb-1">
                        {item.metric}
                      </div>
                      <div className="flex items-center justify-center gap-2">
                        <span className="text-xs line-through text-muted-foreground">
                          {item.before}
                        </span>
                        <ArrowRight className="w-3 h-3 text-muted-foreground" />
                        <span className="text-lg font-bold text-primary">
                          {item.after}
                        </span>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </AnimatedSection>
          )}
        </div>
      </section>

      {/* Challenges */}
      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Industry Challenges
            </h2>
            <p className="text-lg text-muted-foreground">
              Key verification and compliance challenges facing this sector in
              South Africa
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-2 gap-6"
            staggerDelay={0.1}
            containerDelay={0.2}
          >
            {data.challenges.map((challenge, index) => (
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
                      <CardTitle className="text-lg mb-2">
                        {challenge.title}
                      </CardTitle>
                      <CardDescription className="leading-relaxed">
                        {challenge.description}
                      </CardDescription>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <Badge variant="outline" className="text-xs">
                    <AlertTriangle className="w-3 h-3 mr-1" />
                    {challenge.impact}
                  </Badge>
                </CardContent>
              </Card>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* Solutions */}
      <section className="py-20 px-4 bg-green-50/30 dark:bg-green-950/20">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              VeriGate Solutions
            </h2>
            <p className="text-lg text-muted-foreground">
              Comprehensive verification solutions tailored for your industry
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-3 gap-6"
            staggerDelay={0.12}
            containerDelay={0.2}
          >
            {data.solutions.map((solution, index) => (
              <Card
                key={index}
                className={`border-border/50 ${theme.cardHoverBorder} transition-all duration-200 hover:shadow-xl ${theme.cardHoverShadow} hover:-translate-y-1 group bg-gradient-to-br from-card to-card/50 hover:from-card hover:to-category-industry/5`}
              >
                <CardHeader>
                  <CardTitle className="text-xl">{solution.title}</CardTitle>
                  <CardDescription>{solution.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2 mb-4">
                    {solution.features.map((feature, idx) => {
                      const BulletIcon = getBulletIcon("industry", idx);
                      return (
                        <li
                          key={idx}
                          className="flex items-start gap-2 text-sm"
                        >
                          <BulletIcon className={`w-4 h-4 ${theme.iconColor} flex-shrink-0 mt-0.5`} />
                          <span className="text-muted-foreground">
                            {feature}
                          </span>
                        </li>
                      );
                    })}
                  </ul>
                  <Badge variant="default" className="w-full justify-center">
                    <TrendingUp className="w-3 h-3 mr-1" />
                    {solution.result}
                  </Badge>
                </CardContent>
              </Card>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* Regulations */}
      {data.regulations.length > 0 && (
        <section className="py-20 px-4">
          <div className="container mx-auto max-w-6xl">
            <AnimatedSection className="max-w-2xl mb-16">
              <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
                Regulatory Compliance
              </h2>
              <p className="text-lg text-muted-foreground">
                Meet South African and international regulatory requirements
                with automated compliance checks
              </p>
            </AnimatedSection>

            <StaggeredList
              className="grid md:grid-cols-2 lg:grid-cols-3 gap-6"
              staggerDelay={0.1}
              containerDelay={0.2}
            >
              {data.regulations.map((regulation, index) => (
                <Card
                  key={index}
                  className="border-border/50 hover:shadow-md transition-all duration-200"
                >
                  <CardHeader>
                    <div className="flex items-center gap-2 mb-2">
                      <Scale className={`w-5 h-5 ${theme.iconColor}`} />
                      <CardTitle className="text-base">
                        {regulation.name}
                      </CardTitle>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <p className="text-sm text-muted-foreground leading-relaxed">
                      {regulation.description}
                    </p>
                  </CardContent>
                </Card>
              ))}
            </StaggeredList>

            <AnimatedSection delay={0.3} className="mt-8 text-center">
              <Button variant="outline" asChild>
                <Link to="/compliance">
                  View Compliance
                  <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
            </AnimatedSection>
          </div>
        </section>
      )}

      {/* Use Cases */}
      <section className="py-20 px-4 bg-green-50/30 dark:bg-green-950/20">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Use Cases
            </h2>
            <p className="text-lg text-muted-foreground">
              Verification across the full lifecycle in your industry
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-2 gap-6"
            staggerDelay={0.1}
            containerDelay={0.2}
          >
            {data.useCases.map((useCase, index) => (
              <Card
                key={index}
                className="border-border/50 hover:shadow-lg transition-all duration-200"
              >
                <CardHeader>
                  <CardTitle className="text-lg">{useCase.title}</CardTitle>
                  <CardDescription>{useCase.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {useCase.benefits.map((benefit, idx) => {
                      const BulletIcon = getBulletIcon("industry", idx);
                      return (
                        <li
                          key={idx}
                          className="flex items-start gap-2 text-sm"
                        >
                          <BulletIcon className={`w-4 h-4 ${theme.iconColor} flex-shrink-0 mt-0.5`} />
                          <span>{benefit}</span>
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

      {/* ROI Section */}
      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Return on Investment
            </h2>
            <p className="text-lg text-muted-foreground">
              {data.roi.title}
            </p>
          </AnimatedSection>

          <AnimatedSection delay={0.2}>
            <Card className="bg-gradient-to-br from-category-industry/5 to-category-industry/10 border-border/50">
              <CardContent className="pt-8 pb-8">
                <div className="flex items-center gap-3 mb-8">
                  <div className={`w-12 h-12 rounded-lg ${theme.iconBg} flex items-center justify-center`}>
                    <BarChart3 className={`w-6 h-6 ${theme.iconColor}`} />
                  </div>
                  <h3 className="text-xl font-semibold text-foreground">
                    Key Metrics
                  </h3>
                </div>

                <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
                  {data.roi.stats.map((stat, index) => (
                    <div
                      key={index}
                      className="text-center p-4 rounded-lg bg-background/80 border border-border/50"
                    >
                      <div className="text-2xl md:text-3xl font-bold text-primary mb-1">
                        {stat.value}
                      </div>
                      <div className="text-sm text-muted-foreground">
                        {stat.label}
                      </div>
                    </div>
                  ))}
                </div>

                <div className="mt-8 p-4 bg-background/80 rounded-lg border border-border/50 text-center">
                  <p className="text-sm text-muted-foreground">
                    Based on typical South African enterprise deployments.
                    Actual results may vary based on volume and configuration.
                  </p>
                </div>
              </CardContent>
            </Card>
          </AnimatedSection>

          <AnimatedSection delay={0.3} className="mt-8 text-center">
            <Button asChild>
              <Link to="/request-demo">
                Calculate Your ROI
                <ArrowRight className="ml-2 w-4 h-4" />
              </Link>
            </Button>
          </AnimatedSection>
        </div>
      </section>

      {/* CTA Section */}
      <PageCTA variant="industry" />
    </>
  );
};

export default IndustryTemplate;
