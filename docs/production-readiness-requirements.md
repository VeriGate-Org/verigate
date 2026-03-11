# VeriGate Production Readiness Requirements

> **Purpose**: This document defines all outstanding work items required to bring VeriGate to production readiness. Each task is self-contained with context, acceptance criteria, and file references. External API responses may be mocked — the focus is on correct internal logic, test coverage, infrastructure completeness, and operational readiness.
>
> **Repository**: `VeriGate-Org/verigate` (monorepo)
>
> **Date**: 2026-03-11

---

## Table of Contents

1. [Architecture Context](#1-architecture-context)
2. [Critical: Web-BFF Query Side](#2-critical-web-bff-query-side)
3. [Critical: WorldCheck Adapter Implementation](#3-critical-worldcheck-adapter-implementation)
4. [High: Adapter Unit Tests (10 Adapters)](#4-high-adapter-unit-tests-10-adapters)
5. [High: Billing Service Tests](#5-high-billing-service-tests)
6. [High: Integration & E2E Test Suite](#6-high-integration--e2e-test-suite)
7. [Medium: Observability & Correlation IDs](#7-medium-observability--correlation-ids)
8. [Medium: DynamoDB Index for Verification Listing](#8-medium-dynamodb-index-for-verification-listing)
9. [Medium: Environment Configuration Documentation](#9-medium-environment-configuration-documentation)
10. [Medium: Partner Portal Environment Config](#10-medium-partner-portal-environment-config)
11. [Low: Security Hardening](#11-low-security-hardening)
12. [Task Dependency Graph](#12-task-dependency-graph)

---

## 1. Architecture Context

Before starting any task, understand these architectural fundamentals:

- **Monorepo** with 7 services: `verigate-command-gateway`, `verigate-partner`, `verigate-billing`, `verigate-web-bff`, `verigate-partner-portal`, `verigate-public-website`, `verigate-shared-kernel`
- **Clean Architecture**: domain → application → infrastructure layers per module
- **AWS Stack**: Lambda (Java 21), SQS, DynamoDB, EventBridge, Kinesis, Secrets Manager, Cognito
- **DI Framework**: Google Guice for Lambda services; Spring Boot for web-bff only
- **Event-Driven**: Commands dispatched via SQS; domain events published to EventBridge/Kinesis
- **CQRS**: Command side (write via SQS) is complete; query side (read) is partially stubbed
- **All adapters call external APIs** that may be mocked for testing — focus on correct internal wiring, error handling, and domain logic

Read each service's `CLAUDE.md` and `README.md` before working on it.

---

## 2. Critical: Web-BFF Query Side

### Context

The web-bff (`verigate-web-bff/`) is a Spring Boot service that provides the REST API consumed by the partner portal. The `POST /api/verifications` (submit) and `GET /api/verifications/{id}` (single lookup) endpoints work. However, **`GET /api/verifications` (list) is a stub** that returns an empty list.

### Current State

- **File**: `verigate-web-bff/src/main/java/verigate/webbff/verification/controller/VerificationController.java`
- The `listVerifications()` method logs a warning and returns `Collections.emptyList()`
- The method signature already accepts `status` (filter), `offset`, and `limit` (pagination) query parameters
- `CommandStatusRepository` exists with `findById()` but **no list/query method**
- The command-store DynamoDB table has a GSI on `partnerId + createdAt` (`partner-index`) but **no GSI on `status`**

### Requirements

1. **Add a `status` GSI to the command-store DynamoDB table**
   - File: `verigate-command-gateway/iac/terraform/modules/tf-dynamodb/main.tf`
   - Add a GSI with partition key `partnerId` and sort key `status` (or a composite `status#createdAt` sort key for filtering + ordering)
   - Update all 4 environment tfvars if needed

2. **Implement `CommandStatusRepository.findByPartnerId()`**
   - File: `verigate-web-bff/src/main/java/verigate/webbff/verification/repository/` (or equivalent)
   - Query the `partner-index` GSI with `partnerId` from `PartnerContextHolder`
   - Support optional `status` filter
   - Implement cursor-based pagination using DynamoDB's `exclusiveStartKey` pattern (convert to/from opaque offset token)
   - Apply `limit` parameter
   - Return results ordered by `createdAt` descending (most recent first)

3. **Implement `listVerifications()` in the controller**
   - Replace the stub with a call to the repository
   - Map DynamoDB items to response DTOs
   - Return paginated response with metadata (total count or has-next-page indicator)

4. **Add unit tests**
   - Test pagination (first page, subsequent pages, empty results)
   - Test status filtering
   - Test partner isolation (partner A cannot see partner B's verifications)
   - Test limit boundary (max limit enforcement)

5. **Add integration test**
   - Use LocalStack DynamoDB
   - Seed test data, verify query returns correct results

### Acceptance Criteria

- `GET /api/verifications` returns paginated verification history for the authenticated partner
- `GET /api/verifications?status=COMPLETED` filters by status
- Response includes pagination metadata
- Partner isolation enforced (query scoped to `PartnerContextHolder.getPartnerId()`)
- Unit and integration tests pass

---

## 3. Critical: let's 

### Context

The WorldCheck adapter (`verigate-command-gateway/src/verigate-adapter-worldcheck/`) is scaffolded but completely empty. The SQS queue is provisioned in Terraform, but no Lambda handler consumes from it. This means sanctions screening requests are routed to SQS and eventually dead-lettered.

### Reference Implementation

Use the CIPC adapter as the primary reference:
- **Domain**: `verigate-command-gateway/src/verigate-adapter-cipc/verigate-adapter-cipc-domain/`
- **Application**: `verigate-command-gateway/src/verigate-adapter-cipc/verigate-adapter-cipc-application/`
- **Infrastructure**: `verigate-command-gateway/src/verigate-adapter-cipc/verigate-adapter-cipc-infrastructure/`
- **Tests**: `verigate-command-gateway/src/verigate-adapter-cipc/verigate-adapter-cipc-application/src/test/`

Also reference the OpenSanctions adapter (`verigate-adapter-opensanctions/`) as it handles a similar sanctions-screening domain.

### Requirements

1. **Domain Layer** (`verigate-adapter-worldcheck-domain`)
   - Define domain service interface (e.g., `WorldCheckScreeningService`)
   - Define request/response domain models for screening
   - Define verification type constant: `SANCTIONS_SCREENING` (check how CIPC defines its type)
   - Define domain-specific exceptions if needed

2. **Application Layer** (`verigate-adapter-worldcheck-application`)
   - Implement `DefaultVerifyPartyCommandHandler` extending the shared-kernel `CommandHandler`
   - Extract required parameters from the command (name, date of birth, nationality, etc.)
   - Call the domain service interface
   - Map results to domain events (`VerificationCompletedEvent` / `VerificationFailedEvent`)
   - Handle transient vs permanent exceptions using the shared-kernel exception hierarchy

3. **Infrastructure Layer** (`verigate-adapter-worldcheck-infrastructure`)
   - Implement HTTP adapter for WorldCheck Case Management API v3
   - **Authentication**: HMAC-SHA256 request signing (API key + secret from Secrets Manager)
   - Implement the Lambda handler extending shared-kernel's SQS Lambda handler base class
   - Configure Guice module for dependency injection
   - **Mock the actual WorldCheck API calls** — return realistic mock responses

4. **SAM Configuration**
   - File: `verigate-command-gateway/iac/sam/template.yaml`
   - Add Lambda function definition for WorldCheck handler (follow pattern of existing adapter functions)
   - Wire SQS queue as event source

5. **Terraform Updates**
   - Verify the WorldCheck SQS queue module is complete in `verigate-command-gateway/iac/terraform/`
   - Add IAM permissions for the Lambda to read from SQS and access Secrets Manager

6. **Tests**
   - Follow the CIPC test pattern with these cases:
     - Successful screening (no matches)
     - Screening with matches (sanctions hit)
     - Missing required parameters → `IllegalArgumentException`
     - API error → `PermanentException`
     - Network timeout → `TransientException`
     - Async handler success and failure variants

### Acceptance Criteria

- WorldCheck Lambda handler deploys and consumes from its SQS queue
- Commands with `verificationType=SANCTIONS_SCREENING` are processed (with mocked API responses)
- Verification results are published as domain events
- All test cases pass
- `mvn verify` succeeds for the worldcheck module

---

## 4. High: Adapter Unit Tests (10 Adapters)

### Context

Only 3 of 13 adapters have tests (CIPC: 7 tests, DeedsWeb: 5 tests, OpenSanctions: 5 tests). The remaining 10 adapters have handler implementations but **zero test coverage**. All adapter tests should follow the established CIPC pattern.

### Adapters Requiring Tests

| # | Adapter | Module Path | Handler Class |
|---|---------|-------------|---------------|
| 1 | DHA (Home Affairs) | `verigate-adapter-dha` | `DefaultVerifyIdentityCommandHandler` |
| 2 | CreditBureau | `verigate-adapter-creditbureau` | Check handler in application layer |
| 3 | Employment | `verigate-adapter-employment` | Check handler in application layer |
| 4 | FraudWatchlist | `verigate-adapter-fraudwatchlist` | Check handler in application layer |
| 5 | Income | `verigate-adapter-income` | Check handler in application layer |
| 6 | NegativeNews | `verigate-adapter-negativenews` | Check handler in application layer |
| 7 | QLink | `verigate-adapter-qlink` | Check handler in application layer |
| 8 | SAQA | `verigate-adapter-saqa` | Check handler in application layer |
| 9 | SARS | `verigate-adapter-sars` | Check handler in application layer |
| 10 | Document | `verigate-adapter-document` | Check handler in application layer |

All adapters are under: `verigate-command-gateway/src/verigate-adapter-{name}/`

### Test Pattern (from CIPC)

For each adapter, create test class(es) in the **application** module's `src/test/java/` directory:

```
verigate-adapter-{name}/
  verigate-adapter-{name}-application/
    src/test/java/verigate/commandgateway/adapter/{name}/application/
      Default{Action}CommandHandlerTest.java
```

Each test class must cover:

1. **Happy path** — successful verification with mocked external service response
2. **Not found / no data** — external service returns empty/not-found → `SOFT_FAIL` outcome
3. **Inactive / expired entity** — if applicable for the domain → `SOFT_FAIL`
4. **Missing required parameters** — handler throws `IllegalArgumentException`
5. **External service error** — maps to `PermanentException`
6. **Network/transient failure** — passes through as `TransientException`
7. **Async handler success** — tests the async variant if one exists
8. **Async handler failure** — tests error propagation in async path

### Requirements Per Adapter

1. Read the handler implementation to understand:
   - What parameters it extracts from the command
   - What domain service it calls
   - How it maps responses to verification outcomes
2. Create test class using JUnit 5 + Mockito
3. Mock the domain service interface (not the HTTP adapter)
4. Assert correct outcome events are created
5. Assert exception types are correct for error scenarios
6. Ensure `mvn test` passes for the adapter module

### Acceptance Criteria

- All 10 adapters have at minimum 6 test cases each
- All tests pass with `mvn test -pl verigate-command-gateway`
- No adapter handler has untested code paths for success, failure, and missing-parameter scenarios

---

## 5. High: Billing Service Tests

### Context

The billing service (`verigate-billing/`) has complete domain, application, and infrastructure layers but **zero tests**. It processes `VerificationCompletedEvent` from Kinesis, tracks usage per partner, and aggregates billing summaries.

### Key Files to Test

- **Domain**: `verigate-billing/verigate-billing-domain/src/main/java/`
  - `UsageTrackingService` interface and models (`UsageRecord`, `UsageSummary`, `BillingPlan`)
- **Application**: `verigate-billing/verigate-billing-application/src/main/java/`
  - `DefaultBillingService` — billing plan logic
  - `DefaultUsageTrackingService` — usage recording and aggregation
  - Event handlers for `VerificationCompletedEvent`
- **Infrastructure**: `verigate-billing/verigate-billing-infrastructure/src/main/java/`
  - DynamoDB repositories (`UsageRecordRepository`, `UsageSummaryRepository`, `BillingPlanRepository`)
  - Kinesis event consumer Lambda handler
  - Guice DI module

### Requirements

1. **Domain Unit Tests**
   - Test `UsageRecord` creation and field validation
   - Test `UsageSummary` aggregation logic
   - Test `BillingPlan` enforcement (if limits exist)
   - Test domain model invariants

2. **Application Unit Tests**
   - Test `DefaultUsageTrackingService`:
     - Successful usage recording
     - Duplicate event idempotency (same verificationId processed twice)
     - Missing required event fields (verificationId, partnerId, verificationType, outcome)
     - Usage aggregation across period boundaries
   - Test `DefaultBillingService`:
     - Plan lookup and enforcement
     - Usage summary generation

3. **Infrastructure Integration Tests**
   - Use LocalStack/TestContainers for DynamoDB
   - Test repository CRUD operations
   - Test Kinesis consumer Lambda handler with sample event payloads
   - Test idempotency at the storage layer

4. **Event Handler Tests**
   - Test that `VerificationCompletedEvent` is correctly deserialized
   - Test that usage is recorded for each verification type
   - Test error handling for malformed events

### Acceptance Criteria

- Minimum 15 test cases across domain, application, and infrastructure layers
- All tests pass with `mvn test -pl verigate-billing`
- Idempotency is tested and proven
- Error scenarios for malformed/duplicate events are covered

---

## 6. High: Integration & E2E Test Suite

### Context

The CI/CD pipeline has workflow definitions for integration and E2E tests (`.github/workflows/_test-integration.yml`, `_test-integration-e2e.yml`) but they run minimal Maven verify commands with no dedicated test scenarios. The smoke test (`_smoke-test.yml`) only validates infrastructure is alive (Lambda functions ACTIVE, SQS queues exist, DynamoDB tables exist).

### Requirements

1. **Command Gateway Integration Tests**
   - Location: `verigate-command-gateway/src/test/java/` (or a dedicated integration-test module)
   - Use TestContainers with LocalStack for AWS services (SQS, DynamoDB, EventBridge)
   - Test the full command flow:
     - Command arrives on SQS → Lambda handler invoked → adapter processes → result stored in DynamoDB → domain event published
   - Test with at least 2 different adapters (e.g., CIPC and DHA)
   - Test command idempotency (same commandId processed twice → no duplicate)
   - Test dead-letter queue routing for permanent failures

2. **Web-BFF Integration Tests**
   - Location: `verigate-web-bff/src/test/java/`
   - Use Spring Boot test slices (`@WebMvcTest`, `@SpringBootTest`)
   - Test the full REST → SQS dispatch → DynamoDB query flow
   - Test authentication:
     - Valid API key → 200
     - Missing API key → 401
     - Invalid API key → 401
   - Test rate limiting:
     - Requests within limit → 200
     - Requests exceeding limit → 429
   - Test input validation:
     - Valid request → 202
     - Missing required fields → 400 with field errors

3. **Event Propagation Tests**
   - Verify that `VerificationCompletedEvent` published by command-gateway is received by billing service
   - Use LocalStack Kinesis for testing
   - Verify event schema compatibility between producer and consumer

4. **CI Workflow Updates**
   - Update `_test-integration.yml` to set up LocalStack and required AWS resources
   - Update `_test-integration-e2e.yml` with proper environment variables and test data
   - Add test report publishing (JUnit XML → GitHub Actions summary)

### Acceptance Criteria

- Integration tests run in CI with LocalStack (no real AWS dependencies)
- At least 1 full command lifecycle test (submit → process → result)
- Authentication and authorization tested at the BFF layer
- Event propagation between services is verified
- All tests pass in CI pipeline

---

## 7. Medium: Observability & Correlation IDs

### Context

The codebase uses SLF4J for logging and has a partial Datadog setup (StatsD metrics, DD_* environment variables in SAM template). However, there is no request correlation ID propagation, no distributed tracing, and no business-level metrics.

### Requirements

1. **Correlation ID Propagation**
   - Generate a unique `X-Correlation-ID` (or reuse `X-Request-ID`) at the web-bff entry point
   - Add it to the SLF4J MDC (`MDC.put("correlationId", id)`)
   - Pass it as a message attribute on SQS messages sent to command-gateway
   - Extract it in Lambda handlers and set in MDC
   - Include in all domain events published to EventBridge/Kinesis
   - **Web-BFF**: Add a Spring `OncePerRequestFilter` that extracts/generates the correlation ID
   - **Command Gateway**: Read from SQS message attributes in the Lambda handler base class
   - **Billing**: Read from Kinesis event attributes

2. **Structured Logging**
   - Configure Logback/Log4j2 to output JSON format in Lambda environments
   - Include fields: `correlationId`, `partnerId`, `commandId`, `verificationType`, `timestamp`, `level`, `message`
   - Keep human-readable format for local development

3. **Business Metrics**
   - Using the existing `DatadogMeter` in shared-kernel, add metrics for:
     - `verification.submitted` (counter, tagged by verificationType)
     - `verification.completed` (counter, tagged by verificationType + outcome)
     - `verification.failed` (counter, tagged by verificationType + errorType)
     - `verification.duration_ms` (histogram, tagged by verificationType + adapter)
     - `api.request` (counter, tagged by endpoint + statusCode)

4. **CloudWatch Alarms** (if not already configured)
   - File: `verigate-command-gateway/iac/terraform/modules/shared/datadog-monitoring/`
   - Add alarms for:
     - Lambda error rate > 5% over 5 minutes
     - DLQ message count > 0
     - SQS age of oldest message > threshold
     - API 5xx error rate > 1%

### Acceptance Criteria

- Every log line in Lambda includes `correlationId` and `partnerId` when available
- A request can be traced from web-bff → command-gateway → adapter → billing by correlation ID
- Business metrics are emitted via StatsD
- `mvn verify` passes (no compilation errors from new logging/metrics code)

---

## 8. Medium: DynamoDB Index for Verification Listing

### Context

The `listVerifications` endpoint (Task 2) requires querying the command-store table by `partnerId` with optional `status` filter. The existing `partner-index` GSI has `partnerId` (partition key) + `createdAt` (sort key), which supports listing by partner but **not filtering by status** efficiently.

### Requirements

1. **Option A: Composite Sort Key on Existing GSI** (preferred)
   - Modify the `partner-index` GSI sort key from `createdAt` to `status#createdAt`
   - This allows `begins_with(status#)` filter for status queries while preserving time ordering
   - **Warning**: Changing a GSI requires deleting and recreating it — plan for data availability during migration

2. **Option B: Add New GSI**
   - Add a `partner-status-index` GSI with `partnerId` (partition) + `status` (sort)
   - Simpler but adds GSI cost and doesn't preserve time ordering within status

3. **Terraform Changes**
   - File: `verigate-command-gateway/iac/terraform/modules/tf-dynamodb/main.tf`
   - Update the command-store table GSI definition
   - Apply across all 4 environments

4. **Repository Update**
   - Update the DynamoDB query in the repository (Task 2) to use the new index structure

### Acceptance Criteria

- DynamoDB table supports efficient query by `partnerId + status`
- Terraform applies cleanly in all environments
- No data loss during index migration
- Query performance validated with sample data

### Dependency

- This task must be completed before or alongside Task 2 (Web-BFF Query Side)

---

## 9. Medium: Environment Configuration Documentation

### Context

Services rely on environment variables for configuration but there is no central documentation of required variables, their purposes, or valid values. This makes deployment to new environments error-prone.

### Requirements

1. **Web-BFF `.env.example`**
   - File: `verigate-web-bff/.env.example`
   - Document all required environment variables:
     ```
     # AWS Region
     AWS_REGION=eu-west-1

     # SQS Queue URLs (one per adapter)
     VERIGATE_QUEUE_CIPC=https://sqs.eu-west-1.amazonaws.com/{account}/verigate-verification-cg-cipc-queue-{env}
     VERIGATE_QUEUE_DHA=...
     # ... (all adapter queues)

     # DynamoDB Tables
     VERIGATE_TABLE_COMMAND_STORE=verigate-verification-cg-command-store-{env}
     VERIGATE_TABLE_API_KEYS=verigate-verification-billing-api-keys-{env}

     # Authentication
     VERIGATE_AUTH_COGNITO_ENABLED=false
     VERIGATE_AUTH_COGNITO_USER_POOL_ID=
     VERIGATE_AUTH_COGNITO_REGION=eu-west-1

     # Rate Limiting
     VERIGATE_RATE_LIMIT_REQUESTS_PER_SECOND=10

     # CORS
     VERIGATE_CORS_ALLOWED_ORIGINS=http://localhost:3000
     ```

2. **Billing Service `.env.example`**
   - File: `verigate-billing/.env.example`
   - Document Kinesis stream name, DynamoDB table names, region

3. **Command Gateway environment variable reference**
   - File: `verigate-command-gateway/docs/environment-variables.md`
   - Document all SAM template environment variables and their purposes
   - Include per-adapter secrets (Secrets Manager ARNs)

4. **GitHub Environments documentation update**
   - File: `docs/github-environments.md`
   - Add complete list of GitHub Actions secrets and variables required per environment
   - Include the account IDs, OIDC role ARNs, and service-specific configs

### Acceptance Criteria

- Every service has a `.env.example` with all required variables documented
- A new developer can set up a local environment using only the `.env.example` files
- GitHub Actions variables and secrets are fully documented

---

## 10. Medium: Partner Portal Environment Config

### Context

The partner portal (`verigate-partner-portal/`) has comprehensive mock services but minimal environment configuration. The `.env` file only contains `NEXT_PUBLIC_LOGO`. There is no `.env.example` documenting the variables needed to connect to the real web-bff backend.

### Requirements

1. **Create `.env.example`**
   - File: `verigate-partner-portal/.env.example`
   - Document:
     ```
     # Backend API
     NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
     NEXT_PUBLIC_API_KEY=your-api-key-here

     # Authentication Provider
     NEXT_PUBLIC_AUTH_PROVIDER=api-key  # or 'cognito'
     NEXT_PUBLIC_COGNITO_USER_POOL_ID=
     NEXT_PUBLIC_COGNITO_CLIENT_ID=
     NEXT_PUBLIC_COGNITO_DOMAIN=

     # Environment
     NEXT_PUBLIC_ENVIRONMENT=development

     # Feature Flags
     NEXT_PUBLIC_MOCK_API=true  # Use MSW mocks instead of real API

     # UI
     NEXT_PUBLIC_LOGO=/verigate-logo.png
     ```

2. **API Client Configuration**
   - Verify that the API client in `src/lib/services/` reads `NEXT_PUBLIC_API_BASE_URL`
   - Ensure the API key is sent as `X-API-Key` header
   - Add a toggle for mock vs real API based on `NEXT_PUBLIC_MOCK_API`

3. **Environment-Specific Configs for Deployment**
   - Create environment config files or document deployment variables for:
     - SBX: Points to sandbox web-bff URL
     - DEV: Points to dev web-bff URL
     - PPE: Points to ppe web-bff URL
     - PROD: Points to prod web-bff URL

### Acceptance Criteria

- `.env.example` is complete and documented
- Partner portal can switch between mock and real API via environment variable
- Deployment docs specify per-environment configuration

---

## 11. Low: Security Hardening

### Context

The current security implementation is reasonable for development but has gaps for production:
- API keys are stored as unsalted SHA-256 hashes
- Cognito JWT validation is disabled by default
- No request signing between BFF and command-gateway

### Requirements

1. **API Key Storage** (if time permits)
   - Consider adding a salt to API key hashes stored in DynamoDB
   - Use constant-time comparison for hash matching to prevent timing attacks
   - File: web-bff API key resolver class

2. **Cognito Enforcement for Production**
   - Ensure `VERIGATE_AUTH_COGNITO_ENABLED=true` is set in PPE and PROD environment configs
   - Verify the Cognito user pool is provisioned in PPE and PROD Terraform
   - File: `verigate-web-bff/src/main/resources/application.yaml` and environment configs

3. **Input Sanitization**
   - Review all controller endpoints for potential injection vectors
   - Ensure DynamoDB queries use parameterized expressions (not string concatenation)
   - Verify SQS message bodies are validated before processing

4. **Secrets Rotation**
   - Document the process for rotating API keys
   - Document the process for rotating adapter secrets in Secrets Manager
   - File: `docs/runbook.md` (update existing runbook)

### Acceptance Criteria

- API key hashing uses salt and constant-time comparison
- Cognito is enabled in PPE and PROD configurations
- No string-concatenated DynamoDB queries exist
- Secrets rotation process is documented

---

## 12. Task Dependency Graph

```
Task 8 (DynamoDB Index) ─────┐
                              ├──→ Task 2 (Web-BFF Query Side)
Task 9 (Env Config Docs) ────┘

Task 3 (WorldCheck Adapter) ──→ Task 4 (Adapter Tests - includes WorldCheck)

Task 5 (Billing Tests) ──────→ Task 6 (Integration/E2E Tests)
Task 4 (Adapter Tests) ──────→ Task 6 (Integration/E2E Tests)
Task 2 (Web-BFF Query) ──────→ Task 6 (Integration/E2E Tests)

Task 7 (Observability) ── independent, can run in parallel

Task 10 (Portal Env Config) ── depends on Task 2 (needs real API to connect to)

Task 11 (Security) ── independent, can run in parallel
```

### Recommended Execution Order

**Phase 1 — Foundations (parallel)**
- Task 8: DynamoDB Index
- Task 9: Environment Configuration Documentation
- Task 3: WorldCheck Adapter Implementation
- Task 7: Observability & Correlation IDs

**Phase 2 — Core Features (after Phase 1)**
- Task 2: Web-BFF Query Side (depends on Task 8)
- Task 4: Adapter Unit Tests (depends on Task 3 for WorldCheck)
- Task 5: Billing Service Tests

**Phase 3 — Integration (after Phase 2)**
- Task 6: Integration & E2E Test Suite
- Task 10: Partner Portal Environment Config

**Phase 4 — Hardening**
- Task 11: Security Hardening

---

## Notes for Agent Orchestrator

- **External APIs**: All adapter external API calls should use mocked responses. Do not integrate with real third-party services.
- **Build verification**: After each task, run `mvn verify` (Java services) or `npm run build` (Node services) to confirm no regressions.
- **Test execution**: Run `mvn test` for the specific module after adding tests. Run full `mvn verify` from root before marking a task complete.
- **Terraform**: Do not apply Terraform changes to real environments. Validate with `terraform plan` only.
- **Existing patterns**: Always read existing implementations (especially CIPC adapter and shared-kernel) before writing new code. Follow established conventions.
- **CLAUDE.md files**: Each service has a `CLAUDE.md` with architectural context. Read it before working on that service.
- **Branch strategy**: Create feature branches per task (e.g., `feat/web-bff-query-side`, `feat/worldcheck-adapter`).
