import { z } from "zod";

// Contact Form Validation Schema
export const contactFormSchema = z.object({
  name: z.string().min(2, "Name must be at least 2 characters").max(100),
  email: z.string().email("Please enter a valid email address"),
  company: z.string().optional(),
  phone: z.string().optional(),
  subject: z.string().min(1, "Please select a subject"),
  message: z.string().min(10, "Message must be at least 10 characters").max(1000),
});

export type ContactFormData = z.infer<typeof contactFormSchema>;

// Demo Request Form Validation Schema
export const demoRequestSchema = z.object({
  firstName: z.string().min(2, "First name must be at least 2 characters"),
  lastName: z.string().min(2, "Last name must be at least 2 characters"),
  email: z.string().email("Please enter a valid email address"),
  company: z.string().min(2, "Company name is required"),
  jobTitle: z.string().min(2, "Job title is required"),
  phone: z.string().optional(),
  companySize: z.string().min(1, "Please select company size"),
  useCase: z.string().min(10, "Please describe your use case (min 10 characters)"),
  preferredDate: z.string().optional(),
});

export type DemoRequestData = z.infer<typeof demoRequestSchema>;

// Newsletter Signup Validation Schema
export const newsletterSchema = z.object({
  email: z.string().email("Please enter a valid email address"),
  source: z.string().optional(),
});

export type NewsletterData = z.infer<typeof newsletterSchema>;
