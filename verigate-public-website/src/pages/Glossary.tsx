import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { 
  Search,
  FileText,
  Globe,
  CheckCircle2,
  AlertCircle,
  MapPin
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";

const Glossary = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedLetter, setSelectedLetter] = useState("All");

  const alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

  const terms = [
    {
      term: "AML (Anti-Money Laundering)",
      definition: "A set of procedures, laws, and regulations designed to stop the practice of generating income through illegal actions. In identity verification, AML screening checks individuals against sanctions lists, PEPs, and adverse media.",
      category: "Compliance",
      relatedTerms: ["KYC", "PEP", "Sanctions Screening"]
    },
    {
      term: "Biometric Authentication",
      definition: "A security process that relies on unique biological characteristics of individuals to verify they are who they claim to be. Common methods include facial recognition, fingerprint scanning, and iris scanning.",
      category: "Security",
      relatedTerms: ["Liveness Detection", "Facial Recognition", "FAR/FRR"]
    },
    {
      term: "Document Verification",
      definition: "The process of validating the authenticity and legitimacy of identity documents such as passports, driver's licenses, and national ID cards using automated systems and manual review.",
      category: "Verification",
      relatedTerms: ["OCR", "MRZ", "Security Features"]
    },
    {
      term: "FAR (False Acceptance Rate)",
      definition: "A biometric system performance metric that measures the probability that the system incorrectly matches an input to a non-matching template in the database. Lower FAR indicates better security.",
      category: "Technical",
      relatedTerms: ["FRR", "Biometric Authentication", "Liveness Detection"]
    },
    {
      term: "FRR (False Rejection Rate)",
      definition: "A biometric system performance metric that measures the probability that the system incorrectly fails to match an input to a matching template. Lower FRR indicates better user experience.",
      category: "Technical",
      relatedTerms: ["FAR", "Biometric Authentication"]
    },
    {
      term: "GDPR (General Data Protection Regulation)",
      definition: "European Union regulation on data protection and privacy for individuals within the EU and the European Economic Area. It affects how identity verification systems must handle personal data.",
      category: "Compliance",
      relatedTerms: ["Data Protection", "Privacy", "Consent"]
    },
    {
      term: "KYC (Know Your Customer)",
      definition: "The process of verifying the identity of clients and assessing potential risks of illegal intentions. Required by regulations to prevent identity theft, financial fraud, money laundering, and terrorist financing.",
      category: "Compliance",
      relatedTerms: ["AML", "Identity Verification", "Customer Due Diligence"]
    },
    {
      term: "Liveness Detection",
      definition: "Technology that distinguishes between a live person and a representation (photo, video, or mask). Can be active (requiring user action) or passive (automatic detection without user interaction).",
      category: "Security",
      relatedTerms: ["Biometric Authentication", "Facial Recognition", "Anti-Spoofing"]
    },
    {
      term: "MRZ (Machine Readable Zone)",
      definition: "A section on identity documents that contains encoded information in a format readable by machines. Includes two or three lines of text with standardized information about the document holder.",
      category: "Technical",
      relatedTerms: ["Document Verification", "OCR", "Passport"]
    },
    {
      term: "OCR (Optical Character Recognition)",
      definition: "Technology that converts different types of documents into editable and searchable data. In identity verification, used to extract text from identity documents automatically.",
      category: "Technical",
      relatedTerms: ["Document Verification", "MRZ", "Data Extraction"]
    },
    {
      term: "PEP (Politically Exposed Person)",
      definition: "An individual who is or has been entrusted with a prominent public function. PEPs are considered higher risk for potential involvement in bribery and corruption due to their position and influence.",
      category: "Compliance",
      relatedTerms: ["AML", "Risk Assessment", "Sanctions"]
    },
    {
      term: "Sanctions Screening",
      definition: "The process of checking individuals and entities against government sanctions lists to ensure compliance with international regulations. Required for financial institutions and certain businesses.",
      category: "Compliance",
      relatedTerms: ["AML", "Watchlist", "Compliance"]
    },
    {
      term: "Travel Rule",
      definition: "FATF recommendation requiring virtual asset service providers (VASPs) to share originator and beneficiary information for cryptocurrency transactions above a certain threshold.",
      category: "Compliance",
      relatedTerms: ["FATF", "Cryptocurrency", "AML"]
    },
    {
      term: "Webhook",
      definition: "An HTTP callback that occurs when something happens; a simple event-notification via HTTP POST. In identity verification, used to notify your system when verification status changes.",
      category: "Technical",
      relatedTerms: ["API", "Event Notification", "Integration"]
    }
  ];

  const categories = ["All", "Compliance", "Security", "Technical", "Verification"];

  const filteredTerms = terms.filter(term => {
    const matchesSearch = searchQuery === "" || 
      term.term.toLowerCase().includes(searchQuery.toLowerCase()) ||
      term.definition.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesLetter = selectedLetter === "All" || 
      term.term.startsWith(selectedLetter);
    
    return matchesSearch && matchesLetter;
  });

  const getCategoryColor = (category: string) => {
    const colors: any = {
      "Compliance": "bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200",
      "Security": "bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200",
      "Technical": "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200",
      "Verification": "bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-200"
    };
    return colors[category] || "bg-gray-100 text-gray-800";
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-primary/10 to-secondary/5">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl text-center">
            <Badge className="mb-4" variant="outline">
              <FileText className="w-3 h-3 mr-1" />
              Reference Guide
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Identity Verification Glossary
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Comprehensive guide to identity verification terminology, compliance terms, 
              and technical concepts. Your A-Z reference for KYC, AML, and digital identity.
            </p>
            
            {/* Search */}
            <div className="max-w-2xl mx-auto relative">
              <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
              <Input
                type="search"
                placeholder="Search terms..."
                className="pl-12 h-12 text-lg"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
          </div>
        </div>
      </section>

      {/* Stats */}
      <section className="py-8 border-b bg-background">
        <div className="container mx-auto">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 text-center max-w-4xl mx-auto">
            <div>
              <div className="text-3xl font-bold text-primary mb-1">{terms.length}+</div>
              <div className="text-sm text-muted-foreground">Terms Defined</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">4</div>
              <div className="text-sm text-muted-foreground">Categories</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">A-Z</div>
              <div className="text-sm text-muted-foreground">Alphabetical</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">Free</div>
              <div className="text-sm text-muted-foreground">Open Access</div>
            </div>
          </div>
        </div>
      </section>

      {/* Alphabet Navigation */}
      <section className="py-8 border-b bg-muted/30 sticky top-0 z-10">
        <div className="container mx-auto">
          <div className="flex flex-wrap items-center justify-center gap-2">
            <Button
              variant={selectedLetter === "All" ? "default" : "ghost"}
              size="sm"
              onClick={() => setSelectedLetter("All")}
            >
              All
            </Button>
            {alphabet.map((letter) => (
              <Button
                key={letter}
                variant={selectedLetter === letter ? "default" : "ghost"}
                size="sm"
                onClick={() => setSelectedLetter(letter)}
                className="w-10 h-10 p-0"
              >
                {letter}
              </Button>
            ))}
          </div>
        </div>
      </section>

      {/* Terms List */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            {filteredTerms.length === 0 ? (
              <Card className="p-12 text-center">
                <AlertCircle className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                <p className="text-muted-foreground">No terms found matching your search.</p>
              </Card>
            ) : (
              <div className="space-y-6">
                {filteredTerms.sort((a, b) => a.term.localeCompare(b.term)).map((item, index) => (
                  <Card key={index} id={item.term.toLowerCase().replace(/\s+/g, '-')}>
                    <CardHeader>
                      <div className="flex items-start justify-between gap-4">
                        <div className="flex-1">
                          <CardTitle className="text-2xl mb-2">{item.term}</CardTitle>
                          <Badge className={getCategoryColor(item.category)}>
                            {item.category}
                          </Badge>
                        </div>
                      </div>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <p className="text-lg leading-relaxed">{item.definition}</p>
                      
                      {item.relatedTerms && item.relatedTerms.length > 0 && (
                        <div>
                          <h4 className="text-sm font-semibold mb-2 text-muted-foreground">Related Terms:</h4>
                          <div className="flex flex-wrap gap-2">
                            {item.relatedTerms.map((related, idx) => (
                              <Badge key={idx} variant="outline" className="cursor-pointer hover:bg-muted">
                                {related}
                              </Badge>
                            ))}
                          </div>
                        </div>
                      )}
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>
        </div>
      </section>

      {/* Categories Overview */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8 text-center">Browse by Category</h2>
            
            <div className="grid md:grid-cols-2 gap-6">
              {categories.filter(c => c !== "All").map((category, index) => (
                <Card key={index} className="hover:shadow-lg transition-shadow cursor-pointer">
                  <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                      {category}
                      <Badge variant="secondary">
                        {terms.filter(t => t.category === category).length} terms
                      </Badge>
                    </CardTitle>
                    <CardDescription>
                      {category === "Compliance" && "Regulatory requirements and standards"}
                      {category === "Security" && "Security technologies and protocols"}
                      {category === "Technical" && "Technical specifications and metrics"}
                      {category === "Verification" && "Verification methods and processes"}
                    </CardDescription>
                  </CardHeader>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16">
        <div className="container mx-auto">
          <Card className="max-w-3xl mx-auto bg-gradient-to-br from-primary to-primary/80 text-primary-foreground">
            <CardContent className="p-12 text-center">
              <h2 className="text-3xl font-bold mb-4">Need More Information?</h2>
              <p className="text-lg mb-8 text-primary-foreground/90">
                Explore our complete documentation or contact our team for expert guidance
              </p>
              <div className="flex flex-wrap gap-4 justify-center">
                <Button size="lg" variant="secondary" asChild>
                  <Link to="/developers/api-reference">
                    View Documentation
                  </Link>
                </Button>
                <Button 
                  size="lg" 
                  variant="outline"
                  className="bg-transparent border-primary-foreground text-primary-foreground hover:bg-primary-foreground/10"
                  asChild
                >
                  <Link to="/contact">
                    Contact Us
                  </Link>
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Glossary;
