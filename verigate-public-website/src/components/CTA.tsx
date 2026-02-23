import { Button } from "@/components/ui/button";
import { ArrowRight } from "lucide-react";
import { AnimatedSection } from "@/components/AnimatedSection";

const CTA = () => {
  return (
    <section className="py-24 px-4 bg-gradient-hero relative overflow-hidden">
      {/* Decorative elements */}
      <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
      
      <div className="container mx-auto max-w-6xl relative z-10">
        <AnimatedSection className="max-w-2xl space-y-8">
          <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold text-primary-foreground">
            Ready to Streamline Your Verification Process?
          </h2>
          <p className="text-xl text-primary-foreground/90">
            Join leading organizations worldwide who trust VeriGate for secure, compliant identity verification
          </p>
          
          <div className="flex flex-col sm:flex-row gap-4 items-start pt-4">
            <Button 
              size="lg" 
              className="min-w-[200px] bg-primary-foreground text-primary hover:bg-primary-foreground/90 shadow-lg hover:scale-105 transition-transform duration-200"
            >
              Get Started
              <ArrowRight className="ml-2 w-5 h-5" />
            </Button>
            <Button 
              size="lg" 
              variant="outline"
              className="min-w-[200px] border-2 border-primary-foreground/30 text-primary-foreground hover:bg-primary-foreground/10 hover:scale-105 transition-transform duration-200"
            >
              Schedule Demo
            </Button>
          </div>
        </AnimatedSection>
      </div>
    </section>
  );
};

export default CTA;
