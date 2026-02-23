import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Shield, Users, Zap, Globe, Award, Target, Heart, Lightbulb } from "lucide-react";

const About = () => {
  const values = [
    {
      icon: Shield,
      title: "Security First",
      description: "We prioritize the security and privacy of our customers' data above all else, maintaining the highest industry standards.",
    },
    {
      icon: Users,
      title: "Customer Success",
      description: "Our customers' success is our success. We're committed to providing exceptional support and innovative solutions.",
    },
    {
      icon: Lightbulb,
      title: "Innovation",
      description: "We continuously invest in cutting-edge technology to stay ahead of evolving verification challenges.",
    },
    {
      icon: Heart,
      title: "Integrity",
      description: "We operate with transparency and honesty, building trust through our actions and commitments.",
    },
  ];

  const milestones = [
    {
      year: "2020",
      title: "Company Founded",
      description: "VeriGate was founded with a mission to make identity verification accessible and secure for businesses worldwide.",
    },
    {
      year: "2021",
      title: "Series A Funding",
      description: "Raised $10M to expand our platform and grow our team of verification experts.",
    },
    {
      year: "2022",
      title: "Global Expansion",
      description: "Opened offices in London and Singapore, serving customers across 50+ countries.",
    },
    {
      year: "2023",
      title: "1 Billion Verifications",
      description: "Processed our billionth identity verification, serving 500+ enterprise customers.",
    },
    {
      year: "2024",
      title: "AI-Powered Platform",
      description: "Launched next-generation AI verification engine with 99.8% accuracy rate.",
    },
  ];

  const team = [
    {
      name: "Sarah Johnson",
      role: "Chief Executive Officer",
      bio: "Former VP at Stripe, 15+ years in fintech and identity verification",
      image: "/placeholder.svg",
    },
    {
      name: "Michael Chen",
      role: "Chief Technology Officer",
      bio: "Ex-Google engineer, PhD in Computer Science from Stanford",
      image: "/placeholder.svg",
    },
    {
      name: "Emily Rodriguez",
      role: "Chief Product Officer",
      bio: "Previously led product at Onfido, expert in KYC compliance",
      image: "/placeholder.svg",
    },
    {
      name: "David Kim",
      role: "Chief Security Officer",
      bio: "20 years in cybersecurity, former CISO at major financial institutions",
      image: "/placeholder.svg",
    },
  ];

  const stats = [
    { value: "500+", label: "Enterprise Customers" },
    { value: "190+", label: "Countries Supported" },
    { value: "1B+", label: "Verifications Processed" },
    { value: "99.8%", label: "Accuracy Rate" },
  ];

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container relative z-10">
          <div className="container mx-auto max-w-6xl text-center space-y-6">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              Building Trust Through
              <span className="block text-accent mt-2">Verified Identity</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-2xl">
              We're on a mission to make the digital world safer and more accessible through enterprise-grade identity verification.
            </p>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-12 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {stats.map((stat) => (
              <div key={stat.label} className="text-center">
                <div className="text-4xl md:text-5xl font-bold text-accent mb-2">
                  {stat.value}
                </div>
                <div className="text-sm text-muted-foreground">
                  {stat.label}
                </div>
              </div>
            ))}
          </div>
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
              VeriGate was born from a simple observation: in an increasingly digital world, identity verification was becoming both more critical and more complex. Businesses needed a solution that was secure, compliant, and easy to implement.
            </p>
            <p>
              Founded in 2020 by a team of security experts and fintech veterans, we set out to build the identity verification platform we wished existed. One that combined cutting-edge AI technology with human expertise, regulatory compliance with user experience, and enterprise-grade security with developer-friendly APIs.
            </p>
            <p>
              Today, we serve over 500 enterprise customers across banking, fintech, gaming, healthcare, and e-commerce. We've processed over a billion identity verifications, helping businesses reduce fraud, ensure compliance, and build trust with their users.
            </p>
            <p>
              But we're just getting started. As digital transformation accelerates and new regulations emerge, our mission remains the same: to make identity verification simple, secure, and accessible for everyone.
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
                  <div className="mx-auto w-32 h-32 rounded-full bg-gradient-to-br from-primary to-accent mb-4 flex items-center justify-center text-4xl font-bold text-white">
                    {member.name.split(' ').map(n => n[0]).join('')}
                  </div>
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
              Trusted by enterprises worldwide
            </p>
          </div>
          
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {['ISO 27001', 'SOC 2 Type II', 'GDPR', 'CCPA'].map((cert) => (
              <div key={cert} className="flex flex-col items-center justify-center p-6 border border-border rounded-lg">
                <Award className="w-12 h-12 text-accent mb-4" />
                <span className="font-semibold text-center">{cert}</span>
                <span className="text-xs text-muted-foreground mt-2">Certified</span>
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
            We're always looking for talented individuals who share our passion for building secure, accessible digital experiences.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Button size="lg" variant="default">
              View Open Positions
            </Button>
            <Button size="lg" variant="outline">
              Learn About Our Culture
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default About;
