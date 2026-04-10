import { ContactForm } from "@/components/forms/ContactForm";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Mail, Phone, MapPin, MessageSquare, Clock, Globe } from "lucide-react";
import { Link } from "react-router-dom";

const Contact = () => {
  const contactMethods = [
    {
      icon: Mail,
      title: "Email",
      primary: "info@verigate.co.za",
      secondary: "General enquiries",
      href: "mailto:info@verigate.co.za",
    },
    {
      icon: Phone,
      title: "Phone",
      primary: "+27 (0)21 555 0123",
      secondary: "Mon-Fri, 08:00-17:00 SAST",
      href: "tel:+27215550123",
    },
    {
      icon: Mail,
      title: "Support",
      primary: "support@verigate.co.za",
      secondary: "Technical support",
      href: "mailto:support@verigate.co.za",
    },
    {
      icon: MessageSquare,
      title: "Live Chat",
      primary: "Chat with us",
      secondary: "Mon-Fri, 08:00-17:00 SAST",
      href: "#",
    },
  ];

  const enquiryTypes = [
    "General Enquiry",
    "Request a Demo",
    "Pricing Information",
    "Technical Support",
    "Partnership Enquiry",
  ];

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container relative z-10">
          <div className="max-w-3xl space-y-6">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              Get in Touch
            </h1>
            <p className="text-xl text-muted-foreground">
              Have questions about our verification services? We're here to help. Reach out to our team and we'll get back to you within one business day.
            </p>
          </div>
        </div>
      </section>

      {/* Contact Methods */}
      <section className="py-12 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {contactMethods.map((method) => {
              const Icon = method.icon;
              return (
                <Card
                  key={method.title}
                  className="border-border hover:shadow-lg transition-shadow"
                >
                  <CardHeader>
                    <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center mb-4">
                      <Icon className="w-6 h-6 text-accent" />
                    </div>
                    <CardTitle className="text-lg">{method.title}</CardTitle>
                    <CardDescription>{method.secondary}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <a
                      href={method.href}
                      className="text-foreground font-medium hover:text-accent transition-colors"
                    >
                      {method.primary}
                    </a>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* Contact Form Section */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Send Us a Message
            </h2>
            <p className="text-lg text-muted-foreground">
              Fill out the form below and our team will respond within one business day
            </p>
          </div>

          <Card className="border-border">
            <CardContent className="pt-6">
              <ContactForm />
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Office Location */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Our Office
            </h2>
          </div>

          <Card className="border-border">
            <CardHeader>
              <div className="flex items-start gap-4">
                <div className="p-2 rounded-lg bg-accent/10">
                  <MapPin className="w-5 h-5 text-accent" />
                </div>
                <div className="flex-1">
                  <CardTitle className="text-xl mb-2">Cape Town</CardTitle>
                  <CardDescription className="space-y-1 text-sm">
                    <p>4th Floor, The Terraces</p>
                    <p>34 Bree Street</p>
                    <p>Cape Town, 8001</p>
                    <p>South Africa</p>
                  </CardDescription>
                </div>
              </div>
            </CardHeader>
          </Card>
        </div>
      </section>

      {/* Business Hours & Info */}
      <section className="py-20">
        <div className="container mx-auto max-w-4xl">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <Card className="border-border">
              <CardHeader>
                <div className="flex items-center gap-3 mb-2">
                  <Clock className="w-6 h-6 text-accent" />
                  <CardTitle>Business Hours</CardTitle>
                </div>
              </CardHeader>
              <CardContent className="space-y-2 text-muted-foreground">
                <div className="flex justify-between">
                  <span>Monday - Friday:</span>
                  <span className="font-medium text-foreground">08:00 - 17:00 SAST</span>
                </div>
                <div className="flex justify-between">
                  <span>Saturday:</span>
                  <span className="font-medium text-foreground">Closed</span>
                </div>
                <div className="flex justify-between">
                  <span>Sunday:</span>
                  <span className="font-medium text-foreground">Closed</span>
                </div>
                <p className="text-sm pt-4 border-t mt-4">
                  * Enterprise clients have access to 24/7 support via their dedicated account manager
                </p>
              </CardContent>
            </Card>

            <Card className="border-border">
              <CardHeader>
                <div className="flex items-center gap-3 mb-2">
                  <Globe className="w-6 h-6 text-accent" />
                  <CardTitle>Enquiry Types</CardTitle>
                </div>
              </CardHeader>
              <CardContent className="space-y-4 text-muted-foreground">
                <p>We handle a range of enquiries:</p>
                <div className="space-y-2">
                  {enquiryTypes.map((type) => (
                    <div key={type} className="flex items-center gap-2">
                      <div className="w-2 h-2 rounded-full bg-accent" />
                      <span>{type}</span>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* FAQ Quick Links */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl text-center">
          <h2 className="text-2xl md:text-3xl font-bold text-foreground mb-4">
            Looking for Quick Answers?
          </h2>
          <p className="text-lg text-muted-foreground mb-8">
            Check out our frequently asked questions or browse our support resources
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link to="/faqs">
              <Card className="border-border hover:shadow-lg transition-shadow cursor-pointer p-6">
                <h3 className="font-semibold mb-2">FAQs</h3>
                <p className="text-sm text-muted-foreground">
                  Browse common questions and answers
                </p>
              </Card>
            </Link>
            <Link to="/technical-support">
              <Card className="border-border hover:shadow-lg transition-shadow cursor-pointer p-6">
                <h3 className="font-semibold mb-2">Technical Support</h3>
                <p className="text-sm text-muted-foreground">
                  Access guides and documentation
                </p>
              </Card>
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Contact;
