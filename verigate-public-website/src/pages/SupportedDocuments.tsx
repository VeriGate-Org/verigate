import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  FileText,
  Globe,
  FileCheck,
  FileSearch,
  BadgeCheck,
  ArrowRight,
  MapPin,
  GraduationCap,
  Home,
  Fingerprint,
} from "lucide-react";
import { Link } from "react-router-dom";

const bulletIcons = [FileCheck, FileSearch, BadgeCheck];

const SupportedDocuments = () => {
  const saDocuments = [
    {
      name: "Smart ID Card",
      description: "New-format South African identity card with chip and biometric data",
      verification: "Verified against DHA National Population Register",
    },
    {
      name: "Green ID Book",
      description: "Legacy South African identity document with barcode",
      verification: "Barcode validation and DHA database cross-reference",
    },
    {
      name: "South African Passport",
      description: "Current machine-readable SA passport with biometric page",
      verification: "MRZ reading and DHA validation",
    },
    {
      name: "Driver's Licence",
      description: "Credit card format SA driver's licence with photo and codes",
      verification: "Licence code verification and eNaTIS cross-reference",
    },
    {
      name: "Work Permit / Work Visa",
      description: "General Work Visa, Critical Skills Visa, and Intra-Company Transfer",
      verification: "DHA visa status verification and validity check",
    },
    {
      name: "Asylum Seeker Permit",
      description: "Section 22 asylum seeker permit and Section 24 refugee status",
      verification: "Permit status verification through DHA refugee systems",
    },
  ];

  const africanDocuments = [
    {
      country: "Nigeria",
      documents: [
        "National Identification Number (NIN) Slip",
        "Nigerian International Passport",
        "Voter's Card (PVC)",
        "Driver's Licence",
      ],
    },
    {
      country: "Kenya",
      documents: [
        "Kenyan National ID (Huduma Namba)",
        "Kenyan Passport",
        "KRA PIN Certificate",
        "Driver's Licence",
      ],
    },
    {
      country: "Zimbabwe",
      documents: [
        "Zimbabwean Passport",
        "National ID (Metal/Plastic)",
        "Birth Certificate",
        "Driver's Licence",
      ],
    },
    {
      country: "Ghana",
      documents: [
        "Ghana Card (National ID)",
        "Ghanaian Passport",
        "Voter's ID",
        "SSNIT Card",
      ],
    },
    {
      country: "Botswana",
      documents: [
        "Omang (National ID)",
        "Botswana Passport",
        "Driver's Licence",
        "Work Permit",
      ],
    },
    {
      country: "Mozambique",
      documents: [
        "Bilhete de Identidade (National ID)",
        "Mozambican Passport",
        "DIRE (Residence Permit)",
        "Driver's Licence",
      ],
    },
  ];

  const internationalDocuments = [
    {
      category: "Passports",
      description: "Machine-readable passports from 100+ countries",
      details: [
        "Machine-Readable Zone (MRZ) extraction and validation",
        "Biometric page data cross-referencing",
        "Visa stamps and endorsement tracking",
        "Expired passport detection and flagging",
      ],
    },
    {
      category: "National IDs",
      description: "Government-issued identity cards from major countries",
      details: [
        "European national ID cards (EU/EEA member states)",
        "UK Biometric Residence Permits",
        "US state-issued IDs and Real ID",
        "Indian Aadhaar and PAN cards",
      ],
    },
  ];

  const proofOfAddress = [
    {
      type: "Utility Bills",
      items: [
        "Electricity bills (Eskom, City Power, municipal)",
        "Water and sanitation accounts",
        "Gas bills (including prepaid statements)",
        "Landline telephone bills",
      ],
    },
    {
      type: "Bank Statements",
      items: [
        "Major SA banks (ABSA, FNB, Nedbank, Standard Bank, Capitec)",
        "Digital bank statements (TymeBank, Discovery Bank, Bank Zero)",
        "Credit card statements",
        "Investment account statements",
      ],
    },
    {
      type: "Municipal Accounts",
      items: [
        "Rates and taxes statements",
        "Municipal combined service accounts",
        "Levy statements (sectional title/HOA)",
        "Property valuation notices",
      ],
    },
    {
      type: "Lease Agreements",
      items: [
        "Residential lease agreements",
        "Commercial lease agreements",
        "Rental agent correspondence",
        "Proof of accommodation letters",
      ],
    },
  ];

  const professionalDocuments = [
    {
      category: "SAQA Certificates",
      icon: GraduationCap,
      items: [
        "National Senior Certificate (Matric)",
        "Higher Certificate / Diploma",
        "Bachelor's, Honours, Master's, Doctoral degrees",
        "Foreign qualification evaluations",
      ],
    },
    {
      category: "University Degrees",
      icon: GraduationCap,
      items: [
        "All 26 public SA universities",
        "Private higher education institutions",
        "International university verifications",
        "Academic transcript verification",
      ],
    },
    {
      category: "Professional Registrations",
      icon: FileText,
      items: [
        "HPCSA (Health Professions Council)",
        "SAICA (Chartered Accountants)",
        "ECSA (Engineering Council)",
        "SACAP (Architectural Profession)",
      ],
    },
    {
      category: "Trade & Technical",
      icon: FileText,
      items: [
        "SETA certificates and learnerships",
        "Artisan trade test certificates",
        "QCTO qualifications",
        "Short course certificates from accredited providers",
      ],
    },
  ];

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-50" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <div className="max-w-4xl space-y-6">
            <Badge variant="secondary" className="mb-4">
              <FileText className="w-3 h-3 mr-1" />
              Document Verification
            </Badge>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
              Supported Document
              <span className="block text-accent mt-2">Types</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl">
              VeriGate verifies a comprehensive range of South African, African
              regional, and international document types. From Smart ID cards to
              professional qualifications.
            </p>
          </div>
        </div>
      </section>

      {/* SA Documents */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-lg bg-accent/10 flex items-center justify-center">
                <MapPin className="w-5 h-5 text-accent" />
              </div>
              <h2 className="text-3xl md:text-4xl font-bold text-foreground">
                South African Documents
              </h2>
            </div>
            <p className="text-lg text-muted-foreground">
              Primary identity documents verified against DHA and government databases
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {saDocuments.map((doc, index) => (
              <Card key={index} className="border-border hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-center gap-2 mb-2">
                    <Fingerprint className="w-5 h-5 text-accent" />
                    <CardTitle className="text-lg">{doc.name}</CardTitle>
                  </div>
                  <CardDescription>{doc.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="flex items-start gap-2 text-sm text-muted-foreground bg-accent/5 p-3 rounded-lg">
                    <FileCheck className="w-4 h-4 text-accent flex-shrink-0 mt-0.5" />
                    <span>{doc.verification}</span>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* African Regional Documents */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-lg bg-accent/10 flex items-center justify-center">
                <Globe className="w-5 h-5 text-accent" />
              </div>
              <h2 className="text-3xl md:text-4xl font-bold text-foreground">
                African Regional Documents
              </h2>
            </div>
            <p className="text-lg text-muted-foreground">
              Identity documents from key African markets for cross-border verification
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {africanDocuments.map((country, index) => (
              <Card key={index} className="border-border">
                <CardHeader>
                  <CardTitle className="text-lg">{country.country}</CardTitle>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {country.documents.map((doc, idx) => {
                      const BulletIcon = bulletIcons[idx % bulletIcons.length];
                      return (
                        <li key={idx} className="flex items-start gap-2 text-sm">
                          <BulletIcon className="w-4 h-4 text-accent flex-shrink-0 mt-0.5" />
                          <span>{doc}</span>
                        </li>
                      );
                    })}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* International Documents */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-lg bg-accent/10 flex items-center justify-center">
                <Globe className="w-5 h-5 text-accent" />
              </div>
              <h2 className="text-3xl md:text-4xl font-bold text-foreground">
                International Documents
              </h2>
            </div>
            <p className="text-lg text-muted-foreground">
              Passports and national IDs from 100+ countries worldwide
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            {internationalDocuments.map((category, index) => (
              <Card key={index} className="border-border">
                <CardHeader>
                  <CardTitle className="text-xl">{category.category}</CardTitle>
                  <CardDescription>{category.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-3">
                    {category.details.map((detail, idx) => {
                      const BulletIcon = bulletIcons[idx % bulletIcons.length];
                      return (
                        <li key={idx} className="flex items-start gap-2 text-sm">
                          <BulletIcon className="w-4 h-4 text-accent flex-shrink-0 mt-0.5" />
                          <span>{detail}</span>
                        </li>
                      );
                    })}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Proof of Address */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-lg bg-accent/10 flex items-center justify-center">
                <Home className="w-5 h-5 text-accent" />
              </div>
              <h2 className="text-3xl md:text-4xl font-bold text-foreground">
                Proof of Address
              </h2>
            </div>
            <p className="text-lg text-muted-foreground">
              Accepted proof-of-address documents for FICA compliance and address verification
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {proofOfAddress.map((category, index) => (
              <Card key={index} className="border-border">
                <CardHeader>
                  <CardTitle className="text-base">{category.type}</CardTitle>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {category.items.map((item, idx) => {
                      const BulletIcon = bulletIcons[idx % bulletIcons.length];
                      return (
                        <li key={idx} className="flex items-start gap-2 text-sm">
                          <BulletIcon className="w-3 h-3 text-accent flex-shrink-0 mt-1" />
                          <span className="text-muted-foreground">{item}</span>
                        </li>
                      );
                    })}
                  </ul>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Professional Documents */}
      <section className="py-20">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-lg bg-accent/10 flex items-center justify-center">
                <GraduationCap className="w-5 h-5 text-accent" />
              </div>
              <h2 className="text-3xl md:text-4xl font-bold text-foreground">
                Professional & Educational Documents
              </h2>
            </div>
            <p className="text-lg text-muted-foreground">
              Qualification certificates, professional registrations, and educational credentials
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {professionalDocuments.map((category, index) => {
              const Icon = category.icon;
              return (
                <Card key={index} className="border-border">
                  <CardHeader>
                    <div className="flex items-center gap-2 mb-2">
                      <Icon className="w-5 h-5 text-accent" />
                      <CardTitle className="text-base">{category.category}</CardTitle>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <ul className="space-y-2">
                      {category.items.map((item, idx) => {
                        const BulletIcon = bulletIcons[idx % bulletIcons.length];
                        return (
                          <li key={idx} className="flex items-start gap-2 text-sm">
                            <BulletIcon className="w-3 h-3 text-accent flex-shrink-0 mt-1" />
                            <span className="text-muted-foreground">{item}</span>
                          </li>
                        );
                      })}
                    </ul>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-secondary/30">
        <div className="container mx-auto max-w-6xl">
          <Card className="border-accent bg-gradient-to-br from-primary/5 to-accent/5">
            <CardHeader className="text-center pb-6">
              <CardTitle className="text-3xl md:text-4xl mb-4">
                Don't See Your Document Type?
              </CardTitle>
              <CardDescription className="text-lg">
                We're constantly expanding our supported document list. Contact us to
                discuss your specific verification requirements.
              </CardDescription>
            </CardHeader>
            <CardContent className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" className="min-w-[200px]" asChild>
                <Link to="/contact">
                  Contact Us
                  <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" className="min-w-[200px]" asChild>
                <Link to="/request-demo">Request a Demo</Link>
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  );
};

export default SupportedDocuments;
