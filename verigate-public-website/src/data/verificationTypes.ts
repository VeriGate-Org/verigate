// Verification Types data — 16 SA-focused verification products
// Consumed by /verification-types (hub) and /verification-types/:slug (detail)
import { VerificationTypeData } from "@/components/templates/VerificationTypeTemplate";

export const verificationTypes: VerificationTypeData[] = [
  // ──────────────────────────────────────────────
  // 1. Criminal Record Checks
  // ──────────────────────────────────────────────
  {
    slug: "criminal-record-checks",
    title: "Criminal Record Checks",
    subtitle: "SAPS Criminal Clearance & Police Clearance Certificates",
    description:
      "Run comprehensive criminal background checks through the South African Police Service (SAPS) and the Department of Justice. VeriGate automates the full clearance process, including fingerprint-based checks, pending case searches, and court-record verification under the Criminal Procedure Act.",
    icon: "Shield",
    turnaround: "3-5 days",
    howItWorks: [
      {
        step: "01",
        title: "Submit Candidate Details",
        description:
          "Upload the candidate's ID number, full name, and consent form via the VeriGate dashboard or API.",
      },
      {
        step: "02",
        title: "SAPS & DOJ Query",
        description:
          "VeriGate submits fingerprint-matched requests to the SAPS Criminal Record Centre and cross-references Department of Justice court records.",
      },
      {
        step: "03",
        title: "Pending Case & Court Roll Scan",
        description:
          "Outstanding warrants, pending criminal cases, and magistrate/high court records are scanned for matches under the Criminal Procedure Act.",
      },
      {
        step: "04",
        title: "Clearance Certificate Delivered",
        description:
          "A verified police clearance report is generated with case details (if any), risk rating, and a downloadable PDF clearance certificate.",
      },
    ],
    features: [
      {
        title: "SAPS Integration",
        description:
          "Direct integration with the SAPS Criminal Record Centre for fingerprint-based lookups and automated status tracking.",
        items: [
          "Direct integration with the SAPS Criminal Record Centre for fingerprint-based lookups",
          "Automated submission and status tracking of police clearance requests",
          "Support for both electronic and manual fingerprint submissions",
          "Real-time notification when clearance results are available",
        ],
      },
      {
        title: "Court Record Search",
        description:
          "Cross-reference with Department of Justice databases to uncover pending cases and historical convictions.",
        items: [
          "Cross-reference with Department of Justice court roll databases",
          "Magistrate and High Court record verification across all nine provinces",
          "Pending case and outstanding warrant detection",
          "Historical conviction lookup with sentencing details",
        ],
      },
      {
        title: "Compliance & Reporting",
        description:
          "POPIA-compliant processes with audit-ready clearance certificates and configurable risk thresholds.",
        items: [
          "POPIA-compliant consent capture and data retention policies",
          "Audit-ready PDF clearance certificates with tamper-proof digital signatures",
          "Criminal Procedure Act Section 271 compliant processes",
          "Configurable risk thresholds aligned with your internal hiring policies",
        ],
      },
    ],
    useCases: [
      {
        title: "Pre-Employment Screening",
        description:
          "Screen new hires across all industries before making employment offers, reducing workplace risk and protecting company reputation.",
      },
      {
        title: "Government Tender Compliance",
        description:
          "Contractor and vendor onboarding for government tenders requiring SAPS clearance as a mandatory bid requirement.",
      },
      {
        title: "Childcare & Education",
        description:
          "Children's Act compliance requiring criminal clearance for anyone working with children in schools, creches, and care facilities.",
      },
      {
        title: "Financial Services Vetting",
        description:
          "Employee screening mandated by FICA and the Banks Act for staff in positions of trust within financial institutions.",
      },
    ],
    relatedTypes: [
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Confirm candidate identity via DHA before running criminal checks.",
      },
      {
        slug: "qualification-checks",
        title: "Qualification Checks",
        description: "Verify academic qualifications alongside criminal history for a complete profile.",
      },
      {
        slug: "employment-history",
        title: "Employment History",
        description: "Cross-reference employment claims with criminal background findings.",
      },
    ],
    ctaTitle: "Start Running Criminal Checks Today",
  },

  // ──────────────────────────────────────────────
  // 2. Identity Verification
  // ──────────────────────────────────────────────
  {
    slug: "identity-verification",
    title: "Identity Verification",
    subtitle: "DHA-Connected ID Verification for Smart IDs & Passports",
    description:
      "Verify South African identities in real time against the Department of Home Affairs (DHA) population register. VeriGate validates ID numbers, matches biographical data, confirms citizenship status, and detects deceased or fraudulent identities, supporting Smart ID cards, green bar-coded IDs, and SA passports.",
    icon: "UserCheck",
    turnaround: "Instant-4hrs",
    howItWorks: [
      {
        step: "01",
        title: "Capture ID Number",
        description:
          "The candidate provides their 13-digit SA ID number or passport number via your app, portal, or API integration.",
      },
      {
        step: "02",
        title: "DHA Population Register Lookup",
        description:
          "VeriGate queries the DHA population register to confirm identity, citizenship status, and demographic details in real time.",
      },
      {
        step: "03",
        title: "Deceased & Fraud Checks",
        description:
          "The ID is cross-referenced against the DHA deceased persons register and known fraudulent identity databases.",
      },
      {
        step: "04",
        title: "Verified Identity Report",
        description:
          "A comprehensive verification report is returned with match confidence scores, photo (where available), and risk indicators.",
      },
    ],
    features: [
      {
        title: "DHA Direct Integration",
        description:
          "Real-time queries against the DHA National Population Register with support for all SA identity document types.",
        items: [
          "Real-time queries against the DHA National Population Register (NPR)",
          "Smart ID card, green bar-coded ID book, and SA passport validation",
          "Citizenship and permanent residency status confirmation",
          "Marital status and demographic data extraction where consented",
        ],
      },
      {
        title: "Fraud Detection",
        description:
          "Detect deceased identities, duplicate IDs, and fraudulent identity numbers through multiple cross-checks.",
        items: [
          "Deceased person identity flag via DHA death register cross-check",
          "Duplicate and cloned ID number detection",
          "Identity number format and checksum validation (Luhn algorithm)",
          "Known fraudulent identity watchlist screening",
        ],
      },
      {
        title: "Flexible Integration",
        description:
          "RESTful API, batch processing, and embeddable SDKs for seamless integration into any workflow.",
        items: [
          "RESTful API with sub-second response times for real-time flows",
          "Batch upload for processing thousands of verifications simultaneously",
          "White-label web SDK for embedded identity capture in your UI",
          "Webhook notifications for asynchronous verification completion",
        ],
      },
    ],
    useCases: [
      {
        title: "FICA Customer Identification",
        description:
          "Mandatory identity verification for bank account opening and financial services onboarding under the Financial Intelligence Centre Act.",
      },
      {
        title: "Healthcare Patient Verification",
        description:
          "Confirm patient identities at hospitals and clinics to prevent medical identity fraud and ensure correct record matching.",
      },
      {
        title: "Tenant Screening",
        description:
          "Property managers verify prospective tenant identities before signing lease agreements to reduce rental fraud.",
      },
      {
        title: "Age-Restricted Sales",
        description:
          "Retailers and e-commerce platforms confirm customer age for alcohol, tobacco, and other regulated product sales.",
      },
    ],
    relatedTypes: [
      {
        slug: "eidv",
        title: "Electronic Identity Verification",
        description: "Frictionless eIDV without document uploads for low-risk onboarding.",
      },
      {
        slug: "face-verification",
        title: "Face Verification",
        description: "Add biometric selfie matching to confirm the person behind the ID.",
      },
      {
        slug: "document-verification",
        title: "Document Verification",
        description: "Validate the physical or digital document alongside the identity data.",
      },
    ],
    ctaTitle: "Verify Identities in Seconds",
  },

  // ──────────────────────────────────────────────
  // 3. Qualification Checks
  // ──────────────────────────────────────────────
  {
    slug: "qualification-checks",
    title: "Qualification Checks",
    subtitle: "SAQA, HPCSA & University Verification with NQF Level Confirmation",
    description:
      "Validate academic qualifications, professional registrations, and NQF levels through SAQA (South African Qualifications Authority), HPCSA, ECSA, and direct university verification channels. VeriGate detects fraudulent qualifications, confirms degree completion, and verifies professional board registrations for regulated industries.",
    icon: "GraduationCap",
    turnaround: "2-5 days",
    howItWorks: [
      {
        step: "01",
        title: "Submit Qualification Details",
        description:
          "Provide the candidate's full name, ID number, institution name, qualification title, and year of completion.",
      },
      {
        step: "02",
        title: "SAQA & Institution Verification",
        description:
          "VeriGate queries the SAQA National Learners' Records Database (NLRD) and contacts the institution directly to confirm enrolment and graduation.",
      },
      {
        step: "03",
        title: "Professional Board Check",
        description:
          "For regulated professions, registrations with HPCSA, ECSA, SACPLAN, SANC, and other statutory bodies are verified.",
      },
      {
        step: "04",
        title: "Verified Qualification Report",
        description:
          "A detailed report confirms the qualification, NQF level, accreditation status, and flags any discrepancies or fraudulent claims.",
      },
    ],
    features: [
      {
        title: "SAQA Integration",
        description:
          "Direct queries against the NLRD for NQF-registered qualifications, SETA accreditations, and foreign qualification evaluations.",
        items: [
          "Direct NLRD queries for NQF-registered qualifications and unit standards",
          "NQF level confirmation and SAQA qualification ID verification",
          "Accreditation status checks for SETA and CHE-accredited programmes",
          "Foreign qualification evaluation status via SAQA's international comparisons",
        ],
      },
      {
        title: "Institution Verification",
        description:
          "Direct verification with all 26 public universities, TVET colleges, and registered private institutions.",
        items: [
          "Direct verification with all 26 South African public universities",
          "Private higher education institution (PHEI) registration confirmation with DHET",
          "TVET college qualification and N-diploma verification",
          "Matric certificate validation through the Department of Basic Education",
        ],
      },
      {
        title: "Professional Registration",
        description:
          "Statutory body verification for healthcare, engineering, accounting, and other regulated professions.",
        items: [
          "HPCSA registration verification for doctors, dentists, and allied health professionals",
          "SANC verification for registered nurses and enrolled nursing assistants",
          "ECSA registration for professional engineers and technologists",
          "SAICA, SAIPA, and IRBA checks for accounting professionals",
        ],
      },
    ],
    useCases: [
      {
        title: "Pre-Employment Screening",
        description:
          "Detect fraudulent degrees and certificates before making hiring decisions, protecting your organisation from CV fraud.",
      },
      {
        title: "Healthcare Compliance",
        description:
          "Confirm HPCSA and SANC registration for medical professionals as required by the National Health Act and facility licensing.",
      },
      {
        title: "Engineering Due Diligence",
        description:
          "Verify ECSA professional registration for engineers and technologists on infrastructure and construction projects.",
      },
      {
        title: "Government Vetting",
        description:
          "Confirm minimum NQF level requirements for public service posts as mandated by the Department of Public Service and Administration.",
      },
    ],
    relatedTypes: [
      {
        slug: "employment-history",
        title: "Employment History",
        description: "Pair qualification checks with employment verification for a complete candidate profile.",
      },
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Confirm the candidate's identity before verifying their claimed qualifications.",
      },
      {
        slug: "document-verification",
        title: "Document Verification",
        description: "Authenticate the physical certificate or transcript document for tampering.",
      },
    ],
    ctaTitle: "Verify Qualifications with Confidence",
  },

  // ──────────────────────────────────────────────
  // 4. Employment History
  // ──────────────────────────────────────────────
  {
    slug: "employment-history",
    title: "Employment History Verification",
    subtitle: "Previous Employer Checks for Dates, Titles & Reason for Leaving",
    description:
      "Confirm candidates' employment history by contacting previous employers directly. VeriGate verifies job titles, employment dates, responsibilities, reason for leaving, and rehire eligibility, helping South African businesses make informed hiring decisions and reduce the risk of CV fraud.",
    icon: "Briefcase",
    turnaround: "2-3 days",
    howItWorks: [
      {
        step: "01",
        title: "Capture Employment Details",
        description:
          "The candidate provides their employment history including company names, job titles, employment dates, and HR contact details.",
      },
      {
        step: "02",
        title: "Employer Contact & Verification",
        description:
          "VeriGate's verification team contacts each listed employer's HR department to confirm the details provided by the candidate.",
      },
      {
        step: "03",
        title: "Discrepancy Analysis",
        description:
          "Any mismatches between claimed and verified information are flagged, including title inflation, date discrepancies, and fabricated employment.",
      },
      {
        step: "04",
        title: "Verified Employment Report",
        description:
          "A detailed report is delivered with confirmed positions, discrepancy flags, reason for leaving, and rehire eligibility status.",
      },
    ],
    features: [
      {
        title: "Comprehensive Verification",
        description:
          "Direct contact with HR departments to confirm every aspect of the claimed employment relationship.",
        items: [
          "Direct HR department contact for each listed employer",
          "Job title, department, and seniority level confirmation",
          "Exact employment start and end date verification",
          "Reason for leaving and rehire eligibility checks",
        ],
      },
      {
        title: "Discrepancy Detection",
        description:
          "AI-powered analysis identifies title inflation, fabricated employers, and employment timeline inconsistencies.",
        items: [
          "Title inflation and role exaggeration flagging",
          "Employment gap analysis and timeline inconsistency detection",
          "Fabricated employer identification through CIPC company registration checks",
          "Cross-referencing with UIF records for employment date confirmation",
        ],
      },
      {
        title: "South African Coverage",
        description:
          "Broad verification coverage across private sector, public service, SOEs, and SMMEs nationwide.",
        items: [
          "Verification coverage across all nine SA provinces and major employers",
          "Public sector verification including national and provincial government departments",
          "SOE (State-Owned Enterprise) employment confirmation processes",
          "SMME and informal sector verification through alternative referencing methods",
        ],
      },
    ],
    useCases: [
      {
        title: "Executive Appointments",
        description:
          "Senior executive and C-suite appointment due diligence requiring thorough employment history verification.",
      },
      {
        title: "Financial Services Vetting",
        description:
          "Employee screening under FICA and FAIS requirements for positions handling client funds and financial advice.",
      },
      {
        title: "Security Clearance",
        description:
          "Government security clearance processes requiring full and verified employment history going back 10+ years.",
      },
      {
        title: "Recruitment QA",
        description:
          "Recruitment agency quality assurance ensuring placed candidates' CVs accurately reflect their employment history.",
      },
    ],
    relatedTypes: [
      {
        slug: "qualification-checks",
        title: "Qualification Checks",
        description: "Verify qualifications alongside employment history for a complete candidate profile.",
      },
      {
        slug: "criminal-record-checks",
        title: "Criminal Record Checks",
        description: "Add criminal screening to employment verification for high-trust roles.",
      },
      {
        slug: "credit-screening",
        title: "Credit Screening",
        description: "Assess financial standing alongside employment history for fiduciary positions.",
      },
    ],
    ctaTitle: "Verify Employment History Accurately",
  },

  // ──────────────────────────────────────────────
  // 5. Credit Screening
  // ──────────────────────────────────────────────
  {
    slug: "credit-screening",
    title: "Credit Screening",
    subtitle: "TransUnion SA, Experian SA & XDS Bureau Checks with NCA Compliance",
    description:
      "Access credit bureau data from TransUnion SA, Experian SA, and XDS to assess candidates' financial standing. VeriGate delivers credit scores, judgments, defaults, and insolvency records while ensuring full compliance with the National Credit Act (NCA) and POPIA regulations.",
    icon: "CreditCard",
    turnaround: "Instant-4hrs",
    howItWorks: [
      {
        step: "01",
        title: "Obtain Candidate Consent",
        description:
          "Capture POPIA and NCA-compliant consent from the candidate before initiating any credit bureau enquiry.",
      },
      {
        step: "02",
        title: "Multi-Bureau Query",
        description:
          "VeriGate queries TransUnion SA, Experian SA, and XDS simultaneously to compile a comprehensive credit profile.",
      },
      {
        step: "03",
        title: "Risk Assessment & Scoring",
        description:
          "Credit scores, payment profiles, judgments, defaults, and insolvency records are aggregated into a single risk assessment.",
      },
      {
        step: "04",
        title: "Credit Screening Report",
        description:
          "A detailed credit screening report is delivered with risk scores, adverse information, and NCA-compliant summaries.",
      },
    ],
    features: [
      {
        title: "Multi-Bureau Coverage",
        description:
          "Consolidated credit view from all three major SA credit bureaux for comprehensive financial assessment.",
        items: [
          "TransUnion SA (formerly ITC) credit score and payment profile",
          "Experian SA consumer credit report and Delphi score",
          "XDS credit bureau check for comprehensive coverage",
          "Consolidated multi-bureau view with de-duplicated records",
        ],
      },
      {
        title: "Adverse Information Detection",
        description:
          "Identify judgments, debt review, sequestration, and default listings from court and bureau records.",
        items: [
          "Civil court judgments and administration orders",
          "Debt review status under the NCA Section 86 process",
          "Sequestration and insolvency record checks",
          "Default listings, handed-over accounts, and write-offs",
        ],
      },
      {
        title: "Regulatory Compliance",
        description:
          "Full NCA and POPIA compliance with consent management and NCR registration requirements.",
        items: [
          "Full NCA (National Credit Act No. 34 of 2005) compliance",
          "POPIA-compliant consent management and data handling",
          "NCR (National Credit Regulator) registration requirements met",
          "Audit trail for every credit enquiry with consent evidence",
        ],
      },
    ],
    useCases: [
      {
        title: "Financial Services Screening",
        description:
          "Screen employees in positions handling client funds or financial products for creditworthiness and financial risk.",
      },
      {
        title: "Executive Due Diligence",
        description:
          "Senior management and executive appointment financial due diligence for board and fiduciary positions.",
      },
      {
        title: "Tenant Affordability",
        description:
          "Property management companies assessing tenant affordability and payment history before signing lease agreements.",
      },
      {
        title: "Insurance Vetting",
        description:
          "Insurance industry employee screening for positions with fiduciary responsibilities and claims authority.",
      },
    ],
    relatedTypes: [
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Confirm identity before running credit bureau checks for accurate matching.",
      },
      {
        slug: "employment-history",
        title: "Employment History",
        description: "Combine credit checks with employment verification for a complete financial picture.",
      },
      {
        slug: "business-verification",
        title: "Business Verification",
        description: "Add company-level credit checks when screening business partners and vendors.",
      },
    ],
    ctaTitle: "Run Credit Checks in Minutes",
  },

  // ──────────────────────────────────────────────
  // 6. Face Verification
  // ──────────────────────────────────────────────
  {
    slug: "face-verification",
    title: "Face Verification",
    subtitle: "Biometric Facial Recognition with Liveness Detection",
    description:
      "Match a live selfie to an identity document photo using AI-powered facial recognition with 99.8% accuracy. VeriGate's liveness detection prevents spoofing via printed photos, screen replays, and 3D masks, ensuring the person presenting the ID is physically present and genuine.",
    icon: "ScanFace",
    turnaround: "Instant",
    howItWorks: [
      {
        step: "01",
        title: "Capture Selfie",
        description:
          "The user takes a live selfie through the VeriGate web SDK or mobile SDK with guided framing and lighting prompts.",
      },
      {
        step: "02",
        title: "Liveness Detection",
        description:
          "Passive and active liveness checks confirm the user is a real, physically present person, not a photo, video, or 3D mask.",
      },
      {
        step: "03",
        title: "Biometric Face Match",
        description:
          "The selfie is compared to the photo on the submitted ID document using deep-learning facial recognition with 99.8% accuracy.",
      },
      {
        step: "04",
        title: "Match Result Delivered",
        description:
          "A confidence score and match/no-match result is returned instantly, with detailed liveness and face-match analytics.",
      },
    ],
    features: [
      {
        title: "AI Facial Recognition",
        description:
          "Deep-learning face matching engine with 99.8% accuracy, built to perform equally across all demographics.",
        items: [
          "Deep-learning face matching engine with 99.8% accuracy across all demographics",
          "Age, lighting, and accessory tolerance for real-world selfie conditions",
          "Support for matching against Smart ID, passport, and driver's licence photos",
          "ISO 19795 and ISO 30107-3 compliant biometric matching algorithms",
        ],
      },
      {
        title: "Liveness Detection",
        description:
          "Multi-layered spoof prevention combining passive analysis and active challenges to defeat all known attack vectors.",
        items: [
          "Passive liveness analysis requiring no user interaction for seamless UX",
          "Active liveness challenges (blink, turn, smile) for high-security flows",
          "Printed photo, screen replay, and video injection attack prevention",
          "3D mask and deepfake detection using depth estimation and texture analysis",
        ],
      },
      {
        title: "Developer-Friendly SDK",
        description:
          "Web, iOS, and Android SDKs with guided capture, auto-framing, and customisable branding.",
        items: [
          "Web SDK with guided selfie capture and auto-framing",
          "iOS and Android native SDKs with camera optimisation",
          "Customisable UI themes to match your brand identity",
          "On-device processing option for low-latency, privacy-first deployments",
        ],
      },
    ],
    useCases: [
      {
        title: "Digital Onboarding",
        description:
          "Selfie-to-ID matching during digital account opening for banks, fintechs, and insurance providers.",
      },
      {
        title: "Step-Up Authentication",
        description:
          "Biometric verification for high-value transactions, password resets, and account recovery in financial services.",
      },
      {
        title: "Remote Workforce",
        description:
          "Confirm the identity of remote employees and contractors during onboarding and periodic re-verification.",
      },
      {
        title: "Access Control",
        description:
          "Event and venue access control using facial recognition for secure entry at corporate offices and events.",
      },
    ],
    relatedTypes: [
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Combine face matching with DHA identity verification for a complete KYC flow.",
      },
      {
        slug: "document-verification",
        title: "Document Verification",
        description: "Verify the document first, then match the selfie to the document photo.",
      },
      {
        slug: "video-identification",
        title: "Video Identification",
        description: "Escalate to live video verification when automated face matching is inconclusive.",
      },
    ],
    ctaTitle: "Add Face Verification to Your App",
  },

  // ──────────────────────────────────────────────
  // 7. Document Verification
  // ──────────────────────────────────────────────
  {
    slug: "document-verification",
    title: "Document Verification",
    subtitle: "AI-Powered ID, Driver's Licence & Work Permit Authentication",
    description:
      "Verify the authenticity of South African identity documents, driver's licences, work permits, and supporting documents using AI-powered OCR extraction and fraud detection. VeriGate analyses security features, detects tampering, and extracts structured data from over 6 500 document types across 200+ countries.",
    icon: "FileCheck",
    turnaround: "Instant-1hr",
    howItWorks: [
      {
        step: "01",
        title: "Upload Document",
        description:
          "The user photographs or uploads their document via the VeriGate web/mobile SDK, API, or dashboard.",
      },
      {
        step: "02",
        title: "AI-Powered OCR Extraction",
        description:
          "Machine learning models extract text, MRZ codes, barcodes, and biographical data from the document image.",
      },
      {
        step: "03",
        title: "Authenticity & Tampering Analysis",
        description:
          "Security features (holograms, microprint, UV patterns) are validated and tampering indicators are analysed using computer vision.",
      },
      {
        step: "04",
        title: "Verified Document Report",
        description:
          "A comprehensive report with extracted data, authenticity score, tampering flags, and document classification is delivered.",
      },
    ],
    features: [
      {
        title: "OCR & Data Extraction",
        description:
          "AI-powered text and barcode extraction with 99.5% character accuracy across all SA document types.",
        items: [
          "AI-powered text extraction with 99.5% character accuracy",
          "MRZ (Machine Readable Zone) parsing for passports and travel documents",
          "South African ID barcode decoding for Smart IDs and green ID books",
          "Structured JSON output of all extracted fields for API consumption",
        ],
      },
      {
        title: "Fraud Detection",
        description:
          "Pixel-level forensic analysis detects digital tampering, font inconsistencies, and duplicate submissions.",
        items: [
          "Digital tampering detection using pixel-level forensic analysis",
          "Font consistency and alignment verification across document fields",
          "Security feature validation including holograms and watermarks",
          "Duplicate document detection across your entire verification history",
        ],
      },
      {
        title: "Broad Document Coverage",
        description:
          "Support for SA identity documents, driver's licences, work permits, and supporting financial documents.",
        items: [
          "South African Smart IDs, green ID books, and temporary IDs",
          "SA driver's licences (card and paper format) and learner's permits",
          "Work permits, asylum seeker permits, and refugee status documents",
          "Proof of residence, bank statements, and utility bills for address verification",
        ],
      },
    ],
    useCases: [
      {
        title: "KYC Document Capture",
        description:
          "Automate document capture and validation during customer onboarding, reducing manual data entry and errors.",
      },
      {
        title: "Right-to-Work Verification",
        description:
          "HR departments verify work permits and right-to-work documentation for foreign nationals employed in South Africa.",
      },
      {
        title: "Insurance Claims",
        description:
          "Authenticate claim-supporting documents to prevent fraudulent insurance submissions and reduce claim leakage.",
      },
      {
        title: "Government Services",
        description:
          "Verify identity documentation for government service delivery, grant applications, and public facility access.",
      },
    ],
    relatedTypes: [
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Cross-check extracted document data against the DHA population register.",
      },
      {
        slug: "face-verification",
        title: "Face Verification",
        description: "Match a live selfie to the document photo for biometric confirmation.",
      },
      {
        slug: "nfc-verification",
        title: "NFC Verification",
        description: "Read the NFC chip on Smart IDs for chip-level document authenticity.",
      },
    ],
    ctaTitle: "Automate Document Verification",
  },

  // ──────────────────────────────────────────────
  // 8. Business Verification
  // ──────────────────────────────────────────────
  {
    slug: "business-verification",
    title: "Business Verification",
    subtitle: "CIPC Company Registration, BEE Verification & Director Checks",
    description:
      "Verify South African businesses through CIPC (Companies and Intellectual Property Commission) registration checks, BEE certificate validation, director lookups, and company status confirmation. VeriGate ensures your business partners and vendors are legitimate, compliant, and in good standing.",
    icon: "Building2",
    turnaround: "1-3 days",
    howItWorks: [
      {
        step: "01",
        title: "Submit Company Details",
        description:
          "Provide the company registration number, trading name, or director details via the dashboard or API.",
      },
      {
        step: "02",
        title: "CIPC Registry Lookup",
        description:
          "VeriGate queries the CIPC company register to confirm registration status, company type, and registered details.",
      },
      {
        step: "03",
        title: "Director & BEE Verification",
        description:
          "Directors and shareholders are identified, and BEE certificates are validated against the BBBEE Commission records.",
      },
      {
        step: "04",
        title: "Business Verification Report",
        description:
          "A comprehensive report is delivered with company status, director details, BEE level, and compliance flags.",
      },
    ],
    features: [
      {
        title: "CIPC Integration",
        description:
          "Real-time company registration and annual return status checks against the CIPC company register.",
        items: [
          "Real-time CIPC company registration status and type confirmation",
          "Annual return filing status and compliance flag checks",
          "Registered address and principal business activity verification",
          "Company name history and name change tracking",
        ],
      },
      {
        title: "Director & Ownership Checks",
        description:
          "Identify directors, beneficial owners, and related company networks to detect shell company structures.",
        items: [
          "Director appointment and resignation history from CIPC records",
          "Beneficial ownership identification for FICA compliance",
          "Director cross-referencing with criminal and credit databases",
          "Related company network mapping to detect shell company structures",
        ],
      },
      {
        title: "BEE & Tax Verification",
        description:
          "Validate BEE certificates, SARS tax clearance, and government supplier registration status.",
        items: [
          "BEE certificate authenticity verification and level confirmation",
          "SARS tax clearance certificate (TCC) validation",
          "CIDB (Construction Industry Development Board) grading confirmation",
          "CSD (Central Supplier Database) registration verification for government suppliers",
        ],
      },
    ],
    useCases: [
      {
        title: "Vendor Onboarding",
        description:
          "Due diligence on vendors and suppliers before onboarding them to your procurement system or supply chain.",
      },
      {
        title: "KYB Compliance",
        description:
          "Know Your Business compliance for banks and financial services during corporate client onboarding.",
      },
      {
        title: "Tender Evaluation",
        description:
          "Government and private sector tender evaluation requiring CIPC, BEE, and tax clearance verification.",
      },
      {
        title: "M&A Due Diligence",
        description:
          "Target company verification, director screening, and ownership chain analysis for mergers and acquisitions.",
      },
    ],
    relatedTypes: [
      {
        slug: "credit-screening",
        title: "Credit Screening",
        description: "Add credit checks for directors and the company itself during business verification.",
      },
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Verify the identities of directors and authorised signatories.",
      },
      {
        slug: "address-verification",
        title: "Address Verification",
        description: "Confirm the business's registered and physical address for FICA compliance.",
      },
    ],
    ctaTitle: "Verify Businesses Instantly",
  },

  // ──────────────────────────────────────────────
  // 9. Address Verification
  // ──────────────────────────────────────────────
  {
    slug: "address-verification",
    title: "Address Verification",
    subtitle: "Proof of Residence Validation via Utility Bills & Municipal Records",
    description:
      "Verify South African residential and business addresses through document-based proof of residence validation, municipal records cross-referencing, and geolocation checks. VeriGate ensures FICA-compliant address verification by validating utility bills, bank statements, and lease agreements.",
    icon: "MapPin",
    turnaround: "1-2 days",
    howItWorks: [
      {
        step: "01",
        title: "Upload Proof of Residence",
        description:
          "The candidate uploads a recent utility bill, bank statement, municipal account, or lease agreement (not older than 3 months).",
      },
      {
        step: "02",
        title: "Document Extraction & Validation",
        description:
          "AI-powered OCR extracts the address, account holder name, and issue date, then validates the document's authenticity.",
      },
      {
        step: "03",
        title: "Address Cross-Referencing",
        description:
          "The extracted address is cross-referenced with municipal records, postal databases, and credit bureau address histories.",
      },
      {
        step: "04",
        title: "Verified Address Report",
        description:
          "A report confirms the address match, document validity, address tenure, and any discrepancies between claimed and verified addresses.",
      },
    ],
    features: [
      {
        title: "Document-Based Verification",
        description:
          "Validate utility bills, bank statements, and lease agreements from major SA service providers.",
        items: [
          "Utility bill validation from Eskom, municipal providers, and water boards",
          "Bank statement address extraction from all major SA banks",
          "Lease agreement and title deed verification for property-related checks",
          "Telkom, Vodacom, and MTN account statement address confirmation",
        ],
      },
      {
        title: "Database Cross-Referencing",
        description:
          "Cross-reference addresses against municipal, postal, and credit bureau databases for independent confirmation.",
        items: [
          "Municipal rates account and property registration lookup",
          "South African Post Office address database validation",
          "Credit bureau address history from TransUnion and Experian SA",
          "Geolocation pin-drop verification for physical address confirmation",
        ],
      },
      {
        title: "FICA Compliance",
        description:
          "Built-in FICA address verification rules including document recency and qualifying document type enforcement.",
        items: [
          "Full compliance with FICA address verification requirements",
          "3-month document recency validation as per FICA guidelines",
          "Automated rejection of expired or non-qualifying proof of residence",
          "Audit trail with document retention per FICA record-keeping requirements",
        ],
      },
    ],
    useCases: [
      {
        title: "Bank Account Opening",
        description:
          "FICA-mandated proof of residence verification for new bank account applications and account upgrades.",
      },
      {
        title: "Insurance Risk Assessment",
        description:
          "Confirm policyholder addresses for accurate risk assessment, premium calculation, and claims processing.",
      },
      {
        title: "Credit Applications",
        description:
          "Address verification under NCA requirements during credit applications for loans and retail accounts.",
      },
      {
        title: "Tenant Screening",
        description:
          "Verify tenant address history for property management companies to assess reliability and stability.",
      },
    ],
    relatedTypes: [
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Confirm identity alongside address for complete FICA customer due diligence.",
      },
      {
        slug: "document-verification",
        title: "Document Verification",
        description: "Authenticate the proof of residence document before extracting address data.",
      },
      {
        slug: "credit-screening",
        title: "Credit Screening",
        description: "Cross-reference credit bureau address records with submitted proof of residence.",
      },
    ],
    ctaTitle: "Verify Addresses Reliably",
  },

  // ──────────────────────────────────────────────
  // 10. eIDV
  // ──────────────────────────────────────────────
  {
    slug: "eidv",
    title: "Electronic Identity Verification (eIDV)",
    subtitle: "Real-Time DHA Validation Without Document Uploads",
    description:
      "Perform electronic identity verification without requiring physical document uploads. VeriGate validates identity data in real time against the Department of Home Affairs population register, credit bureau records, and other authoritative South African databases, enabling frictionless digital onboarding.",
    icon: "Fingerprint",
    turnaround: "Instant",
    howItWorks: [
      {
        step: "01",
        title: "Collect Identity Data",
        description:
          "The user provides their 13-digit SA ID number, full name, and date of birth through your application or form.",
      },
      {
        step: "02",
        title: "Multi-Source Verification",
        description:
          "VeriGate simultaneously queries the DHA population register, credit bureau records, and voter's roll data.",
      },
      {
        step: "03",
        title: "Data Match Scoring",
        description:
          "Each data point (name, surname, date of birth, gender) is scored for match accuracy across all sources.",
      },
      {
        step: "04",
        title: "eIDV Result Returned",
        description:
          "An instant pass/fail result with detailed match scores is returned via API, enabling real-time decisioning.",
      },
    ],
    features: [
      {
        title: "Real-Time Database Checks",
        description:
          "Simultaneous queries against DHA, credit bureaux, and electoral data for multi-source identity confirmation.",
        items: [
          "DHA population register for citizenship and identity confirmation",
          "Credit bureau (TransUnion, Experian, XDS) identity data cross-check",
          "Voter's roll and electoral commission data matching",
          "Deceased persons register check to prevent identity fraud",
        ],
      },
      {
        title: "Frictionless Experience",
        description:
          "No document uploads required, delivering sub-second verification for seamless digital onboarding.",
        items: [
          "No document upload required for a seamless user experience",
          "Sub-second API response times for real-time onboarding flows",
          "Progressive verification allowing step-up to document checks when needed",
          "Mobile-optimised data capture with SA ID number validation",
        ],
      },
      {
        title: "Configurable Matching",
        description:
          "Adjustable match thresholds, fuzzy name matching, and fallback routing for your specific risk appetite.",
        items: [
          "Adjustable match thresholds per data field for your risk appetite",
          "Fuzzy name matching to handle spelling variations and maiden names",
          "Multi-source consensus scoring for higher confidence results",
          "Fallback routing to manual review when automated scores are inconclusive",
        ],
      },
    ],
    useCases: [
      {
        title: "Digital Account Opening",
        description:
          "Low-friction identity verification for banks and fintechs during digital-first account opening flows.",
      },
      {
        title: "RICA SIM Registration",
        description:
          "Instant identity confirmation for SIM card registration under RICA requirements at mobile operator outlets.",
      },
      {
        title: "E-Commerce Checkout",
        description:
          "Verify customer identity during high-value e-commerce purchases to prevent fraud and chargebacks.",
      },
      {
        title: "Insurance Quote-to-Bind",
        description:
          "Identity validation during the insurance application process without slowing down the quote-to-bind journey.",
      },
    ],
    relatedTypes: [
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Step up to document-based verification when eIDV scores are borderline.",
      },
      {
        slug: "face-verification",
        title: "Face Verification",
        description: "Add biometric face matching for higher assurance when eIDV alone is insufficient.",
      },
      {
        slug: "age-verification",
        title: "Age Verification",
        description: "Extract and verify age from eIDV results for age-restricted services.",
      },
    ],
    ctaTitle: "Enable Frictionless eIDV",
  },

  // ──────────────────────────────────────────────
  // 11. Age Verification
  // ──────────────────────────────────────────────
  {
    slug: "age-verification",
    title: "Age Verification",
    subtitle: "Instant Date of Birth Validation for Age-Restricted Services",
    description:
      "Confirm that users meet minimum age requirements for age-restricted products and services in South Africa. VeriGate verifies age through ID number parsing, DHA database lookups, and document-based date of birth extraction, ensuring compliance with the Liquor Act, National Gambling Act, and Films and Publications Act.",
    icon: "Calendar",
    turnaround: "Instant",
    howItWorks: [
      {
        step: "01",
        title: "Capture Age Data",
        description:
          "The user provides their SA ID number or uploads an identity document for date of birth extraction.",
      },
      {
        step: "02",
        title: "Date of Birth Extraction",
        description:
          "The date of birth is extracted from the ID number (first 6 digits) or from the document via OCR.",
      },
      {
        step: "03",
        title: "Age Calculation & Threshold Check",
        description:
          "The user's age is calculated and compared against your configured minimum age requirement (e.g., 18 or 21).",
      },
      {
        step: "04",
        title: "Age Verification Result",
        description:
          "An instant pass/fail result is returned with the verified age, method used, and compliance confirmation.",
      },
    ],
    features: [
      {
        title: "Multiple Verification Methods",
        description:
          "Verify age via ID number parsing, document OCR, DHA cross-reference, and AI face-age estimation.",
        items: [
          "SA ID number date of birth extraction (embedded in the 13-digit format)",
          "Document OCR for passport and driver's licence date of birth extraction",
          "DHA database cross-reference for authoritative age confirmation",
          "Face-age estimation as an additional confidence layer using AI",
        ],
      },
      {
        title: "Configurable Age Rules",
        description:
          "Set custom minimum age thresholds per product, service, or province with real-time enforcement.",
        items: [
          "Custom minimum age thresholds per product, service, or jurisdiction",
          "Support for multiple age tiers (e.g., 16, 18, 21) in a single integration",
          "Real-time age gate for e-commerce checkout flows",
          "Geolocation-aware age requirements based on provincial regulations",
        ],
      },
      {
        title: "SA Regulatory Compliance",
        description:
          "Built-in compliance with the Liquor Act, National Gambling Act, Films Act, and Tobacco Control Act.",
        items: [
          "Liquor Act (Act 59 of 2003) age verification for alcohol sales",
          "National Gambling Act compliance for online and in-person gambling",
          "Films and Publications Act compliance for age-restricted content",
          "Tobacco Products Control Act compliance for cigarette and vape sales",
        ],
      },
    ],
    useCases: [
      {
        title: "Alcohol Delivery",
        description:
          "Online alcohol delivery services requiring age verification at checkout and delivery to comply with the Liquor Act.",
      },
      {
        title: "Online Gambling",
        description:
          "Casino and online gambling platforms verifying player age under the National Gambling Act before allowing play.",
      },
      {
        title: "Content Platforms",
        description:
          "Streaming and content platforms restricting access to age-inappropriate material under the Films and Publications Act.",
      },
      {
        title: "Tobacco & Vape Retail",
        description:
          "Confirm legal purchase age for cigarettes, e-cigarettes, and vaping products at online and physical retail outlets.",
      },
    ],
    relatedTypes: [
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Full identity verification when age check alone is not sufficient for compliance.",
      },
      {
        slug: "eidv",
        title: "Electronic Identity Verification",
        description: "Extract age data from eIDV results without requiring document uploads.",
      },
      {
        slug: "document-verification",
        title: "Document Verification",
        description: "Authenticate the identity document used for date of birth extraction.",
      },
    ],
    ctaTitle: "Add Age Verification Today",
  },

  // ──────────────────────────────────────────────
  // 12. Behavioural Biometrics
  // ──────────────────────────────────────────────
  {
    slug: "behavioral-biometrics",
    title: "Behavioural Biometrics",
    subtitle: "Continuous Authentication via Typing, Mouse & Device Patterns",
    description:
      "Detect fraud and authenticate users continuously by analysing behavioural signals such as typing cadence, mouse movement patterns, touchscreen pressure, and device fingerprinting. VeriGate's behavioural biometrics engine operates invisibly in the background, providing risk scores without adding friction to the user experience.",
    icon: "Activity",
    turnaround: "Instant",
    howItWorks: [
      {
        step: "01",
        title: "Deploy Tracking Script",
        description:
          "Add VeriGate's lightweight JavaScript snippet to your web application or integrate the mobile SDK into your native app.",
      },
      {
        step: "02",
        title: "Behavioural Data Collection",
        description:
          "The SDK passively collects typing speed, rhythm, mouse movements, scroll patterns, and device sensor data.",
      },
      {
        step: "03",
        title: "AI Pattern Analysis",
        description:
          "Machine learning models build a behavioural profile and detect anomalies indicating bots, account takeover, or impersonation.",
      },
      {
        step: "04",
        title: "Risk Score Delivered",
        description:
          "A real-time risk score and session trust level is returned, enabling automated step-up authentication or blocking decisions.",
      },
    ],
    features: [
      {
        title: "Typing & Interaction Analysis",
        description:
          "Measure keystroke dynamics, mouse velocity, touchscreen gestures, and scroll patterns for user profiling.",
        items: [
          "Keystroke dynamics measuring typing speed, dwell time, and flight time",
          "Mouse movement velocity, curvature, and click pattern analysis",
          "Touchscreen gesture recognition including pressure and swipe patterns",
          "Scroll behaviour and page navigation pattern profiling",
        ],
      },
      {
        title: "Device Intelligence",
        description:
          "Fingerprint browsers and devices across 200+ attributes to detect emulators, VPNs, and suspicious environments.",
        items: [
          "Browser and device fingerprinting across 200+ attributes",
          "Emulator, virtual machine, and remote desktop detection",
          "VPN, proxy, and Tor exit node identification",
          "Device reputation scoring based on historical fraud signals",
        ],
      },
      {
        title: "Continuous Authentication",
        description:
          "Session-long monitoring with anomaly detection and automated step-up triggers at configurable risk thresholds.",
        items: [
          "Session-long risk monitoring without interrupting the user",
          "Anomaly detection when behaviour deviates from the established profile",
          "Automated step-up authentication triggers at configurable risk thresholds",
          "Integration with your existing fraud rules engine via webhooks and API",
        ],
      },
    ],
    useCases: [
      {
        title: "Banking Session Protection",
        description:
          "Detect account takeover attacks during online banking sessions by identifying behavioural anomalies in real time.",
      },
      {
        title: "E-Commerce Bot Detection",
        description:
          "Identify automated bots performing credential stuffing, inventory hoarding, and payment fraud on retail sites.",
      },
      {
        title: "Insurance Fraud Detection",
        description:
          "Detect fraudulent behaviour patterns during online insurance claims submission and policy changes.",
      },
      {
        title: "Corporate App Security",
        description:
          "Protect enterprise applications and remote workforce access against credential sharing and impersonation.",
      },
    ],
    relatedTypes: [
      {
        slug: "face-verification",
        title: "Face Verification",
        description: "Trigger biometric face verification when behavioural anomalies are detected.",
      },
      {
        slug: "eidv",
        title: "Electronic Identity Verification",
        description: "Combine behavioural signals with electronic identity checks for multi-layered fraud prevention.",
      },
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Re-verify identity when behavioural analysis flags a compromised session.",
      },
    ],
    ctaTitle: "Deploy Behavioural Biometrics",
  },

  // ──────────────────────────────────────────────
  // 13. Consent Verification
  // ──────────────────────────────────────────────
  {
    slug: "consent-verification",
    title: "Consent Verification",
    subtitle: "POPIA Consent Management & Data Subject Rights",
    description:
      "Manage and verify user consent in compliance with South Africa's Protection of Personal Information Act (POPIA). VeriGate provides a complete consent lifecycle platform covering collection, storage, withdrawal, and audit trails, ensuring your organisation meets Information Regulator requirements.",
    icon: "ShieldCheck",
    turnaround: "Instant",
    howItWorks: [
      {
        step: "01",
        title: "Configure Consent Types",
        description:
          "Define the types of consent required (identity verification, credit checks, criminal checks, marketing) in the VeriGate dashboard.",
      },
      {
        step: "02",
        title: "Collect Consent",
        description:
          "Present POPIA-compliant consent forms to data subjects via the embeddable widget, email, or SMS with digital acceptance.",
      },
      {
        step: "03",
        title: "Store & Manage Records",
        description:
          "Consent records are stored with timestamps, IP addresses, and version tracking in a tamper-proof audit log.",
      },
      {
        step: "04",
        title: "Facilitate Data Subject Rights",
        description:
          "Enable data subjects to view, withdraw, or modify consent and submit POPIA access requests through a self-service portal.",
      },
    ],
    features: [
      {
        title: "POPIA Compliance",
        description:
          "Pre-built consent templates, version tracking, and re-consent triggers aligned with POPIA Section 11.",
        items: [
          "Pre-built consent templates aligned with POPIA Section 11 requirements",
          "Specific, informed, and voluntary consent collection mechanisms",
          "Consent version tracking with automatic re-consent triggers on policy changes",
          "Information Regulator audit-ready reporting and documentation",
        ],
      },
      {
        title: "Data Subject Rights",
        description:
          "Self-service portal for consent viewing, withdrawal, access requests, and right to correction.",
        items: [
          "Self-service portal for data subjects to view their consent status",
          "One-click consent withdrawal with automated downstream data handling",
          "POPIA Section 23 access request processing and fulfillment",
          "Right to correction and deletion request management workflows",
        ],
      },
      {
        title: "Integration & Automation",
        description:
          "Embeddable widgets, API-first consent checks, and CRM/HRIS integrations for centralised tracking.",
        items: [
          "Embeddable consent widget for your website, app, and email campaigns",
          "API-first design for consent checks before any verification process",
          "Automated consent expiry and renewal reminders",
          "Integration with your CRM, HRIS, and ATS platforms for centralised consent tracking",
        ],
      },
    ],
    useCases: [
      {
        title: "Pre-Verification Consent",
        description:
          "Collect consent before running criminal, credit, or qualification checks as legally required by POPIA.",
      },
      {
        title: "Marketing Consent",
        description:
          "Manage customer marketing consent for email, SMS, and direct marketing communications under POPIA.",
      },
      {
        title: "Employee Data Consent",
        description:
          "HR departments managing employee consent for processing personal and special personal information.",
      },
      {
        title: "Healthcare Consent",
        description:
          "Patient consent management for processing personal and health information in healthcare facilities.",
      },
    ],
    relatedTypes: [
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Verify identity to confirm the data subject before processing consent records.",
      },
      {
        slug: "e-signature",
        title: "Electronic Signatures",
        description: "Capture legally binding electronic signatures on consent forms.",
      },
      {
        slug: "criminal-record-checks",
        title: "Criminal Record Checks",
        description: "Ensure consent is captured before initiating criminal background checks.",
      },
    ],
    ctaTitle: "Manage Consent Compliantly",
  },

  // ──────────────────────────────────────────────
  // 14. E-Signature
  // ──────────────────────────────────────────────
  {
    slug: "e-signature",
    title: "Electronic Signatures",
    subtitle: "Identity-Linked Document Signing with Legal Validity",
    description:
      "Enable secure electronic document signing that is legally valid under South Africa's Electronic Communications and Transactions (ECT) Act. VeriGate links each signature to a verified identity, creating a tamper-proof, auditable signing experience for contracts, consent forms, and compliance documents.",
    icon: "PenTool",
    turnaround: "Instant",
    howItWorks: [
      {
        step: "01",
        title: "Upload Document",
        description:
          "Upload the document to be signed via the dashboard or API, and specify the signing parties and signature fields.",
      },
      {
        step: "02",
        title: "Identity-Linked Signing",
        description:
          "Each signer verifies their identity via eIDV, face match, or OTP before signing, linking the signature to a verified identity.",
      },
      {
        step: "03",
        title: "Secure Signature Application",
        description:
          "The electronic signature is applied with a timestamp, IP address, device fingerprint, and cryptographic hash of the document.",
      },
      {
        step: "04",
        title: "Signed Document & Audit Trail",
        description:
          "The signed document is delivered with a comprehensive audit trail certificate proving the integrity of the signing process.",
      },
    ],
    features: [
      {
        title: "Legal Validity",
        description:
          "ECT Act-compliant electronic and advanced electronic signatures with tamper-evident cryptographic sealing.",
        items: [
          "Compliant with the ECT Act (Act 25 of 2002) for electronic signatures",
          "Advanced Electronic Signature (AES) support for high-assurance requirements",
          "Tamper-evident cryptographic sealing of signed documents",
          "Court-admissible audit trail certificates with full signing chain of custody",
        ],
      },
      {
        title: "Identity-Linked Signing",
        description:
          "Signatures are cryptographically bound to verified identities via eIDV, face match, or MFA.",
        items: [
          "Pre-signature identity verification via eIDV, face match, or SMS OTP",
          "Each signature cryptographically bound to the signer's verified identity",
          "Multi-factor authentication options for high-value document signing",
          "Signer photograph capture at the moment of signing for additional evidence",
        ],
      },
      {
        title: "Workflow Management",
        description:
          "Sequential and parallel signing workflows with automated reminders and bulk signing capabilities.",
        items: [
          "Sequential and parallel signing workflows for multi-party documents",
          "Automated signing reminders and escalation for overdue signatures",
          "Template library for frequently signed document types",
          "Bulk signing capabilities for high-volume document processing",
        ],
      },
    ],
    useCases: [
      {
        title: "Employment Contracts",
        description:
          "Sign employment contracts with identity-verified electronic signatures during digital onboarding.",
      },
      {
        title: "Consent Forms",
        description:
          "POPIA consent form signing with audit-ready compliance documentation and tamper-proof storage.",
      },
      {
        title: "Property Transactions",
        description:
          "Lease agreements and sale contracts for real estate transactions with legally binding electronic signatures.",
      },
      {
        title: "Insurance Documents",
        description:
          "Policy documents, claims forms, and underwriting agreements signed digitally during onboarding.",
      },
    ],
    relatedTypes: [
      {
        slug: "consent-verification",
        title: "Consent Verification",
        description: "Pair consent management with e-signatures for complete POPIA compliance.",
      },
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Verify signer identity before applying the electronic signature.",
      },
      {
        slug: "document-verification",
        title: "Document Verification",
        description: "Authenticate supporting documents before including them in signing workflows.",
      },
    ],
    ctaTitle: "Enable Secure E-Signatures",
  },

  // ──────────────────────────────────────────────
  // 15. NFC Verification
  // ──────────────────────────────────────────────
  {
    slug: "nfc-verification",
    title: "NFC Verification",
    subtitle: "NFC Chip Reading for Smart IDs & E-Passports",
    description:
      "Read and verify the cryptographic data stored in NFC chips embedded in South African Smart ID cards and e-passports. VeriGate's NFC verification confirms document authenticity at the chip level, extracts high-resolution biometric photos, and validates digital signatures issued by the Department of Home Affairs.",
    icon: "Nfc",
    turnaround: "Instant",
    howItWorks: [
      {
        step: "01",
        title: "Initiate NFC Read",
        description:
          "The user is prompted to hold their Smart ID card or e-passport against their NFC-enabled mobile device.",
      },
      {
        step: "02",
        title: "Chip Data Extraction",
        description:
          "The VeriGate mobile SDK reads the NFC chip, extracting biographical data, the high-resolution photo, and digital signatures.",
      },
      {
        step: "03",
        title: "Cryptographic Validation",
        description:
          "The chip's digital signature is verified against the issuing authority's (DHA) certificate chain, confirming the chip has not been cloned or tampered with.",
      },
      {
        step: "04",
        title: "NFC Verification Result",
        description:
          "A verification result confirms chip authenticity, extracted biometric data, and document validity, with a downloadable report.",
      },
    ],
    features: [
      {
        title: "Chip-Level Authentication",
        description:
          "BAC, PACE, Passive and Active Authentication protocols verify the chip is genuine and unmodified.",
        items: [
          "BAC (Basic Access Control) and PACE protocol support for secure chip access",
          "Passive Authentication verifying the chip data's digital signature integrity",
          "Active Authentication proving the chip is genuine and not cloned",
          "ICAO 9303 and ISO 14443 compliance for international travel document standards",
        ],
      },
      {
        title: "Biometric Data Extraction",
        description:
          "High-resolution facial photos, fingerprint templates, and MRZ data extracted directly from the NFC chip.",
        items: [
          "High-resolution facial photo extraction from the NFC chip (higher quality than printed photo)",
          "Fingerprint template extraction where available on the chip",
          "Machine-readable zone (MRZ) data validation against chip contents",
          "Biographical data extraction matching printed and chip-stored information",
        ],
      },
      {
        title: "SA Document Support",
        description:
          "Full support for SA Smart ID cards, e-passports, and foreign e-passports with graceful fallback.",
        items: [
          "South African Smart ID card NFC chip reading and validation",
          "SA e-passport chip verification with DHA certificate chain validation",
          "Foreign e-passport support for international visitor verification",
          "Graceful fallback to optical verification for documents without NFC chips",
        ],
      },
    ],
    useCases: [
      {
        title: "High-Assurance Onboarding",
        description:
          "Chip-level identity proof for premium banking, wealth management, and high-trust account opening.",
      },
      {
        title: "Border & Travel",
        description:
          "Airport and border control identity verification for e-passport holders at automated gates.",
      },
      {
        title: "Premium Banking",
        description:
          "The highest level of document authenticity for private banking and premium financial services onboarding.",
      },
      {
        title: "Government Services",
        description:
          "Smart ID chip verification for government service delivery, voter registration, and secure facility access.",
      },
    ],
    relatedTypes: [
      {
        slug: "document-verification",
        title: "Document Verification",
        description: "Combine optical document analysis with NFC chip data for maximum authenticity.",
      },
      {
        slug: "face-verification",
        title: "Face Verification",
        description: "Match a live selfie to the high-resolution chip photo for biometric confirmation.",
      },
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Cross-check chip-extracted data against the DHA population register.",
      },
    ],
    ctaTitle: "Add NFC Verification",
  },

  // ──────────────────────────────────────────────
  // 16. Video Identification
  // ──────────────────────────────────────────────
  {
    slug: "video-identification",
    title: "Video Identification",
    subtitle: "Live Video Verification with Trained Agents",
    description:
      "Conduct real-time video identification sessions where a trained VeriGate agent verifies the user's identity via live video call. The agent guides the user through document presentation, facial comparison, and challenge-response questions, providing the highest level of assurance for remote identity verification in South Africa.",
    icon: "Video",
    turnaround: "15-30min",
    howItWorks: [
      {
        step: "01",
        title: "Schedule or Start Video Call",
        description:
          "The user initiates an on-demand video call or schedules an appointment through the VeriGate platform.",
      },
      {
        step: "02",
        title: "Live Document Presentation",
        description:
          "The user presents their identity document on camera while the trained agent inspects security features, photos, and details.",
      },
      {
        step: "03",
        title: "Facial Comparison & Challenge Questions",
        description:
          "The agent performs a live facial comparison and asks challenge-response questions to confirm the user's identity.",
      },
      {
        step: "04",
        title: "Verification Decision & Recording",
        description:
          "The agent makes a verification decision. The full session is recorded and stored as an audit trail with the decision rationale.",
      },
    ],
    features: [
      {
        title: "Trained Verification Agents",
        description:
          "South African-based, multilingual agents trained in document authentication and fraud detection.",
        items: [
          "South African-based agents trained in document authentication and fraud detection",
          "Multilingual support covering English, Afrikaans, Zulu, Xhosa, and Sotho",
          "Agent quality scoring and continuous training on emerging fraud tactics",
          "Average call duration of 5-10 minutes for a seamless user experience",
        ],
      },
      {
        title: "Session Management",
        description:
          "On-demand and scheduled video calls with tamper-proof recording and automated transcription.",
        items: [
          "On-demand video calls with average wait times under 2 minutes",
          "Scheduled appointment booking for user convenience",
          "Session recording with tamper-proof storage for compliance audits",
          "Automated session transcription and decision logging",
        ],
      },
      {
        title: "High-Assurance Use Cases",
        description:
          "Meets the highest KYC assurance levels for SARB, FSCA, and enhanced due diligence requirements.",
        items: [
          "Meets the highest KYC assurance levels for financial services regulators",
          "Compatible with SARB and FSCA enhanced due diligence requirements",
          "Fallback option when automated verification cannot reach a conclusive result",
          "White-label video interface customisable with your branding",
        ],
      },
    ],
    useCases: [
      {
        title: "High-Value Account Opening",
        description:
          "Enhanced due diligence for high-value financial accounts requiring the highest level of identity assurance.",
      },
      {
        title: "Wealth Management",
        description:
          "Private banking and wealth management onboarding for high-net-worth individual (HNWI) clients.",
      },
      {
        title: "Cross-Border Verification",
        description:
          "Identity verification for foreign nationals opening South African accounts or conducting regulated transactions.",
      },
      {
        title: "Inconclusive Fallback",
        description:
          "Human-assisted verification when automated checks produce inconclusive or borderline results.",
      },
    ],
    relatedTypes: [
      {
        slug: "face-verification",
        title: "Face Verification",
        description: "Automated face matching as the first step before escalating to video identification.",
      },
      {
        slug: "identity-verification",
        title: "Identity Verification",
        description: "Combine database identity checks with live video verification for maximum assurance.",
      },
      {
        slug: "document-verification",
        title: "Document Verification",
        description: "Authenticate documents optically before or after the live video session.",
      },
    ],
    ctaTitle: "Enable Video Identification",
  },
];

/** Look up a single verification type by slug */
export const getVerificationType = (slug: string): VerificationTypeData | undefined =>
  verificationTypes.find((vt) => vt.slug === slug);

/** Get related verification types for a given slug */
export const getRelatedVerificationTypes = (slugs: string[]): VerificationTypeData[] =>
  slugs
    .map((s) => getVerificationType(s))
    .filter((vt): vt is VerificationTypeData => vt !== undefined);
