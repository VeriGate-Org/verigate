import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Checkbox } from "@/components/ui/checkbox";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  CircleCheck,
  Shield,
  Zap,
  Clock,
  Users,
  BarChart3,
  ArrowRight,
  Send,
} from "lucide-react";
import { useState } from "react";

const RequestDemo = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    workEmail: "",
    phone: "",
    companyName: "",
    companySize: "",
    industry: "",
    message: "",
  });

  const [verificationNeeds, setVerificationNeeds] = useState({
    criminal: false,
    identity: false,
    qualification: false,
    employment: false,
    credit: false,
    other: false,
  });

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSelectChange = (name: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleCheckboxChange = (name: string, checked: boolean) => {
    setVerificationNeeds((prev) => ({
      ...prev,
      [name]: checked,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    // Simulate form submission
    setTimeout(() => {
      setIsSubmitting(false);
      setIsSubmitted(true);
    }, 1500);
  };

  const companySizes = [
    { value: "1-50", label: "1-50 employees" },
    { value: "51-200", label: "51-200 employees" },
    { value: "201-500", label: "201-500 employees" },
    { value: "500+", label: "500+ employees" },
  ];

  const industries = [
    { value: "banking-finance", label: "Banking & Financial Services" },
    { value: "insurance", label: "Insurance" },
    { value: "healthcare", label: "Healthcare" },
    { value: "telecommunications", label: "Telecommunications" },
    { value: "retail", label: "Retail & E-commerce" },
    { value: "mining", label: "Mining & Resources" },
    { value: "government", label: "Government & Public Sector" },
    { value: "legal", label: "Legal & Professional Services" },
    { value: "education", label: "Education" },
    { value: "manufacturing", label: "Manufacturing" },
    { value: "technology", label: "Technology" },
    { value: "other", label: "Other" },
  ];

  const verificationOptions = [
    { key: "criminal", label: "Criminal Checks" },
    { key: "identity", label: "Identity Verification" },
    { key: "qualification", label: "Qualification Checks" },
    { key: "employment", label: "Employment History" },
    { key: "credit", label: "Credit Screening" },
    { key: "other", label: "Other" },
  ];

  const benefits = [
    {
      icon: Zap,
      title: "Fast Turnaround",
      description: "Most verifications completed within 24 hours",
    },
    {
      icon: Shield,
      title: "POPIA Compliant",
      description: "Full compliance with South African data protection laws",
    },
    {
      icon: BarChart3,
      title: "99.2% Accuracy",
      description: "Industry-leading accuracy across all verification types",
    },
    {
      icon: Users,
      title: "200+ Clients",
      description: "Trusted by leading South African organisations",
    },
    {
      icon: Clock,
      title: "Dedicated Support",
      description: "Priority support with dedicated account managers",
    },
  ];

  if (isSubmitted) {
    return (
      <div className="bg-background">
        <section className="relative pt-32 pb-20 overflow-hidden">
          <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
          <div className="container mx-auto max-w-2xl relative z-10 text-center">
            <div className="w-20 h-20 rounded-full bg-accent/10 flex items-center justify-center mx-auto mb-6">
              <CircleCheck className="w-10 h-10 text-accent" />
            </div>
            <h1 className="text-4xl md:text-5xl font-bold text-foreground mb-6">
              Thank You!
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Your demo request has been submitted successfully. Our team will be
              in touch within one business day to schedule your personalised demo.
            </p>
            <div className="flex gap-4 justify-center flex-wrap">
              <Button size="lg" variant="outline" asChild>
                <a href="/">Return to Home</a>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <a href="/platform">Explore Platform</a>
              </Button>
            </div>
          </div>
        </section>
      </div>
    );
  }

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="max-w-3xl space-y-6">
            <Badge variant="secondary" className="mb-4">
              <Send className="w-3 h-3 mr-1" />
              Request a Demo
            </Badge>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              See VeriGate in Action
            </h1>
            <p className="text-xl text-muted-foreground">
              Book a personalised demo and see how VeriGate can streamline your
              background screening. Our team will walk you through the platform
              tailored to your industry and requirements.
            </p>
          </div>
        </div>
      </section>

      {/* Form + Side Panel */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="grid lg:grid-cols-3 gap-12">
            {/* Form */}
            <div className="lg:col-span-2">
              <Card className="border-border">
                <CardHeader>
                  <CardTitle className="text-2xl">Request Your Demo</CardTitle>
                  <CardDescription>
                    Fill out the form below and we'll get back to you within one
                    business day.
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <form onSubmit={handleSubmit} className="space-y-6">
                    {/* Name Row */}
                    <div className="grid md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="firstName">First Name *</Label>
                        <Input
                          id="firstName"
                          name="firstName"
                          placeholder="Enter your first name"
                          value={formData.firstName}
                          onChange={handleInputChange}
                          required
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="lastName">Last Name *</Label>
                        <Input
                          id="lastName"
                          name="lastName"
                          placeholder="Enter your last name"
                          value={formData.lastName}
                          onChange={handleInputChange}
                          required
                        />
                      </div>
                    </div>

                    {/* Contact Row */}
                    <div className="grid md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="workEmail">Work Email *</Label>
                        <Input
                          id="workEmail"
                          name="workEmail"
                          type="email"
                          placeholder="you@company.co.za"
                          value={formData.workEmail}
                          onChange={handleInputChange}
                          required
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="phone">Phone Number</Label>
                        <Input
                          id="phone"
                          name="phone"
                          type="tel"
                          placeholder="+27 (0)XX XXX XXXX"
                          value={formData.phone}
                          onChange={handleInputChange}
                        />
                      </div>
                    </div>

                    {/* Company Row */}
                    <div className="grid md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="companyName">Company Name *</Label>
                        <Input
                          id="companyName"
                          name="companyName"
                          placeholder="Your company name"
                          value={formData.companyName}
                          onChange={handleInputChange}
                          required
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="companySize">Company Size *</Label>
                        <Select
                          value={formData.companySize}
                          onValueChange={(value) =>
                            handleSelectChange("companySize", value)
                          }
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Select company size" />
                          </SelectTrigger>
                          <SelectContent>
                            {companySizes.map((size) => (
                              <SelectItem key={size.value} value={size.value}>
                                {size.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>
                    </div>

                    {/* Industry */}
                    <div className="space-y-2">
                      <Label htmlFor="industry">Industry *</Label>
                      <Select
                        value={formData.industry}
                        onValueChange={(value) =>
                          handleSelectChange("industry", value)
                        }
                      >
                        <SelectTrigger>
                          <SelectValue placeholder="Select your industry" />
                        </SelectTrigger>
                        <SelectContent>
                          {industries.map((industry) => (
                            <SelectItem key={industry.value} value={industry.value}>
                              {industry.label}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>

                    {/* Verification Needs */}
                    <div className="space-y-3">
                      <Label>Verification Needs</Label>
                      <p className="text-sm text-muted-foreground">
                        Select all that apply
                      </p>
                      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                        {verificationOptions.map((option) => (
                          <div
                            key={option.key}
                            className="flex items-center space-x-2"
                          >
                            <Checkbox
                              id={option.key}
                              checked={
                                verificationNeeds[
                                  option.key as keyof typeof verificationNeeds
                                ]
                              }
                              onCheckedChange={(checked) =>
                                handleCheckboxChange(
                                  option.key,
                                  checked as boolean
                                )
                              }
                            />
                            <Label
                              htmlFor={option.key}
                              className="text-sm font-normal cursor-pointer"
                            >
                              {option.label}
                            </Label>
                          </div>
                        ))}
                      </div>
                    </div>

                    {/* Message */}
                    <div className="space-y-2">
                      <Label htmlFor="message">Message / Notes</Label>
                      <Textarea
                        id="message"
                        name="message"
                        placeholder="Tell us about your specific requirements, current challenges, or any questions you have..."
                        rows={4}
                        value={formData.message}
                        onChange={handleInputChange}
                      />
                    </div>

                    {/* Submit */}
                    <Button
                      type="submit"
                      size="lg"
                      className="w-full"
                      disabled={isSubmitting}
                    >
                      {isSubmitting ? (
                        "Submitting..."
                      ) : (
                        <>
                          Request Demo
                          <ArrowRight className="w-4 h-4 ml-2" />
                        </>
                      )}
                    </Button>

                    <p className="text-xs text-muted-foreground text-center">
                      By submitting this form, you agree to our{" "}
                      <a href="/privacy" className="text-accent hover:underline">
                        Privacy Policy
                      </a>{" "}
                      and consent to VeriGate contacting you about our products and
                      services.
                    </p>
                  </form>
                </CardContent>
              </Card>
            </div>

            {/* Side Panel - Why VeriGate */}
            <div className="space-y-6">
              <Card className="border-border bg-secondary/30">
                <CardHeader>
                  <CardTitle className="text-xl">Why VeriGate?</CardTitle>
                  <CardDescription>
                    Trusted by 200+ South African organisations
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                  {benefits.map((benefit, index) => {
                    const Icon = benefit.icon;
                    return (
                      <div key={index} className="flex items-start gap-3">
                        <div className="w-10 h-10 rounded-lg bg-accent/10 flex items-center justify-center flex-shrink-0">
                          <Icon className="w-5 h-5 text-accent" />
                        </div>
                        <div>
                          <h3 className="font-semibold text-sm">{benefit.title}</h3>
                          <p className="text-sm text-muted-foreground">
                            {benefit.description}
                          </p>
                        </div>
                      </div>
                    );
                  })}
                </CardContent>
              </Card>

              <Card className="border-border">
                <CardContent className="pt-6">
                  <p className="text-sm text-muted-foreground mb-4">
                    "VeriGate reduced our screening turnaround from 5 days to under
                    24 hours. The POPIA compliance features gave us complete peace of
                    mind."
                  </p>
                  <div>
                    <p className="font-semibold text-sm">Sarah van der Merwe</p>
                    <p className="text-xs text-muted-foreground">
                      HR Director, Leading SA Insurance Group
                    </p>
                  </div>
                </CardContent>
              </Card>

              <Card className="border-border">
                <CardContent className="pt-6 text-center">
                  <p className="text-sm text-muted-foreground mb-2">
                    Prefer to speak directly?
                  </p>
                  <p className="font-semibold text-accent">+27 (0)21 555 0123</p>
                  <p className="text-xs text-muted-foreground mt-1">
                    Mon-Fri, 08:00 - 17:00 SAST
                  </p>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default RequestDemo;
