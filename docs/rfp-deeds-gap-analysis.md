# Newcastle Municipality Deeds RFP Gap Analysis

## Source

RFP reviewed: `A012 - 2025'26 DEEDS SEARCH BID DOCUMENT''.pdf`

Focus used for this analysis:
- Functional and technical requirements from the Terms of Reference and evaluation sections on pages 59-66.
- Current-state implementation evidence from the VeriGate codebase.

This analysis excludes procurement and bid-administration paperwork unless it directly affects technical delivery.

## Executive Summary

The current codebase contains a useful foundation for a deeds-related capability, but it does not yet satisfy the RFP as an end-to-end municipal deeds search platform.

The strongest current assets are:
- A modular verification platform with authentication, queue-based routing, reporting scaffolding, monitoring scaffolding, and a partner portal.
- A partial deeds-domain model for property ownership checks.
- A portal screen for property ownership search.

The largest gaps are:
- The wired deeds adapter is still implemented and tested like an OpenSanctions adapter rather than a municipal deeds records solution.
- The live API contract returns generic verification status, not rich deeds search, reporting, document, or alert data.
- The portal's deeds experience is largely mock-backed by default.
- Core RFP requirements such as transfer alerts, subdivision/consolidation monitoring, spatial mapping, map search, document copy retrieval, DOTS support, automated valuation, and system-to-system exports are not implemented end to end.

## Extracted Functional Requirements

From RFP pages 59-63:

1. Provide online access to Deeds Office records through a web portal.
2. Support accurate property and ownership transfer maintenance and ownership verification.
3. Notify the municipality of key property changes:
   - transfers
   - subdivisions
   - consolidations
   - portions list and ownership of parent property
   - registration dates
4. Support spatial mapping of municipal boundaries and updates to the property register.
5. Support map-based search and visual investigation of properties and streets, including street-name variation handling.
6. Generate standard and custom area reports for changes in property information.
7. Export results for transfers, subdivisions, and consolidations for import into municipal systems.
8. Provide deep-dive individual property reports for unpaid properties.
9. Provide a centralized single view of property information and related reports.
10. Provide saved current reports that auto-update.
11. Support property searches using:
   - full title details
   - farm details
   - sectional title details
12. Support:
   - deed property reports
   - automated valuation
   - transfer searches by day and by month
   - street-to-erf conversions
   - erf-to-street conversions
   - deed alerts
13. Support document searches for:
   - deeds office document/title deed copies
   - title deed endorsement copies
   - DOTS barcode, person, and property searches
   - document copies registered against a title deed
   - surveyor-general diagrams
   - judgment copies
   - automated DOTS tracking
14. Provide a nationwide property ownership profile including:
   - registration date
   - purchase price
   - access to deeds office document copies
   - surveyor-general diagrams
15. Provide unlimited portal access for unlimited users.
16. Provide administrator rights to five or more people to manage user access.
17. Deliver system access within 30 days of award.
18. Provide training and ongoing technical support to current and future users.
19. Provide telephone and email support within 24 hours of a logged request.
20. Interface with other municipal operational systems.
21. Provide at least monthly extracts/exports/data transfer or migration required for interfacing.

## Extracted Technical / Non-Functional Requirements

From RFP pages 61-65:

1. Web-based software with an online portal.
2. Multi-user access with administrator-controlled access rights.
3. Ability to install, test, commission, and demonstrate the deeds search and monitoring tools.
4. Ability to interface with external municipal systems.
5. Data extract/export capability at least monthly.
6. Bulk search turnaround of two weeks or less.
7. POPIA compliance and alignment with municipal ICT governance requirements.
8. Training / transfer of skills for municipal users.
9. Support model via telephone and email within 24 hours.
10. Operational onboarding within 30 days from appointment.

## Current-State Evidence

### What exists

