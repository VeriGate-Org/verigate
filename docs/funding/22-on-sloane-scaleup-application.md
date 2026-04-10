# 22 On Sloane Scale Up Programme Application

**Company:** Arthmatic DevWorks (Pty) Ltd
**Product:** VeriGate -- Real-Time Risk Intelligence Platform
**Location:** Cape Town, South Africa
**Date:** April 2026
**Contact:** Arthur Manena, Founder & CTO

---

## 1. Company Overview & Vision

Arthmatic DevWorks is a Cape Town-based software development firm building enterprise-grade infrastructure for Africa's digital economy. Our flagship product, **VeriGate**, is a real-time risk intelligence platform that combines cross-agency verification, AI-powered risk scoring, and fraud detection -- purpose-built for the realities of African government and enterprise environments.

**Our vision:** Become the continent's default risk intelligence layer -- the infrastructure that sits between every agency, bank, insurer, and law firm that needs to verify, assess risk, detect conflicts of interest, and prevent fraud in real time. One API. Fourteen verification types. Three AI modules. Automated conflict of interest detection. Zero centralised data storage.

VeriGate is not a point verification solution. It is risk intelligence infrastructure. We are building Africa's Plaid for identity, compliance, and risk -- except we go far beyond banking, covering government registries, sanctions databases, property records, tax status, qualifications, and more, with AI-powered risk scoring that correlates signals across all sources.

---

## 2. Problem Being Solved

Verification in South Africa -- and across Africa -- is broken.

**For businesses:**
- Onboarding a single customer can require checks against 6-10 different agencies (DHA for identity, CIPC for company status, SARS for tax, SAQA for qualifications, credit bureaus, sanctions lists, and more).
- Each agency has its own portal, its own credentials, its own data format, and its own downtime schedule.
- There is no unified API. Businesses stitch together manual processes, CSV uploads, and bespoke integrations that break constantly.
- A single end-to-end KYC/KYB check can take 3-5 business days and involve multiple teams.

**For government agencies:**
- Cross-department verification is largely manual. DHA cannot easily verify SARS status. CIPC data does not talk to DeedsWeb data.
- Fraud and conflicts of interest slip through the cracks because no single system correlates signals across agencies. A procurement officer who is also a director of the bidding company is invisible when CIPC, DHA, and supply chain data are checked in separate silos.
- Each department builds its own verification silo, duplicating effort and cost.
- The Auditor-General consistently flags undisclosed conflicts of interest and inadequate due diligence as root causes of irregular expenditure.

**The cost of the status quo:**
- South Africa loses an estimated R30+ billion annually to identity fraud and grant fraud alone (National Treasury, SIU reports).
- Banks spend R500M-R1B+ per year on fragmented KYC/AML compliance.
- Insurance fraud costs the industry R7+ billion annually, much of it preventable with better cross-source verification.

**The root cause is architectural:** there is no shared risk intelligence layer that respects data sovereignty while enabling real-time cross-agency verification and fraud detection. VeriGate is that layer.

---

## 3. Product Description & Demo-Ready Features

### What VeriGate Is

A real-time risk intelligence platform that connects to 14 verification sources through a single, unified API, with AI-powered risk scoring and fraud detection layered on top. Each agency or data source is accessed through a purpose-built adapter, and the platform is deployed using a cross-account AWS model where every participating agency retains full control of its own environment. There is no centralised database -- data sovereignty is enforced by architecture, not by policy.

### 14 Verification Types (Live or Integration-Ready)

| # | Verification Type | Source | Status |
|---|---|---|---|
| 1 | Identity | DHA (Home Affairs) | Adapter built |
| 2 | Company | CIPC (Companies Register) | Adapter built |
| 3 | Bank Account | QLink | Adapter built |
| 4 | Property | DeedsWeb | Adapter built |
| 5 | Sanctions | WorldCheck, OpenSanctions | Adapter built |
| 6 | Tax Status | SARS | Adapter built |
| 7 | Qualifications | SAQA | Adapter built |
| 8 | Credit Check | Bureau (TransUnion/Experian) | Pending NCR registration |
| 9 | Employment | Employer databases | Adapter built |
| 10 | Income | Payroll/bank statement | Adapter built |
| 11 | Document Verification | AI/OCR (Bedrock) | Adapter built |
| 12 | Negative News | Media screening | Adapter built |
| 13 | Fraud Watchlist | Industry databases | Adapter built |
| 14 | VAT Vendor | SARS VAT registry | Adapter built |

