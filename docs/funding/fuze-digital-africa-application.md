# Fuze (Digital Africa) Application — VeriGate

**Company:** Arthmatic DevWorks (Pty) Ltd
**Product:** VeriGate — Real-Time Risk Intelligence Platform
**Co-Founders:** Arthur Manena (CEO) & Mhlonipheni Ntleko (CTO)
**Location:** Cape Town, South Africa
**Date:** April 2026
**Funding Request:** €100,000

---

## ELIGIBILITY CHECKLIST

| Criterion | VeriGate Status |
|---|---|
| Less than 18 months old, OR less than 36 months if generating revenue or raised funds | Incorporated 21 Dec 2024 — 16 months old (within 18-month threshold) |
| Founded/co-founded by at least one African national | Arthur Manena & Mhlonipheni Ntleko — South African citizens |
| Tech component in product/business model | Core tech platform — Java 21, AWS serverless, AI/ML |
| Operations (costs or revenue) in an African country | Cape Town, South Africa — all development, infrastructure, and target market |
| Growth and impact potential on the continent | Pan-African expansion via adapter architecture (see below) |

**Note on geographic focus:** While VeriGate is headquartered in South Africa (one of the "Big 4" tech hubs), the platform's adapter-based architecture is purpose-built for continental expansion. The verification infrastructure gap is even more acute in the 18 priority countries where government digitisation is earlier-stage and fragmented verification causes greater economic harm. VeriGate's roadmap includes adapter development for Nigeria, Kenya, Ghana, Tanzania, and Rwanda — markets where Fuze's network and expertise would be directly valuable.

---

## PART 1: APPLICATION FORM RESPONSES

### 1.1 Company Name
Arthmatic DevWorks (Pty) Ltd

### 1.2 Product Name
VeriGate

### 1.3 Website
arthmatic.co.za

### 1.4 Country of Registration
South Africa

### 1.5 Date of Incorporation
21 December 2024 (~16 months old — within the 18-month threshold)

### 1.6 Sector
GovTech / RegTech / FinTech — Risk Intelligence & Identity Verification

### 1.7 Stage
Pre-seed — Working platform (TRL 6–7), pre-revenue

### 1.8 Funding Requested
€100,000

### 1.9 Founder(s)

**Arthur Manena — Co-Founder & CEO**
South African citizen. Full-stack architect and engineer with deep expertise in enterprise software, cloud-native architecture, and security-first design. Responsible for product vision, business strategy, commercial partnerships, and fundraising. Hands-on technical founder who understands both the code and the commercial opportunity.

Core competencies: Java/Spring Boot enterprise systems, AWS serverless architecture, security architecture, API design, AI/ML integration, .NET enterprise systems.

**Mhlonipheni Ntleko — Co-Founder & CTO**
BSc Computer Science. 15+ years of software engineering experience. Leads platform architecture, engineering execution, and technical strategy. Responsible for adapter development, infrastructure, and production readiness.

### 1.10 Team Size
2 co-founders (full-time). Hiring plan in place for 4 additional roles post-funding.

---

### 2. PROBLEM

Verifying a person or company's identity, tax compliance, property ownership, qualifications, and creditworthiness in South Africa — and across Africa — requires querying multiple disconnected government and private systems separately. Each agency has its own portal, its own credentials, its own data format, and its own downtime schedule. There is no unified, secure way to orchestrate these checks at scale.

**The consequences are severe:**

- **Fraud:** South Africa loses an estimated R30+ billion annually to identity fraud and grant fraud alone (National Treasury, SIU reports). Insurance fraud costs R7+ billion per year.
- **Invisible corruption:** When CIPC, DHA, property, and supply chain data live in separate silos, conflicts of interest are invisible. A procurement officer who is also a director of the bidding company cannot be detected without cross-referencing multiple sources in real time. The Auditor-General consistently flags undisclosed conflicts of interest as a root cause of irregular government expenditure.
- **Regulatory burden:** Banks spend R500M–R1B+ per year on fragmented KYC/AML compliance. A single end-to-end KYC/KYB check takes 3–5 business days involving multiple teams and manual processes.
- **SMME exclusion:** Slow verification stalls government contracts for small businesses, locking them out of the formal economy.

