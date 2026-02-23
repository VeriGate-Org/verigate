import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  CheckCircle2,
  Plane,
  Shield,
  Globe,
  Clock,
  Users,
  MapPin,
  CreditCard,
  AlertTriangle,
  Hotel,
  ArrowRight,
  Passport,
  Luggage
} from "lucide-react";
import { Link } from "react-router-dom";

const TravelHospitality = () => {
  const challenges = [
    {
      icon: AlertTriangle,
      title: "Identity Fraud & Booking Scams",
      description: "Prevent fraudulent bookings, stolen credit cards, and fake identity reservations",
      stat: "$4.8B lost to travel fraud annually",
    },
    {
      icon: Users,
      title: "Guest Verification",
      description: "Verify guest identity for security, local regulations, and trust & safety",
      stat: "Required in 100+ destinations worldwide",
    },
    {
      icon: Clock,
      title: "Contactless Check-in",
      description: "Enable self-service check-in while maintaining security and compliance",
      stat: "78% of travelers prefer digital check-in",
    },
    {
      icon: CreditCard,
      title: "Payment Fraud",
      description: "Combat card-not-present fraud and chargebacks on reservations",
      stat: "1.8% chargeback rate in travel industry",
    },
  ];

  const solutions = [
    {
      title: "Digital Check-in",
      description: "Contactless identity verification for hotels and vacation rentals",
      features: [
        "Pre-arrival verification",
        "Mobile ID capture",
        "Passport scanning",
        "Local compliance",
      ],
      metric: "< 2 min check-in",
    },
    {
      title: "Booking Verification",
      description: "Prevent fraud during reservation process",
      features: [
        "Guest identity confirmation",
        "Payment authentication",
        "Risk scoring",
        "Chargeback prevention",
      ],
      metric: "85% fraud reduction",
    },
    {
      title: "Regulatory Compliance",
      description: "Meet local registration and reporting requirements",
      features: [
        "Guest registration",
        "Police reporting (Italy, Spain, etc.)",
        "Tax collection compliance",
        "Data privacy (GDPR)",
      ],
      metric: "100+ jurisdictions",
    },
  ];

  const travelSegments = [
    {
      segment: "Hotels & Resorts",
      description: "Streamline check-in and meet regulatory requirements",
      requirements: [
        "Pre-arrival guest verification",
        "Passport/ID scanning",
        "Local police reporting",
        "Contactless check-in",
        "Minibar & incidental charges",
      ],
    },
    {
      segment: "Vacation Rentals (Airbnb-style)",
      description: "Trust & safety for property owners and guests",
      requirements: [
        "Host verification",
        "Guest background checks",
        "Identity confirmation",
        "Damage protection",
        "Party prevention",
      ],
    },
    {
      segment: "Airlines",
      description: "Secure ticketing and passenger verification",
      requirements: [
        "Passenger identity verification",
        "Ticket fraud prevention",
        "Loyalty program protection",
        "Upgrade eligibility",
        "Document verification",
      ],
    },
    {
      segment: "Car Rental",
      description: "Driver verification and fraud prevention",
      requirements: [
        "Driver's license validation",
        "Age verification",
        "Insurance compliance",
        "Theft prevention",
        "International license checks",
      ],
    },
    {
      segment: "Tour Operators",
      description: "Group bookings and activity verification",
      requirements: [
        "Traveler identity confirmation",
        "Age-restricted activities",
        "Group management",
        "Liability waivers",
        "Emergency contact verification",
      ],
    },
    {
      segment: "Travel Agencies & OTAs",
      description: "Multi-supplier booking verification",
      requirements: [
        "Customer verification",
        "Payment fraud prevention",
        "Booking modification auth",
        "Refund verification",
        "Travel insurance validation",
      ],
    },
  ];

  const compliance = [
    {
      region: "European Union",
      requirements: "GDPR compliance, guest registration, Schengen area reporting",
      examples: "Italy (police reporting), Spain (Alojaweb), France (guest registration)",
    },
    {
      region: "United States",
      requirements: "State-specific hotel taxes, occupancy limits, short-term rental regulations",
      examples: "NYC registration, California TOT, Florida tourist taxes",
    },
    {
      region: "Middle East",
      requirements: "Passport registration, visa verification, local sponsorship",
      examples: "UAE tourist dirham, Saudi Arabia Absher, Qatar registration",
    },
    {
      region: "Asia Pacific",
      requirements: "Foreigner registration, hotel reporting, tax compliance",
      examples: "Japan immigration forms, Thailand TM30, Singapore registration",
    },
  ];

  const useCases = [
    {
      title: "Contactless Hotel Check-in",
      description: "Enable guests to check-in via mobile before arrival",
      benefits: ["Pre-arrival verification", "Room key activation", "Express lobby experience", "COVID-safe process"],
    },
    {
      title: "Vacation Rental Protection",
      description: "Verify guests for property damage and party prevention",
      benefits: ["Background checks", "Age verification", "Guest screening", "Insurance qualification"],
    },
    {
      title: "Flight Booking Verification",
      description: "Prevent ticket fraud and identity mismatches",
      benefits: ["Passenger name matching", "Payment verification", "Loyalty fraud prevention", "Group booking validation"],
    },
    {
      title: "Car Rental Authentication",
      description: "Verify driver credentials and prevent theft",
      benefits: ["License validation", "Age checks", "Insurance verification", "International license support"],
    },
  ];

  const metrics = [
    { label: "Fraud Reduction", value: "85%", icon: Shield },
    { label: "Check-in Speed", value: "< 2 min", icon: Clock },
    { label: "Global Coverage", value: "190+", icon: Globe },
    { label: "Compliance", value: "100%", icon: CheckCircle2 },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-sky-50 via-background to-sky-100 dark:from-sky-950 dark:to-background">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center justify-center gap-2 mb-4">
              <Plane className="w-8 h-8 text-primary" />
              <Badge variant="secondary" className="text-sm">
                Travel & Hospitality
              </Badge>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Identity Verification for
              <span className="text-primary block mt-2">Travel & Hospitality Industry</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
              Enable contactless check-in and meet global compliance requirements. Prevent booking fraud 
              while delivering seamless guest experiences across hotels, rentals, and travel platforms.
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

      {/* Travel Challenges */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Travel Industry Challenges</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Unique verification needs in travel and hospitality
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
            <h2 className="text-3xl font-bold mb-4">Travel Verification Solutions</h2>
            <p className="text-lg text-muted-foreground">
              Seamless verification for modern travelers
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

      {/* Travel Segments */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Travel & Hospitality Segments</h2>
            <p className="text-lg text-muted-foreground">
              Solutions for every part of the travel industry
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {travelSegments.map((segment, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{segment.segment}</CardTitle>
                  <CardDescription>{segment.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {segment.requirements.map((req, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-sm">
                        <CheckCircle2 className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                        <span>{req}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Global Compliance */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Global Compliance Coverage</h2>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Meet local registration and reporting requirements worldwide
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {compliance.map((item, index) => (
              <Card key={index}>
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <MapPin className="w-5 h-5 text-primary" />
                    <CardTitle className="text-lg">{item.region}</CardTitle>
                  </div>
                  <CardDescription>{item.requirements}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="text-xs text-muted-foreground">
                    <span className="font-medium">Examples:</span> {item.examples}
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
              Verification across the traveler journey
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
            <h2 className="text-3xl font-bold mb-4">Simple Integration</h2>
            <p className="text-lg text-muted-foreground">
              Add guest verification to your platform
            </p>
          </div>

          <Card className="max-w-3xl mx-auto">
            <CardHeader>
              <CardTitle>Hotel Contactless Check-in</CardTitle>
              <CardDescription>Pre-arrival guest verification</CardDescription>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-900 text-gray-100 p-6 rounded-lg overflow-x-auto text-sm">
                <code>{`// Initialize VeriGate for hospitality
const verigate = new VeriGate(apiKey, {
  industry: 'hospitality',
  jurisdiction: 'IT' // Italy
});

// Send pre-arrival verification link
await verigate.hospitality.sendCheckInLink({
  reservationId: booking.id,
  guestEmail: booking.email,
  checkInDate: booking.checkIn,
  propertyId: property.id
});

// When guest completes verification
const verification = await verigate.hospitality.verifyGuest({
  passportImage: guestPassport,
  selfieImage: guestSelfie,
  guestData: {
    firstName: 'John',
    lastName: 'Doe',
    nationality: 'US',
    passportNumber: 'AB1234567',
    dateOfBirth: '1985-03-15'
  },
  
  // Compliance requirements
  propertyAddress: property.address,
  reportingRequired: true, // Italy requires police reporting
  stayDuration: booking.nights
});

// Auto-submit to local authorities
if (verification.approved) {
  // Submit to Italian police (Alloggiati Web)
  await verigate.hospitality.submitPoliceReport({
    verificationId: verification.id,
    propertyId: property.id,
    checkInDate: booking.checkIn,
    checkOutDate: booking.checkOut
  });
  
  // Activate digital room key
  await activateRoomKey({
    guestId: verification.guestId,
    roomNumber: booking.room,
    validFrom: booking.checkIn,
    validUntil: booking.checkOut
  });
  
  // Send check-in confirmation
  await sendEmail({
    to: booking.email,
    template: 'check_in_complete',
    data: {
      roomNumber: booking.room,
      digitalKey: true
    }
  });
}`}</code>
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
            Transform Your Guest Experience
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join leading hotels and travel platforms using VeriGate for seamless, compliant guest verification
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

export default TravelHospitality;
