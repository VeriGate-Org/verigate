import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Slider } from "@/components/ui/slider";
import {
  Calculator,
  TrendingUp,
  DollarSign,
  Clock,
  ArrowRight,
  Zap,
  BadgeCheck,
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";
import { AnimatedSection } from "@/components/AnimatedSection";

const ROICalculator = () => {
  const [monthlyVolume, setMonthlyVolume] = useState(100);
  const [currentCostPerCheck, setCurrentCostPerCheck] = useState(45);
  const [currentTurnaround, setCurrentTurnaround] = useState(5);

  // VeriGate pricing tiers (ZAR)
  const getVerigateRate = (volume: number): number => {
    if (volume <= 100) return 29;
    if (volume <= 1000) return 22;
    return 18;
  };

  const verigateRate = getVerigateRate(monthlyVolume);
  const monthlyCurrentCost = monthlyVolume * currentCostPerCheck;
  const monthlyVerigateCost = monthlyVolume * verigateRate;
  const monthlySavings = monthlyCurrentCost - monthlyVerigateCost;
  const annualSavings = monthlySavings * 12;

  // Time savings: VeriGate average is 1 day turnaround
  const verigateTurnaround = 1;
  const daysSavedPerCheck = Math.max(0, currentTurnaround - verigateTurnaround);
  const totalDaysSavedPerMonth = daysSavedPerCheck * monthlyVolume;

  // Determine pricing tier label
  const getPricingTierLabel = (volume: number): string => {
    if (volume <= 100) return "Starter (R29/check)";
    if (volume <= 1000) return "Professional (R22/check)";
    return "Enterprise (R18/check)";
  };

  const formatZAR = (amount: number): string => {
    return `R${amount.toLocaleString("en-ZA", { maximumFractionDigits: 0 })}`;
  };

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container relative z-10">
          <AnimatedSection>
            <div className="container mx-auto max-w-6xl text-center space-y-6">
              <Badge variant="outline" className="mb-2">
                <Calculator className="w-3 h-3 mr-1" />
                Interactive Tool
              </Badge>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                ROI Calculator
                <span className="block text-accent mt-2">
                  Calculate Your Screening Savings
                </span>
              </h1>
              <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
                See how much your organisation could save by switching to VeriGate
                for background screening. Enter your current volumes and costs to
                get an instant estimate in ZAR.
              </p>
            </div>
          </AnimatedSection>
        </div>
      </section>

      {/* Calculator Section */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="grid lg:grid-cols-2 gap-8">
            {/* Input Panel */}
            <AnimatedSection>
              <Card className="border-border">
                <CardHeader>
                  <CardTitle className="text-xl">
                    Your Current Screening Costs
                  </CardTitle>
                  <CardDescription>
                    Enter your current verification volumes and costs to calculate
                    potential savings with VeriGate.
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-8">
                  {/* Monthly Volume */}
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <Label htmlFor="volume">
                        Monthly Verification Volume
                      </Label>
                      <span className="text-lg font-bold text-accent">
                        {monthlyVolume.toLocaleString()}
                      </span>
                    </div>
                    <Slider
                      id="volume"
                      min={10}
                      max={5000}
                      step={10}
                      value={[monthlyVolume]}
                      onValueChange={(value) => setMonthlyVolume(value[0])}
                    />
                    <div className="flex justify-between text-xs text-muted-foreground">
                      <span>10</span>
                      <span>5,000</span>
                    </div>
                    <div className="text-xs text-muted-foreground">
                      Your pricing tier:{" "}
                      <span className="font-semibold text-accent">
                        {getPricingTierLabel(monthlyVolume)}
                      </span>
                    </div>
                  </div>

                  {/* Current Cost per Verification */}
                  <div className="space-y-3">
                    <Label htmlFor="cost">
                      Current Cost per Verification (ZAR)
                    </Label>
                    <div className="relative">
                      <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground font-medium">
                        R
                      </span>
                      <Input
                        id="cost"
                        type="number"
                        min={1}
                        max={500}
                        value={currentCostPerCheck}
                        onChange={(e) =>
                          setCurrentCostPerCheck(
                            Math.max(1, Number(e.target.value))
                          )
                        }
                        className="pl-8"
                      />
                    </div>
                    <p className="text-xs text-muted-foreground">
                      The average cost you currently pay per background check,
                      including criminal, identity, and qualification
                      verifications.
                    </p>
                  </div>

                  {/* Current Turnaround */}
                  <div className="space-y-3">
                    <Label htmlFor="turnaround">
                      Current Average Turnaround (Days)
                    </Label>
                    <div className="relative">
                      <Input
                        id="turnaround"
                        type="number"
                        min={1}
                        max={30}
                        value={currentTurnaround}
                        onChange={(e) =>
                          setCurrentTurnaround(
                            Math.max(1, Number(e.target.value))
                          )
                        }
                      />
                    </div>
                    <p className="text-xs text-muted-foreground">
                      How many business days your current provider takes on
                      average to return verification results.
                    </p>
                  </div>
                </CardContent>
              </Card>
            </AnimatedSection>

            {/* Results Panel */}
            <AnimatedSection delay={0.2}>
              <div className="space-y-6">
                {/* Summary Card */}
                <Card
                  className={`border-2 ${
                    monthlySavings > 0 ? "border-accent" : "border-border"
                  }`}
                >
                  <CardHeader className="text-center pb-2">
                    <CardDescription className="text-sm">
                      Estimated Annual Savings
                    </CardDescription>
                    <CardTitle
                      className={`text-5xl font-bold ${
                        annualSavings > 0 ? "text-accent" : "text-foreground"
                      }`}
                    >
                      {formatZAR(Math.max(0, annualSavings))}
                    </CardTitle>
                    {annualSavings > 0 && (
                      <div className="flex items-center justify-center gap-2 text-accent mt-2">
                        <TrendingUp className="w-4 h-4" />
                        <span className="text-sm font-medium">
                          {(
                            ((currentCostPerCheck - verigateRate) /
                              currentCostPerCheck) *
                            100
                          ).toFixed(0)}
                          % cost reduction per check
                        </span>
                      </div>
                    )}
                    {annualSavings <= 0 && (
                      <p className="text-sm text-muted-foreground mt-2">
                        Your current rate is already competitive. Contact us for
                        a custom Enterprise quote.
                      </p>
                    )}
                  </CardHeader>
                </Card>

                {/* Detailed Breakdown */}
                <Card className="border-border">
                  <CardHeader>
                    <CardTitle className="text-lg">Results Breakdown</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="flex items-center justify-between p-4 rounded-lg bg-secondary/50">
                      <div className="flex items-center gap-3">
                        <DollarSign className="w-5 h-5 text-accent" />
                        <div>
                          <div className="font-semibold text-sm">
                            Monthly Cost with VeriGate
                          </div>
                          <div className="text-xs text-muted-foreground">
                            Based on {monthlyVolume.toLocaleString()} checks at R{verigateRate}/check
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="font-bold text-foreground">
                          {formatZAR(monthlyVerigateCost)}
                        </div>
                        <div className="text-xs text-muted-foreground">
                          per month
                        </div>
                      </div>
                    </div>

                    <div className="flex items-center justify-between p-4 rounded-lg bg-secondary/50">
                      <div className="flex items-center gap-3">
                        <TrendingUp className="w-5 h-5 text-accent" />
                        <div>
                          <div className="font-semibold text-sm">
                            Monthly Savings
                          </div>
                          <div className="text-xs text-muted-foreground">
                            Difference vs your current cost
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="font-bold text-accent">
                          {formatZAR(Math.max(0, monthlySavings))}
                        </div>
                        <div className="text-xs text-muted-foreground">
                          per month
                        </div>
                      </div>
                    </div>

                    <div className="flex items-center justify-between p-4 rounded-lg bg-secondary/50">
                      <div className="flex items-center gap-3">
                        <Clock className="w-5 h-5 text-accent" />
                        <div>
                          <div className="font-semibold text-sm">
                            Time Saved per Month
                          </div>
                          <div className="text-xs text-muted-foreground">
                            Based on VeriGate 24hr avg turnaround
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="font-bold text-accent">
                          {totalDaysSavedPerMonth.toLocaleString()} days
                        </div>
                        <div className="text-xs text-muted-foreground">
                          total across all checks
                        </div>
                      </div>
                    </div>

                    <div className="flex items-center justify-between p-4 rounded-lg bg-secondary/50">
                      <div className="flex items-center gap-3">
                        <Zap className="w-5 h-5 text-accent" />
                        <div>
                          <div className="font-semibold text-sm">
                            Cost per Check Comparison
                          </div>
                          <div className="text-xs text-muted-foreground">
                            Your current rate vs VeriGate
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="text-sm">
                          <span className="text-muted-foreground line-through mr-2">
                            R{currentCostPerCheck}
                          </span>
                          <span className="font-bold text-accent">
                            R{verigateRate}
                          </span>
                        </div>
                        <div className="text-xs text-muted-foreground">
                          per verification
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>

                {/* Pricing Tiers Reference */}
                <Card className="border-border">
                  <CardHeader>
                    <CardTitle className="text-lg">
                      VeriGate Pricing Tiers
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-3">
                      {[
                        {
                          tier: "Starter",
                          volume: "Up to 100 checks/month",
                          rate: "R29",
                          active: monthlyVolume <= 100,
                        },
                        {
                          tier: "Professional",
                          volume: "101 - 1,000 checks/month",
                          rate: "R22",
                          active:
                            monthlyVolume > 100 && monthlyVolume <= 1000,
                        },
                        {
                          tier: "Enterprise",
                          volume: "1,000+ checks/month",
                          rate: "R18",
                          active: monthlyVolume > 1000,
                        },
                      ].map((tier) => (
                        <div
                          key={tier.tier}
                          className={`flex items-center justify-between p-3 rounded-lg text-sm ${
                            tier.active
                              ? "bg-accent/10 ring-1 ring-accent/30"
                              : "bg-secondary/30"
                          }`}
                        >
                          <div className="flex items-center gap-2">
                            {tier.active && (
                              <BadgeCheck className="w-4 h-4 text-accent" />
                            )}
                            <div>
                              <span className="font-semibold">
                                {tier.tier}
                              </span>
                              <span className="text-muted-foreground ml-2">
                                {tier.volume}
                              </span>
                            </div>
                          </div>
                          <span
                            className={`font-bold ${
                              tier.active ? "text-accent" : "text-foreground"
                            }`}
                          >
                            {tier.rate}/check
                          </span>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>

                {/* CTA */}
                <div className="flex flex-col sm:flex-row gap-4">
                  <Button className="flex-1" size="lg" asChild>
                    <Link to="/request-demo">
                      Ready to Save? Request a Demo
                      <ArrowRight className="w-4 h-4 ml-2" />
                    </Link>
                  </Button>
                  <Button
                    variant="outline"
                    className="flex-1"
                    size="lg"
                    asChild
                  >
                    <Link to="/pricing">View Full Pricing</Link>
                  </Button>
                </div>
              </div>
            </AnimatedSection>
          </div>
        </div>
      </section>

      {/* Why VeriGate */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection>
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Why Organisations Switch to VeriGate
              </h2>
              <p className="text-lg text-muted-foreground">
                Beyond cost savings, VeriGate delivers measurable improvements
                across your screening process.
              </p>
            </div>
          </AnimatedSection>

          <AnimatedSection delay={0.2}>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              {[
                {
                  stat: "24hr",
                  label: "Average Turnaround",
                  detail:
                    "Most verifications completed within one business day via direct DHA, SAPS, and SAQA integrations.",
                },
                {
                  stat: "99.2%",
                  label: "Accuracy Rate",
                  detail:
                    "Verified against primary South African data sources for reliable, auditable results.",
                },
                {
                  stat: "100%",
                  label: "POPIA Compliant",
                  detail:
                    "Built-in consent management, data minimisation, and full POPIA compliance from day one.",
                },
                {
                  stat: "200+",
                  label: "SA Clients",
                  detail:
                    "Trusted by organisations across banking, insurance, telecoms, and professional services.",
                },
              ].map((item) => (
                <Card key={item.label} className="border-border text-center">
                  <CardContent className="pt-6">
                    <div className="text-3xl font-bold text-accent mb-2">
                      {item.stat}
                    </div>
                    <div className="font-semibold text-foreground mb-2">
                      {item.label}
                    </div>
                    <p className="text-xs text-muted-foreground">
                      {item.detail}
                    </p>
                  </CardContent>
                </Card>
              ))}
            </div>
          </AnimatedSection>
        </div>
      </section>
    </div>
  );
};

export default ROICalculator;
