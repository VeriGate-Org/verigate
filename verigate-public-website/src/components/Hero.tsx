import { Button } from "@/components/ui/button";
import { ShieldCheck, Award, BadgeCheck } from "lucide-react";
import { Link } from "react-router-dom";
import heroBackground from "@/assets/hero-background.jpg";
import DotPattern from "@/components/DotPattern";
import { ShieldVerification } from "@/components/illustrations";

const trustIndicators = [
  { icon: ShieldCheck, label: "POPIA Compliant" },
  { icon: Award, label: "ISO 27001 Certified" },
  { icon: BadgeCheck, label: "SOC 2 Type II" },
];

const Hero = () => {
  return (
    <section className="relative min-h-screen flex items-center justify-center overflow-hidden pt-16">
      {/* Background with gradient overlay */}
      <div className="absolute inset-0 z-0">
        <img
          src={heroBackground}
          alt="VeriGate Security Background"
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-gradient-to-br from-primary/95 via-primary/90 to-accent/80" />
      </div>

      {/* Mesh gradient overlay */}
      <div className="absolute inset-0 bg-gradient-mesh z-0" />

      {/* Dot pattern */}
      <DotPattern opacity={0.15} />

      {/* Floating shapes */}
      <div className="absolute top-20 right-10 w-64 h-64 bg-cyan-400/20 rounded-full blur-3xl animate-float" />
      <div className="absolute bottom-20 left-10 w-96 h-96 bg-primary/20 rounded-full blur-3xl animate-float-delayed" />
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-80 h-80 bg-blue-400/10 rounded-full blur-3xl animate-pulse-slow" />

      {/* Content */}
      <div className="container mx-auto max-w-6xl relative z-10 py-20 md:py-32">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          <div className="space-y-8">
            {/* Heading */}
            <h1 className="text-4xl md:text-6xl lg:text-7xl font-bold text-primary-foreground leading-tight">
              Enterprise Verification Platform
              <span className="block mt-2 bg-gradient-to-r from-cyan-400 via-blue-400 to-cyan-500 bg-clip-text text-transparent">
                Trusted Background Screening for South Africa
              </span>
            </h1>

            {/* Subheading */}
            <p className="text-xl md:text-2xl text-primary-foreground/90 max-w-2xl">
              Comprehensive criminal checks, qualification verification, employment history, and identity validation for modern South African businesses
            </p>

            {/* CTA Buttons */}
            <div className="flex flex-col sm:flex-row gap-4 items-start pt-4">
              <Button size="lg" variant="hero" className="min-w-[200px]" asChild>
                <Link to="/request-demo">Get Started</Link>
              </Button>
              <Button size="lg" variant="outline" className="min-w-[200px] border-primary-foreground/30 text-primary-foreground hover:bg-primary-foreground hover:text-primary" asChild>
                <Link to="/platform">View Platform</Link>
              </Button>
            </div>

            {/* Trust Indicators */}
            <div className="flex flex-wrap gap-8 pt-12">
              {trustIndicators.map((item) => (
                <div key={item.label} className="flex items-center gap-2 text-primary-foreground/80">
                  <item.icon className="w-5 h-5 text-accent" />
                  <span className="text-sm font-medium">{item.label}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Illustration */}
          <div className="hidden lg:flex items-center justify-center">
            <ShieldVerification className="w-full max-w-md opacity-90" />
          </div>
        </div>
      </div>

      {/* Bottom gradient fade */}
      <div className="absolute bottom-0 left-0 right-0 h-32 bg-gradient-to-t from-background to-transparent z-0" />
    </section>
  );
};

export default Hero;
