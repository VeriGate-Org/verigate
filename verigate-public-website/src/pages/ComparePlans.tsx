import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Check, X, ArrowRight, Zap, Building2, Rocket } from "lucide-react";
import { Link } from "react-router-dom";

const ComparePlans = () => {
  type FeatureValue = boolean | string;

  interface ComparisonRow {
    feature: string;
    starter: FeatureValue;
    professional: FeatureValue;
    enterprise: FeatureValue;
    category: string;
  }

  const comparisonRows: ComparisonRow[] = [
    // Verification Types
    { feature: "Criminal Record Checks (SAPS)", starter: true, professional: true, enterprise: true, category: "Verification Types" },
    { feature: "Identity Verification (DHA)", starter: true, professional: true, enterprise: true, category: "Verification Types" },
    { feature: "Qualification Verification (SAQA)", starter: false, professional: true, enterprise: true, category: "Verification Types" },
    { feature: "Employment History Checks", starter: false, professional: true, enterprise: true, category: "Verification Types" },
    { feature: "Credit Screening (TransUnion, Experian, XDS)", starter: false, professional: true, enterprise: true, category: "Verification Types" },
    { feature: "Business Verification (CIPC)", starter: false, professional: false, enterprise: true, category: "Verification Types" },
    { feature: "Address Verification", starter: false, professional: true, enterprise: true, category: "Verification Types" },
    { feature: "Biometric Verification", starter: false, professional: false, enterprise: true, category: "Verification Types" },

    // Volume & Pricing
    { feature: "Monthly Volume Limit", starter: "100", professional: "1,000", enterprise: "Unlimited", category: "Volume & Pricing" },
    { feature: "Price per Verification", starter: "R29", professional: "R22", enterprise: "Custom", category: "Volume & Pricing" },
    { feature: "Overage Rate", starter: "R35/check", professional: "R27/check", enterprise: "N/A", category: "Volume & Pricing" },

    // Technical Features
    { feature: "Dashboard Access", starter: true, professional: true, enterprise: true, category: "Technical Features" },
    { feature: "API Access", starter: false, professional: true, enterprise: true, category: "Technical Features" },
    { feature: "Webhooks", starter: false, professional: true, enterprise: true, category: "Technical Features" },
    { feature: "Bulk CSV Upload", starter: false, professional: true, enterprise: true, category: "Technical Features" },
    { feature: "Custom Workflow Builder", starter: false, professional: false, enterprise: true, category: "Technical Features" },
    { feature: "Advanced Analytics & BI Export", starter: false, professional: true, enterprise: true, category: "Technical Features" },

    // Support
    { feature: "Support Channel", starter: "Email", professional: "Email & Phone", enterprise: "24/7 Premium", category: "Support" },
    { feature: "Response Time SLA", starter: "24 hours", professional: "4 hours", enterprise: "2 hours", category: "Support" },
    { feature: "Dedicated Account Manager", starter: false, professional: false, enterprise: true, category: "Support" },
    { feature: "On-site Training & Onboarding", starter: false, professional: false, enterprise: true, category: "Support" },

    // Compliance & Branding
    { feature: "POPIA-Compliant Processing", starter: true, professional: true, enterprise: true, category: "Compliance & Branding" },
    { feature: "Uptime SLA", starter: "99.5%", professional: "99.9%", enterprise: "99.9%", category: "Compliance & Branding" },
    { feature: "Custom Branding", starter: false, professional: true, enterprise: true, category: "Compliance & Branding" },
    { feature: "White-Label Solution", starter: false, professional: false, enterprise: true, category: "Compliance & Branding" },
  ];

  const categories = [...new Set(comparisonRows.map((row) => row.category))];

  const renderValue = (value: FeatureValue) => {
    if (typeof value === "boolean") {
      return value ? (
        <Check className="w-5 h-5 text-accent mx-auto" />
      ) : (
        <X className="w-5 h-5 text-muted-foreground/40 mx-auto" />
      );
    }
    return <span className="text-sm font-medium">{value}</span>;
  };

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="max-w-4xl space-y-6">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              Compare Plans
            </h1>
            <p className="text-xl text-muted-foreground max-w-2xl">
              Detailed feature comparison across all VeriGate plans. All prices in
              ZAR. Find the right fit for your organisation.
            </p>
          </div>
        </div>
      </section>

      {/* Plan Header Cards */}
      <section className="py-12 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card className="border-border text-center">
              <CardHeader>
                <div className="mx-auto mb-4 w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                  <Zap className="w-6 h-6 text-accent" />
                </div>
                <CardTitle className="text-2xl">Starter</CardTitle>
                <CardDescription>For small businesses</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-bold text-foreground mb-1">R29</div>
                <p className="text-sm text-muted-foreground mb-4">/verification</p>
                <p className="text-sm text-muted-foreground mb-4">
                  Up to 100 verifications/month
                </p>
                <Button className="w-full" variant="outline" asChild>
                  <Link to="/request-demo">Get Started</Link>
                </Button>
              </CardContent>
            </Card>

            <Card className="border-accent shadow-lg text-center relative">
              <div className="absolute -top-4 left-1/2 -translate-x-1/2 bg-accent text-accent-foreground px-4 py-1 rounded-full text-sm font-semibold">
                Most Popular
              </div>
              <CardHeader>
                <div className="mx-auto mb-4 w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                  <Building2 className="w-6 h-6 text-accent" />
                </div>
                <CardTitle className="text-2xl">Professional</CardTitle>
                <CardDescription>For growing companies</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-bold text-foreground mb-1">R22</div>
                <p className="text-sm text-muted-foreground mb-4">/verification</p>
                <p className="text-sm text-muted-foreground mb-4">
                  Up to 1,000 verifications/month
                </p>
                <Button className="w-full" asChild>
                  <Link to="/request-demo">Get Started</Link>
                </Button>
              </CardContent>
            </Card>

            <Card className="border-border text-center">
              <CardHeader>
                <div className="mx-auto mb-4 w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                  <Rocket className="w-6 h-6 text-accent" />
                </div>
                <CardTitle className="text-2xl">Enterprise</CardTitle>
                <CardDescription>For large organisations</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-bold text-foreground mb-1">Custom</div>
                <p className="text-sm text-muted-foreground mb-4">Volume discounts</p>
                <p className="text-sm text-muted-foreground mb-4">
                  Unlimited verifications
                </p>
                <Button className="w-full" variant="outline" asChild>
                  <Link to="/contact">Contact Sales</Link>
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Comparison Table */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <Card className="border-border overflow-hidden">
            <CardContent className="p-0">
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="bg-secondary">
                      <th className="px-6 py-4 text-left font-semibold min-w-[280px]">
                        Feature
                      </th>
                      <th className="px-6 py-4 text-center font-semibold min-w-[140px]">
                        Starter
                      </th>
                      <th className="px-6 py-4 text-center font-semibold text-accent min-w-[140px]">
                        Professional
                      </th>
                      <th className="px-6 py-4 text-center font-semibold min-w-[140px]">
                        Enterprise
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {categories.map((category) => (
                      <>
                        <tr key={`cat-${category}`} className="bg-muted/50">
                          <td
                            colSpan={4}
                            className="px-6 py-3 font-semibold text-sm text-foreground"
                          >
                            {category}
                          </td>
                        </tr>
                        {comparisonRows
                          .filter((row) => row.category === category)
                          .map((row, index) => (
                            <tr
                              key={`${category}-${index}`}
                              className="border-t border-border hover:bg-secondary/50 transition-colors"
                            >
                              <td className="px-6 py-4 text-sm">{row.feature}</td>
                              <td className="px-6 py-4 text-center">
                                {renderValue(row.starter)}
                              </td>
                              <td className="px-6 py-4 text-center bg-accent/5">
                                {renderValue(row.professional)}
                              </td>
                              <td className="px-6 py-4 text-center">
                                {renderValue(row.enterprise)}
                              </td>
                            </tr>
                          ))}
                      </>
                    ))}
                  </tbody>
                </table>
              </div>
            </CardContent>
          </Card>

          <div className="mt-6 text-center text-sm text-muted-foreground">
            <p>
              All prices exclude VAT. Enterprise pricing is customised based on
              volume and requirements.
            </p>
            <p className="mt-1">
              Need help choosing?{" "}
              <Link to="/contact" className="text-accent hover:underline font-medium">
                Talk to our sales team
              </Link>
            </p>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <Card className="border-accent bg-gradient-to-br from-primary/5 to-accent/5">
            <CardHeader className="text-center pb-6">
              <CardTitle className="text-3xl md:text-4xl mb-4">
                Still Have Questions?
              </CardTitle>
              <CardDescription className="text-lg">
                Our team is ready to help you find the right plan for your organisation's
                screening needs.
              </CardDescription>
            </CardHeader>
            <CardContent className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" className="min-w-[200px]" asChild>
                <Link to="/request-demo">
                  Request a Demo
                  <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" className="min-w-[200px]" asChild>
                <Link to="/pricing">Back to Pricing</Link>
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  );
};

export default ComparePlans;
