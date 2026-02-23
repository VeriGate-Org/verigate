import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  ShoppingCart,
  Shield,
  Zap,
  CreditCard,
  Users,
  TrendingUp,
  Package,
  Truck,
  Globe,
  ArrowRight,
  Lock,
  AlertTriangle
} from "lucide-react";
import { Link } from "react-router-dom";

const Ecommerce = () => {
  const challenges = [
    {
      icon: AlertTriangle,
      title: "Payment Fraud",
      description: "Card-not-present fraud and account takeover attacks costing billions annually",
      stat: "$41B lost to e-commerce fraud in 2023",
    },
    {
      icon: Users,
      title: "Account Takeover",
      description: "Prevent unauthorized access to customer accounts and stored payment methods",
      stat: "24% increase in ATO attacks",
    },
    {
      icon: Package,
      title: "Return Fraud",
      description: "Combat friendly fraud, wardrobing, and serial returners",
      stat: "$101B in fraudulent returns annually",
    },
    {
      icon: ShoppingCart,
      title: "Cart Abandonment",
      description: "Reduce friction at checkout while maintaining security",
      stat: "70% average cart abandonment rate",
    },
  ];

  const solutions = [
    {
      title: "Checkout Verification",
      description: "Frictionless identity verification for high-value purchases",
      features: [
        "Step-up authentication",
        "Biometric verification",
        "Device fingerprinting",
        "Real-time fraud scoring",
      ],
      metric: "3% fraud reduction",
    },
    {
      title: "Account Protection",
      description: "Secure customer accounts from takeover attempts",
      features: [
        "Login verification",
        "Password reset authentication",
        "Suspicious activity detection",
        "Multi-factor authentication",
      ],
      metric: "90% ATO prevention",
    },
    {
      title: "Age-Restricted Products",
      description: "Instant age verification for restricted items",
      features: [
        "Alcohol & tobacco verification",
        "Age-gated content",
        "Prescription verification",
        "Instant validation",
      ],
      metric: "< 10 sec verification",
    },
  ];

  const ecommerceSegments = [
    {
      segment: "Fashion & Apparel",
      description: "Combat wardrobing and return fraud",
      features: [
        "High-value purchase verification",
        "Return fraud prevention",
        "Account takeover protection",
        "Influencer/reseller detection",
      ],
    },
    {
      segment: "Electronics & High-Value",
      description: "Prevent fraud on expensive items",
      features: [
        "Purchase amount thresholds",
        "Shipping address verification",
        "Payment method authentication",
        "Bulk purchase monitoring",
      ],
    },
    {
      segment: "Alcohol & Tobacco",
      description: "Age verification for restricted products",
      features: [
        "Age verification at checkout",
        "ID verification for delivery",
        "Regional compliance",
        "Repeat customer recognition",
      ],
    },
    {
      segment: "Luxury Goods",
      description: "Protect premium brands from fraud",
      features: [
        "Enhanced customer verification",
        "Counterfeit prevention",
        "VIP customer authentication",
        "Gray market protection",
      ],
    },
    {
      segment: "Marketplace Platforms",
      description: "Verify both buyers and sellers",
      features: [
        "Seller identity verification",
        "Buyer protection",
        "Dispute resolution support",
        "Trust score integration",
      ],
    },
    {
      segment: "Subscription Services",
      description: "Prevent trial abuse and payment fraud",
      features: [
        "New subscriber verification",
        "Trial abuse prevention",
        "Payment update authentication",
        "Cancellation fraud prevention",
      ],
    },
  ];

  const fraudPrevention = [
    {
      threat: "Card Testing",
      description: "Detect and block automated card validation attempts",
      solution: "Rate limiting, device fingerprinting, CAPTCHA challenges",
    },
    {
      threat: "Friendly Fraud",
      description: "Verify legitimate purchases to dispute chargebacks",
      solution: "Purchase verification, delivery confirmation, audit trails",
    },
    {
      threat: "Account Creation Fraud",
      description: "Prevent fake account creation for promotions",
      solution: "Email verification, phone validation, behavioral analysis",
    },
    {
      threat: "Coupon Abuse",
      description: "Stop serial abusers of promotional codes",
      solution: "Identity linking, multi-account detection, purchase history",
    },
  ];

  const useCases = [
    {
      title: "High-Value Purchase Verification",
      description: "Additional authentication for orders over threshold amounts",
      benefits: ["Reduce chargebacks", "Prevent card fraud", "Protect brand reputation", "Maintain customer trust"],
    },
    {
      title: "Age-Restricted Checkout",
      description: "Instant age verification for alcohol, tobacco, and adult products",
      benefits: ["Legal compliance", "Instant verification", "No manual review", "Delivery confirmation"],
    },
    {
      title: "Account Recovery",
      description: "Secure password reset and account access restoration",
      benefits: ["Prevent account takeover", "Reduce support tickets", "User-friendly process", "Multi-factor options"],
    },
    {
      title: "Seller Onboarding",
      description: "Verify merchant identity on marketplace platforms",
      benefits: ["Prevent fraud sellers", "Build buyer trust", "Reduce disputes", "Tax compliance"],
    },
  ];

  const metrics = [
    { label: "Fraud Reduction", value: "93%", icon: Shield },
    { label: "Checkout Speed", value: "< 10 sec", icon: Zap },
    { label: "False Positives", value: "< 1%", icon: CheckCircle2 },
    { label: "ROI", value: "12:1", icon: TrendingUp },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-purple-50 via-background to-purple-100 dark:from-purple-950 dark:to-background">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center justify-center gap-2 mb-4">
              <ShoppingCart className="w-8 h-8 text-primary" />
              <Badge variant="secondary" className="text-sm">
                E-commerce Solutions
              </Badge>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Identity Verification for
              <span className="text-primary block mt-2">E-commerce & Online Retail</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
              Reduce fraud and chargebacks while improving checkout conversion. Verify high-value purchases, 
              age-restricted products, and protect customer accounts with frictionless authentication.
            </p>
            <div className="flex gap-4 justify-center flex-wrap">
              <Button size="lg" asChild>
                <Link to="/contact">Request Demo</Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/case-studies">View Case Studies</Link>
              </Button>
            </div>
          </div>

          {/* Key Metrics */}
          <div className="grid md:grid-cols-4 gap-6">
            {metrics.map((metric, index) => (
              <Card key={index} className="text-center">
                <CardHeader className="pb-2">
                  <metric.icon className="w-6 h-6 text-primary mx-auto" />
                </CardHeader>
                <CardContent>
                  <div className="text-3xl font-bold text-primary mb-1">{metric.value}</div>
                  <div className="text-sm text-muted-foreground">{metric.label}</div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* E-commerce Challenges */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">E-commerce Fraud Challenges</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Combat growing fraud while maintaining seamless shopping experiences
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {challenges.map((challenge, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-start gap-3">
                    <div className="p-2 bg-destructive/10 rounded-lg">
                      <challenge.icon className="w-6 h-6 text-destructive" />
                    </div>
                    <div className="flex-1">
                      <CardTitle className="text-lg mb-2">{challenge.title}</CardTitle>
                      <CardDescription>{challenge.description}</CardDescription>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <Badge variant="outline">{challenge.stat}</Badge>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Solutions */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">E-commerce Verification Solutions</h2>
            <p className="text-lg text-muted-foreground">
              Protect revenue without sacrificing conversion
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {solutions.map((solution, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{solution.title}</CardTitle>
                  <CardDescription>{solution.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2 mb-4">
                    {solution.features.map((feature, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{feature}</span>
                      </li>
                    ))}
                  </ul>
                  <Badge variant="default" className="w-full justify-center">
                    {solution.metric}
                  </Badge>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* E-commerce Segments */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">E-commerce Verticals</h2>
            <p className="text-lg text-muted-foreground">
              Tailored solutions for different retail categories
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {ecommerceSegments.map((segment, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{segment.segment}</CardTitle>
                  <CardDescription>{segment.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {segment.features.map((feature, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{feature}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Fraud Prevention */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Comprehensive Fraud Prevention</h2>
            <p className="text-lg text-muted-foreground">
              Multi-layered protection against all fraud types
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {fraudPrevention.map((fraud, index) => (
              <Card key={index}>
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <Shield className="w-5 h-5 text-primary" />
                    <CardTitle className="text-lg">{fraud.threat}</CardTitle>
                  </div>
                  <CardDescription>{fraud.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="text-sm">
                    <span className="font-medium">Solution:</span>
                    <p className="text-muted-foreground mt-1">{fraud.solution}</p>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Use Cases */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Common Use Cases</h2>
            <p className="text-lg text-muted-foreground">
              Verification throughout the e-commerce journey
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {useCases.map((useCase, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{useCase.title}</CardTitle>
                  <CardDescription>{useCase.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {useCase.benefits.map((benefit, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{benefit}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Integration Example */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Easy Integration</h2>
            <p className="text-lg text-muted-foreground">
              Add verification to your checkout flow
            </p>
          </div>

          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>High-Value Purchase Verification</CardTitle>
              <CardDescription>Step-up authentication for fraud prevention</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// Initialize VeriGate for e-commerce
const verigate = new VeriGate(apiKey, {
  industry: 'ecommerce'
});

// Check if verification needed
const order = await getOrderDetails(orderId);
const needsVerification = 
  order.amount > 500 || // High value
  order.shippingAddress !== order.billingAddress ||
  order.isFirstPurchase;

if (needsVerification) {
  // Trigger step-up verification
  const verification = await verigate.ecommerce.verifyPurchase({
    orderId: order.id,
    amount: order.amount,
    customerEmail: order.email,
    verificationMethod: 'biometric', // or 'sms', 'email'
    
    // Optional: Age verification for restricted items
    ageVerification: order.hasAgeRestrictedItems,
    minimumAge: 21
  });

  if (!verification.approved) {
    // Handle failed verification
    await flagOrder(orderId, {
      reason: verification.failureReason,
      riskScore: verification.riskScore
    });
    return { status: 'verification_failed' };
  }

  // Age check for restricted items
  if (order.hasAgeRestrictedItems && !verification.ageVerified) {
    return { status: 'age_verification_failed' };
  }
}

// Process order
await processPayment(order);
await fulfillOrder(order);`}</code>
              </pre>
            </CardContent>
          </Card>

          <div className="mt-8 text-center">
            <Button variant="outline" asChild>
              <Link to="/developers">
                View API Documentation <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-white">
        <div className="container mx-auto max-w-6xl text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            Reduce Fraud, Increase Revenue
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join leading e-commerce brands using VeriGate to protect revenue and build customer trust
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" variant="secondary" asChild>
              <Link to="/contact">Request Demo</Link>
            </Button>
            <Button size="lg" variant="outline" className="bg-white/10 hover:bg-white/20 border-white text-white" asChild>
              <Link to="/pricing">View Pricing</Link>
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Ecommerce;
