import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Check, HelpCircle, ArrowRight, Zap, Building2, Rocket } from "lucide-react";
import { useState } from "react";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";

const Pricing = () => {
  const [billingCycle, setBillingCycle] = useState<"monthly" | "annual">("annual");

  const pricingPlans = [
    {
      name: "Starter",
      description: "Perfect for startups and small businesses getting started with identity verification",
      icon: Zap,
      monthlyPrice: 299,
      annualPrice: 249,
      annualSavings: "Save $600/year",
      features: [
        { text: "Up to 500 verifications/month", included: true },
        { text: "Basic KYC verification", included: true },
        { text: "Document verification", included: true },
        { text: "Email support", included: true },
        { text: "Standard API access", included: true },
        { text: "Basic analytics dashboard", included: true },
        { text: "99.5% uptime SLA", included: true },
        { text: "Advanced biometric verification", included: false },
        { text: "AML screening", included: false },
        { text: "Dedicated account manager", included: false },
        { text: "Custom workflows", included: false },
        { text: "White-label options", included: false },
      ],
      cta: "Start Free Trial",
      popular: false,
      highlight: false,
    },
    {
      name: "Professional",
      description: "Ideal for growing companies with higher verification volumes",
      icon: Building2,
      monthlyPrice: 799,
      annualPrice: 665,
      annualSavings: "Save $1,608/year",
      features: [
        { text: "Up to 5,000 verifications/month", included: true },
        { text: "Advanced KYC & KYB verification", included: true },
        { text: "Document + biometric verification", included: true },
        { text: "Priority email & chat support", included: true },
        { text: "Full API access with webhooks", included: true },
        { text: "Advanced analytics & reporting", included: true },
        { text: "99.9% uptime SLA", included: true },
        { text: "AML screening (basic)", included: true },
        { text: "Liveness detection", included: true },
        { text: "Custom branding", included: true },
        { text: "Multi-user accounts", included: true },
        { text: "Dedicated account manager", included: false },
        { text: "Custom workflows", included: false },
      ],
      cta: "Start Free Trial",
      popular: true,
      highlight: true,
    },
    {
      name: "Enterprise",
      description: "Custom solutions for large organizations with complex requirements",
      icon: Rocket,
      monthlyPrice: null,
      annualPrice: null,
      annualSavings: "Volume discounts available",
      features: [
        { text: "Unlimited verifications", included: true },
        { text: "Full KYC, KYB & AML suite", included: true },
        { text: "All verification methods", included: true },
        { text: "24/7 premium support", included: true },
        { text: "Custom API integration", included: true },
        { text: "Enterprise analytics & BI tools", included: true },
        { text: "99.99% uptime SLA", included: true },
        { text: "Advanced AML screening", included: true },
        { text: "Ongoing monitoring", included: true },
        { text: "White-label solution", included: true },
        { text: "Custom workflow builder", included: true },
        { text: "Dedicated account manager", included: true },
        { text: "On-premise deployment option", included: true },
        { text: "Custom SLA agreements", included: true },
      ],
      cta: "Contact Sales",
      popular: false,
      highlight: false,
    },
  ];

  const addons = [
    {
      name: "Advanced AML Screening",
      description: "Enhanced screening against global watchlists, PEPs, and sanctions lists",
      price: "$0.50 per check",
    },
    {
      name: "Ongoing Monitoring",
      description: "Continuous monitoring of verified identities for regulatory changes",
      price: "$99/month",
    },
    {
      name: "Premium Support",
      description: "24/7 dedicated support with 1-hour response time SLA",
      price: "$499/month",
    },
    {
      name: "Custom Integration",
      description: "Dedicated engineering support for custom integration projects",
      price: "Custom pricing",
    },
  ];

  const faqs = [
    {
      question: "What's included in the free trial?",
      answer: "Our 14-day free trial includes full access to all features in your selected plan, with up to 100 free verifications. No credit card required.",
    },
    {
      question: "What happens if I exceed my monthly verification limit?",
      answer: "Overages are billed at a reduced rate: $0.89 for Starter and $0.69 for Professional plans. We'll notify you when you reach 80% of your limit.",
    },
    {
      question: "Can I switch plans at any time?",
      answer: "Yes, you can upgrade or downgrade your plan at any time. Changes take effect immediately, and we'll prorate any billing differences.",
    },
    {
      question: "What payment methods do you accept?",
      answer: "We accept all major credit cards (Visa, MasterCard, American Express), ACH transfers for annual plans, and wire transfers for Enterprise customers.",
    },
    {
      question: "Is there a setup fee?",
      answer: "No setup fees for Starter and Professional plans. Enterprise plans may include custom integration services priced separately.",
    },
    {
      question: "What's your refund policy?",
      answer: "We offer a 30-day money-back guarantee for annual plans. Monthly plans can be cancelled anytime with no penalty.",
    },
    {
      question: "Do you offer discounts for nonprofits or educational institutions?",
      answer: "Yes, we offer up to 30% discount for qualified nonprofits and educational institutions. Contact our sales team for details.",
    },
    {
      question: "What regions do you support?",
      answer: "We support identity verification in 190+ countries with document recognition for 5,000+ document types. Regional compliance includes GDPR, CCPA, and other local regulations.",
    },
  ];

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="max-w-4xl space-y-6">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              Simple, Transparent Pricing
            </h1>
            <p className="text-xl text-muted-foreground max-w-2xl">
              Choose the perfect plan for your business. All plans include a 14-day free trial with no credit card required.
            </p>
            
            {/* Billing Toggle */}
            <div className="flex items-center gap-4 pt-8">
              <span className={`text-sm font-medium ${billingCycle === "monthly" ? "text-foreground" : "text-muted-foreground"}`}>
                Monthly
              </span>
              <button
                onClick={() => setBillingCycle(billingCycle === "monthly" ? "annual" : "monthly")}
                className={`relative w-16 h-8 rounded-full transition-colors ${
                  billingCycle === "annual" ? "bg-accent" : "bg-muted"
                }`}
                aria-label="Toggle billing cycle"
              >
                <span
                  className={`absolute top-1 left-1 w-6 h-6 bg-white rounded-full transition-transform ${
                    billingCycle === "annual" ? "translate-x-8" : ""
                  }`}
                />
              </button>
              <span className={`text-sm font-medium ${billingCycle === "annual" ? "text-foreground" : "text-muted-foreground"}`}>
                Annual
              </span>
              {billingCycle === "annual" && (
                <span className="text-sm font-semibold text-accent bg-accent/10 px-3 py-1 rounded-full">
                  Save up to 20%
                </span>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Pricing Cards */}
      <section className="py-12">
        <div className="container mx-auto max-w-6xl">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {pricingPlans.map((plan) => {
              const Icon = plan.icon;
              const price = billingCycle === "annual" ? plan.annualPrice : plan.monthlyPrice;
              
              return (
                <Card
                  key={plan.name}
                  className={`relative flex flex-col ${
                    plan.highlight
                      ? "border-accent shadow-lg scale-105 lg:scale-110"
                      : "border-border"
                  }`}
                >
                  {plan.popular && (
                    <div className="absolute -top-4 left-1/2 -translate-x-1/2 bg-accent text-accent-foreground px-4 py-1 rounded-full text-sm font-semibold">
                      Most Popular
                    </div>
                  )}
                  
                  <CardHeader className="text-center pb-8">
                    <div className="mx-auto mb-4 w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                      <Icon className="w-6 h-6 text-accent" />
                    </div>
                    <CardTitle className="text-2xl">{plan.name}</CardTitle>
                    <CardDescription className="text-sm mt-2">
                      {plan.description}
                    </CardDescription>
                  </CardHeader>
                  
                  <CardContent className="flex-grow">
                    {/* Pricing */}
                    <div className="text-center mb-8">
                      {price ? (
                        <>
                          <div className="flex items-baseline justify-center gap-2">
                            <span className="text-4xl font-bold text-foreground">
                              ${price}
                            </span>
                            <span className="text-muted-foreground">/month</span>
                          </div>
                          {billingCycle === "annual" && (
                            <p className="text-sm text-accent font-medium mt-2">
                              {plan.annualSavings}
                            </p>
                          )}
                        </>
                      ) : (
                        <div className="text-3xl font-bold text-foreground">
                          Custom
                        </div>
                      )}
                      {billingCycle === "annual" && price && (
                        <p className="text-xs text-muted-foreground mt-1">
                          Billed annually at ${price * 12}
                        </p>
                      )}
                    </div>

                    {/* Features */}
                    <ul className="space-y-3">
                      {plan.features.map((feature, index) => (
                        <li
                          key={index}
                          className={`flex items-start gap-3 text-sm ${
                            feature.included ? "text-foreground" : "text-muted-foreground"
                          }`}
                        >
                          <Check
                            className={`w-5 h-5 flex-shrink-0 mt-0.5 ${
                              feature.included ? "text-accent" : "text-muted-foreground/50"
                            }`}
                          />
                          <span className={!feature.included ? "line-through" : ""}>
                            {feature.text}
                          </span>
                        </li>
                      ))}
                    </ul>
                  </CardContent>
                  
                  <CardFooter className="pt-6">
                    <Button
                      className="w-full"
                      size="lg"
                      variant={plan.highlight ? "default" : "outline"}
                    >
                      {plan.cta}
                      <ArrowRight className="w-4 h-4 ml-2" />
                    </Button>
                  </CardFooter>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* Add-ons Section */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Optional Add-ons
            </h2>
            <p className="text-lg text-muted-foreground">
              Enhance your plan with additional features and services
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {addons.map((addon) => (
              <Card key={addon.name} className="border-border">
                <CardHeader>
                  <CardTitle className="text-lg">{addon.name}</CardTitle>
                  <CardDescription>{addon.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="flex items-center justify-between">
                    <span className="text-2xl font-bold text-accent">{addon.price}</span>
                    <Button variant="outline" size="sm">
                      Add to Plan
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Comparison Table */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Compare Plans
            </h2>
            <p className="text-lg text-muted-foreground">
              See all features side-by-side to find the right fit
            </p>
          </div>
          
          <div className="overflow-x-auto">
            <div className="inline-block min-w-full align-middle">
              <div className="overflow-hidden border border-border rounded-lg">
                <table className="min-w-full divide-y divide-border">
                  <thead className="bg-secondary/50">
                    <tr>
                      <th scope="col" className="py-4 px-6 text-left text-sm font-semibold text-foreground">
                        Feature
                      </th>
                      {pricingPlans.map((plan) => (
                        <th key={plan.name} scope="col" className="py-4 px-6 text-center text-sm font-semibold text-foreground">
                          {plan.name}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody className="bg-background divide-y divide-border">
                    <tr className="bg-secondary/20">
                      <td className="py-3 px-6 text-sm font-semibold text-foreground" colSpan={4}>
                        Verification Volume
                      </td>
                    </tr>
                    <tr>
                      <td className="py-4 px-6 text-sm text-foreground">Monthly verifications</td>
                      <td className="py-4 px-6 text-sm text-center text-muted-foreground">500</td>
                      <td className="py-4 px-6 text-sm text-center text-muted-foreground">5,000</td>
                      <td className="py-4 px-6 text-sm text-center text-muted-foreground">Unlimited</td>
                    </tr>
                    <tr className="bg-secondary/20">
                      <td className="py-3 px-6 text-sm font-semibold text-foreground" colSpan={4}>
                        Core Features
                      </td>
                    </tr>
                    <tr>
                      <td className="py-4 px-6 text-sm text-foreground">Document verification</td>
                      <td className="py-4 px-6 text-center"><Check className="w-5 h-5 text-accent mx-auto" /></td>
                      <td className="py-4 px-6 text-center"><Check className="w-5 h-5 text-accent mx-auto" /></td>
                      <td className="py-4 px-6 text-center"><Check className="w-5 h-5 text-accent mx-auto" /></td>
                    </tr>
                    <tr>
                      <td className="py-4 px-6 text-sm text-foreground">Biometric verification</td>
                      <td className="py-4 px-6 text-center"><span className="text-muted-foreground">-</span></td>
                      <td className="py-4 px-6 text-center"><Check className="w-5 h-5 text-accent mx-auto" /></td>
                      <td className="py-4 px-6 text-center"><Check className="w-5 h-5 text-accent mx-auto" /></td>
                    </tr>
                    <tr>
                      <td className="py-4 px-6 text-sm text-foreground">AML screening</td>
                      <td className="py-4 px-6 text-center"><span className="text-muted-foreground">-</span></td>
                      <td className="py-4 px-6 text-center text-sm text-muted-foreground">Basic</td>
                      <td className="py-4 px-6 text-center text-sm text-muted-foreground">Advanced</td>
                    </tr>
                    <tr className="bg-secondary/20">
                      <td className="py-3 px-6 text-sm font-semibold text-foreground" colSpan={4}>
                        Support & SLA
                      </td>
                    </tr>
                    <tr>
                      <td className="py-4 px-6 text-sm text-foreground">Support channels</td>
                      <td className="py-4 px-6 text-center text-sm text-muted-foreground">Email</td>
                      <td className="py-4 px-6 text-center text-sm text-muted-foreground">Email & Chat</td>
                      <td className="py-4 px-6 text-center text-sm text-muted-foreground">24/7 All channels</td>
                    </tr>
                    <tr>
                      <td className="py-4 px-6 text-sm text-foreground">Uptime SLA</td>
                      <td className="py-4 px-6 text-center text-sm text-muted-foreground">99.5%</td>
                      <td className="py-4 px-6 text-center text-sm text-muted-foreground">99.9%</td>
                      <td className="py-4 px-6 text-center text-sm text-muted-foreground">99.99%</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Frequently Asked Questions
            </h2>
            <p className="text-lg text-muted-foreground">
              Have questions? We've got answers.
            </p>
          </div>
          
          <div className="space-y-6">
            {faqs.map((faq, index) => (
              <Card key={index} className="border-border">
                <CardHeader>
                  <CardTitle className="text-lg flex items-start gap-3">
                    <TooltipProvider>
                      <Tooltip>
                        <TooltipTrigger>
                          <HelpCircle className="w-5 h-5 text-accent flex-shrink-0 mt-0.5" />
                        </TooltipTrigger>
                        <TooltipContent>
                          <p>Question {index + 1}</p>
                        </TooltipContent>
                      </Tooltip>
                    </TooltipProvider>
                    <span>{faq.question}</span>
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="text-muted-foreground">{faq.answer}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <Card className="border-accent bg-gradient-to-br from-primary/5 to-accent/5">
            <CardHeader className="text-center pb-6">
              <CardTitle className="text-3xl md:text-4xl mb-4">
                Ready to Get Started?
              </CardTitle>
              <CardDescription className="text-lg">
                Join hundreds of companies already using VeriGate to verify identities and prevent fraud.
              </CardDescription>
            </CardHeader>
            <CardContent className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" className="min-w-[200px]">
                Start Free Trial
                <ArrowRight className="w-4 h-4 ml-2" />
              </Button>
              <Button size="lg" variant="outline" className="min-w-[200px]">
                Schedule a Demo
              </Button>
            </CardContent>
            <CardFooter className="text-center text-sm text-muted-foreground">
              No credit card required • 14-day free trial • Cancel anytime
            </CardFooter>
          </Card>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Pricing;
