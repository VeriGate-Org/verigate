import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { newsletterSchema, type NewsletterData } from "@/lib/validations";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useToast } from "@/hooks/use-toast";
import { Loader2, Mail, CheckCircle2 } from "lucide-react";

interface NewsletterSignupProps {
  source?: string;
  variant?: "inline" | "card";
  className?: string;
}

export function NewsletterSignup({ 
  source = "footer", 
  variant = "inline",
  className = "" 
}: NewsletterSignupProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const { toast } = useToast();

  const form = useForm<NewsletterData>({
    resolver: zodResolver(newsletterSchema),
    defaultValues: {
      email: "",
      source,
    },
  });

  const onSubmit = async (data: NewsletterData) => {
    setIsSubmitting(true);
    
    try {
      // Send welcome email
      const { emailService } = await import('@/lib/email');
      const emailSent = await emailService.sendNewsletterWelcome({
        email: data.email,
        consent: data.consent,
        source: 'website_footer',
      });

      // Add to CRM newsletter list
      const { crmService } = await import('@/lib/crm');
      const added = await crmService.addNewsletterSubscriber(data.email, 'website_footer');

      // Track subscription
      const { analyticsService } = await import('@/lib/analytics');
      analyticsService.trackFormSubmission('newsletter', {
        source: 'footer',
      });

      if (emailSent || added) {
        setIsSuccess(true);
        form.reset();
        
        toast({
          title: "Successfully subscribed!",
          description: "Thank you for subscribing to our newsletter.",
          duration: 5000,
        });

        // Reset success state after 5 seconds
        setTimeout(() => setIsSuccess(false), 5000);
      } else {
        throw new Error('Failed to subscribe');
      }
    } catch (error) {
      console.error("Newsletter submission error:", error);
      toast({
        variant: "destructive",
        title: "Subscription failed",
        description: "Please try again later.",
        duration: 5000,
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (variant === "card") {
    return (
      <div className={`p-6 rounded-lg border border-border bg-card ${className}`}>
        <div className="flex items-center gap-3 mb-4">
          <div className="p-2 rounded-lg bg-accent/10">
            <Mail className="w-5 h-5 text-accent" />
          </div>
          <div>
            <h3 className="font-semibold">Stay Updated</h3>
            <p className="text-sm text-muted-foreground">
              Get the latest identity verification insights
            </p>
          </div>
        </div>
        
        {isSuccess ? (
          <div className="flex items-center gap-2 text-sm text-accent">
            <CheckCircle2 className="w-4 h-4" />
            <span>Successfully subscribed!</span>
          </div>
        ) : (
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-3">
            <Input
              type="email"
              placeholder="Enter your email"
              {...form.register("email")}
              disabled={isSubmitting}
            />
            {form.formState.errors.email && (
              <p className="text-sm text-destructive">
                {form.formState.errors.email.message}
              </p>
            )}
            <Button 
              type="submit" 
              className="w-full" 
              disabled={isSubmitting}
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Subscribing...
                </>
              ) : (
                "Subscribe"
              )}
            </Button>
          </form>
        )}
      </div>
    );
  }

  // Inline variant (default)
  return (
    <div className={className}>
      {isSuccess ? (
        <div className="flex items-center gap-2 text-sm text-accent">
          <CheckCircle2 className="w-4 h-4" />
          <span>Successfully subscribed!</span>
        </div>
      ) : (
        <form onSubmit={form.handleSubmit(onSubmit)} className="flex gap-2">
          <div className="flex-1">
            <Input
              type="email"
              placeholder="Enter your email"
              {...form.register("email")}
              disabled={isSubmitting}
              className="w-full"
            />
            {form.formState.errors.email && (
              <p className="text-sm text-destructive mt-1">
                {form.formState.errors.email.message}
              </p>
            )}
          </div>
          <Button 
            type="submit" 
            disabled={isSubmitting}
            className="whitespace-nowrap"
          >
            {isSubmitting ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              "Subscribe"
            )}
          </Button>
        </form>
      )}
    </div>
  );
}
