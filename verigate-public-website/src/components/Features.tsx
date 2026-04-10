import { Card, CardContent } from "@/components/ui/card";
import { UserCheck, FileSearch, Fingerprint, GraduationCap, CreditCard, Shield } from "lucide-react";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

const features = [
  {
    icon: Shield,
    title: "Criminal Checks",
    description: "Comprehensive SAPS criminal record checks across all South African jurisdictions. Results within 24 hours for most cases.",
  },
  {
    icon: GraduationCap,
    title: "Qualification Verification",
    description: "Verify degrees, diplomas, and professional qualifications through SAQA, universities, and professional bodies like HPCSA.",
  },
  {
    icon: UserCheck,
    title: "Employment History",
    description: "Thorough employment history verification including dates, job titles, and reason for leaving with previous employers.",
  },
  {
    icon: Fingerprint,
    title: "Identity Validation",
    description: "Validate South African IDs, passports, and work permits against Department of Home Affairs records.",
  },
  {
    icon: CreditCard,
    title: "Credit Screening",
    description: "Credit bureau checks through TransUnion SA, Experian SA, and XDS for comprehensive financial risk assessment.",
  },
  {
    icon: FileSearch,
    title: "Compliance Monitoring",
    description: "Ongoing FICA, POPIA, and sector-specific compliance monitoring with automated alerts and audit trails.",
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
            Everything you need to screen candidates, verify identities, and ensure compliance in one powerful platform
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
              className="border-border/50 hover:border-accent/50 transition-all duration-200 hover:shadow-xl hover:shadow-cyan-500/10 hover:-translate-y-1 group bg-gradient-to-br from-card to-card/50 hover:from-card hover:to-accent/5"
            >
              <CardContent className="p-6 space-y-4">
                <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center group-hover:bg-accent/20 group-hover:scale-110 transition-all duration-200">
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
