# ISO 27001 Certification Research for Arthmatic DevWorks (VeriGate)

**Prepared:** 7 April 2026
**Company:** Arthmatic DevWorks (Pty) Ltd
**Product:** VeriGate -- Real-Time Risk Intelligence Platform
**Employees:** < 20
**Location:** Cape Town, Western Cape, South Africa

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Why ISO 27001 for VeriGate](#2-why-iso-27001-for-verigate)
3. [Process Overview -- Decision to Certification](#3-process-overview--decision-to-certification)
4. [Accredited Certification Bodies in South Africa](#4-accredited-certification-bodies-in-south-africa)
5. [Consulting / Implementation Providers in Cape Town & SA](#5-consulting--implementation-providers-in-cape-town--sa)
6. [Cost Breakdown](#6-cost-breakdown)
7. [Timeline](#7-timeline)
8. [Provider Comparison Table](#8-provider-comparison-table)
9. [POPIA and ISO 27001 Overlap](#9-popia-and-iso-27001-overlap)
10. [Government Subsidies and Support](#10-government-subsidies-and-support)
11. [Tips for Small Tech Companies](#11-tips-for-small-tech-companies)
12. [Recommended Approach for Arthmatic DevWorks](#12-recommended-approach-for-arthmatic-devworks)
13. [Next Steps](#13-next-steps)
14. [Sources](#14-sources)

---

## 1. Executive Summary

VeriGate is a real-time risk intelligence platform handling highly sensitive personal and government data -- DHA identity checks, CIPC company verification, credit bureau lookups, SARS tax data, and AI-powered risk scoring. ISO/IEC 27001:2022 certification is essential for:

- **Market access**: Enterprise and government clients increasingly require ISO 27001 as a procurement prerequisite.
- **Regulatory alignment**: Supports POPIA compliance obligations.
- **Trust signal**: Demonstrates to data providers (DHA, SARS, credit bureaus) that Arthmatic DevWorks has a mature information security posture.
- **Competitive differentiation**: Few small SA fintech/regtech platforms hold ISO 27001.

For a company of Arthmatic DevWorks' size (< 20 employees), the estimated total cost for first-year certification is **R280,000 -- R550,000** (approximately $15,000 -- $30,000 USD), with annual maintenance of approximately **R80,000 -- R150,000** thereafter. The process typically takes **4 -- 8 months** from decision to certificate issuance.

---

## 2. Why ISO 27001 for VeriGate

| Factor | Relevance to VeriGate |
|--------|----------------------|
| Sensitive data handling | DHA identity numbers, CIPC company data, credit bureau records, SARS tax information, directorship and shareholding records used for conflict of interest detection |
| Conflict of interest detection | VeriGate cross-references CIPC directorships, shareholding percentages, property ownership, and PEP databases to detect undisclosed relationships -- this involves processing highly sensitive corporate governance and beneficial ownership data that demands rigorous security controls |
| Government data sources | DHA and SARS integrations require demonstrable security controls |
| Regulatory environment | POPIA requires "appropriate, reasonable technical and organisational measures" -- ISO 27001 is the gold standard for proving this |
| Enterprise sales | Banks, insurers, and corporates expect ISO 27001 from data-handling vendors |
| API security | VeriGate exposes APIs that handle PII at scale -- needs formal ISMS |
| Insurance | Cyber liability insurance premiums are often reduced with ISO 27001 |

---

## 3. Process Overview -- Decision to Certification

### Phase 1: Preparation (Months 1-2)

| Step | Description |
|------|-------------|
| **1. Management commitment** | Secure executive buy-in, appoint an ISMS lead (can be CTO or senior engineer), allocate budget |
| **2. Define ISMS scope** | For VeriGate: the verification gateway platform, its APIs, infrastructure, data processing, and supporting business processes. Keep scope tight -- only what is needed |
| **3. Gap analysis** | Engage a consultant or do internally -- assess current security posture against ISO 27001:2022 Annex A controls (93 controls across 4 categories: Organisational, People, Physical, Technological) |
| **4. Risk assessment** | Identify information assets, threats, vulnerabilities. Create risk register. Define risk treatment plan |

### Phase 2: Implementation (Months 2-5)

| Step | Description |
|------|-------------|
| **5. Develop ISMS documentation** | Information Security Policy, Risk Assessment Methodology, Statement of Applicability (SoA), risk treatment plan, procedures for access control, incident management, business continuity, etc. |
| **6. Implement controls** | Technical controls (encryption, access management, logging, vulnerability scanning), organisational controls (policies, training, vendor management), physical controls (office security) |
| **7. Staff awareness training** | All employees must understand the ISMS, their roles, and security procedures |
| **8. Internal audit** | Conduct a full internal audit against the standard. Identify non-conformities and address them |
| **9. Management review** | Formal management review of the ISMS -- required by the standard |

### Phase 3: Certification Audit (Months 5-7)

| Step | Description |
|------|-------------|
| **10. Stage 1 audit (Document review)** | Certification body reviews ISMS documentation, scope, risk assessment, SoA. Typically 1-2 days on-site or remote. Identifies any major gaps before Stage 2 |
| **11. Address Stage 1 findings** | Fix any issues identified. Typically 2-4 weeks between stages |
| **12. Stage 2 audit (Implementation audit)** | On-site assessment of whether the ISMS is actually implemented and operating. Auditors interview staff, review evidence, test controls. Typically 2-4 days for a small company |
| **13. Certificate issuance** | If no major non-conformities, certificate is issued. Minor non-conformities must have corrective action plans |

### Phase 4: Maintenance (Ongoing)

| Step | Description |
|------|-------------|
| **14. Surveillance audit -- Year 1** | Annual audit by the certification body (typically 1-2 days). Checks continued compliance |
| **15. Surveillance audit -- Year 2** | Second annual check |
| **16. Recertification audit -- Year 3** | Full recertification audit at end of 3-year cycle |

---

## 4. Accredited Certification Bodies in South Africa

These are the organisations that actually **issue** the ISO 27001 certificate. They must be accredited by a national accreditation body (SANAS in South Africa, or an international equivalent under the IAF MLA).

| Certification Body | Accreditation | SA Presence | Website | Notes |
|--------------------|--------------|-------------|---------|-------|
| **BSI (British Standards Institution)** | UKAS (UK), recognised by SANAS | Johannesburg office, services nationally | [bsigroup.com/en-ZA](https://www.bsigroup.com/en-ZA/) | Gold standard, premium pricing. 300+ auditors nationally. Well-known internationally -- strong signal for enterprise clients |
| **SGS South Africa** | SANAS accredited | Head office Woodmead, JHB. National coverage | [sgs.com/en-za](https://www.sgs.com/en-za) | Global leader in TIC (Testing, Inspection, Certification). Tel: +27 11 800 1000 |
| **Bureau Veritas South Africa** | SANAS accredited | National offices | [bureauveritas.co.za](https://www.bureauveritas.co.za/) | 125+ years in Africa. Strong reputation. ISO 27001 certification services across the continent |
| **DQS South Africa** | DAkkS (Germany) and SANAS dual accreditation | South Africa office | [dqsglobal.com/en-za](https://www.dqsglobal.com/en-za/) | Specialist in management system certification. Offers ISO 27001 Lead Implementer training |
| **SACAS (SA Certification & Auditing Services)** | SANAS accredited | Vanderbijlpark, Gauteng | [sacas.co.za](https://www.sacas.co.za/) | South African-owned. SANAS accredited for ISO 27001:2022. More affordable than global brands. Tel: +27 16 931 2001 |
| **SABS (SA Bureau of Standards)** | SANAS (they are the national standards body) | Pretoria, national coverage | [sabs.co.za](https://www.sabs.co.za/) | National standards body. Primarily known for product certification and ISO 9001/14001/45001. ISO 27001 certification capability but less common |
| **TUV Rheinland** | DAkkS, recognised internationally | Johannesburg | [tuv.com](https://www.tuv.com/) | German certification body with global reach |

**Recommendation for VeriGate:** BSI or SGS would carry the most international recognition, which matters when dealing with financial institutions and government agencies. SACAS or DQS are more cost-effective options while still being SANAS accredited.

---

## 5. Consulting / Implementation Providers in Cape Town & SA

These are consultants who help you **prepare** for certification (gap analysis, implementation, documentation, training, internal audit). They do NOT issue the certificate -- that must come from an accredited certification body.

### Cape Town Based

| Provider | Location | Services | Website | Contact | Notes |
|----------|----------|----------|---------|---------|-------|
| **WWISE** | Century City, Cape Town (Block 3, Unit 303, Bridgewater One, 4 Conference Lane, Century City, 7441) | Gap analysis, implementation, training (including ISO 27001:2022 courses), internal audit, maintenance | [wwise.co.za](https://wwise.co.za/) | Cape Town: 021 525 9159, National: 086 109 9473 | Established SA firm. ECSA recognised. Strong local presence. Also offers e-learning platform |
| **Imbertech** | Cape Town | Gap analysis, implementation, certification support, POPIA + ISO 27001 combined services, ongoing monitoring | [imbertech.co.za](https://imbertech.co.za/services/compliance) | Via website | Specifically offers combined POPIA + ISO 27001 compliance packages. Good fit for VeriGate's dual requirement |
| **TopCertifier** | Cape Town, Johannesburg, Nelspruit | Consulting, assessment, gap analysis (free initial), third-party audit coordination, training (Lead Auditor, Lead Implementer) | [iso-certification.co.za](https://www.iso-certification.co.za/) | Via website | Claims to offer free gap analysis. Comprehensive service offering |

### National (Servicing Cape Town)

| Provider | Location | Services | Website | Contact | Notes |
|----------|----------|----------|---------|---------|-------|
| **IMSM** | National (global firm, SA operation since 1994) | Fixed-fee implementation consulting, documentation, IMSMLoop client portal, ongoing support | [imsm.com/south-africa](https://www.imsm.com/south-africa/) | Via website | 150+ experts worldwide, 15,000+ clients. Transparent fixed fees. Flexible approach |
| **Hosi Technologies** | Johannesburg (services nationally) | ISO 27001 + POPIA consulting, DIY packages, internal audits, managed services | [hosi.co.za](https://hosi.co.za/) | Via website | ISO 27001 certified themselves. Offers combined ISO + POPIA track. Helping hundreds of organisations with POPIA |
| **Cyber Security Institute (CSI)** | South Africa | ISO 27001 implementation, POPIA compliance, cyber risk governance, training, internal audit | [cybersecurityinstitute.co.za](https://cybersecurityinstitute.co.za/) | Via website | Specialises in cyber security + compliance. Good for tech companies. Bespoke and certification training |
| **CertValue** | National (international firm) | Training, audit, documentation, certification, gap analysis, implementation | [certvalue.com](https://certvalue.com/iso-27001-certification-in-south-africa/) | Via website | International consultancy with SA presence |
| **VerosCert** | National (international firm) | Implementation, certification support, bundled packages | [veroscert.com](https://veroscert.com/southafrica/iso-27001-certification-southafrica.html) | Via website | Offers startup discounts and bundled pricing for multiple certifications |
| **Factocert** | National (international firm) | ISMS roadmap, documentation, training, certification assistance | [factocert.com](https://factocert.com/south-africa/iso-27001-certification-in-south-africa/) | Via website | International consultancy |
| **NRKC (Norocke Consulting)** | Pretoria | ISO implementation, training, maintenance | [nrkc.co.za](https://www.nrkc.co.za/) | Via website | 100% Black Women Owned. BBBEE Level 1. Good for transformation scorecards |

---

## 6. Cost Breakdown

All estimates below are for a **small tech company with < 20 employees** and a focused ISMS scope (the VeriGate platform and supporting processes). Costs are provided in both ZAR and USD (at approximately R18.50/USD).

### 6.1 Detailed Cost Estimates

| Cost Component | ZAR Estimate | USD Estimate | Notes |
|---------------|-------------|-------------|-------|
| **Gap Analysis** | R55,000 -- R110,000 | $3,000 -- $6,000 | External consultant assesses current state vs ISO 27001:2022 requirements. Can be done internally to save costs |
| **Implementation Consulting** | R90,000 -- R185,000 | $5,000 -- $10,000 | Help building documentation, policies, risk register, SoA. Can do much of this in-house using templates/toolkits |
| **Staff Training** | R18,500 -- R55,000 | $1,000 -- $3,000 | Security awareness for all staff + ISO 27001 Lead Implementer course for ISMS lead. Lead Implementer courses start at ~R60,000 per person |
| **Internal Audit** | R37,000 -- R75,000 | $2,000 -- $4,000 | Pre-certification internal audit. Can be done by trained internal staff or outsourced |
| **Technology / Tools** | R18,500 -- R55,000 | $1,000 -- $3,000 | GRC platform (optional), vulnerability scanning, SIEM, documentation management. Many open-source options available |
| **Certification Audit (Stage 1 + Stage 2)** | R90,000 -- R185,000 | $5,000 -- $10,000 | Paid to the accredited certification body. Small company = 3-6 audit days at ~$1,500/day. BSI/SGS at premium end, SACAS at lower end |
| **TOTAL FIRST YEAR** | **R310,000 -- R665,000** | **$17,000 -- $36,000** | |

### 6.2 Realistic VeriGate Estimate (Lean Approach)

Given Arthmatic DevWorks' tech capability (can handle much of the implementation internally):

| Cost Component | ZAR Estimate | Approach |
|---------------|-------------|----------|
| Gap Analysis | R40,000 -- R60,000 | External consultant, 2-3 days |
| Implementation (DIY + Templates) | R30,000 -- R50,000 | In-house using ISO 27001 toolkit/templates, minimal external help |
| Training | R25,000 -- R40,000 | Lead Implementer course for 1 person + awareness training for team |
| Internal Audit | R25,000 -- R40,000 | Outsource to consultant (cannot be done by same person who implemented) |
| Certification Audit | R90,000 -- R130,000 | SACAS or DQS (more affordable than BSI/SGS) |
| **TOTAL (Lean)** | **R210,000 -- R320,000** | |

### 6.3 Annual Maintenance Costs (Year 2 onwards)

| Cost Component | ZAR Estimate | USD Estimate |
|---------------|-------------|-------------|
| Surveillance Audit (annual) | R50,000 -- R90,000 | $2,700 -- $5,000 |
| Internal Audit (annual) | R20,000 -- R40,000 | $1,100 -- $2,200 |
| Ongoing improvements / training | R15,000 -- R30,000 | $800 -- $1,600 |
| **Annual Maintenance Total** | **R85,000 -- R160,000** | **$4,600 -- $8,800** |

### 6.4 Recertification (Every 3 Years)

| Cost Component | ZAR Estimate |
|---------------|-------------|
| Recertification Audit | R75,000 -- R150,000 |

---

## 7. Timeline

### Realistic Timeline for Arthmatic DevWorks (< 20 employees)

| Phase | Duration | Cumulative |
|-------|----------|-----------|
| Management commitment + scope definition | 1-2 weeks | Week 2 |
| Gap analysis | 2-3 weeks | Week 5 |
| Risk assessment + treatment plan | 2-3 weeks | Week 8 |
| Documentation and policy development | 4-6 weeks | Week 14 |
| Control implementation | 2-4 weeks (overlap with documentation) | Week 16 |
| Staff training | 1-2 weeks | Week 17 |
| ISMS operation (evidence collection) | 4-6 weeks (minimum -- auditors want evidence of the ISMS operating) | Week 23 |
| Internal audit | 1-2 weeks | Week 24 |
| Management review | 1 week | Week 25 |
| Stage 1 audit | 1-2 days | Week 26 |
| Address Stage 1 findings | 2-4 weeks | Week 29 |
| Stage 2 audit | 2-4 days | Week 30 |
| Certificate issuance | 2-4 weeks | **Week 34 (~8 months)** |

### Accelerated Timeline (with dedicated resource and good existing practices)

If Arthmatic DevWorks already has documented security practices, the timeline can compress to **4-5 months**:

| Phase | Duration |
|-------|----------|
| Gap analysis + risk assessment | 3 weeks |
| Documentation + implementation | 6 weeks |
| ISMS operation period | 4 weeks |
| Internal audit + management review | 2 weeks |
| Stage 1 + Stage 2 audit | 3 weeks |
| **Total** | **~18 weeks (4.5 months)** |

---

## 8. Provider Comparison Table

### Consulting Providers

| Provider | Location | Cape Town Office | Combined POPIA + ISO 27001 | Estimated Cost (Consulting Only) | Best For |
|----------|----------|-----------------|---------------------------|--------------------------------|----------|
| **WWISE** | Century City, CPT | Yes (physical office) | Not explicitly stated | R100,000 -- R200,000 | Local presence, established reputation, e-learning |
| **Imbertech** | Cape Town | Yes | Yes (explicitly offered) | R80,000 -- R180,000 | Combined POPIA + ISO 27001. End-to-end including ongoing monitoring |
| **IMSM** | National (global) | Services CPT | Not stated | R120,000 -- R250,000 | Fixed-fee transparency, global methodology, client portal |
| **Hosi Technologies** | Johannesburg | No (national service) | Yes (explicitly offered) | R80,000 -- R180,000 | DIY packages available. Already ISO 27001 certified. Tech-focused |
| **CSI** | National | No | Yes | R70,000 -- R150,000 | Cyber security specialists. Good for tech companies. Research-driven |
| **VerosCert** | National (international) | No | Bundled packages | R60,000 -- R150,000 | Startup discounts, bundled multi-standard packages |
| **TopCertifier** | CPT + JHB | Yes | Not stated | R60,000 -- R120,000 | Free initial gap analysis. Budget-friendly |

### Certification Bodies (Audit Costs Only)

| Certification Body | SANAS Accredited | Estimated Audit Cost (Small Company) | Brand Recognition | Best For |
|--------------------|-----------------|--------------------------------------|-------------------|----------|
| **BSI** | Yes (via UKAS/IAF) | R130,000 -- R185,000 | Very High (international gold standard) | Maximum credibility with enterprise and government clients |
| **SGS** | Yes | R110,000 -- R165,000 | Very High | Global recognition, strong TIC reputation |
| **Bureau Veritas** | Yes | R100,000 -- R155,000 | Very High | Long Africa history, strong brand |
| **DQS** | Yes (dual DAkkS/SANAS) | R90,000 -- R140,000 | High | Management system specialists, good value |
| **SACAS** | Yes | R70,000 -- R120,000 | Moderate (SA-focused) | Most affordable SANAS-accredited option |

> **Note**: All cost estimates in the comparison tables are indicative and based on market research. Actual costs vary significantly based on scope, complexity, and negotiation. Always obtain multiple formal quotes.

---

## 9. POPIA and ISO 27001 Overlap

VeriGate will need both ISO 27001 compliance AND POPIA compliance. The good news is there is significant overlap -- implementing one significantly supports the other.

### Where They Overlap

| Area | ISO 27001 Clause/Control | POPIA Section | Overlap |
|------|-------------------------|---------------|---------|
| **Information security policies** | A.5.1 Policies for information security | Section 19 -- Security safeguards | Both require documented security policies |
| **Risk assessment** | Clause 6.1.2 -- Information security risk assessment | Section 19(1) -- Appropriate measures | Both require risk-based approach to protecting information |
| **Access control** | A.8 -- Technological controls (access management) | Section 19(1)(a) -- Prevent unauthorised access | Direct overlap -- restrict who can access personal data |
| **Encryption** | A.8.24 -- Use of cryptography | Section 19(1)(a) -- Prevent loss/damage | Both require encryption of sensitive data |
| **Incident management** | A.5.24-5.28 -- Incident management | Section 22 -- Notification of security compromise | Both require incident detection, response, and notification |
| **Third-party management** | A.5.19-5.23 -- Supplier relationships | Section 20-21 -- Operator (third party) obligations | Both require controls on how third parties handle data |
| **Training and awareness** | A.6.3 -- Information security awareness | Section 8 -- Responsibilities of information officer | Both require staff training on data handling |
| **Data classification** | A.5.12-5.13 -- Information classification | Section 14 -- Personal information categories | Both require understanding and classifying data types |
| **Records and documentation** | Clause 7.5 -- Documented information | Section 14, 17 -- Records of processing | Both require maintaining records |
| **Continuous improvement** | Clause 10 -- Improvement | Section 8 -- Compliance monitoring | Both require ongoing review and improvement |

### Where POPIA Goes Beyond ISO 27001

| POPIA Requirement | ISO 27001 Coverage |
|-------------------|-------------------|
| Lawful basis for processing (Section 9-12) | Not covered -- POPIA-specific |
| Data subject rights (access, correction, deletion) (Section 23-25) | Partially covered via A.5.34 (privacy) |
| Purpose limitation (Section 13) | Not directly covered |
| Information Officer registration with CIPC (Section 55) | Not covered -- POPIA-specific |
| Cross-border transfer restrictions (Section 72) | Not directly covered |
| Direct marketing consent (Section 69) | Not covered |
| Special personal information (Section 26-33) | Partially covered via data classification |

### Recommended Strategy

1. **Implement ISO 27001 first** -- it provides the security management framework.
2. **Layer POPIA-specific requirements on top** -- data subject rights, purpose limitation, lawful processing bases, Information Officer appointment.
3. **Use the ISO 27001 risk assessment** to identify POPIA risks as part of the same exercise.
4. **Leverage ISO 27001 Annex A control A.5.34** (Privacy and protection of PII) as the bridge between the two frameworks.
5. **Estimated additional effort for POPIA** after ISO 27001: 2-4 weeks of focused work to address the gaps listed above.

---

## 10. Government Subsidies and Support

### 10.1 SEDA / SEDFA -- Quality and Standards Support

**Background:** As of 1 October 2024, SEDA merged with sefa and CBDA to form the **Small Enterprise Development and Finance Agency (SEDFA)**. Existing programmes continue.

| Programme | Relevance | Max Grant | Details |
|-----------|-----------|-----------|---------|
| **SEDA Technology Programme (STP) -- Quality & Standards** | **Directly relevant** | R600,000 per project | Non-repayable grant. The Quality and Standards Unit explicitly supports ISO certification. Has precedent of funding ISO 9001, ISO 13485 certification for small businesses |
| **Black Business Supplier Development Programme (BBSDP)** | Relevant | R100,000 | Can fund quality certifications and process improvements |
| **Free needs assessment** | Relevant | N/A (free service) | Visit any SEDA/SEDFA branch for a free business assessment to identify the right programme |

**How to apply for STP Quality & Standards:**

1. Create an eThuse account at [ethuse.co.za](https://ethuse.co.za)
2. Visit nearest SEDA/SEDFA branch for a free needs assessment
3. Prepare: business plan, CIPC registration, SARS tax clearance, quotations from ISO consultants
4. Submit application (online or in-person)
5. Processing: approximately 6-8 weeks
6. Approval and disbursement within 30 days

**Eligibility baseline:**
- South African-owned and operated
- Registered with CIPC
- Tax compliant with SARS (valid tax clearance)
- Annual turnover below R50 million

**Contact:** SEDFA info@sedfa.org.za | Call centre: 0860 103 703 | [sedfa.org.za](https://www.sedfa.org.za/)

### 10.2 dtic (Department of Trade, Industry and Competition)

| Programme | Relevance | Details |
|-----------|-----------|---------|
| **Support Programme for Industrial Innovation (SPII)** | Partially relevant | Funds development of innovative products/processes. Could potentially cover VeriGate platform development but not directly ISO certification |
| **Technology and Human Resources for Industry Programme (THRIP)** | Low relevance | Primarily for R&D collaboration with universities |
| **Sector Specific Assistance Scheme (SSAS)** | Low relevance | Primarily for export market access |

**Website:** [thedtic.gov.za/incentives](https://www.thedtic.gov.za/incentives/)
**Incentives guide 2025/26:** [PDF download](https://www.thedtic.gov.za/wp-content/uploads/the-dtic-incentive-schemes-guide-2026.pdf)

### 10.3 Practical Subsidy Strategy

The **SEDA Technology Programme (STP)** is the most promising avenue. The R600,000 non-repayable grant could cover the bulk of ISO 27001 certification costs. Arthmatic DevWorks should:

1. Visit the nearest SEDA/SEDFA office in Cape Town immediately.
2. Request a needs assessment specifically mentioning ISO 27001 certification.
3. Get formal quotations from 2-3 ISO 27001 consultants (needed for the application).
4. Apply for the STP Quality & Standards grant.
5. Proceed with certification while the application is being processed (if cash flow allows) -- the grant can reimburse costs.

> **Important caveat:** Grant availability and approval are not guaranteed. Budget for full self-funding but pursue the grant in parallel.

---

## 11. Tips for Small Tech Companies

### How to Make It Faster

1. **Define the narrowest possible scope.** For VeriGate: scope it to "the VeriGate verification gateway platform, its cloud infrastructure, APIs, data processing, and directly supporting business processes." Do NOT include the entire company if not all functions handle sensitive data.

2. **Assign a dedicated ISMS lead.** Two committed people can drive a startup through certification. The CTO or a senior engineer is ideal -- no need for a dedicated CISO.

3. **Use a toolkit / template approach.** Do not start from scratch. Purchase an ISO 27001 implementation toolkit (R5,000 -- R15,000). These include policy templates, risk assessment spreadsheets, SoA templates, and procedure documents. Customise to VeriGate's context.

4. **Leverage existing practices.** If Arthmatic DevWorks already has documented coding standards, access controls, incident response procedures, or security reviews -- these count. Map them to ISO 27001 controls.

5. **Start collecting evidence early.** Auditors want evidence that the ISMS has been operating for a period (minimum ~4-8 weeks). Start logging security events, access reviews, and vulnerability scans as early as possible.

6. **Consider a compliance automation platform.** Tools like Vanta, Drata, Sprinto, or Scytale can accelerate implementation and evidence collection. However, watch for per-seat pricing scaling costs. For < 20 employees this may be cost-effective.

### How to Make It Cheaper

1. **Do as much as possible in-house.** As a tech company, Arthmatic DevWorks has the capability to write policies, conduct risk assessments, and implement technical controls internally. Use external consultants only for gap analysis, internal audit, and advice.

2. **Choose a cost-effective certification body.** SACAS or DQS are SANAS accredited but significantly cheaper than BSI or SGS. The certificate carries the same ISO 27001:2022 designation regardless of which accredited body issues it.

3. **Bundle POPIA and ISO 27001.** Engage one consultant for both. The overlap means you avoid duplicating work and paying twice.

4. **Apply for the SEDA STP grant.** Up to R600,000 in non-repayable funding.

5. **Negotiate fixed fees.** IMSM and some other consultants offer fixed-fee arrangements. This removes cost uncertainty.

6. **Remote audits.** Post-COVID, most certification bodies offer remote/hybrid audits. These reduce travel costs (especially if the CB is based in Johannesburg).

### Common Pitfalls to Avoid

| Pitfall | Impact | How to Avoid |
|---------|--------|-------------|
| **Scope too broad** | Increases audit days, documentation, and cost | Define scope tightly around VeriGate platform only |
| **Over-documenting** | Wastes time, creates maintenance burden | Write policies you will actually follow. ISO 27001 requires documented procedures, not a novel |
| **Treating it as a one-off project** | Fails at first surveillance audit | Build the ISMS into daily operations from day one |
| **No management commitment** | Auditors will fail you if management is not visibly involved | Ensure management reviews happen, resources are allocated |
| **Ignoring the risk assessment** | The risk assessment IS the ISMS foundation | Take it seriously. It drives which controls you implement |
| **Choosing controls without justification** | Auditors will ask why each control is included or excluded | The Statement of Applicability must justify every control decision |
| **Not allowing enough ISMS operating time** | Auditors need evidence the system is working | Run the ISMS for at least 6-8 weeks before Stage 2 audit |
| **Picking the cheapest consultant** | Poor implementation = failed audit = wasted money | Choose based on ISO 27001 expertise and tech company experience, not just price |

---

## 12. Recommended Approach for Arthmatic DevWorks

### Recommended Path

| Decision | Recommendation | Rationale |
|----------|---------------|-----------|
| **Consulting partner** | **Imbertech** or **WWISE** | Cape Town based. Imbertech offers combined POPIA + ISO 27001. WWISE has strong local reputation and training capabilities |
| **Certification body** | **DQS** (primary) or **SACAS** (budget) | DQS has dual DAkkS/SANAS accreditation -- recognised internationally. Good value. SACAS is the most affordable SANAS-accredited option |
| **Implementation approach** | Hybrid: consultant for gap analysis + internal audit, in-house for implementation | Leverages Arthmatic DevWorks' technical capability. Keeps costs down |
| **Timeline target** | 5-6 months | Realistic for a small, motivated team |
| **Budget target** | R250,000 -- R350,000 | Lean approach with in-house implementation |
| **Funding** | Apply for SEDA STP grant in parallel | Could recover up to R600,000 |

### Suggested Budget Allocation

| Item | Budget (ZAR) |
|------|-------------|
| Gap analysis (external consultant) | R45,000 |
| ISO 27001 toolkit / templates | R10,000 |
| Implementation (mostly in-house, some consultant guidance) | R35,000 |
| Lead Implementer training (1 person) | R30,000 |
| Staff awareness training | R10,000 |
| Internal audit (outsourced) | R30,000 |
| Certification audit (DQS -- Stage 1 + Stage 2) | R100,000 |
| Contingency (10%) | R26,000 |
| **Total** | **R286,000** |

---

## 13. Next Steps

1. **Immediate: Contact SEDA/SEDFA Cape Town** -- Schedule a free needs assessment. Mention ISO 27001 certification for a data-handling tech platform. Ask about STP Quality & Standards grant eligibility.

2. **Week 1: Get formal quotes from 3 consulting providers:**
   - Imbertech (Cape Town) -- combined POPIA + ISO 27001
   - WWISE (Century City) -- 021 525 9159
   - One of: Hosi Technologies, CSI, or VerosCert

3. **Week 1: Get formal quotes from 2 certification bodies:**
   - DQS South Africa -- [dqsglobal.com/en-za](https://www.dqsglobal.com/en-za/)
   - SACAS -- +27 16 931 2001

4. **Week 2: Decide on consultant and certification body.** Negotiate fixed fees where possible.

5. **Week 2: Appoint internal ISMS lead.** CTO or senior engineer. Does not need to be a dedicated role but needs allocated time (estimate 40-60% for the first 3-4 months).

6. **Week 3: Begin gap analysis.**

7. **In parallel: Submit SEDA STP grant application** with consultant quotations as supporting documentation.

---

## 14. Sources

### Certification Bodies
- [BSI South Africa -- ISO 27001 Certification](https://www.bsigroup.com/en-ZA/ISOIEC-27001-Information-Security/Certification-for-ISO-27001/)
- [SGS South Africa -- ISO/IEC 27001 Certification](https://www.sgs.com/en-za/services/iso-iec-27001-certification-information-security-cybersecurity-and-privacy-protection)
- [Bureau Veritas South Africa -- ISO 27001](https://www.bureauveritas.co.za/ISO-27001)
- [DQS South Africa -- ISO 27001 Certification](https://www.dqsglobal.com/en-za/certify/iso-27001-certification)
- [SACAS -- South African Certification and Auditing Services](https://www.sacas.co.za/)

### Consulting Providers
- [WWISE -- ISO Consulting & Implementation](https://wwise.co.za/)
- [Imbertech -- POPIA & ISO 27001 Compliance](https://imbertech.co.za/services/compliance)
- [IMSM South Africa -- ISO 27001](https://www.imsm.com/south-africa/iso-27001/)
- [Hosi Technologies -- Governance Risk and Compliance](https://hosi.co.za/information-security/compliance-governance/)
- [Cyber Security Institute -- ISO 27001 and POPIA](https://cybersecurityinstitute.co.za/iso27001andpopia/)
- [TopCertifier -- ISO 27001 Cape Town](https://www.iso-certification.co.za/iso-27001-certification-consulting-cape-town.html)
- [VerosCert -- ISO 27001 South Africa](https://veroscert.com/southafrica/iso-27001-certification-southafrica.html)
- [CertValue -- ISO 27001 South Africa](https://certvalue.com/iso-27001-certification-in-south-africa/)
- [Factocert -- ISO 27001 South Africa](https://factocert.com/south-africa/iso-27001-certification-in-south-africa/)
- [NRKC Norocke Consulting -- ISO Certification Pretoria](https://www.nrkc.co.za/)

### Cost Research
- [StrongDM -- ISO 27001 Certification Cost Breakdown 2026](https://www.strongdm.com/blog/iso-27001-certification-cost)
- [High Table -- ISO 27001 Certification Cost 2026](https://hightable.io/iso-27001-certification-cost/)
- [Sprinto -- ISO 27001 Certification Cost](https://sprinto.com/blog/iso-27001-certification-cost/)
- [Vanta -- ISO 27001 Certification Cost](https://www.vanta.com/collection/iso-27001/iso-27001-certification-cost)
- [NRKC -- Cost of ISO Certification in South Africa](https://www.nrkc.co.za/the-cost-of-iso-certification-in-pretoria-what-to-expect-in-south-africa/)
- [Factocert -- ISO Certification South Africa Step-by-Step Guide](https://factocert.com/how-to-get-iso-certification-in-south-africa-a-step-by-step-guide-with-benefits-and-costs-explained/)

### Timeline & Process
- [Konfirmity -- ISO 27001 Audit Timeline 2026](https://www.konfirmity.com/blog/iso-27001-audit-timeline)
- [Secureframe -- ISO 27001 Certification Timeline](https://secureframe.com/hub/iso-27001/certification-timeline)
- [Drata -- ISO 27001 Certification Process](https://drata.com/grc-central/iso-27001/certification-process)
- [Vanta -- ISO 27001 for Startups](https://www.vanta.com/collection/iso-27001/iso-27001-for-startups)

### Tips for Startups
- [Sprinto -- ISO 27001 for Startups](https://sprinto.com/blog/iso-27001-compliance-for-startups/)
- [Drata -- ISO 27001 for Startups: Ten Best Practices](https://drata.com/grc-central/get-started-iso-27001/iso-27001-for-startups)
- [High Table -- ISO 27001 for Tech Startups](https://hightable.io/iso-27001-for-tech-startups-everything-you-need-to-know/)
- [Scytale -- ISO 27001 for Startups](https://scytale.ai/resources/iso-27001-for-startups/)

### POPIA & ISO 27001 Overlap
- [ISMS.online -- Guide to ISO 27001 in South Africa](https://www.isms.online/iso-27001/country/south-africa/)
- [Scytale -- POPIA Compliance](https://scytale.ai/resources/south-africa-popia-compliance/)
- [Cyber Security Institute -- ISO 27001 and POPIA](https://cybersecurityinstitute.co.za/iso27001andpopia/)
- [Imbertech -- POPIA & ISO 27001 Compliance](https://imbertech.co.za/services/compliance)

### Government Subsidies & Support
- [SEDFA (formerly SEDA)](https://www.sedfa.org.za/)
- [SEDA Technology Programme -- dtic](https://www.thedtic.gov.za/financial-and-non-financial-support/incentives/seda-technology-programme/)
- [STP Quality Standards and Technology Transfer Fund -- dtic](https://www.thedtic.gov.za/financial-and-non-financial-support/incentives/stp/)
- [GrantZA -- SEDA/SEDFA Support Programmes 2026](https://grantza.org.za/business/seda)
- [dtic Incentive Schemes Guide 2025/26](https://www.thedtic.gov.za/wp-content/uploads/the-dtic-incentive-schemes-guide-2026.pdf)
- [SANAS -- South African National Accreditation System](https://www.sanas.co.za/)
- [Top 9 ISO Certification Bodies in South Africa -- WWISE](https://wwise.co.za/uncategorized/the-top-9-iso-certification-bodies-in-south-africa/)

---

*This document is for internal planning purposes. All cost estimates are indicative and based on publicly available information as of April 2026. Formal quotations should be obtained from providers before budgeting.*
