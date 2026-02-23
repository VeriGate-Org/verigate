import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { ContactForm } from "@/components/forms/ContactForm";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Mail, Phone, MapPin, MessageSquare, Clock, Globe } from "lucide-react";

const Contact = () => {
  const contactMethods = [
    {
      icon: Mail,
      title: "Email",
      primary: "sales@verigate.com",
      secondary: "For sales inquiries",
      href: "mailto:sales@verigate.com",
    },
    {
      icon: Mail,
      title: "Support",
      primary: "support@verigate.com",
      secondary: "For technical support",
      href: "mailto:support@verigate.com",
    },
    {
      icon: Phone,
      title: "Phone",
      primary: "+1 (555) 123-4567",
      secondary: "Mon-Fri, 9am-6pm EST",
      href: "tel:+15551234567",
    },
    {
      icon: MessageSquare,
      title: "Live Chat",
      primary: "Chat with us",
      secondary: "Available 24/7",
      href: "#",
      onClick: () => {
        // TODO: Open chat widget
        console.log("Open chat widget");
      },
    },
  ];

  const offices = [
    {
      city: "San Francisco",
      address: "123 Market Street, Suite 400",
      region: "San Francisco, CA 94105",
      country: "United States",
    },
    {
      city: "New York",
      address: "456 Broadway, Floor 12",
      region: "New York, NY 10013",
      country: "United States",
    },
    {
      city: "London",
      address: "789 Oxford Street",
      region: "London W1D 2HG",
      country: "United Kingdom",
    },
  ];

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container relative z-10">
          <div className="max-w-3xl space-y-6">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              Get in Touch
            </h1>
            <p className="text-xl text-muted-foreground">
              Have questions? We're here to help. Reach out to our team and we'll get back to you as soon as possible.
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
                  className="border-border hover:shadow-lg transition-shadow cursor-pointer"
                  onClick={method.onClick}
                >
                  <CardHeader>
                    <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center mb-4">
                      <Icon className="w-6 h-6 text-accent" />
                    </div>
                    <CardTitle className="text-lg">{method.title}</CardTitle>
                    <CardDescription>{method.secondary}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    {method.href.startsWith('#') ? (
                      <button className="text-foreground font-medium hover:text-accent transition-colors">
                        {method.primary}
                      </button>
                    ) : (
                      <a 
                        href={method.href} 
                        className="text-foreground font-medium hover:text-accent transition-colors"
                      >
                        {method.primary}
                      </a>
                    )}
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
              Fill out the form below and our team will respond within 24 hours
            </p>
          </div>
          
          <Card className="border-border">
            <CardContent className="pt-6">
              <ContactForm />
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Office Locations */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-4xl">
          <div className="mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Our Offices
            </h2>
            <p className="text-lg text-muted-foreground">
              Visit us at one of our global locations
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {offices.map((office) => (
              <Card key={office.city} className="border-border">
                <CardHeader>
                  <div className="flex items-start gap-4">
                    <div className="p-2 rounded-lg bg-accent/10">
                      <MapPin className="w-5 h-5 text-accent" />
                    </div>
                    <div className="flex-1">
                      <CardTitle className="text-xl mb-2">{office.city}</CardTitle>
                      <CardDescription className="space-y-1 text-sm">
                        <p>{office.address}</p>
                        <p>{office.region}</p>
                        <p>{office.country}</p>
                      </CardDescription>
                    </div>
                  </div>
                </CardHeader>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Additional Information */}
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
                  <span className="font-medium text-foreground">9:00 AM - 6:00 PM EST</span>
                </div>
                <div className="flex justify-between">
                  <span>Saturday:</span>
                  <span className="font-medium text-foreground">10:00 AM - 4:00 PM EST</span>
                </div>
                <div className="flex justify-between">
                  <span>Sunday:</span>
                  <span className="font-medium text-foreground">Closed</span>
                </div>
                <p className="text-sm pt-4 border-t mt-4">
                  * Support available 24/7 via email and chat for enterprise customers
                </p>
              </CardContent>
            </Card>

            <Card className="border-border">
              <CardHeader>
                <div className="flex items-center gap-3 mb-2">
                  <Globe className="w-6 h-6 text-accent" />
                  <CardTitle>Global Support</CardTitle>
                </div>
              </CardHeader>
              <CardContent className="space-y-4 text-muted-foreground">
                <p>
                  We provide support in multiple languages and time zones to serve our global customer base.
                </p>
                <div className="space-y-2">
                  <div className="flex items-center gap-2">
                    <div className="w-2 h-2 rounded-full bg-accent" />
                    <span>English, Spanish, French, German</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="w-2 h-2 rounded-full bg-accent" />
                    <span>24/7 emergency support for enterprise</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="w-2 h-2 rounded-full bg-accent" />
                    <span>Dedicated account managers</span>
                  </div>
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
            Check out our frequently asked questions or browse our help center
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <a href="/faq">
              <Card className="border-border hover:shadow-lg transition-shadow cursor-pointer p-6">
                <h3 className="font-semibold mb-2">FAQ</h3>
                <p className="text-sm text-muted-foreground">
                  Browse common questions and answers
                </p>
              </Card>
            </a>
            <a href="/help-center">
              <Card className="border-border hover:shadow-lg transition-shadow cursor-pointer p-6">
                <h3 className="font-semibold mb-2">Help Center</h3>
                <p className="text-sm text-muted-foreground">
                  Access guides and documentation
                </p>
              </Card>
            </a>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Contact;
