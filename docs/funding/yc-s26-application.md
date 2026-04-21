# Y Combinator Summer 2026 Application — VeriGate

**Deadline:** May 4, 2026 at 8:00 PM Pacific Time
**Decision Date:** By June 5, 2026
**Company:** Arthmatic DevWorks (Pty) Ltd
**Product:** VeriGate
**Co-Founders:** Arthur Manena (CEO) & Mhlonipheni Ntleko (CTO)
**Apply at:** https://apply.ycombinator.com/home

---

## COMPANY INFORMATION

### Company Name
Arthmatic DevWorks

### Company Description (50 characters max)
```
Real-time risk intelligence API for Africa
```
(43 characters)

### Company URL
arthmatic.co.za

### Demo URL
[Insert demo link if available before submission]

### What is your company going to make?

VeriGate is a single API that connects to 14 government and private verification sources in South Africa — identity, company registration, tax, property, sanctions, qualifications, credit, and more — and returns a unified risk assessment in seconds instead of days. Our AI engine cross-references results across all sources to automatically detect conflicts of interest and hidden relationships that no single-source check can find. A bank running KYC, an insurer underwriting a policy, or a procurement team vetting a supplier sends one API call and gets back a composite risk score with full audit trail.

We charge R8–R120 per verification (roughly $0.50–$7) with 65–80% gross margins. Every verification source is a self-contained adapter module, so expanding to Nigeria, Kenya, or any African country means building adapters for local registries — not rebuilding the platform. We have 10 of 14 adapters live today, 3 AI modules operational, and a partner portal shipped. R800,000 (~$44K) self-funded with zero external capital.

### Company Location
Cape Town, South Africa. After YC, we would establish a US entity (Delaware C-Corp) as the parent company and maintain our Cape Town engineering office.

### Company Category
B2B / FinTech / RegTech

---

## FOUNDER INFORMATION

### Founder 1: Arthur Manena — Co-Founder & CEO

**Email:** arthur@arthmatic.co.za
**LinkedIn:** [Insert LinkedIn URL]
**Role:** CEO — Product vision, business strategy, commercial partnerships, fundraising. Also hands-on architect and full-stack engineer.

### Founder 2: Mhlonipheni Ntleko — Co-Founder & CTO

**Email:** [Insert email]
**LinkedIn:** [Insert LinkedIn URL]
**Role:** CTO — Platform architecture, engineering execution, technical strategy, adapter development, infrastructure, and production readiness.
**Education:** BSc Computer Science
**Experience:** 15+ years software engineering

### Please tell us in one or two sentences about something impressive that each founder has built or achieved.

**Arthur Manena:** Built VeriGate's entire platform — 14 verification adapters, 3 AI modules, cross-account AWS deployment, partner portal — from zero to working system in 12 months with no external funding. Won the 2025 Old Mutual SMEGO Pitchathon.

**Mhlonipheni Ntleko:** 15+ years engineering enterprise-grade systems across financial services and insurance. Architected and shipped production platforms handling millions of transactions for some of South Africa's largest institutions.

### How long have the founders known one another and how did you meet?

