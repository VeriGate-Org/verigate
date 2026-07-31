# Story 2.1 — Datanamix Capability Gap Analysis

## Source

Jira story: **2.1 — Evaluate Datanamix API Capabilities & Map Coverage Gaps** (Priority: High, 5 points, assignee role R2).

> Full per-product technical reference, pricing tables, and sandbox verification logs live in the appendix: **`docs/datanamix-story-2-1-product-catalogue.md`**. This doc covers the decision-relevant findings only.

## Task Status

| # | Task | Role | Status |
| --- | --- | --- | --- |
| 1 | Obtain Datanamix API docs, sandbox credentials, service catalogue | R4 | **Done** — sandbox auth confirmed and live-tested against 9 real endpoints, 12 products fully documented, VeriGate confirmed to already hold a Datanamix API account. |
| 2 | Audit VeriGate verification types, identify checks with no direct source integration | R2 | **Done** |
| 3 | Map Datanamix verification types against gaps → coverage matrix | R2 | **Done** — see Coverage Matrix below |
| 4 | Review pricing, SLAs, POPIA compliance posture | R4 + R1 | **In progress, close to done.** Full pricing across all 19 categories obtained. Both Datanamix's and VeriGate's compliance postures documented at policy level. Remaining: a signed DPA and real SLA numbers — both gated behind starting Datanamix's Master Services Agreement (MSA) process, which is a business/legal action, not something more doc review resolves. |
| 5 | Decision gate: approve Datanamix as gap-fill provider | R1 | Not started — depends on #4 |

## Executive Summary

VeriGate has 7 verification types with no real data source behind them (`CREDIT_CHECK`, `EMPLOYMENT_VERIFICATION`, `INCOME_VERIFICATION`, `FRAUD_WATCHLIST_SCREENING`, `TAX_COMPLIANCE_VERIFICATION`, `DOCUMENT_VERIFICATION`, `BIOMETRIC_VERIFICATION`/`LIVENESS_CHECK`). Datanamix, evaluated in depth against 12 of its products (live-tested where possible, not just docs-reviewed), **covers or likely covers 6 of those 7** — including `BIOMETRIC_VERIFICATION`/`LIVENESS_CHECK`, which sat outside the original "credit bureau" framing of this story but turned out to have strong dedicated coverage (FaceTec liveness + HANIS photo-matching). Only `TAX_COMPLIANCE_VERIFICATION` has no Datanamix match.

Two things elevate this beyond a simple gap-fill decision: Datanamix resells **Experian and TransUnion** directly, matching VeriGate's existing (currently unused) multi-bureau data model almost exactly — one integration could cover most of the SA credit bureau market. And VeriGate's account **already has 12 Datanamix products enabled**, including a purpose-built adverse-media product that would meaningfully upgrade the currently generic-news-API-backed `NEGATIVE_NEWS_SCREENING` — yet nothing Datanamix-related exists anywhere in the codebase. That's worth separate attention regardless of this story's outcome.

The technical case is strong and well-evidenced. What's left is entirely commercial/legal: a signed DPA and real SLA numbers, both gated behind starting Datanamix's MSA process.

## Gaps Identified (Task 2)

Verification types with no real external data source wired, found by checking each adapter's actual configured endpoint (see appendix Methodology):

1. `CREDIT_CHECK` — no credit bureau wired despite the domain model anticipating Experian/TransUnion/Compuscan.
2. `EMPLOYMENT_VERIFICATION` — no employer/payroll source wired.
3. `INCOME_VERIFICATION` — no income/affordability source wired.
4. `FRAUD_WATCHLIST_SCREENING` — no fraud/ID-theft watchlist source wired.
5. `TAX_COMPLIANCE_VERIFICATION` — no SARS tax compliance source wired.
6. `DOCUMENT_VERIFICATION` — no document authenticity/OCR vendor wired.
7. `BIOMETRIC_VERIFICATION` / `LIVENESS_CHECK` — unimplemented, no adapter at all.

Two unrelated platform bugs surfaced from the same routing-table audit — flagged here, but out of this story's scope; recommend filing as separate tickets:
- `VERIFICATION_OF_PERSONAL_DETAILS` routes to a WorldCheck adapter that no longer exists in the codebase — will fail at runtime if ever dispatched.
- `PROPERTY_OWNERSHIP_VERIFICATION`'s live handler is still OpenSanctions-shaped, not deeds-shaped (tracked in `docs/rfp-deeds-gap-analysis.md`).

## Coverage Matrix (Task 3)

