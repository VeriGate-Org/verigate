# Story 2.1 — Datanamix Product Catalogue & Technical Reference

Appendix to `docs/datanamix-story-2-1-gap-analysis.md`. This file holds the detailed,
implementation-time reference material: per-product endpoint documentation, full
pricing, sandbox verification logs, and methodology. Read the main doc first for
the coverage matrix and decisions needed — come here when actually building an
adapter.

## Methodology

For each `VerificationType` known to the platform, the routing table in
`verigate-command-gateway` (`DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP`,
[link](../verigate-command-gateway/src/verigate-command-gateway/verigate-command-gateway-domain/src/main/java/verigate/verification/cg/domain/constants/DomainConstants.java#L17))
was used to find which adapter, if any, handles it. For each adapter, the
infrastructure module's `EnvironmentConstants` default base URL was inspected to
determine whether the adapter targets a real named external system (government
domain, named vendor domain) or a `*-dev.verigate.co.za` placeholder — VeriGate's
own unimplemented stub domain, which is a reliable signal that no real source is
wired yet regardless of how complete the adapter's internal plumbing (retry logic,
rate limiting, DTOs) looks.

This mirrors the approach used in `docs/rfp-deeds-gap-analysis.md`: don't trust
that an adapter's existence means the integration is real — check what's actually
on the other end of the HTTP call.

## Verification Type Inventory

| Verification Type | Adapter / Queue | External endpoint (default) | Classification |
| --- | --- | --- | --- |
| `VERIFICATION_OF_PERSONAL_DETAILS` | `WORLDCHECK_ADAPTER_QUEUE_NAME` | — | **Broken** — no `verigate-adapter-worldcheck` module exists in the repo; routing points at a non-existent adapter |
| `VERIFICATION_OF_BANK_DETAILS` | QLink | `api.qlink.co.za/bank-verification/v1` | Direct |
| `BANK_ACCOUNT_VERIFICATION` (alias) | QLink | `api.qlink.co.za/bank-verification/v1` | Direct |
| `SANCTIONS_SCREENING` | OpenSanctions | `api.opensanctions.org` | Direct — recently completed full integration ([PR #33](../../../pull/33), merge `76b37b0`) |
| `WATCHLIST_SCREENING` (alias) | OpenSanctions | `api.opensanctions.org` | Direct |
| `IDENTITY_VERIFICATION` | DHA | `dha-api-dev.services.gov.za/identity/v1`, HANIS `hanisrvs1.hanis.gov.za` | Direct |
| `COMPANY_VERIFICATION` | CIPC | `cipc-apm-rs-dev.azure-api.net/enterprise/v1` | Direct |
| `PROPERTY_OWNERSHIP_VERIFICATION` | DeedsWeb | `deedssoap.deeds.gov.za:443/deeds-registration-soap/` | **Partial/broken** — real gov endpoint configured, but per `docs/rfp-deeds-gap-analysis.md` the live command handler is still implemented as an OpenSanctions clone, not real deeds logic |
| `EMPLOYMENT_VERIFICATION` | Employment | `employment-api-dev.verigate.co.za/api/v1` | **Gap** — placeholder domain, no named external source |
| `NEGATIVE_NEWS_SCREENING` | NegativeNews | `newsapi.org` | Partial — real source wired, but NewsAPI.org is a generic news search API, not a purpose-built adverse-media/negative-news screening data product |
| `FRAUD_WATCHLIST_SCREENING` | FraudWatchlist | `fraudwatchlist-api-dev.verigate.co.za/v1` | **Gap** — placeholder domain, no named external source |
| `DOCUMENT_VERIFICATION` | Document | `document-verification-api-dev.verigate.co.za/api/v1` | **Gap** — placeholder domain; document/OCR authenticity check against S3-stored files, no vendor wired |
| `QUALIFICATION_VERIFICATION` | SAQA | `api.saqa.org.za/qualifications/v1` | Direct |
| `CREDIT_CHECK` | CreditBureau | `creditbureau-api-dev.verigate.co.za/api/v1` | **Gap** — placeholder domain. `CreditBureauProvider` enum models `EXPERIAN`, `TRANSUNION`, `COMPUSCAN` but no provider-specific routing exists; the HTTP adapter is a single generic client pointed at VeriGate's own unimplemented stub |
| `TAX_COMPLIANCE_VERIFICATION` | SARS | `sars-efiling-api-dev.verigate.co.za/api/v1` | **Gap** — placeholder domain |
| `INCOME_VERIFICATION` | Income | `income-api-dev.verigate.co.za/api/v1` | **Gap** — placeholder domain |
| `VAT_VENDOR_VERIFICATION` | SARS VAT | `secure.sarsefiling.co.za/VATVendorSearch/...` | Direct — real SARS endpoint, but this type isn't yet exposed in the BFF's `VerificationType` enum (command-gateway/BFF enum sync gap) |
| `FULL_VERIFICATION` | — (composite) | — | N/A — orchestrates other types, not a direct source integration |
| `BIOMETRIC_VERIFICATION` | none | — | **Gap** — explicitly flagged in code as "future, adapter not yet implemented" |
| `LIVENESS_CHECK` | none | — | **Gap** — same as above |

## Datanamix API Fundamentals

- **Sandbox access**: `EnvironmentType: SANDBOX` requests require **no OAuth / no Authorization header** for JSON, PDF, or JSON_AND_PDF output, on essentially every product.
- **Live access**: requires OAuth2 client-credentials flow (`POST /v1/oauth/token` on `api.datanamix.com`) once a client is registered via the Datanamix Developer Portal. Tokens are valid ~4 hours (14399s), revocable via `/v1/oauth/revoktoken`, inspectable via `/v1/oauth/tokenintrospect`, and the OIDC discovery document is at `/v1/oauth/.well-known/openid-configuration`.
- **Payload integrity**: successful responses carry an `X-Signature` header (a detached JWS, base64url) covering the response body. Verification: decode the JWS, look up the public key by the JWS's `kid` from the JWKS endpoint, re-hash the payload (SHA-256) and verify against the signature using the key's RSA modulus/exponent (`RSASignaturePadding.Pkcs1`, sample C# provided in the docs). This should be implemented once for any Datanamix adapter and reused across all endpoints, similar to how `verigate-shared-kernel` centralizes HMAC signing for WorldCheck-style integrations.
  - **Doc inconsistency to raise with Datanamix support**: the OAuth reference page gives the JWKS endpoint as `/v1/oauth/jwks.json`, but the dedicated Payload Verification page gives it as `/1/signature/jwks.json` (different path, and note the missing `v` in the version segment). Needs clarifying before implementation.
- **Common response shapes**: a `DatanamixFailureEnvelope` (`Success: false`, `ResponseCode`, `Messages[]`) and an HTTP-status-to-payload-code mapping component are documented, suggesting a consistent error contract across endpoints. `EnvironmentType` (`SANDBOX`/`LIVE`) and `OutputFormat` (`JSON`/`PDF`/`JSON_AND_PDF`, default `JSON`) are shared request parameters across the API.
- **Product/scope catalogue (partial, inferred early on)**: the OAuth reference page's example access token embeds a base64-encoded `scope` claim. Decoded: `offline_id_verification`, `realtime_id_verification`, `vat_number_search`, `company_search`, `retrieve_company_record`, `director_search_by_id_number`, `retrieve_credit_bureau_data`, `addresses_from_sacrra`, `realtime_id_photo`, `get_id_number_from_contact_number`, `create_account_verification`, `retrieve_questions`, `post_answers`, `unblock_consumer`, `id_scan_to_hanis_verification`, `verify_photo_to_hanis`, `affordability_assessment`, `consumer_express_score`, `xds_consum...` (truncated). This was later superseded by the real account catalogue (see Authoritative Product Catalogue below), which is more complete — the commercial credit products in particular never appeared in this scope list at all.

## Authoritative Product Catalogue & Pricing (from VeriGate's Datanamix API Manager account)

VeriGate holds a Datanamix API account. Its API Manager portal ("VeriID", `veriid.com`) lists 19 product categories:

Recon Reports · ID Verification · CIPC Company Verification · Know Your Customer · Address Verification · Mobile Verification · Bank Account Verification · Authentication Questions · FICA · Affordability · Consumer Credit Reports · Commercial Credit Reports · AML Sanctions and PEP · Phone ID, Phone Score and SIM Swap · Facetec · Collections and Recoveries · Deceased Estates · OCR · Document Validation

### Pricing — ID Verification category

All prices ZAR, per call. All products below currently show `Disabled` status.

| Product | Price (ZAR) | Status |
| --- | --- | --- |
| Home Affairs - RealtimeIDV (Live) | R 2.62 | Disabled |
| Datanamix - ProfileIDV (Offline) Result | R 1.68 | Disabled |
| Home Affairs - ID Photo | R 8.93 | Disabled |
| Datanamix - Biometric Fingerprint Validation | R 14.70 | Disabled |
| Datanamix - ID Scan to Hanis Verification | R 0.00 | Disabled |
| Verify Photo To HANIS | R 14.70 | Disabled |
| Datanamix - Profile Plus IDV and Photo | R 4.50 | Disabled |
| Datanamix - ID Scan to Profile Plus Verification | R 15.00 | Disabled |
| Home Affairs - RealtimeID Plus Photo | R 1.00 | Disabled |

Notes:
- `Verify Photo To HANIS` is a real, distinct, separately-priced product (R14.70) — resolves the earlier scope-decoding ambiguity about whether `verify_photo_to_hanis` was separate from `id_scan_to_hanis_verification`. It is.
- `Datanamix - Biometric Fingerprint Validation` (R14.70) is a fingerprint-based biometric, distinct from FaceTec's face-based liveness/matching — a third biometric modality, not otherwise documented anywhere in this analysis.
- `Datanamix - ID Scan to Hanis Verification` priced at R 0.00 is odd for a premium live-HANIS product — worth confirming with Datanamix whether genuine free/promotional, unconfigured, or a pricing error.
- Home Affairs-branded line items appear to be Datanamix's direct-to-DHA products, priced separately from the Datanamix-branded/bureau-sourced equivalents.

### Pricing — products already `Enabled` on VeriGate's account (12 total)

| Product | Price (ZAR) | Category |
| --- | --- | --- |
| CIPC Company Standard Result | R 15.80 | CIPC Company Verification |
| CIPC Director Search | R 9.50 | CIPC Company Verification |
| CIPC Plus Company Advanced Search | R 16.80 | CIPC Company Verification |
| CIPC Director Result | R 9.50 | CIPC Company Verification |
| CIPC Plus Company Advanced Result | R 16.80 | CIPC Company Verification |
| Bank - Account Holder Verification Standard | R 2.80 | Bank Account Verification |
| FICA Factory Start Consumer Kyc | R 10.50 | FICA |
| Sanctions Standard - AML Sanction+Crime+PEP Data | R 1.30 | AML Sanctions and PEP |
| Sanctions Standard - PEP Only | R 1.30 | AML Sanctions and PEP |
| Sanctions Premium Consumer - AML PEP/PIP Adverse Media Result | R 23.00 | AML Sanctions and PEP |
| Sanctions Premium Commercial - AML PEP/PIP Adverse Media Result | R 23.00 | AML Sanctions and PEP |
| Sanctions Standard - Adverse Media | R 1.80 | AML Sanctions and PEP |

**Cross-check**: `grep -ri datanamix` across the entire VeriGate monorepo returns no matches outside this documentation. None of the above are wired into any VeriGate adapter today, even though enabled/billable.

### Pricing — Consumer/Commercial Credit Reports (multi-bureau aggregator finding)

| Product | Price (ZAR) | Status |
| --- | --- | --- |
| Datanamix - Express Consumer Score - Result | R 8.61 | Disabled |
| Experian - Express Credit Score Report | R 0.00 | Disabled |
| XDS - Consumer Credit Report Result | R 33.60 | Disabled |
| TransUnion - Consumer Credit Report | R 40.95 | Disabled |
| Datanamix - Consumer Credit Report | R 0.00 | Disabled |
| Experian Consumer Credit Report | R 37.80 | Disabled |
| Datanamix - Commercial Credit Report | R 0.00 | Disabled |
| TransUnion - Commercial Credit Result | R 0.00 | Disabled |
| TransUnion - Commercial CV4B Report | R 0.00 | Disabled |
| XDS - Commercial Credit Report Result | R 70.00 | Disabled |
| Datanamix - Commercial Express Score | R 5.00 | Disabled |

Datanamix resells **Experian** and **TransUnion** directly, matching VeriGate's existing `CreditBureauProvider` enum (`EXPERIAN`, `TRANSUNION`, `COMPUSCAN`) — one integration point for two of three modeled bureaus. `COMPUSCAN` doesn't appear anywhere in Datanamix's catalogue.

### Pricing — all other categories

| Product | Price (ZAR) | Category | Status |
| --- | --- | --- | --- |
| Datanamix - Consumer KYC Plus Source Result | R 4.09 | Recon Reports / KYC | Disabled |
| Master High Court - Trust Result | R 50.00 | Recon Reports / KYC | Disabled |
| Datanamix - Address Plus Profile IDV | R 8.29 | Address Verification | Disabled |
| Datanamix - Digital KYC | R 12.60 | Address Verification / KYC | Disabled |
| Datanamix - Mobile Number to ID Verification | R 4.94 | Mobile Verification | Disabled |
| Bank - Account Holder Verification R-V3 | R 3.68 | Bank Account Verification | Disabled |
| Bank - Account Holder Verification Advanced | R 0.00 | Bank Account Verification | Disabled |
| Datanamix - Authentication Questions Plus Profile | R 4.73 | Authentication Questions | Disabled |
| Datanamix - Affordability Assessment | R 5.78 | Affordability | Disabled |
| Datanamix Basic Sanctions Search | R 2.62 | AML Sanctions and PEP | Disabled |
| Phone - Carrier Identity | R 5.25 | Phone ID / Phone Score / SIM Swap | Disabled |
| Datanamix Phone Score | R 5.25 | Phone ID / Phone Score / SIM Swap | Disabled |
| FaceTec (all endpoints except Match 3D:3D) | R 0.00 each | Facetec | Disabled |
| Facetec - Match 3D:3D | R 3.68 | Facetec | Disabled |
| GlobalData – AU - Document Validation Service | R 2.00 | Document Validation | Disabled |
| Datanamix - Collections 360 Result | R 0.00 | Collections and Recoveries | Disabled |
| Datanamix - Payment Predictor | R 0.00 | Collections and Recoveries | Disabled |
| GPW - Section 29 Deceased Estate | R 0.20 | Deceased Estates | Disabled |
| OCR AI - Document Classification | R 0.38 | OCR | Disabled |
| OCR AI - Document Extraction | R 0.63 | OCR | Disabled |

Notable items:
- `GPW - Section 29 Deceased Estate` — GPW is the Government Printing Works, publisher of the official government gazette; a real, authoritative deceased-estate registry check.
- `Master High Court - Trust Result` (R50, priciest item found) — direct High Court trust registry data. No current VeriGate type covers trusts specifically, though CIPC/AVS accept a `TrustNumber` identity type.
- `GlobalData – AU - Document Validation Service` — the "AU" prefix suggests Australian-document scope, not South African. Needs confirming.
- `OCR AI` products are classification/extraction, not authenticity/fraud checks — likely insufficient alone for `DOCUMENT_VERIFICATION`.
- Almost all of FaceTec is priced R0.00 except `Match 3D:3D` — worth confirming genuinely free vs. unconfigured, same as the HANIS R0.00 item above.

## POPIA / PAIA / SLA — Datanamix's own posture

Datanamix is a registered credit bureau and risk management platform in South Africa, with stated compliance protocols for POPIA and PAIA.

**Data Protection & Security**: built-in compliance for consumer tracing, credit vetting, and collections (POPIA/NCA guidelines); end-to-end encryption in transit and at rest for API integrations; password-protected encrypted PDF reports for audit trails (matches the `PDFEncryptionPassword` field on nearly every product); strict customer due diligence/identity verification/vetting required before portal or API access is granted.

**PAIA**: maintains an official Access to Information Manual as a private body under PAIA regulations.

**SLA & commercial access**: specific SLA terms (uptime, response times, support commitments) are **not publicly documented anywhere** — they require a formal **Master Services Agreement (MSA)** plus Service Request documentation as part of commercial onboarding. Account tiers are prepaid or 30-day commercial, both subject to vetting.

## VeriGate's own consent & compliance framework

Separate from Datanamix's posture above — this is how VeriGate structures compliance with its own clients.

**Consent management**: clients are contractually required to obtain and record explicit consent from the data subject before any verification request; VeriGate enforces this as a hard prerequisite; consent records are captured, stored, and fully auditable.

| Area | VeriGate's approach |
| --- | --- |
| POPIA | Explicit consent obtained and recorded prior to any data processing; data minimisation and purpose limitation enforced; data subject rights supported. |
| NCA | Credit data accessed only for prescribed purposes with valid consent; NCR registration requirements for credit bureau data access are understood. |
| FICA | AML/sanctions screening performed in compliance with FIC Act obligations; PEP and sanctions list screening supports clients' FICA duties. |
| Data retention | Data is processed in-flight and not stored beyond what is required for audit and regulatory retention periods. |
| Data resale | No resale of raw data; data used strictly within the context of the verification service delivered to the client. |

**POPIA relationship model** (as stated by VeriGate): Datanamix = **data provider/credit bureau**; VeriGate = **third-party verification service provider (operator/processor)**; VeriGate's client = **responsible party** (e.g. a bank verifying a loan applicant); End User = **data subject**.

**Open legal question**: this model places Datanamix simply as "data provider," but as an NCA-registered credit bureau, Datanamix likely also independently qualifies as its own "responsible party" for its own database. Whether VeriGate needs a formal operator/sub-operator agreement directly with Datanamix — distinct from VeriGate's consent-based relationship with its own clients — isn't settled by this framework alone, and is exactly what an MSA would need to address.

## Product Detail

### Bank Account Verification (AVS / AVS Advanced)

Scope `create_account_verification`. Overlaps `VERIFICATION_OF_BANK_DETAILS`/`BANK_ACCOUNT_VERIFICATION`, already Direct via QLink.

- **Endpoints**: `POST /v1/bank/account-verification` (AVS), `POST /v1/bank/account-verification-advanced` (AVS Advanced).
- **What it verifies**: identity number (ID/Passport/Temp ID/Company Reg/Trust) matched against a bank account number, across all major SA banks (Absa, FNB, Nedbank, Standard Bank, Capitec, Investec, Discovery, African Bank, Access Bank, Bidvest, Grindrod, Sasfin, GoTyme). Confirms account exists, open >90 days, accepts debits/credits, initials/name/email/cellphone match.
- **AVS Advanced adds**: `Contactability` data — contact number/email matches, RTTC (Right To Contact Customer) scoring per time-of-day/channel. Capability QLink doesn't appear to expose.
- **Sandbox**: `SB1233` (individual, open), `SB5345` (individual, closed), `SB76546` (corporate, open), `SB65439` (corporate, closed). Unsupported account numbers → HTTP 200, `Success: false`, `ResponseCode: 6`.
- **Response codes**: `0` success, `4` not found, `5` service unavailable, `6` validation error, `7` internal error.
- **Output**: JSON, or inline Base64 PDF (`PDFReport`) when `OutputFormat` is `PDF`/`JSON_AND_PDF`.
- **Live-verified 2026-07-29**: matches docs exactly.

### CIPC Search / CIPC Search Plus

Scopes `company_search`, `retrieve_company_record`, `director_search_by_id_number`. Overlaps `COMPANY_VERIFICATION`, already Direct via CIPC.

- **Two-step flow, both tiers**: search returns a shortlist with `EnquiryID`/`EnquiryResultID` pairs; result fetches the full record. Search accepts exactly one of `BusinessRegistrationNumber`, `BusinessName`, `VatNumber`, `SolePropIDNumber`.
  - Standard: `POST /v1/cipc/company-search` → `POST /v1/cipc/company-result`
  - Plus: `POST /v1/cipc/company-search-plus` → `POST /v1/cipc/company-result-plus`
- **Standard tier**: basic company info, active status, registered address, director list with ID numbers, director ID verification against DHA, director contact info.
- **Plus tier adds**: change history (`ChangeHistoryInformation`), VAT number, live VAT verification via `AdditionalVatNumberVerification`, richer director data, auditor info, active-principal contacts.
- **Registration number formats**: `YYYY/NNNNNN/NN` suffix per entity type (Pty Ltd `/07`, Ltd `/06`, NPC `/08`, CC `/23`, etc.), optional letter prefixes (K/M/N/P/S/T/C/D/L/R).
- **Sandbox**: `BusinessName: "XYZ Shoes"` → fixed 3-result shortlist, primary pair `EnquiryID: 234567`/`EnquiryResultID: 876543`.
- **Comparison to VeriGate's CIPC adapter**: `verigate-adapter-cipc`'s domain model already has `ChangeHistory`, `Auditor`, `CompanyComplianceScore`, `Director`, `Secretary`, `Capital` types — anticipates most of CIPC Plus. Whether the live Azure APIM integration actually populates these fields wasn't checked.
- **Live-verified 2026-07-29**: search matches docs exactly.

### CIPC Director Search / Result

Scope `director_search_by_id_number`. `POST /v1/cipc/director-search` (by `IDNumber`) → `POST /v1/cipc/director-result` (by `EnquiryID`/`EnquiryResultID`). Despite the "CIPC director" name, the result payload is a full consumer-bureau-style record — confirmed real via live sandbox call on 2026-07-29 (not a documentation artifact), and independently corroborated by Affordability Assessment and XDS Consumer Credit Report returning the same shape.

Key fields:
- `ConsumerFraudIndicatorsSummary` — `HomeAffairsVerificationYN`, `HomeAffairsDeceasedStatus`/`Date`, `EmployerFraudVerificationYN`, `ProtectiveVerificationYN`. Relevant to `FRAUD_WATCHLIST_SCREENING`.
- `ConsumerEmploymentHistory` — `{EmployerDetail, Designation, FirstReportedDate, LastUpdatedDate}`. Relevant to `EMPLOYMENT_VERIFICATION`.
- `ConsumerPropertyInformation` — full deeds-style record (title deed number, buyer/seller, transfer date, bond details, erf number, deeds office, purchase price). Relevant to `PROPERTY_OWNERSHIP_VERIFICATION`.
- `ConsumerEnquiryHistory`/`DirectorEnquiryHistory` — credit-grantor enquiry history. Relevant to `CREDIT_CHECK`.
- `ConsumerMaritalStatusEnquiry`, `ConsumerAddressHistory`, `ConsumerTelephoneHistory` — no current VeriGate type.
- **Sandbox**: `IDNumber: 7904065101087` → "John Doe", pair `123456`/`654321`.
- **Live-verified 2026-07-29**: matches docs exactly, including the full payload shape.

### Affordability Assessment

Scope `affordability_assessment`. Direct hit on `INCOME_VERIFICATION`.

- **Endpoint**: single call, `POST /v1/credit/datanamix/DatanamixAffordabilityAssessment` (no search/result split).
- **Request**: `IDNumber` XOR `PassportNumber`, `FirstName`, `Surname`, `BirthDate` (`yyyy-MM-dd`), `TotalNetMonthlyIncome`, required `EnquiryReason` permissible-purpose code (`CreditApplication`, `CreditLimit`, `InsuranceApplication`, `InsuranceClaim`, `CreditAssessment`, `FraudInvestigation`, `AccountLimitDetermination`, `InsuranceAssessment`, `AffordabilityAssessment`, `FraudDetection`, `CreditScoringSystem`). Applies NCA-mandated minimum living expense regulations automatically.
- **Response**: `ConsumerCPANLRDebtSummary` (CPA/NLR debt: accounts, arrears, judgments, court notices, defaults); `FirstMonthLoan`/`SecondMonthLoan`/`ThirdMonthLoan` (scored/decisioned loan scenarios); `ConsumerAffordability` (`GrossMonthlyIncome`, `PredAvailableInstalment`, `TotalCommitments`, `EstimatedExpenses`); same fraud/employment/property/enquiry blocks as CIPC Director Result.
- **Sandbox**: `IDNumber: 9000000000000`.
- **Compliance note**: the `EnquiryReason` purpose code is exactly the kind of thing a POPIA/NCA review cares about — must be correctly declared per pull.
- **Live-verified 2026-07-29**: matches docs exactly, including `SAFPSListingYN` showing as access-restricted in live sandbox (real behavior, not just documented).

### XDS Consumer Credit Report

Likely the scope truncated as `xds_consum...`. Richest consumer credit product, strongest `CREDIT_CHECK` candidate.

- **Two-step**: `POST /v1/credit/xds/consumer-credit-search` (ID/Passport + `EnquiryReason` + `Reference`) → `POST /v1/credit/xds/consumer-credit-result`.
- **Adds beyond CIPC Director Result/Affordability**: `ConsumerScoring` (`FinalScore`, `ReasonCode1-3`, `classification`, `RiskCategory`); `AdverseInformation` (`ConsumerDebtReviewStatus`, `ConsumerRehabilitationOrder`, `ConsumerAdminOrder`, `ConsumerSequestration`, `ConsumerJudgement`, `ConsumerDefaults` — full case numbers/courts/plaintiffs/amounts); CPA/NLR 24-month payment history grids; `AkaNames`, full address/contact/email history.
- **Sandbox**: any ID → "John Doe", pair `1234`/`12345`.
- XDS is itself a separate, well-known SA credit bureau — Datanamix resells/proxies it.
- **Live-verified 2026-07-29**: search matches docs exactly.

### Express Consumer Score

Scope `consumer_express_score`. Lighter-weight than XDS/Affordability.

- **Two-step**: `POST /v1/credit/datanamix/express-consumer-score-search` → `POST /v1/credit/datanamix/express-consumer-score-result`.
- **Response**: `ConsumerDetail` + one `ConsumerScoring` block only — no fraud indicators, property, or debt detail. A distinct, cheaper product, not a smaller view of the same data.
- **Positioned as cost-optimization**: Datanamix's own copy: "Reduce Credit Costs by only viewing the essential credit data before accessing the full report." Design implication: call this first, escalate to XDS/Affordability only when warranted.
- **Distinct refusal**: `ResponseCode: 8` — "ID Belongs To Minor."
- **Identity caveat**: docs state ID data here "is not updated from DHA" — not a substitute for VeriGate's DHA/HANIS-based `IDENTITY_VERIFICATION`.
- **Sandbox**: `IdNumber: 0000000000001`.
- **Live-verified 2026-07-29**: **discrepancy found** — docs' schema says response wraps results in `ListOfConsumers`; live response actually returns them under `Result`. An adapter built strictly off the documented schema would fail to deserialize.

### FaceTec (ZoOm Liveness / Face Matching)

Strongest candidate for `BIOMETRIC_VERIFICATION`/`LIVENESS_CHECK` — VeriGate's two zero-adapter verification types.

- **Separate sub-platform**: `face.datanamix.com/v9`, **HTTP Basic Auth** (not the OAuth2 flow used elsewhere). Needs its own auth handling in any adapter.
- FaceTec's ZoOm product, white-labeled by Datanamix — iBeta/NIST/ISO-certified liveness detection. Requires a separate client-side SDK (password-protected download) for device-side capture; this REST API is the server-side counterpart.
- **Endpoints**:
  - Session: `GET /session-token`, `GET /status`.
  - Liveness: `POST /liveness-3d` (certified), `POST /liveness-2d` ("free unlimited").
  - Enrollment: `POST /enrollment-3d`, `GET /enrollment-3d/{externalDatabaseRefID}`.
  - Matching: `POST /match-3d-3d`, `POST /match-3d-2d-idscan` (face vs. ID document — standout capability), `POST /match-3d-2d-face-portrait`, `POST /match-3d-2d-3rdparty-idphoto` (+ low-quality variant), `POST /match-3d-2d-profile-pic`, `POST /match-2d-2d`.
  - Age estimation: `POST /estimate-age-3d`/`2d` (age bracket), `POST /check-age-3d`/`2d` (pass/fail vs. threshold). No current VeriGate type.
  - Audit/keys: `POST /facemap-audit-data`, `/device-license-key`, `/public-face-map-encryption-key`, `/production-keys`, `/mobile-production-keys`.
- `match-3d-2d-idscan` matches a liveness-proven selfie against a scanned ID document — a complete, HANIS-independent ID-photo-verification flow.
- **No sandbox matrix published** for this product — sandbox testability unconfirmed, not live-tested.

### HANIS Photo-Match Family (ID Scan to HANIS, ID Photo From HANIS, Profile Plus ID Photo Match) + Profile ID

Resolves scope names `realtime_id_photo`, `verify_photo_to_hanis`, `id_scan_to_hanis_verification`. **Important**: these are photo-match products (confirm a photo resembles a record), not liveness products. Liveness remains FaceTec-only.

- **`POST /v1/face/id-scan-to-hanis-verification`** — `IDNumber` + `CaptureImage` (scanned ID doc); extracts face, retrieves HANIS portrait, returns `MatchScore` + both images + HANIS metadata. Live, government-authoritative, fails when DHA is offline.
- **`POST /v1/face/id-photo-from-hanis`** — retrieval only, no `CaptureImage` needed.
- **`POST /v1/face/profile-plus-id-photo-match-verification`** — same mechanic, compares against Datanamix's own bureau photo instead of live HANIS. Marketed as the DHA-down fallback: faster, cheaper, available during outages.
- **`POST /v2/id-verification/profileID`** — distinct, lighter product: single-call KYC (name, gender, DOB, age, deceased status, citizenship), no photo.
- **Response-status vocabulary maps to VeriGate's `VerificationOutcome`**: `1 Face Match Successful` → SUCCEEDED, `2 Face Match Failed` → HARD_FAIL, `3 Face Extraction Failed`/`6 Validation Error` → HARD_FAIL, `4 No DHA Image Found` → SOFT_FAIL, `5 DHA Unavailable` → SYSTEM_OUTAGE.
- **Minor refusal**: shared pattern (validation error 6 for photo-match products, `ResponseCode: 8` for Profile ID).
- **Resilience angle**: the DHA-down fallback pattern (HANIS primary, Profile Plus failover) is directly relevant to `verigate-adapter-dha`, which likely has no equivalent fallback today.
- **Sandbox**: shared `IDNumber` set exercising each status code (e.g. `0101010000081` → Face Match Successful, `...085` → DHA Unavailable, `...086` → Validation Error). Profile ID has a 15-persona catalog.
- **Live-verified 2026-07-29** (`id-photo-from-hanis` and `profileID`): **discrepancy found** on `id-photo-from-hanis` — docs' schema section correctly names the response field `PhotoResults` (confirmed live), but that page's example JSON shows `VerificationResults` instead (copy-paste artifact from the sibling ID Scan to HANIS page).

### Commercial Credit Products (Datanamix Commercial, Commercial Express Score, XDS Commercial)

Business-side counterparts to the consumer credit products; further enrichment on `COMPANY_VERIFICATION`.

- **Datanamix Commercial Credit Report** (`POST /v1/credit/datanamix/commercial-search` → `.../commercial-result`) — aggregate of business + director bureau data, CIPC data, anti-fraud indicators. `CommercialPrincipalInformation` links through to each director's consumer credit report.
- **Datanamix Commercial Express Score** (`.../commercial-express-score-search` → `-result`) — cheap triage: `CommercialScore`/`Band`, turnover bracket, adverse counts.
- **XDS Commercial Credit Report** (`.../commercial-credit-search` → `-result`) — richest: `AdverseInformation` (`Judgment`/`PossibleJudgment` with case detail), `BlackEconomicEmpowerment` (BEE level/score/certificate), `ISOStatus`, `TradeReferencesInformation`, business `PropertyInformation`, named `CommercialScoring` risk bands (1–5).
- None of these three appeared in the originally-decoded OAuth scope list — the scope list was never an exhaustive catalogue.
- **Open scope question**: VeriGate's `CREDIT_CHECK`/`CreditBureauProvider` don't distinguish consumer vs. commercial. BEE and ISO certification data have no current VeriGate analog at all.

## Sandbox Verification Log (live-tested 2026-07-29)

Nine sandbox endpoints across seven products called directly (`api.datanamix.com`, no credentials needed):

| Endpoint | Result |
| --- | --- |
| `POST /v1/cipc/company-search` | Matches docs exactly. |
| `POST /v1/bank/account-verification` | Matches docs exactly. |
| `POST /v1/cipc/director-search` | Matches docs exactly. |
| `POST /v1/cipc/director-result` | Matches docs exactly, including the full consumer-bureau payload — confirms it's real, not a documentation artifact. |
| `POST /v1/credit/datanamix/DatanamixAffordabilityAssessment` | Matches docs exactly, including `SAFPSListingYN` access restriction (real live behavior). |
| `POST /v1/credit/datanamix/express-consumer-score-search` | **Discrepancy**: response wraps results in `Result`, not documented `ListOfConsumers`. |
| `POST /v2/id-verification/profileID` | Matches docs exactly. |
| `POST /v1/credit/xds/consumer-credit-search` | Matches docs exactly. |
| `POST /v1/face/id-photo-from-hanis` | **Discrepancy**: example JSON shows `VerificationResults`; schema and live response both use `PhotoResults`. |

**Recommendation**: raise both discrepancies with Datanamix support before adapter implementation.
