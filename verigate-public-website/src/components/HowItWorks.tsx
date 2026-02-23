import { Card, CardContent } from "@/components/ui/card";
import { Upload, ScanSearch, CheckCircle2 } from "lucide-react";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

const steps = [
  {
    icon: Upload,
    step: "01",
    title: "Submit Information",
    description: "Upload documents and provide necessary details through our secure API or dashboard interface.",
  },
  {
    icon: ScanSearch,
    step: "02",
    title: "AI-Powered Verification",
    description: "Our advanced AI algorithms verify documents, check databases, and perform comprehensive risk assessments.",
  },
  {
    icon: CheckCircle2,
    step: "03",
    title: "Instant Results",
    description: "Receive detailed verification reports and compliance scores in real-time, with full audit trails.",
  },
];

const HowItWorks = () => {
  return (
    <section className="py-24 px-4">
      <div className="container mx-auto max-w-6xl">
        {/* Section Header */}
        <AnimatedSection className="max-w-2xl mb-16">
          <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold mb-4 text-foreground">
            How VeriGate Works
          </h2>
          <p className="text-lg text-muted-foreground">
            Simple, secure, and seamless verification in three easy steps
          </p>
        </AnimatedSection>
        
        {/* Steps */}
        <StaggeredList 
          className="grid md:grid-cols-3 gap-8 relative"
          staggerDelay={0.15}
          containerDelay={0.2}
        >
          {/* Connector lines - hidden on mobile */}
          <div className="hidden md:block absolute top-20 left-0 right-0 h-0.5 bg-gradient-to-r from-accent/20 via-accent to-accent/20 -z-10" />
          
          {steps.map((step, index) => (
            <Card 
              key={index} 
              className="border-border/50 hover:border-accent/50 transition-all duration-300 hover:shadow-lg relative bg-card"
            >
              <CardContent className="p-8 space-y-6">
                {/* Step Number Badge */}
                <div className="absolute -top-4 left-8 w-12 h-12 rounded-full bg-gradient-hero flex items-center justify-center text-primary-foreground font-bold border-4 border-background">
                  {step.step}
                </div>
                
                {/* Icon */}
                <div className="w-16 h-16 rounded-lg bg-accent/10 flex items-center justify-center mt-4">
                  <step.icon className="w-8 h-8 text-accent" />
                </div>
                
                {/* Content */}
                <h3 className="text-xl font-semibold text-foreground">
                  {step.title}
                </h3>
                <p className="text-muted-foreground leading-relaxed">
                  {step.description}
                </p>
              </CardContent>
            </Card>
          ))}
        </StaggeredList>
      </div>
    </section>
  );
};

export default HowItWorks;
