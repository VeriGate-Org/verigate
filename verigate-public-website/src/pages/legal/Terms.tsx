import LegalPageTemplate from "@/components/templates/LegalPageTemplate";

const termsSections = [
  {
    id: "acceptance-of-terms",
    title: "Acceptance of Terms",
    content: `
      <p>By accessing or using the VeriGate platform, website, APIs, or any services provided by VeriGate (Pty) Ltd ("VeriGate", "we", "us", or "our"), you ("Client", "you", or "your") agree to be bound by these Terms of Service ("Terms").</p>
      <p>If you are entering into these Terms on behalf of a company or other legal entity, you represent that you have the authority to bind such entity to these Terms. If you do not have such authority, or if you do not agree with these Terms, you must not accept these Terms and may not use the Services.</p>
      <p>We reserve the right to update or modify these Terms at any time. We will notify you of any material changes by email or through a prominent notice on our platform. Your continued use of the Services after such changes constitutes acceptance of the updated Terms.</p>
    `,
  },
  {
    id: "service-description",
    title: "Service Description",
    content: `
      <p>VeriGate provides cloud-based background screening and verification services for South African organisations ("Services"). Our Services include, but are not limited to:</p>
      <p><strong>Identity Verification:</strong> Verification of South African identities against the Department of Home Affairs (DHA) population register and other authoritative databases.</p>
      <p><strong>Criminal Record Checks:</strong> Screening against South African Police Service (SAPS) criminal record databases and court records.</p>
      <p><strong>Qualification Verification:</strong> Verification of academic qualifications through SAQA, universities, TVET colleges, and professional registration bodies.</p>
      <p><strong>Credit Screening:</strong> Credit bureau checks through TransUnion SA, Experian SA, and XDS, subject to National Credit Act (NCA) compliance.</p>
      <p><strong>Employment History Verification:</strong> Verification of employment history, job titles, and dates of employment through direct employer contact.</p>
      <p><strong>Reference Checks:</strong> Professional and character reference verification through structured interviews and questionnaires.</p>
      <p><strong>Document Verification:</strong> Authentication of identity documents, certificates, and supporting documentation using AI and manual review.</p>
      <p>We provide these Services through our web-based platform, RESTful APIs, and associated tools. Service availability and specific check types may vary and are subject to the plan selected by the Client.</p>
    `,
  },
  {
    id: "account-registration",
    title: "Account Registration",
    content: `
      <p>To access our Services, you must register for an account. During registration, you agree to:</p>
      <p><strong>Accurate Information:</strong> Provide accurate, current, and complete information during the registration process and keep your account information up to date at all times.</p>
      <p><strong>Account Security:</strong> Maintain the confidentiality of your account credentials, including your password and API keys. You are responsible for all activities that occur under your account, whether or not authorised by you.</p>
      <p><strong>Authorised Users:</strong> Ensure that all individuals who access the Services through your account are authorised to do so and comply with these Terms.</p>
      <p><strong>Notification of Breach:</strong> Notify VeriGate immediately of any unauthorised use of your account or any other breach of security by contacting us at <a href="mailto:info@verigate.co.za" class="text-primary hover:underline">info@verigate.co.za</a>.</p>
      <p>VeriGate reserves the right to suspend or terminate accounts that contain false or inaccurate information, or that are used in violation of these Terms.</p>
    `,
  },
  {
    id: "fees-and-payment",
    title: "Fees and Payment",
    content: `
      <p>All fees for our Services are denominated in South African Rand (ZAR) and are exclusive of VAT unless otherwise stated.</p>
      <p><strong>Pricing:</strong> Current pricing is published on our website at <a href="/pricing" class="text-primary hover:underline">verigate.co.za/pricing</a>. VeriGate reserves the right to change pricing with 30 days' written notice. Changes will not affect Services already ordered and paid for.</p>
      <p><strong>Payment Terms:</strong> Invoices are payable within 30 days of the invoice date unless otherwise agreed in writing. Overdue amounts will accrue interest at the rate prescribed by the Prescribed Rate of Interest Act, 1975 (Act No. 55 of 1975).</p>
      <p><strong>Prepaid Credits:</strong> Where Services are purchased on a prepaid credit basis, credits must be used within 12 months of purchase. Unused credits are non-refundable after 12 months unless otherwise agreed.</p>
      <p><strong>Overages:</strong> If you exceed your plan's included verification volume, additional verifications will be charged at the applicable per-check rate as specified in your plan documentation. You will be notified when you approach your plan limits.</p>
      <p><strong>Taxes:</strong> You are responsible for all applicable taxes, including VAT at the prevailing rate. VeriGate is a registered VAT vendor and will issue tax invoices in accordance with the Value-Added Tax Act, 1991.</p>
    `,
  },
  {
    id: "acceptable-use",
    title: "Acceptable Use",
    content: `
      <p>You agree to use the Services only for lawful purposes and in compliance with all applicable South African laws and regulations. You shall not:</p>
      <p><strong>Prohibited Uses:</strong></p>
      <ul>
        <li>Use the Services to discriminate against any person on the basis of race, gender, sex, pregnancy, marital status, ethnic or social origin, colour, sexual orientation, age, disability, religion, conscience, belief, culture, language, or birth, in violation of the Constitution of the Republic of South Africa or the Employment Equity Act.</li>
        <li>Submit verification requests without the valid, informed consent of the data subject as required by POPIA.</li>
        <li>Use verification results for purposes other than those for which consent was obtained.</li>
        <li>Attempt to access, tamper with, or use non-public areas of the platform, VeriGate's computer systems, or the technical delivery systems of VeriGate's providers.</li>
        <li>Reverse engineer, decompile, or disassemble any aspect of the Services.</li>
        <li>Use the Services to conduct any form of stalking, harassment, or intimidation.</li>
        <li>Share, resell, or redistribute verification results or API access to third parties without VeriGate's prior written consent.</li>
      </ul>
      <p><strong>Compliance with Laws:</strong> You are responsible for ensuring that your use of the Services complies with all applicable laws, including but not limited to POPIA, FICA, the NCA, and the Employment Equity Act.</p>
    `,
  },
  {
    id: "intellectual-property",
    title: "Intellectual Property",
    content: `
      <p><strong>VeriGate IP:</strong> The Services, including the platform, APIs, documentation, software, algorithms, user interfaces, designs, trademarks, and all related intellectual property, are and shall remain the exclusive property of VeriGate (Pty) Ltd. Nothing in these Terms grants you any right, title, or interest in VeriGate's intellectual property except the limited right to use the Services as described herein.</p>
      <p><strong>Client Data:</strong> You retain all ownership rights in the data you submit to the Services ("Client Data"). By submitting Client Data, you grant VeriGate a limited, non-exclusive licence to process such data solely for the purpose of providing the Services.</p>
      <p><strong>Verification Results:</strong> Verification results generated by VeriGate are provided for your use in accordance with these Terms. You may use verification results for your internal business purposes and as required by law, but may not resell or redistribute results without our prior written consent.</p>
      <p><strong>Feedback:</strong> If you provide suggestions, ideas, or feedback about the Services, VeriGate may use such feedback without any obligation to compensate you.</p>
    `,
  },
  {
    id: "data-protection",
    title: "Data Protection",
    content: `
      <p>VeriGate processes personal information in accordance with the Protection of Personal Information Act, 2013 (POPIA). Our full <a href="/privacy" class="text-primary hover:underline">Privacy Policy</a> sets out how we collect, use, store, and protect personal information.</p>
      <p><strong>Roles and Responsibilities:</strong> In the context of our Services, VeriGate acts as an operator (as defined in POPIA Section 1) processing personal information on behalf of the Client, who is the responsible party. Both parties agree to comply with their respective obligations under POPIA.</p>
      <p><strong>Data Processing Agreement:</strong> By using the Services, you agree to the terms of VeriGate's Data Processing Agreement, which forms part of these Terms and governs the processing of personal information by VeriGate on your behalf.</p>
      <p><strong>Security Measures:</strong> VeriGate implements appropriate technical and organisational measures to secure personal information against loss, damage, or unauthorised access, in accordance with POPIA Section 19. These measures include encryption at rest and in transit, access controls, regular security audits, and incident response procedures.</p>
      <p><strong>Data Breach Notification:</strong> In the event of a data breach that compromises personal information, VeriGate will notify the Client without unreasonable delay, and cooperate with the Client in notifying the Information Regulator and affected data subjects as required by POPIA Section 22.</p>
    `,
  },
  {
    id: "limitation-of-liability",
    title: "Limitation of Liability",
    content: `
      <p>To the maximum extent permitted by South African law:</p>
      <p><strong>Liability Cap:</strong> VeriGate's total cumulative liability to you for all claims arising out of or relating to these Terms or the Services shall not exceed the total fees paid by you to VeriGate in the twelve (12) months immediately preceding the event giving rise to the claim.</p>
      <p><strong>Exclusion of Consequential Damages:</strong> In no event shall VeriGate be liable for any indirect, incidental, special, consequential, or punitive damages, including but not limited to loss of profits, data, business opportunities, or goodwill, whether based on contract, delict, strict liability, or any other legal theory, even if VeriGate has been advised of the possibility of such damages.</p>
      <p><strong>Third-Party Data Sources:</strong> VeriGate's verification results are based on data obtained from third-party sources, including government databases, credit bureaus, and educational institutions. VeriGate does not guarantee the accuracy, completeness, or timeliness of data provided by these sources. VeriGate shall not be liable for any inaccuracies in verification results attributable to errors in third-party source data.</p>
      <p><strong>No Guarantee of Outcomes:</strong> VeriGate does not guarantee any particular outcome from the use of verification results in your hiring, onboarding, or business decisions. The Client is solely responsible for decisions made based on verification results.</p>
      <p><strong>Consumer Protection:</strong> Nothing in these Terms is intended to limit or exclude liability that cannot be limited or excluded under the Consumer Protection Act, 2008 (Act No. 68 of 2008) or any other mandatory provision of South African law.</p>
    `,
  },
  {
    id: "indemnification",
    title: "Indemnification",
    content: `
      <p>You agree to indemnify, defend, and hold harmless VeriGate (Pty) Ltd, its directors, officers, employees, agents, and affiliates from and against any and all claims, damages, losses, liabilities, costs, and expenses (including reasonable legal fees) arising out of or related to:</p>
      <ul>
        <li>Your use of the Services in violation of these Terms or applicable law.</li>
        <li>Your failure to obtain valid consent from data subjects before submitting verification requests.</li>
        <li>Any decision you make based on verification results provided by VeriGate.</li>
        <li>Your violation of any third-party rights, including privacy rights under POPIA.</li>
        <li>Any claim by a data subject arising from your processing of their personal information in connection with VeriGate's Services.</li>
      </ul>
      <p>This indemnification obligation shall survive the termination of these Terms and your use of the Services.</p>
    `,
  },
  {
    id: "termination",
    title: "Termination",
    content: `
      <p><strong>Termination by Either Party:</strong> Either party may terminate these Terms by providing 30 days' written notice to the other party. Notice must be sent by email to the address on record.</p>
      <p><strong>Termination for Cause:</strong> Either party may terminate these Terms immediately upon written notice if the other party: (a) materially breaches these Terms and fails to cure such breach within 14 days of receiving notice; (b) becomes insolvent, is placed under business rescue, or is liquidated; or (c) ceases to carry on business.</p>
      <p><strong>Effect of Termination:</strong> Upon termination:</p>
      <ul>
        <li>Your access to the Services will be suspended and your account will be deactivated.</li>
        <li>Any outstanding fees for Services rendered prior to termination shall become immediately due and payable.</li>
        <li>You may request a copy of your Client Data within 30 days of termination. After this period, VeriGate may delete your Client Data in accordance with our data retention policies.</li>
        <li>VeriGate will retain verification records for the minimum period required by applicable law (typically 5 years under FICA).</li>
      </ul>
      <p><strong>Data Return and Deletion:</strong> Upon termination, VeriGate will, at your election, return or securely delete your Client Data, except where retention is required by law. We will provide a written confirmation of data deletion upon request.</p>
    `,
  },
  {
    id: "governing-law",
    title: "Governing Law",
    content: `
      <p>These Terms shall be governed by and construed in accordance with the laws of the Republic of South Africa, without regard to its conflict of law provisions.</p>
      <p><strong>Jurisdiction:</strong> Any disputes arising out of or relating to these Terms or the Services shall be subject to the exclusive jurisdiction of the Western Cape Division of the High Court of South Africa, Cape Town.</p>
      <p><strong>Dispute Resolution:</strong> Before initiating any legal proceedings, the parties agree to attempt to resolve any dispute through good-faith negotiation for a period of not less than 30 days. If the dispute cannot be resolved through negotiation, either party may refer the matter to mediation administered by the Arbitration Foundation of Southern Africa (AFSA) before proceeding to litigation.</p>
      <p><strong>Severability:</strong> If any provision of these Terms is found to be invalid or unenforceable by a court of competent jurisdiction, such provision shall be severed from these Terms and the remaining provisions shall continue in full force and effect.</p>
      <p><strong>Entire Agreement:</strong> These Terms, together with the Privacy Policy, Cookie Policy, and any applicable Data Processing Agreement, constitute the entire agreement between you and VeriGate with respect to the Services and supersede all prior or contemporaneous agreements, representations, and understandings.</p>
      <p>For any questions regarding these Terms of Service, please contact us at <a href="mailto:info@verigate.co.za" class="text-primary hover:underline">info@verigate.co.za</a>.</p>
    `,
  },
];

const Terms = () => {
  return (
    <LegalPageTemplate
      title="Terms of Service"
      lastUpdated="1 April 2026"
      sections={termsSections}
    />
  );
};

export default Terms;
