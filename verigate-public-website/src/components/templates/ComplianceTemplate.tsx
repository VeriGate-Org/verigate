import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  ArrowRight,
  ArrowDown,
  Shield,
  Scale,
  FileCheck,
} from "lucide-react";
import { Link } from "react-router-dom";
import { getCategoryTheme } from "@/lib/categoryTheme";
import { getBulletIcon } from "@/lib/contextIcons";
import { ComplianceFlow } from "@/components/illustrations/ComplianceFlow";
import PageCTA from "@/components/PageCTA";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

export interface ComplianceData {
  slug: string;
  title: string;
  subtitle: string;
  description: string;
  icon: string;
  requirements: { title: string; description: string }[];
  benefits: { title: string; description: string }[];
  process: { step: string; title: string; description: string }[];
  relatedCompliance: { slug: string; title: string; description: string }[];
  ctaTitle: string;
}

interface ComplianceTemplateProps {
  data: ComplianceData;
}

const ComplianceTemplate = ({ data }: ComplianceTemplateProps) => {
  const theme = getCategoryTheme("compliance");

  return (
    <>
      {/* Hero Section */}
      <section className="pt-24 pb-16 bg-gradient-to-br from-secondary via-background to-category-compliance/5 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <AnimatedSection>
              <Badge className="mb-4 text-sm bg-category-compliance/10 text-category-compliance border-category-compliance/30">
                <Scale className="w-3.5 h-3.5 mr-1.5" />
                Compliance
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
              <ComplianceFlow className="w-full max-w-sm" />
            </AnimatedSection>
          </div>
        </div>
      </section>

      {/* Requirements / Checks */}
      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Requirements & Checks
            </h2>
            <p className="text-lg text-muted-foreground">
              Key compliance requirements and the checks VeriGate performs to
              keep your organisation compliant in South Africa
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-2 lg:grid-cols-3 gap-6"
            staggerDelay={0.1}
            containerDelay={0.2}
          >
            {data.requirements.map((requirement, index) => (
              <Card
                key={index}
                className={`border-border/50 ${theme.cardHoverBorder} transition-all duration-200 hover:shadow-lg group`}
              >
                <CardHeader>
                  <div className="flex items-start gap-3">
                    <div className={`w-10 h-10 rounded-lg ${theme.iconBg} flex items-center justify-center flex-shrink-0 group-hover:bg-category-compliance/20 transition-colors duration-200`}>
                      <FileCheck className={`w-5 h-5 ${theme.iconColor}`} />
                    </div>
                    <div>
                      <CardTitle className="text-lg mb-1">
                        {requirement.title}
                      </CardTitle>
                      <CardDescription className="leading-relaxed">
                        {requirement.description}
                      </CardDescription>
                    </div>
                  </div>
                </CardHeader>
              </Card>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* Benefits */}
      <section className="py-20 px-4 bg-teal-50/30 dark:bg-teal-950/20">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Benefits
            </h2>
            <p className="text-lg text-muted-foreground">
              Why South African organisations choose VeriGate for compliance
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-2 gap-6"
            staggerDelay={0.1}
            containerDelay={0.2}
          >
            {data.benefits.map((benefit, index) => {
              const BulletIcon = getBulletIcon("compliance", index);
              return (
                <Card
                  key={index}
                  className={`border-border/50 ${theme.cardHoverBorder} transition-all duration-200 hover:shadow-xl ${theme.cardHoverShadow} hover:-translate-y-1 group bg-gradient-to-br from-card to-card/50 hover:from-card hover:to-category-compliance/5`}
                >
                  <CardContent className="p-6">
                    <div className="flex items-start gap-4">
                      <div className={`w-10 h-10 rounded-full ${theme.iconBg} flex items-center justify-center flex-shrink-0 group-hover:bg-category-compliance/20 group-hover:scale-110 transition-all duration-200`}>
                        <BulletIcon className={`w-5 h-5 ${theme.iconColor}`} />
                      </div>
                      <div>
                        <h3 className="text-lg font-semibold text-foreground mb-2">
                          {benefit.title}
                        </h3>
                        <p className="text-muted-foreground text-sm leading-relaxed">
                          {benefit.description}
                        </p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* Process Steps */}
      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Compliance Process
            </h2>
            <p className="text-lg text-muted-foreground">
              How VeriGate guides you through every step of the compliance
              journey
            </p>
          </AnimatedSection>

          <StaggeredList
            className="flex flex-col lg:flex-row items-stretch gap-0"
            staggerDelay={0.15}
            containerDelay={0.2}
          >
            {data.process.map((step, index) => (
              <div key={index} className="flex flex-col lg:flex-row items-center flex-1">
                {/* Step card */}
                <div className="flex flex-col items-center text-center flex-1 px-4">
                  {/* Step Number */}
                  <div className="w-14 h-14 rounded-full bg-gradient-hero flex items-center justify-center text-primary-foreground font-bold text-sm shadow-lg mb-4">
                    {step.step}
                  </div>

                  {/* Content */}
                  <h3 className="text-lg font-semibold text-foreground mb-2">
                    {step.title}
                  </h3>
                  <p className="text-sm text-muted-foreground leading-relaxed max-w-[220px]">
                    {step.description}
                  </p>
                </div>

                {/* Arrow connector */}
                {index < data.process.length - 1 && (
                  <>
                    {/* Horizontal arrow — desktop */}
                    <div className="hidden lg:flex items-center justify-center flex-shrink-0 px-2">
                      <div className="w-8 h-0.5 bg-border" />
                      <ArrowRight className="w-4 h-4 text-category-compliance -ml-1" />
                    </div>
                    {/* Vertical arrow — mobile */}
                    <div className="flex lg:hidden items-center justify-center py-4">
                      <ArrowDown className="w-5 h-5 text-category-compliance" />
                    </div>
                  </>
                )}
              </div>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* Related Compliance */}
      {data.relatedCompliance.length > 0 && (
        <section className="py-20 px-4 bg-teal-50/30 dark:bg-teal-950/20">
          <div className="container mx-auto max-w-6xl">
            <AnimatedSection className="max-w-2xl mb-16">
              <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
                Related Compliance Areas
              </h2>
              <p className="text-lg text-muted-foreground">
                Explore related regulatory and compliance frameworks
              </p>
            </AnimatedSection>

            <StaggeredList
              className="grid md:grid-cols-3 gap-6"
              staggerDelay={0.1}
              containerDelay={0.2}
            >
              {data.relatedCompliance.map((related, index) => (
                <Link
                  key={index}
                  to={`/compliance/${related.slug}`}
                  className="group"
                >
                  <Card className={`h-full border-border/50 ${theme.cardHoverBorder} transition-all duration-200 hover:shadow-lg group-hover:-translate-y-1`}>
                    <CardHeader>
                      <div className="flex items-center gap-2 mb-2">
                        <Shield className={`w-5 h-5 ${theme.iconColor}`} />
                        <CardTitle className="text-lg">
                          {related.title}
                        </CardTitle>
                      </div>
                      <CardDescription className="leading-relaxed">
                        {related.description}
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <span className="inline-flex items-center gap-1.5 text-sm font-medium text-category-compliance group-hover:gap-2.5 transition-all duration-200">
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
      <PageCTA variant="compliance" />
    </>
  );
};

export default ComplianceTemplate;
