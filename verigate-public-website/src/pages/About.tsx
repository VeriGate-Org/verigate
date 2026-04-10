import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Shield, Users, Zap, Heart, Award, Target } from "lucide-react";
import { Link } from "react-router-dom";
import { StatsBar } from "@/components/StatsBar";

const About = () => {
  const values = [
    {
      icon: Target,
      title: "Accuracy",
      description: "We are committed to delivering verification results you can trust, with a 99.2% accuracy rate across all check types.",
    },
    {
      icon: Zap,
      title: "Speed",
      description: "Fast turnaround without compromising quality. Most verifications completed within 24 hours.",
    },
    {
      icon: Shield,
      title: "Compliance",
      description: "Full adherence to POPIA, FICA, and international standards including ISO 27001 and SOC 2 Type II.",
    },
    {
      icon: Heart,
      title: "Integrity",
      description: "We operate with transparency and honesty, building trust through our actions and long-term client relationships.",
    },
  ];

  const milestones = [
    {
      year: "2020",
      title: "Company Founded",
      description: "VeriGate was founded in Cape Town with a mission to modernise background screening in South Africa.",
    },
    {
      year: "2021",
      title: "Platform Launch",
      description: "Launched our cloud-based verification platform with integrations to DHA, SAPS, and SAQA.",
    },
    {
      year: "2022",
      title: "100 Clients Milestone",
      description: "Reached 100 active clients across banking, insurance, telecoms, and professional services.",
    },
    {
      year: "2023",
      title: "API & Bulk Processing",
      description: "Launched REST API and bulk upload capabilities, enabling enterprise-scale verification processing.",
    },
    {
      year: "2024",
      title: "ISO 27001 Certification",
      description: "Achieved ISO 27001 certification and SOC 2 Type II compliance, reinforcing our security commitment.",
    },
    {
      year: "2025",
      title: "200+ Clients, 50K+ Verifications",
      description: "Surpassed 200 clients and 50,000 verifications processed with 99.2% accuracy rate.",
    },
  ];

  const team = [
    {
      name: "Thabo Ndlovu",
      role: "Chief Executive Officer",
      bio: "15+ years in fintech and risk management. Former Head of Compliance at a leading SA bank.",
      photo: "https://images.unsplash.com/photo-1659444003277-6cb0a5ffc8bd?w=200&h=200&fit=crop&crop=face",
    },
    {
      name: "Sarah van der Merwe",
      role: "Chief Technology Officer",
      bio: "Software architect with expertise in secure platforms. Previously led engineering at a Cape Town fintech startup.",
      photo: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&h=200&fit=crop&crop=face",
    },
    {
      name: "James Motsepe",
      role: "Head of Compliance",
      bio: "Regulatory expert specialising in FICA, POPIA, and financial services compliance across Southern Africa.",
      photo: "https://images.unsplash.com/photo-1698885765700-77c5a9b5cc8a?w=200&h=200&fit=crop&crop=face",
    },
    {
      name: "Priya Naidoo",
      role: "Head of Operations",
      bio: "Operations leader with a track record of scaling verification teams and processes across multiple industries.",
      photo: "https://images.unsplash.com/photo-1659355894139-ca46ea6fa67a?w=200&h=200&fit=crop&crop=face",
    },
  ];

  const stats = [
    { value: "200+", label: "Clients Nationwide" },
    { value: "50,000+", label: "Verifications Completed" },
    { value: "99.2%", label: "Accuracy Rate" },
    { value: "24hr", label: "Average Turnaround" },
  ];

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container relative z-10">
          <div className="container mx-auto max-w-6xl text-center space-y-6">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              Modernising Background Screening
              <span className="block text-accent mt-2">for South Africa</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
              We're on a mission to make background verification faster, more accurate, and fully compliant for South African organisations.
            </p>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-12 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <StatsBar stats={stats} />
        </div>
      </section>

      {/* Our Story */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Our Story
            </h2>
          </div>

          <div className="prose prose-lg max-w-none text-muted-foreground space-y-6">
            <p>
              VeriGate was founded in 2020 in Cape Town by a team of compliance professionals and technology experts who saw a critical gap in South Africa's background screening industry. Traditional verification processes were slow, manual, and fragmented — taking days or weeks to complete while businesses needed results in hours.
            </p>
            <p>
              We built VeriGate to change that. Our cloud-based platform connects directly to South Africa's key data sources — the Department of Home Affairs, South African Police Service, South African Qualifications Authority, credit bureaus, and professional bodies — to deliver fast, accurate, and POPIA-compliant verification results.
            </p>
            <p>
              Today, we serve over 200 organisations across banking, insurance, telecoms, healthcare, and professional services. We've processed more than 50,000 verifications with a 99.2% accuracy rate, helping our clients make confident hiring decisions while meeting their regulatory obligations.
            </p>
            <p>
              As South Africa's regulatory landscape continues to evolve, we remain committed to staying ahead — ensuring our platform meets every compliance requirement so our clients can focus on what they do best.
            </p>
          </div>
        </div>
      </section>

      {/* Core Values */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Our Core Values
            </h2>
            <p className="text-lg text-muted-foreground">
              The principles that guide everything we do
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {values.map((value) => {
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
          </div>
        </div>
      </section>

      {/* Timeline */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Our Journey
            </h2>
            <p className="text-lg text-muted-foreground">
              Key milestones in our growth story
            </p>
          </div>

          <div className="space-y-8">
            {milestones.map((milestone, index) => (
              <div key={milestone.year} className="flex gap-6 items-start">
                <div className="flex flex-col items-center">
                  <div className="w-16 h-16 rounded-full bg-accent text-accent-foreground font-bold flex items-center justify-center text-lg shrink-0">
                    {milestone.year}
                  </div>
                  {index < milestones.length - 1 && (
                    <div className="w-0.5 h-full min-h-[60px] bg-border mt-4" />
                  )}
                </div>
                <Card className="flex-1 border-border">
                  <CardHeader>
                    <CardTitle>{milestone.title}</CardTitle>
                    <CardDescription>{milestone.description}</CardDescription>
                  </CardHeader>
                </Card>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Leadership Team */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Leadership Team
            </h2>
            <p className="text-lg text-muted-foreground">
              Meet the experienced leaders driving our vision forward
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {team.map((member) => (
              <Card key={member.name} className="border-border text-center">
                <CardHeader>
                  {member.photo ? (
                    <img src={member.photo} alt={member.name} className="mx-auto w-32 h-32 rounded-full object-cover mb-4 ring-4 ring-primary/10" loading="lazy" />
                  ) : (
                    <div className="mx-auto w-32 h-32 rounded-full bg-gradient-to-br from-primary to-accent mb-4 flex items-center justify-center text-4xl font-bold text-white">
                      {member.name.split(' ').map(n => n[0]).join('')}
                    </div>
                  )}
                  <CardTitle className="text-lg">{member.name}</CardTitle>
                  <CardDescription className="text-sm font-semibold text-accent">
                    {member.role}
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground">
                    {member.bio}
                  </p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Certifications */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Certifications & Compliance
            </h2>
            <p className="text-lg text-muted-foreground">
              Meeting the highest standards for data security and regulatory compliance
            </p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {[
              { name: "POPIA", logo: null },
              { name: "ISO 27001", logo: "/logos/certifications/iso-27001.svg" },
              { name: "SOC 2 Type II", logo: "/logos/certifications/soc2.svg" },
              { name: "FICA", logo: null },
            ].map((cert) => (
              <div key={cert.name} className="flex flex-col items-center justify-center p-6 border border-border rounded-lg">
                {cert.logo ? (
                  <img
                    src={cert.logo}
                    alt={cert.name}
                    className="h-12 w-auto object-contain mb-4"
                  />
                ) : (
                  <Award className="w-12 h-12 text-accent mb-4" />
                )}
                <span className="font-semibold text-center">{cert.name}</span>
                <span className="text-xs text-muted-foreground mt-2">Compliant</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl text-center">
          <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
            Join Our Team
          </h2>
          <p className="text-lg text-muted-foreground mb-8">
            We're always looking for talented individuals who share our passion for building secure, compliant verification solutions for South Africa.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Button size="lg" variant="default" asChild>
              <Link to="/careers">View Open Positions</Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link to="/contact">Get in Touch</Link>
            </Button>
          </div>
        </div>
      </section>
    </div>
  );
};

export default About;
