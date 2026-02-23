import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
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
  Shield,
  Download,
  Zap
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";

const ROICalculator = () => {
  const [monthlyVolume, setMonthlyVolume] = useState(10000);
  const [currentCost, setCurrentCost] = useState(5);
  const [manualReviewTime, setManualReviewTime] = useState(10);
  const [fraudRate, setFraudRate] = useState(2);
  const [avgTransactionValue, setAvgTransactionValue] = useState(500);

  // Calculations
  const annualVolume = monthlyVolume * 12;
  const currentAnnualCost = annualVolume * currentCost;
  const verigateCost = annualVolume * 2.5; // $2.50 per verification
  const costSavings = currentAnnualCost - verigateCost;
  
  const manualReviewCost = (manualReviewTime / 60) * 25 * annualVolume; // $25/hour
  const automationSavings = manualReviewCost * 0.85; // 85% automation
  
  const fraudLosses = (fraudRate / 100) * annualVolume * avgTransactionValue;
  const fraudReduction = fraudLosses * 0.87; // 87% fraud reduction
  
  const totalSavings = costSavings + automationSavings + fraudReduction;
  const roi = ((totalSavings - verigateCost) / verigateCost) * 100;
  const paybackMonths = verigateCost / (totalSavings / 12);

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-primary/10 to-secondary/5">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl text-center">
            <Badge className="mb-4" variant="outline">
              <Calculator className="w-3 h-3 mr-1" />
              Interactive Tool
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              ROI Calculator
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Calculate the return on investment from automating your identity verification 
              with VeriGate. See real cost savings, time savings, and fraud reduction.
            </p>
          </div>
        </div>
      </section>

      {/* Calculator */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <div className="grid lg:grid-cols-2 gap-8">
              {/* Input Panel */}
              <div className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle>Your Current Situation</CardTitle>
                    <CardDescription>
                      Enter your current verification metrics
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-6">
                    {/* Monthly Volume */}
                    <div className="space-y-3">
                      <div className="flex items-center justify-between">
                        <Label htmlFor="volume">Monthly Verification Volume</Label>
                        <span className="font-semibold">{monthlyVolume.toLocaleString()}</span>
                      </div>
                      <Slider
                        id="volume"
                        min={100}
                        max={100000}
                        step={100}
                        value={[monthlyVolume]}
                        onValueChange={(value) => setMonthlyVolume(value[0])}
                      />
                      <div className="flex justify-between text-xs text-muted-foreground">
                        <span>100</span>
                        <span>100,000</span>
                      </div>
                    </div>

                    {/* Current Cost */}
                    <div className="space-y-3">
                      <div className="flex items-center justify-between">
                        <Label htmlFor="cost">Current Cost per Verification</Label>
                        <span className="font-semibold">${currentCost.toFixed(2)}</span>
                      </div>
                      <Slider
                        id="cost"
                        min={1}
                        max={20}
                        step={0.5}
                        value={[currentCost]}
                        onValueChange={(value) => setCurrentCost(value[0])}
                      />
                      <div className="flex justify-between text-xs text-muted-foreground">
                        <span>$1</span>
                        <span>$20</span>
                      </div>
                    </div>

                    {/* Manual Review Time */}
                    <div className="space-y-3">
                      <div className="flex items-center justify-between">
                        <Label htmlFor="time">Manual Review Time (minutes)</Label>
                        <span className="font-semibold">{manualReviewTime} min</span>
                      </div>
                      <Slider
                        id="time"
                        min={1}
                        max={30}
                        step={1}
                        value={[manualReviewTime]}
                        onValueChange={(value) => setManualReviewTime(value[0])}
                      />
                      <div className="flex justify-between text-xs text-muted-foreground">
                        <span>1 min</span>
                        <span>30 min</span>
                      </div>
                    </div>

                    {/* Fraud Rate */}
                    <div className="space-y-3">
                      <div className="flex items-center justify-between">
                        <Label htmlFor="fraud">Current Fraud Rate</Label>
                        <span className="font-semibold">{fraudRate.toFixed(1)}%</span>
                      </div>
                      <Slider
                        id="fraud"
                        min={0.1}
                        max={10}
                        step={0.1}
                        value={[fraudRate]}
                        onValueChange={(value) => setFraudRate(value[0])}
                      />
                      <div className="flex justify-between text-xs text-muted-foreground">
                        <span>0.1%</span>
                        <span>10%</span>
                      </div>
                    </div>

                    {/* Avg Transaction */}
                    <div className="space-y-3">
                      <div className="flex items-center justify-between">
                        <Label htmlFor="transaction">Average Transaction Value</Label>
                        <span className="font-semibold">${avgTransactionValue.toLocaleString()}</span>
                      </div>
                      <Slider
                        id="transaction"
                        min={50}
                        max={10000}
                        step={50}
                        value={[avgTransactionValue]}
                        onValueChange={(value) => setAvgTransactionValue(value[0])}
                      />
                      <div className="flex justify-between text-xs text-muted-foreground">
                        <span>$50</span>
                        <span>$10,000</span>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </div>

              {/* Results Panel */}
              <div className="space-y-6">
                {/* Total Savings */}
                <Card className="bg-gradient-to-br from-primary to-primary/80 text-primary-foreground">
                  <CardContent className="p-8">
                    <div className="text-center">
                      <div className="text-sm mb-2 text-primary-foreground/80">Annual Savings</div>
                      <div className="text-5xl font-bold mb-2">${totalSavings.toLocaleString(undefined, { maximumFractionDigits: 0 })}</div>
                      <div className="flex items-center justify-center gap-2 text-primary-foreground/90">
                        <TrendingUp className="w-4 h-4" />
                        <span>{roi.toFixed(0)}% ROI</span>
                      </div>
                    </div>
                  </CardContent>
                </Card>

                {/* Breakdown */}
                <Card>
                  <CardHeader>
                    <CardTitle>Savings Breakdown</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="flex items-center justify-between p-4 bg-green-50 dark:bg-green-950 rounded-lg">
                      <div className="flex items-center gap-3">
                        <DollarSign className="w-5 h-5 text-green-600" />
                        <div>
                          <div className="font-semibold">Cost Reduction</div>
                          <div className="text-sm text-muted-foreground">Lower per-verification cost</div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="font-bold text-green-600">${costSavings.toLocaleString(undefined, { maximumFractionDigits: 0 })}</div>
                        <div className="text-xs text-muted-foreground">annually</div>
                      </div>
                    </div>

                    <div className="flex items-center justify-between p-4 bg-blue-50 dark:bg-blue-950 rounded-lg">
                      <div className="flex items-center gap-3">
                        <Clock className="w-5 h-5 text-blue-600" />
                        <div>
                          <div className="font-semibold">Time Savings</div>
                          <div className="text-sm text-muted-foreground">85% automation rate</div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="font-bold text-blue-600">${automationSavings.toLocaleString(undefined, { maximumFractionDigits: 0 })}</div>
                        <div className="text-xs text-muted-foreground">annually</div>
                      </div>
                    </div>

                    <div className="flex items-center justify-between p-4 bg-purple-50 dark:bg-purple-950 rounded-lg">
                      <div className="flex items-center gap-3">
                        <Shield className="w-5 h-5 text-purple-600" />
                        <div>
                          <div className="font-semibold">Fraud Reduction</div>
                          <div className="text-sm text-muted-foreground">87% fraud prevention</div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="font-bold text-purple-600">${fraudReduction.toLocaleString(undefined, { maximumFractionDigits: 0 })}</div>
                        <div className="text-xs text-muted-foreground">annually</div>
                      </div>
                    </div>
                  </CardContent>
                </Card>

                {/* Key Metrics */}
                <Card>
                  <CardHeader>
                    <CardTitle>Key Metrics</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground">Annual Volume</span>
                      <span className="font-semibold">{annualVolume.toLocaleString()} verifications</span>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground">VeriGate Annual Cost</span>
                      <span className="font-semibold">${verigateCost.toLocaleString(undefined, { maximumFractionDigits: 0 })}</span>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground">Payback Period</span>
                      <span className="font-semibold">{paybackMonths.toFixed(1)} months</span>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground">3-Year Value</span>
                      <span className="font-semibold text-primary">${(totalSavings * 3).toLocaleString(undefined, { maximumFractionDigits: 0 })}</span>
                    </div>
                  </CardContent>
                </Card>

                {/* Actions */}
                <div className="flex gap-4">
                  <Button className="flex-1" asChild>
                    <Link to="/contact">
                      Get Custom Analysis
                    </Link>
                  </Button>
                  <Button variant="outline" className="flex-1">
                    <Download className="w-4 h-4 mr-2" />
                    Download PDF
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Industry Templates */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8 text-center">Industry Templates</h2>
            <p className="text-center text-muted-foreground mb-8">
              Pre-configured templates with industry-average values
            </p>

            <div className="grid md:grid-cols-3 gap-6">
              <Card className="cursor-pointer hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">Fintech</CardTitle>
                  <CardDescription>Digital banking and lending</CardDescription>
                </CardHeader>
                <CardContent className="text-sm space-y-2">
                  <div className="flex justify-between">
                    <span>Volume:</span>
                    <span className="font-medium">25,000/mo</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Current cost:</span>
                    <span className="font-medium">$4.50</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Fraud rate:</span>
                    <span className="font-medium">1.8%</span>
                  </div>
                </CardContent>
              </Card>

              <Card className="cursor-pointer hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">E-commerce</CardTitle>
                  <CardDescription>Online retail and marketplaces</CardDescription>
                </CardHeader>
                <CardContent className="text-sm space-y-2">
                  <div className="flex justify-between">
                    <span>Volume:</span>
                    <span className="font-medium">50,000/mo</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Current cost:</span>
                    <span className="font-medium">$3.00</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Fraud rate:</span>
                    <span className="font-medium">3.2%</span>
                  </div>
                </CardContent>
              </Card>

              <Card className="cursor-pointer hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">Gaming</CardTitle>
                  <CardDescription>Online gaming and betting</CardDescription>
                </CardHeader>
                <CardContent className="text-sm space-y-2">
                  <div className="flex justify-between">
                    <span>Volume:</span>
                    <span className="font-medium">15,000/mo</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Current cost:</span>
                    <span className="font-medium">$6.00</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Fraud rate:</span>
                    <span className="font-medium">2.5%</span>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16">
        <div className="container mx-auto">
          <Card className="max-w-3xl mx-auto bg-gradient-to-br from-primary to-primary/80 text-primary-foreground">
            <CardContent className="p-12 text-center">
              <Zap className="w-12 h-12 mx-auto mb-4" />
              <h2 className="text-3xl font-bold mb-4">Ready to Get Started?</h2>
              <p className="text-lg mb-8 text-primary-foreground/90">
                Schedule a demo to see how VeriGate can transform your verification process
              </p>
              <Button size="lg" variant="secondary" asChild>
                <Link to="/contact">
                  Schedule Demo
                </Link>
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default ROICalculator;
