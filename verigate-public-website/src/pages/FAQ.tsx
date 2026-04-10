import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
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

  const faqs = [
    {
      q: "What types of verification checks does VeriGate offer?",
      a: "VeriGate offers a comprehensive range of verification checks including criminal record checks (SAPS), identity verification (DHA), qualification verification (SAQA), employment history checks, credit screening (TransUnion SA, Experian SA, XDS), document verification, business verification (CIPC), address verification, and biometric face verification.",
    },
    {
      q: "How long does a typical verification take?",
      a: "Turnaround times vary by check type. Identity verification and credit screening are typically instant to 4 hours. Employment history and qualification checks take 2-5 business days. Criminal record checks via SAPS take 3-5 business days. Our platform provides real-time status updates so you always know where each check stands.",
    },
    {
      q: "Is VeriGate POPIA-compliant?",
      a: "Yes, VeriGate is fully compliant with the Protection of Personal Information Act (POPIA). We handle consent management, data minimisation, purpose limitation, and secure data handling in accordance with the Act. We also maintain ISO 27001 and SOC 2 Type II certifications for information security.",
    },
    {
      q: "How does VeriGate integrate with our existing systems?",
      a: "VeriGate offers multiple integration options: a REST API for custom integrations, webhooks for real-time notifications, bulk CSV upload for batch processing, and a web dashboard for manual submissions. We also offer pre-built integrations with popular HRIS and ATS platforms. Our Professional and Enterprise plans include full API access.",
    },
    {
      q: "What does verification cost?",
      a: "Pricing starts at R29 per verification on our Starter plan (up to 100/month), R22 per verification on Professional (up to 1,000/month), and custom pricing for Enterprise volumes. All prices are in ZAR and include VAT. Visit our pricing page for full details.",
    },
    {
      q: "Can VeriGate verify international documents and qualifications?",
      a: "Yes, while our primary focus is South African verifications, we can verify international documents including passports, foreign qualifications, and international employment history. Our document verification engine supports documents from over 100 countries. Contact us for specific country coverage.",
    },
    {
      q: "What compliance frameworks does VeriGate support?",
      a: "VeriGate supports POPIA (Protection of Personal Information Act), FICA (Financial Intelligence Centre Act), NCA (National Credit Act), and sector-specific regulations including SARB directives for financial services, HPCSA requirements for healthcare, and the National Gambling Act for gaming. We also maintain ISO 27001 and SOC 2 Type II certifications.",
    },
    {
      q: "How accurate are VeriGate's verification results?",
      a: "VeriGate maintains a 99.2% accuracy rate across all verification types. Every result undergoes quality assurance review by our compliance team before delivery. In the rare event of a discrepancy, we investigate and provide updated results at no additional cost.",
    },
    {
      q: "Do you require long-term contracts?",
      a: "No, our Starter and Professional plans are month-to-month with no long-term commitment. Enterprise plans typically involve an annual agreement with negotiated pricing and SLA guarantees. You can upgrade, downgrade, or cancel at any time.",
    },
    {
      q: "How does VeriGate protect our data?",
      a: "All data is encrypted in transit (TLS 1.3) and at rest (AES-256). We maintain ISO 27001 and SOC 2 Type II certifications, operate from secure South African data centres, and follow the principle of data minimisation. Access controls, audit logging, and regular security assessments ensure your data remains protected.",
    },
    {
      q: "What support options are available?",
      a: "Starter plans include email support (response within 24 hours). Professional plans include priority email and phone support (response within 4 hours). Enterprise plans include a dedicated account manager and 24/7 support with 2-hour SLA. All clients have access to our knowledge base and documentation.",
    },
    {
      q: "Can we process verifications in bulk?",
      a: "Yes, our Professional and Enterprise plans include bulk upload functionality. Upload a CSV file with candidate details through our dashboard, and we'll process all verifications in parallel. Our API also supports batch submissions for automated workflows. There's no limit on batch size for Enterprise clients.",
    },
  ];

  const filteredFaqs = searchQuery
    ? faqs.filter(
        (faq) =>
          faq.q.toLowerCase().includes(searchQuery.toLowerCase()) ||
          faq.a.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : faqs;

  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-background to-primary/10">
        <div className="container mx-auto max-w-6xl text-center">
          <h1 className="text-4xl md:text-5xl font-bold mb-6">
            Frequently Asked Questions
          </h1>
          <p className="text-xl text-muted-foreground mb-8">
            Find answers to common questions about VeriGate's verification services
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

      {/* FAQ List */}
      <section className="py-16">
        <div className="container mx-auto max-w-4xl">
          <Card>
            <CardContent className="p-0">
              <Accordion type="single" collapsible className="w-full">
                {filteredFaqs.map((faq, index) => (
                  <AccordionItem key={index} value={`faq-${index}`} className="border-b last:border-b-0 px-6">
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

          {filteredFaqs.length === 0 && (
            <div className="text-center py-12">
              <p className="text-muted-foreground">No matching questions found. Try a different search term.</p>
            </div>
          )}
        </div>
      </section>

      {/* Still Have Questions */}
      <section className="py-16 bg-secondary">
        <div className="container mx-auto max-w-6xl text-center">
          <MessageCircle className="w-16 h-16 text-primary mx-auto mb-6" />
          <h2 className="text-3xl font-bold mb-4">
            Still Have Questions?
          </h2>
          <p className="text-xl text-muted-foreground mb-8">
            Our team is here to help during business hours (Mon-Fri, 08:00-17:00 SAST)
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Button size="lg" asChild>
              <Link to="/contact">Contact Us</Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link to="/technical-support">Technical Support</Link>
            </Button>
          </div>
        </div>
      </section>
    </div>
  );
};

export default FAQ;
