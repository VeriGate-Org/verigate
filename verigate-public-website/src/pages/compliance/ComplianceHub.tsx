import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { ArrowRight, Scale, ChevronRight } from "lucide-react";
import { Link } from "react-router-dom";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";
import { complianceData } from "@/data/compliance";

const ComplianceHub = () => {
  return (
    <>
      {/* Hero Section */}
      <section className="pt-24 pb-16 bg-gradient-to-br from-secondary via-background to-primary/5 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <AnimatedSection className="max-w-3xl mx-auto text-center">
            <Badge variant="secondary" className="mb-4 text-sm">
              <Scale className="w-3.5 h-3.5 mr-1.5" />
              Regulatory Compliance
            </Badge>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground">
              Compliance
            </h1>
            <p className="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto">
              Stay ahead of South African regulatory requirements with VeriGate's automated compliance checks. POPIA, FICA, KYC, and sector-specific regulations covered in a single platform.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
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
        </div>
      </section>

      {/* Compliance Areas Grid */}
      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Compliance Areas
            </h2>
            <p className="text-lg text-muted-foreground">
              Explore our compliance solutions covering the full spectrum of South African regulatory requirements
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-2 lg:grid-cols-3 gap-6"
            staggerDelay={0.08}
            containerDelay={0.15}
          >
            {complianceData.map((item) => (
              <Link
                key={item.slug}
                to={`/compliance/${item.slug}`}
                className="group"
              >
                <Card className="h-full border-border/50 hover:border-accent/50 transition-all duration-200 hover:shadow-xl hover:shadow-cyan-500/10 group-hover:-translate-y-1 bg-gradient-to-br from-card to-card/50 hover:from-card hover:to-accent/5">
                  <CardHeader>
                    <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center mb-3 group-hover:bg-primary/20 group-hover:scale-110 transition-all duration-200">
                      <Scale className="w-6 h-6 text-primary" />
                    </div>
                    <CardTitle className="text-xl flex items-center justify-between">
                      {item.title}
                      <ChevronRight className="w-5 h-5 text-muted-foreground group-hover:text-accent group-hover:translate-x-1 transition-all duration-200" />
                    </CardTitle>
                    <CardDescription className="leading-relaxed">
                      {item.description}
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <span className="inline-flex items-center gap-1.5 text-sm font-medium text-accent group-hover:gap-2.5 transition-all duration-200">
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

      {/* CTA Section */}
      <section className="py-24 px-4 bg-gradient-hero relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <AnimatedSection className="max-w-2xl space-y-8">
            <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold text-primary-foreground">
              Ensure Full Regulatory Compliance
            </h2>
            <p className="text-xl text-primary-foreground/90">
              Stay ahead of South African regulatory requirements with VeriGate's automated compliance checks. POPIA, FICA, and sector-specific regulations covered.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 items-start pt-4">
              <Button
                size="lg"
                className="min-w-[200px] bg-primary-foreground text-primary hover:bg-primary-foreground/90 shadow-lg hover:scale-105 transition-transform duration-200"
                asChild
              >
                <Link to="/request-demo">
                  Get Started
                  <ArrowRight className="ml-2 w-5 h-5" />
                </Link>
              </Button>
              <Button
                size="lg"
                variant="outline"
                className="min-w-[200px] border-2 border-primary-foreground/30 text-primary-foreground hover:bg-primary-foreground/10 hover:scale-105 transition-transform duration-200"
                asChild
              >
                <Link to="/pricing">View Pricing</Link>
              </Button>
            </div>
          </AnimatedSection>
        </div>
      </section>
    </>
  );
};

export default ComplianceHub;
