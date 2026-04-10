import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Shield,
  Lightbulb,
  Target,
  Heart,
  GraduationCap,
  MapPin,
  Briefcase,
  Clock,
  ArrowRight,
  Mail,
  Users,
  Laptop,
  Calendar,
  Banknote,
  PartyPopper,
} from "lucide-react";
import { Link } from "react-router-dom";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";
import PageCTA from "@/components/PageCTA";

const Careers = () => {
  const cultureValues = [
    {
      icon: Target,
      title: "Mission-Driven",
      description:
        "Every verification we process helps organisations make better hiring decisions. Our work directly contributes to building trust in the South African economy.",
    },
    {
      icon: Users,
      title: "Collaborative",
      description:
        "We work as one team across engineering, compliance, sales, and operations. Cross-functional collaboration is how we ship great products.",
    },
    {
      icon: Lightbulb,
      title: "Growth-Oriented",
      description:
        "We invest in our people. Continuous learning, career progression, and personal development are core to who we are as a company.",
    },
  ];

  const positions = [
    {
      title: "Senior Full-Stack Developer",
      department: "Engineering",
      location: "Cape Town",
      type: "Full-time",
      description:
        "Build and scale our verification platform using TypeScript, React, Node.js, and AWS. You will work on integrations with DHA, SAPS, SAQA, and credit bureaus, ensuring high availability and POPIA-compliant data processing across our microservices architecture.",
    },
    {
      title: "Compliance Analyst",
      department: "Legal & Compliance",
      location: "Cape Town",
      type: "Full-time",
      description:
        "Ensure VeriGate remains fully compliant with POPIA, FICA, NCA, and industry regulations. Monitor legislative changes, conduct internal audits, manage our ISO 27001 certification process, and advise product and engineering teams on compliance requirements.",
    },
    {
      title: "Customer Success Manager",
      department: "Operations",
      location: "Cape Town",
      type: "Full-time",
      description:
        "Onboard new clients, drive platform adoption, and ensure long-term retention. Be the trusted advisor for our clients' screening workflows, helping them get maximum value from VeriGate while maintaining compliance with POPIA and FICA requirements.",
    },
    {
      title: "Data Engineer",
      department: "Engineering",
      location: "Remote (SA)",
      type: "Full-time",
      description:
        "Design and maintain our data pipelines, analytics infrastructure, and reporting systems. Work with verification data from DHA, SAPS, SAQA, and credit bureaus to build reliable, scalable data processing workflows that power our analytics dashboard.",
    },
    {
      title: "Business Development Manager",
      department: "Sales",
      location: "Johannesburg",
      type: "Full-time",
      description:
        "Drive new business across Gauteng and nationally. Sell VeriGate's background screening solutions to enterprise clients in banking, insurance, telecoms, and professional services. Build and manage a pipeline of opportunities and close deals.",
    },
  ];

  const benefits = [
    {
      icon: Heart,
      title: "Medical Aid Contribution",
      description:
        "Comprehensive medical aid contribution through Discovery Health for you and your dependants.",
    },
    {
      icon: Banknote,
      title: "Retirement Fund",
      description:
        "Company-matched retirement annuity contributions to help you plan for the future.",
    },
    {
      icon: Clock,
      title: "Flexible Hours",
      description:
        "Core hours from 10:00 to 15:00 with flexibility to start early or finish late to suit your schedule.",
    },
    {
      icon: Laptop,
      title: "Remote Work Options",
      description:
        "Work from home or our Cape Town office. We have team members across South Africa working remotely.",
    },
    {
      icon: GraduationCap,
      title: "Learning Budget",
      description:
        "Annual learning budget of R15,000 for courses, conferences, certifications, and books of your choice.",
    },
    {
      icon: PartyPopper,
      title: "Team Events",
      description:
        "Regular team socials, quarterly off-sites, and annual company retreats to celebrate our wins together.",
    },
  ];

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container relative z-10">
          <AnimatedSection>
            <div className="container mx-auto max-w-6xl text-center space-y-6">
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                Join the VeriGate Team
              </h1>
              <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
                We are a Cape Town-based team on a mission to modernise background
                screening for South African organisations. Join us and make a real
                impact on how businesses build trust.
              </p>
              <div className="flex flex-col sm:flex-row gap-4 justify-center pt-4">
                <Button size="lg" asChild>
                  <a href="#positions">
                    View Open Positions
                    <ArrowRight className="w-4 h-4 ml-2" />
                  </a>
                </Button>
                <Button size="lg" variant="outline" asChild>
                  <Link to="/about">Learn About Us</Link>
                </Button>
              </div>
            </div>
          </AnimatedSection>
        </div>
      </section>

      {/* Culture Section */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Our Culture
              </h2>
              <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
                VeriGate is built by people who care deeply about accuracy, compliance,
                and building products that make a difference.
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {cultureValues.map((value) => {
              const Icon = value.icon;
              return (
                <Card key={value.title} className="border-border text-center">
                  <CardHeader>
                    <div className="mx-auto w-16 h-16 rounded-lg bg-accent/10 flex items-center justify-center mb-4">
                      <Icon className="w-8 h-8 text-accent" />
                    </div>
                    <CardTitle className="text-xl">{value.title}</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <CardDescription className="text-sm">
                      {value.description}
                    </CardDescription>
                  </CardContent>
                </Card>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* Open Positions */}
      <section id="positions" className="py-20">
        <div className="container mx-auto max-w-4xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Open Positions
              </h2>
              <p className="text-lg text-muted-foreground">
                Find your next role at VeriGate. We are currently hiring for the
                following positions.
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="space-y-6">
            {positions.map((position) => (
              <Card
                key={position.title}
                className="border-border hover:shadow-lg transition-shadow"
              >
                <CardHeader>
                  <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
                    <div>
                      <CardTitle className="text-xl mb-2">
                        {position.title}
                      </CardTitle>
                      <div className="flex flex-wrap gap-2">
                        <Badge variant="secondary" className="text-xs">
                          <Briefcase className="w-3 h-3 mr-1" />
                          {position.department}
                        </Badge>
                        <Badge variant="secondary" className="text-xs">
                          <MapPin className="w-3 h-3 mr-1" />
                          {position.location}
                        </Badge>
                        <Badge variant="secondary" className="text-xs">
                          <Clock className="w-3 h-3 mr-1" />
                          {position.type}
                        </Badge>
                      </div>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <p className="text-muted-foreground text-sm mb-4">
                    {position.description}
                  </p>
                  <Button size="sm" asChild>
                    <a
                      href={`mailto:careers@verigate.co.za?subject=Application: ${position.title}`}
                    >
                      Apply Now
                      <ArrowRight className="w-4 h-4 ml-2" />
                    </a>
                  </Button>
                </CardContent>
              </Card>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* Benefits */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Benefits & Perks
              </h2>
              <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
                We invest in our people so they can do their best work
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {benefits.map((benefit) => {
              const Icon = benefit.icon;
              return (
                <Card key={benefit.title} className="border-border">
                  <CardHeader>
                    <div className="flex items-center gap-3">
                      <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center flex-shrink-0">
                        <Icon className="w-6 h-6 text-accent" />
                      </div>
                      <CardTitle className="text-lg">{benefit.title}</CardTitle>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <CardDescription className="text-sm">
                      {benefit.description}
                    </CardDescription>
                  </CardContent>
                </Card>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* Life at VeriGate Stats */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Life at VeriGate
              </h2>
            </div>
          </AnimatedSection>

          <StaggeredList className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {[
              {
                icon: Users,
                stat: "35+",
                label: "Team Members",
                detail: "Across Cape Town, Johannesburg, and remote",
              },
              {
                icon: MapPin,
                stat: "Cape Town",
                label: "Headquarters",
                detail: "With remote team members across South Africa",
              },
              {
                icon: GraduationCap,
                stat: "R15K",
                label: "Learning Budget",
                detail: "Annual professional development allowance per person",
              },
            ].map((item) => {
              const Icon = item.icon;
              return (
                <div key={item.label} className="text-center">
                  <div className="mx-auto w-14 h-14 rounded-full bg-accent/10 flex items-center justify-center mb-4">
                    <Icon className="w-7 h-7 text-accent" />
                  </div>
                  <div className="text-3xl font-bold text-accent mb-1">
                    {item.stat}
                  </div>
                  <div className="font-semibold text-foreground">
                    {item.label}
                  </div>
                  <p className="text-sm text-muted-foreground mt-1">
                    {item.detail}
                  </p>
                </div>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* CTA */}
      <PageCTA variant="careers" />
    </div>
  );
};

export default Careers;