**The root cause is architectural:** there is no shared risk intelligence layer that respects data sovereignty while enabling real-time cross-agency verification and fraud detection.

---

### 3. SOLUTION

VeriGate is a real-time risk intelligence platform that routes verification requests to **14 authoritative sources** through a single API, returning standardised results with AI-powered risk scoring, automated conflict of interest detection, fraud detection, and full audit trails.

**How it works:**
1. A partner (bank, government department, law firm) submits a verification request via VeriGate's API — e.g., "verify this person's identity, company status, tax compliance, and check for sanctions."
2. VeriGate's command gateway routes the request to the relevant adapters (DHA for identity, CIPC for company, SARS for tax, OpenSanctions for sanctions).
3. Each adapter queries its authoritative source in real time and returns structured results.
4. The AI Risk Engine aggregates all results, cross-references CIPC directorships with shareholding data, property ownership, PEP databases, and sanctions lists to automatically detect conflicts of interest and hidden relationships.
5. The partner receives a single, auditable response with verification results and a composite risk score — in seconds, not days.

**14 Verification Types:**

| # | Type | Source | Status |
|---|---|---|---|
| 1 | Identity | DHA (Home Affairs) | Built |
| 2 | Company Registration | CIPC | Built |
| 3 | Bank Account | QLink | Built |
| 4 | Property Ownership | DeedsWeb | Built |
| 5 | Sanctions Screening | WorldCheck, OpenSanctions | Built |
| 6 | Tax Compliance | SARS | Built |
| 7 | Qualifications | SAQA | Built |
| 8 | Credit Check | Bureau (TransUnion/Experian) | Pending NCR registration |
| 9 | Employment | Employer databases | Built |
| 10 | Income | Payroll/bank data | Built |
| 11 | Document Verification | AI/OCR (AWS Bedrock) | Built |
| 12 | Negative News | Media screening | Built |
| 13 | Fraud Watchlist | Industry databases | Built |
| 14 | VAT Vendor | SARS VAT registry | Built |

**3 AI Modules (AWS Bedrock):**
- **Risk Signals Engine** — Aggregates results, detects anomalies, powers conflict of interest detection by cross-referencing directorships, shareholding, property, PEP status, and sanctions across sources.
- **Fraud Detector** — Pattern matching and ML models to detect synthetic identities, document tampering, and fraud rings.
- **Bedrock Common** — Shared AI infrastructure for document OCR, entity extraction, and NLP.

---

### 4. TECHNOLOGY & INNOVATION

**Tech Stack:**
- **Backend:** Java 21, Spring Boot 3.3 (enterprise-grade)
- **Compute:** AWS Lambda (serverless) — scales to zero, pay-per-use
- **Messaging:** SQS FIFO (ordered, exactly-once delivery)
- **Database:** DynamoDB (event store, partner config, billing)
- **AI/ML:** AWS Bedrock (document verification, risk scoring, fraud detection)
- **Event Streaming:** Kinesis, EventBridge
- **Identity:** AWS Cognito
- **Frontend (Partner Portal):** Next.js 15.5, React 19, TypeScript, Tailwind CSS
- **Architecture:** Clean Architecture, CQRS, event-driven, adapter-based

**What makes VeriGate technically innovative:**

1. **Cross-account deployment model** — Each participating agency gets its own AWS account. VeriGate orchestrates verification across accounts without ever centralising raw data. Data sovereignty is enforced by architecture, not by policy. This is a hard requirement for government adoption and extraordinarily difficult to retrofit.

2. **Adapter-based architecture** — Every verification source is a self-contained adapter module. Adding a new country's registries (e.g., Nigeria's NIMC, Kenya's eCitizen) means building adapters, not re-architecting the platform. This is how VeriGate scales across Africa without proportional engineering cost.

3. **Real-time conflict of interest detection** — No competitor in Africa offers automated cross-referencing of CIPC directorships, shareholding percentages, property ownership, PEP databases, and sanctions lists to surface hidden relationships in real time. This is VeriGate's flagship differentiator.