### 3 AI Modules

- **Risk Signals Engine:** Aggregates verification results and flags anomalies across sources in real time. Surfaces risk indicators that single-source checks would miss. Powers **conflict of interest detection** by cross-referencing CIPC directorship records, shareholding percentages, property ownership, PEP status, and sanctions lists to automatically flag undisclosed relationships -- such as a supplier's director who also holds a position in the procuring department, or beneficial ownership overlaps between contracting parties. Uses a weighted compliance scoring model with configurable thresholds to produce real-time Accept/Review/Deny decisions.
- **Fraud Detector:** Uses pattern matching and ML models to detect synthetic identities, document tampering, coordinated fraud rings, and multi-party abuse patterns that may indicate concealed conflicts of interest.
- **Bedrock Common:** Shared AI/ML infrastructure layer built on AWS Bedrock for document OCR, entity extraction, and natural language processing of unstructured records.

### Partner Portal (Next.js 15.5)

- Visual policy builder -- partners define their own verification workflows (e.g., "for property transactions over R2M, run identity + CIPC + DeedsWeb + sanctions + tax").
- Real-time dashboard with verification status, throughput, and risk scoring.
- API key management, webhook configuration, and audit trail access.

### Architecture Highlights

- **Java 21 + Spring Boot 3.3** core -- battle-tested, enterprise-grade.
- **Fully serverless on AWS:** Lambda, SQS FIFO, DynamoDB, Kinesis, EventBridge, Cognito.
- **Adapter-based architecture:** Each verification source is a self-contained adapter. Adding a new source (e.g., a Kenyan or Nigerian registry) means building one adapter, not re-architecting the platform.
- **Cross-account deployment model:** Each agency gets its own AWS account. VeriGate orchestrates across accounts without ever centralising raw data.
- **Event-driven:** Every verification request, result, and decision is an event -- fully auditable, replayable, and analyzable.

### Demo-Ready

The platform is at **TRL 6-7** (system demonstrated in a relevant environment). Core verification flows work end-to-end. The partner portal is functional. The AI modules produce real risk scores. This is not a prototype -- it is a working platform that needs customers and compliance certification to go to market.

---

## 4. Traction & Milestones

| Milestone | Status |
|---|---|
| Core platform architecture designed and built | Done |
| 14 verification adapters developed (13 complete, 1 pending NCR) | Done |
| Cross-account AWS deployment model proven | Done |
| 3 AI modules (Risk Signals, Fraud Detector, Bedrock Common) built | Done |
| Partner portal with visual policy builder shipped | Done |
| Event-driven audit trail and compliance logging operational | Done |
| Adapter-based architecture validated (new sources plug in cleanly) | Done |
| ISO 27001 certification | In progress |
| NCR registration (for credit bureau access) | In progress |
| First pilot customer | Actively pursuing |
| Revenue | Pre-revenue |

**What this means:** We have done the hardest part -- building a working, production-grade platform that handles the complexity of multi-agency verification with full data sovereignty. The remaining work is commercial: certification, partnerships, and landing the first paying customers. That is exactly where 22 On Sloane can accelerate us.

---

## 5. Market Size & Opportunity

### South Africa

| Segment | Annual Spend on Verification/KYC | VeriGate Addressable |
|---|---|---|
| Banking & Financial Services | R2-4 billion | R800M-R1.5B |
| Insurance | R1-2 billion | R400M-R800M |
| Government (DHA, SARS, SIU, etc.) | R1-3 billion | R500M-R1B |
| Property & Legal | R500M-R1B | R200M-R400M |
| Telecoms & Retail (RICA, FICA) | R500M-R1B | R200M-R400M |
| **Total SA TAM** | **R5-11 billion** | **R2.1-4.1 billion** |

### Africa (5-year horizon)