| VeriGate gap | Datanamix coverage | Verdict | Notes |
| --- | --- | --- | --- |
| `CREDIT_CHECK` | XDS Consumer Credit Report (adverse history + scoring), Affordability Assessment's debt summary, Express Consumer Score (cheap triage) — plus direct Experian/TransUnion resale | **Covered** | Strongest-evidenced gap-fill. Recommend triaging via Express Consumer Score before escalating to a full report, per Datanamix's own cost-optimization design. None yet `Enabled` on VeriGate's account. |
| `INCOME_VERIFICATION` | Affordability Assessment's `ConsumerAffordability` block | **Covered** | Direct, confirmed match. Not yet `Enabled`. |
| `LIVENESS_CHECK` | FaceTec (ZoOm) `liveness-3d`/`liveness-2d` | **Covered** | Only Datanamix product that proves a live person was present (iBeta/NIST/ISO-certified). Separate host/auth (Basic Auth) from the rest of the API. |
| `BIOMETRIC_VERIFICATION` | FaceTec face-matching + HANIS Photo-Match Family (live HANIS match + DHA-down bureau fallback) | **Covered** | Two complementary sources for a type with zero adapter today. Worth escalating even though outside the original "credit bureau" framing. |
| `FRAUD_WATCHLIST_SCREENING` | `ConsumerFraudIndicatorsSummary` (corroborated across 3 products) + AML Sanctions/PEP category, 5 of 6 products already `Enabled` | **Likely covered** | `SAFPSListingYN` shows access-restricted even in sandbox — a subscription-tier cost question for task 4. |
| `EMPLOYMENT_VERIFICATION` | `ConsumerEmploymentHistory` (corroborated across 3 products) | **Likely covered** | Reverses an earlier "not covered" read from before the corroborating products were found. |
| `PROPERTY_OWNERSHIP_VERIFICATION` (already Direct-ish via miswired `deedsweb`) | `ConsumerPropertyInformation` (corroborated across 3 products) — full deeds-style fields | **Possible new coverage** | Not in the original gap list. Could be a cleaner path than fixing the existing miswired handler, if usable standalone. |
| `DOCUMENT_VERIFICATION` | `OCR AI` (classification/extraction, not authenticity) + `GlobalData – AU - Document Validation Service` (likely Australian-document-scoped) | **Still open** | Neither confirmed as a real fit; needs a direct question to Datanamix. |
| `TAX_COMPLIANCE_VERIFICATION` | — | **No match found** | Would need a different vendor. |
| `COMPANY_VERIFICATION` (already Direct via CIPC) | 5 Datanamix/XDS products, incl. CIPC Search/Plus — all 5 CIPC products already `Enabled` | Alternate source, not a gap-fill | Real question: is any of the enrichment (BEE status, ISO certification, commercial scoring) worth adding regardless of the provider decision? |
| `VERIFICATION_OF_BANK_DETAILS` (already Direct via QLink) | AVS / AVS Advanced | Alternate source with added value | AVS Advanced's contactability/RTTC data is a capability QLink doesn't expose. |
| `NEGATIVE_NEWS_SCREENING` (currently Partial, generic NewsAPI.org) | `Sanctions Standard - Adverse Media` — already `Enabled` | **Upgrade opportunity** | Purpose-built, already enabled, unused. Worth flagging to R1 as a quality upgrade independent of the rest of this decision. |
| No current VeriGate type | KBA (Authentication Questions), address enrichment, reverse phone lookup, AML Sanctions/PEP, Recon Reports, FICA, Collections, Deceased Estates | New capability | Out of scope for this story; worth a future-story note. |

Full per-product endpoint documentation and evidence behind every row above is in the appendix.

## Key Findings Requiring a Decision

1. **Multi-bureau aggregation** — Datanamix resells Experian and TransUnion directly, covering 2 of the 3 providers in VeriGate's existing `CreditBureauProvider` model in one integration. `COMPUSCAN` isn't available through Datanamix at all.
2. **12 products already `Enabled`, zero code integration** — a full-repo grep finds no Datanamix references anywhere in the codebase, yet 5 CIPC products, bank AVS, FICA KYC, and most of the AML/Sanctions/PEP suite are enabled and presumably billable on VeriGate's account today. Worth escalating to whoever owns that account regardless of this story's outcome.
3. **`NEGATIVE_NEWS_SCREENING` upgrade opportunity** — an already-enabled, purpose-built adverse-media product sitting unused while the live adapter uses a generic news-search API.
4. **DPA/SLA are a business action, not a docs question** — both require starting Datanamix's MSA process. No further research resolves this.
5. **Open legal question for R1/legal**: Datanamix, as an NCA-registered credit bureau, likely independently qualifies as its own "responsible party" under POPIA for its own database — whether VeriGate needs a formal operator/sub-operator agreement with Datanamix specifically (distinct from VeriGate's consent-based relationship with its own clients) isn't settled and should be part of the MSA discussion.
6. **Two API documentation discrepancies found via live sandbox testing** (Express Consumer Score's response field, ID Photo From HANIS's example JSON) — should be raised with Datanamix support before any adapter is built against them.

## Next Steps

1. **R4/R1**: initiate Datanamix's Master Services Agreement process to obtain a signed DPA and real SLA numbers — the one remaining blocker to task 4.
2. **R4**: confirm the `SAFPSListingYN` subscription tier and cost; confirm whether various R0.00-priced items (most of FaceTec, `ID Scan to Hanis Verification`) are genuinely free or unconfigured.
3. Flag the 12-enabled-unused-products finding to whoever owns the Datanamix account relationship.
4. File the two platform bugs (dangling WorldCheck routing, miswired deedsweb handler) as separate tickets if not already tracked.
5. Once task 4 closes: **R1 decision gate** — approve/reject Datanamix as gap-fill provider and confirm the final list of checks to route through it.
