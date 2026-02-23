import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import { Search, MessageCircle } from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";

const FAQ = () => {
  const [searchQuery, setSearchQuery] = useState("");

  const faqCategories = [
    {
      category: "Getting Started",
      icon: "🚀",
      questions: [
        {
          q: "How do I get started with VeriGate?",
          a: "Getting started is easy! Sign up for a free account, get your API keys from the dashboard, and follow our Quick Start guide. You can make your first verification request in under 5 minutes using our API or SDKs.",
        },
        {
          q: "Do I need technical knowledge to use VeriGate?",
          a: "For API integration, basic programming knowledge is helpful. However, we offer no-code solutions like hosted verification pages and pre-built integrations with platforms like Shopify, WordPress, and Webflow. Our support team can also help with implementation.",
        },
        {
          q: "How long does implementation take?",
          a: "Most developers complete basic integration in 1-2 hours. Full production deployment typically takes 1-2 weeks depending on your requirements and customization needs.",
        },
        {
          q: "Is there a free trial?",
          a: "Yes! We offer a free tier with 100 verifications per month. No credit card required. You can upgrade anytime as your volume grows.",
        },
      ],
    },
    {
      category: "Pricing & Billing",
      icon: "💳",
      questions: [
        {
          q: "How does pricing work?",
          a: "We charge per verification with volume-based discounts. Pricing starts at $0.50 per verification and decreases with higher volumes. Enterprise customers get custom pricing. Check our Pricing page for detailed tiers.",
        },
        {
          q: "What payment methods do you accept?",
          a: "We accept all major credit cards (Visa, Mastercard, Amex), ACH transfers, wire transfers, and can invoice for enterprise contracts. Payment is processed monthly based on usage.",
        },
        {
          q: "Can I get a refund?",
          a: "We offer refunds for unused credits within 30 days of purchase. Processed verifications cannot be refunded, but we'll credit your account if a verification failed due to our system error.",
        },
        {
          q: "Are there any setup fees or monthly minimums?",
          a: "No setup fees. No monthly minimums on pay-as-you-go plans. Enterprise plans may have monthly minimums depending on your agreement.",
        },
      ],
    },
    {
      category: "Technical & Integration",
      icon: "⚙️",
      questions: [
        {
          q: "Which programming languages do you support?",
          a: "We provide official SDKs for JavaScript/Node.js, Python, Ruby, PHP, Java, Go, C#/.NET, Swift (iOS), and Kotlin (Android). You can also use our REST API with any language that supports HTTP requests.",
        },
        {
          q: "Do you provide webhooks?",
          a: "Yes! Webhooks notify you in real-time when verification status changes. You can configure webhook endpoints in your dashboard and receive events like 'verification.completed', 'verification.failed', etc.",
        },
        {
          q: "What's your API uptime guarantee?",
          a: "We guarantee 99.9% uptime with our SLA. Our infrastructure is hosted on AWS with multi-region redundancy. Check our Status page for real-time uptime monitoring.",
        },
        {
          q: "Can I test the API before going live?",
          a: "Absolutely! Every account has a test mode with separate API keys. Use test mode to integrate and verify everything works before processing real verifications.",
        },
        {
          q: "What's the API rate limit?",
          a: "Free tier: 10 requests/second. Paid plans: 100 requests/second. Enterprise: custom rate limits. Rate limits are per API key and can be increased upon request.",
        },
      ],
    },
    {
      category: "Verification Process",
      icon: "✅",
      questions: [
        {
          q: "How long does a verification take?",
          a: "Most verifications complete in under 5 seconds. Document verification: 2-3 seconds, Biometric verification: 1-2 seconds, AML screening: 3-5 seconds. Complex cases requiring manual review may take up to 24 hours.",
        },
        {
          q: "What documents do you support?",
          a: "We support 5,000+ document types from 190+ countries including passports, national IDs, driver's licenses, residence permits, utility bills, bank statements, and more.",
        },
        {
          q: "What if a verification fails?",
          a: "Failed verifications include a detailed reason code (e.g., 'document_expired', 'face_mismatch', 'poor_image_quality'). Users can retry with better photos. You're only charged for successful verifications.",
        },
        {
          q: "Can users verify from their mobile phones?",
          a: "Yes! Our solution is mobile-optimized. We provide native mobile SDKs for iOS and Android, plus responsive web components that work perfectly on mobile browsers.",
        },
        {
          q: "Do you support video verification?",
          a: "Yes, for liveness detection. Users record a short video selfie while following prompts. Our AI analyzes the video to confirm it's a real person, not a photo or deepfake.",
        },
      ],
    },
    {
      category: "Security & Compliance",
      icon: "🔒",
      questions: [
        {
          q: "Is VeriGate GDPR compliant?",
          a: "Yes, we're fully GDPR compliant. We process data only as necessary for verification, provide data deletion on request, maintain EU data residency options, and have a Data Processing Agreement (DPA) available.",
        },
        {
          q: "Are you SOC 2 certified?",
          a: "Yes, we maintain SOC 2 Type II certification. Our security practices are audited annually. Reports available to enterprise customers under NDA.",
        },
        {
          q: "How do you protect sensitive data?",
          a: "All data is encrypted in transit (TLS 1.3) and at rest (AES-256). Biometric data is hashed and cannot be reverse-engineered. We follow the principle of minimal data retention and can delete data on request.",
        },
        {
          q: "Are you HIPAA compliant?",
          a: "Yes, we offer HIPAA-compliant verification for healthcare customers. We provide Business Associate Agreements (BAA) and maintain appropriate safeguards for PHI.",
        },
        {
          q: "Where is data stored?",
          a: "Primary data centers in US, EU, and Asia. You can choose your data residency region. Enterprise customers can request dedicated infrastructure in specific regions.",
        },
      ],
    },
    {
      category: "Features & Capabilities",
      icon: "⚡",
      questions: [
        {
          q: "Can I customize the verification flow?",
          a: "Yes! You can customize branding (colors, logos), required fields, accepted document types, liveness methods, and more. Enterprise customers can fully white-label the experience.",
        },
        {
          q: "Do you support age verification?",
          a: "Yes, we extract date of birth from documents and calculate age. You can set minimum age requirements (e.g., 18+, 21+) and block underage users automatically.",
        },
        {
          q: "Can I verify the same person multiple times?",
          a: "Yes. You can perform periodic re-verification or one-time re-checks. We can also detect duplicate accounts using biometric matching to prevent multi-accounting.",
        },
        {
          q: "Do you support bulk verification?",
          a: "Yes, via our Batch API. Upload CSV files with user data, and we'll process verifications asynchronously. Great for onboarding existing users or compliance audits.",
        },
        {
          q: "Can I get raw document data (OCR)?",
          a: "Yes, our API returns extracted data like name, address, date of birth, document number, expiry date, etc. Structured JSON format makes it easy to populate your database.",
        },
      ],
    },
    {
      category: "Support & Troubleshooting",
      icon: "🆘",
      questions: [
        {
          q: "What support channels are available?",
          a: "24/7 live chat, email support (support@verigate.com), developer forum, and phone support for enterprise customers. Average response time: under 2 hours.",
        },
        {
          q: "Do you offer professional services?",
          a: "Yes! We offer implementation support, custom integration development, training, and dedicated customer success managers for enterprise customers.",
        },
        {
          q: "Why is my verification failing?",
          a: "Common reasons: poor image quality, expired documents, face mismatch, unsupported document type, or user younger than required age. Check the error code in the response for specific details.",
        },
        {
          q: "How do I report a bug?",
          a: "Email support@verigate.com or use the bug report form in your dashboard. Include API request/response, timestamps, and steps to reproduce. Critical bugs are addressed within 24 hours.",
        },
      ],
    },
    {
      category: "Industry-Specific",
      icon: "🏢",
      questions: [
        {
          q: "Can VeriGate work for cryptocurrency exchanges?",
          a: "Absolutely! We support enhanced due diligence (EDD), source of funds verification, Travel Rule compliance, wallet address screening, and tiered KYC based on transaction volumes.",
        },
        {
          q: "Is VeriGate suitable for gaming/gambling platforms?",
          a: "Yes, we specialize in gaming compliance. Features include age verification (18+/21+), self-exclusion database checks, responsible gambling tools, and multi-jurisdiction compliance.",
        },
        {
          q: "Can financial institutions use VeriGate for KYC/AML?",
          a: "Yes, we're used by banks, neobanks, and fintechs for regulatory compliance. We screen against global sanctions lists, PEP databases, and support continuous monitoring.",
        },
        {
          q: "Does VeriGate work for marketplace platforms?",
          a: "Yes! Verify both buyers and sellers, prevent fraud, build trust scores, and support dispute resolution with verified identities.",
        },
      ],
    },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-background to-primary/10">
        <div className="container mx-auto max-w-6xl text-center">
          <h1 className="text-4xl md:text-5xl font-bold mb-6">
            Frequently Asked Questions
          </h1>
          <p className="text-xl text-muted-foreground mb-8">
            Find answers to common questions about VeriGate
          </p>
          
          {/* Search Bar */}
          <div className="relative max-w-2xl mx-auto">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
            <Input
              type="text"
              placeholder="Search FAQ..."
              className="pl-12 pr-4 py-6 text-lg"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
        </div>
      </section>

      {/* FAQ Categories */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="space-y-12">
            {faqCategories.map((category, catIndex) => (
              <div key={catIndex}>
                <div className="flex items-center gap-3 mb-6">
                  <span className="text-4xl">{category.icon}</span>
                  <div>
                    <h2 className="text-2xl font-bold">{category.category}</h2>
                    <p className="text-sm text-muted-foreground">{category.questions.length} questions</p>
                  </div>
                </div>

                <Card>
                  <CardContent className="p-0">
                    <Accordion type="single" collapsible className="w-full">
                      {category.questions.map((faq, qIndex) => (
                        <AccordionItem key={qIndex} value={`${catIndex}-${qIndex}`} className="border-b last:border-b-0 px-6">
                          <AccordionTrigger className="text-left hover:no-underline py-4">
                            <span className="font-semibold">{faq.q}</span>
                          </AccordionTrigger>
                          <AccordionContent className="text-muted-foreground pb-4">
                            {faq.a}
                          </AccordionContent>
                        </AccordionItem>
                      ))}
                    </Accordion>
                  </CardContent>
                </Card>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Still Have Questions */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl text-center">
          <MessageCircle className="w-16 h-16 text-primary mx-auto mb-6" />
          <h2 className="text-3xl font-bold mb-4">
            Still Have Questions?
          </h2>
          <p className="text-xl text-muted-foreground mb-8">
            Our support team is here to help 24/7
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" asChild>
              <Link to="/contact">Contact Support</Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link to="/help">Visit Help Center</Link>
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default FAQ;
