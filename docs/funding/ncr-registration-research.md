# NCR Registration Research for VeriGate

**Date:** 7 April 2026
**Purpose:** Research into National Credit Regulator (NCR) registration requirements for VeriGate, a real-time risk intelligence platform that needs to access credit bureau data for verification, conflict of interest detection, and risk scoring purposes (not lending).
**Legislation:** National Credit Act No. 34 of 2005 (NCA), as amended by the National Credit Amendment Act No. 19 of 2014.

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Registration Categories -- Which One Applies to VeriGate?](#2-registration-categories--which-one-applies-to-verigate)
3. [Full Registration Process (Step-by-Step)](#3-full-registration-process-step-by-step)
4. [Required Documents and Prerequisites](#4-required-documents-and-prerequisites)
5. [Registration Fees and Costs](#5-registration-fees-and-costs)
6. [Processing Timeline](#6-processing-timeline)
7. [Credit Bureaus in South Africa](#7-credit-bureaus-in-south-africa)
8. [Ongoing Compliance Requirements](#8-ongoing-compliance-requirements)
9. [Alternatives -- Accessing Credit Data Through an Intermediary](#9-alternatives--accessing-credit-data-through-an-intermediary)
10. [Recent Regulatory Changes (2024-2026)](#10-recent-regulatory-changes-2024-2026)
11. [NCR Contact Details](#11-ncr-contact-details)
12. [Prescribed Reasons for Credit Bureau Enquiries](#12-prescribed-reasons-for-credit-bureau-enquiries)
13. [Recommendation for VeriGate](#13-recommendation-for-verigate)
14. [Sources](#14-sources)

---

## 1. Executive Summary

VeriGate intends to access credit bureau data as part of a verification gateway service -- not as a credit provider or lender. Under the National Credit Act, any entity that accesses, resells, or distributes consumer credit information must be registered with the NCR as a **credit bureau**. Since VeriGate will not maintain its own credit database but will pull data from existing registered credit bureaus, the most appropriate registration category is **Reseller Credit Bureau**.

There are two strategic paths available:

1. **Register directly with the NCR as a Reseller Credit Bureau** (Section 43 of the NCA) -- full autonomy, direct bureau relationships, but heavier regulatory and compliance burden.
2. **Partner with an already-registered intermediary** (e.g., Datanamix, MarisIT, Gathr, OmniCheck) -- faster time-to-market, lower compliance overhead, but introduces a dependency and reduces margin.

A hybrid approach is recommended: **start with an intermediary for MVP/launch, then pursue direct NCR registration in parallel** to gain full autonomy over time.

---

## 2. Registration Categories -- Which One Applies to VeriGate?

The NCR registers four types of entities:

| Entity Type | Section of NCA | Who This Applies To |
|---|---|---|
| **Credit Provider** | Section 40 | Entities that extend credit (loans, financing, etc.) |
| **Credit Bureau** | Section 43 | Entities that compile, maintain, and issue consumer credit reports |
| **Reseller Credit Bureau** | Section 43 (subset) | Entities that pull credit data from registered bureaus and resell/distribute it (do not maintain their own database) |
| **Debt Counsellor** | Section 44 | Individuals providing debt review services |

### VeriGate's Classification: Reseller Credit Bureau

VeriGate is **not** extending credit, so it does not need credit provider registration. Instead, VeriGate will be pulling consumer credit information from registered full-member credit bureaus and presenting it as part of its verification service. This activity falls squarely under the definition of a **credit bureau** per the NCA:

> An entity qualifies to register as a credit bureau if it is "engaged in the business of receiving reports or investigating credit applications, credit agreements, payment history or patterns, or consumer credit information relating to consumers or prospective consumers."

The NCA permits entities that pull credit information from existing registered credit bureaus (rather than maintaining their own databases) to register as **reseller credit bureaus**. Reseller credit bureaus are subject to a **lighter-touch regulatory regime** and are exempt from certain obligations imposed on full credit bureaus.

**Key distinction:** A reseller credit bureau does not compile or maintain its own consumer data -- it accesses and redistributes data from full-member bureaus under licence.

---

## 3. Full Registration Process (Step-by-Step)

### Step 1: Determine Eligibility and Prepare the Business

- Ensure the entity is properly registered with CIPC (Companies and Intellectual Property Commission).
- Confirm all directors/shareholders have clean criminal records (no fraud, theft, or dishonesty convictions).
- Establish a compliant IT infrastructure for handling consumer credit data (encryption, access controls, audit trails).
- Ensure POPIA (Protection of Personal Information Act) compliance framework is in place.

### Step 2: Obtain Required Documents

Gather all documentation listed in Section 4 below. Key items include criminal clearance certificates (which can take 2-4 weeks to obtain from SAPS) and the signed bank confirmation letter.

### Step 3: Complete NCR Form 5

Download and complete **Form 5 -- Application for Registration as Credit Bureau** from the NCR website:
- URL: https://www.ncr.org.za/act/list-of-forms
- Direct PDF: https://www.ncr.org.za/documents/pages/registration&compliance/credit_bureau/form%205.pdf

The form requires:
- Full entity details (name, registration number, physical/postal address)
- Nature of credit bureau activities to be conducted
- Details of all directors, members, shareholders, trustees
- Declaration of compliance with the NCA

### Step 4: Pay the Application Fee

Pay the non-refundable application fee of **R550** to the NCR. Payment details are provided on the application form.

### Step 5: Submit the Application

Submit the completed Form 5 along with all supporting documents to:

**Physical delivery:**
127 15th Road, Randjespark, Midrand, 1683

**Postal:**
PO Box 209, Halfway House, 1685

### Step 6: NCR Assessment

The NCR will:
- Review the application for completeness
- Verify all supporting documentation
- Conduct background checks on directors/shareholders
- Assess the applicant's technical and operational capability
- May request additional information (applicant has **15 business days** to respond per Section 45(2))

### Step 7: Registration Decision

The NCR issues a registration certificate with conditions of registration, or provides written reasons for refusal. Once registered, VeriGate will appear on the NCR's Register of Registrants.

### Step 8: Negotiate Bureau Access Agreements

After registration, negotiate and sign service level agreements (SLAs) with the full-member credit bureaus (TransUnion, Experian, XDS, etc.) to gain API access to their data.

---

## 4. Required Documents and Prerequisites

### For a Company (Pty Ltd):

| Document | Notes |
|---|---|
| Completed and signed **Form 5** | Application for Registration as Credit Bureau |
| CIPC registration certificate | COR14.3 or COR15.1 |
| Company share certificate(s) | If applicable |
| Certified copies of ID/passport | For **all** directors, shareholders, members, trustees |
| Criminal clearance certificates | For **all** directors/shareholders; must be less than 6 months old |
| Signed and stamped bank confirmation letter | From the company's bank, confirming banking details |
| SARS tax registration confirmation | Proof of registration with SARS |
| Company letterhead | Specimen letterhead |
| High-level organogram | Showing CEO and first level of senior management |
| Service level agreements (SLAs) | With key contractors/outsourced service providers |
| NCR-specific resolution | Signed resolution authorising the NCR application |
| Proof of physical address | For the business premises |
| POPIA compliance documentation | Privacy policy, consent mechanisms, data processing agreements |

### Additional Requirements for Credit Bureaus Specifically:

- Description of the IT systems and infrastructure used to store/process credit data
- Data security and access control measures
- Description of the credit bureau activities to be conducted
- Details of the credit bureaus from which data will be sourced (for resellers)
- Consumer complaints handling procedures

---

## 5. Registration Fees and Costs

### Credit Bureau Registration Fees

| Fee Type | Amount | Notes |
|---|---|---|
| **Application fee** | R550 | Non-refundable, payable on submission |
| **Registration fee** | R10,000 base | Plus R5 per 1,000 consumer credit enquiries |
| **Maximum registration fee** | R210,000 | Cap on registration fee |
| **Branch fee** | R250 per branch | Per physical branch location |
| **Annual renewal fee** | Varies | Due by **31 July** each year |

**Note:** The registration fee calculation for credit bureaus is based on the number of consumer credit enquiries for the past 12-month period, as certified by the CEO of the applicant. For a new entity with no history, the base fee of R10,000 would apply initially.

### Comparison with Credit Provider Fees

Credit provider registration uses a different 9-category fee structure based on principal debt extended, ranging from R1,000 (Category 9, <R250K debt) to R330,000 (Category 1, >R15 billion). This is not applicable to VeriGate since it is not extending credit.

### Budget Estimate for VeriGate

| Item | Estimated Cost |
|---|---|
| Application fee | R550 |
| Initial registration fee | R10,000 |
| Criminal clearance certificates (3-4 directors) | R100-200 each |
| Legal/compliance advisory | R10,000-30,000 |
| IT infrastructure compliance setup | R20,000-50,000 |
| **Total estimated initial cost** | **R40,000-90,000** |

---

## 6. Processing Timeline

| Phase | Estimated Duration |
|---|---|
| Document preparation (criminal clearances, bank letters, etc.) | 2-4 weeks |
| NCR application review and processing | **8-12 weeks** |
| Additional information requests (if any) | +2-4 weeks |
| Bureau SLA negotiations (post-registration) | 4-8 weeks |
| Technical API integration with bureaus | 4-8 weeks |
| **Total end-to-end (registration to live)** | **5-8 months** |

**Important notes:**
- The 8-12 week processing time by the NCR assumes complete and accurate documentation is submitted. Incomplete applications will result in delays.
- The NCR may request additional information at any point, and the applicant has 15 business days to respond.
- Bureau SLA negotiations run in parallel with the later stages of NCR registration.

---

## 7. Credit Bureaus in South Africa

### Credit Bureau Association (CBA) Members

The CBA categorises credit bureaus into three tiers:

#### Full Member Credit Bureaus (Primary Data Holders)

| Bureau | Notes |
|---|---|
| **Experian South Africa** | One of the "Big 4"; acquired Compuscan in 2019 |
| **TransUnion South Africa** | One of the "Big 4"; major consumer and commercial data |
| **XDS (Xpert Decision Systems)** | Full member; growing market share |
| **VeriCred** | Full member; consumer credit data |
| **Consumer Profile Bureau (CPB)** | Full member |

#### Intermediate Credit Bureau Members

| Bureau | Notes |
|---|---|
| **MIE (Managed Integrity Evaluation)** | Employment and background screening |
| **Tenant Profile Network (TPN)** | Rental/tenant credit data |

#### Reseller Credit Bureau Members

| Bureau | Notes |
|---|---|
| **LexisNexis Risk Management Services** | Legal and risk data |
| **SearchWorks 360** | Verification and search services |
| **Credit IT Data Risk Management Solution** | Risk management |
| **Africa Credit Bureau** | Pan-African credit data |

**Note on Compuscan:** Experian acquired Compuscan in 2019. Compuscan no longer operates independently but its data is accessible through Experian's platforms.

### Key Bureaus for VeriGate's Use Case

For a verification gateway service, the most relevant bureaus to integrate with would be:
1. **TransUnion** -- largest consumer database in SA
2. **Experian** (including former Compuscan data) -- comprehensive consumer and commercial data
3. **XDS** -- growing alternative with competitive pricing
4. **CPB** -- additional coverage for consumer profiles

---

## 8. Ongoing Compliance Requirements

Once registered as a credit bureau with the NCR, VeriGate must comply with the following on an ongoing basis:

### Annual Obligations

| Obligation | Details |
|---|---|
| **Annual renewal fee** | Must be paid by **31 July** each year, regardless of registration date |
| **Annual compliance report** | Must be submitted to the NCR within 6 months of financial year-end |
| **Audit of consumer credit enquiries** | CEO must certify the number of enquiries for fee calculation |

### Data and Consumer Protection Obligations

| Obligation | Details |
|---|---|
| **Data accuracy** | Must ensure all consumer data accessed and distributed is accurate |
| **Error correction** | Must remove or correct inaccurate information immediately, at no cost to the consumer, upon complaint |
| **Consumer consent** | Must obtain and retain proof of written consumer consent before accessing credit data (per NCA and POPIA) |
| **Prescribed reasons** | Every credit enquiry must be for a prescribed reason under the NCA |
| **POPIA compliance** | Full compliance with the Protection of Personal Information Act -- data minimisation, purpose limitation, retention limits, security safeguards |
| **FICA compliance** | Where applicable, Financial Intelligence Centre Act compliance for KYC/AML |

### Regulatory Obligations

| Obligation | Details |
|---|---|
| **NCR compliance notices** | Must respond to and comply with any compliance notices issued by the NCR |
| **Record keeping** | Must maintain records of all credit enquiries, consents, and data access |
| **Consumer complaints** | Must maintain a complaints handling procedure and resolve complaints within prescribed timeframes |
| **NCR inspections** | Must cooperate with NCR inspections and provide access to records |

### Penalties for Non-Compliance

- The NCR can issue compliance notices.
- Failure to comply can result in **civil liability** (fines) or **criminal liability** (fines and/or imprisonment).
- Non-payment of renewal fees can result in **deregistration**.
- Operating as an unregistered credit bureau renders any agreements **unlawful and unenforceable** per Section 89 of the NCA.

---

## 9. Alternatives -- Accessing Credit Data Through an Intermediary

VeriGate does not necessarily need to register directly with the NCR to access credit bureau data. Several **already-registered intermediaries** offer API access to credit bureau data, allowing VeriGate to operate without its own NCR registration (at least initially).

### Option A: Partner with a Registered Reseller/Intermediary

#### Datanamix (formerly PBVerify)
- **NCR Status:** Registered reseller credit bureau (Section 43 of the NCA)
- **Bureau Access:** TransUnion, Experian, XDS
- **Services:** Consumer credit reports, ID verification, bank account verification, AML screening, business verification
- **Integration:** REST API and web portal
- **Compliance:** ISO 9001:2015 certified, ISO 27001 IT security, POPIA/FICA compliant
- **Onboarding:** Online registration, business validation, compliance checks; API credentials issued upon approval
- **Key Consideration:** Credit report API services **cannot be resold to third parties** and must be used for NCR-prescribed purposes
- **Website:** https://www.datanamix.com

#### MarisIT
- **NCR Status:** Registered credit bureau (NCRCB23)
- **Bureau Access:** Multiple SA credit bureaus
- **Services:** Credit scoring, vetting, qualification verification, vehicle verification
- **Integration:** Web-based reporting platform and API
- **Compliance:** ISO 27001:2022 certified
- **Website:** https://www.marisit.co.za

#### OmniCheck (by OmniSol)
- **NCR Status:** Registered reseller credit bureau with the Credit Bureau Association
- **Bureau Access:** All major credit bureaus, plus CIPC, SARS, DHA
- **Services:** ID verification, credit checks, fraud prevention, company searches
- **Integration:** RESTful API over secure SSL
- **Website:** https://www.verifyid.co.za

#### Gathr
- **NCR Status:** Operates through partnerships with registered credit bureaus (e.g., TransUnion)
- **Bureau Access:** TransUnion and others via partnerships
- **Services:** Credit bureau checks, affordability assessments, ID verification, bank account verification
- **Integration:** API-first platform with modular architecture
- **Onboarding:** Demo > Contract > Technical onboarding > UAT > Go-live
- **Website:** https://gathrdocs.com

#### Standard Bank OneHub API Marketplace
- **Bureau Access:** CompuScan (Experian), Experian Sigma, TransUnion, VeriCred, XDS
- **Services:** Company and consumer credit checks via API
- **Key Consideration:** Requires Standard Bank business relationship
- **Website:** https://corporateandinvestment.standardbank.com

### Option B: Direct NCR Registration as Reseller Credit Bureau

See Sections 3-6 above for the full registration process. This gives VeriGate full control and better margins but requires 5-8 months and ongoing compliance investment.

### Comparison Table

| Factor | Intermediary Partner | Direct NCR Registration |
|---|---|---|
| **Time to market** | 2-4 weeks | 5-8 months |
| **Upfront cost** | Minimal (API fees) | R40,000-90,000 |
| **Ongoing cost** | Per-enquiry fees (higher per unit) | Annual renewal + lower per-unit bureau fees |
| **Compliance burden** | Low (intermediary handles) | High (full NCR compliance) |
| **Control over data** | Limited | Full |
| **Bureau relationships** | Indirect | Direct |
| **Margin per transaction** | Lower | Higher |
| **Risk of dependency** | High (single point of failure) | Low |
| **Scalability** | Good initially, expensive at volume | Better unit economics at scale |

---

## 10. Recent Regulatory Changes (2024-2026)

### National Credit Amendment Act (2019, effective ongoing)

The National Credit Amendment Act No. 7 of 2019 introduced debt intervention for low-income consumers. While not directly impacting credit bureau registration, it expanded the NCR's mandate and increased scrutiny on data accuracy in credit bureau records.

### Draft Regulation Amendments (August 2025)

Published in **Government Gazette No. 53154** on 13 August 2025, the Department of Trade, Industry and Competition proposed significant amendments to NCA Regulations. Public comments were due by 13 September 2025. Key changes:

#### Regulation 18 -- Consumer Identification
- Strengthened requirements for consumer identification using ID numbers, passport numbers, or registration numbers
- Where standard identifiers are unavailable, credit bureaus must use "other reasonable identifiers"
- New provisions for small business credit applications

#### Regulation 19 -- Credit Bureau Data Scope (IMPORTANT FOR VERIGATE)
- **Expanded the types of data credit bureaus may collect**, now including payment histories for goods, services, and utilities
- Permitted data collection from **insurers, educational institutions, fraud investigation entities, and debt collectors**
- Mandated submission of personal details including names, ID/passport numbers, dates of birth, addresses, and employment information
- Required alignment with NCR guidelines on data format and quality

#### Regulation 23A -- Affordability Assessments
- New standardised minimum expense norms table based on income brackets
- Credit providers must evaluate discretionary income, financial means, and revenue flows
- Mandatory validation of gross income

### SACRRA Data Transmission Hub (DTH)

The NCR has designated the **SACRRA (South African Credit and Risk Reporting Association) Data Transmission Hub** as the prescribed mechanism for submitting credit information to authorised credit bureaus. Key requirements:
- Both monthly and daily files must be submitted
- Daily data submissions (opened/closed accounts) must be made within **48 hours**
- All data must be accurate and current at the time of submission

### Impact on VeriGate

The 2025 draft regulation changes are **positive for VeriGate's use case**:
- The expanded data scope means more verification data points will be available through credit bureaus
- The inclusion of employment information, utility payment histories, and fraud investigation data aligns well with a verification gateway's needs
- Credit bureau data combined with VeriGate's CIPC directorship cross-referencing strengthens the platform's **conflict of interest detection** capability -- credit exposure patterns, directorial overlaps, and beneficial ownership signals can be correlated across sources to flag undisclosed relationships in procurement and onboarding
- Tighter data quality standards will improve the reliability of credit bureau data for verification purposes

**Status:** As of April 2026, it should be confirmed whether these draft regulations have been finalised and gazetted as law. Contact the NCR or check the Government Gazette for the latest status.

---

## 11. NCR Contact Details

| Channel | Details |
|---|---|
| **Call Centre** | 0860 627 627 |
| **Reception** | 011 554 2700 |
| **General Enquiries** | info@ncr.org.za |
| **Complaints** | complaints@ncr.org.za |
| **Workshops** | workshops@ncr.org.za |
| **Physical Address** | 127 15th Road, Randjespark, Midrand, Johannesburg, 1685 |
| **Postal Address** | PO Box 209, Halfway House, 1685 |
| **Website** | https://www.ncr.org.za |
| **Twitter/X** | @NCRegulator |
| **Forms Download** | https://www.ncr.org.za/act/list-of-forms |
| **Register of Registrants** | https://www.ncr.org.za/register_of_registrants/ |

### Draft Regulation Comments Submission
- **Email:** credit@thedtic.gov.za
- **Physical:** Department of Trade, Industry and Competition offices, Pretoria

---

## 12. Prescribed Reasons for Credit Bureau Enquiries

Under the NCA, every credit bureau enquiry must have a **prescribed reason**. The following are the recognised prescribed purposes relevant to VeriGate's verification use case:

| Prescribed Reason | Consent Required? | Relevance to VeriGate |
|---|---|---|
| **Credit risk assessment** (Section 81(2)) | Yes | If verifying creditworthiness |
| **Insurance application** | Yes | If verifying for insurance purposes |
| **Employment (positions requiring trust/finances)** | Yes | Pre-employment verification |
| **Fraud detection and prevention** | Context-dependent | Core VeriGate use case |
| **Setting limit for goods/utility/service** | Yes | Verification for service providers |
| **Affordability/financial means assessment** | Yes | Financial verification |
| **Tracing by credit provider/agent** | No (within terms) | Skip tracing services |
| **Fraud, corruption, or theft investigation** | No (SAPS/enforcement) | Law enforcement support |

**Critical note for VeriGate:** Consumer consent must be **voluntary, informed, and specific** per POPIA. VeriGate's platform must implement robust consent collection and storage mechanisms. Each enquiry must be tagged with the applicable prescribed reason.

---

## 13. Recommendation for VeriGate

### Recommended Strategy: Phased Approach

#### Phase 1: MVP via Intermediary (Months 1-3)
- Partner with **Datanamix** or **OmniCheck** as the primary credit data source
- Integrate their REST API into VeriGate's platform
- Implement POPIA-compliant consent collection
- Begin serving customers with credit verification as part of VeriGate's gateway
- **Cost:** API integration costs + per-enquiry fees (pay-as-you-go)

#### Phase 2: NCR Registration (Months 1-8, parallel)
- Begin NCR registration process immediately (runs in parallel with Phase 1)
- Obtain criminal clearances, prepare documentation, complete Form 5
- Submit application and await NCR approval
- **Cost:** R40,000-90,000 for registration and compliance setup

#### Phase 3: Direct Bureau Integration (Months 6-10)
- Once NCR-registered, negotiate direct SLAs with TransUnion, Experian, and XDS
- Implement direct API integrations with each bureau
- Transition from intermediary to direct access (or maintain both for redundancy)
- **Cost:** API integration development + bureau setup fees

### Key Risks and Mitigations

| Risk | Mitigation |
|---|---|
| NCR registration takes longer than expected | Intermediary partnership allows VeriGate to operate while waiting |
| Intermediary restricts resale of credit data | Ensure VeriGate's use case qualifies as direct use (verification for own clients), not resale |
| Regulatory changes increase compliance burden | Monitor Government Gazette and NCR communications; engage a compliance advisor |
| Consumer consent challenges | Build robust consent management into VeriGate's platform from day one |
| Data breach liability | Implement ISO 27001-aligned security controls; consider cyber insurance |

### Regulatory Advisory

Given the complexity of NCA compliance, it is strongly recommended that VeriGate engage a **specialist NCR registration consultant** (such as Bonmas Consulting, Barnard Inc., or Masthead) to assist with the registration process and ongoing compliance. Typical consulting fees range from R10,000-30,000 for the registration process.

---

## 14. Sources

- [NCR Official Website](https://www.ncr.org.za)
- [NCR Register of Registrants -- Credit Bureaus](https://www.ncr.org.za/register_of_registrants/registered_cb1.php)
- [NCR List of Forms (Form 5)](https://www.ncr.org.za/act/list-of-forms)
- [NCR Requirements for Registration as a Credit Bureau (PDF)](https://ncr.org.za/documents/pages/compliance&reports_cp/Requirements%20for%20registration%20as%20a%20credit%20bureau%20final.pdf)
- [National Credit Act No. 34 of 2005](https://www.thedtic.gov.za/wp-content/uploads/National_Credit_Act34of2005.pdf)
- [Government Gazette -- Determination of Fees Regulations](https://www.gov.za/documents/national-credit-act-determination-application-registration-and-renewal-fees-regulations-11)
- [Barnard Inc. -- NCR Credit Registration Guide](https://barnardinc.co.za/2024/11/12/what-is-ncr-credit-registration-and-why-is-it-important-in-south-africa/)
- [Barnard Inc. -- How to Register as a Credit Provider](https://barnardinc.co.za/how-to-register-as-a-credit-provider/)
- [SME South Africa -- Register with the NCR](https://smesouthafrica.co.za/register-with-the-national-credit-regulator-ncr/)
- [Bonmas Consulting -- NCR Registration](https://bonmasconsulting.co.za/ncr-registration.php)
- [Mondaq -- NCR Credit Registration Importance](https://www.mondaq.com/southafrica/consumer-credit/1544142/what-is-ncr-credit-registration-and-why-is-it-important-in-south-africa)
- [Masthead -- Compliance for Credit Providers](https://www.masthead.co.za/compliance-for-credit-providers/)
- [Masthead -- Proposed NCA Regulation Amendments (Aug 2025)](https://www.masthead.co.za/newsletter/proposed-national-credit-act-regulations-aim-to-strengthen-consumer-protection-and-data-standards/)
- [Moonstone -- Draft NCA Regulations (Aug 2025)](https://www.moonstone.co.za/draft-nca-regulations-widen-credit-data-scope-and-tighten-affordability-tests/)
- [Afriwise -- Draft NCA Affordability Assessment Amendments](https://www.afriwise.com/blog/draft-amendments-to-the-affordability-assessment-regulations-under-the-national-credit-act)
- [Datanamix -- How It Works](https://www.datanamix.com/how-it-works/)
- [Datanamix -- API Overview](https://pbverify.freshdesk.com/support/solutions/articles/17000007324-data-api-overview)
- [MarisIT -- Credit Services South Africa](https://www.marisit.co.za/)
- [OmniCheck -- Verification Platform](https://www.verifyid.co.za/)
- [Gathr -- Credit Bureau Check Module](https://gathrdocs.com/unpacking-gathr-module-6-credit-bureau-check/)
- [Standard Bank OneHub -- Credit Checks API](https://corporateandinvestment.standardbank.com/cib/global/products-and-services/onehub/api-marketplace/consumer-credit-checks)
- [Credit Bureau Association -- Members](https://www.cba.co.za/members)
- [SACRRA -- About Us](https://sacrra.org.za/about-us/)
- [Cred-it-data -- Enquiry Reasons and Prescribed Purposes](https://creditdata.co.za/enquiryreasons/)
- [NCR Annual Report 2024/2025](https://www.thedtic.gov.za/wp-content/uploads/NCR-Annual-Report-2024_2025.pdf)
- [Arcadia Finance -- Credit Bureaus in South Africa](https://www.arcadiafinance.co.za/loans/credit-bureaus-in-south-africa/)
- [National Government -- NCR Overview](https://nationalgovernment.co.za/units/view/126/national-credit-regulator-ncr)

---

*This research document was compiled on 7 April 2026. Regulatory information should be verified with the NCR directly before making business decisions, as regulations and fees may have changed since the date of this research.*
