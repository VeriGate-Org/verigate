# DeedsWeb Adapter - CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with the DeedsWeb adapter.

## Build and Test Commands

### Building the Project
```bash
# Build the entire DeedsWeb adapter (from project root)
mvn -pl src/verigate-adapter-deedsweb,\
src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-domain,\
src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-application,\
src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure -am clean install

# Build specific module (from adapter root)
cd src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure && mvn clean compile
```

The `cxf-codegen-plugin` runs in the `generate-sources` phase of
`verigate-adapter-deedsweb-infrastructure` and emits CXF stubs into
`target/generated-sources/cxf/verigate/adapter/deedsweb/infrastructure/soap/generated/`.
A clean build is required after WSDL changes.

### Running Tests
```bash
# Run all tests in the adapter
mvn -pl src/verigate-adapter-deedsweb/... -am test

# Run a single test class
mvn test -Dtest=CxfDeedsRegistryClientTest

# Live (gated) integration test against the real DeedsWeb endpoint
cp src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure/src/test/resources/integration-test-local.properties.template \
   src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure/src/test/resources/integration-test-local.properties
# (edit credentials, then:)
mvn -pl src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure test \
    -Dtest=DeedsWebLiveIntegrationTest \
    -Dintegration.test.enabled=true
```

The live test must run from inside the VPC (or via `scripts/test-deeds-soap.sh`)
because DeedsWeb whitelists source IPs (NAT EIP `13.246.247.144`).

## High-Level Architecture

### DeedsWeb Adapter Overview
This adapter wraps the South African DeedsWeb SOAP registry for property-ownership
verification. It exposes 15 SOAP operations from the WSDL behind a single
`DeedsRegistryClient` domain interface.

**SOAP endpoint:** `http://deedssoap.deeds.gov.za:80/deeds-registration-soap/`
**WSDL (build input):** `verigate-adapter-deedsweb-infrastructure/src/main/resources/wsdl/deeds-registration-soap.xml`

### Clean Architecture Implementation
```
verigate-adapter-deedsweb/
├── verigate-adapter-deedsweb-domain/           # Pure domain models + DeedsRegistryClient interface
├── verigate-adapter-deedsweb-application/      # PropertyOwnershipVerificationService
└── verigate-adapter-deedsweb-infrastructure/   # CXF SOAP client + Secrets Manager + Lambda handler
```

### Key Architectural Components

#### 1. Domain Layer (`-domain/`)
- **Services**: `DeedsRegistryClient` (15 SOAP-shaped operations) and
  `PropertyOwnershipVerificationService` (verification orchestration)
- **Models**:
  - `PropertyDetails`, `PropertyOwnershipCheck`, `OwnershipVerificationResult`
  - `PersonFullProperty`, `PersonDetails`, `PropertyEndorsement`,
    `PropertyHistoryEntry`
  - `OfficeRegistry`, `DeedsPropertyType`
  - `PropertySearchRequest` — request value object including `officeCode`
  - `DeedsWebCredentials` — record carrying SOAP username/password
- **Handlers**: `PropertyVerificationCommandHandler`
- **Mappers**: `VerificationResultMapper`
- **Constants**: `DomainConstants`

#### 2. Application Layer (`-application/`)
- **Command Handlers**: `DefaultPropertyVerificationCommandHandler` — extracts
  `searchType`, `query`, `province`, `officeCode` from
  `VerifyPartyCommand.metadata` and delegates to the service.
- **Services**: `DefaultPropertyOwnershipVerificationService` — dispatches by
  `searchType` to the right `DeedsRegistryClient` operation, applies province
  and ID filters, and computes ownership confidence.

#### 3. Infrastructure Layer (`-infrastructure/`)
- **SOAP**:
  - `CxfDeedsRegistryClient` — implements `DeedsRegistryClient` against the
    CXF-generated `DeedsRegistrationEnquiryService` port. Resolves credentials
    once per verification request and fans out across every office in parallel
    when `officeCode` is null/blank/`"all"`.
  - `SoapResponseMapper` — pure functions mapping CXF-generated response types
    → domain models.
  - `SoapErrorClassifier` — translates SOAP faults / transport errors into
    `PermanentException` (auth) or `TransientException` (everything else).
  - `CachingOfficeRegistry` — caches the office list for the Lambda's warm
    lifetime; refreshed on cold start.
  - `CxfPortFactory` — builds the JAX-WS port with timeouts pulled from
    `DeedsWebApiConfiguration` and CXF logging hooked into SLF4J.
- **Configuration**: `DeedsWebApiConfiguration`, `ConfigurationValidator`
- **Secrets**: `SecretsManagerDeedsWebCredentialsProvider` — fetches the
  username/password JSON from AWS Secrets Manager via `AwsSecretManager` on
  every verification request (no in-process caching).
- **Lambda**: `VerifyPropertyOwnershipLambdaHandler` (SQS-driven),
  `ServiceModule` (Guice wiring).

### CXF Codegen
- Plugin: `org.apache.cxf:cxf-codegen-plugin` 4.0.5
- Source WSDL: `src/main/resources/wsdl/deeds-registration-soap.xml`
- Output package:
  `verigate.adapter.deedsweb.infrastructure.soap.generated`
- Output directory: `target/generated-sources/cxf` (excluded from checkstyle)

### Authentication
SOAP operations carry `username` and `password` parameters in their request
bodies (per WSDL). Credentials live in AWS Secrets Manager under
`verigate/deedsweb/credentials`, payload:
```json
{ "username": "<user>", "password": "<pass>" }
```
`SecretsManagerDeedsWebCredentialsProvider.get()` fetches and parses the
secret per verification request. The same `DeedsWebCredentials` instance is
reused across any fan-out SOAP calls within that request.