- **Nigeria:** National Identity Management Commission (NIMC) alone processes 100M+ identity records. KYC spend across Nigerian banks exceeds $500M/year.
- **Kenya:** Huduma Namba, eCitizen, and mobile money KYC create a $200M+ verification market.
- **East Africa (Kenya, Tanzania, Uganda, Rwanda):** Combined $400M+ addressable market.
- **West Africa (Nigeria, Ghana, Senegal):** Combined $700M+ addressable market.

| Market | TAM (5-year) |
|---|---|
| South Africa | R4B ($220M) |
| Rest of Africa | $1.5B+ |
| **Total Addressable Market** | **$1.7B+** |

**SAM (Serviceable Addressable Market -- SA, Year 1-3):** R500M-R800M
Focus on financial services, government, and property sectors in South Africa.

**SOM (Serviceable Obtainable Market -- Year 1-2):** R15M-R40M
Land 3-5 enterprise/government pilot customers, each running 50K-500K verifications per month.

### Why Now

- **POPIA enforcement is real.** The Information Regulator is actively investigating and fining. Companies need compliant verification, and VeriGate's data-sovereignty architecture is purpose-built for POPIA.
- **Government digitisation is accelerating.** DHA's modernisation programme, SARS eFiling expansion, and the push for integrated service delivery all create demand for cross-agency verification.
- **AfCFTA (African Continental Free Trade Area)** requires cross-border KYB/KYC for trade -- a market VeriGate's adapter architecture is designed to serve.
- **Banks are under pressure.** The SARB's updated AML/CFT guidance (Guidance Note 7) demands more rigorous, multi-source verification. Manual processes cannot scale.

---

## 6. Business Model

### Revenue Model: SaaS -- Per-Verification Pricing + Subscription Tiers

**Per-Verification Pricing (pay-as-you-go):**

| Verification Type | Price per Check |
|---|---|
| Identity (DHA) | R8-R15 |
| Company (CIPC) | R10-R20 |
| Bank Account | R12-R25 |
| Sanctions Screening | R5-R10 |
| Property (DeedsWeb) | R15-R30 |
| Tax Status (SARS) | R10-R20 |
| Document Verification (AI/OCR) | R15-R35 |
| Full KYC Bundle (5+ checks) | R40-R80 |
| Full KYB Bundle (company + directors) | R60-R120 |

**Subscription Tiers:**

| Tier | Monthly Fee | Included Verifications | Overage Rate |
|---|---|---|---|
| Starter | R5,000 | 500 | Standard rates |
| Professional | R25,000 | 3,000 | 15% discount |
| Enterprise | R75,000+ | 15,000+ | 25% discount + SLA |
| Government | Custom | Custom | Custom + dedicated support |

**Unit Economics Target:**
- Gross margin per verification: 65-80% (cost is primarily API fees to source agencies + compute).
- Blended ARPC (Average Revenue Per Customer): R25,000-R100,000/month for enterprise customers.
- LTV:CAC target: 5:1+ (enterprise contracts are sticky -- switching verification providers is painful).

**Revenue Projections:**

| Period | Customers | Monthly Verifications | MRR | ARR |
|---|---|---|---|---|
| Month 6 | 2-3 pilots | 20K-50K | R80K-R200K | R1M-R2.4M |
| Month 12 | 5-8 paying | 100K-300K | R400K-R1.2M | R5M-R14M |
| Month 24 | 15-25 | 500K-1.5M | R2M-R6M | R24M-R72M |

---

## 7. What We Need from 22 On Sloane

We are not looking for generic startup advice. VeriGate is a technically mature platform that needs specific commercial acceleration. Here is exactly what we are looking for:

### 7.1 Access to Investor Networks

- **Seed/Series A introductions:** We are targeting a R10M-R20M seed round to fund go-to-market, ISO 27001 certification, NCR registration, and the first 12 months of commercial operations.
- **Investor profile:** We need investors who understand B2G/B2B infrastructure plays, not consumer apps. Ideal investors have portfolios in fintech, govtech, regtech, or enterprise SaaS.
- **Specific funds of interest:** Knife Capital, 4Di Capital, Naspers Foundry, SAVCA members with govtech exposure, Norrsken, TLcom Capital.

### 7.2 Government & Enterprise Partnership Introductions

This is the single highest-value ask. VeriGate's sales cycle is enterprise/government -- it requires warm introductions and credibility signals.

