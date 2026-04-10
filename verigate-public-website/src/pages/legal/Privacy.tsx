import LegalPageTemplate from "@/components/templates/LegalPageTemplate";

const privacySections = [
  {
    id: "introduction",
    title: "Introduction",
    content: `
      <p>VeriGate (Pty) Ltd ("VeriGate", "we", "us", or "our") is a company registered in the Republic of South Africa. We are committed to protecting the privacy and personal information of our clients, their candidates, and all users of our services.</p>
      <p>This Privacy Policy explains how we collect, use, store, and protect personal information in accordance with the Protection of Personal Information Act, 2013 (Act No. 4 of 2013) ("POPIA") and other applicable South African legislation.</p>
      <p>By using our website or services, you acknowledge that you have read and understood this Privacy Policy. If you do not agree with any part of this policy, please discontinue use of our services immediately.</p>
    `,
  },
  {
    id: "information-we-collect",
    title: "Information We Collect",
    content: `
      <p>We collect and process the following categories of personal information:</p>
      <p><strong>Personal Information:</strong> Full name, South African ID number, date of birth, gender, contact details (email address, phone number, physical address), and employment information provided during account registration or service usage.</p>
      <p><strong>Verification Data:</strong> Information collected as part of our background screening and verification services, including but not limited to identity verification results, criminal record check outcomes, qualification verification data, credit screening results, employment history verification, and reference check responses.</p>
      <p><strong>Usage Data:</strong> Information about how you interact with our platform, including IP addresses, browser type, device information, pages visited, time spent on pages, and navigation patterns.</p>
      <p><strong>Cookies and Tracking Data:</strong> We use cookies and similar tracking technologies to collect information about your browsing activity. Please refer to our <a href="/cookie-policy" class="text-primary hover:underline">Cookie Policy</a> for detailed information about our cookie usage.</p>
      <p><strong>Special Personal Information:</strong> In the course of providing verification services, we may process special personal information as defined in POPIA Section 26, including criminal history and biometric data. This information is processed only with explicit consent and in accordance with the lawful processing conditions set out in POPIA.</p>
    `,
  },
  {
    id: "how-we-use-information",
    title: "How We Use Your Information",
    content: `
      <p>We use the personal information we collect for the following purposes:</p>
      <p><strong>Verification Processing:</strong> To perform background screening and verification checks as requested by our clients, including identity verification, criminal record checks, qualification verification, credit screening, employment history verification, and reference checks.</p>
      <p><strong>Compliance and Legal Obligations:</strong> To comply with applicable laws and regulations, including POPIA, FICA (Financial Intelligence Centre Act), and sector-specific legislation. We also use information to respond to lawful requests from law enforcement and regulatory authorities.</p>
      <p><strong>Communication:</strong> To send you service-related communications, respond to enquiries, provide customer support, and send important notices about changes to our services or policies. With your consent, we may also send marketing communications about our products and services.</p>
      <p><strong>Service Improvement:</strong> To analyse usage patterns, improve our platform and services, develop new features, ensure platform security, and detect and prevent fraud or misuse of our services.</p>
    `,
  },
  {
    id: "legal-basis",
    title: "Legal Basis for Processing",
    content: `
      <p>In accordance with POPIA Section 11, we process personal information based on the following lawful processing conditions:</p>
      <p><strong>Consent (Section 11(1)(a)):</strong> Where you have given us your specific, informed, and voluntary consent to process your personal information for a defined purpose. You have the right to withdraw consent at any time, though this will not affect the lawfulness of processing conducted before withdrawal.</p>
      <p><strong>Contractual Necessity (Section 11(1)(b)):</strong> Processing necessary for the performance of a contract to which the data subject is a party, or to take steps at the request of the data subject prior to entering into a contract.</p>
      <p><strong>Legal Obligation (Section 11(1)(c)):</strong> Processing necessary for compliance with a legal obligation to which VeriGate is subject, including obligations under FICA, the Companies Act, and sector-specific regulations.</p>
      <p><strong>Legitimate Interest (Section 11(1)(f)):</strong> Processing necessary for the pursuit of the legitimate interests of VeriGate or a third party to whom the information is supplied, provided that the interests of the data subject do not override those legitimate interests. This includes fraud prevention, platform security, and service improvement.</p>
      <p>Where we process special personal information (such as criminal records or biometric data), we do so based on explicit consent or as authorised by law, in accordance with POPIA Sections 26-33.</p>
    `,
  },
  {
    id: "data-sharing",
    title: "Data Sharing",
    content: `
      <p>We share personal information with the following categories of third parties, strictly on a need-to-know basis and in accordance with POPIA requirements:</p>
      <p><strong>Government and Regulatory Data Sources:</strong> We query official databases to perform verification checks, including the Department of Home Affairs (DHA) for identity verification, the South African Police Service (SAPS) for criminal record checks, the South African Qualifications Authority (SAQA) for qualification verification, and various professional registration bodies (HPCSA, SANC, ECSA, SAICA).</p>
      <p><strong>Credit Bureaus:</strong> We access credit bureau data from TransUnion SA, Experian SA, and XDS for credit screening services, in compliance with the National Credit Act (NCA).</p>
      <p><strong>Service Providers:</strong> We engage trusted service providers who assist us in operating our platform, including cloud hosting providers, communication service providers, and analytics partners. All service providers are contractually bound to process personal information only on our instructions and in accordance with POPIA.</p>
      <p><strong>Our Clients:</strong> Verification results are shared with the client who requested the screening, in accordance with the consent provided by the data subject.</p>
      <p><strong>Legal Requirements:</strong> We may disclose personal information where required by law, regulation, legal process, or enforceable governmental request, including requests from the South African Revenue Service (SARS), the Financial Intelligence Centre (FIC), and law enforcement agencies.</p>
      <p>We do not sell personal information to third parties. Where personal information is transferred outside of South Africa, we ensure adequate safeguards are in place as required by POPIA Section 72.</p>
    `,
  },
  {
    id: "data-retention",
    title: "Data Retention",
    content: `
      <p>We retain personal information only for as long as necessary to fulfil the purposes for which it was collected, or as required by law:</p>
      <p><strong>Verification Records:</strong> Verification results and supporting documentation are retained for a minimum of 5 years from the date of the verification, in line with FICA record-keeping requirements and general commercial practice in South Africa.</p>
      <p><strong>Account Information:</strong> Client account information is retained for the duration of the business relationship and for 5 years thereafter.</p>
      <p><strong>Consent Records:</strong> Records of consent are retained for the period of the consent plus 5 years, to demonstrate compliance with POPIA.</p>
      <p><strong>Usage Data:</strong> Website usage data and analytics are retained for a maximum of 24 months.</p>
      <p><strong>Deletion on Request:</strong> You may request the deletion of your personal information at any time, subject to our legal and regulatory retention obligations. Where we are required by law to retain certain information, we will inform you of the applicable retention period and restrict processing to storage only.</p>
      <p>When personal information is no longer required, it is securely destroyed or de-identified in accordance with POPIA Section 14 and our internal data destruction procedures.</p>
    `,
  },
  {
    id: "your-rights",
    title: "Your Rights Under POPIA",
    content: `
      <p>As a data subject under POPIA, you have the following rights:</p>
      <p><strong>Right of Access (Section 23):</strong> You have the right to request access to the personal information we hold about you, including confirmation of whether we process your information and a description of the information held.</p>
      <p><strong>Right to Correction (Section 24):</strong> You have the right to request the correction or deletion of personal information that is inaccurate, irrelevant, excessive, out of date, incomplete, misleading, or obtained unlawfully.</p>
      <p><strong>Right to Deletion (Section 24):</strong> You may request the deletion of your personal information where it is no longer necessary for the purpose for which it was collected, subject to any legal retention obligations.</p>
      <p><strong>Right to Object (Section 11(3)):</strong> You have the right to object to the processing of your personal information on reasonable grounds, unless legislation provides for such processing.</p>
      <p><strong>Right to Data Portability:</strong> Where technically feasible, you may request that your personal information be transmitted to another responsible party in a structured, commonly used, and machine-readable format.</p>
      <p><strong>Right to Complain to the Information Regulator:</strong> If you believe that your personal information has been processed in violation of POPIA, you have the right to lodge a complaint with the Information Regulator of South Africa:</p>
      <p>Information Regulator (South Africa)<br />
      JD House, 27 Stiemens Street, Braamfontein, Johannesburg, 2001<br />
      Email: complaints.IR@justice.gov.za<br />
      Tel: +27 (0)10 023 5207</p>
      <p>To exercise any of these rights, please contact our Information Officer using the details provided in Section 8 below.</p>
    `,
  },
  {
    id: "contact-us",
    title: "Contact Us",
    content: `
      <p>If you have any questions about this Privacy Policy, wish to exercise your rights under POPIA, or need to report a data breach or privacy concern, please contact our Information Officer:</p>
      <p><strong>Information Officer</strong><br />
      VeriGate (Pty) Ltd<br />
      Email: <a href="mailto:info@verigate.co.za" class="text-primary hover:underline">info@verigate.co.za</a><br />
      Phone: +27 (0)21 555 0100<br />
      Address: 4th Floor, The Foundry, 68 Sir Lowry Road, Woodstock, Cape Town, 7925, South Africa</p>
      <p>We will respond to all requests and enquiries within 30 days of receipt, as required by POPIA. If we require an extension, we will notify you of the reason and the expected timeframe.</p>
      <p>For urgent data breach notifications, please email <a href="mailto:info@verigate.co.za" class="text-primary hover:underline">info@verigate.co.za</a> with the subject line "URGENT: Data Breach Notification".</p>
    `,
  },
];

const Privacy = () => {
  return (
    <LegalPageTemplate
      title="Privacy Policy"
      lastUpdated="1 April 2026"
      sections={privacySections}
    />
  );
};

export default Privacy;
