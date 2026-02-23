import { Card, CardContent } from "@/components/ui/card";
import { UserCheck, FileSearch, Fingerprint, Database, Shield, Zap } from "lucide-react";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

const features = [
  {
    icon: UserCheck,
    title: "KYC Verification",
    description: "Automated identity verification with document scanning, facial recognition, and liveness detection for seamless onboarding.",
  },
  {
    icon: FileSearch,
    title: "Due Diligence",
    description: "Comprehensive background checks, risk assessment, and compliance monitoring to ensure regulatory adherence.",
  },
  {
    icon: Fingerprint,
    title: "Digital Identity",
    description: "Secure digital identity creation and management with biometric authentication and encrypted data storage.",
  },
  {
    icon: Database,
    title: "Data Intelligence",
    description: "Access to global databases and registries for thorough verification and risk profiling across jurisdictions.",
  },
  {
    icon: Shield,
    title: "Compliance Automation",
    description: "Stay ahead of AML, CFT, and sanctions regulations with automated compliance checks and real-time monitoring.",
  },
  {
    icon: Zap,
    title: "Real-Time Processing",
    description: "Lightning-fast verification results with industry-leading accuracy and instant API responses.",
  },
];

const Features = () => {
  return (
    <section className="py-24 px-4 bg-gradient-mesh">
      <div className="container mx-auto max-w-6xl">
        {/* Section Header */}
        <AnimatedSection className="max-w-2xl mb-16">
          <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold mb-4 text-foreground">
            Complete Verification Suite
          </h2>
          <p className="text-lg text-muted-foreground">
            Everything you need to verify identities, ensure compliance, and mitigate risk in one powerful platform
          </p>
        </AnimatedSection>
        
        {/* Features Grid */}
        <StaggeredList 
          className="grid md:grid-cols-2 lg:grid-cols-3 gap-6"
          staggerDelay={0.1}
          containerDelay={0.2}
        >
          {features.map((feature, index) => (
            <Card 
              key={index} 
              className="border-border/50 hover:border-accent/50 transition-all duration-300 hover:shadow-xl hover:shadow-cyan-500/10 hover:-translate-y-1 group bg-gradient-to-br from-card to-card/50 hover:from-card hover:to-accent/5"
            >
              <CardContent className="p-6 space-y-4">
                <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center group-hover:bg-accent/20 group-hover:scale-110 transition-all duration-300">
                  <feature.icon className="w-6 h-6 text-accent" />
                </div>
                <h3 className="text-xl font-semibold text-foreground">
                  {feature.title}
                </h3>
                <p className="text-muted-foreground leading-relaxed">
                  {feature.description}
                </p>
              </CardContent>
            </Card>
          ))}
        </StaggeredList>
      </div>
    </section>
  );
};

export default Features;
