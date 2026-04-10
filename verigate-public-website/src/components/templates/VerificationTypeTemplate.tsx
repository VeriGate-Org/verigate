import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  ArrowRight,
  ArrowDown,
  Shield,
  Clock,
  Zap,
} from "lucide-react";
import { Link } from "react-router-dom";
import { getCategoryTheme } from "@/lib/categoryTheme";
import { getBulletIcon } from "@/lib/contextIcons";
import { ShieldVerification } from "@/components/illustrations/ShieldVerification";
import PageCTA from "@/components/PageCTA";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

export interface VerificationTypeData {
  slug: string;
  title: string;
  subtitle: string;
  description: string;
  icon: string;
  turnaround: string;
  howItWorks: { step: string; title: string; description: string }[];
  features: { title: string; description: string; items: string[] }[];
  useCases: { title: string; description: string }[];
  relatedTypes: { slug: string; title: string; description: string }[];
  ctaTitle: string;
}

interface VerificationTypeTemplateProps {
  data: VerificationTypeData;
}

const VerificationTypeTemplate = ({ data }: VerificationTypeTemplateProps) => {
  const theme = getCategoryTheme("verification");

  return (
    <>
      {/* Hero Section */}
      <section className="pt-24 pb-16 bg-gradient-to-br from-secondary via-background to-category-verification/5 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <AnimatedSection>
              <Badge className="mb-4 text-sm bg-category-verification/10 text-category-verification border-category-verification/30">
                <Shield className="w-3.5 h-3.5 mr-1.5" />
                Verification Type
              </Badge>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground">
                {data.title}
                <span className="block mt-2 text-primary">{data.subtitle}</span>
              </h1>
              <p className="text-xl text-muted-foreground mb-8 max-w-2xl">
                {data.description}
              </p>

              {/* Turnaround stat */}
              <div className="inline-flex items-center gap-2 bg-category-verification/10 border border-category-verification/20 rounded-full px-5 py-2.5 mb-8">
                <Clock className="w-5 h-5 text-category-verification" />
                <span className="text-sm font-semibold text-foreground">
                  Typical Turnaround:
                </span>
                <span className="text-sm font-bold text-category-verification">
                  {data.turnaround}
                </span>
              </div>

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
              <ShieldVerification className="w-full max-w-sm" />
            </AnimatedSection>
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              How It Works
            </h2>
            <p className="text-lg text-muted-foreground">
              A simple, streamlined process from request to verified result
            </p>
          </AnimatedSection>

          <StaggeredList
            className="flex flex-col lg:flex-row items-stretch gap-0"
            staggerDelay={0.15}
            containerDelay={0.2}
          >
            {data.howItWorks.map((step, index) => (
              <div key={index} className="flex flex-col lg:flex-row items-center flex-1">
                {/* Step card */}
                <div className="flex flex-col items-center text-center flex-1 px-4">
                  {/* Step Number */}
                  <div className="relative mb-4">
                    <div className="w-16 h-16 rounded-full bg-gradient-hero flex items-center justify-center text-primary-foreground shadow-lg">
                      <Zap className="w-7 h-7" />
                    </div>
                    <div className="absolute -top-1 -right-1 w-6 h-6 rounded-full bg-category-verification text-primary-foreground text-xs font-bold flex items-center justify-center border-2 border-background">
                      {step.step}
                    </div>
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
                {index < data.howItWorks.length - 1 && (
                  <>
                    {/* Horizontal arrow — desktop */}
                    <div className="hidden lg:flex items-center justify-center flex-shrink-0 px-2">
                      <div className="w-8 h-0.5 bg-border" />
                      <ArrowRight className="w-4 h-4 text-category-verification -ml-1" />
                    </div>
                    {/* Vertical arrow — mobile */}
                    <div className="flex lg:hidden items-center justify-center py-4">
                      <ArrowDown className="w-5 h-5 text-category-verification" />
                    </div>
                  </>
                )}
              </div>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* Key Features */}
      <section className="py-20 px-4 bg-blue-50/30 dark:bg-blue-950/20">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Key Features
            </h2>
            <p className="text-lg text-muted-foreground">
              Comprehensive capabilities built for South African organisations
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-3 gap-8"
            staggerDelay={0.12}
            containerDelay={0.2}
          >
            {data.features.map((feature, index) => (
              <Card
                key={index}
                className={`border-border/50 ${theme.cardHoverBorder} transition-all duration-200 hover:shadow-xl ${theme.cardHoverShadow} hover:-translate-y-1 group bg-gradient-to-br from-card to-card/50 hover:from-card hover:to-category-verification/5`}
              >
                <CardHeader>
                  <CardTitle className="text-xl">{feature.title}</CardTitle>
                  <CardDescription>{feature.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2.5">
                    {feature.items.map((item, idx) => {
                      const BulletIcon = getBulletIcon("verification", idx);
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

      {/* Use Cases */}
      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Use Cases
            </h2>
            <p className="text-lg text-muted-foreground">
              How South African organisations leverage this verification type
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-2 gap-6"
            staggerDelay={0.1}
            containerDelay={0.2}
          >
            {data.useCases.map((useCase, index) => {
              const BulletIcon = getBulletIcon("verification", index);
              return (
                <Card
                  key={index}
                  className="border-border/50 hover:shadow-lg transition-all duration-200"
                >
                  <CardHeader>
                    <div className="flex items-start gap-3">
                      <div className={`w-10 h-10 rounded-lg ${theme.iconBg} flex items-center justify-center flex-shrink-0`}>
                        <BulletIcon className={`w-5 h-5 ${theme.iconColor}`} />
                      </div>
                      <div>
                        <CardTitle className="text-lg mb-1">
                          {useCase.title}
                        </CardTitle>
                        <CardDescription className="leading-relaxed">
                          {useCase.description}
                        </CardDescription>
                      </div>
                    </div>
                  </CardHeader>
                </Card>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* Related Verification Types */}
      {data.relatedTypes.length > 0 && (
        <section className="py-20 px-4 bg-blue-50/30 dark:bg-blue-950/20">
          <div className="container mx-auto max-w-6xl">
            <AnimatedSection className="max-w-2xl mb-16">
              <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
                Related Verification Types
              </h2>
              <p className="text-lg text-muted-foreground">
                Explore other verification checks that complement this service
              </p>
            </AnimatedSection>

            <StaggeredList
              className="grid md:grid-cols-3 gap-6"
              staggerDelay={0.1}
              containerDelay={0.2}
            >
              {data.relatedTypes.map((related, index) => (
                <Link
                  key={index}
                  to={`/verification-types/${related.slug}`}
                  className="group"
                >
                  <Card className={`h-full border-border/50 ${theme.cardHoverBorder} transition-all duration-200 hover:shadow-lg group-hover:-translate-y-1`}>
                    <CardHeader>
                      <CardTitle className="text-lg flex items-center justify-between">
                        {related.title}
                        <ArrowRight className="w-4 h-4 text-muted-foreground group-hover:text-category-verification group-hover:translate-x-1 transition-all duration-200" />
                      </CardTitle>
                      <CardDescription className="leading-relaxed">
                        {related.description}
                      </CardDescription>
                    </CardHeader>
                  </Card>
                </Link>
              ))}
            </StaggeredList>
          </div>
        </section>
      )}

      {/* CTA Section */}
      <PageCTA variant="verification" />
    </>
  );
};

export default VerificationTypeTemplate;