[Arthur — please insert: how long you've known Mhlonipheni, how you met, and what your working relationship has been like. Example: "We've known each other for X years. We met through [context] and have worked together on [projects/companies]. We decided to co-found Arthmatic DevWorks because [reason]."]

### Please tell us about an interesting project, preferably outside of class or work, that two or more of you created together.

[Arthur — please insert: a specific project you and Mhlonipheni built together outside of your day jobs. This could be VeriGate itself (built entirely on your own time/capital), a side project, an open-source contribution, or anything that shows you work well together and ship things. YC wants to see that you collaborate effectively and can execute.]

### Please tell us about the time you most successfully hacked some (non-computer) system to your advantage.

We were trying to get access to South African government verification APIs — CIPC, DHA, SARS — as a pre-revenue startup with no enterprise credentials. These agencies do not have self-service developer portals. The official path requires enterprise contracts, compliance certifications, and months of procurement paperwork.

So we reverse-engineered the public-facing web portals, studied the API patterns behind them, built adapter modules that could interface with the data sources, and validated our architecture against real data structures — all without waiting for formal partnerships. This let us prove the platform works end-to-end, which is the leverage we now use to negotiate actual API agreements from a position of demonstrated capability rather than a slide deck.

We also won the 2025 Old Mutual SMEGO Pitchathon — not by pitching a vision, but by demonstrating a working conflict-of-interest detection system live on stage. The judges could see it was real. That credibility signal opened doors that cold emails never would have.

### Who writes code, or does other technical work on your product? Was any of it done by a non-founder?

Both founders write code. Arthur built the core platform architecture, adapter framework, AI modules, and partner portal. Mhlonipheni leads adapter development, infrastructure, and production hardening. No non-founder has contributed code. The entire platform — backend (Java 21, Spring Boot 3.3), frontend (Next.js 15.5, React 19), AI modules (AWS Bedrock), and infrastructure (Lambda, DynamoDB, SQS, EventBridge) — was built by the two of us.

### How long has each of you been working on this? How much of that has been full-time?

We incorporated Arthmatic DevWorks on 21 December 2024 — about 16 months ago. Arthur has been working on VeriGate full-time for the past 12+ months. Mhlonipheni has been contributing consistently alongside his engineering work, transitioning to full-time as we approach go-to-market.

### Are you looking for a cofounder?
No.

---

## PROGRESS & TRACTION

### How far along are you?

Working platform at Technology Readiness Level 6–7. This is not a prototype — it is a functional system:

- 10 of 14 verification adapters built and integration-tested (DHA identity, CIPC company, SARS tax, DeedsWeb property, sanctions screening, SAQA qualifications, bank account, employment, income, document verification via AI/OCR, negative news, fraud watchlist, VAT vendor). The remaining adapter (credit bureau) is pending NCR registration.
- 3 AI modules operational: Risk Signals Engine (cross-source anomaly detection and conflict of interest identification), Fraud Detector (synthetic identity and document tampering detection), and Bedrock Common (OCR and entity extraction).
- Partner portal shipped with visual policy builder, real-time dashboard, API key management, and audit trail access.
- Cross-account AWS deployment model validated — each participating agency retains its own data in its own AWS account. Data sovereignty enforced by architecture.
- Event-driven audit trail operational — every verification is an immutable, replayable event.

### When will you have a prototype or beta?
We already have a working platform. We are in the go-to-market phase — the next milestone is landing our first paying pilot customers.

### Are people using your product?
Not yet in production. We have demonstrated the platform to potential customers and at pitch events (including the 2025 Old Mutual SMEGO Pitchathon, which we won). We are actively pursuing pilot agreements with financial services companies and insurers.

### How many active users or customers do you have? How many are paying?
0 paying customers. Pre-revenue. We have been focused on building a production-grade platform before taking it to market — we did not want to sell something half-built to enterprise customers who expect reliability.

### Who is paying you the most, and how much do they pay you?
N/A — pre-revenue.

### Revenue over the last several months
$0. Pre-revenue.

### What is your monthly growth rate?
N/A — pre-revenue. Our growth metric at this stage is platform completeness: we went from 0 to 10 functional adapters and 3 AI modules in 12 months.

---

## IDEA & MARKET

### Why did you pick this idea to work on? Do you have domain expertise in this area? How do you know people need what you're making?

We picked this because we lived the problem. Working in enterprise software for financial services and insurance companies in South Africa, we saw firsthand how verification is done: manually, across disconnected government portals, with spreadsheets to cross-reference results. A single KYC/KYB check takes 3–5 business days and involves multiple teams. Banks spend R500M–R1B+ per year on this. Insurance fraud costs R7B+ per year, much of it preventable with better cross-source verification. The Auditor-General flags undisclosed conflicts of interest as a root cause of irregular government expenditure every single year.

The problem is not that individual data sources are unavailable — it is that nobody has built the aggregation and intelligence layer that connects them, cross-references results, and surfaces risk in real time while respecting data sovereignty. We have 15+ years of combined experience building enterprise systems for exactly these customers. We know what they need, we know what their compliance teams demand, and we know what their IT procurement processes look like.

We also know people need this because we won a national pitchathon with it. When we demonstrated live conflict-of-interest detection — showing how VeriGate automatically surfaces that a bidding company's director is related to the procurement officer — the reaction was immediate. Every judge asked "when can we buy this?"

### What do you understand about your business that other companies in it just don't get?

Verification in Africa is not a data problem — it is an architecture problem. The data exists across government registries, credit bureaus, sanctions databases, and corporate records. The missing piece is the intelligence layer that connects these sources, cross-references them in real time, and respects the non-negotiable constraint that nobody — not the government, not the customer, not us — is willing to centralise all this sensitive data in one place.

Every competitor we have seen either (a) does single-source checks (credit bureau or identity only — no cross-referencing), (b) centralises data (which governments and regulators will never accept at scale), or (c) requires manual integration work for each customer.

VeriGate's cross-account deployment model — where each agency keeps its data in its own environment and VeriGate orchestrates across accounts — is the architectural decision that unlocks government and enterprise trust. It is also extraordinarily difficult to retrofit into a platform that was not designed for it from day one. That is our moat: not a feature, but a fundamental architectural choice that would take a competitor years to replicate.

### Who are your competitors?

**Direct competitors in Africa (single-source or narrow-scope):**
- **XDS / TransUnion / Experian:** Credit checks only. They do not cross-reference across identity, company, property, tax, and sanctions. VeriGate treats credit as one of 14 inputs.
- **Idenfo / Smile Identity:** Identity verification (ID + biometrics). They stop at "is this person who they say they are?" VeriGate asks "is this person a risk?" by cross-referencing identity with company directorships, property, sanctions, and PEP databases.
- **Refinitiv WorldCheck:** Sanctions and PEP screening. VeriGate integrates WorldCheck as one adapter among 14 and adds cross-referencing that WorldCheck alone cannot do.

**Indirect competitors (global, not Africa-focused):**
- **Plaid / Middesk / Alloy (US):** Verification infrastructure for US financial services. They do not operate in Africa and have no adapters for African government registries.

**The real competitor is the status quo:** manual processes, government portal hopping, spreadsheets, and phone calls. This is what 90%+ of the market uses today.

**Our differentiation:** No competitor in Africa offers automated, real-time cross-referencing of CIPC directorships, shareholding, property ownership, PEP databases, and sanctions lists to surface hidden conflicts of interest. This is VeriGate's flagship capability and the feature that won us the Old Mutual SMEGO Pitchathon.

### How will you make money?

Per-verification pricing with subscription tiers. Simple and predictable for customers.

- **Per-check pricing:** R8–R120 ($0.50–$7) depending on verification type. Identity checks are cheapest; full KYB bundles (company + all directors + tax + sanctions + property) are most expensive.
- **Subscription tiers:** R5,000/month (500 checks) to R75,000+/month (15,000+ checks) for enterprise customers.
- **Gross margin:** 65–80% per verification. Our cost is the API fee to the source agency plus compute.
- **Expansion revenue:** Customers start with 2–3 verification types and add more over time. A bank that starts with identity + CIPC will naturally add sanctions + tax + property as they see the value of cross-referencing.
- **Switching costs are high:** Once an enterprise integrates our API into its compliance workflow, switching is expensive and risky. Verification is embedded in critical business processes.

### How will you get users?

**Phase 1 (Months 1–6): Direct enterprise sales.**
Our beachhead is mid-tier financial services companies and insurers in South Africa — large enough to have a real verification spend (R100K+/month) but small enough to not have built their own systems. We go to them with a working demo, not a pitch deck, and offer a 30-day pilot at discounted rates. Once they see verification time drop from days to seconds, conversion to paid is straightforward.

**Phase 2 (Months 6–12): Channel partnerships.**
System integrators who sell to government and enterprise (e.g., Accenture, Deloitte, PwC advisory) can resell VeriGate as part of compliance modernisation projects. They get a solution to offer; we get distribution without hiring a 50-person sales team.

**Phase 3 (Months 12–24): Self-service API portal.**
Smaller companies (law firms, fintechs, property groups) can sign up, get API keys, and start verifying without talking to sales. This is the long-tail volume play.

We also have credibility signals that accelerate sales: the Old Mutual SMEGO Pitchathon win, a working platform (not vaporware), and — if we get into YC — the strongest credibility signal in global startup ecosystems.

---

## LEGAL & FUNDING

### Have you formed any legal entity yet?
Yes. Arthmatic DevWorks (Pty) Ltd, registered in South Africa (CIPC), incorporated 21 December 2024.

### Please describe your legal structure.
South African private company (Pty) Ltd. If accepted to YC, we will incorporate a Delaware C-Corp as the parent entity, with the South African company becoming a wholly-owned subsidiary. This is standard for international YC companies.

### Equity breakdown among founders, employees, and other stockholders.

[Arthur — please insert the actual equity split between you and Mhlonipheni. Example format:]

- Arthur Manena (CEO): [X]%
- Mhlonipheni Ntleko (CTO): [X]%
- Unallocated/ESOP pool: [X]% (if applicable)
- No external investors. No employees with equity.

### Have you taken any investment yet?
No external investment. We have self-funded R800,000 (~$44,000 / ~€40,000) from personal capital. This covers 12+ months of full-time engineering, AWS infrastructure costs during development, and all platform development to date.

### How much money do you spend per month?
[Arthur — please insert actual monthly burn rate. Rough estimate based on the self-funding: if R800K over ~16 months, that is approximately R50,000/month (~$2,800/month). Adjust to reflect actual current spend including AWS costs, any tools, etc.]

### How much money does your company have in the bank now?
[Arthur — please insert actual bank balance.]

### Have you participated in any incubator, accelerator, or pre-accelerator programme?
No. We have not participated in any accelerator or incubator programme. VeriGate was built entirely independently.

---

## YC-SPECIFIC QUESTIONS

### What convinced you to apply to Y Combinator?

Two things. First, VeriGate is infrastructure — it is the kind of company that compounds in value as more sources connect and more customers integrate. YC has the best track record of scaling infrastructure companies (Stripe, Plaid, Brex). We want partners who understand that our first 3 customers matter more than our first 3 million in revenue, because each integration proves the model and makes the next sale easier.

Second, credibility. We are selling to banks, insurers, and procurement teams in Africa. These buyers are risk-averse by nature. "YC-backed" is the single strongest credibility signal we could carry into those conversations. It compresses our sales cycle from months to weeks.

### How did you hear about Y Combinator?
Through the global startup ecosystem. YC's reputation in Africa's tech community is well-known, and several African YC alumni companies (Paystack, Flutterwave, 54gene) have demonstrated that YC works for Africa-focused infrastructure companies.

### Have you applied to YC before?
No, this is our first application.

### If you had any other ideas you considered applying with, please list them.
No. VeriGate is the only idea we considered. We have been building this for over a year and have invested R800K of our own money. This is not a side project — it is the company.

---

## AI SAFETY DISCLOSURE (150 characters max)

```
VeriGate uses AWS Bedrock for document OCR, entity extraction, and risk scoring. No autonomous decisions — AI augments human review with audit trails.
```
(150 characters)

---

## 1-MINUTE VIDEO SCRIPT

**[Duration: exactly 60 seconds. Both founders on camera. Natural, direct, no slides.]**

**Arthur (0:00–0:25):**
"Hi, I'm Arthur Manena, CEO of Arthmatic DevWorks. This is my co-founder Mhlonipheni Ntleko, our CTO. We built VeriGate — a single API that connects to 14 government and private verification sources in South Africa and returns a unified risk assessment in seconds.

Right now, if a bank wants to verify a customer's identity, company status, tax compliance, and check for sanctions, they query four different systems manually. It takes days. VeriGate does it in one API call. But the real magic is what happens next."

**Mhlonipheni (0:25–0:45):**
"Our AI engine cross-references all results in real time. It checks CIPC directorship records against shareholding data, property ownership, PEP databases, and sanctions lists — automatically. It detects conflicts of interest that are invisible when you check each source separately. No other platform in Africa does this. We demonstrated it live and won the 2025 Old Mutual SMEGO Pitchathon."

**Arthur (0:45–0:60):**
"We have 10 of 14 adapters live, 3 AI modules operational, and a partner portal shipped. We self-funded R800,000 to build this with no external capital. Every verification source is an adapter — adding a new country means building adapters, not rebuilding the platform. That is how we scale across Africa. We are ready for customers. We are ready for YC."

---

## ADDITIONAL NOTES FOR SUBMISSION

### Key Positioning Reminders
- **Frame as RegTech/FinTech infrastructure**, not GovTech. Primary market: financial services, insurance, private companies. Government is a secondary market.
- **Emphasise the architecture** (adapter-based, cross-account, data sovereignty by design) — this is the moat.
- **Conflict of interest detection** is the flagship feature that differentiates from every competitor. Lead with it.
- **Self-funded R800K** with a working platform is the strongest signal of execution ability.
- **Old Mutual SMEGO Pitchathon win** is a concrete credibility marker.

### Before Submitting — Checklist
- [ ] Fill in equity split between Arthur and Mhlonipheni
- [ ] Fill in "How long have founders known each other" answer
- [ ] Fill in "Project created together" answer
- [ ] Fill in monthly burn rate
- [ ] Fill in bank balance
- [ ] Insert LinkedIn URLs for both founders
- [ ] Insert Mhlonipheni's email address
- [ ] Insert demo URL (if available)
- [ ] Record 1-minute video (both founders on camera, upload as unlisted YouTube video)
- [ ] Incorporate Delaware C-Corp or confirm plan to do so
- [ ] Review all character limits against actual YC form before pasting

### Incorporation Note
YC requires companies incorporated outside the US, Canada, Singapore, or Cayman Islands to create a parent company in one of those jurisdictions. The South African Pty Ltd becomes a subsidiary. Plan to incorporate a Delaware C-Corp before or shortly after acceptance. Services like Stripe Atlas or Clerky can handle this for $500–$2,000.

---

*Prepared for Y Combinator Summer 2026 application. Deadline: May 4, 2026 at 8:00 PM PT.*
*Arthmatic DevWorks (Pty) Ltd | Cape Town, South Africa*