- Generic verification submission and status APIs exist in the BFF:
  - [VerificationController.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/verification/controller/VerificationController.java#L27)
  - [VerificationStatusResponse.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/verification/model/VerificationStatusResponse.java#L7)
- A deeds verification type is routed through the platform:
  - [VerificationService.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/verification/service/VerificationService.java#L51)
- A deeds-specific property ownership service exists and models property details such as title deed reference, registration date, transfer date, purchase price, bond holder, and bond amount:
  - [DefaultPropertyOwnershipVerificationService.java](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-application/src/main/java/verigate/adapter/deedsweb/application/services/DefaultPropertyOwnershipVerificationService.java#L30)
- A partner portal page exists for property ownership search and PDF export:
  - [PropertyOwnership.client.tsx](/Users/arthurmanena/Documents/source/verigate/verigate-partner-portal/src/components/services/PropertyOwnership.client.tsx#L33)
- Generic partner reports, notifications, API keys, and monitoring/alerts scaffolding exist:
  - [PartnerController.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/partner/controller/PartnerController.java#L101)
  - [PartnerFeatureService.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/partner/service/PartnerFeatureService.java#L111)
  - [MonitoringController.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/verification/controller/MonitoringController.java#L24)

### Critical current limitation

The deeds adapter that is actually wired for command handling appears to still be an OpenSanctions-based implementation:

- Handler comments, log messages, provider label, and failure messages all refer to sanctions/OpenSanctions:
  - [DefaultPropertyVerificationCommandHandler.java](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-application/src/main/java/verigate/adapter/deedsweb/application/handlers/DefaultPropertyVerificationCommandHandler.java#L29)
- The request mapper builds sanctions-style entity matching requests with `sanctions` dataset and sanctions/PEP topics:
  - [VerifyPartyCommandMapper.java](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-domain/src/main/java/verigate/adapter/deedsweb/domain/mappers/VerifyPartyCommandMapper.java#L18)
  - [DomainConstants.java](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-domain/src/main/java/verigate/adapter/deedsweb/domain/constants/DomainConstants.java#L9)
- Environment variables and live integration tests are also OpenSanctions-oriented:
  - [environment-variables.md](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure/src/main/resources/environment-variables.md#L1)
  - [QuickLiveTest.java](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure/src/test/java/verigate/adapter/deedsweb/infrastructure/integration/QuickLiveTest.java#L23)

Inference:
- I found no evidence that `DefaultPropertyOwnershipVerificationService` is currently wired into the live command handling path. Repository search only showed its interface and implementation.

## Gap Analysis Matrix

| Requirement | Current state | Gap |
| --- | --- | --- |
| Online deeds portal access | Partial | Portal page exists, but live path is generic verification status and mock-backed by default. |
| Ownership verification | Partial | Property ownership service exists, but live command handler is still OpenSanctions-oriented. |
| Transfer / subdivision / consolidation alerts | Missing | Generic monitoring exists, but no deeds-change detection or deeds-specific alert generation. |
| Portions list, parent ownership, registration-date notifications | Missing | No evidence of these domain objects, workflows, or outputs. |
| Spatial municipal boundary mapping | Missing | No GIS or boundary-management implementation found. |
| Map search / visual property search | Missing | No live map or geospatial search flow found. |
| Standard and custom area reporting | Partial | Generic report CRUD exists, but not deeds-specific report generation. |
| Export results for municipal import | Partial | Generic verification export exists; no deeds-domain transfer/subdivision/consolidation export contract found. |
| Deep-dive individual property reports | Partial | UI mock supports detailed property cards; no live deeds report endpoint found. |
| Centralized single property view | Partial | UI implies this in mock view, but no live API/domain aggregation contract found. |
| Auto-updated saved current reports | Missing | Generic report save/schedule exists; no auto-refreshing deeds current report logic found. |
| Search by full title / farm / sectional title | Missing to Partial | UI offers owner name, owner ID, and erf/title inputs; backend evidence only supports owner-ID-style search in the deeds property service. |
| Deed property report | Partial | Domain model can hold basic property details, but no end-to-end report generation flow found. |
| Automated valuation | Missing | No valuation logic or endpoint found. |
| Transfer searches per day / per month | Missing | No time-windowed deeds transfer query flow found. |
| Street-to-erf conversion | Missing | No conversion logic found. |
| Erf-to-street conversion | Missing | No conversion logic found. |
| Deed alerts / property watch | Missing | No deeds watchlist/alert implementation found. |
| Title deed / document copy retrieval | Missing | No deeds document retrieval flow found. Current document controller is for S3-hosted verification documents, not deeds-office copies. |
| Title deed endorsements | Missing | No endpoint or adapter capability found. |
| DOTS barcode/person/property searches | Missing | No DOTS integration found. |
| Document copies against title deed | Missing | No implementation found. |
| Surveyor-General diagrams | Missing | No implementation found. |
| Judgment copies | Missing | No implementation found. |
| Automated DOTS tracking | Missing | No implementation found. |
| Nationwide ownership profile incl. registration date and purchase price | Partial | Property domain model supports registration date and purchase price, but not exposed as a proven live feature. |
| Access to deed document copies and SG diagrams | Missing | No end-to-end support found. |
| Unlimited access / unlimited users | Missing | No tenancy/user-seat or licensing model proving this requirement. |
| Five-plus administrators managing access | Partial | General admin/auth exists, but no partner-scoped user-management workflow proving five-plus delegated admins. |
| Interface with municipal systems | Partial | Platform is API-first and export-capable in general, but no concrete municipal deeds integration contract found. |
| Monthly extracts / migration | Missing to Partial | RFP-specific monthly deeds extracts are not implemented; only generic reporting/export scaffolding exists. |
| Training and future-user support | Missing in code | Operational requirement, not represented in the product implementation. |
| Telephone/email support within 24h | Missing in code | Operational support process not represented in the product implementation. |
| POPIA / ICT governance alignment | Partial | General authenticated, partner-scoped APIs exist, but there is no deeds-specific compliance implementation evidence proving the RFP requirement end to end. |

## Requirement-by-Requirement Notes

### 1. Deeds adapter readiness

Status: `High risk / not RFP-ready`

Why:
- The live deeds command handler still behaves like a sanctions adapter and returns only a generic verification outcome map, not deeds-domain data:
  - [DefaultPropertyVerificationCommandHandler.java](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-application/src/main/java/verigate/adapter/deedsweb/application/handlers/DefaultPropertyVerificationCommandHandler.java#L58)
- The mapper and constants are aligned to sanctions datasets and topics rather than deeds search semantics:
  - [VerifyPartyCommandMapper.java](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-domain/src/main/java/verigate/adapter/deedsweb/domain/mappers/VerifyPartyCommandMapper.java#L38)
  - [DomainConstants.java](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-domain/src/main/java/verigate/adapter/deedsweb/domain/constants/DomainConstants.java#L15)

Impact:
- This is the single most important gap. Until the adapter is reworked, the platform cannot credibly meet the RFP's deeds-search scope.

### 2. Property ownership service

Status: `Promising but incomplete`

Why:
- The property ownership service models useful fields and searches a `deeds` dataset:
  - [DefaultPropertyOwnershipVerificationService.java](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-application/src/main/java/verigate/adapter/deedsweb/application/services/DefaultPropertyOwnershipVerificationService.java#L36)
- It can map deed number, title deed reference, registration/transfer dates, purchase price, and bond information:
  - [DefaultPropertyOwnershipVerificationService.java](/Users/arthurmanena/Documents/source/verigate/verigate-command-gateway/src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-application/src/main/java/verigate/adapter/deedsweb/application/services/DefaultPropertyOwnershipVerificationService.java#L164)

Gap:
- No evidence that this service is exposed through the live BFF or the active command handler.
- It also covers only a narrow subset of the RFP, mainly ownership lookup, not the broader search/report/document/alert feature set.

### 3. API surface

Status: `Too generic for the RFP`

Why:
- The BFF exposes generic submit-and-poll verification endpoints:
  - [VerificationController.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/verification/controller/VerificationController.java#L39)
- The verification response shape is generic command status plus string `auxiliaryData`:
  - [VerificationStatusResponse.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/verification/model/VerificationStatusResponse.java#L7)
- The OpenAPI is also framed as a generic verification platform API:
  - [verigate-api.yaml](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/resources/openapi/verigate-api.yaml#L42)

Gap:
- The RFP requires first-class deeds capabilities, not only an asynchronous verification envelope.

### 4. Partner portal

Status: `Strong UX scaffold, weak live integration`

Why:
- The deeds screen is reasonably detailed and supports PDF export:
  - [PropertyOwnership.client.tsx](/Users/arthurmanena/Documents/source/verigate/verigate-partner-portal/src/components/services/PropertyOwnership.client.tsx#L91)
- But portal verification dispatch uses mocks by default:
  - [config.ts](/Users/arthurmanena/Documents/source/verigate/verigate-partner-portal/src/lib/config.ts#L1)
  - [verification-service.ts](/Users/arthurmanena/Documents/source/verigate/verigate-partner-portal/src/lib/services/verification-service.ts#L32)

Gap:
- In live mode, the client receives generic verification status, not the rich `PropertyOwnershipResponse` shape expected by the screen.

### 5. Reporting, alerts, and administration

Status: `Partial platform capability, not deeds-specific`

Why:
- Reports can be created, scheduled, listed, and deleted:
  - [PartnerController.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/partner/controller/PartnerController.java#L101)
  - [PartnerFeatureService.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/partner/service/PartnerFeatureService.java#L113)
- Monitoring subjects and alerts also exist:
  - [MonitoringController.java](/Users/arthurmanena/Documents/source/verigate/verigate-web-bff/src/main/java/verigate/webbff/verification/controller/MonitoringController.java#L36)

Gap:
- These are generic platform features and do not currently implement deeds-specific change alerts, area-based report generation, subdivision/consolidation tracking, or municipal boundary mapping.

## Overall Readiness Assessment

### Implemented well enough to reuse

- Core platform scaffolding:
  - authentication
  - partner scoping
  - queue-based dispatch
  - generic verification lifecycle
  - partner report/monitoring scaffolding
- A partial deeds-domain model for property ownership records
- A good starting portal UX for deeds/property search

### Partial and likely reusable with refactoring

- Property ownership lookup logic in the deeds application service
- Generic monitoring infrastructure for future deeds alerts
- Generic reporting infrastructure for future area/custom deeds reports
- Generic document handling patterns for future deeds-document retrieval

### Missing or not fit for purpose

- True deeds search adapter implementation
- True deeds document retrieval integration
- DOTS support
- Surveyor-General diagram support
- Judgment copy retrieval
- Automated valuation
- Street/erf conversions
- Subdivision/consolidation detection
- Map search and municipal spatial boundary tooling
- End-to-end live response model for deeds search/report data
- Municipal-system export/integration workflows specific to the RFP

## Recommended Remediation Priorities

1. Replace the current deeds command handler path with an actual deeds-domain workflow.
2. Promote deeds features to first-class BFF APIs instead of relying only on generic verification status responses.
3. Wire the existing property ownership service into the live flow, then extend it for:
   - title/farm/sectional searches
   - transfer history
   - document retrieval
   - valuation
4. Add a real deeds result contract for the portal and remove mock dependence for the deeds module.
5. Build deeds-specific reporting, alerts, exports, and municipal-system integration.
6. Add spatial/GIS and map-search capabilities if the bid response intends to claim those requirements.

## Bottom Line

Current fit against the RFP:
- Core platform maturity: `Moderate`
- Deeds-specific functional fit: `Low`
- End-to-end bid readiness for this RFP: `Low`

Most RFP-critical gaps are not cosmetic. They are product-scope and integration gaps.