### Office-Code Dispatch
Per-request office targeting via `VerifyPartyCommand.metadata.officeCode`:
- A specific code (`"T"`, `"J"`, ...) → single SOAP call against that office.
- `null` / blank / `"all"` → fan out across every office returned by
  `getOfficeRegistryList()`, merge results.

The office registry is cached at Lambda warm scope by `CachingOfficeRegistry`
(one call per cold start). Fan-out uses a fixed thread pool sized for the
4-vCPU Lambda memory tier.

### Configuration

#### Environment Variables (Lambda)
- `DEEDSWEB_CREDENTIALS_SECRET_NAME` (required, default `verigate/deedsweb/credentials`)
- `DEEDSWEB_BASE_URL` (default `http://deedssoap.deeds.gov.za:80/deeds-registration-soap/`)
- `DEEDSWEB_CONNECTION_TIMEOUT_MS` (default `30000`)
- `DEEDSWEB_READ_TIMEOUT_MS` (default `60000`)
- `DEEDSWEB_RETRY_ATTEMPTS` (default `3`)
- `DEEDSWEB_RETRY_DELAY_MS` (default `1000`)

See `src/main/resources/environment-variables.md` for full details.

#### IAM
The verification Lambda inherits the central role at
`/application/iam-role/${stack-name}-${env}/arn`, which already grants
`secretsmanager:GetSecretValue` on the account's secrets (scoped via principal
account condition). No adapter-specific IAM policy is required.

### Verification Workflow
1. **Command Reception**: Gateway sends `VerifyPartyCommand` (type
   `PROPERTY_OWNERSHIP_VERIFICATION`) with `metadata.{searchType, query,
   province, officeCode}`.
2. **Property Lookup**:
   `DefaultPropertyOwnershipVerificationService.searchProperties(...)`
   dispatches by `searchType` to `findPropertiesByIdNumber` /
   `findPropertiesByCompany` etc.
3. **Filtering**: Province + search-type filters reduce the result set.
4. **Ownership Check**: `checkOwnership(...)` walks the returned properties
   looking for an ID-number match.
5. **Confidence Scoring**: `calculateMatchConfidence(...)` boosts confidence
   based on subject-name vs registered-owner-name similarity (substring,
   token overlap).
6. **Response**: `VerificationResult` with property payload returned to gateway.

### Error Mapping
| SOAP outcome                                          | Domain exception                       |
|--------------------------------------------------------|----------------------------------------|
| HTTP 5xx, socket timeout, connection refused           | `TransientException` (gateway retries) |
| SOAP fault with auth-related faultstring               | `PermanentException`                   |
| SOAP fault other                                       | `TransientException`                   |
| HTTP 200, empty `propertySummaryResponseList`          | empty list (not an error)              |
| Transport `WebServiceException` containing `401`       | `PermanentException`                   |

### Testing Strategy

#### Unit Tests
- `CxfDeedsRegistryClientTest` — Mockito-mocked CXF port; covers single-office,
  fan-out, partial-failure, auth-error, transport-error paths.
- `SoapResponseMapperTest` — pure-function mapping from CXF types → domain.
- `SoapErrorClassifierTest` — fault → exception classification.
- `CachingOfficeRegistryTest` — caching + invalidation + fault propagation.
- `SecretsManagerDeedsWebCredentialsProviderTest` — JSON parsing + missing-field handling.
- `DefaultPropertyOwnershipVerificationServiceTest` — dispatch, filtering, confidence scoring.
- `DefaultPropertyVerificationCommandHandlerTest` — command handler integration with the service.

#### Integration / Live Tests (planned in this module's roadmap)
- `CxfDeedsRegistryClientWireMockIT` — captured-fixture SOAP responses against a WireMock endpoint.
- `DeedsWebLiveIntegrationTest` — gated by `integration.test.enabled=true`,
  exercises `getOfficeRegistryList()` (no creds) and a known-ID lookup against
  Pretoria.

### Development Notes

#### Adding a New SOAP Operation
1. Confirm the operation exists in
   `src/main/resources/wsdl/deeds-registration-soap.xml`.
2. Add a method to `DeedsRegistryClient` (domain) returning a domain type.
3. Implement it in `CxfDeedsRegistryClient`, using `fanOutOrSingle` /
   `invokeList` helpers.
4. Add a mapper in `SoapResponseMapper` for the response type.
5. Wire into `DefaultPropertyOwnershipVerificationService.dispatch(...)` if it
   should be reachable via `searchType`.
6. Add a unit test in `CxfDeedsRegistryClientTest` and a mapper test in
   `SoapResponseMapperTest`.

#### Configuration Changes
1. Add constants to `EnvironmentConstants`.
2. Add a getter to `DeedsWebApiConfiguration`.
3. Add the property to `application.properties`.
4. Update validation in `ConfigurationValidator`.
5. Document in `src/main/resources/environment-variables.md`.
6. Update SAM template (`iac/sam/template.yml`) — env vars on
   `VerifyPropertyOwnershipLambdaHandler`.

### DeedsWeb API Reference
- **SOAP endpoint**: `http://deedssoap.deeds.gov.za:80/deeds-registration-soap/`
- **WSDL**: `verigate-adapter-deedsweb-infrastructure/src/main/resources/wsdl/deeds-registration-soap.xml`
- **Network**: requires source IP whitelisting (NAT EIP `13.246.247.144`)
- **Operations**: 15 (see `DeedsRegistryClient`); the two no-credential
  operations (`getOfficeRegistryList`, `getDeedsPropertyTypeList`) are useful
  for connectivity smoke tests.
