import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  Handshake,
  TrendingUp,
  Users,
  Award,
  Rocket,
  DollarSign,
  Target,
  Code,
  Globe,
  Briefcase,
  GraduationCap
} from "lucide-react";
import { Link } from "react-router-dom";

const Partners = () => {
  const partnerTypes = [
    {
      type: "Technology Partners",
      icon: Code,
      description: "Integrate VeriGate into your platform or application",
      benefits: [
        "Co-marketing opportunities",
        "Technical integration support",
        "Revenue sharing program",
        "Early access to new features",
        "Partner portal and resources",
        "Joint go-to-market strategies",
      ],
      idealFor: "SaaS platforms, e-commerce solutions, authentication providers, fintech apps",
    },
    {
      type: "Reseller Partners",
      icon: Briefcase,
      description: "Sell VeriGate solutions to your customer base",
      benefits: [
        "Generous commission structure",
        "Dedicated partner manager",
        "Sales enablement materials",
        "Deal registration program",
        "Lead sharing agreements",
        "Co-selling opportunities",
      ],
      idealFor: "System integrators, IT consultancies, managed service providers, VARs",
    },
    {
      type: "Referral Partners",
      icon: Handshake,
      description: "Refer customers and earn commissions",
      benefits: [
        "30% commission on first year",
        "Recurring revenue share",
        "Simple referral process",
        "Fast commission payouts",
        "No sales quota required",
        "Partner dashboard access",
      ],
      idealFor: "Agencies, consultants, advisors, industry experts",
    },
    {
      type: "Implementation Partners",
      icon: Rocket,
      description: "Help customers implement VeriGate solutions",
      benefits: [
        "Implementation fees",
        "Certified training program",
        "Technical certification",
        "Priority support access",
        "Implementation playbooks",
        "Customer referrals",
      ],
      idealFor: "Development agencies, consulting firms, integration specialists",
    },
  ];

  const benefits = [
    {
      icon: DollarSign,
      title: "Revenue Growth",
      description: "Earn competitive commissions and recurring revenue from customer referrals",
    },
    {
      icon: Users,
      title: "Expand Your Offering",
      description: "Add enterprise-grade identity verification to your product portfolio",
    },
    {
      icon: Award,
      title: "Trusted Brand",
      description: "Partner with a leader in identity verification trusted by 500+ enterprises",
    },
    {
      icon: Target,
      title: "Market Leadership",
      description: "Access our expertise, resources, and proven go-to-market strategies",
    },
    {
      icon: Globe,
      title: "Global Reach",
      description: "Tap into our international presence across 190+ countries",
    },
    {
      icon: GraduationCap,
      title: "Training & Support",
      description: "Comprehensive onboarding, certification, and ongoing technical support",
    },
  ];

  const partnerTiers = [
    {
      tier: "Registered Partner",
      requirements: ["Complete partner application", "Sign partner agreement", "Complete basic training"],
      benefits: ["Partner portal access", "Marketing materials", "Technical documentation", "Email support"],
    },
    {
      tier: "Certified Partner",
      requirements: ["5+ successful implementations", "Pass certification exam", "Customer references"],
      benefits: ["All Registered benefits", "Co-marketing opportunities", "Priority support", "Deal registration"],
      highlighted: true,
    },
    {
      tier: "Premier Partner",
      requirements: ["20+ successful implementations", "Dedicated resources", "Annual business plan"],
      benefits: ["All Certified benefits", "Dedicated partner manager", "Joint marketing budget", "Revenue targets"],
    },
  ];

  const successStories = [
    {
      partner: "CloudTech Solutions",
      type: "Technology Partner",
      achievement: "Integrated VeriGate into their SaaS platform, added 50+ shared customers",
      result: "$500K+ in annual revenue",
      logo: "☁️",
    },
    {
      partner: "FinServe Consulting",
      type: "Implementation Partner",
      achievement: "Certified team completed 30+ implementations in financial services",
      result: "85% customer satisfaction",
      logo: "💼",
    },
    {
      partner: "Digital Identity Hub",
      type: "Reseller Partner",
      achievement: "Top-performing reseller in APAC region with 100+ customers",
      result: "$1.2M in sales revenue",
      logo: "🌏",
    },
  ];

  const resources = [
    { title: "Partner Portal", description: "Access resources, track referrals, and manage your account", icon: Globe },
    { title: "Training Academy", description: "Certification courses and technical training programs", icon: GraduationCap },
    { title: "Marketing Assets", description: "Logos, case studies, presentations, and co-branding materials", icon: Target },
    { title: "Sales Tools", description: "Demos, pricing calculators, ROI templates, and competitive intelligence", icon: Briefcase },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-background to-primary/10">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <Badge className="mb-4" variant="secondary">
              Partner Program
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Grow Your Business
              <span className="text-primary block mt-2">with VeriGate Partnership</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
              Join our partner ecosystem and help businesses worldwide verify identities securely. 
              Earn competitive commissions while delivering value to your customers.
            </p>
            <div className="flex gap-4 justify-center flex-wrap">
              <Button size="lg" asChild>
                <a href="#apply">Become a Partner</a>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <a href="#partner-types">Explore Programs</a>
              </Button>
            </div>
          </div>

          {/* Quick Stats */}
          <div className="grid md:grid-cols-4 gap-6">
            <Card>
              <CardContent className="pt-6 text-center">
                <div className="text-3xl font-bold text-primary mb-2">200+</div>
                <div className="text-sm text-muted-foreground">Active Partners</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6 text-center">
                <div className="text-3xl font-bold text-primary mb-2">30%</div>
                <div className="text-sm text-muted-foreground">Average Commission</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6 text-center">
                <div className="text-3xl font-bold text-primary mb-2">$5M+</div>
                <div className="text-sm text-muted-foreground">Partner Revenue (2023)</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6 text-center">
                <div className="text-3xl font-bold text-primary mb-2">50+</div>
                <div className="text-sm text-muted-foreground">Countries</div>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Partner Types */}
      <section id="partner-types" className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Choose Your Partner Program</h2>
            <p className="text-lg text-muted-foreground">
              Multiple partnership models to fit your business
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {partnerTypes.map((partner, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-center gap-3 mb-2">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <partner.icon className="w-6 h-6 text-primary" />
                    </div>
                    <CardTitle className="text-xl">{partner.type}</CardTitle>
                  </div>
                  <CardDescription>{partner.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div>
                      <div className="text-sm font-medium mb-2">Benefits:</div>
                      <ul className="space-y-1">
                        {partner.benefits.map((benefit, idx) => (
                          <li key={idx} className="flex items-start gap-2 text-sm">
                            <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                            <span>{benefit}</span>
                          </li>
                        ))}
                      </ul>
                    </div>
                    <div className="pt-3 border-t">
                      <div className="text-xs text-muted-foreground mb-1">Ideal for:</div>
                      <p className="text-sm">{partner.idealFor}</p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Benefits */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Why Partner with VeriGate</h2>
            <p className="text-lg text-muted-foreground">
              Accelerate your growth with our partnership program
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {benefits.map((benefit, index) => (
              <div key={index} className="text-center">
                <div className="inline-flex p-4 bg-primary/10 rounded-lg mb-4">
                  <benefit.icon className="w-8 h-8 text-primary" />
                </div>
                <h3 className="text-lg font-semibold mb-2">{benefit.title}</h3>
                <p className="text-muted-foreground text-sm">{benefit.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Partner Tiers */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Partner Tiers</h2>
            <p className="text-lg text-muted-foreground">
              Grow your partnership with increasing benefits
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {partnerTiers.map((tier, index) => (
              <Card key={index} className={tier.highlighted ? "border-primary shadow-lg" : ""}>
                <CardHeader>
                  {tier.highlighted && <Badge className="w-fit mb-2">Recommended</Badge>}
                  <CardTitle>{tier.tier}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <div className="text-sm font-medium mb-2">Requirements:</div>
                    <ul className="space-y-1">
                      {tier.requirements.map((req, idx) => (
                        <li key={idx} className="text-sm text-muted-foreground flex items-start gap-2">
                          <span className="text-primary">•</span>
                          <span>{req}</span>
                        </li>
                      ))}
                    </ul>
                  </div>
                  <div>
                    <div className="text-sm font-medium mb-2">Benefits:</div>
                    <ul className="space-y-1">
                      {tier.benefits.map((benefit, idx) => (
                        <li key={idx} className="flex items-start gap-2 text-sm">
                          <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                          <span>{benefit}</span>
                        </li>
                      ))}
                    </ul>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Success Stories */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Partner Success Stories</h2>
            <p className="text-lg text-muted-foreground">
              Real results from our partner ecosystem
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {successStories.map((story, index) => (
              <Card key={index}>
                <CardHeader>
                  <div className="text-5xl mb-2">{story.logo}</div>
                  <CardTitle className="text-lg">{story.partner}</CardTitle>
                  <Badge variant="secondary" className="w-fit">{story.type}</Badge>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground mb-3">{story.achievement}</p>
                  <Badge variant="default" className="font-semibold">
                    <TrendingUp className="w-3 h-3 mr-1" />
                    {story.result}
                  </Badge>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Resources */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Partner Resources</h2>
            <p className="text-lg text-muted-foreground">
              Everything you need to succeed
            </p>
          </div>

          <div className="grid md:grid-cols-4 gap-6">
            {resources.map((resource, index) => (
              <Card key={index} className="text-center hover:shadow-lg transition-shadow">
                <CardContent className="pt-6">
                  <resource.icon className="w-12 h-12 text-primary mx-auto mb-3" />
                  <h3 className="font-semibold mb-2">{resource.title}</h3>
                  <p className="text-sm text-muted-foreground">{resource.description}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Application Form */}
      <section id="apply" className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="max-w-3xl mx-auto">
          <div className="text-center mb-8">
            <h2 className="text-3xl font-bold mb-4">Become a Partner</h2>
            <p className="text-lg text-muted-foreground">
              Fill out the form below and we'll be in touch within 24 hours
            </p>
          </div>

          <Card>
            <CardContent className="pt-6">
              <p className="text-center text-muted-foreground mb-4">
                Contact us at <a href="mailto:partners@verigate.com" className="text-primary hover:underline font-semibold">partners@verigate.com</a> or click below to get started
              </p>
              <Button className="w-full" size="lg" asChild>
                <Link to="/contact">Apply for Partnership</Link>
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* FAQ */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Partner Program FAQ</h2>
          </div>

          <div className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">What are the requirements to become a partner?</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">
                  Basic requirements include a registered business, relevant industry experience, and commitment 
                  to customer success. Specific requirements vary by partner type. Contact us to discuss your qualification.
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg">How much commission can I earn?</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">
                  Commission rates vary by partner type and tier. Referral partners earn 30% first-year commission 
                  with recurring revenue share. Resellers receive volume-based discounts. Contact us for detailed pricing.
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg">What support do partners receive?</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">
                  All partners get access to training, marketing materials, and technical documentation. Certified 
                  and Premier partners receive dedicated support, co-marketing funds, and a partner success manager.
                </p>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-white">
        <div className="container mx-auto max-w-6xl text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            Ready to Partner with VeriGate?
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join 200+ partners growing their business with our partnership program
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" variant="secondary" asChild>
              <a href="#apply">Apply Now</a>
            </Button>
            <Button size="lg" variant="outline" className="bg-white/10 hover:bg-white/20 border-white text-white" asChild>
              <Link to="/contact">Contact Partner Team</Link>
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Partners;
