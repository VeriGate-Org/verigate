import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Calendar,
  MapPin,
  Clock,
  ArrowRight,
  Video,
  Users,
  Shield,
  BookOpen,
  Wrench,
  Rocket,
  MessageSquare,
} from "lucide-react";
import { Link } from "react-router-dom";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

const Events = () => {
  const upcomingEvents = [
    {
      title: "POPIA Compliance Masterclass",
      type: "Webinar",
      typeIcon: Video,
      date: "15 May 2026",
      time: "10:00 - 12:00 SAST",
      location: "Online",
      price: "Free",
      description:
        "A comprehensive deep-dive into POPIA compliance for background screening. Learn how to handle consent management, data minimisation, and subject access requests when processing employee verifications. Featuring insights from VeriGate's Head of Compliance and a guest speaker from the Information Regulator.",
      cta: "Register Free",
      featured: true,
    },
    {
      title: "HR Tech Africa Summit",
      type: "Conference",
      typeIcon: Users,
      date: "22-23 June 2026",
      time: "08:30 - 17:00 SAST",
      location: "Johannesburg",
      price: "R3,500",
      description:
        "South Africa's premier HR technology conference. Two days of keynotes, panels, and workshops covering the future of talent acquisition, background screening automation, AI in HR, and compliance technology. VeriGate will be exhibiting and presenting on verification API integrations.",
      cta: "Book Your Seat",
      featured: false,
    },
    {
      title: "Verification API Workshop",
      type: "Workshop",
      typeIcon: Wrench,
      date: "10 July 2026",
      time: "14:00 - 16:00 SAST",
      location: "Online",
      price: "Free",
      description:
        "A hands-on technical workshop for developers integrating VeriGate's REST API. Topics include OAuth 2.0 authentication, webhook configuration, batch processing endpoints, error handling, and SDK usage in Node.js, Python, and C#. Bring your laptop and follow along.",
      cta: "Register Free",
      featured: false,
    },
    {
      title: "Financial Services Compliance Forum",
      type: "Panel",
      typeIcon: MessageSquare,
      date: "5 August 2026",
      time: "09:00 - 13:00 SAST",
      location: "Cape Town",
      price: "R1,500",
      description:
        "A half-day forum focused on FICA, NCA, and POPIA compliance for the financial services sector. VeriGate's CEO will join a panel discussion on modernising KYC and CDD processes. Includes networking lunch and a live platform demo.",
      cta: "Book Your Seat",
      featured: true,
    },
  ];

  const pastEvents = [
    {
      title: "State of Background Screening 2026",
      type: "Webinar",
      typeIcon: Video,
      date: "12 February 2026",
      description:
        "We launched our annual State of Background Screening report, covering trends in verification volumes, turnaround times, and compliance across South Africa. The report analysed data from over 50,000 verifications processed through our platform in 2025.",
      attendees: "450+ registrations",
    },
    {
      title: "FICA Amendments Workshop",
      type: "Workshop",
      typeIcon: Shield,
      date: "28 January 2026",
      description:
        "A hands-on workshop in Cape Town for compliance officers and risk managers on the latest FICA amendments. Participants learned practical strategies for enhanced customer due diligence, suspicious transaction reporting, and integrating automated KYC checks.",
      attendees: "85 attendees",
    },
    {
      title: "VeriGate Product Launch",
      type: "Launch Event",
      typeIcon: Rocket,
      date: "15 November 2025",
      description:
        "The official launch of VeriGate's next-generation verification platform, featuring the new Workflow Builder, Compliance Engine, and Analytics Dashboard. Attended by clients, partners, and industry leaders at a Cape Town venue.",
      attendees: "150+ attendees",
    },
  ];

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container relative z-10">
          <AnimatedSection>
            <div className="container mx-auto max-w-6xl text-center space-y-6">
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                Events & Webinars
              </h1>
              <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
                Join us at industry events, webinars, and workshops focused on
                background screening, compliance, and verification best practices
                in South Africa.
              </p>
            </div>
          </AnimatedSection>
        </div>
      </section>

      {/* Upcoming Events */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <AnimatedSection>
            <div className="mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Upcoming Events
              </h2>
              <p className="text-lg text-muted-foreground">
                Register now to secure your place at our upcoming events and
                webinars.
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="space-y-8">
            {upcomingEvents.map((event) => {
              const TypeIcon = event.typeIcon;
              return (
                <Card
                  key={event.title}
                  className={`border-border hover:shadow-lg transition-shadow ${
                    event.featured ? "ring-1 ring-accent/30" : ""
                  }`}
                >
                  <CardHeader>
                    <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
                      <div className="space-y-3">
                        {event.featured && (
                          <Badge className="bg-accent text-accent-foreground">
                            Featured
                          </Badge>
                        )}
                        <CardTitle className="text-xl">
                          {event.title}
                        </CardTitle>
                        <div className="flex flex-wrap gap-3 text-sm text-muted-foreground">
                          <span className="flex items-center gap-1">
                            <TypeIcon className="w-4 h-4" />
                            {event.type}
                          </span>
                          <span className="flex items-center gap-1">
                            <Calendar className="w-4 h-4" />
                            {event.date}
                          </span>
                          <span className="flex items-center gap-1">
                            <Clock className="w-4 h-4" />
                            {event.time}
                          </span>
                          <span className="flex items-center gap-1">
                            <MapPin className="w-4 h-4" />
                            {event.location}
                          </span>
                        </div>
                      </div>
                      <div className="text-right shrink-0">
                        <span className="text-2xl font-bold text-accent">
                          {event.price}
                        </span>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <p className="text-muted-foreground text-sm mb-6">
                      {event.description}
                    </p>
                    <Button asChild>
                      <Link to="/contact">
                        {event.cta}
                        <ArrowRight className="w-4 h-4 ml-2" />
                      </Link>
                    </Button>
                  </CardContent>
                </Card>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* Past Events */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <AnimatedSection>
            <div className="mb-12">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Past Events
              </h2>
              <p className="text-lg text-muted-foreground">
                Catch up on what you may have missed. Recordings and resources are
                available for selected events.
              </p>
            </div>
          </AnimatedSection>

          <StaggeredList className="space-y-6">
            {pastEvents.map((event) => {
              const TypeIcon = event.typeIcon;
              return (
                <Card
                  key={event.title}
                  className="border-border opacity-90"
                >
                  <CardHeader>
                    <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3">
                      <div>
                        <CardTitle className="text-lg mb-2">
                          {event.title}
                        </CardTitle>
                        <div className="flex flex-wrap gap-3 text-sm text-muted-foreground">
                          <span className="flex items-center gap-1">
                            <TypeIcon className="w-4 h-4" />
                            {event.type}
                          </span>
                          <span className="flex items-center gap-1">
                            <Calendar className="w-4 h-4" />
                            {event.date}
                          </span>
                        </div>
                      </div>
                      <Badge variant="secondary" className="text-xs shrink-0">
                        {event.attendees}
                      </Badge>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <p className="text-muted-foreground text-sm">
                      {event.description}
                    </p>
                  </CardContent>
                </Card>
              );
            })}
          </StaggeredList>
        </div>
      </section>

      {/* Speaking Opportunities CTA */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl text-center">
          <AnimatedSection>
            <div className="mx-auto w-16 h-16 rounded-full bg-accent/10 flex items-center justify-center mb-6">
              <BookOpen className="w-8 h-8 text-accent" />
            </div>
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Speaking Opportunities
            </h2>
            <p className="text-lg text-muted-foreground mb-8 max-w-2xl mx-auto">
              Interested in speaking at a VeriGate event or inviting us to speak at
              yours? We regularly present on background screening, compliance, and
              verification technology in South Africa.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" asChild>
                <Link to="/contact">
                  Contact Us About Speaking
                  <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/resources">Browse Resources</Link>
              </Button>
            </div>
          </AnimatedSection>
        </div>
      </section>
    </div>
  );
};

export default Events;