- **Government:** Introductions to Wesgro (Western Cape investment), DHA (Home Affairs modernisation programme), SARS (eFiling/verification), National Treasury (fraud prevention), SIU (Special Investigating Unit), SITA (State Information Technology Agency).
- **Financial Services:** Introductions to CIO/CTO/CISO offices at Tier 1 banks (Standard Bank, FNB, Absa, Nedbank), insurance companies (Old Mutual, Sanlam, Discovery), and payment processors.
- **Property & Legal:** Introductions to major conveyancing firms, property groups, and the Law Society of South Africa.

### 7.3 B2G Sales Strategy Mentorship

- We need mentors who have sold software to South African government departments. The procurement process (SCM, bid committees, BBBEE requirements, SITA involvement) is unique and unforgiving.
- Guidance on structuring government pilot agreements that can convert to multi-year contracts.
- Help understanding the cadence: when do departments issue RFPs, how to get on vendor panels, how to position for transversal contracts.

### 7.4 Pilot Customer Facilitation

- Help us land 2-3 paid pilot customers within the first 6 months of the programme.
- Ideal pilot profile: a financial institution or government agency running 50K+ verifications per month, currently using manual or fragmented processes.

### 7.5 Strategic Partner Connections

- **AWS:** We are built entirely on AWS. An introduction to the AWS Public Sector team (South Africa) or the AWS ISV Accelerate programme would be high-impact.
- **Credit Bureaus:** TransUnion and Experian South Africa -- we need commercial agreements and NCR-compliant access.
- **Industry Bodies:** SABRIC (banking risk), ASISA (insurance), PASA (payments).

---

## 8. Growth Plan (6-12 Month Roadmap)

### Phase 1: Certification & First Revenue (Months 1-4)

| Action | Timeline | Outcome |
|---|---|---|
| Complete ISO 27001 certification | Month 1-3 | Mandatory for government and banking contracts |
| Obtain NCR registration | Month 1-4 | Unlocks credit bureau adapter (14th verification type) |
| Land first 2 pilot customers | Month 2-4 | Prove commercial viability, generate case studies |
| Finalise per-verification pricing with real cost data | Month 2-3 | Validate unit economics |
| Hire 1x enterprise sales lead (Johannesburg-based) | Month 2 | B2G/B2B sales capacity |

### Phase 2: Commercial Traction (Months 4-8)

| Action | Timeline | Outcome |
|---|---|---|
| Convert pilots to paid contracts | Month 4-6 | First recurring revenue |
| Land 3-5 additional enterprise customers | Month 4-8 | R400K+ MRR |
| Launch self-service API portal for SME market | Month 5-7 | Volume play -- long-tail revenue |
| Build 2 additional adapters (market-driven) | Month 5-8 | Expand verification coverage based on customer demand |
| Begin POPIA compliance audit (external) | Month 6 | Compliance credential for sales collateral |

### Phase 3: Scale & Expansion (Months 8-12)

| Action | Timeline | Outcome |
|---|---|---|
| Reach 10+ paying customers | Month 8-12 | R1M+ MRR target |
| Begin Nigeria market research and adapter scoping | Month 9-10 | Africa expansion groundwork |
| Prepare Series A materials | Month 10-12 | R30M-R50M raise for continental expansion |
| Hire 2x engineers for adapter development | Month 8-10 | Capacity for new markets |
| Establish partnerships with 2+ system integrators | Month 8-12 | Channel sales for government |

---

## 9. Team

### Arthur Manena -- Founder & CTO

Full-stack architect and engineer with deep expertise in enterprise software, cloud-native architecture, and security-first design. Built VeriGate's entire platform architecture -- from the adapter framework and cross-account AWS deployment model to the AI modules and partner portal. Hands-on technical founder who understands both the code and the commercial opportunity.

**Core competencies:** Java/Spring Boot enterprise systems, AWS serverless architecture, security architecture, API design, AI/ML integration, .NET enterprise systems.

**Current role:** Architect, engineer, and commercial lead. Responsible for all technical decisions, product direction, and early-stage business development.

### Hiring Plan (Funded)

