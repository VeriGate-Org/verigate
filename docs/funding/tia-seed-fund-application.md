# TIA Seed Fund Application

## VeriGate: Real-Time Risk Intelligence Platform

**Applicant:** Arthmatic DevWorks (Pty) Ltd
**Location:** Cape Town, Western Cape, South Africa
**Date:** April 2026
**Funding Requested:** R1,500,000 (including VAT)
**Project Duration:** 18 months
**TRL at Application:** 6-7
**TRL Target at Completion:** 8-9

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Problem Statement](#2-problem-statement)
3. [Solution Overview](#3-solution-overview)
4. [Technology Readiness Level Assessment](#4-technology-readiness-level-assessment)
5. [Market Opportunity](#5-market-opportunity)
6. [Competitive Advantage](#6-competitive-advantage)
7. [Team and Capability](#7-team-and-capability)
8. [Development Roadmap](#8-development-roadmap)
9. [Budget Breakdown](#9-budget-breakdown)
10. [Expected Outcomes and Impact](#10-expected-outcomes-and-impact)
11. [Sustainability Plan](#11-sustainability-plan)
12. [Risk Mitigation](#12-risk-mitigation)

---

## 1. Executive Summary

Arthmatic DevWorks seeks R1,500,000 from the TIA Seed Fund to commercialise **VeriGate**, a Real-Time Risk Intelligence Platform that enables secure, cross-agency data verification, AI-powered risk scoring, conflict of interest detection, and fraud detection across South African government agencies and enterprises. VeriGate addresses one of the most persistent bottlenecks in public service delivery and private sector compliance: the inability to verify identity, company registration, tax status, qualifications, property ownership, and other critical data points across organisational boundaries without resorting to slow, manual, paper-based processes.

The platform is built on a zero-trust, data-sovereign architecture. No centralised database is created; each participating agency retains full custody and control of its data within its own isolated AWS environment. VeriGate acts as a secure verification broker, routing encrypted queries through adapter modules that translate between disparate agency systems. The result is sub-second verification that today takes days or weeks.

The core platform has been developed and demonstrated in a relevant environment (TRL 6-7). Fourteen verification adapter types have been designed, with the majority functional. The requested funding will close the remaining gaps required for market entry: ISO 27001 certification (a non-negotiable requirement for government procurement), NCR registration for credit bureau adapters, completion of outstanding adapter development, legal frameworks for data processing agreements, and structured go-to-market activities targeting government agencies and regulated enterprises.

VeriGate directly advances South Africa's National Digital and Future Skills Strategy, supports the Department of Public Service and Administration's modernisation agenda, and aligns with the broader objectives of the National Development Plan by reducing fraud, improving government efficiency, and enabling inclusive economic participation through trustworthy digital verification infrastructure.

Upon completion of the funded programme, VeriGate will be positioned as a self-sustaining SaaS platform generating revenue through per-verification transaction fees, with a clear path to profitability within 12 months of first commercial deployment.

---

## 2. Problem Statement

### 2.1 The Fragmented Verification Landscape

South Africa's government and enterprise sectors operate within a deeply fragmented verification ecosystem. When a government department needs to verify a supplier's tax compliance, it must separately contact SARS. When it needs to confirm a director's identity, it must query the Department of Home Affairs. When it needs to check company registration status, it must access CIPC systems. Each of these interactions follows its own protocol, authentication model, data format, and response timeline.

This fragmentation produces measurable harm:

**Delays in service delivery.** Government procurement processes routinely stall for weeks while manual verification checks are conducted. The National Treasury's Central Supplier Database (CSD) partially addresses supplier verification, but covers only a subset of the checks required for due diligence, leaving departments to conduct the remainder manually.

**Fraud, corruption, and conflict of interest.** The absence of real-time, cross-referenced verification creates opportunities for fraudulent actors and concealed conflicts of interest. The Auditor-General's 2024/25 reports consistently highlight irregular expenditure linked to inadequate supplier due diligence and undisclosed relationships between officials and suppliers. A procurement officer who is also a director of the bidding company, a tender evaluator with undeclared shareholding in the preferred supplier, or a politically exposed person channelling contracts through a family member's company -- these conflicts are invisible when verification happens in isolated silos. Identity fraud, fictitious companies, and misrepresented qualifications cost the South African economy billions of rands annually. The SIU has repeatedly flagged weak verification and undisclosed conflicts of interest as root causes of procurement fraud.

**Regulatory burden on the private sector.** Banks, insurers, and large corporates face onerous Know Your Customer (KYC), Anti-Money Laundering (AML), and Financial Intelligence Centre Act (FICA) obligations. Each institution independently builds and maintains integrations with the same set of data sources, duplicating effort and cost across the economy.

**Exclusion of SMMEs and emerging enterprises.** Small businesses and informal traders are disproportionately affected by slow verification. A delayed company registration check or an unresolved identity query can stall a contract award for months, threatening the viability of small enterprises that lack the cash reserves to absorb such delays.

### 2.2 Why Existing Solutions Fall Short

Current approaches to cross-agency verification in South Africa suffer from one or more of the following limitations:

- **Point-to-point integrations** are expensive to build, fragile to maintain, and do not scale. Each new data source requires a bespoke integration.
- **Centralised databases** (where data is copied into a single repository) create unacceptable data sovereignty and privacy risks, and face resistance from data custodian agencies unwilling to relinquish control.
- **Manual processes** (phone calls, emails, physical document submissions) are slow, error-prone, and susceptible to social engineering and forgery.
- **International verification platforms** (e.g., Refinitiv World-Check, LexisNexis) address only a narrow slice of the problem (typically sanctions and PEP screening) and are priced for multinational corporations, not South African government departments or SMMEs.

There is no South African platform that provides a unified, real-time risk intelligence capability -- combining cross-agency verification, AI-powered risk scoring, and fraud detection -- across the breadth of sources required for comprehensive due diligence.

---

## 3. Solution Overview

### 3.1 What VeriGate Does

VeriGate is a modular, API-driven real-time risk intelligence platform that enables any authorised entity to perform verification queries, risk assessments, and fraud detection against multiple data sources through a single integration point. A requesting party submits a verification request (e.g., "Verify that ID number 8501015800083 is a valid South African identity") and receives a structured, auditable response within seconds.

The platform supports **14 verification types**, each implemented as an independent adapter module:

| # | Verification Type | Data Source | Status |
|---|------------------|-------------|--------|
| 1 | Identity | Department of Home Affairs (DHA) | Functional |
| 2 | Company Registration | CIPC | Functional |
| 3 | Bank Account | QLink | Functional |
| 4 | Property Ownership | DeedsWeb | Functional |
| 5 | Sanctions Screening | WorldCheck, OpenSanctions | Functional |
| 6 | Tax Compliance | SARS | In development |
| 7 | Qualifications | SAQA | In development |
| 8 | Credit Check | Credit Bureaus | Pending NCR registration |
| 9 | Employment Verification | UIF/Employer APIs | In development |
| 10 | Income Verification | Payroll/Bank data | In development |
| 11 | Document Verification | AI/OCR (Bedrock) | Functional |
| 12 | Negative News Screening | Media/Public records | Functional |
| 13 | Fraud Watchlist | SABRIC, internal models | In development |
| 14 | VAT Vendor Verification | SARS VAT registry | In development |

### 3.2 Architecture: Data Sovereignty by Design

VeriGate's architecture is fundamentally different from conventional integration platforms. It employs a **cross-account AWS deployment model** in which each participating agency operates within its own isolated AWS account. There is no centralised database. Data never leaves the custody of the agency that owns it.

The architecture comprises the following layers:

**Verification Broker Layer.** The central orchestration component that receives verification requests, routes them to the appropriate adapter, manages retry logic, and returns results. Built on AWS Lambda (serverless) for elastic scalability and cost efficiency.

**Adapter Layer.** Each data source is accessed through a purpose-built adapter that encapsulates the source-specific protocol, authentication, data mapping, and error handling. Adapters are deployed within the data custodian's AWS account, ensuring that raw data never traverses network boundaries. Only verification results (yes/no/match score) are returned.

**AI/ML Layer.** Three AI modules built on AWS Bedrock enhance the platform's intelligence:
- **Bedrock Common Module:** Provides natural language processing for document verification, name matching across transliteration variants, and address normalisation.
- **Risk Signals Module:** Aggregates signals across multiple verification types to produce a composite risk score, identifying patterns that individual checks would miss. This module is central to conflict of interest detection -- it cross-references CIPC directorship records, shareholding data, property ownership, and PEP databases to automatically flag undisclosed relationships between parties in a transaction. For example, it can detect that a supplier's director also holds a position in the procuring department, or that an applicant's beneficial ownership overlaps with a sanctioned entity.
- **Fraud Detector Module:** Applies anomaly detection and behavioural analysis to flag potentially fraudulent verification patterns (e.g., unusually high query volumes for a single identity, verification requests from atypical geographic locations, multi-partner abuse patterns that may indicate coordinated conflict of interest schemes).

**Partner Portal.** A Next.js 15.5 web application that provides a dashboard for partner organisations to manage their verification activities, view audit logs, configure alert thresholds, and access analytics.

**Security Layer.** AWS Cognito for identity and access management, with fine-grained role-based access control. All data in transit is encrypted with TLS 1.3. All data at rest is encrypted with AES-256 via AWS KMS. Every verification request and response is immutably logged for audit purposes.

### 3.3 Technology Stack

| Component | Technology |
|-----------|-----------|
| Core Platform | Java 21, Spring Boot 3.3 |
| Serverless Compute | AWS Lambda |
| Message Queuing | AWS SQS FIFO (ordered, exactly-once processing) |
| Database | AWS DynamoDB (NoSQL, single-digit millisecond latency) |
| Event Streaming | AWS Kinesis |
| Event Orchestration | AWS EventBridge |
| Identity Management | AWS Cognito |
| AI/ML | AWS Bedrock |
| Partner Portal | Next.js 15.5 (React) |
| Architecture Pattern | Adapter-based, event-driven, CQRS |

---

## 4. Technology Readiness Level Assessment

### 4.1 Current State: TRL 6-7

VeriGate's current technology readiness sits between TRL 6 (system/subsystem model or prototype demonstration in a relevant environment) and TRL 7 (system prototype demonstration in an operational environment).

**Evidence supporting TRL 6-7 classification:**

- The core verification broker has been developed, tested, and demonstrated with live data sources in a staging environment that mirrors production infrastructure.
- Eight of fourteen adapter modules are functional and have processed verification queries against real-world data sources.
- The cross-account AWS deployment model has been validated, confirming that the data sovereignty architecture works as designed.
- The AI/ML modules (document verification via OCR, risk signal aggregation) have been demonstrated with production-representative data.
- The partner portal is functional, providing dashboard, audit log, and configuration capabilities.
- Load testing has confirmed the platform's ability to handle concurrent verification requests at volumes representative of initial commercial deployment.

**Gaps preventing TRL 8-9 classification:**

- Six adapter modules remain in development or are pending external prerequisites (NCR registration for credit checks).
- ISO 27001 certification has not been obtained. This is a prerequisite for government procurement and enterprise adoption.
- No pilot deployment with a paying customer has been conducted.
- Data processing agreements and legal frameworks for agency participation have not been finalised.
- The Fraud Detector AI module requires training on a larger dataset to achieve production-grade accuracy.

### 4.2 Target State: TRL 8-9

At the conclusion of the funded programme (18 months), VeriGate will achieve TRL 8-9:

- All fourteen adapter modules will be functional and tested.
- ISO 27001 certification will be obtained.
- At least two pilot deployments with government agencies or enterprise customers will have been completed.
- The platform will be operating in a production environment processing live verification requests.
- Legal and contractual frameworks will be in place for agency onboarding.

---

## 5. Market Opportunity

### 5.1 Total Addressable Market

The South African verification services market spans multiple segments:

**Government Procurement and Compliance.** National and provincial government departments, state-owned enterprises, and municipalities collectively process hundreds of thousands of supplier verifications annually. National Treasury's procurement spend exceeds R1 trillion per annum, all of which requires supplier due diligence.

**Financial Services KYC/AML.** South Africa's banking sector processes millions of KYC checks annually. The Financial Intelligence Centre Act (FICA) and its amendments have progressively tightened verification requirements. Banks, insurers, asset managers, and payment service providers all require ongoing customer verification.

**Property and Real Estate.** Property transactions require identity verification, FICA compliance, ownership confirmation, and rates clearance. The Deeds Registry processes hundreds of thousands of transfers annually.

**Professional Services and Recruitment.** Employers, recruitment agencies, and professional bodies require qualification verification, identity confirmation, criminal record checks, and reference validation.

**Conservative Market Sizing:**

| Segment | Estimated Annual Verification Volume | Average Revenue per Verification | Segment Revenue Potential |
|---------|--------------------------------------|----------------------------------|--------------------------|
| Government | 2,000,000 | R15 | R30,000,000 |
| Financial Services | 5,000,000 | R20 | R100,000,000 |
| Property | 500,000 | R25 | R12,500,000 |
| Professional Services | 1,000,000 | R12 | R12,000,000 |
| **Total** | **8,500,000** | | **R154,500,000** |

VeriGate's initial target is to capture 5-10% of the government segment within 3 years of launch, representing R1.5M-R3M in annual recurring revenue, with expansion into financial services and property thereafter.

### 5.2 Beachhead Market

The initial go-to-market strategy targets **Western Cape government agencies** as the beachhead market. The Western Cape Government has demonstrated a strong commitment to digital transformation, and agencies such as **Wesgro** (the Western Cape Trade and Investment Promotion Agency) have expressed interest in streamlining verification processes for investor onboarding and trade facilitation.

The Department of Home Affairs (DHA) is a secondary target, given its central role as both a data custodian (identity verification) and a high-volume consumer of verification services (immigration, civic services).

---

## 6. Competitive Advantage

### 6.1 Differentiation

VeriGate's competitive position rests on five structural advantages:

**1. Data Sovereignty Architecture.** Unlike platforms that centralise data, VeriGate's cross-account model ensures that each agency retains full custody of its data. This eliminates the single biggest objection to adopting a shared verification platform and aligns with the Protection of Personal Information Act (POPIA) principle of purpose limitation.

**2. Breadth of Coverage.** No existing South African platform offers fourteen verification types through a single integration. Competitors typically offer 2-4 verification types and require customers to maintain additional integrations for the remainder.

**3. Adapter-Based Extensibility.** New data sources can be integrated by developing a new adapter module without modifying the core platform. This architectural choice means VeriGate can add new verification types in weeks rather than months.

**4. AI-Enhanced Risk Intelligence and Conflict of Interest Detection.** The Risk Signals and Fraud Detector modules elevate VeriGate from a verification tool to a real-time risk intelligence platform. By correlating signals across multiple verification types -- CIPC directorships, shareholding percentages, property ownership, PEP status, and sanctions lists -- VeriGate automatically detects conflicts of interest that are invisible to single-source checks. A weighted compliance scoring model assesses director status, company standing, and beneficial ownership patterns, producing a composite risk decision (Accept/Review/Deny) that surfaces undisclosed relationships, directorial overlaps, and hidden financial connections. This capability is uniquely valuable for government supply chain management, where procurement fraud and undeclared conflicts of interest are among the Auditor-General's most frequently cited findings.

**5. South African Built, South African Hosted.** VeriGate is developed in South Africa, by a South African company, and deployed on South African (or African-region) cloud infrastructure. This addresses data residency requirements and supports the government's localisation and transformation objectives.

### 6.2 Competitive Landscape

| Competitor | Coverage | Data Model | SA Focus | Pricing |
|-----------|----------|------------|----------|---------|
| XDS / TransUnion | Credit, Identity | Centralised | Yes | Per-query, premium |
| LexisNexis SA | Identity, Compliance | Centralised | Partial | Enterprise licensing |
| Refinitiv World-Check | Sanctions, PEP | Centralised | No | Enterprise licensing |
| Managed Integrity Evaluation | Background screening | Manual + Digital | Yes | Per-check, slow |
| **VeriGate** | **14 types, unified** | **Decentralised, sovereign** | **Yes** | **Per-verification, competitive** |

---

## 7. Team and Capability

### 7.1 Arthmatic DevWorks

Arthmatic DevWorks is a Cape Town-based custom software development firm specialising in cloud services, application development, and business process automation. The company has delivered enterprise-grade software solutions for clients across government and the private sector, with deep expertise in AWS cloud architecture, Java/Spring Boot development, and modern frontend technologies.

### 7.2 Core Competencies Relevant to VeriGate

- **Cloud-native architecture:** The team has extensive experience designing and deploying serverless, event-driven systems on AWS, including multi-account architectures with cross-account access patterns.
- **Government and enterprise integration:** Prior project experience integrating with South African government systems and regulatory data sources.
- **Security-first development:** Established practices around secure coding, encryption, access control, and audit logging that align with ISO 27001 requirements.
- **AI/ML implementation:** Practical experience deploying AWS Bedrock models for document processing, natural language understanding, and anomaly detection.

### 7.3 Resource Plan

The TIA Seed Fund will enable the following resource allocation over the 18-month programme:

| Role | Allocation | Responsibility |
|------|-----------|----------------|
| Technical Lead / Architect | Full-time | Platform architecture, adapter development, security |
| Backend Developer | Full-time | Adapter module development, API development, testing |
| Frontend Developer | Part-time | Partner portal enhancements, dashboard development |
| DevOps / Cloud Engineer | Part-time | AWS infrastructure, CI/CD, monitoring, compliance |
| Business Development | Part-time | Go-to-market, agency engagement, partnership development |
| Legal / Compliance | Contract | ISO 27001, NCR registration, data processing agreements |

---

## 8. Development Roadmap

The 18-month programme is structured in three phases:

### Phase 1: Compliance and Foundation (Months 1-6)

**Objective:** Obtain the certifications and registrations required for market entry.

| Deliverable | Timeline | Budget Allocation |
|-------------|----------|-------------------|
| ISO 27001 gap analysis and remediation | Months 1-3 | R150,000 |
| ISO 27001 certification audit | Months 4-6 | R200,000 |
| NCR registration application and processing | Months 1-4 | R100,000 |
| Data processing agreement templates (legal) | Months 2-4 | R80,000 |
| Terms and conditions, SLAs, contract frameworks | Months 3-5 | R70,000 |

**Phase 1 Total: R600,000**

**Milestone:** ISO 27001 certificate obtained; NCR registration approved; legal frameworks ready for agency onboarding.

### Phase 2: Adapter Completion and Pilot (Months 4-12)

**Objective:** Complete all fourteen adapters and conduct at least two pilot deployments.

| Deliverable | Timeline | Budget Allocation |
|-------------|----------|-------------------|
| Tax compliance adapter (SARS integration) | Months 4-6 | R60,000 |
| Qualifications adapter (SAQA integration) | Months 5-7 | R50,000 |
| Credit check adapter (post-NCR registration) | Months 6-8 | R60,000 |
| Employment verification adapter | Months 7-9 | R50,000 |
| Income verification adapter | Months 8-10 | R50,000 |
| Fraud watchlist adapter | Months 9-11 | R50,000 |
| VAT vendor verification adapter | Months 7-9 | R40,000 |
| Fraud Detector AI model training and validation | Months 6-10 | R40,000 |
| Pilot deployment 1 (target: Wesgro or WCG agency) | Months 8-12 | Included in GTM |
| Pilot deployment 2 (target: enterprise client) | Months 10-12 | Included in GTM |

**Phase 2 Total: R400,000**

**Milestone:** All fourteen adapters functional and tested; two pilot deployments underway with live verification queries being processed.

### Phase 3: Go-to-Market and Scale (Months 10-18)

**Objective:** Transition from pilot to commercial operations; establish a sustainable revenue pipeline.

| Deliverable | Timeline | Budget Allocation |
|-------------|----------|-------------------|
| Go-to-market strategy execution | Months 10-18 | R200,000 |
| Government procurement registration (CSD, transversal contracts) | Months 10-12 | R30,000 |
| Conference presentations, demos, industry engagement | Months 12-18 | R70,000 |
| Cloud infrastructure (production environment, 12 months) | Months 7-18 | R150,000 |
| Ongoing security monitoring and penetration testing | Months 7-18 | R50,000 |

**Phase 3 Total: R500,000**

**Milestone:** At least one paying customer; production environment operational; revenue generation commenced.

---

## 9. Budget Breakdown

### 9.1 Summary by Category

| Category | Amount (Incl. VAT) | % of Total |
|----------|-------------------|------------|
| ISO 27001 Certification | R350,000 | 23.3% |
| NCR Registration | R100,000 | 6.7% |
| Adapter Development | R400,000 | 26.7% |
| Legal and Compliance | R150,000 | 10.0% |
| Go-to-Market | R300,000 | 20.0% |
| Cloud Infrastructure | R150,000 | 10.0% |
| Security and Testing | R50,000 | 3.3% |
| **Total** | **R1,500,000** | **100%** |

### 9.2 Summary by Phase

| Phase | Duration | Amount (Incl. VAT) | % of Total |
|-------|----------|-------------------|------------|
| Phase 1: Compliance and Foundation | Months 1-6 | R600,000 | 40% |
| Phase 2: Adapter Completion and Pilot | Months 4-12 | R400,000 | 27% |
| Phase 3: Go-to-Market and Scale | Months 10-18 | R500,000 | 33% |
| **Total** | **18 months** | **R1,500,000** | **100%** |

### 9.3 Arthmatic DevWorks Co-Investment

Arthmatic DevWorks has already invested approximately **R800,000** in VeriGate development to date, comprising:

- Core platform architecture and development (12+ months of engineering effort)
- AWS infrastructure costs during development and testing
- Initial adapter development (eight functional adapters)
- AI/ML module development and training
- Partner portal design and development

This co-investment demonstrates the company's commitment to the project and de-risks the TIA Seed Fund investment.

---

## 10. Expected Outcomes and Impact

### 10.1 Direct Outcomes (18-Month Programme)

| Outcome | Measure | Target |
|---------|---------|--------|
| Platform readiness | All 14 adapters functional | 100% |
| Certification | ISO 27001 obtained | Yes |
| Regulatory registration | NCR registration obtained | Yes |
| Pilot deployments | Active pilot customers | 2+ |
| Revenue | Monthly recurring revenue at programme end | R50,000+ |
| Technology readiness | TRL level at programme end | 8-9 |

### 10.2 Economic Impact (3-Year Horizon)

**Job Creation.**
- Direct employment: 5-8 full-time positions within Arthmatic DevWorks (developers, DevOps, support, business development) by Year 3.
- Indirect employment: 10-15 positions at implementation partners, resellers, and client organisations requiring VeriGate integration specialists.
- Skills development: Training and upskilling of developers in cloud-native, security-first, AI-enhanced platform development, directly contributing to South Africa's scarce skills pipeline.

**Fraud Reduction and Conflict of Interest Prevention.**
- Real-time cross-agency verification directly reduces the window of opportunity for identity fraud, tender fraud, and procurement irregularities.
- VeriGate's conflict of interest detection capability addresses a root cause of procurement fraud: undisclosed relationships between officials and suppliers. By automatically cross-referencing CIPC directorships, shareholding, and PEP databases, VeriGate makes it possible to identify conflicts before a contract is awarded -- not after the Auditor-General flags it years later.
- Conservative estimate: If VeriGate prevents even 0.1% of the R30B+ in annual irregular expenditure identified by the Auditor-General, the economic saving exceeds R30M per year, a 20x return on the TIA Seed Fund investment.

**Government Efficiency.**
- Verification processes that currently take 5-15 business days can be completed in seconds.
- Reduced manual processing effort frees government officials to focus on value-adding activities.
- Standardised verification reduces the administrative burden on SMMEs interacting with government.

**Digital Transformation.**
- VeriGate provides foundational infrastructure for digital government services, enabling other platforms and services to build on reliable, real-time verification.
- The adapter-based architecture creates a template for secure inter-agency data sharing that can be replicated across other government functions.

**B-BBEE and Transformation.**
- Arthmatic DevWorks is a Black-owned enterprise, and VeriGate's commercialisation will directly contribute to transformation in the ICT sector.
- By reducing verification barriers for SMMEs, VeriGate indirectly supports the participation of emerging enterprises in government procurement.

### 10.3 Alignment with National Priorities

| National Priority | VeriGate Contribution |
|-------------------|----------------------|
| National Development Plan (NDP) | Supports efficient, corruption-free public service delivery |
| National Digital and Future Skills Strategy | Develops scarce cloud, AI, and cybersecurity skills |
| DPSA Modernisation | Provides digital verification infrastructure for government |
| POPIA Compliance | Data sovereignty architecture inherently supports privacy |
| Operation Vulindlela (structural reform) | Reduces bureaucratic friction in government processes |
| District Development Model | Enables consistent verification standards across all districts |

---

## 11. Sustainability Plan

### 11.1 Revenue Model

VeriGate will generate revenue through a **per-verification transaction fee** model, supplemented by platform access fees for high-volume customers.

| Pricing Tier | Monthly Verification Volume | Per-Verification Fee | Monthly Platform Fee |
|-------------|---------------------------|---------------------|---------------------|
| Starter | Up to 500 | R25 | R0 |
| Professional | 501 - 5,000 | R18 | R2,500 |
| Enterprise | 5,001 - 50,000 | R12 | R10,000 |
| Government | 50,001+ | R8 | R25,000 |

### 11.2 Revenue Projections

| Period | Customers | Avg. Monthly Verifications | Monthly Revenue | Annual Revenue |
|--------|-----------|---------------------------|-----------------|----------------|
| Year 1 (post-pilot) | 3-5 | 5,000 | R75,000 | R900,000 |
| Year 2 | 10-15 | 25,000 | R350,000 | R4,200,000 |
| Year 3 | 25-40 | 100,000 | R1,100,000 | R13,200,000 |

### 11.3 Path to Profitability

- **Break-even point:** Approximately 3,000 verifications per month (achievable with 2-3 Professional-tier customers).
- **Gross margin at scale:** 70-80% (cloud infrastructure costs scale sub-linearly with volume due to serverless architecture).
- **Expected profitability:** Month 6-9 post-commercial launch (Month 18-24 from programme start).

### 11.4 Growth Strategy

1. **Land with government, expand to enterprise.** Government agencies provide credibility and volume. Enterprise clients (banks, insurers, property companies) provide higher per-verification revenue.
2. **Platform network effects.** Each new agency that joins VeriGate as a data source increases the platform's value to all existing and potential customers.
3. **Geographic expansion.** The adapter-based architecture allows VeriGate to expand into other Southern African markets (Namibia, Botswana, Mozambique) by developing country-specific adapters while retaining the core platform.
4. **API marketplace.** In the medium term, third-party developers can build on VeriGate's verification infrastructure, creating an ecosystem of verification-enhanced applications.

---

## 12. Risk Mitigation

### 12.1 Risk Register

| Risk | Likelihood | Impact | Mitigation Strategy |
|------|-----------|--------|---------------------|
| **Government agency reluctance to participate** | Medium | High | Begin with agencies that have expressed interest (Wesgro). Demonstrate data sovereignty architecture to address custody concerns. Engage through existing government relationships. Offer pilot at no cost to reduce adoption friction. |
| **ISO 27001 certification delays** | Medium | High | Engage certification body early (Month 1). Conduct gap analysis before formal audit process. Allocate contingency time in the schedule. Many ISO 27001 controls are already embedded in the platform's architecture. |
| **NCR registration delays or refusal** | Low | Medium | Begin application process immediately. Engage NCR compliance consultants. If registration is delayed beyond Month 6, prioritise the remaining 13 verification types and add credit checks when registration is obtained. |
| **Data source API changes or unavailability** | Medium | Medium | Adapter-based architecture isolates the impact of source-side changes to a single module. Implement health monitoring and alerting for all adapters. Maintain relationships with data source technical teams. |
| **Cloud infrastructure costs exceeding budget** | Low | Medium | Serverless architecture (Lambda, DynamoDB on-demand) ensures costs scale with usage. Reserved capacity pricing for predictable workloads. Monthly cost monitoring with automated alerts. |
| **Competitive entry by established player** | Low | Medium | First-mover advantage in the data-sovereign, cross-agency segment. Deep local knowledge and relationships are difficult to replicate. Open standards and competitive pricing reduce incentive for customers to switch. |
| **POPIA regulatory challenge** | Low | High | Architecture is designed for POPIA compliance from the ground up (data sovereignty, purpose limitation, audit trails). Engage Information Regulator proactively. Obtain legal opinion on data processing model. |
| **Key person dependency** | Medium | Medium | Document architecture and code comprehensively. Use infrastructure-as-code (AWS CDK/CloudFormation) to ensure reproducibility. Cross-train team members on critical components. |

### 12.2 Contingency Planning

- **Budget contingency:** 10% of each phase budget is held as contingency within the allocated amounts. If ISO 27001 costs exceed estimates, go-to-market activities will be phased to accommodate.
- **Timeline contingency:** The 18-month programme includes 2 months of overlap between phases, providing schedule buffer.
- **Technical contingency:** If specific data source integrations prove infeasible within the programme period, VeriGate will launch with a subset of verification types (minimum 10 of 14) and add the remainder post-programme.

---

## Appendices

### Appendix A: Company Details

| Field | Detail |
|-------|--------|
| Company Name | Arthmatic DevWorks (Pty) Ltd |
| Registration | CIPC registered |
| Location | Cape Town, Western Cape |
| CSD Registration | Registered on Central Supplier Database |
| Tax Status | SARS tax compliant |
| B-BBEE | Compliant |

### Appendix B: TIA Seed Fund Eligibility Checklist

| Requirement | Status |
|-------------|--------|
| South African citizen or permanent resident (founder) | Confirmed |
| CIPC registered entity | Confirmed |
| CSD registered | Confirmed |
| Tax compliant (SARS) | Confirmed |
| B-BBEE compliant | Confirmed |
| Technology at TRL 5-6+ | Confirmed (TRL 6-7) |
| Funding request within R1,500,000 cap | Confirmed (R1,500,000) |
| Programme duration within 12-24 months | Confirmed (18 months) |

### Appendix C: Letters of Support

*To be attached:*
- Letter of interest from target pilot agency
- AWS Partner Network membership confirmation
- Technical reference from prior government project

### Appendix D: Technical Architecture Diagram

*To be attached:*
- VeriGate system architecture diagram
- Cross-account AWS deployment model diagram
- Adapter module interaction flow diagram
- Data flow and encryption diagram

---

**Contact:**

Arthmatic DevWorks (Pty) Ltd
Cape Town, Western Cape, South Africa

---

*This application is submitted in support of a TIA Seed Fund grant to bridge VeriGate -- South Africa's first Real-Time Risk Intelligence Platform -- from its current prototype/MVP stage (TRL 6-7) to commercial readiness (TRL 8-9), directly advancing the Technology Innovation Agency's mandate to bridge the innovation chasm between research and commercialisation in South Africa.*
