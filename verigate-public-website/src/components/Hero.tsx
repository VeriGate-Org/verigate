import { Button } from "@/components/ui/button";
import { CheckCircle2 } from "lucide-react";
import heroBackground from "@/assets/hero-background.jpg";
import DotPattern from "@/components/DotPattern";

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
        <div className="max-w-3xl space-y-8">

          {/* Heading */}
          <h1 className="text-4xl md:text-6xl lg:text-7xl font-bold text-primary-foreground leading-tight">
            Real Time Risk Intelligence
            <span className="block mt-2 bg-gradient-to-r from-cyan-400 via-blue-400 to-cyan-500 bg-clip-text text-transparent">
              Redefined
            </span>
          </h1>

          {/* Subheading */}
          <p className="text-xl md:text-2xl text-primary-foreground/90 max-w-2xl">
            Comprehensive due diligence, KYC compliance, and digital identification solutions for modern businesses
          </p>

          {/* CTA Buttons */}
          <div className="flex flex-col sm:flex-row gap-4 items-start pt-4">
            <Button size="lg" variant="hero" className="min-w-[200px]">
              Request Demo
            </Button>
            <Button size="lg" variant="outline" className="min-w-[200px] border-primary-foreground/30 text-primary-foreground hover:bg-primary-foreground hover:text-primary">
              Learn More
            </Button>
          </div>

          {/* Trust Indicators */}
          <div className="flex flex-wrap gap-8 pt-12">
            <div className="flex items-center gap-2 text-primary-foreground/80">
              <CheckCircle2 className="w-5 h-5 text-accent" />
              <span className="text-sm font-medium">ISO 27001 Certified</span>
            </div>
            <div className="flex items-center gap-2 text-primary-foreground/80">
              <CheckCircle2 className="w-5 h-5 text-accent" />
              <span className="text-sm font-medium">GDPR Compliant</span>
            </div>
            <div className="flex items-center gap-2 text-primary-foreground/80">
              <CheckCircle2 className="w-5 h-5 text-accent" />
              <span className="text-sm font-medium">99.9% Uptime SLA</span>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom gradient fade */}
      <div className="absolute bottom-0 left-0 right-0 h-32 bg-gradient-to-t from-background to-transparent z-0" />
    </section>
  );
};

export default Hero;
