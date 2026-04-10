import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Check, HelpCircle, ArrowRight, Zap, Building2, Rocket } from "lucide-react";
import { useState } from "react";
import { Link } from "react-router-dom";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";

const Pricing = () => {
  const pricingPlans = [
    {
      name: "Starter",
      description: "For small businesses and startups getting started with background screening",
      icon: Zap,
      pricePerVerification: "R29",
      volumeLimit: "Up to 100 verifications/month",
      features: [
        { text: "Up to 100 verifications/month", included: true },
        { text: "Criminal record checks", included: true },
        { text: "Identity verification (DHA)", included: true },
        { text: "Email support", included: true },
        { text: "Dashboard access", included: true },
        { text: "Basic reporting", included: true },
        { text: "POPIA-compliant processing", included: true },
        { text: "Qualification verification", included: false },
        { text: "Credit screening", included: false },
        { text: "API access", included: false },
        { text: "Bulk upload", included: false },
        { text: "Dedicated account manager", included: false },
      ],
      cta: "Get Started",
      popular: false,
      highlight: false,
    },
    {
      name: "Professional",
      description: "For growing companies with higher screening volumes and advanced needs",
      icon: Building2,
      pricePerVerification: "R22",
      volumeLimit: "Up to 1,000 verifications/month",
      features: [
        { text: "Up to 1,000 verifications/month", included: true },
        { text: "All Starter verification types", included: true },
        { text: "Qualification verification (SAQA)", included: true },
        { text: "Employment history checks", included: true },
        { text: "Credit screening (TransUnion, Experian)", included: true },
        { text: "Priority email & phone support", included: true },
        { text: "Full API access with webhooks", included: true },
        { text: "Bulk upload processing", included: true },
        { text: "Advanced analytics & reporting", included: true },
        { text: "Custom branding", included: true },
        { text: "Dedicated account manager", included: false },
        { text: "Custom workflows", included: false },
      ],
      cta: "Get Started",
      popular: true,
      highlight: true,
    },
    {
      name: "Enterprise",
      description: "Custom solutions for large organisations with complex screening requirements",
      icon: Rocket,
      pricePerVerification: null,
      volumeLimit: "Unlimited verifications",
      features: [
        { text: "Unlimited verifications", included: true },
        { text: "All Professional verification types", included: true },
        { text: "Business verification (CIPC)", included: true },
        { text: "International document checks", included: true },
        { text: "24/7 premium support", included: true },
        { text: "Custom API integration", included: true },
        { text: "Enterprise analytics & BI export", included: true },
        { text: "White-label solution", included: true },
        { text: "Custom workflow builder", included: true },
        { text: "Dedicated account manager", included: true },
        { text: "SLA guarantee (99.9% uptime)", included: true },
        { text: "On-site training & onboarding", included: true },
      ],
      cta: "Contact Sales",
      popular: false,
      highlight: false,
    },
  ];

  const addons = [
    {
      name: "Advanced AML Screening",
      description: "Enhanced screening against SARB sanctions lists, PEPs, and global watchlists",
      price: "R8.50 per check",
    },
    {
      name: "Ongoing Monitoring",
      description: "Continuous monitoring of verified individuals for criminal records and compliance changes",
      price: "R1,500/month",
    },
    {
      name: "Premium Support",
      description: "24/7 dedicated support with 2-hour response time SLA",
      price: "R7,500/month",
    },
    {
      name: "Custom Integration",
      description: "Dedicated engineering support for ERP, HRIS, and custom system integrations",
      price: "Custom pricing",
    },
  ];

  const faqs = [
    {
      question: "How does per-verification pricing work?",
      answer: "You pay per verification check completed. The price depends on your plan tier: R29 (Starter), R22 (Professional), or custom rates for Enterprise. Each check type (criminal, qualification, credit, etc.) counts as one verification.",
    },
    {
      question: "What payment methods do you accept?",
      answer: "We accept EFT/bank transfer, debit orders, credit cards (Visa, Mastercard), and can invoice for Enterprise customers. All prices are in ZAR and include VAT.",
    },
    {
      question: "Can I switch plans at any time?",
      answer: "Yes, you can upgrade or downgrade your plan at any time. Changes take effect at the start of your next billing cycle. We'll prorate any billing differences.",
    },
    {
      question: "What happens if I exceed my monthly limit?",
      answer: "Overages are billed at a slightly higher per-check rate. We'll notify you when you reach 80% of your limit so you can upgrade if needed.",
    },
    {
      question: "Is there a setup fee?",
      answer: "No setup fees for Starter and Professional plans. Enterprise plans may include custom integration services priced separately.",
    },
    {
      question: "Are results POPIA-compliant?",
      answer: "Yes, all verification processing is fully POPIA-compliant. We handle consent management, data minimisation, and secure data handling in accordance with the Protection of Personal Information Act.",
    },
  ];

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="max-w-4xl space-y-6">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              Simple, Transparent Pricing
            </h1>
            <p className="text-xl text-muted-foreground max-w-2xl">
              Pay per verification with volume-based discounts. No hidden fees. All prices in ZAR.
            </p>
          </div>
        </div>
      </section>

      {/* Pricing Cards */}
      <section className="py-12">
        <div className="container mx-auto max-w-6xl">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {pricingPlans.map((plan) => {
              const Icon = plan.icon;

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
                      {plan.pricePerVerification ? (
                        <>
                          <div className="flex items-baseline justify-center gap-2">
                            <span className="text-4xl font-bold text-foreground">
                              {plan.pricePerVerification}
                            </span>
                            <span className="text-muted-foreground">/verification</span>
                          </div>
                          <p className="text-sm text-muted-foreground mt-2">
                            {plan.volumeLimit}
                          </p>
                        </>
                      ) : (
                        <>
                          <div className="text-3xl font-bold text-foreground">
                            Custom
                          </div>
                          <p className="text-sm text-muted-foreground mt-2">
                            Volume discounts available
                          </p>
                        </>
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
                      asChild
                    >
                      <Link to={plan.name === "Enterprise" ? "/contact" : "/request-demo"}>
                        {plan.cta}
                        <ArrowRight className="w-4 h-4 ml-2" />
                      </Link>
                    </Button>
                  </CardFooter>
                </Card>
              );
            })}
          </div>

          <div className="text-center mt-8">
            <Link to="/compare-plans" className="text-accent hover:underline font-medium">
              View detailed plan comparison →
            </Link>
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
                    <Button variant="outline" size="sm" asChild>
                      <Link to="/contact">Enquire</Link>
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Pricing FAQ
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
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <Card className="border-accent bg-gradient-to-br from-primary/5 to-accent/5">
            <CardHeader className="text-center pb-6">
              <CardTitle className="text-3xl md:text-4xl mb-4">
                Ready to Get Started?
              </CardTitle>
              <CardDescription className="text-lg">
                Join 200+ South African organisations already using VeriGate for background screening.
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
                <Link to="/contact">Contact Sales</Link>
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  );
};

export default Pricing;
