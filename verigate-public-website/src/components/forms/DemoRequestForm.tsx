import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { demoRequestSchema, type DemoRequestData } from "@/lib/validations";
import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormDescription,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";
import { Loader2, CheckCircle2, Calendar } from "lucide-react";

export function DemoRequestForm() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const { toast } = useToast();

  const form = useForm<DemoRequestData>({
    resolver: zodResolver(demoRequestSchema),
    defaultValues: {
      firstName: "",
      lastName: "",
      email: "",
      company: "",
      jobTitle: "",
      phone: "",
      companySize: "",
      useCase: "",
      preferredDate: "",
    },
  });

  const onSubmit = async (data: DemoRequestData) => {
    setIsSubmitting(true);
    
    try {
      // Send email via email service
      const { emailService } = await import('@/lib/email');
      const emailSent = await emailService.sendDemoRequestEmail({
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        company: data.company,
        jobTitle: data.jobTitle,
        phone: data.phone,
        companySize: data.companySize,
        useCase: data.useCase,
        message: data.message,
        consent: data.consent,
      });

      // Create lead in CRM
      const { crmService } = await import('@/lib/crm');
      const leadCreated = await crmService.createLeadFromDemo({
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        company: data.company,
        jobTitle: data.jobTitle,
        phone: data.phone,
        companySize: data.companySize,
        useCase: data.useCase,
        message: data.message,
      });

      // Track demo request (high-value conversion)
      const { analyticsService } = await import('@/lib/analytics');
      analyticsService.trackFormSubmission('demo_request', {
        company_size: data.companySize,
        use_case: data.useCase,
        has_job_title: !!data.jobTitle,
        has_phone: !!data.phone,
      });

      if (emailSent || leadCreated) {
        setIsSuccess(true);
        form.reset();
        
        toast({
          title: "Demo request received!",
          description: "Our team will contact you shortly to schedule your personalized demo.",
          duration: 5000,
        });
      } else {
        throw new Error('Failed to process demo request');
      }
    } catch (error) {
      console.error("Form submission error:", error);
      toast({
        variant: "destructive",
        title: "Submission failed",
        description: "Please try again or contact us at sales@verigate.com",
        duration: 5000,
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isSuccess) {
    return (
      <div className="flex flex-col items-center justify-center p-12 text-center bg-secondary/20 rounded-lg border border-border">
        <CheckCircle2 className="w-16 h-16 text-accent mb-4" />
        <h3 className="text-2xl font-bold mb-2">Demo Request Received!</h3>
        <p className="text-muted-foreground mb-6 max-w-md">
          Thank you for your interest in VeriGate. A member of our team will reach out within the next business day to schedule your personalized demo.
        </p>
        <div className="flex gap-4">
          <Button onClick={() => setIsSuccess(false)} variant="outline">
            Request Another Demo
          </Button>
          <Button onClick={() => window.location.href = "/"}>
            Return to Home
          </Button>
        </div>
      </div>
    );
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        {/* Personal Information */}
        <div className="space-y-4">
          <h3 className="text-lg font-semibold">Personal Information</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <FormField
              control={form.control}
              name="firstName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>First Name *</FormLabel>
                  <FormControl>
                    <Input placeholder="John" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="lastName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Last Name *</FormLabel>
                  <FormControl>
                    <Input placeholder="Doe" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Work Email *</FormLabel>
                  <FormControl>
                    <Input type="email" placeholder="john@company.com" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="phone"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Phone Number</FormLabel>
                  <FormControl>
                    <Input type="tel" placeholder="+1 (555) 123-4567" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>
        </div>

        {/* Company Information */}
        <div className="space-y-4">
          <h3 className="text-lg font-semibold">Company Information</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <FormField
              control={form.control}
              name="company"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Company Name *</FormLabel>
                  <FormControl>
                    <Input placeholder="Your Company Inc." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="jobTitle"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Job Title *</FormLabel>
                  <FormControl>
                    <Input placeholder="Chief Technology Officer" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <FormField
            control={form.control}
            name="companySize"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Company Size *</FormLabel>
                <Select onValueChange={field.onChange} defaultValue={field.value}>
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder="Select company size" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value="1-10">1-10 employees</SelectItem>
                    <SelectItem value="11-50">11-50 employees</SelectItem>
                    <SelectItem value="51-200">51-200 employees</SelectItem>
                    <SelectItem value="201-500">201-500 employees</SelectItem>
                    <SelectItem value="501-1000">501-1,000 employees</SelectItem>
                    <SelectItem value="1000+">1,000+ employees</SelectItem>
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Use Case Information */}
        <div className="space-y-4">
          <h3 className="text-lg font-semibold">Tell Us About Your Needs</h3>
          
          <FormField
            control={form.control}
            name="useCase"
            render={({ field }) => (
              <FormItem>
                <FormLabel>What's your primary use case? *</FormLabel>
                <FormControl>
                  <Textarea
                    placeholder="e.g., We need to verify customer identities for regulatory compliance in our fintech platform..."
                    className="min-h-[120px] resize-none"
                    {...field}
                  />
                </FormControl>
                <FormDescription>
                  Help us prepare a personalized demo by describing your verification needs
                </FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="preferredDate"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Preferred Demo Date</FormLabel>
                <FormControl>
                  <div className="relative">
                    <Input 
                      type="date" 
                      min={new Date().toISOString().split('T')[0]}
                      {...field} 
                    />
                    <Calendar className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground pointer-events-none" />
                  </div>
                </FormControl>
                <FormDescription>
                  Optional - We'll work with your schedule to find the best time
                </FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <div className="flex items-center justify-between pt-4 border-t">
          <p className="text-sm text-muted-foreground">
            * Required fields
          </p>
          <Button type="submit" size="lg" disabled={isSubmitting}>
            {isSubmitting ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Submitting...
              </>
            ) : (
              "Request Demo"
            )}
          </Button>
        </div>
      </form>
    </Form>
  );
}