4. **Event-driven audit trail** — Every verification request, result, and decision is an immutable event — fully auditable, replayable, and analyzable. This meets the most stringent compliance requirements (POPIA, FICA, NCR).

---

### 5. MARKET OPPORTUNITY

**South Africa (Beachhead):**

| Segment | Annual Verification Spend | VeriGate Addressable |
|---|---|---|
| Banking & Financial Services | R2–4 billion | R800M–R1.5B |
| Insurance | R1–2 billion | R400M–R800M |
| Government | R1–3 billion | R500M–R1B |
| Property & Legal | R500M–R1B | R200M–R400M |
| Telecoms & Retail (RICA/FICA) | R500M–R1B | R200M–R400M |
| **Total SA TAM** | **R5–11 billion** | **R2.1–4.1 billion** |

**Africa (5-year horizon):**

| Market | TAM |
|---|---|
| South Africa | R4B ($220M) |
| Nigeria | $500M+ |
| East Africa (Kenya, Tanzania, Uganda, Rwanda) | $400M+ |
| West Africa (Ghana, Senegal, Cote d'Ivoire) | $300M+ |
| **Total Continental TAM** | **$1.7B+** |

**Why now:**
- POPIA enforcement is active — the Information Regulator is investigating and fining. VeriGate's architecture is POPIA-native.
- Government digitisation is accelerating (DHA modernisation, SARS eFiling, integrated service delivery).
- AfCFTA requires cross-border KYB/KYC for trade — VeriGate's adapter architecture is designed for this.
- SARB Guidance Note 7 demands multi-source verification that manual processes cannot deliver at scale.

---

### 6. BUSINESS MODEL

**SaaS per-verification pricing with subscription tiers.**

| Verification Type | Price per Check |
|---|---|
| Identity (DHA) | R8–R15 |
| Company (CIPC) | R10–R20 |
| Sanctions Screening | R5–R10 |
| Property (DeedsWeb) | R15–R30 |
| Document Verification (AI/OCR) | R15–R35 |
| Full KYC Bundle (5+ checks) | R40–R80 |
| Full KYB Bundle (company + directors) | R60–R120 |

**Subscription Tiers:**

| Tier | Monthly Fee | Included Verifications |
|---|---|---|
| Starter | R5,000 | 500 |
| Professional | R25,000 | 3,000 |
| Enterprise | R75,000+ | 15,000+ |
| Government | Custom | Custom |

**Unit Economics:** 65–80% gross margin per verification (cost = API fees to source agencies + compute).

**Revenue Projections:**

| Period | Customers | Monthly Verifications | MRR | ARR |
|---|---|---|---|---|
| Month 6 | 2–3 pilots | 20K–50K | R80K–R200K | R1M–R2.4M |
| Month 12 | 5–8 paying | 100K–300K | R400K–R1.2M | R5M–R14M |
| Month 24 | 15–25 | 500K–1.5M | R2M–R6M | R24M–R72M |

---

### 7. TRACTION & MILESTONES

| Milestone | Status |
|---|---|
| Core platform architecture designed and built | Done |
| 10 of 14 verification adapters live | Done |
| Cross-account AWS deployment model validated | Done |
| 3 AI modules built and demonstrated | Done |
| Partner portal with visual policy builder shipped | Done |
| Event-driven audit trail operational | Done |
| ISO 27001 certification | In progress |
| NCR registration (credit bureau access) | In progress |
| First pilot customer | Actively pursuing |
| Revenue | Pre-revenue |

**Co-investment to date:** Arthmatic DevWorks has invested approximately **R800,000 (~€40,000)** in VeriGate development, comprising:
- 12+ months of full-time engineering effort
- AWS infrastructure costs during development and testing
- 10 functional adapter modules
- 3 AI modules developed and trained
- Partner portal design and development

---

### 8. GROWTH & IMPACT POTENTIAL

**Growth trajectory:**
- **Year 1:** R900K ARR — ISO 27001 certified, NCR registered, 2+ pilot customers, all 14 adapters functional
- **Year 2:** R4.2M ARR — 5–8 enterprise/government paying customers, adapter development for first non-SA market (Nigeria or Kenya)
- **Year 3:** R13.2M ARR — 15+ customers, 2+ African markets live, Series A readiness

**Continental scalability:**
VeriGate's adapter architecture is the key to pan-African expansion. The core platform (API layer, AI modules, partner portal, event infrastructure) remains identical. Entering a new market means building adapters for local registries — Nigeria's NIMC and CAC, Kenya's eCitizen and KRA, Ghana's NIA and Registrar General. Each new country is an incremental engineering effort, not a rebuild.

**Impact on the continent:**
- **Anti-corruption:** Automated conflict of interest detection directly reduces procurement fraud. When a government department can instantly see that a bidding company's director is related to the procurement officer, corruption becomes harder to hide.
- **Financial inclusion:** Faster, cheaper KYC lowers the barrier for banks and fintechs to onboard customers in underserved communities. A verification that takes 5 days and costs R500 today could take 5 seconds and cost R40 through VeriGate.
- **SMME empowerment:** Small businesses waiting weeks for government contract verifications can be cleared in minutes, unlocking working capital and economic participation.
- **Job creation:** VeriGate's hiring plan includes 4 roles in Year 1, growing to 10+ as the platform scales across Africa.
- **Women-led business verification:** VeriGate can be configured to track and report on verification of women-owned businesses, supporting gender-focused procurement targets across African governments.

---

### 9. USE OF FUNDS (€100,000)

| Category | Amount (€) | % | Purpose |
|---|---|---|---|
| ISO 27001 Certification | €15,000 | 15% | Mandatory for government and financial services contracts. Includes consulting, implementation, and certification body audit. |
| NCR Registration | €5,000 | 5% | National Credit Regulator registration to unlock credit bureau adapter (14th verification type). |
| Production Hardening | €20,000 | 20% | Complete remaining adapter unit tests, query-side implementation, observability, security hardening, and load testing. |
| Go-to-Market | €25,000 | 25% | Enterprise sales capacity — hire part-time sales lead, attend industry events (GovTech Summit, BASA conference), produce sales collateral, travel for government department meetings. |
| Pilot Customer Support | €15,000 | 15% | Dedicated engineering time and infrastructure for onboarding first 2–3 pilot customers. |
| Africa Expansion Research | €10,000 | 10% | Market research and initial adapter scoping for Nigeria (NIMC, CAC) and Kenya (eCitizen, KRA) — groundwork for continental expansion. |
| Operating Costs | €10,000 | 10% | AWS infrastructure, domain/hosting, legal, and administrative costs for 12 months. |
| **Total** | **€100,000** | **100%** | |

---

### 10. COMPETITIVE LANDSCAPE

| Competitor | What They Do | VeriGate Advantage |
|---|---|---|
| XDS, TransUnion, Experian | Single-source credit checks | VeriGate aggregates 14 sources with AI risk scoring — credit is one input, not the whole picture |
| Idenfo / Smile Identity | Identity verification (ID + biometrics) | VeriGate goes beyond identity — company, property, tax, sanctions, conflict of interest detection |
| Refinitiv WorldCheck | Sanctions/PEP screening | VeriGate integrates WorldCheck as one adapter among 14, cross-referencing results across all sources |
| Manual processes (status quo) | Government portals, spreadsheets, phone calls | VeriGate replaces days of manual work with seconds of automated, auditable verification |

**No competitor in Africa offers real-time cross-referencing of CIPC directorships, shareholding, property ownership, and PEP databases to surface hidden conflicts of interest. This is VeriGate's flagship capability.**

---

### 11. WHY FUZE / DIGITAL AFRICA

VeriGate is seeking Fuze investment specifically because:

1. **Pan-African expansion alignment:** VeriGate's adapter architecture is designed for exactly the markets Digital Africa champions. The verification infrastructure gap is even more acute in emerging African tech ecosystems. Fuze's network across 18 priority countries directly accelerates VeriGate's continental roadmap.

2. **Portfolio synergy:** Every startup in the Fuze/Digital Africa portfolio that handles customer onboarding, KYC, lending, insurance, or government contracts is a potential VeriGate customer or integration partner. Verification is horizontal infrastructure.

3. **Impact multiplier:** VeriGate is infrastructure that makes other African startups more compliant, more secure, and faster to market. Investing in VeriGate compounds impact across the ecosystem.

4. **Smart capital at the right stage:** VeriGate has done the hardest part — building a working, enterprise-grade platform with zero external funding. What we need now is not more engineering time — it is certification, commercial traction, and continental network. That is exactly what Fuze provides.

---

## PART 2: PITCH DECK OUTLINE (12 Slides)

### Slide 1 — Title
**VeriGate**
Real-Time Risk Intelligence for Africa
*Cross-agency verification. AI-powered risk scoring. Fraud detection. Zero centralised data.*
Arthmatic DevWorks | Cape Town

### Slide 2 — The Problem
- Verifying people and companies across Africa requires querying 6–10 disconnected government systems manually
- R30B+ lost to identity fraud annually in SA alone; R7B+ to insurance fraud
- Conflicts of interest invisible when data lives in silos — Auditor-General flags this every year
- 3–5 business days for a single KYC check; banks spend R500M–R1B/year on fragmented compliance
- **Visual:** Disconnected agency logos (DHA, CIPC, SARS, SAQA, Deeds) with broken links between them

### Slide 3 — The Solution
- One API. 14 verification types. 3 AI modules. Real-time results.
- Submit a request → VeriGate routes to relevant sources → AI cross-references results → single auditable response in seconds
- **Visual:** Simple flow diagram: Partner → VeriGate API → Adapters (DHA, CIPC, SARS, etc.) → AI Risk Engine → Response

### Slide 4 — How It Works (Architecture)
- Adapter-based: each source is a plug-in module — add a country by adding adapters, not rebuilding
- Cross-account AWS: each agency keeps its own data — sovereignty by architecture, not policy
- Event-driven: every verification is an immutable, auditable event
- **Visual:** Architecture diagram showing cross-account model with adapters

### Slide 5 — Conflict of Interest Detection (Flagship Feature)
- Real-time cross-referencing of CIPC directorships + shareholding + property + PEP + sanctions
- Example: "Bidding company's director is married to procurement officer" — detected automatically
- No competitor in Africa offers this
- **Visual:** Network graph showing detected relationship between entities

### Slide 6 — Market Opportunity
- SA TAM: R5–11B across banking, insurance, government, property, telecoms
- Africa TAM: $1.7B+ (Nigeria $500M+, East Africa $400M+, West Africa $300M+)
- **Why now:** POPIA enforcement, government digitisation, AfCFTA, SARB Guidance Note 7
- **Visual:** Map of Africa with market sizes by region

### Slide 7 — Business Model
- Per-verification pricing: R8–R120 per check depending on type
- Subscription tiers: R5K–R75K+/month
- 65–80% gross margin
- Enterprise contracts are sticky — switching verification providers is painful
- **Visual:** Pricing table + unit economics

### Slide 8 — Traction
- Platform at TRL 6–7 — this is a working product, not a prototype
- 10 of 14 adapters built and integration-tested
- 3 AI modules operational
- Partner portal with visual policy builder shipped
- R800,000 (~€40K) self-funded to date
- ISO 27001 and NCR registration in progress
- **Visual:** Milestone timeline

### Slide 9 — Revenue Projections
- Month 6: R80K–R200K MRR (2–3 pilots)
- Month 12: R400K–R1.2M MRR (5–8 customers)
- Month 24: R2M–R6M MRR (15–25 customers)
- **Visual:** Revenue growth chart

### Slide 10 — Competitive Advantage
- First-mover in unified real-time risk intelligence for Africa
- Conflict of interest detection — no competitor offers this
- Adapter architecture = continental scalability without proportional cost
- Data sovereignty by design = government trust
- **Visual:** Competitive matrix (VeriGate vs XDS, Idenfo, Smile Identity, WorldCheck, manual)

### Slide 11 — Use of Funds (€100K)
- 15% ISO 27001 certification
- 5% NCR registration
- 20% Production hardening
- 25% Go-to-market
- 15% Pilot customer support
- 10% Africa expansion research
- 10% Operating costs
- **Visual:** Pie chart

### Slide 12 — The Ask
- €100,000 Fuze investment
- Certification → first customers → revenue → Series A in 18–24 months
- VeriGate is infrastructure that makes every African startup and government department more secure, more compliant, and faster
- **Contact:** Arthur Manena | arthur@arthmatic.co.za | arthmatic.co.za

---

## PART 3: 3-MINUTE VIDEO PITCH SCRIPT

**[Duration: ~3 minutes | Tone: confident, direct, no hype]**

---

**[0:00–0:20] — Hook**

"Imagine you are a government procurement officer in South Africa. A company bids on a R50 million contract. You need to verify that the company is real, tax compliant, not sanctioned, and that none of its directors have a hidden relationship with anyone in your department. To do that today, you open six different government portals, manually cross-reference data in spreadsheets, and hope you do not miss anything. It takes days. And you will miss things. That is the problem VeriGate solves."

**[0:20–0:50] — What VeriGate Is**

"VeriGate is a real-time risk intelligence platform. One API that connects to fourteen authoritative data sources — Home Affairs for identity, CIPC for company registration, SARS for tax, the Deeds Office for property, SAQA for qualifications, sanctions databases, credit bureaus, and more.

A partner submits a verification request. VeriGate routes it to the relevant sources, collects the results, and then does something no other platform in Africa can do: it cross-references everything in real time using AI. CIPC directorships, shareholding data, property ownership, PEP databases, sanctions lists — all correlated automatically to detect conflicts of interest and hidden relationships. The partner gets a single, auditable response with a composite risk score in seconds."

**[0:50–1:20] — The Architecture (Why It Matters)**

"But here is what makes VeriGate truly different: the architecture. Every data source is a self-contained adapter module. Adding a new source — or a new country — means building one adapter, not rebuilding the platform. And critically, there is no centralised database. Each agency retains full control of its own data in its own environment. Data sovereignty is enforced by architecture, not by policy. This is not a nice-to-have — it is the hard requirement for government adoption, and it is extraordinarily difficult to retrofit into a platform that was not designed for it from day one. VeriGate was."

**[1:20–1:50] — Market & Why Now**

"South Africa's verification and risk intelligence market is worth R5 to R11 billion annually across banking, insurance, government, property, and telecoms. Across Africa, the total addressable market exceeds 1.7 billion US dollars. And the timing is right: POPIA enforcement is active, government digitisation is accelerating, the African Continental Free Trade Area needs cross-border KYC, and the Reserve Bank is demanding multi-source verification that manual processes simply cannot deliver at scale."

**[1:50–2:20] — Traction & Business Model**

"VeriGate is not a slide deck. It is a working platform at technology readiness level six to seven. Ten of fourteen adapters are live and tested. Three AI modules are operational. The partner portal with a visual policy builder is shipped. We have invested approximately 800,000 Rand of our own capital to get here — twelve-plus months of engineering, infrastructure costs, and AI development — with zero external funding.

The business model is SaaS: per-verification pricing ranging from 8 to 120 Rand per check, with subscription tiers for enterprise customers. Gross margins of 65 to 80 percent. Enterprise contracts are inherently sticky — once you integrate a verification API into your compliance workflow, you do not switch it out casually."

**[2:20–2:50] — The Ask & Impact**

"We are requesting €100,000 from Fuze. This funds ISO 27001 certification — mandatory for government and banking contracts — NCR registration for credit bureau access, production hardening, go-to-market, and initial research for expanding into Nigeria and Kenya.

The impact is direct: every conflict of interest VeriGate detects is a corrupt procurement deal prevented. Every KYC check completed in seconds instead of days is a small business unblocked, a customer onboarded, an economy made more efficient. VeriGate is infrastructure. It makes every bank, insurer, law firm, and government department in Africa more secure, more compliant, and faster. And it makes every other startup in the ecosystem that depends on verification more viable."

**[2:50–3:00] — Close**

"My name is Arthur Manena. Together with my co-founder Mhlonipheni Ntleko, we built VeriGate at Arthmatic DevWorks in Cape Town. The platform is built. The market is ready. We are looking for the right partner to take it to market — and across the continent. Thank you."

---

*Prepared for Fuze (Digital Africa) application, April 2026.*
*Arthmatic DevWorks (Pty) Ltd | Cape Town, South Africa*
