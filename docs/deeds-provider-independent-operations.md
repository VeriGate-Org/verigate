# Deeds Provider-Independent Operations

This document describes the provider-independent deeds functionality implemented before the live DeedsWeb SOAP contract is available.

## Current Capabilities

- Deeds search, cached property reports, document manifests, current reports, export history, and watch management.
- Deeds team management for delegated admins/operators/viewers.
- Deeds audit events for saved reports, exports, watches, team actions, and refresh cycles.
- Spatial search shell backed by cached deeds properties and synthetic municipal boundaries.
- Street-to-ERF and ERF-to-street conversion against cached property references.
- Rules-based valuation estimates backed by cached transfer activity and comparable sales.
- Manual refresh cycle for saved current reports and watch recalculation.

## Main Backend Entry Points

- BFF deeds controller:
  - `/api/partner/deeds/reports/area`
  - `/api/partner/deeds/reports/saved`
  - `/api/partner/deeds/exports`
  - `/api/partner/deeds/watches`
  - `/api/partner/deeds/team`
  - `/api/partner/deeds/audit`
  - `/api/partner/deeds/map/search`
  - `/api/partner/deeds/conversion`
  - `/api/partner/deeds/valuation`
  - `/api/partner/deeds/operations/refresh`

- Main service:
  - `verigate.webbff.deeds.service.DeedsPortfolioService`

## Persistence Model

Partner-scoped deeds entities are stored through the generic partner data repository with these prefixes:

- `DEEDS_REPORT#`
- `DEEDS_EXPORT#`
- `DEEDS_TEAM#`
- `DEEDS_AUDIT#`

Monitoring-backed deeds watches remain in the monitoring subject store with deeds metadata persisted in `metadataJson`.

## Portal Surfaces

- `/services/property-ownership`
- `/services/deeds-map`
- `/services/property-conversion`
- `/services/property-valuation`
- `/reports`
- `/settings?tab=deeds-ops`

## Metrics

The BFF now records these counters:

- `deeds.reports.refreshed`
- `deeds.watches.recalculated`
- `deeds.exports.created`

## Operational Notes

- Document retrieval, DOTS, SG diagrams, judgments, and live provider-backed exports are still placeholders until the external contract is mapped.
- Spatial geometries are synthetic and intended only to support the shell and workflow scaffolding.
- Valuations are internal estimates only and must not be presented as official valuations.
- The refresh cycle is currently manual through the BFF endpoint; a scheduler can be added once job orchestration is chosen.

## Remaining Provider-Blocked Areas

- Real SOAP authentication and operations
- Real DeedsWeb response mapping
- Real title deed/document retrieval
- Real DOTS queries
- Real cadastral/boundary datasets
- Provider-backed valuation inputs
