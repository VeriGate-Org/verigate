// Fraud Prevention data — 5 SA-focused fraud prevention types
// Consumed by /fraud-prevention (hub) and /fraud-prevention/:slug (detail)
import { FraudPreventionData } from "@/components/templates/FraudPreventionTemplate";

export const fraudPreventionData: FraudPreventionData[] = [
  // ──────────────────────────────────────────────
  // 1. Identity Fraud
  // ──────────────────────────────────────────────
  {
    slug: "identity-fraud",
    title: "Identity Fraud Prevention",
    subtitle: "Detect & Prevent Identity Theft, Impersonation & Stolen Credentials",
    description:
      "Protect your organisation against identity fraud, impersonation, and stolen credentials in South Africa. VeriGate's multi-layered identity fraud detection combines DHA verification, biometric matching, credit bureau intelligence, and consortium fraud databases to stop fraudsters before they cause damage.",
    icon: "Fingerprint",
    threats: [
      {
        title: "Stolen Identity Documents",
        description:
          "Criminals use stolen or lost South African ID documents to impersonate legitimate individuals during onboarding, account opening, or employment applications. Over 900 000 ID documents are reported lost or stolen annually in South Africa.",
        severity: "Critical",
      },
      {
        title: "Deceased Identity Exploitation",
        description:
          "Using the identity documents of deceased persons to open bank accounts, apply for credit, or pass background checks, exploiting delays in cross-system updates between DHA and financial institutions.",
        severity: "High",
      },
      {
        title: "Credential Stuffing & Account Takeover",
        description:
          "Stolen login credentials from data breaches are used to access existing customer accounts, change details, and conduct fraudulent transactions at South African financial institutions.",
        severity: "High",
      },
    ],
    detectionMethods: [
      {
        title: "DHA Population Register Verification",
        description: "Real-time identity validation against official DHA records to confirm the identity exists and is alive",
        items: [
          "DHA population register cross-referencing for biographical data confirmation",
          "Deceased persons register screening to prevent ghost identity usage",
          "Citizenship and immigration status verification",
          "ID number format and Luhn checksum validation",
        ],
      },
      {
        title: "Biometric Face Matching",
        description: "AI-powered facial recognition and liveness detection to confirm the person matches their claimed identity",
        items: [
          "Selfie-to-document photo comparison with 99.8% accuracy",
          "Passive and active liveness detection to defeat spoofing attacks",
          "3D mask, printed photo, and screen replay prevention",
          "Deepfake detection using neural texture analysis",
        ],
      },
      {
        title: "Consortium Fraud Intelligence",
        description: "Cross-organisation identity intelligence leveraging VeriGate's network of South African clients",
        items: [
          "Shared fraud database screening for previously flagged identities",
          "Velocity checks detecting the same identity used across multiple organisations",
          "Device and IP address correlation for linked fraud attempts",
          "Real-time consortium alerts when new fraud patterns emerge",
        ],
      },
    ],
    solutions: [
      {
        title: "Real-Time Identity Screening",
        description:
          "Screen every identity against DHA records, credit bureau data, and consortium fraud databases in real time during onboarding, catching stolen and deceased identities before approval.",
      },
      {
        title: "Multi-Factor Verification",
        description:
          "Combine identity verification, document authentication, and biometric face matching for layered fraud prevention that defeats even sophisticated impersonation attempts.",
      },
      {
        title: "Fraud Pattern Detection",
        description:
          "AI-powered pattern analysis identifies suspicious identity usage across your verification history, detecting repeat offenders and emerging fraud rings targeting South African businesses.",
      },
    ],
    relatedPrevention: [
      {
        slug: "document-fraud",
        title: "Document Fraud",
        description: "Detect forged and altered documents used to support identity fraud schemes.",
      },
      {
        slug: "deepfake-detection",
        title: "Deepfake Detection",
        description: "Identify AI-generated faces and synthetic media used to impersonate real people.",
      },
      {
        slug: "synthetic-identity",
        title: "Synthetic Identity Fraud",
        description: "Expose fabricated identities built from combinations of real and fake data.",
      },
    ],
    ctaTitle: "Stop Identity Fraud Today",
  },

  // ──────────────────────────────────────────────
  // 2. Document Fraud
  // ──────────────────────────────────────────────
  {
    slug: "document-fraud",
    title: "Document Fraud Prevention",
    subtitle: "Detect Forged Documents, Tampered IDs & Fake Qualifications",
    description:
      "Detect forged, altered, and counterfeit documents with AI-powered forensic analysis. VeriGate's document fraud prevention covers South African Smart IDs, green ID books, qualifications, payslips, bank statements, and professional certificates, analysing over 50 authenticity markers per document.",
    icon: "FileWarning",
    threats: [
      {
        title: "Digital Tampering & Forgery",
        description:
          "Fraudsters use photo editing software and AI tools to alter legitimate documents, changing names, dates, amounts, and qualification details on South African IDs, payslips, and certificates.",
        severity: "Critical",
      },
      {
        title: "Counterfeit Identity Documents",
        description:
          "Entirely fabricated South African Smart IDs, green ID books, and passports designed to mimic genuine DHA-issued documents, often produced by sophisticated criminal syndicates.",
        severity: "High",
      },
      {
        title: "Fake Qualifications & Certificates",
        description:
          "Fraudulent degree certificates, matric results, and professional registration documents from diploma mills or created using purchased templates, exploiting SA's high qualification fraud rate.",
        severity: "High",
      },
    ],
    detectionMethods: [
      {
        title: "AI Forensic Analysis",
        description: "Machine learning models trained on millions of documents to detect pixel-level manipulation and forgery",
        items: [
          "Pixel-level manipulation detection for cropped, pasted, and retouched areas",
          "Font substitution and inconsistency analysis across document fields",
          "Compression artefact anomaly detection for edited JPEG and PDF documents",
          "Copy-paste and clone stamp identification using error level analysis",
        ],
      },
      {
        title: "Security Feature Validation",
        description: "Automated verification of document-specific security features for SA identity documents",
        items: [
          "Hologram and watermark detection for Smart ID cards and passports",
          "Microprint and guilloche pattern validation using high-resolution scanning",
          "Barcode and MRZ data cross-referencing against printed document fields",
          "Known fraudulent template and serial number database screening",
        ],
      },
      {
        title: "Issuer Verification & Cross-Referencing",
        description: "Direct confirmation with issuing institutions and cross-reference against authoritative databases",
        items: [
          "SAQA NLRD queries for qualification certificate verification",
          "Direct university and TVET college graduation confirmation",
          "HPCSA, ECSA, SANC professional registration cross-checks",
          "SARS tax clearance and payslip employer verification",
        ],
      },
    ],
    solutions: [
      {
        title: "Automated Document Screening",
        description:
          "Every uploaded document is automatically screened for authenticity markers, tampering, and known fraud patterns using AI that analyses over 50 features specific to South African document formats.",
      },
      {
        title: "Expert Manual Review",
        description:
          "Flagged documents are escalated to trained forensic document examiners for conclusive authentication, providing expert human judgment for borderline cases.",
      },
      {
        title: "Cross-Organisation Detection",
        description:
          "Identify documents that have been submitted to multiple organisations using VeriGate's document hash database, catching recycled fraudulent documents across the consortium.",
      },
    ],
    relatedPrevention: [
      {
        slug: "identity-fraud",
        title: "Identity Fraud",
        description: "Document fraud is often part of a broader identity fraud scheme. Combine both checks.",
      },
      {
        slug: "deepfake-detection",
        title: "Deepfake Detection",
        description: "AI-generated documents represent the next evolution of document fraud.",
      },
      {
        slug: "synthetic-identity",
        title: "Synthetic Identity Fraud",
        description: "Forged documents are used to support synthetic identity creation and credit applications.",
      },
    ],
    ctaTitle: "Detect Document Fraud Instantly",
  },

  // ──────────────────────────────────────────────
  // 3. Deepfake Detection
  // ──────────────────────────────────────────────
  {
    slug: "deepfake-detection",
    title: "Deepfake Detection",
    subtitle: "Identify AI-Generated Faces, Synthetic Media & Video Manipulation",
    description:
      "Combat the rising threat of AI-generated fake documents, synthetic video calls, and biometric spoofing in South Africa. VeriGate's deepfake detection technology uses advanced neural analysis to distinguish genuine submissions from AI-fabricated content, protecting your organisation from next-generation fraud.",
    icon: "ScanFace",
    threats: [
      {
        title: "AI-Generated Identity Documents",
        description:
          "Fraudsters use generative AI tools to create convincing fake South African Smart IDs, green ID books, and passports that can bypass basic document checks and even deceive manual reviewers.",
        severity: "Critical",
      },
      {
        title: "Synthetic Video Verification Fraud",
        description:
          "Deepfake video technology is used to impersonate real individuals during live video verification calls, fooling KYC agents and automated liveness checks with real-time face-swaps.",
        severity: "Critical",
      },
      {
        title: "Biometric Spoofing with AI",
        description:
          "Attackers use high-resolution AI-generated face renders, 3D-printed masks, and GAN-produced images to defeat facial recognition and liveness systems at South African financial institutions.",
        severity: "High",
      },
    ],
    detectionMethods: [
      {
        title: "Neural Image Analysis",
        description: "Deep learning models trained to detect AI-generated visual artefacts invisible to the human eye",
        items: [
          "GAN fingerprint detection in submitted photos and document images",
          "Pixel-level inconsistency analysis for AI-generated imagery",
          "Compression artefact pattern matching against known generative AI tools",
          "Metadata integrity checks for synthetic content indicators",
        ],
      },
      {
        title: "Advanced Liveness Detection",
        description: "Multi-layered biometric liveness verification purpose-built to defeat AI-powered spoofing",
        items: [
          "Active liveness challenges with randomised prompts (blink, turn, smile)",
          "Passive liveness analysis using texture, reflection, and depth cues",
          "3D face mapping to detect flat images, screen replays, and printed photos",
          "Micro-expression and involuntary movement analysis for presence confirmation",
        ],
      },
      {
        title: "Video Integrity Analysis",
        description: "Real-time video stream analysis to detect deepfake manipulation during live calls",
        items: [
          "Frame-by-frame temporal consistency checks for face-swap detection",
          "Audio-visual sync analysis to detect lip-sync manipulation",
          "Lighting and shadow consistency verification across the video frame",
          "Face boundary artefact detection for real-time face-swap technology",
        ],
      },
    ],
    solutions: [
      {
        title: "AI Document Authenticity Engine",
        description:
          "Every submitted document is analysed by VeriGate's neural networks to detect AI-generated content, synthetic imagery, and digitally fabricated security features specific to South African document formats.",
      },
      {
        title: "Advanced Liveness Verification",
        description:
          "Multi-modal liveness detection combines active challenges, passive analysis, and 3D depth mapping to ensure the person in front of the camera is real, present, and matches their identity document.",
      },
      {
        title: "Continuous Model Updates",
        description:
          "VeriGate's deepfake detection models are continuously retrained against the latest generative AI techniques, ensuring protection keeps pace with evolving fraud methods in the South African market.",
      },
    ],
    relatedPrevention: [
      {
        slug: "identity-fraud",
        title: "Identity Fraud",
        description: "Deepfakes are increasingly used to support identity fraud. Combine detection approaches.",
      },
      {
        slug: "document-fraud",
        title: "Document Fraud",
        description: "AI-generated documents are the next evolution of document fraud.",
      },
      {
        slug: "synthetic-identity",
        title: "Synthetic Identity Fraud",
        description: "Deepfakes enable synthetic identities with AI-generated photos and documents.",
      },
    ],
    ctaTitle: "Defeat Deepfake Fraud",
  },

  // ──────────────────────────────────────────────
  // 4. Synthetic Identity Fraud
  // ──────────────────────────────────────────────
  {
    slug: "synthetic-identity",
    title: "Synthetic Identity Fraud Prevention",
    subtitle: "Expose Fabricated Identities Built from Real & Fake Data",
    description:
      "Detect and prevent synthetic identity fraud in South Africa, where criminals combine real ID numbers with fabricated personal information to create fictitious identities. VeriGate cross-references DHA records, credit bureau data, and consortium intelligence to identify ghost employees, fake applicants, and identities that don't belong to real people.",
    icon: "UserX",
    threats: [
      {
        title: "Frankenstein Identities",
        description:
          "Fraudsters combine a real South African ID number with a fake name, date of birth, or address to create a new identity that passes basic validation but belongs to no real person, then use it for credit applications and account opening.",
        severity: "Critical",
      },
      {
        title: "Credit Bureau Manipulation",
        description:
          "Synthetic identities are systematically used to build credit profiles at TransUnion, Experian, and XDS, then exploited for large-scale credit bust-out schemes costing South African lenders hundreds of millions of Rand annually.",
        severity: "Critical",
      },
      {
        title: "Ghost Employees",
        description:
          "Fabricated identities are added to company payrolls to divert salary payments to criminal-controlled bank accounts, a prevalent fraud type in both private sector and South African government departments.",
        severity: "High",
      },
    ],
    detectionMethods: [
      {
        title: "DHA Deep Validation",
        description: "Full biographical data matching against the Department of Home Affairs population register",
        items: [
          "Full name, date of birth, and gender matching against DHA records",
          "Deceased persons register real-time screening",
          "ID number issuance date and demographic consistency checks",
          "Citizenship and immigration status cross-validation",
        ],
      },
      {
        title: "Credit Bureau Intelligence",
        description: "Pattern analysis across all three South African credit bureaux to detect synthetic footprints",
        items: [
          "Credit file age versus identity age consistency analysis",
          "Thin-file and no-file identity risk scoring for new applicants",
          "Address and employer linkage network analysis across bureaux",
          "Authorised user and piggyback credit pattern detection",
        ],
      },
      {
        title: "Network Link Analysis",
        description: "Graph-based analysis to expose clusters of synthetic identities sharing common elements",
        items: [
          "Identity element reuse detection across multiple applications",
          "Shared address, phone number, and email clustering analysis",
          "Velocity checks for identity elements across the VeriGate consortium",
          "Known synthetic identity pattern matching from SA fraud databases",
        ],
      },
    ],
    solutions: [
      {
        title: "Identity Existence Verification",
        description:
          "VeriGate confirms that every identity element -- ID number, name, date of birth, and address -- corresponds to a real, living person in the DHA population register before onboarding proceeds.",
      },
      {
        title: "Credit Footprint Analysis",
        description:
          "Analyse the credit bureau footprint of every applicant to detect the telltale signs of synthetic identities: thin files, recent file creation, inconsistent addresses, and anomalous credit behaviour.",
      },
      {
        title: "Ongoing Identity Monitoring",
        description:
          "Continuously monitor onboarded identities for signs of synthetic identity fraud, including sudden credit bureau changes, DHA status updates, and consortium fraud alerts from other organisations.",
      },
    ],
    relatedPrevention: [
      {
        slug: "identity-fraud",
        title: "Identity Fraud",
        description: "Synthetic identity fraud is a specialised form of identity fraud. Combine both approaches.",
      },
      {
        slug: "deepfake-detection",
        title: "Deepfake Detection",
        description: "Synthetic identities are increasingly supported by AI-generated photos and documents.",
      },
      {
        slug: "device-intelligence",
        title: "Device Intelligence",
        description: "Detect when multiple synthetic identities originate from the same device or network.",
      },
    ],
    ctaTitle: "Stop Synthetic Identity Fraud",
  },

  // ──────────────────────────────────────────────
  // 5. Device Intelligence
  // ──────────────────────────────────────────────
  {
    slug: "device-intelligence",
    title: "Device Intelligence",
    subtitle: "Device Fingerprinting, Bot Detection & VPN/Proxy Detection",
    description:
      "Leverage device fingerprinting, SIM swap detection, bot identification, and geolocation verification to identify fraudulent activity before it reaches your verification pipeline. VeriGate's device intelligence is purpose-built for South African fraud vectors, including the country's significant SIM swap fraud problem.",
    icon: "Smartphone",
    threats: [
      {
        title: "SIM Swap Fraud",
        description:
          "South Africa's most prevalent fraud vector -- criminals fraudulently port a victim's mobile number to a new SIM card, intercepting OTPs and bypassing SMS-based authentication used by banks and verification platforms.",
        severity: "Critical",
      },
      {
        title: "Bot & Emulator Attacks",
        description:
          "Fraudsters use Android emulators, headless browsers, and automated bots to submit high volumes of fraudulent verification requests from a single machine while appearing as different devices.",
        severity: "High",
      },
      {
        title: "VPN/Proxy & Location Spoofing",
        description:
          "Criminals route traffic through VPNs, proxies, and Tor networks to mask their true location, making fraudulent applications appear to originate from within South Africa when they come from overseas fraud operations.",
        severity: "High",
      },
    ],
    detectionMethods: [
      {
        title: "Device Fingerprinting",
        description: "Unique device identification using 200+ hardware, software, and browser attributes",
        items: [
          "Browser and OS fingerprint collection across 200+ attributes",
          "Canvas and WebGL fingerprinting for device uniqueness",
          "Hardware attribute profiling (screen, GPU, CPU characteristics)",
          "Cross-session device recognition for repeat fraud detection",
        ],
      },
      {
        title: "SIM Swap Detection",
        description: "Real-time SIM swap monitoring integrated with South African mobile network operators",
        items: [
          "Mobile network operator SIM swap status API integration (Vodacom, MTN, Telkom, Cell C)",
          "SIM tenure and recent port-out activity checks",
          "IMEI-to-SIM binding consistency verification",
          "OTP delivery channel risk scoring based on SIM history",
        ],
      },
      {
        title: "Geolocation & Network Analysis",
        description: "Location and network intelligence to detect spoofing, proxies, and anomalous access patterns",
        items: [
          "IP geolocation verification against claimed South African address",
          "VPN, proxy, and Tor exit node detection and blocking",
          "GPS location spoofing and mock location detection on mobile",
          "Network carrier and ASN reputation scoring for known fraud networks",
        ],
      },
    ],
    solutions: [
      {
        title: "Real-Time SIM Swap Alerts",
        description:
          "VeriGate integrates with South African mobile network operators to detect recent SIM swaps before OTP-based verification is triggered, preventing account takeover and OTP interception fraud.",
      },
      {
        title: "Device Risk Scoring",
        description:
          "Every verification request is assigned a device risk score based on fingerprint uniqueness, emulator detection, VPN usage, and historical fraud association, enabling automated blocking of high-risk submissions.",
      },
      {
        title: "Device Velocity Controls",
        description:
          "Monitor and limit the number of verification requests from a single device, IP address, or network within configurable time windows to shut down device farm and bot operations targeting your platform.",
      },
    ],
    relatedPrevention: [
      {
        slug: "synthetic-identity",
        title: "Synthetic Identity Fraud",
        description: "Device intelligence reveals when multiple synthetic identities come from the same device.",
      },
      {
        slug: "identity-fraud",
        title: "Identity Fraud",
        description: "Add device-level signals to identity verification for a more complete fraud risk picture.",
      },
      {
        slug: "deepfake-detection",
        title: "Deepfake Detection",
        description: "Combine device intelligence with deepfake detection for content and infrastructure fraud coverage.",
      },
    ],
    ctaTitle: "Activate Device Intelligence",
  },
];

/** Look up a single fraud prevention type by slug */
export const getFraudPreventionType = (slug: string): FraudPreventionData | undefined =>
  fraudPreventionData.find((fp) => fp.slug === slug);
