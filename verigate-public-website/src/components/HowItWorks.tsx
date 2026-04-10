import { Upload, ScanSearch, ShieldCheck, FileText, ArrowRight, ArrowDown } from "lucide-react";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

const steps = [
  {
    icon: Upload,
    step: "01",
    title: "Submit Request",
    description: "Upload candidate details and consent forms through our secure dashboard or API. Select the verification checks required.",
  },
  {
    icon: ScanSearch,
    step: "02",
    title: "Processing & Verification",
    description: "Our team cross-references information with DHA, SAPS, SAQA, credit bureaus, and previous employers across South Africa.",
  },
  {
    icon: ShieldCheck,
    step: "03",
    title: "Quality Assurance",
    description: "Every result undergoes rigorous QA review by our compliance team to ensure accuracy and regulatory adherence.",
  },
  {
    icon: FileText,
    step: "04",
    title: "Report Delivered",
    description: "Receive comprehensive, POPIA-compliant verification reports with clear findings, risk flags, and audit trails.",
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
            Simple, secure, and thorough verification in four steps
          </p>
        </AnimatedSection>

        {/* Steps — horizontal flow */}
        <StaggeredList
          className="flex flex-col lg:flex-row items-stretch gap-0"
          staggerDelay={0.15}
          containerDelay={0.2}
        >
          {steps.map((step, index) => (
            <div key={index} className="flex flex-col lg:flex-row items-center flex-1">
              {/* Step card */}
              <div className="flex flex-col items-center text-center flex-1 px-4">
                {/* Step Number + Icon */}
                <div className="relative mb-4">
                  <div className="w-16 h-16 rounded-full bg-gradient-hero flex items-center justify-center text-primary-foreground shadow-lg">
                    <step.icon className="w-7 h-7" />
                  </div>
                  <div className="absolute -top-1 -right-1 w-6 h-6 rounded-full bg-accent text-primary-foreground text-xs font-bold flex items-center justify-center border-2 border-background">
                    {step.step}
                  </div>
                </div>

                {/* Content */}
                <h3 className="text-lg font-semibold text-foreground mb-2">
                  {step.title}
                </h3>
                <p className="text-sm text-muted-foreground leading-relaxed max-w-[220px]">
                  {step.description}
                </p>
              </div>

              {/* Arrow connector */}
              {index < steps.length - 1 && (
                <>
                  {/* Horizontal arrow — desktop */}
                  <div className="hidden lg:flex items-center justify-center flex-shrink-0 px-2">
                    <div className="w-8 h-0.5 bg-border" />
                    <ArrowRight className="w-4 h-4 text-accent -ml-1" />
                  </div>
                  {/* Vertical arrow — mobile */}
                  <div className="flex lg:hidden items-center justify-center py-4">
                    <ArrowDown className="w-5 h-5 text-accent" />
                  </div>
                </>
              )}
            </div>
          ))}
        </StaggeredList>
      </div>
    </section>
  );
};

export default HowItWorks;