| Role | Priority | Timeline |
|---|---|---|
| Enterprise Sales Lead (B2G/B2B) | Critical | Month 1-2 |
| Solutions Architect / Pre-Sales Engineer | High | Month 3-4 |
| Backend Engineer (adapter development) | High | Month 4-6 |
| Compliance & Partnerships Manager | Medium | Month 6-8 |

---

## 10. Why VeriGate Will Succeed

### 10.1 First-Mover Advantage in South Africa

There is no unified real-time risk intelligence platform in the South African market. Existing solutions are single-source (e.g., XDS for credit, Idenfo for identity) or manual (government portals). VeriGate is the first platform to aggregate 14 verification types with AI-powered risk scoring, automated conflict of interest detection, and fraud detection behind a single API, with full data sovereignty. No competitor offers real-time cross-referencing of CIPC directorships, shareholding, property ownership, and PEP databases to surface hidden relationships -- this is VeriGate's flagship differentiator.

### 10.2 Architecture Is the Moat

VeriGate's competitive moat is not a feature list -- it is the architecture.

- **Adapter-based design:** Each new verification source is a self-contained adapter. Expanding to a new country (Nigeria, Kenya) means building adapters for local registries, not rebuilding the platform. This is how we scale across Africa without proportional engineering cost.
- **Cross-account deployment:** No centralised database. Each agency controls its own data in its own AWS account. This is not a nice-to-have -- it is a hard requirement for government adoption, and it is extraordinarily difficult to retrofit into a platform that was not designed for it from day one.
- **Event-driven audit trail:** Every verification is an immutable event. This gives customers a complete compliance record and gives VeriGate a rich dataset for fraud detection.

### 10.3 Regulatory Tailwinds

- **POPIA** demands data minimisation and purpose limitation. VeriGate's architecture is POPIA-native -- data is never centralised, never stored longer than necessary, and every access is logged.
- **FICA/FIC Act amendments** are expanding KYC requirements to more sectors. More sectors needing verification = larger addressable market.
- **SARB Guidance Note 7** pushes banks toward multi-source verification. Manual processes cannot meet the new standard at scale.

### 10.4 Network Effects

Every new agency or data source that connects to VeriGate makes the platform more valuable for every existing customer. A bank that starts with identity + CIPC checks will naturally expand to sanctions + tax + property checks as they become available. This creates organic revenue expansion within accounts.

### 10.5 Switching Costs

Once an enterprise integrates VeriGate's API into its onboarding, compliance, or underwriting workflow, switching is expensive and risky. Verification is embedded in critical business processes -- it is not a tool you swap out casually.

### 10.6 Africa-Scale Opportunity

The adapter architecture means VeriGate can expand to any African country by building local adapters. The core platform, AI modules, partner portal, and API layer remain the same. This is how a South African startup becomes a continental infrastructure company.

Nigeria alone -- with 200M+ people, a booming fintech sector, and the NIMC identity programme -- represents a larger market than South Africa. Kenya, Ghana, Rwanda, and Tanzania are all digitising government services and creating demand for the exact infrastructure VeriGate provides.

### 10.7 The Team Ships

VeriGate is not a slide deck. It is a working platform at TRL 6-7 with 14 verification adapters, 3 AI modules, a partner portal with a visual policy builder, and a proven cross-account deployment model. This was built by a small team with zero external funding. That combination of technical depth and capital efficiency is rare, and it is the strongest signal that this team can execute.

---

## Summary

| | |
|---|---|
| **Company** | Arthmatic DevWorks (Pty) Ltd |
| **Product** | VeriGate -- Real-Time Risk Intelligence Platform |
| **Stage** | Working platform (TRL 6-7), pre-revenue |
| **Ask** | 22 On Sloane Scale Up Programme membership |
| **Key Need** | Investor introductions, government/enterprise partnerships, B2G sales mentorship |
| **TAM (Africa)** | $1.7B+ |
| **SOM (Year 1-2)** | R15M-R40M |
| **Revenue Model** | SaaS -- per-verification pricing + subscription tiers |
| **Moat** | First-mover, adapter architecture, data-sovereignty by design, AI-powered conflict of interest detection and real-time risk intelligence |
| **Location** | Cape Town (open to Johannesburg presence) |

---

*Prepared by Arthmatic DevWorks for the 22 On Sloane Scale Up Programme, April 2026.*
